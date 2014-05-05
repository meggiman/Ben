package Gameboard;

public class Tables {
	private static byte[] moveTable;
	
	public static void generateTables() {
		moveTable = Tablegenerator.generatemovetable();
	}
	
	public static byte moveLookup(int movingplayerrow, int otherplayerrow, int move) {
		return moveTable[movingplayerrow|(otherplayerrow<<8)|(move<<16)];
	}
	
}
