
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketException;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.OverlayLayout;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import ModelFiles.*;
import ModelFiles.Action;
import ModelFiles.ActionType;
import ModelFiles.Delay;
import ModelFiles.MindQueue;
import ModelFiles.Model;
import ModelFiles.Vision;
import ModelFiles.XPlaneConnect;

public class Main {
    public static void main(String[] args) {
        // Encapsulation (or lack thereof) Test
        Model m = new Model();
        testprocess1 tp1 = new testprocess1(m, null);
        tp1.runProcess();
    }
}
