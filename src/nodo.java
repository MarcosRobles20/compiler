public class nodo {
    String lexema;
    int token;
    int renglon;
    nodo sig = null;
    String operacionIntermedia; // Nueva propiedad
    String operando1; // Nueva propiedad
    String operando2; // Nueva propiedad

    // Constructor para nodos con token e información léxica
    nodo(String lexema, int token, int renglon) {
        this.lexema = lexema;
        this.token = token;
        this.renglon = renglon;
    }

    // Constructor para nodos con información de operación intermedia
    nodo(String operacionIntermedia) {
        this.operacionIntermedia = operacionIntermedia;
    }

    // Constructor para nodos con información de operación intermedia y un operando
    nodo(String operacionIntermedia, String operando1) {
        this.operacionIntermedia = operacionIntermedia;
        this.operando1 = operando1;
    }

    // Constructor para nodos con información de operación intermedia y dos operandos
    nodo(String operacionIntermedia, String operando1, String operando2) {
        this.operacionIntermedia = operacionIntermedia;
        this.operando1 = operando1;
        this.operando2 = operando2;
    }

    // Métodos para obtener la representación de la operación intermedia como cadena de texto
    public String getOperacionIntermedia() {
        if (operando2 != null) {
            return   operacionIntermedia + " " + operando1 + " " + operando2;
        } else if (operando1 != null) {
            return lexema + " " + operacionIntermedia + " " + operando1;
        } else if (operacionIntermedia != null) {
            return operacionIntermedia;
        } else {
            return lexema;
        }
    }

    String[] split(String s) {
       return s.split("\\s+");
    }


}
