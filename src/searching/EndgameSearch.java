package searching;

import Gameboard.Bitboard;

public class EndgameSearch{
    long nextMove(Bitboard gb, boolean player){
        return 0;
    }

    public static class OutcomeSearch{
        public static int  outcome;
        public static long nodeCount;
        public static int  remainingStones;

        public final static long nextMove(Bitboard gb){
            nodeCount = 0;
            remainingStones = gb.countStones(true) + gb.countStones(false);
            long possibleMoves = gb.getPossibleMoves(true);
            if(possibleMoves == 0){
                long possibleMovesEnemy = gb.getPossibleMoves(false);
                if(possibleMovesEnemy != 0){
                    outcome = (int) min(gb, (byte) -1, (byte) 1);
                }
                return 0;
            }
            long bestMove = Long.highestOneBit(possibleMoves);
            byte alpha = (byte) -1;
            while(possibleMoves != 0){
                long coords = Long.highestOneBit(possibleMoves);
                long changedFields = gb.makeMove(true, coords);
                byte value = min(gb, alpha, (byte) 1);
                gb.undoMove(changedFields, coords, true);
                if(value == 1){
                    outcome = 1;
                    return coords;
                }
                if(value == 0){
                    outcome = 0;
                    bestMove = coords;
                    alpha = 0;
                }
                possibleMoves ^= coords;
            }
            outcome = -1;
            return bestMove;
        }

        final static byte min(Bitboard gb, byte alpha, byte beta){
            nodeCount++;
            long possibleMoves = gb.getPossibleMoves(false);
            if(possibleMoves == 0){
                if(gb.getPossibleMoves(true) == 0){
                    int myStones = gb.countStones(false);
                    int opponentStones = gb.countStones(true);
                    if(myStones > opponentStones){
                        return (byte) -1;
                    }
                    else if(opponentStones > myStones){
                        return (byte) 1;
                    }
                    else{
                        return 0;
                    }
                }
                return max(gb, alpha, beta);
            }
            if(alpha == -1 && beta == 1){
                do{
                    long coords = Long.highestOneBit(possibleMoves);
                    long changedFields = gb.makeMove(false, coords);
                    byte value = max(gb, alpha, beta);
                    gb.undoMove(changedFields, coords, false);
                    if(value == -1){
                        return -1;
                    }
                    possibleMoves ^= coords;
                    if(value == 0){
                        beta = 0;
                        break;
                    }
                }while(possibleMoves != 0);
            }
            if(beta == 0){
                while(possibleMoves != 0){
                    long coords = Long.highestOneBit(possibleMoves);
                    long changedFields = gb.makeMove(false, coords);
                    if(max(gb, (byte) -1, (byte) 0) == -1){
                        gb.undoMove(changedFields, coords, false);
                        return -1;
                    }
                    gb.undoMove(changedFields, coords, false);
                    possibleMoves ^= coords;
                }
                return 0;
            }
            if(alpha == 0){
                do{
                    long coords = Long.highestOneBit(possibleMoves);
                    long changedFields = gb.makeMove(false, coords);
                    if(max(gb, (byte) 0, (byte) 1) <= 0){
                        gb.undoMove(changedFields, coords, false);
                        return 0;
                    }
                    gb.undoMove(changedFields, coords, false);
                    possibleMoves ^= coords;
                }while(possibleMoves != 0);
                return 1;
            }
            return 1;
        }

        final static byte max(Bitboard gb, byte alpha, byte beta){
            nodeCount++;
            long possibleMoves = gb.getPossibleMoves(true);
            if(possibleMoves == 0){
                if(gb.getPossibleMoves(false) == 0){
                    int myStones = gb.countStones(true);
                    int opponentStones = gb.countStones(false);
                    if(myStones > opponentStones){
                        return (byte) 1;
                    }
                    else if(opponentStones > myStones){
                        return (byte) -1;
                    }
                    else{
                        return 0;
                    }
                }
                return min(gb, alpha, beta);
            }
            if(alpha == -1 && beta == 1){
                do{
                    long coords = Long.highestOneBit(possibleMoves);
                    long changedFields = gb.makeMove(true, coords);
                    byte value = min(gb, alpha, beta);
                    gb.undoMove(changedFields, coords, true);
                    if(value == 1){
                        return 1;
                    }
                    possibleMoves ^= coords;
                    if(value == 0){
                        alpha = 0;
                        break;
                    }
                }while(possibleMoves != 0);
            }
            if(alpha == 0){
                while(possibleMoves != 0){
                    long coords = Long.highestOneBit(possibleMoves);
                    long changedFields = gb.makeMove(true, coords);
                    if(min(gb, (byte) 0, (byte) 1) == 1){
                        gb.undoMove(changedFields, coords, true);
                        return 1;
                    }
                    gb.undoMove(changedFields, coords, true);
                    possibleMoves ^= coords;
                }
                return 0;
            }
            if(beta == 0){
                do{
                    long coords = Long.highestOneBit(possibleMoves);
                    long changedFields = gb.makeMove(true, coords);
                    if(min(gb, (byte) -1, (byte) 0) >= 0){
                        gb.undoMove(changedFields, coords, true);
                        return 0;
                    }
                    gb.undoMove(changedFields, coords, true);
                    possibleMoves ^= coords;
                }while(possibleMoves != 0);
                return -1;
            }
            return -1;
        }
    }

    public static class ExactSearch{

    }
}
