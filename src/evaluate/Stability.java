package evaluate;

public class Stability{

    short[] edgeTable = new short[59049];

    byte    maskC     = 0b01000010;
    byte    maskA     = 0b00100100;
    byte    maskB     = 0b00011000;

    Stability(){
        for (int k = 0; k < 59049; k++){
            int c = k;
            short[] board = new short[3];
            for (int z = 0; z < 10; z++){
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

            short score = 0;

            // Score red
            score += Integer.bitCount(maskC & unstableRed) * -50;
            score += Integer.bitCount(maskA & unstableRed) * 20;
            score += Integer.bitCount(maskB & unstableRed) * 15;

            score += Integer.bitCount(maskA & unanchoredRed) * 300;
            score += Integer.bitCount(maskB & unanchoredRed) * 200;

            score += Integer.bitCount(maskC & aloneRed) * -75;
            score += Integer.bitCount(maskA & aloneRed) * -25;
            score += Integer.bitCount(maskB & aloneRed) * -50;

            score += Integer.bitCount(stable1Red) * 800;

            score += Integer.bitCount(maskC & stable3Red) * 1200;
            score += Integer.bitCount(maskA & stable3Red) * 1000;
            score += Integer.bitCount(maskB & stable3Red) * 1000;
            score += Integer.bitCount(0b10000001 & stable3Red) * 800;

            score += Integer.bitCount(maskC & semiRed) * -125;
            score += Integer.bitCount(maskA & semiRed) * 100;
            score += Integer.bitCount(maskB & semiRed) * 100;

            // Negative score Green
            score -= Integer.bitCount(maskC & unstableGreen) * -50;
            score -= Integer.bitCount(maskA & unstableGreen) * 20;
            score -= Integer.bitCount(maskB & unstableGreen) * 15;

            score -= Integer.bitCount(maskA & unanchoredGreen) * 300;
            score -= Integer.bitCount(maskB & unanchoredGreen) * 200;

            score -= Integer.bitCount(maskC & aloneGreen) * -75;
            score -= Integer.bitCount(maskA & aloneGreen) * -25;
            score -= Integer.bitCount(maskB & aloneGreen) * -50;

            score -= Integer.bitCount(stable1Green) * 800;

            score -= Integer.bitCount(maskC & stable3Green) * 1200;
            score -= Integer.bitCount(maskA & stable3Green) * 1000;
            score -= Integer.bitCount(maskB & stable3Green) * 1000;
            score -= Integer.bitCount(0b10000001 & stable3Green) * 800;

            score -= Integer.bitCount(maskC & semiGreen) * -125;
            score -= Integer.bitCount(maskA & semiGreen) * 100;
            score -= Integer.bitCount(maskB & semiGreen) * 100;

            edgeTable[k] = score;
            System.out.println(k);
            System.out.println(String.format("%8s",
                    Integer.toBinaryString(0xFF & board[1])).replace(' ', '0'));
            System.out.println(String.format("%8s",
                    Integer.toBinaryString(0xFF & board[2])).replace(' ', '0'));
            System.out.println(score);
            System.out.println("-------------------");

        }
        System.out.println("bla");
    }

    final static byte getStable3EdgePieces(byte borderRed, byte borderGreen){
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

    final static byte getStable1EdgePieces(byte borderRed, byte borderGreen){
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

    final static byte getAloneEdgePieces(byte borderRed, byte borderGreen){
        byte emptyEdge = (byte) ~(borderRed | borderGreen);
        return (byte) (((((borderRed << 1) & emptyEdge) >>> 2) & emptyEdge) << 1);
    }

    final static byte getUnstableEdgePieces(byte borderRed, byte borderGreen){
        byte emptyEdge = (byte) ~(borderRed | borderGreen);
        byte potentiallyUnstable = (byte) ((borderRed << 1) & emptyEdge);
        byte unstable = (byte) (((potentiallyUnstable >>> 2) & borderGreen) << 1);
        potentiallyUnstable = (byte) ((borderRed >>> 1) & emptyEdge);
        unstable |= (((potentiallyUnstable << 2) & borderGreen) >>> 1);
        return (byte) unstable;
    }

    final static byte getUnanchoredStableEdgePieces(byte borderRed, byte unstableGreen){
        return (byte) (((((borderRed << 1) & unstableGreen) >>> 2) & unstableGreen) << 1);
    }

    public static void main(String[] args){
        Stability s = new Stability();
        // System.out.println(s.getStable1EdgePieces((byte) 0b10101000, (byte)
        // 84));
    }
}
