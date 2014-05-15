package evaluate;

import Gameboard.Bitboard;

public class StrategicEvaluatorNoah implements IEvaluator{

    Stability                 stability       = new Stability();

    /**
     * Bitmask used for Leftshifts of the Bitboard.
     */
    private static final long leftshiftMask   = 0xFEFEFEFEFEFEFEFEL;

    /**
     * Bitmask used for Rightshifts of the whole Bitboard.
     */
    private static final long rightshiftMask  = 0x7F7F7F7F7F7F7F7FL;

    /**
     * Bitmask used for shifting correction.
     */
    private static final long shiftMask       = 0x7E7E7E7E7E7E7E7EL;

    /*
     * 012
     * 7 3
     * 654
     */
    // bitmasks for every line
    private static long       LINES15[]       = {
                                              // vertical 15
            0x0101010101010101L,
            0x0202020202020202L,
            0x0404040404040404L,
            0x0808080808080808L,
            0x1010101010101010L,
            0x2020202020202020L,
            0x4040404040404040L,
            0x8080808080808080L              };
    private static long       LINES37[]       = {
                                              // horizontal 37
            0x00000000000000ffL,
            0x000000000000ff00L,
            0x0000000000ff0000L,
            0x00000000ff000000L,
            0x000000ff00000000L,
            0x0000ff0000000000L,
            0x00ff000000000000L,
            0xff00000000000000L              };
    private static long       LINES04[]       = {
                                              // diagonal 04
            0x8040201008040201L,
            0x0080402010080402L,
            0x0000804020100804L,
            0x0000008040201008L,
            0x0000000080402010L,
            0x0000000000804020L,
            0x4020100804020100L,
            0x2010080402010000L,
            0x1008040201000000L,
            0x0804020100000000L,
            0x0402010000000000L              };
    private static long       LINES26[]       = {
                                              // diagonal 26
            0x0102040810204080L,
            0x0001020408102040L,
            0x0000010204081020L,
            0x0000000102040810L,
            0x0000000001020408L,
            0x0000000000010204L,
            0x0204081020408000L,
            0x0408102040800000L,
            0x0810204080000000L,
            0x1020408000000000L,
            0x2040800000000000L              };

    private final static long CORNERS         = 0x8100000000000081L;

    // bitedges top
    private final static long TOPEDGE         = 0x00000000000000FFL;
    // bitedges bottom
    private final static long BOTTOMEDGE      = 0xFF00000000000000L;
    // bitedges top & bottom
    private final static long VERTICALEDGES   = TOPEDGE | BOTTOMEDGE;
    // bitedges left
    private final static long LEFTEDGE        = 0x0101010101010101L;
    // bitedges right
    private final static long RIGHTEDGE       = 0x8080808080808080L;
    // bitedges left & right
    private final static long HORIZONTALEDGES = LEFTEDGE | RIGHTEDGE;
    // bitedges left & right
    private final static long EDGES           = VERTICALEDGES | HORIZONTALEDGES;

    private long getStableDisks(Bitboard board, boolean player){
        long current = 0, before = board.red | board.green, filled04 = EDGES, filled15 = VERTICALEDGES, filled26 = EDGES, filled37 = HORIZONTALEDGES;
        int i;
        for (i = 0; i < 8; i++){
            if((before & LINES15[i]) == LINES15[i]){
                filled15 |= LINES15[i];
            }
            if((before & LINES37[i]) == LINES37[i]){
                filled37 |= LINES37[i];
            }
        }
        for (i = 0; i < 11; i++){
            if((before & LINES04[i]) == LINES04[i]){
                filled04 |= LINES04[i];
            }
            if((before & LINES26[i]) == LINES26[i]){
                filled26 |= LINES26[i];
            }
        }

        while(current != before){
            before = current;
            current |= (player ? board.red : board.green)
                    & ((current << 8) | (current >>> 8) | filled15) // 15
                    & ((current << 1) | (current >>> 1) | filled37) // 37
                    & ((current << 9) | (current >>> 9) | filled04) // 04
                    & ((current << 7) | (current >>> 7) | filled26); // 26
        }
        return current;
    }

    public StrategicEvaluatorNoah(){
    }

    @Override
    public short evaluate(Bitboard gb, long possibleMovesLong, boolean player){
        double discs = gb.getDiscCount();
        double EC = 3.5;
        double MC = 750;
        double MC2 = 50;
        double SC = 2;

        if(player){

        }
        int possibleMoves;
        int possibleMovesEnemy;
        if(player){
            possibleMoves = Long.bitCount(possibleMovesLong);
            possibleMovesEnemy = Long.bitCount(gb.getPossibleMoves(!player));
        }
        else{
            possibleMoves = Long.bitCount(gb.getPossibleMoves(player));
            possibleMovesEnemy = Long.bitCount(possibleMovesLong);
        }
        int potentialMoves = Long.bitCount(Bitboard.fillAdjacent(gb.red)
                & ~gb.green);
        int potentialMovesEnemy = Long.bitCount(Bitboard.fillAdjacent(gb.green)
                & ~gb.red);

        int edgeAdvantage = (int) (stability.getEdgeValue(gb));

        float mobilityAdvantage = ((possibleMoves + possibleMovesEnemy) == 0 ? 0
                : ((float) (possibleMoves - possibleMovesEnemy)
                / (possibleMoves + possibleMovesEnemy)));

        float potentialMobilityAdvantage = (float) ((potentialMoves + potentialMovesEnemy) == 0 ? 0
                : ((float) (potentialMoves - potentialMovesEnemy)
                / (potentialMoves + potentialMovesEnemy)));

        int occupiedSquareAdvantage = (int) (Long.bitCount(getStableDisks(gb, player))
                - Long.bitCount(getStableDisks(gb, !player)));

        int score = (int) (
                EC * edgeAdvantage
                        + MC * mobilityAdvantage
                        + MC2 * potentialMobilityAdvantage
                        + SC * occupiedSquareAdvantage
                );
        if(score > 32767 || score < -32768){
            System.out.println("ALERT!SWEG!11ELF!!");
        }
        // // gb.print();
        // System.out.println("EdgeAdvantage: " + edgeAdvantage);
        // System.out.println("MobilityAdvantage: " + mobilityAdvantage);
        // System.out.println("occupiedSquareAdvantage: "
        // + occupiedSquareAdvantage);
        // System.out.println(score);
        // System.out.println("------------------------");

        return (short) score;
    }
}
