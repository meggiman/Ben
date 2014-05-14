// 0.50
package OtherPlayers;

import java.util.Random;

import reversi.Coordinates;
import reversi.GameBoard;
import reversi.OutOfBoundsException;
import reversi.ReversiPlayer;

/**
 * 
 * @author JMC fuchsha@student.ethz.ch
 */
public class Xiaolong implements ReversiPlayer{
    private long              BB[]            = new long[2];
    private long              LS[]            = new long[2];
    private int               player, color, realDepth;
    private int               maxValue, maxDepth;
    private long              timeLimit, nodes, hashhits, hashmisses,
                              startTime, hashkey;
    private boolean           aborted         = false;
    private static int        HASHSIZE        = 0x800000;                       // 64
                                                                                 // MB
    private static int        HASHSIZEAND;                                      // 23
                                                                                 // bits
    private final static int  HASHITER        = 0x1;
    private final static int  TYPEAND         = 0x3;                            // 2
                                                                                 // bits
    private final static int  DEPTHAND        = 0x7F;                           // 7
                                                                                 // bits
                                                                                 // private
                                                                                 // final
                                                                                 // static
                                                                                 // int
                                                                                 // MOVEAND
                                                                                 // =
                                                                                 // 0x7F;
                                                                                 // //
                                                                                 // 7
                                                                                 // bits
    private final static int  VALUEAND        = 0x7FFF;                         // 15
                                                                                 // bits
    private static long       ht[];
    private final static long HASHFLIPXOR[][] = new long[2][64];
    private final static long HASHMOVEXOR[][] = new long[2][64];
    private final static long HASHPLAYERXOR[] = new long[2];
    private final static int  INFINITY        = 15000;                          // less
                                                                                 // than
                                                                                 // 2^14
    // maximum valuation
    private final static int  MATE            = 10000;                          // less
                                                                                 // than
                                                                                 // 2^14
                                                                                 // -
                                                                                 // 512
    // check time after every CHECKTIME nodes + 1
    private final static int  CHECKTIME       = 0xFFF;
    /*
     * CHOOSEN BIT BOARD REPRESENTATION
     * TOP
     * 00 01 02 03 04 05 06 07
     * 08 09 10 11 12 13 14 15
     * 16 17 18 19 20 21 22 23
     * LEFT 24 25 26 27 28 29 30 31 RIGHT
     * 32 33 34 35 36 37 38 39
     * 40 41 42 43 44 45 46 47
     * 48 49 50 51 52 53 54 55
     * 56 57 58 59 60 61 62 63
     * BOTTOM
     */
    // bit shifting masks
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
    // left bound zeros
    private final static long LEFTMASK        = ~LEFTEDGE;
    // right bound zeros
    private final static long RIGHTMASK       = ~RIGHTEDGE;
    /**
     * POSITION EQUALITY
     * --------------------------
     * |10 9 8 7 7 8 9 10
     * | 9 4 5 6 6 5 4 9
     * | 8 5 2 3 3 2 5 8
     * | 7 6 3 1 1 3 6 7
     * | 7 6 3 1 1 3 6 7
     * | 8 5 2 3 3 2 5 8
     * | 9 4 5 6 6 5 4 9
     * |10 9 8 7 7 8 9 10
     * --------------------------
     * --------------------------
     * | O C A E E A C O
     * | C X 1 1 1 1 X C
     * | A 1 A 2 2 A 1 A
     * | E 1 2 1 1 2 1 E
     * | E 1 2 1 1 2 1 E
     * | A 1 A 2 2 A 1 A
     * | C X 1 1 1 1 X C
     * | O C A E E A C O
     * --------------------------
     * 
     */
    private final static long PMO             = 0x8100000000000081L;
    // private final static long PMC = 0x4281000000008142L;
    // private final static long PMX = 0x0042000000004200L;
    private final static long PME             = 0x3C0081818181003CL;
    // private final static long PMA = 0x0000240000240000L;
    // private final static long PM1 = 0x003c425a5a423c00L;
    // private final static long PM2 = 0x0000182424180000L;
    private final static int  moveList[][]    = new int[64][64];
    private final static int  moveOrder[]     = { 0, 7, 56, 63, // 10
            18, 21, 42, 45, // 2
            2, 5, 16, 23, 40, 47, 58, 61, // 8
            3, 4, 24, 31, 32, 39, 59, 60, // 7
            19, 20, 26, 29, 34, 37, 43, 44, // 3
            11, 12, 25, 30, 33, 38, 51, 52, // 6
            10, 13, 17, 22, 41, 46, 50, 53, // 5
            1, 6, 8, 15, 48, 55, 57, 62, // 9
            9, 14, 49, 54, // 4
            27, 28, 35, 36                   // 1
                                              };
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

    // origin in p1
    private static long getMoves(final long p1, final long p2){
        long moves = 0, pre = ~(p1 | p2), tmp;
        int i;
        // top
        tmp = (p1 >>> 8);
        for (i = 1; i < 7 && tmp != 0; i++){
            tmp = ((tmp & p2) >>> 8);
            moves |= tmp & pre;
        }
        // bottom
        tmp = (p1 << 8);
        for (i = 1; i < 7 && tmp != 0; i++){
            tmp = ((tmp & p2) << 8);
            moves |= tmp & pre;
        }
        // right
        tmp = (p1 << 1) & LEFTMASK;
        for (i = 1; i < 7 && tmp != 0; i++){
            tmp = ((tmp & p2) << 1) & LEFTMASK;
            moves |= tmp & pre;
        }
        // left
        tmp = (p1 >>> 1) & RIGHTMASK;
        for (i = 1; i < 7 && tmp != 0; i++){
            tmp = ((tmp & p2) >>> 1) & RIGHTMASK;
            moves |= tmp & pre;
        }
        // topleft
        tmp = (p1 >>> 9) & RIGHTMASK;
        for (i = 1; i < 7 && tmp != 0; i++){
            tmp = ((tmp & p2) >>> 9) & RIGHTMASK;
            moves |= tmp & pre;
        }
        // bottomleft
        tmp = (p1 << 7) & RIGHTMASK;
        for (i = 1; i < 7 && tmp != 0; i++){
            tmp = ((tmp & p2) << 7) & RIGHTMASK;
            moves |= tmp & pre;
        }
        // topright
        tmp = (p1 >>> 7) & LEFTMASK;
        for (i = 1; i < 7 && tmp != 0; i++){
            tmp = ((tmp & p2) >>> 7) & LEFTMASK;
            moves |= tmp & pre;
        }
        // bottomright
        tmp = (p1 << 9) & LEFTMASK;
        for (i = 1; i < 7 && tmp != 0; i++){
            tmp = ((tmp & p2) << 9) & LEFTMASK;
            moves |= tmp & pre;
        }
        return moves;
    }

    /*
     * originally from Software Optimization Guide for AMD Athlon™ 64 and
     * Opteron™ Processors
     * standard parallel bit count including bytewise addition
     */
    private static int parallelCount(final long n){
        long c;
        c = n - ((n >>> 1) & 0x5555555555555555L);
        c = (c & 0x3333333333333333L) + ((c >>> 2) & 0x3333333333333333L);
        return (int) (((c + (c >>> 4) & 0xF0F0F0F0F0F0F0FL) * 0x0101010101010101L) >>> 56);
    }

    private static long flipDisks(final long a, long b, final long pos){
        long ret = 0, tmp;
        // up
        tmp = pos >>> 8;
        if((tmp & b) != 0){
            do{
                tmp >>>= 8;
            }while((tmp & b) != 0);
            if((tmp & a) != 0){
                tmp <<= 8;
                do{
                    ret |= tmp;
                    tmp <<= 8;
                }while(tmp != pos);
            }
        }
        // down
        tmp = pos << 8;
        if((tmp & b) != 0){
            do{
                tmp <<= 8;
            }while((tmp & b) != 0);
            if((tmp & a) != 0){
                tmp >>>= 8;
                do{
                    ret |= tmp;
                    tmp >>>= 8;
                }while(tmp != pos);
            }
        }
        b &= 0x7E7E7E7E7E7E7E7EL;
        // left
        tmp = pos >>> 1;
        if((tmp & b) != 0){
            do{
                tmp >>>= 1;
            }while((tmp & b) != 0);
            if((tmp & a) != 0){
                tmp <<= 1;
                do{
                    ret |= tmp;
                    tmp <<= 1;
                }while(tmp != pos);
            }
        }
        // right
        tmp = pos << 1;
        if((tmp & b) != 0){
            do{
                tmp <<= 1;
            }while((tmp & b) != 0);
            if((tmp & a) != 0){
                tmp >>>= 1;
                do{
                    ret |= tmp;
                    tmp >>>= 1;
                }while(tmp != pos);
            }
        }
        // up-left
        tmp = pos >>> 9;
        if((tmp & b) != 0){
            do{
                tmp >>>= 9;
            }while((tmp & b) != 0);
            if((tmp & a) != 0){
                tmp <<= 9;
                do{
                    ret |= tmp;
                    tmp <<= 9;
                }while(tmp != pos);
            }
        }
        // up-right
        tmp = pos >>> 7;
        if((tmp & b) != 0){
            do{
                tmp >>>= 7;
            }while((tmp & b) != 0);
            if((tmp & a) != 0){
                tmp <<= 7;
                do{
                    ret |= tmp;
                    tmp <<= 7;
                }while(tmp != pos);
            }
        }
        // down-left
        tmp = pos << 9;
        if((tmp & b) != 0){
            do{
                tmp <<= 9;
            }while((tmp & b) != 0);
            if((tmp & a) != 0){
                tmp >>>= 9;
                do{
                    ret |= tmp;
                    tmp >>>= 9;
                }while(tmp != pos);
            }
        }
        // down-right
        tmp = pos << 7;
        if((tmp & b) != 0){
            do{
                tmp <<= 7;
            }while((tmp & b) != 0);
            if((tmp & a) != 0){
                tmp >>>= 7;
                do{
                    ret |= tmp;
                    tmp >>>= 7;
                }while(tmp != pos);
            }
        }
        return ret;
    }

    private long flipDisks2(final long a, long b, int index){
        long pos = 1L << index;
        long ret = 0, tmp;
        int in;
        // up
        tmp = pos >>> 8;
        in = index - 8;
        if((tmp & b) != 0){
            do{
                tmp >>>= 8;
                in -= 8;
            }while((tmp & b) != 0);
            if((tmp & a) != 0){
                tmp <<= 8;
                in += 8;
                do{
                    ret |= tmp;
                    hashkey ^= HASHFLIPXOR[player][in];
                    tmp <<= 8;
                    in += 8;
                }while(tmp != pos);
            }
        }
        // down
        tmp = pos << 8;
        in = index + 8;
        if((tmp & b) != 0){
            do{
                tmp <<= 8;
                in += 8;
            }while((tmp & b) != 0);
            if((tmp & a) != 0){
                tmp >>>= 8;
                in -= 8;
                do{
                    ret |= tmp;
                    hashkey ^= HASHFLIPXOR[player][in];
                    tmp >>>= 8;
                    in -= 8;
                }while(tmp != pos);
            }
        }
        b &= 0x7E7E7E7E7E7E7E7EL;
        // left
        tmp = pos >>> 1;
        in = index - 1;
        if((tmp & b) != 0){
            do{
                tmp >>>= 1;
                in -= 1;
            }while((tmp & b) != 0);
            if((tmp & a) != 0){
                tmp <<= 1;
                in += 1;
                do{
                    ret |= tmp;
                    hashkey ^= HASHFLIPXOR[player][in];
                    tmp <<= 1;
                    in += 1;
                }while(tmp != pos);
            }
        }
        // right
        tmp = pos << 1;
        in = index + 1;
        if((tmp & b) != 0){
            do{
                tmp <<= 1;
                in += 1;
            }while((tmp & b) != 0);
            if((tmp & a) != 0){
                tmp >>>= 1;
                in -= 1;
                do{
                    ret |= tmp;
                    hashkey ^= HASHFLIPXOR[player][in];
                    tmp >>>= 1;
                    in -= 1;
                }while(tmp != pos);
            }
        }
        // up-left
        tmp = pos >>> 9;
        in = index - 9;
        if((tmp & b) != 0){
            do{
                tmp >>>= 9;
                in -= 9;
            }while((tmp & b) != 0);
            if((tmp & a) != 0){
                tmp <<= 9;
                in += 9;
                do{
                    ret |= tmp;
                    hashkey ^= HASHFLIPXOR[player][in];
                    tmp <<= 9;
                    in += 9;
                }while(tmp != pos);
            }
        }
        // up-right
        tmp = pos >>> 7;
        in = index - 7;
        if((tmp & b) != 0){
            do{
                tmp >>>= 7;
                in -= 7;
            }while((tmp & b) != 0);
            if((tmp & a) != 0){
                tmp <<= 7;
                in += 7;
                do{
                    ret |= tmp;
                    hashkey ^= HASHFLIPXOR[player][in];
                    tmp <<= 7;
                    in += 7;
                }while(tmp != pos);
            }
        }
        // down-left
        tmp = pos << 9;
        in = index + 9;
        if((tmp & b) != 0){
            do{
                tmp <<= 9;
                in += 9;
            }while((tmp & b) != 0);
            if((tmp & a) != 0){
                tmp >>>= 9;
                in -= 9;
                do{
                    ret |= tmp;
                    hashkey ^= HASHFLIPXOR[player][in];
                    tmp >>>= 9;
                    in -= 9;
                }while(tmp != pos);
            }
        }
        // down-right
        tmp = pos << 7;
        in = index + 7;
        if((tmp & b) != 0){
            do{
                tmp <<= 7;
                in += 7;
            }while((tmp & b) != 0);
            if((tmp & a) != 0){
                tmp >>>= 7;
                in -= 7;
                do{
                    ret |= tmp;
                    hashkey ^= HASHFLIPXOR[player][in];
                    tmp >>>= 7;
                    in -= 7;
                }while(tmp != pos);
            }
        }
        return ret;
    }

    private void toBB(GameBoard gb){
        BB[0] = 0;
        BB[1] = 0;
        for (int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){
                try{
                    if(gb.getOccupation(new Coordinates(i + 1, j + 1)) == GameBoard.GREEN){
                        BB[getIndex(GameBoard.GREEN)] |= 1L << (i * 8 + j);
                    }else if(gb.getOccupation(new Coordinates(i + 1, j + 1)) == GameBoard.RED){
                        BB[getIndex(GameBoard.RED)] |= 1L << (i * 8 + j);
                    }
                }catch(OutOfBoundsException ex){
                }
            }
        }
    }

    private long getStableDisks(long a, long b){
        long cur = 0, pre = a | b, filled04 = EDGES, filled15 = VERTICALEDGES, filled26 = EDGES, filled37 = HORIZONTALEDGES;
        int i;
        for (i = 0; i < 8; i++){
            if((pre & LINES15[i]) == LINES15[i]){
                filled15 |= LINES15[i];
            }
            if((pre & LINES37[i]) == LINES37[i]){
                filled37 |= LINES37[i];
            }
        }
        for (i = 0; i < 11; i++){
            if((pre & LINES04[i]) == LINES04[i]){
                filled04 |= LINES04[i];
            }
            if((pre & LINES26[i]) == LINES26[i]){
                filled26 |= LINES26[i];
            }
        }

        while(cur != pre){
            pre = cur;
            cur |= a
                    & ((cur << 8) | (cur >>> 8) | filled15) // 15
                    & ((cur << 1) | (cur >>> 1) | filled37) // 37
                    & ((cur << 9) | (cur >>> 9) | filled04) // 04
                    & ((cur << 7) | (cur >>> 7) | filled26); // 26
        }
        return cur;
    }

    // return index for the specified color
    private int getIndex(int p){
        return (p == GameBoard.GREEN ? 0 : 1);
    }

    // evaluate position of the current board state BB by counting disks
    private int evaluate(){
        long p1 = BB[player], p2 = BB[player ^ 1];
        return (((p1 | p2) & PMO) == 0 ? (realDepth - parallelCount(p2) < 2 ? -30
                : 0)
                : 36 * parallelCount(getStableDisks(p1, p2)) - 38
                        * parallelCount(getStableDisks(p2, p1)))
                + 8
                * (parallelCount(getBorders(p2, p1)))
                - 10
                * (parallelCount(getBorders(p1, p2)))
                + 4
                * (parallelCount(getMoves(p1, p2)))
                - 5
                * (parallelCount(getMoves(p2, p1)))
                + 1 * (parallelCount(p1 & PME) - parallelCount(p2 & PME))
                + 24 * (parallelCount(p1 & (PMO)) - parallelCount(p2 & PMO));
    }

    public void initialize(int i, long l){
        if(l < 25){
            l = 50;
        }
        timeLimit = l - 25; // -25 ms less as a sufficient time buffer for
                            // abortion
        hashkey = 0;
        color = i;
        BB[0] = 0;
        BB[1] = 0;
        LS[0] = -1;
        LS[1] = -1;
        System.out.println(this.getClass().getSimpleName() + " is "
                + (color == GameBoard.GREEN ? "green" : "red"));
        initHashTable();
        initMoveList();
    }

    // from the Java Programmers FAQ - Part B by Peter van der Linden
    private static void fillArray(long[] array, long value){
        int len = array.length;
        if(len > 0){
            array[0] = value;
        }
        for (int i = 1; i < len; i += i){
            System.arraycopy(array, 0, array, i, ((len - i) < i) ? (len - i)
                    : i);
        }
    }

    private void initMoveList(){
        int i, j;
        for (i = 0; i < 64; i++){
            shuffleMoves();
            for (j = 0; j < 64; j++){
                moveList[i][j] = moveOrder[j];
            }
        }
    }

    private Coordinates search(){
        long availableMoves, disksToFlip, currentMove, storedHash;
        int depth, bestValue = -INFINITY, bestIndex = -1, value, cD, index, moveIndex, moveCount;
        maxDepth = 0;
        maxValue = -INFINITY;
        startTime = System.currentTimeMillis();
        aborted = false;
        nodes = 0;
        hashhits = 0;
        hashmisses = 0;
        Coordinates myMove = null;
        cD = parallelCount(BB[0] | BB[1]);
        fillArray(ht, 0);
        realDepth = cD;
        storedHash = hashkey;
        availableMoves = getMoves(BB[player], BB[player ^ 1]);
        if(availableMoves != 0){
            depth = 0;
            if(cD < 56){
                if(timeLimit > 1500){
                    depth = 6;
                }else if(timeLimit > 500){
                    depth = 4;
                }else{
                    depth = 2;
                }
            }
            for (; !aborted; depth += 2){
                maxDepth = depth - 2;
                bestValue = -INFINITY;
                moveCount = parallelCount(availableMoves);
                for (index = 0; moveCount > 0 && !aborted; index++){
                    moveIndex = moveList[realDepth][index];
                    currentMove = availableMoves & (0x1L << moveIndex);
                    if(currentMove == 0){
                        continue;
                    }
                    moveCount--;
                    disksToFlip = flipDisks2(BB[player], BB[player ^ 1], moveIndex);
                    // move start
                    BB[player] ^= disksToFlip | currentMove;
                    BB[player ^ 1] ^= disksToFlip;
                    hashkey ^= HASHPLAYERXOR[player];
                    hashkey ^= HASHMOVEXOR[player][moveIndex];
                    player ^= 1;
                    realDepth++;
                    // move end
                    value = -pvsearch(depth, -INFINITY, INFINITY, false);
                    // undo move start
                    realDepth--;
                    player ^= 1;
                    hashkey = storedHash;
                    BB[player ^ 1] ^= disksToFlip;
                    BB[player] ^= disksToFlip | currentMove;
                    // undo move end
                    if(aborted){
                        break;
                    }
                    if(value > bestValue){
                        bestIndex = moveIndex;
                        bestValue = value;
                    }
                    if(bestValue >= maxValue && bestIndex != -1){
                        maxValue = bestValue;
                        maxDepth = depth;
                        myMove = new Coordinates((bestIndex / 8) + 1, (bestIndex & 0x7) + 1);
                    }
                }
                if(aborted
                        || ((System.currentTimeMillis() - startTime) > timeLimit)){
                    aborted = true;
                    break;
                }else{
                    if((maxValue < MATE || bestValue >= maxValue)
                            && bestIndex != -1){
                        myMove = new Coordinates((bestIndex / 8) + 1, (bestIndex & 0x7) + 1);
                        maxValue = bestValue;
                        maxDepth = depth;
                    }
                }
                if((maxDepth + cD) > 65){
                    break;
                }
            }
        }
        return myMove;
    }

    private void shuffleMoves(){
        Random r = new Random();
        int f = 0, p = 0, t = 0, i, pos[] = { 0, 4, 8, 16, 24, 32, 40, 48, 56,
                60 }, tmp, in;
        for (i = 0; i < 60; i++){
            if(pos[p] == i){
                f = pos[p];
                p++;
                t = pos[p];
            }
            in = f + r.nextInt(t - f);
            tmp = moveOrder[in];
            moveOrder[in] = moveOrder[i];
            moveOrder[i] = tmp;
        }
    }

    public Coordinates nextMove(GameBoard gb){
        int cD;
        long stopTime, elapsedTime;
        Coordinates myMove;
        player = getIndex(color);
        toBB(gb);
        cD = parallelCount((BB[0] | BB[1]));
        if(parallelCount((BB[0] | BB[1]) ^ (LS[0] | LS[1])) != 1){
            initMoveList();
        }
        hashkey = 0;
        LS[0] = BB[0];
        LS[1] = BB[1];
        myMove = search();
        stopTime = System.currentTimeMillis();
        elapsedTime = stopTime - startTime;
        System.out.println("===" + cD + "===" + this.getClass().getSimpleName());
        System.out.printf("Valuation: %d, depths complete: %d, time used: %.3fs, total nodes: %.3fkn, nodes/s: %.3fkn/s, hashhits: %.1f%%\n", maxValue, (maxDepth
                + cD == 64 ? 64 - cD : maxDepth + 1), (elapsedTime * 0.001), (nodes * 0.001), (nodes * 1.0)
                / (elapsedTime * 1.0), (hashhits * 100.0)
                / (hashhits + hashmisses));
        return myMove;
    }

    /*
     * get border bits (neighbours of empty) of the current situation
     */
    private static long getBorders(long a, long b){
        long empty = ~(a | b);
        return a & ((empty >>> 8) | (empty << 8)
                | ((LEFTMASK & empty) >>> 1) | ((RIGHTMASK & empty) << 1)
                | ((LEFTMASK & empty) >>> 7) | ((RIGHTMASK & empty) << 7)
                | ((LEFTMASK & empty) >>> 9) | ((RIGHTMASK & empty) << 9));
    }

    /*
     * search using alpha beta pruning & principal variations
     */
    private int pvsearch(int depth, int alpha, int beta, boolean passed){
        int bestValue = -INFINITY, value = 0, moveCount, bestMove = -1, moveIndex, countA, countB, index, bestIndex = -1, hash;
        long currentMove, disksToFlip, availableMoves, storedHash;
        boolean principalVariation = false;
        nodes++;
        if(aborted
                || ((nodes & CHECKTIME) == 0 && (System.currentTimeMillis() - startTime) > timeLimit)){
            aborted = true;
            return 0;
        }
        if(realDepth == 64){ // game finished
            countA = parallelCount(BB[player]);
            if(countA > 32){
                return MATE + 4 * countA - 128;
            }else if(countA < 32){
                return -MATE - 128 + 4 * countA;
            }else{
                return 0;
            }
        }else if(depth <= 0){ // reached depth
            return evaluate();
        }
        availableMoves = getMoves(BB[player], BB[player ^ 1]);
        storedHash = hashkey;
        if(availableMoves == 0){
            if(passed){ // deadlock
                countA = parallelCount(BB[player]);
                countB = parallelCount(BB[player ^ 1]);
                if(countA > countB){
                    return MATE + 128 - 4 * countB;
                }else if(countA < countB){
                    return -MATE - 128 + 4 * countA;
                }else{
                    return 0;
                }
            }else{ // repetition
                hashkey ^= HASHPLAYERXOR[player];
                player ^= 1;
                value = -pvsearch(depth, -beta, -alpha, true);
                player ^= 1;
                hashkey = storedHash;
            }
            return value;
        }
        hash = retrieve(depth);
        if(hash != 0){
            value = ((hash >>> 14) & VALUEAND) - INFINITY;
            switch((hash >>> 29) & TYPEAND){
                case 1: // lower
                    if(value >= beta){
                        return value;
                    }else if(value > alpha){
                        alpha = value;
                    }
                    break;
                case 2:// upper
                    if(value <= alpha){
                        return value;
                    }else if(value < beta){
                        beta = value;
                    }
                    break;
                default:
                    return value;// exact
            }
        }
        moveCount = parallelCount(availableMoves);
        if(depth == 1){
            for (index = 0; moveCount > 0; index++){
                moveIndex = moveList[realDepth][index];
                currentMove = availableMoves & (0x1L << moveIndex);
                if(currentMove == 0){
                    continue;
                }
                nodes++;
                if(aborted
                        || ((nodes & CHECKTIME) == 0 && (System.currentTimeMillis() - startTime) > timeLimit)){
                    aborted = true;
                    return 0;
                }
                moveCount--;
                disksToFlip = flipDisks(BB[player], BB[player ^ 1], currentMove);
                if(realDepth >= 63){
                    // move start
                    BB[player] ^= disksToFlip | currentMove;
                    BB[player ^ 1] ^= disksToFlip;
                    // move end
                    countA = parallelCount(BB[player]);
                    // undo move start
                    BB[player ^ 1] ^= disksToFlip;
                    BB[player] ^= disksToFlip | currentMove;
                    // undo move end
                    if(countA > 32){
                        return MATE + 4 * countA - 128;
                    }else if(countA < 32){
                        return -MATE - 128 + 4 * countA;
                    }else{
                        return 0;
                    }
                }else{
                    // move start
                    BB[player] ^= disksToFlip | currentMove;
                    BB[player ^ 1] ^= disksToFlip;
                    player ^= 1;
                    realDepth++;
                    // move end
                    value = -evaluate();
                    // abort search
                    if(aborted){
                        return 0;
                    }
                    // undo move start
                    realDepth--;
                    player ^= 1;
                    BB[player ^ 1] ^= disksToFlip;
                    BB[player] ^= disksToFlip | currentMove;
                    // undo move end
                }
                // beta pruning
                if(value > bestValue){
                    bestValue = value;
                    bestIndex = index;
                    bestMove = moveIndex;
                }
                if(moveCount == 0){
                    break;
                }
                if(value >= beta){
                    break;
                }
            }
        }else{
            for (index = 0; moveCount > 0; index++){
                moveIndex = moveList[realDepth][index];
                currentMove = availableMoves & (0x1L << moveIndex);
                if(currentMove == 0){
                    continue;
                }
                moveCount--;
                disksToFlip = flipDisks2(BB[player], BB[player ^ 1], moveIndex);
                // move start
                BB[player] ^= disksToFlip | currentMove;
                BB[player ^ 1] ^= disksToFlip;
                hashkey ^= HASHPLAYERXOR[player];
                hashkey ^= HASHMOVEXOR[player][moveIndex];
                player ^= 1;
                realDepth++;
                // move end
                if(principalVariation){
                    value = -pvsearch(depth - 1, -alpha - 1, -alpha, false);
                    if(value > alpha && value < beta){
                        value = -pvsearch(depth - 1, -beta, -alpha, false);
                    }
                }else{
                    value = -pvsearch(depth - 1, -beta, -alpha, false);
                }
                // abort search
                if(aborted){
                    return 0;
                }
                // undo move start
                realDepth--;
                player ^= 1;
                hashkey = storedHash;
                BB[player ^ 1] ^= disksToFlip;
                BB[player] ^= disksToFlip | currentMove;
                // undo move end
                if(value > bestValue){
                    bestValue = value;
                    bestIndex = index;
                    bestMove = moveIndex;
                }
                if(moveCount == 0){
                    break;
                }
                if(value >= beta){
                    break;
                }
                if(value > alpha){
                    alpha = value;
                    principalVariation = true;
                }
            }
        }
        // abort search
        if(aborted){
            return 0;
        }
        // increase move ranking
        if(bestIndex > 0){
            value = moveList[realDepth][bestIndex - 1];
            moveList[realDepth][bestIndex - 1] = moveList[realDepth][bestIndex];
            moveList[realDepth][bestIndex] = value;
        }
        if(depth > 0){
            store(depth, alpha, beta, bestValue, bestMove);
        }
        return bestValue;
    }

    private void initHashTable(){
        Random r = new Random();
        int i;
        HASHSIZE = 0x800000; // 64MB
        ht = null;
        System.gc();
        while(Runtime.getRuntime().totalMemory() + HASHSIZE * 8 > Runtime.getRuntime().maxMemory()){
            HASHSIZE >>>= 1;
        }
        HASHSIZEAND = HASHSIZE - 1;
        ht = new long[HASHSIZE + HASHITER];
        System.out.printf("hashtable memory in use: %.1fMB\n", (HASHSIZE * 8.0)
                / (1024 * 1024));
        HASHPLAYERXOR[0] = (r.nextLong() << 21) ^ (r.nextLong() >>> 35)
                ^ (r.nextLong() << 4);
        HASHPLAYERXOR[1] = (r.nextLong() << 21) ^ (r.nextLong() >>> 35)
                ^ (r.nextLong() << 4);
        for (i = 0; i < 64; i++){
            HASHFLIPXOR[0][i] = (r.nextLong() << 21) ^ (r.nextLong() >>> 35)
                    ^ (r.nextLong() << 4);
            HASHFLIPXOR[1][i] = (r.nextLong() << 21) ^ (r.nextLong() >>> 35)
                    ^ (r.nextLong() << 4);
            HASHMOVEXOR[0][i] = (r.nextLong() << 21) ^ (r.nextLong() >>> 35)
                    ^ (r.nextLong() << 4);
            HASHMOVEXOR[1][i] = (r.nextLong() << 21) ^ (r.nextLong() >>> 35)
                    ^ (r.nextLong() << 4);
        }
    }

    private void store(int depth, int alpha, int beta, int bestValue, int bestIndex){ // could
                                                                                      // be
                                                                                      // extended
                                                                                      // with
                                                                                      // move
                                                                                      // usage,
                                                                                      // no
                                                                                      // use
                                                                                      // since
                                                                                      // mpc
                                                                                      // table
                                                                                      // got
                                                                                      // lost
        // 0-6, 7-13, 14-28, 29-30, 32-63
        // depth, move, value, type, key
        // type: 0 exact, 1 lower, 2 upper
        int i, type, cDepth = 64, lowestDepth = 64, lowestIndex = 0, index = (int) (hashkey & HASHSIZEAND), key = (int) (hashkey >>> 32);
        long hash;
        if(aborted){
            return;
        }
        type = 0;
        if(bestValue >= beta){
            type = 1;
        }
        if(bestValue <= alpha){
            type = 2;
        }
        for (i = 0; i < HASHITER; i++){
            hash = ht[index + i];
            cDepth = (int) (hash & DEPTHAND);
            if(hash == 0 || (int) (hash >>> 32) == key){
                ht[index + i] = ((long) key << 32) | (type << 29)
                        | ((bestValue + INFINITY) << 14) | (bestIndex << 7)
                        | depth;
                return;
            }
            if(cDepth < lowestDepth){
                lowestDepth = cDepth;
                lowestIndex = i;
            }
        }
        ht[index + lowestIndex] = ((long) key << 32) | (type << 29)
                | ((bestValue + INFINITY) << 14) | (bestIndex << 7) | depth;
    }

    private int retrieve(int depth){
        // 0-6, 7-13, 14-28, 29-30, 32-63
        // depth, move, value, type, key
        int i, index = (int) (hashkey & HASHSIZEAND), key = (int) (hashkey >>> 32);
        long hash;
        for (i = 0; i < HASHITER; i++){
            hash = ht[index + i];
            if((int) (hash >>> 32) == key){
                if((int) (hash & DEPTHAND) >= depth){
                    hashhits++;
                    return (int) hash; // 31 bits
                }else{
                    hashmisses++;
                    return 0;
                }
            }
        }
        hashmisses++;
        return 0;
    }
}
