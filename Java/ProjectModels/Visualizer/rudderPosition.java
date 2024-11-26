package Visualizer;

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

public class rudderPosition extends JComponent {

    int xBound = 0;
    int yBound = 0;

    float rudderTravel = 0;

    rudderPosition(int currentXBound, int currentYBound) {
       xBound = currentXBound;
       yBound = currentYBound;
    }
  
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        int spacer = 10;
        g.setColor(Color.gray);
        g2.fillRect(0, spacer, xBound, yBound - spacer);

        g2.setColor(Color.red);
        int width = (int) (xBound/2 * rudderTravel);
        //g2.drawRect(xBound/2, 0 +spacer, width, yBound);
        g2.fillRect(xBound/2, 0 + spacer, width, yBound);
        
        g.drawString("Yaw Left",0, yBound/2);
        int textWidth = g.getFontMetrics().stringWidth("Roll Right");
        g.drawString("Yaw Right", xBound - textWidth, yBound/2);
        // try {
        //     final BufferedImage image = ImageIO.read(new File("/Users/flyingtopher/Desktop/Code Citadel/School/2. Research/Fork Clone/XPlaneConnectCSP/Java/ProjectModels/Visualizer/assets/Rudder Pedals.png"));
        //     Image scaledImage = image.getScaledInstance(xBound, yBound, Image.SCALE_DEFAULT);
        //     g.drawImage(scaledImage, 0, 0, getFocusCycleRootAncestor());
        // } catch (IOException e) {
        //     System.out.print("Rudder Image Failed");
        // }
    }

    public void setX(float newX) {
        this.rudderTravel = newX;
    }

    public void setXBound(int newX) {
        this.xBound = newX;
    }

    public void setYBound(int newY) {
        this.yBound = newY;
    }

}