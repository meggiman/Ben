package glhf;

public class HashValue
{
	
	public byte depth;
	public byte remainingStones;
	public short score;
	public byte moveInt;
	public long hash;
	/**
	 * @param depth
	 * @param remainingStones
	 * @param score
	 * @param moveInt
	 * @param availableMovesCount
	 */
	public HashValue(byte depth, byte remainingStones, short score, byte moveInt, long hash)
	{
		this.depth = depth;
		this.remainingStones = remainingStones;
		this.score = score;
		this.moveInt = moveInt;
		this.hash = hash;
	}
	public HashValue()
	{
		this.depth = 0;
		this.remainingStones = 0;
		this.score = 0;
		this.moveInt = 0;
		this.hash = 0L;
	}
	/**
	 * @param numberOfTrailingZeros
	 * @param alpha
	 * @param remainingStones2
	 * @param bitCount
	 * @param depth2
	 * @param availableMovesCount2
	 */
	public HashValue(int numberOfTrailingZeros, int alpha, int remainingStones2, int depth2, long hash)
	{
		depth = (byte) depth2;
		remainingStones = (byte) remainingStones2;
		score = (short) alpha;
		moveInt = (byte) numberOfTrailingZeros;
		this.hash = hash;
	}
	
	/**
	 * @return size of one HashValue in bytes
	 */
	public static int size()
	{
		return 13;
	}
	
	public String toString()
	{
		return "depth: " + depth + " remainingStones: " + remainingStones + " score: " + score + " moveInt: " + moveInt + " hash: " + hash;
	}
}
