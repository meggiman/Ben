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
	 * @return Die Anzahl der Knoten im Baum, welche w�hrend der letzten Suche dursucht worden sind. Dazu z�hlen auch die Evaluierten Knoten und die Bl�tter
	 * (entschiedene Spielsituationen). Wird iterative deepening verwendet, so werden die Knoten mehrmals gez�hlt.
	 */
	public long getnodescount();
	
	/**
	 * 
	 * @return Die Anzahl der Knoten im Baum, welche w�hrend der letzten Suche evaluiert werden mussten. Wird iterative deepening verwendet, so werden mehrfache Evaluierungen auch mehrmals gez�hlt. 
	 */
	public long getevaluatednodes();
	
	/**
	 * 
	 * @return Die Tiefe, aus welcher die letzte Suche abgebrochen wurde.
	 */
	public int getdepthoflatestsearch();
	
	/**
	 * 
	 * @return Der Evaluationswert des zuletzt gew�hlten Zuges.
	 */
	public int getvalueoflatestsearch();
	
	/**
	 * 
	 * @return An wievielter Stelle der Zug nach dem Move-Ordering war, welcher ausgesucht wurde. Wenn der erste durchsuchte Zug sich nach der Suche als der beste herausstelle gib 0 zur�ck.
	 * Wenn der zweite durchsuchte Zug sich als der beste herausstellte, gib 1 zur�ck usw.
	 */
	public int getmoveNroflatestSearch();
	
	/**
	 * 
	 * @return Die Totale Anzahl der Treffer in der TranspositionTable w�hrend der letzten Suche. 0, wenn keine Transpositiontables verwendet werden.
	 */
	public long getNrofTTHits();
}
