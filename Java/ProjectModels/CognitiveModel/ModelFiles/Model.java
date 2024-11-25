package ModelFiles;

import java.io.IOException;

// import ModelFiles.ActionType;
// import ModelFiles.MotorType;

public class Model {

    private MindQueue q; // Queue for actions
    boolean modelActive; // On/Off Switch for the model execution
    XPlaneConnect xpc; // Allows connection to the simulator
    float[] storedVision;

    // Constructors
    public Model() {
        q = new MindQueue();
        modelActive = false;
    }

    public Model(XPlaneConnect xpc) {
        q = new MindQueue();
        modelActive = false;
        this.xpc = xpc;
    }

    /* Getters */
    public MindQueue getQueue() {
        return q;
    }

    /* Setters */

    /*
     * Setup Methods
     * Printing, Empty check, Activation/Deactivation of Model, etc.
     */
    public void activateModel() {
        modelActive = true;
    }

    public void establishConnection(XPlaneConnect newXPC) {
        this.xpc = newXPC;
    }

    public void deactivateModel() {
        modelActive = false;
    }

    public boolean isEmpty() {
        return q.isEmpty();
    }

    public void push(Action a) {
        q.push(a);
    }

    public boolean isActive() {
        return modelActive;
    }

    public void printModelQueue() {
        System.out.println(q.printQueue());
    }

    public int getModelQueueLength() {
        return q.queueLength();
    }

    public void createAction(ActionType actionType, MotorType motorType, int delay, String target) {
        Action newAction = null;
        switch (actionType) {
            case VISION:
                newAction = new Vision(delay, target);
                this.push(newAction);
                break;
            case MOTOR:
                newAction = new Motor(motorType, delay, target);
                this.push(newAction);
                break;

            case DELAY:
                newAction = new Delay(delay);
                this.push(newAction);
                break;
        }
    }

    /*
     * Processing Methods
     * 
     */

    /*
     * Process the next event in the model queue
     */
    public float[] next() {
        float[] returnArray = null;
        Action temp = q.pop();
        // System.out.println("Type of Action: " + temp.getType());

        if (temp.getType() == ActionType.VISION) { // Vision Action (Get Data)
            handelVisionAction(temp, returnArray);
        } else if (temp.getType() == ActionType.MOTOR) { // Motor Action (Act Upon Data)
            handleMotorAction(temp);
        } else if (temp.getType() == ActionType.DELAY) {// Pure Delays (Do nothing)
            handleDelayAction(temp);
        }
        // q.push(temp);
        return returnArray;

    }

    /* Next Helpers */
    private void handelVisionAction(Action temp, float[] returnArray) {
        Vision tempV = (Vision) temp;
        initiateDelay(tempV.getDelay());
        String dref = tempV.getTarget();
        try {
            returnArray = xpc.getDREF(dref);
            storedVision = returnArray.clone();
            // System.out.println(returnArray[0]);
        } catch (IOException e) {

        }
    }

    private void handleMotorAction(Action temp) {
        Motor tempM = (Motor) temp;
        MotorType motorType = tempM.getMotorType();
        float[] currentControls = null;
        try {
            currentControls = xpc.getCTRL(0);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {

            switch (motorType) {
                case PITCHUP:
                    float[] pitchUp = { currentControls[0] + 0.01f };
                    if (storedVision[0] > 80) {
                        if (currentControls[0] < 0.2f) {
                            System.out.println("Pitching Up");
                            xpc.sendCTRL(pitchUp);
                        }
                    }

                    break;
                case PITCHDOWN:
                    float[] pitchDown = { currentControls[0] - 0.01f };
                    if (storedVision[0] < 80) {
                        if (currentControls[0] > -0.2f) {
                            // System.out.println("Sending Pitch Down");
                            System.out.println("Pitching Down");
                            xpc.sendCTRL(pitchDown);
                        }
                    }

                    break;
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void handleDelayAction(Action temp) {
        Delay tempD = (Delay) temp;
        initiateDelay(tempD.getDelay());
    }

    public static void initiateDelay(int delay) {
        try {
            Thread.sleep(delay); // Using the action's Built in Delay
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
