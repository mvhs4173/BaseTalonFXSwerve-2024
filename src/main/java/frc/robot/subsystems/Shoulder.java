// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.math.controller.ArmFeedforward;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import edu.wpi.first.math.util.Units;

public class Shoulder extends SubsystemBase {
  // left motor will be the main motor: the right will follow it.  Do not set speed on right one.
  private final SparkMaxMotor m_leftMotor = new SparkMaxMotor(Constants.ShoulderConstants.Left.kCANId,
    Constants.ShoulderConstants.encoderRotationsPerFinalRotation,
    Constants.ShoulderConstants.Left.kName);
  private final SparkMaxMotor m_rightMotor = new SparkMaxMotor(Constants.ShoulderConstants.Right.kCANId,
    Constants.ShoulderConstants.encoderRotationsPerFinalRotation,
    Constants.ShoulderConstants.Right.kName);
  private double m_positionAtHorizontal = -0.042177;
  //things related to feedforward for the arm
  private static class FfConstants{
    public static double kS = 0.0;
    public static double kG = 0.56;
    public static double kV = 2.05;
    public static double kA = 0.02;
  };
  ArmFeedforward m_feedforward= new ArmFeedforward(FfConstants.kS, FfConstants.kG, FfConstants.kV, FfConstants.kA);
  /** Creates a new Shoulder. */
  public Shoulder() {
    m_leftMotor.setToBrakeOnIdle(true);
    m_rightMotor.setToBrakeOnIdle(true);
    m_leftMotor.addFollower(m_rightMotor, true);
    m_leftMotor.setPIDCoefficients(Constants.ShoulderConstants.PID.kP,
       Constants.ShoulderConstants.PID.kI,
       Constants.ShoulderConstants.PID.kD,
       Constants.ShoulderConstants.PID.kIZone,
       Constants.ShoulderConstants.PID.kFeedForward,
       Constants.ShoulderConstants.PID.kMinOutput,
       Constants.ShoulderConstants.PID.kMaxOutput);
    m_leftMotor.setAndEnableLowerSoftLimit(Constants.ShoulderConstants.lowerSoftLimit);
    m_leftMotor.setAndEnableUpperSoftLimit(Constants.ShoulderConstants.upperSoftLimit);

  }

  public SparkMaxMotor getSparkMaxMotor(){
    return m_leftMotor;
  }

  public void setRPM(double RPM){
    m_leftMotor.setRPM(RPM);
  }

  public void setPercentSpeed(double percentSpeed){
    m_leftMotor.setPercentSpeed(percentSpeed);
  }

  public void setVoltage(double voltage){
    m_leftMotor.setVoltage(voltage);
  }

  public void holdPosition(){
    setRPM(0.0); // see if this works in both arm raising and chassis raising modes
  }

  public void setCurrentPositionAsZeroEncoderPosition(){
    m_leftMotor.setCurrentPositionAsZeroEncoderPosition();
  }
/**
 * 
 * @return position in rotations clockwise from start position (when looking from left).
 */
  public double getPosition(){
    return m_leftMotor.getPosition();
  }

  /**
   * @return position in degrees clockwise from horizontal (when looking from left).
   */
  public double getPositionDegreesFromHorizontal(){
    return getPositionRotationsFromHorizontal()*360;
  }
  /**
   * 
   * @return position in rotations clockwise from horizontal (when looking from left)
   */
  public double getPositionRotationsFromHorizontal(){
    return getPosition()-m_positionAtHorizontal;
  }
  public double getPositionRadiansFromHorizontal(){
    return getPositionRotationsFromHorizontal()*2.0*Math.PI;
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    SmartDashboard.putNumber("Shoulder Position", getPosition());
    SmartDashboard.putNumber("Shoulder Position Degrees from Horizontal", getPositionDegreesFromHorizontal());
    double voltage = m_feedforward.calculate(getPositionRadiansFromHorizontal(), Units.rotationsToRadians(0.9), 0.0);
    SmartDashboard.putNumber("Voltage", voltage);
  } 
}
