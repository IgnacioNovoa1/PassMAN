package passman.ui;

public class CifradoCesar {
    private static final String ESPECIALES = "!¡?¿@#$%^&.*()-,_=+\0";

    public static String cifrar(String texto, int desplazamiento) {
        StringBuilder resultado = new StringBuilder();
        int longitudEspeciales = ESPECIALES.length() - 1;

        for (char caracter : texto.toCharArray()) {
            if (Character.isLetter(caracter)) {
                // Manejo de letras
                char base = Character.isUpperCase(caracter) ? 'A' : 'a';
                int codigo = (caracter - base + desplazamiento) % 26;
                if (codigo < 0) codigo += 26;
                resultado.append((char) (codigo + base));
            } else if (Character.isDigit(caracter)) {
                // Manejo de dígitos
                int digito = Character.getNumericValue(caracter);
                int codigoDigito = (digito + desplazamiento) % 10;
                if (codigoDigito < 0) codigoDigito += 10;
                resultado.append(codigoDigito);
            } else if (ESPECIALES.indexOf(caracter) != -1) {
                // Manejo de especiales
                int index = ESPECIALES.indexOf(caracter);
                int codigoIndex = (index + desplazamiento) % longitudEspeciales;
                if (codigoIndex < 0) codigoIndex += longitudEspeciales;
                resultado.append(ESPECIALES.charAt(codigoIndex));
            } else {
                // Otros caracteres
                resultado.append(caracter);
            }
        }
        return resultado.toString();
    }

    public static String descifrar(String texto, int desplazamiento) {
        return cifrar(texto, -desplazamiento);
    }
}
