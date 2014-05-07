package evaluate;

import Gameboard.Bitboard;

public class Stability{
    Bitboard                 board;

    public static final long mask_northWest = 0b1111111011111110111111101111111011111110111111101111111000000000L;

    public static final long mask_northEast = 0b0111111101111111011111110111111101111111011111110111111100000000L;

    public static final long mask_southWest = 0b0000000011111110111111101111111011111110111111101111111011111110L;

    public static final long mask_southEast = 0b0000000001111111011111110111111101111111011111110111111101111111L;

    public static final long mask_east      = 0b0111111101111111011111110111111101111111011111110111111101111111L;

    public static final long mask_west      = 0b1111111011111110111111101111111011111110111111101111111011111110L;

    // Shift in all 8 directions

    private final long shiftNorthWest(long board){
        return (board << 9) & mask_northWest;
    }

    private final long shiftNorthEast(long board){
        return (board << 7) & mask_northEast;
    }

    private final long shiftSouthWest(long board){
        return (board >>> 7) & mask_southWest;
    }

    private final long shiftSouthEast(long board){
        return (board >>> 9) & mask_southEast;
    }

    private final long shiftNorth(long board){
        return (board << 8);
    }

    private final long shiftSouth(long board){
        return (board >>> 8);
    }

    private final long shiftWest(long board){
        return (board << 1) & mask_west;
    }

    private final long shiftEast(long board){
        return (board >>> 1) & mask_east;
    }

    boolean isStable(boolean player, byte piece){
        if(board.red >> piece == -128){
            return true;
        }
        if((board.red & 0b00000001) == 1){
            if(board.red << (7 - 1 - piece) == -128){
                return true;
            }
        }
        return false;
    }

    long getSouthStablePieces(long player){
        long tempBoard;
        long mask = 0b11111111;

        long stable = player & mask;
        long potentiallyStable = player & mask;
        while(potentiallyStable != 0){
            tempBoard = shiftNorth(potentiallyStable);
            potentiallyStable = tempBoard & player;
            stable |= potentiallyStable;
        }
        return stable;
    }

    long getNorthStablePieces(long player){
        long tempBoard;
        long mask = 0b11111111_00000000_00000000_00000000_00000000_00000000_00000000_00000000L;

        long stable = player & mask;
        long potentiallyStable = player & mask;
        while(potentiallyStable != 0){
            tempBoard = shiftSouth(potentiallyStable);
            potentiallyStable = tempBoard & player;
            stable |= potentiallyStable;
        }
        return stable;
    }

    long getEastStablePieces(long player){
        long tempBoard;
        long mask = 0b1_00000001_00000001_00000001_00000001_00000001_00000001_00000001L;

        long stable = player & mask;
        long potentiallyStable = player & mask;
        while(potentiallyStable != 0){
            tempBoard = shiftWest(potentiallyStable);
            potentiallyStable = tempBoard & player;
            stable |= potentiallyStable;
        }
        return stable;
    }

    long getWestStablePieces(long player){
        long tempBoard;
        long mask = 0b10000000_10000000_10000000_10000000_10000000_10000000_10000000_10000000L;

        long stable = player & mask;
        long potentiallyStable = player & mask;
        while(potentiallyStable != 0){
            tempBoard = shiftEast(potentiallyStable);
            potentiallyStable = tempBoard & player;
            stable |= potentiallyStable;
        }
        return stable;
    }

    byte getStable3EdgePieces(byte border){
        byte stable = (byte) (border & 1);
        byte potentiallyStable = (byte) (border & 1);
        byte tempBoard;
        while(potentiallyStable != 0){
            tempBoard = (byte) (potentiallyStable << 1);
            potentiallyStable = (byte) (tempBoard & border);
            stable |= potentiallyStable;
        }

        stable |= (byte) (border & 0b10000000);
        potentiallyStable = (byte) (border & 0b10000000);
        while(potentiallyStable != 0){
            tempBoard = (byte) ((potentiallyStable & 0xFF) >>> 1);
            potentiallyStable = (byte) (tempBoard & border);
            stable |= potentiallyStable;
        }
        return stable;
    }

    byte getStable1EdgePieces(byte borderRed, byte borderGreen){
        byte borderRedCopy = borderRed;
        byte borderGreenCopy = borderGreen;
        byte emptyEdge = (byte) ~(borderRed | borderGreen);
        byte stable = 0;
        byte potentiallyStable = (byte) (((borderRed << 1) & borderGreen) >>> 1);
        emptyEdge = (byte) (emptyEdge << 1);
        while(potentiallyStable != 0){
            borderGreen = (byte) (borderGreen << 1);
            emptyEdge = (byte) (emptyEdge << 1);
            potentiallyStable = (byte) (potentiallyStable & borderGreen);
            stable |= potentiallyStable & emptyEdge;
        }

        borderRed = borderRedCopy;
        borderGreen = borderGreenCopy;
        emptyEdge = (byte) ~(borderRed | borderGreen);
        potentiallyStable = (byte) (((0xFF & borderRed >>> 1) & borderGreen) << 1);
        emptyEdge = (byte) (0xFF & emptyEdge >>> 1);
        while(potentiallyStable != 0){
            borderGreen = (byte) (0xFF & borderGreen >>> 1);
            emptyEdge = (byte) (0xFF & emptyEdge >>> 1);
            potentiallyStable = (byte) (potentiallyStable & borderGreen);
            stable |= potentiallyStable & emptyEdge;
        }
        return stable;
    }

    byte getAloneEdgePieces(byte borderRed, byte borderGreen){
        // Shift left and right and check via & if there is an empty piece
        byte emptyEdge = (byte) ~(borderRed | borderGreen);
        return (byte) (((((borderRed << 1) & emptyEdge) >>> 2) & emptyEdge) << 1);
    }

    byte getUnstableEdgePieces(byte borderRed, byte borderGreen){
        byte emptyEdge = (byte) ~(borderRed | borderGreen);
        byte potentiallyUnstable = (byte) ((borderRed << 1) & emptyEdge);
        byte unstable = (byte) (((potentiallyUnstable >>> 2) & borderGreen) << 1);
        potentiallyUnstable = (byte) ((borderRed >>> 1) & emptyEdge);
        unstable |= (((potentiallyUnstable << 2) & borderGreen) >>> 1);
        return (byte) unstable;
    }

    byte getUnanchoredStableEdgePieces(byte borderRed, byte unstableGreen){
        return (byte) (((((borderRed << 1) & unstableGreen) >>> 2) & unstableGreen) << 1);
    }

    public static void main(String[] args){
        Stability s = new Stability();
        System.out.println(s.getStable1EdgePieces((byte) 20, (byte) 40));
    }
}
