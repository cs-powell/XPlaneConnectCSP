from time import sleep
import xpc
import pyactr as actr #Importing ACT-R python edition

def ex():
    ## ACT-R Setup
    playing_memory = actr.ACTRModel()

    actr.chunktype("pitch", "up, throttle")
    initial_chunk = actr.makechunk(typename="pitch", up="20")
    ## Define new chunk type(s)
    # 
    # Control Updates
    #   Rudder = #### (0.5) 
    #   Elevator = #### (0)
    #   Aileron = #### 
    #   Throttle = ##### 
    # Rules to define How much to change based on current parameters
    # Within a given rule, multiple values can be updated (which values depend on 
    # acceptable ranges/current envrionment state/current goal state(pre-touchdown (cares about altitude) vs. 
    # after touchdown braking (doesn't care about altitude)))

    actr.chunktype("Control Update", "rudder, elevator, aileron, throttle")
    ##Use goal states to frame acceptable ranges for parameters (descent vs touchdown vs level flight etc etc.)
    ## Divide landing task into subproblems
        ## Level Flight
        # Initial Descent
        # .......
        # and/or tracking landing signal (ILS, RNAV, etc.) [Quantitative measure]



        ## Environment Conditions: Calm...very calm
        
        ##First Focus: Begin Simulation with the tedious button pressing all complete -- [go the route of Dead Reckoning] 
        # (i.e. focus on command and control not beuracracy)
        # Define acceptable range of parameters for each stage
        # Stabilized descent (Maintaining a given descent rate in FPM -- Maintaining Speed, loosing altitude)
            # -->  Rudder
            # -->  Aileron
            # -->  Elevator
            # -->  Throttle
            # -->> Altitude (Throtte)
            # -->> Airspeed (Pitch = Elevator)
            # -->> Pitch (Elevator)
            # -->>>>>>>>>> Transition Point
                # Initial Parameter Above a certain altitude (50ish feet)

        # Search for established equations & control schema/relationships for the controls and/or environmental stimuli

        
        # Round out/ Flare & Crosswind Corections(i.e. don't land sideways) [Arresting the descent, maintaing wings level,
        # Letting the speed bleed off while maintaing altitude]
        # -->
        # Touchdown 
        # -->
        # Rollout & Braking & Centerline tracking
        # -->

    goal = playing_memory.set_goal("goal")
    goal.add(initial_chunk)
    print(goal)

    playing_memory.productionstring(name="startpitch", string="""
    =goal>
    isa  pitch
    up 20
    throttle None
    ?manual>
    state free
    ==>
    =goal>
    isa pitch
    throttle 100""") #this rule will be modified later
   

    playing_memory.productionstring(name="modifyPitch", string="""
    =goal>
                                    
    isa  pitch
    up 20
    throttle 100
    ==>
                                    
    +manual>
    isa _manual
    #                                 

    cmd press_key
    key 1
                                    

    #
    =goal>
                                    
    isa pitch
    throttle None""") #this rule works correctly

    simulation_game = playing_memory.simulation()
    simulation_game.run()


    # print(goal)
    # final_chunk = goal.pop()
    # print(final_chunk)

    # print("X-Plane Connect example script")
    # print("Setting up simulation")
    # with xpc.XPlaneConnect() as client:
    #     # Verify connection
    #     try:
    #         # If X-Plane does not respond to the request, a timeout error
    #         # will be raised.
    #         client.getDREF("sim/test/test_float")
    #     except:
    #         print("Error establishing connection to X-Plane.")
    #         print("Exiting...")
    #         return

    #     # Set position of the player aircraft
    #     print("Setting position")
    #     #       Lat     Lon         Alt   Pitch Roll Yaw Gear
    #     posi = [37.524, -122.06899, 2500, 0,    0,   0,  1]
    #     client.sendPOSI(posi)
        
    #     # Set position of a non-player aircraft
    #     print("Setting NPC position")
    #     #       Lat       Lon         Alt   Pitch Roll Yaw Gear
    #     posi = [37.52465, -122.06899, 2500, 0,    20,   0,  1]
    #     client.sendPOSI(posi, 1)

    #     # Set angle of attack, velocity, and orientation using the DATA command
    #     print("Setting orientation")
    #     data = [\
    #         [18,   0, -998,   0, -998, -998, -998, -998, -998],\
    #         [ 3, 130,  130, 130,  130, -998, -998, -998, -998],\
    #         [16,   0,    0,   0, -998, -998, -998, -998, -998]\
    #         ]
    #     client.sendDATA(data)

    #     # Set control surfaces and throttle of the player aircraft using sendCTRL
    #     print("Setting controls")
    #     ctrl = [0.0, 0.0, 0.0, 0.8]
    #     client.sendCTRL(ctrl)

    #     # Pause the sim
    #     print("Pausing")
    #     client.pauseSim(True)
    #     sleep(2)

    #     # Toggle pause state to resume
    #     print("Resuming")
    #     client.pauseSim(False)

    #     # Stow landing gear using a dataref
    #     print("Stowing gear")
    #     gear_dref = "sim/cockpit/switches/gear_handle_status"
    #     client.sendDREF(gear_dref, 0)

    #     # Let the sim run for a bit.
    #     sleep(4)

    #     # Make sure gear was stowed successfully
    #     gear_status = client.getDREF(gear_dref)
    #     if gear_status[0] == 0:
    #         print("Gear stowed")
    #     else:
    #         print("Error stowing gear")

    print("End of Python client example")
    input("Press any key to exit...")

if __name__ == "__main__":
    ex()