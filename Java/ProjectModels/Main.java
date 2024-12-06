
import javax.swing.UIManager;

//import CognitiveModel.ModelFiles.*;

public class Main {
    public static void main(String[] args) {
       
        // Encapsulation (or lack thereof) Test
//        Model m = new Model();
//        testprocess1 tp1 = new testprocess1(m, null);
        // tp1.runProcess();
//        testprocess2 tp2 = new testprocess2(m, null);
//        tp2.runProcess();

        for (int i = 1; i<10; i++) {
            if (i%2 == 0) {
                int t = 0;
            } else if(i == 9) {
                System.out.println("Breaking now");
                break;
            }
        }

    }
}
