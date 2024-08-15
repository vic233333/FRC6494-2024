package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Translation2d;
import frc.robot.Constants;
import frc.robot.subsystems.Swerve;
import frc.robot.subsystems.VisionSubsystem;

public class AlignToAprilTagCommand extends Command {
    private final Swerve swerve;
    private final VisionSubsystem vision;
    private final PIDController rotationController;
    private final PIDController distanceController;

    public AlignToAprilTagCommand(Swerve swerve, VisionSubsystem vision) {
        this.swerve = swerve;
        this.vision = vision;
        addRequirements(swerve, vision);
        rotationController = new PIDController(Constants.Swerve.headingKP, Constants.Swerve.headingKI, Constants.Swerve.headingKD);
        rotationController.setTolerance(1);
        rotationController.enableContinuousInput(-Math.PI, Math.PI);
        
        distanceController = new PIDController(Constants.AutoConstants.kP, Constants.AutoConstants.kI, Constants.AutoConstants.kD);
        distanceController.setTolerance(0.05); // 5cm tolerance
    }

    @Override
    public void execute() {
        if (vision.haveSpeakerTarget()) {
            double rotationSpeed = rotationController.calculate(vision.angleError().getRadians(), 0);
            double forwardSpeed = distanceController.calculate(vision.distanceToSpeakerFromCenter(), Constants.Shooter.optimalShootingDistance);
            swerve.drive(new Translation2d(forwardSpeed, 0), rotationSpeed, true, false);
        } else {
            swerve.stop();
        }
    }

    @Override
    public boolean isFinished() {
        return (rotationController.atSetpoint() && distanceController.atSetpoint()) && vision.haveSpeakerTarget();
    }

    @Override
    public void end(boolean interrupted) {
        swerve.stop();
    }
}