package TestablePlayers;

import evaluate.IEvaluator;
import evaluate.strategicevaluator;
import reversi.Coordinates;
import reversi.GameBoard;
import searching.AlphabetaTT;
import searching.Searchalgorithm;
import searching.alphabeta;
import searching.alphabetanocloneing;
import Gameboard.Bitboard;
import Gameboard.Bitboard_lookup_Table;
import Gameboard.Tables;
import Testing.ITestablePlayer;

public class PlayerB implements ITestablePlayer {
	private int myColor;
	private long timeLimit;
	public Searchalgorithm suchalgorithmus = new AlphabetaTT();
	public IEvaluator evaluator = new strategicevaluator();
	public String name = "alphabetaTT lookup Bitboard";
	

	@Override
	public void initialize(int myColor, long timeLimit) {
		this.myColor = myColor;
		this.timeLimit = timeLimit;
		suchalgorithmus = new searching.AlphabetaTT();
		suchalgorithmus.evaluator = evaluator;
		Tables.generateTables();
	}

	@Override
	public Coordinates nextMove(GameBoard gb) {
		return Bitboard.longtoCoordinates(nextMove(Bitboard_lookup_Table.convert(gb)));
	}
	public long nextMove(Bitboard gb){
		if (myColor == GameBoard.GREEN) {
			long temp = gb.green;
			gb.green = gb.red;
			gb.red = temp;
		}
		suchalgorithmus.deadline=System.nanoTime()+timeLimit*1000000-20000000;
		System.out.println("Player B searching...");
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
