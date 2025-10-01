package amedeo.mignano.entities;


import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table (name = "venditore")
public abstract class Venditore {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;


    // costruttore vuoto
    public Venditore() {}


    public UUID getId() {
        return id;
    }


    @Override
    public String toString() {
        return "Venditore{" +
                "id=" + id +
                '}';
    }
}
