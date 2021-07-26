package org.firstinspires.ftc.teamcode.Subsystems;


import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

public class SideServo {
    // Define hardware objects
    // Servo is from the hardware class so we have to pick a new name for our instance of it.
    // "sideservo" is used becase that is where it is located. Usually you would call it by the
    // function it does like clamp, gripper etc.

    public Servo sideservo = null;

    // List constants

    public static final double SERVOLEFT = .1;
    public static final double SERVORIGHT = 0.9;
    public static final double SERVOCENTER = 0.5;

    private ElapsedTime runtime = new ElapsedTime();


    // Contructor for Servo

    public SideServo(){

    }

    // initialize servo and assign a name

    public void init(HardwareMap hwMap) {


        // initialize al the drive motors
        sideservo = hwMap.get(Servo.class, "Servo");


    }

    public void moveServoLeft(){
       sideservo.setPosition(SERVOLEFT);

    }

    public void moveServoRight(){
        sideservo.setPosition(SERVORIGHT);
    }
    public void moveServoCenter(){
        sideservo.setPosition(SERVOCENTER);
    }



    }



