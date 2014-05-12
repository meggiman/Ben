package Players;

import reversi.Coordinates;
import reversi.GameBoard;
import reversi.ReversiPlayer;
import searching.AlphaBetaNegamax;
import Gameboard.Bitboard;

public class Negamaxplayer implements ReversiPlayer{
    private long myColor;

    @Override
    public void initialize(int myColor, long timeLimit){
        // TODO Automatisch generierter Methodenstub
        this.myColor = myColor;
    }

    public long nextMove(Bitboard gb){
        if(myColor == GameBoard.GREEN){
            long temp = gb.green;
            gb.green = gb.red;
            gb.red = temp;
        }
        return negamax(gb, 10);
    }

    @Override
    public Coordinates nextMove(GameBoard gb){
        return Bitboard.longToCoordinates(nextMove(Bitboard.convert(gb)));
    }

    private long negamax(Bitboard gb, int depth){
        long[] possiblemoves = Bitboard.serializeBitboard(gb.getPossibleMoves(true));
        if(possiblemoves.length == 0){
            return 0;
        }
        Bitboard nextboard;
        Bitboard bestboard = null;
        int bestvalue = -100000;
        int temp;
        long bestmove = 0;
        long time = System.currentTimeMillis();
        for (long coord : possiblemoves){
            nextboard = (Bitboard) gb.clone();
            nextboard.makeMove(true, coord);
            temp = AlphaBetaNegamax.search(gb, false, depth - 1, -10000, 10000);
            if(temp > bestvalue){
                bestvalue = temp;
                bestboard = nextboard;
                bestmove = coord;
            }
        }
        gb.green = bestboard.green;
        gb.red = bestboard.red;
        System.out.println("Negamax: " + (System.currentTimeMillis() - time));
        return bestmove;
    }

}
