package Ben;

import reversi.Coordinates;
import reversi.GameBoard;
import reversi.OutOfBoundsException;
import searching.AlphaBetaNoCloneing;
import Gameboard.Bitboard;
import Testing.ITestablePlayer;
import evaluate.StrategicEvaluatorNoah;

public class Ben implements ITestablePlayer{
    /**
     * Bitmasks
     */
    private static final long   leftshiftMask      = 0xFEFEFEFEFEFEFEFEL;
    private static final long   rightshiftMask     = 0x7F7F7F7F7F7F7F7FL;
    private static final long   shiftMask          = 0x7E7E7E7E7E7E7E7EL;

    private static final long   CORNERS            = 0x8100000000000081L;
    private static final long   TOPEDGE            = 0x00000000000000FFL;
    private static final long   BOTTOMEDGE         = 0xFF00000000000000L;
    private static final long   VERTICALEDGES      = TOPEDGE | BOTTOMEDGE;
    private static final long   LEFTEDGE           = 0x0101010101010101L;
    private static final long   RIGHTEDGE          = 0x8080808080808080L;
    private static final long   HORIZONTALEDGES    = LEFTEDGE | RIGHTEDGE;
    private static final long   EDGES              = VERTICALEDGES | HORIZONTALEDGES;

    static final byte           maskC              = 0b01000010;
    static final byte           maskA              = 0b00100100;
    static final byte           maskB              = 0b00011000;

    /*
     * 012
     * 7 3
     * 654
     */
    // bitmasks for every line-direction
    private static long         LINES15[]          = {
                                                   0x0101010101010101L, 0x0202020202020202L, 0x0404040404040404L, 0x0808080808080808L,
                                                   0x1010101010101010L, 0x2020202020202020L, 0x4040404040404040L, 0x8080808080808080L
                                                   };
    private static long         LINES37[]          = {
                                                   0x00000000000000ffL, 0x000000000000ff00L, 0x0000000000ff0000L, 0x00000000ff000000L,
                                                   0x000000ff00000000L, 0x0000ff0000000000L, 0x00ff000000000000L, 0xff00000000000000L
                                                   };
    private static long         LINES04[]          = {
                                                   0x8040201008040201L, 0x0080402010080402L, 0x0000804020100804L, 0x0000008040201008L,
                                                   0x0000000080402010L, 0x0000000000804020L, 0x4020100804020100L, 0x2010080402010000L,
                                                   0x1008040201000000L, 0x0804020100000000L, 0x0402010000000000L
                                                   };
    private static long         LINES26[]          = {
                                                   0x0102040810204080L, 0x0001020408102040L, 0x0000010204081020L, 0x0000000102040810L,
                                                   0x0000000001020408L, 0x0000000000010204L, 0x0204081020408000L, 0x0408102040800000L,
                                                   0x0810204080000000L, 0x1020408000000000L, 0x2040800000000000L
                                                   };

    private static final long[] zobristRandomRed   = {
                                                   0xfbf185c26c378076L, 0x14fc57c338f4f1a5L, 0x925f95f86089b91dL, 0xf7532bd039d1a1f4L,
                                                   0xecfbaddab0d6fce0L, 0x2a8ca0dd2626b862L, 0x23d47c43ff36ce38L, 0x3e6baf923c28f448L,
                                                   0x20d9010265a3f40fL, 0x6388d91cddc1c1cL, 0xafa8e8696ed1738bL, 0x90bc3d08fc1ec9a2L,
                                                   0x36c017d07c49546bL, 0xf2ef8f4cac6794f1L, 0xf56bb2fcbdc8d2b1L, 0x7fa139f336fc98feL,
                                                   0x3e4731ff2edbd886L, 0x2341ecdb9dadb92bL, 0x715f9e7d5c37bf11L, 0xeae7498f80a35fa8L,
                                                   0xcbfb8bf37d9c9a6aL, 0x6519cc1b281cd98eL, 0x1220e41bc588fd4dL, 0xbf7683bbe19659e9L,
                                                   0x627d82e87a1a5c8fL, 0x66bb352a4233da01L, 0xb118e976734e752dL, 0x45059a12dd803309L,
                                                   0xde761d1d4fee1963L, 0x77079d25f4eacf3aL, 0x6d9a57385cee69cfL, 0xb1effad871ebfc7eL,
                                                   0x1a0140e9b3b4f9b1L, 0x3717a23db835e737L, 0x79c6039fc1149b5L, 0x5232f65d4634ab04L,
                                                   0xa7382a6bc45005a0L, 0x84df5634e6026379L, 0x49fb2991a7b8d155L, 0xbe66b547b3145c54L,
                                                   0x5a285e00e8e52ce9L, 0xb0023175a5dfd33L, 0x18eb9696bfc766ceL, 0x6898f099c66dfdcbL,
                                                   0x3c5f157a41176274L, 0x330c473b052343acL, 0xe331484b0c5eea6bL, 0xc8ec9114c856a3cbL,
                                                   0x77ea0c91db3b5df9L, 0x8fd55922b8489683L, 0x3006266598dc8ceL, 0x441c36d33f203effL,
                                                   0x34cc29731c5d4a3dL, 0x553bbe5df8f53190L, 0x17ea7878ae609e80L, 0x199cdc8e23095b40L,
                                                   0xc048ab76910b2c5aL, 0x5d11eefe79c29638L, 0x658cfdf9d3598681L, 0x66fa01853fe29ec3L,
                                                   0x8485961d728f09b5L, 0xcc1281bbe4c0cc6aL, 0x4d16571ec4e48e15L, 0x957c6ae1831d84faL
                                                   };

    /**
     * 64 random {@code long} constants used to generate zobrist-hashes.
     */
    private static final long[] zobristRandomGreen = {
                                                   0x5bb837cfc908843L, 0xe69cb281253ce62L, 0x83f782b737295f4L, 0xa35ce4ae312051eL,
                                                   0x2c73b38abd54779L, 0x6bcc139d081d3ecL, 0x5ba9b28ba7956b0L, 0x283347aa7b70f62L,
                                                   0xfe227cbe0394798L, 0x9e9f51b8c1bcd62L, 0x499cfb3401dae88L, 0xf44f6d6cc626537L,
                                                   0xd8b04996dc30640L, 0x8eff63264fb72e4L, 0x3167782e4db58eaL, 0xd92bad68f07579eL,
                                                   0x6845abc389b5264L, 0x35602c1d9a6f48eL, 0x919b13e0e5568f8L, 0x90a954c53a004daL,
                                                   0xa2d619f9dbae31dL, 0x1365e1683ffb279L, 0x4f41aa83ce66e8eL, 0xc736a9c06c3f22aL,
                                                   0xba6792597e7ffbL, 0x1c609e23d070278L, 0x11f440ed2f335eaL, 0xaccc44df185fac1L,
                                                   0x4696c44a66f0ab6L, 0x0a65c0f2aadc6e9L, 0xe19d03419fe3211L, 0x429b01059ebf8e5L,
                                                   0xf88106b9ba1df0aL, 0xdb20740fe57146bL, 0x1a6735b4c7ed329L, 0x85b8735e1b17690L,
                                                   0xfa84f02a44a5049L, 0x3171824e0effc88L, 0x31b3be1593ed5bL, 0x1de88b282db9797L,
                                                   0xe8d2d1068b805eeL, 0x0cb563ecbeb5579L, 0x3a11efffddd4fcdL, 0x09e06c8b3685961L,
                                                   0xe4ce0db3a503bacL, 0x4d3e0caf0edac32L, 0x1853bc5766968dfL, 0x2f3279e4b68386fL,
                                                   0xa198dcd2f4d33acL, 0x4374ee42f26df21L, 0x22fcb599ce87fe0L, 0x70fd9d9fa5ab8acL,
                                                   0x481fdc67f3a56eeL, 0x175cfd246232343L, 0x224d059741530cL, 0x7af6dd1d43b0ebcL,
                                                   0xb3305942dec8e66L, 0x1e08678051046b9L, 0x71dcb32e7d6ecd4L, 0x0873bfc1c5ef92dL,
                                                   0x7cfaac0a8ac0dcbL, 0xc4e99466f54709aL, 0xac95c64d48d5682L, 0x55000a14de5437L
                                                   };
    // Transposition Table
    private final static byte   CUTNODE            = (byte) 2, PVNODE = (byte) 4, ALLNODE = (byte) 1;
    private final static int    NOTFOUND           = -2147483648;
    private final static int    TTsize             = (0x80000000 >>> Integer.numberOfLeadingZeros(10000000));
    private final static long   TTindexMask        = ~(0x8000000000000000L >> (32 + Integer.numberOfLeadingZeros(TTsize)));
    private static long[]       TTkeys             = new long[TTsize];
    private static short[]      TTvalues           = new short[TTsize];
    private static byte[]       TTdepths           = new byte[TTsize];
    private static byte[]       TTtypes            = new byte[TTsize];
    private static byte[]       TTplayedStones     = new byte[TTsize];

    private static long         hash;

    private static long[][]     SboardsRed         = new long[30][30];
    private static long[][]     SboardsGreen       = new long[30][30];
    private static int[][]      Svalues            = new int[30][30];
    private static int[][]      Sindices           = new int[30][30];
    private static int[]        Ssize              = new int[30];
    private static long[][]     Shashes            = new long[30][30];
    private static long[][]     SpossibleMoves     = new long[30][30];
    private static long[][]     Smoves             = new long[30][30];

    private static short[]      edgeTable          = new short[65536];

    private final static short  INFINITY           = 32767;

    /**
     * Searched Nodes in last Search.
     */
    private static int          searchedNodes;

    /**
     * Evaluation Result of the best move found in last search.
     */
    private static int          resultOfSearch;

    /**
     * Boolean flag for Search Algorithms to determine if its time to return
     * from recursion.
     */
    private static boolean      returnFromSearch;

    private static boolean      useOutcomeSearch;
    private static boolean      useExactSearch;
    /**
     * Deadline for current Search Algorithm.
     */
    private static long         localDeadline;

    /**
     * Deadline for this Move.
     */
    private static long         globalDeadline;

    private static byte         playedStones;

    private static int          pvsFirstMoveIndex;

    private static int          myColor;
    private static long         timeLimit;

    /**
     * Converts a {@code GameBoard} into a long array of size 2.
     * This method has to be used carefully since it affords a lot of
     * calculation time!
     * 
     * @param gb
     *            the {@link GameBoard} to be converted
     * @return the converted GameBoard as long array of size 2
     */
    private static final long[] convertToBitboard(final GameBoard gb){
        long[] board = new long[2];
        Coordinates coord;
        try{
            for (int i = 1; i < 9; i++){
                for (int j = 1; j < 9; j++){
                    coord = new Coordinates(i, j);
                    int occupation = gb.getOccupation(coord);
                    if(occupation != GameBoard.EMPTY){
                        if(occupation == GameBoard.RED){
                            board[0] |= Bitboard.coordinatesToLong(coord);
                        }
                        else{
                            board[1] |= Bitboard.coordinatesToLong(coord);
                        }
                    }
                }
            }
        }catch(OutOfBoundsException e){
        }
        return board;
    }

    /**
     * Converts {@code coords} to the long equivalent in the
     * bitboard-represenation
     * 
     * @param coords
     *            the {@link Coordinates} object that is to be converted
     * @return the converted coordinates.
     */
    private static final long coordinatesToLong(final Coordinates coords){
        if(coords == null){
            return 0;
        }
        return 1L << (8 - coords.getCol() + 64 - 8 * coords.getRow());
    }

    /**
     * 
     * @param player
     *            board in view of the player whose stones are to be count
     * @return the amount of stones for the player which was given in
     *         {@code player}
     */
    private static final int countStones(long player){
        return Long.bitCount(player);
    }

    private static int endGameFewRemainingMax(long red, long green, int alpha, int beta, boolean passed){
        searchedNodes++;
        long emptyfields = ~(red | green);
        long changedfields = 0;
        long coord = 0;
        int bestvalue = alpha;
        int value = alpha;
        boolean nomoveavailable = true;
        while(emptyfields != 0){
            coord = Long.highestOneBit(emptyfields);
            changedfields = Bitboard.getflippedDiskRed(red, green, coord);
            if(changedfields != 0){
                value = endGameFewRemainingMin(red ^ changedfields
                        ^ coord, green ^ changedfields, bestvalue, beta, false);
                if(value > bestvalue){
                    if(value >= beta){
                        return beta;
                    }
                    bestvalue = value;
                }
                nomoveavailable = false;
            }
            emptyfields ^= coord;
        }
        if(nomoveavailable){
            if(passed){
                return Long.bitCount(red) - Long.bitCount(green);
            }
            return endGameFewRemainingMin(red, green, alpha, beta, true);
        }
        return bestvalue;
    }

    private static int endGameFewRemainingMin(long red, long green, int alpha, int beta, boolean passed){
        searchedNodes++;
        long emptyfields = ~(red | green);
        long changedfields = 0;
        long coord = 0;
        int bestvalue = beta;
        int value = beta;
        boolean nomoveavailable = true;
        while(emptyfields != 0){
            coord = Long.highestOneBit(emptyfields);
            changedfields = Bitboard.getflippedDiskRed(green, red, coord);
            if(changedfields != 0){
                value = endGameFewRemainingMax(red ^ changedfields, green
                        ^ changedfields ^ coord, alpha, bestvalue, false);
                if(value < bestvalue){
                    if(value <= alpha){
                        return alpha;
                    }
                    bestvalue = value;
                }
                nomoveavailable = false;
            }
            emptyfields ^= coord;
        }
        if(nomoveavailable){
            if(passed){
                return Long.bitCount(red) - Long.bitCount(green);
            }
            return endGameFewRemainingMax(red, green, alpha, beta, true);
        }
        return bestvalue;
    }

    private final static int endGameMax(long red, long green, int alpha, int beta, int remainingStones, long possibleMoves){
        searchedNodes++;
        if(returnFromSearch){
            return NOTFOUND;
        }
        if(remainingStones <= 4){
            if(System.nanoTime() >= localDeadline){
                returnFromSearch = true;
                return NOTFOUND;
            }
            return endGameFewRemainingMax(red, green, alpha, beta, false);
        }
        if(possibleMoves == 0){
            if(possibleMovesRed(green, red) == 0){
                return Long.bitCount(red) - Long.bitCount(green);
            }
            return endGameMin(red, green, alpha, beta, remainingStones - 1, Bitboard.possibleMovesRed(green, red));
        }
        int bestvalue = alpha;
        int value = alpha;
        int moveListsize = endGameSortMoves(red, green, possibleMoves, remainingStones);
        for (int i = 0; i < moveListsize; i++){
            int index = Sindices[remainingStones][i];
            value = endGameMin(SboardsRed[remainingStones][index], SboardsGreen[remainingStones][index], value, beta, remainingStones - 1, SpossibleMoves[remainingStones][index]);
            if(value > alpha){
                if(value >= beta){
                    return beta;
                }
                bestvalue = value;
            }
        }
        return bestvalue;
    }

    private final static int endGameMin(long red, long green, int alpha, int beta, int remainingStonestones, long possibleMoves){
        searchedNodes++;
        if(returnFromSearch){
            return NOTFOUND;
        }
        if(remainingStonestones <= 4){
            if(System.nanoTime() >= localDeadline){
                returnFromSearch = true;
            }
            return endGameFewRemainingMin(red, green, alpha, beta, false);
        }
        if(possibleMoves == 0){
            if(possibleMovesRed(red, green) == 0){
                return Long.bitCount(red) - Long.bitCount(green);
            }
            return endGameMax(red, green, alpha, beta, remainingStonestones - 1, Bitboard.possibleMovesRed(red, green));
        }
        int bestvalue = beta;
        int value = beta;
        int moveListsize = endGameSortMoves(green, red, possibleMoves, remainingStonestones);
        for (int i = 0; i < moveListsize; i++){
            int index = Sindices[remainingStonestones][i];
            value = endGameMax(SboardsGreen[remainingStonestones][index], SboardsRed[remainingStonestones][index], alpha, value, remainingStonestones - 1, SpossibleMoves[remainingStonestones][index]);
            if(value < beta){
                if(value <= alpha){
                    return alpha;
                }
                bestvalue = value;
            }
        }
        return bestvalue;
    }

    /**
     * 
     * EVALUATION
     * 
     */
    private final static long endGameSearch(long red, long green, int alpha, int beta){
        searchedNodes = 0;
        returnFromSearch = false;
        int remainingStones = 64 - Long.bitCount(red) - Long.bitCount(green);
        long possibleMoves = possibleMovesRed(red, green);
        if(possibleMoves == 0){
            long possibleMovesEnemy = possibleMovesRed(green, red);
            if(possibleMovesEnemy != 0){
                resultOfSearch = endGameMin(red, green, alpha, beta, remainingStones, Bitboard.possibleMovesRed(green, red));
            }
            return 0;
        }
        long bestmove = Long.highestOneBit(possibleMoves);
        int bestvalue = alpha;
        int value = alpha;
        int moveListsize = endGameSortMoves(red, green, possibleMoves, remainingStones);
        for (int i = 0; i < moveListsize; i++){
            int index = Sindices[remainingStones][i];
            value = endGameMin(SboardsRed[remainingStones][index], SboardsGreen[remainingStones][index], value, beta, remainingStones - 1, SpossibleMoves[remainingStones][index]);
            if(value > bestvalue){
                if(value >= beta){
                    bestvalue = value;
                    resultOfSearch = value;
                    return Smoves[remainingStones][index];
                }
                bestvalue = value;
                bestmove = Smoves[remainingStones][index];
            }
            possibleMoves ^= Smoves[remainingStones][index];
        }
        resultOfSearch = bestvalue;
        if(returnFromSearch){
            resultOfSearch = NOTFOUND;
        }
        return bestmove;
    }

    private final static int endGameSortMoves(long red, long green, long possibleMoves, int remainingStones){
        int size = Long.bitCount(possibleMoves);
        Ssize[remainingStones] = size;
        for (int i = 0; i < size; i++){
            Sindices[remainingStones][i] = i;
            long coord = Long.highestOneBit(possibleMoves);
            Smoves[remainingStones][i] = coord;
            long flipeddisk = getFlippedDiskRed(red, green, coord);
            long newred = red ^ flipeddisk ^ coord;
            long newgreen = green ^ flipeddisk;
            SboardsRed[remainingStones][i] = newred;
            SboardsGreen[remainingStones][i] = newgreen;
            long newpossiblemoves = possibleMovesRed(newgreen, newred);
            SpossibleMoves[remainingStones][i] = newpossiblemoves;
            long occupiedCorners = newred & CORNERS;
            long cornerstability = occupiedCorners;
            cornerstability |= occupiedCorners >>> 1 & 0x7f7f7f7f7f7f7f7fL;
            cornerstability |= occupiedCorners >>> 8;
            cornerstability |= occupiedCorners << 1 & 0xfefefefefefefefeL;
            cornerstability |= occupiedCorners << 8;
            cornerstability &= newred;
            int score = 8 * Long.bitCount(cornerstability) - 8
                    * Long.bitCount(newpossiblemoves & CORNERS)
                    - Long.bitCount(newpossiblemoves);
            Svalues[remainingStones][i] = score;
            possibleMoves ^= coord;
        }
        insertionSortDesc(Svalues[remainingStones], Sindices[remainingStones], size);
        return size;
    }

    private static final short evaluate(long red, long green, long possibleMovesRedLong, long possibleMovesGreenLong){
        double EC = 4;
        double MC = 80;
        double MC2 = 100;
        double SC = 18;

        // Mobility
        int possibleMovesRed = Long.bitCount(possibleMovesRedLong);
        int possibleMovesGreen = Long.bitCount(possibleMovesGreenLong);

        // Potential mobility
        long empty = ~(red | green);
        int potentialMovesRed = Long.bitCount(Bitboard.fillAdjacent(empty)
                & green);
        int potentialMovesEnemyGreen = Long.bitCount(Bitboard.fillAdjacent(empty)
                & red);

        // Edge advantage
        int edgeAdvantage = (int) getEdgeValue(red, green);

        // Mobility advantage
        float mobilityAdvantage = possibleMovesRed - possibleMovesGreen;

        // Potential mobility advantage
        float potentialMobilityAdvantage = potentialMovesRed - potentialMovesEnemyGreen;

        int occupiedSquareAdvantage = (int) (Long.bitCount(getStableDisks(red, green))
                - Long.bitCount(getStableDisks(green, red)));

        int score = (int) (
                EC * edgeAdvantage
                        + MC * mobilityAdvantage
                        + MC2 * potentialMobilityAdvantage
                        + SC * occupiedSquareAdvantage
                );
        if(score > 32767 || score < -32768){
            System.out.println("ALERT!SWEG!11ELF!!");
        }
        // gb.print();
        // System.out.println("Evaluator Noah");
        // System.out.println("EdgeAdvantage: " + EC * edgeAdvantage);
        // System.out.println("MobilityAdvantage: " + (MC * mobilityAdvantage));
        // System.out.println("PotentialMobility: "
        // + (MC2 * potentialMobilityAdvantage));
        // System.out.println("occupiedSquareAdvantage: "
        // + SC * occupiedSquareAdvantage);
        // System.out.println(score);
        // System.out.println("------------------------");
        // System.out.println("Evaluator Xiaolon");
        // System.out.println(testXiaolong.evaluate(gb.red, gb.green));
        return (short) score;
    }

    /**
     * Masks all the adjacent fields of all set bits in the bitboard
     * 
     * @param bitboard
     *            of which the neighbor cells should be calculated
     * @return a new bitboard with just the newly set bits; without the masked
     *         bits
     */
    private static final long fillAdjacent(final long bitboard){
        long filledbitboard = bitboard;
        filledbitboard |= filledbitboard >>> 1 & 0x7f7f7f7f7f7f7f7fL;
        filledbitboard |= filledbitboard >>> 8;
        filledbitboard |= filledbitboard << 1 & 0xfefefefefefefefeL;
        filledbitboard |= filledbitboard << 8;
        return filledbitboard ^ bitboard;
    }

    /**
     * 
     * EDGE TABLES
     * 
     */

    private static final void generateEdgeTable(){
        for (int k = 0; k < 6561; k++){
            int c = k;
            short[] board = new short[3];
            for (int z = 0; z < 8; z++){
                board[c % 3] |= (short) (1 << z);
                // System.out.print(c % 3);
                c /= 3;
            }

            // Red values
            byte unstableRed = getUnstableEdgePieces((byte) (0xFF & board[1]), (byte) (0xFF & board[2]));
            byte unanchoredRed = getUnanchoredStableEdgePieces((byte) (0xFF & board[1]), getUnstableEdgePieces((byte) (0xFF & board[2]), (byte) (0xFF & board[1])));
            byte aloneRed = getAloneEdgePieces((byte) (0xFF & board[1]), (byte) (0xFF & board[2]));
            byte stable1Red = getStable1EdgePieces((byte) (0xFF & board[1]), (byte) (0xFF & board[2]));
            byte stable3Red = getStable3EdgePieces((byte) (0xFF & board[1]), (byte) (0xFF & board[2]));
            byte semiRed = (byte) ((0b11111111 ^ unstableRed ^ unanchoredRed
                    ^ aloneRed
                    ^ stable1Red
                    ^ stable3Red) & board[1]);

            // Green values
            byte unstableGreen = getUnstableEdgePieces((byte) (0xFF & board[2]), (byte) (0xFF & board[1]));
            byte unanchoredGreen = getUnanchoredStableEdgePieces((byte) (0xFF & board[2]), getUnstableEdgePieces((byte) (0xFF & board[1]), (byte) (0xFF & board[2])));
            byte aloneGreen = getAloneEdgePieces((byte) (0xFF & board[2]), (byte) (0xFF & board[1]));
            byte stable1Green = getStable1EdgePieces((byte) (0xFF & board[2]), (byte) (0xFF & board[1]));
            byte stable3Green = getStable3EdgePieces((byte) (0xFF & board[2]), (byte) (0xFF & board[1]));
            byte semiGreen = (byte) ((0b11111111 ^ unstableGreen
                    ^ unanchoredGreen ^ aloneGreen
                    ^ stable1Green
                    ^ stable3Green) & board[2]);

            float score = 0;

            // Score red
            score += Integer.bitCount(maskC & unstableRed) * -0.5;
            score += Integer.bitCount(maskA & unstableRed) * 0.2;
            score += Integer.bitCount(maskB & unstableRed) * 0.15;

            score += Integer.bitCount(maskA & unanchoredRed) * 3;
            score += Integer.bitCount(maskB & unanchoredRed) * 2;

            score += Integer.bitCount(maskC & aloneRed) * -0.75;
            score += Integer.bitCount(maskA & aloneRed) * -0.25;
            score += Integer.bitCount(maskB & aloneRed) * -0.5;

            score += Integer.bitCount(stable1Red) * 8;

            score += Integer.bitCount(maskC & stable3Red) * 12;
            score += Integer.bitCount(maskA & stable3Red) * 10;
            score += Integer.bitCount(maskB & stable3Red) * 10;
            score += Integer.bitCount(0b10000001 & stable3Red) * 80;

            score += Integer.bitCount(maskC & semiRed) * -1.25;
            score += Integer.bitCount(maskA & semiRed) * 1;
            score += Integer.bitCount(maskB & semiRed) * 1;

            // Negative score Green
            score -= Integer.bitCount(maskC & unstableGreen) * -0.5;
            score -= Integer.bitCount(maskA & unstableGreen) * 0.2;
            score -= Integer.bitCount(maskB & unstableGreen) * 0.15;

            score -= Integer.bitCount(maskA & unanchoredGreen) * 3;
            score -= Integer.bitCount(maskB & unanchoredGreen) * 2;

            score -= Integer.bitCount(maskC & aloneGreen) * -0.75;
            score -= Integer.bitCount(maskA & aloneGreen) * -0.25;
            score -= Integer.bitCount(maskB & aloneGreen) * -0.5;

            score -= Integer.bitCount(stable1Green) * 8;

            score -= Integer.bitCount(maskC & stable3Green) * 12;
            score -= Integer.bitCount(maskA & stable3Green) * 10;
            score -= Integer.bitCount(maskB & stable3Green) * 10;
            score -= Integer.bitCount(0b10000001 & stable3Green) * 80;

            score -= Integer.bitCount(maskC & semiGreen) * -10.25;
            score -= Integer.bitCount(maskA & semiGreen) * 1;
            score -= Integer.bitCount(maskB & semiGreen) * 1;

            edgeTable[(board[1] << 8) | board[2]] = (short) score;
            // System.out.println(k);
            // System.out.println(String.format("%8s",
            // Integer.toBinaryString(0xFF & board[1])).replace(' ', '0'));
            // System.out.println(String.format("%8s",
            // Integer.toBinaryString(0xFF & board[2])).replace(' ', '0'));
            // System.out.println(score);
            // System.out.println("-------------------");

        }
    }

    /**
     * 
     * @param red
     *            board in view of the red player
     * @param green
     *            board in view of the red player
     * @return a hash to determine the position in the hashtable
     */

    private static final long generateZobristHash(final long red, final long green){
        long value = 0;
        for (long bit : serializeBitboard(red)){
            value ^= zobristRandomRed[Long.numberOfTrailingZeros(bit)];
        }
        for (long bit : serializeBitboard(green)){
            value ^= zobristRandomGreen[Long.numberOfTrailingZeros(bit)];
        }
        return value;
    }

    /**
     * get the occupation of a field for {@code player}
     * 
     * @param player
     *            the player which we talk of
     * @param x
     *            the x coordinate of the field
     * @param y
     *            the y coordinate of the field
     * @return true if occupated, false if not
     */

    private static final boolean get(final long player, final byte x, final byte y){
        if(x > 7 || x < 0 || y > 7 || y < 0)
            return false;
        return ((player >>> (63 - y * 8 - x)) & 1) == 1;
    }

    private static final byte getAloneEdgePieces(byte borderRed, byte borderGreen){
        byte emptyEdge = (byte) ~(borderRed | borderGreen);
        return (byte) (((((borderRed << 1) & emptyEdge) >>> 2) & emptyEdge) << 1);
    }

    /**
     * @return the number of ALL stones on the field
     */

    private static final int getDiscCount(final long red, final long green){
        return (byte) (Long.bitCount(red) + Long.bitCount(green));
    }

    private static final short getEdgeValue(long red, long green){
        short edgeTopRed = (short) (red >>> 56);
        short edgeBotRed = (short) (red & 0xFF);
        long bitboard = red & 0x0101010101010101L;
        bitboard |= bitboard >>> 28;
        bitboard |= bitboard >>> 14;
        bitboard |= bitboard >>> 7;
        short edgeRightRed = (short) (bitboard & 0xFF);
        bitboard = (red >>> 7) & 0x0101010101010101L;
        bitboard |= bitboard >>> 28;
        bitboard |= bitboard >>> 14;
        bitboard |= bitboard >>> 7;
        short edgeLeftRed = (short) (bitboard & 0xFF);

        short edgeTopGreen = (short) (green >>> 56);
        short edgeBotGreen = (short) (green & 0xFF);
        bitboard = green & 0x0101010101010101L;
        bitboard |= bitboard >>> 28;
        bitboard |= bitboard >>> 14;
        bitboard |= bitboard >>> 7;
        short edgeRightGreen = (short) (bitboard & 0xFF);
        bitboard = (green >>> 7) & 0x0101010101010101L;
        bitboard |= bitboard >>> 28;
        bitboard |= bitboard >>> 14;
        bitboard |= bitboard >>> 7;
        short edgeLeftGreen = (short) (bitboard & 0xFF);

        return (short) (edgeTable[(edgeTopRed << 8) | edgeTopGreen]
                + edgeTable[(edgeBotRed << 8) | edgeBotGreen]
                + edgeTable[(edgeLeftRed << 8) | edgeLeftGreen] + edgeTable[(edgeRightRed << 8)
                | edgeRightGreen]);

    }

    private static final long getFlippedDiskRed(final long red, long green, final long coord){
        long cursor;
        long possiblyChangedFields = 0;
        long changedFields = 0;

        // upshift
        cursor = (coord << 8) & green;
        if(cursor != 0){
            possiblyChangedFields = 0;
            do{
                possiblyChangedFields |= cursor;
                cursor = (cursor << 8);
            }while((cursor & green) != 0);
            if((cursor & red) != 0){
                changedFields |= possiblyChangedFields;
            }
        }
        // downshift
        cursor = (coord >>> 8) & green;
        if(cursor != 0){
            possiblyChangedFields = 0;
            do{
                possiblyChangedFields |= cursor;
                cursor = (cursor >>> 8);
            }while((cursor & green) != 0);
            if((cursor & red) != 0){
                changedFields |= possiblyChangedFields;
            }
        }

        // shift correction
        green &= shiftMask;

        // leftshift
        cursor = (coord << 1) & green;
        if(cursor != 0){
            possiblyChangedFields = 0;
            do{
                possiblyChangedFields |= cursor;
                cursor = (cursor << 1);
            }while((cursor & green) != 0);
            if((cursor & red) != 0){
                changedFields |= possiblyChangedFields;
            }
        }

        // rightshift
        cursor = (coord >>> 1) & green;
        if(cursor != 0){
            possiblyChangedFields = 0;
            do{
                possiblyChangedFields |= cursor;
                cursor = (cursor >>> 1);
            }while((cursor & green) != 0);
            if((cursor & red) != 0){
                changedFields |= possiblyChangedFields;
            }
        }

        // upleftshift
        cursor = (coord << 9) & green;
        if(cursor != 0){
            possiblyChangedFields = 0;
            do{
                possiblyChangedFields |= cursor;
                cursor = (cursor << 9);
            }while((cursor & green) != 0);
            if((cursor & red) != 0){
                changedFields |= possiblyChangedFields;
            }
        }

        // uprightshift
        cursor = (coord << 7) & green;
        if(cursor != 0){
            possiblyChangedFields = 0;
            do{
                possiblyChangedFields |= cursor;
                cursor = (cursor << 7);
            }while((cursor & green) != 0);
            if((cursor & red) != 0){
                changedFields |= possiblyChangedFields;
            }
        }

        // downleftshift
        cursor = (coord >>> 7) & green;
        if(cursor != 0){
            possiblyChangedFields = 0;
            do{
                possiblyChangedFields |= cursor;
                cursor = (cursor >>> 7);
            }while((cursor & green) != 0);
            if((cursor & red) != 0){
                changedFields |= possiblyChangedFields;
            }
        }

        // downrightshift
        cursor = (coord >>> 9) & green;
        if(cursor != 0){
            possiblyChangedFields = 0;
            do{
                possiblyChangedFields |= cursor;
                cursor = (cursor >>> 9);
            }while((cursor & green) != 0);
            if((cursor & red) != 0){
                changedFields |= possiblyChangedFields;
            }
        }

        return changedFields;
    }

    private final static long getflippedDiskRedHash(long red, long green, final long coord){
        long cursor;
        long possiblychangedfields = 0;
        long possibleHash = 0;
        long changedfields = 0;
        int index = Long.numberOfTrailingZeros(coord);
        int tmpindex = index + 8;

        // upshift
        cursor = (coord << 8) & green;
        if(cursor != 0){
            do{
                possiblychangedfields |= cursor;
                possibleHash ^= zobristRandomGreen[tmpindex]
                        ^ zobristRandomRed[tmpindex];
                tmpindex += 8;
                cursor = (cursor << 8);
            }while((cursor & green) != 0);
            if((cursor & red) != 0){
                changedfields |= possiblychangedfields;
                hash ^= possibleHash;
            }
        }
        // downshift
        cursor = (coord >>> 8) & green;
        if(cursor != 0){
            tmpindex = index - 8;
            possiblychangedfields = 0;
            possibleHash = 0;
            do{
                possiblychangedfields |= cursor;
                possibleHash ^= zobristRandomGreen[tmpindex]
                        ^ zobristRandomRed[tmpindex];
                tmpindex -= 8;
                cursor = (cursor >>> 8);
            }while((cursor & green) != 0);
            if((cursor & red) != 0){
                changedfields |= possiblychangedfields;
                hash ^= possibleHash;
            }
        }

        // shift correction
        green &= shiftMask;

        // leftshift
        cursor = (coord << 1) & green;
        if(cursor != 0){
            tmpindex = index + 1;
            possiblychangedfields = 0;
            possibleHash = 0;
            do{
                possiblychangedfields |= cursor;
                possibleHash ^= zobristRandomGreen[tmpindex]
                        ^ zobristRandomRed[tmpindex];
                tmpindex += 1;
                cursor = (cursor << 1);
            }while((cursor & green) != 0);
            if((cursor & red) != 0){
                changedfields |= possiblychangedfields;
                hash ^= possibleHash;
            }
        }

        // rightshift
        cursor = (coord >>> 1) & green;
        if(cursor != 0){
            tmpindex = index - 1;
            possiblychangedfields = 0;
            possibleHash = 0;
            do{
                possiblychangedfields |= cursor;
                possibleHash ^= zobristRandomGreen[tmpindex]
                        ^ zobristRandomRed[tmpindex];
                tmpindex -= 1;
                cursor = (cursor >>> 1);
            }while((cursor & green) != 0);
            if((cursor & red) != 0){
                changedfields |= possiblychangedfields;
                hash ^= possibleHash;
            }
        }

        // upleftshift
        cursor = (coord << 9) & green;
        if(cursor != 0){
            tmpindex = index + 9;
            possiblychangedfields = 0;
            possibleHash = 0;
            do{
                possiblychangedfields |= cursor;
                possibleHash ^= zobristRandomGreen[tmpindex]
                        ^ zobristRandomRed[tmpindex];
                tmpindex += 9;
                cursor = (cursor << 9);
            }while((cursor & green) != 0);
            if((cursor & red) != 0){
                changedfields |= possiblychangedfields;
                hash ^= possibleHash;
            }
        }

        // uprightshift
        cursor = (coord << 7) & green;
        if(cursor != 0){
            tmpindex = index + 7;
            possiblychangedfields = 0;
            possibleHash = 0;
            do{
                possiblychangedfields |= cursor;
                possibleHash ^= zobristRandomGreen[tmpindex]
                        ^ zobristRandomRed[tmpindex];
                tmpindex += 7;
                cursor = (cursor << 7);
            }while((cursor & green) != 0);
            if((cursor & red) != 0){
                changedfields |= possiblychangedfields;
                hash ^= possibleHash;
            }
        }

        // downleftshift
        cursor = (coord >>> 7) & green;
        if(cursor != 0){
            tmpindex = index - 7;
            possiblychangedfields = 0;
            possibleHash = 0;
            do{
                possiblychangedfields |= cursor;
                possibleHash ^= zobristRandomGreen[tmpindex]
                        ^ zobristRandomRed[tmpindex];
                tmpindex -= 7;
                cursor = (cursor >>> 7);
            }while((cursor & green) != 0);
            if((cursor & red) != 0){
                changedfields |= possiblychangedfields;
                hash ^= possibleHash;
            }
        }

        // downrightshift
        cursor = (coord >>> 9) & green;
        if(cursor != 0){
            tmpindex = index - 9;
            possiblychangedfields = 0;
            possibleHash = 0;
            do{
                possiblychangedfields |= cursor;
                possibleHash ^= zobristRandomGreen[tmpindex]
                        ^ zobristRandomRed[tmpindex];
                tmpindex -= 9;
                cursor = (cursor >>> 9);
            }while((cursor & green) != 0);
            if((cursor & red) != 0){
                changedfields |= possiblychangedfields;
                hash ^= possibleHash;
            }
        }
        hash ^= zobristRandomRed[index];
        return changedfields;
    }

    private final static long getflippedDiskGreenHash(long red, long green, final long coord){
        long cursor;
        long possiblychangedfields = 0;
        long possibleHash = 0;
        long changedfields = 0;
        int index = Long.numberOfTrailingZeros(coord);
        int tmpindex = index + 8;

        // upshift
        cursor = (coord << 8) & red;
        if(cursor != 0){
            do{
                possiblychangedfields |= cursor;
                possibleHash ^= zobristRandomGreen[tmpindex]
                        ^ zobristRandomRed[tmpindex];
                tmpindex += 8;
                cursor = (cursor << 8);
            }while((cursor & red) != 0);
            if((cursor & green) != 0){
                changedfields |= possiblychangedfields;
                hash ^= possibleHash;
            }
        }
        // downshift
        cursor = (coord >>> 8) & red;
        if(cursor != 0){
            tmpindex = index - 8;
            possiblychangedfields = 0;
            possibleHash = 0;
            do{
                possiblychangedfields |= cursor;
                possibleHash ^= zobristRandomGreen[tmpindex]
                        ^ zobristRandomRed[tmpindex];
                tmpindex -= 8;
                cursor = (cursor >>> 8);
            }while((cursor & red) != 0);
            if((cursor & green) != 0){
                changedfields |= possiblychangedfields;
                hash ^= possibleHash;
            }
        }

        // shift correction
        red &= shiftMask;

        // leftshift
        cursor = (coord << 1) & red;
        if(cursor != 0){
            tmpindex = index + 1;
            possiblychangedfields = 0;
            possibleHash = 0;
            do{
                possiblychangedfields |= cursor;
                possibleHash ^= zobristRandomGreen[tmpindex]
                        ^ zobristRandomRed[tmpindex];
                tmpindex += 1;
                cursor = (cursor << 1);
            }while((cursor & red) != 0);
            if((cursor & green) != 0){
                changedfields |= possiblychangedfields;
                hash ^= possibleHash;
            }
        }

        // rightshift
        cursor = (coord >>> 1) & red;
        if(cursor != 0){
            tmpindex = index - 1;
            possiblychangedfields = 0;
            possibleHash = 0;
            do{
                possiblychangedfields |= cursor;
                possibleHash ^= zobristRandomGreen[tmpindex]
                        ^ zobristRandomRed[tmpindex];
                tmpindex -= 1;
                cursor = (cursor >>> 1);
            }while((cursor & red) != 0);
            if((cursor & green) != 0){
                changedfields |= possiblychangedfields;
                hash ^= possibleHash;
            }
        }

        // upleftshift
        cursor = (coord << 9) & red;
        if(cursor != 0){
            tmpindex = index + 9;
            possiblychangedfields = 0;
            possibleHash = 0;
            do{
                possiblychangedfields |= cursor;
                possibleHash ^= zobristRandomGreen[tmpindex]
                        ^ zobristRandomRed[tmpindex];
                tmpindex += 9;
                cursor = (cursor << 9);
            }while((cursor & red) != 0);
            if((cursor & green) != 0){
                changedfields |= possiblychangedfields;
                hash ^= possibleHash;
            }
        }

        // uprightshift
        cursor = (coord << 7) & red;
        if(cursor != 0){
            tmpindex = index + 7;
            possiblychangedfields = 0;
            possibleHash = 0;
            do{
                possiblychangedfields |= cursor;
                possibleHash ^= zobristRandomGreen[tmpindex]
                        ^ zobristRandomRed[tmpindex];
                tmpindex += 7;
                cursor = (cursor << 7);
            }while((cursor & red) != 0);
            if((cursor & green) != 0){
                changedfields |= possiblychangedfields;
                hash ^= possibleHash;
            }
        }

        // downleftshift
        cursor = (coord >>> 7) & red;
        if(cursor != 0){
            tmpindex = index - 7;
            possiblychangedfields = 0;
            possibleHash = 0;
            do{
                possiblychangedfields |= cursor;
                possibleHash ^= zobristRandomGreen[tmpindex]
                        ^ zobristRandomRed[tmpindex];
                tmpindex -= 7;
                cursor = (cursor >>> 7);
            }while((cursor & red) != 0);
            if((cursor & green) != 0){
                changedfields |= possiblychangedfields;
                hash ^= possibleHash;
            }
        }

        // downrightshift
        cursor = (coord >>> 9) & red;
        if(cursor != 0){
            tmpindex = index - 9;
            possiblychangedfields = 0;
            possibleHash = 0;
            do{
                possiblychangedfields |= cursor;
                possibleHash ^= zobristRandomGreen[tmpindex]
                        ^ zobristRandomRed[tmpindex];
                tmpindex -= 9;
                cursor = (cursor >>> 9);
            }while((cursor & red) != 0);
            if((cursor & green) != 0){
                changedfields |= possiblychangedfields;
                hash ^= possibleHash;
            }
        }
        hash ^= zobristRandomGreen[index];
        return changedfields;
    }

    private static final byte getStable1EdgePieces(byte borderRed, byte borderGreen){
        byte borderRedCopy = borderRed;
        byte borderGreenCopy = borderGreen;
        byte emptyEdge = (byte) ~(borderRed | borderGreen);
        byte stable = 0;
        byte potentiallyStable = (byte) ((0xFF & (borderRed << 1) & borderGreen) >>> 1);
        emptyEdge = (byte) (emptyEdge << 1);
        while(potentiallyStable != 0){
            borderRed = (byte) (borderRed << 1);
            borderGreen = (byte) (borderGreen << 1);
            emptyEdge = (byte) (emptyEdge << 1);
            potentiallyStable = (byte) (potentiallyStable & borderGreen);
            stable |= potentiallyStable & emptyEdge;
            stable |= potentiallyStable & (borderRed << 1);
        }

        borderRed = borderRedCopy;
        borderGreen = borderGreenCopy;
        emptyEdge = (byte) ~(borderRed | borderGreen);
        potentiallyStable = (byte) ((((0xFF & borderRed) >>> 1) & borderGreen) << 1);
        emptyEdge = (byte) ((0xFF & emptyEdge) >>> 1);
        while(potentiallyStable != 0){
            borderRed = (byte) ((0xFF & borderRed) >>> 1);
            borderGreen = (byte) ((0xFF & borderGreen) >>> 1);
            emptyEdge = (byte) ((0xFF & emptyEdge) >>> 1);
            potentiallyStable = (byte) (potentiallyStable & borderGreen);
            stable |= potentiallyStable & emptyEdge;
            stable |= potentiallyStable & ((0xFF & borderRed) >>> 1);
        }
        return stable;
    }

    private static final byte getStable3EdgePieces(byte borderRed, byte borderGreen){
        byte emptyEdge = (byte) ~(borderRed | borderGreen);
        if(Integer.bitCount(emptyEdge) == 0){
            return borderRed;
        }
        byte stable = (byte) (borderRed & 1);
        byte potentiallyStable = (byte) (borderRed & 1);
        byte tempBoard;
        while(potentiallyStable != 0){
            tempBoard = (byte) (potentiallyStable << 1);
            potentiallyStable = (byte) (tempBoard & borderRed);
            stable |= potentiallyStable;
        }

        stable |= (byte) (borderRed & 0b10000000);
        potentiallyStable = (byte) (borderRed & 0b10000000);
        while(potentiallyStable != 0){
            tempBoard = (byte) ((potentiallyStable & 0xFF) >>> 1);
            potentiallyStable = (byte) (tempBoard & borderRed);
            stable |= potentiallyStable;
        }

        return stable;
    }

    private static final long getStableDisks(long red, long green){
        long current = 0, before = red | green, filled04 = EDGES, filled15 = VERTICALEDGES, filled26 = EDGES, filled37 = HORIZONTALEDGES;
        int i;
        for (i = 0; i < 8; i++){
            if((before & LINES15[i]) == LINES15[i]){
                filled15 |= LINES15[i];
            }
            if((before & LINES37[i]) == LINES37[i]){
                filled37 |= LINES37[i];
            }
        }
        for (i = 0; i < 11; i++){
            if((before & LINES04[i]) == LINES04[i]){
                filled04 |= LINES04[i];
            }
            if((before & LINES26[i]) == LINES26[i]){
                filled26 |= LINES26[i];
            }
        }

        while(current != before){
            before = current;
            current |= red
                    & ((current << 8) | (current >>> 8) | filled15) // 15
                    & ((current << 1) | (current >>> 1) | filled37) // 37
                    & ((current << 9) | (current >>> 9) | filled04) // 04
                    & ((current << 7) | (current >>> 7) | filled26); // 26
        }
        return current;
    }

    private static final long getStableDisks(final long red, final long green, final boolean player){
        long current = 0, before = red | green, filled04 = EDGES, filled15 = VERTICALEDGES, filled26 = EDGES, filled37 = HORIZONTALEDGES;

        int i;
        for (i = 0; i < 8; i++){
            if((before & LINES15[i]) == LINES15[i]){
                filled15 |= LINES15[i];
            }
            if((before & LINES37[i]) == LINES37[i]){
                filled37 |= LINES37[i];
            }
        }
        for (i = 0; i < 11; i++){
            if((before & LINES04[i]) == LINES04[i]){
                filled04 |= LINES04[i];
            }
            if((before & LINES26[i]) == LINES26[i]){
                filled26 |= LINES26[i];
            }
        }

        while(current != before){
            before = current;
            current |= red
                    & ((current << 8) | (current >>> 8) | filled15) // 15
                    & ((current << 1) | (current >>> 1) | filled37) // 37
                    & ((current << 9) | (current >>> 9) | filled04) // 04
                    & ((current << 7) | (current >>> 7) | filled26); // 26
        }
        return current;
    }

    private static final byte getUnanchoredStableEdgePieces(byte borderRed, byte unstableGreen){
        return (byte) (((((borderRed << 1) & unstableGreen) >>> 2) & unstableGreen) << 1);
    }

    private static final byte getUnstableEdgePieces(byte borderRed, byte borderGreen){
        byte emptyEdge = (byte) ~(borderRed | borderGreen);
        byte potentiallyUnstable = (byte) ((borderRed << 1) & emptyEdge);
        byte unstable = (byte) (((potentiallyUnstable >>> 2) & borderGreen) << 1);
        potentiallyUnstable = (byte) ((borderRed >>> 1) & emptyEdge);
        unstable |= (((potentiallyUnstable << 2) & borderGreen) >>> 1);
        return (byte) unstable;
    }

    private final static void insertionSortAsc(int[] orderedScore, int[] orderedIndices, int size){
        for (int i = 1; i < size; i++){
            int j;
            int tempScore = orderedScore[i];
            int tempIndex = orderedIndices[i];
            for (j = i - 1; j >= 0 && tempScore < orderedScore[j]; j--){
                orderedScore[j + 1] = orderedScore[j];
                orderedIndices[j + 1] = orderedIndices[j];
            }
            j++;
            orderedScore[j] = tempScore;
            orderedIndices[j] = tempIndex;
        }
    }

    private final static void insertionSortDesc(int[] orderedScore, int[] orderedIndices, int size){
        for (int i = 1; i < size; i++){
            int j;
            int tempScore = orderedScore[i];
            int tempIndex = orderedIndices[i];
            for (j = i - 1; j >= 0 && tempScore > orderedScore[j]; j--){
                orderedScore[j + 1] = orderedScore[j];
                orderedIndices[j + 1] = orderedIndices[j];
            }
            j++;
            orderedScore[j] = tempScore;
            orderedIndices[j] = tempIndex;
        }
    }

    /**
     * Converts a long coordinates bitboard-representation into its equivalent
     * {@link Coordinates} object.
     * 
     * @param coords
     *            the coordinates to convert
     * @return the converted coordinates
     */
    private static final Coordinates longToCoordinates(final long coords){
        if(coords == 0){
            return null;
        }
        return new Coordinates(1 + (Long.numberOfLeadingZeros(coords) >>> 3), 1 + Long.numberOfLeadingZeros(coords) % 8);
    }

    private static final long possibleMovesRed(final long red, long green){
        long emptyFields = ~(red | green);
        long validMoves = 0;
        long potentialMoves;

        // upshift
        potentialMoves = (((red << 8) & green) << 8);
        while(potentialMoves != 0){
            validMoves |= (potentialMoves & emptyFields);
            potentialMoves = (potentialMoves & green) << 8;
        }

        // downshift
        potentialMoves = (((red >>> 8) & green) >>> 8);
        while(potentialMoves != 0){
            validMoves |= (potentialMoves & emptyFields);
            potentialMoves = (potentialMoves & green) >>> 8;
        }

        green &= shiftMask;
        // leftshift
        potentialMoves = (((red << 1) & green) << 1);
        while(potentialMoves != 0){
            validMoves |= (potentialMoves & emptyFields);
            potentialMoves = (potentialMoves & green) << 1;
        }
        // rightshift
        potentialMoves = (((red >>> 1) & green) >>> 1);
        while(potentialMoves != 0){
            validMoves |= (potentialMoves & emptyFields);
            potentialMoves = (potentialMoves & green) >>> 1;
        }

        // upleftshift
        potentialMoves = (((red << 9) & green) << 9);
        while(potentialMoves != 0){
            validMoves |= (potentialMoves & emptyFields);
            potentialMoves = (potentialMoves & green) << 9;
        }
        // uprightshift
        potentialMoves = (((red << 7) & green) << 7);
        while(potentialMoves != 0){
            validMoves |= (potentialMoves & emptyFields);
            potentialMoves = (potentialMoves & green) << 7;
        }
        // downleftshift
        potentialMoves = (((red >>> 7) & green) >>> 7);
        while(potentialMoves != 0){
            validMoves |= (potentialMoves & emptyFields);
            potentialMoves = (potentialMoves & green) >>> 7;
        }
        // downrightshift
        potentialMoves = (((red >>> 9) & green) >>> 9);
        while(potentialMoves != 0){
            validMoves |= (potentialMoves & emptyFields);
            potentialMoves = (potentialMoves & green) >>> 9;
        }
        return validMoves;
    }

    private static final void print(final long red, final long green){
        for (byte i = 0; i < 8; i++){
            for (byte k = 0; k < 8; k++){
                if(get(red, k, i))
                    System.out.print("1 ");
                else if(get(green, k, i))
                    System.out.print("2 ");
                else
                    System.out.print("0 ");
            }
            System.out.println("");
        }
    }

    private static Bitboard            testgb      = new Bitboard();
    private static AlphaBetaNoCloneing validsearch = new AlphaBetaNoCloneing();

    private final static int pvsMax(long red, long green, int alpha, int beta, int depth, long hash, int stonesOnBoard){
        searchedNodes++;
        if(returnFromSearch){
            return beta;
        }
        else if(System.nanoTime() >= localDeadline){
            returnFromSearch = true;
            return beta;
        }

        // Transposition Query
        int TTindex = TTget(hash);
        if(TTindex != NOTFOUND){
            if(TTdepths[TTindex] >= depth){
                byte TTtype = TTtypes[TTindex];
                if(TTtype == PVNODE){
                    return TTvalues[TTindex];
                }
                short TTvalue = TTvalues[TTindex];
                if(TTvalue >= beta && TTtype == CUTNODE){
                    return beta;
                }
                if(TTvalue <= alpha){
                    return alpha;
                }
            }
        }
        if(depth < 6){
            return pvsNearLeavesMax(red, green, alpha, beta, depth, false);
        }
        // Check for end of game
        long possibleMovesRed = possibleMovesRed(red, green);
        if(possibleMovesRed == 0){
            if(Bitboard.possibleMovesRed(green, red) == 0){
                int stonesRed = Long.bitCount(red);
                int stonesGreen = Long.bitCount(green);
                if(stonesRed > stonesGreen){
                    searchedNodes++;
                    return 30000 + stonesRed - stonesGreen;
                }else if(stonesRed < stonesGreen){
                    searchedNodes++;
                    return -30000 - stonesGreen + stonesRed;
                }else{
                    searchedNodes++;
                    return 0;
                }
            }
            return pvsMin(red, green, alpha, beta, depth - 1, hash, stonesOnBoard);
        }

        // evaluate if depth == 0 reached
        // if(depth <= 0){
        // long possibleMovesGreen = possibleMovesRed(green, red);
        // return evaluate(red, green, possibleMovesRed, possibleMovesGreen);
        // }

        int bestvalue = alpha;
        int value = alpha;
        if(sortMovesDesc(red, green, possibleMovesRed, depth, hash)){
            int index = Sindices[depth][0];
            byte TTnodeType = ALLNODE;
            value = pvsMin(SboardsRed[depth][index], SboardsGreen[depth][index], alpha, beta, depth - 1, Shashes[depth][index], stonesOnBoard + 1);
            if(returnFromSearch){
                return beta;
            }
            if(value > alpha){
                alpha = value;
                if(alpha >= beta){
                    TTput(hash, (short) alpha, (byte) depth, CUTNODE, (byte) stonesOnBoard);
                    return beta;
                }
                TTnodeType = PVNODE;
            }
            for (int i = 1; i < Ssize[depth]; i++){
                index = Sindices[depth][i];
                value = pvsMin(SboardsRed[depth][index], SboardsGreen[depth][index], alpha, alpha + 1, depth - 1, Shashes[depth][index], stonesOnBoard + 1);
                if(returnFromSearch){
                    return beta;
                }
                if(value > alpha){
                    value = pvsMin(SboardsRed[depth][index], SboardsGreen[depth][index], alpha, beta, depth - 1, Shashes[depth][index], stonesOnBoard + 1);
                    if(returnFromSearch){
                        return beta;
                    }
                    alpha = value;
                    if(alpha >= beta){
                        TTput(hash, (short) alpha, (byte) depth, CUTNODE, (byte) stonesOnBoard);
                        return beta;
                    }
                    TTnodeType = PVNODE;
                }
            }
            TTput(hash, (short) alpha, (byte) depth, TTnodeType, (byte) stonesOnBoard);
            return alpha;
        }
        else{
            byte TTnodeType = ALLNODE;
            for (int i = 0; i < Ssize[depth]; i++){
                int index = Sindices[depth][i];
                value = pvsMin(SboardsRed[depth][index], SboardsGreen[depth][index], bestvalue, beta, depth - 1, Shashes[depth][index], stonesOnBoard + 1);
                if(returnFromSearch){
                    return beta;
                }
                if(value > bestvalue){
                    if(value >= beta){
                        TTput(hash, (short) value, (byte) depth, CUTNODE, (byte) stonesOnBoard);
                        return beta;
                    }
                    TTnodeType = PVNODE;
                    bestvalue = value;
                }
            }
            TTput(hash, (short) bestvalue, (byte) depth, TTnodeType, (byte) stonesOnBoard);
            return bestvalue;
        }
    }

    private final static int pvsMin(long red, long green, int alpha, int beta, int depth, long hash, int stonesOnBoard){
        searchedNodes++;
        if(returnFromSearch){
            return alpha;
        }
        else if(System.nanoTime() >= localDeadline){
            returnFromSearch = true;
            return alpha;
        }

        // Transposition Query
        int TTindex = TTget(hash);
        if(TTindex != NOTFOUND){
            if(TTdepths[TTindex] >= depth){
                byte TTtype = TTtypes[TTindex];
                if(TTtype == PVNODE){
                    return TTvalues[TTindex];
                }
                short TTvalue = TTvalues[TTindex];
                if(TTvalue <= alpha && TTtype == CUTNODE){
                    return alpha;
                }
                if(TTvalue >= beta){
                    return beta;
                }
            }
        }
        if(depth < 6){
            return pvsNearLeavesMin(red, green, alpha, beta, depth, false);
        }
        // Check for end of game
        long possibleMovesGreen = possibleMovesRed(green, red);
        if(possibleMovesGreen == 0){
            if(possibleMovesRed(red, green) == 0){
                int stonesRed = Long.bitCount(red);
                int stonesGreen = Long.bitCount(green);
                if(stonesRed > stonesGreen){
                    searchedNodes++;
                    return 30000 + stonesRed - stonesGreen;
                }else if(stonesRed < stonesGreen){
                    searchedNodes++;
                    return -30000 - stonesGreen + stonesRed;
                }else{
                    searchedNodes++;
                    return 0;
                }
            }
            return pvsMax(red, green, alpha, beta, depth - 1, hash, stonesOnBoard);
        }

        // evaluate if depth == 0 reached
        // if(depth <= 0){
        // long possibleMovesRed = possibleMovesRed(red, green);
        // return evaluate(red, green, possibleMovesRed, possibleMovesGreen);
        // }

        int bestvalue = beta;
        int value = beta;
        if(sortMovesAsc(red, green, possibleMovesGreen, depth, hash)){
            byte TTnodeType = ALLNODE;
            int index = Sindices[depth][0];
            value = pvsMax(SboardsRed[depth][index], SboardsGreen[depth][index], alpha, beta, depth - 1, Shashes[depth][index], stonesOnBoard + 1);
            if(returnFromSearch){
                return alpha;
            }
            if(value < beta){
                beta = value;
                if(beta <= alpha){
                    TTput(hash, (short) beta, (byte) depth, CUTNODE, (byte) stonesOnBoard);
                    return alpha;
                }
                TTnodeType = PVNODE;
            }
            for (int i = 1; i < Ssize[depth]; i++){
                index = Sindices[depth][i];
                value = pvsMax(SboardsRed[depth][index], SboardsGreen[depth][index], beta - 1, beta, depth - 1, Shashes[depth][index], stonesOnBoard);
                if(returnFromSearch){
                    return alpha;
                }
                if(value < beta){
                    value = pvsMax(SboardsRed[depth][index], SboardsGreen[depth][index], alpha, beta, depth - 1, Shashes[depth][index], stonesOnBoard);
                    if(returnFromSearch){
                        return alpha;
                    }
                    beta = value;
                    if(beta <= alpha){
                        TTput(hash, (short) beta, (byte) depth, CUTNODE, (byte) stonesOnBoard);
                        return alpha;
                    }
                    TTnodeType = PVNODE;
                }
            }
            TTput(hash, (short) beta, (byte) depth, TTnodeType, (byte) stonesOnBoard);
            return beta;
        }
        else{
            byte TTnodeType = ALLNODE;
            for (int i = 0; i < Ssize[depth]; i++){
                int index = Sindices[depth][i];
                value = pvsMax(SboardsRed[depth][index], SboardsGreen[depth][index], alpha, bestvalue, depth - 1, Shashes[depth][index], stonesOnBoard + 1);
                if(returnFromSearch){
                    return alpha;
                }
                if(value < bestvalue){
                    if(value <= alpha){
                        TTput(hash, (short) value, (byte) depth, CUTNODE, (byte) stonesOnBoard);
                        return alpha;
                    }
                    TTnodeType = PVNODE;
                    bestvalue = value;
                }
            }
            TTput(hash, (short) bestvalue, (byte) depth, TTnodeType, (byte) stonesOnBoard);
            return bestvalue;
        }
    }

    private final static int pvsNearLeavesMax(long red, long green, int alpha, int beta, int depth, boolean passed){
        searchedNodes++;
        if(returnFromSearch){
            return beta;
        }
        else if(System.nanoTime() >= localDeadline){
            returnFromSearch = true;
            return beta;
        }

        // evaluate if depth == 0 reached
        if(depth <= 0){
            long possibleMovesRed = possibleMovesRed(red, green);
            long possibleMovesGreen = possibleMovesRed(green, red);
            return evaluate(red, green, possibleMovesRed, possibleMovesGreen);
        }

        long emptyfields = ~(red | green);
        long changedfields = 0;
        long coord = 0;
        int bestvalue = alpha;
        int value = alpha;
        boolean nomoveavailable = true;
        while(emptyfields != 0){
            coord = Long.highestOneBit(emptyfields);
            changedfields = getFlippedDiskRed(red, green, coord);
            if(changedfields != 0){
                value = pvsNearLeavesMin(red ^ changedfields ^ coord, green ^ changedfields, bestvalue, beta, depth - 1, false);
                if(value > bestvalue){
                    if(value >= beta){
                        return beta;
                    }
                    bestvalue = value;
                }
                nomoveavailable = false;
            }
            emptyfields ^= coord;
        }
        if(nomoveavailable){
            if(passed){
                return Long.bitCount(red) - Long.bitCount(green);
            }
            return pvsNearLeavesMin(red, green, alpha, beta, depth - 1, true);
        }
        return bestvalue;
    }

    private final static int pvsNearLeavesMin(long red, long green, int alpha, int beta, int depth, boolean passed){
        searchedNodes++;
        if(returnFromSearch){
            return alpha;
        }
        else if(System.nanoTime() >= localDeadline){
            returnFromSearch = true;
            return alpha;
        }

        // evaluate if depth == 0 reached
        if(depth <= 0){
            long possibleMovesGreen = possibleMovesRed(green, red);
            long possibleMovesRed = possibleMovesRed(red, green);
            return evaluate(red, green, possibleMovesRed, possibleMovesGreen);
        }
        long emptyfields = ~(red | green);
        long changedfields = 0;
        long coord = 0;
        int bestvalue = beta;
        int value = beta;
        boolean nomoveavailable = true;
        while(emptyfields != 0){
            coord = Long.highestOneBit(emptyfields);
            changedfields = getFlippedDiskRed(green, red, coord);
            if(changedfields != 0){
                value = pvsNearLeavesMax(red ^ changedfields, green ^ changedfields ^ coord, alpha, bestvalue, depth - 1, false);
                if(value < bestvalue){
                    if(value <= alpha){
                        return alpha;
                    }
                    bestvalue = value;
                }
                nomoveavailable = false;
            }
            emptyfields ^= coord;
        }
        if(nomoveavailable){
            if(passed){
                return Long.bitCount(red) - Long.bitCount(green);
            }
            return pvsNearLeavesMax(red, green, alpha, beta, depth - 1, true);
        }
        return bestvalue;

    }

    private final static long pvsSearch(long red, long green, int depth){
        long possibleMovesRed = Bitboard.possibleMovesRed(red, green);
        if(possibleMovesRed == 0){
            return 0;
        }
        int alpha = -INFINITY;
        int value = -INFINITY;
        int beta = INFINITY;
        sortMovesDesc(red, green, possibleMovesRed, depth, generateZobristHash(red, green));
        int index = pvsFirstMoveIndex;
        long bestmove = Smoves[depth][index];
        alpha = pvsMin(SboardsRed[depth][index], SboardsGreen[depth][index], alpha, beta, depth - 1, Shashes[depth][index], playedStones + 1);
        if(returnFromSearch){
            return bestmove;
        }
        int alreadyCheckedIndex = index;
        for (int i = 0; i < Ssize[depth]; i++){
            index = Sindices[depth][i];
            if(index == alreadyCheckedIndex){
                continue;
            }
            value = pvsMin(SboardsRed[depth][index], SboardsGreen[depth][index], alpha, alpha + 1, depth - 1, Shashes[depth][index], playedStones + 1);
            if(returnFromSearch){
                resultOfSearch = alpha;
                return bestmove;
            }
            if(value > alpha){
                value = pvsMin(SboardsRed[depth][index], SboardsGreen[depth][index], alpha, beta, depth - 1, Shashes[depth][index], playedStones + 1);
                alpha = value;
                pvsFirstMoveIndex = index;
                bestmove = Smoves[depth][index];
            }
        }
        if(alpha != -INFINITY){
            resultOfSearch = alpha;
        }
        return bestmove;
    }

    private static final long[] serializeBitboard(long bitboard){
        int bitcount = Long.bitCount(bitboard);
        long tmp;
        long[] bitboards = new long[bitcount];
        for (int i = 0; i < bitcount; i++){
            tmp = Long.highestOneBit(bitboard);
            bitboards[i] = tmp;
            bitboard ^= tmp;
        }
        return bitboards;
    }

    private static final boolean sortMovesAsc(long red, long green, long possibleMoves, int depth, long oldHash){
        int i = 0;
        boolean hasPVorCutNode = false;
        while(possibleMoves != 0){
            long move = Long.highestOneBit(possibleMoves);
            possibleMoves ^= move;
            hash = oldHash;
            long flippedDisks = getflippedDiskGreenHash(red, green, move);
            SboardsRed[depth][i] = red ^ flippedDisks;
            SboardsGreen[depth][i] = green ^ flippedDisks ^ move;
            Smoves[depth][i] = move;
            Shashes[depth][i] = hash;

            int index = TTget(hash);
            if(index == NOTFOUND){
                Svalues[depth][i] = 32767;
            }
            else{
                if(TTtypes[index] > CUTNODE){
                    hasPVorCutNode = true;
                }
                Svalues[depth][i] = TTvalues[index] - (TTtypes[index] << 15);
            }
            Sindices[depth][i] = i;
            i++;
        }
        insertionSortDesc(Svalues[depth], Sindices[depth], i);
        Ssize[depth] = i;
        return hasPVorCutNode;
    }

    /**
     * 
     * MOVE ORDERING
     * 
     */

    private static final boolean sortMovesDesc(long red, long green, long possibleMoves, int depth, long oldHash){
        int i = 0;
        boolean hasPVorCutNode = false;
        while(possibleMoves != 0){
            long move = Long.highestOneBit(possibleMoves);
            possibleMoves ^= move;
            hash = oldHash;
            long flippedDisks = getflippedDiskRedHash(red, green, move);
            SboardsRed[depth][i] = red ^ flippedDisks ^ move;
            SboardsGreen[depth][i] = green ^ flippedDisks;
            Smoves[depth][i] = move;
            Shashes[depth][i] = hash;

            int index = TTget(hash);
            if(index == NOTFOUND){
                Svalues[depth][i] = -32768;
            }
            else{
                if(TTtypes[index] > CUTNODE){
                    hasPVorCutNode = true;
                }
                Svalues[depth][i] = TTvalues[index] + (TTtypes[index] << 15);
            }
            Sindices[depth][i] = i;
            i++;
        }
        insertionSortDesc(Svalues[depth], Sindices[depth], i);
        Ssize[depth] = i;
        return hasPVorCutNode;
    }

    private final static int TTget(long key){
        int index = (int) (key & TTindexMask);
        if(TTkeys[index] == key){
            return index;
        }
        else{
            return NOTFOUND;
        }
    }

    private final static void TTput(long key, short value, byte depth, byte type, byte stonesOnBoard){
        int index = (int) (key & TTindexMask);
        if(TTkeys[index] == key){
            if(TTdepths[index] <= depth){
                TTvalues[index] = value;
                TTdepths[index] = depth;
                TTtypes[index] = type;
                TTplayedStones[index] = stonesOnBoard;
            }
        }
        else{
            if(TTplayedStones[index] < playedStones || TTtypes[index] < type){
                TTkeys[index] = key;
                TTvalues[index] = value;
                TTdepths[index] = depth;
                TTtypes[index] = type;
                TTplayedStones[index] = stonesOnBoard;
            }
        }
    }

    @Override
    public void initialize(int myColor, long timeLimit){
        Ben.myColor = myColor;
        Ben.timeLimit = timeLimit * 1000000;
        playedStones = (myColor == GameBoard.RED) ? (byte) 2 : (byte) 3;
        generateEdgeTable();
        validsearch.evaluator = new StrategicEvaluatorNoah();
    }

    @Override
    public Coordinates nextMove(GameBoard gb){
        globalDeadline = System.nanoTime() + timeLimit - 20000000L;
        playedStones += 2;
        long[] bitboard = convertToBitboard(gb);
        long red;
        long green;
        if(myColor == GameBoard.RED){
            red = bitboard[0];
            green = bitboard[1];
        }
        else{
            red = bitboard[1];
            green = bitboard[0];
        }
        pvsFirstMoveIndex = 0;
        searchedNodes = 0;
        returnFromSearch = false;
        long bestmove = 0;
        int depth = 0;
        boolean tryOutcomeSearch = false;
        System.out.println("-----------------Ben-----------------");
        System.out.println("Move Nr. " + playedStones);
        if(!useOutcomeSearch){
            if(playedStones > 39){
                tryOutcomeSearch = true;
                localDeadline = System.nanoTime() + timeLimit / 2;
            }
            else{
                localDeadline = globalDeadline;
            }
            while(!returnFromSearch && depth < 29){
                depth++;
                bestmove = pvsSearch(red, green, depth);
            }
            System.out.println("PVS SEARCH:");
            System.out.println("Searched Nodes: " + searchedNodes + " Depth: " + depth + " Evaluationresult: " + resultOfSearch + " Move: " + bestmove);
        }
        if(tryOutcomeSearch || useOutcomeSearch){
            localDeadline = globalDeadline;
            returnFromSearch = false;
            searchedNodes = 0;
            long coord = endGameSearch(red, green, -1, 1);
            System.out.println("OUTCOME SEARCH:");
            if(resultOfSearch != NOTFOUND){
                if(resultOfSearch != -1){
                    bestmove = coord;
                    if(resultOfSearch == 1){
                        useOutcomeSearch = true;
                        System.out.println("Outcome Search finished. I'll win.");
                    }
                    else{
                        useOutcomeSearch = true;
                        System.out.println("Outcome Search finished. The least result will be draw.");
                    }
                    System.out.println("Searched Nodes: " + searchedNodes);
                }
                else{
                    System.out.println("Outcome Search finished. I'll probably loose.");
                    System.out.println("Searched Nodes: " + searchedNodes);
                }
            }
            else{
                System.out.println("Outcome Search failed.");
                System.out.println("Searched Nodes: " + searchedNodes);
            }
        }

        if(useExactSearch || (useOutcomeSearch && !returnFromSearch)){
            searchedNodes = 0;
            long coord = endGameSearch(red, green, -64, +64);
            System.out.println("EXACT SEARCH: ");
            if(resultOfSearch != NOTFOUND){
                System.out.println("Tree solved. Result will be at least: " + resultOfSearch);
                useExactSearch = true;
                bestmove = coord;
            }
            else{
                System.out.println("Exact search failed.");
            }
            System.out.println("Searched Nodes: " + searchedNodes);
        }
        System.out.println("");
        return longToCoordinates(bestmove);
    }

    @Override
    public String getName(){
        return "Ben";
    }

    @Override
    public long getNodesCount(){
        return searchedNodes;
    }

    @Override
    public long getEvaluatedNodesCount(){
        return 0;
    }

    @Override
    public int getDepthOfLatestSearch(){
        return 0;
    }

    @Override
    public int getValueOfLatestSearch(){
        return resultOfSearch;
    }

    @Override
    public int getMoveNrOfLatestSearch(){
        return 0;
    }

    @Override
    public long getNrOfTTHits(){
        return 0;
    }
}
