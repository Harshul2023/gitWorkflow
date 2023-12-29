package in.sunfox.healthcare.java.spandan_qms.ecg_processsor_interpretetions;

import java.util.ArrayList;
import java.util.List;

public class SignalsFromRPeaks {
    // Constants
    private static final int NUM_SIGNALS = 8;
    private static final int TRUNCATED_SIGNAL_LENGTH = 4500;
    private static final int SIGNAL_LENGTH = 500;
    private static final double RMS_THRESHOLD_MULTIPLIER = 1.5;

    public ArrayList<ArrayList<Double>> findRpeaks(ArrayList<ArrayList<Double>> signals, int i) {
        if (i != 2) {
            for (int signalIndex = 0; signalIndex < NUM_SIGNALS; signalIndex++) {
                int rPeakIndex = findFirstRpeak(signals.get(signalIndex));
                int truncatedLength = signals.get(signalIndex).size() - rPeakIndex;

                ArrayList<Double> truncatedSignal = new ArrayList<Double>();
                for (int k = 0; k < TRUNCATED_SIGNAL_LENGTH; k++) {
                    truncatedSignal.add(k, signals.get(signalIndex).get(rPeakIndex + k));
                }
                signals.set(signalIndex, truncatedSignal);
            }
            return signals;
        }
        else{
            int rPeakIndex = findFirstRpeak(signals.get(0));
            int truncatedLength = signals.get(0).size() - rPeakIndex;

            ArrayList<Double> truncatedSignal = new ArrayList<Double>();
            for (int k = 0; k < TRUNCATED_SIGNAL_LENGTH; k++) {
                truncatedSignal.add(k, signals.get(0).get(rPeakIndex + k));
            }
            signals.set(0, truncatedSignal);
        }
        return signals;
    }

    public int findFirstRpeak(ArrayList<Double> signal) {
        List<Integer> points = new ArrayList<>();
        points.add(1);

        for (int i = 2; i < SIGNAL_LENGTH - 1; i++) {
            if ((signal.get(i) - signal.get(i - 1)) * (signal.get(i + 1) - signal.get(i)) <= 0) {
                points.add(i);
            }
        }

        double[] array = new double[SIGNAL_LENGTH];
        int count = 0;
        for (int i = 1; i < points.size(); i++) {
            if (signal.get(points.get(i)) - signal.get(points.get(i - 1)) > 0) {
                array[points.get(i)] = (signal.get(points.get(i)) - signal.get(points.get(i - 1))) / (points.get(i) - points.get(i - 1));
                count++;
            }
        }

        double valueOfRms = 0;
        for (int i = 0; i < array.length; i++) {
            valueOfRms += array[i] * array[i];
        }
        valueOfRms = Math.sqrt(valueOfRms / count);

        int firstPeak = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i] > valueOfRms * RMS_THRESHOLD_MULTIPLIER) {
                firstPeak = i;
                break;
            }
        }

        return firstPeak;
    }
}
