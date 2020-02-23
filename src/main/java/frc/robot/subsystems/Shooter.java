/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.ControlType;

import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.lib.util.Util;
import frc.robot.constants.RobotConstants;

public class Shooter extends SubsystemBase {
	
	private CANSparkMax neoMaster, neoSlave;
	private CANPIDController flywheelPIDController;

	private double speedSetpoint;
	
	/**
	* Creates a new Shooter.
	*/
	public Shooter() {
		
		neoMaster = new CANSparkMax(33, MotorType.kBrushless);
		neoMaster.restoreFactoryDefaults();
		neoMaster.setInverted(false);
		
		neoSlave = new CANSparkMax(52, MotorType.kBrushless);
		neoSlave.restoreFactoryDefaults();
		neoSlave.follow(neoMaster, true);
		
		neoMaster.getEncoder().setVelocityConversionFactor(RobotConstants.FLYWHEEL_PULLEY_RATIO);
		
		flywheelPIDController = neoMaster.getPIDController();
		
		flywheelPIDController.setP(RobotConstants.FLYWHEEL_kP, 0);
		flywheelPIDController.setI(RobotConstants.FLYWHEEL_kI, 0);
		flywheelPIDController.setD(RobotConstants.FLYWHEEL_kD, 0);
		flywheelPIDController.setFF(RobotConstants.FLYWHEEL_kF, 0);
	}
	
	@Override
	public void periodic() {
		// This method will be called once per scheduler run
	}
	
	public void setSpeed(double rpm) {
		double speed = rpm;
		if(rpm > RobotConstants.FLYWHEEL_MAX_SPEED) speed = RobotConstants.FLYWHEEL_MAX_SPEED;
		if(rpm < RobotConstants.FLYWHEEL_MIN_SPEED) speed = RobotConstants.FLYWHEEL_MIN_SPEED;
		speedSetpoint = speed;
		flywheelPIDController.setReference(speed, ControlType.kVelocity);
	}

	public double getSpeed() {
		return neoMaster.getEncoder().getVelocity();
	}

	public boolean atSpeed(double threshold) {
		return Util.deadband(speedSetpoint - getSpeed(), threshold) == 0;
	}

	public void slowDown() {
		neoMaster.set(0);
		speedSetpoint = 0;
	}

	/**
	 * Function to convert a distance into flywheel speed
	 * @param distance Horizontal distance to target, in meters
	 * @return Flywheel speed, in RPM
	 */
	public double getSpeedFromDistance(double distance) {
		// TODO
		return 100;
	}
	
}
