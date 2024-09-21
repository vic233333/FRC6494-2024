package frc.robot.commands;

import frc.robot.subsystems.*;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.Command;

public class TeleopIntaker extends Command {    
    private IntakerSubsysem s_Intaker; 
    private DoubleSupplier leftRaw;
    private DoubleSupplier rightRaw;

    public TeleopIntaker(IntakerSubsysem s_Intaker,DoubleSupplier leftRaw,DoubleSupplier rightRaw) {
        this.s_Intaker = s_Intaker;
        addRequirements(s_Intaker);

        this.leftRaw = leftRaw;
        this.rightRaw = rightRaw;
    }

    @Override
    public void execute() {
        if(leftRaw.getAsDouble() >0.5 && rightRaw.getAsDouble() <0.5 || leftRaw.getAsDouble() <0.5 && rightRaw.getAsDouble() >0.5) s_Intaker.setIntakerSpeed();
        else s_Intaker.stop();
    }
}