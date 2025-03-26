import time
from time import sleep
import xpc
from cognitiveModel import AircraftLandingModel

def ex():

    print("Model Test: Xplane setting up connection")
    print("Setting up simulation")
    with xpc.XPlaneConnect() as client:
        # Verify connection
        try:
            # If X-Plane does not respond to the request, a timeout error
            # will beff raised.
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



        # client.pauseSim(True)
        # # Set position of the player aircraft
        # print("Setting Experiment Position: Denver Airport Runway")
        # #       Lat     Lon         Alt   Pitch Roll Yaw Gear
        # posi = [39.945, -104.70, 2500, 0,    0,   0,  1] #3NM Approach for Denver Intl Runway 16R (16,000 feet)
        # client.sendPOSI(posi)
        # quat = (1.0,0.0,0.0,173.0)
        # quatDref = "sim/flightmodel/position/q"
        # client.sendDREF(quatDref,quat)
        # speed = (1.0,0.0,0.0,173.0)
        # client.sendDREF("sim/flightmodel/position/indicated_airspeed",speed)
        # client.pauseSim(False)


        # # Set angle of attack, velocity, and orientation using the DATA command
        # print("Setting orientation")
        # data = [\
        #     [18,   0, -998,   0, -998, -998, -998, -998, -998],\
        #     [ 3, 130,  130, 130,  130, -998, -998, -998, -998],\
        #     [16,   0,    0,   0, -998, -998, -998, -998, -998]\
        #     ]
        # client.sendDATA(data)


        """
        Set orientation and Position 
            16 - Angular Velocities
            17 - PitchRoll and Headings
            18 - Angle of Attack
            20 - Latitude and Longitude
        """
        print("Setting orientation for test")
        data = [\
            [16,   -998, -998,   -998, -998, -998, -998, -998, -998],
            [17,   -998, -998,   -998, -998, -998, -998, -998, -998],\
            [18,   -998, -998,   -998, -998, -998, -998, -998, -998],\
            [19,   -998, -998,   -998, -998, -998, -998, -998, -998],\
            [20,   -998, -998,   -998, -998, -998, -998, -998, -998]\
            ]
        client.sendDATA(data)

        # Set control surfaces and throttle of the player aircraft using sendCTRL
        print("Setting controls")
        ctrl = [0.0, 0.0, 0.0, 0.0]
        client.sendCTRL(ctrl)
        # Pitch, Roll, Rudder, Throttle
        # Pause the sim
        client.pauseSim(False)
        count = 0
        innercount = 0
        clockStart = time.time()
        #Doing stuff In between Test SECOND INCREMENTS
        while(count < 1000000):
            clockStart = time.time() #START TIMER 
            client.pauseSim(True) # Pause Simulator

            #Run Model 
            cogModel.update_aircraft_state()
            cogModel.update_controls_simultaneously()

            client.pauseSim(False) #Unpause Simulator 
            clockEnd = time.time() # STOP TIMER
            count+=1
            print("Clock Time: " + str(clockEnd - clockStart)) ## LOG TIME TAKEN 
            sleep(0.05) # LET Simulator Run 50 Milliseconds

            #
        print("Model has finished running")
        #Copy data.txt to the cloudddddd using python magic and accurate filepaths
        
        input("Press any key to exit...")

if __name__ == "__main__":
    ex()