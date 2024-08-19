package frc.robot.subsystems;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

import edu.wpi.first.wpilibj.Timer;
public class ShooterSubsystem extends SubsystemBase {
    private final TalonFX leftMotor;
    private final TalonFX rightMotor;
    private Timer t;

    public ShooterSubsystem() {

        leftMotor = new TalonFX(Constants.Shooter.leftMotorID, Constants.Shooter.motorCanBus);
        rightMotor = new TalonFX(Constants.Shooter.rightMotorID, Constants.Shooter.motorCanBus);

        configureMotors();
        t = new Timer();
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
        t.restart();
        while(t.get() < 2.0){
            setShooterSpeed();
        }
        stop();
    }

    public void setShooterSpeed() {
        leftMotor.setControl(new DutyCycleOut(/*Constants.Shooter.leftMotorOutput*/1));
        rightMotor.setControl(new DutyCycleOut(/*Constants.Shooter.rightMotorOutput*/-1));
    }

    public Command shotCommand() {
        return Commands.sequence(
            Commands.runOnce(this::Shoot),
            Commands.waitSeconds(3),
            Commands.runOnce(this::stop)
        );
    }

    public void stop() {
        leftMotor.setControl(new DutyCycleOut(0));
        rightMotor.setControl(new DutyCycleOut(0));
    }


    @Override
    public void periodic() {
    }
}