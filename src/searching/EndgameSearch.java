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
			remainingStones = gb.countStones(true) + gb.countStones(false);
			long possibleMoves = gb.possiblemoves(true);
			if (possibleMoves == 0) {
				long possibleMovesEnemy = gb.possiblemoves(false);
				if (possibleMovesEnemy != 0) {
					outcome = (int)min(gb, (byte)-1, (byte)1);
				}
				return 0;
			}
			long bestmove = Long.highestOneBit(possibleMoves);
			byte alpha = (byte)-1;
			while (possibleMoves != 0) {
				long coord = Long.highestOneBit(possibleMoves);
				long changedfields = gb.makeMove(true, coord);
				byte value = min(gb, alpha, (byte)1);
				gb.undomove(changedfields, coord, true);
				if (value == 1) {
					outcome = 1;
					return coord;
				}
				if (value == 0){
					outcome = 0;
					bestmove = coord;
					alpha = 0;
				}
				possibleMoves ^= coord;
			}
			outcome = -1;
			return bestmove;
		}
		
		final static byte min(Bitboard gb,byte alpha, byte beta){
			nodecount++;
			long possibleMoves = gb.possiblemoves(false);
			if (possibleMoves == 0) {
				if (gb.possiblemoves(true) == 0) {
					int myStones = gb.countStones(false);
					int opponentStones = gb.countStones(true);
					if (myStones>opponentStones) {
						return (byte)-1;
					}
					else if (opponentStones>myStones) {
						return (byte)1;
					}
					else {
						return 0;
					}
				}
				return max(gb,alpha, beta);
			}
			if (alpha ==-1 && beta == 1) {
				do {
					long coord = Long.highestOneBit(possibleMoves);
					long changedfields = gb.makeMove(false, coord);
					byte value = max(gb, alpha, beta);
					gb.undomove(changedfields, coord, false);
					if (value == -1) {
						return -1;
					} 
					possibleMoves ^= coord;
					if (value == 0) {
						beta = 0;
						break;
					}
				} while (possibleMoves != 0);
			}
			if (beta == 0) {
				while (possibleMoves != 0) {
					long coord = Long.highestOneBit(possibleMoves);
					long changedfields = gb.makeMove(false, coord);
					if (max(gb, (byte) -1, (byte) 0) == -1) {
						gb.undomove(changedfields, coord, false);
						return -1;
					}
					gb.undomove(changedfields, coord, false);
					possibleMoves ^= coord;
				}
				return 0;
			}
			if (alpha == 0) {
				do {
					long coord = Long.highestOneBit(possibleMoves);
					long changedfields = gb.makeMove(false, coord);
					if (max(gb, (byte) 0, (byte) 1) <= 0) {
						gb.undomove(changedfields, coord, false);
						return 0;
					}
					gb.undomove(changedfields, coord, false);
					possibleMoves ^= coord;
				}while (possibleMoves != 0);
				return 1;
			}
			return 1;
		}
		
		
		
		final static byte max(Bitboard gb,byte alpha, byte beta){
			nodecount++;
			long possibleMoves = gb.possiblemoves(true);
			if (possibleMoves == 0) {
				if (gb.possiblemoves(false) == 0) {
					int myStones = gb.countStones(true);
					int opponentStones = gb.countStones(false);
					if (myStones>opponentStones) {
						return (byte)1;
					}
					else if (opponentStones>myStones) {
						return (byte)-1;
					}
					else {
						return 0;
					}
				}
				return min(gb,alpha, beta);
			}
			if (alpha ==-1 && beta == 1) {
				do {
					long coord = Long.highestOneBit(possibleMoves);
					long changedfields = gb.makeMove(true, coord);
					byte value = min(gb, alpha, beta);
					gb.undomove(changedfields, coord, true);
					if (value == 1) {
						return 1;
					} 
					possibleMoves ^= coord;
					if (value == 0) {
						alpha = 0;
						break;
					}
				} while (possibleMoves != 0);
			}
			if (alpha == 0) {
				while (possibleMoves != 0) {
					long coord = Long.highestOneBit(possibleMoves);
					long changedfields = gb.makeMove(true, coord);
					if (min(gb, (byte) 0, (byte) 1) == 1) {
						gb.undomove(changedfields, coord, true);
						return 1;
					}
					gb.undomove(changedfields, coord, true);
					possibleMoves ^= coord;
				}
				return 0;
			}
			if (beta == 0) {
				do {
					long coord = Long.highestOneBit(possibleMoves);
					long changedfields = gb.makeMove(true, coord);
					if (min(gb, (byte) -1, (byte) 0) >= 0) {
						gb.undomove(changedfields, coord, true);
						return 0;
					}
					gb.undomove(changedfields, coord, true);
					possibleMoves ^= coord;
				}while (possibleMoves != 0);
				return -1;
			}
			return -1;
		}
	}
	public static class ExactSearch{
		
	}
}
