package in.sunfox.healthcare.java.spandan_qms.spandan_pro_serial_communication.SpandanUnlocking;

import com.fazecast.jSerialComm.SerialPort;
import in.sunfox.healthcare.java.spandan_qms.spandan_neo_serial_communication.SpandanUnlocking.DecodingUtility;
import in.sunfox.healthcare.java.spandan_qms.spandan_neo_serial_communication.SpandanUnlocking.RandomNumberGenerator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UnlockMechanism {

    @FXML
    TextArea consoleUiTextArea;
//    static SerialPort mySerialPort;
    public String mode = "";
    static String generatedHashValue = "";

    public boolean unlocking2(String mode,SerialPort mySerialPort) throws NoSuchAlgorithmException {
//        SerialPort[] AvailablePorts = SerialPort.getCommPorts();
//        mySerialPort = AvailablePorts[0];
        int BaudRate = 115200;
        int DataBits = 8;
        int StopBits = SerialPort.ONE_STOP_BIT;
        int Parity = SerialPort.NO_PARITY;

        mySerialPort.setComPortParameters(BaudRate,
                DataBits,
                StopBits,
                Parity);

        mySerialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 1000, 0);

        mySerialPort.openPort();

        if (mySerialPort.isOpen())
            System.out.println("Port is Open ");
        else
            System.out.println(" Port not open ");


        String encoded16 = RandomNumberGenerator.generateRandomNumber(new StringBuilder("16"));


        if (mode.equals("USER_MODE")) {
            String rtuResponse = getCommandFromSerialPort("USER", "RTU" + encoded16, consoleUiTextArea,mySerialPort);
            if (rtuResponse.equalsIgnoreCase("error")) {
                mySerialPort.closePort();
                return false;
            }
            rtuResponse = rtuResponse.substring(7);
            String decoded = DecodingUtility.decodeRtuFromSerialPort(rtuResponse, encoded16);
            String deviceId = decoded.substring(0, 40).toLowerCase();
            String microControllerId = decoded.substring(40, 64).toUpperCase();
            String receivedHash = decoded.substring(64).toLowerCase();

            StringBuilder asciiHashValue = new StringBuilder();
            for (int i = 0; i < receivedHash.length() - 1; i += 2) {
                String hexCharacter = receivedHash.substring(i, i + 2);
                int asciiValue = Integer.parseInt(hexCharacter, 16);
                char asciiCharacter = (char) asciiValue;
                asciiHashValue.append(asciiCharacter);
            }
            receivedHash = String.valueOf(asciiHashValue);

            generatedHashValue = generateHash(deviceId, microControllerId);
            generatedHashValue = generatedHashValue.trim();
            if (generatedHashValue.equals(receivedHash)) {
                System.out.println("Hash Matched");
                System.out.println(generatedHashValue);
                getCommandFromSerialPort("USER", "RTC" + generatedHashValue, consoleUiTextArea, mySerialPort);
                mySerialPort.closePort();
                return true;
            } else
                System.out.println("Hash not matched");
            System.out.println(generatedHashValue);
            mySerialPort.closePort();
            return false;
        }
        mySerialPort.closePort();
        return false;
    }



    private static String generateHash(String deviceId, String microControllerId) throws NoSuchAlgorithmException {

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
//        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
//        String input =deviceId+microControllerId;
//        byte[] hashBytes = sha256.digest(input.getBytes());
//
//        StringBuilder hash = new StringBuilder();
//        for (byte b : hashBytes) {
//            hash.append(String.format("%02x", b));
//        }
//        return hash.toString();
    }

    private static String asciiToHex(String asciiStr) {
        char[] chars = asciiStr.toCharArray();
        StringBuilder hex = new StringBuilder();
        for (char ch : chars) {
            hex.append(Integer.toHexString((int) ch));
        }
        return hex.toString();
    }

    private static String getCommandFromSerialPort(String mode, String command, TextArea consoleUiTextArea, SerialPort mySerialPort) {
        String s = "";

        try {
            if(mySerialPort.isOpen()) {
                byte[] WriteByte = command.getBytes();
                mySerialPort.writeBytes(WriteByte, command.getBytes().length);

                byte[] readBuffer = new byte[1024];
                try {
                    if (command.equals("STA")) {
                        readBuffer = new byte[4];
                        StringBuilder s2 = new StringBuilder();
                        while (mySerialPort.readBytes(readBuffer, readBuffer.length) != 0) {

                            System.out.println(mySerialPort.getLastErrorLocation());
                            if (mySerialPort.getLastErrorLocation() == 1024 || mySerialPort.getLastErrorLocation() == 1029) {
                                mySerialPort.flushIOBuffers();
                                mySerialPort.closePort();


                                mySerialPort = null;
                                System.out.println("Error occurred");
                                return "error";
                            }
                            s = new String(readBuffer, StandardCharsets.UTF_8);
                            for (int i = 0; i < s.length(); i++) {
                                String sTemp = String.valueOf(s.charAt(i));
                                switch (sTemp) {
                                    case "2" -> sTemp = "0";
                                    case "3" -> sTemp = "1";
                                    case "1" -> sTemp = "2";
                                    case "5" -> sTemp = "3";
                                    case "7" -> sTemp = "4";
                                    case "9" -> sTemp = "5";
                                    case "4" -> sTemp = "6";
                                    case "6" -> sTemp = "7";
                                    case "0" -> sTemp = "8";
                                    case "8" -> sTemp = "9";
                                    default -> String.valueOf(s.charAt(i));
                                }
                                s2.append(sTemp);
                            }
                            System.out.print(s2);

                            s2 = new StringBuilder("");
                        }

                    } else {

                        while (mySerialPort.readBytes(readBuffer, readBuffer.length) != 0) {
                            System.out.println(mySerialPort.getLastErrorLocation());
                            if (mySerialPort.getLastErrorLocation() == 763 || mySerialPort.getLastErrorLocation() == 1024 || mySerialPort.getLastErrorLocation() == 1029 || mySerialPort.getLastErrorLocation() == 955) {
                                mySerialPort.closePort();
                                return "error";
                            }
                            s = new String(readBuffer, StandardCharsets.UTF_8).trim();

                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        s += "\n";
        String finalS = s;
        if (mode.equals("ADMIN"))
            Platform.runLater(() -> {
                consoleUiTextArea.appendText(finalS);
            });
        return s.trim();
    }

    private static void sendCommandToSerialPort(String command, TextArea consoleUiTextArea,SerialPort mySerialPort) {
        try {
            byte[] WriteByte = command.getBytes();

            int bytesTxed = 0;

            bytesTxed = mySerialPort.writeBytes(WriteByte, command.getBytes().length);

            System.out.print(" Bytes Transmitted -> " + bytesTxed + "\n");
            int finalBytesTxed = bytesTxed;
            Platform.runLater(() -> {
                consoleUiTextArea.appendText(" Bytes Transmitted -> " + finalBytesTxed + "\n");
            });

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}