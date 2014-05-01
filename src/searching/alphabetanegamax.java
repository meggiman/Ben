package searching;

import Gameboard.Bitboard;
import evaluate.IEvaluator;
import evaluate.strategicevaluator;

public class alphabetanegamax{
    public static IEvaluator evaluator = new strategicevaluator();

    public static int search(Bitboard gb, boolean player, int depth, int alpha, int beta){
        long posssiblemovesbitboard = gb.getPossibleMoves(player);

        if(posssiblemovesbitboard == 0 && gb.getPossibleMoves(!player) == 0){
            return (gb.countStones(player) - gb.countStones(!player)) << 10;
        }

        if(depth == 0){
            return evaluator.evaluate(gb, posssiblemovesbitboard, player);
        }
        // Sorter sorter = new Sorter(posssiblemovesbitboard, gb, player);
        // Bitboard nextgb = sorter.nextGb(gb);
        // if (nextgb == null) {
        // nextgb = gb;
        // }
        int maxvalue = alpha;
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
        return maxvalue;
    }
}
