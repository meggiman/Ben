package glhf;

public class Hash {
	private long[][] ZobristKeys;
	private long[] updateOtherColor;
	public Hash()
	{
		java.util.Random rand = new java.util.Random(0x6015dc3d6d6c31c6L);  //0x188cbbc60e0ee3b8L = a random Long from random.org
		ZobristKeys = new long[64][2];
		updateOtherColor = new long[64];
		for(int i = 0; i < 64; i++)
		{
			ZobristKeys[i][0] = rand.nextLong();
			ZobristKeys[i][1] = rand.nextLong();
//			ZobristKeys[i][0] = 1L<<i;
//			ZobristKeys[i][1] = 0;
			//System.out.println(i + ": Zobrist 0 " + ZobristKeys[i][0] + " Zobrist 1 " + ZobristKeys[i][1]);
			
			updateOtherColor[i] = ZobristKeys[i][0] ^ ZobristKeys[i][1];
		}
	}
	
	public long BoardToHash(long[] board)
	{
		long result = 0L;
		long currentBoard = board[0];
		long opponentBoard = board[1];
		int index = 0;
		for(int i = 0; i < Long.bitCount(board[0]); i++)
		{
			index = Long.numberOfTrailingZeros(currentBoard);
			result ^= ZobristKeys[index][0];
			currentBoard &= ~(C.fields[index]);
		}
		for(int i = 0; i < Long.bitCount(board[1]); i++)
		{
			index = Long.numberOfTrailingZeros(opponentBoard);
			result ^= ZobristKeys[index][1];
			opponentBoard &= ~(C.fields[index]);
		}
		return result;
	}
	public long BoardToHash(Board b)
	{
		return BoardToHash(b.getBoard());
	}
	

	public long[][] getZobristKeys()
	{
		return ZobristKeys;
	}
	/**
	 * @param field input from 0 to 63
	 * @param color input 0 or 1
	 * @return
	 */
	public long getSpecificZobristKey(int field, int color)
	{
		return ZobristKeys[field][color];
	}

	/** Updates the hash if the field that has to be changed was an empty field
	 * 
	 * @param hash
	 * @param field
	 * @param color
	 * @return
	 */
	public long updateHashEmpty(long hash, int field, int color)
	{
		return (hash ^ ZobristKeys[field][color]);
	}
	
	
	/** Updates the hash if the field that has to be changed was an empty field
	 * 
	 * @param hash
	 * @param field
	 * @param color
	 * @return
	 */
	public long updateHashEmpty(long hash, long field, int color)
	{
		return hash ^ ZobristKeys[Long.numberOfTrailingZeros(field)][color];
		
//		for(int i = 0;i < 64; i++)
//		{
//			if(((field >>> i) & 1L) == 1L)
//			{
//				return hash ^ ZobristKeys[i][color];
//			}
//		}
//		return hash;
	}
	
	/**	Updates the hash if the field that has to be changed was a field possessed by the other color
	 * @param hash
	 * @param field
	 * @param color
	 * @return
	 */
	public long updateHashOtherColor(long hash, int field)
	{
		return (hash ^ updateOtherColor[field]);
	}
	
	/**	Updates the hash if the field that has to be changed was a field possessed by the other color
	 * @param hash
	 * @param field
	 * @param color
	 * @return
	 */
	public long updateHashOtherColor(long hash, long field)
	{
		return hash ^ updateOtherColor[Long.numberOfTrailingZeros(field)];
//		for(int i = 0;i < 64; i++)
//		{
//			if(((field >>> i) & 1L) == 1L)
//			{
//				return hash ^ updateOtherColor[i];
//			}
//		}
//		return hash;
	}
}
