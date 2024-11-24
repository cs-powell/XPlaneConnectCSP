import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketException;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.OverlayLayout;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

public class Main {
    public static void main(String[] args) {


        //Encapsulation (or lack thereof) Test
        Model m = new Model();
        MindQueue q = m.getQueue();
        Action a = new Vision();
        System.out.println(q.pop());
        q.push(a);
        MindQueue q2 = m.getQueue();
        System.out.println(q2.pop());


        try(XPlaneConnect xpc = new XPlaneConnect())
        {
        Action b = new Vision();
            // Ensure connection established.
        m = new Model(xpc);
        m.activate();
        m.push(b);
        while(m.isActive() && !m.isEmpty()) {
            float[] array = m.next();
            if(array != null){
                System.out.println(array[0]);
            } else {
                System.out.println("no dice");
                // System.out.println(xpc.getDREF(null)");
            }
            
        }
        
        }
        catch (SocketException ex)
        {
            System.out.println("Unable to set up the connection. (Error message was '" + ex.getMessage() + "'.)");
        }
        catch (IOException ex)
        {
            System.out.println("Something went wrong with one of the commands. (Error message was '" + ex.getMessage() + "'.)");
            
        }
        System.out.println("Exiting");
    }

}
