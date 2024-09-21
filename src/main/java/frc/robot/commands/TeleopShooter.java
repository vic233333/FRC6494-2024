package frc.robot.commands;

import frc.robot.subsystems.*;
import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.Command;

public class TeleopShooter extends Command {   
    private ShooterSubsystem s_Shooter; 
    private DoubleSupplier leftRaw;
    private DoubleSupplier rightRaw;

    public TeleopShooter(ShooterSubsystem s_Shooter,DoubleSupplier leftRaw,DoubleSupplier rightRaw) {
        this.s_Shooter = s_Shooter;
        addRequirements(s_Shooter);

        this.leftRaw = leftRaw;
        this.rightRaw = rightRaw;
    }

    @Override
    public void execute() {
         if(leftRaw.getAsDouble() <0.3 && rightRaw.getAsDouble() >0.3)
             s_Shooter.Shoot();
         else
             s_Shooter.stop();
    }
}