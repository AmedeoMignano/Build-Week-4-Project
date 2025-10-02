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

    @Column(name = "attivo")
    private boolean attivo;

    public Distributore() {}

    public Distributore(boolean attivo) {
        this.attivo = attivo;
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
                ", attivo=" + attivo +
                '}';
    }
}
