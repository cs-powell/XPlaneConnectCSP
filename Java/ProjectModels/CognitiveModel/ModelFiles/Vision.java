package ModelFiles;

public class Vision extends Action {

    public Vision() {
        super(ActionType.VISION, 1000);
    }

    public Vision(int delay) {
        super(ActionType.VISION, delay);
    }

    public Vision(String dref) {
        super(ActionType.VISION, 1000, dref);
    }

    public Vision(int delay, String dref) {
        super(ActionType.VISION, delay, dref);
    }

}