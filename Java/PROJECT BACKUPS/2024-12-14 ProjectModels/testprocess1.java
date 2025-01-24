import java.io.IOException;
import java.net.SocketException;

import CognitiveModel.ModelFiles.*;
import Visualizer.visualizer;

public class testprocess1 extends testprocess {

    public testprocess1(Model inputM, XPlaneConnect xpc) {
        super(inputM, xpc); 
        
    }

    @Override
    public void innerProcess(visualizer vis1) {
            float[] array = m.next();
            double r = Math.random();
            if (m.getModelQueueLength() < 5) {
               if (r > 0.5) {
//                   m.createAction(ActionType.DELAY, null, 200, null);
//                   m.createAction(ActionType.DELAY, null, 200, null);
                   m.createAction(ActionType.VISION, null, 0, "sim/cockpit2/gauges/indicators/airspeed_kts_pilot");
               } else {
                   m.createAction(ActionType.VISION, null, 0, "sim/cockpit2/gauges/indicators/airspeed_kts_pilot");
               }
            }
            // m.printModelQueue();
            m.logData();
            try {
                vis1.updateVisualizer(m);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
    }
}