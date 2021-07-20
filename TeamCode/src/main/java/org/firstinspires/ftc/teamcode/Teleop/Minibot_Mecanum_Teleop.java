package org.firstinspires.ftc.teamcode.Teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.Autonomous.BasicMiniBotMeccanum;
import org.firstinspires.ftc.teamcode.Subsystems.Four_Motor_Minibot_Meccanum_Drivetrain;

@TeleOp(name="Mecanum MiniBot Teleop", group="Teleop")

public class Minibot_Mecanum_Teleop extends BasicMiniBotMeccanum {

    @Override
    public void runOpMode() {
        Four_Motor_Minibot_Meccanum_Drivetrain drivetrain  = new Four_Motor_Minibot_Meccanum_Drivetrain(true);   // Use subsystem Dri
        drivetrain.init(hardwareMap);

        // uncomment for Mecanum #3 Only the motor directions are odd
        drivetrain.leftFront.setDirection(DcMotor.Direction.REVERSE);
        drivetrain.rightFront.setDirection(DcMotor.Direction.FORWARD);
        drivetrain.leftRear.setDirection(DcMotor.Direction.REVERSE);
       drivetrain.rightRear.setDirection(DcMotor.Direction.FORWARD);


        waitForStart();
        if (isStopRequested()) return;
        while (opModeIsActive()) {
            double y = -gamepad1.left_stick_y; // Remember, this is reversed!
            double x = gamepad1.left_stick_x;
            double rx = gamepad1.right_stick_x;
            double lf, lr, rf, rr;

            lf = (y + x + rx);
            lr = (y - x + rx);
            rf = (y - x - rx);
            rr = (y + x - rx);

            if (Math.abs(lf) > 1 || Math.abs(lr) > 1 || Math.abs(rf) > 1 || Math.abs(rr) > 1) {
                double max = 0;
                max = Math.max(Math.abs(lf), Math.abs(lr));
                max = Math.max(Math.abs(rf), max);
                max = Math.max(Math.abs(rr), max);

                lf /= max;
                lr /= max;
                rf /= max;
                rr /= max;

            }


            drivetrain.leftFront.setPower(lf);
            drivetrain.leftRear.setPower(lr);
            drivetrain.rightFront.setPower(rf);
            drivetrain.rightRear.setPower(rr);

            telemetry.addData("Left Stick Y-Fwd", "%5.2f", y);
            telemetry.addData("Right  Stick X-Turn", "%5.2f", x);
            telemetry.addData("Left Stick X-Spin", "%5.2f", rx);
            telemetry.update();

        }
    }
}

