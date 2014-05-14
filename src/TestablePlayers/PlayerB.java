package TestablePlayers;

import reversi.Coordinates;
import reversi.GameBoard;
import searching.Searchalgorithm;
import Gameboard.Bitboard;
import Testing.ITestablePlayer;
import evaluate.StrategicEvaluatorNoah;

public class PlayerB implements ITestablePlayer{
    private int            myColor;
    private long           timeLimit;
    public Searchalgorithm searchalgorithm;
    public String          name = "alphabetaNC";

    @Override
    public void initialize(int myColor, long timeLimit){
        this.myColor = myColor;
        this.timeLimit = timeLimit;
        searchalgorithm = new searching.AlphaBetaNoCloneing();
        searchalgorithm.evaluator = new StrategicEvaluatorNoah();
    }

    @Override
    public Coordinates nextMove(GameBoard gb){
        return Bitboard.longToCoordinates(nextMove(Bitboard.convert(gb)));
    }

    public long nextMove(Bitboard gb){
        if(myColor == GameBoard.GREEN){
            long temp = gb.green;
            gb.green = gb.red;
            gb.red = temp;
        }
        searchalgorithm.deadline = System.nanoTime() + timeLimit * 1000000
                - 20000000;
        System.out.println("Player B searching...");
        long coord = searchalgorithm.nextMove(gb);
        System.out.println(searchalgorithm.searchedNodesCount);
        System.out.println(searchalgorithm.reachedDepth);
        System.out.println(searchalgorithm.valueOfLastMove);
        return coord;
    }

    @Override
    public String getName(){
        return name;
    }

    @Override
    public long getNodesCount(){
        return searchalgorithm.searchedNodesCount;
    }

    @Override
    public long getEvaluatedNodesCount(){
        return searchalgorithm.evaluatedNodesCount;
    }

    @Override
    public int getDepthOfLatestSearch(){
        return searchalgorithm.reachedDepth;
    }

    @Override
    public int getValueOfLatestSearch(){
        return searchalgorithm.valueOfLastMove;
    }

    @Override
    public int getMoveNrOfLatestSearch(){
        return searchalgorithm.moveNr;
    }

    @Override
    public long getNrOfTTHits(){
        return searchalgorithm.TTHits;
    }

}