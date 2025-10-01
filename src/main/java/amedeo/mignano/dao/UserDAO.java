package amedeo.mignano.dao;

import amedeo.mignano.entities.*;
import jakarta.persistence.*;
import java.time.format.*;
import java.time.LocalDate;
import java.util.Scanner;

public class UserDAO {
    private final EntityManager em;

    public UserDAO(EntityManager em) {
        this.em = em;
    }

    public void newUser() {
        Scanner scanner = new Scanner(System.in);
        try {
            String name;
            while (true) {
                System.out.print("Inserisci il nome utente: ");
                name = scanner.nextLine().trim();
                if (!name.matches("^[A-Za-zÀ-ÖØ-öø-ÿ'\\-\\s]+$")) {
                    System.out.println("Il nome non può contenere numeri o caratteri speciali.\n");
                } else {
                    break;
                }
            }

            String surname;
            while (true) {
                System.out.print("Inserisci il cognome utente: ");
                surname = scanner.nextLine().trim();
                if (!surname.matches("^[A-Za-zÀ-ÖØ-öø-ÿ'\\-\\s]+$")) {
                    System.out.println("Il cognome non può contenere numeri o caratteri speciali.\n");
                } else {
                    break;
                }
            }

            LocalDate born_date;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            while (true) {
                System.out.print("Inserisci la data di nascita (YYYY-MM-DD): ");
                String date = scanner.nextLine().trim();

                if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    System.out.println("Formato non valido. Usa YYYY-MM-DD.\n");
                    continue;
                }

                try {
                    born_date = LocalDate.parse(date, formatter);
                    int year = born_date.getYear();
                    if (year < 1950 || year > LocalDate.now().getYear()) {
                        System.out.println("L'anno deve essere compreso tra il 1950 e l'anno corrente!\n");
                        continue;
                    }
                    break;
                } catch (DateTimeParseException e) {
                    System.out.println("Data non valida. Verifica giorno e mese.\n");
                }
            }

            User user = new User(name, surname, born_date);
            Card card = new Card(LocalDate.now(), LocalDate.now().plusYears(1), false, LocalDate.now());
            card.setUser(user);
            user.setCard(card);

            EntityTransaction t = em.getTransaction();
            try {
                t.begin();
                em.persist(user);
                t.commit();
                System.out.println("\nUtente " + user.getName() + " aggiunto con successo!\n");
            } catch (Exception e) {
                if (t.isActive()) t.rollback();
                throw e;
            }

        } catch (Exception e) {
            System.out.println("\nInserimento non valido: " + e.getMessage());
        }

        scanner.close();
    }
}
