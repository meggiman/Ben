package searching;

import Gameboard.Bitboard;

public class EndgameSearch {
	long nextMove(Bitboard gb, boolean player){
		return 0;
	}
	public static class OutcomeSearch{
		public static int outcome;
		public static long nodecount;
		public static int remainingStones;
		public final static long nextMove(Bitboard gb){
			nodecount = 0;
			int alpha = -1;
			int beta = 1;
			remainingStones = 64 - gb.countStones(true) - gb.countStones(false);
			long possibleMoves = gb.possiblemoves(true);
			if (possibleMoves == 0) {
				long possibleMovesEnemy = gb.possiblemoves(false);
				if (possibleMovesEnemy != 0) {
					outcome = min(gb.red, gb.green, alpha, beta);
				}
				return 0;
			}
			long bestmove = Long.highestOneBit(possibleMoves);
			int bestvalue = alpha;
			do {
				long coord = Long.highestOneBit(possibleMoves);
				long changedfields = gb.makeMove(true, coord);
				int value = min(gb.red, gb.green, bestvalue, beta);
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
		
		final static int min(long red, long green, int alpha, int beta){
			nodecount++;
			remainingStones--;
			if (remainingStones<=6) {
				remainingStones++;
				return finalScorefewremainingMin(red, green, alpha, beta, false);
			}
			long possibleMoves = Bitboard.possibleMovesRed(green, red);
			if (possibleMoves == 0) {
				if (Bitboard.possibleMovesRed(red, green) == 0) {
					remainingStones++;
					return Long.bitCount(red) - Long.bitCount(green);
				}
				remainingStones++;
				return max(red, green,alpha, beta);
			}
			int bestvalue = beta;
			int value = beta;
			do {
				long coord = Long.highestOneBit(possibleMoves);
				long changedfields = Bitboard.getflippedDiskRed(green, red, coord);
				value = max(red^changedfields, green^changedfields^coord, alpha, value);
				if (value < beta) {
					if (value <= alpha) {
						remainingStones++;
						return alpha;
					}
					bestvalue = value;
				}
				possibleMoves ^= coord;
			} while (possibleMoves != 0);
			remainingStones++;
			return bestvalue;
		}
		
		final static int max(long red, long green, int alpha, int beta){
			nodecount++;
			remainingStones--;
			if (remainingStones<=6) {
				remainingStones++;
				return finalScorefewremainingMax(red, green, alpha, beta, false);
			}
			long possibleMoves = Bitboard.possibleMovesRed(red, green);
			if (possibleMoves == 0) {
				if (Bitboard.possibleMovesRed(green, red) == 0) {
					remainingStones++;
					return Long.bitCount(red) - Long.bitCount(green);
				}
				remainingStones++;
				return min(red, green,alpha, beta);
			}
			int bestvalue = alpha;
			int value = alpha;
			do {
				long coord = Long.highestOneBit(possibleMoves);
				long changedfields = Bitboard.getflippedDiskRed(red, green, coord);
				value = min(red^changedfields^coord, green^changedfields, value, beta);
				if (value > alpha) {
					if (value >= beta) {
						remainingStones++;
						return beta;
					}
					bestvalue = value;
				}
				possibleMoves ^= coord;
			} while (possibleMoves != 0);
			remainingStones++;
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
	}
	
	
		
	
	
	public static class ExactSearch{
		
	}
}
