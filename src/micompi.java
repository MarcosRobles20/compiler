
public class micompi {

    private Interfaz interfaz;

    public micompi(Interfaz interfaz) {
        this.interfaz = interfaz;
    }
    nodo p;

    public void ejecutarAnalisis(String texto) {
        lexico lexico = new lexico(texto);
        lexico.imprimirNodos();
        if (!lexico.errorEncontrado) {
            interfaz.actualizarEstadoLexico(false);
            sintaxis sintaxis = new sintaxis(texto);
            if (!sintaxis.errorSintactico) {
                interfaz.actualizarEstadoSintactico(false);
            } else {
                interfaz.actualizarEstadoSintactico(true);
            }
            if (!sintaxis.errorSemantico){
                interfaz.actualizarEstadoSemantico(false);
            } else {
                interfaz.actualizarEstadoSemantico(true);
            }
        } else {
            interfaz.actualizarEstadoLexico(true);
        }
    }
}
