// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Shoulder;

public class SetShoulderVoltage extends Command {
  private Shoulder m_shoulder;
  private double m_voltage;
  /** Creates a new SetShoulderVoltage. */
  public SetShoulderVoltage(Shoulder shoulder, double voltage) {
    // Use addRequirements() here to declare subsystem dependencies.
    m_shoulder = shoulder;
    m_voltage = voltage;
    addRequirements(m_shoulder);
    addRequirements(m_shoulder);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    System.out.println("setting shoulder speed to " + m_voltage);
    m_shoulder.getSparkMaxMotor().setVoltage(m_voltage);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {}

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    System.out.println("stopping shoulder motor " + m_voltage);
    m_shoulder.getSparkMaxMotor().setVoltage(0.0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
