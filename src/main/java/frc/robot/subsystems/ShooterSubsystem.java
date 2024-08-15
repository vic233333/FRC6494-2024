package frc.robot.subsystems;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class ShooterSubsystem extends SubsystemBase {
    private final TalonFX leftMotor;
    private final TalonFX rightMotor;
    private final DutyCycleOut leftout = new DutyCycleOut(1);
    private final DutyCycleOut rightout = new DutyCycleOut(-1);

    public ShooterSubsystem() {

        leftMotor = new TalonFX(Constants.Shooter.leftMotorID, Constants.Shooter.motorCanBus);
        rightMotor = new TalonFX(Constants.Shooter.rightMotorID, Constants.Shooter.motorCanBus);

        configureMotors();

    }

    private void configureMotors() {
        TalonFXConfiguration config = new TalonFXConfiguration();
        config.Slot0.kP = Constants.Shooter.kP;
        config.Slot0.kI = Constants.Shooter.kI;
        config.Slot0.kD = Constants.Shooter.kD;
        config.Slot0.kS = Constants.Shooter.kS;
        config.Slot0.kV = 1.0 / Constants.Shooter.RPMsPerVolt;

        leftMotor.getConfigurator().apply(config);
        rightMotor.getConfigurator().apply(config);
    }
    public void Shoot(){
        setShooterSpeed();
    }

    public void setShooterSpeed() {
        leftMotor.setControl(leftout);
        rightMotor.setControl(rightout);
    }


    public void stop() {
        leftMotor.setControl(new DutyCycleOut(0));
        rightMotor.setControl(new DutyCycleOut(0));
    }


    @Override
    public void periodic() {
    }
}