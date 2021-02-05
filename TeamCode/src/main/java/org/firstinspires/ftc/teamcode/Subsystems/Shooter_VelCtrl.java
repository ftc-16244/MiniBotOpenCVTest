package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import static java.lang.Thread.sleep;

public class Shooter_VelCtrl {
    // Define hardware objects
    public DcMotorEx  shooterleft=null;
    public DcMotorEx  shooterright=null;
    public Servo    leftFlipper=null;
    public Servo    rightFlipper=null;
    public Servo    stacker=null;

    //
    //Constants for shooter motors
    private static final double ShooterSpeedfastleft=.58; // 0.58 reset after meet 1 to improve consistiencey
    private static final double ShooterSpeedfastright=.30; // 0.51
    //We are changing the speeds to see which speeds make the first ring in
    private static final double shooterSpeedslowleft=.52;
    private static final double shooterSpeedslowright=.30;
    private static final double jamClear=-.35;
    //Constants for stacker servos
    private static final double leftUp = 0.70; // .75 a little shy but ok due to hitting bolt // was 0.75
    private static final double leftBack = .4; //good at 0.4;
    private static final double rightUp = (1-leftUp);
    private static final double rightBack = (1-leftBack);
    private static final double flippercenter = 0.5;
    private static final double stackerReload = 0.44; // 0.42 causes rings to slide forward too much
    private static final double stackerShoot = 0.545; // 0.55 is a bit high
    //private static final double stackerShootAutoOnly = 0.54; // slightly different in Auto so Teleop stays the same
    private static final double stacketMidLoad = .44; // tips stacker back so it loads better

    // Power Shoot speeds
    private static final int    TICKS_PER_MTR_REV = 28;
    private static final int    LEFT_SHOOTER_SPEED_PS = 2550; // RPM
    private static final int    RIGHT_SHOOTER_SPEED_PS = 1600; //RPM
    private static final double LEFT_SHOOTER_TICKS_PERSEC_PS = TICKS_PER_MTR_REV *LEFT_SHOOTER_SPEED_PS/60; //ticks per second
    private static final double RIGHT_SHOOTER_TICKS_PERSEC_PS = TICKS_PER_MTR_REV *RIGHT_SHOOTER_SPEED_PS/60; //ticks per second
    // Hight Goal speeds
    private static final int    LEFT_SHOOTER_SPEED_HG = 2600; // RPM
    private static final int    RIGHT_SHOOTER_SPEED_HG = 1750; // RPM
    private static final double    LEFT_SHOOTER_TICKS_PERSEC_HG = TICKS_PER_MTR_REV *LEFT_SHOOTER_SPEED_HG/60;
    private static final double    RIGHT_SHOOTER_TICKS_PERSEC_HG = TICKS_PER_MTR_REV *RIGHT_SHOOTER_SPEED_HG/60;




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


    // Multi Function Methods that combine several simple methods together
    public void shootonePowerShots() {
        shootMiddleGoal();
        stackerMoveToShoot();
        flipperBackward();

    }

    public void shootOneRingHigh() {
        shootHighGoal();
        stackerMoveToShootInAutoOnly();
        flipperBackward();

    }


    public void shooterReload() {
        stackerMoveToMidLoad(); //
        flipperBackward(); // move flippers back
        shooterOff(); // turn off shooters

        }
    // not used
    public void jamClear() {
        shooterleft.setPower(jamClear);
        shooterright.setPower(jamClear);


    }


    public void shoot_N_rings(int rings) throws InterruptedException {
        int ShotCount = 0;
        while (ShotCount<rings)  {

                shootOneRingHigh(); // this is only used in auto due to different stacker position
                sleep(400);
                flipperForward();
                sleep(400);
                flipperBackward();
                ShotCount++;



        }
        shooterOff(); // turn off when we exit the loop

    }
}












