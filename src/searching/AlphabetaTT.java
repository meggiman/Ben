package searching;

import evaluate.IEvaluator;
import evaluate.strategicevaluator;
import reversi.GameBoard;
import Gameboard.Bitboard;
import Tables.TranspositionTable;
import Tables.TranspositionTable.TableEntry;

public class AlphabetaTT extends Searchalgorithm {
	private TranspositionTable tt = new TranspositionTable(8000000, new TranspositionTable.pvnodepriority());
	private TableEntry newEntry = new TableEntry();
	public IEvaluator evaluator = new strategicevaluator();
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
		int bestvalue = 0;
		int value;
		long bestmove  = 0;
		for (int i = 1; !cancel ; i++) {
			bestvalue = -20065;
			long tmpbestmove = 0;
			int tmpmovenr = 0;
			for (int j = 0; j < possiblemoves.length;j++) {
				long coord = possiblemoves[j];
				nextboard = (Bitboard) gb.clone();
				nextboard.makeMove(true, coord);
				value = min(nextboard, -10065, 10065, i-1);
				if (value > bestvalue) {
					bestvalue = value;
					tmpbestmove = coord;
					tmpmovenr = j;
				}
				if (cancel) {
					return bestmove;
				}
			}
			if (i>65 - countofmoves) {
				cancel = true;
			}
			bestmove = tmpbestmove;
			valueoflastmove = bestvalue;
			movenr = tmpmovenr;
			reacheddepth = i;
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
		//Look for transposition in transposition table
		TranspositionTable.TableEntry entry = tt.get(gb.hash);
		if (entry!=null) {
			if (entry.isExact && entry.depth>=depth) {
				TTHits++;
				return entry.value;
			}
		}
		
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
		//Look for transpositions in transposition table
		TranspositionTable.TableEntry entry = tt.get(gb.hash);
		if (entry!=null) {
			if (entry.isExact && entry.depth>=depth) {
				TTHits++;
				return entry.value;
			}
		}
			
		if (depth<=0) {
			searchednodes++;
			evaluatednodes++;
			int value = evaluator.evaluate(gb, possiblemoves, true);
			TableEntry.recycleEntry(newEntry, (short)value, (byte)depth, true, false, countofmoves);
			tt.put(gb.hash, newEntry);
			return value;
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
