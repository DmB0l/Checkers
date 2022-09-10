package com.company.model;

import java.util.Objects;

public class Move {
    public final int fromRow;
    public final int fromCol;
    public final int toRow;
    public final int toCol;

    public Move(int fromRow, int fromCol, int toRow, int toCol) {
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return fromRow == move.fromRow && fromCol == move.fromCol && toRow == move.toRow && toCol == move.toCol;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromRow, fromCol, toRow, toCol);
    }

    public boolean isEat(Data board) {
        if (fromRow - toRow > 1) {
            if (toCol < fromCol) {
                for (int i = toRow, j = toCol; i < fromRow && j < fromCol; i++, j++) {
                    if (board.pieceAt(i, j) != board.EMPTY) {
                        return true;
                    }
                }
            }
            else {
                for (int i = toRow, j = toCol; i < fromRow && j > fromCol; i++, j--) {
                    if (board.pieceAt(i, j) != board.EMPTY) {
                        return true;
                    }
                }
            }
        }

        else if (fromRow - toRow < -1) {
            if (toCol < fromCol) {
                for (int i = toRow, j = toCol; i > fromRow && j < fromCol; i--, j++) {
                    if (board.pieceAt(i, j) != board.EMPTY) {
                        return true;
                    }
                }
            } else {
                for (int i = toRow, j = toCol; i > fromRow && j > fromCol; i--, j--) {
                    if (board.pieceAt(i, j) != board.EMPTY) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
