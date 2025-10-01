package amedeo.mignano.entities;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class TempiPercorrenza {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "tempo_percorrenza_effettivo")
    private double tempoPercorrenzaEffettivo;

    @ManyToOne
    @JoinColumn(name = "mezzoTrasporto_id")
    private MezzoTrasporto mezzoTrasporto;
    @ManyToOne
    @JoinColumn(name = "tratta_id")
    private Tratta tratta;


    public TempiPercorrenza(){};
    public TempiPercorrenza(double tempoPercorrenzaEffettivo, MezzoTrasporto mt, Tratta t){
        this.tempoPercorrenzaEffettivo=tempoPercorrenzaEffettivo;
        this.mezzoTrasporto=mt;
        this.tratta=t;
    };


    public UUID getId() {
        return id;
    }

    public double getTempoPercorrenzaEffettivo() {
        return tempoPercorrenzaEffettivo;
    }

    public void setTempoPercorrenzaEffettivo(double tempoPercorrenzaEffettivo) {
        this.tempoPercorrenzaEffettivo = tempoPercorrenzaEffettivo;
    }

    public MezzoTrasporto getMezzoTrasporto() {
        return mezzoTrasporto;
    }

    public void setMezzoTrasporto(MezzoTrasporto mezzoTrasporto) {
        this.mezzoTrasporto = mezzoTrasporto;
    }

    public Tratta getTratta() {
        return tratta;
    }

    public void setTratta(Tratta tratta) {
        this.tratta = tratta;
    }

    @Override
    public String toString() {
        return "TempiPercorrenza{" +
                "id=" + id +
                ", tempoPercorrenzaEffettivo=" + tempoPercorrenzaEffettivo +
                ", mezzoTrasporto=" + mezzoTrasporto +
                ", tratta=" + tratta +
                '}';
    }
}
