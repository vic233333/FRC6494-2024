package frc.lib;

import com.ctre.phoenix6.signals.NeutralModeValue;
import com.ctre.phoenix6.configs.TalonFXConfiguration;

public class TalonFxConstants {
    public final int deviceNumber;
    public final TalonFXConfiguration allConfigs;
    public final NeutralModeValue neutralModeValue;
    public final boolean isInverted;
    public final boolean slowStatusFrame;    
    
    /**
     * Constants to be used with LazyTalonFX Util
     * @param deviceNumber
     * @param allConfigs
     * @param neutralMode
     * @param isInverted
     * @param slowStatusFrames
     */
    public TalonFxConstants(int deviceNumber, TalonFXConfiguration allConfigs,NeutralModeValue neutralModeValue, boolean isInverted, boolean slowStatusFrame) {
        this.deviceNumber = deviceNumber;
        this.allConfigs = allConfigs;
        this.neutralModeValue = neutralModeValue;
        this.isInverted = isInverted;
        this.slowStatusFrame = slowStatusFrame;
    }
}
