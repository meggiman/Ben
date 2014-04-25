package searching;

import Gameboard.Bitboard;
import reversi.Coordinates;
import evaluate.IEvaluator;

public abstract class Searchalgorithm {
	public IEvaluator evaluator = null;
	public long deadline = 0;
	public long searchednodes = 0;
	public long evaluatednodes = 0;
	public long TTHits = 0;
	public int reacheddepth = 0;
	public int movenr = 0;
	public int valueoflastmove = 0;
	public abstract long nextmove(Bitboard gb);
}
