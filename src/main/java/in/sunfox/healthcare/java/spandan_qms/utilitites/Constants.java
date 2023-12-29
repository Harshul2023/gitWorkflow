package in.sunfox.healthcare.java.spandan_qms.utilitites;

public final class Constants {

    public static final String environment = System.getenv("databaseEnvironment");
    public static String API_URL;

    public static final double Q_AMPLITUDE_LOWER_BOUND = -0.0663464351;
    public static final double Q_AMPLITUDE_UPPER_BOUND = -0.0542834469;
    public static final double R_AMPLITUDE_LOWER_BOUND = 0.4458549807;
    public static final double R_AMPLITUDE_UPPER_BOUND = 0.5449338653;
    public static final double S_AMPLITUDE_LOWER_BOUND = -0.0381112677;
    public static final double S_AMPLITUDE_UPPER_BOUND = -0.0311819463;
    public static final int HEART_RATE_LOWER_BOUND = 72;
    public static final int HEART_RATE_UPPER_BOUND = 82;
    public static final int PR_LOWER_BOUND = 140;
    public static final int PR_UPPER_BOUND = 165;
    public static final int QRS_LOWER_BOUND = 82;
    public static final int QRS_UPPER_BOUND = 112;
    public static final int QT_LOWER_BOUND = 350;
    public static final int QT_UPPER_BOUND = 415;
    public static final int QTC_LOWER_BOUND = 411;
    public static final int QTC_UPPER_BOUND = 471;
    public static final int CORRELATION_ARRAY_SIZE = 8;
    public static String USER_WORKING_DIRECTORY;
    static {
        if ("debug".equalsIgnoreCase(environment)) {
            API_URL = "https://api.sunfox.in/qms/dev/v1";
        } else if (environment == null || environment.equals("production")) {
            API_URL = "https://api.sunfox.in/qms/prod/v1";
        }
    }
    static {
        if ("debug".equalsIgnoreCase(environment)) {
            USER_WORKING_DIRECTORY = "D:\\Firmwares\\";
        } else if (environment == null || environment.equals("production")) {
            USER_WORKING_DIRECTORY = System.getProperty("user.dir");;
        }
    }

}
