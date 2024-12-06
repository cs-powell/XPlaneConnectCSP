import java.io.IOException;
import java.net.SocketException;

import ModelFiles.*;
import Visualizer.visualizer;

public abstract class testprocess {


    Model m = new Model();

    public testprocess(Model inputM, XPlaneConnect xpc) {
        m = inputM;
        m.establishConnection(xpc);
    }


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
                innerProcess(vis1);
            }

            
            
        } catch (SocketException ex) {
            System.out.println("Unable to set up the connection. (Error message was '" + ex.getMessage() + "'.)");
        } catch (IOException ex) {
            System.out.println(
                    "Something went wrong with one of the commands. (Error message was '" + ex.getMessage() + "'.)");
        }
        System.out.println("Exiting");
        
    }

    public void innerProcess(visualizer vis1){
        System.out.println("Using default innerProcess");
    
    }

}
