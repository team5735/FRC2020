/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.shooter;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.lib.util.Util;
import frc.robot.constants.RobotConstants;
import frc.robot.subsystems.Banana;
import frc.robot.subsystems.Shooter;
import frc.robot.subsystems.Vision;

public class RampShooterCommand extends CommandBase {
	@SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
	private final Shooter shooter;
	private final Vision vision;
	private final Banana banana;
	private double rpm;
	
	public RampShooterCommand(Shooter shooter, Vision vision, Banana banana, double rpm) {
		this.shooter = shooter;
		this.vision = vision;
		this.banana = banana;
		this.rpm = rpm;
		
		// Use addRequirements() here to declare subsystem dependencies.
		addRequirements(this.shooter);
	}
	
	// Called when the command is initially scheduled.
	@Override
	public void initialize() {
		// TODO Get interpolation working
		// double distance = vision.getDistanceToTarget();
		// double rpm = 0;
		// if(distance < 4.9) {
		// 	// line, or near line
		// 	rpm = RobotConstants.FLYWHEEL_PRESET_LINE;
		// } else {
		// 	rpm = RobotConstants.FLYWHEEL_PRESET_TRENCH;
		// }

		// rpm = SmartDashboard.getNumber("RPM", 0);
		SmartDashboard.putNumber("RPM Setpoint", rpm);
		if(rpm == 0) {
			System.out.println("RPM 0");
			shooter.slowDown();
			banana.retract();
		} else {
			shooter.setSpeed(rpm); // only need to call once?
		}

		// TODO: Adjust banana as necessary, LOOKUP BANANA POS WITH RPM
		// double bananaPos = //shooter.getSpeedFromDistance(distance);
		// banana.moveBanana(ControlMode.Position, bananaPos);
	}
	
	// Called every time the scheduler runs while the command is scheduled.
	@Override
	public void execute() {
		SmartDashboard.putNumber("Shooter Speed (RPM)", shooter.getSpeed());
		// double speed = shooter.getSpeed();
	}
	
	// Called once the command ends or is interrupted.
	@Override
	public void end(boolean interrupted) {
		System.out.println("RAMP SHOOTER COMMAND | END");
	}
	
	// Returns true when the command should end.
	@Override
	public boolean isFinished() {
		return shooter.atSpeed(RobotConstants.FLYWHEEL_RPM_DEADBAND);
		// return false;
	}
}