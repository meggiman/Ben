package searching;

import java.util.Iterator;

import Gameboard.Bitboard;

public class EndgameSearch {
	long nextMove(Bitboard gb, boolean player){
		return 0;
	}
	public static class OutcomeSearch{
		public static int outcome;
		public static long nodecount;
		public static MoveList[] moveListPool = MoveList.generateListPool(30, 25);
		
		public final static long nextMove(Bitboard gb){
			nodecount = 0;
			int alpha = -1;
			int beta = 1;
			int remainingStones = 64 - gb.countStones(true) - gb.countStones(false);
			long possibleMoves = gb.possiblemoves(true);
			if (possibleMoves == 0) {
				long possibleMovesEnemy = gb.possiblemoves(false);
				if (possibleMovesEnemy != 0) {
					outcome = min(gb.red, gb.green, alpha, beta, remainingStones);
				}
				return 0;
			}
			long bestmove = Long.highestOneBit(possibleMoves);
			int bestvalue = alpha;
			do {
				long coord = Long.highestOneBit(possibleMoves);
				long changedfields = gb.makeMove(true, coord);
				int value = min(gb.red, gb.green, bestvalue, beta, remainingStones-1);
				gb.undomove(changedfields, coord, true);
				if (value > bestvalue) {
					if (value >= beta) {
						bestvalue = value;
						outcome = value;
						return coord;
					}
					bestvalue = value;
					bestmove = coord;
				}
				possibleMoves ^= coord;
			} while (possibleMoves != 0);
			outcome = bestvalue;
			return bestmove;
		}
		
		final static int min(long red, long green, int alpha, int beta, int remainingstones){
			nodecount++;
			if (remainingstones<=6) {
				return finalScorefewremainingMin(red, green, alpha, beta, false);
			}
			long possibleMoves = Bitboard.possibleMovesRed(green, red);
			if (possibleMoves == 0) {
				if (Bitboard.possibleMovesRed(red, green) == 0) {
					return Long.bitCount(red) - Long.bitCount(green);
				}
				return max(red, green,alpha, beta, remainingstones-1);
			}
			int bestvalue = beta;
			int value = beta;
			do {
				long coord = Long.highestOneBit(possibleMoves);
				long changedfields = Bitboard.getflippedDiskRed(green, red, coord);
				value = max(red^changedfields, green^changedfields^coord, alpha, value, remainingstones-1);
				if (value < beta) {
					if (value <= alpha) {
						return alpha;
					}
					bestvalue = value;
				}
				possibleMoves ^= coord;
			} while (possibleMoves != 0);
			return bestvalue;
		}
		
		final static int max(long red, long green, int alpha, int beta, int remainingstones){
			nodecount++;
			if (remainingstones<=6) {
				return finalScorefewremainingMax(red, green, alpha, beta, false);
			}
			long possibleMoves = Bitboard.possibleMovesRed(red, green);
			if (possibleMoves == 0) {
				if (Bitboard.possibleMovesRed(green, red) == 0) {
					return Long.bitCount(red) - Long.bitCount(green);
				}
				return min(red, green,alpha, beta, remainingstones-1);
			}
			int bestvalue = alpha;
			int value = alpha;
			do {
				long coord = Long.highestOneBit(possibleMoves);
				long changedfields = Bitboard.getflippedDiskRed(red, green, coord);
				value = min(red^changedfields^coord, green^changedfields, value, beta, remainingstones-1);
				if (value > alpha) {
					if (value >= beta) {
						return beta;
					}
					bestvalue = value;
				}
				possibleMoves ^= coord;
			} while (possibleMoves != 0);
			return bestvalue;
		}
		
		public static int finalScorefewremainingMin(long red, long green,int alpha, int beta, boolean passed){
			nodecount++;
			long emptyfields = ~(red|green);
			long changedfields = 0;
			long coord = 0;
			int bestvalue = beta;
			int value = beta;
			boolean nomoveavailable = true;
			while (emptyfields != 0) {
				coord = Long.highestOneBit(emptyfields);
				changedfields = Bitboard.getflippedDiskRed(green, red, coord);
				if (changedfields != 0) {
					value = finalScorefewremainingMax(red^changedfields, green^changedfields^coord,alpha, bestvalue, false);
					if (value < beta) {
						if (value <= alpha) {
							return alpha;
						}
						bestvalue = value;
					}
					nomoveavailable = false;
				}
				emptyfields ^= coord;
			}
			if (nomoveavailable) {
				if (passed) {
					return Long.bitCount(red) - Long.bitCount(green);
				}
				return finalScorefewremainingMax(red, green, alpha, beta, true);
			}
			return bestvalue;
		}
		
		public static int finalScorefewremainingMax(long red, long green,int alpha, int beta, boolean passed){
			nodecount++;
			long emptyfields = ~(red|green);
			long changedfields = 0;
			long coord = 0;
			int bestvalue = alpha;
			int value = alpha;
			boolean nomoveavailable = true;
			while (emptyfields != 0) {
				coord = Long.highestOneBit(emptyfields);
				changedfields = Bitboard.getflippedDiskRed(red, green, coord);
				if (changedfields != 0) {
					value = finalScorefewremainingMin(red^changedfields^coord, green^changedfields,bestvalue, beta, false);
					if (value > alpha) {
						if (value >= beta) {
							return beta;
						}
						bestvalue = value;
					}
					nomoveavailable = false;
				}
				emptyfields ^= coord;
			}
			if (nomoveavailable) {
				if (passed) {
					return Long.bitCount(red) - Long.bitCount(green);
				}
				return finalScorefewremainingMin(red, green, alpha, beta, true);
			}
			return bestvalue;
		}
		
		private static class MoveList{
			private static final long CORNERS = 0x8100000000000081L;
			public long[] possiblemoves;
			public long[] red;
			public long[] green;
			public int[] orderedScore;
			public int[] orderedIndices;
			public int size;
			public static MoveList[] generateListPool(int poolSize, int listSize) {
				MoveList[] pool = new MoveList[poolSize];
				for (int i = 0; i < pool.length; i++) {
					pool[i] = new MoveList(listSize);
				}
				return pool;
			}
			public MoveList(int size){
				possiblemoves = new long[size];
				red  = new long[size];
				green = new long[size];
				orderedScore = new int[size];
				orderedIndices = new int[size];
			}
			public final static void recycle(MoveList oldlist, long possiblemoves, long red, long green) {
				int size = Long.bitCount(possiblemoves);
				oldlist.size = size;
				for (int i = 0; i < size; i++){
					oldlist.orderedIndices[i] = i;
					long coord = Long.highestOneBit(possiblemoves);
					long flipeddisk = Bitboard.getflippedDiskRed(green, red, coord);
					long newred = red^flipeddisk;
					long newgreen = green^flipeddisk^coord;
					oldlist.red[i] = newred;
					oldlist.green[i] = newgreen;
					long newpossiblemoves = Bitboard.possibleMovesRed(newgreen, newred);
					oldlist.possiblemoves[i] = newpossiblemoves;
					long cornerstability = Bitboard.filladjacent(red&CORNERS);
					cornerstability &= red;
					int score = 3*Long.bitCount(cornerstability) - Long.bitCount(possiblemoves);
					oldlist.orderedScore[i] = score;
				}
				insertionSort(oldlist.orderedScore, oldlist.orderedIndices, size);
			}
			
			private final static void insertionSort(int[] orderedScore, int[] orderedIndices, int size){
				for (int i = 1; i < size; i++) {
					int j;
					int tempScore = orderedScore[i];
					int tempIndex = orderedIndices[i];
					for (j = i-1;  j >= 0 && tempScore < orderedScore[j] ; j--) {
						orderedScore[j+1] = orderedScore[j];
						orderedIndices[j+1] = orderedIndices[j];
					}
					orderedScore[j] = tempScore;
					orderedIndices[j] = tempIndex;
				}
			}
		}
	}
	
	
		
	
	
	public static class ExactSearch{
		
	}
}
