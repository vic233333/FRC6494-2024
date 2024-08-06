package frc.robot;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.signals.AbsoluteSensorRangeValue;
import com.ctre.phoenix6.signals.SensorDirectionValue;
import com.ctre.phoenix6.configs.CANcoderConfiguration;
import com.ctre.phoenix6.configs.CurrentLimitsConfigs;

public final class CTREConfigs {
    public TalonFXConfiguration swerveAngleFXConfig;
    public TalonFXConfiguration swerveDriveFXConfig;
    public CANcoderConfiguration swerveCanCoderConfig;
    public SensorDirectionValue CounterClockwise_Positive;
    public CTREConfigs(){
        swerveAngleFXConfig = new TalonFXConfiguration();
        swerveDriveFXConfig = new TalonFXConfiguration();
        swerveCanCoderConfig = new CANcoderConfiguration();

        /* Swerve Angle Motor Configurations */
        CurrentLimitsConfigs angleSupplyLimit = new CurrentLimitsConfigs();
        angleSupplyLimit.SupplyCurrentLimitEnable = Constants.Swerve.angleEnableCurrentLimit;
        angleSupplyLimit.SupplyCurrentLimit = Constants.Swerve.angleContinuousCurrentLimit;
        angleSupplyLimit.SupplyCurrentThreshold = Constants.Swerve.anglePeakCurrentLimit;
        angleSupplyLimit.SupplyTimeThreshold = Constants.Swerve.anglePeakCurrentDuration;

        swerveAngleFXConfig.Slot0.kP = Constants.Swerve.angleKP;
        swerveAngleFXConfig.Slot0.kI = Constants.Swerve.angleKI;
        swerveAngleFXConfig.Slot0.kD = Constants.Swerve.angleKD;
        swerveAngleFXConfig.Slot0.kV = Constants.Swerve.angleKF;
        swerveAngleFXConfig.CurrentLimits =angleSupplyLimit;

        /* Swerve Drive Motor Configuration */
        CurrentLimitsConfigs driveSupplyLimit = new CurrentLimitsConfigs();
        driveSupplyLimit.SupplyCurrentLimitEnable = Constants.Swerve.driveEnableCurrentLimit;
        driveSupplyLimit.SupplyCurrentLimit = Constants.Swerve.driveContinuousCurrentLimit;
        driveSupplyLimit.SupplyCurrentThreshold = Constants.Swerve.drivePeakCurrentLimit;
        driveSupplyLimit.SupplyTimeThreshold = Constants.Swerve.drivePeakCurrentDuration;

        swerveDriveFXConfig.Slot0.kP = Constants.Swerve.driveKP;
        swerveDriveFXConfig.Slot0.kI = Constants.Swerve.driveKI;
        swerveDriveFXConfig.Slot0.kD = Constants.Swerve.driveKD;
        swerveDriveFXConfig.Slot0.kV = Constants.Swerve.driveKF;
        swerveDriveFXConfig.CurrentLimits = driveSupplyLimit;   
        swerveDriveFXConfig.OpenLoopRamps.DutyCycleOpenLoopRampPeriod = Constants.Swerve.openLoopRamp;
        swerveDriveFXConfig.ClosedLoopRamps.DutyCycleClosedLoopRampPeriod = Constants.Swerve.closedLoopRamp;
        
        /* Swerve CANCoder Configuration */
        swerveCanCoderConfig.MagnetSensor.withAbsoluteSensorRange(AbsoluteSensorRangeValue.Unsigned_0To1);

        swerveCanCoderConfig.MagnetSensor.withSensorDirection(CounterClockwise_Positive);
    }
}