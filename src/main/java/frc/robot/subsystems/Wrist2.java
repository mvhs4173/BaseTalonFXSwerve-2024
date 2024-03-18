// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class Wrist2 extends SubsystemBase {
  private SparkMaxMotor m_motor;
  private double m_percentSpeed;

  /** Creates a new Wrist2, the joint between the arm and the shooter2.
   * This is powered by a brushless NEW with a planetary gearbox and chain-drive sprockets
   */
  public Wrist2() {
    m_motor = new SparkMaxMotor(Constants.Wrist2Constants.kCANId,
                                Constants.Wrist2Constants.encoderRotationsPerFinalRotation,
                                Constants.Wrist2Constants.name);
    m_motor.setToBrakeOnIdle(true);
    m_motor.setAndEnableLowerSoftLimit(Constants.Wrist2Constants.lowerSoftLimit); // 0.0
    m_motor.setAndEnableUpperSoftLimit(Constants.Wrist2Constants.upperSoftLimit); // 0.28 or so
    m_percentSpeed = 0.0;
  }

  /**
   * Set speed on scale of [-1,1].  I think positive causes clockwise rotation when viewed from the robot's left.
   * Be very careful - speeds around 0.04 will raise wrist from hanging straight down to about horizontal, where it
   * stalls.  Much higher will let it go to straight up, at which point 0.04 will cause it to move fast.
   * @param percentSpeed - speed on scale of [-1,1]
   */
  public void setPercentSpeed(double percentSpeed){
    m_percentSpeed = percentSpeed;
  }

  /**
   * Cut power to wrist motor.  Shooter may droop, relying only on brake mode to hold it in place.
   * (There is not holdPosition method yet.)
   */
  public void stop(){
    m_percentSpeed = 0.0;
  }

  /**
   * @return position of shooter in number rotations of axle from 0
   */
  public double getPosition(){
    return m_motor.getPosition();
  }

  /**
   * @return velocity in rotations of wrist axle per minute
   */
  public double getVelocity(){
    return m_motor.getVelocity();
  }

  public SparkMaxMotor getSparkMaxMotor(){
    return m_motor;
  }

  public void setCurrentPositionAsZeroEncoderPosition(){
    m_motor.setCurrentPositionAsZeroEncoderPosition();
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    m_motor.setPercentSpeed(m_percentSpeed);
  }
}
