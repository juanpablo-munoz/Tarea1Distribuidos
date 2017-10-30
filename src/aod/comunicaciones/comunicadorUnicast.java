package aod.comunicaciones;


import java.io.BufferedReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class comunicadorUnicast {
    InetAddress ip = null;
    int puerto;
    InetAddress ipUltimoCliente = null;
    int puertoUltimoCliente;
    DatagramSocket unicastSocket = null;
    byte[] mensajeRecibido = new byte[1024];
    byte[] mensajeEnviado = new byte[1024];

    public comunicadorUnicast(String arg_ip, int arg_puerto) {
        try {
            ip = InetAddress.getByName(arg_ip);
        } catch (Exception e) {
            System.out.println("Error al reconocer la IP.");
            e.printStackTrace();
        }

        puerto = arg_puerto;
        try {
            unicastSocket = new DatagramSocket(puerto);
        } catch (Exception e) {
            System.out.println("Error al crear DatagramSocket.");
            unicastSocket = null;
            e.printStackTrace();
        }
    }

    public comunicadorUnicast(int arg_puerto) {
        try {
            ip = InetAddress.getLocalHost();
        } catch (Exception e) {
            System.out.println("Error al reconocer la IP.");
            e.printStackTrace();
        }

        puerto = arg_puerto;
        try {
            unicastSocket = new DatagramSocket(puerto);
        } catch (Exception e) {
            System.out.println("Error al crear DatagramSocket.");
            unicastSocket = null;
            e.printStackTrace();
        }
    }

    public boolean bienInicializado() {
        return ip != null && unicastSocket != null;
    }

    public String getIP() {
        return ip.getHostAddress();
    }

    public void enviarMensajeRespuesta(String msj) {
        mensajeEnviado = msj.getBytes();
        DatagramPacket paqueteEnviado =
                new DatagramPacket(mensajeEnviado, mensajeEnviado.length, ipUltimoCliente, puertoUltimoCliente);
        try {
            unicastSocket.send(paqueteEnviado);
            //System.out.println("\""+msj+"\": mensaje enviado!");
        } catch (Exception e) {
            System.out.println("Error al intentar enviar mensaje unicast.");
            e.printStackTrace();
        }
    }

    public void enviarMensajePeticion(String msj) {
        mensajeEnviado = msj.getBytes();
        DatagramPacket paqueteEnviado =
                new DatagramPacket(mensajeEnviado, mensajeEnviado.length, ip, puerto);
        try {
            unicastSocket.send(paqueteEnviado);
            //System.out.println("\""+msj+"\": mensaje enviado!");
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
        ipUltimoCliente = paqueteRecibido.getAddress();
        puertoUltimoCliente = paqueteRecibido.getPort();
        return new String(paqueteRecibido.getData()).trim();
    }

    public String getIPUltimoCliente() {
        return ipUltimoCliente.getHostName();
    }

    public int getPuertoUltimoCliente() {
        return puertoUltimoCliente;
    }
}
