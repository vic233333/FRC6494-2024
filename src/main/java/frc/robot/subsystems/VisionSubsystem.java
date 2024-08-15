package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.io.IOException;
import java.util.Optional;

import org.photonvision.EstimatedRobotPose;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;
import org.photonvision.PhotonPoseEstimator.PoseStrategy;

public class VisionSubsystem extends SubsystemBase {
    private static VisionSubsystem instance;
    private final PhotonCamera camera;
    private final PhotonPoseEstimator photonEstimator;
    private AprilTagFieldLayout kTagLayout;
    private final Field2d field = new Field2d();
    private double lastEstTimestamp = 0.0;
    private boolean haveTarget = false;
    private boolean haveSpeakerTarget = false;
    private Pose2d lastPose = new Pose2d();
    private boolean updateDashboard = true;

    public VisionSubsystem() {
        camera = new PhotonCamera(Constants.Vision.cameraName);

        try {
            kTagLayout = new AprilTagFieldLayout(Filesystem.getDeployDirectory().toPath().resolve("2024-crescendo-hq.json"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        photonEstimator = new PhotonPoseEstimator(kTagLayout, PoseStrategy.MULTI_TAG_PNP_ON_COPROCESSOR, camera, Constants.Vision.CAMERA_TO_ROBOT);
        photonEstimator.setMultiTagFallbackStrategy(PoseStrategy.LOWEST_AMBIGUITY);

        SmartDashboard.putData("vision/Field", field);
    }

    public static VisionSubsystem getInstance() {
        if (instance == null) {
            instance = new VisionSubsystem();
        }
        return instance;
    }

    public boolean haveTarget() {
        return haveTarget;
    }

    public boolean haveSpeakerTarget() {
        return haveSpeakerTarget;
    }

    private Translation2d speakerOffset() {
        return lastPose.getTranslation().minus(Constants.Vision.speakerPosition);
    }

    public Rotation2d angleToSpeaker() {
        return speakerOffset().getAngle();
    }

    public Rotation2d angleError() {
        if (!haveSpeakerTarget) {
            return new Rotation2d(0.0);
        }

        Rotation2d speakerAngle = angleToSpeaker();
        Rotation2d robotAngle = lastPose.getRotation();

        return speakerAngle.minus(robotAngle);
    }

    public double distanceToSpeakerFromCenter() {
        return lastPose.getTranslation().getDistance(Constants.Vision.speakerPosition);
    }

    @Override
    public void periodic() {
        Optional<EstimatedRobotPose> result = photonEstimator.update();
        
        haveTarget = result.isPresent();
        if (haveTarget) {
            EstimatedRobotPose pose = result.get();
            lastEstTimestamp = pose.timestampSeconds;
            lastPose = pose.estimatedPose.toPose2d();
            field.setRobotPose(lastPose);

            // Assuming speaker tags are 3, 4, 7, and 8 (you may need to adjust this)
            haveSpeakerTarget = pose.targetsUsed.stream()
                .anyMatch(target -> target.getFiducialId() == 3 || target.getFiducialId() == 4 ||
                                    target.getFiducialId() == 7 || target.getFiducialId() == 8);
        }

        if (updateDashboard) {
            SmartDashboard.putBoolean("vision/Have target(s)", haveTarget);
            SmartDashboard.putBoolean("vision/Have speaker target", haveSpeakerTarget);
            SmartDashboard.putNumber("vision/distance", distanceToSpeakerFromCenter());
            SmartDashboard.putString("vision/Last pose", lastPose.toString());
            SmartDashboard.putNumber("vision/Angle error", angleError().getDegrees());
        }
    }
}