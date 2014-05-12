package searching;

import Gameboard.Bitboard;
import reversi.GameBoard;
import evaluate.IEvaluator;
import evaluate.strategicevaluator;

public class alphabetanocloneing extends Searchalgorithm{
	public IEvaluator evaluator = new strategicevaluator();
	//private static TranspositionTable table = new TranspositionTable(2000000, replaceStrategy);
	private boolean cancel = false;
	private byte countofmoves = 0;
	public long nextmove(Bitboard gb) {
		countofmoves++;
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
			bestvalue = -20065;
			long tmpbestmove = 0;
			int tmpmovenr =0;
			for (int j = 0; j < possiblemoves.length;j++) {
				long coord = possiblemoves[j];
				nextboard = (Bitboard) gb.clone();
				nextboard.makeMove(true, coord);
				value = min(nextboard, -20065, 200065, i-1);
				if (value > bestvalue) {
					bestvalue = value;
					tmpbestmove = coord;
					tmpmovenr = j;
				}
				if (cancel) {
					return bestmove;
				}
			}
			if (i>35-countofmoves) {
				cancel = true;
			}
			bestmove = tmpbestmove;
			valueoflastmove = bestvalue;
			movenr = tmpmovenr;
			reacheddepth=i;
		}
		return bestmove;
	}
	
	public int max(Bitboard gb, int alpha, int beta, int depth){
		if (cancel) {
			return beta;
		}
		else if (System.nanoTime() >= deadline) {
			cancel = true;
			return beta;
		}
		int maxvalue = alpha;
		long possiblemoves = gb.possiblemoves(true);
		if (possiblemoves == 0) {
			if (gb.possiblemoves(false) == 0) {
				int stonesred = gb.countStones(GameBoard.RED);
				int stonesgreen = gb.countStones(GameBoard.GREEN);
				if (stonesred > stonesgreen) {
					searchednodes++;
					return 10000 + stonesred - stonesgreen;
				} else if (stonesred < stonesgreen) {
					searchednodes++;
					return -10000 - stonesgreen + stonesred;
				} else {
					searchednodes++;
					return 0;
				}
			}
			return min(gb, alpha, beta, depth-1);
		}
		if (depth<=0) {
			searchednodes++;
			evaluatednodes++;
			return evaluator.evaluate(gb,possiblemoves,true);
		}
		searchednodes++;
		long changedfields = 0;
		int value;
		long nextmove;
		int count = Long.bitCount(possiblemoves);
		for (int i = 0; i < count; i++) {
			nextmove = Long.lowestOneBit(possiblemoves);
			possiblemoves ^= nextmove;
			changedfields = gb.makeMove(true, nextmove);
			value = min(gb, maxvalue, beta, depth-1);
			gb.undomove(changedfields, nextmove, true);
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
	
	public int min(Bitboard gb, int alpha, int beta, int depth) {
		if (cancel) {
			return alpha;
		}
		else if (System.nanoTime() >= deadline) {
			cancel = true;
			return alpha;
		}
		int minvalue = beta;
		long possiblemoves = gb.possiblemoves(false);
		if (possiblemoves == 0) {
			if (gb.possiblemoves(true) == 0) {
				int stonesred = gb.countStones(GameBoard.RED);
				int stonesgreen = gb.countStones(GameBoard.GREEN);
				if (stonesred > stonesgreen) {
					searchednodes++;
					return 10000 + stonesred-stonesgreen;
				} else if (stonesred < stonesgreen) {
					searchednodes++;
					return -10000 - stonesgreen+stonesred;
				} else {
					searchednodes++;
					return 0;
				}
			}
			return max(gb, alpha, beta, depth-1);
		}
		if (depth<=0) {
			searchednodes++;
			evaluatednodes++;
			return evaluator.evaluate(gb,possiblemoves,true);
		}
		searchednodes++;
		int value;
		long changedfields = 0;
		long nextmove;
		int count = Long.bitCount(possiblemoves);
		for (int i = 0; i < count; i++) {
			nextmove = Long.lowestOneBit(possiblemoves);
			possiblemoves ^= nextmove;
			changedfields = gb.makeMove(false, nextmove);
			value = max(gb, alpha, minvalue, depth-1);
			gb.undomove(changedfields, nextmove, false);
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
