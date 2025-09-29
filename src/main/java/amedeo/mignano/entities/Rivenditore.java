package amedeo.mignano.entities;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

import java.util.Random;
import java.util.UUID;

@Entity
@Table
@PrimaryKeyJoinColumn(name = "id")
public class Rivenditore extends Venditore {

    @Column(name = "rivenditore_id", nullable = false)
    private Long rivenditoreId;

    //costruttore vuoto
    public Rivenditore() {}

    //costruttore per rivenditori
    public Rivenditore(UUID ticketEmessoId) {
        super(ticketEmessoId);
        this.rivenditoreId= Math.abs(new Random().nextLong());
    }

    public Long getRivenditoreId() {
        return rivenditoreId;
    }

    @Override
    public String toString() {
        return "Rivenditore{" +
                "id=" + getId() +
                ", ticketEmessoId=" + getTicketEmessoId() +
                ", rivenditoreId=" + rivenditoreId +
                '}';
    }
}
