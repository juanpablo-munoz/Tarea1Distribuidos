package aod.entidades;

import java.util.List;
import java.util.ArrayList;

public class TitanesDelDistrito {
    private List<Titan> vivos = new ArrayList<>();
    private List<Titan> capturados = new ArrayList<>();
    private List<Titan> asesinados = new ArrayList<>();

    public void agregarTitan(Titan t) {
        vivos.add(t);
    }

    public void capturarTitan(Titan t) {
        vivos.remove(t);
        capturados.add(t);
    }

    public void asesinarTitan(Titan t) {
        vivos.remove(t);
        asesinados.add(t);
    }

    public List<Titan> getVivos() {
        return vivos;
    }

    public List<Titan> getCapturados() {
        return capturados;
    }

    public List<Titan> getAsesinados() {
        return asesinados;
    }

    public Titan titanMasReciente() {
        Titan t = null;
        int ID_mas_nueva = Integer.MIN_VALUE;

        for(int i=0; i<vivos.size(); i++){
            if(vivos.get(i).ID > ID_mas_nueva){
                ID_mas_nueva = vivos.get(i).ID;
                t = vivos.get(i);
            }
        }
        for(int i=0; i<capturados.size(); i++){
            if(capturados.get(i).ID > ID_mas_nueva){
                ID_mas_nueva = capturados.get(i).ID;
                t = capturados.get(i);
            }
        }
        for(int i=0; i<asesinados.size(); i++){
            if(asesinados.get(i).ID > ID_mas_nueva){
                ID_mas_nueva = asesinados.get(i).ID;
                t = asesinados.get(i);
            }
        }
        return t;
    }

}
