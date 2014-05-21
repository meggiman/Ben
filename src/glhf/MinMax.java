package glhf;

import java.util.concurrent.TimeoutException;

public class MinMax
{
	public static long timeout;
	private long bestMove;
	public long nodes;
	public long cuts;
	public long hashHits;
	private long[] fields;
	
	public MinMax()
	{
		fields = new long[60];
		int index = 0;
		for(int i = 0; i < 64; i++)
		{
			if(i != 27 & i != 28 & i != 35 & i != 36)
			{
				fields[index] = 1L << i;
			}
			else
			{
				index--;
			}
			index++;
		}
	}
	
	public void initialize()
	{
		fields = new long[60];
		int index = 0;
		for(int i = 0; i < 64; i++)
		{
			if(i != 27 & i != 28 & i != 35 & i != 36)
			{
				fields[index] = 1L << i;
			}
			else
			{
				index--;
			}
			index++;
		}
	}
	
	/**
	 * @param board
	 * @param depth
	 * @param alpha
	 * @param beta
	 * @return
	 * @throws TimeoutException
	 */
	public int getNextMove(Board board, int depth, int alpha, int beta) throws TimeoutException
	{
		nodes++;
		if(timeout()) 	throw new TimeoutException();
		long moves = board.availableMoves();
		if(depth <= 0 | board.gameHasEnded(moves))	return board.getScore(moves);
		
		long best = 0L;
		int result = -1000;
		
		
		if(moves == 0L) //pass
		{
			board.swap();
			result = - getNextMoveBeta(board, depth -1, -beta, -alpha);
			bestMove = 0L;
			return result;
		}
		for(int i = 0; i < 60; i++)
		{
			if((moves & fields[i]) == 0){}
			else
			{
				long move = fields[i];
				result = - getNextMoveBeta(board.makeMove(move), depth-1, -beta, -alpha);
				if(result >= beta)
				{
//					bestMove = move;
					cuts++;
//					int[] value2 = {Long.numberOfTrailingZeros(move), beta, board.remainingStones(), Long.bitCount(moves), depth};
//					C.hashMap.addHash(board.getHash(), value2);
					return beta;
				}
				if(result > alpha)
				{
					alpha = result;
					best = move;
				}
				
			}
		}
		bestMove = best;
//		int[] value2 = {Long.numberOfTrailingZeros(best), alpha, board.remainingStones(), Long.bitCount(moves), depth};
		HashValue value2 = new HashValue(Long.numberOfTrailingZeros(best), alpha, board.remainingStones(), depth, board.getHash());
		C.hashMap.addHash(value2);
		return alpha;
	}
	
	public int getNextMoveBeta(Board board, int depth, int alpha, int beta) throws TimeoutException
	{
		nodes++;
		if(timeout()) 	throw new TimeoutException();
		long movesLong = board.availableMoves();
		if(depth <= 0 | board.gameHasEnded(movesLong))	return board.getScore(movesLong);
		
		
		
		long best = 0L;
		int result = -1000;
		
		long[] moves = board.sortMidGame(movesLong);
		if(moves.length == 0) //pass
		{
			board.swap();
			result = - getNextMoveBeta(board, depth -1, -beta, -alpha);
			//bestMove = 0L;
			return result;
		}
		
		//HashTable
		HashValue value = C.hashMap.searchHash(board.getHash());
		if(value != null)
		{
			long moveHashTable = 1L << value.moveInt;
			if(value.hash == board.getHash())
			{
				for(int i = 1; i < moves.length; i++)
				{
					//long move = moves[i];
					if(moves[i] == moveHashTable)
					{
						//put best move in front
						
						hashHits++;
						long mt = moves[i];
						moves[i] = moves[0];
						moves[0] = mt;	
						
						break;
					}
				}
			}
		}
		
		
		for(long move : moves)
		{
			result = - getNextMoveBeta(board.makeMove(move), depth-1, -beta, -alpha);
			if(result >= beta)
			{
				//bestMove = best;
				cuts++;
//				int[] value2 = {Long.numberOfTrailingZeros(move), beta, board.remainingStones(), moves.length, depth};
//				C.hashMap.addHash(board.getHash(), value2);
				return beta;
			}
			if(result > alpha)
			{
				alpha = result;
				best = move;
			}
			
		}
		//bestMove = best;
//		int[] value2 = {Long.numberOfTrailingZeros(best), alpha, board.remainingStones(), moves.length, depth};
		HashValue value2 = new HashValue(Long.numberOfTrailingZeros(best), alpha, board.remainingStones(), depth, board.getHash());
		C.hashMap.addHash(value2);
		return alpha;
	}
	
	/**
	 * @param board
	 * @param depth
	 * @param alpha
	 * @param beta
	 * @return
	 * @throws TimeoutException
	 */
	public int getNextMoveOld(Board board ,int depth, int alpha, int beta) throws TimeoutException
	{
		nodes++;
		if(timeout()) 	throw new TimeoutException();
		long movesLong = board.availableMoves();
		if(depth <= 0 | board.gameHasEnded(movesLong))	return board.getScore(movesLong);
		
		
		
		long best = 0L;
		int result = -1000;
		
		long[] moves = board.sortMidGame(movesLong);
		if(moves.length == 0) //pass
		{
			board.swap();
			result = - getNextMoveOld(board, depth -1, -beta, -alpha);
			bestMove = 0L;
			return result;
		}
		
		//HashTable
		HashValue value = C.hashMap.searchHash(board.getHash());
		if(value != null)
		{
			long moveHashTable = 1L << value.moveInt;
			if(value.hash == board.getHash())
			{
				for(int i = 1; i < moves.length; i++)
				{
					//long move = moves[i];
					if(moves[i] == moveHashTable)
					{
						//put best move in front
						
						hashHits++;
						long mt = moves[i];
						moves[i] = moves[0];
						moves[0] = mt;	
						
						break;
					}
				}
			}
		}
		
		
		for(long move : moves)
		{
			result = - getNextMoveOld(board.makeMove(move), depth-1, -beta, -alpha);
			if(result >= beta)
			{
//				bestMove = move;
				cuts++;
//				int[] value2 = {Long.numberOfTrailingZeros(move), beta, board.remainingStones(), moves.length, depth};
//				C.hashMap.addHash(board.getHash(), value2);
				return beta;
			}
			if(result > alpha)
			{
				alpha = result;
				best = move;
			}
			
		}
		bestMove = best;
//		int[] value2 = {Long.numberOfTrailingZeros(best), alpha, board.remainingStones(), moves.length, depth};
		HashValue value2 = new HashValue(Long.numberOfTrailingZeros(best), alpha, board.remainingStones(), depth, board.getHash());
		C.hashMap.addHash(value2);
		return alpha;
		
	}
	
	public int endGame(Board board, int alpha, int beta) throws TimeoutException
	{
		nodes++;
		if(board.gameHasEnded())	return board.stoneDifference();
		if(timeout())				throw new TimeoutException();
		long best = 0L;
		int result = 0;
		
		long[] moves = board.availableMovesArray();
		if(moves.length == 0) //passen
		{
			board.swap();
			result = - endGame(board, -beta, -alpha);
			bestMove = 0L;
			return result;
		}
		
		//HashTable
//		int[] value = hashTable.searchHash(board.getHash());
//		if(value != null)
//		{
//			long moveHashTable = 1 << value[0];
//			for(int i = 1; i < moves.length; i++)
//			{
//				long move = moves[i];
//				if(moves[i] == moveHashTable)
//				{
					//put best move in front
					
//					long mt = moves[i];
//					moves[i] = moves[0];
//					moves[0] = mt;	
//					
//					break;
//				}
//			}
//		}
		
		for(long move : moves)
		{
			result = - endGame(board.makeMoveWithoutHash(move), -beta, -alpha);
			if(result >= beta)
			{
				bestMove = best;
				cuts++;
				return beta;
			}
			if(result > alpha)
			{
				alpha = result;
				best = move;
			}
			
		}
		bestMove = best;
//		int[] value2 = {Long.numberOfTrailingZeros(best), alpha, board.remainingStones(), moves.length};
//		hashTable.addHash(board.getHash(), value2);
		return alpha;
	}
	
	public int endGameMax(Board board) throws TimeoutException
	{
		nodes++;
		if(board.gameHasEnded())	return board.stoneDifference();
		if(timeout())				throw new TimeoutException();
		long best = 0L;
		int result = 0;
		int bestScore = -100;
		
		
		long[] moves = board.availableMovesArray();
		if(moves.length == 0) //passen
		{
			board.swap();
			result = - endGameMax(board);
			bestMove = 0L;
			return result;
		}
		
		
		for(long move : moves)
		{
			result = - endGameMax(board.makeMoveWithoutHash(move));
			if(result > bestScore)
			{
				bestScore = result;
				best = move;
			}
		}
		bestMove = best;
		
		return bestScore;
	}
	
	
	public int endGameNegaMax(Board board, int alpha, int beta, long movesLong) throws TimeoutException 
	{
		nodes++;
		if(timeout()) 	throw new TimeoutException();
		if(board.gameHasEnded(movesLong))	return board.stoneDifference();
		
		
		
		long best = 0L;
		int result = 0;
		
		long[][] moves = board.sortEndGameNew(movesLong);
		if(moves.length == 0) //pass
		{
			board.swap();
			result = - endGameNegaMax(board, -beta, -alpha, board.availableMoves());
			bestMove = 0L;
			return result;
		}
		
		
		
		
		for(int i = 0; i < moves.length; i++)
		{
			long move = moves[i][0]; 
			result = - endGameNegaMax(board.makeMoveWithoutHash(move), -beta, -alpha, moves[i][1]);
			
			
			if(result >= beta)
			{
//				bestMove = best;
				cuts++;
				return result;
			}
			if(result > alpha)
			{
				alpha = result;
				best = move;
			}
		}
		bestMove = best;
//		int[] value2 = {Long.numberOfTrailingZeros(best), alpha, board.remainingStones(), moves.length};
//		hashTable.addHash(board.getHash(), value2);
		return alpha;
		
	}
	public int endGameNegaMaxNew(Board board, int alpha, int beta, long availableMoves) throws TimeoutException 
	{
		nodes++;
		if(timeout()) 	throw new TimeoutException();
		if(board.gameHasEnded(availableMoves))	return board.stoneDifference();
		
		
		int bestScore = -65;
		long best = 0L;
		int result = 0;
		
		long[][] moves = board.sortEndGameNew(availableMoves);
		if(moves.length == 0) //pass
		{
			board.swap();
			result = - endGameNegaMaxNew(board, -beta, -alpha, board.availableMoves());
			bestMove = 0L;
			return result;
		}
		
		//HashTable
		HashValue value = C.hashMap.searchHash(board.getHash());
		if(value != null)
		{
			if(value.hash == board.getHash())
			{
				long movehashTable = 1L << value.moveInt;
				hashHits++;
				if(value.depth >= board.remainingStones())
				{
					bestMove = movehashTable;
					return value.score;
				}
				for(int i = 1; i < moves.length; i++)
				{
					//long move = moves[i];
					if(moves[i][0] == movehashTable)
					{
						long[] mt = moves[i];
						moves[i][0] = moves[0][0];
						moves[0] = mt;	
						
						break;
					}
				}
			}
		}
		
		
		for(int i = 0; i < moves.length; i++)
		{
			long move = moves[i][0]; 
			result = - endGameNegaMaxNew(board.makeMove(move), -beta, -alpha, moves[i][1]);
			
			
			if(result >= beta)
			{
//				bestMove = best;
				cuts++;
				return result;
			}
			if(result > bestScore)
			{
				bestScore = result;
				if(result > alpha)
				{
					alpha = result;
				}
				best = move;
			}
		}
		bestMove = best;
		if(best != 0L) C.hashMap.addHash(new HashValue(Long.numberOfTrailingZeros(best), alpha, board.remainingStones(), board.remainingStones(), board.getHash()));
		return alpha;
		
	}
	
	public int endGameNegaCStar(Board board, int min, int max) throws TimeoutException
	{
		int score = min;
		while(min < max)
		{
			int alpha = (min+max) / 2;
			score = endGameNegaMaxNew(board, alpha, alpha+1, board.availableMoves());
			if(score > alpha)	min = score;
			else				max = score;
		}
		return score;
	}
	
	public int MTDf(int guess, Board board) throws TimeoutException
	{
		int min = -65;
		int max = 65;
		while(min < max)
		{
			int beta = 0;
			if(guess == min)	beta = guess + 1;
			else				beta = guess;
			guess = endGameNegaMaxNew(board, beta-1, beta, board.availableMoves());
			if(guess < beta)	max = guess;
			else 				min = guess;
		}
		return guess;
	}
	
	public int negaScout(Board board, int depth, int alpha, int beta) throws TimeoutException 
	{
		nodes++;
		if(timeout()) 	throw new TimeoutException();
		long movesLong = board.availableMoves();
		if(depth <= 0 | board.gameHasEnded(movesLong))	return board.getScore(movesLong);
		
		
		
		long best = 0L;
		int result = 0;
		
		int b;
		b = beta;
		
		long[] moves = board.sortMidGame(movesLong);
		if(moves.length == 0) //pass
		{
			board.swap();
			result = - negaScout(board, depth -1, -beta, -alpha);
			bestMove = 0L;
			return result;
		}
		
		//HashTable
		HashValue value = C.hashMap.searchHash(board.getHash());
		if(value != null)
		{
			long moveHashTable = 1L << value.moveInt;
			if(value.hash == board.getHash())
			{
				hashHits++;
				//if value.depth >= depth return value.score
				if(value.depth >= depth)
				{
					bestMove = moveHashTable;
					return value.score;
				}
				//else try to put value.bestMove in front of all the moves.
				for(int i = 1; i < moves.length; i++)
				{
					//long move = moves[i];
					if(moves[i] == moveHashTable)
					{
						long mt = moves[i];
						moves[i] = moves[0];
						moves[0] = mt;	
						
						break;
					}
				}
			}
		}
		
		for(int i = 0; i < moves.length; i++)
		{
			long move = moves[i]; 
			Board bo = board.makeMove(move);
			result = - negaScout(bo, depth-1, -b, -alpha);
			
			if((result> alpha) && (result < beta) && (i > 0))
				result = - negaScout(bo, depth-1, -beta, -result);
			if(result >= beta)
			{
//				bestMove = move;
				cuts++;
//				int[] value2 = {Long.numberOfTrailingZeros(move), beta, board.remainingStones(), moves.length, depth};
//				C.hashMap.addHash(board.getHash(), value2);
				return result;
			}
			if(result > alpha)
			{
				alpha = result;
				best = move;
			}
			
			b = alpha + 1;
		}
		bestMove = best;
//		int[] value2 = {Long.numberOfTrailingZeros(best), alpha, board.remainingStones(), moves.length, depth};
		if(best != 0L) C.hashMap.addHash(new HashValue(Long.numberOfTrailingZeros(best), alpha, board.remainingStones(), depth, board.getHash()));
		return alpha;
	}
	
	public int negaScoutNewer(Board board, int depth, int alpha, int beta) throws TimeoutException 
	{
		nodes++;
		if(timeout()) 	throw new TimeoutException();
		long movesLong = board.availableMoves();
		if(depth <= 0 | board.gameHasEnded(movesLong))	return board.getScore(movesLong);
		
		
		
		long best = 0L;
		int result = 0;
		
		int b;
		b = beta;
		
		long[] moves = board.sortMidGame(movesLong);
		if(moves.length == 0) //pass
		{
			board.swap();
			result = - negaScoutNewer(board, depth -1, -beta, -alpha);
			bestMove = 0L;
			return result;
		}
		long hashSaved = board.getHash();
		
		//HashTable
		HashValue value = C.hashMap.searchHash(board.getHash());
		if(value != null)
		{
			long moveHashTable = 1L << value.moveInt;
			if(value.hash == board.getHash())
			{
				for(int i = 1; i < moves.length; i++)
				{
					//long move = moves[i];
					if(moves[i] == moveHashTable)
					{
						//put best move in front
						
						hashHits++;
						long mt = moves[i];
						moves[i] = moves[0];
						moves[0] = mt;	
						
						break;
					}
				}
			}
		}
		
		for(int i = 0; i < moves.length; i++)
		{
			long move = moves[i]; 
			long disksToFlip = board.disksToFlip(move);
			board.flipDisks(disksToFlip, move);
			result = - negaScoutNewer(board, depth-1, -b, -alpha);
			
			//revert move
			board.setHash(hashSaved);
			board.undoMove(disksToFlip, move);
			
			if((result> alpha) && (result < beta) && (i > 0))
			{
				board.flipDisks(disksToFlip, move);
				result = - negaScoutNewer(board, depth-1, -beta, -result);
				//revert move
				board.setHash(hashSaved);
				board.undoMove(disksToFlip, move);
			}
//			board.setHash(hashSaved);
//			board.undoMove(disksToFlip, board.getColor(), move);
			
			if(result >= beta)
			{
				bestMove = move;
				cuts++;
//				int[] value2 = {Long.numberOfTrailingZeros(move), beta, board.remainingStones(), moves.length, depth};
//				C.hashMap.addHash(board.getHash(), value2);
				return beta;
			}
			if(result > alpha)
			{
				alpha = result;
				best = move;
			}
			
			b = alpha + 1;
		}
		bestMove = best;
//		int[] value2 = {Long.numberOfTrailingZeros(best), alpha, board.remainingStones(), moves.length, depth};
		HashValue value2 = new HashValue(Long.numberOfTrailingZeros(best), alpha, board.remainingStones(), depth, board.getHash());
		C.hashMap.addHash(value2);
		return alpha;
	}
	
	public int negaScoutBeta(Board board, int depth, int alpha, int beta) throws TimeoutException 
	{
		nodes++;
		if(timeout()) 	throw new TimeoutException();
		long movesLong = board.availableMoves();
		if(depth <= 0 | board.gameHasEnded(movesLong))	return board.getScore(movesLong);
		
		
		
		long best = 0L;
		int result = 0;
		
		int b;
		b = beta;
		
		long[] moves = board.sortMidGame(movesLong);
		if(moves.length == 0) //pass
		{
			board.swap();
			result = - negaScoutBeta(board, depth -1, -beta, -alpha);
			//bestMove = 0L;
			return result;
		}
		
		//HashTable
		HashValue value = C.hashMap.searchHash(board.getHash());
		if(value != null)
		{
			long moveHashTable = 1L << value.moveInt;
			if(value.hash == board.getHash())
			{
				for(int i = 1; i < moves.length; i++)
				{
					//long move = moves[i];
					if(moves[i] == moveHashTable)
					{
						//put best move in front
						hashHits++;
						long mt = moves[i];
						moves[i] = moves[0];
						moves[0] = mt;	
						
						break;
					}
				}
			}
		}
		
		for(int i = 0; i < moves.length; i++)
		{
			long move = moves[i]; 
			Board bo = board.makeMove(move);
			result = - negaScoutBeta(bo, depth-1, -b, -alpha);
			
			if((result> alpha) && (result < beta) && (i > 0))
				result = - negaScoutBeta(bo, depth-1, -beta, -result);
			if(result >= beta)
			{
				//bestMove = best;
				cuts++;
//				int[] value2 = {Long.numberOfTrailingZeros(move), beta, board.remainingStones(), moves.length, depth};
//				C.hashMap.addHash(board.getHash(), value2);
				return beta;
			}
			if(result > alpha)
			{
				alpha = result;
				best = move;
			}
			
			b = alpha + 1;
		}
		//bestMove = best;
//		int[] value2 = {Long.numberOfTrailingZeros(best), alpha, board.remainingStones(), moves.length, depth};
		if(best != 0L) C.hashMap.addHash(new HashValue(Long.numberOfTrailingZeros(best), alpha, board.remainingStones(), depth, board.getHash()));
		
		return alpha;
	}

	public int negaScoutNew(Board board, int depth, int alpha, int beta) throws TimeoutException
	{
		nodes++;
		
		long best = 0L;
		int result = 0;
		
		int b;
		b = beta;
		
		long movesLong = board.availableMoves();
		
		long[] moves = board.sortMidGame(movesLong);
		if(moves.length == 0) //pass
		{
			board.swap();
			result = - negaScoutBeta(board, depth -1, -beta, -alpha);
			bestMove = 0L;
			return result;
		}
		//HashTable
		HashValue value = C.hashMap.searchHash(board.getHash());
		if(value != null)
		{
			long moveHashTable = 1L << value.moveInt;
			if(value.hash == board.getHash())
			{
				for(int i = 1; i < moves.length; i++)
				{
					//long move = moves[i];
					if(moves[i] == moveHashTable)
					{
						//put best move in front
						hashHits++;
						long mt = moves[i];
						moves[i] = moves[0];
						moves[0] = mt;	
						
						break;
					}
				}
			}
		}
		
		for(int i = 0; i < moves.length; i++)
		{
			long move = moves[i]; 
			Board bo = board.makeMove(move);
			result = - negaScoutBeta(bo, depth-1, -b, -alpha);
			
//			if((result> alpha) && (result < beta) && (depth > 0) && (i > 0))
//				result = - negaScoutBeta(bo, depth-1, -beta, -result);
			if(result >= beta)
			{
				bestMove = move;
				cuts++;
//				int[] value2 = {Long.numberOfTrailingZeros(move), beta, board.remainingStones(), moves.length, depth};
//				C.hashMap.addHash(board.getHash(), value2);
				return beta;
			}
			if(result > alpha)
			{
				alpha = result;
				best = move;
			}
			
			b = alpha + 1;
		}
		bestMove = best;
//		int[] value2 = {Long.numberOfTrailingZeros(best), alpha, board.remainingStones(), moves.length, depth};
		if(best != 0L) C.hashMap.addHash(new HashValue(Long.numberOfTrailingZeros(best), alpha, board.remainingStones(), depth, board.getHash()));
		return alpha;
	}
	
	
	public static boolean timeout(){
		return (System.currentTimeMillis() > timeout);
	}

	public long getBestMove() {
		return bestMove;
	}
}