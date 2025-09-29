package amedeo.mignano.dao;

import amedeo.mignano.entities.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class UserDAO {
    private final EntityManager em;

    public UserDAO(EntityManager em) {
        this.em = em;
    }

    public void save(User user) {
        try {
            EntityTransaction t = em.getTransaction();
            t.begin();
            em.persist(user);
            t.commit();
            System.out.println("\n Utente " + user.getName() + " aggiunto con successo!\n");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
