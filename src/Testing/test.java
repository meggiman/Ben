package Testing;

import java.io.IOException;

import javax.swing.JFileChooser;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import Gameboard.Bitboard;

public class test{

    public static void main(String[] args){
        TestArena testArena = new TestArena(new TestablePlayers.PlayerNoah(), new TestablePlayers.PlayerA(), 1000);
        TestArena.TestResult result = testArena.normalGame(Bitboard.RED, 10);
        JFileChooser fileChooser = new JFileChooser();
        int returned = fileChooser.showSaveDialog(null);
        if(returned == JFileChooser.APPROVE_OPTION){
            try{
                result.writetoxmlfile(fileChooser.getSelectedFile());
            }catch(XMLStreamException e){
                System.out.println("XMLStreamException");
            }catch(FactoryConfigurationError e){
                System.out.println("FactoryConfigurationError");
            }catch(IOException e){
                System.out.println("IOException");
            }
        }

    }

    public static long countnodes(Bitboard gb, int depth, boolean player){
        if(depth == 0){
            return 1;
        }
        else{
            long count = 1L;
            for (Bitboard gbBitboard : gb.getBitboards(player, Bitboard.serializeBitboard(gb.getPossibleMoves(player)))){
                count += countnodes(gbBitboard, depth - 1, !player);
            }
            return count;
        }
    }

}
