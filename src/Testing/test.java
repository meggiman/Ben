package Testing;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Templates;

import Gameboard.Bitboard;
import OtherPlayers.Xiaolong;
import reversi.Coordinates;
import reversi.GameBoard;
import reversi.ReversiPlayer;
import searching.EndgameSearch;


public class test {

	public static void main(String[] args) {
		Bitboard gb = new Bitboard(8655921825290661984L, 296389804679891980L);
		long ps2 = Bitboard.possibleMovesRed(8962216103846544383L, 8954353875483648L);
		long ps = Bitboard.getflippedDiskRed(103079215104L, 402653184l, 1125899906842624L);
		long test = 0;
//		long time2 = System.nanoTime();
//			test = countnodes(103079215104L, 402653184L, 12);
//		System.out.println(System.nanoTime()-time2);
		System.out.println(test);
		long coord = EndgameSearch.OutcomeSearch.nextMove(gb);
		coord += EndgameSearch.OutcomeSearch.nextMove(gb)-gb.red;
		System.out.println("test:");
		long time = System.nanoTime();
			 coord = EndgameSearch.OutcomeSearch.nextMove(gb)-gb.green;
		System.out.println("Zeit in ms: "+(System.nanoTime()-time)+"\n"+coord);
		TestArena testArena = new TestArena(new TestablePlayers.PlayerA(), new TestablePlayers.PlayerB(), 200);
		TestArena.TestResult result = testArena.randomTimeGame(10, 50);
		JFileChooser fileChooser = new JFileChooser();
		
		fileChooser.setSelectedFile(new File("C:\\Users\\Manuel\\workspace\\Reversi\\src\\Testing\\Tests\\testwithTT.xml"));
		int returned = fileChooser.showSaveDialog(null);
		if (returned == JFileChooser.APPROVE_OPTION) {
			try {
				result.writetoxmlfile(fileChooser.getSelectedFile());
			} catch (XMLStreamException e) {
				System.out.println("XMLStreamException");
			} catch (FactoryConfigurationError e) {
				System.out.println("FactoryConfigurationError");
			} catch (IOException e) {
				System.out.println("IOException");
			}
		}
		
	}
	
	private static Bitboard gb = new Bitboard(103079215104L,402653184L);
	
	public static long countnodes(long red, long green, int depth){
		if (depth == 0) {
			return 1;
		}
		else {
			long count = 1L;
			long flippeddisks = 0;
			long xiaolongdisk = 0;
//			gb.red = red;
//			gb.green = green;
//			long possiblemoves = gb.possiblemoves(true);
//			long possiblemoves2 = Bitboard.possibleMovesRed(red, green);
//			if (possiblemoves!= possiblemoves2) {
//				System.out.println("Fehler");
//				possiblemoves = gb.possiblemoves(true);
//				possiblemoves2 = Bitboard.possibleMovesRed(red, green);
//			}
//			for (long coord : Bitboard.bitboardserialize(gb.possiblemoves(true))) {
			for (long coord : Bitboard.bitboardserialize(Bitboard.possibleMovesRed(red, green))) {
				flippeddisks = Bitboard.getflippedDiskRed(red, green, coord);
				count += 1+countnodes(green^flippeddisks, red^flippeddisks^coord, depth-1);
			}
			return count;
		}
	}
	
}
