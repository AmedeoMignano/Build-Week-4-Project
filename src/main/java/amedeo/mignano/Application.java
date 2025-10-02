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


    public static void main(String[] args) {
while (true) {
    try {
        System.out.println("1 -> ADMIN\n2 -> UTENTE\n0 -> TERMINARE");
        int scelta = Integer.parseInt(scanner.nextLine());
        switch (scelta) {
            case 1 :
                menuAdmin();
                break;
            case 2 :
                menuUtente();
                break;
            case 0 :
                return;
            default:
                throw  new InputErratoException("SOLO NUMERI 1/2");
        }
    } catch (NumberFormatException ex) {
        System.out.println("SOLO INPUT NUMERICO");
    } catch (InputErratoException ex) {
        System.out.println(ex.getMessage());

    }
}
    }

    // Metodi dei menu e delle creazioni

    public static void ticketMenu() {
        boolean run = true;
        while (run) {
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

    public static void creaBiglietto() {
        try {
            System.out.println("Inserisci l'id del venditore:");
            String venditoreId = scanner.nextLine();
            Optional<Venditore> optVend = Optional.ofNullable(em.find(Venditore.class, UUID.fromString(venditoreId)));
            if (optVend.isEmpty()) {
                System.out.println("Venditore non presente");
                return;
            }
            Venditore venditore = optVend.get();
            LocalDate now = LocalDate.now();
            Biglietto biglietto = new Biglietto(venditore, now);
            ticketDao.salvaBiglietto(biglietto);
            System.out.println("Biglietto con id: " + biglietto.getId() + " acquistato con successo");
        } catch (IllegalArgumentException e) {
            System.out.println("Inserisci un UUID valido");
            System.out.println("Es: 02ce3860-3126-42af-8ac7-c2a661134129");
        }
    }

    public static void creaAbbonamento() {
        try {
            System.out.println("Inserisci l'id del venditore:");
            String venditoreId = scanner.nextLine();
            Optional<Venditore> optVend = Optional.ofNullable(em.find(Venditore.class, UUID.fromString(venditoreId)));
            if (optVend.isEmpty()) {
                System.out.println("Venditore non presente");
                return;
            }
            Venditore venditore = optVend.get();
            System.out.println("Seleziona tipologia abbonamento (SETTIMANALE/MENSILE):");
            String tipo = scanner.nextLine();
            if (!tipo.equalsIgnoreCase("SETTIMANALE") && !tipo.equalsIgnoreCase("MENSILE")) {
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
            System.out.println("4. Acquista biglietti e abbonamenti");
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
                case "4" -> ticketMenu();
                case "0" -> running = false;
                default -> System.out.println("Scelta non valida, riprova.");
            }
        }
    }

    public static void creaUtente() {
        try {
            String name;
            while (true) {
                System.out.print("Inserisci il nome utente: ");
                name = scanner.nextLine().trim();
                if (!name.matches("^[A-Za-zÀ-ÖØ-öø-ÿ'\\-\\s]+$")) {
                    System.out.println("Il nome non può contenere numeri o caratteri speciali.\n");
                } else break;
            }

            String surname;
            while (true) {
                System.out.print("Inserisci il cognome utente: ");
                surname = scanner.nextLine().trim();
                if (!surname.matches("^[A-Za-zÀ-ÖØ-öø-ÿ'\\-\\s]+$")) {
                    System.out.println("Il cognome non può contenere numeri o caratteri speciali.\n");
                } else break;
            }

            LocalDate bornDate;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            while (true) {
                System.out.print("Inserisci la data di nascita (YYYY-MM-DD): ");
                String date = scanner.nextLine().trim();

                if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    System.out.println("Formato non valido. Usa YYYY-MM-DD.\n");
                    continue;
                }

                try {
                    bornDate = LocalDate.parse(date, formatter);
                    int year = bornDate.getYear();
                    if (year < 1950 || year > LocalDate.now().getYear()) {
                        System.out.println("L'anno deve essere compreso tra il 1950 e l'anno corrente!\n");
                        continue;
                    }
                    break;
                } catch (DateTimeParseException e) {
                    System.out.println("Data non valida. Verifica giorno e mese.\n");
                }
            }

            User user = new User(name, surname, bornDate);
            Card card = new Card(LocalDate.now(), LocalDate.now().plusYears(1), false, LocalDate.now());
            card.setUser(user);
            user.setCard(card);
            uDao.saveUser(user);
            System.out.println("Utente " + user.getName() + " aggiunto con successo con card valida fino al "
                    + card.getDue_date());
        } catch (Exception e) {
            System.out.println("Errore durante la creazione utente: " + e.getMessage());
        }
    }

    public static void rinnovaCardMenu() {
        while (true) {
            System.out.print("Inserisci l'ID della tessera o premi 0 per uscire:");
            String idCard = scanner.nextLine();

            if (idCard.equalsIgnoreCase("0")) {
                System.out.println("Uscita dal rinnovo tessera.");
                break;
            }
            try {
                Optional<Card> optionalCard = Optional.ofNullable(em.find(Card.class, UUID.fromString(idCard)));
                if (optionalCard.isEmpty()) {
                    System.out.println("Tessera non presente");
                    return;
                }
                Card card = optionalCard.get();

                cd.rinnovaCard(card);
            } catch (IllegalArgumentException e) {
                System.out.println("Inserisci un UUID valido");
                System.out.println("Es: 02ce3860-3126-42af-8ac7-c2a661134129");
            } catch (Exception e) {
                System.out.println("Errore durante l'aggiornamento card: " + e.getMessage());
                break;
            }
        }
    }

    public static void menuUtente(){
        boolean running = true;
        while (running) {
            try {
            System.out.println("\n--- Menu Utente ---");
            System.out.println("1. Registra utente");
            System.out.println("2. Rinnova card");
            System.out.println("3. Sali sul mezzo");
            System.out.println("4. Biglietteria");
            System.out.println("0. Torna indietro");
            System.out.print("Scelta: ");
            String scelta = scanner.nextLine();
            switch (scelta) {
                case "1" -> {
                    creaUtente();
                }
                case "2" -> {
                    rinnovaCardMenu();
                }
                case "3" -> saliSulMezzo();
                case "4" -> ticketMenu();
                case "0" -> running = false;
                default ->  throw  new InputErratoException("INPUT NON VALIDO");
            }} catch (InputErratoException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
    
    public static void readCardAndValidate() {
        try {
            System.out.println("Inserisci l'ID della tessera (UUID): ");
            String cardId = scanner.nextLine();
            Optional<Card> optC = Optional.ofNullable(cd.getEm().find(Card.class, UUID.fromString(cardId)));
            if (optC.isEmpty()){
                System.out.println("Card non trovata");
                return;
            }
            Card card = optC.get();
            User a = card.getUser();
            if(a.getName().equalsIgnoreCase("Ajeje") && a.getSurname().equalsIgnoreCase("Brazorf")){
                System.out.println("Controllore: Mi faccia vedere il biglietto, se ce l'ha");
                System.out.println("Altrimenti le farò una contravvenzione");
            }else{
                ticketDao.subValidity(card.getId());
            }
        } catch (IllegalArgumentException e) {
            System.out.println("UUID non valido. Assicurati di usare il formato corretto (es. 123e4567-e89b-12d3-a456-426614174000).");
        }
    }

    public static void menustatistiche() {
        boolean running = true;
        while (running) {
            System.out.println("Menu per statistiche biglietti");
            System.out.println("1. conta biglietti validati per mezzo");
            System.out.println("2. conta biglietti validati in un mezzo per periodo");
            System.out.println("3. conta biglietti validati");
            System.out.println("4. calcola media tempi percorrenza per mezzo");
            System.out.println("5. calcola n volte che un dato mezzo percorre una data tratta");
            System.out.println("0. esci ");
            System.out.println("scelta: ");

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
                        System.out.println("uscita dal menu statistiche");
                    }
                    default -> System.out.println("scelta non valida riprova");
                }
            } catch (Exception e) {
                System.out.println("Errore input " + e.getMessage());
            }
        }
    }
    private static void contaPerMezzo() {
       try {
           System.out.println("inserisci ID del mezzo");
           int mezzoId = Integer.parseInt(scanner.nextLine());
           long result = ticketDao.countBigliettiValidatiByMezzo(mezzoId);
           System.out.println("Biglietti validati sul mezzo " + mezzoId + ": " + result);
       }catch (NumberFormatException ex){
           System.out.println(ex.getMessage());
       }
    }
    private static void contaPerPeriodo() {
        try {
            System.out.println("inserisci data inizio (YYY-MM-DD): ");
            LocalDate start = LocalDate.parse(scanner.nextLine());
            System.out.println("inserisci data fine (YYY-MM-DD): ");
            LocalDate end = LocalDate.parse(scanner.nextLine());
            int mezzoId;
            System.out.println("Inserisci ID mezzo di trasporto:");
            mezzoId = Integer.parseInt(scanner.nextLine());
            long result = ticketDao.countBigliettiValidatiInPeriodo(start, end, mezzoId);
            System.out.println("biglietti validati tra " + start + "e " + end + ": " + result);
        } catch (InputErratoException e) {
            System.out.println("Inserisci un formato valido");
        } catch (NumberFormatException ex) {
            System.out.println("SOLO NUMERI");
    }catch (Exception e){
         System.out.println(e.getMessage());
     }
    }

    private static void contaBigliettiValidati() {
           long result = ticketDao.countBigliettiValidati();
           System.out.println("biglietti validati: " + result);
    }
    public static void creaMezzo(StatoMezzoTrasportoDAO std) {
        TipoMezzoTrasporto tipo;
        try {
            System.out.println("Inserisci TIPOLOGIA MEZZO: AUTOBUS / TRAM");
            String input = scanner.nextLine().toUpperCase().trim();
            if (input.equals("AUTOBUS") || input.equals("BUS"))
                tipo = TipoMezzoTrasporto.AUTOBUS;
            else if (input.equals("TRAM")) {
                tipo = TipoMezzoTrasporto.TRAM;
            } else {
                throw new InputErratoException("INPUT ERRATO! SOLO TRAM O BUS!");
            }
        } catch (NumberFormatException | InputErratoException ex) {
            System.out.println(ex.getMessage());
            return;
        }
        MezzoTrasporto mt = new MezzoTrasporto(tipo);
        mtd.creaSalva(std, mt);
        System.out.println("Mezzo salvato in DB!\nId: " + mt.getId());
    }

    public static void updateStatoMezzo( StatoMezzoTrasportoDAO std){
        try {
            System.out.println("Inserisci ID mezzo di trasporto:");
            int mezzoId;
            try {
                mezzoId = Integer.parseInt(scanner.nextLine());
                if (mezzoId <= 0) {
                    throw new NumberFormatException("ID non valido");
                }
            } catch (NumberFormatException e) {
                throw new InputErratoException("INSERISCI SOLO NUMERI");
            }
            System.out.println("Inserisci nuovo stato: IN_SERVIZIO / FUORI_SERVIZIO / IN_MANUTENZIONE");
            String inputStato = scanner.nextLine().toUpperCase();
            Stato nuovoStato;
            if (inputStato.equals("IN_SERVIZIO") || inputStato.equals("INSERVIZIO") || inputStato.equals("IN SERVIZIO")) {
                nuovoStato = Stato.IN_SERVIZIO;
            } else if (inputStato.equals("FUORI_SERVIZIO") || inputStato.equals("FUORISERVIZIO") || inputStato.equals("FUORI SERVIZIO")) {
                nuovoStato = Stato.FUORI_SERVIZIO;
            } else if (inputStato.equals("IN_MANUTENZIONE") || inputStato.equals("INMANUTENZIONE") || inputStato.equals("IN MANUTENZIONE")) {
                nuovoStato = Stato.IN_MANUTENZIONE;
            } else {
                throw new InputErratoException("STATO NON VALIDO! SOLO: IN_SERVIZIO, FUORI_SERVIZIO o IN_MANUTENZIONE");
            }
            mtd.updateStato(std,mezzoId, nuovoStato);
        } catch (InputErratoException | ElementoNonTrovatoException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void creaTratta() {
        try {
            System.out.println("Inserisci PARTENZA");
            String partenza = scanner.nextLine();
            System.out.println("Inserisci CAPOLINEA");
            String capolinea = scanner.nextLine();
            System.out.println("Inserisci TEMPO PERCORRENZA PREVISTO");
            String tempoInput = scanner.nextLine();
            double tempoPercorrenzaPrevisto;
            try {
                tempoPercorrenzaPrevisto = Double.parseDouble(tempoInput);
                if (tempoPercorrenzaPrevisto <= 0) {
                    throw new NumberFormatException("INPUT NON VALIDO");
                }
            } catch (NumberFormatException ex) {
                System.out.println(ex.getMessage());
                return;
            }
            td.creaSalva(partenza,capolinea,tempoPercorrenzaPrevisto);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void calcoloTrattaMedia(){
        try {
            System.out.println("Inserisci Id tratta: ");
            UUID trattaId = UUID.fromString(scanner.nextLine());
            System.out.println("Inserisci Id mezzo: ");
            int mezzoId = Integer.parseInt(scanner.nextLine());
            td.calcoloTrattaMezzoAvg(trattaId, mezzoId);
        } catch (NumberFormatException ex) {
            System.out.println("ID NON VALIDO");
        } catch (ElementoNonTrovatoException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void stampaNumPercorsaTratta() {
        try {
            System.out.println("Inserisci ID mezzo di trasporto:");
            int mezzoId = Integer.parseInt(scanner.nextLine());
            System.out.println("Inserisci ID tratta:");
            UUID trattaId = UUID.fromString(scanner.nextLine());
            MezzoTrasporto mezzo = em.find(MezzoTrasporto.class, mezzoId);
            Tratta tratta = em.find(Tratta.class, trattaId);
            if (mezzo == null || tratta == null) {
                throw new ElementoNonTrovatoException("MEZZO O TRATTA NON TROVATI IN DB");
            }
            mtd.stampaNPercorsaTratta(mezzo, tratta);
        } catch (NumberFormatException e) {
            System.out.println("ID NON VALIDO");
        } catch (IllegalArgumentException e) {
            System.out.println("ID NON CORRETTO");
        } catch (ElementoNonTrovatoException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void menuAdmin() {
        boolean running = true;
        while (running) {
            System.out.println("\n--- Menu Admin ---");
            System.out.println("1. Aggiungi mezzo di trasporto");
            System.out.println("2. Update stato mezzo di trasporto");
            System.out.println("3. Inserisci nuova tratta");
            System.out.println("4. Inserisci venditore");
            System.out.println("5. Visualizza statistiche");
            System.out.println("0. Torna indietro");
            System.out.print("Scelta: ");
            String scelta = scanner.nextLine();
            switch (scelta) {
                case "1" -> {
                    creaMezzo(smtd);
                }
                case "2" -> {
                    updateStatoMezzo(smtd);
                }
                case "3" -> creaTratta();
                case "4" -> menuVenditori();
                case "5" -> menustatistiche();
                case "0" -> running = false;
                default -> System.out.println("Scelta non valida, riprova.");
            }
        }
    }

    public static void saliSulMezzo(){
        try {
            System.out.println("Inserisci id del mezzo");
            int mezzoId = Integer.parseInt(scanner.nextLine());
            Optional<MezzoTrasporto> optM = Optional.ofNullable(mtd.getEntityManager().find(MezzoTrasporto.class, mezzoId));
            if(optM.isEmpty()){
                System.out.println("Mezzo non trovato");
                return;
            }
            MezzoTrasporto mezzoTrasporto = optM.get();
            if (mezzoTrasporto.getStato() == Stato.IN_SERVIZIO){
                System.out.println("Scanerizza il biglietto");
                String bigliettoId = scanner.nextLine();
                Optional<Biglietto> optB = Optional.ofNullable(ticketDao.getEntityManager().find(Biglietto.class, UUID.fromString(bigliettoId)));
                if (optB.isEmpty()){
                    System.out.println("Biglietto Non Trovato");
                    return;
                }
                Biglietto biglietto = optB.get();
                if(biglietto.isValidazione()){
                    System.out.println("Questo biglietto è già stato validato");
                }else {
                    System.out.println("validazione in corso");
                    biglietto.setMezzo(mezzoTrasporto);
                    biglietto.setValidazione(true);
                    biglietto.setDataValidazione(LocalDate.now());
                    biglietto.setDataScadenza(LocalDate.now().plusDays(1));

                    ticketDao.aggiornaTicket(biglietto);
                    System.out.println("Biglietto validato con successo");
                }

            }else {
                System.out.println("Il mezzo non è attualmente in servizio");
            }
        }catch (NumberFormatException e){
            System.out.println("INSERISCI UN NUMERO");
        }catch (IllegalArgumentException e){
            System.out.println("Inserisci un UUID valido");
            System.out.println("Es: 02ce3860-3126-42af-8ac7-c2a661134129");
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

}











