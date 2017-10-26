package aod.comunicaciones;

import sun.nio.cs.ext.MacUkraine;

import java.net.*;
import java.io.*;
import java.util.*;

    public class receptorDifusion {

        private InetAddress grupo;
        private int puerto;
        private MulticastSocket mcSocket = null;
        private byte[ ] buffer = new byte[4096];

        public receptorDifusion(String arg_ip, int arg_puerto) throws Exception {

            try {
                grupo = InetAddress.getByName(arg_ip);
                mcSocket = new MulticastSocket(arg_puerto);
                mcSocket.joinGroup(grupo);

            } catch (Exception e) {
                System.out.println("Error en el constructor de receptorDifusion");
                e.printStackTrace();
            }
        }

        public DatagramPacket recibirDifusion() {

            try {

                DatagramPacket p = new DatagramPacket(buffer, buffer.length);
                mcSocket.receive(p);
                System.out.println("Recibido mensaje de difusion!\n");
                System.out.println(new String(buffer));
                return p;

            } catch (Exception e) {
                System.out.println("Error en recibirDifusion");
                e.printStackTrace();
            }
            return null;
        }



}
