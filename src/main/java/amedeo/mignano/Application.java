package amedeo.mignano;

import amedeo.mignano.dao.*;
import amedeo.mignano.entities.*;
import amedeo.mignano.entities.enums.Tipologia;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

import amedeo.mignano.entities.Rivenditore;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class Application {

    public static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("bw4pu");
    public static EntityManager em = emf.createEntityManager();
    public static Scanner scanner = new Scanner(System.in);
    public static UserDAO us = new UserDAO(em);
    public static TicketDao ticketDao = new TicketDao(em);
    public static VenditoreDAO vd = new VenditoreDAO(em);
    public static MezzoTrasportoDAO mtd = new MezzoTrasportoDAO(em);
    public static StatoMezzoTrasportoDAO smtd = new StatoMezzoTrasportoDAO(em);
    public static TrattaDAO td = new TrattaDAO(em);

    public static void main(String[] args) {

        //dao.menu();
        //us.newUser();
        //tic.readCardAndValidate();
        //c.cardUpdate();

        



        }




        // Metodi dei menu e delle creazioni

    public static void ticketMenu() {
        boolean run = true;
        while (true) {
            System.out.println("\n--- Menu Ticket ---");
            System.out.println("1. Crea Biglietto");
            System.out.println("2. Crea Abbonamento");
            System.out.println("0. Torna indietro");
            System.out.print("Scelta: ");
            String input = scanner.nextLine();
            switch (input) {
                case "1" -> creaBiglietto();
                case "2" -> creaAbbonamento();
                case "0" -> run = false;
                default -> System.out.println("Scelta non valida");
            }
        }
    }

    public static void creaBiglietto(){
        try {
            System.out.println("Inserisci l'id del venditore:");
            String venditoreId = scanner.nextLine();
            Optional<Venditore> optVend = Optional.ofNullable(em.find(Venditore.class, UUID.fromString(venditoreId)));
            if(optVend.isEmpty()){
                System.out.println("Venditore non presente");
                return;
            }
            Venditore venditore = optVend.get();

            LocalDate now = LocalDate.now();

            Biglietto biglietto = new Biglietto(venditore, now);
            ticketDao.salvaBiglietto(biglietto);
            System.out.println("Biglietto con id: " + biglietto.getId() + " acquistato con successo");
        }catch (IllegalArgumentException e){
            System.out.println("Inserisci un UUID valido");
            System.out.println("Es: 02ce3860-3126-42af-8ac7-c2a661134129"); }
    }

    public static void creaAbbonamento(){
        try {
            System.out.println("Inserisci l'id del venditore:");
            String venditoreId = scanner.nextLine();
            Optional<Venditore> optVend = Optional.ofNullable(em.find(Venditore.class, UUID.fromString(venditoreId)));
            if(optVend.isEmpty()){
                System.out.println("Venditore non presente");
                return;
            }
            Venditore venditore = optVend.get();

            System.out.println("Seleziona tipologia abbonamento (SETTIMANALE/MENSILE):");
            String tipo = scanner.nextLine();
            if(!tipo.equalsIgnoreCase("SETTIMANALE") && !tipo.equalsIgnoreCase("MENSILE")){
                System.out.println("Scelta non valida, scegli tra SETTIMANALE o MENSILE");
                return;
            }
            Tipologia tipologia = Tipologia.valueOf(tipo.toUpperCase());
            LocalDate now = LocalDate.now();
            LocalDate scadenza = tipo.equalsIgnoreCase("SETTIMANALE") ?
                    now.plusWeeks(1) : now.plusMonths(1);
            System.out.println("Inserisci Id tessera:");
            String cardId = scanner.nextLine();
            Optional<Card> optCard = Optional.ofNullable(
                    em.find(Card.class, UUID.fromString(cardId))
            );

            if (optCard.isEmpty()) {
                System.out.println("Tessera non trovata.");
                return;
            }

            Card tessera = optCard.get();
            if (tessera.isExpired()) {
                System.out.println("Tessera scaduta, vuoi rinnovarla? (Y/N)");
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("Y")) {
                    tessera.setActivation_date(LocalDate.now());
                    tessera.setDue_date(LocalDate.now().plusYears(1));
                    em.getTransaction().begin();
                    em.merge(tessera);
                    em.getTransaction().commit();
                    System.out.println("Tessera rinnovata.");
                } else {
                    System.out.println("Impossibile acquistare con tessera scaduta.");
                    return;
                }
            }

            Abbonamento abbonamento = new Abbonamento(venditore, now, scadenza, tipologia, tessera);
            ticketDao.salvaAbbonamento(abbonamento);

            System.out.println("Abbonamento creato con ID: " + abbonamento.getId());

        } catch (IllegalArgumentException e) {
            System.out.println("Formato UUID non valido.");
        }
    }

    public static void menuVenditori() {
        boolean running = true;
        while (running) {
            System.out.println("\n--- Menu Venditori ---");
            System.out.println("1. Aggiungi Rivenditore");
            System.out.println("2. Aggiungi Distributore");
            System.out.println("3. Mostra Venditori");
            System.out.println("0. Torna indietro");
            System.out.print("Scelta: ");

            String scelta = scanner.nextLine();
            switch (scelta) {
                case "1" -> {
                    Rivenditore r = new Rivenditore();
                    vd.aggiungiRivenditore(r);
                    System.out.println("Rivenditore creato con ID: " + r.getId());
                }
                case "2" -> {
                    System.out.print("Distributore attivo? (true/false): ");
                    boolean attivo = Boolean.parseBoolean(scanner.nextLine().trim());
                    Distributore d = new Distributore();
                    d.setAttivo(attivo);
                    vd.aggiungiDistributore(d);
                    System.out.println("Distributore creato con ID: " + d.getId());
                }
                case "3" -> vd.mostraVenditori();
                case "0" -> running = false;
                default -> System.out.println("Scelta non valida, riprova.");
            }
        }
        }
    }











