package ModelFiles;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Tests {

    MindQueue m = new MindQueue();

    @Test
    public void test1() {
        Integer i = 1;
        Action a = new Vision();
        m.push(a);
        assertEquals(i, m.pop());
    }
    
}
