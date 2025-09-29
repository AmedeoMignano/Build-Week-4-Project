package amedeo.mignano;

import amedeo.mignano.dao.VenditoreDAO;
import amedeo.mignano.entities.Rivenditore;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.UUID;

public class Application {

    public static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("bw4pu");
    public static void main(String[] args) {
        var em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(new Rivenditore(UUID.randomUUID()));
        em.getTransaction().commit();
        em.close();


        VenditoreDAO dao = new VenditoreDAO();
        dao.menu();
    }
}
