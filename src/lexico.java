import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
public class lexico {
    nodo cabeza = null, p;
    int estado = 0, columna, valorMT, numRenglon = 1;
    int caracter = 0;
    String lexema = "";
    boolean errorEncontrado = false;

    // Declarar ruta de archivo a leer
    //String archivo = "C:\\Users\\Jorge\\Documents\\Compilador21-10\\src\\codigo.txt";
    int matriz[][] = {

            // letra @ _ digito + - * / ^ < > = ! & | ( ) { } , ; " eb tab nl . eof oc
            // 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27
            /* 0 */ { 1, 1, 1, 2, 103, 104, 105, 5, 107, 8, 9, 10, 11, 12, 13, 117, 118, 119, 120, 124, 125, 14, 0, 0,
                    0, 505, 0, 505 },
            /* 1 */ { 1, 1, 1, 1, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100,
                    100, 100, 100, 100, 100, 100, 100 },
            /* 2 */ { 101, 101, 101, 2, 101, 101, 101, 101, 101, 101, 101, 101, 101, 101, 101, 101, 101, 101, 101, 101,
                    101, 101, 101, 101, 101, 3, 101, 101 },
            /* 3 */ { 500, 500, 500, 4, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500,
                    500, 500, 500, 500, 500, 500, 500, 500 },
            /* 4 */ { 102, 102, 102, 4, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102,
                    102, 102, 102, 102, 102, 102, 102, 102 },
            /* 5 */ { 106, 106, 106, 106, 106, 106, 6, 106, 106, 106, 106, 106, 106, 106, 106, 106, 106, 106, 106, 106,
                    106, 106, 106, 106, 106, 106, 106, 106 },
            /* 6 */ { 6, 6, 6, 6, 6, 6, 7, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 501, 6 },
            /* 7 */ { 6, 6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 501, 6 },
            /* 8 */ { 108, 108, 108, 108, 108, 108, 108, 108, 108, 108, 108, 110, 108, 108, 108, 108, 108, 108, 108,
                    108, 108, 108, 108, 108, 108, 108, 108, 108 },
            /* 9 */ { 109, 109, 109, 109, 109, 109, 109, 109, 109, 109, 109, 111, 109, 109, 109, 109, 109, 109, 109,
                    109, 109, 109, 109, 109, 109, 109, 109, 109 },
            /* 10 */{ 123, 123, 123, 123, 123, 123, 123, 123, 123, 123, 123, 112, 123, 123, 123, 123, 123, 123, 123,
                    123, 123, 123, 123, 123, 123, 123, 123, 123 },
            /* 11 */{ 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 113, 116, 116, 116, 116, 116, 116, 116,
                    116, 116, 116, 116, 116, 116, 116, 116, 116 },
            /* 12 */{ 502, 502, 502, 502, 502, 502, 502, 502, 502, 502, 502, 502, 502, 114, 502, 502, 502, 502, 502,
                    502, 502, 502, 502, 502, 502, 502, 502, 502 },
            /* 13 */{ 503, 503, 503, 503, 503, 503, 503, 503, 503, 503, 503, 503, 503, 503, 115, 503, 503, 503, 503,
                    503, 503, 503, 503, 503, 503, 503, 503, 503 },
            /* 14 */{ 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 122, 14, 14,
                    504, 14, 504, 14 }

    };
    String palReservadas[][] = {
            // 0 1
            /* 0 */{ "break", "200" },
            /* 1 */{ "if", "201" },
            /* 2 */{ "else", "202" },
            /* 3 */{ "main", "203" },
            /* 4 */{ "while", "204" },
            /* 5 */{ "go to", "205" },
            /* 6 */{ "print", "206" },
            /* 7 */{ "new", "207" },
            /* 8 */{ "float", "208" },
            /* 9 */{ "int", "209" },
            /* 10 */{ "false", "210" },
            /* 11 */{ "true", "211" },
            /* 12 */{ "string", "212" },
            /* 13 */{ "bool", "213" },
            /* 14 */{ "getvalue", "214" }
    };
    String errores[][] = {
            // 0 1 <---- Número de columna del arreglo
            /* 0 */{ "Se espera un digito", "500" },
            /* 1 */{ "Se espera cierre de comentario", "501" },
            /* 2 */{ "Se espera un &", "502" },
            /* 3 */{ "Se espera un |", "503" },
            /* 4 */{ "Se espera cierre de cadena", "504" },
            /* 5 */{ "Caracter no valido", "505" }
    };

    // Crear variable file para asignar el archivo
    //RandomAccessFile file = null;

    public lexico(String texto) {
        try {
                        // Inicializa el análisis léxico con el texto proporcionado
            ByteArrayInputStream stream = new ByteArrayInputStream(texto.getBytes(StandardCharsets.UTF_8));
            InputStreamReader reader = new InputStreamReader(stream);

            // Inicializa la variable de archivo con el nuevo lector
            BufferedReader bufferedReader = new BufferedReader(reader);
            // Asignar el archivo (solo lectura)
           int length = texto.length();
           int pos = 0;
           caracter = -1;

            // Lectura de caracter
            while (pos < length) {
                caracter = texto.charAt(pos);
                if (Character.isLetter(((char) caracter))) {
                    columna = 0;

                } else if (Character.isDigit(((char) caracter))) {
                    columna = 3;
                } else if (caracter == -1) {
                    columna = 26;
                } else {
                    switch ((char) caracter) {
                        case '@':
                            columna = 1;
                            break;
                        case '_':
                            columna = 2;
                            break;
                        case '+':
                            columna = 4;
                            break;
                        case '-':
                            columna = 5;
                            break;
                        case '*':
                            columna = 6;
                            break;
                        case '/':
                            columna = 7;
                            break;
                        case '^':
                            columna = 8;
                            break;
                        case '<':
                            columna = 9;
                            break;
                        case '>':
                            columna = 10;
                            break;
                        case '=':
                            columna = 11;
                            break;
                        case '!':
                            columna = 12;
                            break;
                        case '&':
                            columna = 13;
                            break;
                        case '|':
                            columna = 14;
                            break;
                        case '(':
                            columna = 15;
                            break;
                        case ')':
                            columna = 16;
                            break;
                        case '{':
                            columna = 17;
                            break;
                        case '}':
                            columna = 18;
                            break;
                        case ',':
                            columna = 19;
                            break;
                        case ';':
                            columna = 20;
                            break;
                        case '"':
                            columna = 21;
                            break;
                        case ' ':
                            columna = 22;
                            break;
                        case 9:
                            columna = 23;// tab
                            break;
                        case 10: {
                            columna = 24;// nueva linea
                            numRenglon = numRenglon + 1;
                        }
                            break;
                        case 13:
                            columna = 24;// retorno de carro
                            break;
                        case '.':
                            columna = 25;
                            break;
                        default:
                            columna = 27;
                            break;
                    }// switch
                } // if
                valorMT = matriz[estado][columna];
                if (valorMT < 100) { // cambiar de estado
                    estado = valorMT;
                    if (estado == 0) {
                        lexema = "";
                    } else {
                        lexema = lexema + (char) caracter;
                    }
                } else if (valorMT >= 100 && valorMT < 500) {
                    if (valorMT == 100) {
                        validarSiEsPalabraReservada();
                    }
                    if (valorMT == 100 || valorMT == 101 || valorMT == 102 ||
                            valorMT == 106 || valorMT == 123 || valorMT == 108 || valorMT == 109
                            || valorMT == 116 || valorMT == 200 || valorMT == 201 || valorMT == 202
                            || valorMT == 203 || valorMT == 204 || valorMT == 206 || valorMT == 210 || valorMT == 211
                            || valorMT == 214) {
                        pos--; // i--
                    } else {
                        lexema = lexema + (char) caracter;
                    }
                    insertarNodo();
                    estado = 0;
                    lexema = "";
                } else {// estado de error
                    imprimirMensajeError();
                    //break;
                }
                pos++;
            }
            // imprimirNodos();
            /*
             * if(!errorEncontrado){
             * sintaxis();
             * }
             */

        } catch (Exception e) {
            System.out.println(e.getMessage());
        } 
    }

    public void imprimirNodos() {
        p = cabeza;
        while (p != null) {
            System.out.println(p.lexema + " " + p.token + " " + p.renglon);
            p = p.sig;
        }
    }

    private void validarSiEsPalabraReservada() {
        for (String[] palReservada : palReservadas) {
            if (lexema.equals(palReservada[0])) {
                valorMT = Integer.valueOf(palReservada[1]);
            }
        }
    }

    private void imprimirMensajeError() {
        if (valorMT >= 500) {
            for (String[] errore : errores) {
                if (valorMT == Integer.valueOf(errore[1])) {
                    System.out.println("El error encontrado es: " + errore[0] + " error " + valorMT + " caracter "
                            + caracter + " en el renglon " + numRenglon);
                }

            }
            errorEncontrado = true;

        }
    }

    private void insertarNodo() {
        nodo nodo = new nodo(lexema, valorMT, numRenglon);
        if (cabeza == null) {
            cabeza = nodo;
            p = cabeza;
        } else {
            p.sig = nodo;
            p = nodo;
        }
    }


}
