package Testing;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Templates;

import Gameboard.Bitboard;
import reversi.Coordinates;
import reversi.GameBoard;
import reversi.ReversiPlayer;
import searching.EndgameSearch;


public class test {

	public static void main(String[] args) {
		Bitboard gb = new Bitboard(2201394854367868928L,4625217755795637628L);
		long coord = EndgameSearch.OutcomeSearch.nextMove(gb); //Coord = 281474976710656 gb before green to move g 4625215556772382076 r 2201678528367835136
		gb.makeMove(true, 281474976710656L);
		System.out.println(EndgameSearch.OutcomeSearch.outcome);
		Bitboard[] board = gb.getbitboards(false, Bitboard.bitboardserialize(gb.possiblemoves(false)));
		gb.green = 4643335853073450364L;
		gb.red = 2201572630576248832L;
		EndgameSearch.OutcomeSearch.nextMove(gb);
		System.out.println(EndgameSearch.OutcomeSearch.outcome);
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
	public static long countnodes(Bitboard gb, int depth, boolean player){
		if (depth == 0) {
			return 1;
		}
		else {
			long count = 1L;
			for (Bitboard gbBitboard : gb.getbitboards(player, Bitboard.bitboardserialize(gb.possiblemoves(player)))) {
				count += countnodes(gbBitboard, depth-1, !player);
			}
			return count;
		}
	}
	
}
