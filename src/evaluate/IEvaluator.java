package evaluate;

import Gameboard.Bitboard;

public interface IEvaluator {
	/**
	 * Evaluates the {@code Bitboard} gb. This Method uses the parameter {@code possiblemoves} so it doesn't have to recalculate this information. 
	 * @param gb the {@link Bitboard} to evaluate.
	 * @param possibleMoves all the possible moves for Player {@code palyer} in current situation.
	 * @param player the player from which's perspective the {@code Bitboard} is rated.
	 * @return an integer value that represents the goodness of the current game situation.
	 */
	public short evaluate(Bitboard gb, long possibleMoves, boolean player);
}
