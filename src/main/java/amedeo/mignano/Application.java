package amedeo.mignano;

import amedeo.mignano.dao.*;
import amedeo.mignano.entities.*;
import amedeo.mignano.entities.enums.Stato;
import amedeo.mignano.entities.enums.TipoMezzoTrasporto;
import amedeo.mignano.entities.enums.Tipologia;
import amedeo.mignano.exceptions.ElementoNonTrovatoException;
import amedeo.mignano.exceptions.InputErratoException;
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
    public static UserDAO uDao = new UserDAO(em);
    public static TicketDao ticketDao = new TicketDao(em);
    public static VenditoreDAO vd = new VenditoreDAO(em);
    public static MezzoTrasportoDAO mtd = new MezzoTrasportoDAO(em);
    public static StatoMezzoTrasportoDAO smtd = new StatoMezzoTrasportoDAO(em);
    public static TrattaDAO td = new TrattaDAO(em);
    public static CardDAO cd = new CardDAO(em);
    public static  TempiPercorrenzaDAO tpd = new TempiPercorrenzaDAO(em);

    //COLORI COLORATI COLORELLI
    public static final String GIALLO = "\u001B[33m";
    public static  final String BLU = "\u001B[34m";
    public static final String VERDE = "\u001B[32m";
    public static  final String VIOLA = "\u001B[35m";
    public static final String ROSSO = "\u001B[31m";
    public static final String RESET = "\u001B[0m";


    public static void main(String[] args) {
        while (true) {
            try {
                System.out.println(VERDE + "\n--- Benvenuti in EpiTransports ---");
                System.out.println(BLU + "\n1 -> ACCEDI COME ADMIN " + RESET + GIALLO + "\n2 -> ACCEDI COME UTENTE" + RESET + VIOLA + "\n0 -> ESCI" + RESET );
                int scelta = Integer.parseInt(scanner.nextLine());
                switch (scelta) {
                    case 1 :
                        chiediPassword();
                        break;
                    case 2 :
                        menuUtente();
                        break;
                    case 0 :
                        return;
                    default:
                        throw  new InputErratoException("SOLO NUMERI 1/2\n");
                }
            } catch (NumberFormatException ex) {
                System.out.println("SOLO INPUT NUMERICO\n");
            } catch (InputErratoException ex) {
                System.out.println(ex.getMessage());

            }
        }
    }

    /* ============================== MENU ADMIN ============================== */
    public static void chiediPassword(){
        String PASSWORD = "Admin";
        System.out.print(BLU + "\nInserisci password amministratore: " + RESET);
        String pass = scanner.nextLine();
        if(pass.equals(PASSWORD)){
            menuAdmin();
        }else {
            System.out.println(ROSSO + "Password errata" + RESET);
        }
    }
    public static void menuAdmin() {
        boolean running = true;
        while (running) {
            System.out.println(BLU + "\n--- Menu Admin ---");
            System.out.println("1. Aggiungi mezzo di trasporto");
            System.out.println("2. Update stato mezzo di trasporto");
            System.out.println("3. Controlla periodi SERVIZIO/MANUTENZIONE mezzo");
            System.out.println("4. Cerca mezzi per FUORI_SERVIZIO, IN_MANUTENZIONE/PERIODO");
            System.out.println("5. Inserisci nuova tratta");
            System.out.println("6. Menu venditori");
            System.out.println("7. Visualizza statistiche");
            System.out.println("8. Collega Bus/Tram a tratta");
            System.out.println("9. Controlla Abbonamento" );
            System.out.println(VIOLA + "0. Torna indietro");
            System.out.print(BLU + "Scelta: " + RESET);
            String scelta = scanner.nextLine();

            switch (scelta) {
                case "1" -> creaMezzo(smtd);
                case "2" -> updateStatoMezzo(smtd);
                case "3" -> stampaPeriodiMezzo();
                case "4" -> cercaMezziPerStatoPeriodo();
                case "5" -> creaTratta();
                case "6" -> menuVenditori();
                case "7" -> menustatistiche();
                case "8" -> collegaBusTratta();
                case "9" -> readCardAndValidate();
                case "0" -> running = false;
                default -> System.out.println(ROSSO + "Scelta non valida, riprova." + RESET);
            }
        }
    }

    public static void stampaPeriodiMezzo() {
        try {
            System.out.print(BLU + "\nInserisci ID del mezzo: " + RESET);
            int id = Integer.parseInt(scanner.nextLine());

            MezzoTrasporto mezzo = mtd.getEntityManager().find(MezzoTrasporto.class, id);
            if (mezzo == null) {
                throw new ElementoNonTrovatoException("Mezzo non trovato con ID: " + id);
            }

            List<StatoMezzoTrasporto> periodi = mtd.getPeriodiMezzo(id);
            if (periodi.isEmpty()) {
                System.out.println(ROSSO + "Nessun periodo registrato per questo mezzo." + RESET);
            } else {
                System.out.println(VERDE + "Periodi per il mezzo " + id + ":" + RESET);
                for (StatoMezzoTrasporto s : periodi) {
                    String fine = (s.getDataFine() != null) ? s.getDataFine().toString() : "Ancora attivo";
                    System.out.println(VERDE + "Stato: " + s.getStato() + ", Inizio: " + s.getDataInizio() + ", Fine: " + fine + RESET);
                }
            }
        } catch (InputMismatchException e) {
            System.out.println(ROSSO + "Errore: inserire un numero intero valido per l'ID." + RESET);
        } catch (ElementoNonTrovatoException e) {
            System.out.println(ROSSO + e.getMessage() + RESET);
        } catch (Exception e) {
            System.out.println(ROSSO + "Errore inatteso: " + e.getMessage() + RESET);
        }
    }

    public static void cercaMezziPerStatoPeriodo() {
        try {
            System.out.print(BLU + "\nInserisci stato (FUORI_SERVIZIO / IN_MANUTENZIONE): " + RESET);
            Stato stato = Stato.valueOf(scanner.nextLine().toUpperCase());

            System.out.print(BLU + "Inserisci data inizio (YYYY-MM-DD): " + RESET);
            LocalDate inizio = LocalDate.parse(scanner.nextLine());

            System.out.print(BLU + "Inserisci data fine (YYYY-MM-DD): " + RESET);
            LocalDate fine = LocalDate.parse(scanner.nextLine());

            if (fine.isBefore(inizio)) {
                System.out.println(ROSSO + "Errore: la data fine non può essere precedente alla data inizio." + RESET);
                return;
            }

            List<MezzoTrasporto> mezzi = mtd.getMezziByStatoPeriodo(stato, inizio, fine);
            if (mezzi.isEmpty()) {
                System.out.println(ROSSO + "Nessun mezzo trovato per lo stato e il periodo indicato." + RESET);
            } else {
                System.out.println(VERDE + "Mezzi trovati:" + RESET);
                for (MezzoTrasporto m : mezzi) {
                    System.out.println(VERDE + m + RESET);
                }
            }
        } catch (IllegalArgumentException e) {
            System.out.println(ROSSO +"Errore: stato inserito non valido. Usa FUORI_SERVIZIO o IN_MANUTENZIONE." + RESET);
        } catch (DateTimeParseException e) {
            System.out.println(ROSSO + "Errore: formato data non valido. Usa YYYY-MM-DD." + RESET);
        } catch (Exception e) {
            System.out.println(ROSSO + "Errore inatteso: " + e.getMessage() + RESET);
        }
    }

    public static void creaMezzo(StatoMezzoTrasportoDAO std) {
        TipoMezzoTrasporto tipo;
        try {
            System.out.print(BLU + "\nInserisci TIPOLOGIA MEZZO (AUTOBUS / TRAM): " + RESET);
            String input = scanner.nextLine().toUpperCase().trim();
            if (input.equals("AUTOBUS") || input.equals("BUS"))
                tipo = TipoMezzoTrasporto.AUTOBUS;
            else if (input.equals("TRAM")) {
                tipo = TipoMezzoTrasporto.TRAM;
            } else {
                throw new InputErratoException("INPUT ERRATO! SOLO TRAM O BUS!");
            }
        } catch (NumberFormatException | InputErratoException ex) {
            System.out.println(ROSSO + ex.getMessage() + RESET);
            return;
        }
        MezzoTrasporto mt = new MezzoTrasporto(tipo);
        mtd.creaSalva(std, mt);
        System.out.println(VERDE + "Mezzo salvato in DB!\nId: " + mt.getId() + RESET);
    }
    public static void updateStatoMezzo( StatoMezzoTrasportoDAO std){
        try {
            System.out.print(BLU + "\nInserisci ID mezzo di trasporto: " + RESET);
            int mezzoId;
            try {
                mezzoId = Integer.parseInt(scanner.nextLine());
                Optional<MezzoTrasporto> om = Optional.ofNullable(mtd.getEntityManager().find(MezzoTrasporto.class, mezzoId));
                if (om.isEmpty()) {
                    System.out.println(ROSSO + "ID NON TROVATO!" + RESET);
                    return;
                }
                MezzoTrasporto mt = om.get();
                if (mt.getId() <= 0) {
                    throw new NumberFormatException("ID non valido");
                }
            } catch (NumberFormatException e) {
                throw new InputErratoException("INSERISCI SOLO NUMERI");
            }
            System.out.print(BLU + "Inserisci nuovo stato (IN_SERVIZIO / FUORI_SERVIZIO / IN_MANUTENZIONE): " + RESET);
            String inputStato = scanner.nextLine().toUpperCase();
            Stato nuovoStato;
            if (inputStato.equals("IN_SERVIZIO") || inputStato.equals("INSERVIZIO") || inputStato.equals("IN SERVIZIO")) {
                nuovoStato = Stato.IN_SERVIZIO;
            } else if (inputStato.equals("FUORI_SERVIZIO") || inputStato.equals("FUORISERVIZIO") || inputStato.equals("FUORI SERVIZIO")) {
                nuovoStato = Stato.FUORI_SERVIZIO;
            } else if (inputStato.equals("IN_MANUTENZIONE") || inputStato.equals("INMANUTENZIONE") || inputStato.equals("IN MANUTENZIONE")) {
                nuovoStato = Stato.IN_MANUTENZIONE;
            } else {
                throw new InputErratoException("STATO NON VALIDO! SOLO: IN_SERVIZIO, FUORI_SERVIZIO o IN_MANUTENZIONE\n");
            }
            mtd.updateStato(std,mezzoId, nuovoStato);
        } catch (InputErratoException | ElementoNonTrovatoException ex) {
            System.out.println(ROSSO + ex.getMessage() + RESET);
        }
    }

    public static void creaTratta() {
        try {
            String partenza;
            while (true) {
                System.out.print(BLU + "\nInserisci PARTENZA: " + RESET);
                partenza = scanner.nextLine();
                if (!partenza.matches("^[A-Za-zÀ-ÖØ-öø-ÿ'\\-\\s]+$")) {
                    System.out.println(ROSSO + "Il nome non può contenere numeri o caratteri speciali." + RESET);
                } else break;
            }

            String capolinea = null;
            while (true) {
                System.out.print(BLU + "Inserisci CAPOLINEA: " + RESET);
                capolinea = scanner.nextLine();
                if (!capolinea.matches("^[A-Za-zÀ-ÖØ-öø-ÿ'\\-\\s]+$")) {
                    System.out.println(ROSSO + "Il nome non può contenere numeri o caratteri speciali.\n" + RESET);
                } else break;
            }

            System.out.print(BLU + "Inserisci TEMPO PERCORRENZA PREVISTO: " + RESET);
            String tempoInput = scanner.nextLine();
            double tempoPercorrenzaPrevisto;
            try {
                tempoPercorrenzaPrevisto = Double.parseDouble(tempoInput);
                if (tempoPercorrenzaPrevisto <= 0) {
                    throw new NumberFormatException("INPUT NON VALIDO\n");
                }
            } catch (NumberFormatException ex) {
                System.out.println(ROSSO + ex.getMessage() + RESET);
                return;
            }
            td.creaSalva(partenza,capolinea,tempoPercorrenzaPrevisto);
        } catch (Exception ex) {
            System.out.println(ROSSO + ex.getMessage() + RESET);
        }
    }

    public static void menuVenditori() {
        boolean running = true;
        while (running) {
            System.out.println(BLU + "\n--- Menu Venditori ---");
            System.out.println("1. Aggiungi Rivenditore");
            System.out.println("2. Aggiungi Distributore");
            System.out.println("3. Mostra Venditori");
            System.out.println("4. Acquista biglietti e abbonamenti");
            System.out.println("5. Mostra biglietti venduti per venditore");
            System.out.println("6. Mostra biglietti venduti per venditore/periodo");
            System.out.println("0. Torna indietro");
            System.out.print("Scelta: " + RESET);

            String scelta = scanner.nextLine();
            switch (scelta) {
                case "1" -> {
                    Rivenditore r = new Rivenditore();
                    vd.aggiungiRivenditore(r);
                    System.out.println(VERDE + "Rivenditore creato con ID: " + r.getId() + RESET);
                }
                case "2" -> {
                    System.out.print(BLU + "\nDistributore attivo? (true/false): " + RESET);
                    Distributore d ;
                    try {
                        String scelta3 = scanner.nextLine();
                        if (scelta3.equals("true")) {
                            d = new Distributore(true);
                            vd.aggiungiDistributore(d);
                            System.out.println(VERDE + "Distributore creato con ID: " + d.getId() + RESET);
                        } else if (scelta3.equals("false")) {
                            d = new Distributore(false);
                            vd.aggiungiDistributore(d);
                            System.out.println(VERDE + "Distributore creato con ID: " + d.getId() + RESET);
                        }
                        else { throw new InputErratoException("INPUT NON VALIDO");}
                    } catch (InputErratoException ex) {
                        System.out.println(ROSSO + ex.getMessage() + RESET);
                    }
                }
                case "3" -> vd.mostraVenditori();
                case "4" -> ticketMenu();
                case "5" -> reportBigliettiVenditore();
                case "6" -> reportBigliettiVenditorePeriodo();
                case "0" -> running = false;
                default -> System.out.println(ROSSO + "Scelta non valida, riprova." + RESET);
            }
        }
    }

    public static void reportBigliettiVenditore() {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print(BLU + "\nInserisci l'ID del venditore (UUID): " + RESET);
            UUID venditoreId = UUID.fromString(scanner.nextLine());

            List<Object[]> results = ticketDao.countPerVenditore(venditoreId);

            if (results.isEmpty()) {
                System.out.println(ROSSO + "Nessun biglietto venduto da questo venditore." + RESET);
            } else {
                Object[] row = results.get(0);
                Venditore v = (Venditore) row[0];
                Long count = (Long) row[1];

                System.out.println(VERDE + "Venditore ID: " + v.getId() + RESET);
                System.out.println(VERDE + "Biglietti venduti totali: " + count + RESET);
            }

        } catch (IllegalArgumentException e) {
            System.out.println(ROSSO + "UUID non valido. Usa il formato corretto (es. 123e4567-e89b-12d3-a456-426614174000)." + RESET);
        } catch (Exception e) {
            System.out.println(ROSSO + "Errore: " + e.getMessage() + RESET);
        }
    }
    public static void reportBigliettiVenditorePeriodo() {
        try {
            System.out.print(BLU + "\nInserisci l'ID del venditore (UUID): " + RESET);
            UUID venditoreId = UUID.fromString(scanner.nextLine());

            System.out.print(BLU + "Inserisci la data di inizio (formato: yyyy-MM-dd): " + RESET);
            LocalDate start = LocalDate.parse(scanner.nextLine());

            System.out.print(BLU + "Inserisci la data di fine (formato: yyyy-MM-dd): " + RESET);
            LocalDate end = LocalDate.parse(scanner.nextLine());

            List<Object[]> results = ticketDao.countPerVenditorePerPeriodo(venditoreId, start, end);

            if (results.isEmpty()) {
                System.out.println(ROSSO + "Nessun biglietto venduto da questo venditore nel periodo selezionato." + RESET);
            } else {
                Object[] row = results.get(0);
                Venditore v = (Venditore) row[0];
                Long count = (Long) row[1];

                System.out.println(VERDE + "Venditore ID: " + v.getId() + RESET);
                System.out.println(VERDE + "Biglietti venduti nel periodo: " + count + RESET);
            }

        } catch (IllegalArgumentException e) {
            System.out.println(ROSSO + "UUID non valido. Assicurati di usare il formato corretto (es. 123e4567-e89b-12d3-a456-426614174000)." + RESET);
        } catch (Exception e) {
            System.out.println(ROSSO + "Errore: " + e.getMessage() + RESET);
        }
    }

    public static void menustatistiche() {
        boolean running = true;
        while (running) {
            System.out.println(BLU + "\nMenu per statistiche biglietti");
            System.out.println("1. conta biglietti validati per mezzo");
            System.out.println("2. conta biglietti validati in un mezzo per periodo");
            System.out.println("3. conta biglietti validati");
            System.out.println("4. calcola media tempi percorrenza per mezzo");
            System.out.println("5. calcola n volte che un dato mezzo percorre una data tratta");
            System.out.println("0. esci ");
            System.out.print("Scelta: " + RESET);

            try {
                int scelta = Integer.parseInt(scanner.nextLine().trim());
                switch (scelta) {
                    case 1 -> contaPerMezzo();
                    case 2 -> contaPerPeriodo();
                    case 3 -> contaBigliettiValidati();
                    case 4 -> calcoloTrattaMedia() ;
                    case 5 -> stampaNumPercorsaTratta();
                    case 0 -> {
                        running = false;
                        System.out.println(BLU + "Uscita dal menu statistiche" + RESET);
                    }
                    default -> System.out.println(ROSSO + "Scelta non valida riprova" + RESET);
                }
            } catch (Exception e) {
                System.out.println(ROSSO + "Errore input " + e.getMessage() + RESET);
            }
        }
    }
    private static void contaPerMezzo() {
        try {
            System.out.print(BLU + "\nInserisci ID del mezzo: " + RESET);
            int mezzoId = Integer.parseInt(scanner.nextLine());
            long result = ticketDao.countBigliettiValidatiByMezzo(mezzoId);
            System.out.println(VERDE + "Biglietti validati sul mezzo " + mezzoId + ": " + result + RESET);
        }catch (NumberFormatException ex){
            System.out.println(ex.getMessage());
        }
    }
    private static void contaPerPeriodo() {
        try {
            System.out.print(BLU + "\nInserisci data inizio (YYYY-MM-DD): " + RESET);
            LocalDate start = LocalDate.parse(scanner.nextLine());
            System.out.print(BLU + "Inserisci data fine (YYYY-MM-DD): " + RESET);
            LocalDate end = LocalDate.parse(scanner.nextLine());
            int mezzoId;
            System.out.print(BLU + "Inserisci ID mezzo di trasporto: " + RESET);
            mezzoId = Integer.parseInt(scanner.nextLine());
            long result = ticketDao.countBigliettiValidatiInPeriodo(start, end, mezzoId);
            System.out.println(VERDE + "Biglietti validati tra " + start + " e " + end + ": " + result + RESET);
        } catch (InputErratoException e) {
            System.out.print(ROSSO + "Inserisci un formato valido!" + RESET);
        } catch (NumberFormatException ex) {
            System.out.println(ROSSO + "SOLO NUMERI." + RESET);
        }catch (Exception e){
            System.out.println(ROSSO + e.getMessage() + RESET);
        }
    }
    private static void contaBigliettiValidati() {
        long result = ticketDao.countBigliettiValidati();
        System.out.println(VERDE + "\nBiglietti validati: " + result + RESET);
    }
    public static void calcoloTrattaMedia(){
        try {
            System.out.print(BLU + "\nInserisci Id tratta: " + RESET);
            UUID trattaId = UUID.fromString(scanner.nextLine());
            System.out.print(BLU + "Inserisci Id mezzo: " + RESET);
            int mezzoId = Integer.parseInt(scanner.nextLine());
            td.calcoloTrattaMezzoAvg(trattaId, mezzoId);
        } catch (NumberFormatException ex) {
            System.out.println(ROSSO + "ID NON VALIDO!" + RESET);
        } catch (ElementoNonTrovatoException ex) {
            System.out.println(ROSSO + ex.getMessage() + RESET);
        }
    }
    public static void stampaNumPercorsaTratta() {
        try {
            System.out.print(BLU + "\nInserisci ID mezzo di trasporto: " + RESET);
            int mezzoId = Integer.parseInt(scanner.nextLine());
            System.out.print(BLU + "Inserisci ID tratta: " + RESET);
            UUID trattaId = UUID.fromString(scanner.nextLine());
            MezzoTrasporto mezzo = em.find(MezzoTrasporto.class, mezzoId);
            Tratta tratta = em.find(Tratta.class, trattaId);
            if (mezzo == null || tratta == null) {
                throw new ElementoNonTrovatoException("MEZZO O TRATTA NON TROVATI IN DB.");
            }
            mtd.stampaNPercorsaTratta(mezzo, tratta);
        } catch (NumberFormatException e) {
            System.out.println(ROSSO + "ID MEZZO NON VALIDO." + RESET);
        } catch (IllegalArgumentException e) {
            System.out.println(ROSSO + "ID TRATTA NON CORRETTO." + RESET);
        } catch (ElementoNonTrovatoException ex) {
            System.out.println(ROSSO + ex.getMessage() + RESET);
        }
    }

    public static void collegaBusTratta() {
        MezzoTrasporto elTrovato;
        try {
            System.out.print(BLU + "\nInserisci ID mezzo di trasporto: " + RESET);
            int id = Integer.parseInt(scanner.nextLine());
            elTrovato = em.find(MezzoTrasporto.class, id);
            if (elTrovato == null) {
                throw new ElementoNonTrovatoException("ELEMENTO NON PRESENTE IN DB O ID INCORRETTO");
            }
        } catch (NumberFormatException ex) {
            System.out.println(ROSSO + "INSERISCI SOLO NUMERI" + RESET);
            return;
        } catch (ElementoNonTrovatoException ex) {
            System.out.println(ROSSO + ex.getMessage() + RESET);
            return;
        }
        Tratta tTrovata;
        try {
            System.out.print(BLU + "Inserisci ID tratta: " + RESET);
            UUID id = UUID.fromString(scanner.nextLine());
            tTrovata = em.find(Tratta.class, id);
            if (tTrovata == null) {
                throw new ElementoNonTrovatoException("ELEMENTO NON PRESENTE IN DB O ID INCORRETTO");
            }
        } catch (ElementoNonTrovatoException ex) {
            System.out.println(ROSSO + ex.getMessage() + RESET);
            return;
        } catch (IllegalArgumentException ex) {
            System.out.println(ROSSO + "ID TRATTA ERRATO" + RESET);
            return;
        }
        System.out.print(BLU + "Inserisci TEMPO PERCORRENZA EFFETTIVO: " + RESET);
        String tempoEffettivoInput = scanner.nextLine();
        double tempoPercorrenzaEffettivo;
        try {
            tempoPercorrenzaEffettivo = Double.parseDouble(tempoEffettivoInput);
            if (tempoPercorrenzaEffettivo <= 0) {
                throw new NumberFormatException("INPUT NON VALIDO");
            }
        } catch (NumberFormatException ex) {
            System.out.println(ROSSO + ex.getMessage() + RESET);
            return;
        }
        TempiPercorrenza tp = new TempiPercorrenza(tempoPercorrenzaEffettivo, elTrovato, tTrovata);
        tpd.creaSalva(tp);
    }
    public static void readCardAndValidate() {
        try {
            System.out.print(BLU + "\nInserisci l'ID della tessera (UUID): " + RESET);
            String cardId = scanner.nextLine();
            Optional<Card> optC = Optional.ofNullable(cd.getEm().find(Card.class, UUID.fromString(cardId)));
            if (optC.isEmpty()){
                System.out.println(ROSSO + "Tessera non trovata!" + RESET);
                return;
            }
            Card card = optC.get();
            User a = card.getUser();
            if(a.getName().equalsIgnoreCase("Ajeje") && a.getSurname().equalsIgnoreCase("Brazorf")){
                System.out.println(ROSSO + "Controllore: Mi faccia vedere il biglietto, se ce l'ha.");
                System.out.println("Altrimenti le farò una contravvenzione!" + RESET);
            }else{
                ticketDao.subValidity(card.getId());
            }
        } catch (IllegalArgumentException e) {
            System.out.println(ROSSO + "UUID non valido. Assicurati di usare il formato corretto (es. 123e4567-e89b-12d3-a456-426614174000)." + RESET);
        }
    }
    /* ============================== MENU USERS ============================== */
    public static void menuUtente(){
        boolean running = true;
        while (running) {
            try {
                System.out.println(GIALLO + "\n--- Menu Utente ---");
                System.out.println("1. Registra utente");
                System.out.println("2. Rinnova tessera");
                System.out.println("3. Sali sul mezzo");
                System.out.println("4. Biglietteria");
                System.out.println(VIOLA + "0. Torna indietro");
                System.out.print(GIALLO + "Scelta: " + RESET);
                String scelta = scanner.nextLine();

                switch (scelta) {
                    case "1" -> creaUtente();
                    case "2" -> rinnovaCardMenu();
                    case "3" -> saliSulMezzo();
                    case "4" -> ticketMenu();
                    case "0" -> running = false;
                    default ->  throw  new InputErratoException("INPUT NON VALIDO");
                }} catch (InputErratoException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public static void creaUtente() {
        try {
            String name;
            while (true) {
                System.out.print(GIALLO + "\nInserisci il nome utente: " + RESET);
                name = scanner.nextLine().trim();
                if (!name.matches("^[A-Za-zÀ-ÖØ-öø-ÿ'\\-\\s]+$")) {
                    System.out.println(ROSSO + "Il nome non può contenere numeri o caratteri speciali." + RESET);
                } else break;
            }

            String surname;
            while (true) {
                System.out.print(GIALLO + "Inserisci il cognome utente: " + RESET);
                surname = scanner.nextLine().trim();
                if (!surname.matches("^[A-Za-zÀ-ÖØ-öø-ÿ'\\-\\s]+$")) {
                    System.out.println(ROSSO + "Il nome non può contenere numeri o caratteri speciali.\n" + RESET);
                } else break;
            }

            LocalDate bornDate;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            while (true) {
                System.out.print(GIALLO + "Inserisci la data di nascita (YYYY-MM-DD): " + RESET);
                String date = scanner.nextLine().trim();

                if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    System.out.println(ROSSO + "Formato non valido. Usa YYYY-MM-DD.\n" + RESET);
                    continue;
                }

                try {
                    bornDate = LocalDate.parse(date, formatter);
                    int year = bornDate.getYear();
                    if (year < 1950 || year > LocalDate.now().getYear()) {
                        System.out.println(ROSSO + "L'anno deve essere compreso tra il 1950 e l'anno corrente!\n" + RESET);
                        continue;
                    }
                    break;
                } catch (DateTimeParseException e) {
                    System.out.println(ROSSO + "Data non valida. Verifica giorno e mese.\n" + RESET);
                }
            }

            User user = new User(name, surname, bornDate);
            Card card = new Card(LocalDate.now(), LocalDate.now().plusYears(1), false, LocalDate.now());
            card.setUser(user);
            user.setCard(card);
            uDao.saveUser(user);
            System.out.println(VERDE + "Utente " + user.getName() + " aggiunto con successo con tessera " + card.getId() + " valida fino al "
                    + card.getDue_date() + RESET);
        } catch (Exception e) {
            System.out.println(ROSSO + "Errore durante la creazione utente: " + e.getMessage() + RESET);
        }
    }

    public static void rinnovaCardMenu() {
        while (true) {
            System.out.print(GIALLO + "\nInserisci l'ID della tessera o premi 0 per uscire: " + RESET);
            String idCard = scanner.nextLine();

            if (idCard.equalsIgnoreCase("0")) {
                System.out.println(VIOLA + "Uscita dal rinnovo tessera." + RESET);
                break;
            }
            try {
                Optional<Card> optionalCard = Optional.ofNullable(em.find(Card.class, UUID.fromString(idCard)));
                if (optionalCard.isEmpty()) {
                    System.out.println(ROSSO + "Tessera non presente!" + RESET);
                    return;
                }
                Card card = optionalCard.get();

                cd.rinnovaCard(card);
                System.out.println(VERDE + "Tessera aggiornata!" + RESET);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(ROSSO + "Inserisci un UUID valido" + RESET);
                System.out.println(ROSSO + "Es: 02ce3860-3126-42af-8ac7-c2a661134129" + RESET);
            } catch (Exception e) {
                System.out.println(ROSSO + "Errore durante l'aggiornamento card: " + e.getMessage() + RESET);
                break;
            }
        }
    }

    public static void saliSulMezzo(){
        try {
            System.out.print(GIALLO + "\nInserisci ID del mezzo: " + RESET);
            int mezzoId = Integer.parseInt(scanner.nextLine());
            Optional<MezzoTrasporto> optM = Optional.ofNullable(mtd.getEntityManager().find(MezzoTrasporto.class, mezzoId));
            if(optM.isEmpty()){
                System.out.println(ROSSO + "Mezzo non trovato" + RESET);
                return;
            }
            MezzoTrasporto mezzoTrasporto = optM.get();
            if (mezzoTrasporto.getStato() == Stato.IN_SERVIZIO){
                System.out.print(GIALLO + "Scannerizza il biglietto... " + RESET);
                String bigliettoId = scanner.nextLine();
                Optional<Biglietto> optB = Optional.ofNullable(ticketDao.getEntityManager().find(Biglietto.class, UUID.fromString(bigliettoId)));
                if (optB.isEmpty()){
                    System.out.println(ROSSO + "Biglietto Non Trovato" + RESET);
                    return;
                }
                Biglietto biglietto = optB.get();
                if(biglietto.isValidazione()){
                    System.out.println(ROSSO + "Questo biglietto è già stato validato!" + RESET);
                }else {
                    System.out.println(GIALLO + "validazione in corso..." + RESET);
                    biglietto.setMezzo(mezzoTrasporto);
                    biglietto.setValidazione(true);
                    biglietto.setDataValidazione(LocalDate.now());
                    biglietto.setDataScadenza(LocalDate.now().plusDays(1));

                    ticketDao.aggiornaTicket(biglietto);
                    System.out.println(VERDE + "Biglietto validato con successo!" + RESET);
                }

            }else {
                System.out.println(ROSSO + "Il mezzo non è attualmente in servizio!" + RESET);
            }
        }catch (NumberFormatException e){
            System.out.println(ROSSO + "INSERISCI UN NUMERO" + RESET);
        }catch (IllegalArgumentException e){
            System.out.println(ROSSO + "Inserisci un UUID valido" + RESET);
            System.out.println(ROSSO + "Es: 02ce3860-3126-42af-8ac7-c2a661134129" + RESET);
        }catch (Exception e){
            System.out.println(ROSSO + e.getMessage() + RESET);
        }
    }

    public static void ticketMenu() {
        boolean run = true;
        while (run) {
            System.out.println(GIALLO + "\n--- Menu Ticket ---");
            System.out.println("1. Crea Biglietto");
            System.out.println("2. Crea Abbonamento");
            System.out.println(VIOLA + "0. Torna indietro");
            System.out.print(GIALLO + "Scelta: " + RESET);
            String input = scanner.nextLine();
            switch (input) {
                case "1" -> creaBiglietto();
                case "2" -> creaAbbonamento();
                case "0" -> run = false;
                default -> System.out.println(ROSSO + "Scelta non valida" + RESET);
            }
        }
    }
    public static void creaBiglietto() {
        try {
            System.out.print(GIALLO + "\nInserisci l'ID del venditore: " + RESET);
            String venditoreId = scanner.nextLine();
            Optional<Venditore> optVend = Optional.ofNullable(em.find(Venditore.class, UUID.fromString(venditoreId)));
            if (optVend.isEmpty()) {
                System.out.println(ROSSO + "Venditore non presente" + RESET);
                return;
            }
            Venditore venditore = optVend.get();
            LocalDate now = LocalDate.now();
            Biglietto biglietto = new Biglietto(venditore, now);
            ticketDao.salvaBiglietto(biglietto);
            System.out.println(VERDE + "Biglietto con id: " + biglietto.getId() + " acquistato con successo" + RESET);
        } catch (IllegalArgumentException e) {
            System.out.println(ROSSO + "Inserisci un UUID valido" + RESET);
            System.out.println(ROSSO + "Es: 02ce3860-3126-42af-8ac7-c2a661134129" + RESET);
        }
    }
    public static void creaAbbonamento() {
        try {
            System.out.print(GIALLO + "\nInserisci l'ID del venditore: " + RESET);
            String venditoreId = scanner.nextLine();
            Optional<Venditore> optVend = Optional.ofNullable(em.find(Venditore.class, UUID.fromString(venditoreId)));
            if (optVend.isEmpty()) {
                System.out.println(ROSSO + "Venditore non presente" + RESET);
                return;
            }
            Venditore venditore = optVend.get();
            System.out.print(GIALLO + "Seleziona tipologia abbonamento (SETTIMANALE/MENSILE): " + RESET);
            String tipo = scanner.nextLine();
            if (!tipo.equalsIgnoreCase("SETTIMANALE") && !tipo.equalsIgnoreCase("MENSILE")) {
                System.out.println(ROSSO + "Scelta non valida, scegli tra SETTIMANALE o MENSILE\n" + RESET);
                return;
            }
            Tipologia tipologia = Tipologia.valueOf(tipo.toUpperCase());
            LocalDate now = LocalDate.now();
            LocalDate scadenza = tipo.equalsIgnoreCase("SETTIMANALE") ?
                    now.plusWeeks(1) : now.plusMonths(1);
            System.out.print(GIALLO + "Inserisci Id tessera: " + RESET);
            String cardId = scanner.nextLine();
            Optional<Card> optCard = Optional.ofNullable(
                    em.find(Card.class, UUID.fromString(cardId))
            );

            if (optCard.isEmpty()) {
                System.out.println(ROSSO + "Tessera non trovata." + RESET);
                return;
            }

            Card tessera = optCard.get();
            if (tessera.isExpired()) {
                System.out.print(GIALLO + "Tessera scaduta, vuoi rinnovarla (Y/N)? " + RESET);
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("Y")) {
                    cd.rinnovaCard(tessera);
                    System.out.println(VERDE + "Tessera rinnovata." + RESET);
                } else {
                    System.out.println(ROSSO + "Impossibile acquistare con tessera scaduta.\n" + RESET);
                    return;
                }
            }

            Abbonamento abbonamento = new Abbonamento(venditore, now, scadenza, tipologia, tessera);
            ticketDao.salvaAbbonamento(abbonamento);

            System.out.println(VERDE + "Abbonamento creato con ID: " + abbonamento.getId() + RESET);

        } catch (IllegalArgumentException e) {
            System.out.println(ROSSO + "Formato UUID non valido." + RESET);
        }
    }
}
