// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Shoulder;

public class SetShoulderSpeed extends Command {
  private Shoulder m_shoulder;
  private double m_speed;
  /** Creates a new SetShoulderSpeed. */
  public SetShoulderSpeed(Shoulder shoulder, double speed) {
    m_shoulder = shoulder;
    m_speed = speed;
    addRequirements(m_shoulder);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    System.out.println("setting shoulder speed to " + m_speed);
    m_shoulder.getSparkMaxMotor().setRPM(m_speed);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {}

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    System.out.println("stopping shoulder motor");
    m_shoulder.getSparkMaxMotor().setRPM(0.0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}