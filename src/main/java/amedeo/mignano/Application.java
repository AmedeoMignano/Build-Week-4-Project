package amedeo.mignano;

import amedeo.mignano.dao.MezzoTrasportoDAO;
import amedeo.mignano.dao.StatoMezzoTrasportoDAO;
import amedeo.mignano.dao.TrattaDAO;
import amedeo.mignano.dao.VenditoreDAO;
import amedeo.mignano.entities.Rivenditore;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.UUID;

public class Application {

    public static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("bw4pu");
    public static void main(String[] args) {
        EntityManager em = emf.createEntityManager();
        MezzoTrasportoDAO mtd = new MezzoTrasportoDAO(em);
        StatoMezzoTrasportoDAO smtd = new StatoMezzoTrasportoDAO(em);
        TrattaDAO td = new TrattaDAO(em);
        
        em.getTransaction().begin();
        em.persist(new Rivenditore(UUID.randomUUID()));
        em.getTransaction().commit();
        em.close();


        VenditoreDAO dao = new VenditoreDAO();
        dao.menu();
    }
}
