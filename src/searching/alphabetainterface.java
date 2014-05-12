package searching;

import reversi.GameBoard;
import Gameboard.Bitboard;

public class alphabetainterface extends Searchalgorithm {
	private boolean cancel = false;

	@Override
	public long nextMove(Bitboard gb) {
		cancel = false;
		evaluatedNodesCount = 0;
		searchedNodesCount = 0;
		TTHits = 0;
		long[] possiblemoves = Bitboard.serializeBitboard(gb.getPossibleMoves(true));
		if (possiblemoves.length == 0) {
			return 0;
		}
		Bitboard nextboard;
		int bestvalue=-10000;
		int value;
		long bestmove  = 0;
		//Schleife bricht durch verstreichen des Zeitlimits intern ab.
		for (int i = 1; true ; i++) {
			bestvalue = -10000;
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
					reachedDepth = i;
					moveNr = j;
					return bestmove;
				}
			}
			if (cancel) {
				return bestmove;
			}
		}
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
		long possiblemoves = gb.getPossibleMoves(true);
		if (possiblemoves==0 && gb.getPossibleMoves(false)==0) {
			int stonesred = gb.countStones(GameBoard.RED);
			int stonesgreen = gb.countStones(GameBoard.GREEN);
			if (stonesred>stonesgreen) {
				searchedNodesCount++;
				return 10000+stonesred-stonesgreen;
			}
			else if (stonesred<stonesgreen){
				searchedNodesCount++;
				return -10000-stonesgreen+stonesred;
			}
			else {
				searchedNodesCount++;
				return 0;
			}
		}
		if (depth<=0) {
			searchedNodesCount++;
			evaluatedNodesCount++;
			return evaluator.evaluate(gb,possiblemoves,true);
		}
		searchedNodesCount++;
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
	
	private int min(Bitboard gb, int alpha, int beta, int depth) {
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
				searchedNodesCount++;
				return 10000+stonesred;
			}
			else if (stonesred<stonesgreen){
				searchedNodesCount++;
				return -10000-stonesgreen;
			}
			else {
				searchedNodesCount++;
				return 0;
			}
		}
		if (depth<=0) {
			searchedNodesCount++;
			evaluatedNodesCount++;
			return evaluator.evaluate(gb,possiblemoves,true);
		}
		searchedNodesCount++;
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


