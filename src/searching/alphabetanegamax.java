package searching;

import Gameboard.Bitboard;
import evaluate.IEvaluator;
import evaluate.StrategicEvaluator;

public class AlphaBetaNegamax{
    public static IEvaluator evaluator = new StrategicEvaluator();

    public static int search(Bitboard gb, boolean player, int depth, int alpha, int beta){
        long posssibleMovesBitboard = gb.getPossibleMoves(player);

        if(posssibleMovesBitboard == 0 && gb.getPossibleMoves(!player) == 0){
            return (gb.countStones(player) - gb.countStones(!player)) << 10;
        }

        if(depth == 0){
            return evaluator.evaluate(gb, posssibleMovesBitboard, player);
        }
        // Sorter sorter = new Sorter(posssiblemovesbitboard, gb, player);
        // Bitboard nextgb = sorter.nextGb(gb);
        // if (nextgb == null) {
        // nextgb = gb;
        // }
        int maxValue = alpha;
        int value;
        // do {
        // // value = -search(nextgb, !player, depth-1, -beta, -maxvalue);
        // if (value>maxvalue) {
        // maxvalue = value;
        // if (value >= beta) {
        // break;
        // }
        // }
        // // nextgb = sorter.nextGb(gb);
        // } while (nextgb != null);
        return maxValue;
    }
}
