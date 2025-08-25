package de.jr.loadstone;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


public class DeviceRotation implements SensorEventListener {

    private final SensorManager sensorManager;

    private final float[] accelerometerReading = new float[3];
    private final float[] magnetometerReading = new float[3];
    private final float[] rotationMatrix = new float[9];
    public final float[] orientationAngles = new float[3];

    private OrientationListener orientationListener;
    private AccuracyListener accuracyListener;


    public DeviceRotation(SensorManager sensorManager) {
        this.sensorManager = sensorManager;
    }


    public interface AccuracyListener {
        void onAccuracyChanged(Sensor sensor, int accuracy);
    }

    public void setAccuracyListener(AccuracyListener listener) {
        this.accuracyListener = listener;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if (accuracyListener != null) {
            accuracyListener.onAccuracyChanged(sensor, accuracy);
        }
    }


    public interface OrientationListener {
        void onOrientationUpdated(float[] orientationAngles);
    }

    public void setOrientationListener(OrientationListener listener) {
        this.orientationListener = listener;
    }

    public void updateOrientationAngles() {
        SensorManager.getRotationMatrix(rotationMatrix, null,
                accelerometerReading, magnetometerReading);
        SensorManager.getOrientation(rotationMatrix, orientationAngles);

        if (orientationListener != null) {
            orientationListener.onOrientationUpdated(orientationAngles);
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelerometerReading,
                    0, accelerometerReading.length);
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magnetometerReading,
                    0, magnetometerReading.length);
        }
        updateOrientationAngles();
    }


    public void resume() {

        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        }
        Sensor magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (magneticField != null) {
            sensorManager.registerListener(this, magneticField,
                    SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        }
    }

    public void pause() {
        sensorManager.unregisterListener(this);
    }
}