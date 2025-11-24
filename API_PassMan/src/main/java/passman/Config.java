package passman;

public class Config {

    // --- CONFIGURACIÓN BASE DE DATOS ---
    public static final String DB_HOST = getEnv("PASSMAN_DB_HOST");
    public static final String DB_PORT = "5432";
    public static final String DB_NAME = "passman_db"; 
    public static final String DB_USER = "passman_user"; 
    public static final String DB_PASS = getEnv("PASSMAN_DB_PASS"); 
    
    public static final String DB_URL = String.format(
            "jdbc:postgresql://%s:%s/%s", DB_HOST, DB_PORT, DB_NAME
    );

   // --- CONFIGURACIÓN KMS ---
    public static final String KMS_ENDPOINT = getEnv("PASSMAN_KMS_ENDPOINT");
    public static final String KMS_REGION = "us-east-1"; 
    public static final String KMS_KEY_ARN = getEnv("PASSMAN_KMS_ARN"); 

    private static String getEnv(String variableName) {
        String value = System.getenv(variableName);
        if (value == null || value.isEmpty()) {
            throw new RuntimeException("Falta variable de entorno crítica: " + variableName);
        }
        return value;
    }
}