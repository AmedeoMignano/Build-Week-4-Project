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

import java.time.LocalDate;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;

public class TicketDao {
    private final EntityManager em;

    public TicketDao(EntityManager em){

        this.em = em;
    }

    public void salvaTicket(Ticket ticket){
        EntityTransaction tr = em.getTransaction();
        tr.begin();
        em.persist(ticket);
        tr.commit();
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



    //counter biblietti stesso intervallo ma di un solo venditore
    public List<Object[]> countPerVenditore(LocalDate start,LocalDate end) {
        TypedQuery<Object[]> query = em.createQuery(
                "SELECT v, COUNT(t) " +
                        "FROM Ticket t JOIN t.venditore v " +
                        "WHERE t.dataVendita BETWEEN :start AND :end" +
                        "GROUP BY v",
                Object[].class
        );

        query.setParameter("start", start);
        query.setParameter("end", end);
        return query.getResultList();

    }

    public EntityManager getEntityManager() {
        return em;
    }
}
