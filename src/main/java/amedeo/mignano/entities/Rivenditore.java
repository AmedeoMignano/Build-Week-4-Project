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

    //costruttore vuoto
    public Rivenditore() {}

    //costruttore per rivenditori

    @Override
    public String toString() {
        return "Rivenditore{" +
                "id=" + getId() +
                '}';
    }
}
