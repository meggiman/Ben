package glhf;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeoutException;

import reversi.BoardFactory;
import reversi.Coordinates;

public class testset {
	public static void main(String args[])
	{
		C.initialize((int) Math.pow(2, 21));
		System.out.println("HashMap initialized! size: " + Math.pow(2, 21) + " size(MB): " + C.hashMap.size() + "MB");
		//Board.initialize();
		System.out.println("Go!");
		
		
		
		//System.out.println(Score.stableMasks());
		
		Random rand = new Random();
		
		Board[] boards = new Board[50000];
		for(int i = 0; i < boards.length; i++)
		{
			boards[i] = randBoard();
		}
		
		MinMax MinMax = new MinMax();
		
		System.out.println(infos(0xe7a5c30000c3a5e7L) + "\n StableDisks: \n" + infos(Score.stableDisksNewLong(0xe7a5c30000c3a5e7L)));
		for(int i = 0; i < 10; i++)
		{
			System.out.println(infos(boards[i]) + "\n StableDisks: \n" + infos(Score.stableDisksNewLong(boards[i].getBoard0() | boards[i].getBoard1())));
		}
		
		
//		long[] startBoard = {0x0000001800000000L, 0x0000000018000000L};
//		Board t4 = new Board(startBoard, C.gen.BoardToHash(startBoard), 0);
//		t4 = t4.makeMove(t4.availableMovesArray()[0]);
//		System.out.println("test: currentBoard & 0x81 / currentBoard & 0x8181003C \n" + infos(boards[0].getCurrentPlayerBoard()) + "\n" + infos(boards[0].getCurrentPlayerBoard() & 0x81) + "\n" + infos(boards[0].getCurrentPlayerBoard() & 0x8181003c));
//		System.out.println("testhash: Board:\n" + infos(t4, t4.availableMovesArray()[0]) + "\nHash:\n" + infos(C.gen.updateHashEmpty(t4.getHash(),  t4.availableMovesArray()[0], 0)));
//		System.out.println("testhash: Board:\n" + infos(t4, t4.availableMovesArray()[0]) + "\nHash:\n" + infos(C.gen.updateHashOtherColor(t4.getHash(),  0x0000001000000000L)));
//		System.out.println("testhash: Board:\n" + infos(t4, t4.availableMovesArray()[0]) + "\nHash:\n" + infos(t4.makeMove(t4.availableMovesArray()[0]).getHash()));
		
//		long[] movesSaved;
		for(Board t : boards)
		{
			for(int i = 0; i < 10; i++)
			{
				t.availableMovesArray();
				t.availableMovesArraySort();
				t.availableMovesArrayNew();
			}
		}
		
		System.out.println("TEST: availableMoves");
		
		long currentTime = System.currentTimeMillis();
		for(Board t : boards)
		{
			for(int i = 0; i < 10; i++)
			{
				t.availableMovesArray();
			}
		}
		System.out.println("OLD: time used: \t\t" + (System.currentTimeMillis()- currentTime));
		
		currentTime = System.currentTimeMillis();
		for(Board t : boards)
		{
			for(int i = 0; i < 10; i++)
			{
				t.availableMovesArraySort();
			}
		}
		System.out.println("OLD + MoveOrder: time used: \t" + (System.currentTimeMillis()- currentTime));
		
		currentTime = System.currentTimeMillis();
		for(Board t : boards)
		{
			for(int i = 0; i < 10; i++)
			{
				t.availableMovesArrayNew();
			}
		}
		System.out.println("NEW + MoveOrder: time used: \t" + (System.currentTimeMillis()- currentTime));
		
		currentTime = System.currentTimeMillis();
		for(Board t : boards)
		{
			for(int i = 0; i < 10; i++)
			{
				t.availableMovesNew();
			}
		}
		System.out.println("NEW - array: time used: \t" + (System.currentTimeMillis()- currentTime));
		
		currentTime = System.currentTimeMillis();
		for(Board t : boards)
		{
			for(int i = 0; i < 10; i++)
			{
				t.availableMoves();
			}
		}
		System.out.println("OLD - array: time used: \t" + (System.currentTimeMillis()- currentTime));
		
		
		System.out.println("\nTEST: BitCount");
		long[] randtemp = new long[25000000];
		for(int i = 0; i < 25000000; i++) randtemp[i] = rand.nextLong();
		currentTime = System.currentTimeMillis();
		for(int i = 0; i < 1; i++)
		{
			for(int j = 0; j < 25000000; j++)		Long.bitCount(randtemp[j]);
		}
		System.out.println("OLD: time used: \t\t" + (System.currentTimeMillis()- currentTime));
		
		currentTime = System.currentTimeMillis();
		for(int i = 0; i < 1; i++)
		{
			for(int j = 0; j < 25000000; j++)		parallelCount(randtemp[j]);
		}
		System.out.println("NEW: time used: \t\t" + (System.currentTimeMillis()- currentTime));
		
		
		System.out.println("\nTEST: makeMove");
		long[][] moves = new long[boards.length][];
		for(int i = 0; i < boards.length; i++)
		{
			moves[i] = boards[i].availableMovesArray();
		}
		
		currentTime = System.currentTimeMillis();
		for(int i = 0; i < 1; i++)
		{
			for(int j = 0; j < boards.length; j++)
			{
				long[] movest = moves[j];
				for(long move : movest)
				{
					boards[j].makeMoveOld(move);
				}
			}
		}
		System.out.println("OLD: time used: \t\t" + (System.currentTimeMillis()- currentTime));
		
		currentTime = System.currentTimeMillis();
		for(int i = 0; i < 1; i++)
		{
			for(int j = 0; j < boards.length; j++)
			{
				long[] movest = moves[j];
				for(long move : movest)
				{
					boards[j].makeMove(move);
				}
			}
		}
		System.out.println("NEW: time used: \t\t" + (System.currentTimeMillis()- currentTime));
		
		currentTime = System.currentTimeMillis();
		for(int i = 0; i < 1; i++)
		{
			for(int j = 0; j < boards.length; j++)
			{
				long[] movest = moves[j];
				for(long move : movest)
				{
					boards[j].makeMoveWithoutHash(move);
				}
			}
		}
		System.out.println("NEW - Hash: time used: \t\t" + (System.currentTimeMillis()- currentTime));
		
		currentTime = System.currentTimeMillis();
		for(int i = 0; i < 1; i++)
		{
			for(int j = 0; j < boards.length; j++)
			{
				long[] movest = moves[j];
				for(long move : movest)
				{
					makeMoveXiao(boards[j], move);
				}
			}
		}
		
		System.out.println("Xiaolong + Hash: time used: \t" + (System.currentTimeMillis()- currentTime));
		long disksToFlip = 0;
//		long hash = 0;
//		long[] boardSaved = new long[2];
		currentTime = System.currentTimeMillis();
		for(int i = 0; i < 1; i++)
		{
			for(int j = 0; j < boards.length; j++)
			{
				long[] movest = moves[j];
				for(long move : movest)
				{
					
					disksToFlip = boards[j].disksToFlip(move);
//					hash = boards[j].getHash();
//					Board temp = boards[j].clone();
//					boardSaved[0] = boards[j].getBoard0();
//					boardSaved[1] = boards[j].getBoard1();
					boards[j].flipDisks(disksToFlip, move);
//					boards[j].undoMove(disksToFlip, move);
//					boards[j].setBoard(boardSaved);
//					boards[j].setHash(hash);
				}
			}
		}
		
		System.out.println("NEWEST: time used: \t\t" + (System.currentTimeMillis()- currentTime));
		
		currentTime = System.currentTimeMillis();
		for(int i = 0; i < 1; i++)
		{
			for(int j = 0; j < boards.length; j++)
			{
				long[] movest = moves[j];
				for(long move : movest)
				{
					makeMoveXiaoWithoutHash(boards[j], move);
				}
			}
		}
		System.out.println("Xiaolong-Hash: time used: \t" + (System.currentTimeMillis()- currentTime) + "\n");

		//System.out.println(OpeningBook.printBook());
		checkMoves();
		//testFFotest40();
		
		reversi.GameBoard gb = BoardFactory.create();
		Board t = new Board(gb, 1);
		t.swap();
		
//		long[] btold = { 0x00080c0c04200000L, 0x0000001038101000L};
		long[] bt = { 0x000c0c1c00000000L, 0x000000203c301000L};
		t = new Board(bt, C.gen.BoardToHash(bt), 1);
		
		long time = 200;
//		int hashTableSize = 3000000;
		int result = 0;
		long bestMove = 0L;
		System.out.println("\nTest: MinMax");
		currentTime = System.currentTimeMillis();
//		C.initialize(hashTableSize);
		C.hashMap.renew(0);
		MinMax.timeout = System.currentTimeMillis() + time;
		MinMax.nodes = 0;
		MinMax.cuts = 0;
		MinMax.hashHits = 0;
		int maxdepth = 0;
		try
		{
			for(int i = 1; i < 30; i++)
			{
				result = MinMax.getNextMoveOld(t, i, -100000, 100000);
				bestMove = MinMax.getBestMove();
				maxdepth = i;
				System.out.println("depth: " + i + "\t " + convert(MinMax.getBestMove()).toString() + " score: " + result + "\ttime: " + (System.currentTimeMillis()- currentTime));
			}
		}
		catch(TimeoutException e){}
		System.out.println("Old:\t nodes: " + MinMax.nodes + " depth: " + maxdepth 
				+ " Chosen Move: " + convert(bestMove).toString() + " value: " + result
				+ " time: " + (System.currentTimeMillis()- currentTime)
				+ " hashHits: " + MinMax.hashHits);
		long nodes = MinMax.nodes;
		
//		C.initialize(hashTableSize);
		C.hashMap.renew(0);
		currentTime = System.currentTimeMillis();
		MinMax.timeout = System.currentTimeMillis() + time;
		MinMax.nodes = 0;
		MinMax.cuts = 0;
		MinMax.hashHits = 0;
		try
		{
			for(int i = 1; i < 30; i++)
			{
				result = MinMax.getNextMove(t, i, -100000, 100000);
				bestMove = MinMax.getBestMove();
				maxdepth = i;
				System.out.println("depth: " + i + "\t " + convert(MinMax.getBestMove()).toString() + " score: " + result + "\ttime: " + (System.currentTimeMillis()- currentTime));
			}
		}
		catch(TimeoutException e){}
		System.out.println("New:\t nodes: " + MinMax.nodes + " depth: " + maxdepth 
				+ " Chosen Move: " + convert(bestMove).toString() + " value: " + result
				+ " time: " + (System.currentTimeMillis()- currentTime)
				+ " hashHits: " + MinMax.hashHits);
		System.out.println("%-increase: " + (((double) MinMax.nodes) / ((double) nodes) * 100) + "%");
		
//		C.initialize(hashTableSize);
		C.hashMap.renew(0);
		currentTime = System.currentTimeMillis();
		MinMax.timeout = System.currentTimeMillis() + time;
		MinMax.nodes = 0;
		MinMax.cuts = 0;
		MinMax.hashHits = 0;
		try
		{
			for(int i = 1; i < 30; i++)
			{
				result = MinMax.negaScout(t, i, -1000000, 1000000);
				bestMove = MinMax.getBestMove();
				maxdepth = i;
				System.out.println("depth: " + i + "\t " + convert(MinMax.getBestMove()).toString() + " score: " + result + "\ttime: " + (System.currentTimeMillis()- currentTime));
			}
		}
		catch(TimeoutException e){}
		System.out.println("NegaScout:\t nodes: " + MinMax.nodes + " depth: " + maxdepth 
				+ " Chosen Move: " + convert(bestMove).toString() + " value: " + result
				+ " time: " + (System.currentTimeMillis()- currentTime)
				+ " hashHits: " + MinMax.hashHits);
		
//		C.initialize(hashTableSize);
		C.hashMap.renew(0);
		currentTime = System.currentTimeMillis();
		MinMax.timeout = System.currentTimeMillis() + time;
		MinMax.nodes = 0;
		MinMax.cuts = 0;
		MinMax.hashHits = 0;
		
		try
		{
			for(int i = 1; i < 30; i++)
			{
				result = MinMax.negaScoutNew(t, i, -1000000, 1000000);
				bestMove = MinMax.getBestMove();
				maxdepth = i;
				System.out.println("depth: " + i + "\t" + convert(MinMax.getBestMove()).toString() + " score: " + result + "\ttime: " + (System.currentTimeMillis()- currentTime));
			}
		}
		catch(TimeoutException e){}
		System.out.println("NegaScoutNew:\t nodes: " + MinMax.nodes + " depth: " + maxdepth 
				+ " Chosen Move: " + convert(bestMove).toString() + " value: " + result
				+ " time: " + (System.currentTimeMillis()- currentTime)
				+ " hashHits: " + MinMax.hashHits);
		
		
		
		
//		C.initialize(hashTableSize);
		C.hashMap.renew(0);
		currentTime = System.currentTimeMillis();
		MinMax.timeout = System.currentTimeMillis() + time;
		MinMax.nodes = 0;
		MinMax.cuts = 0;
		MinMax.hashHits = 0;
		
		try
		{
			for(int i = 1; i < 30; i++)
			{
				result = MinMax.negaScoutNewer(t, i, -1000000, 1000000);
				bestMove = MinMax.getBestMove();
				maxdepth = i;
				System.out.println("depth: " + i + "\t" + 
				convert(MinMax.getBestMove()).toString() + 
				" score: " + result + "\ttime: " + (System.currentTimeMillis()- currentTime));
			}
		}
		catch(TimeoutException e){}
		System.out.println("NegaScoutNew:\t nodes: " + MinMax.nodes + " depth: " + maxdepth 
				+ " Chosen Move: " + convert(bestMove).toString()
				+ " value: " + result
				+ " time: " + (System.currentTimeMillis()- currentTime)
				+ " hashHits: " + MinMax.hashHits);
		
		//endgame
		
		
		long[] endGameBoard = {0x0000434dfd513170L, 0x3e7c3c32022e0200L};
		t = new Board(endGameBoard, C.gen.BoardToHash(endGameBoard), 0);
		Board t2 = new Board(endGameBoard, C.gen.BoardToHash(endGameBoard), 0);
		t= t.makeMove(1L);
		t = t.makeMove(1L << 1);
		System.out.println(t2.getColor() + " remainingStones: " + t2.remainingStones() + "\n" + infos(t2, t2.availableMoves()));
		System.out.println("\nTest: EndGame \n WIN/LOSS depth: " + t2.remainingStones());

		time = 5000;
		
		
//		C.initialize(hashTableSize);
		C.hashMap.renew(0);
		currentTime = System.currentTimeMillis();
		MinMax.timeout = System.currentTimeMillis() + time;
		MinMax.nodes = 0;
		MinMax.cuts = 0;
		MinMax.hashHits = 0;
		
		try
		{
			result = MinMax.negaScout(t2,30, -1, 1);
			bestMove = MinMax.getBestMove();		
		}
		catch(TimeoutException e){}
		System.out.println("Endgame:\t\t nodes: " + MinMax.nodes
				+ " Chosen Move: " + convert(bestMove).toString()
				+ " value: " + result
				+ " time: " + (System.currentTimeMillis()- currentTime)
				+ " hashHits: " + MinMax.hashHits);
		
//		C.initialize(hashTableSize);
		C.hashMap.renew(0);
		currentTime = System.currentTimeMillis();
		MinMax.timeout = System.currentTimeMillis() + time;
		MinMax.nodes = 0;
		MinMax.cuts = 0;
		MinMax.hashHits = 0;
		
		try
		{
			result = MinMax.endGameNegaMax(t2, -1, 1, t2.availableMoves());
			bestMove = MinMax.getBestMove();		
		}
		catch(TimeoutException e){}
		System.out.println("NegamaxEndGame+sort:\t nodes: " + MinMax.nodes
				+ " Chosen Move: " + convert(bestMove).toString()
				+ " value: " + result
				+ " time: " + (System.currentTimeMillis()- currentTime)
				+ " hashHits: " + MinMax.hashHits);
		
		C.hashMap.renew(0);
		currentTime = System.currentTimeMillis();
		MinMax.timeout = System.currentTimeMillis() + time;
		MinMax.nodes = 0;
		MinMax.cuts = 0;
		MinMax.hashHits = 0;
		
		try
		{
			result = MinMax.endGameNegaMaxNew(t2, -1, 1, t2.availableMoves());
			bestMove = MinMax.getBestMove();		
		}
		catch(TimeoutException e){}
		System.out.println("NegamaxEndGame+sortNEW:\t nodes: " + MinMax.nodes
				+ " Chosen Move: " + convert(bestMove).toString()
				+ " value: " + result
				+ " time: " + (System.currentTimeMillis()- currentTime)
				+ " hashHits: " + MinMax.hashHits);
		
		
		C.hashMap.renew(0);
		currentTime = System.currentTimeMillis();
		MinMax.timeout = System.currentTimeMillis() + time;
		MinMax.nodes = 0;
		MinMax.cuts = 0;
		MinMax.hashHits = 0;
		
		try
		{
			result = MinMax.endGameNegaCStar(t2, -64, 64);
			bestMove = MinMax.getBestMove();		
		}
		catch(TimeoutException e){}
		System.out.println("endGameNegaCStar:\t nodes: " + MinMax.nodes
//				+ " Chosen Move: " + convert(bestMove).toString()
				+ " value: " + result
				+ " time: " + (System.currentTimeMillis()- currentTime)
				+ " hashHits: " + MinMax.hashHits);
		
		C.hashMap.renew(0);
		currentTime = System.currentTimeMillis();
		MinMax.timeout = System.currentTimeMillis() + time;
		MinMax.nodes = 0;
		MinMax.cuts = 0;
		MinMax.hashHits = 0;
		
		try
		{
			result = MinMax.MTDf(0, t2);
			bestMove = MinMax.getBestMove();		
		}
		catch(TimeoutException e){}
		System.out.println("MTDf:\t\t\t nodes: " + MinMax.nodes
//				+ " Chosen Move: " + convert(bestMove).toString()
				+ " value: " + result
				+ " time: " + (System.currentTimeMillis()- currentTime)
				+ " hashHits: " + MinMax.hashHits);
		
		
		System.out.println("\nTest: EndGame \n ECAXT depth: " + t.remainingStones());
		
		time = 10000;
		
//		C.initialize(hashTableSize);
		C.hashMap.renew(0);
		currentTime = System.currentTimeMillis();
		MinMax.timeout = System.currentTimeMillis() + time;
		MinMax.nodes = 0;
		MinMax.cuts = 0;
		MinMax.hashHits = 0;
		
		try
		{
			result = MinMax.getNextMove(t,30, -1000000, 1000000);
			bestMove = MinMax.getBestMove();		
		}
		catch(TimeoutException e){}
		System.out.println("Endgame:\t\t nodes: " + MinMax.nodes
				+ " Chosen Move: " + convert(bestMove).toString()
				+ " value: " + result
				+ " time: " + (System.currentTimeMillis()- currentTime)
				+ " hashHits: " + MinMax.hashHits);
		
//		C.initialize(hashTableSize);
		C.hashMap.renew(0);
		currentTime = System.currentTimeMillis();
		MinMax.timeout = System.currentTimeMillis() + time;
		MinMax.nodes = 0;
		MinMax.cuts = 0;
		MinMax.hashHits = 0;
		
		try
		{
			result = MinMax.endGameNegaMax(t, -64, 64, t.availableMoves());
			bestMove = MinMax.getBestMove();		
		}
		catch(TimeoutException e){}
		System.out.println("NegamaxEndGame+sort:\t nodes: " + MinMax.nodes
				+ " Chosen Move: " + convert(bestMove).toString()
				+ " value: " + result
				+ " time: " + (System.currentTimeMillis()- currentTime)
				+ " hashHits: " + MinMax.hashHits);
		
		C.hashMap.renew(0);
		currentTime = System.currentTimeMillis();
		MinMax.timeout = System.currentTimeMillis() + time;
		MinMax.nodes = 0;
		MinMax.cuts = 0;
		MinMax.hashHits = 0;
		
		try
		{
			result = MinMax.endGameNegaMaxNew(t, -64, 64, t.availableMoves());
			bestMove = MinMax.getBestMove();		
		}
		catch(TimeoutException e){}
		System.out.println("NegamaxEndGame+sortNEW:\t nodes: " + MinMax.nodes
				+ " Chosen Move: " + convert(bestMove).toString()
				+ " value: " + result
				+ " time: " + (System.currentTimeMillis()- currentTime)
				+ " hashHits: " + MinMax.hashHits);
		
		
		C.hashMap.renew(0);
		currentTime = System.currentTimeMillis();
		MinMax.timeout = System.currentTimeMillis() + time;
		MinMax.nodes = 0;
		MinMax.cuts = 0;
		MinMax.hashHits = 0;
		
		try
		{
			result = MinMax.endGameNegaCStar(t, -64, 64);
			bestMove = MinMax.getBestMove();		
		}
		catch(TimeoutException e){}
		System.out.println("endGameNegaCStar:\t nodes: " + MinMax.nodes
//				+ " Chosen Move: " + convert(bestMove).toString()
				+ " value: " + result
				+ " time: " + (System.currentTimeMillis()- currentTime)
				+ " hashHits: " + MinMax.hashHits);
		
		
		C.hashMap.renew(0);
		currentTime = System.currentTimeMillis();
		MinMax.timeout = System.currentTimeMillis() + time;
		MinMax.nodes = 0;
		MinMax.cuts = 0;
		MinMax.hashHits = 0;
		
		try
		{
			result = MinMax.MTDf(0, t);
			bestMove = MinMax.getBestMove();		
		}
		catch(TimeoutException e){}
		System.out.println("MTDf:\t\t\t nodes: " + MinMax.nodes
//				+ " Chosen Move: " + convert(bestMove).toString()
				+ " value: " + result
				+ " time: " + (System.currentTimeMillis()- currentTime)
				+ " hashHits: " + MinMax.hashHits);
		
		testFFotest40();
		
	}
	
	private static int parallelCount(long paramLong)
	  {
	    long l = paramLong - (paramLong >>> 1 & 0x55555555);
	    l = (l & 0x33333333) + (l >>> 2 & 0x33333333);
	    return (int)((l + (l >>> 4) & 0xF0F0F0F) * 72340172838076673L >>> 56);
	  }
	private static void checkRandomMovesRandomBoards()
	{
		Random rand = new Random();
		for(int i = 0; i < 100000; i++)
		{
			long[] board = {rand.nextLong(), rand.nextLong()};
			for(int j = 0; j < 64; j++)
			{
				if((board[0] & C.fields[j] & board[1]) == C.fields[j])
				{
					board[1] ^= C.fields[j];
				}
			}
			Board t = new Board(board, C.gen.BoardToHash(board), 0);
			
			long[] moves = t.availableMovesArray();
			for(long move : moves)
			{
				t.makeMove(move);
			}
			
			
			
			
			//System.out.println(i + " / 1000000");
		}
	}

	private static void checkRandomMovesRandomBoardsNew()
	{
		Random rand = new Random();
		for(int i = 0; i < 100000; i++)
		{
			long[] board = {rand.nextLong(), rand.nextLong()};
			for(int j = 0; j < 64; j++)
			{
				if((board[0] & C.fields[j] & board[1]) == C.fields[j])
				{
					board[1] ^= C.fields[j];
				}
			}
			Board t = new Board(board, C.gen.BoardToHash(board), 0);
			
			long[] moves = t.availableMovesArray();
			for(long move : moves)
			{
				t.makeMove(move);
			}
			
			
			
			
			//System.out.println(i + " / 1000000");
		}
	}

	private static void checkSpecificMoves()
	{
		long[][] testBoards = new long[1][2];
		testBoards[0][0] = 0x0000180000000000L;
		testBoards[0][1] = 0x003c243c00000000L;
		
		Board t = new Board(testBoards[0], C.gen.BoardToHash(testBoards[0]), 0);
		System.out.println(t.toString() + "\n available Moves" + t.longToString(t.availableMoves()));
		
		long[] moves = t.availableMovesArray();
		for(long move : moves)
		{
			Board tmp = t.makeMove(move);
			System.out.println("Move: \n" + t.longToString(move) + "\n Board: \n" + tmp.toString());
		}
	}
	
	public static void checkMoves()
	{
		long start = System.currentTimeMillis();
		for(int k = 0; k < 1000; k++)
		{
			reversi.GameBoard gb = BoardFactory.create();
			Board t = new Board(gb, 1);
			t.swap();
			//System.out.println(gb.countStones(1) + "/" + gb.countStones(2));
			
			Random rand = new Random();
			int color = 1;
			while(gb.isMoveAvailable(1) | gb.isMoveAvailable(2))
			{
				ArrayList<Coordinates> moves = new ArrayList<Coordinates>();
	
				//getMoves GameBoard
				for(int i = 1; i <= 8; i++)
				{
					for(int j = 1; j <= 8; j++)
					{
						if(gb.checkMove(color, new Coordinates(i, j)))
						{
							moves.add(new Coordinates(i,j));
						}
					}
				}
				
				//getMoves Board
				long[] moves2 = t.availableMovesArraySort();
				//printInfos(t);
				for(int i = 0; i < moves2.length; i++)
				{
					if(!moves.contains(convert(moves2[i])))
					{
						System.out.println("Availablemoves ERROR!!!");
					}
				}
				if(moves2.length != moves.size())
				{
					System.out.println("Error: not the same avalableMoves! "+ moves2.length + "/" + moves.size());
					printInfos(t);
					break;
				}
				//System.out.print(moves2.length + " ");
				if(moves2.length == 0)
				{
					color = otherColor(color);
					t.swap();
				}
				else
				{
					int r = rand.nextInt(moves2.length);
					
					
					
					Board tmp = t.makeMoveWithoutHash(moves2[r]);
					gb.checkMove(color, convert(moves2[r]));
					gb.makeMove(color, convert(moves2[r]));
					color = otherColor(color);
					if(!tmp.equals(new Board(gb, color-1)))
					{
						System.out.println("Error: makeMove fail!!!");
						System.out.println("color board1: " + tmp.getColor() + " | color board2: " + (color-1));
						System.out.println("Board1(fail board): \n" + infos(tmp) + "\n Board 2(correct board): \n" + infos(new Board(gb,color-1)));
						
						System.out.println(infos(t, moves2[r]));
						
						//for the purpose of debugging, move gets repeated
						//t.makeMove(moves2[r]);
						
						break;
					}
//					if(tmp.getHash() != C.gen.BoardToHash(tmp))
//					{
//						System.out.println("Hasherror!\n" +"board: \n" + infos(tmp) + "\nhash:\n"  + infos(tmp.getHash()) + "\nhash correct:\n" + infos(C.gen.BoardToHash(tmp.getBoard())));
//						break;
//					}
					if(tmp.remainingStones() != Long.bitCount(~(tmp.getBoard0() | tmp.getBoard1())))
					{
						System.out.println("remainingStones error!");
					}
					t	=	tmp;
				}
			}
			
			if(k % 100 == 99)	System.out.println("Games played with CheckMoves routine: " + (k+1) + "/1000 time: " + (System.currentTimeMillis() - start));
		}
	}
	
	private static void playGames(int numberOfGames)
	{
		for(int k = 0; k < numberOfGames; k++)
		{
			reversi.GameBoard gb = BoardFactory.create();
			Board t = new Board(gb, 1);
			t.swap();
			//System.out.println(gb.countStones(1) + "/" + gb.countStones(2));
			
			Random rand = new Random();
			
			int color = 1;
			while(!(t.availableMoves() == 0 & t.availableMovesOpponent() == 0))
			{
				
				//getMoves Board
				long[] moves2 = t.availableMovesArray();
				
				
				//System.out.print(moves2.length + " ");
				if(moves2.length == 0)
				{
					color = otherColor(color);
					t.swap();
				}
				else
				{
					int r = rand.nextInt(moves2.length);
					
					t = t.makeMove(moves2[r]);
					color = otherColor(color);
					
				}
			}
			//System.out.println(k + "/1000");
		}
	}
	
	private static int otherColor(int color)
	{
		if(color == 1)	return 2;
		return 1;
	}
	private static void printInfos(Board b)
	{
		//System.out.println("current board: \n" + b.toString());
		//System.out.println("\n current avalable moves: \n" + b.longToString(b.availableMoves()));
		
		System.out.println(infos(b, b.availableMoves()));
	}
	private static Coordinates convert(long move)
	{
		if(move == 0L)		return null;
		int index = Long.numberOfTrailingZeros(move);
		Coordinates coord = new Coordinates(((int)(index/8) + 1), ((int)(index%8) + 1));
		return coord;
	}

	public static String infos(Board t)
	{
		// ☻ ☺
		String result = "0 1 2 3 4 5 6 7\n";
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

	public static String infos(Board input, long movelong)
	{
		// ☻ ☺
		String result = "0 1 2 3 4 5 6 7\n";
		Board t = input.clone();
		t.swap();
		//int move = Long.numberOfTrailingZeros(movelong);
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
			else if((C.fields[i] & movelong) != 0)
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
	private static String infos()
	{
		// ☻ ☺
		String result = "0 1 2 3 4 5 6 7\n";
		//int move = Long.numberOfTrailingZeros(movelong);
		for(int i = 0; i < 64; i++)
		{
			result += i + "\t";
			if(i %8 == 7)
			{
				result += "\n";
			}
			
		}
		return result;
		
	}
	
	private static int testFFotest40()
	{
		glhf.MinMax minMax = new MinMax();
		minMax.initialize();
		long[] board = {0,0};
		String s = "O--OOOOX-OOOOOOXOOXXOOOXOOXOOOXXOOOOOOXX---OOOOX----O--X--------";
		for(int i = 0; i < 64; i++)
		{
			if(s.charAt(i) == 'O')	board[0] |= C.fields[i];
			if(s.charAt(i) == 'X')	board[1] |= C.fields[i];
		}
		Board t = new Board(board, 0, 1);
		int color = 1;
		//printInfos(t);
		int result = 0;
		minMax.timeout = System.currentTimeMillis() + 100000;
		long currenttime = System.currentTimeMillis();
		try {
			result = minMax.endGameNegaMax(t, -64, 64, t.availableMoves());
		} catch (TimeoutException e) {
			
		}
		System.out.println("nodes: " + minMax.nodes + " time used: " + (System.currentTimeMillis() - currenttime) + " result: " + result + " nodes/s: " + (minMax.nodes / ((System.currentTimeMillis() - currenttime)/1000)));
		return result;
	}
	
	private static Board makeMoveXiao(Board b, long move)
	{
		long[] board = b.getBoard();
		int col = b.getColor(); int opcol = b.getOtherColor();
		long disksToFlip = flipDisks(board[col], board[opcol], move);
		long[] result = {board[0] ^ disksToFlip, board[1] ^ disksToFlip};
		result[col] ^= move;
		Board t = new Board(result, C.gen.BoardToHash(result), opcol);
		return t;
	}
	
	private static Board makeMoveXiaoWithoutHash(Board b, long move)
	{
		long[] board = b.getBoard();
		int col = b.getColor(); int opcol = b.getOtherColor();
		long disksToFlip = flipDisks(board[col], board[opcol], move);
		long[] result = {board[0] ^ disksToFlip, board[1] ^ disksToFlip};
		result[col] ^= move;
		Board t = new Board(result, 0, opcol);
		return t;
	}
	
	private static long flipDisks(long currentBoard, long opponentBoard, long move) {
	    long result = 0L;

	    long temp = move >>> 8;
	    if ((temp & opponentBoard) != 0L) {
	      do
	        temp >>>= 8;
	      while ((temp & opponentBoard) != 0L);
	      if ((temp & currentBoard) != 0L) {
	        temp <<= 8;
	        do {
	          result |= temp;
	          temp <<= 8;
	        }while (temp != move);
	      }
	    }

	    temp = move << 8;
	    if ((temp & opponentBoard) != 0L) {
	      do
	        temp <<= 8;
	      while ((temp & opponentBoard) != 0L);
	      if ((temp & currentBoard) != 0L) {
	        temp >>>= 8;
	        do {
	          result |= temp;
	          temp >>>= 8;
	        }while (temp != move);
	      }
	    }
	    opponentBoard &= 9114861777597660798L; //0x7e 7e 7e 7e 7e 7e 7e 7e

	    temp = move >>> 1;
	    if ((temp & opponentBoard) != 0L) {
	      do
	        temp >>>= 1;
	      while ((temp & opponentBoard) != 0L);
	      if ((temp & currentBoard) != 0L) {
	        temp <<= 1;
	        do {
	          result |= temp;
	          temp <<= 1;
	        }while (temp != move);
	      }
	    }

	    temp = move << 1;
	    if ((temp & opponentBoard) != 0L) {
	      do
	        temp <<= 1;
	      while ((temp & opponentBoard) != 0L);
	      if ((temp & currentBoard) != 0L) {
	        temp >>>= 1;
	        do {
	          result |= temp;
	          temp >>>= 1;
	        }while (temp != move);
	      }
	    }

	    temp = move >>> 9;
	    if ((temp & opponentBoard) != 0L) {
	      do
	        temp >>>= 9;
	      while ((temp & opponentBoard) != 0L);
	      if ((temp & currentBoard) != 0L) {
	        temp <<= 9;
	        do {
	          result |= temp;
	          temp <<= 9;
	        }while (temp != move);
	      }
	    }

	    temp = move >>> 7;
	    if ((temp & opponentBoard) != 0L) {
	      do
	        temp >>>= 7;
	      while ((temp & opponentBoard) != 0L);
	      if ((temp & currentBoard) != 0L) {
	        temp <<= 7;
	        do {
	          result |= temp;
	          temp <<= 7;
	        }while (temp != move);
	      }
	    }

	    temp = move << 9;
	    if ((temp & opponentBoard) != 0L) {
	      do
	        temp <<= 9;
	      while ((temp & opponentBoard) != 0L);
	      if ((temp & currentBoard) != 0L) {
	        temp >>>= 9;
	        do {
	          result |= temp;
	          temp >>>= 9;
	        }while (temp != move);
	      }
	    }

	    temp = move << 7;
	    if ((temp & opponentBoard) != 0L) {
	      do
	        temp <<= 7;
	      while ((temp & opponentBoard) != 0L);
	      if ((temp & currentBoard) != 0L) {
	        temp >>>= 7;
	        do {
	          result |= temp;
	          temp >>>= 7;
	        }while (temp != move);
	      }
	    }
	    return result;
	  }
	private static Board randBoard()
	{
		Random rand = new Random();
		long[] board = {rand.nextLong(), rand.nextLong()};
		board[1] |= C.fields[27] | C.fields[28] | C.fields[35]| C.fields[36]; 
		for(int j = 0; j < 64; j++)
		{
			if((board[0] & C.fields[j] & board[1]) == C.fields[j])
			{
				board[1] ^= C.fields[j];
			}
		}
		
		Board t = new Board(board, C.gen.BoardToHash(board), 0);
		return t;
	}
	
}
