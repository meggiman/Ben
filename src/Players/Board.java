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
 * first try on a bitboard implementation - 
 * left to the random player to compare speed
 ******************************************************************************/

package Players;

import reversi.Coordinates;
import reversi.GameBoard;
import reversi.OutOfBoundsException;

public class Board{

    public short[] board = { 0b0000000000000000,
                         0b0000000000000000,
                         0b0000000000000000,
                         0b0000000101000000,
                         0b0000001010000000,
                         0b0000000000000000,
                         0b0000000000000000,
                         0b0000000000000000 };

    public Board(){
    }

    public Board(GameBoard gb){
        copy(gb);
    }

    public boolean put(byte color, byte x, byte y){
        byte currentColor;
        boolean turnedStones = false;
        if(get(x, y) == 0){

            // -x
            boolean stoneBetween = false;
            byte nx = x;
            while(--nx >= 0){
                currentColor = get(nx, y);
                if(currentColor == enemy(color)){
                    stoneBetween = true;
                    continue;
                }
                else if(currentColor == color && stoneBetween){
                    for (byte i = x; i >= nx; i--)
                        set(color, i, y);
                    turnedStones = true;
                    break;
                }
                else
                    break;
            }

            // +x
            stoneBetween = false;
            nx = x;
            while(++nx <= 7){
                currentColor = get(nx, y);
                if(currentColor == enemy(color)){
                    stoneBetween = true;
                    continue;
                }
                else if(currentColor == color && stoneBetween){
                    for (byte i = x; i <= nx; i++)
                        set(color, i, y);
                    turnedStones = true;
                    break;
                }
                else
                    break;
            }

            // -y
            stoneBetween = false;
            byte ny = y;
            while(--ny >= 0){
                currentColor = get(x, ny);
                if(currentColor == enemy(color)){
                    stoneBetween = true;
                    continue;
                }
                else if(currentColor == color && stoneBetween){
                    for (byte i = y; i >= ny; i--)
                        set(color, x, i);
                    turnedStones = true;
                    break;
                }
                else
                    break;
            }

            // +y
            stoneBetween = false;
            ny = y;
            while(++ny <= 7){
                currentColor = get(x, ny);
                if(currentColor == enemy(color)){
                    stoneBetween = true;
                    continue;
                }
                else if(currentColor == color && stoneBetween){
                    for (byte i = y; i <= ny; i++)
                        set(color, x, i);
                    turnedStones = true;
                    break;
                }
                else
                    break;
            }

            // -x -y
            stoneBetween = false;
            nx = x;
            ny = y;
            while(--nx >= 0 && --ny >= 0){
                currentColor = get(nx, ny);
                if(currentColor == enemy(color)){
                    stoneBetween = true;
                    continue;
                }
                else if(currentColor == color && stoneBetween){
                    byte k = y;
                    for (byte i = x; i >= nx; i--){
                        set(color, i, k);
                        k--;
                    }
                    turnedStones = true;
                    break;
                }
                else
                    break;
            }

            // +x -y
            stoneBetween = false;
            nx = x;
            ny = y;
            while(++nx <= 7 && --ny >= 0){
                currentColor = get(nx, ny);
                if(currentColor == enemy(color)){
                    stoneBetween = true;
                    continue;
                }
                else if(currentColor == color && stoneBetween){
                    byte k = y;
                    for (byte i = x; i <= nx; i++){
                        set(color, i, k);
                        k--;
                    }
                    turnedStones = true;
                    break;
                }
                else
                    break;
            }

            // +x +y
            stoneBetween = false;
            nx = x;
            ny = y;
            while(++nx <= 7 && ++ny <= 7){
                currentColor = get(nx, ny);
                if(currentColor == enemy(color)){
                    stoneBetween = true;
                    continue;
                }
                else if(currentColor == color && stoneBetween){
                    byte k = y;
                    for (byte i = x; i <= nx; i++){
                        set(color, i, k);
                        k++;
                    }
                    turnedStones = true;
                    break;
                }
                else
                    break;
            }

            // -x +y
            stoneBetween = false;
            nx = x;
            ny = y;
            while(--nx >= 0 && ++ny <= 7){
                currentColor = get(nx, ny);
                if(currentColor == enemy(color)){
                    stoneBetween = true;
                    continue;
                }
                else if(currentColor == color && stoneBetween){
                    byte k = y;
                    for (byte i = x; i >= nx; i--){
                        set(color, i, k);
                        k++;
                    }
                    turnedStones = true;
                    break;
                }
                else
                    break;
            }
        }
        return turnedStones;
    }

    public boolean check(byte color, byte x, byte y){
        byte currentColor;
        boolean turnedStones = false;
        if(get(x, y) == 0){

            // -x
            boolean stoneBetween = false;
            byte nx = x;
            while(--nx >= 0){
                currentColor = get(nx, y);
                if(currentColor == enemy(color)){
                    stoneBetween = true;
                    continue;
                }
                else if(currentColor == color && stoneBetween){
                    turnedStones = true;
                    break;
                }
                else
                    break;
            }

            // +x
            stoneBetween = false;
            nx = x;
            while(++nx <= 7){
                currentColor = get(nx, y);
                if(currentColor == enemy(color)){
                    stoneBetween = true;
                    continue;
                }
                else if(currentColor == color && stoneBetween){
                    turnedStones = true;
                    break;
                }
                else
                    break;
            }

            // -y
            stoneBetween = false;
            byte ny = y;
            while(--ny >= 0){
                currentColor = get(x, ny);
                if(currentColor == enemy(color)){
                    stoneBetween = true;
                    continue;
                }
                else if(currentColor == color && stoneBetween){
                    turnedStones = true;
                    break;
                }
                else
                    break;
            }

            // +y
            stoneBetween = false;
            ny = y;
            while(++ny <= 7){
                currentColor = get(x, ny);
                if(currentColor == enemy(color)){
                    stoneBetween = true;
                    continue;
                }
                else if(currentColor == color && stoneBetween){
                    turnedStones = true;
                    break;
                }
                else
                    break;
            }

            // -x -y
            stoneBetween = false;
            nx = x;
            ny = y;
            while(--nx >= 0 && --ny >= 0){
                currentColor = get(nx, ny);
                if(currentColor == enemy(color)){
                    stoneBetween = true;
                    continue;
                }
                else if(currentColor == color && stoneBetween){
                    turnedStones = true;
                    break;
                }
                else
                    break;
            }

            // +x -y
            stoneBetween = false;
            nx = x;
            ny = y;
            while(++nx <= 7 && --ny >= 0){
                currentColor = get(nx, ny);
                if(currentColor == enemy(color)){
                    stoneBetween = true;
                    continue;
                }
                else if(currentColor == color && stoneBetween){
                    turnedStones = true;
                    break;
                }
                else
                    break;
            }

            // +x +y
            stoneBetween = false;
            nx = x;
            ny = y;
            while(++nx <= 7 && ++ny <= 7){
                currentColor = get(nx, ny);
                if(currentColor == enemy(color)){
                    stoneBetween = true;
                    continue;
                }
                else if(currentColor == color && stoneBetween){
                    turnedStones = true;
                    break;
                }
                else
                    break;
            }

            // -x +y
            stoneBetween = false;
            nx = x;
            ny = y;
            while(--nx >= 0 && ++ny <= 7){
                currentColor = get(nx, ny);
                if(currentColor == enemy(color)){
                    stoneBetween = true;
                    continue;
                }
                else if(currentColor == color && stoneBetween){
                    turnedStones = true;
                    break;
                }
                else
                    break;
            }
        }
        return turnedStones;
    }

    public void set(byte color, byte x, byte y){
        board[y] &= ~(0b11 << 2 * (7 - x));
        board[y] |= (color << 2 * (7 - x));
    }

    final public byte get(byte x, byte y){
        return (byte) ((board[y] & (0b11 << 2 * (7 - x))) >>> 2 * (7 - x));
    }

    final public static byte enemy(byte color){
        if(color == 1)
            return 2;
        else
            return 1;
    }

    public void copy(GameBoard gb){
        for (byte i = 1; i < 9; i++)
            for (byte k = 1; k < 9; k++)
                try{
                    set((byte) gb.getOccupation(new Coordinates(i, k)), (byte) (k - 1), (byte) (i - 1));
                }catch(OutOfBoundsException e){
                    e.printStackTrace();
                    System.out.println(k + "/" + i);
                }
    }

    public void copy(Board b){
        for (byte i = 0; i < 8; i++)
            board[i] = b.board[i];
    }

    public static void printOld(GameBoard gb){
        for (byte i = 1; i < 9; i++){
            for (byte k = 1; k < 9; k++)
                try{
                    System.out.print((byte) gb.getOccupation(new Coordinates(i, k))
                            + " ");
                }catch(OutOfBoundsException e){
                    e.printStackTrace();
                    System.out.println(k + "/" + i);
                }
            System.out.print("\r");
        }
    }

    public void print(){
        for (byte i = 0; i < 8; i++){
            for (byte k = 0; k < 8; k++)
                System.out.print(get(k, i) + " ");
            System.out.print("\r");
        }
    }

}
