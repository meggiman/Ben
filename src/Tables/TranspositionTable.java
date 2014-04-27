package Tables;

import java.io.Serializable;

import Gameboard.Bitboard;

/**
 * Stellt Klassen und Methoden zum Speichern und Verwalten von Transpositions
 * Tabellen bereit.
 * 
 */
public class TranspositionTable{
    /**
     * Die Anzahl der Elemente der TranspositionTable. Maximalgroesse ist
     * 2147483647, sofern der System-Speicher gross genug ist.
     */
    private int              maxsize;
    private IReplaceStrategy replaceStrategy;

    public TranspositionTable(int maxsize, IReplaceStrategy replaceStrategy){
        this.maxsize = ~(0x80000000 >> Integer.numberOfLeadingZeros(maxsize));
    }

    public class HashMap implements Serializable{
        private final int  size    = 31 - Integer.numberOfLeadingZeros(maxsize);
        private long[]     keys    = new long[maxsize];
        TableEntry[]       entries = new TableEntry[maxsize];
        private final long mask    = ~(0x8000000000000000L >> (63 - size));

        /**
         * Speichert einen neuen Eintrag in der Hashmap. Die Methode verwendet
         * die Methode des statischen {@link IReplaceStrategy} Objekts von
         * {@link TranspostitionTabble}.
         * 
         * @param key
         *            Der Schluessel, unter welchem der Eintrag kuenftig
         *            abrufbar ist.
         * @param entry
         *            der Eintrag
         */
        public void put(long key, TableEntry entry){
            int index = (int) (key & mask);
            if(keys[index] == 0 || keys[index] == key){
                entries[index] = entry;
            }
            else if(replaceStrategy.replace(entries[index], entry)){
                keys[index] = key;
                entries[index] = entry;
            }
        }

        /**
         * Gibt den dem key entsprechenden Eintrag zurueck.
         * 
         * @param key
         *            der Schluessel des gesuschten Eintrags
         * @return den dem Schluessel entsprechenden Eintrag. Gibt null zurueck,
         *         wenn der Eintrag nicht gefunden wurde.
         */
        public TableEntry get(long key){
            int index = (int) (key & mask);
            if(keys[index] == key){
                return entries[index];
            }
            else{
                return null;
            }
        }
    }

    /**
     * Subklasse zum Speichern der {@link TranspositionTable} Eintraege.
     * 
     */
    public static class TableEntry implements Serializable{
        /**
         * Der Wert der Spielposition.
         */
        public short   value;

        /**
         * Tiefe, aus welcher {@code value} stammt.
         */
        public byte    depth;

        /**
         * Dieses Flag gibt an, ob {@code value} exakt ist oder nur eine obere
         * oder untere Schranke.
         */
        public boolean isExact;

        /**
         * Wenn {@code true} ist {@code value} eine obere Schranke, wenn
         * {@code false} eine untere Schranke. Dieses Flag ist bei gesetzem
         * {@code isexact} Flag bedeutungslos.
         */
        public boolean isPvnode;

        /**
         * Gibt den besten jemals gefundenen naechsten Zug in {@code long}
         * Repraesentation an.
         */
        public byte    countofmoves;
    }

    /**
     * Interface um verschiedene Ersetzungsstrategien fuer verschiedene Hashmaps
     * zu verwenden.
     * 
     */
    private interface IReplaceStrategy{
        /**
         * Ersetzungsstrategie fuer die Hashmap. Die Strategie ist
         * implementierungsabhaengig.
         * 
         * @param oldEntry
         *            der evtl. zu ersetzende Wert
         * @param newEntry
         *            der evtl. neue Wert
         * @return true, wenn der alte Eintrag ersetzt werden soll, ansonsten
         *         false
         */
        public boolean replace(TableEntry oldEntry, TableEntry newEntry);
    }

    private class alwaysreplace implements IReplaceStrategy{
        /**
         * Ersetzt den alten Wert immer.
         */
        @Override
        public boolean replace(TableEntry oldEntry, TableEntry newEntry){
            return true;
        }

    }

    private class neverreplace implements IReplaceStrategy{
        /**
         * Ersetzt den alten Wert niemals.
         */
        @Override
        public boolean replace(TableEntry oldEntry, TableEntry newEntry){
            return false;
        }

    }

    private class pvnodepriority implements IReplaceStrategy{

        @Override
        public boolean replace(TableEntry oldEntry, TableEntry newEntry){
            if(oldEntry.depth < newEntry.depth){
                return true;
            }

        }

    }

    /**
     * recyclable helper class to sort all moves, best first, worst last,
     * assuming a pvNode
     * is the best way to go exact values are always better than non exact
     * values
     */
    public static class Sorter{
        private Bitboard     board;
        private HashMap      map;
        private long[]       possibleMoves;
        private TableEntry[] sortedMoves;
        private byte[]       sortedReferences;
        boolean              player;
        boolean              maxNode;
        byte                 count        = 0;
        byte                 searchedUpTo = 0;

        public Sorter(long possiblemoves, Bitboard board, boolean player, boolean maxNode, HashMap map){
            this.board = board;
            this.map = map;
            this.player = player;
            this.maxNode = maxNode;
            this.possibleMoves = Bitboard.bitboardserialize(possiblemoves);
            sortedMoves = new TableEntry[possibleMoves.length - 1];
            sortedReferences = new byte[possibleMoves.length - 1];
        }

        /**
         * Recycles the Sorter object to not have a thousand of it floating
         * around
         * 
         * @param possiblemoves
         *            all possible moves
         * @param board
         *            the current Bitboard
         * @param player
         *            whose turn it is
         * @param maxNode
         *            is it a maxNode (greater values are better)?
         * 
         * @param map
         *            the transposition table
         */
        public void recycle(long possiblemoves, Bitboard board, boolean player, boolean maxNode, HashMap map){
            this.board = board;
            this.map = map;
            this.player = player;
            this.maxNode = maxNode;
            this.possibleMoves = Bitboard.bitboardserialize(possiblemoves);
            sortedMoves = new TableEntry[possibleMoves.length - 1];
            sortedReferences = new byte[possibleMoves.length - 1];
        }

        /**
         * Searches for the next best move if necessairy and returns it
         * 
         * @returns the next best available move
         */
        public long getNextMove(){
            // If no move was returned yet, search for the pvNode
            if(count == 0){
                for (byte i = 0; i < possibleMoves.length; i++){
                    long changedfields = board.makeMove(player, possibleMoves[i]);
                    sortedMoves[searchedUpTo] = map.get(board.hash);
                    sortedReferences[searchedUpTo] = i;
                    board.undomove(changedfields, possibleMoves[i], player);
                    if(sortedMoves[searchedUpTo].isPvnode){
                        count++;
                        return possibleMoves[i];
                    }
                    searchedUpTo++;
                }
            }
            // pvNode was returned but no other move. Evaluate all moves and
            // return the best of them
            if(count == 1){
                for (byte i = (byte) (searchedUpTo + 1); i < possibleMoves.length; i++){
                    long changedfields = board.makeMove(player, possibleMoves[i]);
                    sortedMoves[searchedUpTo++] = map.get(board.hash);
                    board.undomove(changedfields, possibleMoves[i], player);
                }
                sortEntries((byte) 0, (byte) (sortedMoves.length - 1));
            }

            return possibleMoves[sortedReferences[(searchedUpTo++ - 1)]];
        }

        /**
         * Quicksort
         * 
         * @param left
         *            left boundary
         * @param right
         *            right boundary
         */
        private void sortEntries(byte left, byte right){
            if(left < right){
                byte separator = separate(left, right);
                sortEntries(left, (byte) (separator - 1));
                sortEntries((byte) (separator + 1), right);
            }

        }

        /**
         * Swaps all worse moves from left to better moves from the right
         * 
         * @param left
         *            left boundary
         * @param right
         *            right boundary
         * @returns the position of the pivot
         **/
        private byte separate(byte left, byte right){
            byte i = left;
            byte j = (byte) (right - 1);
            TableEntry pivot = sortedMoves[right];
            do{
                while(better(sortedMoves[i], pivot) && i < right){
                    i++;
                }

                while(worse(sortedMoves[j], pivot) && j > left){
                    j++;
                }

                if(i < j){
                    TableEntry tmp = sortedMoves[i];
                    sortedMoves[i] = sortedMoves[j];
                    sortedMoves[j] = tmp;
                    byte tmp2 = sortedReferences[i];
                    sortedReferences[i] = sortedReferences[j];
                    sortedReferences[j] = tmp2;
                }
            }while(i < j);

            if(worse(sortedMoves[i], pivot)){
                TableEntry tmp = sortedMoves[i];
                sortedMoves[i] = sortedMoves[j];
                sortedMoves[j] = tmp;
                byte tmp2 = sortedReferences[i];
                sortedReferences[i] = sortedReferences[j];
                sortedReferences[j] = tmp2;
            }

            return i;

        }

        /**
         * This coparison depends on wether it's a maxNode oder a minNode!
         * 
         * @param left
         *            entry to compare
         * @param right
         *            the entry compared with
         * @returns true if {@code left} is worse than {@code right}
         **/

        private boolean worse(TableEntry left, TableEntry right){
            if(left.isExact == right.isExact){
                if(maxNode){
                    if(left.value < right.value){
                        return true;
                    }
                }
                else{
                    if(left.value > right.value){
                        return true;
                    }
                }
                return false;
            }
            else{
                if(left.isExact){
                    return false;
                }
                return true;
            }
        }

        /**
         * This coparison depends on wether it's a maxNode oder a minNode!
         * 
         * @param left
         *            entry to compare
         * @param right
         *            the entry compared with
         * @returns true if {@code left} is better than {@code right}
         **/
        private boolean better(TableEntry left, TableEntry right){
            if(left.isExact == right.isExact){
                if(maxNode){
                    if(left.value > right.value){
                        return true;
                    }
                }
                else{
                    if(left.value < right.value){
                        return true;
                    }
                }
                return false;
            }
            else{
                if(left.isExact){
                    return true;
                }
                return false;
            }
        }
    }
}
