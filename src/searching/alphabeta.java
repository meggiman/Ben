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
		int bestvalue = 0;
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
				if (cancel||bestvalue<-10000||bestvalue>10000) {
					reacheddepth = i;
					movenr = j;
					valueoflastmove = bestvalue;
					return bestmove;
				}
			}
		}
		valueoflastmove = bestvalue;
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
		int value;
		long nextmove;
		Bitboard nextposition;
		int count = Long.bitCount(possiblemoves);
		for (int i = 0; i < count; i++) {
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
		int value;
		long nextmove;
		Bitboard nextposition;
		int count = Long.bitCount(possiblemoves);
		for (int i = 0; i < count; i++) {
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
}


