package amedeo.mignano.dao;

import amedeo.mignano.entities.Ticket;
import amedeo.mignano.entities.Abbonamento;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class TicketDao {
    private final EntityManager em;

    public TicketDao(EntityManager em){
        this.em = em;
    }

    public void salva(Ticket ticket){
        EntityTransaction tr = em.getTransaction();
        tr.begin();
        em.persist(ticket);
        tr.commit();
    }

    //Verifica rapida della validità di un abbonamento in base al numero di tessera dell'utente
    public void readCardAndValidate() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("\nInserisci l'ID della tessera (UUID): ");
        String input = scanner.nextLine();

        try {
            UUID cardId = UUID.fromString(input);
            subValidity(cardId);
        } catch (IllegalArgumentException e) {
            System.out.println("UUID non valido. Assicurati di usare il formato corretto (es. 123e4567-e89b-12d3-a456-426614174000).");
        }
    }

    public void subValidity(UUID cardId) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            TypedQuery<Abbonamento> query = em.createQuery(
                    "SELECT a FROM Abbonamento a " +
                            "JOIN a.card c " +
                            "WHERE c.id = :card_id",
                    Abbonamento.class).setParameter("card_id", cardId);

            List<Abbonamento> sub = query.getResultList();

            if (sub != null) {
                System.out.println(sub);
            } else {
                System.out.println("Nessun abbonamento trovato per la card: " + cardId);
            }

            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        }
    }
}
