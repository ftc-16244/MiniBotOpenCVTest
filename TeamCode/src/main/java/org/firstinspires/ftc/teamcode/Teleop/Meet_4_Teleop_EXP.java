package org.firstinspires.ftc.teamcode.Teleop;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Enums.DriveSpeedState;
import org.firstinspires.ftc.teamcode.Enums.LiftMode;
import org.firstinspires.ftc.teamcode.Enums.RingCollectionState;
import org.firstinspires.ftc.teamcode.Enums.WobbleLiftPosn;
import org.firstinspires.ftc.teamcode.Enums.WristPosn;
import org.firstinspires.ftc.teamcode.Subsystems.Debouce;
import org.firstinspires.ftc.teamcode.Subsystems.Drivetrain_v3;
import org.firstinspires.ftc.teamcode.Subsystems.Elevator;
import org.firstinspires.ftc.teamcode.Subsystems.Intake;
import org.firstinspires.ftc.teamcode.Subsystems.Ring_Spreader;
import org.firstinspires.ftc.teamcode.Subsystems.Shooter_VelCtrl;
import org.firstinspires.ftc.teamcode.Subsystems.Wobblegoal;

@TeleOp(name="Meet 4 Teleop Exp", group="Teleop")
// Creted to fix the lift poblem
//@Disabled
public class Meet_4_Teleop_EXP extends OpMode {


    /* Declare OpMode members. */

    private ElapsedTime     runtime     = new ElapsedTime();
    public Drivetrain_v3    drivetrain  = new Drivetrain_v3(true);   // Use subsystem Drivetrain
    public Shooter_VelCtrl  shooter     = new Shooter_VelCtrl(); //experiment to see if thsi helps
    public Intake           intake      = new Intake();
    public Wobblegoal       wobble      = new Wobblegoal();
    public Elevator         elevator    = new Elevator();
    public Ring_Spreader m_Ring_Spreader = new Ring_Spreader();

    public ElapsedTime gripperCloseTimer = new ElapsedTime();
    //public ElapsedTime debounceTimer = new ElapsedTime();
    private Debouce mdebounce = new Debouce();

    // ENUMS
    private DriveSpeedState  currDriveState;
    private RingCollectionState ringCollectorState;
    private double          gripperCloseTime = 1.0;

    public WobbleLiftPosn   liftposn    = WobbleLiftPosn.IDLE; // Default target zone
    public WristPosn        wristPosn   = WristPosn.PARK;
    public LiftMode         liftmode    = LiftMode.ENCODER; // default lift mode

    private ElapsedTime     wristTimer  = new ElapsedTime();

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
   public void init() {

        drivetrain.init(hardwareMap);
        intake.init(hardwareMap);
        wobble.init(hardwareMap);
        elevator.init(hardwareMap);
        shooter.init(hardwareMap);
        m_Ring_Spreader.init(hardwareMap);
        //newState(currDriveState);
        currDriveState = DriveSpeedState.DRIVE_FAST; // initialize robot to FAST
        ringCollectorState = RingCollectionState.OFF;

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Say", "Hello Driver");

        telemetry.addData("FAST DRIVE","Mode");//
        //telemetry.update();

    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {


    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {

    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        double left;
        double right;
        double drive;
        double turn;
        double max;
        double speedfactor = 0.5;
        double manualLiftSpeed;

        /**
         * /////////////////////////////////////////////////////////////////////////////////////
         * Gamepad #1 Section
         * /////////////////////////////////////////////////////////////////////////////////////
         **/


        /**
         *
         * Gamepad #1 Joysticks
         *
         **/

        // left joystick is assigned to drive speed
        drive = -gamepad1.left_stick_y;
        // right joystick is for turning
        turn  =  gamepad1.right_stick_x;
        // Combine drive and turn for blended motion.
        left  = drive + turn;
        right = drive - turn;

        // Normalize the values so neither exceed +/- 1.0
        max = Math.max(Math.abs(left), Math.abs(right));
        if (max > 1.0)
        {
            left /= max; // does this to stay within the limit and keeps the ratio the same
            right /= max;
        }

        /**
         *
         * Gamepad #1 Bumpers
         *
         **/

        if (gamepad1.left_bumper && ringCollectorState == RingCollectionState.OFF) {
            shooter.flipperBackward();
            shooter.stackerMoveToMidLoad();
            ringCollectorState = RingCollectionState.COLLECT;
            telemetry.addData("Collector State", ringCollectorState);
            mdebounce.debounce(175); // need to pause for a few ms to let drive release the button

        }
        if (gamepad1.left_bumper && ringCollectorState == RingCollectionState.COLLECT) {
            shooter.flipperBackward();
            shooter.stackerMoveToMidLoad();
            ringCollectorState = RingCollectionState.OFF;
            telemetry.addData("Collector State", ringCollectorState);
            mdebounce.debounce(175);
        }


        if (gamepad1.right_bumper && ringCollectorState == RingCollectionState.OFF) {
            shooter.flipperBackward();
            shooter.stackerMoveToReload();
            ringCollectorState = RingCollectionState.EJECT;
            telemetry.addData("Collector State", ringCollectorState);
            mdebounce.debounce(175);

        }

        if (gamepad1.right_bumper && ringCollectorState == RingCollectionState.EJECT) {
            shooter.flipperBackward();
            shooter.stackerMoveToReload();
            ringCollectorState = RingCollectionState.OFF;
            telemetry.addData("Collector State", ringCollectorState);
            mdebounce.debounce(175);

        }

        /**
         *
         * Gamepad #1 Buttons - Stacker and Shooter Motors
         *
         **/

        if (gamepad1.x) {
            //shooter.shooterReload();
            shooter.stackerMoveToReload();
            telemetry.addData("Stacker Reset", "Complete ");

        }
        if (gamepad1.y) {
            shooter.shootOneRingHigh();
            //shooter.shootMiddleGoal();
            ringCollectorState = RingCollectionState.OFF;

            telemetry.addData("Shooter High", "Complete ");
        }

        if (gamepad1.a) {
            shooter.shooterReload();
            //shooter.shooterOff();
            telemetry.addData("Shooter High", "Complete ");
        }
        if (gamepad1.b) {
            shooter.stackerMoveToShoot();
            ringCollectorState = RingCollectionState.OFF;
            telemetry.addData("Stacker Ready to Shoot", "Complete ");
        }
        /**
         *
         * Gamepad #1 Triggers - Soot Ring and Low Speed
         *
         **/

        if (gamepad1.left_trigger > 0.25) {
            shooter.flipperForward();
            debounce(500);
            telemetry.addData("Flipper Fwd", "Complete ");
            shooter.flipperBackward();
            debounce(500);
        }
        if (gamepad1.right_trigger > 0.25) {
            //shooter.flipperBackward();
            //telemetry.addData("Flipper Back", "Complete ");
            shooter.shootonePowerShots();
            currDriveState =  DriveSpeedState.DRIVE_SLOW;
            telemetry.addData("SHooter Low for Power Shots", "Complete ");
        }

        /**
         *
         * Gamepad #1 Stick Buttons - Drive Speeds
         *
         **/

        if (gamepad1.left_stick_button)
        {
            currDriveState = DriveSpeedState.DRIVE_FAST;
        }
        if (gamepad1.right_stick_button)
        {
            currDriveState =  DriveSpeedState.DRIVE_SLOW;
        }


        /**
         *
         * Gamepad #1 DPAD Wobble Controls
         *
         **/

        if (gamepad1.dpad_left && liftmode == LiftMode.ENCODER) {
            wobble.GripperOpen();
            //wobble.ArmExtend();
            wristPosn = WristPosn.DOWN;
            wobble.lowerWobbleClamp();
            //wobble.wobbleWristDown();

            telemetry.addData("Ready to rab Wobble", "Complete ");
        }

        if (gamepad1.dpad_up && liftmode == LiftMode.ENCODER){
            gripperCloseTimer.reset();
            wobble.GripperClose();
            //while (gripperCloseTimer.time() < gripperCloseTime){

                // stall program so gripper can close
                // not necessary in Linear Opmode just in iterative
                //more than a couple seconds and this will trow error
            //}

            wobble.raiseWobbleClamp();
            //wobble.readyToGrabGoal();
           telemetry.addData("Carrying Wobble", "Complete ");
        }
        if (gamepad1.dpad_right && liftmode == LiftMode.ENCODER) {
            wobble.GripperOpen();


            telemetry.addData("Dropping Wobble", "Complete ");
        }
        if (gamepad1.dpad_down && liftmode == LiftMode.ENCODER) {

            wobble.GripperClose();
            //wobble.LiftLower();
            liftposn = WobbleLiftPosn.DOWN; // lift position state
            //wobble.wobbleWristUp();
            wristPosn = WristPosn.UP; // wrist position state
            wobble.raiseWobbleClamp();

        }
        // Hold dpad down then move right hand (turn stick) back (in the Y direction) to park wrist
        if (gamepad1.dpad_down && gamepad1.right_stick_y > 0.5){

            wristPosn = WristPosn.PARK; //
        }


        if (gamepad1.back && liftmode == LiftMode.ENCODER){

            //wobble.LiftRise();
            liftposn = WobbleLiftPosn.UP;
            //wobble.raiseWobbleClamp();
            wristPosn = WristPosn.DOWN; // wrist position state
            wobble.raiseWobbleClamp();

        }

        /**
         * /////////////////////////////////////////////////////////////////////////////////////
         * Gamepad #2 Section
         * /////////////////////////////////////////////////////////////////////////////////////
         **/
        // left joystick is assigned to drive speed.


        /**
         *
         * Gamepad #2 Joysticks
         *
         **/

        manualLiftSpeed = -gamepad2.left_stick_y; // always calculated may or may not use


        /**
         *
         * Gamepad #2 Bumpers
         *
         **/

        // Swap control of the wobble lift between Encoder modea nd manual mode
        if (gamepad2.left_bumper){
            liftmode =  LiftMode.MANUAL;
            wobble.WobbleLift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            liftposn =WobbleLiftPosn.IDLE; // set to idle for whne you reactivate Encoder mode
        }

        if (gamepad2.right_bumper){
            liftmode =  LiftMode.ENCODER;

            wobble.WobbleLift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            wobble.WobbleLift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            liftposn =WobbleLiftPosn.IDLE; // set idle becasue therwise it will go to the state where it left off before switching to manual
        }

        /**
         *
         * Gamepad #2 DPAD - Wobble Secondary Driver
         *
         **/

        if (gamepad2.dpad_left && liftmode == LiftMode.MANUAL) {
           wobble.GripperOpen();
           //wobble.ArmExtend();
           wristPosn = WristPosn.DOWN;
           wobble.lowerWobbleClamp();
           telemetry.addData("Ready to rab Wobble", "Complete ");
        }

        if (gamepad2.dpad_up && liftmode == LiftMode.MANUAL){
           gripperCloseTimer.reset();
           wobble.GripperClose();
           wobble.raiseWobbleClamp();
           //wobble.readyToGrabGoal();
           telemetry.addData("Carrying Wobble", "Complete ");
        }
        if (gamepad2.dpad_right && liftmode == LiftMode.MANUAL) {
           wobble.GripperOpen();
           telemetry.addData("Dropping Wobble", "Complete ");
        }


        if (gamepad2.dpad_down && liftmode == LiftMode.MANUAL){
            wristPosn = WristPosn.PARK;
            wobble.GripperClose();

        }

        /**
         *
         * Gamepad #2 Buttons- Ring Spreader
         *
         **/

        if (gamepad2.x) {
            m_Ring_Spreader.ringSpreaderUp();
        }
        if (gamepad2.b) {
            m_Ring_Spreader.ringSpreaderDown();
        }

        if (gamepad2.back){
            shooter.stackerMoveToDump();
            intake.Intakeoff();
            elevator.Elevatoroff();
        }
       // switch case to determine what mode the arm needs to operate in.


        /**
         * Switch Case to manage high and low drive speeds
         * @Param currDriveState this is an ENUM that describes high or low speed
         **/

        switch(currDriveState) {

            case DRIVE_FAST:
                telemetry.addData("Drive Speed",currDriveState);
                drivetrain.leftFront.setPower(left);
                drivetrain.rightFront.setPower(right);
                //leftFront.setPower(left);
                //rightFront.setPower(right);

                // Send telemetry message to signify robot running;
                telemetry.addData("left",  "%.2f", left);
                telemetry.addData("right", "%.2f", right);
                break;

            case DRIVE_SLOW:
                telemetry.addData("Drive Speed",currDriveState);
                drivetrain.leftFront.setPower(left*speedfactor);
                drivetrain.rightFront.setPower(right*speedfactor);
                //leftFront.setPower(left*speedfactor);
                //rightFront.setPower(right*speedfactor);

                // Send telemetry message to signify robot running;
                telemetry.addData("left",  "%.2f", left);
                telemetry.addData("right", "%.2f", right);
                break;
        }


        /**
         * Switch Case and If to allow the lift to be controlled by the primary driver
         * on Gamepad #1.
         * @Param liftmode this is an ENUM is either ENCODER for the primary driver to control
         * or MANUAL to allow the secondary driver to take control of the lift. We suspect that we
         * had a static problem which made resetting the lift impossible.
         **/

        if (liftmode == LiftMode.ENCODER){
            telemetry.addData("Lift Mode",liftmode);
            switch (liftposn) {

                case DOWN:
                    telemetry.addData("Lift Position", liftposn);
                    wobble.LiftLower();
                    if (wobble.getLiftHeight() < 0.15) {
                        liftposn = WobbleLiftPosn.IDLE;

                    }
                    break;

                case UP:
                    telemetry.addData("Lift Position", liftposn);
                    wobble.LiftRise();
                    //wobble.wobbleWristDown();


                    break;

                case IDLE:
                    telemetry.addData("Lift Position", liftposn);
                    wobble.LiftIdle();
                    wobble.WobbleLift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);


                    break;
            }
        } // bracket for the if statement

        /**
         * Switch Case and If to allow the lift to be controlled by the secondary driver
         * on Gamepad #2.
         * @Param liftmode is an ENUM set to MANUAL in this situation.
         **/
        if (liftmode == LiftMode.MANUAL){

            telemetry.addData("Lift Mode",liftmode);
            wobble.WobbleLift.setPower(manualLiftSpeed);

        }
        /**
         * Switch Case and If to allow the lift to be controlled by the secondary driver
         * on Gamepad #2.
         * @Param wristPosn is an ENUM to set different wrist positions.
         **/

        switch(wristPosn) {

            case PARK:
                telemetry.addData("Wrist  Position",wristPosn);
                wobble.wobbleWristStart();

                break;

            case UP:
                telemetry.addData("Wrist  Position",wristPosn);
                wobble.wobbleWristUp();

                break;

            case DOWN:
                telemetry.addData("Wrist  Position",wristPosn);
                wobble.wobbleWristDown();


                break;
        }


        // States for intake direction
        switch(ringCollectorState) {

            case OFF:
                telemetry.addData("Collector State",ringCollectorState);
                intake.Intakeoff();;
                elevator.Elevatoroff();

                break;

            case COLLECT:
                telemetry.addData("Collector State",ringCollectorState);
                intake.Intakeon();;
                elevator.ElevatorSpeedfast();
                break;

            case EJECT:
                telemetry.addData("Collector State",ringCollectorState);
                intake.IntakeReverse();;
                elevator.Elevatorbackup();
                break;

        }


    }
    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
    }
    //===================================================================
    // Helper Methods
    //==================================================================

    void debounce(long debounceTime){
        try {
            Thread.sleep(debounceTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
