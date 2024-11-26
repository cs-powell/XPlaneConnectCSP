

import ModelFiles.*;

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

/**
 * An example program demonstrating the basic features of the X-Plane Connect toolbox.
 *
 * @author Jason Watkins
 * @version 1.0
 * @since 2015-04-06
 */
public class Main
{
    public static void main(String[] args) {
        System.out.println("X-Plane Connect example program");
        System.out.println("Setting up simulation...");
        JFrame frame = new JFrame();

        try(XPlaneConnect xpc = new XPlaneConnect())
        {
            // Ensure connection established.
        xpc.getDREF("sim/test/test_float");

            // System.out.println("Setting player aircraft position");
            // double[] posi = new double[] {37.524, -122.06899, 2500, 0, 0, 0, 1};
            // xpc.sendPOSI(posi);

            // System.out.println("Setting another aircraft position");
            // posi[0] = 37.52465;
            // posi[4] = 20;
            // xpc.sendPOSI(posi, 1);

            System.out.println("Setting rates");
            float[][] data = new float[3][9];
            for(float[] row : data)
            {
                Arrays.fill(row, -998);
            }
            data[0][0] = 0; //Alpha
            data[0][1] = 0;
            data[0][3] = 0;

            data[1][0] = 0; //Velocity
            data[1][1] = 0;
            data[1][2] = 0;
            data[1][3] = 0;
            data[1][4] = 0;

            data[2][0] = 0; //PQR
            data[2][1] = 0;
            data[2][2] = 0;
            data[2][3] = 0;

            xpc.sendDATA(data);

        System.out.println("Trying something new!!");

        
        frame.setPreferredSize(new Dimension(1700,270));
        frame.setLayout(new GridLayout(1,2));
        frame.setVisible(true);

        JPanel display = new JPanel();
        display.setPreferredSize(new Dimension(100,100));
        display.setLayout(new GridLayout(2,4));

        Font fontTitles = new Font("Impact", 1,40);
        Font fontData = new Font("Monospaced", 1,30);
        JLabel titleOne = new JLabel("Elevator");
        titleOne.setFont(fontTitles);
        titleOne.setHorizontalAlignment(SwingConstants.CENTER);
        titleOne.setVerticalAlignment(SwingConstants.BOTTOM);
        JLabel titleTwo = new JLabel("Roll");
        titleTwo.setFont(fontTitles);
        titleTwo.setHorizontalAlignment(SwingConstants.CENTER);
        titleTwo.setVerticalAlignment(SwingConstants.BOTTOM);
        JLabel titleThree = new JLabel("Yaw");
        titleThree.setFont(fontTitles);
        titleThree.setHorizontalAlignment(SwingConstants.CENTER);
        titleThree.setVerticalAlignment(SwingConstants.BOTTOM);
        JLabel titleFour = new JLabel("Throttle");
        titleFour.setFont(fontTitles);
        titleFour.setHorizontalAlignment(SwingConstants.CENTER);
        titleFour.setVerticalAlignment(SwingConstants.BOTTOM);
        JLabel one = new JLabel();
        one.setFont(fontData);
        one.setHorizontalAlignment(SwingConstants.CENTER);
        one.setVerticalAlignment(SwingConstants.TOP);
        JLabel two = new JLabel();
        two.setFont(fontData);
        two.setHorizontalAlignment(SwingConstants.CENTER);
        two.setVerticalAlignment(SwingConstants.TOP);
        JLabel three = new JLabel();
        three.setFont(fontData);
        three.setHorizontalAlignment(SwingConstants.CENTER);
        three.setVerticalAlignment(SwingConstants.TOP);
        JLabel four = new JLabel();
        four.setFont(fontData);
        four.setHorizontalAlignment(SwingConstants.CENTER);
        four.setVerticalAlignment(SwingConstants.TOP);

        one.setText("Test");
        two.setText("Test");
        three.setText("Test");
        four.setText("Test");
        display.add(titleOne);
        display.add(titleTwo);
        display.add(titleThree);
        display.add(titleFour);
        display.add(one);
        display.add(two);
        display.add(three);
        display.add(four);

        JPanel visualizer = new JPanel();
        visualizer.setLayout(new GridLayout(2,1));


        //Col 1 of Visualizer
        JPanel yokeGrid = new JPanel();
        yokeGrid.setMinimumSize(new Dimension(100,100));
        //grid.setOpaque(true);
        // grid.setLayout(null);
        yokeGrid.setBackground(Color.black);
        yokeGrid.setVisible(true);
        Integer layer1 = 0;
        Integer layer2 = 1;
        //    grid.setLayout(new GridLayout(1,1));
        yokeGrid.setLayout(new OverlayLayout(yokeGrid));

        yokePosition yoke = new yokePosition(yokeGrid.getWidth(), yokeGrid.getHeight());
        // yaw yaw = new yaw(grid.getWidth(), grid.getHeight());
        axis axis = new axis(yokeGrid.getWidth(), yokeGrid.getHeight());
        
        // grid.setLayer(axis, layer1);
        yokeGrid.add(axis);
        yokeGrid.add(yoke);
        // grid.add(yaw);
        // grid.setLayer(yoke, layer2);
        
        Border greenLine = BorderFactory.createLineBorder(Color.green);
        yokeGrid.setBorder(greenLine);

        visualizer.add(yokeGrid);


        //Row 2 of Visualizer
        //Rudder Vizualizer
        rudderPosition  rudderGrid = new rudderPosition(yokeGrid.getWidth(), 0);
        visualizer.add(rudderGrid);



        
        frame.add(display); // Left Side
        frame.add(visualizer); // Right Side
        frame.pack();

        // JLabel text = new JLabel("Hello");
        // JLabel text2 = new JLabel("Hello");
        // JPanel display = new JPanel();
        // display.setLayout(new GridLayout());
        // display.setPreferredSize(size);
        // display.add(text);
        // display.add(text2);

        // container.add(display);
        // container.pack();




        int aircraft = 0;
        boolean takeoff = true;
        boolean climb = false;
        boolean cruise = false;
        boolean throttleFull = false;
        boolean brakeOff = false;
        boolean switchTrack = false;
        String dref = "sim/cockpit2/gauges/indicators/airspeed_kts_pilot";
        String dref2 = "sim/flightmodel/position/true_phi";
        String drefHDG = "sim/cockpit2/gauges/indicators/heading_AHARS_deg_mag_pilot";
        String drefHDGBug = "sim/cockpit/autopilot/heading_mag";
        String drefAltitude = "sim/cockpit2/gauges/indicators/altitude_ft_pilot";

        File file = new File("/Users/flyingtopher/Desktop/Code Citadel/School/2. Research/Fork Clone/XPlaneConnectCSP/Java/Project_Models/Model-1/src/output");
        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter fw = new FileWriter(file, true);
        BufferedWriter bw = new BufferedWriter(fw);

        while(true) {

            //THE GETTERS
            double[] posi1 = xpc.getPOSI(aircraft); // FIXME: change this to 64-bit double
            float[] ctrl1 = xpc.getCTRL(aircraft);
            float[] value = xpc.getDREF(dref);
            float[] value2 = xpc.getDREF(dref2);
            float[] valueHDG = xpc.getDREF(drefHDG);
            float[] valueHDGBug = xpc.getDREF(drefHDGBug);
            float[] valueAltitude = xpc.getDREF(drefAltitude);
            float bugged = 50;
            float rwyHDG = valueHDGBug[0];
        /*
         * Outputs
         */
            // System.out.format("\r[Elevator: %2f] [Roll: %2f] [Yaw: %2f] ---- [Throttle:%2f] ---- [Flaps: %2f] -- [Data Ref: %2f] -- [T/O: %b ][Cruise: %b ]",
            //          ctrl1[0], ctrl1[1], ctrl1[2], ctrl1[3], ctrl1[5], valueAltitude[0], takeoff, cruise);

            String log = String.format("[Elevator: %2f] [Roll: %2f] [Yaw: %2f] [Throttle:%2f] [Flaps: %2f] [Data Ref: %2f] [T/O: %b ][Cruise: %b ]",
                     ctrl1[0], ctrl1[1], ctrl1[2], ctrl1[3], ctrl1[5], valueAltitude[0], takeoff, cruise);

            one.setText(String.valueOf(ctrl1[0]));
            if(ctrl1[0] >= 0 ) {
                one.setForeground(Color.green);
            } else {
                one.setForeground(Color.red);
            }
            two.setText(String.valueOf(ctrl1[1]));
            if(ctrl1[1] >= 0 ) {
                two.setForeground(Color.green);
            } else {
                two.setForeground(Color.red);
            }
            three.setText(String.valueOf(ctrl1[2]));
            if(ctrl1[2] >= 0 ) {
                three.setForeground(Color.green);
            } else {
                three.setForeground(Color.red);
            }
            four.setText(String.valueOf(ctrl1[3]));
            if(ctrl1[3] >= 0 ) {
                four.setForeground(Color.green);
            } else {
                four.setForeground(Color.red);
            }

            yoke.setXBound(yokeGrid.getWidth());
            yoke.setYBound(yokeGrid.getHeight());
            yoke.setX(ctrl1[1]);
            yoke.setY(ctrl1[0]);
            yoke.repaint();

            rudderGrid.setXBound(yokeGrid.getWidth());
            rudderGrid.setYBound(yokeGrid.getHeight());
            rudderGrid.setX(ctrl1[2]);
            rudderGrid.repaint();

            

            // yaw.setYBound(grid.getHeight());
            // yaw.setYBound(grid.getHeight());
            // yaw.setX(ctrl1[2]);
            // yaw.repaint();

            axis.setXBound(yokeGrid.getWidth());
            axis.setYBound(yokeGrid.getHeight());
            axis.repaint();

            //Writing Data to a File
            try {
                bw.write(log + "\n");
                bw.flush();
            } catch (IOException e) {
                System.out.println("Log Data Failed");
            }
        /*
         * Flight Controls
         */
            // //Basic Autopilot For Roll (based on yoke position)
            // float[] rollRight = {-998.0f, ctrl1[1]-(ctrl1[1]/2)};
            // float[] rollLeft = {-998.0f, ctrl1[1]-(ctrl1[1]/2)};
            // if(ctrl1[1] < 0) {
            //     xpc.sendCTRL(rollRight);
            // } else if(ctrl1[1] > 0) {
            //     xpc.sendCTRL(rollLeft);
            // }

            //Basic Autopilot For Pitch (based on VSI)
            // float[] pitchUp = {ctrl1[0] + 0.01f};
            // float[] pitchDown = {ctrl1[0]- 0.01f};
            // if(value[0] < 0) {
            //     xpc.sendCTRL(pitchUp);
            // } else if(value[0] > 0) {
            //     xpc.sendCTRL(pitchDown);
            // }



            if(valueAltitude[0] > 1000f && !switchTrack && takeoff == true) {
                System.out.println("In switch");
                takeoff = false;
                cruise = true;
                switchTrack = true;
            } else if (valueAltitude[0] < 1000f && switchTrack && takeoff == true) {
                takeoff = true;
                cruise = false;
                switchTrack = false;
            }
            

            //Control Profiles
            if(takeoff) {
                float[] fullThrottle = {-998.0f, -998.0f, -998.0f, 1f};
                if(!throttleFull) {
                    xpc.sendCTRL(fullThrottle);
                    throttleFull = true;
                }

                if(throttleFull && !brakeOff) {
                    String parkingBrake = "sim/cockpit2/controls/parking_brake_ratio";
                    xpc.sendDREF(parkingBrake, 0f);
                    brakeOff = true;
                }

                //Takeoff Pitch Control
                float[] pitchUp = {ctrl1[0] + 0.01f};
                if(value[0] > bugged) {
                        if(ctrl1[0] < 0.1f){
                            xpc.sendCTRL(pitchUp);
                        }
                }

                //Takeoff Roll Control 
                float[] rollRight = {-998.0f, ctrl1[1] + 0.01f};
                float[] rollLeft = {-998.0f, ctrl1[1] - 0.01f};
                if(value2[0] < 0  && value[0] > bugged) {
                    if(ctrl1[1] < 0.15f) {
                        xpc.sendCTRL(rollRight);
                    }

                } else if(value2[0] > 0  && value[0] > bugged) {
                    if(ctrl1[1] > -0.15f) {
                        xpc.sendCTRL(rollLeft);
                    }
                }

                //Takeoff Rudder Control 
                float[] yawRight = {-998.0f, -998.0f, ctrl1[2] + 0.03f};
                float[] yawLeft = {-998.0f, -998.0f, ctrl1[2] - 0.03f};
                if(valueHDG[0] < rwyHDG && value[0] > 0) {

                    if(ctrl1[2] < 0.5f) {
                        xpc.sendCTRL(yawRight); // YAW RIGHT
                    }

                } else if(valueHDG[0] > rwyHDG && value[0] > 0) {
                    if(ctrl1[2] > -0.5f){
                        xpc.sendCTRL(yawLeft); // YAW LEFT 
                    }
                }
            }


            if(cruise) {
            //Basic Autopilot For Roll (based on bank angle)
             float[] rollRight = {-998.0f, ctrl1[1] + 0.01f};
             float[] rollLeft = {-998.0f, ctrl1[1] - 0.01f};
             if(value2[0] < 0) {

                if(ctrl1[1] < 0.15f) {
                    xpc.sendCTRL(rollRight);
                }

             } else if(value2[0] > 0) {
                if(ctrl1[1] > -0.15f) {
                    xpc.sendCTRL(rollLeft);
                }
             }

             //Basic Autopilot For Pitch (based on speed)
             float[] pitchUp = {ctrl1[0] + 0.01f};
             float[] pitchDown = {ctrl1[0]- 0.01f};

             if(value[0] > bugged+25) {
                if(ctrl1[0] < 0.2f) {
                    xpc.sendCTRL(pitchUp);
                }
             } else if(value[0] < bugged+25) {
                if(ctrl1[0] > -0.2f) {
                    xpc.sendCTRL(pitchDown);
                }
             }

            }
            
            try {
                Thread.sleep(0);
            }
            catch (InterruptedException ex) {}
        
            if (System.in.available() > 0) {
                break;
            }
        }

            // System.out.println("Setting controls");
            // float[] ctrl = new float[4];
            // ctrl[3] = 0.8F;
            // xpc.sendCTRL(ctrl);

            // System.out.println("Pausing sim");
            // xpc.pauseSim(true);
            // try { Thread.sleep(5000); } catch (InterruptedException ex) {}
            // System.out.println("Un-pausing");
            // xpc.pauseSim(false);
            
            System.out.println("Example complete");
            
        }
        catch (SocketException ex)
        {
            System.out.println("Unable to set up the connection. (Error message was '" + ex.getMessage() + "'.)");
        }
        catch (IOException ex)
        {
            System.out.println("Something went wrong with one of the commands. (Error message was '" + ex.getMessage() + "'.)");
            frame.dispose();
            
        }
        System.out.println("Exiting");
    }


    //Helper Methods
    // public static void logData (BufferedWriter bw, String log) {
    //         bw.write(log);
    // }
}

class axis extends JComponent {

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



// class yaw extends JComponent {

//     int xBound;
//     int yBound;
//     float x = 0;


//     int currX = (int)(x*xBound) + xBound/2;

//     yaw(int currentX, int currentY) {
//         xBound = currentX;
//         yBound = currentY;
//     }

//     public void paint(Graphics g)
//     {
//         Graphics2D g2 = (Graphics2D) g;
//         g2.setColor(Color.blue);
//         g2.drawLine(currX, 0, currX, yBound);
//         g2.fillOval(currX, yBound/2, 20, 20);
//     }

//     public void setXBound(int newX){
//         this.xBound = newX;
//     }

//     public void setYBound(int newY){
//         this.yBound = newY;
//     }

//     public void setX(float newX){
//         this.x = newX;
//     }
// }










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

        try {
            final BufferedImage image = ImageIO.read(new File("/Users/flyingtopher/Desktop/Code Citadel/School/2. Research/Fork Clone/XPlaneConnectCSP/Java/Project_Models/Model-1/content/Yoke Symbol.png"));
            Image scaledImage = image.getScaledInstance(xBound, yBound, Image.SCALE_DEFAULT);
            
            g.drawImage(scaledImage, 0, 0, getFocusCycleRootAncestor());
        } catch (IOException e){
            System.out.print("Rudder Image Failed");
        }

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


class rudderPosition extends JComponent {

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
        try {
            final BufferedImage image = ImageIO.read(new File("/Users/flyingtopher/Desktop/Code Citadel/School/2. Research/Fork Clone/XPlaneConnectCSP/Java/Project_Models/Model-1/content/Rudder Pedals.png"));
            Image scaledImage = image.getScaledInstance(xBound, yBound, Image.SCALE_DEFAULT);
            g.drawImage(scaledImage, 0, 0, getFocusCycleRootAncestor());
        } catch (IOException e) {
            System.out.print("Rudder Image Failed");
        }
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