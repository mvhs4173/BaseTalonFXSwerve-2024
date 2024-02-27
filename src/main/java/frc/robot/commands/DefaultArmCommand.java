// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Shoulder;

public class DefaultArmCommand extends Command {
  private Shoulder m_shoulder;
  private double m_desiredShoulderRPM;
  private XboxController m_controller;
  /** Creates a new DefaultArmCommand. */
  public DefaultArmCommand(Shoulder shoulder, XboxController controller) {
    m_shoulder = shoulder;
    m_desiredShoulderRPM = 0;
    m_controller = controller;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(m_shoulder);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    double right = m_controller.getRightTriggerAxis();
    double left  = m_controller.getLeftTriggerAxis();
    if (right > 0 && left > 0) {
      System.out.println("Do not press right and left triggers at same time");
      m_desiredShoulderRPM = 0.0;
    } else if (right > 0){
      m_desiredShoulderRPM = right * 24;
    } else if (left > 0){
      m_desiredShoulderRPM = -left * 24;
    } else {
      m_desiredShoulderRPM = 0.0;
    }
    m_shoulder.getSparkMaxMotor().setRPM(m_desiredShoulderRPM);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    m_shoulder.getSparkMaxMotor().setPercentSpeed(0.0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
