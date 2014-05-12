package searching;

import reversi.GameBoard;
import Gameboard.Bitboard;
import Tables.TranspositionTable;
import Tables.TranspositionTable.Sorter;
import Tables.TranspositionTable.TableEntry;
import evaluate.IEvaluator;

public class AlphaBetaNoah extends Searchalgorithm{

    // Game feature providing
    private boolean            ENEMY           = false;
    private boolean            PLAYER          = true;

    private byte               MAXDEPTH        = 10;
    private static short       TOPBORDER       = 32767;
    private Bitboard           board;
    private long               startingTime;
    private long               timeLimit;
    private boolean            run;

    private Sorter[]           sorters         = new Sorter[30];
    private TableEntry         spareTableEntry = new TableEntry();
    private TranspositionTable table           = new TranspositionTable(4000000, new TranspositionTable.pvNodePriority());

    IEvaluator                 evaluator;

    // Measuring
    // public IEvaluator evaluator = null;
    // public long TTHits = 0;

    // Helpers

    private int fuseBits(short value, byte position){
        return (int) value << 8 | (int) position;
    }

    private short extractValue(int move){
        return (short) ((move & 0xFFFFFF00) >> 8);
    }

    private byte extractPosition(int move){
        return (byte) (move & 0x000000FF);
    }

    public AlphaBetaNoah(int color, long timeLimit, IEvaluator evaluator){
        if(color == GameBoard.RED){
            PLAYER = true;
            ENEMY = false;
        }
        else{
            PLAYER = false;
            ENEMY = true;
        }
        this.deadline = timeLimit;
        this.evaluator = evaluator;
        for (byte i = 0; i < 30; i++){
            sorters[i] = new Sorter();
        }
    }

    // Do new turn

    @Override
    public long nextMove(Bitboard gb){
        // Start time measuring
        System.out.println("Move started!");
        long start = System.nanoTime();

        board = gb.copy();

        // Get next best move by recursion.
        // Initiating with enemy first because that way it returns the
        // coordinates of the next best move too instead of getting them by a
        // loop and initiating with the player
        int move = node(ENEMY, TOPBORDER, MAXDEPTH);
        int position = extractPosition(move);

        // End time measuring
        long time = (System.nanoTime() - start) / 1000000;
        System.out.println("Best value was: " + extractValue(move));
        System.out.println("Took me " + time + "ms");

        valueOfLastMove = extractValue(move);
        reachedDepth = MAXDEPTH;

        // If no move was found, return null
        // Also check if found move is valid. Bugs are common; can happen
        // anytime; if it happens, return first best move available!
        if(position == 127)
            return 0;
        return (0b10000000_00000000_00000000_00000000_00000000_00000000_00000000_00000000l) >>> position;
    }

    // Initiate recursion with opposite color!
    private int node(boolean player, short border, byte depth){
        // Measuring
        searchedNodesCount++;

        // Set value to min/max depenting on wether it is a min or max node
        short value;
        if(player == PLAYER)
            value = TOPBORDER;
        else
            value = 0;

        // Set position to null as default
        byte position = 127;
        long changedFields = 0;

        // if max depth is reached, return the board value, else find the best
        // value of successor boards
        if(depth > 0){
            // Get all moves and iterate over 'em
            long[] possibleMoves = Bitboard.serializeBitboard(board.getPossibleMoves(!player));

            for (byte i = 0; i < possibleMoves.length; i++){
                // Copy board from slot above in slot of current depth and make
                // the move
                changedFields = board.makeMove(!player, possibleMoves[i]);

                // Get best value of successors of that move
                int temp = node(!player, value, (byte) (depth - 1));
                short tempv = extractValue(temp);
                board.undoMove(changedFields, possibleMoves[i], !player);

                // Min node
                if(player == PLAYER){
                    if(tempv <= value){
                        value = tempv;
                        position = (byte) Long.numberOfLeadingZeros(possibleMoves[i]);
                        moveNr = i;
                    }
                    // a-pruning
                    if(value <= border){
                        // System.out.println(player + "/" + depth + "/"
                        // + value
                        // + " ----- min prune NORMAL PLAYER");
                        return fuseBits(value, position);
                    }
                }
                // Max node
                else{
                    if(value <= tempv){
                        value = tempv;
                        position = (byte) Long.numberOfLeadingZeros(possibleMoves[i]);
                        moveNr = i;
                    }
                    // b-pruning
                    if(value >= border){
                        // System.out.println(player + "/" + depth + "/"
                        // + value
                        // + " ----- max prune NORMAL PLAYER");
                        return fuseBits(value, position);
                    }
                }
            }
            // If no move is available, let the other player turn again
            if(position == 127){
                int temp = node(player, border, (byte) (depth - 1));
                value = extractValue(temp);
            }
        }
        // Calculate value of the board when depth 0 is reached
        else{
            value = (short) evaluator.evaluate(board, 0, player);
        }
        // System.out.println(player + "/" + depth + "/" + value
        // + " ----- normal exit NORMAL PLAYER");
        // Measuring
        evaluatedNodesCount++;
        return fuseBits(value, position);
    }

}
