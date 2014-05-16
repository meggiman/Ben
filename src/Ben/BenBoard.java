package Ben;

import reversi.Coordinates;
import reversi.GameBoard;
import reversi.OutOfBoundsException;
import Gameboard.Bitboard;

public class BenBoard{
    /**
     * Bitmasks
     */
    private static final long   leftshiftMask      = 0xFEFEFEFEFEFEFEFEL;
    private static final long   rightshiftMask     = 0x7F7F7F7F7F7F7F7FL;
    private static final long   shiftMask          = 0x7E7E7E7E7E7E7E7EL;

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

    /**
     * Converts a {@code GameBoard} into a long array of size 2.
     * This method has to be used carefully since it affords a lot of
     * calculation time!
     * 
     * @param gb
     *            the {@link GameBoard} to be converted
     * @return the converted GameBoard as long array of size 2
     */
    public static final long[] convertToBitboard(final GameBoard gb){
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
    public static final long coordinatesToLong(final Coordinates coords){
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
    public static final long fillAdjacent(final long bitboard){
        long filledbitboard = bitboard;
        filledbitboard |= filledbitboard >>> 1 & 0x7f7f7f7f7f7f7f7fL;
        filledbitboard |= filledbitboard >>> 8;
        filledbitboard |= filledbitboard << 1 & 0xfefefefefefefefeL;
        filledbitboard |= filledbitboard << 8;
        return filledbitboard ^ bitboard;
    }

    public static final long getFlippedDiskRed(final long red, long green, final long coord){
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

    /**
     * Converts a long coordinates bitboard-representation into its equivalent
     * {@link Coordinates} object.
     * 
     * @param coords
     *            the coordinates to convert
     * @return the converted coordinates
     */
    public static final Coordinates longToCoordinates(final long coords){
        if(coords == 0){
            return null;
        }
        return new Coordinates(1 + (Long.numberOfLeadingZeros(coords) >>> 3), 1 + Long.numberOfLeadingZeros(coords) % 8);
    }

    public static final long possibleMovesRed(final long red, long green){
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
    public static final long[] serializeBitboard(long bitboard){
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

    /**
     * 
     * @param player
     *            board in view of the player whose stones are to be count
     * @return the amount of stones for the player which was given in
     *         {@code player}
     */

    public static final int countStones(long player){
        return Long.bitCount(player);
    }

    /**
     * 
     * @param red
     *            board in view of the red player
     * @param green
     *            board in view of the red player
     * @return a hash to determine the position in the hashtable
     */

    public static final long generateZobristHash(final long red, final long green){
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
     * @return the number of ALL stones on the field
     */

    public static final int getDiscCount(final long red, final long green){
        return (byte) (Long.bitCount(red) + Long.bitCount(green));
    }

    /**
     * @param player
     *            The player to move; use {@code true} for red or {@code false}
     *            for green.
     * @return returns a long containing all possible moves. If there is no
     *         legal moves 0 is returned
     */
    public static final long getPossibleMoves(final long red, final long green){
        long emptyFields = ~(red | green);
        long validMoves = 0;
        long potentialMoves;
        // leftshift
        potentialMoves = (((red << 1) & leftshiftMask & green) << 1)
                & leftshiftMask;
        while(potentialMoves != 0){
            validMoves |= (potentialMoves & emptyFields);
            potentialMoves = (potentialMoves & green) << 1
                    & leftshiftMask;
        }
        // rightshift
        potentialMoves = (((red >>> 1) & rightshiftMask & green) >>> 1)
                & rightshiftMask;
        while(potentialMoves != 0){
            validMoves |= (potentialMoves & emptyFields);
            potentialMoves = (potentialMoves & green) >>> 1
                    & rightshiftMask;
        }
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
        // upleftshift
        potentialMoves = (((red << 9) & leftshiftMask & green) << 9)
                & leftshiftMask;
        while(potentialMoves != 0){
            validMoves |= (potentialMoves & emptyFields);
            potentialMoves = (potentialMoves & green) << 9
                    & leftshiftMask;
        }
        // uprightshift
        potentialMoves = (((red << 7) & rightshiftMask & green) << 7)
                & rightshiftMask;
        while(potentialMoves != 0){
            validMoves |= (potentialMoves & emptyFields);
            potentialMoves = (potentialMoves & green) << 7
                    & rightshiftMask;
        }
        // downleftshift
        potentialMoves = (((red >>> 7) & leftshiftMask & green) >>> 7)
                & leftshiftMask;
        while(potentialMoves != 0){
            validMoves |= (potentialMoves & emptyFields);
            potentialMoves = (potentialMoves & green) >>> 7
                    & leftshiftMask;
        }
        // downrightshift
        potentialMoves = (((red >>> 9) & rightshiftMask & green) >>> 9)
                & rightshiftMask;
        while(potentialMoves != 0){
            validMoves |= (potentialMoves & emptyFields);
            potentialMoves = (potentialMoves & green) >>> 9
                    & rightshiftMask;
        }
        return validMoves;
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

    public static final boolean get(final long player, final byte x, final byte y){
        if(x > 7 || x < 0 || y > 7 || y < 0)
            return false;
        return ((player >>> (63 - y * 8 - x)) & 1) == 1;
    }

    public static final void print(final long red, final long green){
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

}
