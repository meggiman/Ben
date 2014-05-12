package Tables;

import java.io.Serializable;

import Gameboard.Bitboard;

/**
 * Provides classes and methodes to save and maintanin transposition tables
 **/
public class TranspositionTable{
    /**
     * The number of entries in the table.
     * Maxsize is 2147483647, provided there is enough memory.
     */
    private int              maxSize;
    private IReplaceStrategy replaceStrategy;
    public HashMap           hashMap;

    public TranspositionTable(int maxSize, IReplaceStrategy replaceStrategy){
        this.maxSize = (0x80000000 >>> Integer.numberOfLeadingZeros(maxSize));
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
        private final int    size;
        private final long[] keys;
        TableEntry[]         entries;
        private final long   mask;

        public HashMap(){
            size = 31 - Integer.numberOfLeadingZeros(maxSize);
            keys = new long[maxSize];
            entries = new TableEntry[maxSize];
            mask = ~(0x8000000000000000L >> (63 - size));
            for (int i = 0;i<entries.length;i++) {
				entries[i] = new TableEntry();
			}
        }

        /**
         * Saves a new entry to the hashmap. This method uses the method of the
         * static {@link IReplaceStrategy} object of the containing
         * {@link TranspostitionTabble}.
         * 
         * @param key
         *            the key pointing to the value.
         * @param entry
         *            the value
         */
        public void put(long key, TableEntry entry){
            int index = (int) (key & mask);
            if(entries[index] == null){
                TableEntry newEntry = new TableEntry(entry.value, entry.depth, entry.isExact, entry.isPvnode, entry.countOfMoves);
                entries[index] = newEntry;
            }
            else if(keys[index] == key){
                keys[index] = key;
                TableEntry entryInTable = entries[index];
                entryInTable.countOfMoves = entry.countOfMoves;
                entryInTable.depth = entry.depth;
                entryInTable.isExact = entry.isExact;
                entryInTable.isPvnode = entry.isPvnode;
//                entryInTable.bestMove = entry.bestMove;
                entryInTable.value = entry.value;
            }
            else if(replaceStrategy.replace(entries[index], entry)){
                keys[index] = key;
                TableEntry oldEntry = entries[index];
                oldEntry.countOfMoves = entry.countOfMoves;
                oldEntry.depth = entry.depth;
                oldEntry.isExact = entry.isExact;
                oldEntry.isPvnode = entry.isPvnode;
//                oldEntry.bestMove = entry.bestMove;
                oldEntry.value = entry.value;
            }
        }

        /**
         * Returns the value key is pointing to.
         * 
         * @param key
         *            the key pointing to the value
         * @return the value key is pointing to; null if there was no entry.
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
     * A struct beholding all the data concerning one transposition, contained
     * by a {@link TranspositionTable}.
     */
    public static class TableEntry implements Serializable{

        /**
         * The value of the transposition.
         */
        public short   value;

        /**
         * depth on which {@code value} was found.
         */
        public byte    depth;

        /**
         * This flag tells wether {@code value} exact or just a boundary.
         */
        public boolean isExact;

        /**
         * This flag tells wether it's a Pvnode or not.
         */
        public boolean isPvnode;

//        /**
//         * The index of the best move going from this transposition.
//         */
//        public byte    bestMove;

        /**
         * Contains the amount of moves done by both players so far.
         */
        public byte    countOfMoves;

        public TableEntry(){
        }

        public TableEntry(short value, byte depth, boolean isExact, boolean isPvNode, byte countOfMoves){
            this.value = value;
            this.depth = depth;
            this.isExact = isExact;
            this.isPvnode = isPvNode;
//            this.bestMove = bestMove;
            this.countOfMoves = countOfMoves;
        }

        public static void recycleEntry(TableEntry entry, short value, byte depth, boolean isExact, boolean isPvNode, byte countOfMoves){
            entry.value = value;
            entry.depth = depth;
            entry.isExact = isExact;
            entry.isPvnode = isPvNode;
//            entry.bestMove = bestMove;
            entry.countOfMoves = countOfMoves;
        }
    }

    /**
     * Interface to use the different replacement strategies for the hasmaps
     * 
     */
    public static interface IReplaceStrategy{
        /**
         * A replacement algorithm. Depending on the implementation.
         * 
         * @param oldEntry
         *            the old, maybe to be replaced entry.
         * @param newEntry
         *            the new, maybe replacing entry
         * @return true, if the old entry should be replaced, false if not.
         */
        public boolean replace(TableEntry oldEntry, TableEntry newEntry);
    }

    public static class alwaysReplace implements IReplaceStrategy{
        /**
         * Always replaces the old entry
         */
        @Override
        public boolean replace(TableEntry oldEntry, TableEntry newEntry){
            return true;
        }

    }

    public static class neverReplace implements IReplaceStrategy{
        /**
         * never replaces the old entry
         */
        @Override
        public boolean replace(TableEntry oldEntry, TableEntry newEntry){
            return false;
        }

    }

    public static class pvNodePriority implements IReplaceStrategy{

        @Override
        public boolean replace(TableEntry oldEntry, TableEntry newEntry){
            if(oldEntry.countOfMoves < newEntry.countOfMoves
                    || !oldEntry.isExact){
                return true;
            }
            if(newEntry.isPvnode){
                return true;
            }
            if(!oldEntry.isPvnode && newEntry.isExact){
                return true;
            }
            else{
                return false;
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

        public Sorter(){

        }

        public Sorter(long possibleMovesAsLong, Bitboard board, boolean player, boolean maxNode, HashMap map){
            this.board = board;
            this.map = map;
            this.player = player;
            this.maxNode = maxNode;
            this.possibleMoves = Bitboard.serializeBitboard(possibleMovesAsLong);
            sortedMoves = new TableEntry[possibleMoves.length - 1];
            sortedReferences = new byte[possibleMoves.length - 1];
        }

        /**
         * Recycles the Sorter object to not have a thousand of it floating
         * around
         * 
         * @param possibleMovesAsLong
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
        public void recycle(long possibleMovesAsLong, Bitboard board, boolean player, boolean maxNode, HashMap map){
            this.board = board;
            this.map = map;
            this.player = player;
            this.maxNode = maxNode;
            this.possibleMoves = Bitboard.serializeBitboard(possibleMovesAsLong);
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
                    long changedFields = board.makeMove(player, possibleMoves[i]);
                    sortedMoves[searchedUpTo] = map.get(board.hash);
                    sortedReferences[searchedUpTo] = i;
                    board.undoMove(changedFields, possibleMoves[i], player);
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
                    long changedFields = board.makeMove(player, possibleMoves[i]);
                    sortedMoves[searchedUpTo++] = map.get(board.hash);
                    board.undoMove(changedFields, possibleMoves[i], player);
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
            if(left == null){
                return true;
            }
            else if(right == null){
                return false;
            }
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
            if(left == null){
                return false;
            }
            else if(right == null){
                return true;
            }
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
