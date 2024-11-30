import java.io.IOException;
import java.net.SocketException;

import ModelFiles.*;
import Visualizer.visualizer;

public class testprocess1 extends testprocess {

    public testprocess1(Model inputM, XPlaneConnect xpc) {
        super(inputM, xpc); 
        
    }

    @Override
    public void runProcess() {
        try (XPlaneConnect xpc = new XPlaneConnect()) {
            // Ensure connection established.
            m = new Model(xpc);
            m.activateModel();
            m.createAction(ActionType.VISION, null, 0, "sim/cockpit2/gauges/indicators/airspeed_kts_pilot");
            m.createAction(ActionType.MOTOR, MotorType.PITCHUP, 0, null);
            m.printModelQueue();
            visualizer vis1 = new visualizer(m);
            vis1.initializeDisplay();
            while (m.isActive() && !m.isEmpty()) {
                float[] array = m.next();
                if (m.getModelQueueLength() < 5) {
                    m.createAction(ActionType.VISION, null, 0, "sim/cockpit2/gauges/indicators/airspeed_kts_pilot");
                    // m.createAction(ActionType.MOTOR, MotorType.PITCHUP, 0, null);
                    // m.createAction(ActionType.MOTOR, MotorType.PITCHDOWN, 0, null);
                }
                // m.printModelQueue();
                m.logData();
                vis1.updateVisualizer(m.getCurrentControls());
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