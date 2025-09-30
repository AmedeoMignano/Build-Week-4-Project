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
    @Column(name = "tempo_percorrenza_effettivo")
    private double tempoPercorrenzaEffettivo;
    @Column(name = "n_volte_tratta_percorsa")
    private int nVolteTrattaPercorsa;

    @ManyToOne
    @JoinColumn(name = "mezzoTrasporto_id")
    private MezzoTrasporto mezzoTrasporto;



    public Tratta(){};
    public Tratta( String capolinea, String partenza, double tempoPercorrenzaPrevisto,  double tempoPercorrenzaEffettivo, int nVolteTrattaPercorsa, MezzoTrasporto mt){
        this.capolinea=capolinea;
        this.partenza=partenza;
        this.tempoPercorrenzaPrevisto=tempoPercorrenzaPrevisto;
        this.tempoPercorrenzaEffettivo=tempoPercorrenzaEffettivo;
        this.nVolteTrattaPercorsa=nVolteTrattaPercorsa;
        this.mezzoTrasporto= mt;
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

    public double getTempoPercorrenzaEffettivo() {
        return tempoPercorrenzaEffettivo;
    }

    public void setTempoPercorrenzaEffettivo(double tempoPercorrenzaEffettivo) {
        this.tempoPercorrenzaEffettivo = tempoPercorrenzaEffettivo;
    }

    public int getnVolteTrattaPercorsa() {
        return nVolteTrattaPercorsa;
    }

    public void setnVolteTrattaPercorsa(int nVolteTrattaPercorsa) {
        this.nVolteTrattaPercorsa = nVolteTrattaPercorsa;
    }

    public MezzoTrasporto getMezzoTrasporto() {
        return mezzoTrasporto;
    }

    public void setMezzoTrasporto(MezzoTrasporto mezzoTrasporto) {
        this.mezzoTrasporto = mezzoTrasporto;
    }

    @Override
    public String toString() {
        return "Tratta{" +
                "id=" + id +
                ", capolinea='" + capolinea + '\'' +
                ", partenza='" + partenza + '\'' +
                ", tempoPercorrenzaPrevisto=" + tempoPercorrenzaPrevisto +
                ", tempoPercorrenzaEffettivo=" + tempoPercorrenzaEffettivo +
                ", nVolteTrattaPercorsa=" + nVolteTrattaPercorsa +
                ", mezzoTrasporto=" + mezzoTrasporto +
                '}';
    }
}