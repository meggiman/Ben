package Versuche;

import java.util.HashMap;

import javax.xml.transform.Templates;

public class Tablegenerator {

	public static byte[] generatemovetable(){
		//Generiere alle möglichen Rowobjekte und erzeuge eine Liste aller möglichen.
		Row temp = new Row();
		int key;
		byte[] table = new byte[256*256*8];
		temp = new Row((byte)97,(byte)26,(byte)4);
		temp.makemove();
		for (int i = 0; i < 256; i++) {
			for (int j = 0; j < 256; j++) {
				for (int j2 = 1; j2 < 8; j2++) {
					temp = new Row((byte)i, (byte)j, (byte)j2);
					if (temp.checkrow()) {
						key = i|(j<<8)|(j2<<16);
						table[key] =  temp.makemove();
					}
				}
			}
		}
		return table;
	}
	
 	
	private static class Row {
		public byte currentplayer;
		public byte otherplayer;
		public byte move;
		
		public Row(){};
		
		public Row(byte currentplayer, byte otherplayer, byte move){
			this.currentplayer = currentplayer;
			this.otherplayer = otherplayer;
			this.move = move;
		}
		public boolean checkrow(){
			if ((currentplayer&otherplayer) != 0) {
				return false;
			}
			if (((currentplayer|otherplayer)&(1<<move)) != 0) {
				return false;
			}
			return true;
		}
		public byte makemove(){
			//leftshift
			byte coord = (byte)(1<<move);
			byte cursor = (byte) (coord<<1);
			byte possiblychangedfields=0;
			byte changedfields = 0;
			
			while (cursor !=0){
				cursor &= otherplayer;
				possiblychangedfields |= cursor;
				cursor = (byte) ((cursor << 1));
				if ((cursor&currentplayer)!=0) {
					changedfields|=possiblychangedfields;
					break;
				}
			}
			cursor = (byte) (coord>>>1);
			while (cursor !=0){
				cursor &= otherplayer;
				possiblychangedfields |= cursor;
				cursor = (byte) ((cursor>>> 1));
				if ((cursor&currentplayer)!=0) {
					changedfields|=possiblychangedfields;
					break;
				}
			}
			return changedfields;
			
		}
	}
}
