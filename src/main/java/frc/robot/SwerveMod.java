package frc.robot;

import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import frc.lib.LazyCANCoder;
import frc.lib.LazyTalonFX;
import frc.lib.math.Conversions;
import frc.lib.util.CTREModuleState;
import frc.lib.util.SwerveModuleConstants;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.controls.PositionDutyCycle;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.hardware.CANcoder;
public class SwerveMod {
    public int moduleNumber;
    private Rotation2d angleOffset;
    private Rotation2d lastAngle;

    private TalonFX mAngleMotor;
    private TalonFX mDriveMotor;
    private CANcoder angleEncoder;
    SimpleMotorFeedforward feedforward = new SimpleMotorFeedforward(Constants.Swerve.driveKS, Constants.Swerve.driveKV, Constants.Swerve.driveKA);

    public SwerveMod(int moduleNumber, SwerveModuleConstants moduleConstants){
        this.moduleNumber = moduleNumber;
        this.angleOffset = moduleConstants.angleOffset;
        
        /* Angle Encoder Config */
        angleEncoder = new LazyCANCoder(
            moduleConstants.cancoderID, 
            Robot.ctreConfigs.swerveCanCoderConfig
        );

        /* Angle Motor Config */
        mAngleMotor = new LazyTalonFX(
            moduleConstants.angleMotorID,
            Robot.ctreConfigs.swerveAngleFXConfig,
            Constants.Swerve.angleNeutralMode,
            Constants.Swerve.angleMotorInvert,
            false
        );

        /* Drive Motor Config */
        mDriveMotor = new LazyTalonFX(
            moduleConstants.driveMotorID,
            Robot.ctreConfigs.swerveDriveFXConfig,
            Constants.Swerve.driveNeutralMode,
            Constants.Swerve.driveMotorInvert,
            false);

        lastAngle = getState().angle;
    }

    public void setDesiredState(SwerveModuleState desiredState, boolean isOpenLoop){
        /* This is a custom optimize function, since default WPILib optimize assumes continuous controller which CTRE and Rev onboard is not */
        desiredState = CTREModuleState.optimize(desiredState, getState().angle); 
        setAngle(desiredState);
        setSpeed(desiredState, isOpenLoop);
    }

    private void setSpeed(SwerveModuleState desiredState, boolean isOpenLoop){
        if(isOpenLoop){
            double percentOutput = desiredState.speedMetersPerSecond / Constants.Swerve.maxSpeed;
            final DutyCycleOut m_talonFXOut = new DutyCycleOut(percentOutput);
            mDriveMotor.setControl(m_talonFXOut);
        }
        else {
            double velocity = Conversions.MPSToFalcon(desiredState.speedMetersPerSecond, Constants.Swerve.wheelCircumference, Constants.Swerve.driveGearRatio);
            final VelocityVoltage m_velocity = new VelocityVoltage(0);
            m_velocity.Slot = 0;
            mDriveMotor.setControl(m_velocity.withVelocity(velocity));
        }
    }

    private void setAngle(SwerveModuleState desiredState){
        Rotation2d angle = (Math.abs(desiredState.speedMetersPerSecond) <= (Constants.Swerve.maxSpeed * 0.01)) ? lastAngle : desiredState.angle; //Prevent rotating module if speed is less then 1%. Prevents Jittering.
        final PositionDutyCycle m_position = new PositionDutyCycle(0);
        mAngleMotor.setControl(m_position.withPosition(angle.getCos()));
        lastAngle = angle;
    }

    private Rotation2d getAngle(){
        var rotorPosSignal = mAngleMotor.getRotorPosition();
        var rotorPos = rotorPosSignal.getValue();
        rotorPosSignal.waitForUpdate(0.020);
        return Rotation2d.fromDegrees(Conversions.falconToDegrees(rotorPos, Constants.Swerve.angleGearRatio));
    }

    public Rotation2d getCanCoder(){
        StatusSignal<Double> AbsolutePosition_Status_Signal_Object=angleEncoder.getAbsolutePosition();
        return Rotation2d.fromDegrees(AbsolutePosition_Status_Signal_Object.getValueAsDouble());
    }

    public void resetToAbsolute(){
        double absolutePosition = Conversions.degreesToFalcon(getCanCoder().getDegrees() - angleOffset.getDegrees(), Constants.Swerve.angleGearRatio);

        mAngleMotor.set(absolutePosition);
    }

    public SwerveModuleState getState(){
        var rotorVelSignal = mDriveMotor.getRotorVelocity();
        var rotorVel = rotorVelSignal.getValue();
        rotorVelSignal.waitForUpdate(0.020);
        return new SwerveModuleState(
            Conversions.falconToMPS(rotorVel, Constants.Swerve.wheelCircumference, Constants.Swerve.driveGearRatio), 
            getAngle()
        ); 
    }

    public SwerveModulePosition getPosition(){
        var rotorPosSignal = mDriveMotor.getRotorPosition();
        var rotorPos = rotorPosSignal.getValue();
        rotorPosSignal.waitForUpdate(0.020);
        return new SwerveModulePosition(
            Conversions.falconToMeters(rotorPos, Constants.Swerve.wheelCircumference, Constants.Swerve.driveGearRatio), 
            getAngle()
        );
    }
}