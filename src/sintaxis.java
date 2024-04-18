// Importa la clase GeneradorCodigoIntermedio en tu clase sintaxis

import java.util.ArrayList;
import java.util.List;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;
import javax.swing.SpringLayout;

public class sintaxis extends lexico {

    boolean errorSintactico = false;
    boolean errorSemantico = false;
    ArrayList<Variables> listaVariables = new ArrayList<>();
    String tipoDato;
    String valorAsignado;
    String tipoVariable;
    String tipoVariableExp1;
    String tipoVariableExp2;
    Deque<String> pilaTemporal = new LinkedList<>();
    String temp1;
    private GeneradorCodigoIntermedio generadorCodigoIntermedio;
    int contadortemporal = 0;
    String codigoEnsamblador;
    String principio = "INCLUDE macros.mac\nDOSSEG\n.MODEL SMALL\n"
            + ".STACK 100h\n.DATA\n\tMAXLEN DB 254\n\tLEN DB 0\n\tMSG DB 254 DUP(?)\n\tMSG_DD DD MSG"
            + "\n\tBUFFER DB 8 DUP('$')\n\tCADENA_NUM DB 10 DUP('$')"
            + "\n\tBUFFERTEMP DB 8 DUP('$')\n\tBLANCO DB '#'\n\tBLANCOS"
            + " DB '$'\n\tMENOS DB '-$'\n\tCOUNT DW 0\n\tNEGATIVO "
            + "DB 0\n\tBUF DW 10\n\tLISTAPAR LABEL BYTE\n\tLONGMAX "
            + "DB 254\n\tTRUE DW 1\n\tFALSE DW 0\n\tINTRODUCIDOS "
            + "DB 254 DUP ('$')\n\tMULT10 DW 1\n\ts_true DB 'true$'\n\t"
            + "s_false DB 'false$'\n";
    String code = ".CODE\n.STARTUP\n";
    int contador = 0;

    public sintaxis(String texto) {
        super(texto);
        generadorCodigoIntermedio = new GeneradorCodigoIntermedio();

        try {
            if (cabeza == null) {
                errorSintactico = true;
                System.out.println("Codigo sin tokens");
            } else {
                p = cabeza;
                while (p != null) {
                    if (p.token == 203) {// main
                        p = p.sig;
                        if (p.token == 117) {// (
                            p = p.sig;
                            if (p.token == 118) {// )
                                p = p.sig;
                                if (p.token == 119) {// {
                                    p = p.sig;
                                    variables();
                                    if (!errorSintactico) {
                                        while ((p.token == 201 || p.token == 202 || p.token == 204
                                                || p.token == 206 || p.token == 214
                                                || p.token == 100) && !errorSintactico) {
                                            statement();
                                        }

                                        ImprimirLista();
                                        ImprimirCodigoIntermedio();
                                        CodigoEnsamblador();
                                        if (p.token != 120 && !errorSintactico) {
                                            System.out.println("Se espera '}'");
                                        }
                                        if (p.sig != null && !errorSintactico) {
                                            System.out.println("Statement fuera de main");
                                            errorSintactico = true;
                                        }
                                    }
                                } else {
                                    System.out.println("Se espera '{'");
                                    errorSintactico = true;
                                }
                            } else {
                                System.out.println("Se espera ')'");
                                errorSintactico = true;
                            }
                        } else {
                            System.out.println("Se espera '('");
                            errorSintactico = true;
                        }
                    } else {
                        System.out.println("Se espera 'main'");
                        errorSintactico = true;
                    }
                    break;
                }
            }
        } catch (NullPointerException e) {
            errorSintactico = true;
            System.out.println("Catch exception Se espera '}'");
        }
    }

    private void variables() {
        if (p.token == 207) {// new
            p = p.sig;
            tipos();
            if (!errorSintactico && !errorSemantico) {
                if (p.token == 100) {
                    ValidarRepeticion();
                    GuardarVariable();
                    generadorCodigoIntermedio.generarDeclaracion(p.lexema, tipoDato);

                    p = p.sig;
                    while (p.token == 124 && !errorSintactico && !errorSemantico) {// ,
                        p = p.sig;
                        if (p.token != 100) {
                            System.out.println("Se espera un identificador");
                            errorSintactico = true;
                        } else {
                            ValidarRepeticion();
                            GuardarVariable();
                            generadorCodigoIntermedio.generarDeclaracion(p.lexema, tipoDato);
                            p = p.sig;
                        }
                    }
                    if (!errorSintactico) {
                        if (p.token == 125) {
                            p = p.sig;

                            if (p.token == 207 || p.token == 209 || p.token == 208 || p.token == 213 || p.token == 212) {//new
                                variables();
                            }
                        } else {
                            System.out.println("Se espera ;");
                            errorSintactico = true;
                        }
                    }

                } else {
                    System.out.println("Se espera un identificador");
                    errorSintactico = true;
                }
            }

        } else {
            System.out.println("Se espera 'new'");
            errorSintactico = true;
        }
    }

    private void tipos() {
        if (p.token != 209 && p.token != 208 && p.token != 213 && p.token != 212) {
            System.out.println("Se espera un tipo de variable");
            errorSintactico = true;
        } else {
            tipoDato = p.lexema;
            p = p.sig;
        }
    }

    private void statement() {
        switch (p.token) {
            case 214:// getvalue()
                p = p.sig;
                if (p.token == 117) {
                    p = p.sig;
                    if (p.token == 118) {
                        p = p.sig;
                        if (p.token == 125) {
                            p = p.sig;
                        } else {
                            System.out.println("Se espera ';'");
                            errorSintactico = true;
                        }
                    } else {
                        System.out.println("Se espera ')'");
                        errorSintactico = true;
                    }
                } else {
                    System.out.println("Se espera '('");
                    errorSintactico = true;
                }
                break;

            case 100: // Asignación
                tipoVariableExp1 = p.lexema;
                tipoVariable = obtenerTipoVariable(p.lexema); // Obtener el ID
                p = p.sig;
                if (p.token == 123) {
                    p = p.sig;
                    valorAsignado = p.lexema;
                    exp_simple();
                    if (!errorSintactico) {
                        if (tipoVariable != null) {
                            if (validarTipoDato(tipoVariable, valorAsignado)) {
                                if (validarDesbordamiento(tipoVariable, valorAsignado)) {
                                    generadorCodigoIntermedio.generarAsignacion(tipoVariableExp1, valorAsignado);

                                } else {
                                    System.out.println("Error en el renglon " + p.renglon + ": Desbordamiento de tipo " + tipoVariable + " valor asignado: " + valorAsignado);
                                    errorSemantico = true;

                                }
                            } else {
                                System.out.println("Error en el renglon " + p.renglon + ": El tipo de dato no coincide para la variable '" + tipoVariable + "'.");
                                errorSemantico = true;
                            }
                        } else {
                            System.out.println("Error en el renglón " + p.renglon + ": La variable no ha sido definida.");
                            errorSemantico = true;
                        }
                        if (p.token == 125) {
                            p = p.sig;

                        } else {
                            System.out.println("Se espera ';'");
                            errorSintactico = true;
                        }
                    }
                } else {
                    System.out.println("Se espera '='");
                    errorSintactico = true;
                }
                break;

            case 201:// if
                String condicion = "";
                p = p.sig;
                if (p.token == 117) {
                    p = p.sig;
                    nodo aux = p;
                    while (p.token != 118) {
                        condicion += p.lexema + " ";
                        p = p.sig;
                    }
                    p = aux;
                    exp_cond();
                    if (!errorSintactico) {
                        if (p.token == 118) {
                            p = p.sig;
                            generadorCodigoIntermedio.generarIF(condicion);
                            if (p.token == 119) {
                                p = p.sig;
                                generadorCodigoIntermedio.generarBRF();
                                while ((p.token == 201 || p.token == 202 || p.token == 204
                                        || p.token == 206 || p.token == 214
                                        || p.token == 100) && !errorSintactico) {
                                    statement();
                                }
                                generadorCodigoIntermedio.generarA();
                                if (!errorSintactico) {
                                    if (p.token == 120) {

                                        p = p.sig;
                                        if (p.token == 202) {//ELSE
                                            p = p.sig;
                                            if (p.token == 119) {
                                                generadorCodigoIntermedio.generarBRI();

                                                p = p.sig;
                                                while ((p.token == 201 || p.token == 202 || p.token == 204
                                                        || p.token == 206 || p.token == 214
                                                        || p.token == 100) && !errorSintactico) {
                                                    statement();
                                                }
                                                if (!errorSintactico) {
                                                    if (p.token == 120) {
                                                        generadorCodigoIntermedio.generarB();
                                                        p = p.sig;
                                                    } else {
                                                        System.out.println("Se espera '}'");
                                                        errorSintactico = true;

                                                    }
                                                }

                                            } else {
                                                System.out.println("Se espera '{'");
                                                errorSintactico = true;
                                            }
                                        }
                                    } else {
                                        System.out.println("Se espera '}'");
                                        errorSintactico = true;
                                    }
                                }
                            } else {
                                System.out.println("Se espera '{'");
                                errorSintactico = true;
                            }
                        } else {
                            System.out.println("Se espera ')'");
                            errorSintactico = true;
                        }
                    }
                } else {
                    System.out.println("Se espera '('");
                    errorSintactico = true;
                }
                break;
            case 204:// while
                p = p.sig;
                if (p.token == 117) {
                    p = p.sig;
                    exp_cond();
                    if (!errorSintactico) {
                        if (p.token == 118) {
                            p = p.sig;
                            if (p.token == 119) {
                                p = p.sig;
                                while ((p.token == 201 || p.token == 202 || p.token == 204
                                        || p.token == 206 || p.token == 214
                                        || p.token == 100) && !errorSintactico) {
                                    statement();
                                }
                                if (!errorSintactico) {
                                    if (p.token == 120) {
                                        p = p.sig;

                                    } else {
                                        System.out.println("Se espera '}'");
                                        errorSintactico = true;
                                    }
                                }

                            } else {
                                System.out.println("Se espera '{'");
                                errorSintactico = true;
                            }
                        } else {
                            System.out.println("Se espera ')'");
                            errorSintactico = true;
                        }
                    }

                } else {
                    System.out.println("Se espera '('");
                    errorSintactico = true;
                }
                break;
            case 202:
                System.out.println("Else sin un if");
                errorSintactico = true;
                break;
            case 206:// print
                p = p.sig;
                if (p.token == 117) {
                    p = p.sig;
                    if (p.token == 100 || p.token == 122) {
                        tipoVariable = p.lexema;
                        if (existeVariable(tipoVariable)) {
                        } else {
                            System.out.println("La variable: " + tipoVariable + "  no ha sido declarada");
                            errorSemantico = true;
                        }
                        p = p.sig;
                        while (p.token == 124) {
                            p = p.sig;
                            if (p.token != 100 && p.token != 122) {
                                System.out.println("Se espera un identificador o cadena de texto");
                                errorSintactico = true;
                            } else {
                                p = p.sig;
                            }
                        }
                        if (!errorSintactico) {
                            if (p.token == 118) {
                                generadorCodigoIntermedio.generarPrint(tipoVariable);
                                p = p.sig;
                                if (p.token == 125) {
                                    p = p.sig;
                                } else {
                                    System.out.println("Se espera ';'");
                                    errorSintactico = true;
                                }
                            } else {
                                System.out.println("Se espera ')'");
                                errorSintactico = true;
                            }
                        }
                    } else {
                        System.out.println("Se espera un identificador");
                        errorSintactico = true;
                    }
                } else {
                    System.out.println("Se espera '('");
                    errorSintactico = true;
                }
                break;
            default:
                System.out.println("Se espera un statement válido");
                errorSintactico = true;
                break;

        }
    }

    private void exp_simple() {
        if (p.token == 103 || p.token == 104) {
            signo();
        }
        termino();

        if (!errorSintactico) {
            while (p.token == 103 || p.token == 104) {
                String operador = p.lexema;
                p = p.sig;
                String operador2 = p.lexema;
                exp_simple();

                // Generar código intermedio para la operación
                contadortemporal++;
                if (operador.equals("+")) {
                    generadorCodigoIntermedio.generarSuma(valorAsignado, operador2);
                } else if (operador.equals("-")) {
                    generadorCodigoIntermedio.generarResta(valorAsignado, operador2);
                }

            }

        }
    }

    private void termino() {
        factor();
        if (!errorSintactico) {
            while (p.token == 105 || p.token == 106) { // 105 es '*' y 106 es '/'

                String operador = p.lexema;
                p = p.sig;
                String operador2 = p.lexema;
                factor();

                // Generar código intermedio para la operación
                if (operador.equals("*")) {
                    generadorCodigoIntermedio.generarMultiplicacion(valorAsignado, operador2);
                } else if (operador.equals("/")) {
                    generadorCodigoIntermedio.generarDivision(valorAsignado, operador2);
                }
            }
        }
    }

    private void factor() {
        switch (p.token) {
            case 100:
                tipoVariableExp2 = p.lexema;
                if (existeVariable(tipoVariableExp1)) {
                    if (existeVariable(tipoVariableExp2)) {
                    } else {
                        System.out.println("Error: Renglon: " + p.renglon + " la variable: " + tipoVariableExp2 + "  no ha sido definida");
                        errorSemantico = true;
                    }
                } else {
                    System.out.println("Error: Renglon" + p.renglon + " la variable: " + tipoVariableExp1 + "  no ha sido definida");
                    errorSemantico = true;
                }
                if (!compararTiposExpVariables(tipoVariableExp1, tipoVariableExp2)) {
                    System.out.println("Error: Renglon" + p.renglon + " Tipos de datos nooo coinciden para las variables: " + tipoVariableExp1 + "  y  " + tipoVariableExp2);
                    errorSemantico = true;
                }
                tipoVariable = "id";
                p = p.sig;
                break;

            case 101:
                p = p.sig;
                break;

            case 102:
                p = p.sig;
                break;

            case 117:
                p = p.sig;
                exp_simple();
                if (!errorSintactico) {
                    if (p.token == 118) {
                        p = p.sig;
                    } else {
                        System.out.println("Se espera ')'");
                        errorSintactico = true;
                    }
                }

                break;

            case 116:
                p = p.sig;
                factor();
                break;
            case 122:
                p = p.sig;
                break;
            default:
                System.out.println("Se espera un factor valido");
                errorSintactico = true;
        }
    }

    private void op_aditivo() {
        switch (p.token) {
            case 103:
                p = p.sig;
                break;
            case 104:
                p = p.sig;
                break;
            case 115:
                p = p.sig;
                break;
            default:
                System.out.println("Se espera un operador aditivo");
                errorSintactico = true;
                break;
        }
    }

    private void op_multi() {
        switch (p.token) {
            case 105:
                p = p.sig;
                break;
            case 106:
                p = p.sig;
                break;
            case 114:
                p = p.sig;
                break;
            default:
                System.out.println("Se espera un operador multiplicativo");
                errorSintactico = true;
                break;
        }
    }

    private void signo() {
        switch (p.token) {
            case 103:
                p = p.sig;
                break;
            case 104:
                valorAsignado = p.lexema;
                p = p.sig;
                valorAsignado += p.lexema;
                break;

            default:
                System.out.println("Se espera signo '+' o '-'");
                errorSintactico = true;
        }
    }

    private void exp_cond() {

        tipoVariableExp1 = p.lexema;//Obtenemos el tipo de la primera variable para comparar
        exp_simple();
        op_relacio();
        tipoVariableExp2 = p.lexema;//segundo tipo de variable
        if (!errorSintactico) {
            exp_simple();
            if (existeVariable(tipoVariableExp1)) {
                if (existeVariable(tipoVariableExp2)) {
                } else {
                    System.out.println("Error: Renglon: " + p.renglon + " la variable: " + tipoVariableExp2 + "  no ha sido definida");
                    errorSemantico = true;
                }
            } else {
                System.out.println("Error: Renglon" + p.renglon + " la variable: " + tipoVariableExp1 + "  no ha sido definida");
                errorSemantico = true;
            }
            if (!compararTiposExpVariables(tipoVariableExp1, tipoVariableExp2)) {
                System.out.println("Error: Renglon" + p.renglon + " Tipos de datos no coinciden para las variables: " + tipoVariableExp1 + "  y  " + tipoVariableExp2);
                errorSemantico = true;
            }
            if (p.token == 114 || p.token == 115) {
                p = p.sig;
                exp_simple();
                op_relacio();
                if (!errorSintactico) {
                    exp_simple();
                }
            }
        }
    }

    private void op_relacio() {
        switch (p.token) {
            case 109:
                p = p.sig;
                break;
            case 108:
                p = p.sig;
                break;
            case 111:
                p = p.sig;
                break;
            case 110:
                p = p.sig;
                break;
            case 113:
                p = p.sig;
                break;
            case 112:
                p = p.sig;
                break;
            default:
                System.out.println("Se espera un operador relacional");
                errorSintactico = true;
                break;
        }
    }

    private void GuardarVariable() {
        Variables nuevaVariable = new Variables(p.renglon, p.lexema, tipoDato);
        listaVariables.add(nuevaVariable);
    }

    private void ImprimirLista() {
        System.out.println("+-----------------+-----------+--------------+");
        System.out.println("| Renglon |    ID     |  Tipo de Dato |");
        System.out.println("+-----------------+-----------+--------------+");
        for (Variables variable : listaVariables) {
            String numeroRenglon = String.format("| %-15d", variable.getNumeroRenglon());
            String id = String.format("| %-20s", variable.getId());
            String tipo = String.format("| %-20s", variable.getTipoDato());
            System.out.println(numeroRenglon + id + tipo + "|");
        }
        System.out.println("+-----------------+-----------+--------------+");
    }

    private void ValidarRepeticion() {
        boolean encontrado = false;

        for (Variables variable : listaVariables) {
            String id = variable.getId();

            // Comparar el ID con el lexema
            if (id.equals(p.lexema)) {
                encontrado = true;
                break;
            }
        }

        if (encontrado) {
            System.out.println("El ID '[" + p.lexema + "]' ya existe en la lista.");
            errorSemantico = true;
        }
    }

    public boolean existeVariable(String nombreVariable) {
        String[] palabrasReservadas = {"int", "string", "float", "boolean"}; // Lista de palabras reservadas

        for (Variables variable : listaVariables) {
            if (variable.getId().equals(nombreVariable)) {
                return true; // Devuelve verdadero si la variable ya ha sido definida
            }
        }

        // Verificar si el nombre de la variable es una palabra reservada
        for (String palabraReservada : palabrasReservadas) {
            if (palabraReservada.equals(nombreVariable)) {
                return true; // Devuelve verdadero si es una palabra reservada
            }
        }

        return false; // Devuelve falso si la variable no ha sido definida y no es una palabra reservada
    }

    public String obtenerTipoVariable(String nombreVariable) {
        for (Variables variable : listaVariables) {
            if (variable.getId().equals(nombreVariable)) {
                return variable.getTipoDato(); // Devuelve el tipo de la variable si ya ha sido definida
            }
        }
        return null; // Devuelve null si la variable no ha sido definida

    }

    public static boolean validarTipoDato(String tipoVariable, String valorAsignado) {
        switch (tipoVariable) {
            case "int ":
            try {
                Integer.parseInt(valorAsignado);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }

            case "float ":
            try {
                Float.parseFloat(valorAsignado);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }

            case "string ":
                // Verificar si el valor está entre comillas dobles
                return valorAsignado.startsWith("\"") && valorAsignado.endsWith("\"");

            case "boolean ":
                // Verificar si el valor es "true" o "false" (sin distinción entre mayúsculas y minúsculas)
                return valorAsignado.equalsIgnoreCase("true") || valorAsignado.equalsIgnoreCase("false");

            case "id":
                return true;
            default:
                return false;
        }
    }

    public static boolean validarDesbordamiento(String tipoVariable, String valorAsignado) {
        switch (tipoVariable) {
            case "int ":
            try {
                int numero = Integer.parseInt(valorAsignado);
                if (numero <= 10000000 && numero >= -10000000) {
                    return true;
                } else {
                    System.out.println("VALOR MAX PERMITIDO [INT] :+/- : " + " 10,000,000");
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
            case "float ":
            try {
                float numero = Float.parseFloat(valorAsignado);
                if (numero <= 1000.1000 && numero >= -1000.1000) {
                    return true;
                } else {
                    System.out.println("VALOR MAX PERMITIDO [FLOAT]:+/- : " + " 1000.1000");
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
            case "string ":
                // Verificar si el valor está entre comillas dobles
                return valorAsignado.startsWith("\"") && valorAsignado.endsWith("\"");
            case "boolean ":
                // Verificar si el valor es "true" o "false" (sin distinción entre mayúsculas y minúsculas)
                return valorAsignado.equalsIgnoreCase("true") || valorAsignado.equalsIgnoreCase("false");

            default:
                return true;
        }
    }

    private boolean compararTiposExpVariables(String nombreVariable1, String nombreVariable2) {
        String tipoVariable1 = obtenerTipoVariable(nombreVariable1);
        String tipoVariable2 = obtenerTipoVariable(nombreVariable2);

        return tipoVariable1.equals(tipoVariable2);
    }

    private void ImprimirCodigoIntermedio() {
        List<nodo> codigoIntermedio = generadorCodigoIntermedio.getCodigoIntermedio();
        System.out.println("----------CODIGO INTERMEDIO-----------");
        for (nodo instruccion : codigoIntermedio) {
            System.out.println(instruccion.getOperacionIntermedia());
            // Imprimir otros detalles según sea necesario
        }
    }

    private void CodigoEnsamblador() {
        List<nodo> codigoIntermedio = generadorCodigoIntermedio.getCodigoIntermedio();
        for (nodo instruccion : codigoIntermedio) {
            String contenidoNodo = instruccion.getOperacionIntermedia();
            //System.out.println("Contenido del nodo: " + contenidoNodo);
            String[] partes = contenidoNodo.split("\\s+"); // Divide la instrucción por espacios
            if (partes.length < 0) {
                // Manejar errores o instrucciones no válidas según sea necesario
                System.out.println("Error: Instrucción no válida");
                continue;
            }

            String operacion = partes[0].toLowerCase();
            if ("declarar".equals(operacion)) {
                traducirDeclaracion(partes);
            }
            if ("asignar".equals(operacion)) {
                traducirString(partes);
            }
        }
        for (nodo instruccion : codigoIntermedio) {
            String contenidoNodo = instruccion.getOperacionIntermedia();
            //System.out.println("Contenido del nodo: " + contenidoNodo);
            String[] partes = contenidoNodo.split("\\s+"); // Divide la instrucción por espacios
            if (partes.length < 0) {
                // Manejar errores o instrucciones no válidas según sea necesario
                System.out.println("Errorrrr: Instrucción no válida");
                continue;
            }

            String operacion = partes[0].toLowerCase();
            switch (operacion) {
                case "asignar":
                    traducirAsignacion(partes);
                    break;
                case "suma":
                    traducirSuma(partes);
                    break;
                case "resta":
                    traducirResta(partes);
                    break;
                case "mul":
                    traducirMultiplicacion(partes);
                    break;
                case "div":
                    traducirDivision(partes);
                    break;
                case "print":
                    traducirPrint(partes);
                    break;
                case "if":
                    traducirIf(partes);
                    break;
                case "declarar":
                    break;
                default:
                    if (partes[0].contains(":")) {
                        code += partes[0] + "\n";
                    } else if (partes[0].contains("BRF")) {
                        temp1 = pilaTemporal.pop();
                        code += "\tJF " + temp1 + "," + partes[0].split("-")[1] + "\n";
                    } else if (partes[0].contains("BRI")) {
                        code += "\tJMP " + partes[0].split("-")[1] + "\n";
                    } else {
                        pilaTemporal.push(partes[0]);
                    }
                    break;
            }
        }
        code += "\nmov ax, 4C00h\nint 21h\n.EXIT\nEND";
        codigoEnsamblador = principio + code;
        try {
            FileWriter archivo = new FileWriter("C:\\masm614\\asm\\Compi.asm");
            archivo.write(codigoEnsamblador);
            archivo.close();
        } catch (IOException ex) {
            System.out.println("Ocurrio un error al escribir archivo. " + ex.getMessage());
        }
    }

    private void traducirAsignacion(String[] partes) {
        String variable = partes[1];
        String valor = partes[2];
        if (!contieneComillas(valor)) {
            if (partes.length == 3) {
                code += "\tI_ASIGNAR " + variable + "," + valor + "\n";
            } else {
                System.out.println("Error: Formato incorrecto para asignación");
            }
        }
    }

    private void traducirDeclaracion(String[] partes) {
        if (partes.length == 3) {
            String variable = partes[1];
            String tipo = partes[2];

            if ("int".equals(tipo)) {
                principio += "\t" + variable + "  DW 0\n";
            }

            if ("float".equals(tipo)) {
                principio += "\t" + variable + "  DD 0\n";
            }
            // Puedes agregar más lógica según sea necesario para la declaración
        } else {
            System.out.println("Error: Formato incorrecto para declaración");
        }

    }

    private void traducirPrint(String[] partes) {

        if (partes.length >= 2) {
            for (int i = 1; i < partes.length; i++) {
                String elemento = partes[i];

                if (contieneComillas(elemento)) {
                    // Imprimir cadena
                    String cadena = elemento.replaceAll("\"", "");
                    System.out.println(cadena);
                    System.out.println("mov ah, 09h"); // Función para imprimir cadena
                    System.out.println("lea dx, " + cadena); // Puntero a la cadena
                    System.out.println("int 21h"); // Llamada a la interrupción del sistema operativo
                    code += "\tmov dx, offset " + cadena + "\nmov ah, 09h\nint 21h";
                } else {
                    // Imprimir valor
                    // Verificar si el elemento está en la lista de variables declaradas y obtener su tipo
                    String tipo = obtenerTipoVariable(elemento);

                    if ("int ".equals(tipo)) {
                        imprimirNumero(elemento);
                    }
                    if ("string ".equals(tipo)) {
                        code += "\tmov dx, offset " + elemento + "\n\tmov ah, 09h\n\tint 21h";
                        code += "\n\tWRITELN\n";
                    }
                    if ("float ".equals(tipo)) {
                        imprimirNumero(elemento);
                    }
                }
            }
        } else {
            System.out.println("Error: Formato incorrecto para Print");
        }
    }

    private void traducirString(String[] partes) {
        String variable = partes[1];
        String cadenacompleta = "";
        for (int i = 2; i < partes.length; i++) {
            cadenacompleta += partes[i] + " ";
        }
        if (contieneComillas(cadenacompleta)) {
            cadenacompleta = cadenacompleta.replace("\"", "");
            cadenacompleta += "$";
            principio += "\t" + variable + " DB \"" + cadenacompleta + "\", 0 ;\n";
        } else {
        }
    }

    private boolean contieneComillas(String valor) {
        return valor.startsWith("\"") || valor.endsWith("\"");
    }

    private void imprimirNumero(String elemento) {
        code += "\tITOA BUFFER," + elemento + "\n\tWRITE BUFFERTEMP\n";
    }

    private void traducirIf(String[] partes) {
        String temporal = "";
        String variable1 = partes[1];
        String condi = partes[2];
        String variable2 = partes[3];
        switch (condi) {
            case ">":
                temporal = generadorCodigoIntermedio.generarTemporal();
                principio += "\t" + temporal + " DW 0\n";
                pilaTemporal.push(temporal);
                code += "\tI_MAYOR " + variable1 + "," + variable2 + "," + temporal + "\n";
                break;
            case "<":
                temporal = generadorCodigoIntermedio.generarTemporal();
                pilaTemporal.push(temporal);
                principio += "\t" + temporal + " DW 0\n";
                code += "\tI_MENOR " + variable1 + "," + variable2 + "," + temporal + "\n";
                break;
            case ">=":
                temporal = generadorCodigoIntermedio.generarTemporal();
                pilaTemporal.push(temporal);
                principio += "\t" + temporal + " DW 0\n";
                code += "\tI_MAYORIGUAL " + variable1 + "," + variable2 + "," + temporal + "\n";
                break;
            case "<=":
                temporal = generadorCodigoIntermedio.generarTemporal();
                pilaTemporal.push(temporal);
                principio += "\t" + temporal + " DW 0\n";
                code += "\tI_MENORIGUAL " + variable1 + "," + variable2 + "," + temporal + "\n";
                break;
            case "=":
                temporal = generadorCodigoIntermedio.generarTemporal();
                pilaTemporal.push(temporal);
                principio += "\t" + temporal + " DW 0\n";
                code += "\tI_MENORIGUAL " + variable1 + "," + variable2 + "," + temporal + "\n";
                break;
            default:
                throw new AssertionError();
        }
    }

    private void traducirSuma(String[] partes) {
        String valor1 = partes[1];
        String valor2 = partes[2];
        String temporal = generadorCodigoIntermedio.generarTemporal();
        principio += "\t" + temporal + " DW 0\n";
        code += "\tSUMAR " + valor2 + "," + valor1 + "," + temporal + "\n";
    }

    private void traducirResta(String[] partes) {
        String valor1 = partes[1];
        String valor2 = partes[2];
        String temporal = generadorCodigoIntermedio.generarTemporal();
        principio += "\t" + temporal + " DW 0\n";
        code += "\tRESTA " + valor2 + "," + valor1 + "," + temporal + "\n";
    }

    private void traducirMultiplicacion(String[] partes) {
        String valor1 = partes[1];
        String valor2 = partes[2];
        String temporal = generadorCodigoIntermedio.generarTemporal();
        principio += "\t" + temporal + " DW 0\n";
        code += "\tMULTI " + valor2 + "," + valor1 + "," + temporal + "\n";
    }

    private void traducirDivision(String[] partes) {
        String valor1 = partes[1];
        String valor2 = partes[2];
        String temporal = generadorCodigoIntermedio.generarTemporal();
        principio += "\t" + temporal + " DW 0\n";
        code += "\tDIVIDE " + valor2 + "," + valor1 + "," + temporal + "\n";
    }

}
