
public class Variables {

    int renglon;
    String id;
    String tipo;

    Variables(int renglon, String id, String tipo) {
        this.renglon = renglon;
        this.id = id;
        this.tipo = tipo;
    }

    public int getNumeroRenglon() {
        return renglon;
    }

    public String getId() {
        return id;
    }

    public String getTipoDato() {
        return tipo;
    }
}
