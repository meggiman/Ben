package searching;

import reversi.GameBoard;
import Gameboard.Bitboard;
import Tables.TranspositionTable;
import Tables.TranspositionTable.TableEntry;
import evaluate.IEvaluator;
import evaluate.StrategicEvaluator;

public class AlphabetaTT extends Searchalgorithm{
    private TranspositionTable tt           = new TranspositionTable(8000000, new TranspositionTable.pvNodePriority());
    private TableEntry         newEntry     = new TableEntry();
    public IEvaluator          evaluator    = new StrategicEvaluator();
    private boolean            cancel       = false;
    private byte               countOfMoves = 0;

    public long nextMove(Bitboard gb){
        countOfMoves++;
        cancel = false;
        evaluatedNodesCount = 0;
        searchedNodesCount = 0;
        TTHits = 0;
        long[] possibleMoves = Bitboard.serializeBitboard(gb.getPossibleMoves(true));
        if(possibleMoves.length == 0){
            return 0;
        }
        Bitboard nextBoard;
        int bestValue = 0;
        int value;
        long bestMove = 0;
        for (int i = 1; !cancel; i++){
            bestValue = -20065;
            long tmpBestMove = 0;
            int tmpMoveNr = 0;
            for (int j = 0; j < possibleMoves.length; j++){
                long coords = possibleMoves[j];
                nextBoard = (Bitboard) gb.clone();
                nextBoard.makeMove(true, coords);
                value = min(nextBoard, -10065, 10065, i - 1);
                if(value > bestValue){
                    bestValue = value;
                    tmpBestMove = coords;
                    tmpMoveNr = j;
                }
                if(cancel){
                    return bestMove;
                }
            }
            if(i > 34 - countOfMoves){
                cancel = true;
            }
            bestMove = tmpBestMove;
            valueOfLastMove = bestValue;
            moveNr = tmpMoveNr;
            reachedDepth = i;
        }
        return bestMove;
    }

    public int max(Bitboard gb, int alpha, int beta, int depth){
        if(cancel){
            return beta;
        }
        else if(System.nanoTime() >= deadline){
            cancel = true;
            return beta;
        }
        int maxValue = alpha;
        long possibleMoves = gb.getPossibleMoves(true);
        if(possibleMoves == 0){
            if(gb.getPossibleMoves(false) == 0){
                int stonesRed = gb.countStones(GameBoard.RED);
                int stonesGreen = gb.countStones(GameBoard.GREEN);
                if(stonesRed > stonesGreen){
                    searchedNodesCount++;
                    return 10000 + stonesRed - stonesGreen;
                }else if(stonesRed < stonesGreen){
                    searchedNodesCount++;
                    return -10000 - stonesGreen + stonesRed;
                }else{
                    searchedNodesCount++;
                    return 0;
                }
            }
            return min(gb, alpha, beta, depth - 1);
        }
        // Look for transposition in transposition table
        TranspositionTable.TableEntry entry = tt.get(gb.hash);
        if(entry != null){
            if(entry.isExact && entry.depth >= depth){
                TTHits++;
                return entry.value;
            }
        }

        if(depth <= 0){
            searchedNodesCount++;
            evaluatedNodesCount++;
            int value = evaluator.evaluate(gb, possibleMoves, true);
            TableEntry.recycleEntry(newEntry, (short) value, (byte) depth, true, false, countOfMoves);
            tt.put(gb.hash, newEntry);
            return value;
        }
        searchedNodesCount++;
        long changedFields = 0;
        int value;
        long pvNodeKey = 0;
        long nextMove;
        int count = Long.bitCount(possibleMoves);
        for (int i = 0; i < count; i++){
            nextMove = Long.lowestOneBit(possibleMoves);
            possibleMoves ^= nextMove;
            changedFields = gb.makeMove(true, nextMove);
            value = min(gb, maxValue, beta, depth - 1);
            gb.undoMove(changedFields, nextMove, true);
            if(value > maxValue){
                maxValue = value;
                if(value >= beta){
                    TableEntry.recycleEntry(newEntry, (short) beta, (byte) depth, false, false, countOfMoves);
                    tt.put(gb.hash, newEntry);
                    return beta;
                }
                TableEntry.recycleEntry(newEntry, (short) value, (byte) depth, true, false, countOfMoves);
                pvNodeKey = gb.hash;
                tt.put(pvNodeKey, newEntry);
            }
            if(cancel){
                return maxValue;
            }
        }
        if(maxValue != alpha){
            TableEntry.recycleEntry(newEntry, (short) maxValue, (byte) depth, true, true, countOfMoves);
            tt.put(pvNodeKey, newEntry);
        }
        return maxValue;
    }

    public int min(Bitboard gb, int alpha, int beta, int depth){
        if(cancel){
            return alpha;
        }
        else if(System.nanoTime() >= deadline){
            cancel = true;
            return alpha;
        }
        int minValue = beta;
        long possibleMoves = gb.getPossibleMoves(false);
        if(possibleMoves == 0){
            if(gb.getPossibleMoves(true) == 0){
                int stonesRed = gb.countStones(GameBoard.RED);
                int stonesGreen = gb.countStones(GameBoard.GREEN);
                if(stonesRed > stonesGreen){
                    searchedNodesCount++;
                    return 10000 + stonesRed - stonesGreen;
                }else if(stonesRed < stonesGreen){
                    searchedNodesCount++;
                    return -10000 - stonesGreen + stonesRed;
                }else{
                    searchedNodesCount++;
                    return 0;
                }
            }
            return max(gb, alpha, beta, depth - 1);
        }
        // Look for transpositions in transposition table
        TranspositionTable.TableEntry entry = tt.get(gb.hash);
        if(entry != null){
            if(entry.isExact && entry.depth >= depth){
                TTHits++;
                return entry.value;
            }
        }

        if(depth <= 0){
            searchedNodesCount++;
            evaluatedNodesCount++;
            int value = evaluator.evaluate(gb, possibleMoves, true);
            TableEntry.recycleEntry(newEntry, (short) value, (byte) depth, true, false, countOfMoves);
            tt.put(gb.hash, newEntry);
            return value;
        }
        searchedNodesCount++;
        int value;
        long changedFields = 0;
        long pvNodeKey = 0;
        long nextMove;
        int count = Long.bitCount(possibleMoves);
        for (int i = 0; i < count; i++){
            nextMove = Long.lowestOneBit(possibleMoves);
            possibleMoves ^= nextMove;
            changedFields = gb.makeMove(false, nextMove);
            value = max(gb, alpha, minValue, depth - 1);
            gb.undoMove(changedFields, nextMove, false);
            if(value < minValue){
                minValue = value;
                if(value <= alpha){
                    TableEntry.recycleEntry(newEntry, (short) alpha, (byte) depth, false, false, countOfMoves);
                    tt.put(gb.hash, newEntry);
                    return alpha;
                }
                TableEntry.recycleEntry(newEntry, (short) value, (byte) depth, true, false, countOfMoves);
                pvNodeKey = gb.hash;
                tt.put(pvNodeKey, newEntry);
            }
            if(cancel){
                return minValue;
            }
        }
        if(minValue != beta){
            TableEntry.recycleEntry(newEntry, (short) minValue, (byte) depth, true, true, countOfMoves);
            tt.put(pvNodeKey, newEntry);
        }
        return minValue;
    }
}
