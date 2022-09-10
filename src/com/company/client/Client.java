package com.company.client;

import com.company.model.Move;
import com.company.view.BoardMenu;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Client {

    private final Logger logger = Logger.getLogger("MyLog");
    private FileHandler fh;
    private final int BLACK = 3, WHITE = 1;
    private int colorPlayer = 0;
    private boolean onGame = false;
    private boolean isWin = false;
    private String name;
    private String enemy;
    private int changeName = 0;
    private int startGame = 0;
    private boolean locked = false;
    private PrintWriter out;
    private BufferedReader in;
    private JFrame frame;
    private Move move;
    private Move moveNo;

    public Client(JFrame frame) {
//        if(args.length != 2){
//            System.out.println("Wrong");
//        }

//        String ip = args[0];
//        int port = Integer.parseInt(args[1]);

        int counter = 0;
        while (true) {
            File checkFile = new File("logs/clientLog" + counter + ".log");
            if (checkFile.exists()) {
                counter++;
            } else break;
        }

        try {
            fh = new FileHandler("logs/clientLog" + counter + ".log", false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.addHandler(fh);
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);

        this.frame = frame;
        String ip = "localhost";
        int port = 9876;
        Socket socket;

        try {
            socket = new Socket(ip, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            ReaderThread readerThread = new ReaderThread(in, this);
            readerThread.start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMess(String message) {
        synchronized (this) {
            out.println(message);
        }
    }

    public void borderMenu() {
        new BoardMenu(frame, this);
    }

    private class ReaderThread extends Thread {
        private BufferedReader in;
        private Client client;

        public ReaderThread(BufferedReader in, Client client) {
            this.client = client;
            this.in = in;
        }

        public void run() {
            String messageFromServer;
            try {
                while (true) {
                    messageFromServer = in.readLine();
                    synchronized (this) {
                        if (messageFromServer.equals("@!changeName")) {
                            logger.info("Пришел отказ на смену имени");
                            changeName = -1;

                        } else if (messageFromServer.startsWith("@changeName")) {
                            int prefInd = messageFromServer.indexOf(' ');
                            name = messageFromServer.substring(prefInd + 1);
                            logger.info("Пришел запрос на смену имени на " + name);
                            changeName = 1;

                        } else if (messageFromServer.equals("@notOnline")) {
                            logger.info("Пришел ответ: пользователь не в сети");
                            startGame = -1;

                        } else if (messageFromServer.startsWith("@ready?")) {
                            int prefInd = messageFromServer.indexOf(' ');
                            String enemyName = messageFromServer.substring(prefInd + 1);

                            logger.info("Пришел запрос на начало игры с пользователем " + enemyName);

                            if (isOnGame()) {
                                sendMess("@justBusy " + enemyName);
                                continue;
                            }

                            int res = JOptionPane.showConfirmDialog(frame, "Player " + enemyName + " want play with you",
                                    "mess", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                            if (res == JOptionPane.YES_OPTION) {
                                enemy = enemyName;
                                colorPlayer = (int) (Math.random() * 2);
                                int enemyColor;
                                if (colorPlayer == 0) {
                                    colorPlayer = WHITE;
                                    enemyColor = BLACK;
                                } else {
                                    colorPlayer = BLACK;
                                    enemyColor = WHITE;
                                }
                                client.borderMenu();

                                setOnGame(true);
                                sendMess("@agreeReady " + enemyName + " " + enemyColor + " " + name);
                            } else {
                                sendMess("@notAgreeReady " + enemyName);
                            }

                        } else if (messageFromServer.startsWith("@agreeReady")) {
                            int prefInd = messageFromServer.indexOf(' ');
                            int secondPrefInd = messageFromServer.indexOf(' ', prefInd + 1);
                            int thirdPrefInd = messageFromServer.indexOf(' ', secondPrefInd + 1);
                            enemy = messageFromServer.substring(thirdPrefInd + 1);
                            colorPlayer = Integer.parseInt(messageFromServer.substring(secondPrefInd + 1, thirdPrefInd));
                            setOnGame(true);
                            logger.info("Пришел ответ: Пользователь принял приглашение начинать игру");
                            startGame = 1;

                        } else if (messageFromServer.startsWith("@notAgreeReady")) {
                            logger.info("Пришел ответ: Отказ пользователя начинать игру");
                            startGame = -2;
                        } else if (messageFromServer.startsWith("@justBusy")) {
                            logger.info("Пришел ответ: Пользователь занят игрой с другим игроком");
                            startGame = -3;
                        } else if (messageFromServer.startsWith("@youWin")) {
                            logger.info("Пришел запрос на выигрыш");
                            setWin(true);
                        } else if (messageFromServer.startsWith("@goNo")) {
                            setMoveNo(parseMess(messageFromServer));

                            String mess = getColorString();

                            logger.info("Запрос на перестановку шашки без разблокировки для " + mess + " пришел");
                        } else if (messageFromServer.startsWith("@go")) {
                            setMove(parseMess(messageFromServer));

                            String mess = getColorString();

                            logger.info("Запрос на перестановку шашки для " + mess + " пришел");
                        } else if (messageFromServer.startsWith("@unlock")) {
                            setLocked(false);

                            String mess = getColorString();

                            logger.info("Запрос на разблокировку " + mess + " пришел");
                        } else if (messageFromServer.startsWith("@exit")) {
                            logger.info("Запрос на выход игрока пришел" + '\n' + "Игрок вышел");

                            fh.close();

                            System.exit(0);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Move parseMess(String messageFromServer) {
        int prefInd = messageFromServer.indexOf(' ');
        int secondPrefInd = messageFromServer.indexOf(' ', prefInd + 1);
        int prefInd3 = messageFromServer.indexOf(' ', secondPrefInd + 1);
        int prefInd4 = messageFromServer.indexOf(' ', prefInd3 + 1);
        int prefInd5 = messageFromServer.indexOf(' ', prefInd4 + 1);

        int fromRow = 7 - Integer.parseInt(messageFromServer.substring(secondPrefInd + 1, prefInd3));
        int fromCol = 7 - Integer.parseInt(messageFromServer.substring(prefInd3 + 1, prefInd4));
        int toRow = 7 - Integer.parseInt(messageFromServer.substring(prefInd4 + 1, prefInd5));
        int toCol = 7 - Integer.parseInt(messageFromServer.substring(prefInd5 + 1));

        return new Move(fromRow, fromCol, toRow, toCol);
    }

    private String getColorString() {
        String mess = "";
        switch (colorPlayer) {
            case WHITE:
                mess = "белого игрока";
                break;
            case BLACK:
                mess = "черного игрока";
                break;
        }
        return mess;
    }

    public String getName() {
        synchronized (this) {
            return name;
        }
    }

    public int isChangeName() {
        synchronized (this) {
            return changeName;
        }
    }

    public int isStartGame() {
        synchronized (this) {
            return startGame;
        }
    }

    public void setChangeName(int changeName) {
        synchronized (this) {
            this.changeName = changeName;
        }
    }

    public void setStartGame(int startGame) {
        synchronized (this) {
            this.startGame = startGame;
        }
    }

    public int getColorPlayer() {
        synchronized (this) {
            return colorPlayer;
        }
    }

    public boolean isLocked() {
        synchronized (this) {
            return locked;
        }
    }

    public void setLocked(boolean locked) {
        synchronized (this) {
            this.locked = locked;
        }
    }

    public String getEnemy() {
        synchronized (this) {
            return enemy;
        }
    }

    public Move getMove() {
        synchronized (this) {
            return move;
        }
    }

    public void setMove(Move move) {
        synchronized (this) {
            this.move = move;
        }
    }

    public Move getMoveNo() {
        synchronized (this) {
            return moveNo;
        }
    }

    public void setMoveNo(Move moveNo) {
        synchronized (this) {
            this.moveNo = moveNo;
        }
    }

    public void setOnGame(boolean onGame) {
        synchronized (this) {
            this.onGame = onGame;
        }
    }

    public boolean isOnGame() {
        synchronized (this) {
            return onGame;
        }
    }

    public void setWin(boolean win) {
        synchronized (this) {
            isWin = win;
        }
    }

    public boolean isWin() {
        synchronized (this) {
            return isWin;
        }
    }
}
