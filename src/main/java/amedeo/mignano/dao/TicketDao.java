package amedeo.mignano.dao;

import amedeo.mignano.entities.Ticket;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

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

    // counter per biglietti in determinato intervallo di tempo
    public long countByPeriodo(LocalDate start, LocalDate end) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COuNT (t) FROM Ticket t WHERE t.datavendita BETWEEN :start and :end", Long.class);

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
}
