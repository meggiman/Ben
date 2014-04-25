package Tables;

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
	
	public TranspositionTable(int maxsize, IReplaceStrategy replaceStrategy){
		this.maxsize = ~(0x80000000>>Integer.numberOfLeadingZeros(maxsize));
	}

	public class HashMap implements Serializable{
		private final int size = 31-Integer.numberOfLeadingZeros(maxsize);
		private long[] keys = new long[maxsize];
		TableEntry[] entries = new TableEntry[maxsize];
		private final long mask = ~(0x8000000000000000L>>(63-size));
		
		/**
		 * Speichert einen neuen Eintrag in der Hashmap. Die Methode verwendet die Methode des statischen {@link IReplaceStrategy} Objekts von {@link TranspostitionTabble}.
		 * @param key Der Schlüssel, unter welchem der Eintrag künftig abrufbar ist.
		 * @param entry der Eintrag
		 */
		public void put(long key, TableEntry entry){
			int index = (int)(key&mask);
			if (keys[index] == 0 || keys[index] == key) {
				entries[index] = entry;
			}
			else if (replaceStrategy.replace(entries[index], entry)){
				keys[index] = key;
				entries[index] = entry;
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
		public int value;
		
		/**
		 * Tiefe, aus welcher {@code value} stammt.
		 */
		public int depth;
		
		/**
		 * Dieses Flag gibt an, ob {@code value} exakt ist oder nur eine obere oder untere Schranke.
		 */
		public boolean isexact;
		
		/**
		 * Wenn {@code true} ist {@code value} eine obere Schranke, wenn {@code false} eine untere Schranke.
		 * Dieses Flag ist bei gesetzem {@code isexact} Flag bedeutungslos.
		 */
		public boolean isCutnode;
		
		/**
		 * Gibt den besten jemals gefundenen nächsten Zug in {@code long} Repräsentation an.
		 */
		public long bestmove;
	}
	
	/**
	 * Interface um verschiedene Ersetzungsstrategien für verschiedene Hashmaps zu verwenden.
	 *
	 */
	private interface IReplaceStrategy{
		/**
		 * Ersetzungsstrategie für die Hashmap. Die Strategie ist implementierungsabhängig.
		 * @param oldEntry der evtl. zu ersetzende Wert
		 * @param newEntry der evtl. neue Wert
		 * @return true, wenn der alte Eintrag ersetzt werden soll, ansonsten false
		 */
		public boolean replace(TableEntry oldEntry, TableEntry newEntry);
	}
	private class alwaysreplace implements IReplaceStrategy{
		/**
		 * Ersetzt den alten Wert immer.
		 */
		@Override
		public boolean replace(TableEntry oldEntry, TableEntry newEntry) {
			return true;
		}
		
	}
	private class neverreplace implements IReplaceStrategy{
		/**
		 * Ersetzt den alten Wert niemals.
		 */
		@Override
		public boolean replace(TableEntry oldEntry, TableEntry newEntry) {
			return false;
		}
		
	}
	private class pvnodepriority implements IReplaceStrategy{

		@Override
		public boolean replace(TableEntry oldEntry, TableEntry newEntry) {
			if (oldEntry.depth < newEntry.depth) {
				return true;
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
