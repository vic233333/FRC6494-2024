package frc.robot.subsystems;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import frc.robot.Constants;
public class IntakerSubsysem extends SubsystemBase {
    private final DigitalInput seeNote;
    private final TalonFX intakerMotor;
    public IntakerSubsysem(){
        seeNote=new DigitalInput(Constants.Intaker.seeNoteID);
        intakerMotor = new TalonFX(Constants.Intaker.intakerMotorID,Constants.Swerve.kCANivoreBusName);

        configureMotors();
    }

    private void configureMotors() {
        TalonFXConfiguration config = new TalonFXConfiguration();

        intakerMotor.getConfigurator().apply(config);
    }
    private void setIntakerSpeed(){
        intakerMotor.setControl(new DutyCycleOut(Constants.Intaker.intakerMotorOutput));
    }
    private void stop(){
        intakerMotor.setControl(new DutyCycleOut(0));
    }
    
    public void intake(Swerve s_Swerve){
        if(seeNote.get() == true){
        setIntakerSpeed();
        s_Swerve.drive(new Translation2d(1, 0),0,false,false);
        stop();
        }
    }
}
