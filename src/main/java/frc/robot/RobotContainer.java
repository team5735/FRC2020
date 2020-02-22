/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj2.command.Command;
import frc.lib.controllers.BobXboxController;
import frc.robot.commands.drivetrain.ChangeDriveMode;
import frc.robot.commands.drivetrain.DriveFollowTrajectory;
import frc.robot.commands.drivetrain.ResetGyroAngle;
import frc.robot.commands.intake.AngleIntakeCommand;
import frc.robot.commands.intake.IntakeBallCommand;
import frc.robot.commands.intake.MoveConveyorCommand;
import frc.robot.commands.shooter.RampShooterCommand;
import frc.robot.constants.RobotConstants;
import frc.robot.subsystems.Climber;
import frc.robot.subsystems.ColorMatcher;
import frc.robot.subsystems.ColorSpinner;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Shooter;
import frc.robot.subsystems.TrajectoryGenerator;

/**
* This class is where the bulk of the robot should be declared.  Since Command-based is a
* "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
* periodic methods (other than the scheduler calls).  Instead, the structure of the robot
* (including subsystems, commands, and button mappings) should be declared here.
*/
public class RobotContainer {
	// The robot's subsystems and commands are defined here...
	public final ColorMatcher colorMatcher = new ColorMatcher();
	public final ColorSpinner colorSpinner = new ColorSpinner();
	public final Drivetrain drivetrain = new Drivetrain();
	public final Climber climber = new Climber();
	public final Shooter shooter = new Shooter();
	public final Intake intake = new Intake();
	public final TrajectoryGenerator trajectoryGenerator = new TrajectoryGenerator();
	
	public static final BobXboxController driverController = new BobXboxController(0);
	public static final BobXboxController subsystemController = new BobXboxController(1);
	
	/**
	* The container for the robot.  Contains subsystems, OI devices, and commands.
	*/
	public RobotContainer() {
		// Configure the button bindings
		configureDriverBindings();
		// configureSubsystemBindings();
	}
	
	private void configureDriverBindings() {
		// subsystemController.xButton.whenPressed(new ColorSpinCommand(colorSpinner, 4));
		// subsystemController.bButton.whenPressed(new ColorMatchCommand(colorSpinner, colorMatcher));
		subsystemController.yButton.whenPressed(new ResetGyroAngle(drivetrain));
		subsystemController.xButton.whenPressed(new ChangeDriveMode(drivetrain));
		
		subsystemController.aButton.whenPressed(new RampShooterCommand(shooter, 4500));
		subsystemController.aButton.whenReleased(new RampShooterCommand(shooter, 0));
		
		subsystemController.rightTriggerButton.toggleWhenActive(new IntakeBallCommand(intake, subsystemController.triggers.getRight(), false));
		subsystemController.leftTriggerButton.toggleWhenActive(new IntakeBallCommand(intake, subsystemController.triggers.getLeft(), true));
		
		subsystemController.rightBumper.toggleWhenActive(new MoveConveyorCommand(intake, false));
		subsystemController.leftBumper.toggleWhenActive(new MoveConveyorCommand(intake, true));
		
		subsystemController.Dpad.Up.whenPressed(new AngleIntakeCommand(intake, RobotConstants.INTAKE_POSITION_RETRACTED));
		subsystemController.Dpad.Down.whenPressed(new AngleIntakeCommand(intake, RobotConstants.INTAKE_POSITION_DEPLOYED));
		//   trajectoryGenerator.getTrajectorySet().sideStartToNearScale.left, (DrivetrainTrajectory)drivetrain));
	}
	
	/**
	* Use this to pass the autonomous command to the main {@link Robot} class.
	*
	* @return the command to run in autonomous
	*/
	public Command getAutonomousCommand() {
		return new DriveFollowTrajectory(drivetrain);
	}
	
}
