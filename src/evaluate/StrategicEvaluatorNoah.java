package evaluate;

import Gameboard.Bitboard;

public class StrategicEvaluatorNoah implements IEvaluator{

    Stability stability = new Stability();

    public StrategicEvaluatorNoah(){
    }

    @Override
    public short evaluate(Bitboard gb, long possibleMoves, boolean player){
        double discs = gb.discCount();
        double EC = 5;
        double MC = (3.5 - discs / 50);
        double SC;
        if(discs < 10){
            SC = (2 - discs / 100);
        }
        else if(discs < 20){
            SC = (1.9 - (discs - 10) / 50);
        }
        else if(discs < 40){
            SC = (1.7 - (discs - 20) / 20);
        }
        else if(discs < 50){
            SC = (0.7 - 7 * (discs - 40) / 100);
        }
        else{
            SC = 0;
        }

        long possibleMovesEnemy = gb.getPossibleMoves(!player);
        short edgeAdvantage = (short) (stability.getEdgeValue(gb, player) / 32);
        short mobilityAdvantage = (short) ((possibleMoves + possibleMovesEnemy) == 0 ? 0
                : ((float) (possibleMoves - possibleMovesEnemy)
                        / (possibleMoves + possibleMovesEnemy) * 1000));
        short occupiedSquareAdvantage = 0;

        return (short) (EC * edgeAdvantage + MC * mobilityAdvantage + SC
                * occupiedSquareAdvantage);
    }
}
