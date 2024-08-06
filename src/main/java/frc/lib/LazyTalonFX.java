package frc.lib;

import com.ctre.phoenix6.signals.NeutralModeValue;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.configs.TalonFXConfiguration;

/**
 * Thin Falcon wrapper to make setup easier.
 */
public class LazyTalonFX extends TalonFX {

    /**
     * Config using individual parameters.
     * @param deviceNumber
     * @param allConfigs
     * @param neutralModeValue
     * @param anglemotorinvert
     * @param slowStatusFrame
     */
    public LazyTalonFX(int deviceNumber, TalonFXConfiguration allConfigs, NeutralModeValue neutralModeValue, boolean anglemotorinvert, boolean slowStatusFrame){
        super(deviceNumber, "canivore");
        super.getConfigurator().apply(new TalonFXConfiguration());  
        super.setInverted(anglemotorinvert);
        super.setNeutralMode(neutralModeValue);
        super.getPosition().setUpdateFrequency(1);

    }

    /**
     * Config using talonFxConstants.
     * @param talonFxConstants
     */
    public LazyTalonFX(TalonFxConstants talonFxConstants){
        super(talonFxConstants.deviceNumber, "canivore");
        super.getConfigurator().apply(new TalonFXConfiguration());
        super.setNeutralMode(talonFxConstants.neutralModeValue);
        super.setInverted(talonFxConstants.isInverted);
        super.getPosition().setUpdateFrequency(1);

    }
    
}
