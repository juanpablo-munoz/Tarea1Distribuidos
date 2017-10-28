/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor_distrito;
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

public class Distrito {
    public InetAddress grupo;
    public int puerto_grupo;
    public MulticastSocket socketDistrito = null;
    public MulticastSocket socketsecreto = null;
    public InetAddress ip_secreta;
    public int puerto_secreta;
    DatagramSocket socketUDP=null;
            
    public Distrito(String ip, int puerto) throws IOException{
    

        try {
            grupo = InetAddress.getByName(ip);
            puerto_grupo= puerto;
            socketDistrito = new MulticastSocket(puerto_grupo);
            socketDistrito.joinGroup(grupo);
            puerto_secreta = 12345;
            String Ip1_secreta = "230.1.1.1";
            MulticastSocket socket_secreto = null;
            ip_secreta = InetAddress.getByName(Ip1_secreta);
            socketsecreto = new MulticastSocket(puerto_secreta);
            socketsecreto.joinGroup(ip_secreta);
        } catch (UnknownHostException ex) {
            Logger.getLogger(Distrito.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    public static void main(String[] args) throws SocketException, IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("[Distrito] Nombre Servidor:");
        String Nombre_Distrito = scanner.next();
        System.out.print("[Distrito "+ Nombre_Distrito + "] IP multicast: ");
        String ip_multicast = scanner.next();
        System.out.print("[Distrito "+ Nombre_Distrito + "] Puerto multicast: ");
        int puerto_multicast = scanner.nextInt();
        System.out.print("[Distrito "+ Nombre_Distrito + "] IP Peticion: ");
        String ip_peticion = scanner.next();
        System.out.print("[Distrito "+ Nombre_Distrito + "] Puerto Peticion: ");
        int puerto_peticion = scanner.nextInt();
        System.out.println();
        Distrito dist = new Distrito(ip_multicast,puerto_multicast);
        
        Thread hiloA = new MiHilo(1,Nombre_Distrito,dist);
        Thread hiloB = new MiHilo(2,Nombre_Distrito,dist);
        hiloA.start();
        hiloB.start();

    }    
}
class MiHilo extends Thread {
    public int variable;
    public String nombre_del_dist;
    public Distrito Dist=null;
    
    public MiHilo(int numero, String nombre, Distrito distrito){
        variable = numero;
        nombre_del_dist=nombre;
        Dist = distrito;
    }
    @Override
    public void run(){
        if(variable == 1){

        Scanner scanner = new Scanner(System.in);
        // Presenta en pantalla información sobre este hilo en particular
        Titan titan1;
        TitanesDelDistrito titanes = new TitanesDelDistrito();
        
        while(true){
        System.out.print("[Distrito "+ nombre_del_dist + "] Publicar Titán");
        System.out.println();
        System.out.print("[Distrito "+ nombre_del_dist + "]  Introducir nombre: ");
        String nombre_del_titan = scanner.next();
        System.out.print("[Distrito "+ nombre_del_dist + "]   Introducir tipo: ");
        String tipo_del_titan = scanner.next();
        
        String msn = "ok";
        byte[] mensaje = msn.getBytes();
        DatagramPacket miPaquete = new DatagramPacket (mensaje, msn.length(), Dist.ip_secreta, Dist.puerto_secreta);
            try {
                Dist.socketsecreto.send(miPaquete);
            } catch (IOException ex) {
                Logger.getLogger(MiHilo.class.getName()).log(Level.SEVERE, null, ex);
            }
        DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
            try {
                Dist.socketsecreto.receive(packet);
            } catch (IOException ex) {
                Logger.getLogger(MiHilo.class.getName()).log(Level.SEVERE, null, ex);
            }
        String msg = new String(packet.getData(), packet.getOffset(),
            packet.getLength());
        System.out.println("el distrito a respondido  : "+msg);
        
        if (titanes.titanMasReciente() == null){
            titan1 = new Titan(1 , nombre_del_titan , tipo_del_titan , nombre_del_dist);
            titanes.agregarTitan(titan1);
        }
        else{
            titan1= titanes.titanMasReciente();
            titan1= new Titan (titan1.ID+1,nombre_del_titan,tipo_del_titan,nombre_del_dist);
            titanes.agregarTitan(titan1);
        }
        
        List<Titan> lista = titanes.getVivos();
        for(int i = 0; i < lista.size(); i++) {
            System.out.println(lista.get(i).ID);
            System.out.println(lista.get(i).nombre);
            System.out.println(lista.get(i).tipo);
            }
        }
        }
        if(variable == 2){
                DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
                while(true){
                    try {
                        Dist.socketsecreto.receive(packet);
                        String msg = new String(packet.getData(), packet.getOffset(),
                                packet.getLength());
                        if(msg.length() == 2){
                            String msn = "hola";
                            byte[] mensaje = msn.getBytes();
                            DatagramPacket miPaquete = new DatagramPacket (mensaje, msn.length(), Dist.ip_secreta, Dist.puerto_secreta);
                            Dist.socketsecreto.send(miPaquete);
                            System.out.println("[Multicast  Receiver] Received:" + msg);
                        }
                        
                    } catch (IOException ex) {
                        Logger.getLogger(MiHilo.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
        }
        }
     }
    