package amedeo.mignano.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "born_date")
    private LocalDate born_date;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Card card;

    public User() {}

    public User(String name, String surname, LocalDate born_date) {
        this.name = name;
        this.surname = surname;
        this.born_date = born_date;
    }

    public UUID getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public LocalDate getBorn_date() { return born_date; }
    public void setBorn_date(LocalDate born_date) { this.born_date = born_date; }

    public Card getCard() { return card; }
    public void setCard(Card card) {
        this.card = card;
        if (card != null) {
            card.setUser(this);
        }
    }

    @Override
    public String toString() {
        return "ID: " + id + "\n" +
                "Name: " + name + "\n" +
                "Surname: " + surname + "\n" +
                "Born date: " + born_date + "\n" +
                "Card ID: " + card + "\n";
    }
}
