package com.company.view;

import com.company.client.Client;
import com.company.view.resourses.verticalLayout.*;
import com.company.view.resourses.buttons.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu extends JPanel implements ActionListener {
    private JFrame frame;
    private Client client;
    private JLabel namePlayer;
    private JTextField myName;
    private JTextField enemyName;

    public MainMenu(JFrame frame, Client client) {
        super(new VerticalLayout());
        this.client = client;
        this.frame = frame;
        setBackground(new Color(0x7F7F7F));
        JLabel checkers = new JLabel("Chess");
        checkers.setHorizontalAlignment(JLabel.CENTER);
        checkers.setFont(new Font("Serif", Font.BOLD, 64));

        namePlayer = new JLabel();
        namePlayer.setHorizontalAlignment(JLabel.CENTER);
        namePlayer.setFont(new Font("Serif", Font.BOLD, 24));
        if (client.getName() == null) namePlayer.setText("Create name");
        else namePlayer.setText("Your name: " + client.getName());

        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.X_AXIS));
        myName = new JTextField();
        //PromptSupport.setPrompt("Enter your name", myName);
        JButton addName = new ButtonMenuItem("ChangeName", this);
        namePanel.add(myName);
        namePanel.add(addName);


        JButton newGame = new ButtonMenuItem("Play", this);
        enemyName = new JTextField();
        //PromptSupport.setPrompt("Enter enemy name", enemyName);
        JButton exit = new ButtonMenuItem("Exit", this);

        add(checkers);
        add(namePlayer);
        add(newGame);
        add(namePanel);
        add(enemyName);
        add(exit);

        this.frame.setContentPane(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton) e.getSource();
        switch (button.getText()) {
            case "Exit" -> {
                client.sendMess("@exit");
            }
            case "Play" -> {
                if (client.getName() != null) {
                    if (!enemyName.getText().isEmpty()) {
                        if(client.getName().equals(enemyName.getText())) {
                            JOptionPane.showMessageDialog(frame, "enter enemy name, not yours");
                        }
                        else {
                            client.sendMess("@ready " + enemyName.getText());
                            while (true) {
                                if (client.isStartGame() > 0) {
                                    client.setStartGame(0);
                                    //namePlayer.setText("Your name is " + client.getName());
                                    new BoardMenu(frame, client);
                                    frame.setVisible(true);
                                    break;
                                } else if (client.isStartGame() == -1) {
                                    client.setStartGame(0);
                                    JOptionPane.showMessageDialog(frame, "player is not online");
                                    frame.setVisible(true);
                                    break;
                                } else if (client.isStartGame() == -2) {
                                    client.setStartGame(0);
                                    JOptionPane.showMessageDialog(frame, "player dont want play with you");
                                    frame.setVisible(true);
                                    break;
                                }
                                else if (client.isStartGame() == -3) {
                                    client.setStartGame(0);
                                    JOptionPane.showMessageDialog(frame, "player just in game");
                                    frame.setVisible(true);
                                    break;
                                }
                            }
                        }


                    } else JOptionPane.showMessageDialog(frame, "enter enemy name");
                } else JOptionPane.showMessageDialog(frame, "enter your name");
            }
            case "ChangeName" -> {
                if (!myName.getText().isEmpty()) {
                    client.sendMess("@name " + myName.getText());
                    while (true) {
                        if (client.isChangeName() > 0) {
                            namePlayer.setText("Your name is " + client.getName());
                            frame.setVisible(true);
                            break;
                        } else if (client.isChangeName() < 0) {
                            JOptionPane.showMessageDialog(frame, "this name just busy");
                            break;
                        }
                    }
                    client.setChangeName(0);
                    frame.setVisible(true);
                } else JOptionPane.showMessageDialog(frame, "write something on name text field");
            }
        }
    }
}
