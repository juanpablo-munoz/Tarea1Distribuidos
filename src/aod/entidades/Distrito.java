package aod.entidades;

public class Distrito {
    public String nombre;
    public String ipMulticast;
    public int puertoMulticast;
    public String ipPeticiones;
    public int puertoPeticiones;

    public Distrito(String nombre, String ip_multicast, int p_multicast, String ip_unicast, int p_unicast) {
        this.nombre = nombre;
        this.ipMulticast = ip_multicast;
        this.puertoMulticast = p_multicast;
        this.ipPeticiones = ip_unicast;
        this.puertoPeticiones = p_unicast;
    }
}
