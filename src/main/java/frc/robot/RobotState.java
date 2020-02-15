package frc.robot;

import frc.robot.constants.RobotConstants;
import frc.lib.geometry.Pose;
import frc.lib.geometry.Rotation;
import frc.lib.geometry.Translation;
import frc.lib.geometry.Twist;
import frc.lib.util.InterpolatingDouble;
import frc.lib.util.InterpolatingTreeMap;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.util.Map;

public class RobotState {

    private static final int kObservationBufferSize = 100;

    private static final Pose vehicleToCamera = new Pose(
    new Translation(RobotConstants.CAMERAXOFFSET, RobotConstants.CAMERAYOFFSET),
    Rotation.fromDegrees(RobotConstants.CAMERAYAWANGLEDEGREES));

    // FPGATimestamp -> RigidTransform or Rotation
    private InterpolatingTreeMap<InterpolatingDouble, Pose> field_to_vehicle_;
    private Twist vehicle_velocity_predicted_;
    private Twist vehicle_velocity_measured_;
    private double distance_driven_;

    public RobotState() {
        reset(0, new Pose());
    }

    /**
     * Resets the field to robot transform (robot's position on the field)
     */
    public synchronized void reset(double start_time, Pose initial_field_to_vehicle) {
        field_to_vehicle_ = new InterpolatingTreeMap<>(kObservationBufferSize);
        field_to_vehicle_.put(new InterpolatingDouble(start_time), initial_field_to_vehicle);
        // Robot.drivetrain.setHeading(initial_field_to_vehicle.getRotation());
        vehicle_velocity_predicted_ = Twist.identity();
        vehicle_velocity_measured_ = Twist.identity();
        distance_driven_ = 0.0;
    }

    public synchronized void resetDistanceDriven() {
        distance_driven_ = 0.0;
    }

    /**
     * Returns the robot's position on the field at a certain time. Linearly
     * interpolates between stored robot positions to fill in the gaps.
     */
    public synchronized Pose getFieldToVehicle(double timestamp) {
        return field_to_vehicle_.getInterpolated(new InterpolatingDouble(timestamp));
    }

    public synchronized Map.Entry<InterpolatingDouble, Pose> getLatestFieldToVehicle() {
        return field_to_vehicle_.lastEntry();
    }

    public synchronized Pose getPredictedFieldToVehicle(double lookahead_time) {
        return getLatestFieldToVehicle().getValue()
                .transformBy(Pose.exp(vehicle_velocity_predicted_.scaled(lookahead_time)));
    }

    // public synchronized Pose getFieldToLidar(double timestamp) {
    // return getFieldToVehicle(timestamp).transformBy(kVehicleToLidar);
    // }

    public synchronized void addFieldToVehicleObservation(double timestamp, Pose observation) {
        field_to_vehicle_.put(new InterpolatingDouble(timestamp), observation);
    }

    public synchronized void addObservations(double timestamp, Twist measured_velocity, Twist predicted_velocity) {
        addFieldToVehicleObservation(timestamp,
                Kinematics.integrateForwardKinematics(getLatestFieldToVehicle().getValue(), measured_velocity));
        vehicle_velocity_measured_ = measured_velocity;
        vehicle_velocity_predicted_ = predicted_velocity;
    }

    public synchronized Twist generateOdometryFromSensors(double left_encoder_delta_distance,
            double right_encoder_delta_distance, Rotation current_gyro_angle) {
        final Pose last_measurement = getLatestFieldToVehicle().getValue();
        final Twist delta = Kinematics.forwardKinematics(last_measurement.getRotation(), left_encoder_delta_distance,
                right_encoder_delta_distance, current_gyro_angle);
        distance_driven_ += delta.dx; // do we care about dy here?
        return delta;
    }

    public synchronized double getDistanceDriven() {
        return distance_driven_;
    }

    public synchronized Twist getPredictedVelocity() {
        return vehicle_velocity_predicted_;
    }

    public synchronized Twist getMeasuredVelocity() {
        return vehicle_velocity_measured_;
    }

    public void outputToSmartDashboard() {
        Pose odometry = getLatestFieldToVehicle().getValue();
        SmartDashboard.putNumber("Robot Pose X", odometry.getTranslation().x());
        SmartDashboard.putNumber("Robot Pose Y", odometry.getTranslation().y());
        SmartDashboard.putNumber("Robot Pose Theta", odometry.getRotation().degrees());
        SmartDashboard.putNumber("Robot Linear Velocity", vehicle_velocity_measured_.dx);
    }
}
