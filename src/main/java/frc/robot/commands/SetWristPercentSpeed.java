// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.subsystems.Wrist;

public class SetWristPercentSpeed extends Command {
  private Wrist m_wrist;
  private double m_percentSpeed;
  private Trigger m_overrideSoftLimitsTrigger;
  private boolean m_softLimitsAreDisabled;
  
  /**
   * Creates a new SetWristPercentSpeed command
   * @param wrist - a wrist object
   * @param percentSpeed - a number in [-1,1] to control speed
   */
  public SetWristPercentSpeed(Wrist wrist, double percentSpeed) {
    m_wrist = wrist;
    m_percentSpeed = percentSpeed;
    m_overrideSoftLimitsTrigger = null;
    m_softLimitsAreDisabled = false;
    addRequirements(m_wrist);
  }
    /**
   * Creates a new SetWristPercentSpeed command
   * @param wrist - a wrist object
   * @param percentSpeed - a number in [-1,1] to control speed
   * @param overrideSoftLimitsTrigger
   */
  public SetWristPercentSpeed(Wrist wrist, double percentSpeed, Trigger overrideSoftLimitsTrigger){
    this(wrist, percentSpeed);
    m_overrideSoftLimitsTrigger = overrideSoftLimitsTrigger;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    m_wrist.setPercentSpeed(m_percentSpeed);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    boolean disableSoftLimits = m_overrideSoftLimitsTrigger != null && m_overrideSoftLimitsTrigger.getAsBoolean();
    if (disableSoftLimits && !m_softLimitsAreDisabled){
      m_softLimitsAreDisabled = true;
      m_wrist.getSparkMaxMotor().disableSoftLimits();
    } else if (!disableSoftLimits && m_softLimitsAreDisabled){
      m_wrist.getSparkMaxMotor().enableSoftLimits();
      m_softLimitsAreDisabled = false;
    }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    m_wrist.setPercentSpeed(0.0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
