package de.jr.loadstone;

public class Smoothing {

    private final float timeInMs;
    private final float angle;
    private final boolean calmSmoothing;
    private final boolean medianSmoothing;

    private final SortedFixedList<Float> sortedFixedList;
    private final SortedFixedList<Float> sortedFixedList180;

    private long timeSinceLast = 0;
    private float smoothedHoldValue = -1;

    public Smoothing(float timeInMs, float angle, int bufferSize, boolean calmSmoothing, boolean medianSmoothing) {
        this.timeInMs = timeInMs;
        this.angle = angle;

        this.calmSmoothing = calmSmoothing;
        this.medianSmoothing = medianSmoothing;

        sortedFixedList180 = new SortedFixedList<>(bufferSize, 0.f);
        sortedFixedList = new SortedFixedList<>(bufferSize, 0.f);
    }

    public float getSmoothedValue(float sensorRotation, long delta) {

        if (!(calmSmoothing || medianSmoothing))
            return sensorRotation;

        sortedFixedList.add(sensorRotation);
        sortedFixedList180.add((sensorRotation + 180) % 360);
        timeSinceLast += delta;

        float median = sortedFixedList.getMedian();
        float result = (sortedFixedList.get(sortedFixedList.size - 1) - sortedFixedList.get(0));

        if (result > 180)
            median = (sortedFixedList180.getMedian() - 180) % 360;

        result %= 360;

        if (result > angle && medianSmoothing) {
            smoothedHoldValue = -1;
            return median;
        }
        if (calmSmoothing) {
            if (timeSinceLast >= timeInMs || smoothedHoldValue == -1) {
                timeSinceLast = 0;
                smoothedHoldValue = median;
            }
            return smoothedHoldValue;
        }
        return sensorRotation;


    }
}