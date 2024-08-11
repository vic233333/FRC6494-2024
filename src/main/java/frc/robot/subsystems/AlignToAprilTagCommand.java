package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Translation2d;
import frc.robot.subsystems.Swerve;
import frc.robot.subsystems.VisionSubsystem;

public class AlignToAprilTagCommand extends Command {
    private final Swerve swerve;
    private final VisionSubsystem vision;
    private final PIDController rotationController;

    public AlignToAprilTagCommand(Swerve swerve, VisionSubsystem vision) {
        this.swerve = swerve;
        this.vision = vision;
        addRequirements(swerve, vision);

        rotationController = new PIDController(0.1, 0, 0);
        rotationController.setTolerance(2);
        rotationController.enableContinuousInput(-180, 180);
    }

    @Override
    public void execute() {
        if (vision.hasValidTarget()) {
            double rotationSpeed = rotationController.calculate(vision.getEstimatedPose().getRotation().getDegrees(), 0);
            swerve.drive(new Translation2d(0, 0), rotationSpeed, true, false);
        } else {
            swerve.stop();
        }
    }

    @Override
    public boolean isFinished() {
        return rotationController.atSetpoint() && vision.hasValidTarget();
    }

    @Override
    public void end(boolean interrupted) {
        swerve.stop();
    }
}