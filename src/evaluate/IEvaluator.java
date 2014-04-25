package evaluate;

import Gameboard.Bitboard;

public interface IEvaluator {
	public int evaluate(Bitboard gb, long possiblemoves, boolean player);
}
