package amedeo.mignano.entities;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

import java.util.Random;
import java.util.UUID;

@Entity
@Table(name = "distributori")
@PrimaryKeyJoinColumn(name = "id")

public class Distributore extends Venditore{

    @Column(name ="distributore_id", nullable = false)
    private Long distributoreId;

    @Column(name = "attivo", nullable = false)
    private boolean attivo;

    public Distributore() {}

    public Distributore(UUID ticketEmessoId, boolean attivo) {
        super(ticketEmessoId);
        this.distributoreId= Math.abs( new Random(). nextLong());
        this.attivo = attivo;
    }

    public Long getDistributoreId() {
        return distributoreId;
    }

    public void setDistributoreId(Long distributoreId) {
        this.distributoreId = distributoreId;
    }

    public boolean isAttivo() {
        return attivo;
    }

    public void setAttivo(boolean attivo) {
        this.attivo = attivo;
    }

    @Override
    public String toString() {
        return "Distributore{" +
                "id=" + getId() +
                ", ticketEmessoId=" + getTicketEmessoId() +
                ", distributoreId=" + distributoreId +
                ", attivo=" + attivo +
                '}';
    }
}
