package Visualizer;

import javax.swing.border.Border;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import CognitiveModel.ModelFiles.*;

public class visualizer {

    JFrame frame = new JFrame();
    JPanel frameBottom = new JPanel();
    JPanel controlDisplay = new JPanel();
    JPanel modelDisplay = new JPanel();
    JTextArea modelQueue = new JTextArea();
    JTextArea modelVision = new JTextArea();
    JPanel mainDisplay = new JPanel();
    JPanel visualizer = new JPanel();
    JPanel yokeGrid = new JPanel();
    axis axis = new axis(0,0);
    rudderPosition  rudderGrid = new rudderPosition(yokeGrid.getWidth(), 0);
    yokePosition yoke = new yokePosition(yokeGrid.getWidth(), yokeGrid.getHeight());
    JLabel one = new JLabel();
    JLabel two = new JLabel();
    JLabel three = new JLabel();
    JLabel four = new JLabel();
    
    Model m;



    public visualizer(Model m){
        this.m = m;
    }

    public void initializeDisplay() {
        try { 
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel"); 
        } catch(Exception ignored){}
        
        frame.setPreferredSize(new Dimension(1700,270));
        frame.setLayout(new BorderLayout());
        frameBottom.setLayout(new GridLayout(1,2));
        frame.setVisible(true);

       
        controlDisplay.setPreferredSize(new Dimension(100,100));

        controlDisplay.setLayout(new GridLayout(2,4));

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
        one = new JLabel();
        one.setFont(fontData);
        one.setHorizontalAlignment(SwingConstants.CENTER);
        one.setVerticalAlignment(SwingConstants.TOP);
        two = new JLabel();
        two.setFont(fontData);
        two.setHorizontalAlignment(SwingConstants.CENTER);
        two.setVerticalAlignment(SwingConstants.TOP);
        three = new JLabel();
        three.setFont(fontData);
        three.setHorizontalAlignment(SwingConstants.CENTER);
        three.setVerticalAlignment(SwingConstants.TOP);
        four = new JLabel();
        four.setFont(fontData);
        four.setHorizontalAlignment(SwingConstants.CENTER);
        four.setVerticalAlignment(SwingConstants.TOP);

        one.setText("Test");
        two.setText("Test");
        three.setText("Test");
        four.setText("Test");
        controlDisplay.add(titleOne);
        controlDisplay.add(titleTwo);
        controlDisplay.add(titleThree);
        controlDisplay.add(titleFour);
        controlDisplay.add(one);
        controlDisplay.add(two);
        controlDisplay.add(three);
        controlDisplay.add(four);

        
        visualizer.setLayout(new GridLayout(2,1));


        //Col 1 of Visualizer
        
        yokeGrid.setMinimumSize(new Dimension(100,100));
        //grid.setOpaque(true);
        // grid.setLayout(null);
        yokeGrid.setBackground(Color.black);
        yokeGrid.setVisible(true);
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

        JToolBar tool = new JToolBar("Toolbar");
        JButton b1 = new JButton("Data Log Disabled");
        ActionListener press = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(m.logPermission() == false){
                    m.startLog();
                    JOptionPane.showMessageDialog(frame,"Data Logging Enabled");
                    tool.setBackground(Color.green);
                    b1.setText("Data Log Enabled");
                } else {
                    m.stopLog();
                    JOptionPane.showMessageDialog(frame,"Data Logging Disabled");
                    tool.setBackground(Color.white);
                    b1.setText("Data Log Disabled");
                }
                
            }
            
        };
        b1.addActionListener(press);
        JButton b2 = new JButton("button 2");
        tool.setBackground(Color.red);
        tool.setOrientation(1);
        tool.add(b1);
        tool.add(b2);
        frame.add(tool,BorderLayout.WEST);



        JPanel buttonBar = new JPanel();
        buttonBar.setLayout(new GridLayout(2,1));
        JToolBar tool2 = new JToolBar("Toolbar");
        JButton b3 = new JButton("button 2");
        JButton b4 = new JButton("button 2");
        tool2.add(b3);
        tool2.add(b4);

        JToolBar tool3 = new JToolBar("Toolbar");
        JButton b5 = new JButton("button 3");
        JButton b6 = new JButton("button 3");
        tool3.add(b5);
        tool3.add(b6);
        buttonBar.add(tool2);
        buttonBar.add(tool3);

        modelQueue.setRows(3);
        modelQueue.setPreferredSize(new Dimension(250, mainDisplay.getWidth()));


        modelVision.setRows(1);
        Font font = new Font("Verdana", Font.BOLD, 12);
        modelVision.setFont(font);
        modelVision.setBackground(Color.PINK);
        modelVision.setPreferredSize(new Dimension(250, mainDisplay.getWidth()));

        modelDisplay.setLayout(new GridLayout(2,1));
        modelDisplay.add(modelQueue);
        modelDisplay.add(modelVision);


        mainDisplay.setLayout(new BorderLayout());
        mainDisplay.add(controlDisplay,BorderLayout.CENTER);
        mainDisplay.add(modelDisplay,BorderLayout.SOUTH);

        
        frameBottom.add(mainDisplay); // Left Side
        frameBottom.add(visualizer); // Right Side
        frame.add(frameBottom,BorderLayout.CENTER);
        frame.add(buttonBar, BorderLayout.SOUTH);
        frame.pack();
    }


    public void updateVisualizer(Model m) throws IOException {
        float[] ctrl1 = m.getCurrentControls();

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


            modelQueue.setText(m.getQueue().queueToString());
            modelQueue.setPreferredSize(new Dimension(mainDisplay.getWidth(),100));

            modelVision.setPreferredSize(new Dimension(mainDisplay.getWidth(),100));

            modelVision.setText("We just looked at: " + m.getStoredVisionTarget() +"\n" +
                    "Result: " + m.getStoredVisionResult()[0]);


            // yaw.setYBound(grid.getHeight());
            // yaw.setYBound(grid.getHeight());
            // yaw.setX(ctrl1[2]);
            // yaw.repaint();

            axis.setXBound(yokeGrid.getWidth());
            axis.setYBound(yokeGrid.getHeight());
            axis.repaint();
    }
    
}





