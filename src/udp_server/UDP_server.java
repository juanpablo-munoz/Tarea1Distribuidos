/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package udp_server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

/**
 *
 * @author franc
 */
public class UDP_server {

    /**
     * @param args the command line arguments
     */
   public static void main(String[] args) throws SocketException, IOException {
        // TODO code application logic here
        int ID = 1;
        byte[] buf = new byte[1024];
        byte[] buf2 = new byte[1024];
        DatagramPacket packet;
        DatagramSocket socket = new DatagramSocket(4445);
        while(true){
        packet = new DatagramPacket(buf, buf.length);
        

        socket.receive(packet);
        String recibido = new String(packet.getData(),0);

        System.out.println("llego paquete: " + recibido);

        InetAddress address = packet.getAddress();
        
        
        int port = packet.getPort();
        
        String cadena = String.valueOf(ID);
        ID = ID+1;
        String envio = cadena;
        buf2 = envio.getBytes();
        packet = new DatagramPacket(buf2, buf2.length, address, port);
        socket.send(packet);
        }
        }
        
        
}
    

