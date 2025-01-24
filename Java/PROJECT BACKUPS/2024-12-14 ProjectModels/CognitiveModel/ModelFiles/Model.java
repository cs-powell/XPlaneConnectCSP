package CognitiveModel.ModelFiles;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

// import ModelFiles.ActionType;
// import ModelFiles.MotorType;

public class Model {

    private MindQueue q; // Queue for actions
    boolean modelActive; // On/Off Switch for the model execution
    XPlaneConnect xpc; // Allows connection to the simulator
    String storedVisionTarget;
    float[] storedVisionResult;
    boolean logCreated;
    boolean logAllowed;
    File dataLogFile = null;

    // Constructors
    public Model() {
        q = new MindQueue();
        modelActive = false;
        logCreated = false;
    }

    public Model(XPlaneConnect xpc) {
        q = new MindQueue();
        modelActive = false;
        this.xpc = xpc;
        logCreated = false;
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
        System.out.println(q.queueToString());
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
            storedVisionTarget = dref;
            storedVisionResult = returnArray.clone();
            // System.out.println(returnArray[0]);
        } catch (IOException e) {

        }
    }

    private void handleMotorAction(Action temp) {
        /*
        TODO: GoogleDoc Notes 12/11/24

         */

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
                    if (storedVisionResult[0] > 80) {
                        if (currentControls[0] < 0.2f) {
                            System.out.println("Pitching Up");
                            xpc.sendCTRL(pitchUp);
                        }
                    }
                    break;
                case PITCHDOWN:
                    float[] pitchDown = { currentControls[0] - 0.01f };
                    if (storedVisionResult[0] < 80) {
                        if (currentControls[0] > -0.2f) {
                            // System.out.println("Sending Pitch Down");
                            System.out.println("Pitching Down");
                            xpc.sendCTRL(pitchDown);
                        }
                    }
                    break;
                case THROTTLEUP:
                    break;

                case THROTTLEDOWN:
                    
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
        storedVisionTarget = "INTERRUPTION: DELAY";
    }

    public static void initiateDelay(int delay) {
        try {
            Thread.sleep(delay); // Using the action's Built in Delay
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public float[] getCurrentControls() throws IOException{
        float [] returnArray = null;
        try {
            returnArray = xpc.getCTRL(0);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            while(returnArray == null) {
                System.out.println("Waiting for reconnect");
                returnArray = xpc.getCTRL(0);
            }
        }
        return returnArray;
    }

    public void startLog() {
        logAllowed = true;
    }

    public void stopLog() {
        logAllowed = false;
        logCreated = false;
    }

    public boolean logPermission() {
        return logAllowed;
    }

    public void logData() {
        if(logAllowed) {
            logProcess();
        }
    }


    private void logProcess(){
        if(!logCreated) {
            dataLogFile = new File("./dataLog/" + java.time.LocalDateTime.now() + ".csv");
            logCreated = true;
            try {
                float[] ctrl1 = this.getCurrentControls();
                String setup = "Elevator, Roll, Yaw, Throttle, Flaps\n";
                FileWriter fw = new FileWriter(dataLogFile, true);
                BufferedWriter br = new BufferedWriter(fw);
                br.write(setup);
                br.flush();
            } catch (IOException e) {

            }
        }
            String log = null;
            try {
                float[] ctrl1 = this.getCurrentControls();


                //Labled Format
                log = String.format("[Elevator: %2f] [Roll: %2f] [Yaw: %2f] [Throttle:%2f] [Flaps: %2f] \n",
                ctrl1[0], ctrl1[1], ctrl1[2], ctrl1[3], ctrl1[5]);
                //CSV Format
                log = String.format("%2f, %2f, %2f, %2f, %2f\n",
                        ctrl1[0], ctrl1[1], ctrl1[2], ctrl1[3], ctrl1[5]);

                FileWriter fw = new FileWriter(dataLogFile, true);
                BufferedWriter br = new BufferedWriter(fw);
                br.write(log);
                br.flush();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.out.println("No data to log");
            }
    }


    public float[] getStoredVisionResult() {
        return storedVisionResult;
    }

    public String getStoredVisionTarget() {
        return storedVisionTarget;
    }
}
