package amedeo.mignano.dao;

import amedeo.mignano.entities.Card;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.time.LocalDate;
import java.util.UUID;

public class CardDAO {
    private final EntityManager em;

    public CardDAO(EntityManager em) {
        this.em = em;
    }

        while (true) {
            System.out.print("Inserisci l'ID della tessera (UUID) o premi 0 per uscire: ");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("0")) {
                System.out.println("\nUscita dall'aggiornamento card.");
                break;
            }

            UUID cardId;
            try {
                cardId = UUID.fromString(input);
            } catch (IllegalArgumentException e) {
                System.out.println("UUID non valido (es. 123e4567-e89b-12d3-a456-426614174000).");
                continue;
            }

            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();

                Card card = em.find(Card.class, cardId);
                if (card == null) {
                    System.out.println("\nNessuna card trovata con ID: " + cardId);
                    tx.rollback();
                    continue;
                } else {
                    card.setDue_date(LocalDate.now().plusYears(1));
                    card.setRenewal_date(LocalDate.now());
                    card.setExpired(false);

                    System.out.println("\nCard aggiornata correttamente:\n" + card);
                }

                tx.commit();
                break;

            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                e.printStackTrace();
                break;
            }
        }
    public void salvaCard(Card card) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.persist(card);
        tx.commit();
    }

    public void aggiornaCard(Card card) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.merge(card);
        tx.commit();
    }

    public void rinnovaCard(Card card) {
        card.setDue_date(LocalDate.now().plusYears(1));
        card.setRenewal_date(LocalDate.now());
        card.setExpired(false);
        aggiornaCard(card);
    }
}
