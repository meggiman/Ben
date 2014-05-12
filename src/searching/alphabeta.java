package searching;

import Gameboard.Bitboard;
import Tables.TranspositionTable;
import evaluate.IEvaluator;
import evaluate.strategicevaluator;
import reversi.GameBoard;

public class alphabeta {
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
	
	
	public static Bitboard[] sortmovesred(Bitboard gb, long possiblemoves){
		Bitboard[] move = new Bitboard[]{(Bitboard)gb.clone()};
		move[0].makeMove(true, Long.highestOneBit(possiblemoves));
		return move;
	}
	public static Bitboard[] sortmovesgreen(Bitboard gb, long possiblemoves){
		Bitboard[] move = new Bitboard[]{(Bitboard)gb.clone()};
		move[0].makeMove(false, Long.highestOneBit(possiblemoves));
		return move;
	}
}
