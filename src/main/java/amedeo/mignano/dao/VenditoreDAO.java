package amedeo.mignano.dao;

import amedeo.mignano.Application;
import amedeo.mignano.entities.Distributore;
import amedeo.mignano.entities.Rivenditore;
import amedeo.mignano.entities.Venditore;
import jakarta.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class VenditoreDAO {

    //private final List<Venditore> venditori = new ArrayList<>();
    private final Scanner scanner = new Scanner(System.in);

    public void menu() {
        boolean running = true;
        while (running) {
            System.out.println("menu venditori");
            System.out.println("1. Aggiungi rivenditore");
            System.out.println("2. Aggiungi distributore");
            System.out.println("3. Mostra tutti i venditori");
            System.out.println("0. Esci");
            System.out.println("scelta: ");

            try {
                int scelta = Integer.parseInt(scanner.nextLine().trim());
                switch (scelta) {
                    case 1 -> aggiungiRivenditore();
                    case 2 -> aggiungiDistributore();
                    case 3 -> mostraVenditori();
                    case 0 ->  {
                        running = false;
                        System.out.println("uscita dal manu");
                    }
                    default -> System.out.println("scelta non valida riprova");
                }
            } catch (NumberFormatException e) {
                System.out.println("input non valido. inserisci un numero");
            }
        }
    }

    private void aggiungiRivenditore() {
//        try {
//            System.out.println("inserisci id rivenditore (long): ");
//            Long idRiv = Long.parseLong(scanner.nextLine().trim());
//
//            UUID ticketId = UUID.randomUUID();
//
//            Rivenditore r = new Rivenditore(ticketId, idRiv);
//            venditori.add(r);
//            System.out.println("rivenditore aggiunto: " + r);
//        } catch (NumberFormatException e) {
//            System.out.println("ID non valido");
//        }
        EntityManager em = Application.emf.createEntityManager();
        em.getTransaction().begin();

        Rivenditore r = new Rivenditore(UUID.randomUUID());
        em.persist(r);

        em.getTransaction().commit();
        em.close();

        System.out.println("rivenditore aggiunto" + r);
    }

    private void aggiungiDistributore() {
//        try {
//            System.out.println("inserisci id Distributore (long): ");
//            Long idDist = Long.parseLong(scanner.nextLine().trim());
//
//            System.out.println("distributore attivo?: ");
//            Boolean attivo = Boolean.parseBoolean(scanner.nextLine().trim());
//
//            UUID ticketId = UUID.randomUUID();
//            Distributore d = new Distributore(ticketId, idDist, attivo);
//            venditori.add(d);
//            System.out.println("Distributore aggiunto: " + d);
//        } catch (NumberFormatException e) {
//            System.out.println("id non valido");
//        }

        System.out.println("distributore attivo?: ");
        boolean attivo = Boolean.parseBoolean(scanner.nextLine().trim());

        EntityManager em = Application.emf.createEntityManager();
        em.getTransaction().begin();

        Distributore d = new Distributore(UUID.randomUUID(), attivo);
        em.persist(d);

        em.getTransaction().commit();
        em.close();

        System.out.println("distributore aggiunto" + d);
    }

    private void mostraVenditori() {
//        if(venditori.isEmpty()) {
//            System.out.println("nessun venditore disponibile");
//            return;
//        }
//        System.out.println("elenco venditori");
//        for (Venditore v : venditori) {
//            System.out.println(v);
//        }

        EntityManager em = Application.emf.createEntityManager();

        List<Venditore> venditori = em.createQuery("SELECT v FROM Venditore v", Venditore.class).getResultList();
        em.close();

        if(venditori.isEmpty()) {
            System.out.println("nessun venditore trovato");
        } else {
            System.out.println("elenco venditori");
            venditori.forEach(System.out::println);
        }

      }
}
