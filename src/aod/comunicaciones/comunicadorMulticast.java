package aod.comunicaciones;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class comunicadorMulticast {
    private InetAddress grupo = null;
    private int puerto_grupo;
    private MulticastSocket multicastSocket = null;
    byte[] mensajeRecibido = new byte[1024];
    byte[] mensajeEnviado = new byte[1024];

    public comunicadorMulticast(String ip, int port) {
        try {
            grupo = InetAddress.getByName(ip);
        } catch (Exception e) {
            System.out.println("Error al reconocer la IP.");
            e.printStackTrace();
        }

        puerto_grupo = port;
        try {
            multicastSocket = new MulticastSocket();
        } catch (Exception e) {
            System.out.println("Error al crear MulticastSocket.");
            e.printStackTrace();
        }
    }

    public void unirseAlGrupo() {
        try {
            multicastSocket.joinGroup(grupo);
        } catch (Exception e) {
            System.out.println("Error al intentar unirse a grupo multicast.");
            e.printStackTrace();
        }
    }

    public void salirDelGrupo() {
        try {
            multicastSocket.leaveGroup(grupo);
        } catch (Exception e) {
            System.out.println("Error al intentar salirse del grupo multicast.");
            e.printStackTrace();
        }
    }

    public void difundirMensaje(String mensaje) {
        mensajeEnviado = mensaje.getBytes();
        DatagramPacket cuerpo_mensaje =
                new DatagramPacket(mensajeEnviado, mensajeEnviado.length, grupo, puerto_grupo);
        try {
            multicastSocket.send(cuerpo_mensaje);
        } catch (Exception e) {
            System.out.println("Error al intentar difundir mensaje");
            e.printStackTrace();
            return;
        }
        System.out.println("Se difundio mensaje!");
    }

    public String recibirMensaje() {
        DatagramPacket paqueteRecibido =
                new DatagramPacket(mensajeRecibido, mensajeRecibido.length);
        try {
            multicastSocket.receive(paqueteRecibido);
        } catch (Exception e) {
            System.out.println("Error al intentar recibir mensaje multicast.");
            e.printStackTrace();
            return null;
        }
        return new String(paqueteRecibido.getData());
    }
}

