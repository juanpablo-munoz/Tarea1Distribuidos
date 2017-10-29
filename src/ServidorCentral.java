import aod.comunicaciones.*;
import aod.entidades.Distrito;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

class ServidorCentral {
    private static Map<String, Distrito> listaDistritos = new HashMap<String, Distrito>();

    // pedirPuerto() es una funcion que solo retornará si se ingresa un numero entero en la consola
    static private int pedirPuerto() {
        try {
            Scanner r = new Scanner(System.in);
            return r.nextInt();
        } catch (Exception e) {
            System.out.print("[Servidor Central] Debe ingresar numeros: ");
            pedirPuerto();
        }
        return -1;
    }



    public static void main(String[] args) {
        int puertoRecepcion;

        // Pedir datos de Servidor Central
        System.out.print("[Servidor Central] Ingresar Puerto de recepcion de Clientes: ");
        puertoRecepcion = pedirPuerto();

        comunicadorUnicast comunicadorConCliente =
                new comunicadorUnicast(puertoRecepcion);

        System.out.println("[Servidor Central] Iniciando en " + comunicadorConCliente.getIP() + ":" + puertoRecepcion);
        System.out.println("[Servidor Central] Indique la IP y puerto anteriores cuando el Cliente vaya a conectarse.\n");

        Thread registroDistritos = new Thread() {
            public void run() {
                boolean t = true;
                while (t) {
                    t = false;
                    System.out.println("[Servidor Central] Iniciando servicio de registro de Distritos");
                    Scanner r = new Scanner(System.in);
                    boolean lock = true;
                    while (lock) {
                        System.out.print("(1) Agregar un nuevo Distrito: ");
                        if (!r.next().equals("1")) {
                            System.out.println("[Servidor Central] Entrada invalida");
                        }
                        else {
                            lock = false;
                        }
                    }
                    System.out.println("\n---AGREGAR DISTRITO---\n");
                    // Pedir datos de nuevo Distrito
                    Scanner reader = new Scanner(System.in);
                    System.out.print("[Servidor Central: Agregar Distrito] Nombre Distrito: ");
                    String nombreDistrito = reader.next();
                    System.out.print("[Servidor Central: Agregar Distrito] IP Multicast: ");
                    String ipMulticast = reader.next();
                    System.out.print("[Servidor Central: Agregar Distrito] Puerto Multicast: ");
                    int puertoMulticast = pedirPuerto();
                    System.out.print("[Servidor Central: Agregar Distrito] IP Peticiones: ");
                    String ipPeticiones = reader.next();
                    System.out.print("[Servidor Central: Agregar Distrito] Puerto Peticiones: ");
                    int puertoPeticiones = pedirPuerto();
                    listaDistritos.put(nombreDistrito, new Distrito(nombreDistrito, ipMulticast, puertoMulticast, ipPeticiones, puertoPeticiones));
                }
            }
        };

        Thread recibirClientes = new Thread() {
            public void run() {
                while (true) {
                    System.out.println("[Servidor Central] Esperando Clientes...");
                    String mensaje = comunicadorConCliente.recibirMensaje();
                    String ipCliente = comunicadorConCliente.getIPUltimoCliente();
                    int puertoCliente = comunicadorConCliente.getPuertoUltimoCliente();
                    System.out.println("[Servidor Central] Cliente " + ipCliente + ":" + puertoCliente + " -> \"" + mensaje + "\"");
                    if (listaDistritos.get(mensaje) != null) {
                        Distrito distr = listaDistritos.get(mensaje);
                        boolean lock = true;
                        String respuesta = "denegado";
                        while (lock) {
                            System.out.println("[Servidor Central] Dar autorizacion a " + ipCliente + ":" + puertoCliente + " por Distrito " + mensaje + "?");
                            System.out.println("(1) Si");
                            System.out.println("(2) No");
                            Scanner r = new Scanner(System.in);
                            System.out.print("Apruebe o rechace la autorizacion: ");
                            switch (r.next()) {
                                case "1":
                                    lock = false;
                                    System.out.print("[Servidor Central] Respuesta a " + ipCliente + ":" + puertoCliente + " por " + mensaje);
                                    respuesta = "Nombre: " + distr.nombre + ", IP Multicast: " + distr.ipMulticast + ", Puerto Multicast: " + distr.puertoMulticast + ", IP Peticiones: " + distr.ipPeticiones + ", Puerto Peticiones: " + distr.ipPeticiones;
                                    System.out.println(respuesta);
                                    break;
                                case "2":
                                    lock = false;
                                    System.out.print("[Servidor Central] Respuesta a " + ipCliente + ":" + puertoCliente + " por " + mensaje);
                                    respuesta = "denegado";
                                    System.out.println(respuesta);
                                    break;
                                default:
                                    System.out.print("Entrada invalida");
                            }
                        }
                        comunicadorConCliente.enviarMensaje(respuesta);
                    }
                }
            }
        };

        registroDistritos.start();
        recibirClientes.start();
    }
}
