package searching;

import reversi.GameBoard;
import Gameboard.Bitboard;

public class AlphaBetaNoCloneing extends Searchalgorithm{
    // private static TranspositionTable table = new TranspositionTable(2000000,
    // replaceStrategy);
    private boolean cancel       = false;
    private byte    countOfMoves = 0;

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
        int bestValue;
        int value;
        long bestMove = 0;
        for (int i = 1; !cancel; i++){
            bestValue = -30065;
            long tmpBestMove = 0;
            int tmpMoveNr = 0;
            for (int j = 0; j < possibleMoves.length; j++){
                long coord = possibleMoves[j];
                nextBoard = (Bitboard) gb.clone();
                nextBoard.makeMove(true, coord);
                value = min(nextBoard, -30065, 30065, i - 1);
                if(value > bestValue){
                    bestValue = value;
                    tmpBestMove = coord;
                    tmpMoveNr = j;
                }
                if(cancel){
                    return bestMove;
                }
            }
            if(i > 35 - countOfMoves){
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
                    return 30000 + stonesRed - stonesGreen;
                }else if(stonesRed < stonesGreen){
                    searchedNodesCount++;
                    return -30000 - stonesGreen + stonesRed;
                }else{
                    searchedNodesCount++;
                    return 0;
                }
            }
            return min(gb, alpha, beta, depth - 1);
        }
        if(depth <= 0){
            searchedNodesCount++;
            evaluatedNodesCount++;
            return evaluator.evaluate(gb, possibleMoves, true);
        }
        searchedNodesCount++;
        long changedFields = 0;
        int value;
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
                    return beta;
                }
            }
            if(cancel){
                return maxValue;
            }
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
                    return 30000 + stonesRed - stonesGreen;
                }else if(stonesRed < stonesGreen){
                    searchedNodesCount++;
                    return -30000 - stonesGreen + stonesRed;
                }else{
                    searchedNodesCount++;
                    return 0;
                }
            }
            return max(gb, alpha, beta, depth - 1);
        }
        if(depth <= 0){
            searchedNodesCount++;
            evaluatedNodesCount++;
            return evaluator.evaluate(gb, possibleMoves, false);
        }
        searchedNodesCount++;
        int value;
        long changedFields = 0;
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
                    return alpha;
                }
            }
            if(cancel){
                return minValue;
            }
        }
        return minValue;
    }
}
