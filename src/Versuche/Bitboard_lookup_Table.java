package Versuche;

import Gameboard.Bitboard;
import reversi.Coordinates;
import reversi.GameBoard;
import reversi.OutOfBoundsException;

public class Bitboard_lookup_Table extends Bitboard implements GameBoard{
	
	public Bitboard_lookup_Table(long red, long green){
		super(red,green);
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
	
	public long likelyMoves(boolean player){
		if (player) {
			return filladjacent(green)& ~red;
		}
		else {
			return filladjacent(red)& ~green;
		}
	}
	
//	public long possiblemoves(boolean player){
//		long[] likelyMoves = bitboardserialize(likelyMoves(player));
//		long possiblemoves=0;
//		for (long l : likelyMoves) {
//			if (getflipboard(player, l)!=0) {
//				possiblemoves|=l;
//			}
//		}
//		return possiblemoves;
//	}
	
	@Override
	public GameBoard clone(){
		return new Bitboard_lookup_Table(red, green);
	}
	
	public static Bitboard convert(GameBoard gb){
		long red = 0;
		long green = 0;
		Coordinates coord;
		try {
			for (int i = 1; i < 9; i++) {
				for (int j = 1; j < 9; j++) {
					coord = new Coordinates(i, j);
					int occupation = gb.getOccupation(coord);
					if (occupation  != GameBoard.EMPTY) {
						if(occupation == GameBoard.RED){
							red |= Bitboard.coordinatestolong(coord);
						}
						else {
							green |= Bitboard.coordinatestolong(coord);
						}
					}
				}
			}
		} catch (OutOfBoundsException e) {
		}
		return new Bitboard_lookup_Table(red, green);
	}
	
	public long makeMove(boolean player, long coord){
		long flipboard = getflipboard(player, coord);
		red^=flipboard;
		green^=flipboard;
		if (player) {
			red|=coord;
		}
		else {
			green|=coord;
		}
		refreshZobristhash(flipboard, coord, player);
		return flipboard;
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
		opponentrow = getdiagleftright((shiftdistance>0)?opponentbitboard>>>shiftdistance:opponentbitboard<<-shiftdistance);
		fliprow = Tables.moveLookup(playerrow, opponentrow, column);
		flipboard |= (shiftdistance>0)?maptodiagleftright(fliprow)<<shiftdistance:maptodiagleftright(fliprow)>>>-shiftdistance;
		
		//Diagrightleft
		shiftdistance = (column-row)<<3;
		playerrow = getdiagrightleft((shiftdistance>0)?playerbitboard<<shiftdistance:playerbitboard>>>-shiftdistance);
		opponentrow = getdiagrightleft((shiftdistance>0)?opponentbitboard<<shiftdistance:opponentbitboard>>>-shiftdistance);
		fliprow = Tables.moveLookup(playerrow, opponentrow, column);
		flipboard |= (shiftdistance>0)?maptodiagrightleft(fliprow)>>>shiftdistance:maptodiagrightleft(fliprow)<<-shiftdistance;
		return flipboard;
	}

}
