package ProjectModels.CognitiveModel;
import java.util.Map;
import java.util.TreeMap;


public class Delay extends Action {

    public Delay(){
        super(ActionType.DELAY,1000);
    }

    public Delay(int delay){
        super(ActionType.DELAY,delay);
    }

}