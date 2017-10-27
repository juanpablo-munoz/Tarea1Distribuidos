package aod.comunicaciones;


import java.io.BufferedReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class comunicadorUnicast {
    InetAddress ip = null;
    int puerto;
    DatagramSocket unicastSocket = null;
    byte[] mensajeRecibido = new byte[1024];
    byte[] mensajeEnviado = new byte[1024];

    public comunicadorUnicast(String arg_ip, int arg_puerto) {
        try {
            ip = InetAddress.getByName(arg_ip);
        } catch (Exception e) {
            System.out.println("Error al reconocer la IP.");
            //e.printStackTrace();
        }

        puerto = arg_puerto;
        try {
            unicastSocket = new DatagramSocket();
        } catch (Exception e) {
            System.out.println("Error al crear DatagramSocket.");
            unicastSocket = null;
            //e.printStackTrace();
        }
    }

    public boolean bienInicializado() {
        return ip != null && unicastSocket != null;
    }

    public void enviarMensaje(String msj) {
        mensajeEnviado = msj.getBytes();
        DatagramPacket paqueteEnviado =
                new DatagramPacket(mensajeEnviado, mensajeEnviado.length, ip, puerto);
        try {
            unicastSocket.send(paqueteEnviado);
        } catch (Exception e) {
            System.out.println("Error al intentar enviar mensaje unicast.");
            e.printStackTrace();
        }
    }

    public String recibirMensaje() {
        DatagramPacket paqueteRecibido =
                new DatagramPacket(mensajeRecibido, mensajeRecibido.length);
        try {
            unicastSocket.receive(paqueteRecibido);
        } catch (Exception e) {
            System.out.println("Error al intentar recibir mensaje unicast.");
            e.printStackTrace();
            return null;
        }
        return new String(paqueteRecibido.getData());
    }

}
