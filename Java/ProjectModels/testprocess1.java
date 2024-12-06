import java.io.IOException;
import java.net.SocketException;

import CognitiveModel.ModelFiles.*;
import Visualizer.visualizer;

public class testprocess1 extends testprocess {

    public testprocess1(Model inputM, XPlaneConnect xpc) {
        super(inputM, xpc); 
        
    }

    @Override
    public void innerProcess(visualizer vis1){
            float[] array = m.next();
            if (m.getModelQueueLength() < 5) {
                m.createAction(ActionType.VISION, null, 0, "sim/cockpit2/gauges/indicators/airspeed_kts_pilot");
            }
            // m.printModelQueue();
            m.logData();
            try {
                vis1.updateVisualizer(m.getCurrentControls());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
    }
}