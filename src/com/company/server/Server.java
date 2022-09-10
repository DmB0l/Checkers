package com.company.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Server {
    private static final UserList users = new UserList();
    private static final Logger logger = Logger.getLogger("MyLog");

    public static void main(String[] args){
//        if(args.length != 1){
//            System.out.println("Wrong");
//        }
//        int portNumber = Integer.parseInt(args[0]);

        int portNumber = 9876;

        FileHandler fh = null;
        try {
            fh = new FileHandler("logs/serverLog.log", false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.addHandler(fh);
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);

        ServerSocket serverSocket;
        try{
            serverSocket = new ServerSocket(portNumber);
            System.out.println("Server is started");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ServerThread serverThread = new ServerThread(clientSocket,users, logger);
                serverThread.start();
                users.addUser(serverThread);
            }
        } catch (IOException e) {
            System.out.println("Server is not started");
        }
    }
}
