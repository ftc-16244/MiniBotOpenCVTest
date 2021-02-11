package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class  Ring_Spreader {
    //Define Hardware Objects
    public Servo ringSpreader=null;

    //Constants
    private static final double spreaderUp=.65;
    private static final double spreaderDown=.07;

    public void init(HardwareMap hwMap)  {
        ringSpreader     = hwMap.get(Servo.class, "Ring_Spreader");
        ringSpreaderUp();

    }

    public void ringSpreaderUp() {
        ringSpreader.setPosition(spreaderUp);
    }
    public void ringSpreaderDown() {
        ringSpreader.setPosition(spreaderDown);
    }
}
