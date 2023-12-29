package in.sunfox.healthcare.java.spandan_qms.pdf_generation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import in.sunfox.healthcare.java.spandan_qms.database_service.*;
import in.sunfox.healthcare.java.spandan_qms.database_service.LogEcgData;
import in.sunfox.healthcare.java.spandan_qms.database_service.OnDataReceiveListener;
import in.sunfox.healthcare.java.spandan_qms.ecg_processsor_interpretetions.InterPretetions;
import in.sunfox.healthcare.java.spandan_qms.ecg_processsor_interpretetions.LoadTwelveLeadReferenceSignal;
import in.sunfox.healthcare.java.spandan_qms.ecg_processsor_interpretetions.SignalsFromRPeaks;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

public class PdfGenerationClass {
    LogEcgData logEcgDataRef = new LogEcgData();

    public void loadPdf(String uniqueDeviceId, ArrayList<ArrayList<Double>> twelveLeadArrayList, OnDataReceiveListener<PDDocument> onDataReceiveListenerPdfListener, String currentStage) throws IOException {
        final PDDocument[] document = {null};
        InputStream pdfStream = getClass().getResourceAsStream("/spandan_qms_report_editable_form.pdf");
        try {
            document[0] = PDDocument.load(pdfStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        logEcgDataRef = createref();
        try {
            OnDataReceiveListener<String> onDataReceiveListener = new OnDataReceiveListener<String>() {
                @Override
                public void onDataReceived(String data) {


                    Gson gson = new GsonBuilder().create();
                    LogEcgData logEcgData = gson.fromJson(data, LogEcgData.class);
                    int size = 0;

                    try {
                        PDImageXObject pdImage;
                        if (uniqueDeviceId.contains("SPPR")) {
                            URL url = getClass().getResource("/in/sunfox/healthcare/java/spandan_qms/Pro.png"); // Corrected path without the space
                            BufferedImage bufferedImage = ImageIO.read(url);
                            pdImage = LosslessFactory.createFromImage(document[0], bufferedImage);
                        }
                        else if (uniqueDeviceId.contains("SPNE")) {
                            URL url = getClass().getResource("/in/sunfox/healthcare/java/spandan_qms/neo.png"); // Corrected path without the space
                            BufferedImage bufferedImage = ImageIO.read(url);
                            pdImage = LosslessFactory.createFromImage(document[0], bufferedImage);
                        } else {
                            URL url = getClass().getResource("/in/sunfox/healthcare/java/spandan_qms/SunfoxLogoWithR.png"); // Corrected path without the space
                            BufferedImage bufferedImage = ImageIO.read(url);
                            pdImage = LosslessFactory.createFromImage(document[0], bufferedImage);
                        }
                        PDPage page0 = document[0].getPage(0);
                        PDPageContentStream contentStream0 = new PDPageContentStream(document[0], page0, PDPageContentStream.AppendMode.APPEND, true);
                        float imageWidth = 70.0f;
                        float imageHeight = 30.0f;
                        contentStream0.drawImage(pdImage, 15.0f, 750.0f + 37.0f, imageWidth, imageHeight);
                        contentStream0.close();
                        PDPage page1 = document[0].getPage(1);
                        PDPageContentStream contentStream1 = new PDPageContentStream(document[0], page1, PDPageContentStream.AppendMode.APPEND, true);

                        contentStream1.drawImage(pdImage, 15.0f, 750.0f + 37.0f, imageWidth, imageHeight);
                        contentStream1.close();
                        PDPage page2 = document[0].getPage(2);
                        PDPageContentStream contentStream2 = new PDPageContentStream(document[0], page2, PDPageContentStream.AppendMode.APPEND, true);

                        contentStream2.drawImage(pdImage, 15.0f, 750.0f + 37.0f, imageWidth, imageHeight);
                        contentStream2.close();

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
                        ArrayList<ArrayList<Double>> truncatedTestSignal = new ArrayList<>();
                        for (ArrayList<Double> signal : signals) {
                            ArrayList<Double> copySignal = new ArrayList<>(signal);
                            truncatedTestSignal.add(copySignal);
                        }
                        SignalsFromRPeaks signalsFromRPeaks = new SignalsFromRPeaks();
                        truncatedTestSignal = signalsFromRPeaks.findRpeaks(truncatedTestSignal, 0);

                        PDPage page = document[0].getPage(1); // Get the first page
                        PDPageContentStream contentStream = new PDPageContentStream(document[0], page, PDPageContentStream.AppendMode.APPEND, true);
                        // Draw your graph using contentStream
                        float yPosition = 550.0f;
                        contentStream.beginText();
                        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                        contentStream.newLineAtOffset(27.0f, yPosition + 60.0f);
                        contentStream.showText("Position : V1"); // Customize the text here for the first graph
                        contentStream.endText();
                        size = Math.min(truncatedTestSignal.get(0).size(), logEcgDataRef.getV1().size());
                        drawGraph(contentStream, 27.0f, yPosition, truncatedTestSignal.get(0).subList(0, size - 1), logEcgDataRef.getV1().subList(0, size - 1));
                        yPosition -= 74.0f;

                        contentStream.beginText();
                        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                        contentStream.newLineAtOffset(27.0f, yPosition + 60.0f);
                        contentStream.showText("Position : V2"); // Customize the text here for the first graph
                        contentStream.endText();
                        size = Math.min(truncatedTestSignal.get(1).size(), logEcgDataRef.getV2().size());
                        drawGraph(contentStream, 27.0f, yPosition, truncatedTestSignal.get(1).subList(0, size - 1), logEcgDataRef.getV2().subList(0, size - 1));
                        yPosition -= 74.0f;

                        contentStream.beginText();
                        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                        contentStream.newLineAtOffset(27.0f, yPosition + 60.0f);
                        contentStream.showText("Position : V3"); // Customize the text here for the first graph
                        contentStream.endText();
                        size = Math.min(truncatedTestSignal.get(2).size(), logEcgDataRef.getV3().size());
                        drawGraph(contentStream, 27.0f, yPosition, truncatedTestSignal.get(2).subList(0, size - 1), logEcgDataRef.getV3().subList(0, size - 1));
                        yPosition -= 74.0f;

                        contentStream.beginText();
                        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                        contentStream.newLineAtOffset(27.0f, yPosition + 60.0f);
                        contentStream.showText("Position : V4"); // Customize the text here for the first graph
                        contentStream.endText();
                        size = Math.min(truncatedTestSignal.get(3).size(), logEcgDataRef.getV4().size());
                        drawGraph(contentStream, 27.0f, yPosition, truncatedTestSignal.get(3).subList(0, size - 1), logEcgDataRef.getV4().subList(0, size - 1));
                        yPosition -= 74.0f;

                        contentStream.beginText();
                        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                        contentStream.newLineAtOffset(27.0f, yPosition + 60.0f);
                        contentStream.showText("Position : V5"); // Customize the text here for the first graph
                        contentStream.endText();
                        size = Math.min(truncatedTestSignal.get(4).size(), logEcgDataRef.getV5().size());
                        drawGraph(contentStream, 27.0f, yPosition, truncatedTestSignal.get(4).subList(0, size - 1), logEcgDataRef.getV5().subList(0, size - 1));
                        yPosition -= 74.0f;

                        contentStream.beginText();
                        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                        contentStream.newLineAtOffset(27.0f, yPosition + 60.0f);
                        contentStream.showText("Position : V6"); // Customize the text here for the first graph
                        contentStream.endText();
                        size = Math.min(truncatedTestSignal.get(5).size(), logEcgDataRef.getV6().size());
                        drawGraph(contentStream, 27.0f, yPosition, truncatedTestSignal.get(5).subList(0, size - 1), logEcgDataRef.getV6().subList(0, size - 1));
                        yPosition -= 74.0f;

                        contentStream.beginText();
                        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                        contentStream.newLineAtOffset(27.0f, yPosition + 60.0f);
                        contentStream.showText("Position : Lead 1"); // Customize the text here for the first graph
                        contentStream.endText();
                        size = Math.min(truncatedTestSignal.get(7).size(), logEcgDataRef.getL1().size());
                        drawGraph(contentStream, 27.0f, yPosition, truncatedTestSignal.get(7).subList(0, size - 1), logEcgDataRef.getL1().subList(0, size - 1));
                        yPosition -= 74.0f;

                        contentStream.beginText();
                        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                        contentStream.newLineAtOffset(27.0f, yPosition + 60.0f);
                        contentStream.showText("Position : Lead 2"); // Customize the text here for the first graph
                        contentStream.endText();
                        size = Math.min(truncatedTestSignal.get(6).size(), logEcgDataRef.getL2().size());
                        drawGraph(contentStream, 27.0f, yPosition, truncatedTestSignal.get(6).subList(0, size - 1), logEcgDataRef.getL2().subList(0, size - 1));

                        contentStream.close();
                        writeRemaining(document, onDataReceiveListenerPdfListener, uniqueDeviceId, logEcgData);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onDataReceiveError(String errorMsg) {
                    try {
                        document[0].close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    onDataReceiveListenerPdfListener.onDataReceiveError(errorMsg);

                }
            };
            if (twelveLeadArrayList == null) {

                int temp = Integer.parseInt(currentStage);

                temp -= 1;
                if (temp >= 2 && temp <= 5)
                    currentStage = "2";
                else {
                    currentStage = "6";
                }
                DatabaseUtility.getLogEcgData(uniqueDeviceId, onDataReceiveListener, currentStage);
            } else {
                PDImageXObject pdImage;
                if (uniqueDeviceId.contains("SPNE")) {
                    URL url = getClass().getResource("/in/sunfox/healthcare/java/spandan_qms/neo.png"); // Corrected path without the space
                    BufferedImage bufferedImage = ImageIO.read(url);
                    pdImage = LosslessFactory.createFromImage(document[0], bufferedImage);
                } else {
                    URL url = getClass().getResource("/in/sunfox/healthcare/java/spandan_qms/SunfoxLogoWithR.png"); // Corrected path without the space
                    BufferedImage bufferedImage = ImageIO.read(url);
                    pdImage = LosslessFactory.createFromImage(document[0], bufferedImage);
                }
                PDPage page0 = document[0].getPage(0);
                PDPageContentStream contentStream0 = new PDPageContentStream(document[0], page0, PDPageContentStream.AppendMode.APPEND, true);
                float imageWidth = 70.0f;
                float imageHeight = 30.0f;
                contentStream0.drawImage(pdImage, 15.0f, 750.0f + 37.0f, imageWidth, imageHeight);
                contentStream0.close();
                PDPage page1 = document[0].getPage(1);
                PDPageContentStream contentStream1 = new PDPageContentStream(document[0], page1, PDPageContentStream.AppendMode.APPEND, true);

                contentStream1.drawImage(pdImage, 15.0f, 750.0f + 37.0f, imageWidth, imageHeight);
                contentStream1.close();
                PDPage page2 = document[0].getPage(2);
                PDPageContentStream contentStream2 = new PDPageContentStream(document[0], page2, PDPageContentStream.AppendMode.APPEND, true);
                contentStream2.drawImage(pdImage, 15.0f, 750.0f + 37.0f, imageWidth, imageHeight);
                contentStream2.close();
                try {
                    ArrayList<ArrayList<Double>> truncatedTestSignal = new ArrayList<>();
                    for (ArrayList<Double> signal : twelveLeadArrayList) {
                        ArrayList<Double> copySignal = new ArrayList<>(signal);
                        truncatedTestSignal.add(copySignal);
                    }
                    SignalsFromRPeaks signalsFromRPeaks = new SignalsFromRPeaks();
                    truncatedTestSignal = signalsFromRPeaks.findRpeaks(truncatedTestSignal, 0);
                    PDPage page = document[0].getPage(1);
                    PDPageContentStream contentStream = new PDPageContentStream(document[0], page, PDPageContentStream.AppendMode.APPEND, true);
                    // Draw your graph using contentStream
                    float yPosition = 550.0f;
                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                    contentStream.newLineAtOffset(27.0f, yPosition + 60.0f);
                    contentStream.showText("Position : V1"); // Customize the text here for the first graph
                    contentStream.endText();


                    int size = Math.min(truncatedTestSignal.get(0).size(), logEcgDataRef.getV1().size());
                    drawGraph(contentStream, 27.0f, yPosition, truncatedTestSignal.get(0).subList(0, size - 1), logEcgDataRef.getV1().subList(0, size - 1));
                    yPosition -= 74.0f;

                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                    contentStream.newLineAtOffset(27.0f, yPosition + 60.0f);
                    contentStream.showText("Position : V2"); // Customize the text here for the first graph
                    contentStream.endText();
                    size = Math.min(truncatedTestSignal.get(1).size(), logEcgDataRef.getV2().size());
                    drawGraph(contentStream, 27.0f, yPosition, truncatedTestSignal.get(1).subList(0, size - 1), logEcgDataRef.getV2().subList(0, size - 1));
                    yPosition -= 74.0f;

                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                    contentStream.newLineAtOffset(27.0f, yPosition + 60.0f);
                    contentStream.showText("Position : V3"); // Customize the text here for the first graph
                    contentStream.endText();
                    size = Math.min(truncatedTestSignal.get(2).size(), logEcgDataRef.getV3().size());
                    drawGraph(contentStream, 27.0f, yPosition, truncatedTestSignal.get(2).subList(0, size - 1), logEcgDataRef.getV3().subList(0, size - 1));
                    yPosition -= 74.0f;

                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                    contentStream.newLineAtOffset(27.0f, yPosition + 60.0f);
                    contentStream.showText("Position : V4"); // Customize the text here for the first graph
                    contentStream.endText();
                    size = Math.min(truncatedTestSignal.get(3).size(), logEcgDataRef.getV4().size());
                    drawGraph(contentStream, 27.0f, yPosition, truncatedTestSignal.get(3).subList(0, size - 1), logEcgDataRef.getV4().subList(0, size - 1));
                    yPosition -= 74.0f;

                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                    contentStream.newLineAtOffset(27.0f, yPosition + 60.0f);
                    contentStream.showText("Position : V5"); // Customize the text here for the first graph
                    contentStream.endText();
                    size = Math.min(truncatedTestSignal.get(4).size(), logEcgDataRef.getV5().size());
                    drawGraph(contentStream, 27.0f, yPosition, truncatedTestSignal.get(4).subList(0, size - 1), logEcgDataRef.getV5().subList(0, size - 1));
                    yPosition -= 74.0f;

                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                    contentStream.newLineAtOffset(27.0f, yPosition + 60.0f);
                    contentStream.showText("Position : V6"); // Customize the text here for the first graph
                    contentStream.endText();
                    size = Math.min(truncatedTestSignal.get(5).size(), logEcgDataRef.getV6().size());
                    drawGraph(contentStream, 27.0f, yPosition, truncatedTestSignal.get(5).subList(0, size - 1), logEcgDataRef.getV6().subList(0, size - 1));
                    yPosition -= 74.0f;

                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                    contentStream.newLineAtOffset(27.0f, yPosition + 60.0f);
                    contentStream.showText("Position : Lead 1"); // Customize the text here for the first graph
                    contentStream.endText();
                    size = Math.min(truncatedTestSignal.get(7).size(), logEcgDataRef.getL1().size());
                    drawGraph(contentStream, 27.0f, yPosition, truncatedTestSignal.get(7).subList(0, size - 1), logEcgDataRef.getL1().subList(0, size - 1));
                    yPosition -= 74.0f;

                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                    contentStream.newLineAtOffset(27.0f, yPosition + 60.0f);
                    contentStream.showText("Position : Lead 2"); // Customize the text here for the first graph
                    contentStream.endText();
                    size = Math.min(truncatedTestSignal.get(6).size(), logEcgDataRef.getL2().size());
                    drawGraph(contentStream, 27.0f, yPosition, truncatedTestSignal.get(6).subList(0, size - 1), logEcgDataRef.getL2().subList(0, size - 1));

                    contentStream.close();
                    LogEcgData logEcgData = new LogEcgData(twelveLeadArrayList.get(0), twelveLeadArrayList.get(1), twelveLeadArrayList.get(2), twelveLeadArrayList.get(3), twelveLeadArrayList.get(4), twelveLeadArrayList.get(5), twelveLeadArrayList.get(7), twelveLeadArrayList.get(6));
                    writeRemaining(document, onDataReceiveListenerPdfListener, uniqueDeviceId, logEcgData);
                } catch (Exception e) {
                    onDataReceiveListenerPdfListener.onDataReceiveError(e.toString());
                }
            }

        } catch (
                Exception e) {
            onDataReceiveListenerPdfListener.onDataReceiveError(e.toString());
            e.printStackTrace();
        }
    }

    private void writeRemaining(PDDocument[] document, OnDataReceiveListener<PDDocument> onDataReceiveListenerPdfListener, String uniqueDeviceId, LogEcgData logEcgData) throws IOException {
        PDAcroForm acroForm = document[0].getDocumentCatalog().getAcroForm();
        OnDataReceiveListener<DeviceData> onDataReceiveListener1 = new OnDataReceiveListener<DeviceData>() {
            @Override
            public void onDataReceived(DeviceData deviceData) {
                OnDataReceiveListener<DeviceLogData[]> onDataReceiveListener = new OnDataReceiveListener<DeviceLogData[]>() {
                    @Override
                    public void onDataReceived(DeviceLogData[] data) {
                        Map<Integer, DeviceLogData> latestLogs = new HashMap<>();

                        for (DeviceLogData logEntry : data) {
                            int stage = logEntry.getLogOfStage();
                            long timeStamp = logEntry.getTimeStamp();

                            // Check if this log entry is the latest for the given stage
                            if (!latestLogs.containsKey(stage) || timeStamp > latestLogs.get(stage).getTimeStamp()) {
                                latestLogs.put(stage, logEntry);
                            }
                        }
                        // Create a list to store the latest log entries
                        List<DeviceLogData> sortedLogs = new ArrayList<>(latestLogs.values());
                        // Sort the latest log entries by timestamp (optional)
                        Collections.sort(sortedLogs, Comparator.comparingLong(DeviceLogData::getTimeStamp).reversed());
                        HashMap<Integer, DeviceLogData> result = new HashMap<>();
                        for (Map.Entry<Integer, DeviceLogData> entry : latestLogs.entrySet()) {
                            int stage = entry.getKey();
                            DeviceLogData logEntry = entry.getValue();
                            result.put(stage, logEntry);
                        }

                        for (Map.Entry<Integer, DeviceLogData> entry : result.entrySet()) {
                            int stage = entry.getKey();
                            DeviceLogData logEntry = entry.getValue();
                            System.out.println("Stage: " + stage + ", Result: " + logEntry.getResult() + ", TimeStamp: " + logEntry.getTimeStamp() + ", FailREason: " + logEntry.getFailReason());
                        }
                        try {
                            InterPretetions interPretetions = new InterPretetions();
                            ArrayList<ArrayList<Double>> twelveLeadArrayList = new ArrayList<>();
                            System.out.println(logEcgData.getV1());
                            twelveLeadArrayList.add(logEcgData.getV1());
                            twelveLeadArrayList.add(logEcgData.getV2());
                            twelveLeadArrayList.add(logEcgData.getV3());
                            twelveLeadArrayList.add(logEcgData.getV4());
                            twelveLeadArrayList.add(logEcgData.getV5());
                            twelveLeadArrayList.add(logEcgData.getV6());
                            twelveLeadArrayList.add(logEcgData.getL2());
                            twelveLeadArrayList.add(logEcgData.getL1());
                            Map<String, String> ecgInterpretetion = interPretetions.doInterpretetions(twelveLeadArrayList);
                            PDTextField field = (PDTextField) acroForm.getField("textfield_device_id");
                            field.setValue(uniqueDeviceId);

                            field = (PDTextField) acroForm.getField("textfield_mfg_date_time");
                            if (uniqueDeviceId.contains("SPPR") && result.size() >= 6) {
                                Date date = new Date(result.get(6).getTimeStamp() * 1000);
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a");
                                String formattedDateTime = sdf.format(date);
                                System.out.println(formattedDateTime);
                                field.setValue(formattedDateTime);
                            }
                            if (uniqueDeviceId.contains("SPLG") && result.size() >= 6) {
                                Date date = new Date(result.get(6).getTimeStamp() * 1000);
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a");
                                String formattedDateTime = sdf.format(date);
                                System.out.println(formattedDateTime);
                                field.setValue(formattedDateTime);
                            } else if (uniqueDeviceId.contains("SPNE") && result.size() >= 4) {
                                Date date = new Date(result.get(4).getTimeStamp() * 1000);
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a");
                                String formattedDateTime = sdf.format(date);
                                System.out.println(formattedDateTime);
                                field.setValue(formattedDateTime);

                            }

                            field = (PDTextField) acroForm.getField("textfield_production_plant_id");
                            field.setValue(deviceData.getProductionPlantId());
                            field.setReadOnly(true);

                            field = (PDTextField) acroForm.getField("textfield_batch_id");
                            field.setValue(deviceData.getBatchId());
                            field.setReadOnly(true);

                            field = (PDTextField) acroForm.getField("textfield_firmware_version");
                            field.setValue(deviceData.getFirmwareVersion());
                            field.setReadOnly(true);
                            field = (PDTextField) acroForm.getField("textfield_stage_1_operator_id");
                            if (!result.isEmpty()) {
                                field.setValue(result.get(1).getUsername());
                                field = (PDTextField) acroForm.getField("textfield_stage_1_remark");
                                if (result.get(1).getResult().equals("pass")) {
                                    field.setValue("OK");
                                    field.setReadOnly(true);
                                } else {
                                    field.setValue("Not OK");
                                    field.setReadOnly(true);
                                }
                            }
                            field = (PDTextField) acroForm.getField("textfield_stage_2_operator_id");
                            if (result.size() >= 2) {
                                field.setValue(result.get(2).getUsername());
                                field = (PDTextField) acroForm.getField("textfield_stage_2a_remark");
                                if (result.get(2).getResult().equals("pass"))
                                    field.setValue("OK");
                                else
                                    field.setValue("Not OK");
                                field = (PDTextField) acroForm.getField("textfield_stage_2b_remark");
                                if (result.get(2).getResult().equals("pass"))
                                    field.setValue("OK");
                                else
                                    field.setValue("Not OK");
                                field = (PDTextField) acroForm.getField("textfield_stage_2c_remark");
                                if (result.get(2).getResult().equals("pass"))
                                    field.setValue("OK");
                                else
                                    field.setValue("Not OK");
                            }
                            if (uniqueDeviceId.contains("SPLG")) {
                                field = (PDTextField) acroForm.getField("textfield_stage_3_operator_id");
                                if (result.size() >= 3) {
                                    field.setValue(result.get(3).getUsername());
                                    field = (PDTextField) acroForm.getField("textfield_stage_3_remark");
                                    if (result.get(3).getResult().equals("pass"))
                                        field.setValue("OK");
                                    else
                                        field.setValue("Not OK");
                                }
                            }
                            if (uniqueDeviceId.contains("SPPR")) {
                                field = (PDTextField) acroForm.getField("textfield_stage_3_operator_id");
                                if (result.size() >= 3) {
                                    field.setValue(result.get(3).getUsername());
                                    field = (PDTextField) acroForm.getField("textfield_stage_3_remark");
                                    if (result.get(3).getResult().equals("pass"))
                                        field.setValue("OK");
                                    else
                                        field.setValue("Not OK");
                                }
                            }
                            if (uniqueDeviceId.contains("SPNE")) {
                                field = (PDTextField) acroForm.getField("textfield_stage_4_operator_id");
                                if (result.size() >= 3) {
                                    field.setValue(result.get(3).getUsername());
                                    field = (PDTextField) acroForm.getField("textfield_stage_4_remark");
                                    if (result.get(3).getResult().equals("pass"))
                                        field.setValue("OK");
                                    else
                                        field.setValue("Not OK");
                                }
                            } else {
                                field = (PDTextField) acroForm.getField("textfield_stage_4_operator_id");
                                if (result.size() >= 4) {
                                    field.setValue(result.get(4).getUsername());
                                    field = (PDTextField) acroForm.getField("textfield_stage_4_remark");
                                    if (result.get(4).getResult().equals("pass"))
                                        field.setValue("OK");
                                    else
                                        field.setValue("Not OK");
                                }
                            }
                            if (uniqueDeviceId.contains("SPLG")) {
                                field = (PDTextField) acroForm.getField("textfield_stage_5_operator_id");
                                if (result.size() >= 5) {
                                    field.setValue(result.get(5).getUsername());
                                    field = (PDTextField) acroForm.getField("textfield_stage_5_remark");
                                    if (result.get(5).getResult().equals("pass"))
                                        field.setValue("OK");
                                    else
                                        field.setValue("Not OK");
                                }
                            }if (uniqueDeviceId.contains("SPPR")) {
                                field = (PDTextField) acroForm.getField("textfield_stage_5_operator_id");
                                if (result.size() >= 5) {
                                    field.setValue(result.get(5).getUsername());
                                    field = (PDTextField) acroForm.getField("textfield_stage_5_remark");
                                    if (result.get(5).getResult().equals("pass"))
                                        field.setValue("OK");
                                    else
                                        field.setValue("Not OK");
                                }
                            }
                            if (uniqueDeviceId.contains("SPNE")) {
                                field = (PDTextField) acroForm.getField("textfield_stage_6_operator_id");
                                if (result.size() >= 4) {
                                    field.setValue(result.get(4).getUsername());
                                    field = (PDTextField) acroForm.getField("textfield_stage_6a_remark");
                                    if (result.get(4).getResult().equals("pass"))
                                        field.setValue("OK");
                                    else
                                        field.setValue("Not OK");
                                    field = (PDTextField) acroForm.getField("textfield_stage_6b_remark");
                                    if (result.get(4).getResult().equals("pass"))
                                        field.setValue("OK");
                                    else
                                        field.setValue("Not OK");
                                    field = (PDTextField) acroForm.getField("textfield_stage_6c_remark");
                                    if (result.get(4).getResult().equals("pass"))
                                        field.setValue("OK");
                                    else
                                        field.setValue("Not OK");
                                }
                            } else {
                                field = (PDTextField) acroForm.getField("textfield_stage_6_operator_id");
                                if (result.size() >= 6) {
                                    field.setValue(result.get(6).getUsername());
                                    field = (PDTextField) acroForm.getField("textfield_stage_6a_remark");
                                    if (result.get(6).getResult().equals("pass"))
                                        field.setValue("OK");
                                    else
                                        field.setValue("Not OK");
                                    field = (PDTextField) acroForm.getField("textfield_stage_6b_remark");
                                    if (result.get(6).getResult().equals("pass"))
                                        field.setValue("OK");
                                    else
                                        field.setValue("Not OK");
                                    field = (PDTextField) acroForm.getField("textfield_stage_6c_remark");
                                    if (result.get(6).getResult().equals("pass"))
                                        field.setValue("OK");
                                    else
                                        field.setValue("Not OK");
                                }
                            }

                            if(uniqueDeviceId.contains("SPLG")) {
                                if (result.size() >= 6&& result.get(6).getStage() == 7) {
                                    field = (PDTextField) acroForm.getField("textfield_final_assessment_status");
                                    field.setValue("Passed");
                                    field = (PDTextField) acroForm.getField("textfield_final_assessment_reason");
                                    field.setValue("All Stages Passed");
                                    field = (PDTextField) acroForm.getField("textfield_final_assessment_action_required");
                                    field.setValue("Device Is Ready");
                                } else {
                                    field = (PDTextField) acroForm.getField("textfield_final_assessment_status");
                                    field.setValue("failed");
                                    field = (PDTextField) acroForm.getField("textfield_final_assessment_reason");
                                    field.setValue("All Stages Not Passed");
                                    field = (PDTextField) acroForm.getField("textfield_final_assessment_action_required");
                                    field.setValue("Device Is Not Ready");
                                }
                            }
                            if(uniqueDeviceId.contains("SPPR")) {
                                if (result.size() >= 6&& result.get(6).getStage() == 7) {
                                    field = (PDTextField) acroForm.getField("textfield_final_assessment_status");
                                    field.setValue("Passed");
                                    field = (PDTextField) acroForm.getField("textfield_final_assessment_reason");
                                    field.setValue("All Stages Passed");
                                    field = (PDTextField) acroForm.getField("textfield_final_assessment_action_required");
                                    field.setValue("Device Is Ready");
                                } else {
                                    field = (PDTextField) acroForm.getField("textfield_final_assessment_status");
                                    field.setValue("failed");
                                    field = (PDTextField) acroForm.getField("textfield_final_assessment_reason");
                                    field.setValue("All Stages Not Passed");
                                    field = (PDTextField) acroForm.getField("textfield_final_assessment_action_required");
                                    field.setValue("Device Is Not Ready");
                                }
                            }
                            if(uniqueDeviceId.contains("SPNE")) {
                            if (result.size() >= 4 && result.get(4).getStage() == 5){
                                    field = (PDTextField) acroForm.getField("textfield_final_assessment_status");
                                    field.setValue("Passed");
                                    field = (PDTextField) acroForm.getField("textfield_final_assessment_reason");
                                    field.setValue("All Stages Passed");
                                    field = (PDTextField) acroForm.getField("textfield_final_assessment_action_required");
                                    field.setValue("Device Is Ready");
                                } else{
                                    field = (PDTextField) acroForm.getField("textfield_final_assessment_status");
                                    field.setValue("failed");
                                    field = (PDTextField) acroForm.getField("textfield_final_assessment_reason");
                                    field.setValue("All Stages Not Passed");
                                    field = (PDTextField) acroForm.getField("textfield_final_assessment_action_required");
                                    field.setValue("Device Is Not Ready");
                                }
                            }

//  -------------------------PAGE 1 COMPLETE----------------------


                            field = (PDTextField) acroForm.getField("textfield_ecg_pr_interval_ccv");
                            field.setValue(ecgInterpretetion.get("pr"));
                            field = (PDTextField) acroForm.getField("textfield_ecg_qrs_interval_ccv");
                            field.setValue(ecgInterpretetion.get("qrs"));
                            field = (PDTextField) acroForm.getField("textfield_ecg_qt_interval_ccv");
                            field.setValue(ecgInterpretetion.get("qt"));
                            field = (PDTextField) acroForm.getField("textfield_ecg_qtc_interval_ccv");
                            field.setValue(ecgInterpretetion.get("qtc"));
                            field = (PDTextField) acroForm.getField("textfield_ecg_rr_interval_ccv");
                            field.setValue(ecgInterpretetion.get("rr"));
                            field = (PDTextField) acroForm.getField("textfield_ecg_heart_rate_ccv");
                            field.setValue(ecgInterpretetion.get("heartRate"));
                            field = (PDTextField) acroForm.getField("textfield_ecg_r_amplitude_ccv");
                            field.setValue(ecgInterpretetion.get("rAmplitude"));
                            field = (PDTextField) acroForm.getField("textfield_ecg_s_amplitude_ccv");
                            field.setValue(ecgInterpretetion.get("sAmplitude"));
                            field = (PDTextField) acroForm.getField("textfield_ecg_q_amplitude_ccv");
                            field.setValue(ecgInterpretetion.get("qAmplitude"));


                            DecimalFormat decimalFormat = new DecimalFormat("#.##");


                            field = (PDTextField) acroForm.getField("textfield_v1_samples_obtained_ccv");
                            field.setValue(String.valueOf(logEcgData.getV1().size()));
                            field = (PDTextField) acroForm.getField("textfield_v2_samples_obtained_ccv");
                            field.setValue(String.valueOf(logEcgData.getV2().size()));
                            field = (PDTextField) acroForm.getField("textfield_v3_samples_obtained_ccv");
                            field.setValue(String.valueOf(logEcgData.getV3().size()));
                            field = (PDTextField) acroForm.getField("textfield_v4_samples_obtained_ccv");
                            field.setValue(String.valueOf(logEcgData.getV4().size()));
                            field = (PDTextField) acroForm.getField("textfield_v5_samples_obtained_ccv");
                            field.setValue(String.valueOf(logEcgData.getV5().size()));
                            field = (PDTextField) acroForm.getField("textfield_v6_samples_obtained_ccv");
                            field.setValue(String.valueOf(logEcgData.getV6().size()));
                            field = (PDTextField) acroForm.getField("textfield_lead1_samples_obtained_ccv");
                            field.setValue(String.valueOf(logEcgData.getL1().size()));
                            field = (PDTextField) acroForm.getField("textfield_lead2_samples_obtained_ccv");
                            field.setValue(String.valueOf(logEcgData.getL2().size()));

                            decimalFormat = new DecimalFormat("#.###");

                            field = (PDTextField) acroForm.getField("textfield_ecg_pr_interval_difference");
                            field.setValue(decimalFormat.format(165 - Double.parseDouble(ecgInterpretetion.get("pr"))));

                            field = (PDTextField) acroForm.getField("textfield_ecg_qrs_interval_difference");
                            field.setValue(decimalFormat.format(112 - Double.parseDouble(ecgInterpretetion.get("qrs"))));

                            field = (PDTextField) acroForm.getField("textfield_ecg_qt_interval_difference");
                            field.setValue(decimalFormat.format(415 - Double.parseDouble(ecgInterpretetion.get("qt"))));

                            field = (PDTextField) acroForm.getField("textfield_ecg_qtc_interval_difference");
                            field.setValue(decimalFormat.format(471 - Double.parseDouble(ecgInterpretetion.get("qtc"))));

                            field = (PDTextField) acroForm.getField("textfield_ecg_rr_interval_difference");
                            field.setValue(decimalFormat.format(Double.parseDouble(ecgInterpretetion.get("rr"))));

                            field = (PDTextField) acroForm.getField("textfield_ecg_heart_rate_difference");
                            field.setValue(decimalFormat.format(82 - Double.parseDouble(ecgInterpretetion.get("heartRate"))));

                            field = (PDTextField) acroForm.getField("textfield_ecg_q_amplitude_difference");
                            field.setValue(decimalFormat.format(-0.0663464351 - Double.parseDouble(ecgInterpretetion.get("qAmplitude"))));

                            field = (PDTextField) acroForm.getField("textfield_ecg_r_amplitude_difference");
                            field.setValue(decimalFormat.format(0.5449338653 - Double.parseDouble(ecgInterpretetion.get("rAmplitude"))));

                            field = (PDTextField) acroForm.getField("textfield_ecg_s_amplitude_difference");
                            field.setValue(decimalFormat.format(-0.0381112677 - Double.parseDouble(ecgInterpretetion.get("sAmplitude"))));


                            field = (PDTextField) acroForm.getField("textfield_ecg_pr_interval_result");
                            if (Double.parseDouble(ecgInterpretetion.get("pr")) >= 140 && Double.parseDouble(ecgInterpretetion.get("pr")) <= 165)
                                field.setValue("OK");
                            else
                                field.setValue("NOT OK");

                            field = (PDTextField) acroForm.getField("textfield_ecg_qrs_interval_result");
                            if (Double.parseDouble(ecgInterpretetion.get("qrs")) >= 82 && Double.parseDouble(ecgInterpretetion.get("qrs")) <= 112)
                                field.setValue("OK");
                            else
                                field.setValue("NOT OK");

                            field = (PDTextField) acroForm.getField("textfield_ecg_qt_interval_result");
                            if (Double.parseDouble(ecgInterpretetion.get("qt")) >= 350 && Double.parseDouble(ecgInterpretetion.get("qt")) <= 415)
                                field.setValue("OK");
                            else
                                field.setValue("NOT OK");
                            field = (PDTextField) acroForm.getField("textfield_ecg_qtc_interval_result");
                            if (Double.parseDouble(ecgInterpretetion.get("qtc")) >= 411 && Double.parseDouble(ecgInterpretetion.get("qtc")) <= 471)
                                field.setValue("OK");
                            else
                                field.setValue("NOT OK");
                            field = (PDTextField) acroForm.getField("textfield_ecg_rr_interval_result");
//                            if (Integer.parseInt(ecgInterpretetion.get("pr")) >= 140 && Integer.parseInt(ecgInterpretetion.get("pr")) <= 165)
//                                field.setValue("OK");
//                            else
                            field.setValue("OK");
                            field = (PDTextField) acroForm.getField("textfield_ecg_heart_rate_result");
                            if (Double.parseDouble(ecgInterpretetion.get("heartRate")) >= 78 && Double.parseDouble(ecgInterpretetion.get("heartRate")) <= 82)
                                field.setValue("OK");
                            else
                                field.setValue("NOT OK");
                            field = (PDTextField) acroForm.getField("textfield_ecg_q_amplitude_result");
                            field.setValue(ecgInterpretetion.get("qAmplitudeEvaluation"));
                            field = (PDTextField) acroForm.getField("textfield_ecg_r_amplitude_result");

                                field.setValue(ecgInterpretetion.get("rAmplitudeEvaluation"));

                            field = (PDTextField) acroForm.getField("textfield_ecg_s_amplitude_result");
                            field.setValue(ecgInterpretetion.get("sAmplitudeEvaluation"));
                            field = (PDTextField) acroForm.getField("textfield_v1_samples_obtained_difference");
                            field.setValue("0");
                            field = (PDTextField) acroForm.getField("textfield_v2_samples_obtained_difference");
                            field.setValue("0");
                            field = (PDTextField) acroForm.getField("textfield_v3_samples_obtained_difference");
                            field.setValue("0");
                            field = (PDTextField) acroForm.getField("textfield_v4_samples_obtained_difference");
                            field.setValue("0");
                            field = (PDTextField) acroForm.getField("textfield_v5_samples_obtained_difference");
                            field.setValue("0");
                            field = (PDTextField) acroForm.getField("textfield_v6_samples_obtained_difference");
                            field.setValue("0");
                            field = (PDTextField) acroForm.getField("textfield_lead1_samples_obtained_difference");
                            field.setValue("0");
                            field = (PDTextField) acroForm.getField("textfield_lead2_samples_obtained_difference");
                            field.setValue("0");
                            field = (PDTextField) acroForm.getField("textfield_v1_samples_obtained_result");
                            field.setValue("OK");
                            field = (PDTextField) acroForm.getField("textfield_v2_samples_obtained_result");
                            field.setValue("OK");
                            field = (PDTextField) acroForm.getField("textfield_v3_samples_obtained_result");
                            field.setValue("OK");
                            field = (PDTextField) acroForm.getField("textfield_v1_samples_obtained_result");
                            field.setValue("OK");
                            field = (PDTextField) acroForm.getField("textfield_v4_samples_obtained_result");
                            field.setValue("OK");
                            field = (PDTextField) acroForm.getField("textfield_v5_samples_obtained_result");
                            field.setValue("OK");
                            field = (PDTextField) acroForm.getField("textfield_v6_samples_obtained_result");
                            field.setValue("OK");
                            field = (PDTextField) acroForm.getField("textfield_lead1_samples_obtained_result");
                            field.setValue("OK");
                            field = (PDTextField) acroForm.getField("textfield_lead2_samples_obtained_result");
                            field.setValue("OK");
                            PDDocumentInformation docInfo = new PDDocumentInformation();
                            docInfo.setTitle(uniqueDeviceId);
                            document[0].setDocumentInformation(docInfo);
                            for (PDField field2 : acroForm.getFields()) {
                                field2.setReadOnly(true);
                            }
                            onDataReceiveListenerPdfListener.onDataReceived(document[0]);
                        } catch (Exception e) {
                            e.printStackTrace();
                            onDataReceiveListenerPdfListener.onDataReceiveError(e.toString());
                        }


                    }

                    @Override
                    public void onDataReceiveError(String errorMsg) {
                        try {
                            document[0].close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        onDataReceiveListenerPdfListener.onDataReceiveError(errorMsg);

                    }
                };
                DatabaseUtility.getAllLogsOfDevice(uniqueDeviceId, onDataReceiveListener);
            }

            @Override
            public void onDataReceiveError(String errorMsg) {
                try {
                    document[0].close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                onDataReceiveListenerPdfListener.onDataReceiveError(errorMsg);
            }
        };
        DatabaseUtility.getDeviceData(uniqueDeviceId, onDataReceiveListener1);


    }


    private static LogEcgData createref() {
        LoadTwelveLeadReferenceSignal loadTwelveLeadReferenceSignal = new LoadTwelveLeadReferenceSignal();
        LogEcgData logEcgData = loadTwelveLeadReferenceSignal.loadSignal(1);
        return logEcgData;
    }

    private static void drawGraph(PDPageContentStream contentStream, float x, float y, List<Double> series1, List<Double> series2) throws IOException {
        float graphWidth = 535.0f;
        float graphHeight = 54.0f;
        int totalHorizontalLines = 25;
        int totalVerticalLines = 250;

        float scaleX = graphWidth / (series1.size() - 1);
        float scaleY = graphHeight / 1024; // Assuming max value for series is 100

        // Setting up the grid spacings
        float smallSquareSizeX = graphWidth / totalVerticalLines; // assuming you want 500 small squares across the width
        float smallSquareSizeY = graphHeight / totalHorizontalLines; // assuming you want 100 small squares across the height

        contentStream.setStrokingColor(200, 200, 200); // Light gray color for the grid

        // Drawing the grid
        for (int i = 0; i <= totalVerticalLines; i++) {
            // If it's a line of a large square
            if (i % 5 == 0) {
                contentStream.setLineWidth(1f); // thicker line for larger square
            } else {
                contentStream.setLineWidth(0.3f); // thinner line for smaller square
            }
            contentStream.moveTo(x + i * smallSquareSizeX, y);
            contentStream.lineTo(x + i * smallSquareSizeX, y + graphHeight);
            contentStream.stroke();
        }

        for (int i = 0; i <= totalHorizontalLines; i++) {

            if (i % 5 == 0) {
                contentStream.setLineWidth(1f); // thicker line for larger square
            } else {
                contentStream.setLineWidth(0.3f); // thinner line for smaller square
            }
            contentStream.moveTo(x, y + i * smallSquareSizeY);
            contentStream.lineTo(x + graphWidth, y + i * smallSquareSizeY);
            contentStream.stroke();
        }


        contentStream.setStrokingColor(Color.BLUE);
        contentStream.setLineWidth(0.5f);
        for (int i = 1; i < series1.size(); i++) {
            contentStream.moveTo(x + (i - 1) * scaleX, (float) (y + series1.get(i - 1) * scaleY));
            contentStream.lineTo(x + i * scaleX, (float) (y + series1.get(i) * scaleY));
            contentStream.stroke();
        }

        contentStream.setStrokingColor(Color.RED);
        contentStream.setLineWidth(0.5f);
        for (int i = 1; i < series2.size(); i++) {
            contentStream.moveTo(x + (i - 1) * scaleX, (float) (y + series2.get(i - 1) * scaleY));
            contentStream.lineTo(x + i * scaleX, (float) (y + series2.get(i) * scaleY));
            contentStream.stroke();
        }

    }

}
