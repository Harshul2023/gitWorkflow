package in.sunfox.healthcare.java.spandan_qms.ecg_processsor_interpretetions;

import in.sunfox.healthcare.java.commons.ecg_processor.conclusions.characteristics.EcgCharacteristics;
import in.sunfox.healthcare.java.commons.ecg_processor.conclusions.conclusion.TwelveLeadConclusion;
import in.sunfox.healthcare.java.commons.ecg_processor.conclusions.data.LeadTwoData;
import in.sunfox.healthcare.java.commons.ecg_processor.conclusions.data.TwelveLeadData;
import in.sunfox.healthcare.java.commons.ecg_processor.core.EcgProcessor;
import in.sunfox.healthcare.java.commons.ecg_processor.core.models.EcgProcessorResult;
import in.sunfox.healthcare.java.commons.ecg_processor.core.processing.AugmentedLeadGenerator;
import in.sunfox.healthcare.java.commons.ecg_processor.core.processing.Filters;
import in.sunfox.healthcare.java.commons.ecg_processor.core.processing.heart_risk_calculator.matrices.processor.ProcessorType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static in.sunfox.healthcare.java.spandan_qms.utilitites.Constants.*;

public class InterPretetions {
    // Constants

    public boolean isInRange(double value, double upperBound, double lowerBound) {
        return (value >= lowerBound && value <= upperBound);
    }
    public Map<String, String> doInterpretetions(ArrayList<ArrayList<Double>> ecgPoints) throws IOException {
        ArrayList<EcgCharacteristics> detections;
        Map<String, String> resultMap = new HashMap<String, String>();

        ArrayList<Double> signalLead1 = Filters.movingAverage(ecgPoints.get(7));
        ArrayList<Double> signalLead2 = Filters.movingAverage(ecgPoints.get(6));
        AugmentedLeadGenerator generatedLeads = EcgProcessor.Companion.getAugmentedLeadGenerator(signalLead1, signalLead2);

        System.out.println("sjakjfhakjsdfsdsdfsdfsdfsf");
        TwelveLeadData twelveLeadData = new TwelveLeadData(ecgPoints.get(0),
                ecgPoints.get(1),
                ecgPoints.get(2),
                ecgPoints.get(3),
                ecgPoints.get(4),
                ecgPoints.get(5),
                signalLead1,
                signalLead2,
                generatedLeads.getLead3Points(),
                generatedLeads.getAVlPoints(),
                generatedLeads.getAVrPoints(),
                generatedLeads.getAVfPoints()
        );
        EcgProcessorResult ecgProcessorResult = new EcgProcessor(ProcessorType.TWELVE_LEAD, twelveLeadData, false, false).process(generatedLeads, null, 32);
        detections = ecgProcessorResult.getCharacteristics();
        TwelveLeadConclusion conclusions = (TwelveLeadConclusion) ecgProcessorResult.getConclusion();

        int heartRate = detections.get(7).getHeartRate();
        int pr = detections.get(7).getPr();
        int qrs = detections.get(7).getQrs();
        double qtc = detections.get(7).getQtc();
        int qt = detections.get(7).getQt();
        int rr = detections.get(7).getRr();
        double qAmplitudeInLead = detections.get(7).getAverageQAmplitudeInLead();
        double rAmplitudeInLead = detections.get(7).getAverageRAmplitudeInLead();
        double sAmplitudeInLead = detections.get(7).getAverageSAmplitudeInLead();

        resultMap.put("heartRate", String.valueOf(heartRate));
        resultMap.put("pr", String.valueOf(pr));
        resultMap.put("qrs", String.valueOf(qrs));
        resultMap.put("qtc", String.valueOf(qtc));
        resultMap.put("qt", String.valueOf(qt));
        resultMap.put("qAmplitude", String.valueOf(qAmplitudeInLead));
        resultMap.put("qAmplitudeEvaluation", isInRange(qAmplitudeInLead, Q_AMPLITUDE_UPPER_BOUND, Q_AMPLITUDE_LOWER_BOUND) ? "OK" : "NOT OK");

        resultMap.put("rAmplitude", String.valueOf(rAmplitudeInLead));
        resultMap.put("rAmplitudeEvaluation", isInRange(rAmplitudeInLead, R_AMPLITUDE_LOWER_BOUND, R_AMPLITUDE_UPPER_BOUND) ? "OK" : "NOT OK");

        resultMap.put("sAmplitude", String.valueOf(sAmplitudeInLead));
        resultMap.put("sAmplitudeEvaluation", isInRange(sAmplitudeInLead, S_AMPLITUDE_LOWER_BOUND, S_AMPLITUDE_UPPER_BOUND) ? "OK" : "NOT OK");

        resultMap.put("heartRateEvaluation", isInRange(heartRate, HEART_RATE_UPPER_BOUND, HEART_RATE_LOWER_BOUND) ? "OK" : "NOT OK");
        resultMap.put("prEvaluation", isInRange(pr, PR_UPPER_BOUND, PR_LOWER_BOUND) ? "OK" : "NOT OK");
        resultMap.put("qrsEvaluation", isInRange(qrs, QRS_UPPER_BOUND, QRS_LOWER_BOUND) ? "OK" : "NOT OK");
        resultMap.put("qtEvaluation", isInRange(qt, QT_UPPER_BOUND, QT_LOWER_BOUND) ? "OK" : "NOT OK");
        resultMap.put("qtcEvaluation", isInRange(qtc, QTC_UPPER_BOUND, QTC_LOWER_BOUND) ? "OK" : "NOT OK");

        resultMap.put("Correlation", String.format("%.2f%n", calculateCorrelation(ecgPoints)));
        resultMap.put("rr", String.valueOf(rr));

        resultMap.put("overAllEvaluation", isInRange(heartRate, HEART_RATE_UPPER_BOUND, HEART_RATE_LOWER_BOUND) &&
                isInRange(qt, QT_UPPER_BOUND, QT_LOWER_BOUND) &&
                isInRange(qtc, QTC_UPPER_BOUND, QTC_LOWER_BOUND) &&
                isInRange(qrs, QRS_UPPER_BOUND, QRS_LOWER_BOUND) &&
                isInRange(pr, PR_UPPER_BOUND, PR_LOWER_BOUND) ? "OK" : "NOT OK");

        return resultMap;
    }
    public Map<String, String> doLEAD2Interpretetions(ArrayList<ArrayList<Double>> ecgPoints) throws IOException {
        EcgProcessorResult c = new EcgProcessor(ProcessorType.LEAD_TWO,new LeadTwoData(ecgPoints.get(0)),false,true).processForLead2(new LeadTwoData(ecgPoints.get(0)));
        System.out.println(c.getCharacteristics());
        System.out.println(c.getConclusion());
        Map<String, String> resultMap = new HashMap<String, String>();
        int heartRate = c.getCharacteristics().get(0).getHeartRate();
        int pr = c.getCharacteristics().get(0).getPr();
        int qrs = c.getCharacteristics().get(0).getQrs();
        double qtc = c.getCharacteristics().get(0).getQtc();
        int qt = c.getCharacteristics().get(0).getQt();
        int rr = c.getCharacteristics().get(0).getRr();
        double qAmplitudeInLead = c.getCharacteristics().get(0).getAverageQAmplitudeInLead();
        double rAmplitudeInLead = c.getCharacteristics().get(0).getAverageRAmplitudeInLead();
        double sAmplitudeInLead = c.getCharacteristics().get(0).getAverageSAmplitudeInLead();

        resultMap.put("heartRate", String.valueOf(heartRate));
        resultMap.put("pr", String.valueOf(pr));
        resultMap.put("qrs", String.valueOf(qrs));
        resultMap.put("qtc", String.valueOf(qtc));
        resultMap.put("qt", String.valueOf(qt));
        resultMap.put("qAmplitude", String.valueOf(qAmplitudeInLead));
        resultMap.put("qAmplitudeEvaluation", isInRange(qAmplitudeInLead, Q_AMPLITUDE_UPPER_BOUND, Q_AMPLITUDE_LOWER_BOUND) ? "OK" : "NOT OK");

        resultMap.put("rAmplitude", String.valueOf(rAmplitudeInLead));
        resultMap.put("rAmplitudeEvaluation", isInRange(rAmplitudeInLead, R_AMPLITUDE_LOWER_BOUND, R_AMPLITUDE_UPPER_BOUND) ? "OK" : "NOT OK");

        resultMap.put("sAmplitude", String.valueOf(sAmplitudeInLead));
        resultMap.put("sAmplitudeEvaluation", isInRange(sAmplitudeInLead, S_AMPLITUDE_LOWER_BOUND, S_AMPLITUDE_UPPER_BOUND) ? "OK" : "NOT OK");

        resultMap.put("heartRateEvaluation", isInRange(heartRate, HEART_RATE_UPPER_BOUND, HEART_RATE_LOWER_BOUND) ? "OK" : "NOT OK");
        resultMap.put("prEvaluation", isInRange(pr, PR_UPPER_BOUND, PR_LOWER_BOUND) ? "OK" : "NOT OK");
        resultMap.put("qrsEvaluation", isInRange(qrs, QRS_UPPER_BOUND, QRS_LOWER_BOUND) ? "OK" : "NOT OK");
        resultMap.put("qtEvaluation", isInRange(qt, QT_UPPER_BOUND, QT_LOWER_BOUND) ? "OK" : "NOT OK");
        resultMap.put("qtcEvaluation", isInRange(qtc, QTC_UPPER_BOUND, QTC_LOWER_BOUND) ? "OK" : "NOT OK");

        resultMap.put("Correlation", "-");
        resultMap.put("rr", String.valueOf(rr));

        resultMap.put("overAllEvaluation", isInRange(heartRate, HEART_RATE_UPPER_BOUND, HEART_RATE_LOWER_BOUND) &&
                isInRange(qt, QT_UPPER_BOUND, QT_LOWER_BOUND) &&
                isInRange(qtc, QTC_UPPER_BOUND, QTC_LOWER_BOUND) &&
                isInRange(qrs, QRS_UPPER_BOUND, QRS_LOWER_BOUND) &&
                isInRange(pr, PR_UPPER_BOUND, PR_LOWER_BOUND) ? "OK" : "NOT OK");

        return resultMap;
    }

    private double calculateCorrelation(ArrayList<ArrayList<Double>> ecgPoints) {
        SignalProcessing signalProcessing = new SignalProcessing();
        Map<Integer, Double> result = null;
        try {
            result = signalProcessing.calculateCorrelation(ecgPoints);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        double sum = 0.0;
        for (Map.Entry<Integer, Double> entry : result.entrySet()) {
            Integer key = entry.getKey();
            Double value = entry.getValue();
            sum += value;
        }
        return sum / CORRELATION_ARRAY_SIZE;
    }
}
