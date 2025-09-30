package amedeo.mignano;

import amedeo.mignano.dao.*;
import amedeo.mignano.entities.*;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

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
        Scanner scanner = new Scanner(System.in);
        UserDAO us = new UserDAO(em);
        em.getTransaction().begin();
        em.persist(new Rivenditore(UUID.randomUUID()));
        em.getTransaction().commit();
        em.close();
        


//        VenditoreDAO dao = new VenditoreDAO();
//        dao.menu();
//
//        try {
//            String name;
//            while (true) {
//                System.out.print("Inserisci il nome utente: ");
//                name = scanner.nextLine().trim();
//                if (!name.matches("^[A-Za-zÀ-ÖØ-öø-ÿ]+$")) {
//                    System.out.println("Il nome non può contenere numeri o caratteri speciali.\n");
//                } else {
//                    break;
//                }
//            }
//
//            String surname;
//            while (true) {
//                System.out.print("Inserisci il cognome utente: ");
//                surname = scanner.nextLine().trim();
//                if (!surname.matches("^[A-Za-zÀ-ÖØ-öø-ÿ]+$")) {
//                    System.out.println("Il cognome non può contenere numeri o caratteri speciali.\n");
//                } else {
//                    break;
//                }
//            }
//
//            LocalDate born_date;
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//            while (true) {
//                System.out.print("Inserisci la data di nascita (YYYY-MM-DD): ");
//                String date = scanner.nextLine().trim();
//
//                if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
//                    System.out.println("Formato non valido. Usa YYYY-MM-DD.\n");
//                    continue;
//                }
//
//                try {
//                    born_date = LocalDate.parse(date, formatter);
//                    int year = born_date.getYear();
//                    if (year < 1950 || year > 2025) {
//                        System.out.println("L'anno deve essere compreso tra il 1950 e l'anno corrente!\n");
//                        continue;
//                    }
//                    break;
//                } catch (DateTimeParseException e) {
//                    System.out.println("Data non valida. Verifica giorno e mese.\n");
//                }
//            }
//
//            User user = new User(name, surname, born_date);
//            Card card = new Card(LocalDate.now(), LocalDate.now().plusYears(1), false);
//            user.setCard(card);
//
//            us.save(user);
//            System.out.println(user);
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
