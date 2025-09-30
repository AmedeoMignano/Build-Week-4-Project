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
    @Enumerated(EnumType.STRING)
    private Stato stato;

    @OneToMany(mappedBy = "mezzoTrasporto")
    private List<StatoMezzoTrasporto> stati = new ArrayList<>();
    @OneToMany(mappedBy = "mezzoTrasporto")
    private List<Tratta> tratte = new ArrayList<>();


    public MezzoTrasporto(){};
    public MezzoTrasporto(TipoMezzoTrasporto tipoMezzoTrasporto){
        this.tipoMezzoTrasporto=tipoMezzoTrasporto;
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
    public Stato getStato() {
        return stato;
    }

    public void setStato(Stato stato) {
        this.stato = stato;
    }
    public List<Tratta> getTratte() {
        return tratte;
    }

    @Override
    public String toString() {
        return "MezzoTrasporto{" +
                "id=" + id +
                ", tipoMezzoTrasporto=" + tipoMezzoTrasporto +
                ", capienza=" + capienza +
                ", stato=" + stato +
                ", stati=" + stati +
                ", tratte=" + tratte +
                '}';
    }
}
