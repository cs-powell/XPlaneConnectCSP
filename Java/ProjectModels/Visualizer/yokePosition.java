package Visualizer;

import javax.swing.JComponent;
import java.awt.Graphics;
import java.awt.*;

class yokePosition extends JComponent {
    float x = 0;
    int xBound = 0;
    float y = 0;
    int yBound = 0;

    int width = 50;
    int height = 50;

    yokePosition(int currentX, int currentY) {
        xBound = currentX;
        yBound = currentY;
    }

    yokePosition() {
       
    }
  
    public void paint(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;

        // try {
        //     final BufferedImage image = ImageIO.read(new File("/Users/flyingtopher/Desktop/Code Citadel/School/2. Research/Fork Clone/XPlaneConnectCSP/Java/ProjectModels/Visualizer/assets/Yoke Symbol.png"));
        //     Image scaledImage = image.getScaledInstance(xBound, yBound, Image.SCALE_DEFAULT);
            
        //     g.drawImage(scaledImage, 0, 0, getFocusCycleRootAncestor());
        // } catch (IOException e){
        //     System.out.print("Rudder Image Failed");
        // }

        g2.setColor(Color.red);
        int currX = (int)(x*xBound) + xBound/2 - width/2;
        int currY = (int)(y*yBound) + yBound/2 - height/2;
        System.out.println("CurrX, CurrY: " + currX +", " + currY);
        // g2.drawOval(currX, currY, width, height);
       
        g2.fillOval(currX, currY, width, height);
        g.setColor(Color.green);
        int height = g.getFontMetrics().getHeight();
        g.drawString("Nose Down", (xBound/2) + 5, height);
        g.drawString("Nose Up", (xBound/2) + 5, yBound - height/2);
        g.drawString("Roll Left",0, yBound/2);
        int width = g.getFontMetrics().stringWidth("Roll Right");
        g.drawString("Roll Right", xBound - width, yBound/2);



        
    }

    public void setX(float newX){
        this.x = newX;
    }

    public void setXBound(int newX){
        this.xBound = newX;
    }

    public void setY(float newY){
        this.y = newY;
    }
    public void setYBound(int newY){
        this.yBound = newY;
    }
}