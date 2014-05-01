package Testing;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javanet.staxutils.IndentingXMLStreamWriter;

import javax.naming.TimeLimitExceededException;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import reversi.Coordinates;
import reversi.GameBoard;
import reversi.ReversiPlayer;
import Gameboard.Bitboard;
import Testing.TestArena.TestResult.GameResult;

/**
 * Diese Klasse stellt Methoden bereit, um verschieden {@link ReversiPlayer}
 * automatisiert gegeneinander antreten und lernen zu lassen.
 * Die Klasse benutzt nur einen Thread. Die Einhaltung der Timelimits wird erst
 * nach R�ckgabe eines Zuges durch die {@code ReversiPlayer} gepr�ft.
 * Die Testprogramme k�nnen also durch zu lange Berrechnungen stark ausgebremst
 * oder zum Absturz gebracht werden.
 * 
 */
public class TestArena{
    private ITestablePlayer player2;
    private ITestablePlayer player1;
    private int             player1colour;
    private int             player2colour;
    private int             timelimitplayer1;
    private int             timelimitplayer2;
    public static final int DRAW = 0, PLAYER1 = 1, PLAYER2 = 2;

    /**
     * 
     * @param player1
     *            Der {@code ReversiPlayer}, welcher abh�ngig vom gew�hltem
     *            Testmodell nur die Farbe gr�n spielt.
     * @param player2
     *            Der {@code ReversiPlayer}, welcher abh�ngig vom gew�hltem
     *            Testmodell nur die Farbe rot spielt.
     * @param timelimit
     *            Das Zeitlimit, welches die beiden Spieler haben.
     */
    public TestArena(ITestablePlayer player1, ITestablePlayer player2, int timelimit){
        this.player2 = player2;
        this.player1 = player1;
        this.timelimitplayer1 = timelimit;
        this.timelimitplayer2 = timelimit;
    }

    /**
     * 
     * @param player1
     *            Der {@code ReversiPlayer}, welcher abh�ngig vom gew�hltem
     *            Testmodell nur die Farbe gr�n spielt.
     * @param player2
     *            Der {@code ReversiPlayer}, welcher abh�ngig vom gew�hltem
     *            Testmodell nur die Farbe rot spielt.
     * @param timelimitplayer1
     *            Das Zeitlimit, welches der Spieler Rot hat.
     * @param timelimitplayer2
     *            Das Zeitlimit, welches der Spieler Gr�n hat.
     */
    public TestArena(ITestablePlayer player1, ITestablePlayer player2, int timelimitplayer1, int timelimitplayer2){
        this.player2 = player2;
        this.player1 = player1;
        this.timelimitplayer1 = timelimitplayer1;
        this.timelimitplayer2 = timelimitplayer2;
    }

    public TestResult normalGame(int player1colour, int nrofgames){
        TestResult result = new TestResult();
        this.player1colour = player1colour;
        this.player2colour = (player1colour == Bitboard.RED) ? Bitboard.GREEN
                : Bitboard.RED;
        for (int i = 0; i < nrofgames; i++){
            Bitboard gb = new Bitboard(0x18000000L, 0x1800000000L);
            player1.initialize(this.player1colour, timelimitplayer1);
            player2.initialize(this.player2colour, timelimitplayer2);
            TestResult.GameResult game = result.new GameResult();
            if(this.player1colour == Bitboard.GREEN){
                MoveResult move = null;
                try{
                    move = nextmove(gb, player2);
                }catch(TimeLimitExceededException e){
                    game.disqualified2 = true;
                    game.resultcode = GameResult.TIMEEXCEEDED;
                }catch(IllegalMoveException e){
                    game.disqualified2 = true;
                    game.resultcode = GameResult.ILLEGALMOVE;
                }catch(PlayerException e){
                    game.disqualified2 = true;
                    game.resultcode = GameResult.EXCEPTION;
                    game.exception = e;
                }
                if(move != null){
                    game.addmove(move);
                }
            }
            do{
                MoveResult move = null;
                try{
                    move = nextmove(gb, player1);
                }catch(TimeLimitExceededException e){
                    game.disqualified1 = true;
                    game.resultcode = GameResult.TIMEEXCEEDED;
                    break;
                }catch(IllegalMoveException e){
                    game.disqualified1 = true;
                    game.resultcode = GameResult.ILLEGALMOVE;
                    break;
                }catch(PlayerException e){
                    game.disqualified1 = true;
                    game.resultcode = GameResult.EXCEPTION;
                    game.exception = e;
                    break;
                }
                if(move != null){
                    game.addmove(move);
                }

                try{
                    move = nextmove(gb, player2);
                }catch(TimeLimitExceededException e){
                    game.disqualified2 = true;
                    game.resultcode = GameResult.TIMEEXCEEDED;
                    break;
                }catch(IllegalMoveException e){
                    game.disqualified2 = true;
                    game.resultcode = GameResult.ILLEGALMOVE;
                    break;
                }catch(PlayerException e){
                    game.disqualified2 = true;
                    game.resultcode = GameResult.EXCEPTION;
                    game.exception = e;
                    break;
                }
                if(move != null){
                    game.addmove(move);
                }
            }while(!gb.isFinished());
            game.finishgame();
            result.addGame(game);
            System.out.println(i);
            int tmp = this.player1colour;
            this.player1colour = this.player2colour;
            this.player2colour = tmp;
        }
        result.finishtest();
        return result;
    }

    private MoveResult nextmove(GameBoard gb, ITestablePlayer player) throws TimeLimitExceededException, IllegalMoveException, PlayerException{
        int playercode = (player == player1) ? PLAYER1 : PLAYER2;
        Coordinates coord = null;
        long time = System.currentTimeMillis();
        try{
            coord = player.nextMove(gb);
        }catch(Exception e){
            e.printStackTrace();
            throw new PlayerException(e, playercode, gb);
        }
        if(System.currentTimeMillis() - time > ((playercode == PLAYER1) ? timelimitplayer1
                : timelimitplayer2)){
            throw new TimeLimitExceededException("Die Zeit ist abgelaufen.");
        }
        if(coord == null){
            return null;
        }
        int playercolour = (player == player1) ? player1colour : player2colour;
        if(!gb.checkMove(playercolour, coord)){
            throw new IllegalMoveException();
        }

        MoveResult move = new MoveResult();
        move.player = playercode;
        move.depth = player.getDepthOfLatestSearch();
        gb.makeMove(playercolour, coord);
        move.gameboard = gb;
        move.move = coord;
        move.movenr = player.getMoveNrOfLatestSearch();
        move.NrofevaluatedNodes = player.getEvaluatedNodes();
        move.NrofsearchedNodes = player.getNodesCount();
        move.NrofTTHits = player.getNrOfTTHits();
        move.value = player.getValueOfLatestSearch();
        return move;
    }

    public static class PlayerException extends Exception{
        public String    exception;
        public int       player;
        public GameBoard gb;

        public PlayerException(Exception exception, int player, GameBoard gb){
            super(exception);
            this.exception = exception.toString();
            this.player = player;
            this.gb = gb;
        }
    }

    private static class IllegalMoveException extends Exception{
        public IllegalMoveException(){
            super("Der Zug ist ung�ltig.");
        }
    }

    public class TestResult{
        public String                player2name                = player2.getName();
        public String                player1name                = player1.getName();
        public int                   limitplayer1               = timelimitplayer1;
        public int                   limitplayer2               = timelimitplayer2;
        public int                   nrofgames;
        private boolean              isfinished;
        public ArrayList<GameResult> gameResults                = new ArrayList<>();
        // player1
        public long                  totalNrofsearchedNodes1    = 0;
        public long                  averageNrofsearchedNodes1  = 0;
        public long                  totalNrofevaluatedNodes1   = 0;
        public long                  averageNrofevaluatedNodes1 = 0;
        public long                  totalNrofTTHits1           = 0;
        public long                  averageNrofTTHits1         = 0;
        public float                 averagedepth1              = 0;
        private int                  depthcount1                = 0;
        private ArrayList<Integer>   differencevalue1           = new ArrayList<>();
        public float                 averagedifference1;
        public double                standarddeviation1;
        private int                  valuechangedelta1;
        public float[]               moveorderingpercentage1;
        private int                  NrofmadeMoves1             = 0;
        public int                   averageNrofmadeMoves1      = 0;
        public int                   wongames1                  = 0;

        // player2
        public long                  totalNrofsearchedNodes2    = 0;
        public long                  averageNrofsearchedNodes2  = 0;
        public long                  totalNrofevaluatedNodes2   = 0;
        public long                  averageNrofevaluatedNodes2 = 0;
        public long                  totalNrofTTHits2           = 0;
        public long                  averageNrofTTHits2         = 0;
        public float                 averagedepth2              = 0;
        private int                  depthcount2                = 0;
        private ArrayList<Integer>   differencevalue2           = new ArrayList<>();
        public float                 averagedifference2;
        public double                standarddeviation2;
        private int                  valuechangedelta2;
        public float[]               moveorderingpercentage2;
        private int                  NrofmadeMoves2             = 0;
        public int                   averageNrofmadeMoves2      = 0;
        public int                   wongames2                  = 0;

        public void addGame(GameResult game){
            if(!game.isfinished){
                throw new IllegalArgumentException("The game wasn't finished.");
            }
            totalNrofsearchedNodes1 += game.totalNrofsearchedNodes1;
            totalNrofsearchedNodes2 += game.totalNrofsearchedNodes2;
            totalNrofevaluatedNodes1 += game.totalNrofevaluatedNodes1;
            totalNrofevaluatedNodes2 += game.totalNrofevaluatedNodes2;
            totalNrofTTHits1 += game.totalNrofTTHits1;
            totalNrofTTHits2 += game.totalNrofTTHits2;
            NrofmadeMoves1 += game.NrofmadeMoves1;
            NrofmadeMoves2 += game.NrofmadeMoves2;
            depthcount1 += game.depthcount1;
            depthcount2 += game.depthcount2;
            differencevalue1.addAll(game.differencevalue1);
            differencevalue2.addAll(game.differencevalue2);
            if(game.winner == PLAYER1){
                wongames1++;
            }
            else if(game.winner == PLAYER2){
                wongames2++;
            }
            nrofgames++;
            gameResults.add(game);
        }

        public void finishtest(){
            averageNrofsearchedNodes1 = totalNrofsearchedNodes1
                    / NrofmadeMoves1;
            averageNrofsearchedNodes2 = totalNrofsearchedNodes2
                    / NrofmadeMoves2;
            averageNrofevaluatedNodes1 = totalNrofevaluatedNodes1
                    / NrofmadeMoves1;
            averageNrofevaluatedNodes2 = totalNrofevaluatedNodes2
                    / NrofmadeMoves2;
            averageNrofTTHits1 = totalNrofTTHits1 / NrofmadeMoves1;
            averageNrofTTHits2 = totalNrofTTHits2 / NrofmadeMoves2;
            averageNrofmadeMoves1 = NrofmadeMoves1 / nrofgames;
            averageNrofmadeMoves2 = NrofmadeMoves2 / nrofgames;
            averagedepth1 = depthcount1 / (float) NrofmadeMoves1;
            averagedepth2 = depthcount2 / (float) NrofmadeMoves2;
            int tmp = 0;
            for (Integer difference : differencevalue1){
                tmp += difference;
            }
            averagedifference1 = tmp / (float) NrofmadeMoves1;
            tmp = 0;
            for (Integer difference : differencevalue1){
                tmp += Math.pow(averagedifference1 - difference, 2);
            }
            standarddeviation1 = Math.sqrt(tmp / NrofmadeMoves1);
            tmp = 0;
            for (Integer difference : differencevalue2){
                tmp += difference;
            }
            averagedifference2 = tmp / (float) NrofmadeMoves2;
            tmp = 0;
            for (Integer difference : differencevalue2){
                tmp += Math.pow(averagedifference2 - difference, 2);
            }
            standarddeviation2 = Math.sqrt(tmp / NrofmadeMoves2);
            ArrayList<Integer> moveOrderingcount1 = new ArrayList<Integer>();
            ArrayList<Integer> moveOrderingcount2 = new ArrayList<Integer>();
            for (GameResult game : gameResults){
                if(moveOrderingcount1.size() < game.moveorderingcount1.size()){
                    for (int i = moveOrderingcount1.size(); i < game.moveorderingcount1.size(); i++){
                        moveOrderingcount1.add(0);
                    }
                }
                for (int i = 0; i < game.moveorderingcount1.size(); i++){
                    moveOrderingcount1.set(i, moveOrderingcount1.get(i)
                            + game.moveorderingcount1.get(i));
                }
            }
            for (GameResult game : gameResults){
                if(moveOrderingcount2.size() < game.moveorderingcount2.size()){
                    for (int i = moveOrderingcount2.size(); i < game.moveorderingcount2.size(); i++){
                        moveOrderingcount2.add(0);
                    }
                }
                for (int i = 0; i < game.moveorderingcount2.size(); i++){
                    moveOrderingcount2.set(i, moveOrderingcount2.get(i)
                            + game.moveorderingcount2.get(i));
                }
            }
            moveorderingpercentage1 = new float[moveOrderingcount1.size()];
            for (int i = 0; i < moveorderingpercentage1.length; i++){
                moveorderingpercentage1[i] = moveOrderingcount1.get(i)
                        / (float) NrofmadeMoves1;
            }
            moveorderingpercentage2 = new float[moveOrderingcount2.size()];
            for (int i = 0; i < moveorderingpercentage2.length; i++){
                moveorderingpercentage2[i] = moveOrderingcount2.get(i)
                        / (float) NrofmadeMoves2;
            }
        }

        public void writetoxmlfile(File file) throws IOException, XMLStreamException, FactoryConfigurationError{
            FileOutputStream fos = new FileOutputStream(file);
            XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(fos, "UTF-8");
            writer = new IndentingXMLStreamWriter(writer);
            try{
                new xmlwriter().writeResult(writer);
                writer.flush();
                writer.close();
            }catch(XMLStreamException e){
                System.out.println(e.toString());
            }
            fos.close();

        }

        private class xmlwriter{
            private void writeResult(XMLStreamWriter writer) throws XMLStreamException{
                writer.writeStartDocument();
                writer.writeStartElement("Result");
                writer.writeDefaultNamespace("http://n.ethz.ch/student/meggiman");
                writer.writeNamespace("xsi", "http://www.w3.org/2000/10/XMLSchema-instance");
                writer.writeAttribute("http://www.w3.org/2000/10/XMLSchema-instance", "SchemaLocation", "http://n.ethz.ch/student/meggiman TestResultsSchema.xsd");
                writer.setPrefix("tns", "http://n.ethz.ch/student/meggiman");
                writePlayerResultattributes(writer, true);
                writePlayerResultattributes(writer, false);
                writer.writeStartElement("nrofgames");
                writer.writeCharacters(Integer.toString(nrofgames));
                writer.writeEndElement();
                writeGames(writer, gameResults);
                writer.writeEndElement();
                writer.writeEndDocument();
            }

            private void writeGames(XMLStreamWriter writer, ArrayList<GameResult> games) throws XMLStreamException{
                for (int i = 0; i < games.size(); i++){
                    GameResult gameResult = games.get(i);
                    writer.writeStartElement("Game");
                    writer.writeAttribute("GameNr", Integer.toString(i));
                    writer.writeStartElement("finalBoard");
                    writeGameboard(writer, gameResult.finalBoard);
                    writer.writeEndElement();

                    writePlayerGameresultattributes(writer, gameResult);

                    writemoves(writer, gameResult.moves);

                    writer.writeStartElement("resultcode");
                    switch(gameResult.resultcode){
                        case GameResult.REGULARRESULT:
                            writer.writeCharacters("REGULARRESULT");
                            break;
                        case GameResult.ILLEGALMOVE:
                            writer.writeCharacters("ILLEGALMOVE");
                            break;
                        case GameResult.TIMEEXCEEDED:
                            writer.writeCharacters("TIMEEXCEEDED");
                            break;
                        case GameResult.EXCEPTION:
                            writer.writeCharacters("EXCEPTION");
                            break;
                        default:
                            break;
                    }
                    writer.writeEndElement();

                    writer.writeStartElement("winner");
                    switch(gameResult.winner){
                        case PLAYER1:
                            writer.writeCharacters("Player1");
                            break;
                        case PLAYER2:
                            writer.writeCharacters("Player2");
                            break;
                        case DRAW:
                            writer.writeCharacters("draw");
                            break;
                        default:
                            break;
                    }
                    writer.writeEndElement();
                    writer.writeEndElement();
                }
            }

            private void writemoves(XMLStreamWriter writer, ArrayList<MoveResult> moves) throws XMLStreamException{
                writer.writeStartElement("moves");
                for (int i = 0; i < moves.size(); i++){
                    MoveResult moveResult = moves.get(i);
                    writer.writeStartElement("move");
                    writer.writeAttribute("moveNr", Integer.toString(i));
                    writer.writeAttribute("playernr", (moveResult.player == PLAYER1) ? "Player1"
                            : "Player2");

                    writer.writeStartElement("move");
                    writer.writeStartElement("Row");
                    writer.writeCharacters(Integer.toString(moveResult.move.getRow()));
                    writer.writeEndElement();
                    writer.writeStartElement("Column");
                    writer.writeCharacters(Integer.toString(moveResult.move.getCol()));
                    writer.writeEndElement();
                    writer.writeEndElement();

                    writer.writeStartElement("gameboard");
                    writeGameboard(writer, moveResult.gameboard);
                    writer.writeEndElement();

                    writer.writeStartElement("NrofsearchedNodes");
                    writer.writeCharacters(Long.toString(moveResult.NrofsearchedNodes));
                    writer.writeEndElement();

                    writer.writeStartElement("NrofevaluatedNodes");
                    writer.writeCharacters(Long.toString(moveResult.NrofevaluatedNodes));
                    writer.writeEndElement();

                    writer.writeStartElement("NrofTTHits");
                    writer.writeCharacters(Long.toString(moveResult.NrofTTHits));
                    writer.writeEndElement();

                    writer.writeStartElement("depth");
                    writer.writeCharacters(Long.toString(moveResult.depth));
                    writer.writeEndElement();

                    writer.writeStartElement("value");
                    writer.writeCharacters(Long.toString(moveResult.value));
                    writer.writeEndElement();

                    writer.writeStartElement("movenr");
                    writer.writeCharacters(Long.toString(moveResult.movenr));
                    writer.writeEndElement();
                    writer.writeEndElement();
                }
                writer.writeEndElement();
            }

            private void writePlayerGameresultattributes(XMLStreamWriter writer, GameResult gameResult) throws XMLStreamException{

                writer.writeStartElement("player1");
                writer.writeStartElement("totalNrofsearchedNodes");
                writer.writeCharacters(Long.toString(gameResult.totalNrofsearchedNodes1));
                writer.writeEndElement();

                writer.writeStartElement("averageNrofsearchedNodes");
                writer.writeCharacters(Long.toString(gameResult.averageNrofsearchedNodes1));
                writer.writeEndElement();

                writer.writeStartElement("totalNrofevaluatedNodes");
                writer.writeCharacters(Long.toString(gameResult.totalNrofevaluatedNodes1));
                writer.writeEndElement();

                writer.writeStartElement("averageNrofevaluatedNodes");
                writer.writeCharacters(Long.toString(gameResult.averageNrofevaluatedNodes1));
                writer.writeEndElement();

                writer.writeStartElement("totalNrofTTHits");
                writer.writeCharacters(Long.toString(gameResult.totalNrofTTHits1));
                writer.writeEndElement();

                writer.writeStartElement("averageNrofTTHits");
                writer.writeCharacters(Long.toString(gameResult.averageNrofTTHits1));
                writer.writeEndElement();

                writer.writeStartElement("averagedepth");
                writer.writeCharacters(Float.toString(gameResult.averagedepth1));
                writer.writeEndElement();

                writer.writeStartElement("averagevaluechange");
                writer.writeCharacters(Float.toString(gameResult.averagedifference1));
                writer.writeEndElement();

                writer.writeStartElement("standarddeviationvaluechange");
                writer.writeCharacters(Double.toString(gameResult.standarddeviation1));
                writer.writeEndElement();

                writemoveorderingpercentage(writer, moveorderingpercentage1);

                writer.writeStartElement("NrofmadeMoves");
                writer.writeCharacters(Long.toString(gameResult.NrofmadeMoves1));
                writer.writeEndElement();

                writer.writeStartElement("result");
                writer.writeCharacters(Long.toString(gameResult.result1));
                writer.writeEndElement();

                writer.writeStartElement("disqualified");
                writer.writeCharacters(Boolean.toString(gameResult.disqualified1));
                writer.writeEndElement();
                writer.writeEndElement();

                writer.writeStartElement("player2");
                writer.writeStartElement("totalNrofsearchedNodes");
                writer.writeCharacters(Long.toString(gameResult.totalNrofsearchedNodes2));
                writer.writeEndElement();

                writer.writeStartElement("averageNrofsearchedNodes");
                writer.writeCharacters(Long.toString(gameResult.averageNrofsearchedNodes2));
                writer.writeEndElement();

                writer.writeStartElement("totalNrofevaluatedNodes");
                writer.writeCharacters(Long.toString(gameResult.totalNrofevaluatedNodes2));
                writer.writeEndElement();

                writer.writeStartElement("averageNrofevaluatedNodes");
                writer.writeCharacters(Long.toString(gameResult.averageNrofevaluatedNodes2));
                writer.writeEndElement();

                writer.writeStartElement("totalNrofTTHits");
                writer.writeCharacters(Long.toString(gameResult.totalNrofTTHits2));
                writer.writeEndElement();

                writer.writeStartElement("averageNrofTTHits");
                writer.writeCharacters(Long.toString(gameResult.averageNrofTTHits2));
                writer.writeEndElement();

                writer.writeStartElement("averagedepth");
                writer.writeCharacters(Float.toString(gameResult.averagedepth2));
                writer.writeEndElement();

                writer.writeStartElement("averagevaluechange");
                writer.writeCharacters(Float.toString(gameResult.averagedifference2));
                writer.writeEndElement();

                writer.writeStartElement("standarddeviationvaluechange");
                writer.writeCharacters(Double.toString(gameResult.standarddeviation2));
                writer.writeEndElement();

                writemoveorderingpercentage(writer, moveorderingpercentage2);

                writer.writeStartElement("NrofmadeMoves");
                writer.writeCharacters(Long.toString(gameResult.NrofmadeMoves2));
                writer.writeEndElement();

                writer.writeStartElement("result");
                writer.writeCharacters(Long.toString(gameResult.result2));
                writer.writeEndElement();

                writer.writeStartElement("disqualified");
                writer.writeCharacters(Boolean.toString(gameResult.disqualified2));
                writer.writeEndElement();
                writer.writeEndElement();

            }

            private void writeGameboard(XMLStreamWriter writer, GameBoard gb) throws XMLStreamException{
                Bitboard bitboard;
                if(gb instanceof Bitboard){
                    bitboard = (Bitboard) gb;
                }
                else{
                    bitboard = Bitboard.convert(gb);
                }
                writer.writeStartElement("red");
                writer.writeCharacters(Long.toString(bitboard.red));
                writer.writeEndElement();
                writer.writeStartElement("green");
                writer.writeCharacters(Long.toString(bitboard.green));
                writer.writeEndElement();
            }

            private void writePlayerResultattributes(XMLStreamWriter writer, boolean player1) throws XMLStreamException{
                if(player1){
                    writer.writeStartElement("player1");
                    writer.writeStartElement("name");
                    writer.writeCharacters(player1name);
                    writer.writeEndElement();
                    writer.writeStartElement("timelimit");
                    writer.writeCharacters(Long.toString(limitplayer1));
                    writer.writeEndElement();

                    writer.writeStartElement("averagevaluechange");
                    writer.writeCharacters(Float.toString(averagedifference1));
                    writer.writeEndElement();

                    writer.writeStartElement("standarddeviationvaluechange");
                    writer.writeCharacters(Double.toString(standarddeviation1));
                    writer.writeEndElement();

                    writemoveorderingpercentage(writer, moveorderingpercentage1);

                    writer.writeStartElement("totalNrofsearchedNodes");
                    writer.writeCharacters(Long.toString(totalNrofsearchedNodes1));
                    writer.writeEndElement();

                    writer.writeStartElement("averageNrofsearchedNodes");
                    writer.writeCharacters(Long.toString(averageNrofsearchedNodes1));
                    writer.writeEndElement();

                    writer.writeStartElement("totalNrofevaluatedNodes");
                    writer.writeCharacters(Long.toString(totalNrofevaluatedNodes1));
                    writer.writeEndElement();

                    writer.writeStartElement("averageNrofevaluatedNodes");
                    writer.writeCharacters(Long.toString(averageNrofevaluatedNodes1));
                    writer.writeEndElement();

                    writer.writeStartElement("totalNrofTTHits");
                    writer.writeCharacters(Long.toString(totalNrofTTHits1));
                    writer.writeEndElement();

                    writer.writeStartElement("averageNrofTTHits");
                    writer.writeCharacters(Long.toString(averageNrofTTHits1));
                    writer.writeEndElement();

                    writer.writeStartElement("averagedepth");
                    writer.writeCharacters(Float.toString(averagedepth1));
                    writer.writeEndElement();

                    writer.writeStartElement("averageNrofmademoves");
                    writer.writeCharacters(Long.toString(averageNrofmadeMoves1));
                    writer.writeEndElement();

                    writer.writeStartElement("wongames");
                    writer.writeCharacters(Long.toString(wongames1));
                    writer.writeEndElement();
                    writer.writeEndElement();
                }
                else{
                    writer.writeStartElement("player2");
                    writer.writeStartElement("name");
                    writer.writeCharacters(player2name);
                    writer.writeEndElement();
                    writer.writeStartElement("timelimit");
                    writer.writeCharacters(Long.toString(limitplayer2));
                    writer.writeEndElement();

                    writer.writeStartElement("averagevaluechange");
                    writer.writeCharacters(Float.toString(averagedifference2));
                    writer.writeEndElement();

                    writer.writeStartElement("standarddeviationvaluechange");
                    writer.writeCharacters(Double.toString(standarddeviation2));
                    writer.writeEndElement();

                    writemoveorderingpercentage(writer, moveorderingpercentage2);

                    writer.writeStartElement("totalNrofsearchedNodes");
                    writer.writeCharacters(Long.toString(totalNrofsearchedNodes2));
                    writer.writeEndElement();

                    writer.writeStartElement("averageNrofsearchedNodes");
                    writer.writeCharacters(Long.toString(averageNrofsearchedNodes2));
                    writer.writeEndElement();

                    writer.writeStartElement("totalNrofevaluatedNodes");
                    writer.writeCharacters(Long.toString(totalNrofevaluatedNodes2));
                    writer.writeEndElement();

                    writer.writeStartElement("averageNrofevaluatedNodes");
                    writer.writeCharacters(Long.toString(averageNrofevaluatedNodes2));
                    writer.writeEndElement();

                    writer.writeStartElement("totalNrofTTHits");
                    writer.writeCharacters(Long.toString(totalNrofTTHits2));
                    writer.writeEndElement();

                    writer.writeStartElement("averageNrofTTHits");
                    writer.writeCharacters(Long.toString(averageNrofTTHits2));
                    writer.writeEndElement();

                    writer.writeStartElement("averagedepth");
                    writer.writeCharacters(Float.toString(averagedepth2));
                    writer.writeEndElement();

                    writer.writeStartElement("averageNrofmademoves");
                    writer.writeCharacters(Long.toString(averageNrofmadeMoves2));
                    writer.writeEndElement();

                    writer.writeStartElement("wongames");
                    writer.writeCharacters(Long.toString(wongames2));
                    writer.writeEndElement();
                    writer.writeEndElement();
                }
            }

            private void writemoveorderingpercentage(XMLStreamWriter writer, float[] moveorderpercentage) throws XMLStreamException{
                writer.writeStartElement("moveorderingpercentage");
                for (int i = 0; i < moveorderpercentage.length; i++){
                    float f = moveorderpercentage[i];
                    writer.writeStartElement("value");
                    writer.writeStartElement("nr");
                    writer.writeCharacters(Integer.toString(i));
                    writer.writeEndElement();
                    writer.writeStartElement("value");
                    writer.writeCharacters(Float.toString(f));
                    writer.writeEndElement();
                    writer.writeEndElement();
                }
                writer.writeEndElement();
            }
        }

        public class GameResult{
            public static final int    REGULARRESULT              = 0,
                    TIMEEXCEEDED = 1, ILLEGALMOVE = 2, EXCEPTION = 3;
            PlayerException            exception;
            boolean                    isfinished                 = false;
            private int                resultcode                 = REGULARRESULT;
            ArrayList<MoveResult>      moves                      = new ArrayList<>();
            int                        winner                     = DRAW;
            GameBoard                  finalBoard;
            // player1
            public long                totalNrofsearchedNodes1    = 0;
            public long                averageNrofsearchedNodes1  = 0;
            public long                totalNrofevaluatedNodes1   = 0;
            public long                averageNrofevaluatedNodes1 = 0;
            public long                totalNrofTTHits1           = 0;
            public long                averageNrofTTHits1         = 0;
            public float               averagedepth1              = 0;
            int                        depthcount1                = 0;
            private ArrayList<Integer> differencevalue1           = new ArrayList<>();
            public float               averagedifference1;
            public double              standarddeviation1;
            int                        previousvalue1;
            public ArrayList<Integer>  moveorderingcount1         = new ArrayList<>();
            public float[]             moveorderingpercentage1;
            public int                 NrofmadeMoves1             = 0;
            public int                 result1                    = 0;
            public boolean             disqualified1              = false;

            // player2
            public long                totalNrofsearchedNodes2    = 0;
            public long                averageNrofsearchedNodes2  = 0;
            public long                totalNrofevaluatedNodes2   = 0;
            public long                averageNrofevaluatedNodes2 = 0;
            public long                totalNrofTTHits2           = 0;
            public long                averageNrofTTHits2         = 0;
            public float               averagedepth2              = 0;
            int                        depthcount2                = 0;
            private ArrayList<Integer> differencevalue2           = new ArrayList<>();
            public float               averagedifference2;
            public double              standarddeviation2;
            int                        previousvalue2;
            public ArrayList<Integer>  moveorderingcount2         = new ArrayList<>();
            public float[]             moveorderingpercentage2;
            public int                 NrofmadeMoves2             = 0;
            public int                 result2                    = 0;
            public boolean             disqualified2              = false;

            public void addmove(MoveResult move){
                if(move.player == PLAYER1){
                    NrofmadeMoves1++;
                    totalNrofsearchedNodes1 += move.NrofsearchedNodes;
                    totalNrofevaluatedNodes1 += move.NrofevaluatedNodes;
                    totalNrofTTHits1 += move.NrofTTHits;
                    depthcount1 += move.depth;
                    if(moveorderingcount1.size() <= move.movenr){
                        for (int i = moveorderingcount1.size(); i <= move.movenr; i++){
                            moveorderingcount1.add(0);
                        }
                    }
                    moveorderingcount1.set(move.movenr, moveorderingcount1.get(move.movenr) + 1);
                    if(!moves.isEmpty()){
                        differencevalue1.add(move.value - previousvalue1);
                    }
                    previousvalue1 = move.value;
                }
                else{
                    NrofmadeMoves2++;
                    totalNrofsearchedNodes2 += move.NrofsearchedNodes;
                    totalNrofevaluatedNodes2 += move.NrofevaluatedNodes;
                    totalNrofTTHits2 += move.NrofTTHits;
                    depthcount2 += move.depth;
                    if(moveorderingcount2.size() <= move.movenr){
                        for (int i = moveorderingcount2.size(); i <= move.movenr; i++){
                            moveorderingcount2.add(0);
                        }
                    }
                    moveorderingcount2.set(move.movenr, moveorderingcount2.get(move.movenr) + 1);
                    if(!moves.isEmpty()){
                        differencevalue2.add(move.value - previousvalue2);
                    }
                    previousvalue2 = move.value;
                }
                moves.add(move);
            }

            private void calculateaverages(){
                averageNrofsearchedNodes1 = totalNrofsearchedNodes1
                        / NrofmadeMoves1;
                averageNrofsearchedNodes2 = totalNrofsearchedNodes2
                        / NrofmadeMoves2;
                averageNrofevaluatedNodes1 = totalNrofevaluatedNodes1
                        / NrofmadeMoves1;
                averageNrofevaluatedNodes2 = totalNrofevaluatedNodes2
                        / NrofmadeMoves2;
                averageNrofTTHits1 = totalNrofTTHits1 / NrofmadeMoves1;
                averageNrofTTHits2 = totalNrofTTHits2 / NrofmadeMoves2;
                averagedepth1 = depthcount1 / (float) NrofmadeMoves1;
                averagedepth2 = depthcount2 / (float) NrofmadeMoves2;
                int tmp = 0;
                for (Integer difference : differencevalue1){
                    tmp += difference;
                }
                averagedifference1 = tmp / (float) NrofmadeMoves1;
                tmp = 0;
                for (Integer difference : differencevalue1){
                    tmp += Math.pow(averagedifference1 - difference, 2);
                }
                standarddeviation1 = Math.sqrt(tmp / NrofmadeMoves1);
                tmp = 0;
                for (Integer difference : differencevalue2){
                    tmp += difference;
                }
                averagedifference2 = tmp / (float) NrofmadeMoves2;
                tmp = 0;
                for (Integer difference : differencevalue2){
                    tmp += Math.pow(averagedifference2 - difference, 2);
                }
                standarddeviation2 = Math.sqrt(tmp / NrofmadeMoves2);
                moveorderingpercentage1 = new float[moveorderingcount1.size()];
                for (int i = 0; i < moveorderingpercentage1.length; i++){
                    moveorderingpercentage1[i] = moveorderingcount1.get(i)
                            / (float) NrofmadeMoves1;
                }
                moveorderingpercentage2 = new float[moveorderingcount2.size()];
                for (int i = 0; i < moveorderingpercentage2.length; i++){
                    moveorderingpercentage2[i] = moveorderingcount2.get(i)
                            / (float) NrofmadeMoves2;
                }
            }

            public void finishgame(){
                finalBoard = moves.get(moves.size() - 1).gameboard;
                if(resultcode == REGULARRESULT){
                    result1 = finalBoard.countStones(player1colour);
                    result2 = finalBoard.countStones(player2colour);
                }
                else{
                    if(disqualified1){
                        result1 = 0;
                        result2 = 64;
                    }
                    else if(disqualified2){
                        result1 = 64;
                        result2 = 0;
                    }
                    else{
                        throw new IllegalStateException("Das GameResult Objekt muss einen disqualifizierten Spieler haben oder der Resultcode muss gleich REGULARRESULT sein.");
                    }
                }
                if(result1 > result2){
                    winner = PLAYER1;
                }
                else if(result2 > result1){
                    winner = PLAYER2;
                }
                else{
                    winner = DRAW;
                }
                calculateaverages();
                isfinished = true;
            }

            public int getresultcode(){
                return resultcode;
            }
        }

    }

    public static class MoveResult{
        int         player;
        Coordinates move;
        GameBoard   gameboard;
        long        NrofsearchedNodes;
        long        NrofevaluatedNodes;
        long        NrofTTHits;
        int         depth;
        int         value;
        int         movenr;
    }
}
