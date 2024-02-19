package frc.robot;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
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
    private final XboxController m_driveController = 
      TuningVariables.useDriveController.getBoolean() ? new XboxController(0) : null;
    private final XboxController m_armController = 
      TuningVariables.useArmController.getBoolean() 
        ? new XboxController(TuningVariables.useDriveController.getBoolean() ? 1 : 0)
        : null;

    /* Drive Controls */
    private final int translationAxis = XboxController.Axis.kLeftY.value;
    private final int strafeAxis = XboxController.Axis.kLeftX.value;
    private final int rotationAxis = XboxController.Axis.kRightX.value;

    /* Driver controller Buttons */
    private final JoystickButton zeroGyro = m_driveController != null ? new JoystickButton(m_driveController, XboxController.Button.kY.value) : null;
    private final JoystickButton robotCentric = m_driveController != null ? new JoystickButton(m_driveController, XboxController.Button.kLeftBumper.value) : null;

    /* Subsystems */
    private final Swerve s_Swerve = TuningVariables.useSwerve.getBoolean() ? new Swerve() : null; // set s_Swerve to null when testing arm & shooter alone
    private final Shoulder m_shoulder = TuningVariables.useShoulder.getBoolean() ? new Shoulder() : null;
    private final Shooter m_shooter = TuningVariables.useShooter.getBoolean() ? new Shooter() : null;
    private final Wrist m_wrist = TuningVariables.useWrist.getBoolean() ? new Wrist() : null;
    /** The container for the robot. Contains subsystems, OI devices, and commands. */
    public RobotContainer() {
        if (s_Swerve == null){
            System.out.println("No drivetrain object was created - check TuningVariables in Smartdashboard");
        }
        if (m_shoulder == null){
            System.out.println("No shoulder motor objects were created - check TuningVariables in Smartdashboard");
        }
        if (m_driveController == null){
            System.out.println("No driver's Xbox controller was created, arm controller will be in port 0 - check TuningVariabls in Smartdashboard");
        } else {
            System.out.println("Driver's Xbox controller should be attached to port " + m_driveController.getPort());
        }
        if (m_armController == null) {
            System.out.println("No arm Xbox controller was created - check TurningVariables in Smartdashboard");
        } else {
            System.out.println("Note manipulator's Xbox controller should be attached to port " + m_armController.getPort());
        }
        if (s_Swerve != null) s_Swerve.setDefaultCommand(
            new TeleopSwerve(
                s_Swerve, 
                () -> -m_driveController.getRawAxis(translationAxis), 
                () -> -m_driveController.getRawAxis(strafeAxis), 
                () -> -m_driveController.getRawAxis(rotationAxis), 
                () -> robotCentric.getAsBoolean()
            )
        );

        // Configure the button bindings
        configureButtonBindings();
        SmartDashboard.putData("Remove all preferences", new InstantCommand(TuningVariables::removeAllPreferences)); 
        SmartDashboard.putData("Set All TuningVariables to default values", new InstantCommand(TuningVariables::setAllToDefaultValues)); 
    }

    /**
     * Use this method to define your button->command mappings. Buttons can be created by
     * instantiating a {@link GenericHID} or one of its subclasses ({@link
     * edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then passing it to a {@link
     * edu.wpi.first.wpilibj2.command.button.JoystickButton}.
     */
    private void configureButtonBindings() {
        /* Driver Buttons */
        if (s_Swerve != null && zeroGyro != null){
          zeroGyro.onTrue(new InstantCommand(() -> s_Swerve.zeroHeading()));
        }
        if (m_armController != null){
            if (m_wrist != null) {
                new JoystickButton(m_armController, XboxController.Button.kB.value)
                   .onTrue(new WristGoToPosition(m_wrist, 0.5, .20)); 
                new JoystickButton(m_armController, XboxController.Button.kA.value)
                   .onTrue(new WristGoToPosition(m_wrist, 0.5, .22));             
            }
            if (m_shoulder != null){
                new JoystickButton(m_armController, XboxController.Button.kLeftBumper.value)
                    .whileTrue(new SetShoulderSpeed(m_shoulder, 5.0)); // 5 rpm => 3.0 seconds to travel 90 degrees
                new JoystickButton(m_armController, XboxController.Button.kRightBumper.value)
                    .whileTrue(new SetShoulderSpeed(m_shoulder, -5.0));
                new JoystickButton(m_armController, XboxController.Button.kStart.value)
                    .onTrue(new InstantCommand( () -> m_shoulder.getSparkMaxMotor().setCurrentPositionAsZeroEncoderPosition()));
            }
        }
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
}
