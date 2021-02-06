/* Copyright (c) 2019 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode.Autonomous;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.teamcode.Enums.ShooterState;
import org.firstinspires.ftc.teamcode.Enums.WobbleTargetZone;

import java.util.List;

@Autonomous(name = "Meet 4 Auto", group = "Concept")
//@Disabled
public class Meet_4_Auto extends BasicAutonomous {

    WobbleTargetZone Square = WobbleTargetZone.BLUE_A; // Default target zone


    @Override
    public void runOpMode() {

        drivetrain.init(hardwareMap);
        wobble.init(hardwareMap);
        shooter.init(hardwareMap);
        intake.init(hardwareMap);
        elevator.init(hardwareMap);
        m_Ring_Spreader.init(hardwareMap);

        shooter.shooterReload(); // reload = flipper back, stacker mostly down, shooter off


        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample opmode
        parameters.loggingEnabled      = true;
        parameters.loggingTag          = "IMU";
        //parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();

        // Retrieve and initialize the IMU. We expect the IMU to be attached to an I2C port
        // on a Core Device Interface Module, configured to be a sensor of type "AdaFruit IMU",
        // and named "imu".
        //imu = hardwareMap.get(BNO055IMU.class, "imu");
        drivetrain.imu.initialize(parameters);


        // make sure the gyro is calibrated before continuing
        while (!isStopRequested() && !drivetrain.imu.isGyroCalibrated())  {
            sleep(100);
            idle();
        }


        // The TFObjectDetector uses the camera frames from the VuforiaLocalizer, so we create that
        // first.
        initVuforia();
        initTfod();

        /**
         * Activate TensorFlow Object Detection before we wait for the start command.
         * Do it here so that the Camera Stream window will have the TensorFlow annotations visible.
         **/
        if (tfod != null) {
            tfod.activate();

            // The TensorFlow software will scale the input images from the camera to a lower resolution.
            // This can result in lower detection accuracy at longer distances (> 55cm or 22").
            // If your target is at distance greater than 50 cm (20") you can adjust the magnification value
            // to artificially zoom in to the center of image.  For best results, the "aspectRatio" argument
            // should be set to the value of the images used to create the TensorFlow Object Detection model
            // (typically 1.78 or 16/9).

            // Uncomment the following line if you want to adjust the magnification and/or the aspect ratio of the input images.
            tfod.setZoom(2.5, 1.78);
        }
        telemetry.addData(">", "Robot Ready.");    //
        telemetry.update();

        telemetry.addData("Mode", "waiting for start");
        telemetry.addData("imu calib status", drivetrain.imu.getCalibrationStatus().toString());
        /** Wait for the game to begin */
        telemetry.addData("Square", Square);

        telemetry.update();
        //////////////////////////////////////////////////////////////////////////////////////
        waitForStart();
        ////////////////////////////////////////////////////////////////////////////////////
        tfTime.reset(); //  reset the TF timer
        while (tfTime.time() < tfSenseTime && opModeIsActive()) { // need to let TF find the target so timer runs to let it do this
            if (tfod != null) {
                // getUpdatedRecognitions() will return null if no new information is available since
                // the last time that call was made.
                List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                if (updatedRecognitions != null) {
                    telemetry.addData("# Object Detected", updatedRecognitions.size());
                    // step through the list of recognitions and display boundary info.
                    int i = 0;
                    for (Recognition recognition : updatedRecognitions) {
                        telemetry.addData(String.format("label (%d)", i), recognition.getLabel());
                        telemetry.addData(String.format("  left,top (%d)", i), "%.03f , %.03f",
                                recognition.getLeft(), recognition.getTop());
                        telemetry.addData(String.format("  right,bottom (%d)", i), "%.03f , %.03f",
                                recognition.getRight(), recognition.getBottom());
                        ///
                        StackSize = recognition.getLabel();
                        //telemetry.addData("Target", Target);
                        if (StackSize == "Quad") {
                            Square = WobbleTargetZone.BLUE_C;
                            telemetry.addData("Square", Square);
                        } else if (StackSize == "Single") {
                            Square = WobbleTargetZone.BLUE_B;
                            telemetry.addData("Square", Square);

                        }

                    }
                    telemetry.update();
                }
            }
            if (tfod != null) {
                tfod.shutdown();
            }
        }

        // Pick up the Wobble Goal before moving.
        // Sleep statements help let things settle before moving on.
        wobble.GripperPartOpen();
        //wobble.ArmExtend();
        sleep(250);
        wobble.wobbleWristDown();
        sleep(250);
        wobble.GripperClose();
        //sleep(250);
        //wobble.ArmCarryWobble();


        // After picking up the wobble goal the robot always goes to the same spot to shoot the 3 preloaded rings.
        // After delivering the rings, the switch case has the appropriate drive path to the identified Target Zone.

        drivetime.reset(); // reset because time starts when TF starts and time is up before we can call gyroDrive
        // Drive paths are initially all the same to get to the shooter location
        //gyroDrive(DRIVE_SPEED, 54.0, 0.0, 10);

        gyroDrive(DRIVE_SPEED, 54.0, 0.0, 10);// 54 total
        //gyroDrive(DRIVE_SPEED*.6, 10.0, 0.0, 10);// 54 total
        gyroTurn(TURN_SPEED,10,3); //Need to change this angle for the right line start point
        mShooterState = ShooterState.STATE_SHOOTER_ACTIVE;
        shooterStartUp(mShooterState, shooterStartUpTimeAllowed);
        //shoot3Rings(mShooterState, autoShootTimeAllowed);   // call method to start shooter and launch 3 rings. pass shooter state in case it is needed
        try {
          shooter.shoot_N_rings(3);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        shooter.shooterReload();
        drivetime.reset(); // reset because time starts when TF starts and time is up before we can call gyroDrive

        // Switch manages the 3 different Target Zone objectives based on the number of rings stacked up
        // Ring stack is none, one or 4 rings tall and is determined by a randomization process.
        // Robot has to read the stack height and set the Target Zone square state based on Vuforia/ Tensor Flow detection

        switch(Square){
            case BLUE_A: // no rings. 3 tiles (24 inches per tile) forward and 2 tiles to the left from start
                telemetry.addData("Going to BLUE A", "Target Zone");
                gyroTurn(TURN_SPEED,45,2);
                gyroTurn(TURN_SPEED*.4,60,3);
                gyroDrive(DRIVE_SPEED,27,60,4);
                sleep(500);
                wobble.GripperOpen();
                sleep(500);
                //wobble.ArmExtend();


                drivetime.reset();
                gyroDrive(DRIVE_SPEED*.5,-4,60,4);
                wobble.lowerWobbleClamp(); // back up so clamp doest catch on first wobble
                gyroTurn(TURN_SPEED, 155,4);
                gyroTurn(TURN_SPEED*.4, 180,3); // turn and drive straight to front

                gyroDrive(DRIVE_SPEED*.6,36,180,4); // approach 2nd wobble parallel to side wall

                gyroTurn(TURN_SPEED*.4,157,3); // make final correctin turn to get second wobble
                gyroDrive(DRIVE_SPEED*5,7.5,157,2); // drive fwd to get second wobble

                //wobble.ArmExtend();

                //sleep(500);
                wobble.GripperClose();
                sleep(1000);
                //wobble.liftPartial();
                drivetime.reset();
                gyroDrive(DRIVE_SPEED,-55,153,2); // backup with 2nd wobble goal
                gyroTurn(TURN_SPEED*.5,90,3);
                gyroDrive(DRIVE_SPEED,13,90,2);
                //wobble.GripperSuperOpen();
                wobble.GripperOpen();
                sleep(250);
                wobble.raiseWobbleClamp();
                sleep(250);
                gyroDrive(DRIVE_SPEED,-24,90,2);
                //wobble.ArmContract();
                wobble.wobbleWristStart();
                wobble.GripperClose();
                sleep(500);// Keeps power to servo on long enough to allow servo to move


                break;
            case BLUE_B: // one ring  4 tiles straight ahead
                // Drop off wobble
                telemetry.addData("Going to BLUE B", "Target Zone");
                gyroDrive(DRIVE_SPEED, 24.0, 10.0, 5);
                sleep(500);
                wobble.GripperOpen();
                sleep(500);

                // Go towards the single ring and get second wobble
                drivetime.reset();
                gyroDrive(DRIVE_SPEED,-4,10,3);

                drivetime.reset();
                gyroTurn(TURN_SPEED*0.75,150,4);
                gyroTurn(TURN_SPEED*0.4,167,3);
                m_Ring_Spreader.ringSpreaderDown(); // drop ring spreader arm to prevent jamming in case TF reads C and goes to B
                gyroDrive(DRIVE_SPEED*.7, 28, 167, 5);
                gyroDriveandCollectRings(DRIVE_SPEED,8,167,2); // was 8

                intake.Intakeon();
                elevator.ElevatorSpeedfast();
                // Go for second wobbble
                gyroTurn(TURN_SPEED,155,3);
                gyroTurn(TURN_SPEED*.45,150,2);
                wobble.lowerWobbleClamp();
                gyroDrive(DRIVE_SPEED,17,150,3);
                gyroDrive(DRIVE_SPEED*.45,7,148,3);
                wobble.GripperClose();
                sleep(500);
                gyroDrive(DRIVE_SPEED,-19,148,3);

                // Turn back to face the goal and shoot
                gyroTurn(TURN_SPEED*.8,25,3); //turn fast most of the way
                gyroTurn(TURN_SPEED*.4,0,3);// turn slow to be accurate. Need to une PIDSs better instead

                gyroDrive(DRIVE_SPEED*.7, 10 , 0, 3); // drive fwd ro shoot 4th ring

                intake.Intakeoff();
                elevator.Elevatoroff();

                //wobble.wobbleWristStart();
                //wobble.GripperClose();
                mShooterState = ShooterState.STATE_SHOOTER_ACTIVE;
                shooterStartUp(mShooterState, shooterStartUpTimeAllowed);

                try {
                    shooter.shoot_N_rings(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                drivetime.reset();
                gyroDrive(DRIVE_SPEED,21,-6,3);


                wobble.GripperOpen();
                sleep(250);
                wobble.wobbleWristStart();
                sleep(250);
                gyroDrive(DRIVE_SPEED,-3,-8,3);
                wobble.raiseWobbleClamp();
                m_Ring_Spreader.ringSpreaderUp();
                sleep(250);


                break;
            case BLUE_C: // four rings. 5 tiles forward and one tile to the left.
                // Drop off first wobble after shooting 3 rings
                telemetry.addData("Going to BLUE C", "Target Zone");
                gyroTurn(TURN_SPEED *.5,22,2);
                gyroDrive(DRIVE_SPEED, 54, 22,3);
                wobble.GripperOpen();
                sleep(500);

                // Move to the stack of 4 rings
                drivetime.reset();
                gyroDrive(DRIVE_SPEED, -25, 20,3);
                gyroTurn(TURN_SPEED,120,2);
                gyroTurn(TURN_SPEED*.5,180,2);
                m_Ring_Spreader.ringSpreaderDown(); // drop ring spreader arm to prevent jamming
                gyroDrive(DRIVE_SPEED,31,180,2); // last forward move before collecting
                drivetime.reset();

                // Collect from the stack of 4 rings
                gyroDriveandCollectRings(DRIVE_SPEED*.2,10,180,10); // collect from stack
                gyroDriveandCollectRings(DRIVE_SPEED*.6,-3,180,10); // back up to prevent jamming
                gyroDriveandCollectRings(DRIVE_SPEED*.2,7,180,10); // collect from stack
                gyroDriveandCollectRings(DRIVE_SPEED*.8,-14,180,10); // backup leave intake on

                // Keep intake and elevator running to get ring settled
                intake.Intakeon();
                elevator.ElevatorSpeedfast();
                wobble.wobbleWristStart(); // lift up wrist so it is not across the line
                wobble.GripperClose(); // close gripper so rings exiting shooter do not hit the grabber arm

                // Turn to face the goal and shoot rings from the stack
                drivetime.reset();
                gyroTurn(TURN_SPEED,40,2);
                gyroTurn(TURN_SPEED*.30,0 ,1.5);
                intake.Intakeoff();
                elevator.Elevatoroff();
                mShooterState = ShooterState.STATE_SHOOTER_ACTIVE;
                shooterStartUp(mShooterState, shooterStartUpTimeAllowed);
                try {
                    shooter.shoot_N_rings(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                drivetime.reset();

                // Pull ahead to the line
                gyroDrive(DRIVE_SPEED, 10, 0,3);
                m_Ring_Spreader.ringSpreaderUp();
                sleep(500);

                break;
        }
        telemetry.update();

    }

}
