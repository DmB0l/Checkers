package com.company.view;


import com.company.client.Client;
import com.company.view.field.*;

import javax.swing.*;
import java.awt.*;

public class BoardMenu extends JPanel {
    /**
     * Метод вызывается, если создается новая игра
     * @param frame
     */
        public BoardMenu(JFrame frame, Client client){
            setLayout(null);
            setPreferredSize(new Dimension(800,900));

            setBackground(new Color(0x7F7F7F));

            Field field = new Field(frame, client);

            add(field);
            add(field.getMessage());
            add(field.getMenuButton());

            field.setBounds(80,50,640,640);
            field.getMenuButton().setBounds(350, 10, 100, 30);
            field.getMessage().setBounds(250, 750, 300, 40);

            frame.setContentPane(this);
            frame.setVisible(true);
        }
}
