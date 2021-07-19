package org.firstinspires.ftc.teamcode.Autonomous;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Subsystems.Four_Motor_Minibot_Meccanum_Drivetrain;

@Autonomous(name="Mecanum 3 Auto Test", group="Auto")

public class Mecanum_3_TestAuto extends BasicMiniBotMeccanum_Nevrest20{

    @Override
    public void runOpMode() {


        drivetrain.init(hardwareMap); // call the init method in the subsystem. THis saves space here

        // uncomment for Mecanum #3 Only the motor directions are odd
        drivetrain.leftFront.setDirection(DcMotor.Direction.REVERSE);
        drivetrain.rightFront.setDirection(DcMotor.Direction.FORWARD);
        drivetrain.leftRear.setDirection(DcMotor.Direction.FORWARD);
        drivetrain.rightRear.setDirection(DcMotor.Direction.REVERSE);



        // Gyro set-up
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();

        parameters.mode                = BNO055IMU.SensorMode.IMU;
        parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.loggingEnabled      = false;

        // Init gyro parameters then calibrate
        drivetrain.imu.initialize(parameters);

        // Ensure the robot it stationary, then reset the encoders and calibrate the gyro.
        // Encoder rest is handled in the Drivetrain init in Drivetrain class

        // Calibrate gyro

        telemetry.addData("Mode", "calibrating...");
        telemetry.update();

        // make sure the gyro is calibrated before continuing
        while (!isStopRequested() && !drivetrain.imu.isGyroCalibrated())  {
            sleep(50);
            idle();
        }

        telemetry.addData(">", "Robot Ready.");    //
        telemetry.update();

        telemetry.addData("Mode", "waiting for start");
        telemetry.addData("imu calib status", drivetrain.imu.getCalibrationStatus().toString());
        /** Wait for the game to begin */


        telemetry.update();

        /////////////////////////////////////////////////////////////////////////////////////////////
        waitForStart();
        ////////////////////////////////////////////////////////////////////////////////////////////



        drivetime.reset(); // reset because time starts when TF starts and time is up before we can call gyroDrive
        // Drive paths are initially all the same to get to the shooter location
        gyroDrive(DRIVE_SPEED, 48.0, 0.0, 10);
        gyroTurn(TURN_SPEED,90,10);
        gyroDrive(DRIVE_SPEED,48,90,3);
        gyroTurn(TURN_SPEED,180,3);
        gyroDrive(DRIVE_SPEED,48,180,3);
        gyroTurn(TURN_SPEED,-90,3);
        gyroDrive(DRIVE_SPEED,48,-90,3);

        telemetry.addData("Path", "Complete");
        telemetry.update();
    }

//@Disabled



}
