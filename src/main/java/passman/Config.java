package passman;

public class Config {

    // DB
    public static final String DB_HOST = getEnv("PASSMAN_DB_HOST");
    public static final String DB_PORT = "5432";
    public static final String DB_NAME = "passman_db"; 
    public static final String DB_USER = "passman_user"; 
    public static final String DB_PASS = getEnv("PASSMAN_DB_PASS"); 
    
    // URL de conexión JDBC
    public static final String DB_URL = String.format(
            "jdbc:postgresql://%s:%s/%s", DB_HOST, DB_PORT, DB_NAME
    );
   //ENDPOINT DB
    public static final String KMS_ENDPOINT = "http://" + DB_HOST + ":4566";
    public static final String KMS_REGION = "us-east-1"; 
    
   //ARN DE KMS
    public static final String KMS_KEY_ARN = getEnv("PASSMAN_KMS_ARN"); 

    private static String getEnv(String variableName) {
        String value = System.getenv(variableName);
        if (value == null || value.isEmpty()) {
            System.err.println("¡ERROR CRÍTICO DE CONFIGURACIÓN!");
            System.err.println("La variable de entorno '" + variableName + "' no está definida.");
            System.err.println("Por favor, configura las variables antes de ejecutar la aplicación.");
            throw new RuntimeException("Falta variable de entorno: " + variableName);
        }
        return value;
    }
}