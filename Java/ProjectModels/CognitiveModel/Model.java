package ProjectModels.CognitiveModel;
import java.io.IOException;

public class Model {

    private MindQueue q; //Queue for actions
    boolean modelActive; // On/Off Switch for the model execution
    XPlaneConnect xpc; //Allows connection to the simulator


    //Constructors
    public Model() {
        q = new MindQueue();
        modelActive = false;
    }

    public Model(XPlaneConnect xpc) {
        q = new MindQueue();
        modelActive = false;
        this.xpc = xpc; 
    }


    /*Getters*/
    public MindQueue getQueue() {
        return q;
    }

    /*Setters*/

    /*
     * Setup Methods
     * Printing, Empty check, Activation/Deactivation of Model, etc.
    */
    public void activateModel() {
        modelActive = true;
    }

    public void establishConnection(XPlaneConnect newXPC){
        this.xpc = newXPC;
    }

    public void deactivateModel(){
        modelActive = false;
    }

    public boolean isEmpty(){
        return q.isEmpty();
    }

    public void push(Action a){
        q.push(a);
    }

    public boolean isActive(){
        return modelActive;
    }

    public void printModelQueue(){
        System.out.println(q.printQueue());
    }

    public int getModelQueueLength(){
        return q.queueLength();
    }
    
    public void createAction(int actionType,int delay,String target) {
        Action newAction = null;
        switch(actionType){
            case 1:
                newAction = new Vision(delay,target);
                this.push(newAction);
            case 2:
                newAction = new Motor(delay,target);
                this.push(newAction);

            case 3:
                newAction = new Delay(delay);
                this.push(newAction);
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

        if(temp.getType() == ActionType.VISION){ // Vision Action (Get Data)
           handelVisionAction(temp, returnArray);
        } else if (temp.getType() == ActionType.MOTOR){ //Motor Action (Act Upon Data)
            handleMotorAction(temp);
        } else if (temp.getType() == ActionType.DELAY){//Pure Delays (Do nothing)
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
         } catch (IOException e) {
            
         }
    }

    private void handleMotorAction(Action temp){
        Motor tempM = (Motor) temp;
        tempM.getDelay();
    }

    private void handleDelayAction(Action temp){
        Delay tempD = (Delay) temp;
        initiateDelay(tempD.getDelay());
    }

    public static void initiateDelay(int delay){
        try {
            Thread.sleep(delay); // Using the action's Built in Delay 
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }   
    
}
