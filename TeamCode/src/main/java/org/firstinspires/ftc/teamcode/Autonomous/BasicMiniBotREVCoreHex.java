package org.firstinspires.ftc.teamcode.Autonomous;

import android.view.View;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Subsystems.SideServo;

import org.firstinspires.ftc.teamcode.Subsystems.Two_Motor_REV_Core_HEX_Drivetrain;

import static org.firstinspires.ftc.teamcode.Subsystems.Two_Motor_REV_Core_HEX_Drivetrain.COUNTS_PER_INCH;
@Autonomous(name="Basic REV Core Hex OpMode", group="Au@to")
@Disabled

// This opmode is meant to be extend in teleop and autonomus opmodes.
// This enables code reuse and a single source to edit the common methods and constants.
// Since the REVExpansion/ Control Hub is mounted at an odd angle the IMU is not applied here

public class BasicMiniBotREVCoreHex extends LinearOpMode {
    /* Declare OpMode members. */
    public Two_Motor_REV_Core_HEX_Drivetrain drivetrain  = new Two_Motor_REV_Core_HEX_Drivetrain();   // Use subsystem Drivetrain
    public SideServo sideServo = new SideServo();

    // Timers and time limits for each timer
    public ElapsedTime          PIDtimer    = new ElapsedTime(); // PID loop timer
    //public ElapsedTime          drivetime   = new ElapsedTime(); // timeout timer for driving
    public ElapsedTime          runtime    = new ElapsedTime(); //
    NormalizedColorSensor colorSensor; // copied in from color sensor sample code
    View relativeLayout; // copied in from color sensor sample code

    public static double        autoRingCollectTimeAllowed = 1.5; // time allowed to let the single ring to get picked up

    // These constants define the desired driving/control characteristics
    // The can/should be tweaked to suit the specific robot drive train.
    public static final double     DRIVE_SPEED             = 0.60;     // Nominal speed for better accuracy.





    @Override
    public void runOpMode() {
        // If these next two lines are skipped, a null exception error for missing hardware will occur
        drivetrain.init(hardwareMap, true); // initialize drivetrain
        sideServo.init(hardwareMap); // initialize the servo
        // center the servo on init
        sideServo.moveServoCenter();

        telemetry.addData("Mode", "waiting for start");
        telemetry.addData("imu calib status", drivetrain.imu.getCalibrationStatus().toString());
        /** Wait for the game to begin */

        telemetry.update();

        /////////////////////////////////////////////////////////////////////////////////////////////
        waitForStart();
        ////////////////////////////////////////////////////////////////////////////////////////////



        //drivetime.reset(); // reset because time starts when TF starts and time is up before we can call gyroDrive
        encoderDrive(DRIVE_SPEED, 24,24, 10);
        encoderDrive(DRIVE_SPEED, -24,-24, 10);

        telemetry.addData("Path", "Complete");
        telemetry.update();
    }


    public void encoderDrive(double speed,
                             double leftInches, double rightInches,
                             double timeoutS) {
        int newLeftFrontTarget;
        int newRightRearTarget;
        int newRightFrontTarget;
        int newLeftRearTarget;

        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            // Note Counts per inch is imported from the REV Core HEX subsystem
            newLeftFrontTarget = drivetrain.leftFront.getCurrentPosition() + (int)(leftInches * COUNTS_PER_INCH);

            newRightFrontTarget = drivetrain.leftFront.getCurrentPosition() + (int)(leftInches * COUNTS_PER_INCH);

            drivetrain.leftFront.setTargetPosition(newLeftFrontTarget);

            drivetrain.rightFront.setTargetPosition(newRightFrontTarget);


            // Turn On RUN_TO_POSITION
            drivetrain.leftFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            drivetrain.rightFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);


            // reset the timeout time and start motion.
            runtime.reset();
            drivetrain.leftFront.setPower(Math.abs(speed));

            drivetrain.rightFront.setPower(Math.abs(speed));


            // keep looping while we are still active, and there is time left, and both motors are running.
            // Note: We use (isBusy() && isBusy()) in the loop test, which means that when EITHER motor hits
            // its target position, the motion will stop.  This is "safer" in the event that the robot will
            // always end the motion as soon as possible.
            // However, if you require that BOTH motors have finished their moves before the robot continues
            // onto the next step, use (isBusy() || isBusy()) in the loop test.
            while (opModeIsActive() &&
                    (runtime.seconds() < timeoutS) &&
                    (drivetrain.leftFront.isBusy() && drivetrain.rightFront.isBusy() )) {

                // Display it for the driver.
                telemetry.addData("Path1",  "Running to %7d :%7d", newLeftFrontTarget,  newRightFrontTarget);
                telemetry.addData("Path2",  "Running at %7d :%7d",
                        drivetrain.leftFront.getCurrentPosition(),

                        drivetrain.rightFront.getCurrentPosition());


                telemetry.update();
            }

            // Stop all motion;
            drivetrain.leftFront.setPower(0);

            drivetrain.rightFront.setPower(0);


            // Turn off RUN_TO_POSITION
            drivetrain.leftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            drivetrain.rightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);



        }

        sideServo.moveServoRight();
        sleep(500);
        sideServo.moveServoLeft();
    }



    



}
