package evaluate;

import Gameboard.Bitboard;

public class StrategicEvaluatorNoah implements IEvaluator{

    Stability stability = new Stability();

    public StrategicEvaluatorNoah(){
    }

    @Override
    public short evaluate(Bitboard gb, long possibleMoves, boolean player){
        short discs = gb.discCount();
        short EC = 500;
        short MC = (short) (350 - 2 * discs);
        short SC;
        if(discs < 10){
            SC = (short) (200 - discs);
        }
        else if(discs < 20){
            SC = (short) (190 - 2 * (discs - 10));
        }
        else if(discs < 40){
            SC = (short) (170 - 5 * (discs - 20));
        }
        else if(discs < 50){
            SC = (short) (70 - 7 * (discs - 40));
        }
        else{
            SC = 0;
        }

        short edgeAdvantage = (short) (stability.getEdgeValue(gb, player) / 32);
        short mobilityAdvantage = (short) ((possibleMoves - gb.getPossibleMoves(!player)));
        short occupiedSquareAdvantage = 1;

        if(gb.isFinished()){
            if(gb.getRed() > gb.getGreen()){
                if(player){
                    return 32767;
                }
                else{
                    return -32767;
                }
            }
            else if(gb.getRed() < gb.getGreen()){
                if(player){
                    return -32767;
                }
                else{
                    return 32767;
                }
            }
            else{
                return 0;
            }
        }
        return (short) (EC * edgeAdvantage + MC * mobilityAdvantage + SC
                * occupiedSquareAdvantage);
    }
}
