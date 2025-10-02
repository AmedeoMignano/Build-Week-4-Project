package amedeo.mignano.dao;

import amedeo.mignano.entities.MezzoTrasporto;
import amedeo.mignano.entities.StatoMezzoTrasporto;
import amedeo.mignano.entities.TempiPercorrenza;
import amedeo.mignano.entities.Tratta;
import amedeo.mignano.entities.enums.Stato;
import amedeo.mignano.entities.enums.TipoMezzoTrasporto;
import amedeo.mignano.exceptions.ElementoNonTrovatoException;
import amedeo.mignano.exceptions.InputErratoException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;


import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.stream.Collectors;

public class MezzoTrasportoDAO {
    private final EntityManager entityManager;

    public MezzoTrasportoDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void creaSalva(StatoMezzoTrasportoDAO std, MezzoTrasporto mt) {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.persist(mt);
        transaction.commit();
        std.creaSalva(Stato.IN_SERVIZIO, mt);
        System.out.println("Mezzo salvato in DB!\nId: " + mt.getId());
    }

    public void updateStato( StatoMezzoTrasportoDAO std, int mezzoId, Stato nuovoStato){
        EntityTransaction transaction = entityManager.getTransaction();
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
    } }

    public void stampaNPercorsaTratta() {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.println("Inserisci ID mezzo di trasporto:");
            int mezzoId;
            try {
                mezzoId = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                throw new InputErratoException("INSERISCI SOLO NUMERI");
            }
            System.out.println("Inserisci ID tratta:");
            UUID trattaId;
            try {
                trattaId = UUID.fromString(scanner.nextLine());
            } catch (IllegalArgumentException e) {
                throw new InputErratoException("ID NON VALIDO");
            }
            MezzoTrasporto mezzo = entityManager.find(MezzoTrasporto.class, mezzoId);
            if (mezzo == null) {
                throw new ElementoNonTrovatoException("MEZZO DI TRASPORTO NON TROVATO IN DB");
            }
            Tratta tratta = entityManager.find(Tratta.class, trattaId);
            if (tratta == null) {
                throw new ElementoNonTrovatoException("TRATTA NON TROVATA IN DB");
            }
            List<TempiPercorrenza> tempiPercorrenza = entityManager.createQuery(
                            "SELECT tp FROM TempiPercorrenza tp WHERE tp.mezzoTrasporto.id = :mezzoId AND tp.tratta.id = :trattaId ORDER BY tp.tempoPercorrenzaEffettivo", TempiPercorrenza.class)
                    .setParameter("mezzoId", mezzoId)
                    .setParameter("trattaId", trattaId)
                    .getResultList();
            int nPercorsaTratta = tempiPercorrenza.size();
            if (tempiPercorrenza.isEmpty()) {
                System.out.println("NESSUNA TRATTA REGISTRATA PER QUESTO MEZZO TRASPORTO");
            } else {
            System.out.println("Volte che il mezzo di trasporto ha percorso la tratta:  " + nPercorsaTratta);
               List<Double> tpPercEffett = tempiPercorrenza.stream().map(TempiPercorrenza::getTempoPercorrenzaEffettivo).toList();
               tpPercEffett.forEach(System.out::println);
            }
        } catch (InputErratoException | ElementoNonTrovatoException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }
}

