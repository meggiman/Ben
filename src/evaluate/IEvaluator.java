package evaluate;

import Gameboard.Bitboard;

public interface IEvaluator{
    public short evaluate(Bitboard gb, long possiblemoves, boolean player);
}
