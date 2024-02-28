package frc.robot;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.Trigger;
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
    /* Arm/note handler controller buttons are defined in configureButtonBindings below */
    /* Subsystems */
    private final Swerve s_Swerve = TuningVariables.useSwerve.getBoolean() ? new Swerve() : null; // set s_Swerve to null when testing arm & shooter alone
    private final Shoulder m_shoulder = TuningVariables.useShoulder.getBoolean() ? new Shoulder() : null;
    private final Shooter m_shooter = TuningVariables.useShooter.getBoolean() ? new Shooter() : null;
    private final Wrist m_wrist = TuningVariables.useWrist.getBoolean() ? new Wrist() : null;
    private final BeamBreakSensor m_BeamBreakSensor = new BeamBreakSensor();
    private final CollectorRoller m_CollectorRoller = TuningVariables.useCollectorRoller.getBoolean() ? new CollectorRoller() : null;

    /* Autos */
    private final SendableChooser<Command> m_chooser = new SendableChooser<>();
    private final 
    Command m_Auto_1 = null;
        //put auto routine here. You may change the name so that it is more fitting than simpleAuto 
    private final Command m_Auto_2 = null;
        //put auto routine here. You may change the name so that it is more fitting than complexAuto
    
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
        if (m_shoulder != null){
            m_shoulder.setDefaultCommand(new DefaultArmCommand(m_shoulder, m_armController));
        }

        //Add commands to the autonomous command chooser
        m_chooser.setDefaultOption("Simple Auto", new exampleAuto(s_Swerve));
        m_chooser.addOption("Another Auto", m_Auto_1);
        m_chooser.addOption("A Third Auto", m_Auto_2);
        //Put the chooser on the dashboard
        SmartDashboard.putData(m_chooser);

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
        /* Start assuming the m_armController is not null */
        JoystickButton armA = new JoystickButton(m_armController, XboxController.Button.kA.value);
        // JoystickButton armB = new JoystickButton(m_armController, XboxController.Button.kB.value);
        // JoystickButton armX = new JoystickButton(m_armController, XboxController.Button.kX.value);
        // JoystickButton armY = new JoystickButton(m_armController, XboxController.Button.kY.value);
        JoystickButton armLeftBumper = new JoystickButton(m_armController, XboxController.Button.kLeftBumper.value);
        JoystickButton armRightBumper = new JoystickButton(m_armController, XboxController.Button.kRightBumper.value);
        // JoystickButton armStart = new JoystickButton(m_armController, XboxController.Button.kStart.value);
        // JoystickButton armBack = new JoystickButton(m_armController, XboxController.Button.kBack.value);

        Trigger armLeftTrigger = new Trigger(() -> m_armController.getLeftTriggerAxis() > 0.5 );
        Trigger armRightTrigger = new Trigger(() -> m_armController.getRightTriggerAxis() > 0.5);

        Command goToCollectionPosition = new ParallelCommandGroup(
            new MoveShoulderTo(m_shoulder, 0.0, 5.0, 1.0, 0.0)
               .until(() -> Math.abs(m_shoulder.getPosition() - 0.0) < 0.01),
            new WristGoToPosition(m_wrist, 0.6, 0)
        ).withTimeout(3.0);
        Command goToSpeakerShotPosition = new ParallelCommandGroup(
            new MoveShoulderTo(m_shoulder, 0.0, 5.0, 1.0, 0.0)
                .until(() -> Math.abs(m_shoulder.getPosition() - 0.0) < 0.01),
            new WristGoToPosition(m_wrist, 0.7, 0.30)
        ).withTimeout(3.0);
        Command goToAmpShotPosition = new ParallelCommandGroup(
            new MoveShoulderTo(m_shoulder, -.25, 5.0, 1.0, 0.0)
                .until(() -> Math.abs(m_shoulder.getPosition() - (-0.25)) < 0.01),
            new WristGoToPosition(m_wrist, 0.7, 0.20)
        ).withTimeout(3.0);
        Command shootForSpeaker = new Shoot(m_shooter, 5500.0);
        Command shootForAmp = new Shoot(m_shooter, 2000.0);
        Command doIntake = 
          new IntakeUntilBeamBreak(m_CollectorRoller, m_BeamBreakSensor, m_shooter, 750.0).withTimeout(5.0);
        
        armA.onTrue(doIntake);

        armLeftBumper.whileTrue(goToSpeakerShotPosition);
        armLeftBumper.onFalse(goToCollectionPosition);
        armRightBumper.whileTrue(shootForSpeaker);

        armLeftTrigger.whileTrue(goToAmpShotPosition);
        armLeftTrigger.onFalse(goToCollectionPosition);
        armRightTrigger.whileTrue(shootForAmp);

        // Now for manual control of arm and wrist
        // While left joystick is pushed forward, shoulder goes up at constant speed
        new Trigger(() -> m_armController.getLeftY() < -0.5)
          .whileTrue(new SetShoulderSpeed(m_shoulder, -5.0));
        // While left joystick pushed backward, shoulder goes down at constant speed
        new Trigger(() -> m_armController.getLeftY() > 0.5)
          .whileTrue(new SetShoulderSpeed(m_shoulder, 5.0));
        // Right joystick controls wrist in similar way
        new Trigger(() -> m_armController.getRightY() < -0.5)
          .whileTrue(new SetWristPercentSpeed(m_wrist, 0.6));
        new Trigger(() -> m_armController.getRightY() > 0.5)
          .whileTrue(new SetWristPercentSpeed(m_wrist, -0.5));
    }

    /**
     * Use this to pass the autonomous command to the main {@link Robot} class.
     *
     * @return the command to run in autonomous
     */
    public Command getAutonomousCommand() {
        // An ExampleCommand will run in autonomous
        return m_chooser.getSelected();
    }
}
