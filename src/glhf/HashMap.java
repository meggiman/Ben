package glhf;

import java.util.concurrent.TimeoutException;

public class HashMap {
	
//	private long[] hash;
//	private int[][] value;
	
	private HashValue[] map;
	
	private int size;
	
	public int collisions;
	
	// value[i][0] = move = Long.trailingzeros(longmove)
	// value[i][1] = value
	// value[i][2] = age (= ply) (=remainingStones)
	// value[i][3] = Long.bitCount(availableMoves)
	// value[i][4] = depth;
	
	
	public HashMap(int size)
	{
//		hash = new long[size];
//		value = new int[size][5];
		map = new HashValue[size];
		for(int i = 0; i < size; i++)
			map[i] = new HashValue();
		this.size = size;
		collisions = 0;
	}
	
	/**
	 * @param age ex. renew(board.remainingstones)
	 */
	public void renew(int age)
	{
		for(int i = 0; i < size; i++)
		{
			if(map[i].remainingStones > age)
			{
				map[i] = new HashValue();
			}
		}
		collisions = 0;
	}
	
	public void addHash(HashValue value)
	{
		int index = getIndex(value.hash);
		if(map[index].hash == value.hash)
		{
			if(value.remainingStones - value.depth <= map[index].remainingStones - map[index].depth)
			{
				map[index] = value;
			}
		}
		else if(map[index].hash == 0L)
		{
			if(value.depth > 2)		map[index] = value;
		}
		else
		{
			collisions++;
			if(value.remainingStones - value.depth <= map[index].remainingStones - map[index].depth)
			{
				map[index] = value;
			}
		}
	}
	
	public HashValue searchHash(long hash)
	{
		int index = getIndex(hash);
		if(map[index].hash == 0L)
			return null;
		return map[index];
	}
	
	private int getIndex(long hash)
	{
//		return (int) (Math.abs(hash) % size);

		
		return (int) (hash & (size-1));
	}
	
	/**
	 * @return size in MB (MegaByte)
	 */
	public float size()
	{
//		return (size * 64 / 1000 + size * 5 * 32 / 1000) / 1000 / 8;
		return (HashValue.size() * size) / 1000 / 1000;
	}

}
