package in.sunfox.healthcare.java.spandan_qms.ecg_processsor_interpretetions;

import in.sunfox.healthcare.java.spandan_qms.database_service.LogEcgData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignalProcessing {

    public static double calculateCorrelationCoefficient(double[] signal1, double[] signal2) {
        double mean1 = 0.0;
        double mean2 = 0.0;
        int n = signal1.length;
        double sumXY = 0.0;
        double sumX2 = 0.0;
        double sumY2 = 0.0;
        for (int i = 0; i < n; i++) {
            mean1 = mean1 + signal1[i];
            mean2 = mean2 + signal2[i];
        }
        mean1 = mean1 / n;
        mean2 = mean2 / n;
        for (int i = 0; i < n; i++) {
            double x = signal1[i] - mean1;
            double y = signal2[i] - mean2;
            sumXY += x * y;
            sumX2 += x * x;
            sumY2 += y * y;
        }
        double numerator = sumXY;
//        double denominator = Math.sqrt((sumX2) * (sumY2));
        double denominator = 0.0;
        if (sumY2 > sumX2)
           denominator = sumY2;
        else
            denominator = sumX2;
        return ((numerator /denominator) * 100);
    }

    public static int findFirstRpeak(double[] signal) {
        double[] signal2 = new double[signal.length];
        for (int i = 0; i < signal.length; i++) {
            signal2[i] = signal[i];
        }
        int i = 2;
        List<Integer> point = new ArrayList<>();
        point.add(1);
        while (i < 499) {
            if ((signal2[i] - signal2[i - 1]) * (signal2[i + 1] - signal2[i]) <= 0) {
                point.add(i);
            }
            i++;
        }

        double[] array = new double[500];
        int count = 0;
        for (i = 1; i < point.size(); i++) {
            if (signal2[point.get(i)] - signal2[point.get(i - 1)] > 0) {
                array[point.get(i)] = (signal2[point.get(i)] - signal2[point.get(i - 1)]) / (point.get(i) - point.get(i - 1));
                count++;
            }
        }

        double valueOfRms = 0;
        for (i = 0; i < array.length; i++) {
            valueOfRms += array[i] * array[i];
        }
        valueOfRms = Math.sqrt(valueOfRms / count);
        int firstPeak = 0;
        double meanValue = 0.0;
        for(i=0;i<signal.length;i++)
        {
            meanValue+=signal[i];
        }
        meanValue/=signal.length;

        for (i = 0; i < array.length; i++) {
            if (array[i] > valueOfRms * 1.5) {
                if(signal[i]>meanValue)
                {
                    firstPeak = i;
                    break;
                }
            }
        }
        return firstPeak;
    }
    public static double modeOfArray(double[] signalArray) {
        ArrayList<Double> array=new ArrayList<>();
        for(int i=0;i<signalArray.length;i++){
            array.add(signalArray[i]);
        }

        ArrayList<Integer> count = new ArrayList<>();
        int count2 = 0;
        double modeValue = array.get(0);

        int i = 0;
        while (i < array.size()) {
            for (int j = i + 1; j < array.size(); j++) {
                if (array.get(i).equals(array.get(j))) {

                    if (count.size() == 0)
                        count.add(i);

                    count.add(j);
                }
            }

            if (count.size() > count2) {
                count2 = count.size();
                modeValue = array.get(i);
            } else if (count.size() == count2 && count2 != 0) {
                if (array.get(i) < modeValue)
                    modeValue = array.get(i);
            } else {
                if (count2 == 0) {
                    if (array.get(i) < modeValue)
                        modeValue = array.get(i);
                }
            }

            int index ;
            for (int k = 0; k < count.size(); k++) {
                index = count.get(k)-k;
                array.remove(index);
            }

            if (count.size() != 0) {
                count.clear();
                i = -1;
            }

            i = i + 1;
        }
        return modeValue;
    }



    public static void loadSignals(double[][] signals, ArrayList<Double> signal, int signalType) {
        for (int i = 0; i < 5000; i++) {
            signals[signalType][i] = signal.get(i);
        }
    }

    public Map<Integer, Double> calculateCorrelation(ArrayList<ArrayList<Double>> testingArrayList) throws IOException {
        double avgCorrelation = 0.0;
        int numCorrelations = 0;

        LoadTwelveLeadReferenceSignal loadTwelveLeadReferenceSignal = new LoadTwelveLeadReferenceSignal();
        LogEcgData referenceSignal = loadTwelveLeadReferenceSignal.loadSignal(0);
        ArrayList<ArrayList<Double>> referenceArrayList = new ArrayList<>();

        referenceArrayList.add(referenceSignal.getV1());
        referenceArrayList.add(referenceSignal.getV2());
        referenceArrayList.add(referenceSignal.getV3());
        referenceArrayList.add(referenceSignal.getV4());
        referenceArrayList.add(referenceSignal.getV5());
        referenceArrayList.add(referenceSignal.getV6());
        referenceArrayList.add(referenceSignal.getL2());
        referenceArrayList.add(referenceSignal.getL1());
        Map<Integer, Double> result = new HashMap<>();
        int inc = 0;
        while (inc < 8) {
            int i = 0;
            int j = 1;
            int n = 2;
            double[][] signals = new double[n][5000];

            loadSignals(signals, testingArrayList.get(inc), 0);
            loadSignals(signals, referenceArrayList.get(inc), 1);


            int rPeakIndex1 = findFirstRpeak(signals[i]);

            int rPeakIndex2 = findFirstRpeak(signals[j]);

            int truncatedLength = Math.min(signals[i].length - rPeakIndex1, signals[j].length - rPeakIndex2);
            double[] truncatedSignal1 = new double[4500];
            double[] truncatedSignal2 = new double[4500];

            for (int k = 1; k < 4500; k++) {
                truncatedSignal1[k] = signals[i][rPeakIndex1 + k];
                truncatedSignal2[k] = signals[j][rPeakIndex2 + k];
            }

            double correlation = calculateCorrelationCoefficient(truncatedSignal1, truncatedSignal2);
            avgCorrelation += correlation;
            numCorrelations++;

            result.put(inc, correlation);
            System.out.printf("Correlation between signal %d and signal %d: %.2f%n", i + 1, j + 1, correlation);
            inc++;
        }


        return result;
    }

}
