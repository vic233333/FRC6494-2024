package frc.robot.subsystems;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class ShooterSubsystem extends SubsystemBase {
    private final TalonFX topMotor;
    private final TalonFX bottomMotor;
    private final VelocityVoltage topControl = new VelocityVoltage(0).withEnableFOC(true);
    private final VelocityVoltage bottomControl = new VelocityVoltage(0).withEnableFOC(true);

    private double topTargetRPM = 0.0;
    private double bottomTargetRPM = 0.0;

    private final VisionSubsystem visionSubsystem;

    public ShooterSubsystem(VisionSubsystem visionSubsystem) {
        this.visionSubsystem = visionSubsystem;

        topMotor = new TalonFX(Constants.Shooter.topMotorID, Constants.Shooter.motorCanBus);
        bottomMotor = new TalonFX(Constants.Shooter.bottomMotorID, Constants.Shooter.motorCanBus);

        configureMotors();

        SmartDashboard.putNumber("Shooter/Top RPM Adjustment", 0.0);
        SmartDashboard.putNumber("Shooter/Bottom RPM Adjustment", 0.0);
    }

    private void configureMotors() {
        TalonFXConfiguration config = new TalonFXConfiguration();
        config.Slot0.kP = Constants.Shooter.kP;
        config.Slot0.kI = Constants.Shooter.kI;
        config.Slot0.kD = Constants.Shooter.kD;
        config.Slot0.kS = Constants.Shooter.kS;
        config.Slot0.kV = 1.0 / Constants.Shooter.RPMsPerVolt;

        topMotor.getConfigurator().apply(config);
        bottomMotor.getConfigurator().apply(config);
    }

    public void setShooterSpeed(double topRPM, double bottomRPM) {
        topTargetRPM = topRPM + SmartDashboard.getNumber("Shooter/Top RPM Adjustment", 0.0);
        bottomTargetRPM = bottomRPM + SmartDashboard.getNumber("Shooter/Bottom RPM Adjustment", 0.0);

        topMotor.setControl(topControl.withVelocity(Units.rotationsPerMinuteToRadiansPerSecond(topTargetRPM)));
        bottomMotor.setControl(bottomControl.withVelocity(Units.rotationsPerMinuteToRadiansPerSecond(bottomTargetRPM)));
    }

    public void shootBasedOnVision() {
        if (visionSubsystem.hasValidTarget()) {
            double distance = visionSubsystem.getDistanceToSpeaker();
            double[] speeds = calculateSpeedsForDistance(distance);
            setShooterSpeed(speeds[0], speeds[1]);
        } else {
            stop();
        }
    }

    private double[] calculateSpeedsForDistance(double distance) {
        // This is a simplified example. You should implement a more sophisticated
        // calculation based on your robot's characteristics and field testing.
        double baseSpeed = 2000 + (distance * 50); // Example calculation
        return new double[]{baseSpeed, baseSpeed * 0.9};
    }

    public void stop() {
        setShooterSpeed(0, 0);
    }

    public boolean isReadyToShoot() {
        double topVelocity = Units.radiansPerSecondToRotationsPerMinute(topMotor.getVelocity().getValue());
        double bottomVelocity = Units.radiansPerSecondToRotationsPerMinute(bottomMotor.getVelocity().getValue());

        return Math.abs(topVelocity - topTargetRPM) < Constants.Shooter.allowedRPMError &&
               Math.abs(bottomVelocity - bottomTargetRPM) < Constants.Shooter.allowedRPMError;
    }

    @Override
    public void periodic() {
        double topVelocity = Units.radiansPerSecondToRotationsPerMinute(topMotor.getVelocity().getValue());
        double bottomVelocity = Units.radiansPerSecondToRotationsPerMinute(bottomMotor.getVelocity().getValue());

        SmartDashboard.putNumber("Shooter/Top RPM", topVelocity);
        SmartDashboard.putNumber("Shooter/Bottom RPM", bottomVelocity);
        SmartDashboard.putNumber("Shooter/Top Target RPM", topTargetRPM);
        SmartDashboard.putNumber("Shooter/Bottom Target RPM", bottomTargetRPM);
        SmartDashboard.putBoolean("Shooter/Ready", isReadyToShoot());
    }
}