package org.firstinspires.ftc.teamcode.Test;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Autonomous.BasicMiniBotMeccanum_Nevrest20;
import org.firstinspires.ftc.teamcode.Subsystems.Four_Motor_Minibot_Meccanum_Drivetrain_Nevrest20;
import org.firstinspires.ftc.teamcode.Subsystems.Two_Motor_Minibot_Tank_Drivetrain;

import static org.firstinspires.ftc.teamcode.Subsystems.Four_Motor_Minibot_Meccanum_Drivetrain_Nevrest20.COUNTS_PER_INCH;

@TeleOp(name="4 Motor Encoder Test Nevrest 20", group="Auto")

public class MotorEncoderTest extends BasicMiniBotMeccanum_Nevrest20 {
    public Four_Motor_Minibot_Meccanum_Drivetrain_Nevrest20 drivetrain  = new Four_Motor_Minibot_Meccanum_Drivetrain_Nevrest20(true);

    ElapsedTime runtime = new ElapsedTime();
    @Override
    public void runOpMode() {

        /*
         * Initialize the drive system variables.
         * The init() method of the hardware class does all the work here
         */
        drivetrain.init(hardwareMap);
        // uncomment for Mecanum #3 Only the motor directions are odd
        drivetrain.leftFront.setDirection(DcMotor.Direction.REVERSE);
        drivetrain.rightFront.setDirection(DcMotor.Direction.FORWARD);
        drivetrain.leftRear.setDirection(DcMotor.Direction.FORWARD);
        drivetrain.rightRear.setDirection(DcMotor.Direction.REVERSE);

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Status", "Resetting Encoders");
        telemetry.addData("Counts per Inch",Four_Motor_Minibot_Meccanum_Drivetrain_Nevrest20.COUNTS_PER_INCH ); //
        telemetry.update();

        drivetrain.leftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        drivetrain.rightRear.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        drivetrain.rightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        drivetrain.leftRear.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        drivetrain.leftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        drivetrain.rightRear.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        drivetrain.rightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        drivetrain.leftRear.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Send telemetry message to indicate successful Encoder reset
        telemetry.addData("Path0",  "Starting at %7d :%7d :%7d :%7d ",
                drivetrain.leftFront.getCurrentPosition(),
                drivetrain.rightRear.getCurrentPosition(),
                drivetrain.rightFront.getCurrentPosition(),
                drivetrain.leftRear.getCurrentPosition());

        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // Step through each leg of the path,
        // Note: Reverse movement is obtained by setting a negative distance (not speed)
        encoderDrive(DRIVE_SPEED,  24,  24, 15.0);  // S1: Forward 47 Inches with 5 Sec timeout
        //encoderDrive(TURN_SPEED,   12, -12, 4.0);  // S2: Turn Right 12 Inches with 4 Sec timeout
        encoderDrive(DRIVE_SPEED, -24, -24, 15.0);  // S3: Reverse 24 Inches with 4 Sec timeout


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
            newLeftFrontTarget = drivetrain.leftFront.getCurrentPosition() + (int)(leftInches * COUNTS_PER_INCH);
            newRightRearTarget = drivetrain.rightRear.getCurrentPosition() + (int)(rightInches * COUNTS_PER_INCH);
            newRightFrontTarget = drivetrain.leftFront.getCurrentPosition() + (int)(leftInches * COUNTS_PER_INCH);
            newLeftRearTarget = drivetrain.rightRear.getCurrentPosition() + (int)(rightInches * COUNTS_PER_INCH);


            drivetrain.leftFront.setTargetPosition(newLeftFrontTarget);
            drivetrain.rightRear.setTargetPosition(newRightRearTarget);
            drivetrain.rightFront.setTargetPosition(newRightFrontTarget);
            drivetrain.leftRear.setTargetPosition(newLeftRearTarget);

            // Turn On RUN_TO_POSITION
            drivetrain.leftFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            drivetrain.rightRear.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            drivetrain.rightFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            drivetrain.leftRear.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // reset the timeout time and start motion.
            runtime.reset();
            drivetrain.leftFront.setPower(Math.abs(speed));
            drivetrain.rightRear.setPower(Math.abs(speed));
            drivetrain.rightFront.setPower(Math.abs(speed));
            drivetrain.leftRear.setPower(Math.abs(speed));

            // keep looping while we are still active, and there is time left, and both motors are running.
            // Note: We use (isBusy() && isBusy()) in the loop test, which means that when EITHER motor hits
            // its target position, the motion will stop.  This is "safer" in the event that the robot will
            // always end the motion as soon as possible.
            // However, if you require that BOTH motors have finished their moves before the robot continues
            // onto the next step, use (isBusy() || isBusy()) in the loop test.
            while (opModeIsActive() &&
                    (runtime.seconds() < timeoutS) &&
                    (drivetrain.leftFront.isBusy() && drivetrain.rightRear.isBusy() && drivetrain.rightFront.isBusy() && drivetrain.leftRear.isBusy() )) {

                // Display it for the driver.
                telemetry.addData("Path1",  "Running to %7d :%7d : %7d :%7d", newLeftFrontTarget,  newRightRearTarget, newRightFrontTarget, newRightRearTarget);
                telemetry.addData("Path2",  "Running at %7d :%7d : %7d :%7d",
                        drivetrain.leftFront.getCurrentPosition(),
                        drivetrain.rightRear.getCurrentPosition(),
                        drivetrain.rightFront.getCurrentPosition(),
                        drivetrain.leftRear.getCurrentPosition());

                telemetry.update();
            }

            // Stop all motion;
            drivetrain.leftFront.setPower(0);
            drivetrain.rightRear.setPower(0);
            drivetrain.rightFront.setPower(0);
            drivetrain.leftRear.setPower(0);

            // Turn off RUN_TO_POSITION
            drivetrain.leftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            drivetrain.rightRear.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            drivetrain.rightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            drivetrain.leftRear.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            //  sleep(250);   // optional pause after each move
        }
    }


}