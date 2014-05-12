package searching;

import evaluate.IEvaluator;
import evaluate.strategicevaluator;
import reversi.GameBoard;
import Gameboard.Bitboard;
import Tables.TranspositionTable;
import Tables.TranspositionTable.TableEntry;

public class AlphabetaTT extends Searchalgorithm {
	private static TranspositionTable tt = new TranspositionTable(8000000, new TranspositionTable.pvnodepriority());
	private static TableEntry newEntry = new TableEntry();
	public static IEvaluator evaluator = new strategicevaluator();
	private static boolean cancel = false;
	private static byte countofmoves = 0;
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
		//Look for transposition in transposition table
		TranspositionTable.TableEntry entry = tt.get(gb.hash);
//		if (entry!=null) {
//			if (entry.isExact && entry.depth>=depth) {
//				TTHits++;
//				return entry.value;
//			}
//		}
		
		if (depth<=0) {
			searchednodes++;
			evaluatednodes++;
			int value = evaluator.evaluate(gb, possiblemoves, true);
			TableEntry.recycleEntry(newEntry, (short)value, (byte)depth, true, false, countofmoves);
			tt.put(gb.hash, newEntry);
			return value;
		}
		searchednodes++;
		long changedfields = 0;
		int value;
		long pvnodekey = 0;
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
					TableEntry.recycleEntry(newEntry,(short)beta, (byte)depth, false, false, countofmoves);
					tt.put(gb.hash, newEntry);
					return beta;
				}
				TableEntry.recycleEntry(newEntry,(short)value, (byte)depth, true, false, countofmoves);
				pvnodekey = gb.hash;
				tt.put(pvnodekey, newEntry);
			}
			if (cancel) {
				return maxvalue;
			}
		}
		if (maxvalue!=alpha) {
			TableEntry.recycleEntry(newEntry,(short)maxvalue, (byte)depth, true, true, countofmoves);
			tt.put(pvnodekey, newEntry);			
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
		//Look for transpositions in transposition table
		TranspositionTable.TableEntry entry = tt.get(gb.hash);
//		if (entry!=null) {
//			if (entry.isExact && entry.depth>=depth) {
//				TTHits++;
//				return entry.value;
//			}
//		}
			
		if (depth<=0) {
			searchednodes++;
			evaluatednodes++;
			int value = evaluator.evaluate(gb, possiblemoves, true);
			TableEntry.recycleEntry(newEntry, (short)value, (byte)depth, true, false, countofmoves);
			tt.put(gb.hash, newEntry);
		}
		searchednodes++;
		int value;
		long changedfields = 0;
		long pvnodekey=0;
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
					TableEntry.recycleEntry(newEntry,(short)alpha, (byte)depth, false, false, countofmoves);
					tt.put(gb.hash, newEntry);
					return alpha;
				}
				TableEntry.recycleEntry(newEntry,(short)value, (byte)depth, true, false, countofmoves);
				pvnodekey = gb.hash;
				tt.put(pvnodekey, newEntry);
			}
			if (cancel) {
				return minvalue;
			}
		}
		if (minvalue!=beta) {
			TableEntry.recycleEntry(newEntry,(short)minvalue, (byte)depth, true, true, countofmoves);
			tt.put(pvnodekey, newEntry);
			}
		return minvalue;
	}
}
