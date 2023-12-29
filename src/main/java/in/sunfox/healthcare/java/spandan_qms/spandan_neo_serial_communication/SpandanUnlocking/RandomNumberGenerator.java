package in.sunfox.healthcare.java.spandan_qms.spandan_neo_serial_communication.SpandanUnlocking;

import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

public class RandomNumberGenerator {
    private static final String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public static String generateRandomNumber(StringBuilder sb) {
        Random random = new Random();
        Set<Character> usedChars = new LinkedHashSet<>();

        while (sb.length() < 16) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);

            if (!usedChars.contains(randomChar)) {
                // Check if the random character has already been used
                if (sb.indexOf(String.valueOf(randomChar)) == -1) {
                    sb.append(randomChar);
                    usedChars.add(randomChar);
                }
            }
        }

        return sb.toString();
    }
}