package TestablePlayers;

import evaluate.IEvaluator;
import evaluate.strategicevaluator;
import reversi.Coordinates;
import reversi.GameBoard;
import searching.EndgameSearch;
import searching.Searchalgorithm;
import searching.alphabeta;
import searching.alphabetanocloneing;
import Gameboard.Bitboard;
import Testing.ITestablePlayer;

public class PlayerA implements ITestablePlayer {
	private int myColor;
	private long timeLimit;
	public Searchalgorithm suchalgorithmus;
	public IEvaluator evaluator = new strategicevaluator();
	public String name = "alphabetaTT endgame";

	@Override
	public void initialize(int myColor, long timeLimit) {
		this.myColor = myColor;
		this.timeLimit = timeLimit;
		suchalgorithmus = new searching.AlphabetaTT();
		suchalgorithmus.evaluator = evaluator;
	}

	@Override
	public Coordinates nextMove(GameBoard gb) {
		return Bitboard.longtoCoordinates(nextMove(Bitboard.convert(gb)));
	}
	public long nextMove(Bitboard gb){
		if (myColor == GameBoard.GREEN) {
			long temp = gb.green;
			gb.green = gb.red;
			gb.red = temp;
		}
		if ((64 - gb.countStones(true)-gb.countStones(false)) <= 8) {
			System.out.println("Endgamesearch started:");
			long time = System.nanoTime();
			System.out.println("Red: "+gb.red+"L  Green: "+gb.green+"L");
			long move = EndgameSearch.OutcomeSearch.nextMove(gb);
			System.out.println("Zug: "+move+"L");
			System.out.println("Search took "+(System.nanoTime()-time)/1000000+" ms.");
			System.out.println("Searched "+EndgameSearch.OutcomeSearch.nodecount+" Nodes.");
			switch (EndgameSearch.OutcomeSearch.outcome) {
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
		suchalgorithmus.deadline=System.nanoTime()+timeLimit*1000000-20000000;
		System.out.println("Player A searching...");
		return suchalgorithmus.nextmove(gb);
	}

	@Override
	public String getname() {
		return name;
	}

	@Override
	public long getnodescount() {
		return suchalgorithmus.searchednodes;
	}

	@Override
	public long getevaluatednodes() {
		return suchalgorithmus.evaluatednodes;
	}

	@Override
	public int getdepthoflatestsearch() {
		return suchalgorithmus.reacheddepth;
	}

	@Override
	public int getvalueoflatestsearch() {
		return suchalgorithmus.valueoflastmove;
	}

	@Override
	public int getmoveNroflatestSearch() {
		return suchalgorithmus.movenr;
	}

	@Override
	public long getNrofTTHits() {
		return suchalgorithmus.TTHits;
	}

}
