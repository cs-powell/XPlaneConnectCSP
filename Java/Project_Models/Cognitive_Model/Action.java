public abstract class Action {



Integer priority; //Priority of the Task
Integer delay; //Potential Delay before starting the task
Integer taskTime; //How long should the task take
boolean taskComplete; //Is the task completed?
int actionType;

public Action(int actionType, int delay){
 this.actionType = actionType;
 this.delay = delay;
}

public int getType(){
    return actionType;
}


public Integer getDelay(){
    return delay;
}

    
}
