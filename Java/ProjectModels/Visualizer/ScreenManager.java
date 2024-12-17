package Visualizer;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ScreenManager extends JPanel {


    JPanel display = new JPanel(new BorderLayout());
    Screen[] screens = new Screen[2];

    ScreenType currentScreen;

    JButton toggle = new JButton("Cycle Screens");



    public ScreenManager(Screen start, Screen data) {
        super(new BorderLayout());
        this.setVisible(true);
        screens[0] = start;
        screens[1] = data;

        toggle.setVisible(true);
        toggle.addActionListener(e -> {
            if (currentScreen == ScreenType.START) {
                switchScreens(ScreenType.DATA);
            } else {
                switchScreens(ScreenType.DATA2);
            }

//            if (currentScreen == ScreenType.DATA2) {
//                switchScreens(ScreenType.DATA);
//            }

        });
        this.add(toggle, BorderLayout.WEST);
        currentScreen = ScreenType.START;
        display.add(screens[0]);
        this.add(display, BorderLayout.CENTER);
        display.setBackground(Color.black);
    }

    public void switchScreens(ScreenType st) {
        display.removeAll();
        switch(st) {
            case START:
                System.out.println("Switch to start");
                display.add(screens[0],BorderLayout.CENTER);
                currentScreen = ScreenType.START;
                display.repaint();
                revalidate();
                break;
            case DATA:
                System.out.println("Switch to data");
                display.add(screens[1],BorderLayout.CENTER);
                currentScreen = ScreenType.DATA;
                display.repaint();
                revalidate();
                break;

            case DATA2:
                System.out.println("Switch to data 2");
                display.add(((visualizer)screens[1]).exportDisplay(),BorderLayout.CENTER);
                currentScreen = ScreenType.DATA2;
                display.repaint();
                revalidate();
                break;

        }

    }




}
