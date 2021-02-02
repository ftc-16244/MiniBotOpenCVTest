package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import static java.lang.Thread.sleep;

public class Wobblegoal {
    //Define Hardware Objects
    public DcMotor  WobbleLift          =   null;
    public DcMotor  WobbleExtend        =   null; // no longer used
    public Servo    WobbleGrip          =   null;
    public Servo    WobbleBaseClamp     =   null;
    public Servo    WobbleWrist         =   null; // replaces REV Core Hex Motor to extend arm

    //Constants Lift

    private static final double     LIFTSPEED       =   0.78;

    private static final double     LIFTUP          =   13 ; //Number is in inches
    private static final double     LIFTDOWN        =   0; //To make sure it goes all the way down
    private static final int        LIFTPARTIAL        = 8;

    private static final double     TICKS_PER_LIFT_IN = 76; // determined experimentally
    private static final int        LIFT_HEIGHT_HIGH = (int) (LIFTUP * TICKS_PER_LIFT_IN); // converts to ticks
    private static final int        LIFT_HEIGHT_LOW = (int) (LIFTDOWN * TICKS_PER_LIFT_IN); // converts to ticks

    //Constants Gripper
    private static final double     GRIPPERINIT         =   0.4;// 0.35 for V3
    private static final double     GRIPPEROPEN         =   0.78;//0.3 for V3
    private static final double     GRIPPERSUPEROPEN    =   0.01;//0.01 for V3
    private static final double     GRIPPERCLOSE        =   0.22;// 0.8 for V3
    private static final double     GRIPPERPARTOPEN     =   0.55;// 0.8 for V3
    //Constants Arm - obsolete
    private static final int        ARMEXTEND           =   35; //32-33 is good ticks
    private static final int        ARMCONTRACT         =   0; // ticks
    private static final int        ARMCARRY            =   60;
    private static final double     EXTENDSPEED         =   .5;
    //Constants Wobble Base Clam
    public static final double      BASECLAMPUP         =   0.80;
    public static final double      BASECLAMPDOWN       =   0.4;
    //Constants Wobble Wrist - Replaces Arm
    public static final double      WRISTSTART          =   .36 ;
    public static final double      WRISTUP             =   0.45 ;
    public static final double      WRISTDOWN           =   0.65;
    public static final double      WRISTHALFWAY        =   0.50;

    public void init(HardwareMap hwMap)  {
        WobbleLift=hwMap.get(DcMotor.class,"LiftWobble");
        WobbleExtend=hwMap.get(DcMotor.class,"ArmExtend");
        WobbleGrip=hwMap.get(Servo.class,"Gripper");
        WobbleBaseClamp=hwMap.get(Servo.class,"Base Clamp");
        WobbleWrist = hwMap.get(Servo.class,"Wrist");

        //Positive=up and Negative=down
        WobbleLift.setDirection(DcMotor.Direction.FORWARD);
        WobbleExtend.setDirection(DcMotorSimple.Direction.REVERSE);
        WobbleLift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        WobbleExtend.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        WobbleLift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        WobbleExtend.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        WobbleGrip.setPosition(GRIPPERINIT);
        WobbleBaseClamp.setPosition(BASECLAMPUP);
        WobbleWrist.setPosition(WRISTSTART);

    }

    //// Single operation methods - see below for methods to be called in Opmodes
    public void LiftRise() {
        WobbleLift.setTargetPosition(LIFT_HEIGHT_HIGH);// value is in ticks from above calculation
        WobbleLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        WobbleLift.setPower(LIFTSPEED);
    }
    public void LiftLower() {
        WobbleLift.setTargetPosition(LIFT_HEIGHT_LOW); // this one is just zero for now
        WobbleLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        WobbleLift.setPower(LIFTSPEED);
    }


    public void GripperOpen()  {

        WobbleGrip.setPosition(GRIPPEROPEN);
    }

    public void GripperPartOpen() {
        WobbleGrip.setPosition(GRIPPERPARTOPEN);
    }

    public void GripperSuperOpen()  {

        WobbleGrip.setPosition(GRIPPERSUPEROPEN);
    }
    public void GripperClose() {

        WobbleGrip.setPosition(GRIPPERCLOSE);
    }

    // Arm was removed prior to meet 3
    public void ArmExtend() {

        WobbleExtend.setTargetPosition(ARMEXTEND);
        WobbleExtend.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        WobbleExtend.setPower(EXTENDSPEED);

    }
    public void ArmContract() {
        raiseWobbleClamp();
        WobbleExtend.setTargetPosition(ARMCONTRACT);
        WobbleExtend.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        WobbleExtend.setPower(EXTENDSPEED);
        GripperOpen();
    }
    public void ArmCarryWobble() {
        raiseWobbleClamp();
        WobbleExtend.setTargetPosition(ARMCARRY);
        WobbleExtend.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        WobbleExtend.setPower(EXTENDSPEED);
    }

    public void raiseWobbleClamp() {

        WobbleBaseClamp.setPosition(BASECLAMPUP);
    }

    public void lowerWobbleClamp() {

        WobbleBaseClamp.setPosition(BASECLAMPDOWN);
    }

    public void wobbleWristDown()  {

        WobbleWrist.setPosition(WRISTDOWN);
    }

    public void wobbleWristUp() {

        WobbleWrist.setPosition(WRISTUP);
    }
    public void wobbleWristStart() {

        WobbleWrist.setPosition(WRISTSTART);

    }

    public void wobbleWristHalfWay() {

        WobbleWrist.setPosition(WRISTHALFWAY);
    }

    ///// Multi Function methods to be called by the Opmodes

    public void resetWobble() {
        GripperClose();
        ArmContract();
        LiftLower();
    }

    public void readyToGrabWobble() {
        //LiftRise();
        //ArmExtend();
        GripperOpen();
        LiftLower();

        lowerWobbleClamp();
    }

    public void grabAndLift() {
        GripperClose();
        //LiftRise();
        raiseWobbleClamp();
    }

    public void lowerAndRelease() {
        LiftLower();
        GripperOpen();
    }
}

