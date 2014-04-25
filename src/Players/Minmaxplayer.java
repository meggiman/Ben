package Players;

import Gameboard.Bitboard;
import reversi.Coordinates;
import reversi.GameBoard;
import reversi.ReversiPlayer;
import searching.*;

public class Minmaxplayer implements ReversiPlayer{
	private long timeLimit;
	private long myColor;
	


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
		alphabeta.deadline = System.currentTimeMillis()+timeLimit-100;
		alphabeta.cancel = false;
		long[] possiblemoves = Bitboard.bitboardserialize(gb.possiblemoves(true));
		if (possiblemoves.length == 0) {
			return 0;
		}
		Bitboard nextboard;
		Bitboard bestboard = null;
		int bestvalue=-10000;
		int temp;
		long bestmove  = 0;
		for (int i = 1; i < depth; i++) {
			bestvalue = -10000;
			for (long coord : possiblemoves) {
				nextboard = (Bitboard) gb.clone();
				nextboard.makeMove(true, coord);
				temp = alphabeta.min(nextboard, -1065, 1065, i-1);
				if (temp > bestvalue) {
					bestvalue = temp;
					bestboard = nextboard;
					bestmove = coord;
				}
				if (alphabeta.cancel) {
					System.out.println("Erreichte Tiefe: "+i);
					return bestmove;
				}
			}
		}
		if (bestvalue >=1000) {
			System.out.println("Minmaxplayer will win.");
		}
		gb.green = bestboard.green;
		gb.red = bestboard.red;
		return bestmove;
	}
}
