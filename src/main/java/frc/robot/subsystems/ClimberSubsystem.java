package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.Timer;
import com.ctre.phoenix.motorcontrol.VictorSPXControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.VictorSPXConfiguration;

public class ClimberSubsystem extends SubsystemBase {
    //private final DigitalInput seeNote;
    private final VictorSPX climber ;
    public ClimberSubsystem(){
        //seeNote=new DigitalInput(Constants.Intaker.seeNoteID);
        climber = new VictorSPX(9);
        configureMotors();
    }

    private void configureMotors() {
        VictorSPXConfiguration allConfigs = new VictorSPXConfiguration();
        climber.configFactoryDefault();
        climber.configAllSettings(allConfigs);
    }
    public void climb1(){
        climber.set(VictorSPXControlMode.PercentOutput, 0.1);
    }
    public void climb2(){
        climber.set(VictorSPXControlMode.PercentOutput, -0.1);

    }
    public void stop(){
        climber.set(VictorSPXControlMode.PercentOutput, 0);
    }
    
}
