package amedeo.mignano.dao;

import amedeo.mignano.entities.Ticket;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

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
}
