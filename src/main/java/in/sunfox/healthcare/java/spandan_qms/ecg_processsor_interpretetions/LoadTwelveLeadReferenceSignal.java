package in.sunfox.healthcare.java.spandan_qms.ecg_processsor_interpretetions;

import com.google.gson.Gson;
import in.sunfox.healthcare.java.spandan_qms.database_service.LogEcgData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

public class LoadTwelveLeadReferenceSignal {
    public LogEcgData loadSignal(int check){
        // Define the path to your JSON file within the resources folder
        String resourceFilePath = "TwelveLeadReferenceSignal.json";
        try {
            Gson gson = new Gson();
            InputStreamReader inputStreamReader = new InputStreamReader(
                    Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(resourceFilePath)),
                    StandardCharsets.UTF_8
            );
            BufferedReader reader = new BufferedReader(inputStreamReader);
            LogEcgData logEcgData = gson.fromJson(reader, LogEcgData.class);
            reader.close();

//            System.out.println("v1: " + logEcgData.getV1());
//            System.out.println("v2: " + logEcgData.getV2());
//            System.out.println("v3: " + logEcgData.getV3());
//            System.out.println("v4: " + logEcgData.getV4());
//            System.out.println("v5: " + logEcgData.getV5());
//            System.out.println("v6: " + logEcgData.getV6());
//            System.out.println("l1: " + logEcgData.getL1());
//            System.out.println("l2: " + logEcgData.getL2());

            if(check==0)
                return logEcgData;
            else {
                ArrayList<ArrayList<Double>> signals = new ArrayList<>();
                ArrayList<Double> v1 = new ArrayList<>(logEcgData.getV1());
                ArrayList<Double> v2 = new ArrayList<>(logEcgData.getV2());
                ArrayList<Double> v3 = new ArrayList<>(logEcgData.getV3());
                ArrayList<Double> v4 = new ArrayList<>(logEcgData.getV4());
                ArrayList<Double> v5 = new ArrayList<>(logEcgData.getV5());
                ArrayList<Double> v6 = new ArrayList<>(logEcgData.getV6());
                ArrayList<Double> l1 = new ArrayList<>(logEcgData.getL1());
                ArrayList<Double> l2 = new ArrayList<>(logEcgData.getL2());
                signals.add(v1);
                signals.add(v2);
                signals.add(v3);
                signals.add(v4);
                signals.add(v5);
                signals.add(v6);
                signals.add(l2);
                signals.add(l1);

                SignalsFromRPeaks signalsFromRPeaks = new SignalsFromRPeaks();
                signals = signalsFromRPeaks.findRpeaks(signals, 2);
                logEcgData.setV1(signals.get(0));
                logEcgData.setV2(signals.get(1));
                logEcgData.setV3(signals.get(2));
                logEcgData.setV4(signals.get(3));
                logEcgData.setV5(signals.get(4));
                logEcgData.setV6(signals.get(5));
                logEcgData.setL2(signals.get(7));
                logEcgData.setL1(signals.get(6));
            }

            return logEcgData;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
