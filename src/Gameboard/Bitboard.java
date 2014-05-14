package Gameboard;

import reversi.Coordinates;
import reversi.GameBoard;
import reversi.OutOfBoundsException;

/**
 * The Class {@code Bitboard} is an efficient implementation of 8x8 Gameboard
 * for reversi.
 * 
 */
public class Bitboard implements GameBoard{

    /**
     * Bitmask used for Leftshifts of the Bitboard.
     */
    private static final long   leftshiftMask      = 0xFEFEFEFEFEFEFEFEL;

    /**
     * Bitmask used for Rightshifts of the whole Bitboard.
     */
    private static final long   rightshiftMask     = 0x7F7F7F7F7F7F7F7FL;

    /**
     * Bitmask used for shifting correction.
     */
    private static final long   shiftMask          = 0x7E7E7E7E7E7E7E7EL;

    /**
     * 64 random {@code long} constants used to generate zobrist-hashes.
     */
    private static final long[] zobristRandomRed   = { 0xfbf185c26c378076L,
                                                   0x14fc57c338f4f1a5L,
                                                   0x925f95f86089b91dL,
                                                   0xf7532bd039d1a1f4L,
                                                   0xecfbaddab0d6fce0L,
                                                   0x2a8ca0dd2626b862L,
                                                   0x23d47c43ff36ce38L,
                                                   0x3e6baf923c28f448L,
                                                   0x20d9010265a3f40fL,
                                                   0x6388d91cddc1c1cL,
                                                   0xafa8e8696ed1738bL,
                                                   0x90bc3d08fc1ec9a2L,
                                                   0x36c017d07c49546bL,
                                                   0xf2ef8f4cac6794f1L,
                                                   0xf56bb2fcbdc8d2b1L,
                                                   0x7fa139f336fc98feL,
                                                   0x3e4731ff2edbd886L,
                                                   0x2341ecdb9dadb92bL,
                                                   0x715f9e7d5c37bf11L,
                                                   0xeae7498f80a35fa8L,
                                                   0xcbfb8bf37d9c9a6aL,
                                                   0x6519cc1b281cd98eL,
                                                   0x1220e41bc588fd4dL,
                                                   0xbf7683bbe19659e9L,
                                                   0x627d82e87a1a5c8fL,
                                                   0x66bb352a4233da01L,
                                                   0xb118e976734e752dL,
                                                   0x45059a12dd803309L,
                                                   0xde761d1d4fee1963L,
                                                   0x77079d25f4eacf3aL,
                                                   0x6d9a57385cee69cfL,
                                                   0xb1effad871ebfc7eL,
                                                   0x1a0140e9b3b4f9b1L,
                                                   0x3717a23db835e737L,
                                                   0x79c6039fc1149b5L,
                                                   0x5232f65d4634ab04L,
                                                   0xa7382a6bc45005a0L,
                                                   0x84df5634e6026379L,
                                                   0x49fb2991a7b8d155L,
                                                   0xbe66b547b3145c54L,
                                                   0x5a285e00e8e52ce9L,
                                                   0xb0023175a5dfd33L,
                                                   0x18eb9696bfc766ceL,
                                                   0x6898f099c66dfdcbL,
                                                   0x3c5f157a41176274L,
                                                   0x330c473b052343acL,
                                                   0xe331484b0c5eea6bL,
                                                   0xc8ec9114c856a3cbL,
                                                   0x77ea0c91db3b5df9L,
                                                   0x8fd55922b8489683L,
                                                   0x3006266598dc8ceL,
                                                   0x441c36d33f203effL,
                                                   0x34cc29731c5d4a3dL,
                                                   0x553bbe5df8f53190L,
                                                   0x17ea7878ae609e80L,
                                                   0x199cdc8e23095b40L,
                                                   0xc048ab76910b2c5aL,
                                                   0x5d11eefe79c29638L,
                                                   0x658cfdf9d3598681L,
                                                   0x66fa01853fe29ec3L,
                                                   0x8485961d728f09b5L,
                                                   0xcc1281bbe4c0cc6aL,
                                                   0x4d16571ec4e48e15L,
                                                   0x957c6ae1831d84faL };

    /**
     * 64 random {@code long} constants used to generate zobrist-hashes.
     */
    private static final long[] zobristrandomGreen = { 0xc5bb837cfc908843L,
                                                   0xfe69cb281253ce62L,
                                                   0x683f782b737295f4L,
                                                   0xfa35ce4ae312051eL,
                                                   0x42c73b38abd54779L,
                                                   0xa6bcc139d081d3ecL,
                                                   0x15ba9b28ba7956b0L,
                                                   0x6283347aa7b70f62L,
                                                   0x2fe227cbe0394798L,
                                                   0xf9e9f51b8c1bcd62L,
                                                   0xa499cfb3401dae88L,
                                                   0xaf44f6d6cc626537L,
                                                   0xdd8b04996dc30640L,
                                                   0xc8eff63264fb72e4L,
                                                   0x73167782e4db58eaL,
                                                   0x7d92bad68f07579eL,
                                                   0xf6845abc389b5264L,
                                                   0x535602c1d9a6f48eL,
                                                   0x5919b13e0e5568f8L,
                                                   0x190a954c53a004daL,
                                                   0xda2d619f9dbae31dL,
                                                   0x11365e1683ffb279L,
                                                   0x84f41aa83ce66e8eL,
                                                   0x1c736a9c06c3f22aL,
                                                   0xfba6792597e7ffbL,
                                                   0x21c609e23d070278L,
                                                   0x411f440ed2f335eaL,
                                                   0x8accc44df185fac1L,
                                                   0xb4696c44a66f0ab6L,
                                                   0xc0a65c0f2aadc6e9L,
                                                   0xee19d03419fe3211L,
                                                   0x1429b01059ebf8e5L,
                                                   0x4f88106b9ba1df0aL,
                                                   0x1db20740fe57146bL,
                                                   0x31a6735b4c7ed329L,
                                                   0x585b8735e1b17690L,
                                                   0xbfa84f02a44a5049L,
                                                   0x63171824e0effc88L,
                                                   0x631b3be1593ed5bL,
                                                   0xf1de88b282db9797L,
                                                   0xfe8d2d1068b805eeL,
                                                   0x50cb563ecbeb5579L,
                                                   0x63a11efffddd4fcdL,
                                                   0x809e06c8b3685961L,
                                                   0x5e4ce0db3a503bacL,
                                                   0x44d3e0caf0edac32L,
                                                   0x91853bc5766968dfL,
                                                   0xc2f3279e4b68386fL,
                                                   0x9a198dcd2f4d33acL,
                                                   0x34374ee42f26df21L,
                                                   0xc22fcb599ce87fe0L,
                                                   0xd70fd9d9fa5ab8acL,
                                                   0xd481fdc67f3a56eeL,
                                                   0xb175cfd246232343L,
                                                   0x5224d059741530cL,
                                                   0xf7af6dd1d43b0ebcL,
                                                   0xfb3305942dec8e66L,
                                                   0x11e08678051046b9L,
                                                   0x971dcb32e7d6ecd4L,
                                                   0x30873bfc1c5ef92dL,
                                                   0xc7cfaac0a8ac0dcbL,
                                                   0xbc4e99466f54709aL,
                                                   0x5ac95c64d48d5682L,
                                                   0x455000a14de5437L };

    /**
     * This variable is used by {@code hashCode()} to incrementally calculate
     * zobrist-hashes.
     */
    public long                 hash;

    /**
     * Representation of all the red stones on the board. the MSB represents
     * upper left corner.
     * Bit nr. 55 lies one row below upper left corner.
     */
    public long                 red                = 0;

    /**
     * Representation of all the green stones on the board. the MSB represents
     * upper left corner.
     * Bit nr. 55 lies one row below upper left corner.
     */
    public long                 green              = 0;

    /**
     * Creates a new, empty {@link Bitboard}.
     */
    public Bitboard(){
    }

    /**
     * Creates a new Bitboard with parameters {@code red} and {@code green}
     * representing the red and green stones.
     * parameters aren't checked for validity.
     * 
     * @param red
     *            all the red stones
     * @param green
     *            all the green stones
     */
    public Bitboard(long red, long green){
        this.red = red;
        this.green = green;
        hash = generateZobristHash();
    }

    /**
     * Converts a {@code GameBoard} into a {@code Bitboard}.
     * This method has to be used carefully since it affords a lot of
     * calculation time!
     * 
     * @param gb
     *            the {@link GameBoard} to be converted
     * @return the converted GameBoard as Bitboard
     */
    public static Bitboard convert(GameBoard gb){
        long red = 0;
        long green = 0;
        Coordinates coord;
        try{
            for (int i = 1; i < 9; i++){
                for (int j = 1; j < 9; j++){
                    coord = new Coordinates(i, j);
                    int occupation = gb.getOccupation(coord);
                    if(occupation != GameBoard.EMPTY){
                        if(occupation == GameBoard.RED){
                            red |= Bitboard.coordinatesToLong(coord);
                        }
                        else{
                            green |= Bitboard.coordinatesToLong(coord);
                        }
                    }
                }
            }
        }catch(OutOfBoundsException e){
        }
        return new Bitboard(red, green);
    }

    /**
     * Converts {@code coords} to the long equivalent in the
     * bitboard-represenation
     * 
     * @param coords
     *            the {@link Coordinates} object that is to be converted
     * @return the converted coordinates.
     */
    public static long coordinatesToLong(Coordinates coords){
        if(coords == null){
            return 0;
        }
        return 1L << (8 - coords.getCol() + 64 - 8 * coords.getRow());
    }

    /**
     * Masks all the adjacent fields of all set bits in the bitboard
     * 
     * @param bitboard
     *            of which the neighbor cells should be calculated
     * @return a new bitboard with just the newly set bits; without the masked
     *         bits
     */
    public static long fillAdjacent(long bitboard){
        long filledbitboard = bitboard;
        filledbitboard |= filledbitboard >>> 1 & 0x7f7f7f7f7f7f7f7fL;
        filledbitboard |= filledbitboard >>> 8;
        filledbitboard |= filledbitboard << 1 & 0xfefefefefefefefeL;
        filledbitboard |= filledbitboard << 8;
        return filledbitboard ^ bitboard;
    }

    public final static long getflippedDiskRed(long red, long green, final long coord){
        long cursor;
        long possiblychangedfields = 0;
        long changedfields = 0;
        // if ((coord & (green|red)) != 0) {
        // return 0;
        // }

        // upshift
        cursor = (coord << 8) & green;
        if(cursor != 0){
            possiblychangedfields = 0;
            do{
                possiblychangedfields |= cursor;
                cursor = (cursor << 8);
            }while((cursor & green) != 0);
            if((cursor & red) != 0){
                changedfields |= possiblychangedfields;
            }
        }
        // downshift
        cursor = (coord >>> 8) & green;
        if(cursor != 0){
            possiblychangedfields = 0;
            do{
                possiblychangedfields |= cursor;
                cursor = (cursor >>> 8);
            }while((cursor & green) != 0);
            if((cursor & red) != 0){
                changedfields |= possiblychangedfields;
            }
        }

        // shift correction
        green &= shiftMask;

        // leftshift
        cursor = (coord << 1) & green;
        if(cursor != 0){
            possiblychangedfields = 0;
            do{
                possiblychangedfields |= cursor;
                cursor = (cursor << 1);
            }while((cursor & green) != 0);
            if((cursor & red) != 0){
                changedfields |= possiblychangedfields;
            }
        }

        // rightshift
        cursor = (coord >>> 1) & green;
        if(cursor != 0){
            possiblychangedfields = 0;
            do{
                possiblychangedfields |= cursor;
                cursor = (cursor >>> 1);
            }while((cursor & green) != 0);
            if((cursor & red) != 0){
                changedfields |= possiblychangedfields;
            }
        }

        // upleftshift
        cursor = (coord << 9) & green;
        if(cursor != 0){
            possiblychangedfields = 0;
            do{
                possiblychangedfields |= cursor;
                cursor = (cursor << 9);
            }while((cursor & green) != 0);
            if((cursor & red) != 0){
                changedfields |= possiblychangedfields;
            }
        }

        // uprightshift
        cursor = (coord << 7) & green;
        if(cursor != 0){
            possiblychangedfields = 0;
            do{
                possiblychangedfields |= cursor;
                cursor = (cursor << 7);
            }while((cursor & green) != 0);
            if((cursor & red) != 0){
                changedfields |= possiblychangedfields;
            }
        }

        // downleftshift
        cursor = (coord >>> 7) & green;
        if(cursor != 0){
            possiblychangedfields = 0;
            do{
                possiblychangedfields |= cursor;
                cursor = (cursor >>> 7);
            }while((cursor & green) != 0);
            if((cursor & red) != 0){
                changedfields |= possiblychangedfields;
            }
        }

        // downrightshift
        cursor = (coord >>> 9) & green;
        if(cursor != 0){
            possiblychangedfields = 0;
            do{
                possiblychangedfields |= cursor;
                cursor = (cursor >>> 9);
            }while((cursor & green) != 0);
            if((cursor & red) != 0){
                changedfields |= possiblychangedfields;
            }
        }

        return changedfields;
    }

    /**
     * Converts a long coordinates bitboard-representation into its equivalent
     * {@link Coordinates} object.
     * 
     * @param coords
     *            the coordinates to convert
     * @return the converted coordinates
     */
    public static Coordinates longToCoordinates(long coords){
        if(coords == 0){
            return null;
        }
        return new Coordinates(1 + (Long.numberOfLeadingZeros(coords) >>> 3), 1 + Long.numberOfLeadingZeros(coords) % 8);
    }

    public final static long possibleMovesRed(final long red, long green){
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

    /**
     * Creates an Array of long values with one single bit set each.
     * 
     * @param bitboard
     *            the bitboard to serialize.
     * @return the serialized long variable; {@code null} if bitboard is 0
     */
    public static long[] serializeBitboard(long bitboard){
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

    @Override
    public boolean checkMove(int player, Coordinates coords){
        return ((coordinatesToLong(coords) & getPossibleMoves(player == RED)) != 0);
    }

    @Override
    public GameBoard clone(){
        return new Bitboard(this.red, this.green);
    }

    /**
     * @return a copy of the Bitboard
     */

    public Bitboard copy(){
        return new Bitboard(this.red, this.green);
    }

    /**
     * Executes the given move on a copy of {@code this} aus. Upon execution the
     * zobrist hash is refreshed. If the hash is not used on the
     * {@code Bitboard}, {@link Bitboard#makeMove(boolean, long) makeMove}
     * should be executed on a copied bitboard
     * 
     * @param player
     *            the player in charge. Use {@code true} for red or
     *            {@code false} for green.
     * @param coords
     *            the coordinates where to put a stone
     * @return a copy of the
     *         {@code Bitboard, on which the given move was executed}
     */
    public Bitboard copyAndMakeMove(boolean player, long coords){
        Bitboard gb = new Bitboard(this.red, this.green);
        refreshZobristHash(gb.makeMove(player, coords), coords, player);
        return gb;
    }

    public int countStones(boolean player){
        return Long.bitCount((player) ? red : green);
    }

    @Override
    public int countStones(int player){
        if(player == RED){
            return countStones(true);
        }
        else if(player == GREEN){
            return countStones(false);
        }
        else{
            return 0;
        }
    }

    public long generateZobristHash(){
        long value = 0;
        for (long bit : serializeBitboard(red)){
            value ^= zobristRandomRed[Long.numberOfTrailingZeros(bit)];
        }
        for (long bit : serializeBitboard(green)){
            value ^= zobristrandomGreen[Long.numberOfTrailingZeros(bit)];
        }
        return value;
    }

    /**
     * Generates an array containing all successor bitboards {@link Bitboards}.
     * Moves are not tested whether they are legal or not
     * 
     * @param player
     *            the Player to move
     * @param moves
     *            an array, containing all possible Moves
     * @return an array containing all successor boards
     */
    public Bitboard[] getbitboards(boolean player, long[] moves){
        Bitboard[] nextgameboards = new Bitboard[moves.length];
        for (int i = 0; i < nextgameboards.length; i++){
            nextgameboards[i] = ((Bitboard) this.clone());
            nextgameboards[i].makeMove(player, moves[i]);
        }
        return nextgameboards;
    }

    /**
     * @return the number of ALL stones on the field
     */

    public byte getDiscCount(){
        return (byte) (Long.bitCount(red) + Long.bitCount(green));
    }

    @Override
    public int getOccupation(Coordinates coords) throws OutOfBoundsException{
        long coordsAsLong = coordinatesToLong(coords);
        if((coordsAsLong & red) != 0){
            return RED;
        }
        else if((coordsAsLong & green) != 0){
            return GREEN;
        }
        else{
            return EMPTY;
        }
    }

    /**
     * @param player
     *            The player to move; use {@code true} for red or {@code false}
     *            for green.
     * @return returns a long containing all possible moves. If there is no
     *         legal moves 0 is returned
     */
    public long getPossibleMoves(boolean player){
        long emptyFields = ~(red | green);
        long validMoves = 0;
        long potentialMoves;
        long playerFields;
        long otherPlayerFields;
        if(player){
            playerFields = red;
            otherPlayerFields = green;
        }
        else{
            playerFields = green;
            otherPlayerFields = red;
        }
        // leftshift
        potentialMoves = (((playerFields << 1) & leftshiftMask & otherPlayerFields) << 1)
                & leftshiftMask;
        while(potentialMoves != 0){
            validMoves |= (potentialMoves & emptyFields);
            potentialMoves = (potentialMoves & otherPlayerFields) << 1
                    & leftshiftMask;
        }
        // rightshift
        potentialMoves = (((playerFields >>> 1) & rightshiftMask & otherPlayerFields) >>> 1)
                & rightshiftMask;
        while(potentialMoves != 0){
            validMoves |= (potentialMoves & emptyFields);
            potentialMoves = (potentialMoves & otherPlayerFields) >>> 1
                    & rightshiftMask;
        }
        // upshift
        potentialMoves = (((playerFields << 8) & otherPlayerFields) << 8);
        while(potentialMoves != 0){
            validMoves |= (potentialMoves & emptyFields);
            potentialMoves = (potentialMoves & otherPlayerFields) << 8;
        }
        // downshift
        potentialMoves = (((playerFields >>> 8) & otherPlayerFields) >>> 8);
        while(potentialMoves != 0){
            validMoves |= (potentialMoves & emptyFields);
            potentialMoves = (potentialMoves & otherPlayerFields) >>> 8;
        }
        // upleftshift
        potentialMoves = (((playerFields << 9) & leftshiftMask & otherPlayerFields) << 9)
                & leftshiftMask;
        while(potentialMoves != 0){
            validMoves |= (potentialMoves & emptyFields);
            potentialMoves = (potentialMoves & otherPlayerFields) << 9
                    & leftshiftMask;
        }
        // uprightshift
        potentialMoves = (((playerFields << 7) & rightshiftMask & otherPlayerFields) << 7)
                & rightshiftMask;
        while(potentialMoves != 0){
            validMoves |= (potentialMoves & emptyFields);
            potentialMoves = (potentialMoves & otherPlayerFields) << 7
                    & rightshiftMask;
        }
        // downleftshift
        potentialMoves = (((playerFields >>> 7) & leftshiftMask & otherPlayerFields) >>> 7)
                & leftshiftMask;
        while(potentialMoves != 0){
            validMoves |= (potentialMoves & emptyFields);
            potentialMoves = (potentialMoves & otherPlayerFields) >>> 7
                    & leftshiftMask;
        }
        // downrightshift
        potentialMoves = (((playerFields >>> 9) & rightshiftMask & otherPlayerFields) >>> 9)
                & rightshiftMask;
        while(potentialMoves != 0){
            validMoves |= (potentialMoves & emptyFields);
            potentialMoves = (potentialMoves & otherPlayerFields) >>> 9
                    & rightshiftMask;
        }
        return validMoves;
    }

    @Override
    public int getSize(){
        return 8;
    }

    /**
     * @return {@code true} if the game has finished; else {@code false}.
     */
    public boolean hasFinished(){
        return isMoveAvailable(RED) && isMoveAvailable(GREEN);
    }

    @Override
    public boolean isFull(){
        return (red & green) == 0xFFFFFFFFFFFFFFFFL;
    }

    @Override
    public boolean isMoveAvailable(int player){
        return getPossibleMoves(player == RED) == 0;
    }

    /**
     * Sets a stone on the given coordinates and turns all enemy stones
     * following the rules of othello.
     * It is not checked back whether the given move is legal or not!
     * Upon exectuing the zobrist hash is refreshed.
     * 
     * @param player
     *            the player in charge. Use {@code true} for red or
     *            {@code false} for green.
     * @param coords
     *            the coordinates where to put a stone
     * @return a bitmask containing all turned stones. Can be used to revert
     *         moves or to incrementally calculate zobrist hashes
     */
    public long makeMove(boolean player, long coords){
        long playerFields;
        long otherPlayerFields;
        long cursor;
        long possiblyChangedFields = 0;
        long changedFields = 0;
        if(coords == 0){
            return 0;
        }
        if(player){
            playerFields = red;
            otherPlayerFields = green;
        }
        else{
            playerFields = green;
            otherPlayerFields = red;
        }
        // leftshift
        cursor = coords << 1 & leftshiftMask;
        while(cursor != 0){
            cursor &= otherPlayerFields;
            possiblyChangedFields |= cursor;
            cursor = (cursor << 1) & leftshiftMask;
            if((cursor & playerFields) != 0){
                changedFields |= possiblyChangedFields;
                break;
            }
        }
        // rightshift
        possiblyChangedFields = 0;
        cursor = coords >>> 1 & rightshiftMask;
        while(cursor != 0){
            cursor &= otherPlayerFields;
            possiblyChangedFields |= cursor;
            cursor = (cursor >>> 1) & rightshiftMask;
            if((cursor & playerFields) != 0){
                changedFields |= possiblyChangedFields;
                break;
            }
        }
        // upshift
        possiblyChangedFields = 0;
        cursor = coords << 8;
        while(cursor != 0){
            cursor &= otherPlayerFields;
            possiblyChangedFields |= cursor;
            cursor = (cursor << 8);
            if((cursor & playerFields) != 0){
                changedFields |= possiblyChangedFields;
                break;
            }
        }
        // downshift
        possiblyChangedFields = 0;
        cursor = coords >>> 8;
        while(cursor != 0){
            cursor &= otherPlayerFields;
            possiblyChangedFields |= cursor;
            cursor = (cursor >>> 8);
            if((cursor & playerFields) != 0){
                changedFields |= possiblyChangedFields;
                break;
            }
        }
        // upleftshift
        possiblyChangedFields = 0;
        cursor = coords << 9 & leftshiftMask;
        while(cursor != 0){
            cursor &= otherPlayerFields;
            possiblyChangedFields |= cursor;
            cursor = (cursor << 9) & leftshiftMask;
            if((cursor & playerFields) != 0){
                changedFields |= possiblyChangedFields;
                break;
            }
        }
        // uprightshift
        possiblyChangedFields = 0;
        cursor = coords << 7 & rightshiftMask;
        while(cursor != 0){
            cursor &= otherPlayerFields;
            possiblyChangedFields |= cursor;
            cursor = (cursor << 7) & rightshiftMask;
            if((cursor & playerFields) != 0){
                changedFields |= possiblyChangedFields;
                break;
            }
        }
        // downleftshift
        possiblyChangedFields = 0;
        cursor = coords >>> 7 & leftshiftMask;
        while(cursor != 0){
            cursor &= otherPlayerFields;
            possiblyChangedFields |= cursor;
            cursor = (cursor >>> 7) & leftshiftMask;
            if((cursor & playerFields) != 0){
                changedFields |= possiblyChangedFields;
                break;
            }
        }
        // downrightshift
        possiblyChangedFields = 0;
        cursor = coords >>> 9 & rightshiftMask;
        while(cursor != 0){
            cursor &= otherPlayerFields;
            possiblyChangedFields |= cursor;
            cursor = (cursor >>> 9) & rightshiftMask;
            if((cursor & playerFields) != 0){
                changedFields |= possiblyChangedFields;
                break;
            }
        }
        playerFields ^= changedFields | coords;
        otherPlayerFields ^= changedFields;
        if(player){
            red = playerFields;
            green = otherPlayerFields;
        }
        else{
            green = playerFields;
            red = otherPlayerFields;
        }
        refreshZobristHash(changedFields, coords, player);
        return changedFields;
    }

    @Override
    public void makeMove(int player, Coordinates coord){
        makeMove(player == RED, coordinatesToLong(coord));
    }

    boolean get(boolean player, byte x, byte y){
        if(x > 7 || x < 0 || y > 7 || y < 0)
            return false;
        return (((player ? red : green) >>> (63 - y * 8 - x)) & 1) == 1;
    }

    void print(){
        for (byte i = 0; i < 8; i++){
            for (byte k = 0; k < 8; k++){
                if(get(true, k, i))
                    System.out.print("1 ");
                else if(get(false, k, i))
                    System.out.print("2 ");
                else
                    System.out.print("0 ");
            }
            System.out.println("");
        }
    }

    public void refreshZobristHash(long flippedDisks, long moveCoordinates, boolean player){
        int index;
        while(flippedDisks != 0){
            long bit = Long.highestOneBit(flippedDisks);
            flippedDisks ^= bit;
            index = Long.numberOfTrailingZeros(bit);
            hash ^= zobristRandomRed[index];
            hash ^= zobristrandomGreen[index];
        }
        hash ^= (player) ? zobristRandomRed[Long.numberOfTrailingZeros(moveCoordinates)]
                : zobristrandomGreen[Long.numberOfTrailingZeros(moveCoordinates)];
    }

    /**
     * Reverts a done move
     * 
     * @param changedFields
     *            the fileds which were changed due to the move done
     * @param coords
     *            the coordinates of the move
     * @param player
     *            the player that is in charge of the move
     */
    public void undoMove(long changedFields, long coords, boolean player){
        red ^= changedFields;
        green ^= changedFields;
        if(player){
            red ^= coords;
        }
        else{
            green ^= coords;
        }
        refreshZobristHash(changedFields, coords, player);
    }

    @Override
    public boolean validCoordinates(Coordinates coords){
        return coords.getCol() < 9 && coords.getRow() < 9
                && coords.getCol() > 0
                && coords.getRow() > 0;
    }

}
