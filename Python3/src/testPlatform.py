from time import sleep
import xpc
from cognitiveModel import AircraftLandingModel

def ex():
    print("X-Plane Connect example script")
    print("Setting up simulation")
    with xpc.XPlaneConnect() as client:
        # Verify connection
        try:
            # If X-Plane does not respond to the request, a timeout error
            # will be raised.
            client.getDREF("sim/test/test_float")
        except:
            print("Error establishing connection to X-Plane.")
            print("Exiting...")
            return

        cogModel = AircraftLandingModel(client)
    
        # # Set position of the player aircraft
        # print("Setting position")
        # #       Lat     Lon         Alt   Pitch Roll Yaw Gear
        # posi = [37.524, -122.06899, 2500, 0,    0,   0,  1]
        # client.sendPOSI(posi)
        
        # # Set position of a non-player aircraft
        # print("Setting NPC position")
        # #       Lat       Lon         Alt   Pitch Roll Yaw Gear
        # posi = [37.52465, -122.06899, 2500, 0,    20,   0,  1]
        # client.sendPOSI(posi, 1)

        # # Set angle of attack, velocity, and orientation using the DATA command
        # print("Setting orientation")
        # data = [\
        #     [18,   0, -998,   0, -998, -998, -998, -998, -998],\
        #     [ 3, 130,  130, 130,  130, -998, -998, -998, -998],\
        #     [16,   0,    0,   0, -998, -998, -998, -998, -998]\
        #     ]
        # client.sendDATA(data)




        # Set control surfaces and throttle of the player aircraft using sendCTRL
        print("Setting controls")
        ctrl = [0.0, 0.0, 0.0, 0.0]
        client.sendCTRL(ctrl)
        # Pitch, Roll, Rudder, Throttle

        # Pause the sim
        client.pauseSim(False)

        count = 0
        innercount = 0
        #ONE SECOND INCREMENTS
        # while(count < 1000000 ):
        #     client.pauseSim(False)
        #     sleep(1.0)
        #     print("Pausing" + str(count))
        #     client.pauseSim(True)
        #     sleep(1.0)
        #     count+=1

        #0.1 SECOND INCREMENTS
        # while(count < 1000000 ):
        #     client.pauseSim(False)
        #     sleep(0.1)
        #     print("Pausing" + str(count))
        #     client.pauseSim(True)
        #     sleep(0.1)
        #     count+=1

         #0.001 SECOND INCREMENTS
        # while(count < 1000000 ):
        #     client.pauseSim(False)
        #     sleep(0.001)
        #     print("Pausing" + str(count))
        #     client.pauseSim(True)
        #     sleep(0.001)
        #     count+=1

        #0.00001 SECOND INCREMENTS
        # while(count < 1000000 ):
        #     client.pauseSim(False)
        #     sleep(0.00001)
        #     print("Pausing" + str(count))
        #     client.pauseSim(True)
        #     sleep(0.00001)
        #     count+=1


        # sim/operation/override/override_timestep 

        #Doing stuff In between Test SECOND INCREMENTS
        while(count < 1000000):
            #50 Millisecond Timesteps
            sleep(0.05)
            client.pauseSim(False) #Unpause
            # sleep(0.05) # Run 50 Milliseconds
            # client.pauseSim(True) # Pause Simulator
            #Run Model (Send commands to simulator within this process)
            ####Insert Model Here, some assembly required#######
            cogModel.update_aircraft_state()
            cogModel.update_controls_simultaneously()
                #Please work........no excuses now
            #Repeat
            # print("Advanced 50 Milliseconds: Step #" + str(count))
            count+=1





            # print("Pausing" + str(count))
            # innercount = 0
            # client.pauseSim(True)
            # sleep(0.05) #Simulate 50 milliseconds

            # sleep(0.01) #Simulate 50 milliseconds
            # # while(innercount < 100000):
            # #     # print("Doing Stuff" + str(innercount)) # Simulates the model running and computing for 50 Milliseconds maybe?
                
            # #     innercount+=1
            # # else:
            # client.pauseSim(False)
            # print("Exit Pause" + str(count))
            # sleep(0.5)
            # count+=1


        print("End of Python client example")
        input("Press any key to exit...")

if __name__ == "__main__":
    ex()