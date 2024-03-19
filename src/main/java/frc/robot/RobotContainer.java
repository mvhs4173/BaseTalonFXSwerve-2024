package frc.robot;

//import com.pathplanner.lib.auto.NamedCommands;
//import com.pathplanner.lib.commands.PathPlannerAuto;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
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
    private final Shooter2 m_shooter2 = TuningVariables.useShooter2.getBoolean() ? new Shooter2() : null;
    private final Wrist2 m_wrist2 = TuningVariables.useWrist2.getBoolean() ? new Wrist2() : null;
    private final BeamBreakSensor m_BeamBreakSensor = new BeamBreakSensor();
    private final CollectorRoller m_CollectorRoller = TuningVariables.useCollectorRoller.getBoolean() ? new CollectorRoller() : null;
    private final ClimberServo m_climberServo = new ClimberServo(0);
    /* Autos */
    private final SendableChooser<Command> m_chooser = new SendableChooser<>();
    //put auto routines here.  Give them understandable names.
    //In RobotContainer(), make an m_chooser.addOption() entry for each auto.
    //null means no auto.

    private final Command autoGoToAmpShotPosition = new ParallelCommandGroup(
            new ShoulderGoToPosition(m_shoulder, ShoulderGoToPosition.Method.kRPM, 10.0, -0.185),
            new Wrist2GoToPosition(m_wrist2, 0.06, 0.23)
        ).withTimeout(7.0);
    
    private final Command m_Auto_1 = new SequentialCommandGroup(
      new ampAuto(s_Swerve),
      autoGoToAmpShotPosition,
      m_shooter2.shoot2ForAmpCommand().withTimeout(5.0)
    );
    //private final Command m_Blue1AmpShotAuto = new PathPlannerAuto("Blue1AmpShotAuto");
    //private final Command m_Blue2AmpShotAuto = new PathPlannerAuto("Blue2AmpShotAuto");     
    /** The container for the robot. Contains subsystems, OI devices, and commands. */
    public RobotContainer() {
        TuningVariables.setAllToDefaultValues();
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
                () -> robotCentric.getAsBoolean())
            );
            
        //registerNamedPathPlannerCommands();

        //Add commands to the autonomous command chooser
        m_chooser.setDefaultOption("Leave Starting Zone", new exampleAuto(s_Swerve));
        m_chooser.addOption("Amp Auto", m_Auto_1);
        //m_chooser.addOption("Amp Shot Auto", m_Blue1AmpShotAuto);
        //m_chooser.addOption("Amp Shot 2 Auto", m_Blue2AmpShotAuto);
        //Put the chooser on the dashboard
        SmartDashboard.putData(m_chooser);

        // Configure the button bindings
        configureButtonBindings();
        SmartDashboard.putData("Remove all preferences", new InstantCommand(TuningVariables::removeAllPreferences)); 
        SmartDashboard.putData("Set All TuningVariables to default values", new InstantCommand(TuningVariables::setAllToDefaultValues)); 
    }

    private void registerNamedPathPlannerCommands(){
      //PathPlanner autos use "named commands", which must be registered
      Command goToAmpShotPosition = new ParallelCommandGroup(
        new ShoulderGoToPosition(m_shoulder, ShoulderGoToPosition.Method.kRPM, 10.0, -0.185),
        new Wrist2GoToPosition(m_wrist2, 0.06, 0.23)
      ).withTimeout(7.0);
      Command shoot2ForAmp = m_shooter2.shoot2ForAmpCommand();
      Command goToCollectionPosition = new ParallelCommandGroup(
        new ShoulderGoToPosition(m_shoulder, ShoulderGoToPosition.Method.kRPM, 6.0, 0.0),
        new Wrist2GoToPosition(m_wrist2, 0.06, 0)
      ).withTimeout(3.0);
      Command doIntake = m_shooter2.intake2UntilBeamBreak(m_CollectorRoller, m_BeamBreakSensor); 

      //NamedCommands.registerCommand("GoToAmpPosition", goToAmpShotPosition);
      //NamedCommands.registerCommand("Shoot", shoot2ForAmp);
      //NamedCommands.registerCommand("GoToCollectionPosition", goToCollectionPosition);
      //NamedCommands.registerCommand("Collect", doIntake);      
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
        JoystickButton armB = new JoystickButton(m_armController, XboxController.Button.kB.value);
        // JoystickButton armX = new JoystickButton(m_armController, XboxController.Button.kX.value)
        JoystickButton armY = new JoystickButton(m_armController, XboxController.Button.kY.value);
        JoystickButton armLeftBumper = new JoystickButton(m_armController, XboxController.Button.kLeftBumper.value);
        JoystickButton armRightBumper = new JoystickButton(m_armController, XboxController.Button.kRightBumper.value);
        JoystickButton armStart = new JoystickButton(m_armController, XboxController.Button.kStart.value);
        JoystickButton armBack = new JoystickButton(m_armController, XboxController.Button.kBack.value);
        JoystickButton armRightStick = new JoystickButton(m_armController, XboxController.Button.kRightStick.value);

        Trigger armLeftTrigger = new Trigger(() -> m_armController.getLeftTriggerAxis() > 0.5 );
        Trigger armRightTrigger = new Trigger(() -> m_armController.getRightTriggerAxis() > 0.5);

        Command goToCollectionPositionFromSpeaker = new ParallelCommandGroup(
            new ShoulderGoToPosition(m_shoulder, ShoulderGoToPosition.Method.kRPM, 0.1, 0.0),
            new Wrist2GoToPosition(m_wrist2, 0.2, 0)
        ).withTimeout(3.0);
        Command goToCollectionPositionFromAmp = new ParallelCommandGroup(
            new ShoulderGoToPosition(m_shoulder, ShoulderGoToPosition.Method.kRPM, 8.0, 0.0),
            new Wrist2GoToPosition(m_wrist2, 0.1, 0)
        ).withTimeout(4.0);
        Command goToSpeakerShotPosition = new ParallelCommandGroup(
            new ShoulderGoToPosition(m_shoulder, ShoulderGoToPosition.Method.kRPM, 0.1, 0.0),
            new Wrist2GoToPosition(m_wrist2, 0.25, 0.215)
        ).withTimeout(7.0);
        Command goToAmpShotPosition = new ParallelCommandGroup(
            new ShoulderGoToPosition(m_shoulder, ShoulderGoToPosition.Method.kRPM, 8.0, -0.185),
            new Wrist2GoToPosition(m_wrist2, 0.25, 0.23)
        ).withTimeout(7.0);
        // Command gotoClimbPosition = that the arm should be straight up and the shooter should be parallel with the arm, folded inside it. 
        Command goToClimbPosition =  new ParallelCommandGroup(
            new ShoulderGoToPosition(m_shoulder, ShoulderGoToPosition.Method.kRPM, 5.0, -0.22),
            new Wrist2GoToPosition(m_wrist2, 0.03, 0.240)
        ).withTimeout(7.0);
        Command shootForSpeaker = m_shooter2.shoot2ForSpeakerCommand(); // full blast (c. 6000)
        Command shootForAmp = m_shooter2.shoot2ForAmpCommand();
        Command doIntake = m_shooter2.intake2UntilBeamBreak(m_CollectorRoller, m_BeamBreakSensor);
        
        armA.onTrue(doIntake);
        armB.whileTrue(new InstantCommand(() -> m_CollectorRoller.pushOut()));
        armB.onFalse(new InstantCommand(() -> m_CollectorRoller.stop()));

        armLeftBumper.whileTrue(goToSpeakerShotPosition);
        armLeftBumper.onFalse(goToCollectionPositionFromSpeaker);
        armRightBumper.whileTrue(shootForSpeaker);


        armLeftTrigger.whileTrue(goToAmpShotPosition);
        armLeftTrigger.onFalse(goToCollectionPositionFromAmp);
        armRightTrigger.whileTrue(shootForAmp);

        // Now for climbing control.  Climbing requires much more power in shoulder than shooting does.
        // Y button, while held down, causes arm to rise to vertical.
        armY.whileTrue(goToClimbPosition);
        // bottom sectors of POV are for climbing.  SW is weakest (use for engaging the chain),
        // S can raise robot on hook two (from the top), SE can raise robot on topmost hook.
        // All will raise as long as button is pressed.
        new Trigger (() -> m_armController.getPOV() == 225)
          .whileTrue(new SetShoulderRPM(m_shoulder, 5.0));
        new Trigger(() -> m_armController.getPOV() == 180)
          .whileTrue(new SetShoulderRPM(m_shoulder, 10.0));
        new Trigger (() -> m_armController.getPOV()== 135)
          .whileTrue(new SetShoulderRPM(m_shoulder,13.0));
        // depressing top of POV engages ratchet - cannot be undone 
        new Trigger(() -> m_armController.getPOV() == 0)
          .onTrue(new InstantCommand(() -> m_climberServo.setAngle(60)));

        // Now for manual control of arm and wrist
        // While left joystick is pushed forward, shoulder goes up at constant speed
        // While the 'back' button is pressed the soft limits on position will be ignored.
        new Trigger(() -> m_armController.getLeftY() < -0.5)
          .whileTrue(new SetShoulderRPM(m_shoulder, -3.0, armBack));
        // While left joystick pushed backward, shoulder goes down at constant speed
        new Trigger(() -> m_armController.getLeftY() > 0.5)
          .whileTrue(new SetShoulderRPM(m_shoulder, 3.0, armBack));
        // Right joystick controls wrist in similar way
        new Trigger(() -> m_armController.getRightY() < -0.5)
          .whileTrue(new SetWrist2PercentSpeed(m_wrist2, 0.05, armBack));
        new Trigger(() -> m_armController.getRightY() > 0.5)
          .whileTrue(new SetWrist2PercentSpeed(m_wrist2, -0.04, armBack));
        // press right stick button to hold wrist at current position
        armRightStick.onTrue(new InstantCommand(m_wrist2::holdPosition, m_wrist2));
        armStart.onTrue(
            new InstantCommand(() -> m_wrist2.setCurrentPositionAsZeroEncoderPosition())
            .andThen(new InstantCommand(() -> m_shoulder.setCurrentPositionAsZeroEncoderPosition())));
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