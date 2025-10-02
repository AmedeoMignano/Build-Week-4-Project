package amedeo.mignano.dao;

import amedeo.mignano.entities.*;
import amedeo.mignano.entities.enums.Tipologia;
import amedeo.mignano.exceptions.InputErratoException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;

public class TicketDao {
    private final EntityManager em;

    private final Scanner scanner = new Scanner(System.in);

    public TicketDao(EntityManager em){

        this.em = em;
    }

    public void salvaTicket(Ticket ticket){
        EntityTransaction tr = em.getTransaction();
        tr.begin();
        em.persist(ticket);
        tr.commit();
    }

    public void aggiornaTicket(Ticket ticket) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.merge(ticket);
        tx.commit();
    }

    public void subValidity(UUID cardId) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            TypedQuery<Abbonamento> query = em.createQuery(
                    "SELECT a FROM Abbonamento a " +
                            "JOIN a.card c " +
                            "WHERE c.id = :card_id AND a.dataScadenza >= :now",
                    Abbonamento.class).setParameter("card_id", cardId).setParameter("now", LocalDate.now());

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

    public  void salvaBiglietto(Biglietto biglietto){
        EntityTransaction tr = em.getTransaction();
        tr.begin();
        em.persist(biglietto);
        tr.commit();
    }
    public void salvaAbbonamento(Abbonamento abbonamento){
        EntityTransaction tr = em.getTransaction();
        tr.begin();
        em.persist(abbonamento);
        tr.commit();
    }


    // counter per biglietti in determinato intervallo di tempo
    public long countByPeriodo(LocalDate start, LocalDate end) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT (t) FROM Ticket t WHERE t.dataVendita BETWEEN :start and :end", Long.class);

        query.setParameter("start", start);
        query.setParameter("end", end);
        return query.getSingleResult();
    }

    //counter biglietti stesso intervallo ma di un solo venditore
    public List<Object[]> countPerVenditorePerPeriodo(UUID venditoreId, LocalDate start, LocalDate end) {
        TypedQuery<Object[]> query = em.createQuery(
                "SELECT v, COUNT(t) " +
                        "FROM Ticket t JOIN t.venditore v " +
                        "WHERE v.id = :venditoreId " +
                        "AND t.dataVendita BETWEEN :start AND :end " +
                        "GROUP BY v",
                Object[].class
        );

        query.setParameter("venditoreId", venditoreId);
        query.setParameter("start", start);
        query.setParameter("end", end);

        return query.getResultList();
    }

    public List<Object[]> countPerVenditore(UUID venditoreId) {
        TypedQuery<Object[]> query = em.createQuery(
                "SELECT v, COUNT(t) " +
                        "FROM Ticket t JOIN t.venditore v " +
                        "WHERE v.id = :venditoreId " +
                        "GROUP BY v",
                Object[].class
        );

        query.setParameter("venditoreId", venditoreId);

        return query.getResultList();
    }



    // biglietti validati su un mezzo in un determinato periodo di tempo

    // biglietti validati su un determinato mezzo
    public long countBigliettiValidatiByMezzo(int mezzoId) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(b) FROM Biglietto b WHERE b.validazione = true AND b.mezzo.id = :mezzoId",
                Long.class

        );
        query.setParameter("mezzoId", mezzoId);
        return query.getSingleResult();
    }

    // biglietti calidati in un determinato periodo
    public long countBigliettiValidatiInPeriodo( LocalDate start, LocalDate end, int mezzoId) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(b) FROM Biglietto b " +
                        "WHERE b.validazione = true " +
                        "AND b.mezzo.id = :mezzoId " +
                        "AND b.dataValidazione BETWEEN :start AND :end",
                Long.class
        );
        query.setParameter("start",start);
        query.setParameter("end",end);
        query.setParameter("mezzoId", mezzoId);
        return query.getSingleResult();

    }

    public EntityManager getEntityManager() { return em; }
    // biglietti validati su un determinato mezzo in un determinato periodo

    public long countBigliettiValidati() {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(b) FROM Biglietto b WHERE b.validazione = true " ,
                Long.class
        );
        return query.getSingleResult();
    }
}
