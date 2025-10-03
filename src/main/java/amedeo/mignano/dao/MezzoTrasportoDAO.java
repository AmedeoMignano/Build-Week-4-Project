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
    }

    public void updateStato( StatoMezzoTrasportoDAO std, int mezzoId, Stato nuovoStato){
        EntityTransaction transaction = entityManager.getTransaction();
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
                transaction.begin();
                entityManager.merge(mezzo);
                transaction.commit();
                System.out.println("Stato di " + mezzoId + " : " + nuovoStato);
                std.creaSalva(nuovoStato, mezzo);
            } else {
                throw  new ElementoNonTrovatoException("ELEMENTO NON TROVATO IN DB");
    } }

    public void stampaNPercorsaTratta(MezzoTrasporto mezzo, Tratta tratta) {
        List<TempiPercorrenza> tempiPercorrenza = entityManager.createQuery(
                        "SELECT tp FROM TempiPercorrenza tp WHERE tp.mezzoTrasporto.id = :mezzoId AND tp.tratta.id = :trattaId ORDER BY tp.tempoPercorrenzaEffettivo",
                        TempiPercorrenza.class).setParameter("mezzoId", mezzo.getId()).setParameter("trattaId", tratta.getId()).getResultList();

        if (tempiPercorrenza.isEmpty()) {
            System.out.println("NESSUNA TRATTA REGISTRATA PER QUESTO MEZZO TRASPORTO");
        } else {
            int nPercorsaTratta = tempiPercorrenza.size();
                    System.out.println(" Numero di volte che il mezzo di trasporto ha percorso la tratta: " + nPercorsaTratta);
        }
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public List<StatoMezzoTrasporto> getPeriodiMezzo(int mezzoId) {
        return entityManager.createQuery(
                        "SELECT s FROM StatoMezzoTrasporto s WHERE s.mezzoTrasporto.id = :mezzoId ORDER BY s.dataInizio ASC",
                        StatoMezzoTrasporto.class)
                .setParameter("mezzoId", mezzoId)
                .getResultList();
    }

    public List<MezzoTrasporto> getMezziByStatoPeriodo(Stato stato, LocalDate inizio, LocalDate fine) {
        return entityManager.createQuery(
                        "SELECT DISTINCT m " +
                                "FROM MezzoTrasporto m " +
                                "JOIN StatoMezzoTrasporto s ON s.mezzoTrasporto.id = m.id " +
                                "WHERE s.stato = :stato " +
                                "AND s.dataInizio >= :inizio " +
                                "AND (s.dataFine IS NULL OR s.dataFine <= :fine)",
                        MezzoTrasporto.class)
                .setParameter("stato", stato)
                .setParameter("inizio", inizio)
                .setParameter("fine", fine)
                .getResultList();
    }

}

