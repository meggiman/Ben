package TestablePlayers;

import reversi.Coordinates;
import reversi.GameBoard;
import searching.AlphaBetaNoah;
import searching.Searchalgorithm;
import Gameboard.Bitboard;
import Testing.ITestablePlayer;
import evaluate.StrategicEvaluatorNoah;

public class PlayerNoah implements ITestablePlayer{

    Searchalgorithm      searcher;
    private final String name = "Noah";

    public PlayerNoah(){

    }

    @Override
    public void initialize(int color, long timeLimit){
        searcher = new AlphaBetaNoah(color, timeLimit, new StrategicEvaluatorNoah());
    }

    @Override
    public Coordinates nextMove(GameBoard gb){
        return Bitboard.longToCoordinates(searcher.nextMove(Bitboard.convert(gb)));
    }

    @Override
    public String getName(){
        return name;
    }

    @Override
    public long getNodesCount(){
        return searcher.searchedNodesCount;
    }

    @Override
    public long getEvaluatedNodes(){
        return searcher.evaluatedNodesCount;
    }

    @Override
    public int getDepthOfLatestSearch(){
        return searcher.reachedDepth;
    }

    @Override
    public int getValueOfLatestSearch(){
        return searcher.valueOfLastMove;
    }

    @Override
    public int getMoveNrOfLatestSearch(){
        return searcher.moveNr;
    }

    @Override
    public long getNrOfTTHits(){
        return searcher.TTHits;
    }

}
