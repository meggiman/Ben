package searching;

import Gameboard.Bitboard;

public class EndgameSearchMoveOrdering{
    long nextMove(Bitboard gb, boolean player){
        return 0;
    }

    public static class OutcomeSearch{
        public static int        outcome;
        public static long       nodecount;
        public static long       deadline;
        public static MoveList[] moveListPool = MoveList.generateListPool(30, 25);

        public final static long nextMove(Bitboard gb){
            nodecount = 0;
            int alpha = -1;
            int beta = 1;
            int remainingStones = 64 - gb.countStones(true)
                    - gb.countStones(false);
            long possibleMoves = gb.possiblemoves(true);
            if(possibleMoves == 0){
                long possibleMovesEnemy = gb.possiblemoves(false);
                if(possibleMovesEnemy != 0){
                    outcome = min(gb.red, gb.green, alpha, beta, remainingStones, Bitboard.possibleMovesRed(gb.green, gb.red));
                }
                return 0;
            }
            long bestmove = Long.highestOneBit(possibleMoves);
            int bestvalue = alpha;
            int value = alpha;
            MoveList moveList = MoveList.recycle(moveListPool[remainingStones], possibleMoves, gb.red, gb.green);
            for (int i = 0; i < moveList.size; i++){
                int index = moveList.orderedIndices[i];
                value = min(moveList.red[index], moveList.green[index], value, beta, remainingStones - 1, moveList.possiblemoves[index]);
                if(value > bestvalue){
                    if(value >= beta){
                        bestvalue = value;
                        outcome = value;
                        return moveList.move[index];
                    }
                    bestvalue = value;
                    bestmove = moveList.move[index];
                }
                possibleMoves ^= moveList.move[index];
            }
            outcome = bestvalue;
            return bestmove;
        }

        final static int min(long red, long green, int alpha, int beta, int remainingstones, long possibleMoves){
            nodecount++;
            if(remainingstones <= 4){
                return finalScorefewremainingMin(red, green, alpha, beta, false);
            }
            if(possibleMoves == 0){
                if(Bitboard.possibleMovesRed(red, green) == 0){
                    return Long.bitCount(red) - Long.bitCount(green);
                }
                return max(red, green, alpha, beta, remainingstones - 1, Bitboard.possibleMovesRed(red, green));
            }
            int bestvalue = beta;
            int value = beta;
            MoveList moveList = MoveList.recycle(moveListPool[remainingstones], possibleMoves, green, red);
            for (int i = 0; i < moveList.size; i++){
                int index = moveList.orderedIndices[i];
                value = max(moveList.green[index], moveList.red[index], alpha, value, remainingstones - 1, moveList.possiblemoves[index]);
                if(value < beta){
                    if(value <= alpha){
                        return alpha;
                    }
                    bestvalue = value;
                }
            }
            return bestvalue;
        }

        final static int max(long red, long green, int alpha, int beta, int remainingstones, long possibleMoves){
            nodecount++;
            if(remainingstones <= 4){
                return finalScorefewremainingMax(red, green, alpha, beta, false);
            }
            if(possibleMoves == 0){
                if(Bitboard.possibleMovesRed(green, red) == 0){
                    return Long.bitCount(red) - Long.bitCount(green);
                }
                return min(red, green, alpha, beta, remainingstones - 1, Bitboard.possibleMovesRed(green, red));
            }
            int bestvalue = alpha;
            int value = alpha;
            MoveList moveList = MoveList.recycle(moveListPool[remainingstones], possibleMoves, red, green);
            for (int i = 0; i < moveList.size; i++){
                int index = moveList.orderedIndices[i];
                value = min(moveList.red[index], moveList.green[index], value, beta, remainingstones - 1, moveList.possiblemoves[index]);
                if(value > alpha){
                    if(value >= beta){
                        return beta;
                    }
                    bestvalue = value;
                }
            }
            return bestvalue;
        }

        public static int finalScorefewremainingMin(long red, long green, int alpha, int beta, boolean passed){
            nodecount++;
            long emptyfields = ~(red | green);
            long changedfields = 0;
            long coord = 0;
            int bestvalue = beta;
            int value = beta;
            boolean nomoveavailable = true;
            while(emptyfields != 0){
                coord = Long.highestOneBit(emptyfields);
                changedfields = Bitboard.getflippedDiskRed(green, red, coord);
                if(changedfields != 0){
                    value = finalScorefewremainingMax(red ^ changedfields, green
                            ^ changedfields ^ coord, alpha, bestvalue, false);
                    if(value < beta){
                        if(value <= alpha){
                            return alpha;
                        }
                        bestvalue = value;
                    }
                    nomoveavailable = false;
                }
                emptyfields ^= coord;
            }
            if(nomoveavailable){
                if(passed){
                    return Long.bitCount(red) - Long.bitCount(green);
                }
                return finalScorefewremainingMax(red, green, alpha, beta, true);
            }
            return bestvalue;
        }

        public static int finalScorefewremainingMax(long red, long green, int alpha, int beta, boolean passed){
            nodecount++;
            long emptyfields = ~(red | green);
            long changedfields = 0;
            long coord = 0;
            int bestvalue = alpha;
            int value = alpha;
            boolean nomoveavailable = true;
            while(emptyfields != 0){
                coord = Long.highestOneBit(emptyfields);
                changedfields = Bitboard.getflippedDiskRed(red, green, coord);
                if(changedfields != 0){
                    value = finalScorefewremainingMin(red ^ changedfields
                            ^ coord, green ^ changedfields, bestvalue, beta, false);
                    if(value > alpha){
                        if(value >= beta){
                            return beta;
                        }
                        bestvalue = value;
                    }
                    nomoveavailable = false;
                }
                emptyfields ^= coord;
            }
            if(nomoveavailable){
                if(passed){
                    return Long.bitCount(red) - Long.bitCount(green);
                }
                return finalScorefewremainingMin(red, green, alpha, beta, true);
            }
            return bestvalue;
        }

        private static class MoveList{
            private static final long CORNERS = 0x8100000000000081L;
            public long[]             possiblemoves;
            public long[]             red;
            public long[]             green;
            public long[]             move;
            public int[]              orderedScore;
            public int[]              orderedIndices;
            public int                size;

            public static MoveList[] generateListPool(int poolSize, int listSize){
                MoveList[] pool = new MoveList[poolSize];
                for (int i = 0; i < pool.length; i++){
                    pool[i] = new MoveList(listSize);
                }
                return pool;
            }

            public MoveList(int size){
                possiblemoves = new long[size];
                red = new long[size];
                green = new long[size];
                orderedScore = new int[size];
                orderedIndices = new int[size];
                move = new long[size];
            }

            public final static MoveList recycle(MoveList oldlist, long possiblemoves, long red, long green){
                int size = Long.bitCount(possiblemoves);
                oldlist.size = size;
                for (int i = 0; i < size; i++){
                    oldlist.orderedIndices[i] = i;
                    long coord = Long.highestOneBit(possiblemoves);
                    oldlist.move[i] = coord;
                    long flipeddisk = Bitboard.getflippedDiskRed(red, green, coord);
                    long newred = red ^ flipeddisk ^ coord;
                    long newgreen = green ^ flipeddisk;
                    oldlist.red[i] = newred;
                    oldlist.green[i] = newgreen;
                    long newpossiblemoves = Bitboard.possibleMovesRed(newgreen, newred);
                    oldlist.possiblemoves[i] = newpossiblemoves;
                    long occupiedCorners = newred & CORNERS;
                    long cornerstability = occupiedCorners;
                    cornerstability |= occupiedCorners >>> 1 & 0x7f7f7f7f7f7f7f7fL;
                    cornerstability |= occupiedCorners >>> 8;
                    cornerstability |= occupiedCorners << 1 & 0xfefefefefefefefeL;
                    cornerstability |= occupiedCorners << 8;
                    cornerstability &= newred;
                    int score = 8 * Long.bitCount(cornerstability) - 8
                            * Long.bitCount(newpossiblemoves & CORNERS)
                            - Long.bitCount(newpossiblemoves);
                    oldlist.orderedScore[i] = score;
                    possiblemoves ^= coord;
                }
                insertionSort(oldlist.orderedScore, oldlist.orderedIndices, size);
                return oldlist;
            }

            private final static void insertionSort(int[] orderedScore, int[] orderedIndices, int size){
                for (int i = 1; i < size; i++){
                    int j;
                    int tempScore = orderedScore[i];
                    int tempIndex = orderedIndices[i];
                    for (j = i - 1; j >= 0 && tempScore > orderedScore[j]; j--){
                        orderedScore[j + 1] = orderedScore[j];
                        orderedIndices[j + 1] = orderedIndices[j];
                    }
                    j++;
                    orderedScore[j] = tempScore;
                    orderedIndices[j] = tempIndex;
                }
            }
        }
    }

    public static class ExactSearch{

    }
}
