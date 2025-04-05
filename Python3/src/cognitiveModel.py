import pyactr
# from XPlaneConnect import *
import xpc
import math
# import geopy
from geographiclib.geodesic import Geodesic as geo
from rich import print



# Initialize XPlaneConnect client
class scaleFactor():
    SCALEYOKEPULL = 10
    SCALEYOKESTEER = 10
    SCALERUDDER = 10
    SCALELATITUDERUDDER = 0.02
    SCALETHROTTLE = 1000

###Define variables/parameters for aircraft class/category : Wisdom of Raju 
class AircraftLandingModel(pyactr.ACTRModel):
    def __init__(self,client,printFlag):
        super().__init__()
        self.client = client
        self.inProgress = True
        self.printControlsFlag = printFlag
        self.targetLat = 39.895791
        self.targetLong = -104.696014
        """
        Setting DREF variables and loading into drefs array
        """
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
        # self.sources = [airspeedDREF,rollDREF,magneticHeadingDREF,verticalSpeedDREF,altitudeAGLDREF,pitchDREF,brakeDREF,wheelSpeedDREF,wheelWeightDREF]

        self.sources = {
            "airspeed" : airspeedDREF,
            "roll" : rollDREF,
            "heading" : magneticHeadingDREF,
            "latitude": latitudeDREF,
            "longitude": longitudeDREF,
            "vertical speed" : verticalSpeedDREF,
            "altitude": altitudeAGLDREF,
            "pitch" : pitchDREF,
            "brakes": brakeDREF,
            "wheelSpeed": wheelSpeedDREF,
            "wheelWeight": wheelWeightDREF
        }

        """
        Initial Initialization of destination Variables and loading into destinations array
        """
        airspeed = self.client.getDREF("sim/cockpit2/gauges/indicators/airspeed_kts_pilot")
        roll = self.client.getDREF("sim/cockpit2/gauges/indicators/roll_AHARS_deg_pilot")
        heading = self.client.getDREF("sim/cockpit2/gauges/indicators/heading_AHARS_deg_mag_pilot")
        latitude = self.client.getDREF("sim/flightmodel/position/latitude") ##Current Lat 
        longitude = self.client.getDREF("sim/flightmodel/position/longitude") ##Current Long
        descent_rate = self.client.getDREF("sim/flightmodel/position/vh_ind_fpm")
        altitude = self.client.getDREF("sim/flightmodel/position/y_agl")
        pitch = self.client.getDREF("sim/flightmodel/position/true_theta")
        brake = self.client.getDREF("sim/cockpit2/controls/parking_brake_ratio")
        wheelS = self.client.getDREF("sim/flightmodel2/gear/tire_rotation_speed_rad_sec")
        wheelW = self.client.getDREF("sim/flightmodel/parts/tire_vrt_def_veh")

        HARDCODE_HEADING = 179
        
        self.airspeed = airspeed[0]
        self.roll = roll[0]
        self.heading = HARDCODE_HEADING
        self.latitude = latitude[0]
        self.longitude = longitude[0]
        self.descent_rate = descent_rate[0]
        self.altitude = altitude[0]
        self.pitch = pitch[0]
        self.brakes = brake[0]
        self.wheelSpeed = wheelS[0]
        self.wheelWeight = wheelW[0]        
        # self.destinations = [self.airspeed,self.roll,self.heading,self.descent_rate,self.altitude,self.pitch,self.brakes,self.wheelSpeed,self.wheelWeight]
        
        self.destinations = {
            "airspeed" : self.airspeed,
            "roll" : self.roll,
            "heading" : self.heading,
            "latitude": self.latitude,
            "longitude": self.longitude,
            "vertical speed" : self.descent_rate,
            "altitude": self.altitude,
            "pitch" : self.pitch,
            "brakes": self.brakes,
            "wheelSpeed": self.wheelSpeed,
            "wheelWeight": self.wheelWeight
        }

        """
        Initial Initialization of target Values
        """
        self.target_airspeed = 80
        self.target_roll = 0
        self.target_heading = self.heading #Track heading from initialization[DEPRECATED]
        self.target_Lat = self.latitude #Track Lat
        self.target_Long = self.longitude #Track Long
        self.target_descent_rate = 500
        self.target_altitude = -998
        self.target_pitch = 20
        self.targets = [self.target_airspeed,self.target_roll,self.target_heading,self.target_Lat,self.target_Long,self.target_descent_rate,self.target_altitude,self.target_pitch]
        

        #State Flags (Boolean)  & Current State (Integer)
        self.descent = False
        self.flare = False
        self.rollOut = False
        self.currentState = 0
        # self.stateFlags = [self.descent,self.flare,self.rollOut]
        self.phaseFlags = {
            "descent" : self.descent,
            "flare" : self.flare,
            "roll out" : self.rollOut
        }


        # Declare the state for previous values
        self.previous_airspeed = None
        self.previous_roll = None
        self.previous_heading = None
        self.previous_descent_rate = None

        # Initialize the integral errors for each parameter
        self.integral_airspeed = 0
        self.integral_roll = 0
        self.integral_heading = 0
        self.integral_Latitude = 0
        self.integral_Longitude = 0
        self.integral_descent_rate = 0

        #Flare Specific Parameters
        self.integral_pitch = 0 

        # Integral gains (tune these values for performance)
        self.Kp = 0.1  # Proportional gain
        self.Ki = 0.01  # Integral gain                
        # self.Ki = 2  # Integral gain


        """
        Variable Atlas Schema: source => destination => target
        """
        # self.variableAtlas = list(xrange(1000))
        # idx = 0
        # while(idx < len(self.sources) - 1):
        #     self.variableAtlas[0] = [self.sources[idx],self.destinations[idx],self.targets[idx]]
        #     idx += 1

    def dictionaryAccess(self,dictionary,key):
        # print("dictionary access for: " + str(key))
        result = dictionary[key]
        if isinstance(result, tuple):
            return result[0]
        else: 
            return result

        
        
    
    def reassignClient(self,newClient):
        self.client = newClient

    def get_bearing(self,lat1, lat2, long1, long2):
        brng = geo.WGS84.Inverse(lat1, long1, lat2, long2)['azi1']
        self.target_heading = brng
        # print("TARGET BEARING: " + str(brng))

    def getAndLoadDREFS(self):
        results = self.client.getDREFs(self.sources.values())
        idx = 0
        for key in self.sources:
            self.destinations[key] = results[idx]
            idx+=1
        #Update Target Heading
        lat = self.client.getDREF("sim/flightmodel/position/latitude") ##Current Lat 
        long = self.client.getDREF("sim/flightmodel/position/longitude") ##Current Long
        self.get_bearing(lat[0],self.targetLat,long[0],self.targetLong)
        

        # print("getDrefs: " + str(results[1][0]))
        # print("current destination: " + str(self.destinations["airspeed"]))
        # print("current main Airspeed: " + str(self.airspeed))

        # while(idx < len(results) - 2):    
        #     self.destinations[idx] = results[idx][0]
        #     if(idx == 1):
        #         print("getDrefs: " + str(results[idx][0]))
        #         print("current destination: " + str(self.destinations[idx]))
        #         print("current main Airspeed: " + str(self.airspeed))
        #     idx += 1
        

    def printControls(self,calculated,errors,yokePull,yokeSteer,rudder,throttle):
        # print("In print controls")
        if(calculated == 1):
            # print("*        Calculated Controls         *")
            # print("*Parameter,Target,Current,Yoke Pull:      " + "Airspeed, " + str(self.target_airspeed) + "," +  str(self.airspeed)+ "," + str(yokePull))
            # print("*Parameter,Target,Current,Yoke Steer:     " + "Roll, " + str(self.target_roll) + "," +  str(self.destinations["roll"])+ "," + str(yokeSteer))
            # print("*Parameter,Target,Current,Rudder:         " + "Heading, " + str(self.target_heading) + "," +  str(self.heading)+ "," + str(rudder))
            # print("*Parameter,Target,Current,Throttle:       " + "Descent Rate, " + str(self.target_descent_rate) + "," +  str(self.descent_rate)+ "," + str(throttle))
            parameter = ["Airspeed","Roll","Heading","Longitude","Descent Rate","Altitude","Flare: Pitch", "Brakes: Wheel Speed", "Brakes: Wheel Weight"]
            target =  [str(round(self.target_airspeed)),
                       str(round(self.target_roll)),
                       str(round(self.target_heading,3)),
                       str(round(self.target_Long,6)),
                       str(round(self.target_descent_rate,3)),
                       str(round(self.altitude,3)),
                       str(round(self.target_pitch,3)),
                       0, 
                       0]
            
            current = [str(round(self.dictionaryAccess(self.destinations,"pitch"),3)),
                       str(round(self.dictionaryAccess(self.destinations,"roll"),3)),
                       str(round(self.dictionaryAccess(self.destinations,"heading"),3)),
                       str(round(self.dictionaryAccess(self.destinations,"longitude"),6)),

                       str(round(self.dictionaryAccess(self.destinations,"vertical speed"),3)),
                       str(round(self.dictionaryAccess(self.destinations,"altitude"),3)),
                       str(round(self.dictionaryAccess(self.destinations,"pitch"),3)),
                       str(round(self.dictionaryAccess(self.destinations,"wheelSpeed"),3)),
                       str(round(self.dictionaryAccess(self.destinations,"wheelWeight"),3))]
            controlVal = [str(round(yokePull,3)),
                          str(round(yokeSteer,3)),
                          str(round(rudder,3)),
                          str(round(rudder,3)), 
                          str(round(throttle,3)),
                          str(round(self.altitude,3)),
                          str(self.dictionaryAccess(self.phaseFlags,"flare")),
                          str(round(self.dictionaryAccess(self.destinations,"brakes"),3)),
                          str(round(self.dictionaryAccess(self.destinations,"brakes"),3))]

            header_row = "{:<20} {:<20} {:<20} {:>10}"
            headers = "Parameter Target Current Control_Value".split()
            row = "{:<20} {:<20} {:<20} {:>10}"
            print("\n" + header_row.format(*headers))
            print("-" * 81)
            for parameter, target, current, controlVal in zip(parameter, target, current, controlVal):
                    print(row.format(parameter, target, current, controlVal))


    def printVariables(self,errors,target,current,error,param1,param2):
        if(errors == 1):
            targetF = [str(round(target,3))]
            currentF = [str(round(current,3))]
            errorF = [str(round(error,3))]
            param1F = [str(round(param1,3))]
            param2F = [str(round(param2,3))]
            row = "{:<20} {:<20} {:<20} {:>10} {:>7.2f}"
            header_row = "{:<20} {:<20} {:<20} {:>10} {:>7}"
            headers = "Target Current Error (self.Kp*error) (self.Ki*integral_error)".split()
            print("\n" +header_row.format(*headers))
            print("-" * 81)
            # print(print(row.format(first_, last_, major_, credits_, gpa_)))
            for targetF, currentF, errorF, param1F, param2F in zip(targetF, currentF, errorF, param1F, param2F):
                print(row.format(target, current, error, param1, param2))

            # print("*        Target,     Current,     Error,       param1,        param2      \n" +
            #       "*        ______      _______      ______       _______        ______\n       " +
            #       str(round(target,2)) + "\n                " + str(round(current,2)) +
            #         "\n                     " +  str(round(error,2)) +
            #           "\n                               " +  str(round(param1,2)) +
            #             "\n                             " + str(round(param2,2)))
        


    def proportionalIntegralControl(self,print, current, target, integral_error,scalingFactor):
        """
        Proportional-Integral control rule implementation for multiple parameters.
        """
        # Calculate the error (current - target)
        error = target - current
        # error = current - target
        # print("Error: " +  str(error))
        # Update the integral of the error
        integral_error += error
        # print("Integral_error: " +  str(integral_error))

        # Calculate the control value using the PI formula
        control_value = (self.Kp * error) + (self.Ki * integral_error)

        ###Transformations:
        #Simple Sigmoid:
        control_value = (2 / (1 + math.exp(-(control_value/scalingFactor)))) - 1

        # self.printVariables(print,target,current,error,(self.Kp * error),(self.Ki * integral_error))
        return control_value, integral_error  # Return control value and updated integral error


    def update_controls_simultaneously(self):
        """
        Update all controls at the same time by calculating control values for each parameter.
        """
        # print("In update controls")
        # print("Entered Update Controls Simultaneously")
        # Compute control values for all parameters (yoke pull, yoke steer, rudder, throttle)
        if(self.dictionaryAccess(self.phaseFlags,"flare")): 
             yoke_pull, self.integral_airspeed = self.proportionalIntegralControl(1,self.dictionaryAccess(self.destinations,"pitch"), 
                                                                             self.target_pitch, 
                                                                             self.integral_pitch, 
                                                                     scaleFactor.SCALEYOKEPULL)
        
        if(self.dictionaryAccess(self.phaseFlags,"flare") == False):
             self.target_pitch = 10
             yoke_pull, self.integral_airspeed = self.proportionalIntegralControl(1,self.dictionaryAccess(self.destinations,"pitch"), 
                                                                             self.target_pitch, 
                                                                             self.integral_pitch, 
                                                                             scaleFactor.SCALEYOKEPULL)
            # yoke_pull, self.integral_airspeed = self.proportionalIntegralControl(1,self.airspeed, 
            #                                                                  self.target_airspeed, 
            #                                                                  self.integral_airspeed, 
            #                                                                  scaleFactor.SCALEYOKEPULL)

        yoke_steer, self.integral_roll = self.proportionalIntegralControl(0,self.dictionaryAccess(self.destinations,"roll"), self.target_roll, self.integral_roll,scaleFactor.SCALEYOKESTEER)

        """
        RUDDER CONTROLS
        """
        #ORIGINAL
        # rudder, self.integral_heading = self.proportionalIntegralControl(0,self.dictionaryAccess(self.destinations,"heading"), self.target_heading, self.integral_heading,scaleFactor.SCALERUDDER) 
        #lATITUDE/LONGITUDE
        rudder, self.integral_Longitude = self.proportionalIntegralControl(0,self.dictionaryAccess(self.destinations,"longitude"), self.target_Long, self.integral_Longitude,scaleFactor.SCALELATITUDERUDDER)

        
        
        
        throttle, self.integral_descent_rate = self.proportionalIntegralControl(0,self.dictionaryAccess(self.destinations,"vertical speed"), self.target_descent_rate, self.integral_descent_rate,scaleFactor.SCALETHROTTLE)
        ### 1. For Calculated Yoke and Throttle Values 
        #Invert Throttle Control & divide by 5 to scale

        throttle = -throttle
        throttle = throttle/5
        #Invert Yoke Pull & divide by 5 to scale
        yoke_pull = yoke_pull/5
        ## 2. For Constant Yoke and Throttle Values      
        # Constant yoke "back pressure" equal to 20% of total travel distance
        if(self.dictionaryAccess(self.phaseFlags,"flare") == False):
            yoke_pull = yoke_pull * 20
            # yoke_pull = 0.23
            throttle = 0.20
        if(self.dictionaryAccess(self.phaseFlags,"flare") == True):
            # yoke_pull = -yoke_pull
            yoke_pull = yoke_pull * 20
            throttle = 0
        # Constant throttle setting below the threshold needed to maintain straight and level flight
        
        ## Method 1: 
        # if(self.altitude < 350 and self.airspeed > 175): ## Integrate using the control equations;; A goal state update
        #     throttle = 0.1
        #     yoke_pull = 0.4


        # if(self.altitude < 300 and self.airspeed > 170): ## Integrate using the control equations;; A goal state update
        #     throttle = 0.05
        #     yoke_pull = 0.6

        # if(self.altitude < 250 and self.airspeed > 160): ## Integrate using the control equations;; A goal state update
        #     throttle = 0
        #     yoke_pull = 0.8


        # if(self.altitude < 250 and self.airspeed > 160): ## Integrate using the control equations;; A goal state update
        #     throttle = 0
        #     yoke_pull = 0.8
 
        ##Method 2: Same Control Statements with Change in Parameter to decided pitch from Airspeed ---> Local Pitch Relative to the Horizon

        rudder = rudder * -1
        #Switch Target for Pitch to Local Pitch Axis (ex. +10 Degrees nose up)
        if(self.printControlsFlag):
            self.printControls(1,0,yoke_pull,yoke_steer,rudder,throttle) #PRINT CONTROLS 
        # Send all controls simultaneously to X-Plane
        self.send_controls_to_xplane(yoke_pull, yoke_steer, rudder, throttle)


    def send_controls_to_xplane(self, yoke_pull, yoke_steer, rudder, throttle):
        """
        Sends all control inputs to X-Plane using XPlaneConnect
        """
        # Send yoke pull, yoke steer, rudder, and throttle simultaneously
        # print("Yoke Pull:" + str(yoke_pull))

        #Set the Trim to a value that allows the aircraft to osscilate around the target airspeed
        if(self.dictionaryAccess(self.phaseFlags,"flare") == False):
            trimdref = "sim/flightmodel/controls/elv_trim"
            trim = -0.3
            self.client.sendDREF(trimdref,trim)

        if(self.dictionaryAccess(self.phaseFlags,"flare")):
            trimdref = "sim/flightmodel/controls/elv_trim"
            trim = 0
            self.client.sendDREF(trimdref,trim)

        if(self.dictionaryAccess(self.phaseFlags,"roll out")):
            #Cut the Throttle
            throttle = 0
            #Release Yoke Back Pressure (Pitch Up Pressure from the flare maneuver)
            yoke_pull = 0
            #Hit the Brakes
            brakedref = "sim/cockpit2/controls/parking_brake_ratio"
            brake = 1
            self.client.sendDREF(brakedref,brake)

        self.client.sendCTRL([yoke_pull, yoke_steer, rudder, throttle, -998, -998])  # Control inputs: [yoke_pull, yoke_steer, rudder, throttle]

    def conditionChecks(self):

        if(self.dictionaryAccess(self.destinations,"wheelWeight") > 0.01 
           and self.dictionaryAccess(self.destinations,"wheelSpeed") > 1):
            #Two Parameters to Confirm Touchdown and wheel contact
            # "sim/flightmodel/parts/tire_vrt_def_veh" #Gear Strut Deflection (Weight on wheels)
            # "sim/flightmodel2/gear/tire_rotation_rate_rad_sec" #Tire Rotation Rate
            self.phaseFlags["roll out"] = True
            print("Hit the brakes")

        if(self.dictionaryAccess(self.destinations,"altitude") <= 20 
           and self.dictionaryAccess(self.phaseFlags,"flare") == False): 
            self.phaseFlags["flare"] = True
            self.Ki = 0.01  ## Increase Control Authority to compensate for decreasing airspeed
            print("Altitude < 500; Flare Set True")

        if(self.dictionaryAccess(self.destinations,"wheelWeight") > 0.01 
           and self.dictionaryAccess(self.destinations,"wheelSpeed") < 1 
           and self.dictionaryAccess(self.destinations,"airspeed") < 2 
           and self.dictionaryAccess(self.destinations,"brakes") == 1):            
            self.inProgress = False
    
    def getSimulationStatus(self):
        return self.inProgress
        

    # Update the model's DM based on X-Plane data
    def update_aircraft_state(self):
        """
        Faster Method 
        """
        self.getAndLoadDREFS()
        self.conditionChecks()

    # def logData(self):
