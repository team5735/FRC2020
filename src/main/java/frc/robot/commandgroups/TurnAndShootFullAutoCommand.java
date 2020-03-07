package frc.robot.commandgroups;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.ParallelRaceGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.RobotContainer;
import frc.robot.commands.shooter.RampShooterCommand;
import frc.robot.commands.shooter.StopFlywheel;
import frc.robot.commands.vision.TurnOffLimelightCommand;
import frc.robot.commands.vision.TurnToTargetCommand;
import frc.robot.constants.RobotConstants;
import frc.robot.subsystems.Banana;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Drivetrain.DriveMode;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Shooter;
import frc.robot.subsystems.Vision;

public class TurnAndShootFullAutoCommand extends SequentialCommandGroup {

    private Vision vision;
    private Drivetrain drivetrain;
    private Intake intake;
    private Shooter shooter;
    private Banana banana;


    /**
     * Turn to target, ramp up shooter, feed the shooter, and go!
     * @param vision
     * @param drivetrain
     * @param shooter
     */
    public TurnAndShootFullAutoCommand(Vision vision, Drivetrain drivetrain, Intake intake, Shooter shooter, Banana banana) {
        this.vision = vision;
        this.drivetrain = drivetrain;
        this.intake = intake;
        this.shooter = shooter;
        this.banana = banana;
        
        // double distance = vision.getDistanceToTarget();
        addCommands(
            // https://docs.wpilib.org/en/latest/docs/software/commandbased/command-groups.html
            // new ParallelCommandGroup( 
            //     new TurnToTargetCommand(vision, drivetrain),
            //     new RampShooterCommand(shooter, vision, banana)  
            // )//,
            new TurnAndPrepareCommand(vision, drivetrain, intake, shooter, banana),
            new ShootBallCommand(intake, shooter, false),
            new ShootBallCommand(intake, shooter, false),
            new ShootBallCommand(intake, shooter, false),
            // new ShootBallCommand(intake, shooter, false),
            new StopFlywheel(shooter),
            new TurnOffLimelightCommand(vision)
        );
    } 

    @Override
    public void initialize() {
        super.initialize();
        drivetrain.setDriveMode(DriveMode.DISABLED);
    }

    @Override
    public void end(boolean interrupted) {
        super.end(interrupted);
        drivetrain.setDriveMode(DriveMode.STATIC_DRIVE);
    }
    
}