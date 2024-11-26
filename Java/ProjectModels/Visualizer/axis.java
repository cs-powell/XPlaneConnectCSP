package Visualizer;


import javax.swing.JComponent;


import java.awt.Graphics;
import java.awt.*;


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
