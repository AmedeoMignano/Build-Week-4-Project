package amedeo.mignano;

import amedeo.mignano.dao.*;
import amedeo.mignano.entities.*;
import jakarta.persistence.*;
import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Application {

    public static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("bw4pu");

    public static void main(String[] args) {
        EntityManager em = emf.createEntityManager();
        MezzoTrasportoDAO mtd = new MezzoTrasportoDAO(em);
        StatoMezzoTrasportoDAO smtd = new StatoMezzoTrasportoDAO(em);
        UserDAO us = new UserDAO(em);
        VenditoreDAO dao = new VenditoreDAO(em);
        TrattaDAO td = new TrattaDAO(em);
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
//
//            // creazione user e card
//            User user = new User(name, surname, born_date);
//            Card card = new Card(LocalDate.now(), LocalDate.now().plusYears(1), false);
//            user.setCard(card);
//
//            us.save(user);
//            System.out.println(user);
//
//            //counter biglietti ticketDao
//            System.out.println("ricerca ticket per intervallo date");
//            LocalDate starDate = null;
//            LocalDate endDate = null;
//
//            while (starDate == null) {
//                System.out.println("inserisci la data di inizio (YYY-MM-DD):");
//                String input = scanner.nextLine().trim();
//                try {
//                    starDate = LocalDate.parse(input, formatter);
//                } catch (DateTimeParseException e) {
//                    System.out.println("data non valida riprova");
//                }
//            }
//
//            while (endDate == null) {
//                System.out.println("inserisci data fine (YYY-MM-DD): ");
//                String input = scanner.nextLine().trim();
//                try {
//                    endDate = LocalDate.parse(input, formatter);
//                    if(endDate.isBefore(starDate)) {
//                        System.out.println("la data di fine non può essere precedente a quella di inizio");
//                    endDate = null;
//                    }
//                } catch (DateTimeParseException e) {
//                    System.out.println("data non valida riprovare");
//                }
//            }
//
//            // totale ticket venduti
//            long totaleTicket = ticketDao.countByPeriodo(starDate, endDate);
//            System.out.println("numero ticket venduti per venditore tra " + starDate + " e " + endDate + ":" +totaleTicket);
//
//
//            //ticket per venditore
//            List<Object[]> risultati = ticketDao.countPerVenditore(starDate, endDate);
//            System.out.println("vendite per venditore:");
//            for (Object[] row: risultati) {
//                System.out.println("venditore " + row[0] + "ticket venduti:" + row[1]);
//            }
//
//        } catch (Exception e) {
//            System.out.println("\nInserimento non valido: " + e.getMessage());
//        } finally {
//            scanner.close();
//            em.close();
//            emf.close();
//        }
    }
}
