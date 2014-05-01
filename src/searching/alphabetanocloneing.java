package searching;

import Gameboard.Bitboard;
import reversi.GameBoard;
import evaluate.IEvaluator;
import evaluate.strategicevaluator;

public class alphabetanocloneing {
	public static IEvaluator evaluator = new strategicevaluator();
	//private static TranspositionTable table = new TranspositionTable(2000000, replaceStrategy);
	public static long deadline;
	public static boolean cancel = false;
	public static long searchednodes = 0;
	public static long evaluatednodes = 0;
	public static long TTHits = 0;
	
	public static int max(Bitboard gb, int alpha, int beta, int depth){
		if (cancel) {
			return beta;
		}
		else if (System.currentTimeMillis() >= deadline) {
			cancel = true;
			return beta;
		}
		int maxvalue = alpha;
		long possiblemoves = gb.getPossibleMoves(true);
		if (possiblemoves==0 && gb.getPossibleMoves(false)==0) {
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
		long[] movelist = sortmovesred(gb, possiblemoves);
		long changedfields = 0;
		int value;
		for (int i = 0; i < movelist.length; i++) {
			changedfields = gb.makeMove(true, movelist[i]);
			value = min(gb, maxvalue, beta, depth-1);
			gb.undomove(changedfields, movelist[i], true);
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
			changedfields = gb.makeMove(true, nextmove);
			value = min(gb, maxvalue, beta, depth-1);
			gb.undomove(changedfields, nextmove, true);
			if (value > maxvalue) {
				maxvalue = value;
			}
			if (value>= beta) {
				return beta;
			}
			if (cancel) {
				return maxvalue;
			}
		}
		return maxvalue;
	}
	
	public static int min(Bitboard gb, int alpha, int beta, int depth) {
		if (cancel) {
			return alpha;
		}
		else if (System.currentTimeMillis() >= deadline) {
			cancel = true;
			return alpha;
		}
		int minvalue = beta;
		long possiblemoves = gb.getPossibleMoves(false);
		if (possiblemoves==0 && gb.getPossibleMoves(true)==0) {
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
		long[] movelist = sortmovesgreen(gb, possiblemoves);
		int value;
		long changedfields = 0;
		for (int i = 0; i < movelist.length; i++) {
			changedfields = gb.makeMove(false, movelist[i]);
			value = max(gb, alpha, minvalue, depth-1);
			gb.undomove(changedfields, movelist[i], false);
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
			changedfields = gb.makeMove(false, nextmove);
			value = max(gb, alpha, minvalue, depth-1);
			gb.undomove(changedfields, nextmove, false);
			if (value < minvalue) {
				minvalue = value;
			}
			if (value<= alpha) {
				return alpha;
			}
			if (cancel) {
				return minvalue;
			}
		}
		return minvalue;
	}
	
	
	public static long[] sortmovesred(Bitboard gb, long possiblemoves){
		long[] moves = new long[]{ Long.highestOneBit(possiblemoves)};
		return moves;
	}
	public static long[] sortmovesgreen(Bitboard gb, long possiblemoves){
		long[] moves = new long[]{ Long.highestOneBit(possiblemoves)};
		return moves;
	}
}
