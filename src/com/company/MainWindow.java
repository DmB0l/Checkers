package com.company;

import com.company.client.Client;
import com.company.view.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class MainWindow extends JFrame {

    MainWindow(String title) {
        super(title);
        Client client = new Client(this);

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                client.sendMess("@exit");
            }
        });

        setResizable(false);
        setMinimumSize(new Dimension(800, 900));
        new MainMenu(this, client);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MainWindow("Шашки");
            }
        });


    }
}
