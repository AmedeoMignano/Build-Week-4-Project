package amedeo.mignano.dao;

import amedeo.mignano.entities.Distributore;
import amedeo.mignano.entities.Rivenditore;
import amedeo.mignano.entities.Venditore;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.Scanner;

public class VenditoreDAO {
    private final EntityManager em;
    private final Scanner scanner = new Scanner(System.in);

    public VenditoreDAO(EntityManager em){
        this.em = em;
    }

    public Rivenditore aggiungiRivenditore(Rivenditore r) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.persist(r);
        tx.commit();
        return r;
    }

    public void aggiungiDistributore(Distributore d) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.persist(d);
        tx.commit();
    }

    public void mostraVenditori() {
        List<Venditore> venditori = em.createQuery("SELECT v FROM Venditore v", Venditore.class)
                .getResultList();

        if (venditori.isEmpty()) {
            System.out.println(ROSSO + "Nessun venditore trovato." + RESET);
        } else {
            System.out.println(BLU + "\n--- Elenco venditori ---" + RESET);
            venditori.forEach(venditore -> System.out.println(VERDE + venditore + RESET));
        }
    }
    public static final String GIALLO = "\u001B[33m";
    public static  final String BLU = "\u001B[34m";
    public static final String VERDE = "\u001B[32m";
    public static  final String VIOLA = "\u001B[35m";
    public static final String ROSSO = "\u001B[31m";
    public static final String RESET = "\u001B[0m";

}
