package TestablePlayers;

import reversi.Coordinates;
import reversi.GameBoard;
import searching.EndgameSearch;
import searching.Searchalgorithm;
import Gameboard.Bitboard;
import Testing.ITestablePlayer;
import evaluate.IEvaluator;
import evaluate.StrategicEvaluator;

public class PlayerA implements ITestablePlayer{
    private int            myColor;
    private long           timeLimit;
    public Searchalgorithm searchalgorithm;
    public IEvaluator      evaluator = new StrategicEvaluator();
    public String          name      = "alphabetaTT endgame";

    @Override
    public void initialize(int myColor, long timeLimit){
        this.myColor = myColor;
        this.timeLimit = timeLimit;
        searchalgorithm = new searching.AlphabetaTT();
        searchalgorithm.evaluator = evaluator;
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
        if((64 - gb.countStones(true) - gb.countStones(false)) <= 4){
            System.out.println("Endgamesearch started:");
            long time = System.nanoTime();
            System.out.println("Red: " + gb.red + "L  Green: " + gb.green + "L");
            long move = EndgameSearch.OutcomeSearch.nextMove(gb);
            System.out.println("Zug: " + move + "L");
            System.out.println("Search took " + (System.nanoTime() - time)
                    / 1000000 + " ms.");
            System.out.println("Searched "
                    + EndgameSearch.OutcomeSearch.nodeCount + " Nodes.");
            switch(EndgameSearch.OutcomeSearch.outcome){
                case 1:
                    System.out.println("I will win.\n");
                    break;
                case 0:
                    System.out.println("Draw\n");
                default:
                    System.out.println("I will probably loose.\n");
                    break;
            }
            return move;
        }
        searchalgorithm.deadline = System.nanoTime() + timeLimit * 1000000
                - 20000000;
        System.out.println("Player A searching...");
        return searchalgorithm.nextMove(gb);
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