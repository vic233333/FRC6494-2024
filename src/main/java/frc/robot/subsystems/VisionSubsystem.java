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
import java.util.HashMap;
import java.util.Map;

import org.photonvision.EstimatedRobotPose;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;
import org.photonvision.PhotonPoseEstimator.PoseStrategy;
import org.photonvision.targeting.PhotonTrackedTarget;

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
    private Map<Integer, AprilTagInfo> aprilTagMap = new HashMap<>();

    public void robotInit() {
        // 确保相机开始扫描AprilTag
        camera.setDriverMode(false);

        // 初始化SmartDashboard显示
        SmartDashboard.putData("vision/Field", field);
        
        // 设置updateDashboard为true，确保数据会被输出到SmartDashboard
        updateDashboard = true;
    }

    private class AprilTagInfo {
        public boolean isDetected;
        public double distance;
        public double yaw;
        public double pitch;

        public AprilTagInfo() {
            this.isDetected = false;
            this.distance = 0;
            this.yaw = 0;
            this.pitch = 0;
        }
    }

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

        // Initialize AprilTag info for tags 1-16
        for (int i = 1; i <= 16; i++) {
            aprilTagMap.put(i, new AprilTagInfo());
        }
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
        
        // Reset all AprilTags to not detected
        for (AprilTagInfo info : aprilTagMap.values()) {
            info.isDetected = false;
        }

        haveTarget = result.isPresent();
        if (haveTarget) {
            EstimatedRobotPose pose = result.get();
            lastEstTimestamp = pose.timestampSeconds;
            lastPose = pose.estimatedPose.toPose2d();
            field.setRobotPose(lastPose);

            for (PhotonTrackedTarget target : pose.targetsUsed) {
                int id = target.getFiducialId();
                if (aprilTagMap.containsKey(id)) {
                    AprilTagInfo info = aprilTagMap.get(id);
                    info.isDetected = true;
                    info.distance = target.getBestCameraToTarget().getTranslation().getNorm();
                    info.yaw = target.getYaw();
                    info.pitch = target.getPitch();
                }
            }

            // 假设AprilTag是3, 4, 7和8（您可能需要调整这个）
            haveSpeakerTarget = pose.targetsUsed.stream()
                .anyMatch(target -> target.getFiducialId() == 3 || target.getFiducialId() == 4 ||
                                    target.getFiducialId() == 7 || target.getFiducialId() == 8);
        }

        if (updateDashboard) {
            updateSmartDashboard();
        }
    }

    private void updateSmartDashboard() {
        SmartDashboard.putBoolean("vision/Have target(s)", haveTarget);
        SmartDashboard.putBoolean("vision/Have speaker target", haveSpeakerTarget);
        SmartDashboard.putNumber("vision/distance", distanceToSpeakerFromCenter());
        SmartDashboard.putString("vision/Last pose", lastPose.toString());
        SmartDashboard.putNumber("vision/Angle error", angleError().getDegrees());
    
        for (int i = 1; i <= 16; i++) {
            AprilTagInfo info = aprilTagMap.get(i);
            String prefix = "vision/AprilTag " + i + "/";
            
            if (info.isDetected) {
                SmartDashboard.putString(prefix + "Status", "Target Found");
                SmartDashboard.putNumber(prefix + "Distance", info.distance);
                SmartDashboard.putNumber(prefix + "Yaw", info.yaw);
                SmartDashboard.putNumber(prefix + "Pitch", info.pitch);
            } else {
                SmartDashboard.putString(prefix + "Status", "Target Lost");
                SmartDashboard.putString(prefix + "Distance", "/");
                SmartDashboard.putString(prefix + "Yaw", "/");
                SmartDashboard.putString(prefix + "Pitch", "/");
            }
        }
    }
}