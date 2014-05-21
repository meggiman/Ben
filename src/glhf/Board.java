package glhf;

import java.util.Arrays;
import java.util.Comparator;

import reversi.Coordinates;
import reversi.GameBoard;
import reversi.OutOfBoundsException;
import glhf.C;

public class Board {
	
	
	
	private long[] board;		//board[0] := RED-board, board[1] := GREEN-board
	private int currentColor;	//1 RED
	private long hash;
	private int remainingStones;
	
	private final long RIGHT_MASK = 0xfefefefefefefefeL;
	private final long LEFT_MASK 	= 0x7f7f7f7f7f7f7f7fL;
	

//	//Xiaolong
//	private final static int[] moveOrder = { 0, 7, 56, 63, 18, 21, 42, 45, 2, 5, 16, 23, 40, 47, 58, 61, 3, 4, 24, 31, 32, 39, 59, 60, 19, 20, 26, 29, 34, 37, 43, 44, 11, 12, 25, 30, 33, 38, 51, 52, 10, 13, 17, 22, 41, 46, 50, 53, 1, 6, 8, 15, 48, 55, 57, 62, 9, 14, 49, 54, 27, 28, 35, 36 };
//	
//	
//	private static long[] moveOrderFields;
//	private static long[] fields;
//	private static long[] possibleFields;
	
//	public static void initialize()
//	{
//		moveOrderFields = new long[64];
//		fields = new long[64];
//		possibleFields = new long[60];
//		int index = 0;
//		for(int i = 0; i < 64; i++)
//		{
//			fields[i] = 1L << i;
//			moveOrderFields[i] = 1L << moveOrder[i];
//			if(i != 27 & i != 28 & i != 35 & i != 36)
//			{
//				possibleFields[index] = 1L << i;
//			}
//			else
//			{
//				index--;
//			}
//			index++;
//			
//		}
//	}
	public Board(GameBoard gb, int color) 
	{
		long[] result = new long[2];
		result[0] = 0L;
		result[1] = 0L;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				try {
					if (gb.getOccupation(new Coordinates(i + 1, j + 1)) == 1)
						result[0] |= 1L << i * 8 + j;
					else if (gb.getOccupation(new Coordinates(i + 1, j + 1)) == 2)
						result[1] |= 1L << i * 8 + j;
				} catch (OutOfBoundsException e) {
				}
			}
		}
		board = result;
		
		remainingStones = Long.bitCount(~(board[0] | board[1]));
		
		currentColor = color;
		hash = C.gen.BoardToHash(result);
//		initialize();
	}
	public long availableMoves()
	{
		long legal = 0L;
	    long potentialMoves;
	    long currentBoard = board[currentColor];
	    long opponentBoard = board[~(currentColor | 0) + 2];
	    long emptyBoard = ~(board[0] | board[1]);
	    
	    // UP
	    potentialMoves = (currentBoard >>> 8) & opponentBoard;
	    while (potentialMoves != 0L) {
	        long tmp = (potentialMoves >>> 8);
	        legal |= tmp & emptyBoard;
	        potentialMoves = tmp & opponentBoard;
	    }
	    // DOWN
	    potentialMoves = (currentBoard << 8) & opponentBoard;
	    while (potentialMoves != 0L) {
	        long tmp = (potentialMoves << 8);
	        legal |= tmp & emptyBoard;
	        potentialMoves = tmp & opponentBoard;
	    }
	    // LEFT
	    potentialMoves = (currentBoard >>> 1L) & LEFT_MASK & opponentBoard;
	    while (potentialMoves != 0L) {
	        long tmp = (potentialMoves >>> 1L) & LEFT_MASK;
	        legal |= tmp & emptyBoard;
	        potentialMoves = tmp & opponentBoard;
	    }
	    // RIGHT
	    potentialMoves = (currentBoard << 1L) & RIGHT_MASK & opponentBoard;
	    while (potentialMoves != 0L) {
	        long tmp = (potentialMoves << 1L) & RIGHT_MASK;
	        legal |= tmp & emptyBoard;
	        potentialMoves = tmp & opponentBoard;
	    }
	    // UP LEFT
	    potentialMoves = (currentBoard >>> (9L)) & LEFT_MASK & opponentBoard;
	    while (potentialMoves != 0L) {
	        long tmp = (potentialMoves >>> (9L)) & LEFT_MASK;
	        legal |= tmp & emptyBoard;
	        potentialMoves = tmp & opponentBoard;
	    }
	    // UP RIGHT
	    potentialMoves = (currentBoard >>> (7L)) & RIGHT_MASK & opponentBoard;
	    while (potentialMoves != 0L) {
	        long tmp = (potentialMoves >>> (7L)) & RIGHT_MASK;
	        legal |= tmp & emptyBoard;
	        potentialMoves = tmp & opponentBoard;
	    }
	    // DOWN LEFT
	    potentialMoves = (currentBoard << (7L)) & C.LEFT_MASK & opponentBoard;
	    while (potentialMoves != 0L) {
	        long tmp = (potentialMoves << (7L)) & C.LEFT_MASK;
	        legal |= tmp & emptyBoard;
	        potentialMoves = tmp & opponentBoard;
	    }
	    // DOWN RIGHT
	    potentialMoves = (currentBoard << (9L)) & RIGHT_MASK & opponentBoard;
	    while (potentialMoves != 0L) {
	        long tmp = (potentialMoves << (9L)) & RIGHT_MASK;
	        legal |= tmp & emptyBoard;
	        potentialMoves = tmp & opponentBoard;
	    }
	    return legal;
	}

	public long availableMovesNew()
	{
		long legal = 0L;
	    long potentialMoves;
	    long currentBoard = board[currentColor];
	    long opponentBoard = board[~(currentColor | 0) + 2];
	    long emptyBoard = ~(board[0] | board[1]);
	    
	    // UP
	    potentialMoves = (currentBoard >>> 8);
	    while (potentialMoves != 0L) {
	        potentialMoves = (potentialMoves & opponentBoard) >>> 8;
	        legal |= potentialMoves & emptyBoard;
	    }
	    // DOWN
	    potentialMoves = (currentBoard << 8);
	    while (potentialMoves != 0L) {
	        potentialMoves = (potentialMoves & opponentBoard) << 8;
	        legal |= potentialMoves & emptyBoard;
	    }
	    // LEFT
	    potentialMoves = (currentBoard >>> 1) & LEFT_MASK;
	    while (potentialMoves != 0L) {
	        potentialMoves = (potentialMoves & opponentBoard) >>> 1 & LEFT_MASK;
	        legal |= potentialMoves & emptyBoard;
	    }
	    // RIGHT
	    potentialMoves = (currentBoard << 1) & RIGHT_MASK;
	    while (potentialMoves != 0L) {
	        potentialMoves = (potentialMoves & opponentBoard) << 1 & RIGHT_MASK;
	        legal |= potentialMoves & emptyBoard;
	    }
	    // UP LEFT
	    potentialMoves = (currentBoard >>> 9) & LEFT_MASK;
	    while (potentialMoves != 0L) {
	        potentialMoves = (potentialMoves & opponentBoard) >>> 9 & LEFT_MASK;
	        legal |= potentialMoves & emptyBoard;
	    }
	    // UP RIGHT
	    potentialMoves = (currentBoard >>> 7) & RIGHT_MASK;
	    while (potentialMoves != 0L) {
	        potentialMoves = (potentialMoves & opponentBoard) >>> 7 & RIGHT_MASK;
	        legal |= potentialMoves & emptyBoard;
	    }
	    // DOWN LEFT
	    potentialMoves = (currentBoard << 7) & LEFT_MASK;
	    while (potentialMoves != 0L) {
	        potentialMoves = (potentialMoves & opponentBoard) << 7 & LEFT_MASK;
	        legal |= potentialMoves & emptyBoard;
	    }
	    // DOWN RIGHT
	    potentialMoves = (currentBoard << 9) & RIGHT_MASK;
	    while (potentialMoves != 0L) {
	        potentialMoves = (potentialMoves & opponentBoard) << 9 & RIGHT_MASK;
	        legal |= potentialMoves & emptyBoard;
	    }
	    return legal;
	}
	
	public long[] sortMidGame(long data)
	{
		long[] result = new long[Long.bitCount(data)];
		int index = 0;
		for(int i = 0; i < 60; i++)
		{
			if((data & C.moveOrderFields[i]) != 0L)
			{
				result[index] = C.moveOrderFields[i];
				index++;
			}
		}
		return result;
	}
	
	public long[] sortEndGame(long moves)
	{
		int bitCount = Long.bitCount(moves);
		int index = 0;
		long[][] movesArray = new long[bitCount][2];
		for(int i = 0; i < 60; i++)
		{
			if((C.possibleFields[i] & moves) != 0)
			{
				movesArray[index][0] = C.possibleFields[i];
				movesArray[index][1] = (long) Long.bitCount(makeMoveWithoutHash(C.possibleFields[i]).availableMoves());
				index++;
			}
		}
//		int[] copy = Arrays.copyOf(movesAfter, bitCount);
//		long[] result = new long[bitCount];
//		System.arraycopy(movesAfter, 0, copy, 0, bitCount);
//		
//		Arrays.sort(movesAfter);
//		
//		for(int i = 0; i < bitCount; i++)
//		{
//			for(int j = 0; j < bitCount; j++)
//			{
//				if(movesAfter[i] == copy[j])
//				{
//					result[j] = movesArray[i];
//					break;
//				}
//			}
//		}
		Arrays.sort(movesArray, new Comparator<long[]>() {
		    public int compare(long[] int1, long[] int2) {
		        long val1 = int1[1];
		        long val2 = int2[1];
		        return (val1 == val2 ? 0: (val1 < val2 ? -1: 1));
		    }
		});
		long[] result = new long[bitCount];
		for(int i = 0; i < bitCount; i++)
			result[i] = movesArray[i][0];
		return result;
	}

	public long[][] sortEndGameNew(long moves)
	{
		int bitCount = Long.bitCount(moves);
		int index = 0;
		long[][] movesArray = new long[bitCount][2];
		for(int i = 0; i < 60; i++)
		{
			if((C.possibleFields[i] & moves) != 0)
			{
				movesArray[index][0] = C.possibleFields[i];
				Board t = makeMoveWithoutHash(C.possibleFields[i]);
				movesArray[index][1] = t.availableMoves();
//				movesArray[index][1] = (long) Long.bitCount(movesArray[index][2]);
				
				index++;
			}
		}
//		int[] copy = Arrays.copyOf(movesAfter, bitCount);
//		long[] result = new long[bitCount];
//		System.arraycopy(movesAfter, 0, copy, 0, bitCount);
//		
//		Arrays.sort(movesAfter);
//		
//		for(int i = 0; i < bitCount; i++)
//		{
//			for(int j = 0; j < bitCount; j++)
//			{
//				if(movesAfter[i] == copy[j])
//				{
//					result[j] = movesArray[i];
//					break;
//				}
//			}
//		}
		Arrays.sort(movesArray, new Comparator<long[]>() {
		    public int compare(long[] long1, long[] long2) {
		        int val1 = Long.bitCount(long1[1]);
		        int val2 = Long.bitCount(long2[1]);
		        return (val1 == val2 ? 0: (val1 < val2 ? -1: 1));
		    }
		});
		
		return movesArray;
	}
	
	
	public long[] sortScore(long moves)
	{
		int bitCount = Long.bitCount(moves);
		int index = 0;
		long[][] movesArray = new long[bitCount][2];
		for(int i = 0; i < 60; i++)
		{
			if((C.possibleFields[i] & moves) != 0)
			{
				movesArray[index][0] = C.possibleFields[i];
				movesArray[index][1] = (long) makeMoveWithoutHash(C.possibleFields[i]).getScore();
				index++;
			}
		}
//		int[] copy = Arrays.copyOf(movesAfter, bitCount);
//		long[] result = new long[bitCount];
//		System.arraycopy(movesAfter, 0, copy, 0, bitCount);
//		
//		Arrays.sort(movesAfter);
//		
//		for(int i = 0; i < bitCount; i++)
//		{
//			for(int j = 0; j < bitCount; j++)
//			{
//				if(movesAfter[i] == copy[j])
//				{
//					result[j] = movesArray[i];
//					break;
//				}
//			}
//		}
		Arrays.sort(movesArray, new Comparator<long[]>() {
		    public int compare(long[] int1, long[] int2) {
		        long val1 = int1[1];
		        long val2 = int2[1];
		        return (val1 == val2 ? 0: (val1 < val2 ? -1: 1));
		    }
		});
		long[] result = new long[bitCount];
		for(int i = 0; i < bitCount; i++)
			result[i] = movesArray[i][0];
		return result;
	}
	
	
	public long[] split(long data)
	{
		long[] result = new long[Long.bitCount(data)];
		int index = 0;
		for(int i = 0; i < 60; i++)
		{
			if((data & C.possibleFields[i]) != 0L)
			{
				result[index] = C.possibleFields[i];
				index++;
			}
		}
		
//		for(int i = 0; i < result.length; i++)
//		{
//			result[i] = Long.lowestOneBit(data);
//			data ^= result[i];
//		}
		return result;
	}
	
	long[] availableMovesArraySort()
	{
		long moves = this.availableMoves();
		return sortMidGame(moves);
	}
	
	long[] availableMovesArray()
	{
		long moves = this.availableMoves();
		return split(moves);
	}
	
	long[] availableMovesArrayNew()
	{
		long moves = this.availableMovesNew();
		return sortMidGame(moves);
	}
	
	public Board(long[] board, long hash, int color)
	{
		this.board = board;
		this.hash = hash;
		this.remainingStones = Long.bitCount(~(board[0] | board[1]));
		this.currentColor = color;
	}
	
	public Board(long[] board, long hash, int color, int remainingStones)
	{
		this.board = board;
		this.hash = hash;
		this.remainingStones = remainingStones;
		this.currentColor = color;
	}
	
	public Board makeMoveOld(long move)
	{	
		Board t = this.clone();
		long[] board = {t.board[currentColor], t.board[getOtherColor()]};
		long hash = t.hash;
		
		
		//UP
		long temp = move >>> 8;
		while((temp & board[1]) != 0L)
		{
			temp >>>= 8;
		}
		if((temp & board[0]) != 0L)
		{
			temp <<= 8;
			while(temp != move)
			{
				board[1] ^= temp;
				board[0] ^= temp;
				hash = C.gen.updateHashOtherColor(hash, Long.numberOfTrailingZeros(temp));
				temp <<= 8;
			}
		}
		
		//DOWN
		temp = move << 8;
		while((temp & board[1]) != 0L)
		{
			temp <<= 8;
		}
		if((temp & board[0]) != 0L)
		{
			temp >>>= 8;
			while(temp != move)
			{
				board[1] ^= temp;
				board[0] ^= temp;
				hash = C.gen.updateHashOtherColor(hash, Long.numberOfTrailingZeros(temp));
				temp >>>= 8;
			}
		}		

		long tmpBoard = board[1] & 0x8181818181818181L; //nur Rand
		board[1] &= 0x7e7e7e7e7e7e7e7eL; //ohne Rand
		
		//LEFT
		temp = move >>> 1;
		while((temp & board[1]) != 0L)
		{
			temp >>>= 1;
		}
		if((temp & board[0]) != 0L)
		{
			temp <<= 1;
			while(temp != move)
			{
				board[1] ^= temp;
				board[0] ^= temp;
				hash = C.gen.updateHashOtherColor(hash, Long.numberOfTrailingZeros(temp));
				temp <<= 1;
			}
		}

		//RIGHT
		temp = move << 1;
		while((temp & board[1]) != 0L)
		{
			temp <<= 1;
		}
		if((temp & board[0]) != 0L)
		{
			temp >>>= 1;
			while(temp != move)
			{
				board[1] ^= temp;
				board[0] ^= temp;
				hash = C.gen.updateHashOtherColor(hash, Long.numberOfTrailingZeros(temp));
				temp >>>= 1;
			}
		}

		//UP LEFT
		temp = move >>> 9;
		while((temp & board[1]) != 0L)
		{
			temp >>>= 9;
		}
		if((temp & board[0]) != 0L)
		{
			temp <<= 9;
			while(temp != move)
			{
				board[1] ^= temp;
				board[0] ^= temp;
				hash = C.gen.updateHashOtherColor(hash, Long.numberOfTrailingZeros(temp));
				temp <<= 9;
			}
		}

		//UP RIGHT
		temp = move >>> 7;
		while((temp & board[1]) != 0L)
		{
			temp >>>= 7;
		}
		if((temp & board[0]) != 0L)
		{
			temp <<= 7;
			while(temp != move)
			{
				board[1] ^= temp;
				board[0] ^= temp;
				hash = C.gen.updateHashOtherColor(hash, Long.numberOfTrailingZeros(temp));
				temp <<= 7;
			}
		}

		//DOWN LEFT
		temp = move << 7;
		while((temp & board[1]) != 0L)
		{
			temp <<= 7;
		}
		if((temp & board[0]) != 0L)
		{
			temp >>>= 7;
			while(temp != move)
			{
				board[1] ^= temp;
				board[0] ^= temp;
				hash = C.gen.updateHashOtherColor(hash, Long.numberOfTrailingZeros(temp));
				temp >>>= 7;
			}
		}

		//DOWN RIGHT
		temp = move << 9;
		while((temp & board[1]) != 0L)
		{
			temp <<= 9;
		}
		if((temp & board[0]) != 0L)
		{
			temp >>>= 9;
			while(temp != move)
			{
				board[1] ^= temp;
				board[0] ^= temp;
				hash = C.gen.updateHashOtherColor(hash, Long.numberOfTrailingZeros(temp));
				temp >>>= 9;
			}
		}

		board[0] |= move;
		board[1] &= ~move; 
		board[1] |= tmpBoard;
		
		
		t.hash = C.gen.updateHashEmpty(hash, Long.numberOfTrailingZeros(move), currentColor);
		
		t.swap();
		t.board[t.getColor()] = board[1];
		t.board[t.getOtherColor()] = board[0];
		
		t.remainingStones--;
		
		return t;
		
		
	}

	public Board makeMove(long move)
	{	
		Board t = this.clone();
		//long[] board = {t.board[currentColor], t.board[getOtherColor()]};
		long currentBoard = t.getCurrentPlayerBoard();
		long opponentBoard = t.getOtherPlayerBoard();
		long hash = t.hash;
		long disksToFlip = 0;
		
		//UP
		long temp = move >>> 8;
		if ((temp & opponentBoard) != 0L) 
		{
			temp >>>= 8;
			while ((temp & opponentBoard) != 0L) 
			{
				temp >>>= 8;
			}
			if ((temp & currentBoard) != 0L) 
			{
				temp <<= 8;
				while (temp != move) 
				{
					disksToFlip |= temp;
					hash = C.gen.updateHashOtherColor(hash, temp);
					temp <<= 8;
				}
			}
		}
				
		//DOWN
		temp = move << 8;
		if ((temp & opponentBoard) != 0L) 
		{
			temp <<= 8;
			while ((temp & opponentBoard) != 0L) 
			{
				temp <<= 8;
			}
			if ((temp & currentBoard) != 0L) 
			{
				temp >>>= 8;
				while (temp != move) 
				{
					disksToFlip |= temp;
					hash = C.gen.updateHashOtherColor(hash, temp);
					temp >>>= 8;
				}
			}
		}	

//		long tmpBoard = opponentBoard & 0x8181818181818181L; //nur Rand
		opponentBoard &= 0x7e7e7e7e7e7e7e7eL; //ohne Rand
				
		//LEFT
		temp = move >>> 1;
		if ((temp & opponentBoard) != 0L) 
		{
			temp >>>= 1;
			while ((temp & opponentBoard) != 0L) 
			{
				temp >>>= 1;
			}
			if ((temp & currentBoard) != 0L) 
			{
				temp <<= 1;
				while (temp != move) 
				{
					disksToFlip |= temp;
					hash = C.gen.updateHashOtherColor(hash, temp);
					temp <<= 1;
				}
			}
		}

		//RIGHT
		temp = move << 1;
		if ((temp & opponentBoard) != 0L) 
		{
			temp <<= 1;
			while ((temp & opponentBoard) != 0L) 
			{
				temp <<= 1;
			}
			if ((temp & currentBoard) != 0L) 
			{
				temp >>>= 1;
				while (temp != move) 
				{
					disksToFlip |= temp;
					hash = C.gen.updateHashOtherColor(hash, temp);
					temp >>>= 1;
				}
			}
		}

		//UP LEFT
		temp = move >>> 9;
		if ((temp & opponentBoard) != 0L) 
		{
			temp >>>= 9;
			while ((temp & opponentBoard) != 0L) 
			{
				temp >>>= 9;
			}
			if ((temp & currentBoard) != 0L) 
			{
				temp <<= 9;
				while (temp != move) 
				{
					disksToFlip |= temp;
					hash = C.gen.updateHashOtherColor(hash, temp);
					temp <<= 9;
				}
			}
		}

		//UP RIGHT
		temp = move >>> 7;
		if ((temp & opponentBoard) != 0L) 
		{
			temp >>>= 7;
			while ((temp & opponentBoard) != 0L) 
			{
				temp >>>= 7;
			}
			if ((temp & currentBoard) != 0L) 
			{
				temp <<= 7;
				while (temp != move) 
				{
					disksToFlip |= temp;
					hash = C.gen.updateHashOtherColor(hash, temp);
					temp <<= 7;
				}
			}
		}

		//DOWN LEFT
		temp = move << 7;
		if ((temp & opponentBoard) != 0L) 
		{
			temp <<= 7;
			while ((temp & opponentBoard) != 0L) 
			{
				temp <<= 7;
			}
			if ((temp & currentBoard) != 0L) 
			{
				temp >>>= 7;
				while (temp != move) 
				{
					disksToFlip |= temp;
					hash = C.gen.updateHashOtherColor(hash, temp);
					temp >>>= 7;
				}
			}
		}

		//DOWN RIGHT
		temp = move << 9;
		if ((temp & opponentBoard) != 0L) 
		{
			temp <<= 9;
			while ((temp & opponentBoard) != 0L) 
			{
				temp <<= 9;
			}
			if ((temp & currentBoard) != 0L) 
			{
				temp >>>= 9;
				while (temp != move) 
				{
					disksToFlip |= temp;
					hash = C.gen.updateHashOtherColor(hash, temp);
					temp >>>= 9;
				}
			}
		}

		//board[1] |= tmpBoard;
		//board[0] ^= disksToFlip;
		//board[1] ^= disksToFlip;
		
		//board[0] |= move;
		//board[1] &= ~move; 
		
		t.board[t.getColor()] = (currentBoard ^ disksToFlip) | move;
		t.board[t.getOtherColor()] = t.getOtherPlayerBoard() ^ disksToFlip;
		
		
		t.hash = C.gen.updateHashEmpty(hash, move, currentColor);
		
		t.swap();
		
		t.remainingStones--;
		
		return t;
		
		
	}
	//inspired by Xiaolong! But was worse than my method so it doesnt get used
	public long disksToFlip(long move)
	{	
		//Board t = this.clone();
		//long[] board = {t.board[currentColor], t.board[getOtherColor()]};
		long currentBoard = this.getCurrentPlayerBoard();
		long opponentBoard = this.getOtherPlayerBoard();
		//long hash = this.hash;
		long disksToFlip = 0;
		
		//UP
		long temp = move >>> 8;
		if ((temp & opponentBoard) != 0L) 
		{
			temp >>>= 8;
			while ((temp & opponentBoard) != 0L) 
			{
				temp >>>= 8;
			}
			if ((temp & currentBoard) != 0L) 
			{
				temp <<= 8;
				while (temp != move) 
				{
					disksToFlip |= temp;
					hash = C.gen.updateHashOtherColor(hash, Long.numberOfTrailingZeros(temp));
					temp <<= 8;
				}
			}
		}
				
		//DOWN
		temp = move << 8;
		if ((temp & opponentBoard) != 0L) 
		{
			temp <<= 8;
			while ((temp & opponentBoard) != 0L) 
			{
				temp <<= 8;
			}
			if ((temp & currentBoard) != 0L) 
			{
				temp >>>= 8;
				while (temp != move) 
				{
					disksToFlip |= temp;
					hash = C.gen.updateHashOtherColor(hash, Long.numberOfTrailingZeros(temp));
					temp >>>= 8;
				}
			}
		}	

		//long tmpBoard = opponentBoard & 0x8181818181818181L; //nur Rand
		opponentBoard &= 0x7e7e7e7e7e7e7e7eL; //ohne Rand
				
		//LEFT
		temp = move >>> 1;
		if ((temp & opponentBoard) != 0L) 
		{
			temp >>>= 1;
			while ((temp & opponentBoard) != 0L) 
			{
				temp >>>= 1;
			}
			if ((temp & currentBoard) != 0L) 
			{
				temp <<= 1;
				while (temp != move) 
				{
					disksToFlip |= temp;
					hash = C.gen.updateHashOtherColor(hash, Long.numberOfTrailingZeros(temp));
					temp <<= 1;
				}
			}
		}

		//RIGHT
		temp = move << 1;
		if ((temp & opponentBoard) != 0L) 
		{
			temp <<= 1;
			while ((temp & opponentBoard) != 0L) 
			{
				temp <<= 1;
			}
			if ((temp & currentBoard) != 0L) 
			{
				temp >>>= 1;
				while (temp != move) 
				{
					disksToFlip |= temp;
					hash = C.gen.updateHashOtherColor(hash, Long.numberOfTrailingZeros(temp));
					temp >>>= 1;
				}
			}
		}

		//UP LEFT
		temp = move >>> 9;
		if ((temp & opponentBoard) != 0L) 
		{
			temp >>>= 9;
			while ((temp & opponentBoard) != 0L) 
			{
				temp >>>= 9;
			}
			if ((temp & currentBoard) != 0L) 
			{
				temp <<= 9;
				while (temp != move) 
				{
					disksToFlip |= temp;
					hash = C.gen.updateHashOtherColor(hash, Long.numberOfTrailingZeros(temp));
					temp <<= 9;
				}
			}
		}

		//UP RIGHT
		temp = move >>> 7;
		if ((temp & opponentBoard) != 0L) 
		{
			temp >>>= 7;
			while ((temp & opponentBoard) != 0L) 
			{
				temp >>>= 7;
			}
			if ((temp & currentBoard) != 0L) 
			{
				temp <<= 7;
				while (temp != move) 
				{
					disksToFlip |= temp;
					hash = C.gen.updateHashOtherColor(hash, Long.numberOfTrailingZeros(temp));
					temp <<= 7;
				}
			}
		}

		//DOWN LEFT
		temp = move << 7;
		if ((temp & opponentBoard) != 0L) 
		{
			temp <<= 7;
			while ((temp & opponentBoard) != 0L) 
			{
				temp <<= 7;
			}
			if ((temp & currentBoard) != 0L) 
			{
				temp >>>= 7;
				while (temp != move) 
				{
					disksToFlip |= temp;
					hash = C.gen.updateHashOtherColor(hash, Long.numberOfTrailingZeros(temp));
					temp >>>= 7;
				}
			}
		}

		//DOWN RIGHT
		temp = move << 9;
		if ((temp & opponentBoard) != 0L) 
		{
			temp <<= 9;
			while ((temp & opponentBoard) != 0L) 
			{
				temp <<= 9;
			}
			if ((temp & currentBoard) != 0L) 
			{
				temp >>>= 9;
				while (temp != move) 
				{
					disksToFlip |= temp;
					hash = C.gen.updateHashOtherColor(hash, Long.numberOfTrailingZeros(temp));
					temp >>>= 9;
				}
			}
		}

		//board[1] |= tmpBoard;
		//board[0] ^= disksToFlip;
		//board[1] ^= disksToFlip;
		
		//board[0] |= move;
		//board[1] &= ~move; 
		
		//t.board[t.getColor()] = (currentBoard ^ disksToFlip) | move;
		//t.board[t.getOtherColor()] = (opponentBoard | tmpBoard) ^ disksToFlip & ~move;
		
		
		hash = C.gen.updateHashEmpty(hash, Long.numberOfTrailingZeros(move), currentColor);
		
		//t.swap();
		
		
		
		return disksToFlip;
		
		
	}
	
	public void flipDisks(long disksToFlip, long move)
	{
		board[currentColor] ^= (disksToFlip | move);
		board[currentColor ^ 0x1] ^= disksToFlip;
		swap();
		remainingStones--;
	}
	
	public void undoMove(long disksToFlip, long move)
	{
		board[currentColor ^ 0x1] ^= (disksToFlip | move);
		board[currentColor] ^= disksToFlip;
		swap();
		remainingStones++;
	}
	
	public Board makeMoveWithoutHashOld(long move)
	{
		//Board t = this.clone();
		//long[] board = {t.board[currentColor], t.board[getOtherColor()]};
		long currentBoard = this.getCurrentPlayerBoard();
		long opponentBoard = this.getOtherPlayerBoard();
		//long hash = t.hash;
		long disksToFlip = 0L;
		
		//UP
		long temp = move >>> 8;
		if ((temp & opponentBoard) != 0L) 
		{
			temp >>>= 8;
			while ((temp & opponentBoard) != 0L) 
			{
				temp >>>= 8;
			}
			if ((temp & currentBoard) != 0L) 
			{
				temp <<= 8;
				while (temp != move) 
				{
					disksToFlip |= temp;
					// hash = C.gen.updateHashOtherColor(hash,
					// Long.numberOfTrailingZeros(temp), currentColor);
					temp <<= 8;
				}
			}
		}
				
		//DOWN
		temp = move << 8;
		if ((temp & opponentBoard) != 0L) 
		{
			temp <<= 8;
			while ((temp & opponentBoard) != 0L) 
			{
				temp <<= 8;
			}
			if ((temp & currentBoard) != 0L) 
			{
				temp >>>= 8;
				while (temp != move) 
				{
					disksToFlip |= temp;
					// hash = C.gen.updateHashOtherColor(hash,
					// Long.numberOfTrailingZeros(temp), currentColor);
					temp >>>= 8;
				}
			}
		}	

		long tmpBoard = opponentBoard & 0x8181818181818181L; //nur Rand
		opponentBoard &= 0x7e7e7e7e7e7e7e7eL; //ohne Rand
				
		//LEFT
		temp = move >>> 1;
		if ((temp & opponentBoard) != 0L) 
		{
			temp >>>= 1;
			while ((temp & opponentBoard) != 0L) 
			{
				temp >>>= 1;
			}
			if ((temp & currentBoard) != 0L) 
			{
				temp <<= 1;
				while (temp != move) 
				{
					disksToFlip |= temp;
					// hash = C.gen.updateHashOtherColor(hash,
					// Long.numberOfTrailingZeros(temp), currentColor);
					temp <<= 1;
				}
			}
		}

		//RIGHT
		temp = move << 1;
		if ((temp & opponentBoard) != 0L) 
		{
			temp <<= 1;
			while ((temp & opponentBoard) != 0L) 
			{
				temp <<= 1;
			}
			if ((temp & currentBoard) != 0L) 
			{
				temp >>>= 1;
				while (temp != move) 
				{
					disksToFlip |= temp;
					// hash = C.gen.updateHashOtherColor(hash,
					// Long.numberOfTrailingZeros(temp), currentColor);
					temp >>>= 1;
				}
			}
		}

		//UP LEFT
		temp = move >>> 9;
		if ((temp & opponentBoard) != 0L) 
		{
			temp >>>= 9;
			while ((temp & opponentBoard) != 0L) 
			{
				temp >>>= 9;
			}
			if ((temp & currentBoard) != 0L) 
			{
				temp <<= 9;
				while (temp != move) 
				{
					disksToFlip |= temp;
					// hash = C.gen.updateHashOtherColor(hash,
					// Long.numberOfTrailingZeros(temp), currentColor);
					temp <<= 9;
				}
			}
		}

		//UP RIGHT
		temp = move >>> 7;
		if ((temp & opponentBoard) != 0L) 
		{
			temp >>>= 7;
			while ((temp & opponentBoard) != 0L) 
			{
				temp >>>= 7;
			}
			if ((temp & currentBoard) != 0L) 
			{
				temp <<= 7;
				while (temp != move) 
				{
					disksToFlip |= temp;
					// hash = C.gen.updateHashOtherColor(hash,
					// Long.numberOfTrailingZeros(temp), currentColor);
					temp <<= 7;
				}
			}
		}

		//DOWN LEFT
		temp = move << 7;
		if ((temp & opponentBoard) != 0L) 
		{
			temp <<= 7;
			while ((temp & opponentBoard) != 0L) 
			{
				temp <<= 7;
			}
			if ((temp & currentBoard) != 0L) 
			{
				temp >>>= 7;
				while (temp != move) 
				{
					disksToFlip |= temp;
					// hash = C.gen.updateHashOtherColor(hash,
					// Long.numberOfTrailingZeros(temp), currentColor);
					temp >>>= 7;
				}
			}
		}

		//DOWN RIGHT
		temp = move << 9;
		if ((temp & opponentBoard) != 0L) 
		{
			temp <<= 9;
			while ((temp & opponentBoard) != 0L) 
			{
				temp <<= 9;
			}
			if ((temp & currentBoard) != 0L) 
			{
				temp >>>= 9;
				while (temp != move) 
				{
					disksToFlip |= temp;
					// hash = C.gen.updateHashOtherColor(hash,
					// Long.numberOfTrailingZeros(temp), currentColor);
					temp >>>= 9;
				}
			}
		}

		long[] b = {(currentBoard ^ disksToFlip) | move, (opponentBoard | tmpBoard) ^ disksToFlip & ~move};
		
		//t.hash = C.gen.updateHashEmpty(hash, Long.numberOfTrailingZeros(move), currentColor);
		
		
		
		
		
		return new Board(b, 0, getOtherColor(), remainingStones -1);
		
	}
	
	public Board makeMoveWithoutHash(long move)
	{
		Board t = this.clone();
		//long[] board = {t.board[currentColor], t.board[getOtherColor()]};
		long currentBoard = t.getCurrentPlayerBoard();
		long opponentBoard = t.getOtherPlayerBoard();
//		long hash = t.hash;
		long disksToFlip = 0;
		
		//UP
		long temp = move >>> 8;
		if ((temp & opponentBoard) != 0L) 
		{
			temp >>>= 8;
			while ((temp & opponentBoard) != 0L) 
			{
				temp >>>= 8;
			}
			if ((temp & currentBoard) != 0L) 
			{
				temp <<= 8;
				while (temp != move) 
				{
					disksToFlip |= temp;
//					hash = C.gen.updateHashOtherColor(hash, temp);
					temp <<= 8;
				}
			}
		}
				
		//DOWN
		temp = move << 8;
		if ((temp & opponentBoard) != 0L) 
		{
			temp <<= 8;
			while ((temp & opponentBoard) != 0L) 
			{
				temp <<= 8;
			}
			if ((temp & currentBoard) != 0L) 
			{
				temp >>>= 8;
				while (temp != move) 
				{
					disksToFlip |= temp;
//					hash = C.gen.updateHashOtherColor(hash, temp);
					temp >>>= 8;
				}
			}
		}	

//		long tmpBoard = opponentBoard & 0x8181818181818181L; //nur Rand
		opponentBoard &= 0x7e7e7e7e7e7e7e7eL; //ohne Rand
				
		//LEFT
		temp = move >>> 1;
		if ((temp & opponentBoard) != 0L) 
		{
			temp >>>= 1;
			while ((temp & opponentBoard) != 0L) 
			{
				temp >>>= 1;
			}
			if ((temp & currentBoard) != 0L) 
			{
				temp <<= 1;
				while (temp != move) 
				{
					disksToFlip |= temp;
//					hash = C.gen.updateHashOtherColor(hash, temp);
					temp <<= 1;
				}
			}
		}

		//RIGHT
		temp = move << 1;
		if ((temp & opponentBoard) != 0L) 
		{
			temp <<= 1;
			while ((temp & opponentBoard) != 0L) 
			{
				temp <<= 1;
			}
			if ((temp & currentBoard) != 0L) 
			{
				temp >>>= 1;
				while (temp != move) 
				{
					disksToFlip |= temp;
//					hash = C.gen.updateHashOtherColor(hash, temp);
					temp >>>= 1;
				}
			}
		}

		//UP LEFT
		temp = move >>> 9;
		if ((temp & opponentBoard) != 0L) 
		{
			temp >>>= 9;
			while ((temp & opponentBoard) != 0L) 
			{
				temp >>>= 9;
			}
			if ((temp & currentBoard) != 0L) 
			{
				temp <<= 9;
				while (temp != move) 
				{
					disksToFlip |= temp;
//					hash = C.gen.updateHashOtherColor(hash, temp);
					temp <<= 9;
				}
			}
		}

		//UP RIGHT
		temp = move >>> 7;
		if ((temp & opponentBoard) != 0L) 
		{
			temp >>>= 7;
			while ((temp & opponentBoard) != 0L) 
			{
				temp >>>= 7;
			}
			if ((temp & currentBoard) != 0L) 
			{
				temp <<= 7;
				while (temp != move) 
				{
					disksToFlip |= temp;
//					hash = C.gen.updateHashOtherColor(hash, temp);
					temp <<= 7;
				}
			}
		}

		//DOWN LEFT
		temp = move << 7;
		if ((temp & opponentBoard) != 0L) 
		{
			temp <<= 7;
			while ((temp & opponentBoard) != 0L) 
			{
				temp <<= 7;
			}
			if ((temp & currentBoard) != 0L) 
			{
				temp >>>= 7;
				while (temp != move) 
				{
					disksToFlip |= temp;
//					hash = C.gen.updateHashOtherColor(hash, temp);
					temp >>>= 7;
				}
			}
		}

		//DOWN RIGHT
		temp = move << 9;
		if ((temp & opponentBoard) != 0L) 
		{
			temp <<= 9;
			while ((temp & opponentBoard) != 0L) 
			{
				temp <<= 9;
			}
			if ((temp & currentBoard) != 0L) 
			{
				temp >>>= 9;
				while (temp != move) 
				{
					disksToFlip |= temp;
//					hash = C.gen.updateHashOtherColor(hash, temp);
					temp >>>= 9;
				}
			}
		}

		//board[1] |= tmpBoard;
		//board[0] ^= disksToFlip;
		//board[1] ^= disksToFlip;
		
		//board[0] |= move;
		//board[1] &= ~move; 
		
		t.board[t.getColor()] = (currentBoard ^ disksToFlip) | move;
		t.board[t.getOtherColor()] = t.getOtherPlayerBoard() ^ disksToFlip;
		
		
//		t.hash = C.gen.updateHashEmpty(hash, move, currentColor);
		
		t.swap();
		
		t.remainingStones--;
		
		return t;
	}
	
	public String longToString(long b)
	{
		String result = "";
		String binary = Long.toBinaryString(b);
		while(binary.length() != 64)
			binary = "0" + binary;
		for(int i = 0; i < 8; i++)
		{
			for(int j = 0; j < 8; j++)
			{
				result += " " + binary.charAt(i*8+j) + " ";
			}
			result += "\n";
		}
		return result;
	}
	
	public String toString()
	{
		// ☻ ☺
		String result = "0 1 2 3 4 5 6 7\n";
		for(int i = 0; i < 64; i++)
		{
			if((this.getCurrentPlayerBoard() & C.fields[i]) != 0)
			{
				result += "☻ ";
			}
			else if((this.getOtherPlayerBoard() & C.fields[i]) != 0)
			{
				result += "☺ ";
			}
			else
			{
				result += "_ ";
			}
			if(i %8 == 7)
			{
				result += "\n";
			}
		}
		return result;
				
	}
	
	public String toString(long movelong)
	{
		// ☻ ☺
		String result = "0 1 2 3 4 5 6 7\n";
		Board t = this.clone();
		t.swap();
		int move = Long.numberOfTrailingZeros(movelong);
		for(int i = 0; i < 64; i++)
		{
			if((t.getCurrentPlayerBoard() & C.fields[i]) != 0)
			{
				result += "☻ ";
			}
			else if((t.getOtherPlayerBoard() & C.fields[i]) != 0)
			{
				result += "☺ ";
			}
			else if(i == move)
			{
				result += "x ";
			}
			else
			{
				result += "_ ";
			}
			if(i %8 == 7)
			{
				result += "\n";
			}
			
		}
		return result;
		
	}
	
	public Board clone()
	{
		long[] t = {board[0], board[1]};
		return new Board(t, hash, currentColor, remainingStones);
	}
	
	public int getOtherColor()
	{
		return currentColor ^ 0x1; 
	}
	
	public int getColor()
	{
		return this.currentColor;
	}
	
	public long getCurrentPlayerBoard()
	{
		return board[currentColor];
	}
	
	public long getOtherPlayerBoard()
	{
		return board[this.getOtherColor()]; 
	}
	
	public void swap()
	{
		currentColor ^= 0x1;
	}
	
	public long getHash() {
		return hash;
	}

	public void setHash(long hash) {
		this.hash = hash;
	}

	public long[] getBoard() {
		return board;
	}

	public void setBoard(long[] board) {
		this.board[0] = board[0];
		this.board[1] = board[1];
	}
	
	public boolean gameHasEnded()
	{
		if(availableMoves() == 0L && availableMovesOpponent() == 0L)
		{
			return true;
		}
		return false;
	}
	
	public boolean gameHasEnded(long moves1)
	{
		if(moves1 == 0L && availableMovesOpponent() == 0L)
		{
			return true;
		}
		return false;
	}
	
	public int getScore()
	{
		return Score.score(this);
	}
	
	public int getScore(long moves)
	{
		return Score.score(this, moves);
	}

	public long availableMovesOpponent() 
	{
		long legal = 0L;
	    long potentialMoves;
	    long opponentBoard = board[currentColor];
	    long currentBoard = board[currentColor ^ 0x1];
	    long emptyBoard = ~(board[0] | board[1]);
	    
	 // UP
	    potentialMoves = (currentBoard >>> 8);
	    while (potentialMoves != 0L) {
	        potentialMoves = (potentialMoves & opponentBoard) >>> 8;
	        legal |= potentialMoves & emptyBoard;
	    }
	    // DOWN
	    potentialMoves = (currentBoard << 8);
	    while (potentialMoves != 0L) {
	        potentialMoves = (potentialMoves & opponentBoard) << 8;
	        legal |= potentialMoves & emptyBoard;
	    }
	    // LEFT
	    potentialMoves = (currentBoard >>> 1) & LEFT_MASK;
	    while (potentialMoves != 0L) {
	        potentialMoves = (potentialMoves & opponentBoard) >>> 1 & LEFT_MASK;
	        legal |= potentialMoves & emptyBoard;
	    }
	    // RIGHT
	    potentialMoves = (currentBoard << 1) & RIGHT_MASK;
	    while (potentialMoves != 0L) {
	        potentialMoves = (potentialMoves & opponentBoard) << 1 & RIGHT_MASK;
	        legal |= potentialMoves & emptyBoard;
	    }
	    // UP LEFT
	    potentialMoves = (currentBoard >>> 9) & LEFT_MASK;
	    while (potentialMoves != 0L) {
	        potentialMoves = (potentialMoves & opponentBoard) >>> 9 & LEFT_MASK;
	        legal |= potentialMoves & emptyBoard;
	    }
	    // UP RIGHT
	    potentialMoves = (currentBoard >>> 7) & RIGHT_MASK;
	    while (potentialMoves != 0L) {
	        potentialMoves = (potentialMoves & opponentBoard) >>> 7 & RIGHT_MASK;
	        legal |= potentialMoves & emptyBoard;
	    }
	    // DOWN LEFT
	    potentialMoves = (currentBoard << 7) & LEFT_MASK;
	    while (potentialMoves != 0L) {
	        potentialMoves = (potentialMoves & opponentBoard) << 7 & LEFT_MASK;
	        legal |= potentialMoves & emptyBoard;
	    }
	    // DOWN RIGHT
	    potentialMoves = (currentBoard << 9) & RIGHT_MASK;
	    while (potentialMoves != 0L) {
	        potentialMoves = (potentialMoves & opponentBoard) << 9 & RIGHT_MASK;
	        legal |= potentialMoves & emptyBoard;
	    }
	    return legal;
	}
	
	public int remainingStones()
	{
		return remainingStones;
	}
	
	public int stoneDifference()
	{
		return Long.bitCount(board[currentColor]) - Long.bitCount(board[getOtherColor()]);
	}
	
	public boolean equals(Board b)
	{
		if(b.board[0] == this.board[0] & b.board[1] == b.board[1])	return true;
		return false;
	}
	
	public long getBoard0()
	{
		return board[0];
	}
	public long getBoard1()
	{
		return board[1];
	}
}
