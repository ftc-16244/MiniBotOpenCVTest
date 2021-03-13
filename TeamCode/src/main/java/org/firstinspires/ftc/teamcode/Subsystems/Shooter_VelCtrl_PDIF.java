package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;

import static java.lang.Thread.sleep;

public class Shooter_VelCtrl_PDIF {
    // Define hardware objects
    public DcMotorEx  shooterleft=null;
    public DcMotorEx  shooterright=null;
    public Servo    leftFlipper=null;
    public Servo    rightFlipper=null;
    public Servo    stacker=null;

    //
    private double kp= 30;
    private double ki = 0.2;
    private double kd = 6;
    private double kf = 13.5;//13.0;


    PIDFCoefficients pidfCoefficients = new PIDFCoefficients(kp,ki,kd,kf);

    //Constants for stacker servos
    private static final double leftUp = 0.70; // .75 a little shy but ok due to hitting bolt // was 0.75
    private static final double leftBack = .38; //good at 0.4;
    private static final double rightUp = (1-leftUp);
    private static final double rightBack = (1-leftBack);
    private static final double flippercenter = 0.5;
    private static final double stackerReload = 0.44; // 0.42 causes rings to slide forward too much
    private static final double stackerShoot = 0.545; // 0.55 is a bit high
    private static final double stackerDumpOut = .95; // empties on the field is there is a jam
    //private static final double stackerShootAutoOnly = 0.54; // slightly different in Auto so Teleop stays the same
    private static final double stacketMidLoad = .44; // tips stacker back so it loads better

    // Power Shoot speeds for Velocity Control per REV docs suggestion
    private static  final double RIGHT_SHOOTER_GEAR_RATIO = 2.89; // 3:1 Ultra Planetary

    private static final int    TICKS_PER_MTR_REV = 28; //UltraPlanetary 1:1 with 90 mm shooter wheels
    private static final int    LEFT_SHOOTER_SPEED_PS = 2350; // RPM
    private static final int    RIGHT_SHOOTER_SPEED_PS = 1600; //RPM
    private static final double LEFT_SHOOTER_TICKS_PERSEC_PS = TICKS_PER_MTR_REV *LEFT_SHOOTER_SPEED_PS/60; //ticks per second
    private static final double RIGHT_SHOOTER_TICKS_PERSEC_PS = TICKS_PER_MTR_REV *RIGHT_SHOOTER_SPEED_PS*RIGHT_SHOOTER_GEAR_RATIO/60; //ticks per second
    // High Goal Speeds (AUTO) - Velocity Control
    private static final int    LEFT_SHOOTER_SPEED_HG = 2600; // bumped to 2750 from 2700 to avovid low shots
    private static final int    RIGHT_SHOOTER_SPEED_HG = 1800; // RPM
    private static final double    LEFT_SHOOTER_TICKS_PERSEC_HG = TICKS_PER_MTR_REV *LEFT_SHOOTER_SPEED_HG/60;
    private static final double    RIGHT_SHOOTER_TICKS_PERSEC_HG = TICKS_PER_MTR_REV *RIGHT_SHOOTER_SPEED_HG*RIGHT_SHOOTER_GEAR_RATIO/60;

    //High Goal Speeds Teleop should be pretty close to the auto speeds but can make slight changges if need be
    private static final int    LEFT_SHOOTER_SPEED_HG_TELE = 2600; // was 2100/2400 works well
    private static final int    RIGHT_SHOOTER_SPEED_HG_TELE = 1700; // RPM
    private static final double    LEFT_SHOOTER_TICKS_PERSEC_HG_TELE = TICKS_PER_MTR_REV *LEFT_SHOOTER_SPEED_HG_TELE/60;
    private static final double    RIGHT_SHOOTER_TICKS_PERSEC_HG_TELE = TICKS_PER_MTR_REV *RIGHT_SHOOTER_SPEED_HG_TELE*RIGHT_SHOOTER_GEAR_RATIO/60;

    public void init(HardwareMap hwMap)  {
        shooterleft     = hwMap.get(DcMotorEx.class,"LeftShooter");
        shooterright    = hwMap.get(DcMotorEx.class,"RightShooter");
        leftFlipper     = hwMap.get(Servo.class, "Left_Flipper");
        rightFlipper    = hwMap.get(Servo.class, "Right_Flipper");
        stacker         = hwMap.get(Servo.class, "Stacker");

        shooterleft.setDirection(DcMotor.Direction.REVERSE);
        shooterright.setDirection(DcMotor.Direction.FORWARD);
        shooterleft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        shooterright.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        shooterleft.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, pidfCoefficients);

        // make sure flippers are back and stacker it ready to load
        shooterReload();


    // Single Function Methods
    }
    public void shootMiddleGoal() {
        shooterleft.setVelocity(LEFT_SHOOTER_TICKS_PERSEC_PS);
        shooterright.setVelocity(RIGHT_SHOOTER_TICKS_PERSEC_PS);
    }
    public void shootHighGoal() {
        shooterleft.setVelocity(LEFT_SHOOTER_TICKS_PERSEC_HG);
        shooterright.setVelocity(RIGHT_SHOOTER_TICKS_PERSEC_HG);
    }

    public void shootHighGoalTeleop() {
        shooterleft.setVelocity(LEFT_SHOOTER_TICKS_PERSEC_HG_TELE);
        shooterright.setVelocity(RIGHT_SHOOTER_TICKS_PERSEC_HG_TELE);
    }


    public void shooterOff() {
            shooterleft.setPower(0);
            shooterright.setPower(0);
    }

    public void stackerMoveToShoot() {
        stacker.setPosition(stackerShoot);
    }

    public void stackerMoveToShootInAutoOnly() {
        stacker.setPosition(stackerShoot);
    }

    public void stackerMoveToReload() {

        stacker.setPosition(stackerReload);
    }

    public void stackerMoveToMidLoad() {
        stacker.setPosition(stacketMidLoad);

    }

    public void stackerMoveToDump() {
        stacker.setPosition(stackerDumpOut);

    }

    public void flipperCalibrateinCenter() {
        leftFlipper.setPosition(flippercenter);
        rightFlipper.setPosition(flippercenter);
    }
    public void flipperForward() {
        leftFlipper.setPosition(leftUp);
        rightFlipper.setPosition(rightUp);

    }
    public void flipperBackward() {
        leftFlipper.setPosition(leftBack);
        rightFlipper.setPosition(rightBack);
    }


    // Multi Function or Compound Methods
    // These will be called by the opModes
    public void shootonePowerShots() {
        shootMiddleGoal();
        stackerMoveToShoot();
        flipperBackward();

    }
    // this actually should be called shooterReady or Rings_Ready_to_Launch
    public void shootOneRingHigh() {
        shootHighGoalTeleop(); // sets motors for high goal
        stackerMoveToShoot(); // raises ring stacker
        flipperBackward(); // makes sure flippers are behind rings
        //telemetry.addData("Left Shooter Target",LEFT_SHOOTER_SPEED_HG);
        //telemetry.addData("Right Shooter Target",RIGHT_SHOOTER_SPEED_HG);
    }

    // this actually should be called RingsReadytoLoad
    public void shooterReload() {
        stackerMoveToMidLoad(); //
        flipperBackward(); // move flippers back
        shooterOff(); // turn off shooters

        }
    // Used to automate ring flippers in auto and pass the desired cycles
    // THis is set for high goals. Shooter speed is not passed into it.

    public void shoot_N_rings(int rings) throws InterruptedException {
        int ShotCount = 0;
        while (ShotCount<rings)  {

                shootOneRingHigh(); // this is only used in auto due to different stacker position
                sleep(450);
                flipperForward();
                sleep(450 );
                flipperBackward();
                ShotCount++;



        }
        shooterOff(); // turn off when we exit the loop

    }
}












