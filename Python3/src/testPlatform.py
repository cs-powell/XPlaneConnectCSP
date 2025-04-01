import datetime
from math import cos, pi, sin, sqrt
import os
import time
from time import sleep
import xpc
from cognitiveModel import AircraftLandingModel
import shutil


def eulerToQuat(psiInput,thetaInput,phiInput):
    
    psi = float(pi / 360 * psiInput)
    theta = float(pi / 360 * thetaInput)
    phi = float(pi / 360 * phiInput)
    q0 =  cos(psi) * cos(theta) * cos(phi) + sin(psi) * sin(theta) * sin(phi)
    q1 =  cos(psi) * cos(theta) * sin(phi) - sin(psi) * sin(theta) * cos(phi)
    q2 =  cos(psi) * sin(theta) * cos(phi) + sin(psi) * cos(theta) * sin(phi)
    q3 = -cos(psi) * sin(theta) * sin(phi) + sin(psi) * cos(theta) * cos(phi)
    e = sqrt(q0 * q0 + q1 * q1 + q2 * q2 + q3 * q3)

    quat = [q0/e,q1/e,q2/e,q3/e]
    return quat


def experimentSetUp(client,altitudeInput,newExperiment):
    print("Entered: EXPERIMENTSETUP")
    if(newExperiment):
        #Location:
        groundLevel = 5434
        offset = altitudeInput
        altitudeFEET = groundLevel + offset
        altitudeMETERS = altitudeFEET/3.281
        altitude = altitudeMETERS
        print(str(altitude))
        location1 =  [20,   -998, 39.96239,  -104.69713, altitude, -998, -998, -998, -998]
        # testLocation = [20,   -998, 27.20579,  -80.08621, altitude, -998, -998, -998, -998] # 27.20579°N/80.08621°W
        data = [
            location1\
            ]
        client.sendDATA(data)


        print("Zero velocities")
        x = "sim/flightmodel/position/local_vx"
        y = "sim/flightmodel/position/local_vy"
        z = "sim/flightmodel/position/local_vz"
        client.sendDREF(x,0)
        client.sendDREF(y,0)
        client.sendDREF(z,0)


        print("Zero rotation")
        p = "sim/flightmodel/position/P"
        q = "sim/flightmodel/position/Q"
        r = "sim/flightmodel/position/R"
        client.sendDREF(p,0)
        client.sendDREF(q,0)
        client.sendDREF(r,0)

        print("set heading")
        # client.pauseSim(True)
        orient = "sim/flightmodel/position/q"
        # orientCommand = [1,0.0,0.0,0.0]
        pitch = client.getDREF("sim/flightmodel/position/true_theta")
        roll = client.getDREF("sim/flightmodel/position/true_phi")
        
        orientCommand = eulerToQuat(179,0,0) # heading, pitch,Roll
        orientTest = [1.0,1.0,1.0,1.0] # heading, pitch,Roll

        print("ORIENT TO: " + str(orientCommand))
        client.sendDREF(orient,orientCommand)
        orientResult = client.getDREF(orient)
        print(str(orientResult))
        # client.pauseSim(False)

        #Weather:
        windLayer = "sim/weather/wind_altitude_msl_m[0]"
        windLayer2 = "sim/weather/wind_altitude_msl_m[1]"
        windLayer3 = "sim/weather/wind_altitude_msl_m[2]"
        windDirection = "sim/weather/wind_direction_degt[0]"
        windSpeed = "sim/weather/wind_speed_kt[0]"
            # windDirections = [50.0,50.0,50.0,50.0,50.0,50.0,50.0,50.0,50.0,50.0,50.0,50.0,50.0]
            # client.sendDREF(windDirection,windDirections)
            # winds = [50.0,50.0,50.0,50.0,50.0,50.0,50.0,50.0,50.0,50.0,50.0,50.0,50.0]
            # client.sendDREF(windSpeed,winds)
            # result = client.getDREF(windLayer)
            # print("Wind:" + str(result))
        print("Setting Wind Layers")
        client.sendDREF(windLayer,6000)
        client.sendDREF(windLayer2,15000)
        client.sendDREF(windLayer3,15000)
        print("Setting Wind Direction and Speed")
        client.sendDREF(windDirection,170)
        print("Set 1")

        client.sendDREF(windSpeed,0)
        print("Set 2")


        # client.sendDREF(turbulence,turbulencePercentage)

        # preset = "sim/weather/region/weather_preset"
        # value = 8
        # client.sendDREF(preset,value)
        client.pauseSim(True)
        input("Press Enter to finish setting up Simulation")
        client.pauseSim(False)
        print("Setting initial velocity")
        zInit = "sim/flightmodel/position/local_vz"
        client.sendDREF(zInit, 80)
        print("setup complete")


        
        """
        Set orientation and Position
            1 - Time 
            3- Speeds: [3,   V-indicated, 3,   2, 2, -998, VindMPH, V trueMPHas, vtrue MPHgs],\
            16 - Angular Velocities
            17 - PitchRoll and Headings: [17,   Pitch, Roll,   Heading True, -998, -998, -998, -998, -998],\   
            18 - Angle of Attack
            20 - Latitude and Longitude
        """
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
        
        # Set control surfaces and throttle of the player aircraft using sendCTRL
        # print("Setting controls")
        # ctrl = [0.0, 0.0, 0.0, 0.0]
        # cogModel.client.sendCTRL(ctrl)
        # print("past setting controls")
    else:
        print("Experiment currently in progress, not resetting position and environmental conditions")

def runExperiment(title,printFlag,experimentStart):
    print("Model Test: Xplane setting up connection")
    print("Setting up simulation")
    startTime = 0
    endTime = 0
    difference = endTime - startTime
    experimentLive = True
    timeoutLimit = 10
    newExperiment =  experimentStart
    while(difference < timeoutLimit and experimentLive):
        print("Time Elapsed: -----> " + str(difference))
        try:
            with xpc.XPlaneConnect() as client:
                # Verify connection
                client.getDREF("sim/test/test_float")
                cogModel = AircraftLandingModel(client,printFlag)
                experimentSetUp(cogModel.client,3000,newExperiment)
                cogModel.client.pauseSim(False)
                count = 0
                innercount = 0
                clockStart = time.time()
                retry = 0
                newExperiment = False
                while(cogModel.simulationStatus()):  
                    clockStart = time.time() #START TIMER
                    #Run Model
                    cogModel.update_aircraft_state()
                    cogModel.update_controls_simultaneously()
                    client.pauseSim(False) #Unpause Simulator
                    clockEnd = time.time() # STOP TIMER
                    count+=1
                    # print("Clock Time: " + str(clockEnd - clockStart)) ## LOG TIME TAKEN 
                    sleep(0.05) # LET Simulator Run 50 Milliseconds
                    startTime = time.time()
                    experimentLive = cogModel.simulationStatus()
        except:
            print("except detected")
            endTime = time.time()
            print("Start Time:" + str(startTime))
            print("End Time:" + str(endTime))
            difference = endTime - startTime
            continue
    if(difference >= timeoutLimit):
        print("Timeout[" + str(difference) +"]:"+"Error, please run test again")
    else: 
        print("Model has finished running")

    #Copy data.txt to the cloudddddd using python magic and accurate filepaths
    now = datetime.datetime
    shutil.copy("/Users/flyingtopher/X-Plane 11/Data.txt", "/Users/flyingtopher/Desktop/Test Destination/" + title + "_" + str(now.now()) + "_" + ".txt")
    # print("CLEAN UP: Data File Copied and saved")
    # os.remove("/Users/flyingtopher/X-Plane 11/Data.txt")
    print("CLEAN UP: Data File Deleted and Reset")
    exit = input("Press 'y' or any key to continue, press 'n' to exit...")
    if(exit == "n"):
        exit = True
    else:
        exit = False
    return exit
    ##Reset the sim with the keyboard shortcut (wrapper around model that waits for reconnection)
        
def ex():
    ##Store Experiment Battery
    ##Different paramters on every run of the model
    ## Nested loops: 
        ##wind conditions, pilot conditions
    title = input("Please Enter Experiment Set Title, leave blank for trial runs")
    count = 0
    while(count<2):
        input("Press Enter to Start Experiment #" + str(count) + ": ")
        print("Data File Reset")
        f = open("/Users/flyingtopher/X-Plane 11/Data.txt", 'w')
        exit = runExperiment(title,True,True)
        if(exit):
            break
        count+=1
    print("Experiment Battery Complete")

if __name__ == "__main__":
    ex()