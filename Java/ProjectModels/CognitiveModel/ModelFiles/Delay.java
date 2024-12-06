package CognitiveModel.ModelFiles;
// package ProjectModels.CognitiveModel;

public class Delay extends Action {

    public Delay() {
        super(ActionType.DELAY, 1000);
    }

    public Delay(int delay) {
        super(ActionType.DELAY, delay);
    }

}