package amedeo.mignano.dao;

import amedeo.mignano.entities.*;
import amedeo.mignano.entities.enums.Tipologia;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;

public class TicketDao {
    private final EntityManager em;

    private final Scanner scanner = new Scanner(System.in);

    public TicketDao(EntityManager em){

        this.em = em;
    }

    public void salvaTicket(Ticket ticket){
        EntityTransaction tr = em.getTransaction();
        tr.begin();
        em.persist(ticket);
        tr.commit();
    }

    //Verifica rapida della validità di un abbonamento in base al numero di tessera dell'utente
    public void readCardAndValidate() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("\nInserisci l'ID della tessera (UUID): ");
        String input = scanner.nextLine();

        try {
            UUID cardId = UUID.fromString(input);
            subValidity(cardId);
        } catch (IllegalArgumentException e) {
            System.out.println("UUID non valido. Assicurati di usare il formato corretto (es. 123e4567-e89b-12d3-a456-426614174000).");
        }
    }

    public void subValidity(UUID cardId) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            TypedQuery<Abbonamento> query = em.createQuery(
                    "SELECT a FROM Abbonamento a " +
                            "JOIN a.card c " +
                            "WHERE c.id = :card_id",
                    Abbonamento.class).setParameter("card_id", cardId);

            List<Abbonamento> sub = query.getResultList();

            if (sub != null) {
                System.out.println(sub);
            } else {
                System.out.println("Nessun abbonamento trovato per la card: " + cardId);
            }

            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        }
    }

    public  void salvaBiglietto(Biglietto biglietto){
        EntityTransaction tr = em.getTransaction();
        tr.begin();
        em.persist(biglietto);
        tr.commit();
    }
    public void salvaAbbonamento(Abbonamento abbonamento){
        EntityTransaction tr = em.getTransaction();
        tr.begin();
        em.persist(abbonamento);
        tr.commit();
    }

    public void creaTicket(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Scegli cosa acquistare:");
        System.out.println("1) Biglietto");
        System.out.println("2) Abbonamento");
        String input = scanner.nextLine();
        if(input.equals("1")){
            creaBiglietto();}
        else if (input.equals("2")) {
            creaAbbonamento();
        }
        else {
            System.out.println("Scelta Non Valida");
        }
    }

    public void creaBiglietto(){
       try {
           Scanner scanner = new Scanner(System.in);
           System.out.println("Hai selezionato 1, segui le istruzioni per acquistare biglietto");
           System.out.println("Inserisci Id Venditore");
           String venditoreId = scanner.nextLine();
           Optional<Venditore> optvenditore = Optional.ofNullable(em.find(Venditore.class, UUID.fromString(venditoreId)));
           if(optvenditore.isEmpty()) {
               System.out.println("Venditore non presente");
               return;
           }
           Venditore venditore = optvenditore.get();

           System.out.println("Inserisci Id Utente");
           String utenteId = scanner.nextLine();
           Optional<User> optUser = Optional.ofNullable(em.find(User.class, UUID.fromString(utenteId)));
           if(optUser.isEmpty()){
               System.out.println("Utente non trovato");
               return;
           }
           User user = optUser.get();

           LocalDate now = LocalDate.now();

           Biglietto biglietto = new Biglietto(venditore, now, user);
           salvaBiglietto(biglietto);
       }catch (Exception e){
           System.out.println(e.getMessage());
       }

    }

    public void creaAbbonamento(){
       try {
           System.out.println("Hai selezionato 2, segui le istruzioni per acquistare abbonamento");
           Scanner scanner = new Scanner(System.in);
           System.out.println("Inserisci Id Venditore");
           String venditoreId = scanner.nextLine();
           Optional<Venditore> optvenditore = Optional.ofNullable(em.find(Venditore.class, UUID.fromString(venditoreId)));
           if(optvenditore.isEmpty()){
               System.out.println("Venditore non presente");
               return;
           }
           Venditore venditore = optvenditore.get();

           LocalDate now = LocalDate.now();

           System.out.println("Seleziona tipologia abbonamento: SETTIMANALE/MENSILE");
           String tipo = scanner.nextLine();

           if (!tipo.equalsIgnoreCase("SETTIMANALE") && !tipo.equalsIgnoreCase("MENSILE")){
               System.out.println("Scelta non valida, seleziona tra SETTIMANALE e MENSILE");
               return;
           }
           Tipologia tipologia = Tipologia.valueOf(tipo.toUpperCase());


           LocalDate dataScadenza = tipo.equalsIgnoreCase("SETTIMANALE") ?
                   now.plusWeeks(1) : now.plusMonths(1);

           System.out.println("Inserisci Id tessera");
           String cardId = scanner.nextLine();
           Optional<Card> optCard = Optional.ofNullable(em.find(Card.class, UUID.fromString(cardId)));
           if (optCard.isEmpty()) {
               System.out.println("Tessera non trovata");
               return;
           }
           Card tessera = optCard.get();
           if (tessera.isExpired()){
               System.out.println("La tessera è scaduta, vuoi rinnovarla? Y/N");
               String input = scanner.nextLine();
               switch (input){
                   case "Y" ->  {
                       System.out.println("Rinnovo Tessera in corso...");
                       tessera.setActivation_date(LocalDate.now());
                       tessera.setDue_date(LocalDate.now().plusYears(1));
                       System.out.println("Tessera Rinnovata con successo");
                       EntityTransaction tx = em.getTransaction();
                       tx.begin();
                       em.merge(tessera);
                       tx.commit();
                   }
                   case "N" -> {
                       System.out.println("Impossibile acquistare abbonamento con tessera scaduta");
                       return;
                   }
                   default -> System.out.println("Input Non Valido");
               }

           }

           Abbonamento abbonamento = new Abbonamento(venditore, now, dataScadenza, tipologia, tessera);
           salvaAbbonamento(abbonamento);
       }catch (Exception e){
           System.out.println(e.getMessage());
       }


    }

    // counter per biglietti in determinato intervallo di tempo
    public long countByPeriodo(LocalDate start, LocalDate end) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT (t) FROM Ticket t WHERE t.dataVendita BETWEEN :start and :end", Long.class);

        query.setParameter("start", start);
        query.setParameter("end", end);
        return query.getSingleResult();
    }

    //counter biblietti stesso intervallo ma di un solo venditore
    public List<Object[]> countPerVenditore(LocalDate start,LocalDate end) {
        TypedQuery<Object[]> query = em.createQuery(
                "SELECT v, COUNT(t) " +
                        "FROM Ticket t JOIN t.venditore v " +
                        "WHERE t.dataVendita BETWEEN :start AND :end" +
                        "GROUP BY v",
                Object[].class
        );

        query.setParameter("start", start);
        query.setParameter("end", end);
        return query.getResultList();
    }

    // biglietti validati su un mezzo in un determinato periodo di tempo

    // biglietti validati su un determinato mezzo
    public long countBigliettiValidatiByMezzo(int mezzoId) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(b) FROM BIGLIETTO b" +
                        "WHERE b.validazione = true AND b.mezzo.id = :mezzoID",
                Long.class

        );
        query.setParameter("mezzoId", mezzoId);
        return query.getSingleResult();
    }

    // biglietti calidati in un determinato periodo
    public long countBigliettiValidatiInPeriodo( LocalDate start, LocalDate end) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(b) FROM Biglietto b " +
                        "WHERE b.validazioe = true " +
                        "AND b.mezzo.id = :mezzoID " +
                        "AND b.dataValidazione BETWEEN :start AND :end",
                Long.class
        );

        query.setParameter("start",start);
        query.setParameter("end",end);
        return query.getSingleResult();

    }

    // biglietti validati su un determinato mezzo in un determinato periodo

    public long countBigliettiValidatiByMezzoAndPeriodo(int mezzoId, LocalDate start, LocalDate end) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(b) FROM Biglietto b " +
                        "WHERE b.validazione = true " +
                        "AND b.dataValidazione BETWEEN :start AND :end",
                Long.class
        );
        query.setParameter("mezzoId", mezzoId);
        query.setParameter("start", start);
        query.setParameter("end", end);
        return query.getSingleResult();
    }

    // menu statistiche a console
    public void menustatistiche() {
        boolean running = true;
        while (running) {
            System.out.println("Menu per statistiche biglietti");
            System.out.println("1. conta biglietti validati per mezzo");
            System.out.println("2. conta biglietti validati per periodo");
            System.out.println("3. conta biglietti validati per mezzo in un periodo");
            System.out.println("0. esci ");
            System.out.println("scelta: ");

            try {
                int scelta = Integer.parseInt(scanner.nextLine().trim());
                switch (scelta) {
                    case 1 -> contaPerMezzo();
                    case 2 -> contaPerPeriodo();
                    case 3 -> contaPerMezzoEPeriodo();
                    case 0 -> {
                        running = false;
                        System.out.println("uscita dal menu statistiche");
                    }
                    default -> System.out.println("scelta non valida riprova");
                }
            } catch (Exception e) {
                System.out.println("Errore input " + e.getMessage());
            }
        }
    }

    // metodi per menu statistiche
    private void contaPerMezzo() {
        System.out.println("inserisci ID del mezzo");
        int mezzoId = Integer.parseInt(scanner.nextLine());
        long result = countBigliettiValidatiByMezzo(mezzoId);
        System.out.println("Biglietti validati sul mezzo " + mezzoId + ": " + result);
    }

    private void contaPerPeriodo() {
        System.out.println("inserisci data inizio (YYY-MM-DD): ");
        LocalDate start = LocalDate.parse(scanner.nextLine());
        System.out.println("inserisci data fine (YYY-MM-DD): ");
        LocalDate end = LocalDate.parse(scanner.nextLine());
        long result = countBigliettiValidatiInPeriodo(start, end);
        System.out.println("biglietti validati tra " + start + "e " + end + ": " + result);
    }

    private void contaPerMezzoEPeriodo() {
        System.out.println("inserisci id del mezzo: ");
        int mezzoId = Integer.parseInt(scanner.nextLine());
        System.out.println("inserisci data inizio (YYY-MM-DD): ");
        LocalDate start = LocalDate.parse(scanner.nextLine());
        System.out.println("inserisci data fine (YYY-MM-DD): ");
        LocalDate end = LocalDate.parse(scanner.nextLine());
        long result = countBigliettiValidatiByMezzoAndPeriodo(mezzoId, start, end);
        System.out.println("biglietti validati sul mezzo " + mezzoId +
                "tra " + start + "e " + end + ": " + result);
    }
}
