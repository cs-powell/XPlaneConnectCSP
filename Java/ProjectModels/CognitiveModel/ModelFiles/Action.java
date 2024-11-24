package ModelFiles;


public abstract class Action {



Integer priority; //Priority of the Task
Integer delay; //Potential Delay before starting the task
String targetDREF;
Integer taskTime; //How long should the task take
boolean taskComplete; //Is the task completed?
ActionType actionType;

public Action(ActionType actionType, int delay){
 this.actionType = actionType;
 this.delay = delay;
 this.targetDREF = null;
}


public Action(ActionType actionType, int delay, String target){
    this.actionType = actionType;
    this.delay = delay;
    targetDREF = target;
   }

public ActionType getType(){
    return actionType;
}


public Integer getDelay(){
    return delay;
}

public String getTarget(){
    return targetDREF;
}
    
}
