package frc.robot;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.math.geometry.Translation2d;
import frc.robot.subsystems.Swerve;
import frc.robot.subsystems.VisionSubsystem;
import frc.robot.subsystems.ShooterSubsystem;
import frc.robot.subsystems.AlignToAprilTagCommand;

public class AutonomousCommand extends SequentialCommandGroup {
    public AutonomousCommand(Swerve swerve, VisionSubsystem vision, ShooterSubsystem shooter) {
        addCommands(
            // Step 1: Drive backwards for 2 meters
            new RunCommand(() -> 
                swerve.drive(new Translation2d(
                    Constants.AutoConstants.initTranslationX,
                    Constants.AutoConstants.initTranslationY),
                    Constants.AutoConstants.initTranslationRotation,
                    true, false))
                    .withTimeout(2)
                    .andThen(new InstantCommand(swerve::stop)),
            
            // Step 2 & 3: Find AprilTags 7 and 8, and move to the best shooting position
            new AlignToAprilTagCommand(swerve, vision)
            .withTimeout(5)
            .andThen(new InstantCommand(swerve::stop)),
            
            // Step 4: Shoot
            shooter.shotCommand()
            .withTimeout(3),
            
            // Step 5: Stop
            new InstantCommand(swerve::stop)
        );
    }
}