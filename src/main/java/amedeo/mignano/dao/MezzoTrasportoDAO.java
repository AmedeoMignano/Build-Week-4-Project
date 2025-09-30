package amedeo.mignano.dao;

import amedeo.mignano.entities.MezzoTrasporto;
import amedeo.mignano.entities.StatoMezzoTrasporto;
import amedeo.mignano.entities.enums.Stato;
import amedeo.mignano.entities.enums.TipoMezzoTrasporto;
import amedeo.mignano.exceptions.ElementoNonTrovatoException;
import amedeo.mignano.exceptions.InputErratoException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;


import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class MezzoTrasportoDAO {
    private final EntityManager entityManager;

    public MezzoTrasportoDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void creaSalva(StatoMezzoTrasportoDAO std) {
        Scanner scanner = new Scanner(System.in);
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
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.persist(mt);
        transaction.commit();
        std.creaSalva(Stato.IN_SERVIZIO, mt);
        System.out.println("Mezzo salvato in DB!\nId: " + mt.getId());
    }

    public void updateStato( StatoMezzoTrasportoDAO std){
        Scanner scanner = new Scanner(System.in);
        EntityTransaction transaction = entityManager.getTransaction();
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
            transaction.begin();
            MezzoTrasporto mezzo = entityManager.find(MezzoTrasporto.class, mezzoId);
            if (mezzo != null) {
                List<StatoMezzoTrasporto> statiAttivi = entityManager.createQuery("SELECT s FROM StatoMezzoTrasporto s WHERE s.mezzoTrasporto.id = :mezzoId AND s.dataFine IS NULL", StatoMezzoTrasporto.class)
                        .setParameter("mezzoId", mezzoId)
                        .getResultList();
                if (!statiAttivi.isEmpty()) {
                    StatoMezzoTrasporto statoTrovato = statiAttivi.getFirst();
                    statoTrovato.setDataFine(LocalDate.now());
                    entityManager.merge(statoTrovato);
                    System.out.println("Stato precedente AGGIORNATO");
                }
                mezzo.setStato(nuovoStato);
                entityManager.merge(mezzo);
                transaction.commit();
                System.out.println("Stato di " + mezzoId + " : " + nuovoStato);
                std.creaSalva(nuovoStato, mezzo);
            } else {
                throw  new ElementoNonTrovatoException("ELEMENTO NON TROVATO IN DB");
            }
        } catch (InputErratoException | ElementoNonTrovatoException ex) {
            System.out.println(ex.getMessage());
        }
    }
    }

