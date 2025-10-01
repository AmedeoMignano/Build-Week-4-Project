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

    public void menu() {
        boolean running = true;
        while (running) {
            System.out.println("\n--- Menu Venditori ---");
            System.out.println("1. Aggiungi rivenditore");
            System.out.println("2. Aggiungi distributore");
            System.out.println("3. Mostra tutti i venditori");
            System.out.println("0. Esci");
            System.out.print("Scelta: ");

            try {
                int scelta = Integer.parseInt(scanner.nextLine().trim());
                switch (scelta) {
                    case 1 -> aggiungiRivenditore();
                    case 2 -> aggiungiDistributore();
                    case 3 -> mostraVenditori();
                    case 0 -> {
                        running = false;
                        System.out.println("Uscita dal menu venditori.");
                    }
                    default -> System.out.println("Scelta non valida, riprova.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Input non valido, inserisci un numero.");
            }
        }
    }

    public void aggiungiRivenditore() {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        Rivenditore r = new Rivenditore();
        em.persist(r);
        tx.commit();
        System.out.println("Rivenditore aggiunto con id: " + r.getId());
    }

    public void aggiungiDistributore() {
        System.out.print("Distributore attivo? (true/false): ");
        boolean attivo = Boolean.parseBoolean(scanner.nextLine().trim());

        EntityTransaction tx = em.getTransaction();

        tx.begin();

        Distributore d = new Distributore();
        d.setAttivo(attivo); // importante settare i campi letti da input
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
            System.out.println("--- Elenco venditori ---");
            venditori.forEach(System.out::println);
        }
    }
}
