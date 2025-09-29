package amedeo.mignano.dao;

import amedeo.mignano.entities.MezzoTrasporto;
import amedeo.mignano.entities.StatoMezzoTrasporto;

import amedeo.mignano.entities.enums.Stato;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;


import java.time.LocalDate;


public class StatoMezzoTrasportoDAO {
    private final EntityManager entityManager;

    public StatoMezzoTrasportoDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void creaSalva(Stato stato, MezzoTrasporto mt) {
        LocalDate dataInizio = LocalDate.now();
            StatoMezzoTrasporto s = new StatoMezzoTrasporto(stato, dataInizio, mt);
            EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
            entityManager.persist(s);
            transaction.commit();
            System.out.println("Stato salvato in DB!\nId: " + s.getId());
    }
}
