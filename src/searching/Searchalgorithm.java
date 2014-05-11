package searching;

import Gameboard.Bitboard;
import reversi.Coordinates;
import evaluate.IEvaluator;

public abstract class Searchalgorithm {
	public IEvaluator evaluator = null;
	public long deadline = 0;
	public long searchedNodesCount = 0;
	public long evaluatedNodesCount = 0;
	public long TTHits = 0;
	public int reachedDepth = 0;
	public int moveNr = 0;
	public int valueOfLastMove = 0;
	public abstract long nextMove(Bitboard gb);
}
