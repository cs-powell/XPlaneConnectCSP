package gov.nasa.xpc.ex;

import gov.nasa.xpc.XPlaneConnect;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketException;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.border.Border;

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
    public static void main(String[] args)
    {
        System.out.println("X-Plane Connect example program");
        System.out.println("Setting up simulation...");
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
        



        JFrame frame = new JFrame();
        frame.setPreferredSize(new Dimension(300,100));
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
        visualizer.setLayout(new GridLayout(1,2));


        //Col 1 of Visualizer
        JPanel grid = new JPanel();
        grid.setMinimumSize(new Dimension(100,100));
        //grid.setOpaque(true);
        // grid.setLayout(null);
        grid.setBackground(Color.black);
        grid.setVisible(true);
        Integer layer1 = 0;
        Integer layer2 = 1;
    //    grid.setLayout(new GridLayout(1,1));
    grid.setLayout(new OverlayLayout(grid));

        yokePosition yoke = new yokePosition(grid.getWidth(), grid.getHeight());
        axis axis = new axis(grid.getWidth(), grid.getHeight());
        
        // grid.setLayer(axis, layer1);
        grid.add(axis);
        grid.add(yoke);
        // grid.setLayer(yoke, layer2);
        
        Border greenLine = BorderFactory.createLineBorder(Color.green);
        grid.setBorder(greenLine);

        visualizer.add(grid);


        //Col 2 of Visualizer

        
        frame.add(display);
        frame.add(visualizer);
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

        File file = new File("/Users/flyingtopher/Desktop/Code Citadel/School/2. Research/Fork Clone/XPlaneConnectCSP/Java/Examples/Model-1/src/output");
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
            if(ctrl1[0] >= 0 ){
                one.setForeground(Color.green);
            } else {
                one.setForeground(Color.red);
            }
            two.setText(String.valueOf(ctrl1[1]));
            if(ctrl1[1] >= 0 ){
                two.setForeground(Color.green);
            } else {
                two.setForeground(Color.red);
            }
            three.setText(String.valueOf(ctrl1[2]));
            if(ctrl1[2] >= 0 ){
                three.setForeground(Color.green);
            } else {
                three.setForeground(Color.red);
            }
            four.setText(String.valueOf(ctrl1[3]));
            if(ctrl1[3] >= 0 ){
                four.setForeground(Color.green);
            } else {
                four.setForeground(Color.red);
            }

            yoke.setXBound(grid.getWidth());
            yoke.setYBound(grid.getHeight());
            yoke.setX(ctrl1[1]);
            yoke.setY(ctrl1[0]);
            yoke.repaint();

            axis.setXBound(grid.getWidth());
            axis.setYBound(grid.getHeight());
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

            if(valueAltitude[0] > 1000f && !switchTrack){
                System.out.println("In switch");
                takeoff = false;
                cruise = true;
                switchTrack = true;
            }

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
                    if(ctrl1[1] > -0.15f){
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
                if(ctrl1[1] > -0.15f){
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

    axis(int currentX, int currentY){
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





class yokePosition extends JComponent {
    float x = 0;
    int xBound = 0;
    float y = 0;
    int yBound = 0;

    yokePosition(int currentX, int currentY){
        xBound = currentX;
        yBound = currentY;
    }


    yokePosition(){
       
    }
  
    public void paint(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.red);
        int currX = (int)(x*xBound) + xBound/2;
        int currY = (int)(y*yBound) + yBound/2;
        System.out.println("CurrX, CurrY: " + currX +", " + currY);
        g2.drawOval(currX, currY, 50, 50);
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