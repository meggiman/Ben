package Testing;

import java.io.IOException;

import javax.swing.JFileChooser;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Templates;

import Gameboard.Bitboard;
import Players.Minmaxwithoutcloningplayer;
import reversi.Coordinates;
import reversi.GameBoard;
import reversi.ReversiPlayer;


public class test {

	public static void main(String[] args) {
		TestArena testArena = new TestArena(new TestablePlayers.PlayerA(), new TestablePlayers.PlayerC(), 200);
		TestArena.TestResult result = testArena.normalGame(Bitboard.RED,10);
		JFileChooser fileChooser = new JFileChooser();
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
