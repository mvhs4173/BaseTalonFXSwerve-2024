package frc.robot;

import java.util.Optional;

import org.photonvision.EstimatedRobotPose;

/**
 * Used by classes that want to be notified about changes in estimates
 * of the robots pose that come from the Photonvision camera system.
 * The principal user is Swerve, which should update its odometry-based
 * pose estimate with this information.
 * 
 * This is copied from Team-Eagle-Eyes 2023-2024.
 */
public interface PositionListener {
    void onPositionUpdate(Optional<EstimatedRobotPose> position);
}