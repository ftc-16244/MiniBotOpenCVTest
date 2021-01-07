package org.firstinspires.ftc.teamcode.Test;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.Subsystems.Ring_Spreader;

public class Ring_Spreader_Test extends LinearOpMode {
    public Ring_Spreader m_Ring_Spreader = new Ring_Spreader();

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();
        m_Ring_Spreader.init(hardwareMap);
        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).


        waitForStart();
        while (opModeIsActive()) {
            //========================================
            // GAME PAD 2 Only for this test opMode
            //========================================

            // gripper assignment to X and Y buttons on implement gamepad
            // does not work 5/28. wires are in correct port too
            if (gamepad2.y) {
                m_Ring_Spreader.ringSpreaderUp();
                telemetry.addData("Spreader Up", "Complete ");
            }
            if (gamepad2.a) {
                m_Ring_Spreader.ringSpreaderDown();
                telemetry.addData("Spreader Down", "Complete ");
            }

        }
}
