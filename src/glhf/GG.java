package glhf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.TimeoutException;


import reversi.BoardFactory;
import reversi.Coordinates;
import reversi.GameBoard;
import reversi.ReversiPlayer;

import glhf.C;

public class GG implements ReversiPlayer{

	public static long timeout, time;
	int color;
	int hashTableSize = (int) Math.pow(2, 20);
	boolean endGameSolved = false;
	boolean renew = false;
	MinMax minMax;
	
	public boolean verbose = false;
	public String output;
	
	public void initialize(int color, long timeout) {
		// TODO Auto-generated method stub
		System.out.println("Good luck, have fun!");
		//this.time = 950;
		if(color == GameBoard.RED)	
		{
			this.color = C.RED;
			System.out.println("GGs color is: RED");
		}
		else 						
		{
			this.color = C.GREEN;
			System.out.println("GGs color is: RED");
		}
		
		//initialize precomputed fields + hashTable
		C.initialize(hashTableSize);
		
		//initilaize minMax
		minMax = new MinMax();
		System.out.println("HashMap initialized! size: " + C.hashMap.size() + "MB " + Math.pow(2, 20) + " entries");
				
		
		//play through a whole game to get the Garbage Collector to work properly :)
		System.out.println("Playing a whole game against myself, trying not to get fu**** by javas Garbage Collector! PLS no timeout");
//		ReversiPlayer player1 = new Player();
//		ReversiPlayer player2 = new Player();
		this.time = 100;
		PrintStream old = new PrintStream(System.out);
		FileOutputStream fo = null;
		try {
			fo = new FileOutputStream("stdout.log");
			System.setOut(new PrintStream(fo));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		reversi.GameBoard gb = BoardFactory.create();
		
		
		for (int i = 0; i < 1; i++) {
			gb = BoardFactory.create();
			while (!gb.isFull()) {
				long start = System.currentTimeMillis();
				Coordinates move = nextMove(gb);
				if (move != null)
					old.println("RED makes move: \t" + move.toString() + " in "
							+ (System.currentTimeMillis() - start) + "ms");
				if(move == null)	break;
				gb.checkMove(GameBoard.RED, move);
				gb.makeMove(GameBoard.RED, move);

				this.color ^= 0x1;
				start = System.currentTimeMillis();
				move = nextMove(gb);
				if (move != null)
					old.println("GREEN makes move: \t" + move.toString()
							+ " in " + (System.currentTimeMillis() - start)
							+ "ms");
				if(move == null)	break;
				gb.checkMove(GameBoard.GREEN, move);
				gb.makeMove(GameBoard.GREEN, move);

			}
		}
		if(color == GameBoard.RED)	
		{
			this.color = C.RED;
		}
		else 						
		{
			this.color = C.GREEN;
		}
//		renew = true;
		this.time = timeout-25;
		System.setOut(old);
		try {
			fo.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File f = new File("stdout.log");
		f.delete();
		
		System.out.println(gb.countStones(GameBoard.RED) + " - " + gb.countStones(GameBoard.GREEN));
	}

	@Override
	public Coordinates nextMove(GameBoard board) {
		timeout = System.currentTimeMillis() + time;
		MinMax.timeout = timeout;
		minMax.nodes = 0;
		minMax.cuts = 0;
		minMax.hashHits = 0;
		
		Board b = new Board(board, color);

		long[] moves = b.availableMovesArray();
		if(moves.length == 0)
		{
			return null;
		}
		//if(b.availableMovesArray().length == 1)		return convert(b.availableMovesArray()[0]);
		
		//if(b.remainingStones() < 25)
		//{
		//	Score.s[0] = 10;
		//	Score.s[1] = 1;
		//	Score.s[2] = -1;
		//	Score.s[3] = 1;
		//	Score.s[4] = -1;
		//	Score.mob = 1;
		//	Score.stable = 3;
		//}
		
		//System.out.println(b.getColor() + "\n" + b.toString() + "\n" + OpeningBook.printBook());
		
		//Opening Book
//		if(OpeningBook.searchMove(b) != 0)
//		{
//			System.out.println("Opening Book! Credits to WZebra");
//			return convert(OpeningBook.searchMove(b));
//		}
				
//		if(renew) C.hashMap.renew(b.remainingStones());
		
		int i= 5;
		
		//endGameSolved = false;
		
		long bestMove = 0;
		long bestMoveSaved = 0;
		int result, resultSaved;
		result = 0; resultSaved = 0; bestMove = 0L;
		
		//HashTable
		HashValue value = null;
		
		value = C.hashMap.searchHash(b.getHash());
		
		if(value != null)
		{
			long moveHashTable = 1L << value.moveInt;
//			System.out.println("came to here 1!");
			if(value.hash == b.getHash())
			{
				//bestMove from previous run
				System.out.println("Move from last run: " + coordinatesConvert(moveHashTable) + " depth: " + (value.depth) + " value: " + value.score);
//				System.out.println("Move from last run: " + Long.numberOfTrailingZeros(moveHashTable) + " depth: " + (value.depth) + " value: " + value.score);
				i = value.depth;
				bestMove = C.fields[value.moveInt];
				resultSaved = value.score;				
			}
		}
		
		
		
		for(; i <= b.remainingStones()+11; i++)
		{
			try
			{
				if(i+4 >= b.remainingStones())
				{
					result = minMax.endGameNegaMax(b, -64, 64, b.availableMoves());
					System.out.println("Endgame solved : " + result);
					bestMove = minMax.getBestMove();
					resultSaved = result;
					i += 15;
				}
				else if(i + 8 >= b.remainingStones())
				{
					result = minMax.endGameNegaMax(b, -1, 1, b.availableMoves());
					System.out.println("Endgame Win/Loss solved! time: " + (System.currentTimeMillis()-timeout + time));
					i += 8;
				}
				else
				{
					result = minMax.negaScout(b, i, -100000, 100000);
					
					System.out.println("depth: " + i + " " + coordinatesConvert(minMax.getBestMove()) + " score: " + result + " time: " + (System.currentTimeMillis()-timeout + time));
//					System.out.println("depth: " + i + " " + Long.numberOfTrailingZeros(minMax.getBestMove()) + " score: " + result);
				}
			}
			catch (TimeoutException e)
			{
				break;
			}
			bestMove = minMax.getBestMove();
			if(bestMove != 0L)	 bestMoveSaved = bestMove;
			resultSaved = result;
		}
		if(bestMove == 0L)	bestMove = bestMoveSaved;
		System.out.println("Depth: " + (i-1) 
				+ " Value: " + resultSaved 
				+ " time used: " + (System.currentTimeMillis()-timeout + time) 
				+ " nodes: " + ((float)minMax.nodes / 1000) + "kN" 
				+ " N/S: " + (minMax.nodes / (System.currentTimeMillis()-timeout + time + 1))
				+ " collisions: " + C.hashMap.collisions 
				+ " alpha-beta cuts: " + minMax.cuts
				+ " hashhits: " + minMax.hashHits);
		
		//Expectation of the playing line
		String line = "Expected line played: ";
		long moveLine = 0L;
		
		try
		{
			Board t = b.makeMove(bestMove);
			moves = t.availableMovesArray();
			//HashTable
			value = C.hashMap.searchHash(t.getHash());
			if(value != null)
			{
				long moveHashTable = 1L << (value.moveInt);
	//					System.out.println("came to here 1!");
				if(value.hash == t.getHash())
				{
					moveLine = moveHashTable;
					//bestMove from previous run
//					System.out.println("Expected Move: " + convert(moveHashTable).toString() + " depth: " + (value.depth) + " value: " + value.score);
					line += "\n\t" + coordinatesConvert(moveHashTable) + "\t depth: " + value.depth + "\t score: " + value.score;
				}
			}
			t = t.makeMove(moveLine);
			moves = t.availableMovesArray();
			//HashTable
			value = C.hashMap.searchHash(t.getHash());
			if(value != null)
			{
				long moveHashTable = 1L << (value.moveInt);
	//					System.out.println("came to here 1!");
				if(value.hash == t.getHash())
				{
					moveLine = moveHashTable;
					//bestMove from previous run
					//System.out.println("Expected Move: " + convert(moveHashTable).toString() + " depth: " + (value.depth) + " value: " + value.score);
					line += "\n\t" + coordinatesConvert(moveHashTable) + "\t depth: " + value.depth + "\t score: " + value.score;
				}
			}
			t = t.makeMove(moveLine);
			moves = t.availableMovesArray();
			//HashTable
			value = C.hashMap.searchHash(t.getHash());
			if(value != null)
			{
				long moveHashTable = 1L << (value.moveInt);
	//					System.out.println("came to here 1!");
				if(value.hash == t.getHash())
				{
					moveLine = moveHashTable;
					//bestMove from previous run
					//System.out.println("Expected Move: " + convert(moveHashTable).toString() + " depth: " + (value.depth) + " value: " + value.score);
					line += "\n\t" + coordinatesConvert(moveHashTable) + "\t depth: " + value.depth + "\t score: " + value.score;
				}
			}
			System.out.println(line);
		}
		catch (Exception e){}
		
		
		return convert(bestMove);
	}
	private Coordinates convert(long move)
	{
		if(move == 0L)		return null;
		int index = Long.numberOfTrailingZeros(move);
		Coordinates coord = new Coordinates(((int)(index/8) + 1), ((int)(index%8) + 1));
		return coord;
	}

	private String coordinatesConvert(long move)
	{
		return coordinatesConvert(convert(move));
	}
	
	private String coordinatesConvert(Coordinates move)
	{
		return C.letters[move.getCol()-1] + move.getRow();
	}
	public void initialize1(int color, long timeout) {
		// TODO Auto-generated method stub
		System.out.println("Good luck, have fun!");
		//this.time = 950;
		if(color == GameBoard.RED)	
		{
			this.color = C.RED;
			System.out.println("GGs color is: RED");
		}
		else 						
		{
			this.color = C.GREEN;
			System.out.println("GGs color is: RED");
		}
		
		//initialize precomputed fields + hashTable
		C.initialize(hashTableSize);
		
		//initilaize minMax
		minMax = new MinMax();
		System.out.println("HashMap initialized! size: " + C.hashMap.size() + "MB");
				
		
		this.time = timeout-25;
		
	}
}
