package udp_server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

class Cliente {

    // pedirPuerto() es una funcion que solo retornará si se ingresa un numero entero en la consola
    static private int pedirPuerto() {
        try {
            Scanner r = new Scanner(System.in);
            return r.nextInt();
        } catch (Exception e) {
            System.out.print("[Cliente] Debe ingresar numeros: ");
            pedirPuerto();
        }
        return -1;
    }

    public static void main(String[] args) {
        // Pedir datos de Servidor Central
        Scanner reader = new Scanner(System.in);
        System.out.print("[Cliente] Ingresar IP Servidor Central: ");
        String ipServidorCentral = reader.next();
        System.out.print("[Cliente] Ingresar Puerto Servidor Central: ");
        int puertoServidorCentral = pedirPuerto();

        // Crear canal de comunicación Unicast hacia el Servidor Central
        comunicadorUnicast lineaServidorCentral =
                new comunicadorUnicast(ipServidorCentral, puertoServidorCentral);
        while(true){
            // Pedir nombre del Distrito a Investigar
            System.out.print("[Cliente] Ingresar Nombre de Distrito a Investigar: ");
            String distritoInvestigado = reader.next();

            // Hacer Petición para investigar el Distrito al Servidor Central
            lineaServidorCentral.enviarMensajePeticion(distritoInvestigado);
            String resp = lineaServidorCentral.recibirMensaje();
            System.out.println("[Cliente] Servidor Central responde:\n\n"+resp);

            // Si la respuesta del Servidor Central es el string "0", es porque se ha rechazado la petición
            if(resp.trim().equals("0")) {
                System.out.println("[Cliente] Acceso denegado al Distrito " + distritoInvestigado);
                return;
            }
            // Si la respuesta es cualquier otra cosa (por ej.: la info del Distrito), entonces se entiende que ésta fue positiva
            // Pedir la info de conexión con el Distrito
            System.out.println("[Cliente] Puede conectarse al Distrito "+distritoInvestigado+"\n");
            System.out.print("[Cliente] Ingresar IP Multicast del Distrito "+distritoInvestigado+": ");
            String ipGrupoDistrito = reader.next();
            System.out.print("[Cliente] Ingresar Puerto Multicast del Distrito "+distritoInvestigado+": ");
            int puertoGrupoDistrito = pedirPuerto();
            System.out.print("[Cliente] Ingresar IP Peticiones del Distrito "+distritoInvestigado+": ");
            String ipPeticionesDistrito = reader.next();
            System.out.print("[Cliente] Ingresar Puerto Peticiones del Distrito "+distritoInvestigado+": ");
            int puertoPeticionesDistrito = pedirPuerto();

            // Unirse al grupo Multicast del Distrito
            comunicadorMulticast grupoDistrito =
                    new comunicadorMulticast(ipGrupoDistrito, puertoGrupoDistrito);
            grupoDistrito.unirseAlGrupo();

            Thread hiloA = new MiHilo3(1,grupoDistrito);
            hiloA.start();

            // Establecer conexión Unicast con el Distrito, para el paso de peticiones de Captura y Asesinato
            comunicadorUnicast lineaDistrito =
                    new comunicadorUnicast(ipPeticionesDistrito, puertoPeticionesDistrito);

            // Menu principal
            String mensaje_id_titan;
            boolean irse = true; 
            while(irse) {
                System.out.println("[Cliente] (1) Listar Titanes");
                System.out.println("[Cliente] (2) Cambiar Distrito");
                System.out.println("[Cliente] (3) Capturar Titan");
                System.out.println("[Cliente] (4) Asesinar Titan");
                System.out.println("[Cliente] (5) Listar Titanes Capturados");
                System.out.println("[Cliente] (6) Listar Titanes Asesinados");
                String mensaje_recibido = "";
                // Usar la misma funcion pedirPuerto() para garantizar que se ingrese un numero
                System.out.print("[Cliente] Selecione opcion a proceder: ");
                switch (reader.next()) {
                    case "1":
                        lineaDistrito.enviarMensajePeticion("11");
                        mensaje_recibido = lineaDistrito.recibirMensaje();
                        System.out.println("Lista De Titanes Vivos:");
                        System.out.println(mensaje_recibido);
                        break;
                    case "2":
                        grupoDistrito.salirDelGrupo();
                        irse = false;
                        break;
                    case "3":
                        System.out.println("Ingresar ID del titan a capturar");
                        mensaje_id_titan = reader.next();
                        lineaDistrito.enviarMensajePeticion("3"+mensaje_id_titan);
                        mensaje_recibido = lineaDistrito.recibirMensaje();
                        System.out.println(mensaje_recibido);
                        break;
                    case "4":
                        System.out.println("Ingresar ID del titan a Asesinar");
                        mensaje_id_titan = reader.next();
                        lineaDistrito.enviarMensajePeticion("4"+mensaje_id_titan);
                        mensaje_recibido = lineaDistrito.recibirMensaje();
                        System.out.println(mensaje_recibido);
                        break;
                    case "5":
                        //Listar Titanes Capturados
                        break;
                    case "6":
                        //Listar Titanes Asesinados
                        break;
                    default:
                        System.out.println("Elija una de las opciones (1-6).");
                }
            }
        }
    }
}

class MiHilo3 extends Thread {
    public int variable;
    public comunicadorMulticast grupoDistrito;
    public MiHilo3(int numero,comunicadorMulticast multicast){
        variable = numero;
        grupoDistrito = multicast;
    }
    @Override
    public void run(){
        if(variable == 1){
            while(true){
            String Listar_titanes = grupoDistrito.recibirMensaje();
            System.out.println();
            System.out.println("[Cliente]Aparece nuevo Titan! "+Listar_titanes);
            System.out.print("Seleccione una opcion del menu (si es que estaba en el menu): ");}
        }
        }

        
     }