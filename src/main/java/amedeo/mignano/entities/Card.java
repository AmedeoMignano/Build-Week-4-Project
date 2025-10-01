package amedeo.mignano.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "cards")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "activation_date")
    private LocalDate activation_date;

    @Column(name = "due_date")
    private LocalDate due_date;

    @Column(name = "expired")
    private boolean expired;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", unique = true)
    private User user;

    public Card() {}

    public Card(LocalDate activation_date, LocalDate due_date, boolean expired) {
        this.activation_date = activation_date;
        this.due_date = due_date;
        this.expired = expired;
    }

    public UUID getId() { return id; }

    public LocalDate getActivation_date() { return activation_date; }
    public void setActivation_date(LocalDate activation_date) { this.activation_date = activation_date; }

    public LocalDate getDue_date() { return due_date; }
    public void setDue_date(LocalDate due_date) { this.due_date = due_date; }

    public boolean isExpired() { return expired; }
    public void setExpired(boolean expired) { this.expired = expired; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    @Override
    public String toString() {
        return "Card ID: " + id + "\n" +
                "Activation Date: " + activation_date + "\n" +
                "Due Date: " + due_date + "\n" +
                "Expired: " + expired + "\n" +
                "User ID: " + user + "\n";
    }
}
