
import ModelFiles.Model;

public class Main {
    public static void main(String[] args) {
        // Encapsulation (or lack thereof) Test
        Model m = new Model();
        testprocess1 tp1 = new testprocess1(m, null);
        tp1.runProcess();
    }
}
