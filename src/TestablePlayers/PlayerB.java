package TestablePlayers;

import reversi.Coordinates;
import reversi.GameBoard;
import searching.alphabetanocloneing;
import Gameboard.Bitboard;
import Testing.ITestablePlayer;

public class PlayerB implements ITestablePlayer{
    private long         timeLimit;
    private long         myColor;
    private int          reacheddepth = 0;
    private int          movenr;
    private int          bestvalue;
    private final String name         = "Minmaxplayer without cloning";

    @Override
    public void initialize(int myColor, long timeLimit){
        this.myColor = myColor;
        this.timeLimit = timeLimit;
    }

    public long nextMove(Bitboard gb){
        if(myColor == GameBoard.GREEN){
            long temp = gb.green;
            gb.green = gb.red;
            gb.red = temp;
        }
        return alphabetanocloneing(gb, 30);
    }

    @Override
    public Coordinates nextMove(GameBoard gb){
        return Bitboard.longToCoordinates(nextMove(Bitboard.convert(gb)));
    }

    /**
     * Benutzt den Alpha-Beta-Algorithmus um den besten Zug zu finden.
     * 
     * @param gb
     *            Das zu anaysierende {@link Bitboard}. Das Objekt wird
     *            ver�ndert und enth�lt nach der Suche den n�chsten
     *            Spielzustand.
     * @param depth
     *            Die Iterationstiefe der Suche.
     * @return Die Repr�sentation des Zuges als {@link long}.
     */
    private long alphabetanocloneing(Bitboard gb, int depth){
        alphabetanocloneing.deadline = System.currentTimeMillis() + timeLimit
                - 20;
        alphabetanocloneing.cancel = false;
        alphabetanocloneing.evaluatednodes = 0;
        alphabetanocloneing.searchednodes = 0;
        alphabetanocloneing.TTHits = 0;
        long[] possiblemoves = Bitboard.serializeBitboard(gb.getPossibleMoves(true));
        if(possiblemoves.length == 0){
            return 0;
        }
        Bitboard nextboard;
        bestvalue = -10000;
        int value;
        long bestmove = 0;
        for (int i = 1; i < depth; i++){
            bestvalue = -10000;
            for (int j = 0; j < possiblemoves.length; j++){
                long coord = possiblemoves[j];
                nextboard = (Bitboard) gb.clone();
                nextboard.makeMove(true, coord);
                value = alphabetanocloneing.min(nextboard, -10065, 10065, i - 1);
                if(value > bestvalue){
                    bestvalue = value;
                    bestmove = coord;
                }
                if(alphabetanocloneing.cancel){
                    reacheddepth = i;
                    movenr = j;
                    return bestmove;
                }
            }
        }
        return bestmove;
    }

    @Override
    public long getNodesCount(){
        return alphabetanocloneing.searchednodes;
    }

    @Override
    public long getEvaluatedNodes(){
        return alphabetanocloneing.evaluatednodes;
    }

    @Override
    public int getDepthOfLatestSearch(){
        return reacheddepth;
    }

    @Override
    public int getValueOfLatestSearch(){
        return bestvalue;
    }

    @Override
    public int getMoveNrOfLatestSearch(){
        return movenr;
    }

    @Override
    public long getNrOfTTHits(){
        return 0;
    }

    @Override
    public String getName(){
        return name;
    }
}
