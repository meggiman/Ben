package Testing;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import searching.EndgameSearch;
import Gameboard.Bitboard;

public class TestNoah{

    public static void main(String[] args){
        Bitboard gb = new Bitboard(8655921825290661984L, 296389804679891980L);
        long ps2 = Bitboard.possibleMovesRed(8962216103846544383L, 8954353875483648L);
        long ps = Bitboard.getflippedDiskRed(103079215104L, 402653184l, 1125899906842624L);
        long test = 0;
        // long time2 = System.nanoTime();
        // test = countnodes(103079215104L, 402653184L, 12);
        // System.out.println(System.nanoTime()-time2);
        System.out.println(test);
        long coords = EndgameSearch.OutcomeSearch.nextMove(gb);
        coords += EndgameSearch.OutcomeSearch.nextMove(gb) - gb.red;
        System.out.println("test:");
        long time = System.nanoTime();
        coords = EndgameSearch.OutcomeSearch.nextMove(gb) - gb.green;
        System.out.println("Zeit in ms: " + (System.nanoTime() - time) + "\n"
                + coords);
        TestArena testArena = new TestArena(new TestablePlayers.PlayerA(), new TestablePlayers.PlayerB(), 5000);
        TestArena.TestResult result = testArena.randomTimeGame(50, 0);
        JFileChooser fileChooser = new JFileChooser();

        fileChooser.setSelectedFile(new File("C:\\Users\\Manuel\\workspace\\Reversi\\src\\Testing\\Tests\\StrategicEvalNoah.xml"));
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

    private static Bitboard gb = new Bitboard(103079215104L, 402653184L);

    public static long countNodes(long red, long green, int depth){
        if(depth == 0){
            return 1;
        }
        else{
            long count = 1L;
            long flippedDisks = 0;
            long xiaolongDisk = 0;
            // gb.red = red;
            // gb.green = green;
            // long possiblemoves = gb.possiblemoves(true);
            // long possiblemoves2 = Bitboard.possibleMovesRed(red, green);
            // if (possiblemoves!= possiblemoves2) {
            // System.out.println("Fehler");
            // possiblemoves = gb.possiblemoves(true);
            // possiblemoves2 = Bitboard.possibleMovesRed(red, green);
            // }
            // for (long coord :
            // Bitboard.bitboardserialize(gb.possiblemoves(true))) {
            for (long coords : Bitboard.serializeBitboard(Bitboard.possibleMovesRed(red, green))){
                flippedDisks = Bitboard.getflippedDiskRed(red, green, coords);
                count += 1 + countNodes(green ^ flippedDisks, red
                        ^ flippedDisks ^ coords, depth - 1);
            }
            return count;
        }
    }

}
