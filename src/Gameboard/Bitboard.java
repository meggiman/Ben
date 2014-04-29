package Gameboard;

import reversi.Coordinates;
import reversi.GameBoard;
import reversi.OutOfBoundsException;

/**
 * The Class {@code Bitboard} is an efficient implementation of 8x8 Gameboard for reversi.
 *
 */
public class Bitboard implements GameBoard {
	/**
	 * Bitmask used for Leftshifts of the Bitboard.
	 */
	private static final long leftshiftmask = 0xFEFEFEFEFEFEFEFEL;
	
	/**
	 * Bitmask used for Rightshifts of the whole Bitboard.
	 */
	private static final long rightshiftmask = 0x7F7F7F7F7F7F7F7FL;
	
	/**
	 * 64 random {@code long} constants used to generate zobrist-hashes.
	 */
	private static final long[] zobristrandomred = {	0xfbf185c26c378076L, 0x14fc57c338f4f1a5L, 0x925f95f86089b91dL, 0xf7532bd039d1a1f4L, 
												0xecfbaddab0d6fce0L, 0x2a8ca0dd2626b862L, 0x23d47c43ff36ce38L, 0x3e6baf923c28f448L, 
												0x20d9010265a3f40fL, 0x6388d91cddc1c1cL, 0xafa8e8696ed1738bL, 0x90bc3d08fc1ec9a2L, 
												0x36c017d07c49546bL, 0xf2ef8f4cac6794f1L, 0xf56bb2fcbdc8d2b1L, 0x7fa139f336fc98feL, 
												0x3e4731ff2edbd886L, 0x2341ecdb9dadb92bL, 0x715f9e7d5c37bf11L, 0xeae7498f80a35fa8L, 
												0xcbfb8bf37d9c9a6aL, 0x6519cc1b281cd98eL, 0x1220e41bc588fd4dL, 0xbf7683bbe19659e9L, 
												0x627d82e87a1a5c8fL, 0x66bb352a4233da01L, 0xb118e976734e752dL, 0x45059a12dd803309L, 
												0xde761d1d4fee1963L, 0x77079d25f4eacf3aL, 0x6d9a57385cee69cfL, 0xb1effad871ebfc7eL, 
												0x1a0140e9b3b4f9b1L, 0x3717a23db835e737L, 0x79c6039fc1149b5L, 0x5232f65d4634ab04L, 
												0xa7382a6bc45005a0L, 0x84df5634e6026379L, 0x49fb2991a7b8d155L, 0xbe66b547b3145c54L, 
												0x5a285e00e8e52ce9L, 0xb0023175a5dfd33L, 0x18eb9696bfc766ceL, 0x6898f099c66dfdcbL, 
												0x3c5f157a41176274L, 0x330c473b052343acL, 0xe331484b0c5eea6bL, 0xc8ec9114c856a3cbL, 
												0x77ea0c91db3b5df9L, 0x8fd55922b8489683L, 0x3006266598dc8ceL, 0x441c36d33f203effL, 
												0x34cc29731c5d4a3dL, 0x553bbe5df8f53190L, 0x17ea7878ae609e80L, 0x199cdc8e23095b40L, 
												0xc048ab76910b2c5aL, 0x5d11eefe79c29638L, 0x658cfdf9d3598681L, 0x66fa01853fe29ec3L, 
												0x8485961d728f09b5L, 0xcc1281bbe4c0cc6aL, 0x4d16571ec4e48e15L, 0x957c6ae1831d84faL};

	/**
	 * 64 random {@code long} constants used to generate zobrist-hashes.
	 */
	private static final long[] zobristrandomgreen = {0xc5bb837cfc908843L, 0xfe69cb281253ce62L, 0x683f782b737295f4L, 0xfa35ce4ae312051eL, 
												0x42c73b38abd54779L, 0xa6bcc139d081d3ecL, 0x15ba9b28ba7956b0L, 0x6283347aa7b70f62L, 
												0x2fe227cbe0394798L, 0xf9e9f51b8c1bcd62L, 0xa499cfb3401dae88L, 0xaf44f6d6cc626537L, 
												0xdd8b04996dc30640L, 0xc8eff63264fb72e4L, 0x73167782e4db58eaL, 0x7d92bad68f07579eL, 
												0xf6845abc389b5264L, 0x535602c1d9a6f48eL, 0x5919b13e0e5568f8L, 0x190a954c53a004daL, 
												0xda2d619f9dbae31dL, 0x11365e1683ffb279L, 0x84f41aa83ce66e8eL, 0x1c736a9c06c3f22aL, 
												0xfba6792597e7ffbL, 0x21c609e23d070278L, 0x411f440ed2f335eaL, 0x8accc44df185fac1L, 
												0xb4696c44a66f0ab6L, 0xc0a65c0f2aadc6e9L, 0xee19d03419fe3211L, 0x1429b01059ebf8e5L, 
												0x4f88106b9ba1df0aL, 0x1db20740fe57146bL, 0x31a6735b4c7ed329L, 0x585b8735e1b17690L, 
												0xbfa84f02a44a5049L, 0x63171824e0effc88L, 0x631b3be1593ed5bL, 0xf1de88b282db9797L, 
												0xfe8d2d1068b805eeL, 0x50cb563ecbeb5579L, 0x63a11efffddd4fcdL, 0x809e06c8b3685961L, 
												0x5e4ce0db3a503bacL, 0x44d3e0caf0edac32L, 0x91853bc5766968dfL, 0xc2f3279e4b68386fL, 
												0x9a198dcd2f4d33acL, 0x34374ee42f26df21L, 0xc22fcb599ce87fe0L, 0xd70fd9d9fa5ab8acL, 
												0xd481fdc67f3a56eeL, 0xb175cfd246232343L, 0x5224d059741530cL, 0xf7af6dd1d43b0ebcL, 
												0xfb3305942dec8e66L, 0x11e08678051046b9L, 0x971dcb32e7d6ecd4L, 0x30873bfc1c5ef92dL, 
												0xc7cfaac0a8ac0dcbL, 0xbc4e99466f54709aL, 0x5ac95c64d48d5682L, 0x455000a14de5437L};

	/**
	 * This variable is used by {@code hashCode()} to incrementally calculate zobrist-hashes.   
	 */
	public long hash;
	/**
	 * Representation of all the red stones on the board. the MSB represents upper left corner.
	 * Bit nr. 55 lies one row below upper left corner.
	 */
	public long red = 0;
	/**
	 * Representation of all the red stones on the board. the MSB represents upper left corner.
	 * Bit nr. 55 lies one row below upper left corner.
	 */
	public long green = 0;
	
	/**
	 * Erzeugt ein neues, leeres {@link Bitboard}.
	 */
	public Bitboard(){
	}
	
	/**
	 * Creates a new Bitboard with parameters {@code red} and {@code green} representing the red and green stones.
	 * parameters aren't checked for validity.
	 * @param red all the red stones roten Steine
	 * @param green alle grünen Steine
	 */
	public Bitboard(long red, long green){
		this.red = red;
		this.green = green;
		hash = generateZobristhash();
	}
	
	
	

	public void refreshZobristhash(long flippeddisks, long moovecord, boolean player){
		int index;
		while (flippeddisks!=0) {
			long bit = Long.highestOneBit(flippeddisks);
			flippeddisks ^= bit;
			index = Long.numberOfTrailingZeros(bit);
			hash ^= zobristrandomred[index];
			hash ^= zobristrandomgreen[index];
		}
			hash ^= (player)?zobristrandomred[Long.numberOfTrailingZeros(moovecord)]:zobristrandomgreen[Long.numberOfTrailingZeros(moovecord)];
	}

	public long generateZobristhash(){
		long value = 0;
		for (long bit : bitboardserialize(red)) {
			value ^= zobristrandomred[Long.numberOfTrailingZeros(bit)];
		}
		for (long bit : bitboardserialize(green)) {
			value ^= zobristrandomgreen[Long.numberOfTrailingZeros(bit)];
		}
		return value;
	}
	/**
	 * Creates an Array of long values with one single bit set each.
	 * @param bitboard the bitboard to serialize.
	 * @return the serialized long variable. {@code null} if bitboard == 0
	 */
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
	
	/**
	 * Erzeugt aus einem Array von möglichen Spielzügen und dem ziehenden Spieler ein Array der darausfolgenen {@link Bitboards}.
	 * Die Züge werden nicht auf Gültigkeit geprüft. Liefert kein sinnvolles Ergebnis für illegale Züge.
	 * @param player der ziehende Spieler.
	 * @param moves ein Array aller möglichen Züge.
	 * @return
	 */
	public Bitboard[] getbitboards(boolean player, long[] moves) {
		Bitboard[] nextgameboards  = new Bitboard[moves.length];
		for (int i = 0; i < nextgameboards.length; i++) {
			nextgameboards[i] = ((Bitboard)this.clone());
			nextgameboards[i].makeMove(player, moves[i]);
		}
		return nextgameboards;
	}
	
	/**
	 *Konvertiert {@code coord} in die Bitboard-Repräsentation als long Variable.
	 * @param coord das umzuwandelnde {@link Coordinates} Objekt
	 * @return konvertierte Koordinate.
	 */
	public static long coordinatestolong(Coordinates coord){
		if (coord == null) {
			return 0;
		}
		return 1L<<(8-coord.getCol()+64-8*coord.getRow());
	}
	
	/**
	 * Konvertiert eine Koordinate in Bitboard-Repräsentation zurück in ein {@link Coordinates} Objekt.
	 * @param coord zu konvertierende Koordinate
	 * @return konvertierte Koordinate
	 */
	public static Coordinates longtoCoordinates(long coord){
		if (coord == 0) {
			return null;
		}
		return new Coordinates(1+(Long.numberOfLeadingZeros(coord)>>>3), 1+Long.numberOfLeadingZeros(coord)%8);
	}
	
	/**
	 * Markiert die benachbarten Felder aller Bits in bitboard in einem neuen Bitboard.
	 * @param bitboard das Bitboard, von welchem die Nachbarn berechnet werden sollen.
	 * @return neues Bitboard mit allen markierten Nachbarn ohne die Bits von bitboard selbst.
	 */
	public static long filladjacent(long bitboard){
		long filledbitboard = bitboard;
		filledbitboard |= filledbitboard>>>1 & 0x7f7f7f7f7f7f7f7fL;
		filledbitboard |= filledbitboard>>>8;
		filledbitboard |= filledbitboard<<1 & 0xfefefefefefefefeL;
		filledbitboard |= filledbitboard<<8;
		return filledbitboard^bitboard;
	}
	
	/**
	 * Macht den angegebenen Zug rückgängig.
	 * @param changedfields Die Felder, welche sich durch den Zug geändert haben als long.
	 * @param coord Die Koordinate des Zuges
	 * @param player der Spieler, welcher den Zug ausgeführt hatte.
	 */
	public void undomove(long changedfields, long coord, boolean player) {
		red ^= changedfields;
		green ^= changedfields;
		if (player) {
			red ^= coord;
		}
		else {
			green ^= coord;
		}
		refreshZobristhash(changedfields, coord, player);
	}
	
	/**
	 * Konvertiert ein {@code GameBoard} in ein {@code Bitboard}. Diese Methode ist sparsam einzusetzen, da sie jedes Spielfeld einzeln abfragen muss.
	 * @param gb Das umzuwandelne {@link GameBoard}
	 * @return Das konvertierte Bitboard
	 */
	public static Bitboard convert(GameBoard gb) {
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
		return new Bitboard(red, green);
	}
	
	/**
	 * @param player der ziehende Spieler. Verwende {@code true} für rot oder {@code false} für grün.
	 * @return gibt die Bitboard-Repräsentation aller möglichen Züge zurück. Sind keine Züge möglich, wird 0 zurück gegeben.
	 */
	public long possiblemoves(boolean player){
		long emptyfields = ~(red|green);
		long validmoves = 0;
		long potentialmoves;
		long playerfields;
		long otherplayerfields;
		if (player) {
			playerfields = red;
			otherplayerfields = green;
		}
		else {
			playerfields = green;
			otherplayerfields = red;
		}
		//leftshift
		potentialmoves = (((playerfields<<1)&leftshiftmask&otherplayerfields)<<1)&leftshiftmask;
		while (potentialmoves!=0) {
			validmoves |= (potentialmoves&emptyfields);
			potentialmoves = (potentialmoves&otherplayerfields)<<1&leftshiftmask;
		}
		//rightshift
		potentialmoves = (((playerfields>>>1)&rightshiftmask&otherplayerfields)>>>1)&rightshiftmask;
		while (potentialmoves!=0) {
			validmoves |= (potentialmoves&emptyfields);
			potentialmoves = (potentialmoves&otherplayerfields)>>>1&rightshiftmask;
		}
		//upshift
		potentialmoves = (((playerfields<<8)&otherplayerfields)<<8);
		while (potentialmoves!=0) {
			validmoves |= (potentialmoves&emptyfields);
			potentialmoves = (potentialmoves&otherplayerfields)<<8;
		}
		//downshift
		potentialmoves = (((playerfields>>>8)&otherplayerfields)>>>8);
		while (potentialmoves!=0) {
			validmoves |= (potentialmoves&emptyfields);
			potentialmoves = (potentialmoves&otherplayerfields)>>>8;
		}
		//upleftshift
		potentialmoves = (((playerfields<<9)&leftshiftmask&otherplayerfields)<<9)&leftshiftmask;
		while (potentialmoves!=0) {
			validmoves |= (potentialmoves&emptyfields);
			potentialmoves = (potentialmoves&otherplayerfields)<<9&leftshiftmask;
		}
		//uprightshift
		potentialmoves = (((playerfields<<7)&rightshiftmask&otherplayerfields)<<7)&rightshiftmask;
		while (potentialmoves!=0) {
			validmoves |= (potentialmoves&emptyfields);
			potentialmoves = (potentialmoves&otherplayerfields)<<7&rightshiftmask;
		}
		//downleftshift
		potentialmoves = (((playerfields>>>7)&leftshiftmask&otherplayerfields)>>>7)&leftshiftmask;
		while (potentialmoves!=0) {
			validmoves |= (potentialmoves&emptyfields);
			potentialmoves = (potentialmoves&otherplayerfields)>>>7&leftshiftmask;
		}
		//downrightshift
		potentialmoves = (((playerfields>>>9)&rightshiftmask&otherplayerfields)>>>9)&rightshiftmask;
		while (potentialmoves!=0) {
			validmoves |= (potentialmoves&emptyfields);
			potentialmoves = (potentialmoves&otherplayerfields)>>>9&rightshiftmask;
		}
		return validmoves;
	}
	
	@Override
	public boolean checkMove(int player, Coordinates coord) {
		return ((coordinatestolong(coord)&possiblemoves(player==RED))!=0);
	}

	@Override
	public int countStones(int player) {
		if (player == RED) {
			return countStones(true);
		}
		else if (player == GREEN) {
			return countStones(false);
		}
		else {
			return 0;
		}
	}

	public int countStones(boolean player){
		return Long.bitCount((player)?red:green);
	}
	
	@Override
	public int getOccupation(Coordinates coord) throws OutOfBoundsException {
		long coords = coordinatestolong(coord);
		if ((coords&red)!=0) {
			return RED;
		}
		else if ((coords&green) != 0) {
			return GREEN;
		}
		else {
			return EMPTY;
		}
	}

	@Override
	public int getSize() {
		return 8;
	}

	@Override
	public boolean isFull() {
		return (red&green)==0xFFFFFFFFFFFFFFFFL;
	}

	@Override
	public boolean isMoveAvailable(int player) {
		return possiblemoves(player==RED)==0;
	}
	
	/**
	 * 
	 * @return {@code true} wenn das Spiel entschieden ist. Ansonsten {@code false}.
	 */
	public boolean isFinished(){
		return isMoveAvailable(RED) && isMoveAvailable(GREEN);
	}
	
	@Override
	public void makeMove(int player, Coordinates coord) {
		makeMove(player==RED, coordinatestolong(coord));
	}
	
	/**
	 * Diese Methode setzt einen Stein für den angegebenen Spieler auf dem angegebenen Feld, und dreht Steine des Gegners gemäss den Regeln um.
	 * Es wird nicht überprüft, ob der angegeben Zug legal ist. Der Zobrist-Hash wird durch den Aufruf dieser Methode aktualisiert.
	 * @param player der ziehende Spieler. Verwende {@code true} für rot oder {@code false} für grün.
	 * @param coord die Bitboard-Repräsentation des Zuges.
	 * @return die Bitmaske aller umgedrehter Steine. Kann verwendet werden um den Zug rückgängig zu machen oder um inkrementelle Hashes zu berechnen.
	 */
	public long makeMove(boolean player, long coord) {
		long playerfields;
		long otherplayerfields;
		long cursor;
		long possiblychangedfields=0;
		long changedfields=0;
		if (coord == 0) {
			return 0;
		}
		if (player) {
			playerfields = red;
			otherplayerfields = green;
		}
		else {
			playerfields = green;
			otherplayerfields = red;
		}
		//leftshift
		cursor = coord<<1&leftshiftmask;
		while (cursor !=0){
			cursor &= otherplayerfields;
			possiblychangedfields |= cursor;
			cursor = (cursor << 1) & leftshiftmask;
			if ((cursor&playerfields)!=0) {
				changedfields|=possiblychangedfields;
				break;
			}
		}
		//rightshift
		possiblychangedfields=0;
		cursor=coord>>>1&rightshiftmask;
		while (cursor !=0){
			cursor &= otherplayerfields;
			possiblychangedfields |= cursor;
			cursor = (cursor >>> 1) & rightshiftmask;
			if ((cursor&playerfields)!=0) {
				changedfields|=possiblychangedfields;
				break;
			}
		}
		//upshift
		possiblychangedfields=0;
		cursor=coord<<8;
		while (cursor !=0) {
			cursor &= otherplayerfields;
			possiblychangedfields |= cursor;
			cursor = (cursor << 8);
			if ((cursor&playerfields)!=0) {
				changedfields|=possiblychangedfields;
				break;
			}
		}
		//downshift
		possiblychangedfields=0;
		cursor=coord>>>8;
		while (cursor !=0){
			cursor &= otherplayerfields;
			possiblychangedfields |= cursor;
			cursor = (cursor >>> 8);
			if ((cursor&playerfields)!=0) {
				changedfields|=possiblychangedfields;
				break;
			}
		} 
		//upleftshift
		possiblychangedfields=0;
		cursor=coord<<9&leftshiftmask;
		while (cursor !=0) {
			cursor &= otherplayerfields;
			possiblychangedfields |= cursor;
			cursor = (cursor << 9) & leftshiftmask;
			if ((cursor&playerfields)!=0) {
				changedfields|=possiblychangedfields;
				break;
			}
		}
		//uprightshift
		possiblychangedfields=0;
		cursor=coord<<7&rightshiftmask;
		while (cursor !=0){
			cursor &= otherplayerfields;
			possiblychangedfields |= cursor;
			cursor = (cursor << 7) & rightshiftmask;
			if ((cursor&playerfields)!=0) {
				changedfields|=possiblychangedfields;
				break;
			}
		}
		//downleftshift
		possiblychangedfields=0;
		cursor=coord>>>7&leftshiftmask;
		while (cursor !=0) {
			cursor &=otherplayerfields;
			possiblychangedfields |= cursor;
			cursor = (cursor >>> 7) & leftshiftmask;
			if ((cursor&playerfields)!=0) {
				changedfields|=possiblychangedfields;
				break;
			}
		}
		//downrightshift
		possiblychangedfields=0;
		cursor=coord>>>9&rightshiftmask;
		while (cursor !=0) {
			cursor &= otherplayerfields;
			possiblychangedfields |= cursor;
			cursor = (cursor >>> 9) & rightshiftmask;
			if ((cursor&playerfields)!=0) {
				changedfields|=possiblychangedfields;
				break;
			}
		}
		playerfields ^= changedfields|coord;
		otherplayerfields ^= changedfields;
		if (player) {
			red = playerfields;
			green = otherplayerfields;
		}
		else {
			green = playerfields;
			red = otherplayerfields;
		}
		refreshZobristhash(changedfields, changedfields, player);
		return changedfields;
	}
	
	/**
	 * Führ den angegebenen Zug auf einer Kopie von {@code this} aus. Der Zobrist Hash wird dabei aktualisiert. Wird dieser für kein von diesem Objekt stammendes {@code Bitboard} benötigt, 
	 * sollte aus Performancegründen die Methode {@link Bitboard#makeMove(boolean, long) makeMove} auf einem kopierten Objekt ausgeführt werden.
	 * @param player der ziehende Spieler. Verwende {@code true} für rot oder {@code false} für grün.
	 * @param coord die Bitboard-Repräsentation des Zuges.
	 * @return eine Kopie des {@code Bitboards, auf welchem der angegebene Zug ausgeführt wurde}
	 */
	public Bitboard copyandmakemove(boolean player, long coord){
		Bitboard gb = new Bitboard(this.red, this.green);
		refreshZobristhash(gb.makeMove(player, coord), coord, player);
		return gb;
	}
	
	@Override
	public boolean validCoordinates(Coordinates coord) {
		return coord.getCol()<9 && coord.getRow()<9 && coord.getCol()>0 && coord.getRow()>0;
	}
	
	@Override
	public GameBoard clone(){
		return new Bitboard(this.red,this.green);
	}

}
