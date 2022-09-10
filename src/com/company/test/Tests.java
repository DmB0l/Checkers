package com.company.test;

import com.company.model.Data;
import com.company.model.Move;
import org.junit.Before;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Tests {
    Data board;

    @Before
    public void init() {
        board = new Data();
        board.startNewGame(board.WHITE);
    }

    @Test
    public void moveTest() {
        Move move = new Move(5,0,4,1);
        board.makeMove(move);
        int[][] desk = board.getBoard();
        int[][] expected = board.getBoard();
        expected[5][0] = 0;
        expected[4][1] = 1;
        assertEquals(desk, expected);
    }

    @Test
    public void getMoveTest() {
        int[][] desk = new int[8][8];
        desk[2][2] = board.WHITE;
        board.setBoard(desk);

        Move[] moves = board.getMoves(board.WHITE);
        Move[] expected = {new Move(2,2, 1,3) , new Move(2,2,1,1)};

        assertEquals(moves.length, expected.length);

        for(int i = 0; i < moves.length; i++) {
            assertEquals(moves[i],expected[i]);
        }
    }

    @Test
    public void getEatTest() {
        int[][] desk = new int[8][8];
        desk[2][2] = board.WHITE;
        desk[1][1] = board.BLACK;
        board.setBoard(desk);

        Move[] moves = board.getEats(board.WHITE, 2,2);
        Move[] expected = {new Move(2,2, 0,0)};

        assertEquals(moves.length, expected.length);

        for(int i = 0; i < moves.length; i++) {
            assertEquals(moves[i],expected[i]);
        }
    }

    @Test
    public void canMoveTest() {
        int[][] desk = new int[8][8];
        desk[2][2] = board.WHITE;
        board.setBoard(desk);

        assertFalse(board.canMove(board.WHITE, 2,2,3,3));

        assertFalse(board.canMove(board.WHITE,2,2,8,8));

        assertTrue(board.canMove(board.WHITE,2,2,1,1));
    }

    @Test
    public void damkaMoveTest() {
        int[][] desk = new int[8][8];
        desk[0][0] = board.WHITE_QUEEN;
        board.setBoard(desk);

        Move[] moves = board.getMoves(board.WHITE);
        Move[] expected = new Move[7];
        for(int i = 1; i < 8 ; i++) {
            expected[i - 1] = new Move(0,0, i,i);
        }

        assertEquals(moves.length, expected.length);

        for(int i = 0; i < moves.length; i++) {
            assertEquals(moves[i],expected[i]);
        }
    }

    @Test
    public void damkaEatTest() {
        int[][] desk = new int[8][8];
        desk[0][0] = board.WHITE_QUEEN;
        desk[1][1] = board.BLACK_QUEEN;
        board.setBoard(desk);

        Move[] moves = board.getEats(board.WHITE, 0,0);
        Move[] expected = new Move[6];
        for(int i = 2; i < 8 ; i++) {
            expected[i - 2] = new Move(0,0, i,i);
        }

        assertEquals(moves.length, expected.length);

        for(int i = 0; i < moves.length; i++) {
            assertEquals(moves[i],expected[i]);
        }
    }
}

