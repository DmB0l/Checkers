package com.company.model;

import com.company.view.field.Field;

import java.util.ArrayList;

public class Data {

    public final int EMPTY = 0, WHITE = 1, WHITE_QUEEN = 2, BLACK = 3, BLACK_QUEEN = 4;
    private int[][] board = new int[8][8];
    public ArrayList<Integer> countOfMoveBecomeQueen = new ArrayList<>();

    public void startNewGame(int colorPlayer) {
        countOfMoveBecomeQueen.clear();
//        if(colorPlayer == WHITE)  {
//            board[2][3] = BLACK;
//            board[5][4] = WHITE;
//        }
//        if(colorPlayer == BLACK)  {
//            board[2][3] = WHITE;
//            board[5][4] = BLACK;
//        }
        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 8; column++) {
                if (row % 2 != column % 2) {
                    if (row < 3)
                        if (colorPlayer == WHITE) board[row][column] = BLACK;
                        else board[row][column] = WHITE;
                    else if (row > 4)
                        if (colorPlayer == WHITE) board[row][column] = WHITE;
                        else board[row][column] = BLACK;
                    else
                        board[row][column] = EMPTY;
                } else {
                    board[row][column] = EMPTY;
                }
            }
        }
    }

    public int pieceAt(int row, int column) {
        return board[row][column];
    }

    public void makeMove(Move move) {
        makeMove(move.fromRow, move.fromCol, move.toRow, move.toCol);
    }

    /**
     * Функция, которая отвечает за выполнение хода и освобождение позиции, на которой стояла чужая шашка
     *
     * @param fromRow Строка, с которой идет ход
     * @param fromCol Столбец, с которого идет ход
     * @param toRow   Строка, в которую переходит шашка
     * @param toCol   Столбец, на который переходит шашка
     */

    private void makeMove(int fromRow, int fromCol, int toRow, int toCol) {
        board[toRow][toCol] = board[fromRow][fromCol];
        board[fromRow][fromCol] = EMPTY;
        if (board[toRow][toCol] != EMPTY && fromRow != toRow) {
            if (fromRow > toRow) {
                if (toCol < fromCol) {
                    for (int i = toRow, j = toCol; i <= fromRow && j < 8; i++, j++) {
                        if (board[i][j] != EMPTY && i != toRow && j != toCol) {
                            board[i][j] = EMPTY;
                            break;
                        }
                    }
                } else {
                    for (int i = toRow, j = toCol; i <= fromRow && j >= 0; i++, j--) {
                        if (board[i][j] != EMPTY && i != toRow && j != toCol) {
                            board[i][j] = EMPTY;
                            break;
                        }
                    }
                }
            } else {
                if (toCol < fromCol) {
                    for (int i = toRow, j = toCol; i >= fromRow && j < 8; i--, j++) {
                        if (board[i][j] != EMPTY && i != toRow && j != toCol) {
                            board[i][j] = EMPTY;
                            break;
                        }
                    }
                } else {
                    for (int i = toRow, j = toCol; i >= fromRow && j >= 0; i--, j--) {
                        if (board[i][j] != EMPTY && i != toRow && j != toCol) {
                            board[i][j] = EMPTY;
                            break;
                        }
                    }
                }
            }
        }


        if ((toRow == 0 || toRow == 7) && board[toRow][toCol] == WHITE) {
            board[toRow][toCol] = WHITE_QUEEN;
        }
        if ((toRow == 0 || toRow == 7) && board[toRow][toCol] == BLACK) {
            board[toRow][toCol] = BLACK_QUEEN;
        }
    }

    /**
     * Функция, которая находит все возможные ходы текущего игрока
     *
     * @param player Текущий игрок
     * @return Возвращает все возможные ходы шашек. Если нет ходов, возвращает null
     */
    public Move[] getMoves(int player) {
        if (player != WHITE && player != BLACK) return null;

        int playerQueen;
        if (player == WHITE)
            playerQueen = WHITE_QUEEN;
        else
            playerQueen = BLACK_QUEEN;

        ArrayList<Move> moves = new ArrayList<>();

        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 8; column++) {
                if (board[row][column] == player) {
                    if (canEat(player, row + 1, column + 1, row + 2, column + 2) == 1)
                        moves.add(new Move(row, column, row + 2, column + 2));
                    if (canEat(player, row - 1, column + 1, row - 2, column + 2) == 1)
                        moves.add(new Move(row, column, row - 2, column + 2));
                    if (canEat(player, row + 1, column - 1, row + 2, column - 2) == 1)
                        moves.add(new Move(row, column, row + 2, column - 2));
                    if (canEat(player, row - 1, column - 1, row - 2, column - 2) == 1)
                        moves.add(new Move(row, column, row - 2, column - 2));
                }

                if (board[row][column] == playerQueen) {
                    boolean move1 = true;
                    boolean move2 = true;
                    boolean move3 = true;
                    boolean move4 = true;

                    for (int i = 1; i < 8; i++) {
                        for (int j = i + 1; j < 7; j++) {
                            if (move1 && canEat(player, row + i, column + i, row + j, column + j) == 1) {
                                moves.add(new Move(row, column, row + j, column + j));
                            } else if (move1 && canEat(player, row + i, column + i, row + j, column + j) == -1) {
                                move1 = false;
                            }
                            if (move2 && canEat(player, row - i, column + i, row - j, column + j) == 1) {
                                moves.add(new Move(row, column, row - j, column + j));
                            } else if (move2 && canEat(player, row - i, column + i, row - j, column + j) == -1) {
                                move2 = false;
                            }

                            if (move3 && canEat(player, row + i, column - i, row + j, column - j) == 1) {
                                moves.add(new Move(row, column, row + j, column - j));
                            } else if (move3 && canEat(player, row + i, column - i, row + j, column - j) == -1) {
                                move3 = false;
                            }
                            if (move4 && canEat(player, row - i, column - i, row - j, column - j) == 1) {
                                moves.add(new Move(row, column, row - j, column - j));
                            } else if (move4 && canEat(player, row - i, column - i, row - j, column - j) == -1) {
                                move4 = false;
                            }
                        }
                    }
                }
            }
        }

        if (moves.size() == 0) {
            for (int row = 0; row < 8; row++) {
                for (int column = 0; column < 8; column++) {
                    if (board[row][column] == player) {
                        if (canMove(player, row, column, row + 1, column + 1))
                            moves.add(new Move(row, column, row + 1, column + 1));
                        if (canMove(player, row, column, row - 1, column + 1))
                            moves.add(new Move(row, column, row - 1, column + 1));
                        if (canMove(player, row, column, row + 1, column - 1))
                            moves.add(new Move(row, column, row + 1, column - 1));
                        if (canMove(player, row, column, row - 1, column - 1))
                            moves.add(new Move(row, column, row - 1, column - 1));
                    } else if (board[row][column] == playerQueen) {
                        boolean move1 = true;
                        boolean move2 = true;
                        boolean move3 = true;
                        boolean move4 = true;

                        for (int i = 1; i < 8; i++) {
                            if (move1 && canMove(playerQueen, row, column, row + i, column + i))
                                moves.add(new Move(row, column, row + i, column + i));
                            else
                                move1 = false;
                            if (move2 && canMove(playerQueen, row, column, row - i, column + i)) {
                                moves.add(new Move(row, column, row - i, column + i));
                            } else
                                move2 = false;
                            if (move3 && canMove(playerQueen, row, column, row + i, column - i)) {
                                moves.add(new Move(row, column, row + i, column - i));
                            } else
                                move3 = false;
                            if (move4 && canMove(playerQueen, row, column, row - i, column - i))
                                moves.add(new Move(row, column, row - i, column - i));
                            else
                                move4 = false;
                        }
                    }
                }
            }
        }

        if (moves.size() == 0)
            return null;
        else {
            Move[] moveArray = new Move[moves.size()];
            for (int i = 0; i < moves.size(); i++)
                moveArray[i] = moves.get(i);
            return moveArray;
        }
    }

    /**
     * Функция, которая вызывается после того, как съели шашку, и возвращает дальнейшие ходы, куда может съесть текущая шашка
     *
     * @param player Текущий игрок
     * @param row    Строка, на которой находится в данный момент шашка
     * @param column Столбец, на котором находится в данный момент шашка
     * @return Возвращает все возможные дальнейшие пути. Если их нет, возвращает null
     */
    public Move[] getEats(int player, int row, int column) {
        if (player != WHITE && player != BLACK)
            return null;
        int playerQueen;
        if (player == WHITE)
            playerQueen = WHITE_QUEEN;
        else
            playerQueen = BLACK_QUEEN;

        ArrayList<Move> moves = new ArrayList<>();

        if (board[row][column] == player) {
            if (canEat(player, row + 1, column + 1, row + 2, column + 2) == 1)
                moves.add(new Move(row, column, row + 2, column + 2));
            if (canEat(player, row - 1, column + 1, row - 2, column + 2) == 1)
                moves.add(new Move(row, column, row - 2, column + 2));
            if (canEat(player, row + 1, column - 1, row + 2, column - 2) == 1)
                moves.add(new Move(row, column, row + 2, column - 2));
            if (canEat(player, row - 1, column - 1, row - 2, column - 2) == 1)
                moves.add(new Move(row, column, row - 2, column - 2));
        }

        if (board[row][column] == playerQueen) {
            boolean move1 = true;
            boolean move2 = true;
            boolean move3 = true;
            boolean move4 = true;

            for (int i = 1; i < 8; i++) {
                for (int j = i+1; j < 8; j++) {
                    if (move1 && canEat(player, row + i, column + i, row + j, column + j) == 1) {
                        moves.add(new Move(row, column, row + j, column + j));
                    } else if (move1 && canEat(player, row + i, column + i, row + j, column + j) == -1)
                        move1 = false;
                    if (move2 && canEat(player, row - i, column + i, row - j, column + j) == 1) {
                        moves.add(new Move(row, column, row - j, column + j));
                    } else if (move2 && canEat(player, row - i, column + i, row - j, column + j) == -1)
                        move2 = false;
                    if (move3 && canEat(player, row + i, column - i, row + j, column - j) == 1) {
                        moves.add(new Move(row, column, row + j, column - j));
                    } else if (move3 && canEat(player, row + i, column - i, row + j, column - j) == -1)
                        move3 = false;
                    if (move4 && canEat(player, row - i, column - i, row - j, column - j) == 1) {
                        moves.add(new Move(row, column, row - j, column - j));
                    } else if (move4 && canEat(player, row - i, column - i, row - j, column - j) == -1)
                        move4 = false;
                }
            }
        }

        if (moves.size() == 0)
            return null;
        else {
            Move[] moveArray = new Move[moves.size()];
            for (int i = 0; i < moves.size(); i++)
                moveArray[i] = moves.get(i);
            return moveArray;
        }
    }

    public int[][] getBoard() {
        return board;
    }

    public void setBoard(int[][] board) {
        this.board = board;
    }


    /**
     * @param player Текущий игрок
     * @param oppRow Строка, на которой стоит чужая пешка
     * @param oppCol Столбец, на которой стоит чужая пешка
     * @param toRow  Строка, на которую встанет выбранная пешка
     * @param toCol  Столбец, на который встанет выбранная пешка
     * @return -1, если не может съесть, так как чужая пешка защищена; 0, если не может съесть, но из-за некритичных причин; 1, если может съесть
     */
    protected int canEat(int player, int oppRow, int oppCol, int toRow, int toCol) {
        if (toRow < 0 || toRow >= 8 || toCol < 0 || toCol >= 8)
            return 0;

        if (board[oppRow][oppCol] == EMPTY) return 0;

        if (board[toRow][toCol] != EMPTY) {
            return -1;
        }

        if (player == WHITE || player == WHITE_QUEEN) {
            if (board[oppRow][oppCol] == BLACK || board[oppRow][oppCol] == BLACK_QUEEN) return 1;
        } else if (player == BLACK || player == BLACK_QUEEN) {
            if (board[oppRow][oppCol] == WHITE || board[oppRow][oppCol] == WHITE_QUEEN) return 1;
        }

        return -1;
    }

    /**
     * @param fromRow    Строка, с которой рассматривается возможность хода
     * @param fromColumn Столбец, с которого рассматривается возможность хода
     * @param toRow      Строка, в которую рассматривается возможность хода
     * @param toCol      Столбец, в который рассматривается возможность хода
     * @return false, если невозможно пройти; true, если возможно пройти
     */
    public boolean canMove(int player, int fromRow, int fromColumn, int toRow, int toCol) {

        if (toRow < 0 || toRow >= 8 || toCol < 0 || toCol >= 8)
            return false;

        if (board[toRow][toCol] != EMPTY)
            return false;

        if (player == WHITE_QUEEN || player == BLACK_QUEEN)
            return true;

        return toRow < fromRow;

    }
}
