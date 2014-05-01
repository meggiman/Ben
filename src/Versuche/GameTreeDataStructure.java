package Versuche;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.jar.JarEntry;

import Gameboard.Bitboard;

public class GameTreeDataStructure {
	public GameTreeDataStructure(Bitboard situation){
		this.situation = situation;
	}
	
	public Bitboard situation;
	public int value;
	public GameTreeDataStructure[] nextmoves;
	public void evaluate(Ievaluator analyzer) {
	}
	
	public void expandall(int deepth) {
		expandall(deepth<<1, true);
	}
	private void expandall(int deepth, boolean player){
		if (nextmoves == null) {
			Bitboard[] gameboards = situation.getnextgameboards(player);
			nextmoves = new GameTreeDataStructure[gameboards.length];
			for (int i = 0; i < nextmoves.length; i++) {
				nextmoves[i] = new GameTreeDataStructure(gameboards[i]);
			}
		}
		if (deepth>1) {
			for (int i = 0; i < nextmoves.length; i++) {
				nextmoves[i].expandall(deepth-1, !player);
			}
		}
	}
	
	public boolean expandnext(boolean player){
		long possiblemoves = situation.getPossibleMoves(player);
		int max = Long.bitCount(possiblemoves);
		if (nextmoves == null) {
			nextmoves = new GameTreeDataStructure[max];
		}
		int i = 0;
		while (i<nextmoves.length && nextmoves[i]!=null) {
			possiblemoves ^= Long.highestOneBit(possiblemoves);
			i++;
		}
		
		if (possiblemoves != 0) {
			Bitboard tmp = (Bitboard) situation.clone();
			tmp.makeMove(player, Long.highestOneBit(possiblemoves));
			nextmoves[i] = new GameTreeDataStructure(tmp);
			return true;
		}
		return false;
	}
	
}
