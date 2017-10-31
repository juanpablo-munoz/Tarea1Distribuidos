package udp_server;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

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
            String respuesta = "0";
            while (lock) {
                System.out.println("[Servidor Central] Dar autorizacion a " + ipCliente + ":" + puertoCliente + " por Distrito " + mensaje + "?");
                System.out.println("(1) Si");
                System.out.println("(2) No");
                System.out.print("Apruebe o rechace la autorizacion: ");
                switch (r.next()) {
                    case "1":
                        lock = false;
                        System.out.println("[Servidor Central] Respuesta a " + ipCliente + ":" + puertoCliente + " por " + mensaje+":");
                        respuesta = "Nombre: " + distr.nombre + ", IP Multicast: " + distr.ipMulticast + ", Puerto Multicast: " + distr.puertoMulticast + ", IP Peticiones: " + distr.ipPeticiones + ", Puerto Peticiones: " + distr.puertoPeticiones;
                        System.out.println("\""+respuesta+"\"");
                        break;
                    case "2":
                        lock = false;
                        System.out.print("[Servidor Central] Respuesta a " + ipCliente + ":" + puertoCliente + " por " + mensaje);
                        respuesta = "0";
                        System.out.println(" \""+respuesta+"\"");
                        break;
                    default:
                        System.out.print("Entrada invalida");
                }
            }
            cCliente.enviarMensajeRespuesta(respuesta);
        }
        else {
            System.out.println("[Servidor Central] Distrito no se encuentra registrado. Ignorando...");
        }

    }

    public static void main(String[] args) {
        int puertoRecepcion;      	
        List<String> capturados = new ArrayList<String>();
        List<String> asesinados = new ArrayList<String>();
        Thread hiloA = new MiHilo2(1,capturados,asesinados);
        Thread hiloB = new MiHilo2(2,capturados,asesinados);
        Thread hiloC = new MiHilo2(3,capturados,asesinados);
        Thread hiloD = new MiHilo2(4,capturados,asesinados);
        hiloA.start();
        hiloB.start();
        hiloC.start();
        hiloD.start();
        
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

class MiHilo2 extends Thread {
    public int variable;
    public List<String> capturados;
    public List<String> asesinados;
    public MiHilo2(int numero, List<String> cap , List<String> ase){
        variable = numero;
        capturados = cap;
        asesinados = ase;
    }
    @Override
    public void run(){
        if(variable == 1){
            int ID = 1;
            byte[] buf;
            DatagramPacket packet;
            DatagramSocket socket = null;
            try {
                socket = new DatagramSocket(4445);
            } catch (SocketException ex) {
                Logger.getLogger(MiHilo2.class.getName()).log(Level.SEVERE, null, ex);
            }
            while(true){
                buf = new byte[1024];
                packet = new DatagramPacket(buf, buf.length);


                    try {
                        socket.receive(packet);
                    } catch (IOException ex) {
                        Logger.getLogger(MiHilo2.class.getName()).log(Level.SEVERE, null, ex);
                    }

                InetAddress address = packet.getAddress();
                int port = packet.getPort();

                String cadena = String.valueOf(ID);
                ID = ID+1;
                buf = cadena.getBytes();
                packet = new DatagramPacket(buf, buf.length,address,port);
                    try {
                        socket.send(packet);
                    } catch (IOException ex) {
                        Logger.getLogger(MiHilo2.class.getName()).log(Level.SEVERE, null, ex);
                    }
        
            }
        }
        if (variable == 2){
            comunicadorUnicast lineaDistrito = new comunicadorUnicast(4446);
            while(true){
                String mensaje = lineaDistrito.recibirMensaje();
                capturados.add(mensaje);
            }
        }
        if (variable == 3){
            comunicadorUnicast lineaDistrito = new comunicadorUnicast(4447);
            while(true){
                String mensaje = lineaDistrito.recibirMensaje();
                asesinados.add(mensaje);
            }
        }
        if (variable == 4){
            comunicadorUnicast lineaCliente = new comunicadorUnicast(4448);
            
            while(true){
                String opcion = lineaCliente.recibirMensaje();
                int identificador = Integer.parseInt(opcion.trim());
                String lista_total="";
                if(identificador == 1){
                    for (int i=0; i<capturados.size();i++){
                        lista_total+= capturados.get(i) + "\n";
                    }
                    lineaCliente.enviarMensajeRespuesta(lista_total);
                }
                if(identificador == 2){
                    for (int i=0; i<asesinados.size();i++){
                        lista_total+= asesinados.get(i) + "\n";
                    }
                    lineaCliente.enviarMensajeRespuesta(lista_total);
                }
            }
        }
        }

        
     }
