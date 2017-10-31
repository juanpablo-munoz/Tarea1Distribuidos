package UDP_server;

public class Titan {
    public int ID;
    public String nombre = null;
    public String tipo = null;
    public String distrito_origen = null;
    
    public Titan(int id_titan, String nombre_titan, String tipo_titan, String distrito){
        ID = id_titan;
        nombre = nombre_titan;
        tipo = tipo_titan;
        distrito_origen = distrito;
    }
}

