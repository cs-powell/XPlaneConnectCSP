package ModelFiles;
import java.io.IOException;


public class Motor extends Action {

    float[] control;
    MotorType motorType;

    public Motor() {
        super(ActionType.MOTOR,1000);
    }

public Motor(int delay) {
    super(ActionType.MOTOR,delay);
}


public Motor(int delay, String target) {
    super(ActionType.MOTOR,delay,target);
}


public void createMotorControl(){ // TODO: Maybe takes in a formatted set of vision inputs?
    control = null;
}


public MotorType getMotorType(){
    return motorType;
}

public void sendMotorControl(XPlaneConnect xpc) {

    if(control != null) {
        try {
            xpc.sendCTRL(control);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("Control failed to send");
        }
    }
}


}