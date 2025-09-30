package amedeo.mignano.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "tickets")
public abstract class Ticket {
    @Id
    @Column(name = "ticket_id", nullable = false)
    @GeneratedValue
    protected UUID id;

    @ManyToOne
    @JoinColumn(name = "venditore_id")
    private Venditore venditore;

    @Column(name = "data_vendita")
    protected LocalDate dataVendita;

    @Column(name = "data_validazione")
    protected LocalDate dataValidazione;

    @Column(name = "data_scadenza")
    protected LocalDate dataScadenza;

    public Ticket(){}
    public Ticket(Venditore venditore, LocalDate dataVendita, LocalDate dataValidazione, LocalDate dataScadenza){
        this.venditore = venditore;
        this.dataVendita = dataVendita;
        this.dataValidazione = dataValidazione;
        this.dataScadenza = dataScadenza;
    }
    public Ticket(Venditore venditore, LocalDate dataVendita, LocalDate dataScadenza){
        this.venditore = venditore;
        this.dataVendita = dataVendita;
        this.dataScadenza = dataScadenza;
    }

    public Ticket(Venditore venditore, LocalDate dataVendita) {
        this.venditore = venditore;
        this.dataVendita = dataVendita;
    }

    public UUID getId() {
        return id;
    }

    public LocalDate getDataVendita() {
        return dataVendita;
    }

    public void setDataVendita(LocalDate dataVendita) {
        this.dataVendita = dataVendita;
    }

    public LocalDate getDataValidazione() {
        return dataValidazione;
    }

    public void setDataValidazione(LocalDate dataValidazione) {
        this.dataValidazione = dataValidazione;
    }

    public LocalDate getDataScadenza() {
        return dataScadenza;
    }

    public void setDataScadenza(LocalDate dataScadenza) {
        this.dataScadenza = dataScadenza;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", dataVendita=" + dataVendita +
                ", dataValidazione=" + dataValidazione +
                ", dataScadenza=" + dataScadenza +
                '}';
    }
}
