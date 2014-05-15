package searching;

import reversi.GameBoard;
import Gameboard.Bitboard;

public class AlphaBeta extends Searchalgorithm{
    private boolean cancel = false;

    @Override
    public long nextMove(Bitboard gb){
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
            bestValue = -10065;
            for (int j = 0; j < possibleMoves.length; j++){
                long coord = possibleMoves[j];
                nextBoard = (Bitboard) gb.clone();
                nextBoard.makeMove(true, coord);
                value = min(nextBoard, -10065, 10065, i - 1);
                if(value > bestValue){
                    bestValue = value;
                    bestMove = coord;
                }
                if(cancel || bestValue < -10000 || bestValue > 10000){
                    reachedDepth = i;
                    moveNr = j;
                    valueOfLastMove = bestValue;
                    return bestMove;
                }
            }
        }
        valueOfLastMove = bestValue;
        return bestMove;
    }

    private int max(Bitboard gb, int alpha, int beta, int depth){
        if(cancel){
            return beta;
        }
        else if(System.currentTimeMillis() >= deadline){
            cancel = true;
            return beta;
        }
        int maxValue = alpha;
        long possibleMoves = gb.getPossibleMoves(true);
        if(possibleMoves == 0 && gb.getPossibleMoves(false) == 0){
            int stonesRed = gb.countStones(GameBoard.RED);
            int stonesGreen = gb.countStones(GameBoard.GREEN);
            if(stonesRed > stonesGreen){
                searchedNodesCount++;
                return 10000 + stonesRed - stonesGreen;
            }
            else if(stonesRed < stonesGreen){
                searchedNodesCount++;
                return -10000 - stonesGreen + stonesRed;
            }
            else{
                searchedNodesCount++;
                return 0;
            }
        }
        if(depth <= 0){
            searchedNodesCount++;
            evaluatedNodesCount++;
            return evaluator.evaluate(gb, possibleMoves, true);
        }
        searchedNodesCount++;
        int value;
        long nextMove;
        Bitboard nextPosition;
        int count = Long.bitCount(possibleMoves);
        for (int i = 0; i < count; i++){
            nextMove = Long.lowestOneBit(possibleMoves);
            possibleMoves ^= nextMove;
            nextPosition = (Bitboard) gb.clone();
            nextPosition.makeMove(true, nextMove);
            value = min(nextPosition, maxValue, beta, depth - 1);
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

    private int min(Bitboard gb, int alpha, int beta, int depth){
        if(cancel){
            return alpha;
        }
        else if(System.currentTimeMillis() >= deadline){
            cancel = true;
            return alpha;
        }
        int minValue = beta;
        long possibleMoves = gb.getPossibleMoves(false);
        if(possibleMoves == 0 && gb.getPossibleMoves(true) == 0){
            int stonesRed = gb.countStones(GameBoard.RED);
            int stonesGreen = gb.countStones(GameBoard.GREEN);
            if(stonesRed > stonesGreen){
                searchedNodesCount++;
                return 10000 + stonesRed;
            }
            else if(stonesRed < stonesGreen){
                searchedNodesCount++;
                return -10000 - stonesGreen;
            }
            else{
                searchedNodesCount++;
                return 0;
            }
        }
        if(depth <= 0){
            searchedNodesCount++;
            evaluatedNodesCount++;
            return evaluator.evaluate(gb, possibleMoves, true);
        }
        searchedNodesCount++;
        int value;
        long nextMove;
        Bitboard nextPosition;
        int count = Long.bitCount(possibleMoves);
        for (int i = 0; i < count; i++){
            nextMove = Long.lowestOneBit(possibleMoves);
            possibleMoves ^= nextMove;
            nextPosition = (Bitboard) gb.clone();
            nextPosition.makeMove(false, nextMove);
            value = max(nextPosition, alpha, minValue, depth - 1);
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
