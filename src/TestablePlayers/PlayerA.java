package TestablePlayers;

import Gameboard.Bitboard;
import Testing.ITestablePlayer;
import reversi.Coordinates;
import reversi.GameBoard;
import reversi.ReversiPlayer;
import searching.*;

public class PlayerA implements ITestablePlayer{
	private long timeLimit;
	private long myColor;
	private int reacheddepth = 0;
	private int movenr;
	private int bestvalue;
	private final String name = "Minmaxplayer cloning";
	


	@Override
	public void initialize(int myColor, long timeLimit) {
		this.myColor = myColor;
		this.timeLimit = timeLimit;
	}
	
	
	public long nextMove(Bitboard gb) {
		if (myColor == GameBoard.GREEN) {
			long temp = gb.green;
			gb.green = gb.red;
			gb.red = temp;
		}
		System.out.println("PlayerA searching");
		return alphabeta(gb,30);
	}

	
	@Override
	public Coordinates nextMove(GameBoard gb) {
		return Bitboard.longtoCoordinates(nextMove(Bitboard.convert(gb)));
	}

	/**
	 * Benutzt den Alpha-Beta-Algorithmus um den besten Zug zu finden.
	 * @param gb Das zu anaysierende {@link Bitboard}. Das Objekt wird verändert und enthält nach der Suche den nächsten Spielzustand.
	 * @param depth Die Iterationstiefe der Suche.
	 * @return Die Repräsentation des Zuges als {@link long}.
	 */
	private long alphabeta(Bitboard gb, int depth) {
		alphabeta.deadline = System.currentTimeMillis()+timeLimit-20;
		alphabeta.cancel = false;
		alphabeta.evaluatednodes = 0;
		alphabeta.searchednodes = 0;
		alphabeta.TTHits = 0;
		
		long[] possiblemoves = Bitboard.bitboardserialize(gb.possiblemoves(true));
		if (possiblemoves.length == 0) {
			return 0;
		}
		Bitboard nextboard;
		bestvalue=-10000;
		int value;
		long bestmove  = 0;
		for (int i = 1; i < depth; i++) {
			bestvalue = -10000;
			for (int j = 0; j < possiblemoves.length;j++) {
				long coord = possiblemoves[j];
				nextboard = (Bitboard) gb.clone();
				nextboard.makeMove(true, coord);
				value = alphabeta.min(nextboard, -10065, 10065, i-1);
				if (value > bestvalue) {
					bestvalue = value;
					bestmove = coord;
				}
				if (alphabeta.cancel) {
					reacheddepth = i;
					movenr = j;
					return bestmove;
				}
			}
		}
		return bestmove;
	}


	@Override
	public long getnodescount() {
		return alphabeta.searchednodes;
	}


	@Override
	public long getevaluatednodes() {
		return alphabeta.evaluatednodes;
	}


	@Override
	public int getdepthoflatestsearch() {
		return reacheddepth;
	}


	@Override
	public int getvalueoflatestsearch() {
		return bestvalue;
	}


	@Override
	public int getmoveNroflatestSearch() {
		return movenr;
	}


	@Override
	public long getNrofTTHits() {
		return 0;
	}


	@Override
	public String getname() {
		return name;
	}
}
