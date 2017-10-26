package aod.comunicaciones;

import java.net.*;
import java.io.*;
import java.util.*;

public class Difusor {
    public InetAddress grupo = null;
    private int puerto_grupo;
    private MulticastSocket socketDifusor = null;

    public Difusor(String ip, int port) throws Exception {

        try {

            grupo = InetAddress.getByName(ip);
            puerto_grupo = port;
            socketDifusor = new MulticastSocket();

        } catch (Exception e) {
            System.out.println("Error constructor de Difusor");
            e.printStackTrace();
        }

    }

    public int difundir(String mensaje) {

        try {
            DatagramPacket cuerpo_mensaje = new DatagramPacket(mensaje.getBytes(), mensaje.length(), grupo, puerto_grupo);
            socketDifusor.send(cuerpo_mensaje);
        } catch (Exception e) {
            System.out.println("Error al intentar difundir mensaje");
            e.printStackTrace();
            return -1;
        }
        System.out.println("Se difundio mensaje!\n");
        System.out.println(mensaje);
        return 0;
    }




}
