package glhf;

public class Score{
	
	//score 
	private static int c = 250;
	private static int e1 = 5;
	private static int e2 = -7;
	private static int e3 = 10;
	private static int c1 = -7;
	private static int c2 = -25;
	private static int d1 = 5, d2 = -7, d3 = -7, d4 = -7;
	private static int[] scoreBoard = {	c ,c1,e1,d1,d1,e1,c1,c,
										c1,c2,e2,d2,d2,e2,c2,c1,
										e1,e2,e3,d3,d3,e3,e2,e1,
										d1,d2,d3,d4,d4,d3,d2,d1,
										d1,d2,d3,d4,d4,d3,d2,d1,
										e1,e2,e3,d3,d3,e3,e2,e1,
										c1,c2,e2,d2,d2,e2,c2,c1,
										c ,c1,e1,d1,d1,e1,c1,c };
	//							c   c1	c2  e1/d1		e2/e3/d2/d3/d4
	//public static int[] s = {200, -7, -25, -5  		, -12};
	public static int[] s = {90, -3, -5, 2  		, -2};
	private static long[] scoreBoardFast = {0x8100000000000081L, 0x4281000000008142L, 0x0024000000002400L, 0x3c0081818181003cL, 0x003c5a7e7e5a3c00L};
//	public static int mob = 16;
//	public static int stable = 16;
	public static int mob = 5;
	public static int stable = 12;
	public static int border = 10;
	
	private static int[][] cornerDirections = {{1, 7},{-1, 7},{1, -7},{-1,-7}};
	private static int[][] cornerDirectionsSwap = {{7, 1},{7, -1},{-7, 1},{-7,-1}};
	
	private static final long RIGHT_MASK = 0xfefefefefefefefeL;
	private static final long LEFT_MASK 	= 0x7f7f7f7f7f7f7f7fL;
	private static final long JUSTRIGHT = 0x0101010101010101L;
	private static final long JUSTLEFT = 0x8080808080808080L;
	
	
	
	private static long[] rows = {0x8080808080808080L, 0x8080808080808080L >>> 1, 0x8080808080808080L >>> 2, 0x8080808080808080L >>> 3, 0x8080808080808080L >>> 4,
		0x8080808080808080L >>> 5, 0x8080808080808080L >>> 6, 0x8080808080808080L >>> 7};
	private static long[] columns = {0xff00000000000000L, 0x00ff000000000000L, 0x0000ff0000000000L, 0x000000ff00000000L, 0x00000000ff000000L, 0x0000000000ff0000L, 
		0x000000000000ff00L, 0x00000000000000ffL};
	private static long[] diag1 = {0x8040201008040201L, 0x4020100804020100L, 0x2010080402010000L, 0x1008040201000000L, 0x0804020100000000L, 0x0402010000000000L,
		0x0201000000000000L, 0x0100000000000000L,
		0x0080402010080402L, 0x0000804020100804L, 0x0000008040201008L, 0x0000000080402010L, 0x0000000000804020L, 0x0000000000008040L, 0x0000000000000080L};
	private static long[] diag2 = {0x0102040810204080L,  0x0204081020408000L, 0x0408102040800000L, 0x0810204080000000L, 0x1020408000000000L, 0x2040800000000000L,
		0x4080000000000000L, 0x8000000000000000L,
		0x0001020408102040L, 0x0000010204081020L, 0x0000000102040810L, 0x0000000001020408L, 0x0000000000010204L, 0x0000000000000102L, 0x0000000000000001L};
	
	
	/**
	 * @param board
	 * @param color input 1 or 0
	 * @return
	 */
	public static int score(Board b)
	{
		return score(b, b.availableMoves());
	}
	
	public static int score(Board b, long availableMoves)
	{
		//Board b = input.clone();
		if (b.gameHasEnded(availableMoves)) {
			return stoneDifference(b) * 1000;
		}
		int result = 0;
		
		
		long currentBoard = b.getCurrentPlayerBoard();
		long opponentBoard = b.getOtherPlayerBoard();
		
		//old
		//for(int i = 0; i < 64; i++)
		//{
		//	if((currentBoard & C.fields[i]) != 0)				result += scoreBoard[i];
		//	else if((opponentBoard & C.fields[i]) != 0)			result -= scoreBoard[i];
		//}
		
		for(int i = 0; i < s.length; i++)
		{
			result += s[i] * (Long.bitCount(currentBoard & scoreBoardFast[i]) - Long.bitCount(opponentBoard & scoreBoardFast[i]));
		}
		
		long emptyBoard = ~(currentBoard | opponentBoard);
		//parity
		if (Long.bitCount(emptyBoard) % 2 == 1)		result += 50; // if player has the last move
		if (Long.bitCount(emptyBoard) % 2 == 0)		result -= 50;

//		long availableMoves = b.availableMoves();
		availableMoves &= 0xbd3cffffffff3cbdL;
		long availableMoves2 = b.availableMovesOpponent() & 0xbd3cffffffff3cbdL;
		
		// mobility
		result += ((Long.bitCount(availableMoves) - Long.bitCount(availableMoves2)) * mob);
		result += border * (-Long.bitCount(borders(currentBoard, opponentBoard)) + Long.bitCount(borders(opponentBoard, currentBoard)));

		//stable discs edge  -------  not done yet ---- need new algo
		result += stableDisksNew(currentBoard) * stable;
		result -= stableDisksNew(opponentBoard) * stable;
		
//		if((s[0] & (currentBoard | opponentBoard)) != 0)
//		{
//			//result += stable * stableDisksNonEdge(currentBoard, opponentBoard);
//			result += stable * stableDisks(currentBoard);
//		}
		
		
		//special occasions
		//corner can be taken by opponent or player
		//if((availableMoves & C.fields[0])  != 0)			result += c;
		//if((availableMoves & C.fields[7])  != 0)			result += c;
		//if((availableMoves & C.fields[56])  != 0)			result += c;
		//if((availableMoves & C.fields[63])  != 0)			result += c;
		//if((availableMoves2 & C.fields[0])  != 0)			result -= c;
		//if((availableMoves2 & C.fields[7])  != 0)			result -= c;
		//if((availableMoves2 & C.fields[56])  != 0)		result -= c;
		//if((availableMoves2 & C.fields[63])  != 0)		result -= c;
		
		return result;
	}
	
	
	private static int stableDisksNonEdge(long currentBoard, long opponentBoard)
	{
		long stable = 0xffffffffffffffffL;
		long stable2 = stable;
		for(int i = 0; i < rows.length; i++)
		{
			if((currentBoard & rows[i]) != rows[i])				stable &= ~rows[i];
			if((currentBoard & columns[i]) != columns[i])		stable &= ~columns[i];
			if((opponentBoard & rows[i]) != rows[i])			stable2 &= ~rows[i];
			if((opponentBoard & columns[i]) != columns[i])		stable2 &= ~columns[i];
		}
		for(int i = 0; i < diag1.length; i++)
		{
			if((currentBoard & diag1[i]) != diag1[i])			stable &= ~diag1[i];
			if((currentBoard & diag2[i]) != diag2[i])			stable &= ~diag2[i];
			if((opponentBoard & diag1[i]) != diag1[i])			stable2 &= ~diag1[i];
			if((opponentBoard & diag2[i]) != diag2[i])			stable2 &= ~diag2[i];
		}
		
		return (Long.bitCount(stable) - Long.bitCount(stable2));
	}
	//convert to byte would bring possible speed up
	//not yet finished
	private static int stableDisksNew(long board)
	{
		return Long.bitCount(stableDisksNewLong(board));
	}
	
	public static long stableDisksNewLong(long board)
	{
		//corner 0: direction 1, 8
		long stable0 = C.fields[0] & board;
		long stableSaved = stable0;
		long w1, w2;
		stable0 |= ((stableSaved & LEFT_MASK) << 1 & board) |  (stableSaved << 8 & board);
		while(stable0 != 0L & stable0 != stableSaved)
		{
			stableSaved = stable0;
			w1= (stableSaved << 8 & board) & ~stableSaved;
			w2 = ((stableSaved & LEFT_MASK) << 1 & board) & ~stableSaved;
			if((w1 & JUSTRIGHT) != 0L | ((w1 >>> 1 & stableSaved) != 0L))
				stable0 |=  w1;
			if((w2 & 0x00000000000000ffL) != 0L | ((w2 >>> 8) & stableSaved) != 0L)
				stable0 |= w2;
			//stable0 |= ((stableSaved & LEFT_MASK) << 1 & board) |  (stableSaved << 8 & board);
			
		}
		//corner 7: direction -1, 7, 8
		long stable7 = C.fields[7] & board;
		stableSaved = stable7;
		stable7 |= ((stable7 & RIGHT_MASK) >>> 1 & board) |  (stable7 << 8 & board);
		while(stable7 != 0L & stable7 != stableSaved)
		{
			stableSaved = stable7;
			w1 = (stable7 << 8 & board) & ~stableSaved;
			w2 = ((stable7 & RIGHT_MASK) >>> 1 & board) & ~stableSaved;
			if((w1 & JUSTLEFT) != 0L | ((w1 << 1) & stableSaved) != 0L)
				stable7 |= w1;
			if((w2 & 0x00000000000000ffL) != 0L | ((w2 >>> 8) & stableSaved) != 0L)
				stable7 |= w2;
			//stable7 |= ((stable7 & RIGHT_MASK) >>> 1 & board) |  (stable7 << 8 & board);
		}
		//corner 56: direction 1, -7, -8
		long stable56 = C.fields[56] & board;
		stableSaved = stable56;
		stable56 |= ((stable56 & LEFT_MASK) << 1 & board) |  (stable56 >>> 8 & board);
		while(stable56 != 0L & stable56 != stableSaved)
		{
			stableSaved = stable56;
			w1 = (stable56 >>> 8 & board) & ~stableSaved;
			w2 = ((stable56 & LEFT_MASK) << 1 & board) & ~stableSaved;
			if((w1 & JUSTRIGHT) != 0L | ((w1 >> 1) & stableSaved) != 0L)
				stable56 |= w1;
			if((w2 & 0xff00000000000000L) != 0L | ((w2 << 8) & stableSaved) != 0L)
				stable56 |= w2;
			//stable56 |= ((stable56 & LEFT_MASK) << 1 & board) |  (stable56 >>> 8 & board);
		}
		
		//corner 63: direction -1, -9, -8
		long stable63 = C.fields[63] & board;
		stableSaved = stable63;
		stable63 |= ((stable63 & RIGHT_MASK) >>> 1 & board) |  (stable63 >>> 8 & board);
		while(stable63 != 0L & stable63 != stableSaved)
		{
			stableSaved = stable63;
			w1 = (stable63 >>> 8 & board) & ~stableSaved;
			w2 = ((stable63 & RIGHT_MASK) >>> 1 & board) & ~stableSaved;
			if((w1 & JUSTLEFT) != 0L | ((w1 << 1) & stableSaved) != 0L)
				stable63 |= w1;
			if((w2 & 0xff00000000000000L) != 0L | ((w2 << 8) & stableSaved) != 0L)
				stable63 |= w2;
			//stable63 |= ((stable63 & RIGHT_MASK) >>> 1 & board) |  (stable63 >>> 8 & board);
		}
		return stable0 | stable7 | stable56 | stable63;
	}
	//inspired by Xiaolong
	public static long borders(long currentBoard, long opponentBoard)
	{
		long emptyBoard = ~(currentBoard | opponentBoard);
		return currentBoard & (emptyBoard >>> 8 | emptyBoard << 8 | (RIGHT_MASK & emptyBoard) >>> 1 | (LEFT_MASK & emptyBoard) << 1 | 
				(RIGHT_MASK & emptyBoard) >>> 7 | (LEFT_MASK & emptyBoard) >>> 9 | (LEFT_MASK & emptyBoard) << 7 | (RIGHT_MASK & emptyBoard) << 9);
	}
	
	private static int stableDisksOld(long board)
	{
		int result = 0;
		for(int i = 0; i < 4; i++)
		{
			result += -1 + stableEdge(board, C.corners[i], cornerDirections[i])
					+ stableEdge(board, C.corners[i], cornerDirectionsSwap[i]);
		}
		return result;
	}
	
	private static int stableEdge(long board, int field, int direction[]) 
	{
		if(field >= 0 && (board & C.fields[field]) != 0)
		{
			return 1 + stableEdge(board, field+direction[0], direction)
					+ stableMiddle(board, field+direction[1], direction);
		}
		
		return 0;
	}
	private static int stableMiddle(long board, int field, int[] direction)
	{
		if(field - direction[0] < 0 || field - direction[1] < 0)	return 0;
		if((board & C.fields[field - direction[0]]) != 0 && (board & C.fields[field - direction[1]]) != 0)
		{
			return 1 + stableMiddle(board, field + direction[1], direction);
		}
		return 0;
		
	}
	
	public static int stoneDifference(Board board)
	{
		return Long.bitCount(board.getCurrentPlayerBoard()) - Long.bitCount(board.getOtherPlayerBoard());
	}
	
	private static long emptyBoard(long[] b)
	{
		return ~(b[0] | b[1]);
	}
	
	public static String scoreBoard()
	{
		String result = "";
		for(int i = 0; i < 8; i++)
		{
			for(int j = 0; j < 8; j++)
			{
				result += scoreBoard[i*8 + j] + "\t";
			}
			result += "\n";
		}
		return result;
	}
	
	public static String stableMasks()
	{
		String result = "";
		for(int i = 0; i < rows.length; i++)
		{
			result += infos(rows[i]) + "\n";
		}
		for(int i = 0; i < columns.length; i++)
		{
			result += infos(columns[i]) + "\n";
		}
		for(int i = 0; i < diag1.length; i++)
		{
			result += infos(diag1[i]) + "\n";
		}
		for(int i = 0; i < diag2.length; i++)
		{
			result += infos(diag2[i]) + "\n";
		}
		return result;
	}
	private static String infos(long input)
	{
		// ☻ ☺
		String result = "0 1 2 3 4 5 6 7\n";
		//int move = Long.numberOfTrailingZeros(movelong);
		for(int i = 0; i < 64; i++)
		{
			if((input & C.fields[i]) != 0)
			{
				result += "☻ ";
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
}