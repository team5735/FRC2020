/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.vision;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.lib.util.DriveSignal;
import frc.lib.util.Util;
import frc.robot.constants.RobotConstants;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Vision;

public class TurnToTargetCommand extends CommandBase {
	@SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
    private final Vision vision;
	private final Drivetrain drivetrain;

	private final PIDController turnPID;
	
	private double inDeadbandTime = -1;
	
	public TurnToTargetCommand(Vision vision, Drivetrain drivetrain) {
        this.vision = vision;
		this.drivetrain = drivetrain;

		this.turnPID = new PIDController(RobotConstants.VISION_STEER_kP, RobotConstants.VISION_STEER_kI, 0);
		turnPID.setTolerance(RobotConstants.VISION_TARGET_DEADBAND);
		//pid.setIntegratorRange(-0.5, 0.5); use this if it "winds up" and overshoots
		
		// Use addRequirements() here to declare subsystem dependencies.
        addRequirements(vision);
        addRequirements(drivetrain);
	}
	
	// Called when the command is initially scheduled.
	@Override
	public void initialize() {
		vision.enableTracking();
		inDeadbandTime = -1;
	}
	
	// Called every time the scheduler runs while the command is scheduled.
	@Override
	public void execute() {
		if(!vision.isTrackingEnabled()) vision.enableTracking();
		System.out.println(vision.getDistanceToTarget());
		if(vision.hasValidTarget() && false) {
			double degreeError = vision.getLimelight().getdegRotationToTarget() + RobotConstants.VISION_X_OFFSET;
			double steer_cmd = Util.limit(turnPID.calculate(degreeError, 0), -1, 1); // sensor value is limelight, setpoint is 0

			// // if(Util.deadband(degreesRotate, RobotConstants.VISION_TARGET_DEADBAND) == 0) end(true);
			
			// double steer_cmd = RobotConstants.VISION_STEER_kP * degreeError;
			// double steer_cmd = Math.copySign(0.08, degreesRotate);
			System.out.println("TURN TO TARGET: " + steer_cmd);
			drivetrain.drive(ControlMode.PercentOutput, new DriveSignal(steer_cmd, -steer_cmd, 0));
			if(Util.deadband(degreeError, RobotConstants.VISION_TARGET_DEADBAND) == 0) {
				inDeadbandTime = Timer.getFPGATimestamp();
			} else {
				inDeadbandTime = -1;
			}
        }
	}
	
	// Called once the command ends or is interrupted.
	@Override
	public void end(boolean interrupted) {
		System.out.println("TURN TARGET COMMAND | END");
		vision.disableTracking();
		drivetrain.drive(ControlMode.PercentOutput, DriveSignal.NEUTRAL);
	}
	
	// Returns true when the command should end.
	@Override
	public boolean isFinished() {
		//		if greater than 0 and	80 milliseconds have passed		and		we are at setpoint
		// return (inDeadbandTime > 0) && (inDeadbandTime + 0.08 < Timer.getFPGATimestamp()) &&
		//  turnPID.atSetpoint();
		return false;
	}
}
