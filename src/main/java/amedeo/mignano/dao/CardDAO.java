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
