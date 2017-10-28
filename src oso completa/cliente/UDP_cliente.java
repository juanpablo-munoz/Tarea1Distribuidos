/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UDP_cliente;
import java.io.IOException;
import java.net.*;
import javax.swing.JOptionPane;
import java.lang.*;
import java.util.*;
/**
 *
 * @author franc
 */
public class UDP_cliente {
    
    public static void main(String[] args) throws SocketException, IOException {
        int mcPort = 1234;
        String mcIPStr = "230.1.1.3";
        MulticastSocket mcSocket = null;
        InetAddress mcIPAddress = null;
        mcIPAddress = InetAddress.getByName(mcIPStr);
        mcSocket = new MulticastSocket(mcPort);
        System.out.println("Multicast Receiver running at:"
            + mcSocket.getLocalSocketAddress());
        mcSocket.joinGroup(mcIPAddress);
        String msn = JOptionPane.showInputDialog("ingrese el mensaje a enviar");
        byte[] mensaje = msn.getBytes();
        DatagramPacket miPaquete = new DatagramPacket (mensaje, msn.length(), mcIPAddress, mcPort);
        mcSocket.send(miPaquete);
        
    }
}
