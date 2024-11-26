import ModelFiles.Model;
import ModelFiles.XPlaneConnect;

public abstract class testprocess {


    Model m = new Model();

    public testprocess(Model inputM, XPlaneConnect xpc) {
        m = inputM;
        m.establishConnection(xpc);
    }


    public void runProcess() {
        
    }

}
