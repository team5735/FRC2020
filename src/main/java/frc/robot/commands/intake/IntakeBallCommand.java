/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.intake;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.RobotContainer;
import frc.robot.subsystems.Intake;

/**
* An example command that uses an example subsystem.
*/
public class IntakeBallCommand extends CommandBase {
	@SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
	private final Intake intake;
	private final double speed;
	private final boolean inverted;
	
	/**
	* Creates a new ExampleCommand.
	*
	* @param subsystem The subsystem used by this command.
	*/

	public IntakeBallCommand(Intake intake) {
		this.intake = intake;
		this.speed = -1;
		this.inverted = false;
		// addRequirements(intake);
	}

	public IntakeBallCommand(Intake intake, double speed, boolean inverted) {
		this.intake = intake;
		this.speed = speed;
		this.inverted = inverted;
		// Use addRequirements() here to declare subsystem dependencies.
		// addRequirements(intake);
	}
	
	// Called when the command is initially scheduled.
	@Override
	public void initialize() {
		System.out.println("INTAKING BALL"); 
	}
	
	// Called every time the scheduler runs while the command is scheduled.
	@Override
	public void execute() {
		if(speed < 0) {
			// take controller input
			double rightTrigger = 0.5 * RobotContainer.driverController.triggers.getRight();
			double leftTrigger = 0.5 * RobotContainer.driverController.triggers.getLeft();
			if(rightTrigger > 0) {
				intake.intakeBall(rightTrigger, false);
			} else if (leftTrigger > 0) {
				intake.intakeBall(leftTrigger, true);
			}
		} else {
			intake.intakeBall(speed, inverted);
		}
	}
	
	// Called once the command ends or is interrupted.
	@Override
	public void end(boolean interrupted) {
		System.out.println("INTAKE BALL COMMAND | END");
		intake.intakeBall(0, false);
	}
	
	// Returns true when the command should end.
	@Override
	public boolean isFinished() {
		return false;
	}
}
