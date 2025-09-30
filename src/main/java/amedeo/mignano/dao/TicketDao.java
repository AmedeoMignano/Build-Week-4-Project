package amedeo.mignano.dao;

import amedeo.mignano.entities.*;
import amedeo.mignano.entities.enums.Tipologia;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;

public class TicketDao {
    private final EntityManager em;

    public TicketDao(EntityManager em){
        this.em = em;
    }

    public void salvaTicket(Ticket ticket){
        EntityTransaction tr = em.getTransaction();
        tr.begin();
        em.persist(ticket);
        tr.commit();
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


}
