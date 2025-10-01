package amedeo.mignano;

import amedeo.mignano.dao.*;
import amedeo.mignano.entities.*;
import jakarta.persistence.*;

import java.util.Scanner;
import java.util.UUID;

public class Application {

    public static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("bw4pu");

    public static void main(String[] args) {
        EntityManager em = emf.createEntityManager();
        MezzoTrasportoDAO mtd = new MezzoTrasportoDAO(em);
        StatoMezzoTrasportoDAO smtd = new StatoMezzoTrasportoDAO(em);
        UserDAO us = new UserDAO(em);
        VenditoreDAO dao = new VenditoreDAO();
        TicketDao tic = new TicketDao(em);
        em.getTransaction().begin();
        em.persist(new Rivenditore(UUID.randomUUID()));
        em.getTransaction().commit();
        Scanner scanner = new Scanner(System.in);

        //dao.menu();
        //us.newUser();
        //tic.readCardAndValidate();

        em.close();
        emf.close();
    }
}
