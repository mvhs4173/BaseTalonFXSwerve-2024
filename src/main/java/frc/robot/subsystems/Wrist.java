// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class Wrist extends SubsystemBase {
  private SparkMaxMotor m_motor = new SparkMaxMotor(
    Constants.WristConstants.kCANId,
    Constants.WristConstants.encoderRotationsPerFinalRotation,
    Constants.WristConstants.kName,
    Constants.WristConstants.kMotorType,
    Constants.WristConstants.encoderType,
    Constants.WristConstants.encoderCountsPerRevolution);
  private double m_percentSpeed = 0.0;
  private boolean m_calibrationMode = false;
    
  /** Creates a new Wrist. */
  public Wrist() {
    System.out.println("Creating Wrist object");
    if (m_calibrationMode){
      SmartDashboard.putNumber("Wrist %speed", m_percentSpeed);
    }
    m_motor.setToBrakeOnIdle(true);
  }

  /**
   * 
   * @return rotational position of shooter in rotations counterclockwise
   */
  public double getPosition(){
    return m_motor.getPosition();
  }

  public void setPercentSpeed(double percentSpeed){
    m_percentSpeed = percentSpeed;
    m_motor.setPercentSpeed(m_percentSpeed);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    // Following if for testing only - control from Smartdashboard
    if (m_calibrationMode){  
       m_percentSpeed = SmartDashboard.getNumber("Wrist %speed", 0.0);
       m_motor.setPercentSpeed(m_percentSpeed);
    }
    if(false){
    // safety stuff - never continue to go out of bounds.  Need to get direction from controller.
    // see soft limit stuff to be added to SparkMaxMotor.
    if (getPosition() <= Constants.WristConstants.minSafePosition
      && m_motor.getSparkMax().getAppliedOutput() >= 0) {
        m_percentSpeed = 0;
    } else if (getPosition() >= Constants.WristConstants.maxSafePosition
      && m_motor.getSparkMax().getAppliedOutput() <= 0) {
        m_percentSpeed = 0;
    }
  }

    //m_motor.setPercentSpeed(m_percentSpeed);
  }
}
