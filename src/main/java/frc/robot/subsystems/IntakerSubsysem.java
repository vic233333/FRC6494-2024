package frc.robot.subsystems;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.VictorSPXControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import frc.robot.Constants;
import com.ctre.phoenix.motorcontrol.can.VictorSPXConfiguration;
public class IntakerSubsysem extends SubsystemBase {
    //private final DigitalInput seeNote;
    private final TalonFX intakerMotor;
    private final VictorSPX intaker2 ;
    public IntakerSubsysem(){
        //seeNote=new DigitalInput(Constants.Intaker.seeNoteID);
        intakerMotor = new TalonFX(Constants.Intaker.intakerMotorID,Constants.Swerve.kCANivoreBusName);
        intaker2 = new VictorSPX(8);
        configureMotors();
    }

    private void configureMotors() {
        TalonFXConfiguration config = new TalonFXConfiguration();
        VictorSPXConfiguration allConfigs = new VictorSPXConfiguration();
        intakerMotor.getConfigurator().apply(config);
        intaker2.configFactoryDefault();
        intaker2.configAllSettings(allConfigs);
    }
    public void setIntakerSpeed(){
        intakerMotor.setControl(new DutyCycleOut(Constants.Intaker.intakerMotorOutput));
        intaker2.set(VictorSPXControlMode.PercentOutput, -0.5);
    }
    public void setIntakerSpeedF(){
        intakerMotor.setControl(new DutyCycleOut(-Constants.Intaker.intakerMotorOutput));
        intaker2.set(VictorSPXControlMode.PercentOutput, 0.5);
    }
    public void stop(){
        intakerMotor.setControl(new DutyCycleOut(0));
        intaker2.set(VictorSPXControlMode.PercentOutput, 0);
    }
    
}
