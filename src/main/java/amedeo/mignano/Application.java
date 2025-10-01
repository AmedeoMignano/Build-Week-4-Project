package amedeo.mignano;

import amedeo.mignano.dao.*;
import amedeo.mignano.entities.*;
import jakarta.persistence.*;
import java.util.*;

public class Application {

    public static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("bw4pu");

    public static void main(String[] args) {
        EntityManager em = emf.createEntityManager();
        Scanner scanner = new Scanner(System.in);

        //DAO
        MezzoTrasportoDAO mtd = new MezzoTrasportoDAO(em);
        StatoMezzoTrasportoDAO smtd = new StatoMezzoTrasportoDAO(em);
        UserDAO us = new UserDAO(em);
        CardDAO c = new CardDAO(em);
        VenditoreDAO dao = new VenditoreDAO(em);
        TrattaDAO td = new TrattaDAO(em);
        TicketDao tic = new TicketDao(em);
        TempiPercorrenzaDAO tpd = new TempiPercorrenzaDAO(em);

        //em.getTransaction().begin();
        //em.persist(new Rivenditore(UUID.randomUUID()));
        //em.getTransaction().commit();

        //METODI
        //dao.menu();
        //us.newUser();
        //tic.readCardAndValidate();
        c.cardUpdate();

        em.close();
        emf.close();
    }
}
