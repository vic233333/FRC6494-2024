package frc.robot.subsystems;

import frc.robot.SwerveModule;
import frc.robot.Constants;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.controller.PIDController;

import com.ctre.phoenix6.configs.Pigeon2Configuration;
import com.ctre.phoenix6.hardware.Pigeon2;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Swerve extends SubsystemBase {
    public SwerveDriveOdometry swerveOdometry;
    public SwerveModule[] mSwerveMods;
    public Pigeon2 gyro;

    private PIDController headingController;
    private Rotation2d targetHeading;
    private boolean isFieldRelative = true;  
    private final Timer m_timer = new Timer();
    public Swerve() {
        gyro = new Pigeon2(Constants.Swerve.pigeonID, Constants.Swerve.kCANivoreBusName);
        gyro.getConfigurator().apply(new Pigeon2Configuration());
        gyro.setYaw(0);

        headingController = new PIDController(Constants.Swerve.headingKP, Constants.Swerve.headingKI, Constants.Swerve.headingKD);
        headingController.enableContinuousInput(-Math.PI, Math.PI);
        targetHeading = new Rotation2d();

        mSwerveMods = new SwerveModule[] {
            new SwerveModule(0, Constants.Swerve.Mod0.constants),
            new SwerveModule(1, Constants.Swerve.Mod1.constants),
            new SwerveModule(2, Constants.Swerve.Mod2.constants),
            new SwerveModule(3, Constants.Swerve.Mod3.constants)
        };

        swerveOdometry = new SwerveDriveOdometry(Constants.Swerve.swerveKinematics, getGyroYaw(), getModulePositions());
        m_timer.restart();
        while(m_timer.get()<1.0)
            for(SwerveModule mod : mSwerveMods){
                mod.resetDesiredState(new SwerveModuleState(0, mod.getState().angle), true);
            }
    }

    public void drive(Translation2d translation, double rotation, boolean fieldRelative, boolean isOpenLoop) {
        this.isFieldRelative = fieldRelative;
        if (Math.abs(translation.getX()) < Constants.stickDeadband 
            && Math.abs(translation.getY()) < Constants.stickDeadband 
            && Math.abs(rotation) < Constants.stickDeadband) {
            zeroOutput();
            return;
        }

        double headingCorrection = 0;
        if (Math.abs(rotation) < Constants.stickDeadband) {
            headingCorrection = headingController.calculate(getHeading().getRadians(), targetHeading.getRadians());
        } else {
            targetHeading = getHeading();
        }

        SwerveModuleState[] swerveModuleStates =
            Constants.Swerve.swerveKinematics.toSwerveModuleStates(
                fieldRelative ? ChassisSpeeds.fromFieldRelativeSpeeds(
                                    translation.getX(), 
                                    translation.getY(), 
                                    rotation + headingCorrection, 
                                    getHeading()
                                )
                                : new ChassisSpeeds(
                                    translation.getX(), 
                                    translation.getY(), 
                                    rotation + headingCorrection)
                                );
        SwerveDriveKinematics.desaturateWheelSpeeds(swerveModuleStates, Constants.Swerve.maxSpeed);
        for(SwerveModule mod : mSwerveMods){
            mod.setDesiredState(swerveModuleStates[mod.moduleNumber], isOpenLoop);
        }
        

    }

    /* Used by SwerveControllerCommand in Auto */
    public void setModuleStates(SwerveModuleState[] desiredStates) {
        SwerveDriveKinematics.desaturateWheelSpeeds(desiredStates, Constants.Swerve.maxSpeed);
        
        for(SwerveModule mod : mSwerveMods){
            mod.setDesiredState(desiredStates[mod.moduleNumber], false);
        }
    }

    public SwerveModuleState[] getModuleStates(){
        SwerveModuleState[] states = new SwerveModuleState[4];
        for(SwerveModule mod : mSwerveMods){
            states[mod.moduleNumber] = mod.getState();
        }
        return states;
    }

    public SwerveModulePosition[] getModulePositions(){
        SwerveModulePosition[] positions = new SwerveModulePosition[4];
        for(SwerveModule mod : mSwerveMods){
            positions[mod.moduleNumber] = mod.getPosition();
        }
        return positions;
    }
    public void reset1(){
        m_timer.restart();
        while(m_timer.get()<1.0)
            for(SwerveModule mod : mSwerveMods){
                mod.resetDesiredState(new SwerveModuleState(0, mod.getState().angle), true);
            }
        resetModulesToAbsolute();
    }
    public Pose2d getPose() {
        return swerveOdometry.getPoseMeters();
    }

    public void setPose(Pose2d pose) {
        swerveOdometry.resetPosition(getGyroYaw(), getModulePositions(), pose);
    }

    public Rotation2d getHeading(){
        return getPose().getRotation();
    }

    public void setHeading(Rotation2d heading){
        swerveOdometry.resetPosition(getGyroYaw(), getModulePositions(), new Pose2d(getPose().getTranslation(), heading));
    }

    public void zeroHeading(){
        swerveOdometry.resetPosition(getGyroYaw(), getModulePositions(), new Pose2d(getPose().getTranslation(), new Rotation2d()));
    }

    public Rotation2d getGyroYaw() {
        return Rotation2d.fromDegrees(gyro.getYaw().getValue());
    }

    public void resetModulesToAbsolute(){
        for(SwerveModule mod : mSwerveMods){
            mod.resetToAbsolute();
        }
    }

    public void stop() {
        drive(new Translation2d(0, 0), 0, true, true);
    }

    public void zeroOutput() {
        for(SwerveModule mod : mSwerveMods){
            mod.resetDesiredState(new SwerveModuleState(0, mod.getState().angle), true);
        }
    }
    
    public void setTargetHeading(Rotation2d heading) {
        targetHeading = heading;
    }

    @Override
    public void periodic(){
        swerveOdometry.update(getGyroYaw(), getModulePositions());

        for(SwerveModule mod : mSwerveMods){
            SmartDashboard.putNumber("Mod " + mod.moduleNumber + " CANcoder", mod.getCANcoder().getDegrees());
            SmartDashboard.putNumber("Mod " + mod.moduleNumber + " Angle", mod.getState().angle.getDegrees());
            SmartDashboard.putNumber("Mod " + mod.moduleNumber + " RealAngle", mod.getAngle().getDegrees());
            SmartDashboard.putNumber("Mod " + mod.moduleNumber + " Velocity", mod.getState().speedMetersPerSecond);    
        }
        SmartDashboard.putNumber("Gyro Yaw", getGyroYaw().getDegrees());
        SmartDashboard.putNumber("Target Heading", targetHeading.getDegrees());
        SmartDashboard.putNumber("Heading Error", targetHeading.minus(getHeading()).getDegrees());

        SmartDashboard.putString("Drive Mode", isFieldRelative ? "Field Relative" : "Robot Centric");
    }
}