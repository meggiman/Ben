package Tables;

import java.io.ObjectInputStream.GetField;
import java.io.Serializable;

import Gameboard.Bitboard;

/**
 * Stellt Klassen und Methoden zum Speichern und Verwalten von Transpositions Tabellen bereit.
 *
 */
public class TranspositionTable {
	/**
	 * Die Anzahl der Elemente der TranspositionTable.
	 * Maximalgrösse ist 2147483647, sofern der System-Speicher gross genug ist.
	 */
	private int maxsize;
	private IReplaceStrategy replaceStrategy;
	public HashMap hashMap;
	
	public TranspositionTable(int maxsize, IReplaceStrategy replaceStrategy){
		this.maxsize = (0x80000000>>>Integer.numberOfLeadingZeros(maxsize));
		this.replaceStrategy = replaceStrategy;
		hashMap = new HashMap();
	}

	public TableEntry get(long key){
		return hashMap.get(key);
	}
	
	public void put(long key, TableEntry entry){
		hashMap.put(key, entry);
	}
	
	public class HashMap{
		private final int size;
		private final long[] keys;
		TableEntry[] entries;
		private final long mask;
		public HashMap(){
			size = 31-Integer.numberOfLeadingZeros(maxsize);
			keys = new long[maxsize];
			entries = new TableEntry[maxsize];
			mask = ~(0x8000000000000000L>>(63-size));
			for (TableEntry entry : entries) {
				entry = new TableEntry();
			}
		}
		/**
		 * Speichert einen neuen Eintrag in der Hashmap. Die Methode verwendet die Methode des statischen {@link IReplaceStrategy} Objekts von {@link TranspostitionTabble}.
		 * @param key Der Schlüssel, unter welchem der Eintrag künftig abrufbar ist.
		 * @param entry der Eintrag
		 */
		public void put(long key, TableEntry entry){
			int index = (int)(key&mask);
			if (entries[index] == null){
				TableEntry newEntry = new TableEntry(entry.value, entry.depth, entry.isExact, entry.isPvnode, entry.countofmoves);
				entries[index] = newEntry;
			}
			else if (keys[index] == key) {
				keys[index]=key;
				TableEntry entryInTable = entries[index];
				entryInTable.countofmoves = entry.countofmoves;
				entryInTable.depth = entry.depth;
				entryInTable.isExact = entry.isExact;
				entryInTable.isPvnode = entry.isPvnode;
				entryInTable.value = entry.value;
			}
			else if (replaceStrategy.replace(entries[index], entry)){
				keys[index] = key;
				entries[index].countofmoves = entry.countofmoves;
				entries[index].depth = entry.depth;
				entries[index].isExact = entry.isExact;
				entries[index].isPvnode = entry.isPvnode;
				entries[index].value = entry.value;
			}
		}
		/**
		 * Gibt den dem key entsprechenden Eintrag zurück. 
		 * @param key der Schlüssel des gesuschten Eintrags
		 * @return den dem Schlüssel entsprechenden Eintrag. Gibt null zurück, wenn der Eintrag nicht gefunden wurde.
		 */
		public TableEntry get(long key){
			int index = (int)(key&mask);
			if (keys[index]==key) {
				return entries[index];
			}
			else {
				return null;
			}
		}
	}
	
	/**
	 * Subklasse zum Speichern der {@link TranspositionTable} Einträge.
	 *
	 */
	public static class TableEntry implements Serializable{
		
		/**
		 * Der Wert der Spielposition.
		 */
		public short value;
		
		/**
		 * Tiefe, aus welcher {@code value} stammt.
		 */
		public byte depth;
		
		/**
		 * Dieses Flag gibt an, ob {@code value} exakt ist oder nur eine obere oder untere Schranke.
		 */
		public boolean isExact;
		
		/**
		 * Wenn {@code true} ist {@code value} eine obere Schranke, wenn {@code false} eine untere Schranke.
		 * Dieses Flag ist bei gesetzem {@code isexact} Flag bedeutungslos.
		 */
		public boolean isPvnode;
		
		/**
		 * Gibt den besten jemals gefundenen nächsten Zug in {@code long} Repräsentation an.
		 */
		public byte countofmoves;

		public TableEntry(){}

		public TableEntry(short value, byte depth, boolean isExact, boolean isPvnode, byte countofmoves){
			this.value = value;
			this.depth = depth;
			this.isExact = isExact;
			this.isPvnode = isPvnode;
			this.countofmoves = countofmoves;
		}
		
		public static void recycleEntry(TableEntry entry,short value, byte depth, boolean isExact, boolean isPvnode, byte countofmoves){
			entry.value = value;
			entry.depth = depth;
			entry.isExact = isExact;
			entry.isPvnode = isPvnode;
			entry.countofmoves = countofmoves;
		}
	}
	
	/**
	 * Interface um verschiedene Ersetzungsstrategien für verschiedene Hashmaps zu verwenden.
	 *
	 */
	public static interface IReplaceStrategy{
		/**
		 * Ersetzungsstrategie für die Hashmap. Die Strategie ist implementierungsabhängig.
		 * @param oldEntry der evtl. zu ersetzende Wert
		 * @param newEntry der evtl. neue Wert
		 * @return true, wenn der alte Eintrag ersetzt werden soll, ansonsten false
		 */
		public boolean replace(TableEntry oldEntry, TableEntry newEntry);
	}
	public static class alwaysreplace implements IReplaceStrategy{
		/**
		 * Ersetzt den alten Wert immer.
		 */
		@Override
		public boolean replace(TableEntry oldEntry, TableEntry newEntry) {
			return true;
		}
		
	}
	public static class neverreplace implements IReplaceStrategy{
		/**
		 * Ersetzt den alten Wert niemals.
		 */
		@Override
		public boolean replace(TableEntry oldEntry, TableEntry newEntry) {
			return false;
		}
		
	}
	public static class pvnodepriority implements IReplaceStrategy{

		@Override
		public boolean replace(TableEntry oldEntry, TableEntry newEntry) {
			if (oldEntry.countofmoves < newEntry.countofmoves || !oldEntry.isExact) {
				return true;
			}
			if (oldEntry.isExact && newEntry.isExact) {
				return true;
			}
			if (oldEntry.isPvnode && newEntry.isPvnode) {
				return true;
			}
			else {
				return false;
			}
		}
		
	}
	
	/**
	 * Klasse zum Vorsortieren von Zügen. Effizienz dieser Implementierung fraglich.
	 *
	 */
	public static class Sorter{
		private long possiblemovesbitboard;
		private long[] possiblemoves;
		boolean player;
		int count = 0;
		
		public Sorter(long possiblemoves, Bitboard gb, boolean player){
			this.possiblemovesbitboard = possiblemoves;
			this.player =  player;
			this.possiblemoves = Bitboard.bitboardserialize(possiblemoves);
		}
		
		public Bitboard nextGb(Bitboard gb){
			if (count < possiblemoves.length) {
				return gb.copyandmakemove(player, possiblemoves[count++]);
			}
			return null;
		}
		
		
		public long getmove(){
			return possiblemoves[count];
		}
	}
}
