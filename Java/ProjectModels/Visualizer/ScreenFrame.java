package Visualizer;

import javax.script.ScriptEngine;
import javax.swing.*;
import java.awt.*;

public class ScreenFrame extends JFrame {
    ScreenManager sm;
    public ScreenFrame(ScreenManager sm, Dimension d) {
        this.sm = sm;
        this.setPreferredSize(d);
    }

    public void initialize() {
        this.setVisible(true);
        this.setFocusable(true);
        this.add(sm);
        this.pack();
    }
}
