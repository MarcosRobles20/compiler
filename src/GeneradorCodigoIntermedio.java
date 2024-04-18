
import java.util.ArrayList;
import java.util.List;

public class GeneradorCodigoIntermedio {

    private List<nodo> codigoIntermedio;
    private int temporalCounter;

    public GeneradorCodigoIntermedio() {
        codigoIntermedio = new ArrayList<>();
        temporalCounter = 1;

    }

    public void generarDeclaracion(String variable, String tipoDato) {
        nodo nuevoNodo = new nodo("declarar", variable, tipoDato);
        codigoIntermedio.add(nuevoNodo);
    }

    public void generarAsignacion(String variable, String valor) {
        nodo nuevoNodo = new nodo("asignar", variable, valor);
        codigoIntermedio.add(nuevoNodo);
    }

    public void generarSuma(String operando1, String operando2) {
        String operacionIntermedia = String.format("SUMA %s  %s", operando1, operando2);
        nodo nuevaInstruccion = new nodo(operacionIntermedia);
        codigoIntermedio.add(nuevaInstruccion);
    }

    public void generarResta(String operando1, String operando2) {
        String operacionIntermedia = String.format("RESTA %s  %s", operando1, operando2);
        nodo nuevaInstruccion = new nodo(operacionIntermedia);
        codigoIntermedio.add(nuevaInstruccion);
    }

    public void generarDivision(String operando1, String operando2) {
        String operacionIntermedia = String.format("DIV %s  %s", operando1, operando2);
        nodo nuevaInstruccion = new nodo(operacionIntermedia);
        codigoIntermedio.add(nuevaInstruccion);

    }

    public void generarMultiplicacion(String operando1, String operando2) {
        String operacionIntermedia = String.format("MUL %s  %s", operando1, operando2);
        nodo nuevaInstruccion = new nodo(operacionIntermedia);
        codigoIntermedio.add(nuevaInstruccion);
    }

    public void generarPrint(String variable) {
        String operacionIntermedia = String.format("print %s", variable);
        nodo nuevaInstruccion = new nodo(operacionIntermedia);
        codigoIntermedio.add(nuevaInstruccion);
    }

    public void generarIF(String condicion) {
        String operacionIntermedia = String.format("if %s", condicion);
        nodo nuevaInstruccion = new nodo(operacionIntermedia);
        codigoIntermedio.add(nuevaInstruccion);
    }

    public String generarTemporal() {
        String temporal = "t" + temporalCounter;
        temporalCounter++;
        return temporal;
    }

    public List<nodo> getCodigoIntermedio() {
        return codigoIntermedio;
    }

    void generarBRF() {
        String operacionIntermedia = String.format("BRF-A" + temporalCounter);
        nodo nuevaInstruccion = new nodo(operacionIntermedia);
        codigoIntermedio.add(nuevaInstruccion);
        /*String operacion = String.format("B" + temporalCounter + ":");
        nodo nueva = new nodo(operacion);
        codigoIntermedio.add(nueva);*/
    }

    void generarBRI() {
        String operacionIntermedia = String.format("BRI-B" + temporalCounter);
        nodo nuevaInstruccion = new nodo(operacionIntermedia);
        codigoIntermedio.add(nuevaInstruccion);
        /* String operacion = String.format("A" + temporalCounter + ":");
        nodo nueva = new nodo(operacion);
        codigoIntermedio.add(nueva);*/
    }

    void generarA() {
        String operacion = String.format("A" + temporalCounter + ":");
        nodo nueva = new nodo(operacion);
        codigoIntermedio.add(nueva);
    }

    void generarB() {
        String operacion = String.format("B" + temporalCounter + ":");
        nodo nueva = new nodo(operacion);
        codigoIntermedio.add(nueva);
    }
}
