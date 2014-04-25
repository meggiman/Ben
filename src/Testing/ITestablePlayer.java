package Testing;

import reversi.Coordinates;
import reversi.ReversiPlayer;

public interface ITestablePlayer extends ReversiPlayer {
	/**
	 * 
	 * @return einen treffenden Name des Spielers
	 */
	public String getname();
	
	/**
	 * 
	 * @return Die Anzahl der Knoten im Baum, welche während der letzten Suche dursucht worden sind. Dazu zählen auch die Evaluierten Knoten und die Blätter
	 * (entschiedene Spielsituationen). Wird iterative deepening verwendet, so werden die Knoten mehrmals gezählt.
	 */
	public long getnodescount();
	
	/**
	 * 
	 * @return Die Anzahl der Knoten im Baum, welche während der letzten Suche evaluiert werden mussten. Wird iterative deepening verwendet, so werden mehrfache Evaluierungen auch mehrmals gezählt. 
	 */
	public long getevaluatednodes();
	
	/**
	 * 
	 * @return Die Tiefe, aus welcher die letzte Suche abgebrochen wurde.
	 */
	public int getdepthoflatestsearch();
	
	/**
	 * 
	 * @return Der Evaluationswert des zuletzt gewählten Zuges.
	 */
	public int getvalueoflatestsearch();
	
	/**
	 * 
	 * @return An wievielter Stelle der Zug nach dem Move-Ordering war, welcher ausgesucht wurde. Wenn der erste durchsuchte Zug sich nach der Suche als der beste herausstelle gib 0 zurück.
	 * Wenn der zweite durchsuchte Zug sich als der beste herausstellte, gib 1 zurück usw.
	 */
	public int getmoveNroflatestSearch();
	
	/**
	 * 
	 * @return Die Totale Anzahl der Treffer in der TranspositionTable während der letzten Suche. 0, wenn keine Transpositiontables verwendet werden.
	 */
	public long getNrofTTHits();
}
