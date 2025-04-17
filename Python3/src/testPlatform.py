import datetime
from math import cos, pi, sin, sqrt
import os
import time
from time import sleep
import xpc
from cognitiveModel import AircraftLandingModel
import shutil
import csv
from enum import Enum
from playsound import playsound
import threading as threading

# import pyautogui as pag




class messageType(Enum):
    REGULAR = 1
    ERROR = 2

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

def loadFile():
    filename = "Python3/src/experiments/weather_files/weather.csv"
    filename = "Python3/src/experiments/weather_files/turbulence.csv"
    with open(filename,"r") as f:
        matrix = list(csv.reader(f,delimiter=','))
        print(matrix)
        return matrix
    

def selectWeather(matrix,experimentNumber):
    return matrix[experimentNumber]

def experimentSetUp(client,currentConditions,newExperiment,file):
    # input("Check The Loaded File Now")
    print("Entered: EXPERIMENTSETUP")
    if(newExperiment):
        #Location:
        groundLevel = 5434
        offset = float(currentConditions[1])
        altitudeFEET = groundLevel + offset
        altitudeMETERS = altitudeFEET/3.281
        altitude = altitudeMETERS
        print(str(altitude))
        location1 =  [20,   -998, 39.96239,  -104.696032, altitude, -998, -998, -998, -998]
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
        client.sendDREF(windLayer,float(currentConditions[2]))
        # client.sendDREF(windLayer2,15000)
        # client.sendDREF(windLayer3,15000)
        print("Setting Wind Direction and Speed")
        client.sendDREF(windDirection,float(currentConditions[3]))
        print("Set 1")
        client.sendDREF(windSpeed,float(currentConditions[4]))
        print("Set 2")


        print("Setting Turbulence Level")
        turbulenceDREF = "sim/weather/turbulence[0]"
        client.sendDREF(turbulenceDREF,float(currentConditions[5]))

        print("Setting Thermals")
        thermalRateDREF = "sim/weather/thermal_rate_ms"
        thermalPercentDREF = "sim/weather/thermal_percent"
        thermalAltitudeDREF = "sim/weather/thermal_altitude_msl_m"
        client.sendDREF(thermalRateDREF,float(currentConditions[6]))
        client.sendDREF(thermalPercentDREF,float(currentConditions[7]))
        client.sendDREF(thermalAltitudeDREF,float(currentConditions[8]))



        # client.sendDREF(turbulence,turbulencePercentage)
        # preset = "sim/weather/region/weather_preset"
        # value = 8
        # client.sendDREF(preset,value)
        client.pauseSim(True)
        # input("Press Enter to finish setting up Simulation")
        client.pauseSim(False)
        print("Setting initial velocity")
        zInit = "sim/flightmodel/position/local_vz"
        client.sendDREF(zInit, 50)



        print("Setting Fuel to Experiment Level")
        # fuel = "sim/aircraft/weight/acf_m_fuel_tot"
        fuel = "sim/flightmodel/weight/m_fuel"
        fuelLevels = [20,20]
        client.sendDREF(fuel, fuelLevels)
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
        message = "Conditions are set as\n" \
        "Experiment #: {} \n"\
        "Starting Altitude: {} \n" \
        "Layer Altitude: {} \n" \
        "Wind Direction: {} \n" \
        "Wind Speed: {} \n" \
        "Turbulence: {} \n" \
        "Thermal Rate: {} \n" \
        "Thermal Percent: {}\n" \
        "Thermal Altitude: {}\n" \
        "Cognitive Delay: {}\n".format(currentConditions[0],currentConditions[1],currentConditions[2],currentConditions[3],currentConditions[4],
                                        currentConditions[5],currentConditions[6],currentConditions[7],currentConditions[8],currentConditions[9])
        specialPrint(message,False,messageType.REGULAR)
        # file.write("[[[" + message + "]]]")

          
    else:
        print("Experiment currently in progress, not resetting position and environmental conditions")

def printLoop(status,data):
    while(status):
        print(str(data))
    if(not status):
        print("Thread Should Finish Now")


def log(cogModel, file,timeElapsed):
    airspeedDREF = "sim/cockpit2/gauges/indicators/airspeed_kts_pilot"
    rollDREF = "sim/cockpit2/gauges/indicators/roll_AHARS_deg_pilot"
    magneticHeadingDREF = "sim/cockpit2/gauges/indicators/heading_AHARS_deg_mag_pilot"
    latitudeDREF = "sim/flightmodel/position/latitude" ## Lat 
    longitudeDREF = "sim/flightmodel/position/longitude" ##Long
    verticalSpeedDREF = "sim/flightmodel/position/vh_ind_fpm"
    altitudeAGLDREF = "sim/flightmodel/position/y_agl"
    pitchDREF = "sim/flightmodel/position/true_theta"
    brakeDREF = "sim/cockpit2/controls/parking_brake_ratio"
    wheelSpeedDREF = "sim/flightmodel2/gear/tire_rotation_speed_rad_sec"
    wheelWeightDREF = "sim/flightmodel/parts/tire_vrt_def_veh"
    sources = [latitudeDREF, longitudeDREF,altitudeAGLDREF, pitchDREF,rollDREF]
    data = cogModel.client.getDREFs(sources)
    mainString = []
    # data = client.readDATA()
    mainString.append(str(timeElapsed))
    mainString.append(",")
    for d in data:
        mainString.append(str(d[0]))
        mainString.append(",")

    mainString[len(mainString)-1] = "\n"
    finalString = "".join(mainString)
    file.write(finalString)
    # file.write(str(timeElapsed) + "," + str(data[0][0]) + "," + str(data[1][0]) + "," + str(data[2][0]) + "," + str(data[3][0]) +"\n")    


def runExperiment(title,currentConditions,allowPrinting,isNewExperiment,experimentCount,file):
    specialPrint("New Experiment\nSetting Up the Simulation",False,messageType.REGULAR)
    startTime = 0
    endTime = 0
    timeElapsed = endTime - startTime
    experimentInProgress = True
    timeoutLimit = 10
    newExperiment =  isNewExperiment
    while(timeElapsed <= timeoutLimit and experimentInProgress):
        try:
            with xpc.XPlaneConnect() as client:
                """
                Test Connection
                """
                client.getDREF("sim/test/test_float")

                """
                Setup Model
                """
                cogModel = AircraftLandingModel(client,allowPrinting)
                """
                Set Simulator Conditions
                """
                experimentSetUp(cogModel.client,currentConditions,newExperiment,file)
                
                currentDelay = float(currentConditions[9])
                """
                """
                cogModel.client.pauseSim(False)
                newExperiment = False
                """
                SETUP DATA THREAD
                """
                # thread = threading.Thread(target=printLoop(cogModel.getSimulationStatus(),cogModel.destinations.get("airspeed")))
                # thread.start()
                """
                Single Experiment Loop
                """
                startTime = time.time()
                endTime = time.time()
                elapsed = endTime - startTime
                while(experimentInProgress):
                    elapsed = endTime - startTime
                    if(elapsed > currentDelay):
                        cogModel.update_aircraft_state() 
                        cogModel.update_controls_simultaneously()
                        client.pauseSim(False)          #Unpause Simulator
                        startTime = time.time()
                    # sleep(2)                     # Let Simulator Run 50 Milliseconds
                    log(cogModel,file,elapsed)
                    experimentInProgress = cogModel.getSimulationStatus()
                    endTime = time.time()
        except:
            endTime = time.time()
            timeElapsed = endTime - startTime
            message = "Except detected\nStart Time: {a}\nEnd Time: {b}\n" \
            "Time Elapsed: -----> {c} ".format(a= startTime, b=endTime,c=timeElapsed)
            specialPrint(message,False,messageType.ERROR)
            continue

    """
    Parse End Condition: Succesful Run or Timeout-Induced End
    """
    if(timeElapsed >= timeoutLimit):
        print("Timeout[" + str(timeElapsed) +"]:"+"Error, please run test again")
    else: 
        print("Model has finished running")


    """
    Ask Experimenter if they would like to exit experiment battery early 
    """
    # exitDecision = input("Press 'y' or any key to continue, press 'n' to exit...")
    exitDecision = "yes"
    
    if(exitDecision == "n"):
        exitExperimentLoop = True
    else:
        exitExperimentLoop = False
    return exitExperimentLoop


def say(msg = "Finish", voice = "Victoria"):
        os.system(f'say -v {voice} {"Hello"}')

# def playSound():
#     # wave_obj = sa.WaveObject.from_wave_file("Python3/src/experiments/terrain-terrain,-pull-up!-pull-up!-made-with-Voicemod.wav")
#     # wave_obj.play()
#     # # play_obj.wait_done()
#     # song = AudioSegment.from_wav("Python3/src/experiments/terrain-terrain,-pull-up!-pull-up!-made-with-Voicemod.wav")
#     # play(song)
#     # playsound('Python3/src/experiments/terrain-terrain,-pull-up!-pull-up!-made-with-Voicemod.wav')

    

def setUp():
    file = open("/Users/flyingtopher/X-Plane 11/Data.txt", 'w')
    file.close()
    specialPrint("Data Collection File Ready",False,messageType.REGULAR)

def cleanUp(count,title):
    now = datetime.datetime
    # shutil.copy("/Users/flyingtopher/X-Plane 11/Data.txt", "/Users/flyingtopher/Desktop/Test Destination/" + title + "_"+ count + "_" + str(now.now()) + "_" + ".txt")
    shutil.copy("/Users/flyingtopher/X-Plane 11/Data.txt", "/Users/flyingtopher/Desktop/Test Destination/Current Experiment/" + title + "_"+ str(count) + "_" + ".txt")
    print("CLEAN UP: Data File Deleted and Reset")
    specialPrint("Data File Ready",False,messageType.REGULAR)

def specialPrint(text, inputRequested,type): 
    if(type == messageType.REGULAR): 
        print("-" * 81)
        print('====> ', end='')
        print(text, end= " <====\n")
        print("-" * 81 + "\n")
        if(inputRequested):
            inputReceived = input()
            return inputReceived
    if(type == messageType.ERROR):
        print("*" * 81)
        print(text)
        print("*" * 81 + "\n")



def ex():
    # playSound()
    """
    One Time experimental setup
    """
    prefix = specialPrint("Please Enter Experiment Set Title, leave blank for trial runs", True, messageType.REGULAR) 
    experimentConditionMatrix = loadFile()
    startAt = input("Start At Experiment # 1 to " + str(len(experimentConditionMatrix)-1))

    title = str(prefix + "--" + experimentConditionMatrix[0][0])
    specialPrint("Title is: " + title, False, messageType.REGULAR)
    allowPrinting = False
    isNewExperiment = True
    experimentCount = int(startAt)
    header = "Cycle Time,Latitude, Longitude, Altitude, Pitch, Roll\n"
    file2 = open("/Users/flyingtopher/Desktop/Test Destination/Current Experiment/CurrentExperimentList.txt", 'w')
    """
    Experiment Loop
    """
    while(experimentCount<len(experimentConditionMatrix)):
        setUp()
        file = open("/Users/flyingtopher/X-Plane 11/Data.txt", 'a')
        file.write(str(header)) #Write Header to File$
        currentConditions = experimentConditionMatrix[experimentCount]
        file2.write(str(experimentCount) +" // " + str(currentConditions) + "\n")
        exitExperimentLoop = runExperiment(title,currentConditions,allowPrinting,isNewExperiment,experimentCount,file)
        if(exitExperimentLoop):
            break
        cleanUp(experimentCount,title)
        experimentCount+=1
        # pag.alert(text="Experiment " + str(experimentCount+1) + " complete. Starting new Experiment", title="EXPERIMENT STATUS UPDATE")
    """
    End of Experiments
    """
    now = datetime.datetime
    ##Adding something to copy all the battery files into a safe folder so they don't get overwritten
    shutil.copytree("/Users/flyingtopher/Desktop/Test Destination/Current Experiment", ("/Users/flyingtopher/Desktop/Test Destination/Data Storage/" + title + " " + str(now.now())), dirs_exist_ok=True)
    specialPrint("Experiment Battery Complete", False,messageType.REGULAR)

if __name__ == "__main__":
    ex()