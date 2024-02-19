// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Wrist;
/**
 * Move wrist to desired position (rotations counterclockwise from zero,
 * when looking at robot from its right).
 */
public class WristGoToPosition extends Command {
  private Wrist m_wrist;
  private double m_percentSpeed;
  private double m_desiredPosition;
  /** Creates a new WristGoToPosition. */
  public WristGoToPosition(Wrist wrist, double absoluteValuePercentSpeed, double desiredPosition) {
    if (absoluteValuePercentSpeed < 0.0) {
      throw new Error("Wrist's absolutteValuePercentSpeed must be nonnegative, not " + absoluteValuePercentSpeed);
    }
    m_wrist = wrist;
    m_desiredPosition = desiredPosition;
    m_percentSpeed = m_desiredPosition > m_wrist.getPosition() ? absoluteValuePercentSpeed : -absoluteValuePercentSpeed;
    addRequirements(m_wrist);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    m_wrist.setPercentSpeed(m_percentSpeed);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    System.out.println("%speed="+m_percentSpeed+", at "+ m_wrist.getPosition()
    +", to"+m_desiredPosition);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    m_wrist.setPercentSpeed(0.0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    boolean finished = m_percentSpeed >= 0
      ? m_wrist.getPosition() >= m_desiredPosition
      : m_wrist.getPosition() <= m_desiredPosition;
    System.out.println("   finished="+finished);
    return finished;
  }
}
