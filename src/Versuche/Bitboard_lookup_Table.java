package Versuche;

import reversi.Coordinates;
import reversi.GameBoard;
import reversi.OutOfBoundsException;

public class Bitboard_lookup_Table implements GameBoard{

	public long red;
	public long green;
	
	public Bitboard_lookup_Table(long red, long green){
		this.red = red;
		this.green = green;
	}
	
	/**
	 * 
	 * @param bitboard
	 * @return Gibt die 8 Bits der ersten Spalte von {@code bitboard} aus.
	 */
	public static int getfirstcolumn(long bitboard) {
		bitboard &= 0x0101010101010101L;
		bitboard |= bitboard>>>28;
		bitboard |= bitboard>>>14;
		bitboard |= bitboard>>>7;
		return (int)bitboard & 0xFF;
	}
	
	/**
	 * 
	 * @param bitboard
	 * @return Gibt die Diagonale des Bitboards von unten links nach oben rechts aus.
	 */
	public static int getdiagleftright(long bitboard) {
		bitboard &= 0x102040810204080L;
		bitboard |= bitboard>>32;
		bitboard |= bitboard>>16;
		bitboard |= bitboard>>8;
		return (int)bitboard & 0xFF;
	}
	/**
	 * 
	 * @param bitboard
	 * @return Gibt die Diagonale des Bitboards von unten rechts nach oben links aus.
	 */
	public static int getdiagrightleft(long bitboard){
		bitboard &= 0x8040201008040201L;
		bitboard |= bitboard>>32;
		bitboard |= bitboard>>16;
		bitboard |= bitboard>>8;
		return (int)bitboard & 0xFF;
	}
	
	public static long maptocolumn(int row){
		row |= row << 7;
		row |= row << 14;
		return (row | ((long)row << 28)) &0x0101010101010101L;
	}
	
	public static long maptodiagleftright(int row){
		row |= row << 8;
		row |= (row & 0x1122) << 16;
		return (row | ((long)row << 32)) & 0x0102040810204080L;
	}
	
	public static long maptodiagrightleft(int row)
	{
		row |= row <<8;
		long z = row | ((long)row << 16);
		return (z |= z << 32)&0x8040201008040201L;
	}
	
	public static long filladjacent(long bitboard){
		long filledbitboard = bitboard;
		filledbitboard |= filledbitboard>>>1 & 0x7f7f7f7f7f7f7f7fL;
		filledbitboard |= filledbitboard>>>8;
		filledbitboard |= filledbitboard<<1 & 0xfefefefefefefefeL;
		filledbitboard |= filledbitboard<<8;
		return filledbitboard^bitboard;
	}
	
	public static long[] bitboardserialize(long bitboard){
		int bitcount = Long.bitCount(bitboard);
		long tmp;
		long[] bitboards = new long[bitcount];
		for (int i = 0; i < bitcount; i++) {
			tmp = Long.highestOneBit(bitboard);
			bitboards[i] = tmp;
			bitboard ^= tmp;
		}
		return bitboards;
	}
	
	public long likelyMoves(boolean player){
		if (player) {
			return filladjacent(green)& ~red;
		}
		else {
			return filladjacent(red)& ~green;
		}
	}
	
	public long possiblemoves(boolean player){
		long[] likelyMoves = bitboardserialize(likelyMoves(player));
		long possiblemoves=0;
		for (long l : likelyMoves) {
			if (getflipboard(player, l)!=0) {
				possiblemoves|=l;
			}
		}
		return possiblemoves;
	}
	
	@Override
	public GameBoard clone(){
		return new Bitboard_lookup_Table(red, green);
	}
	
	@Override
	public boolean checkMove(int player, Coordinates coord) {
		// TODO Automatisch generierter Methodenstub
		return false;
	}

	@Override
	public int countStones(int player) {
		// TODO Automatisch generierter Methodenstub
		return 0;
	}

	@Override
	public int getOccupation(Coordinates coord) throws OutOfBoundsException {
		// TODO Automatisch generierter Methodenstub
		return 0;
	}

	@Override
	public int getSize() {
		// TODO Automatisch generierter Methodenstub
		return 0;
	}

	@Override
	public boolean isFull() {
		// TODO Automatisch generierter Methodenstub
		return false;
	}

	@Override
	public boolean isMoveAvailable(int player) {
		// TODO Automatisch generierter Methodenstub
		return false;
	}

	@Override
	public void makeMove(int player, Coordinates coord) {
		// TODO Automatisch generierter Methodenstub
		
	}
	
	public void makeMove(boolean player, long coord){
		long flipboard = getflipboard(player, coord);
		red^=flipboard;
		green^=flipboard;
		if (player) {
			red|=coord;
		}
		else {
			green|=coord;
		}
	}
	
	public long getflipboard(boolean player, long coord ){
		int row = (Long.numberOfTrailingZeros(coord)>>>3);
		int column = (Long.numberOfTrailingZeros(coord)%8);
		long flipboard;
		long playerbitboard;
		long opponentbitboard;
		if (player) {
			playerbitboard = red;
			opponentbitboard = green;
		}
		else {
			playerbitboard = green;
			opponentbitboard = red;
		}
		//Rowcheck
		int shiftdistance = row*8;
		int playerrow = (int)((playerbitboard>>>shiftdistance)&0xFFL);
		int opponentrow = (int)((opponentbitboard>>>shiftdistance)&0xFFL);
		int fliprow = Tables.moveLookup(playerrow, opponentrow,column );
		flipboard = (long)fliprow<<shiftdistance;
		
		//Columncheck
		shiftdistance = column;
		playerrow = getfirstcolumn(playerbitboard>>>shiftdistance);
		opponentrow = getfirstcolumn(opponentbitboard>>>shiftdistance);
		fliprow = Tables.moveLookup(playerrow, opponentrow, row);
		flipboard |= (maptocolumn(fliprow)<<shiftdistance);
		
		//Diagleftrightcheck
		shiftdistance = (row+column-7)<<3;
		playerrow = getdiagleftright((shiftdistance>0)?playerbitboard>>>shiftdistance:playerbitboard<<-shiftdistance);
		opponentrow = getdiagleftright((shiftdistance>0)?opponentbitboard>>>shiftdistance:opponentbitboard<<shiftdistance);
		fliprow = Tables.moveLookup(playerrow, opponentrow, column);
		flipboard |= (shiftdistance>0)?maptodiagleftright(fliprow)<<shiftdistance:maptodiagleftright(fliprow)>>>-shiftdistance;
		
		//Diagrightleft
		shiftdistance = (column-row)<<3;
		playerrow = getdiagrightleft((shiftdistance>0)?playerbitboard<<shiftdistance:playerbitboard>>>-shiftdistance);
		opponentrow = getdiagrightleft((shiftdistance>0)?opponentbitboard<<shiftdistance:opponentbitboard>>>-shiftdistance);
		fliprow = Tables.moveLookup(playerrow, opponentrow, column);
		flipboard |= (shiftdistance>0)?maptodiagleftright(fliprow)>>>shiftdistance:maptodiagleftright(fliprow)<<-shiftdistance;
		return flipboard;
	} 
	

	@Override
	public boolean validCoordinates(Coordinates coord) {
		// TODO Automatisch generierter Methodenstub
		return false;
	}

}
