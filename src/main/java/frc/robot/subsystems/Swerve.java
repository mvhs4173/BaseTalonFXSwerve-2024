package frc.robot.subsystems;

import frc.robot.SwerveModule;
import frc.robot.TuningVariables;
import frc.robot.Constants;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
//import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;

//import com.ctre.phoenix6.configs.Pigeon2Configuration;
//import com.ctre.phoenix6.hardware.Pigeon2;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Swerve extends SubsystemBase {
    private SwerveDrivePoseEstimator m_poseEstimator;
    public SwerveModule[] mSwerveMods;
    private boolean m_usePigeon = false;
    // public Pigeon2 gyro;
    private Gyro gyro = new Gyro(m_usePigeon);

    Field2d m_field = new Field2d();

    public Swerve() {

        SmartDashboard.putData("Field", m_field);

        // gyro = new Pigeon2(Constants.Swerve.pigeonID);
        // gyro.getConfigurator().apply(new Pigeon2Configuration());
        gyro.setYaw(0);

        mSwerveMods = new SwerveModule[] {
            new SwerveModule(0, Constants.Swerve.Mod0.constants),
            new SwerveModule(1, Constants.Swerve.Mod1.constants),
            new SwerveModule(2, Constants.Swerve.Mod2.constants),
            new SwerveModule(3, Constants.Swerve.Mod3.constants)
        };

        m_poseEstimator = new SwerveDrivePoseEstimator(Constants.Swerve.swerveKinematics, getGyroYaw(), getModulePositions(),
            new Pose2d(new Translation2d(0.0, 0.0), new Rotation2d(Units.degreesToRadians(0.0))));
    }

    public void drive(Translation2d translation, double rotation, boolean fieldRelative, boolean isOpenLoop) {
        SwerveModuleState[] swerveModuleStates =
            Constants.Swerve.swerveKinematics.toSwerveModuleStates(
                fieldRelative ? ChassisSpeeds.fromFieldRelativeSpeeds(
                                    translation.getX(), 
                                    translation.getY(), 
                                    rotation, 
                                    getHeading()
                                )
                                : new ChassisSpeeds(
                                    translation.getX(), 
                                    translation.getY(), 
                                    rotation)
                                );
        SwerveDriveKinematics.desaturateWheelSpeeds(swerveModuleStates, Constants.Swerve.maxSpeed);

        for(SwerveModule mod : mSwerveMods){
            mod.setDesiredState(swerveModuleStates[mod.moduleNumber], isOpenLoop);
        }
    }    

    /* Used by SwerveControllerCommand in Auto */
    public void setModuleStates(SwerveModuleState[] desiredStates) {
        SwerveDriveKinematics.desaturateWheelSpeeds(desiredStates, Constants.Swerve.maxSpeed);
        
        for(SwerveModule mod : mSwerveMods){
            mod.setDesiredState(desiredStates[mod.moduleNumber], false);
        }
    }

    public SwerveModuleState[] getModuleStates(){
        SwerveModuleState[] states = new SwerveModuleState[4];
        for(SwerveModule mod : mSwerveMods){
            states[mod.moduleNumber] = mod.getState();
        }
        return states;
    }

    public SwerveModulePosition[] getModulePositions(){
        SwerveModulePosition[] positions = new SwerveModulePosition[4];
        for(SwerveModule mod : mSwerveMods){
            positions[mod.moduleNumber] = mod.getPosition();
        }
        return positions;
    }

    /**
     * This allows unlimited direct access to the pose estimator.
     * We would like you to minimize use of this, e.g. by using
     * m_swerve.getPose() instead of m_swerve.getPoseEstimator().getEstimatedPosition().
     * Add more methods to Swerve if needed.
     * @return the pose estimator for the swerve drive
     */
    public SwerveDrivePoseEstimator getPoseEstimator(){
        return m_poseEstimator;
    }

    public Pose2d getPose() {
        return m_poseEstimator.getEstimatedPosition();
    }

    public void setPose(Pose2d pose) {
        m_poseEstimator.resetPosition(getGyroYaw(), getModulePositions(), pose);
    }

    public Rotation2d getHeading(){
        return getPose().getRotation();
    }

    public void setHeading(Rotation2d heading){
        m_poseEstimator.resetPosition(getGyroYaw(), getModulePositions(), new Pose2d(getPose().getTranslation(), heading));
    }

    public void zeroHeading(){
        m_poseEstimator.resetPosition(getGyroYaw(), getModulePositions(), new Pose2d(getPose().getTranslation(), new Rotation2d()));
    }

    public Rotation2d getGyroYaw() {
        //return Rotation2d.fromDegrees(gyro.getYaw().getValue());
        return Rotation2d.fromDegrees(gyro.getYaw());
    }

    public void resetModulesToAbsolute(){
        for(SwerveModule mod : mSwerveMods){
            mod.resetToAbsolute();
        }
    }

    public Field2d getField2d(){
        return m_field;
    }

    public void lockX(){
        SwerveModuleState[] m_states = {
            new SwerveModuleState(0.0, Rotation2d.fromDegrees(135)),
            new SwerveModuleState(0.0, Rotation2d.fromDegrees(45)),
            new SwerveModuleState(0.0, Rotation2d.fromDegrees(135)),
            new SwerveModuleState(0.0, Rotation2d.fromDegrees(45))
        };
        setModuleStates(m_states);
    }

    /**
     * Update pose estimate with pose derived from camera
     * @param visionRobotPoseMeters - The pose of the robot as measured by the vision camera.
     * @param timestampSeconds - The timestamp of the vision measurement in seconds.
     * Note that if you don't use your own time source by calling updateWithTime(double,Rotation2d,WheelPositions)
     * then you must use a timestamp with an epoch since FPGA startup (i.e., the epoch of this timestamp
     * is the same epoch as Timer.getFPGATimestamp().) This means that you should use Timer.getFPGATimestamp()
     * as your time source or sync the epochs.
     */
    public void addVisionMeasurement(Pose2d visionRobotPoseMeters, double timestampSeconds){
        m_poseEstimator.addVisionMeasurement(visionRobotPoseMeters, timestampSeconds);
    }

    @Override
    public void periodic(){
        m_poseEstimator.update(getGyroYaw(), getModulePositions());

        m_field.setRobotPose(getPose());

        if (TuningVariables.debugLevel.getNumber() >= 5.0){
            for(SwerveModule mod : mSwerveMods){
                SmartDashboard.putNumber("Mod " + mod.moduleNumber + " CANcoder", mod.getCANcoder().getDegrees());
                SmartDashboard.putNumber("Mod " + mod.moduleNumber + " Angle", mod.getPosition().angle.getDegrees());
                SmartDashboard.putNumber("Mod " + mod.moduleNumber + " Velocity", mod.getState().speedMetersPerSecond);
                SmartDashboard.putNumber("Mod" + mod.moduleNumber + "Drive Current", mod.getDriveCurrent());
                SmartDashboard.putNumber("Mod" + mod.moduleNumber + "Angle Current", mod.getAngleCurrent());    
            }
        }
    }
}