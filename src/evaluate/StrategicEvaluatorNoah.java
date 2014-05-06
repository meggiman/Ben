package evaluate;

import Gameboard.Bitboard;

public class StrategicEvaluatorNoah implements IEvaluator{

    @Override
    public short evaluate(Bitboard gb, long possiblemoves, boolean player){
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
        return 42;
    }
}
