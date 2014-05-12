/*******************************************************************************
 * Copyright (c) 2014 Noah Huesser.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Noah Huesser - nhuesser@student.ethz.ch - initial API and implementation
 *     
 * A random player to provide a first very weak opponent
 ******************************************************************************/

package Players;

import reversi.Coordinates;
import reversi.GameBoard;
import reversi.ReversiPlayer;
import reversi.Utils;

public class Player implements ReversiPlayer{

    // private long maxTime;

    private byte         ENEMY;
    private byte         PLAYER;
    private long         average   = 0;
    private int          cycles    = 0;
    private byte         MAXDEPTH  = 3;
    private Board[]      boards    = new Board[MAXDEPTH + 1];

    private static short TOPBORDER = 32767;

    private Coordinates  spareCoordinates;

    private int fuseBits(short value, byte x, byte y){
        return (int) value << 16 | (int) x << 8 | (int) y;
    }

    private short extractValue(int move){
        return (short) ((move & 0xFFFF0000) >> 16);
    }

    private byte extractX(int move){
        return (byte) ((move & 0x0000FF00) >> 8);
    }

    private byte extractY(int move){
        return (byte) (move & 0x000000FF);
    }

    @Override
    public void initialize(int myColor, long timeLimit){
        // maxTime = timeLimit;
        ENEMY = (byte) Utils.other(myColor);
        PLAYER = (byte) myColor;
        long start = System.nanoTime();
        for (int i = 0; i < MAXDEPTH + 1; i++)
            boards[i] = new Board();
        long time = ((System.nanoTime() - start) / 1000);
        System.out.println(time);
    }

    @Override
    public Coordinates nextMove(GameBoard gb){
        System.out.println("Move started!");
        long start = System.nanoTime();
        boards[MAXDEPTH].copy(gb);
        int move = node(ENEMY, TOPBORDER, MAXDEPTH);
        int x = extractX(move);
        int y = extractY(move);
        long time = (System.nanoTime() - start) / 1000;
        System.out.println("Took me " + time + "us");
        average += time;
        System.out.println("Average is " + average / ++cycles + "us");
        if(x != 9 && y != 9){
            spareCoordinates = new Coordinates(y + 1, x + 1);
            if(gb.checkMove(PLAYER, spareCoordinates))
                return spareCoordinates;
        }
        else
            return null;
        return null;
    }

    // Initiate recursion with opposite color!
    private int node(byte player, short border, byte depth){
        short value;
        if(player == PLAYER)
            value = TOPBORDER;
        else
            value = 0;

        byte nx = 9;
        byte ny = 9;

        if(depth > 0){
            for (byte i = 0; i < 8; i++){
                for (byte k = 0; k < 8; k++){
                    boards[depth - 1].copy(boards[depth]);
                    if(boards[depth - 1].put(Board.enemy(player), k, i)){
                        int temp = node(Board.enemy(player), value, (byte) (depth - 1));
                        short tempv = extractValue(temp);
                        if(player == PLAYER){
                            // Min
                            if(tempv <= value){
                                value = tempv;
                                nx = k;
                                ny = i;
                            }
                            if(value <= border){
                                // System.out.println(player + "/" + depth + "/"
                                // + value + " ----- min prune RANDOM PLAYER");
                                return fuseBits(value, nx, ny);
                            }
                        }
                        else{
                            // Max
                            if(value <= tempv){
                                value = tempv;
                                nx = k;
                                ny = i;
                            }
                            if(value >= border){
                                // System.out.println(player + "/" + depth + "/"
                                // + value + " ----- max prune RANDOM PLAYER");
                                return fuseBits(value, nx, ny);
                            }
                        }
                    }
                }
            }
            if(nx == 9 && ny == 9){
                int temp = node((byte) player, border, (byte) (depth - 1));
                value = extractValue(temp);
            }
        }
        else{
            value = (short) Math.floor((Math.random() * 500) + 1);
        }
        // System.out.println(player + "/" + depth + "/" + value +
        // " ----- normal exit RANDOM PLAYER");
        return fuseBits(value, nx, ny);
    }
}
