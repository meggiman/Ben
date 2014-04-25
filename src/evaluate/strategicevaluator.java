package evaluate;
import Gameboard.Bitboard;
public class strategicevaluator implements IEvaluator{
private final long cornersmask = 0x8100000000000081L;
private final long xfieldsmask = 0x42000000004200L;
private final long cfieldsmask = 0x4281000000008142L;



	@Override
	public int evaluate(Bitboard gb, long possiblemoves, boolean player) {
		long movingplayer = (player)?gb.red:gb.green;
		long otherplayer = (player)?gb.green:gb.red;
		long possiblemovesenemy = gb.possiblemoves(!player);
		long emptyfields = ~(movingplayer|otherplayer);
		
		int possiblemovescount = Long.bitCount(possiblemoves);
		int possiblemovescountenemy = Long.bitCount(possiblemovesenemy);
		int potentialmobility = Long.bitCount(Bitboard.filladjacent(otherplayer)&emptyfields^possiblemoves);
		int corners = Long.bitCount(movingplayer&cornersmask);
		int cornersenemy = Long.bitCount(otherplayer&cornersmask);
		int xfields = Long.bitCount(movingplayer&xfieldsmask);
		int xfieldsenemy = Long.bitCount(otherplayer&xfieldsmask);
		int cfields = Long.bitCount(movingplayer&cfieldsmask);
		int cfieldsenemy = Long.bitCount(otherplayer&cfieldsmask);
		int stonesred = Long.bitCount(gb.red);
		int stonesgreen = Long.bitCount(gb.green);
		int value=0;
		
		//Mobility
		if (possiblemovescount+possiblemovescountenemy!=0) {
			value = 100*((possiblemovescount-possiblemovescountenemy)*10/(possiblemovescount+possiblemovescountenemy));
		}
		value += 300/(1+possiblemovescountenemy);
		value += 80*potentialmobility;
		
		//Corners
		if (corners+cornersenemy!=0) {
			value += 200 * ((corners - cornersenemy)* 10 / (corners + cornersenemy));
		}
		if (cfields+cfieldsenemy!=0){
			value += 80 * ((cfieldsenemy - cfields)*10 / (cfieldsenemy + cfields));
		}
		if (xfields+xfieldsenemy!=0){
			value += 120 *((xfieldsenemy - xfields)*10 / (xfields + xfieldsenemy));
		}
		
		
		value += 1*(stonesred-stonesgreen)*(stonesred+stonesgreen);
		return value;
	}
	

}
