package Visualizer;


import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;


import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.OverlayLayout;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketException;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.border.Border;
import javax.imageio.ImageIO;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.*;

import ModelFiles.*;


public class axis extends JComponent {

    int xBound = 0;
    int yBound = 0;

    axis(int currentX, int currentY) {
        xBound = currentX;
        yBound = currentY;
    }

    public void paint(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.green);
        g2.drawLine(xBound/2, 0, xBound/2, yBound);
        g2.drawLine(0, yBound/2, xBound, yBound/2);
    }

    public void setXBound(int newX){
        this.xBound = newX;
    }

    public void setYBound(int newY){
        this.yBound = newY;
    }
}
