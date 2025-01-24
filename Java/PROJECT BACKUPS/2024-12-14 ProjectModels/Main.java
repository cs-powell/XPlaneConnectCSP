
import javax.swing.*;

import CognitiveModel.ModelFiles.*;
import Visualizer.Screen;
import Visualizer.ScreenFrame;
import Visualizer.ScreenManager;

import java.awt.*;

public class Main {
    public static void main(String[] args) {
       
        // Encapsulation (or lack thereof) Test
//        Model m = new Model();
//        testprocess1 tp1 = new testprocess1(m, null);
//        tp1.runProcess();


        //Toolkit.getDefaultToolkit().setDynamicLayout(true);

        JLabel t1 = new JLabel("t1");
        JLabel t2 = new JLabel("t2");
        JLabel t3 = new JLabel("t3");
        JLabel t4 = new JLabel("t4");

        Screen s1 = new Screen(new GridLayout(1,2));
        s1.add(t1);
        s1.add(t2);
        Screen s2 = new Screen(new GridLayout(1,2));
        s2.add(t3);
        s2.add(t4);
        ScreenManager m  = new ScreenManager(s1,s2);
        Dimension d = new Dimension(500,500);
        ScreenFrame f = new ScreenFrame(m,d);
        f.initialize();
        while (true) {
            f.repaint();
        }




//        testprocess2 tp2 = new testprocess2(m, null);
//        tp2.runProcess();

//        for (int i = 1; i<10; i++) {
//            if (i%2 == 0) {
//                int t = 0;
//            } else if(i == 9) {
//                System.out.println("Breaking now");
//                break;
//            }
//        }

    }
}
