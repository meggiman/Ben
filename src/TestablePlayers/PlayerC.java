package TestablePlayers;

import reversi.Coordinates;
import reversi.GameBoard;
import searching.Searchalgorithm;
import searching.alphabetainterface;
import Gameboard.Bitboard;
import Testing.ITestablePlayer;
import evaluate.IEvaluator;
import evaluate.strategicevaluator;

public class PlayerC implements ITestablePlayer{
    private int            myColor;
    private long           timeLimit;
    public Searchalgorithm suchalgorithmus = new alphabetainterface();
    public IEvaluator      evaluator       = new strategicevaluator();
    public String          name            = null;

    @Override
    public void initialize(int myColor, long timeLimit){
        this.myColor = myColor;
        this.timeLimit = timeLimit;
        suchalgorithmus.evaluator = evaluator;
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
        suchalgorithmus.deadline = System.currentTimeMillis() + timeLimit - 20;
        System.out.println("PlayerC searching...");
        return suchalgorithmus.nextMove(gb);
    }

    @Override
    public String getName(){
        return name;
    }

    @Override
    public long getNodesCount(){
        return suchalgorithmus.searchedNodesCount;
    }

    @Override
    public long getEvaluatedNodes(){
        return suchalgorithmus.evaluatedNodesCount;
    }

    @Override
    public int getDepthOfLatestSearch(){
        return suchalgorithmus.reachedDepth;
    }

    @Override
    public int getValueOfLatestSearch(){
        return suchalgorithmus.valueOfLastMove;
    }

    @Override
    public int getMoveNrOfLatestSearch(){
        return suchalgorithmus.moveNr;
    }

    @Override
    public long getNrOfTTHits(){
        return suchalgorithmus.TTHits;
    }

}
