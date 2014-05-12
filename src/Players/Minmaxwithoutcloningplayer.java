package Players;

import Gameboard.Bitboard;
import reversi.Coordinates;
import reversi.GameBoard;
import reversi.ReversiPlayer;
import searching.alphabetanocloneing;

public class Minmaxwithoutcloningplayer implements ReversiPlayer {
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
		return Bitboard.longToCoordinates(nextMove(Bitboard.convert(gb)));
	}

	/**
	 * Benutzt den Alpha-Beta-Algorithmus um den besten Zug zu finden.
	 * @param gb Das zu anaysierende {@link Bitboard}. Das Objekt wird ver�ndert und enth�lt nach der Suche den n�chsten Spielzustand.
	 * @param depth Die Iterationstiefe der Suche.
	 * @return Die Repr�sentation des Zuges als {@link long}.
	 */
	private long alphabeta(Bitboard gb, int depth) {
		alphabetanocloneing.deadline = System.currentTimeMillis()+timeLimit-100;
		alphabetanocloneing.cancel = false;
		long[] possiblemoves = Bitboard.serializeBitboard(gb.getPossibleMoves(true));
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
				temp = alphabetanocloneing.min(nextboard, -1065, 1065, i-1);
				if (temp > bestvalue) {
					bestvalue = temp;
					bestboard = nextboard;
					bestmove = coord;
				}
				if (alphabetanocloneing.cancel) {
					System.out.println("Erreichte Tiefe: "+i);
					return bestmove;
				}
			}
		}
		if (bestvalue >=1000) {
			System.out.println("Minmaxplayerwithoutclones will win.");
		}
		gb.green = bestboard.green;
		gb.red = bestboard.red;
		return bestmove;
	}
}
