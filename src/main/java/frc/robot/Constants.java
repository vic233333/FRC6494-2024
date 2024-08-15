    package frc.robot;

import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;
import com.ctre.phoenix6.signals.SensorDirectionValue;

import edu.wpi.first.apriltag.AprilTagPoseEstimator;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.math.util.Units;
import frc.lib.util.COTSTalonFXSwerveConstants;
import frc.lib.util.COTSTalonFXSwerveConstants.SDS;
import frc.lib.util.SwerveModuleConstants;

public final class Constants {
    public static final double stickDeadband = 0.1;

    public static final class Swerve {
        public static final double stickDeadband = 0.1; // 手柄死区设置， Claude提供不保证正常运行

        public static final int pigeonID = 0;

        /* heading PID Values */ // TODO: update heading's PID
        public static final double headingKP = 0.1;
        public static final double headingKI = 0.0;
        public static final double headingKD = 0.0;

        public static final COTSTalonFXSwerveConstants chosenModule =  
            new COTSTalonFXSwerveConstants(
                SDS.MK4i.KrakenX60(SDS.MK4i.driveRatios.L2).wheelDiameter,
                SDS.MK4i.KrakenX60(SDS.MK4i.driveRatios.L2).angleGearRatio,
                SDS.MK4i.KrakenX60(SDS.MK4i.driveRatios.L2).driveGearRatio,
                SDS.MK4i.Falcon500(SDS.MK4i.driveRatios.L2).angleKP,
                SDS.MK4i.Falcon500(SDS.MK4i.driveRatios.L2).angleKI,
                SDS.MK4i.Falcon500(SDS.MK4i.driveRatios.L2).angleKD,
                SDS.MK4i.KrakenX60(SDS.MK4i.driveRatios.L2).driveMotorInvert,       //isInverted false
                SDS.MK4i.Falcon500(SDS.MK4i.driveRatios.L2).angleMotorInvert,       //isInverted true
                SensorDirectionValue.Clockwise_Positive  // 可能需要根据实际情况调整
            );

        /* Can Bus name */
        public static final String kCANivoreBusName = "canivore";

        /* Drivetrain Constants */
        public static final double trackWidth = 0.6075; //TODO: This must be tuned to specific robot
        public static final double wheelBase = 0.6075; //TODO: This must be tuned to specific robot
        public static final double wheelCircumference = chosenModule.wheelCircumference;

        /* Swerve Kinematics 
         * No need to ever change this unless you are not doing a traditional rectangular/square 4 module swerve */
         public static final SwerveDriveKinematics swerveKinematics = new SwerveDriveKinematics(
            new Translation2d(wheelBase / 2.0, trackWidth / 2.0),
            new Translation2d(wheelBase / 2.0, -trackWidth / 2.0),
            new Translation2d(-wheelBase / 2.0, trackWidth / 2.0),
            new Translation2d(-wheelBase / 2.0, -trackWidth / 2.0));

        /* Module Gear Ratios */
        public static final double driveGearRatio = chosenModule.driveGearRatio;
        public static final double angleGearRatio = chosenModule.angleGearRatio;

        /* Motor Inverts */
        public static final InvertedValue angleMotorInvert = chosenModule.angleMotorInvert;
        public static final InvertedValue driveMotorInvert = chosenModule.driveMotorInvert;

        /* Angle Encoder Invert */
        public static final SensorDirectionValue cancoderInvert = chosenModule.cancoderInvert;

        /* Swerve Current Limiting */
        public static final int angleCurrentLimit = 25;
        public static final int angleCurrentThreshold = 40;
        public static final double angleCurrentThresholdTime = 0.1;
        public static final boolean angleEnableCurrentLimit = true;

        public static final int driveCurrentLimit = 35;
        public static final int driveCurrentThreshold = 60;
        public static final double driveCurrentThresholdTime = 0.1;
        public static final boolean driveEnableCurrentLimit = true;

        /* These values are used by the drive falcon to ramp in open loop and closed loop driving.
         * We found a small open loop ramp (0.25) helps with tread wear, tipping, etc */
        public static final double openLoopRamp = 0.25;
        public static final double closedLoopRamp = 0.0;

        /* Angle Motor PID Values */
        public static final double angleKP = chosenModule.angleKP;
        public static final double angleKI = chosenModule.angleKI;
        public static final double angleKD = chosenModule.angleKD;

        /* Drive Motor PID Values */
        public static final double driveKP = 0.25; //TODO: This must be tuned to specific robot
        public static final double driveKI = 0.0;
        public static final double driveKD = 0.0;
        public static final double driveKF = 0.0;

        /* Drive Motor Characterization Values From SYSID */
        public static final double driveKS = 0.32; //TODO: This must be tuned to specific robot
        public static final double driveKV = 1.51;
        public static final double driveKA = 0.27;

        /* Swerve Profiling Values */
        /** Meters per Second */
        public static final double maxSpeed = 4.5; //TODO: This must be tuned to specific robot
        /** Radians per Second */
        public static final double maxAngularVelocity = 10.0; //TODO: This must be tuned to specific robot

        /* Neutral Modes */
        public static final NeutralModeValue angleNeutralMode = NeutralModeValue.Coast;
        public static final NeutralModeValue driveNeutralMode = NeutralModeValue.Brake;

        /* Module Specific Constants */
        /* Front Left Module - Module 0 */
        public static final class Mod0 { //TODO: This must be tuned to specific robot
                    /* Neutral Modes */
            public static final int driveMotorID = 11;
            public static final int angleMotorID = 12;
            public static final int canCoderID = 1;
            public static final Rotation2d angleOffset = Rotation2d.fromDegrees(-65.742);
            public static final SwerveModuleConstants constants = 
                new SwerveModuleConstants(driveMotorID, angleMotorID, canCoderID, angleOffset);
        }

        /* Front Right Module - Module 1 */
        public static final class Mod1 { //TODO: This must be tuned to specific robot
            public static final int driveMotorID = 21;
            public static final int angleMotorID = 22;
            public static final int canCoderID = 2;
            public static final Rotation2d angleOffset = Rotation2d.fromDegrees(124.101);
            public static final SwerveModuleConstants constants = 
                new SwerveModuleConstants(driveMotorID, angleMotorID, canCoderID, angleOffset);
        }
        
        /* Back Left Module - Module 2 */
        public static final class Mod2 { //TODO: This must be tuned to specific robot
            public static final int driveMotorID = 31;
            public static final int angleMotorID = 32;
            public static final int canCoderID = 3;
            public static final Rotation2d angleOffset = Rotation2d.fromDegrees(-25.312);
            public static final SwerveModuleConstants constants = 
                new SwerveModuleConstants(driveMotorID, angleMotorID, canCoderID, angleOffset);
        }

        /* Back Right Module - Module 3 */
        public static final class Mod3 { //TODO: This must be tuned to specific robot
            public static final int driveMotorID = 41;
            public static final int angleMotorID = 42;
            public static final int canCoderID = 4;
            public static final Rotation2d angleOffset = Rotation2d.fromDegrees(147.216);
            public static final SwerveModuleConstants constants = 
                new SwerveModuleConstants(driveMotorID, angleMotorID, canCoderID, angleOffset);
        }
    }

    public static final class Vision {
        public static final String cameraName = "Webcam_C170";
        public static final Translation3d cameraTranslation = new Translation3d(0.5, 0, 0.5); // TODO: 需要根据实际情况调整
        public static final Rotation3d cameraRotation = new Rotation3d(0, Math.toRadians(-30), 0); // TODO: 需要根据实际情况调整
        public static final Translation2d speakerPosition = new Translation2d(8.308, 4.105); // TODO: 2024场地中扬声器的位置，需要确认

        public static final Transform3d CAMERA_TO_ROBOT = new Transform3d(
            cameraTranslation, 
            cameraRotation
        );
    }

    public static final class Shooter { //TODO: 修正shooter数据
        public static final int leftMotorID = 5; 
        public static final int rightMotorID = 6;
        public static final String motorCanBus = "canivore";
        public static final double kP = 0.1;
        public static final double kI = 0.0;
        public static final double kD = 0.0;
        public static final double kS = 0.0;
        public static final double RPMsPerVolt = 500.0;
        public static final double allowedRPMError = 50.0;
        public static final double optimalShootingDistance = 2.0; // TODO: 最佳射击距离
        public static final double leftMotorOutput = 1.0; // 左发射马达转速
        public static final double rightMotorOutput = -1.0; // 右发射马达转速
    }

    public static final class AutoConstants { //TODO: The below constants are used in the example auto, and must be tuned to specific robot
        public static final double kMaxSpeedMetersPerSecond = 3;
        public static final double kMaxAccelerationMetersPerSecondSquared = 3;
        public static final double kMaxAngularSpeedRadiansPerSecond = Math.PI;
        public static final double kMaxAngularSpeedRadiansPerSecondSquared = Math.PI;

        // Automatic constants
        public static final int kAprilTagId7 = 7;
        public static final int kAprilTagId8 = 8;

        public static final double kP = 10.0;
        public static final double kI = 0.0;
        public static final double kD = 0.0;

        public static final double kPXController = 1;
        public static final double kPYController = 1;
        public static final double kPThetaController = 1;

        public static final double tolerance  = 0.05;

        public static final double initTranslationX = -2.0;
        public static final double initTranslationY = 0.0;
        public static final double initTranslationRotation = 0.0;
    
        /* Constraint for the motion profilied robot angle controller */
        public static final TrapezoidProfile.Constraints kThetaControllerConstraints =
            new TrapezoidProfile.Constraints(
                kMaxAngularSpeedRadiansPerSecond, kMaxAngularSpeedRadiansPerSecondSquared);
    }
}
