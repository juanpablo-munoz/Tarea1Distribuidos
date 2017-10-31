/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UDP_server;
import java.io.IOException;
import java.util.Scanner;
import java.net.*;
import java.util.*;
import java.lang.*;
import java.util.logging.Level;
import java.util.logging.Logger;
/**

 * @author franc
 */

public class Distrito_server {
    
    
    public Distrito_server() throws IOException{
    

    }
    public static void main(String[] args) throws SocketException, IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("[Distrito] Nombre Servidor:");
        String Nombre_Distrito = scanner.next();
        System.out.print("[Distrito "+ Nombre_Distrito + "] IP Servidor Central: ");
        String ip_servidor_central = scanner.next();
        System.out.print("[Distrito "+ Nombre_Distrito + "] IP multicast: ");
        String ip_multicast = scanner.next();
        System.out.print("[Distrito "+ Nombre_Distrito + "] Puerto multicast: ");
        int puerto_multicast = scanner.nextInt();
        System.out.print("[Distrito "+ Nombre_Distrito + "] IP Peticion: ");
        String ip_peticion = scanner.next();
        System.out.print("[Distrito "+ Nombre_Distrito + "] Puerto Peticion: ");
        int puerto_peticion = scanner.nextInt();
        System.out.println();

        InetAddress grupo = InetAddress.getByName(ip_multicast);
        MulticastSocket socketDistrito = new MulticastSocket(puerto_multicast);
        socketDistrito.joinGroup(grupo);
        TitanesDelDistrito titanes = new TitanesDelDistrito();
        comunicadorUnicast lineaDistrito = new comunicadorUnicast(puerto_peticion);
        Thread hiloA = new MiHilo(1,Nombre_Distrito,socketDistrito,ip_multicast,puerto_multicast,ip_peticion,puerto_peticion, titanes,ip_servidor_central);
        Thread hiloB = new MiHilo5(titanes,lineaDistrito);
        hiloA.start();
        hiloB.start();


    }    
}
class MiHilo extends Thread {
    public int variable;
    public String nombre_del_dist;
    public DatagramSocket socket = null;
    public MulticastSocket socketDistrito = null;
    public InetAddress address;
    public InetAddress Ip_Multicast;
    public String Ip_unicast;
    public int puerto_multicast;
    public int puerto_unicast;
    byte[] buf;
    public TitanesDelDistrito titanes;
    
    public MiHilo(int numero, String nombre,MulticastSocket multisocket, String ip, int puerto,String ip2, int puerto2,TitanesDelDistrito titan,String ip_server  ) throws SocketException, UnknownHostException{
        variable = numero;
        nombre_del_dist=nombre;
        socket = new DatagramSocket();
        socketDistrito = multisocket;
        address = InetAddress.getByName(ip_server);
        Ip_Multicast =InetAddress.getByName(ip);
        Ip_unicast =ip2;
        puerto_multicast = puerto;
        puerto_unicast = puerto2;
        buf = new byte[1024];
        titanes =titan;
    }
    @Override
    public void run(){
        if(variable == 1){

        Scanner scanner = new Scanner(System.in);
        // Presenta en pantalla información sobre este hilo en particular
        Titan titan1;
            
        while(true){
        System.out.print("[Distrito "+ nombre_del_dist + "] Publicar Titán");
        System.out.println();
        System.out.print("[Distrito "+ nombre_del_dist + "]  Introducir nombre: ");
        String nombre_del_titan = scanner.next();
        System.out.println("[Distrito "+ nombre_del_dist + "]   Introducir tipo: ");
        System.out.println("1.- Normal ");
        System.out.println("2.- Excentrico ");
        System.out.println("3.- Cambiante ");
        int seleccion = scanner.nextInt();
        String tipo_del_titan;
        while(true){
            if (seleccion == 1){
                tipo_del_titan = "Normal";
                break;
            }
            if (seleccion == 2){
                tipo_del_titan = "Excentrico";
                break;
            }
            if (seleccion == 3){
                tipo_del_titan = "Cambiante";
                break;
            }
            else{
            System.out.println("Selecciona una opcion valida");
            }
        }
        buf = "ok".getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
        try {
            socket.send(packet);
        } catch (IOException ex) {
            Logger.getLogger(MiHilo.class.getName()).log(Level.SEVERE, null, ex);
            }
        buf = new byte[1024];
        packet = new DatagramPacket(buf, buf.length);
        try {
            socket.receive(packet);
        } catch (IOException ex) {
            Logger.getLogger(MiHilo.class.getName()).log(Level.SEVERE, null, ex);
            }
        String received = new String(packet.getData(), 0);
        int id_titan = Integer.parseInt(received.trim());

        titan1 = new Titan(id_titan , nombre_del_titan , tipo_del_titan , nombre_del_dist);
        titanes.agregarTitan(titan1);
        
        List<Titan> lista = titanes.getVivos();
        String listString = "";
        String lista_titanes_vivos = "";
        for(int i = 0; i < lista.size(); i++) {
            if (i == (lista.size()-1)){
                listString += lista.get(i).nombre + ", tipo "+ lista.get(i).tipo+ ", ID " + lista.get(i).ID + "\t";
                System.out.println("[Distrito "+nombre_del_dist+"] Se ha publicado el titan: "+lista.get(i).nombre);
                System.out.println("**************");
                System.out.println("ID: "+lista.get(i).ID);
                System.out.println("Nombre: "+lista.get(i).nombre);
                System.out.println("Tipo: "+lista.get(i).tipo);
                System.out.println("**************");
            }
            lista_titanes_vivos += lista.get(i).nombre + ", tipo "+ lista.get(i).tipo+ ", ID " + lista.get(i).ID + "\t";
            }

        buf = listString.getBytes();
        packet = new DatagramPacket(buf, buf.length, Ip_Multicast, puerto_multicast);
            try {
                socketDistrito.send(packet);
            } catch (IOException ex) {
                Logger.getLogger(MiHilo.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        }


        }
     }
 class MiHilo5 extends Thread {
    public TitanesDelDistrito titanes;
    public comunicadorUnicast lineaDistrito;
    
    public MiHilo5(TitanesDelDistrito titan, comunicadorUnicast unicast ) throws SocketException, UnknownHostException{
        titanes = titan;
        lineaDistrito = unicast;
    }
    @Override
    public void run(){
        String mensaje_enviar = "";
        while(true){
            String recibido = lineaDistrito.recibirMensaje();
            char[] aCaracteres = recibido.toCharArray();
            String id_total ="";
            int identificador = Integer.parseInt((String.valueOf(aCaracteres[0])).trim());
            for (int i = 1 ; i<aCaracteres.length;i++){
                id_total+= String.valueOf(aCaracteres[i]);
            }
            int id_titan = Integer.parseInt(id_total.trim());
            if(identificador == 1){
                List<Titan> lista = titanes.getVivos();
                String lista_titanes_vivos = "";
                for(int i = 0; i < lista.size(); i++) {
                lista_titanes_vivos += lista.get(i).nombre + ", tipo "+ lista.get(i).tipo+ ", ID " + lista.get(i).ID + "\t";
                }
                lineaDistrito.enviarMensajeRespuesta(lista_titanes_vivos);
                        
            }
            if(identificador == 3){
                List<Titan> lista = titanes.getVivos();
                mensaje_enviar = "No se encontro la ID del titan";
                for(int i = 0; i < lista.size(); i++) {
                    if (lista.get(i).ID == id_titan && !"Excentrico".equals(lista.get(i).tipo)){
                        titanes.capturarTitan(lista.get(i));
                        mensaje_enviar = "Captura exitosa";
                    }
                    if (lista.get(i).ID == id_titan && "Excentrico".equals(lista.get(i).tipo)){
                        mensaje_enviar = "El titan es tipo excentrico, por lo cual no se puede capturar";
                    }
                }
                lineaDistrito.enviarMensajeRespuesta(mensaje_enviar);
            }
            if(identificador == 4){
                List<Titan> lista = titanes.getVivos();
                mensaje_enviar = "No se encontro la ID del titan";
                for(int i = 0; i < lista.size(); i++) {
                    if (lista.get(i).ID == id_titan && !"Cambiante".equals(lista.get(i).tipo)){
                        titanes.asesinarTitan(lista.get(i));
                        mensaje_enviar = "Asesinato exitoso";
                    }
                    if (lista.get(i).ID == id_titan && "Cambiante".equals(lista.get(i).tipo)){
                        mensaje_enviar = "El titan es tipo Cambiante, por lo cual no se puede Asesinar";
                    }
                }
                lineaDistrito.enviarMensajeRespuesta(mensaje_enviar);
            }

        }
    }

}
   
    