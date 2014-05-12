package HumanPlayer;



import java.io.*;
import reversi.*;


/**
 * HumanPlayer implementiert auf einfache Art das Interface für einen
 * Reversi-Spieler ({@link reversi.ReversiPlayer}). Anstatt einen Zug zu
 * berechnen, fordert <code>HumanPlayer</code> den Benutzer auf, einen Zug
 * über die Konsole einzugeben.
 * 
 * @see reversi.ReversiPlayer
 */
public class HumanPlayer implements ReversiPlayer
{
	/**
	 * Die Farbe des Spielers.
	 */
	private int color = 0;

	/**
	 * Konstante, die vom Benutzer eigegeben werden kann, um zu passen.
	 */
	private final static String PASSEN = "p";

	/**
	 * Konstruktor, der bei der Gründung eines HumanPlayer eine Meldung auf den
	 * Bildschirm ausgibt.
	 */
	public HumanPlayer()
	{
		System.out.println("HumanPlayer erstellt.");
	}

	/**
	 * Speichert die Farbe und den Timeout-Wert in Instanzvariablen ab. Diese
	 * Methode wird vor Beginn des Spiels von {@link Arena} aufgerufen.
	 * 
	 * @see reversi.ReversiPlayer
	 */
	@Override
	public void initialize(int color, long timeout)
	{
		this.color = color;
		if (color == GameBoard.RED)
		{
			System.out.println("HumanPlayer ist Spieler RED.");
		}
		else if (color == GameBoard.GREEN)
		{
			System.out.println("HumanPlayer ist Spieler GREEN.");
		}
	}

	/**
	 * Macht einen Zug für den HumanPlayer, indem der Benutzer zur Eingabe eines
	 * Zuges aufgefordert wird. Diese Methode wird von {@link reversi.Arena}
	 * abwechselnd aufgerufen.
	 * 
	 * @see reversi.ReversiPlayer
	 * @return Der Zug des HumanPlayers.
	 */
	@Override
	public Coordinates nextMove(GameBoard gb)
	{

		Coordinates coord = null;

		System.out.print("HumanPlayer ");
		if (color == GameBoard.RED)
		{
			System.out.print("(RED)");
		}
		else if (color == GameBoard.GREEN)
		{
			System.out.print("(GREEN)");
		}
		System.out
				.print(", gib deinen Zug ein (passen mit '" + PASSEN + "'): ");
		coord = readMoveFromKeyboard();

		return coord;
	} 

	/**
	 * Liest einen Zug vom Benutzer ein. Gültige Eingaben sind entweder ein
	 * Koordinatenpaar bestehend aus Zeile und Spalte (z.B. '6d') oder ein 'p',
	 * um zu passen falls kein Zug möglich ist. Methode wiederholt die
	 * Eingabeaufforderung so lange bis eine gültige Eingabe gemacht wurde.
	 * 
	 * @return Gibt die eingelesenen Koordinaten zurück, bzw. <code>null</code>,
	 *         wenn der Benutzer "Passen" ausgewählt hat.
	 */
	public static Coordinates readMoveFromKeyboard()
	{
		Coordinates result = null;
		while (result == null)
		{
			System.out.print(">");
			String str = null;
			int row = 0, column = 0;

			BufferedReader d = new BufferedReader(new InputStreamReader(
					System.in));

			// String einlesen
			try
			{
				str = d.readLine();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}

			// gelesenen String sezieren
			str.trim();
			str = str.toLowerCase();

			// falls der Zug "passen" bedeutet, beende Schleife (gib 'null'
			// zurück)
			if (str.equals(PASSEN))
			{
				System.out.println("Zug 'PASSEN' wurde ausgewaehlt.");
				break;
			}

			if (str.length() != 2)
			{
				System.out.println("Ungueltige Eingabe: mehr als 2 Zeichen.");
				continue;
			}

			// ist das erste Zeichen eine Ziffer zwischen 0..8?
			row = (str.charAt(0)) - '0';
			// Zeilen 0 und 9 sind ungueltig
			if (row < 1 || row > 8)
			{
				System.out.println("Ungueltige Eingabe: die Zeilennummer muss "
						+ "zwischen 1 und 8 liegen.");
				continue;
			}

			// ist das zweite Zeichen ein Buchstabe zwischen a..h?
			column = (str.charAt(1)) - 'a' + 1;

			if (column < 1 || column > 8)
			{
				System.out.println("Ungueltige Eingabe: die Spaltenummer muss "
						+ "zwischen A und H liegen.");
				continue;
			}

			result = new Coordinates(row, column);
		}
		return result;
	} // end readMoveFromKeyboard()
}
