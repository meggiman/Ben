package glhf;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Random;

import reversi.BoardFactory;
import reversi.Coordinates;
import reversi.GameBoard;
import reversi.ReversiPlayer;

public class TuneScore {
	public static void main(String[] args)
	{
		ReversiPlayer player1 = new GG();
		ReversiPlayer player2 = new GG();
		
		player1.initialize(GameBoard.RED, 100);
		player2.initialize(GameBoard.GREEN, 100);
		
		
		testset.checkMoves();
		
		PrintStream old = new PrintStream(System.out);
		try {
			System.setOut(new PrintStream(new FileOutputStream("stdout.log")));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(int i = 0; i < 10; i++)
		{
			int[] s = {50+10*i, -2, -4, 0  		, -1};
			int[] normal = {90, -2, -4, 0, -1};
			GameBoard gb = BoardFactory.create();
			//C.hashMap.renew(-1);
			GG.time = 10 + i * 500;
			
			
			while(!gb.isFull())
			{
				Score.s = s;
				long start = System.currentTimeMillis();
				Coordinates move = player1.nextMove(gb);
				if(move != null)	old.println("RED makes move: \t" + move.toString() + " in " + (System.currentTimeMillis() - start) + "ms");
				gb.checkMove(GameBoard.RED, move);
				gb.makeMove(GameBoard.RED, move);
				
				Score.s = normal;
				start = System.currentTimeMillis();
				move = player2.nextMove(gb);
				if(move != null) 	old.println("GREEN makes move: \t" + move.toString() + " in " + (System.currentTimeMillis() - start) + "ms");
				gb.checkMove(GameBoard.GREEN, move);
				gb.makeMove(GameBoard.GREEN, move);
				
			}
			//System.setOut(old);
			old.println(gb.countStones(GameBoard.RED) + " - " + gb.countStones(GameBoard.GREEN));
		}
		
		
	}
}
