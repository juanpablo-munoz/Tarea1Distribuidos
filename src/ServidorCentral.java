import aod.comunicaciones.comunicadorUnicast;
import aod.entidades.Distrito;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

class ServidorCentral {
    private static Map<String, Distrito> listaDistritos = new HashMap<>();
    private volatile static Scanner r = new Scanner(System.in);

    // pedirPuerto() es una funcion que solo retornará si se ingresa un numero entero en la consola
    static private int pedirPuerto() {
        try {
            return r.nextInt();
        } catch (Exception e) {
            System.out.print("[Servidor Central] Debe ingresar numeros: ");
            pedirPuerto();
        }
        return -1;
    }

    static private void registroDistritos() {
        System.out.println("[Servidor Central] Iniciando servicio de registro de Distritos");
        System.out.println("Ingrese x en cualquier momento para volver al Menu Principal.");
        System.out.println("\n---AGREGAR DISTRITO---\n");
        // Pedir datos de nuevo Distrito
        System.out.print("[Servidor Central: Agregar Distrito] Nombre Distrito: ");
        String nombreDistrito = r.next();
        if(nombreDistrito.equals("x")) return;
        System.out.print("[Servidor Central: Agregar Distrito] IP Multicast: ");
        String ipMulticast = r.next();
        if(ipMulticast.equals("x")) return;
        System.out.print("[Servidor Central: Agregar Distrito] Puerto Multicast: ");
        int puertoMulticast = pedirPuerto();
        System.out.print("[Servidor Central: Agregar Distrito] IP Peticiones: ");
        String ipPeticiones = r.next();
        if(ipPeticiones.equals("x")) return;
        System.out.print("[Servidor Central: Agregar Distrito] Puerto Peticiones: ");
        int puertoPeticiones = pedirPuerto();
        listaDistritos.put(nombreDistrito, new Distrito(nombreDistrito, ipMulticast, puertoMulticast, ipPeticiones, puertoPeticiones));
        if(listaDistritos.get(nombreDistrito) != null) {
            System.out.println("[Servidor Central: Agregar Distrito] Distrito " + nombreDistrito + " agregado con exito!\n");
        }
        else {
            System.out.println("[Servidor Central: Agregar Distrito] Distrito " + nombreDistrito + " no se ha podido agregar.\n");
        }
    }

    static private void recibirClientes(comunicadorUnicast cCliente) {

        System.out.println("[Servidor Central] Esperando Clientes...");
        String mensaje = cCliente.recibirMensaje();
        System.out.println("[Servidor Central] Cliente recibido!");
        String ipCliente = cCliente.getIPUltimoCliente();
        int puertoCliente = cCliente.getPuertoUltimoCliente();
        System.out.println("[Servidor Central] Cliente " + ipCliente + ":" + puertoCliente + " dice: \"" + mensaje + "\"");
        if (listaDistritos.get(mensaje) != null) {
            Distrito distr = listaDistritos.get(mensaje);
            boolean lock = true;
            String respuesta = "denegado";
            while (lock) {
                System.out.println("[Servidor Central] Dar autorizacion a " + ipCliente + ":" + puertoCliente + " por Distrito " + mensaje + "?");
                System.out.println("(1) Si");
                System.out.println("(2) No");
                System.out.print("Apruebe o rechace la autorizacion: ");
                switch (r.next()) {
                    case "1":
                        lock = false;
                        System.out.println("[Servidor Central] Respuesta a " + ipCliente + ":" + puertoCliente + " por " + mensaje+":");
                        respuesta = "Nombre: " + distr.nombre + ", IP Multicast: " + distr.ipMulticast + ", Puerto Multicast: " + distr.puertoMulticast + ", IP Peticiones: " + distr.ipPeticiones + ", Puerto Peticiones: " + distr.ipPeticiones;
                        System.out.println("\""+respuesta+"\"");
                        break;
                    case "2":
                        lock = false;
                        System.out.print("[Servidor Central] Respuesta a " + ipCliente + ":" + puertoCliente + " por " + mensaje);
                        respuesta = "denegado";
                        System.out.println("\""+respuesta+"\"");
                        break;
                    default:
                        System.out.print("Entrada invalida");
                }
            }
            cCliente.enviarMensaje(respuesta);
        }
        else {
            System.out.println("[Servidor Central] Distrito no se encuentra registrado. Ignorando...");
        }

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

        boolean lock = true;
        while(lock) {
            System.out.println("\n[Servidor Central] Menu Principal\n");
            System.out.println("(1) Agregar Distrito ("+listaDistritos.keySet()+")");
            System.out.println("(2) Escuchar Peticiones de Clientes (indefinidamente, hasta que un Cliente haga una peticion)");
            System.out.println("(3) Salir");
            System.out.print("[Servidor Central] Seleccione una operacion a realizar: ");
            switch(r.next()) {
                case "1":
                    registroDistritos();
                    break;
                case "2":
                    recibirClientes(comunicadorConCliente);
                    break;
                case "3":
                    lock = false;
                    break;
                default:
                    System.out.println("Entrada invalida.");
            }
        }
    }
}
