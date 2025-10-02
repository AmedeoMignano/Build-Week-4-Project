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
        System.out.println("Rivenditore aggiunto con id: " + r.getId());
        return r;
    }

    public void aggiungiDistributore(Distributore d) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.persist(d);
        tx.commit();
        System.out.println("Distributore aggiunto con id: " + d.getId());
    }

    public void mostraVenditori() {
        List<Venditore> venditori = em.createQuery("SELECT v FROM Venditore v", Venditore.class)
                .getResultList();

        if (venditori.isEmpty()) {
            System.out.println("Nessun venditore trovato.");
        } else {
            System.out.println("\n--- Elenco venditori ---");
            venditori.forEach(System.out::println);
        }
    }
}
