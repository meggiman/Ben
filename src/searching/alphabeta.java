package searching;

import reversi.GameBoard;
import Gameboard.Bitboard;

public class alphabeta extends Searchalgorithm {
	private boolean cancel = false;

	@Override
	public long nextmove(Bitboard gb) {
		cancel = false;
		evaluatednodes = 0;
		searchednodes = 0;
		TTHits = 0;
		long[] possiblemoves = Bitboard.bitboardserialize(gb.possiblemoves(true));
		if (possiblemoves.length == 0) {
			return 0;
		}
		Bitboard nextboard;
		int bestvalue;
		int value;
		long bestmove  = 0;
		for (int i = 1; !cancel ; i++) {
			bestvalue = -10065;
			for (int j = 0; j < possiblemoves.length;j++) {
				long coord = possiblemoves[j];
				nextboard = (Bitboard) gb.clone();
				nextboard.makeMove(true, coord);
				value = min(nextboard, -10065, 10065, i-1);
				if (value > bestvalue) {
					bestvalue = value;
					bestmove = coord;
				}
				if (cancel) {
					reacheddepth = i;
					movenr = j;
					return bestmove;
				}
			}
		}
		return bestmove;
	}
	
	private int max(Bitboard gb, int alpha, int beta, int depth){
		if (cancel) {
			return beta;
		}
		else if (System.currentTimeMillis() >= deadline) {
			cancel = true;
			return beta;
		}
		int maxvalue = alpha;
		long possiblemoves = gb.possiblemoves(true);
		if (possiblemoves==0 && gb.possiblemoves(false)==0) {
			int stonesred = gb.countStones(GameBoard.RED);
			int stonesgreen = gb.countStones(GameBoard.GREEN);
			if (stonesred>stonesgreen) {
				searchednodes++;
				return 10000+stonesred-stonesgreen;
			}
			else if (stonesred<stonesgreen){
				searchednodes++;
				return -10000-stonesgreen+stonesred;
			}
			else {
				searchednodes++;
				return 0;
			}
		}
		if (depth<=0) {
			searchednodes++;
			evaluatednodes++;
			return evaluator.evaluate(gb,possiblemoves,true);
		}
		searchednodes++;
		Bitboard[] movelist = sortmovesred(gb, possiblemoves);
		int value;
		for (int i = 0; i < movelist.length; i++) {
			value = min(movelist[i], maxvalue, beta, depth-1);
			if (value > maxvalue) {
				maxvalue = value;
				if (value>= beta) {
					return beta;
				}
			}
			if (cancel) {
				return maxvalue;
				
			}
		}
		long nextmove;
		Bitboard nextposition;
		int count = Long.bitCount(possiblemoves);
		for (int i = movelist.length; i < count; i++) {
			nextmove = Long.lowestOneBit(possiblemoves);
			possiblemoves ^= nextmove;
			nextposition = (Bitboard)gb.clone();
			nextposition.makeMove(true, nextmove);
			value = min(nextposition, maxvalue, beta, depth-1);
			if (value > maxvalue) {
				maxvalue = value;
				if (value>= beta) {
					return beta;
				}
			}
			if (cancel) {
				return maxvalue;
			}
		}
		return maxvalue;
	}
	
	private int min(Bitboard gb, int alpha, int beta, int depth) {
		if (cancel) {
			return alpha;
		}
		else if (System.currentTimeMillis() >= deadline) {
			cancel = true;
			return alpha;
		}
		int minvalue = beta;
		long possiblemoves = gb.possiblemoves(false);
		if (possiblemoves==0 && gb.possiblemoves(true)==0) {
			int stonesred = gb.countStones(GameBoard.RED);
			int stonesgreen = gb.countStones(GameBoard.GREEN);
			if (stonesred>stonesgreen) {
				searchednodes++;
				return 10000+stonesred;
			}
			else if (stonesred<stonesgreen){
				searchednodes++;
				return -10000-stonesgreen;
			}
			else {
				searchednodes++;
				return 0;
			}
		}
		if (depth<=0) {
			searchednodes++;
			evaluatednodes++;
			return evaluator.evaluate(gb,possiblemoves,true);
		}
		searchednodes++;
		Bitboard[] movelist = sortmovesgreen(gb, possiblemoves);
		int value;
		for (int i = 0; i < movelist.length; i++) {
			value = max(movelist[i], alpha, minvalue, depth-1);
			if (value < minvalue) {
				minvalue = value;
				if (value <= alpha) {
					return alpha;
				}
			}
			if (cancel) {
				return minvalue;
			}
		}
		long nextmove;
		Bitboard nextposition;
		int count = Long.bitCount(possiblemoves);
		for (int i = movelist.length; i < count; i++) {
			nextmove = Long.lowestOneBit(possiblemoves);
			possiblemoves ^= nextmove;
			nextposition = (Bitboard)gb.clone();
			nextposition.makeMove(false, nextmove);
			value = max(nextposition, alpha, minvalue, depth-1);
			if (value < minvalue) {
				minvalue = value;
				if (value<= alpha) {
					return alpha;
				}
			}
			if (cancel) {
				return minvalue;
			}
		}
		return minvalue;
	}
		
	private static Bitboard[] sortmovesred(Bitboard gb, long possiblemoves){
		Bitboard[] move = new Bitboard[]{(Bitboard)gb.clone()};
		move[0].makeMove(true, Long.highestOneBit(possiblemoves));
		return move;
	}
	
	private static Bitboard[] sortmovesgreen(Bitboard gb, long possiblemoves){
		Bitboard[] move = new Bitboard[]{(Bitboard)gb.clone()};
		move[0].makeMove(false, Long.highestOneBit(possiblemoves));
		return move;
	}
}


