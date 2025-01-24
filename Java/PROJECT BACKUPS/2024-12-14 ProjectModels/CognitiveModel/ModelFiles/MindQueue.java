package CognitiveModel.ModelFiles;
// package ProjectModels.CognitiveModel;

import java.util.LinkedList;

public class MindQueue {
    /*
    Recommended Changes/Improvements
    See Google Doc Notes 12/11/24
     */

    LinkedList<Action> q;

    public MindQueue() {
        q = new LinkedList<Action>();
    }

    public void push(Action e) {
        q.add(e);
    }

    public Action removeEvent(Action e) {
        Action temp = e;
        q.remove(e);
        return temp;
    }

    public Action pop() {
        Vision v = new Vision();
        if (q.isEmpty()) {
            return v;
        }
        return q.remove();
    }

    public boolean isEmpty() {
        return q.isEmpty();
    }

    public String queueToString() {
        String queueTrace = "Next to Execute ==> ";

        for (Action action : q) {
            if (action.getType() == ActionType.VISION) {
                queueTrace += "[V]";
            }
            if (action.getType() == ActionType.MOTOR) {
                queueTrace += "[M]";
            }
            if (action.getType() == ActionType.DELAY) {
                queueTrace += "[D]";
            }
        }
        queueTrace  += "\n";
        return queueTrace;
    }

    public int queueLength() {
        return q.size();
    }

}
