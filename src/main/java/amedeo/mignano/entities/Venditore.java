package amedeo.mignano.entities;


import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table (name = "venditore")
public abstract class Venditore {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "ticket_emesso_id", nullable = false)
    private  UUID ticketEmessoId;

    // costruttore vuoto
    public Venditore() {}

    public Venditore(UUID ticketEmessoId) {
        this.id = UUID.randomUUID();
        this.ticketEmessoId= ticketEmessoId;
    }

    public UUID getId() {
        return id;
    }

//    public void setId(UUID id) {
//        this.id = id;
//    }

    public UUID getTicketEmessoId() {
        return ticketEmessoId;
    }

//    public void setTickeEmessoId(UUID ticketEmessoId) {
//        this.ticketEmessoId = ticketEmessoId;
//    }

    @Override
    public String toString() {
        return "Venditore{" +
                "id=" + id +
                ", ticketEmessoId=" + ticketEmessoId +
                '}';
    }
}
