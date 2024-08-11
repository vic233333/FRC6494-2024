package frc.robot;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.math.geometry.Translation2d;
import frc.robot.subsystems.Swerve;
import frc.robot.subsystems.VisionSubsystem;
import frc.robot.subsystems.ShooterSubsystem;
import frc.robot.subsystems.AlignToAprilTagCommand;;

public class AutonomousCommand extends SequentialCommandGroup {
    public AutonomousCommand(Swerve swerve, VisionSubsystem vision, ShooterSubsystem shooter) {
        addCommands(
            // Step 1: Drive backwards for a set distance
            new InstantCommand(() -> swerve.drive(new Translation2d(-1, 0), 0, false, true))
                .andThen(new WaitCommand(2))
                .andThen(new InstantCommand(swerve::stop)),
            
            // Step 2: Align with AprilTag
            new AlignToAprilTagCommand(swerve, vision),
            
            // Step 3: Shoot the "note"
            new InstantCommand(shooter::shootBasedOnVision)
                .andThen(new WaitCommand(2))
                .andThen(new InstantCommand(() -> {
                    if (shooter.isReadyToShoot()) {
                        // Trigger note release mechanism here
                    }
                }))
                .andThen(new WaitCommand(1))
                .andThen(shooter::stop)
        );
    }
}