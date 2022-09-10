package com.company.view.resourses.buttons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ButtonMenuItem extends JButton {

    public ButtonMenuItem(String text, ActionListener CustomListener){
        super(text);

        setFont(new Font("Arial", Font.PLAIN, 24));
        addActionListener(CustomListener);
        setContentAreaFilled(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (getModel().isPressed()) g.setColor(new Color(0x7C7CFF));
        else g.setColor(new Color(0xADFD8B2D));
        g.fillRect(0,0,getWidth(), getHeight());
        super.paintComponent(g);
    }
}
