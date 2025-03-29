import datetime
import time
from time import sleep
import xpc
from cognitiveModel import AircraftLandingModel
import shutil

def runExperiment(title):

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

        groundLevel = 5434
        offset = 4000
        altitudeFEET = groundLevel + offset
        altitudeMETERS = altitudeFEET/3.281
        altitude = altitudeMETERS
        """
        Set orientation and Position
            1 - Time 
            3- Speeds: [3,   V-indicated, 3,   2, 2, -998, VindMPH, V trueMPHas, vtrue MPHgs],\
            16 - Angular Velocities
            17 - PitchRoll and Headings: [17,   Pitch, Roll,   Heading True, -998, -998, -998, -998, -998],\   
            18 - Angle of Attack
            20 - Latitude and Longitude
        """
        location1 =  [20,   groundLevel + 3000, 39.96239,  -104.69713, -998, -998, -998, -998, -998]
        testLocation = [20,   -998, 27.20579,  -80.08621, altitude, -998, -998, -998, -998] # 27.20579°N/80.08621°W
        
        kias = -998
        keas = -998
        ktas = -998
        ktgs = -998
        mph = -998
        mphas = 80
        mphgs = 80

        print("Setting orientation for test")
        # client.sendDREF("sim/operation/override/override_planepath",1)
        # client.pauseSim(True)

        # client.sendDREF("sim/flightmodel/position/local_y",2000)
        # client.sendDREFs(["sim/flightmodel/position/P","sim/flightmodel/position/Q","sim/flightmodel/position/R"],[0,0,0])
        # client.sendDREF("sim/flightmodel/position/q",[0.3, 0, 0, 0.6])

        # data = [\
        #     [3,   -998, -998,   -998, -998, -998, -998, mphas, mphgs],\
        #     [16,   0, 0,   0, -998, -998, -998, -998, -998],\
        #     [17,   0, 0,   0, -998, -998, -998, -998, -998],\
        #     [18,   -998, -998,   -998, -998, -998, -998, -998, -998],\
        #     [19,   -998, -998,   -998, -998, -998, -998, -998, -998],\
        #     testLocation\
        #     ]
        # client.sendDATA(data)
        # client.sendDREF("sim/operation/override/override_planepath",0)

        client.pauseSim(False)

        # 39.96239°N/104.69713°W
        runModel = True
        # Set control surfaces and throttle of the player aircraft using sendCTRL
        print("Setting controls")
        ctrl = [0.0, 0.0, 0.0, 0.0]
        client.sendCTRL(ctrl)
        # Pitch, Roll, Rudder, Throttle
        # Pause the sim
        # client.pauseSim(False)
        count = 0
        innercount = 0
        clockStart = time.time()
        #Doing stuff In between Test SECOND INCREMENTS
        while(cogModel.simulationStatus() and runModel):
            clockStart = time.time() #START TIMER
            # client.pauseSim(True) # Pause Simulator
            #Run Model 
            cogModel.update_aircraft_state()
            cogModel.update_controls_simultaneously()
            client.pauseSim(False) #Unpause Simulator
            clockEnd = time.time() # STOP TIMER
            count+=1
            # print("Clock Time: " + str(clockEnd - clockStart)) ## LOG TIME TAKEN 
            sleep(0.05) # LET Simulator Run 50 Milliseconds

        print("Model has finished running")
        #Copy data.txt to the cloudddddd using python magic and accurate filepaths
        now = datetime.datetime
        shutil.copy("/Users/flyingtopher/X-Plane 11/Data.txt", "/Users/flyingtopher/Desktop/Test Destination/" + title + "_" + str(now.now()) + "_" + ".txt")
        
        input("Press any key to exit...")
        ## Copy Data.txt to the repository file for analysis
        ##Reset the sim with the keyboard shortcut (wrapper around model that waits for reconnection)
        
def ex():
    ##Store Experiment Battery

    ##Different paramters on every run of the model
    ## Nested loops: 
        ##wind conditions, pilot conditions
    title = input("Please Enter Experiment Set Title, leave blank for trial runs")
    count = 0
    while(count<2):
        runExperiment(title)
        count+=1
    print("Experiment Battery Complete")

if __name__ == "__main__":
    ex()