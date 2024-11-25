
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
        MindQueue q = m.getQueue();
        Action a = new Vision();
        System.out.println(q.pop());
        q.push(a);
        MindQueue q2 = m.getQueue();
        System.out.println(q2.pop());

        // Using actions
        try (XPlaneConnect xpc = new XPlaneConnect()) {
            Action b = new Vision(10, "sim/cockpit2/gauges/indicators/airspeed_kts_pilot");
            Action d = new Delay(20);
            // Ensure connection established.
            m = new Model(xpc);
            m.activateModel();
            m.createAction(ActionType.VISION, null, 0, "sim/cockpit2/gauges/indicators/airspeed_kts_pilot");
            m.createAction(ActionType.MOTOR, MotorType.PITCHUP, 0, null);
            // m.push(b);
            // m.push(d);
            m.printModelQueue();
            // m.deactivateModel();
            while (m.isActive() && !m.isEmpty()) {
                float[] array = m.next();
                if (array != null) {
                    // System.out.println(array[0]);
                    // if(array[0] > 80.0f) {
                    // m.push(d);
                    // }
                } else {
                    // System.out.println("no dice");
                    // System.out.println(xpc.getDREF(null)");
                }
                if (m.getModelQueueLength() < 5) {
                    m.createAction(ActionType.VISION, null, 0, "sim/cockpit2/gauges/indicators/airspeed_kts_pilot");
                    m.createAction(ActionType.MOTOR, MotorType.PITCHUP, 0, null);
                    m.createAction(ActionType.MOTOR, MotorType.PITCHDOWN, 0, null);
                }

                // m.printModelQueue();
            }
        } catch (SocketException ex) {
            System.out.println("Unable to set up the connection. (Error message was '" + ex.getMessage() + "'.)");
        } catch (IOException ex) {
            System.out.println(
                    "Something went wrong with one of the commands. (Error message was '" + ex.getMessage() + "'.)");
        }
        System.out.println("Exiting");
    }

}
