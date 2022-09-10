package com.company.view.field;

import com.company.client.Client;
import com.company.model.*;
import com.company.view.*;
import com.company.view.resourses.buttons.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Field extends JPanel implements ActionListener, MouseListener {
    private final int WHITE = 1, WHITE_QUEEN = 2, BLACK = 3, BLACK_QUEEN = 4;
    private final Data board;
    private boolean gameInProgress = true;
    private int currentPlayer;
    private boolean mustEat = true;
    private int selectedRow, selectedCol;
    private Move[] moves;
    private final JButton menuButton;
    private final JLabel message;
    private final JFrame frame;
    private final Client client;
    private Thread startGame;
    private Thread thread;
    private Thread winThread;

    public Field(JFrame frame, Client client) {
        this.frame = frame;
        this.client = client;
        setBackground(new Color(0x7F7F7F));
        addMouseListener(this);
        menuButton = new ButtonMenuItem("leave", this);
        message = new JLabel("", JLabel.CENTER);
        message.setFont(new Font("Serif", Font.BOLD, 32));
        message.setForeground(Color.black);
        board = new Data();
        newGame();
    }

    public JButton getMenuButton() {
        return menuButton;
    }

    public JLabel getMessage() {
        return message;
    }

    public void newGame() {
        currentPlayer = client.getColorPlayer();
        winThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    if (client.isWin()) {
                        gameOver("You Win!!!");
                        clearClient();
                        repaint();
                        break;
                    }
                }
            }
        });
        winThread.start();

        if (currentPlayer == BLACK) {
            client.setLocked(true);
            startGame = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!Thread.currentThread().isInterrupted()) {
                        //if (!client.isLocked()) {
                        if (client.getMove() != null) {
                            board.makeMove(client.getMove());
                            client.setMove(null);
                            moves = board.getMoves(currentPlayer);
                            message.setText("Ход черных");
                            repaint();
                            break;
                        }
                    }
                }
            });
            startGame.start();
        }
        System.out.println("Белые играют, черные заблокированы");
        board.startNewGame(currentPlayer);
        moves = board.getMoves(currentPlayer);
        message.setText("Ход белых");
        gameInProgress = true;
        repaint();
    }

    public void gameOver(String str) {
        message.setText(str);
        gameInProgress = false;
    }

    /**
     * Метод, отвечающий за то, что делать при нажатии на клетку
     *
     * @param row Нажатая строка
     * @param col Нажатый столбец
     */

    public void clickOnSquare(int row, int col) {
        for (Move move : moves)
            if (move.fromRow == row && move.fromCol == col) {
                selectedRow = row;
                selectedCol = col;
                if (currentPlayer == board.WHITE)
                    message.setText("Ход белых");
                else
                    message.setText("Ход черных");
                repaint();
                return;
            }

        for (Move legalMove : moves) {
            if (legalMove.fromRow == selectedRow && legalMove.fromCol == selectedCol && legalMove.toRow == row && legalMove.toCol == col) {
                doMakeMove(legalMove);
            }
        }
    }

    /**
     * Функция, которая отвечает за выполнение хода
     *
     * @param move Ход, который должен быть сделан
     */

    public void doMakeMove(Move move) {
        boolean eat = move.isEat(board);
        board.makeMove(move);
        if (eat) {
            moves = board.getEats(currentPlayer, move.toRow, move.toCol);
            if (moves != null) {
                client.sendMess("@goNo " + client.getEnemy() + " " + move.fromRow + " " + move.fromCol + " " + move.toRow + " " + move.toCol);
                message.setText("Вы должны есть");
                selectedRow = move.toRow;
                selectedCol = move.toCol;
                repaint();
                //clickOnSquare(selectedRow, selectedCol);
                return;
            }
        }
        if (currentPlayer == board.WHITE) message.setText("Ход черных");
        else message.setText("Ход белых");

        client.sendMess("@unlock " + client.getEnemy());
        moves = board.getMoves(currentPlayer);
        repaint();

        client.sendMess("@go " + client.getEnemy() + " " + selectedRow + " " + selectedCol + " " + move.toRow + " " + move.toCol);

        client.setLocked(true);
        //System.out.println(currentPlayer + " заблокирован");

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    // if (!client.isLocked()) {
                    if (client.getMove() != null) {
                        board.makeMove(client.getMove());
                        client.setMove(null);
                        if (currentPlayer == board.WHITE) message.setText("Ход белых");
                        else message.setText("Ход черных");
                        moves = board.getMoves(currentPlayer);
                        if (moves == null) {
                            client.sendMess("@youWin " + client.getEnemy());
                            gameOver("You lose");
                            clearClient();
                            repaint();
                            break;
                        }
                        repaint();
                        break;
                    }
                    //  }

                    if (client.getMoveNo() != null) {
                        board.makeMove(client.getMoveNo());
                        client.setMoveNo(null);
                        repaint();
                    }
                }
            }
        });
        thread.start();
    }

    public void paintComponent(Graphics g) {
        int sizeOfSquare = 80;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (row % 2 == col % 2)
                    g.setColor(Color.lightGray);
                else
                    g.setColor(Color.darkGray);
                g.fillRect(col * sizeOfSquare, row * sizeOfSquare, sizeOfSquare, sizeOfSquare);
                int sizeOfPawn = sizeOfSquare - (sizeOfSquare / 10);
                int positionInSquare = (sizeOfSquare - sizeOfPawn) / 2;
                final int y = positionInSquare + (row * sizeOfSquare) + sizeOfPawn / 4;
                final int x = positionInSquare + (col * sizeOfSquare) + sizeOfPawn / 4;
                switch (board.pieceAt(row, col)) {
                    case WHITE -> {
                        g.setColor(Color.WHITE);
                        g.fillOval(positionInSquare + col * sizeOfSquare, positionInSquare + row * sizeOfSquare, sizeOfPawn, sizeOfPawn);
                        g.setColor(Color.black);
                        g.drawOval(positionInSquare + col * sizeOfSquare, positionInSquare + row * sizeOfSquare + 2, sizeOfPawn, sizeOfPawn - 1);
                        g.drawOval(positionInSquare + col * sizeOfSquare, positionInSquare + row * sizeOfSquare + 1, sizeOfPawn, sizeOfPawn - 1);
                        g.drawOval(positionInSquare + col * sizeOfSquare, positionInSquare + row * sizeOfSquare, sizeOfPawn, sizeOfPawn - 1);
                    }
                    case BLACK -> {
                        g.setColor(Color.BLACK);
                        g.fillOval(positionInSquare + col * sizeOfSquare, positionInSquare + row * sizeOfSquare, sizeOfPawn, sizeOfPawn);
                        g.setColor(Color.WHITE);
                        g.drawOval(positionInSquare + col * sizeOfSquare, positionInSquare + row * sizeOfSquare + 2, sizeOfPawn, sizeOfPawn - 1);
                        g.drawOval(positionInSquare + col * sizeOfSquare, positionInSquare + row * sizeOfSquare + 1, sizeOfPawn, sizeOfPawn - 1);
                        g.drawOval(positionInSquare + col * sizeOfSquare, positionInSquare + row * sizeOfSquare, sizeOfPawn, sizeOfPawn - 1);
                    }
                    case WHITE_QUEEN -> {
                        g.setColor(Color.WHITE);
                        g.fillOval(positionInSquare + col * sizeOfSquare, positionInSquare + row * sizeOfSquare, sizeOfPawn, sizeOfPawn);
                        g.setColor(new Color(0xFF727272, true));
                        g.fillOval(x, y, sizeOfPawn / 2, sizeOfPawn / 2);
                    }
                    case BLACK_QUEEN -> {
                        g.setColor(Color.BLACK);
                        g.fillOval(positionInSquare + col * sizeOfSquare, positionInSquare + row * sizeOfSquare, sizeOfPawn, sizeOfPawn);
                        g.setColor(new Color(0xFF727272, true));
                        g.fillOval(x, y, sizeOfPawn / 2, sizeOfPawn / 2);
                    }
                }
            }
        }

        if (gameInProgress && moves != null && !client.isLocked()) {
            // Рисуется ободок вокруг полей, которыми можно ходить
            g.setColor(new Color(0xB8FF70));
            for (Move legalMove : moves) {
                g.drawRect(legalMove.fromCol * sizeOfSquare, legalMove.fromRow * sizeOfSquare, sizeOfSquare - 1, sizeOfSquare - 1);
                g.drawRect(legalMove.fromCol * sizeOfSquare + 1, legalMove.fromRow * sizeOfSquare + 1, sizeOfSquare - 3, sizeOfSquare - 3);
            }
            // После нажатия на пешку, рисуется то, куда можно походить
            if (selectedRow >= 0) {
                g.setColor(Color.white); // Рисуется ободок вокруг выбранной пешки
                g.drawRect(selectedCol * sizeOfSquare, selectedRow * sizeOfSquare, sizeOfSquare - 1, sizeOfSquare - 1);
                g.drawRect(selectedCol * sizeOfSquare, selectedRow * sizeOfSquare, sizeOfSquare - 3, sizeOfSquare - 3);
                g.setColor(Color.green); // рисуется ободок вокруг возможных для хода полей
                for (Move legalMove : moves) {
                    if (legalMove.fromCol == selectedCol && legalMove.fromRow == selectedRow) {
                        g.drawRect(legalMove.toCol * sizeOfSquare, legalMove.toRow * sizeOfSquare, sizeOfSquare - 1, sizeOfSquare - 1);
                        g.drawRect(legalMove.toCol * sizeOfSquare + 1, legalMove.toRow * sizeOfSquare + 1, sizeOfSquare - 3, sizeOfSquare - 3);
                    }
                }
            }
        }
        g.setColor(Color.black);
        g.drawRect(0, 0, getSize().width - 1, getSize().height - 1);
        g.drawRect(1, 1, getWidth() - 3, getHeight() - 3);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (gameInProgress && !client.isLocked()) {
            int col = (e.getX()) / 80;
            int row = (e.getY()) / 80;
            if (col >= 0 && col < 8 && row >= 0 && row < 8) {
                clickOnSquare(row, col);
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == menuButton) {
            if (!client.isWin())
                client.sendMess("@youWin " + client.getEnemy());
            client.setOnGame(false);
            if (thread != null)
                thread.interrupt();
            if (startGame != null)
                startGame.interrupt();
            if (winThread != null)
                winThread.interrupt();
            clearClient();
            client.setWin(false);
            new MainMenu(frame, client);
            frame.setVisible(true);
        }
    }

    private void clearClient() {
        client.setLocked(false);
        client.setMove(null);
        client.setMoveNo(null);
        //client.setWin(false);
    }
}
