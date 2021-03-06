/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.constants.RobotConstants;

public class Telescope extends SubsystemBase {
  private final TalonSRX elevatorMaster;

  /**
   * Creates a new Climber.
   */
  public Telescope() {
    elevatorMaster = new TalonSRX(RobotConstants.TELESCOPE_ID);
    elevatorMaster.configFactoryDefault();
    elevatorMaster.setInverted(false);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

  public void moveElevator(double output) {
    elevatorMaster.set(ControlMode.PercentOutput, output);
  }
}