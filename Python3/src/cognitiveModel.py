import pyactr
# from XPlaneConnect import *
import xpc
import math
# Initialize XPlaneConnect client
class scaleFactor():
    SCALEYOKEPULL = 10
    SCALEYOKESTEER = 10
    SCALERUDDER = 10
    SCALETHROTTLE = 1000

###Define variables/parameters for aircraft class/category : Wisdom of Raju 
class AircraftLandingModel(pyactr.ACTRModel):
    def __init__(self,client):
        super().__init__()

        # Initialize the declarative memory (DM)
        # self.decmem.add( 
        #     [
        #         ("airspeed", 100), # Current airspeed (e.g., 100 knots)
        #         ("roll", 0),  # Current roll (0 for wings level)
        #         ("heading", 0),  # Current heading
        #         ("descent_rate", 500),  # Current descent rate in fpm
        #         ("target_airspeed", 80),  # Target airspeed during descent
        #         ("target_roll", 0),  # Target roll (wings level)
        #         ("target_heading", 90),  # Target heading (runway heading)
        #         ("target_descent_rate", 500)  # Target descent rate (fpm)
        #     ]  
        # )

        self.client = client

        airspeed = self.client.getDREF("sim/cockpit2/gauges/indicators/airspeed_kts_pilot")
        roll = self.client.getDREF("sim/cockpit2/gauges/indicators/roll_AHARS_deg_pilot")
        heading = self.client.getDREF("sim/cockpit2/gauges/indicators/heading_AHARS_deg_mag_pilot")
        descent_rate = self.client.getDREF("sim/flightmodel/position/vh_ind_fpm")
        altitude = self.client.getDREF("sim/flightmodel/position/y_agl")
        pitch = self.client.getDREF("sim/flightmodel/position/true_theta")

        brake = self.client.getDREF("sim/cockpit2/controls/parking_brake_ratio")
        wheelS = self.client.getDREF("sim/flightmodel2/gear/tire_rotation_speed_rad_sec")
        wheelW = self.client.getDREF("sim/flightmodel/parts/tire_vrt_def_veh")


        

       
        
        # Update the model's declarative memory
        # model.declarative_memory["airspeed"] = airspeed
        # model.declarative_memory["roll"] = roll
        # model.declarative_memory["heading"] = heading
        # model.declarative_memory["descent_rate"] = descent_rate
       
        self.airspeed = airspeed[0]
        self.roll = roll[0]
        self.heading = heading[0]
        self.descent_rate = descent_rate[0]
        self.altitude = altitude[0]
        self.brakes = brake[0]
        # print(wheelS)
        self.wheelSpeed = wheelS[0]

        self.wheelWeight = wheelW[0]
       


        #Flare Specific Parameters 
        self.flare = False
        self.pitch = pitch[0]

        #Rollout Specific Parameters
        self.rollOut = False
        




        self.target_airspeed = 80
        self.target_roll = 0
        self.target_heading = 0
        self.target_descent_rate = 500
        self.target_pitch = 20

   
        # Declare the state for previous values
        self.previous_airspeed = None
        self.previous_roll = None
        self.previous_heading = None
        self.previous_descent_rate = None

        # Initialize the integral errors for each parameter
        self.integral_airspeed = 0
        self.integral_roll = 0
        self.integral_heading = 0
        self.integral_descent_rate = 0

        #Flare Specific Parameters
        self.integral_pitch = 0 

        # Integral gains (tune these values for performance)
        self.Kp = 0.1  # Proportional gain
        self.Ki = 0.01  # Integral gain                
        # self.Ki = 2  # Integral gain



    def printControls(self,calculated,errors,yokePull,yokeSteer,rudder,throttle):
        print("In print controls")
        if(calculated == 1):
            # print("*        Calculated Controls         *")
            # print("*Parameter,Target,Current,Yoke Pull:      " + "Airspeed, " + str(self.target_airspeed) + "," +  str(self.airspeed)+ "," + str(yokePull))
            # print("*Parameter,Target,Current,Yoke Steer:     " + "Roll, " + str(self.target_roll) + "," +  str(self.roll)+ "," + str(yokeSteer))
            # print("*Parameter,Target,Current,Rudder:         " + "Heading, " + str(self.target_heading) + "," +  str(self.heading)+ "," + str(rudder))
            # print("*Parameter,Target,Current,Throttle:       " + "Descent Rate, " + str(self.target_descent_rate) + "," +  str(self.descent_rate)+ "," + str(throttle))
            parameter = ["Airspeed","Roll","Heading","Descent Rate","Altitude","Pitch", "Wheel Speed", "Wheel Weight"]
            target =  [str(round(self.target_airspeed)),str(round(self.target_roll)),str(round(self.target_heading,3)),str(round(self.target_descent_rate,3)),str(round(self.altitude,3)),str(round(self.target_pitch,3)),0, 0]
            current = [str(round(self.airspeed,3)),str(round(self.roll,3)),str(round(self.heading,3)),str(round(self.descent_rate,3)),str(round(self.altitude,3)),str(round(self.pitch,3)),str(round(self.wheelSpeed,3)),str(round(self.wheelWeight,3))]
            controlVal = [str(round(yokePull,3)),str(round(yokeSteer,3)),str(round(rudder,3)),str(round(throttle,3)),str(round(self.altitude,3)),str(self.flare),str(round(self.brakes,3)),str(round(self.wheelWeight,3))]

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
        if(self.flare): 
             yoke_pull, self.integral_airspeed = self.proportionalIntegralControl(1,self.pitch, 
                                                                             self.target_pitch, 
                                                                             self.integral_pitch, 
                                                                             scaleFactor.SCALEYOKEPULL)
             print("Flare Control Scheme Active")
        
        if(self.flare == False):
            yoke_pull, self.integral_airspeed = self.proportionalIntegralControl(1,self.airspeed, 
                                                                             self.target_airspeed, 
                                                                             self.integral_airspeed, 
                                                                             scaleFactor.SCALEYOKEPULL)

        yoke_steer, self.integral_roll = self.proportionalIntegralControl(0,self.roll, self.target_roll, self.integral_roll,scaleFactor.SCALEYOKESTEER)
        rudder, self.integral_heading = self.proportionalIntegralControl(0,self.heading, self.target_heading, self.integral_heading,scaleFactor.SCALERUDDER)
        throttle, self.integral_descent_rate = self.proportionalIntegralControl(0,self.descent_rate, self.target_descent_rate, self.integral_descent_rate,scaleFactor.SCALETHROTTLE)
        ### 1. For Calculated Yoke and Throttle Values 
        #Invert Throttle Control & divide by 5 to scale
        throttle = -throttle
        throttle = throttle/5
        #Invert Yoke Pull & divide by 5 to scale
        yoke_pull = -yoke_pull
        yoke_pull = yoke_pull/5
        ## 2. For Constant Yoke and Throttle Values      
        # Constant yoke "back pressure" equal to 20% of total travel distance
        if(self.flare == False):
            yoke_pull = 0.35
            throttle = 0.15
        if(self.flare == True):
            yoke_pull = -yoke_pull
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


        #Switch Target for Pitch to Local Pitch Axis (ex. +10 Degrees nose up)
        
        self.printControls(1,0,yoke_pull,yoke_steer,rudder,throttle)
        # Send all controls simultaneously to X-Plane
        self.send_controls_to_xplane(yoke_pull, yoke_steer, 0, throttle)


    def send_controls_to_xplane(self, yoke_pull, yoke_steer, rudder, throttle):
        """
        Sends all control inputs to X-Plane using XPlaneConnect
        """
        # Send yoke pull, yoke steer, rudder, and throttle simultaneously
        # print("Yoke Pull:" + str(yoke_pull))

        #Set the Trim to a value that allows the aircraft to osscilate around the target airspeed
        if(self.flare == False):
            trimdref = "sim/flightmodel/controls/elv_trim"
            trim = -0.3
            self.client.sendDREF(trimdref,trim)

        if(self.flare):
            trimdref = "sim/flightmodel/controls/elv_trim"
            trim = 0
            self.client.sendDREF(trimdref,trim)

        if(self.rollOut):
            #Cut the Throttle
            throttle = 0
            #Release Yoke Back Pressure (Pitch Up Pressure from the flare maneuver)
            yoke_pull = 0
            #Hit the Brakes
            brakedref = "sim/cockpit2/controls/parking_brake_ratio"
            brake = 1
            self.client.sendDREF(brakedref,brake)

        self.client.sendCTRL([yoke_pull, yoke_steer, rudder, throttle, -998, -998])  # Control inputs: [yoke_pull, yoke_steer, rudder, throttle]



    # Update the model's DM based on X-Plane data
    def update_aircraft_state(self):
        # print("In aircraft state")
        # print("Entered Update Aircraft State")
        # Retrieve current data from X-Plane
        airspeed = self.client.getDREF("sim/cockpit2/gauges/indicators/airspeed_kts_pilot")
        roll = self.client.getDREF("sim/cockpit2/gauges/indicators/roll_AHARS_deg_pilot")
        heading = self.client.getDREF("sim/cockpit2/gauges/indicators/heading_AHARS_deg_mag_pilot")
        descent_rate = self.client.getDREF("sim/flightmodel/position/vh_ind_fpm")
        altitude = self.client.getDREF("sim/cockpit2/gauges/indicators/altitude_ft_pilot")
        pitch = self.client.getDREF("sim/flightmodel/position/true_theta")


        wheelS = self.client.getDREF("sim/flightmodel2/gear/tire_rotation_speed_rad_sec")
        wheelW = self.client.getDREF("sim/flightmodel/parts/tire_vrt_def_veh")

       
        
        # Update the model's declarative memory
        # model.declarative_memory["airspeed"] = airspeed
        # model.declarative_memory["roll"] = roll
        # model.declarative_memory["heading"] = heading
        # model.declarative_memory["descent_rate"] = descent_rate
       
        self.airspeed = airspeed[0]
        self.roll = roll[0]
        self.heading = heading[0]
        self.descent_rate = descent_rate[0]
        self.altitude = altitude[0]
        self.pitch = pitch[0]
        self.wheelSpeed = wheelS[0]
        self.wheelWeight = wheelW[0]

        ##Phase Change Indicator
        # wheelWeight = self.client.getDREF("sim/flightmodel/parts/tire_vrt_def_veh") #Strut deflection, Weight on the wheels
        # wheelRate = self.client.getDREF("sim/flightmodel2/gear/tire_rotation_speed_rad_sec") #Wheel Rotation Rate 

        if(self.altitude <= 50):
            self.flare = True
            self.Ki = 0.01  ## Increase Control Authority to compensate for decreasing airspeed
            print("Altitude < 500; Flare Set True")

        if(self.wheelWeight > 0 and self.wheelSpeed > 0):
            #Two Parameters to Confirm Touchdown and wheel contact
            # "sim/flightmodel/parts/tire_vrt_def_veh" #Gear Strut Deflection (Weight on wheels)
            # "sim/flightmodel2/gear/tire_rotation_rate_rad_sec" #Tire Rotation Rate 
            self.rollOut = True
            print("Hit the brakes")

        
  


    # def rules(self):
    #     """
    #     Define the rules for descent control using proportional-integral control for all controls at once.
    #     """
    #     return [
    #         # Rule to adjust all controls simultaneously based on PI control for each parameter
    #         pyactr.Production(
    #             condition=pyactr.Condition("airspeed", "airspeed") &
    #                       pyactr.Condition("roll", "roll") &
    #                       pyactr.Condition("heading", "heading") &
    #                       pyactr.Condition("descent_rate", "descent_rate") &
    #                       pyactr.Condition("target_airspeed", "target_airspeed") &
    #                       pyactr.Condition("target_roll", "target_roll") &
    #                       pyactr.Condition("target_heading", "target_heading") &
    #                       pyactr.Condition("target_descent_rate", "target_descent_rate"),
    #             action=self.update_controls_simultaneously(),
    #         ),
    #     ]


# # Function to get the current dataref value for a given parameter
# def getDref(parameter_name):
#     # Depending on the parameter name, you would query X-Plane datarefs
#     if parameter_name == "Airspeed":
#         # Get airspeed dataref
#         return client.getData([DATAREF_AIRSPEED])
#     elif parameter_name == "Roll":
#         # Get roll angle dataref
#         return client.getData([DATAREF_ROLL])
#     elif parameter_name == "Hdg":
#         # Get heading dataref
#         return client.getData([DATAREF_HEADING])
#     elif parameter_name == "DescentRate":
#         # Get descent rate dataref
#         return client.getData([DATAREF_DESCENT_RATE])


