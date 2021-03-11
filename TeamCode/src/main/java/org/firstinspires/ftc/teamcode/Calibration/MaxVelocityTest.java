package org.firstinspires.ftc.teamcode.Calibration;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.VoltageSensor;

@TeleOp
public class MaxVelocityTest extends LinearOpMode {
    DcMotorEx motor;
    double currentVelocity;
    double maxVelocity = 0.0;
    double F;
    private VoltageSensor batteryVoltageSensor;

    @Override
    public void runOpMode() {
        motor = hardwareMap.get(DcMotorEx.class, "LeftShooter");
        waitForStart();
        while (opModeIsActive()) {
            currentVelocity = motor.getVelocity();
            if (currentVelocity > maxVelocity) {

                maxVelocity = currentVelocity;

            }

            F = 32767 / maxVelocity;
            batteryVoltageSensor.getVoltage();

            telemetry.addData("current velocity", currentVelocity);
            telemetry.addData("maximum velocity", maxVelocity);
            telemetry.addData("Uncorrected F Value", F);
            telemetry.update();
        }
    }

}
