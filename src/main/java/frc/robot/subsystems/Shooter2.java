// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
/**
 * The shooter built for the second competition.  Has top and bottom rollers
 * at its mouth and a roller near the back to pull the note just out of the range
 * of the main intake motors so the main motors can get up to speed before
 * contacting the note.
 */
public class Shooter2 extends SubsystemBase {
  // For all motors, positive voltage intakes, negative shoots.
  SparkMaxMotor m_lowerRoller;
  SparkMaxMotor m_upperRoller;
  SparkMaxMotor m_indexer;
  /** Creates a new Shooter2. */
  public Shooter2() {
    m_lowerRoller = new SparkMaxMotor(
      Constants.Shooter2Constants.LowerRoller.kCANid,
      Constants.Shooter2Constants.LowerRoller.encoderRotationsPerFinalRotation,
      Constants.Shooter2Constants.LowerRoller.name);
    m_upperRoller = new SparkMaxMotor(
      Constants.Shooter2Constants.UpperRoller.kCANid,
      Constants.Shooter2Constants.UpperRoller.encoderRotationsPerFinalRotation,
      Constants.Shooter2Constants.UpperRoller.name);
    m_indexer = new SparkMaxMotor(
      Constants.Shooter2Constants.Indexer.kCANid,
      Constants.Shooter2Constants.Indexer.encoderRotationsPerFinalRotation,
      Constants.Shooter2Constants.Indexer.name);
  }

  /** 
   * Set speed of front rollers for intake
   * @param percentSpeed - speed on a scale of [0,1].  Bigger number mean faster intake.
  */
  public void setMainRollerPercentSpeedForIntake(double percentSpeed){
    m_lowerRoller.setPercentSpeed(percentSpeed);
    m_upperRoller.setPercentSpeed(percentSpeed);
  }
  /**
   * Set speed of rear roller (the "indexer") for intake 
   * @param percentSpeed
   */
  public void setIndexerPercentSpeedForIntake(double percentSpeed){
    m_indexer.setPercentSpeed(percentSpeed);
  }

  /** 
   * Set speed of front rollers for shooting
   * @param percentSpeed - speed on a scale of [0,1].  Bigger number mean faster shot.
  */
  public void setMainRollerPercentSpeedForShooting(double percentSpeed){
    percentSpeed = -percentSpeed;
    m_lowerRoller.setPercentSpeed(percentSpeed);
    m_upperRoller.setPercentSpeed(percentSpeed);
  }
  /**
   * Set speed of rear roller (the "indexer") for shooting
   * @param percentSpeed - speed on a scale of [0,1].  Bigger number means faster shot.
   */
  public void setIndexerPercentSpeedForShooting(double percentSpeed){
    percentSpeed = -percentSpeed;
    m_indexer.setPercentSpeed(percentSpeed);
  }
  /**
   * Cut power to main rollers
   */
  public void stopMainRoller(){
    setMainRollerPercentSpeedForShooting(0.0);
  }

  /** Cut power to indexer */
  public void stopIndexer(){
    setIndexerPercentSpeedForShooting(0.0);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

  public Command shoot2Command(){
    Command command =
          new InstantCommand(() -> setMainRollerPercentSpeedForShooting(0.6))
              .wait(1000)
              .andThen(new InstantCommand(() -> setIndexerPercentSpeedForShooting(.6)))
              .wait(1000)
              .andThen(new InstantCommand(() -> stopIndexer()))
              .andThen(new InstantCommand(() -> stopMainRoller()));
    return command;
  }
}
