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
//		Bitboard gb = new Bitboard( 4331423886282077216L,-4584469890345205760L);
//		long coord = EndgameSearch.OutcomeSearch.nextMove(gb);
//		coord += EndgameSearch.OutcomeSearch.nextMove(gb)-gb.red;
//		System.out.println("test:");
//		long time = System.nanoTime();
//			coord += EndgameSearch.OutcomeSearch.nextMove(gb)-gb.green;
//		System.out.println("Zeit in ms: "+(System.nanoTime()-time)+"\n"+coord);
		TestArena testArena = new TestArena(new TestablePlayers.PlayerA(), new TestablePlayers.PlayerB(), 1000);
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
