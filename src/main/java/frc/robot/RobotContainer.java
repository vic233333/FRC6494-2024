package frc.robot;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
// import edu.wpi.first.wpilibj.PS4Controller;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

import frc.robot.autos.*;
import frc.robot.commands.*;
import frc.robot.subsystems.*;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {
    /* Controllers */
    private final Joystick driver = new Joystick(0);

    /* Drive Controls */
    // 平移：左摇杆
    private final int translationAxisX = XboxController.Axis.kLeftY.value;
    private final int strafeAxis = XboxController.Axis.kLeftX.value;
    // 旋转：右摇杆X轴
    private final int rotationAxis = XboxController.Axis.kRightX.value;
    // 收球：LT左扳机
    private final int intaker = XboxController.Axis.kLeftTrigger.value;
    // 发球：RT右扳机
    private final int shooter = XboxController.Axis.kRightTrigger.value;

    /* Driver Buttons */
    // 重置陀螺仪：A
    private final JoystickButton zeroGyro = new JoystickButton(driver, XboxController.Button.kA.value);
    // 参考系切换-以机器人为参考系↔以场地为参考系：X
    private final JoystickButton robotCentric = new JoystickButton(driver, XboxController.Button.kX.value);
    // 爬升：Y
    private final JoystickButton climber = new JoystickButton(driver, XboxController.Button.kY.value);
    // 自动对准AprilTag：B
    private final JoystickButton alignToAprilTag = new JoystickButton(driver, XboxController.Button.kB.value);
    
    /* Track the state of robot-centric control */
    private boolean isRobotCentric = false;
    
    /* Subsystems */
    private final Swerve s_Swerve = new Swerve();
    private final ShooterSubsystem s_Shooter = new ShooterSubsystem();
    private final VisionSubsystem s_Vision = new VisionSubsystem();
    private final IntakerSubsysem s_Intaker = new IntakerSubsysem();
    /** The container for the robot. Contains subsystems, OI devices, and commands. */
    public RobotContainer() {
        s_Swerve.setDefaultCommand(
            new TeleopSwerve(
                s_Swerve,
                () -> -driver.getRawAxis(translationAxisX),
                () -> -driver.getRawAxis(strafeAxis),
                () -> -driver.getRawAxis(rotationAxis),
                () -> !robotCentric.getAsBoolean()
            )
        );

        // Configure the button bindings
        configureButtonBindings();

        CommandScheduler.getInstance().registerSubsystem(s_Swerve);
        CommandScheduler.getInstance().registerSubsystem(s_Shooter);
        CommandScheduler.getInstance().registerSubsystem(s_Vision);
    }

    /**
     * Use this method to define your button->command mappings. Buttons can be created by
     * instantiating a {@link GenericHID} or one of its subclasses ({@link
     * edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then passing it to a {@link
     * edu.wpi.first.wpilibj2.command.button.JoystickButton}.
     */
    private void configureButtonBindings() {
        /* Driver Buttons */
        zeroGyro.onTrue(new InstantCommand(() -> s_Swerve.zeroHeading()));
        
        // 射击按钮功能
        if(driver.getRawAxis(shooter)  >0.3 )
            new InstantCommand(() -> s_Shooter.Shoot());
        else 
            new InstantCommand(() -> s_Shooter.stop());
        
        // Intake 功能
        if(driver.getRawAxis(intaker)  >0.3 )
            new InstantCommand(() -> s_Intaker.setIntakerSpeed());
        else 
            new InstantCommand(() -> s_Intaker.stop());
        // Climber 功能
        climber.onTrue(new InstantCommand(() -> {/* TODO: 在这里添加 climber 功能 */}));

        // 切换 robotCentric 模式
        robotCentric.onTrue(new InstantCommand(() -> isRobotCentric = !isRobotCentric));
        
        // 对准 AprilTag 功能
        alignToAprilTag.whileTrue(new AlignToAprilTagCommand(s_Swerve, s_Vision));
    }

    /**
     * Use this to pass the autonomous command to the main {@link Robot} class.
     *
     * @return the command to run in autonomous
     */
    public Command getAutonomousCommand() {
        // An ExampleCommand will run in autonomous
        return new exampleAuto(s_Swerve);
    }

    // Getter methods for subsystems
    public Swerve getSwerve() {
        return s_Swerve;
    }

    public ShooterSubsystem getShooter() {
        return s_Shooter;
    }

    public VisionSubsystem getVision() {
        return s_Vision;
    }
}