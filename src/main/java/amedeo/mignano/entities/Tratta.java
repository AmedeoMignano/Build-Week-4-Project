package amedeo.mignano.entities;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
public class Tratta {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "capolinea")
    private String capolinea;
    @Column(name = "partenza")
    private String partenza;
    @Column(name = "tempo_percorrenza_previsto")
    private double tempoPercorrenzaPrevisto;

    public Tratta(){};
    public Tratta( String capolinea, String partenza, double tempoPercorrenzaPrevisto){
        this.capolinea=capolinea;
        this.partenza=partenza;
        this.tempoPercorrenzaPrevisto=tempoPercorrenzaPrevisto;
    }

    public UUID getId() {
        return id;
    }
    public String getCapolinea() {
        return capolinea;
    }
    public void setCapolinea(String capolinea) {
        this.capolinea = capolinea;
    }
    public String getPartenza() {
        return partenza;
    }
    public void setPartenza(String partenza) {
        this.partenza = partenza;
    }
    public double getTempoPercorrenzaPrevisto() {
        return tempoPercorrenzaPrevisto;
    }
    public void setTempoPercorrenzaPrevisto(double tempoPercorrenzaPrevisto) {
        this.tempoPercorrenzaPrevisto = tempoPercorrenzaPrevisto;
    }

    @Override
    public String toString() {
        return "Tratta{" +
                "id=" + id +
                ", capolinea='" + capolinea + '\'' +
                ", partenza='" + partenza + '\'' +
                ", tempoPercorrenzaPrevisto=" + tempoPercorrenzaPrevisto +
                '}';
    }
}
