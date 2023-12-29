package in.sunfox.healthcare.java.spandan_qms.device_fLashing_and_configuration_classes;

import com.fazecast.jSerialComm.SerialPort;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class DeviceConfigurationNeo {
    public ArrayList<String> configureDevice(String uniqueDeviceId, String masterKey, SerialPort mySerialPort, Label label) throws NoSuchAlgorithmException {
        try {
            mySerialPort.setComPortParameters(115200, 8, 1, 0);
            mySerialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 2000, 0);
            mySerialPort.openPort();
        } catch (Exception e) {
            throw new RuntimeException();
        }

        String command = "ADMIN_SUNFOX";
        byte[] WriteByte = command.getBytes();
        int bytesTxed = 0;
        ArrayList<String>result = new ArrayList<>();
        VBox anchorPane = new VBox();
        anchorPane.setAlignment(Pos.CENTER);

        if(mySerialPort.isOpen()) {

            bytesTxed = mySerialPort.writeBytes(WriteByte, command.getBytes().length);
            try {
                Thread.sleep(20);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.print(" Bytes Transmitted -> " + bytesTxed + "\n");
            command = "SET_DID" + uniqueDeviceId ;
            WriteByte = command.getBytes();
            Platform.runLater(()->{
                label.setStyle("-fx-text-fill:green");
                label.setText("DID----->" + uniqueDeviceId+"\n");
            });
            bytesTxed = mySerialPort.writeBytes(WriteByte, command.getBytes().length);
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.print(" Bytes Transmitted -> " + bytesTxed + "\n");
            if(bytesTxed<20){
                mySerialPort.closePort();
                return null;
            }


            String s = "";
            command = "GET_MID";
            WriteByte = command.getBytes();
            mySerialPort.writeBytes(WriteByte, command.getBytes().length);
            byte[] readBuffer = new byte[1024];
            try {
                {
                    while (mySerialPort.readBytes(readBuffer, readBuffer.length) != 0) {
                        System.out.println(mySerialPort.getLastErrorLocation());
                        if (mySerialPort.getLastErrorLocation() == 763 || mySerialPort.getLastErrorLocation() == 1024 || mySerialPort.getLastErrorLocation() == 1029 || mySerialPort.getLastErrorLocation() == 955) {
                            mySerialPort.closePort();
                            return null;
                        }
                        System.out.println(s);
                        s = new String(readBuffer, StandardCharsets.UTF_8).trim();
                        String finalS = s.substring(7);
                        Platform.runLater(()->{
                            label.setText(label.getText() + "MID----->" + finalS + "\n");


                        });
                        result.add(s.substring(7));
                        break;

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            s = "";
            command = "GET_INF";
            WriteByte = command.getBytes();
            mySerialPort.writeBytes(WriteByte, command.getBytes().length);
            readBuffer = new byte[1024];
            try {
                {
                    while (mySerialPort.readBytes(readBuffer, readBuffer.length) != 0) {
                        System.out.println(mySerialPort.getLastErrorLocation());
                        if (mySerialPort.getLastErrorLocation() == 763 || mySerialPort.getLastErrorLocation() == 1024 || mySerialPort.getLastErrorLocation() == 1029  || mySerialPort.getLastErrorLocation() == 955) {
                            mySerialPort.closePort();
                            return null;
                        }
                        System.out.println(s);
                        s = new String(readBuffer, StandardCharsets.UTF_8).trim();
                        String finalS1 = s.substring(23,29);
                        Platform.runLater(()->{
                            label.setText(label.getText() + "Version----->" + finalS1 + "\n");
                        });
                        result.add(s.substring(23,29));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(uniqueDeviceId.contains(".")) {
                if (masterKey==null||masterKey.contains("null")|| masterKey.isEmpty()) {
                    masterKey= "####################";
                }
                command = "SET_CID" + masterKey;
                WriteByte = command.getBytes();
                String finalMasterKey = masterKey;
                Platform.runLater(() -> {
                    label.setText(label.getText() + "CID----->" + finalMasterKey + "\n");
                });
                bytesTxed = mySerialPort.writeBytes(WriteByte, command.getBytes().length);
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.print(" Bytes Transmitted -> " + bytesTxed + "\n");
                result.add(finalMasterKey);
                if (bytesTxed < 20) {
                    mySerialPort.closePort();
                    return null;
                }
            }
            String generatedHashValue = generateHash(asciiToHex(uniqueDeviceId),result.get(0));
            command = "ADMIN_SUNFOX";
            WriteByte = command.getBytes();
            mySerialPort.writeBytes(WriteByte, command.getBytes().length);
            command = "SET_HAS"+generatedHashValue;
            WriteByte = command.getBytes();
            System.out.println(mySerialPort.writeBytes(WriteByte, command.getBytes().length));
            s = "";
            try {
                Thread.sleep(1000);
            }catch (Exception e){
                e.printStackTrace();
            }
            command = "GET_HAS";
            WriteByte = command.getBytes();
            mySerialPort.writeBytes(WriteByte, command.getBytes().length);
            readBuffer = new byte[1024];
            try {
                {
                    while (mySerialPort.readBytes(readBuffer, readBuffer.length) != 0) {
                        System.out.println(mySerialPort.getLastErrorLocation());
                        if (mySerialPort.getLastErrorLocation() == 763 || mySerialPort.getLastErrorLocation() == 1024 || mySerialPort.getLastErrorLocation() == 1029 || mySerialPort.getLastErrorLocation() == 955 ) {
                            mySerialPort.closePort();
                            return null;
                        }
                        System.out.println(s);
                        s = new String(readBuffer, StandardCharsets.UTF_8).trim();
                        String finalS = s;
                        Platform.runLater(()->{
                            label.setText(label.getText() + "Hash----->" + finalS + "\n");
                        });
                        mySerialPort.closePort();
                        return result;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            return null;
        }
        return null;
    }
    private static String asciiToHex(String asciiStr) {
        char[] chars = asciiStr.toCharArray();
        StringBuilder hex = new StringBuilder();
        for (char ch : chars) {
            hex.append(Integer.toHexString((int) ch));
        }
        return hex.toString();
    }

    private String generateHash(String deviceId, String microControllerId) throws NoSuchAlgorithmException {
        String hashInput = deviceId + microControllerId;
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(hashInput.getBytes(StandardCharsets.UTF_8));
        BigInteger number = new BigInteger(1, hash);

        // Converting the message digest into a Hexa decimal value.
        StringBuilder hexString = new StringBuilder(number.toString(16));

        while (hexString.length() < 64) {
            hexString.insert(0, '0');
        }
        return hexString.toString();
    }
}
