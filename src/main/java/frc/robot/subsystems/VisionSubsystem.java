package frc.robot.subsystems;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.CvSink;
import edu.wpi.first.cscore.CvSource;
import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import edu.wpi.first.apriltag.AprilTagDetector;
import edu.wpi.first.apriltag.AprilTagDetection;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class VisionSubsystem extends SubsystemBase {
    private Thread m_visionThread;
    private final AprilTagDetector aprilTagDetector;
    private Pose2d lastEstimatedPose;

    public VisionSubsystem() {
        aprilTagDetector = new AprilTagDetector();
        aprilTagDetector.addFamily("tag36h11", 3);

        m_visionThread = new Thread(() -> {
            UsbCamera camera = CameraServer.startAutomaticCapture();
            camera.setResolution(640, 480);

            CvSink cvSink = CameraServer.getVideo();
            CvSource outputStream = CameraServer.putVideo("Processed", 640, 480);

            Mat mat = new Mat();

            while (!Thread.interrupted()) {
                if (cvSink.grabFrame(mat) == 0) {
                    outputStream.notifyError(cvSink.getError());
                    continue;
                }

                processFrame(mat);

                outputStream.putFrame(mat);
            }
        });
        m_visionThread.setDaemon(true);
        m_visionThread.start();
    }

    private void processFrame(Mat frame) {
        AprilTagDetection[] detections = aprilTagDetector.detect(frame);

        for (AprilTagDetection detection : detections) {
            Imgproc.rectangle(
                frame,
                new Point(detection.getCornerX(0), detection.getCornerY(0)),
                new Point(detection.getCornerX(2), detection.getCornerY(2)),
                new Scalar(0, 255, 0),
                2
            );

            Imgproc.putText(
                frame,
                "ID: " + detection.getId(),
                new Point(detection.getCenterX() - 20, detection.getCenterY()),
                Imgproc.FONT_HERSHEY_SIMPLEX,
                1,
                new Scalar(0, 0, 255),
                2
            );

            // Here you would calculate the pose based on the detection
            // For simplicity, we're just storing the center of the tag as a 2D pose
            lastEstimatedPose = new Pose2d(detection.getCenterX(), detection.getCenterY(), new Rotation2d());
        }
    }

    public Pose2d getEstimatedPose() {
        return lastEstimatedPose;
    }

    public double getDistanceToSpeaker() {
        if (lastEstimatedPose == null) {
            return Double.POSITIVE_INFINITY;
        }
        return lastEstimatedPose.getTranslation().getDistance(Constants.Vision.speakerPosition);
    }

    public boolean hasValidTarget() {
        return lastEstimatedPose != null;
    }
}