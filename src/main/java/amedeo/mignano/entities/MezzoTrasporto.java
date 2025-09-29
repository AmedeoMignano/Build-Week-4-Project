package amedeo.mignano.entities;

import amedeo.mignano.entities.enums.Stato;
import amedeo.mignano.entities.enums.TipoMezzoTrasporto;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
public class MezzoTrasporto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo")
    private TipoMezzoTrasporto tipoMezzoTrasporto;
    private int capienza;
    @Column(name = "numero_tratte_percorse")
    private int nPercorsaTratta;
    @Column(name = "tempo_percorrenza")
    private double tempoPercorrenza;
    @Enumerated(EnumType.STRING)
    private Stato stato;

    @OneToMany(mappedBy = "mezzoTrasporto")
    private List<StatoMezzoTrasporto> stati = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "mezzoTrasporto_tratta",
            joinColumns = @JoinColumn(name = "mezzoTrasporto_id"),
            inverseJoinColumns = @JoinColumn(name = "tratta_id")
    )
    List<Tratta> tratte = new ArrayList<>();

    public MezzoTrasporto(){};
    public MezzoTrasporto(TipoMezzoTrasporto tipoMezzoTrasporto, double tempoPercorrenza, int nPercorsaTratta){
        this.tipoMezzoTrasporto=tipoMezzoTrasporto;
        this.tempoPercorrenza=tempoPercorrenza;
        this.nPercorsaTratta=nPercorsaTratta;
        this.capienza= tipoMezzoTrasporto.equals(TipoMezzoTrasporto.AUTOBUS) ? 60 : 30;
        this.stato = Stato.IN_SERVIZIO;
    }

    public int getId() {
        return id;
    }
    public TipoMezzoTrasporto getTipoMezzoTrasporto() {
        return tipoMezzoTrasporto;
    }
    public void setTipoMezzoTrasporto(TipoMezzoTrasporto tipoMezzoTrasporto) {
        this.tipoMezzoTrasporto = tipoMezzoTrasporto;
    }
    public int getCapienza() {
        return capienza;
    }
    public void setCapienza(int capienza) {
        this.capienza = capienza;
    }
    public int getnPercorsaTratta() {
        return nPercorsaTratta;
    }
    public void setnPercorsaTratta(int nPercorsaTratta) {
        this.nPercorsaTratta = nPercorsaTratta;
    }
    public double getTempoPercorrenza() {
        return tempoPercorrenza;
    }
    public void setTempoPercorrenza(double tempoPercorrenza) {
        this.tempoPercorrenza = tempoPercorrenza;
    }

    public Stato getStato() {
        return stato;
    }

    public void setStato(Stato stato) {
        this.stato = stato;
    }

    @Override
    public String toString() {
        return "MezzoTrasporto{" +
                "id=" + id +
                ", tipoMezzoTrasporto=" + tipoMezzoTrasporto +
                ", capienza=" + capienza +
                ", nPercorsaTratta=" + nPercorsaTratta +
                ", tempoPercorrenza=" + tempoPercorrenza +
                ", stato=" + stato +
                ", stati=" + stati +
                ", tratte=" + tratte +
                '}';
    }
}
