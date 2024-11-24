import java.io.IOException;

// import gov.nasa.xpc.XPlaneConnect;

public class Model {

    private MindQueue q;
    boolean modelActive;
    XPlaneConnect xpc;

    public Model() {
        q = new MindQueue();
        modelActive = false;
    }

    public Model(XPlaneConnect xpc) {
        q = new MindQueue();
        modelActive = false;
        this.xpc = xpc; 
    }



    public MindQueue getQueue() {
        return q;
    }

    public float[] next(){
        Action temp = q.pop();
        System.out.println("Type of Action: " + temp.getType());
        float[] returnArray = null;
        if(temp.getType() == 1){
            System.out.println("in next if statement");
            Vision tempV = (Vision) temp; 

            //execute delay 
            try {
                Thread.sleep(tempV.getDelay()); // Using the action's Built in Delay 
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            String dref = "sim/cockpit2/gauges/indicators/airspeed_kts_pilot";
            try {
                 returnArray = xpc.getDREF(dref);

            } catch (IOException e) {
                // TODO: handle exception
            }
            
        }
        q.push(temp);
        return returnArray;
        
    }

    public boolean isEmpty(){
       return q.isEmpty();
    }

    public void push(Action a){
        q.push(a);
    }

    public void activate() {
        modelActive = true;
    }

    public void establishConnection(XPlaneConnect newXPC){
        this.xpc = newXPC;
    }

    public void deactivate(){
        modelActive = false;
    }

    public boolean isActive(){
        return modelActive;
    }
    
}
