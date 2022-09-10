package com.company.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Logger;

public class ServerThread extends Thread {
    private Socket socket;
    private PrintWriter out;
    private String userName = "User";
    private BufferedReader in;
    private final UserList users;
    private final Logger logger;

    public ServerThread(Socket s, UserList userList, Logger logger) throws IOException {
        this.logger = logger;
        users = userList;
        socket = s;
        System.out.println("client connected " + s.getPort());
        out = new PrintWriter(s.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(s.getInputStream()));
    }

    public void run() {
        try {
            String message;
            while (true) {
                message = in.readLine();
                if (message.equals("")) {
                    continue;
                }
                if (message.charAt(0) == '@') {
                    int spaceInd = message.indexOf(' ');
                    String command;

                    if (spaceInd == -1) {
                        command = message;
                    } else {
                        command = message.substring(0, spaceInd);
                    }

                    switch (command) {
                        case "@name" -> {
                            logger.info("запрос на смену имени от пользователя " + userName + " пришел");
                            String name = message.substring(spaceInd + 1);
                            if (users.isOnline(name)) {
                                out.println("@!changeName");
                            } else {
                                userName = name;
                                out.println("@changeName " + userName);
                            }
                            break;
                        }
                        case "@ready" -> {
                            String name = message.substring(spaceInd + 1);
                            logger.info("пользователь " + userName + " отправил запрос на новую игру с пользователем " + name);
                            if (users.isOnline(name)) {
                                sendMessage("@ready? " + userName, name);
                            } else {
                                out.println("@notOnline");
                            }
                            break;
                        }
                        case "@agreeReady" -> {
                            int secondSpaceInd = message.indexOf(' ', spaceInd + 1);
                            String name = message.substring(spaceInd + 1, secondSpaceInd);
                            logger.info("пользователь " + userName + " согласился на новую игру с пользователем " + name);
                            if (users.isOnline(name)) {
                                sendMessage(message, name);
                            }
                            break;
                        }
                        case "@notAgreeReady" -> {
                            String name = message.substring(spaceInd + 1);
                            logger.info("пользователь " + userName + " отказался от новой игры с пользователем " + name);
                            if (users.isOnline(name)) {
                                sendMessage(command, name);
                            }
                            break;
                        }
                        case "@justBusy" -> {
                            String name = message.substring(spaceInd + 1);
                            logger.info("пользователь " + userName + " отправил ответ, что он занят пользователю " + name);
                            if (users.isOnline(name)) {
                                sendMessage(command, name);
                            }
                            break;
                        }
                        case "@youWin" -> {
                            String name = message.substring(spaceInd + 1);
                            logger.info("пользователь " + userName + " отправил ответ, что пользователь " + name + " выиграл");
                            if (users.isOnline(name)) {
                                sendMessage(command, name);
                            }
                            break;
                        }
                        case "@go", "@goNo" -> {
                            int secondSpaceInd = message.indexOf(' ', spaceInd + 1);
                            String name = message.substring(spaceInd + 1, secondSpaceInd);
                            logger.info("пользователь " + userName + " отправил запрос на ход к пользователю " + name);
                            if (users.isOnline(name)) {
                                sendMessage(message, name);
                            }
                            break;
                        }
                        case "@unlock" -> {
                            String name = message.substring(spaceInd + 1);
                            logger.info("пользователь " + userName + " отправил запрос на разблокировку пользователю " + name);
                            if (users.isOnline(name)) {
                                sendMessage(command, name);
                            }
                            break;
                        }
                        case "@exit" -> {
                            logger.info("пользователь " + userName + " отправил запрос на выход из игры");
                            out.println(command);
                            users.removeUser(this);
                            socket.close();
                            synchronized (users) {
                                users.removeUser(this);
                            }
                            return;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String message) {
        PrintWriter out;
        synchronized (users) {
            for (ServerThread serverThread : users.getUserList()) {
                if (serverThread == this) {
                    continue;
                }
                out = serverThread.getWriter();
                out.println(message);
            }
        }
    }

    private void sendMessage(String message, String name) {
        PrintWriter out;
        synchronized (users) {
            for (ServerThread serverThread : users.getUserList()) {
                if (serverThread.getUsername().equals(name)) {
                    out = serverThread.getWriter();
                    out.println(message);
                    break;
                }
            }
        }
    }

    public PrintWriter getWriter() {
        return out;
    }

    public BufferedReader getReader() {
        return in;
    }


    public String getUsername() {
        return userName;
    }

}