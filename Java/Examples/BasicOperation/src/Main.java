package gov.nasa.xpc.ex;

import gov.nasa.xpc.XPlaneConnect;
import java.io.IOException;
import java.net.SocketException;
import java.util.Arrays;

/**
 * An example program demonstrating the basic features of the X-Plane Connect toolbox.
 *
 * @author Jason Watkins
 * @version 1.0
 * @since 2015-04-06
 */
public class Main
{
    public static void main(String[] args)
    {
        System.out.println("X-Plane Connect example program");
        System.out.println("Setting up simulation...");
        try(XPlaneConnect xpc = new XPlaneConnect())
        {
            // Ensure connection established.
        xpc.getDREF("sim/test/test_float");

            // System.out.println("Setting player aircraft position");
            // double[] posi = new double[] {37.524, -122.06899, 2500, 0, 0, 0, 1};
            // xpc.sendPOSI(posi);

            // System.out.println("Setting another aircraft position");
            // posi[0] = 37.52465;
            // posi[4] = 20;
            // xpc.sendPOSI(posi, 1);

            System.out.println("Setting rates");
            float[][] data = new float[3][9];
            for(float[] row : data)
            {
                Arrays.fill(row, -998);
            }
            data[0][0] = 0; //Alpha
            data[0][1] = 0;
            data[0][3] = 0;

            data[1][0] = 0; //Velocity
            data[1][1] = 0;
            data[1][2] = 0;
            data[1][3] = 0;
            data[1][4] = 0;

            data[2][0] = 0; //PQR
            data[2][1] = 0;
            data[2][2] = 0;
            data[2][3] = 0;

            xpc.sendDATA(data);



        System.out.println("Trying something new!!");
        int aircraft = 0;



        // JFrame container = new JFrame();
        // Dimension size = new Dimension(500, 500);
        // container.setPreferredSize(size);
        // container.setVisible(true);


        // JLabel text = new JLabel("Hello");
        // JLabel text2 = new JLabel("Hello");
        // JPanel display = new JPanel();
        // display.setLayout(new GridLayout());
        // display.setPreferredSize(size);
        // display.add(text);
        // display.add(text2);

        // container.add(display);
        // container.pack();

        while(true) {

            //THE GETTERS
            double[] posi1 = xpc.getPOSI(aircraft); // FIXME: change this to 64-bit double
            float[] ctrl1 = xpc.getCTRL(aircraft);

            // String dref = "sim/cockpit2/gauges/indicators/airspeed_kts_copilot";
            String dref = "sim/cockpit2/gauges/indicators/airspeed_kts_pilot";
            String dref2 = "sim/flightmodel/position/true_phi";
            String drefHDG = "sim/cockpit2/gauges/indicators/compass_heading_deg_mag";


            boolean takeoff = true;
            boolean climb = false;
            boolean cruise = false;
           


            float[] value = xpc.getDREF(dref);
            float[] value2 = xpc.getDREF(dref2);
            float[] valueHDG = xpc.getDREF(drefHDG);
            float bugged = 50;
            float rwyHDG = 120;
            // float[] value2 = xpc.getDREF(dref2);
            // System.out.println(String.valueOf(value[0]));

            // System.out.format("Loc: (%4f, %4f, %4f) Aileron:%2f Elevator:%2f Rudder:%2f\n",
            //         posi1[0], posi1[1], posi1[2], ctrl1[1], ctrl1[0], ctrl1[2]);


            // System.out.f ormat("\rControl Surfaces: [Elevator: %2f] [Roll: %2f] [Yaw: %2f] ||| Power: [Throttle:%2f] ||| Secondary Control Surfaces: [Flaps: %2f]",
            //          ctrl1[0], ctrl1[1], ctrl1[2], ctrl1[3], ctrl1[5]);

            System.out.format("\r[Elevator: %2f] [Roll: %2f] [Yaw: %2f] ------ [Throttle:%2f] ------ [Flaps: %2f] ---- [Data Ref: %2f]",
                     ctrl1[0], ctrl1[1], ctrl1[2], ctrl1[3], ctrl1[5], value[0]);

            // //Basic Autopilot For Roll (based on yoke position)
            // float[] rollRight = {-998.0f, ctrl1[1]-(ctrl1[1]/2)};
            // float[] rollLeft = {-998.0f, ctrl1[1]-(ctrl1[1]/2)};
            // if(ctrl1[1] < 0) {
            //     xpc.sendCTRL(rollRight);
            // } else if(ctrl1[1] > 0) {
            //     xpc.sendCTRL(rollLeft);
            // }

            //Basic Autopilot For Pitch (based on VSI)
            // float[] pitchUp = {ctrl1[0] + 0.01f};
            // float[] pitchDown = {ctrl1[0]- 0.01f};
            // if(value[0] < 0) {
            //     xpc.sendCTRL(pitchUp);
            // } else if(value[0] > 0) {
            //     xpc.sendCTRL(pitchDown);
            // }

            if(takeoff) {
                float[] pitchUp = {ctrl1[0] + 0.01f};
                if(value[0] > bugged) {
                        if(ctrl1[0] < 0.2f){
                            xpc.sendCTRL(pitchUp);
                        }
                }

                //Takeoff Roll Control 
                float[] rollRight = {-998.0f, ctrl1[1] + 0.01f};
                float[] rollLeft = {-998.0f, ctrl1[1] - 0.01f};
                if(value2[0] < 0 && value[0] > bugged) {
                    if(ctrl1[1] < 0.15f) {
                        xpc.sendCTRL(rollRight);
                    }

                } else if(value2[0] > 0 && value[0] > bugged) {
                    if(ctrl1[1] > -0.15f){
                        xpc.sendCTRL(rollLeft);
                    }
                }

                //Takeoff Rudder Control
                float[] yawRight = {-998.0f, -998.0f, ctrl1[2] + 0.03f};
                float[] yawLeft = {-998.0f, -998.0f, ctrl1[2] - 0.03f};
                if(valueHDG[0] < rwyHDG && value[0] > 1) {

                    if(ctrl1[2] < 0.5f) {
                        xpc.sendCTRL(yawRight); // YAW RIGHT
                    }

                } else if(valueHDG[0] > rwyHDG && value[0] > 1) {
                    if(ctrl1[2] > -0.5f){
                        xpc.sendCTRL(yawLeft); // YAW LEFT 
                    }

                }
            }


            if(cruise) {
                //Basic Autopilot For Roll (based on bank angle)
             float[] rollRight = {-998.0f, ctrl1[1] + 0.01f};
             float[] rollLeft = {-998.0f, ctrl1[1] - 0.01f};
             if(value2[0] < 0) {

                if(ctrl1[1] < 0.15f) {
                    xpc.sendCTRL(rollRight);
                }

             } else if(value2[0] > 0) {
                if(ctrl1[1] > -0.15f){
                    xpc.sendCTRL(rollLeft);
                }
             }

             //Basic Autopilot For Pitch (based on speed)
             float[] pitchUp = {ctrl1[0] + 0.01f};
             float[] pitchDown = {ctrl1[0]- 0.01f};

             if(value[0] > bugged) {
                if(ctrl1[0] < 0.2f) {
                    xpc.sendCTRL(pitchUp);
                }
             } else if(value[0] < bugged) {
                if(ctrl1[0] > -0.2f) {
                    xpc.sendCTRL(pitchDown);
                }
             }

            }
             

            
            // text.setText(String.valueOf(value[0]));
            // display.add(elevator);
            // System.out.println("");
            try {
                Thread.sleep(1);
            }
            catch (InterruptedException ex) {}
        
            if (System.in.available() > 0) {
                break;
            }
        }




            // System.out.println("Setting controls");
            // float[] ctrl = new float[4];
            // ctrl[3] = 0.8F;
            // xpc.sendCTRL(ctrl);

            // System.out.println("Pausing sim");
            // xpc.pauseSim(true);
            // try { Thread.sleep(5000); } catch (InterruptedException ex) {}
            // System.out.println("Un-pausing");
            // xpc.pauseSim(false);
            

            //Let sim run for 10 seconds
            try { Thread.sleep(10000); } catch (InterruptedException ex) {}

            System.out.println("Stowing landing gear");
            xpc.sendDREF("sim/cockpit/switches/gear_handle_status", 1);

            //Let sim run for 10 seconds
            try { Thread.sleep(10000); } catch (InterruptedException ex) {}

            System.out.println("Checking gear and sim status");
            String[] drefs = new String[]
            {
                "sim/cockpit/switches/gear_handle_status",
                "sim/operation/override/override_planepath"
            };
            float[][] results = xpc.getDREFs(drefs);

            if(results[0][0] == 1)
            {
                System.out.println("Gear stowed.");
            }
            else
            {
                System.out.println("Error stowing gear");
            }
            System.out.println("Example complete");
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
