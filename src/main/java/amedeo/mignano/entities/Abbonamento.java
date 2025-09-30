package amedeo.mignano.entities;

import amedeo.mignano.entities.enums.Tipologia;
import jakarta.persistence.*;


import java.time.LocalDate;

@Entity
@Table(name = "abbonamenti")
public class Abbonamento extends Ticket{
    @Enumerated(EnumType.STRING)
    private Tipologia tipologia;
    @ManyToOne
    @JoinColumn(name = "card_id")
    private Card card;

    public Abbonamento(){}

    public Abbonamento(Venditore venditore, LocalDate dataVendita, LocalDate dataScadenza, Tipologia tipologia, Card card) {
        super(venditore, dataVendita, dataScadenza);
        this.tipologia = tipologia;
        this.card = card;
    }

    public Tipologia getTipologia() {
        return tipologia;
    }

    public void setTipologia(Tipologia tipologia) {
        this.tipologia = tipologia;
    }

    public Card getCard() {
        return card;
    }

    @Override
    public String toString() {
        return "Abbonamento{" +
                "tipologia=" + tipologia +
                ", card=" + card +
                ", id=" + id +
                ", dataVendita=" + dataVendita +
                ", dataValidazione=" + dataValidazione +
                ", dataScadenza=" + dataScadenza +
                '}';
    }
}
