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
            
    public Distrito(String ip, int puerto) throws IOException{
    

        try {
            grupo = InetAddress.getByName(ip);
            puerto_grupo= puerto;
            socketDistrito = new MulticastSocket(puerto_grupo);
            socketDistrito.joinGroup(grupo);
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

        // send request
        
        
        Thread hiloA = new MiHilo(1,Nombre_Distrito);
        hiloA.start();


    }    
}
class MiHilo extends Thread {
    public int variable;
    public String nombre_del_dist;
    public comunicadorUnicast Dist=null;
    public DatagramSocket socket = null;
    public InetAddress address;
    byte[] buf;
    public MiHilo(int numero, String nombre) throws SocketException, UnknownHostException{
        variable = numero;
        nombre_del_dist=nombre;
        socket = new DatagramSocket();
        address = InetAddress.getByName("127.0.0.1");
        buf = new byte[1024];
        String envio = new String("ok");
        buf = envio.getBytes();
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
        for(int i = 0; i < lista.size(); i++) {
            System.out.println(lista.get(i).ID);
            System.out.println(lista.get(i).nombre);
            System.out.println(lista.get(i).tipo);
            }
        }
        }

        }
     }
    