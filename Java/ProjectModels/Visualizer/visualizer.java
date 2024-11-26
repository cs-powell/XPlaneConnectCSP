package Visualizer;

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

import java.awt.image.BufferedImage;
import java.awt.*;

import ModelFiles.*;

public class visualizer {

    JFrame frame = new JFrame();
    JPanel display = new JPanel();
    JPanel visualizer = new JPanel();
    JPanel yokeGrid = new JPanel();
    axis axis = new axis(0,0);
    rudderPosition  rudderGrid = new rudderPosition(yokeGrid.getWidth(), 0);
    yokePosition yoke = new yokePosition(yokeGrid.getWidth(), yokeGrid.getHeight());



    public visualizer(Model m){

    }


    public void initializeDisplay() {
        frame.setPreferredSize(new Dimension(1700,270));
        frame.setLayout(new GridLayout(1,2));
        frame.setVisible(true);

       
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

        
        visualizer.setLayout(new GridLayout(2,1));


        //Col 1 of Visualizer
        
        yokeGrid.setMinimumSize(new Dimension(100,100));
        //grid.setOpaque(true);
        // grid.setLayout(null);
        yokeGrid.setBackground(Color.black);
        yokeGrid.setVisible(true);
        Integer layer1 = 0;
        Integer layer2 = 1;
        //    grid.setLayout(new GridLayout(1,1));
        yokeGrid.setLayout(new OverlayLayout(yokeGrid));

        yoke = new yokePosition(yokeGrid.getWidth(), yokeGrid.getHeight());
        // yaw yaw = new yaw(grid.getWidth(), grid.getHeight());
        axis = new axis(yokeGrid.getWidth(), yokeGrid.getHeight());
        
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
        rudderGrid = new rudderPosition(yokeGrid.getWidth(), 0);
        visualizer.add(rudderGrid);

        frame.add(display); // Left Side
        frame.add(visualizer); // Right Side
        frame.pack();
    }


    public void updateVisualizer(float[] ctrl1){
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
    }
    
}





