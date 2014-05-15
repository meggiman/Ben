package evaluate;

import Gameboard.Bitboard;

public class Stability{

    short[] edgeTable = new short[65536];

    byte    maskC     = 0b01000010;
    byte    maskA     = 0b00100100;
    byte    maskB     = 0b00011000;

    Stability(){
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
            score += Integer.bitCount(maskC & unstableRed) * -5;
            score += Integer.bitCount(maskA & unstableRed) * 2;
            score += Integer.bitCount(maskB & unstableRed) * 1.5;

            score += Integer.bitCount(maskA & unanchoredRed) * 30;
            score += Integer.bitCount(maskB & unanchoredRed) * 20;

            score += Integer.bitCount(maskC & aloneRed) * -7.5;
            score += Integer.bitCount(maskA & aloneRed) * -2.5;
            score += Integer.bitCount(maskB & aloneRed) * -5.0;

            score += Integer.bitCount(stable1Red) * 80;

            score += Integer.bitCount(maskC & stable3Red) * 120;
            score += Integer.bitCount(maskA & stable3Red) * 100;
            score += Integer.bitCount(maskB & stable3Red) * 100;
            score += Integer.bitCount(0b10000001 & stable3Red) * 120;

            score += Integer.bitCount(maskC & semiRed) * -12.5;
            score += Integer.bitCount(maskA & semiRed) * 10;
            score += Integer.bitCount(maskB & semiRed) * 10;

            // Negative score Green
            score -= Integer.bitCount(maskC & unstableGreen) * -5 * 1.2;
            score -= Integer.bitCount(maskA & unstableGreen) * 2 * 1.2;
            score -= Integer.bitCount(maskB & unstableGreen) * 1.5 * 1.2;

            score -= Integer.bitCount(maskA & unanchoredGreen) * 30 * 1.2;
            score -= Integer.bitCount(maskB & unanchoredGreen) * 20 * 1.2;

            score -= Integer.bitCount(maskC & aloneGreen) * -7.5 * 1.2;
            score -= Integer.bitCount(maskA & aloneGreen) * -2.5 * 1.2;
            score -= Integer.bitCount(maskB & aloneGreen) * -5 * 1.2;

            score -= Integer.bitCount(stable1Green) * 80 * 1.2;

            score -= Integer.bitCount(maskC & stable3Green) * 120 * 1.2;
            score -= Integer.bitCount(maskA & stable3Green) * 100 * 1.2;
            score -= Integer.bitCount(maskB & stable3Green) * 100 * 1.2;
            score -= Integer.bitCount(0b10000001 & stable3Green) * 120 * 1.6;

            score -= Integer.bitCount(maskC & semiGreen) * -12.5 * 1.2;
            score -= Integer.bitCount(maskA & semiGreen) * 10 * 1.2;
            score -= Integer.bitCount(maskB & semiGreen) * 10 * 1.2;

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

    final short getEdgeValue(Bitboard board){
        short edgeTopRed = (short) (board.red >>> 56);
        short edgeBotRed = (short) (board.red & 0xFF);
        long bitboard = board.red & 0x0101010101010101L;
        bitboard |= bitboard >>> 28;
        bitboard |= bitboard >>> 14;
        bitboard |= bitboard >>> 7;
        short edgeRightRed = (short) (bitboard & 0xFF);
        bitboard = (board.red >>> 7) & 0x0101010101010101L;
        bitboard |= bitboard >>> 28;
        bitboard |= bitboard >>> 14;
        bitboard |= bitboard >>> 7;
        short edgeLeftRed = (short) (bitboard & 0xFF);

        short edgeTopGreen = (short) (board.green >>> 56);
        short edgeBotGreen = (short) (board.green & 0xFF);
        bitboard = board.green & 0x0101010101010101L;
        bitboard |= bitboard >>> 28;
        bitboard |= bitboard >>> 14;
        bitboard |= bitboard >>> 7;
        short edgeRightGreen = (short) (bitboard & 0xFF);
        bitboard = (board.green >>> 7) & 0x0101010101010101L;
        bitboard |= bitboard >>> 28;
        bitboard |= bitboard >>> 14;
        bitboard |= bitboard >>> 7;
        short edgeLeftGreen = (short) (bitboard & 0xFF);

        return (short) (edgeTable[(edgeTopRed << 8) | edgeTopGreen]
                + edgeTable[(edgeBotRed << 8) | edgeBotGreen]
                + edgeTable[(edgeLeftRed << 8) | edgeLeftGreen] + edgeTable[(edgeRightRed << 8)
                | edgeRightGreen]);

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
        Bitboard board = new Bitboard(0x8000800080000000L, 0x80008000000000L);
        s.getEdgeValue(board);
        // System.out.println(s.getStable1EdgePieces((byte) 0b10101000, (byte)
        // 84));
    }
}
