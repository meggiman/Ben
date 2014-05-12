package evaluate;

import Gameboard.Bitboard;

public class StrategicEvaluator implements IEvaluator{
    private final long cornersmask = 0x8100000000000081L;
    private final long xfieldsmask = 0x42000000004200L;
    private final long cfieldsmask = 0x4281000000008142L;

    @Override
    public short evaluate(Bitboard gb, long possibleMoves, boolean player){
        long movingPlayer = (player) ? gb.red : gb.green;
        long otherPlayer = (player) ? gb.green : gb.red;
        long possibleMovesEnemy = gb.getPossibleMoves(!player);
        long emptyFields = ~(movingPlayer | otherPlayer);

        short possibleMovesCount = (short) Long.bitCount(possibleMoves);
        short possibleMovesCountEnemy = (short) Long.bitCount(possibleMovesEnemy);
        int potentialMobility = Long.bitCount(Bitboard.fillAdjacent(otherPlayer)
                & emptyFields ^ possibleMoves);
        int corners = Long.bitCount(movingPlayer & cornersmask);
        int cornersEnemy = Long.bitCount(otherPlayer & cornersmask);
        int xFields = Long.bitCount(movingPlayer & xfieldsmask);
        int xFieldsEnemy = Long.bitCount(otherPlayer & xfieldsmask);
        int cFields = Long.bitCount(movingPlayer & cfieldsmask);
        int cFieldsEnemy = Long.bitCount(otherPlayer & cfieldsmask);
        int stonesRed = Long.bitCount(gb.red);
        int stonesGreen = Long.bitCount(gb.green);
        short value = 0;

        // Mobility
        if(possibleMovesCount + possibleMovesCountEnemy != 0){
            value = (short) (100 * ((possibleMovesCount - possibleMovesCountEnemy) * 10 / (possibleMovesCount + possibleMovesCountEnemy)));
        }
        value += 300 / (1 + possibleMovesCountEnemy);
        value += 80 * potentialMobility;

        // Corners
        if(corners + cornersEnemy != 0){
            value += 200 * ((corners - cornersEnemy) * 10 / (corners + cornersEnemy));
        }
        if(cFields + cFieldsEnemy != 0){
            value += 80 * ((cFieldsEnemy - cFields) * 10 / (cFieldsEnemy + cFields));
        }
        if(xFields + xFieldsEnemy != 0){
            value += 120 * ((xFieldsEnemy - xFields) * 10 / (xFields + xFieldsEnemy));
        }

        value += 1 * (stonesRed - stonesGreen) * (stonesRed + stonesGreen);
        return value;
    }

}