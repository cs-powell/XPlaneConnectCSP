import java.awt.*;
import java.io.IOException;
import java.net.SocketException;

import CognitiveModel.ModelFiles.*;

import Visualizer.Screen;
import Visualizer.ScreenFrame;
import Visualizer.ScreenManager;
import Visualizer.visualizer;

import javax.swing.*;

public abstract class testprocess {

    Model m = new Model();

    public testprocess() {
        m = new Model();
    }


    public void runProcess() {
        try (XPlaneConnect xpc = new XPlaneConnect()) {
            // Ensure connection established.
            m.establishConnection(xpc);
            m.activateModel();
            m.createAction(ActionType.VISION, null, 0, "sim/cockpit2/gauges/indicators/airspeed_kts_pilot");
            //m.createAction(ActionType.MOTOR, MotorType.PITCHUP, 0, null);
            m.printModelQueue();
            visualizer vis1 = new visualizer(m);
            Timer timer1 = vis1.exportTimer();
            JLabel t1 = new JLabel("t1");
            JLabel t2 = new JLabel("t2");
            Screen s1 = new Screen(new GridLayout(1,2));
            s1.add(t1);
            s1.add(t2);
            ScreenManager sm  = new ScreenManager(s1,vis1);
            Dimension d = new Dimension(500,500);
            ScreenFrame f = new ScreenFrame(sm,d);
            f.initialize();
            timer1.start();
            while (m.isActive() && !m.isEmpty()) {
                innerProcess();
            }
        } catch (SocketException ex) {
            System.out.println("Unable to set up the connection. (Error message was '" + ex.getMessage() + "'.)");
        } catch (IOException ex) {
            System.out.println("Something went wrong with one of the commands. (Error message was '" + ex.getMessage() + "'.)");
        }
        System.out.println("Exiting");
        
    }

    public void innerProcess(){
        System.out.println("Using default innerProcess");
    
    }

}
