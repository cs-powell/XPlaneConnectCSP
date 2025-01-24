package Visualizer;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class ScreenManager extends JPanel {


    JPanel display = new JPanel(new BorderLayout());
    Screen[] screens = new Screen[2];

    ScreenType currentScreen;

    JButton toggle = new JButton();


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
                switchScreens(ScreenType.START);
            }

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
        }

    }




}
