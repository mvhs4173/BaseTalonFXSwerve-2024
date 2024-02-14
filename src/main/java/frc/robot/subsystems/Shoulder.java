// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.revrobotics.CANSparkBase.IdleMode;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class Shoulder extends SubsystemBase {
  // left motor will be the main motor: the right will follow it.  Do not set speed on right one.
  private final SparkMaxMotor m_leftMotor = new SparkMaxMotor(Constants.ShoulderConstants.Left.kCANId,
    Constants.ShoulderConstants.encoderRotationsPerFinalRotation,
    Constants.ShoulderConstants.Left.kName);
  private final SparkMaxMotor m_rightMotor = new SparkMaxMotor(Constants.ShoulderConstants.Right.kCANId,
    Constants.ShoulderConstants.encoderRotationsPerFinalRotation,
    Constants.ShoulderConstants.Right.kName);
  /** Creates a new Shoulder. */
  public Shoulder() {
    m_leftMotor.getSparkMax().setIdleMode(IdleMode.kBrake);
    m_rightMotor.getSparkMax().setIdleMode(IdleMode.kBrake);
    m_leftMotor.addFollower(m_rightMotor, true);
    m_leftMotor.setPIDCoefficients(Constants.ShoulderConstants.PID.kP,
       Constants.ShoulderConstants.PID.kI,
       Constants.ShoulderConstants.PID.kD,
       Constants.ShoulderConstants.PID.kIZone,
       Constants.ShoulderConstants.PID.kFeedForward,
       Constants.ShoulderConstants.PID.kMinOutput,
       Constants.ShoulderConstants.PID.kMaxOutput);
  }

  public SparkMaxMotor getSparkMaxMotor(){
    return m_leftMotor;
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  } 
}
