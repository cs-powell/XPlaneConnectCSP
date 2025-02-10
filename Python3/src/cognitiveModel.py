import pyactr
# from XPlaneConnect import *
import xpc

# Initialize XPlaneConnect client

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
        self.airspeed = 100
        self.roll = 0
        self.heading = 0
        self.descent_rate = 500
        self.target_airspeed = 80
        self.target_roll = 0
        self.target_heading = 0
        self.target_descent_rate = 500
   
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

        # Integral gains (tune these values for performance)
        self.Kp = 0.1  # Proportional gain
        self.Ki = 0.01  # Integral gain

    def proportionalIntegralControl(self, current, target, integral_error):
        """
        Proportional-Integral control rule implementation for multiple parameters.
        """
        # Calculate the error (current - target)
        error = target - current
        
        # Update the integral of the error
        integral_error += error

        # Calculate the control value using the PI formula
        control_value = (self.Kp * error) + (self.Ki * integral_error)
        
        return control_value, integral_error  # Return control value and updated integral error

    def update_controls_simultaneously(self):  
        """
        Update all controls at the same time by calculating control values for each parameter.
        """

        print("Entered Update Controls Simultaneously")
        # Compute control values for all parameters (yoke pull, yoke steer, rudder, throttle)
        yoke_pull, self.integral_airspeed = self.proportionalIntegralControl(self.airspeed, self.target_airspeed, self.integral_airspeed)
        yoke_steer, self.integral_roll = self.proportionalIntegralControl(self.roll, self.target_roll, self.integral_roll)
        rudder, self.integral_heading = self.proportionalIntegralControl(self.heading, self.target_heading, self.integral_heading)
        throttle, self.integral_descent_rate = self.proportionalIntegralControl(self.descent_rate, self.target_descent_rate, self.integral_descent_rate)

        # Send all controls simultaneously to X-Plane
        self.send_controls_to_xplane(yoke_pull, yoke_steer, rudder, throttle)

    def send_controls_to_xplane(self, yoke_pull, yoke_steer, rudder, throttle):
        """
        Sends all control inputs to X-Plane using XPlaneConnect
        """
        # Send yoke pull, yoke steer, rudder, and throttle simultaneously
        self.client.sendCTRL([yoke_pull, yoke_steer, rudder, throttle, -998, -998])  # Control inputs: [yoke_pull, yoke_steer, rudder, throttle]



    # Update the model's DM based on X-Plane data
    def update_aircraft_state(self):
        print("Entered Update Aircraft State")
        # Retrieve current data from X-Plane
        airspeed = self.client.getDREF("sim/cockpit2/gauges/indicators/airspeed_kts_pilot")
        roll = self.client.getDREF("sim/cockpit2/gauges/indicators/roll_AHARS_deg_pilot")
        heading = self.client.getDREF("sim/cockpit2/gauges/indicators/heading_AHARS_deg_mag_pilot")
        descent_rate = self.client.getDREF("sim/flightmodel/position/vh_ind_fpm")
        
        # Update the model's declarative memory
        # model.declarative_memory["airspeed"] = airspeed
        # model.declarative_memory["roll"] = roll
        # model.declarative_memory["heading"] = heading
        # model.declarative_memory["descent_rate"] = descent_rate
       
        self.airspeed = airspeed[0]
        self.roll = roll[0]
        self.heading = heading[0]
        self.descent_rate = descent_rate[0]
        print(self.airspeed)
        print(self.roll)
        print(self.heading)
        print(self.descent_rate)


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



