
import javax.swing.UIManager;

import com.formdev.flatlaf.FlatLightLaf;

import ModelFiles.Model;

public class Main {
    public static void main(String[] args) {
        // FlatLightLaf.setup();
       
        // Encapsulation (or lack thereof) Test
        Model m = new Model();
        testprocess1 tp1 = new testprocess1(m, null);
        // tp1.runProcess();
        testprocess2 tp2 = new testprocess2(m, null);
        tp2.runProcess();
    }
}
