package amedeo.mignano.dao;

import amedeo.mignano.entities.MezzoTrasporto;
import amedeo.mignano.entities.StatoMezzoTrasporto;

import amedeo.mignano.entities.enums.Stato;

import amedeo.mignano.exceptions.ElementoNonTrovatoException;
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

    public StatoMezzoTrasporto getStatoById(int id) {
        StatoMezzoTrasporto stato = entityManager.find(StatoMezzoTrasporto.class, id);
        if (stato == null) {
            System.out.println("Nessuno stato trovato con ID: " + id);
        }
        return stato;
    }
    public void setDataFine(int id) {
        EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();
            try {
                StatoMezzoTrasporto stato = entityManager.find(StatoMezzoTrasporto.class, id);
                if (stato != null) {
                    stato.setDataFine(LocalDate.now());
                    entityManager.merge(stato);
                    transaction.commit();
                    System.out.println("Data fine settata per lo stato con ID: " + id);
                } else {
                    throw  new ElementoNonTrovatoException("ID STATO NON CORRETTO");
                }
            } catch (ElementoNonTrovatoException ex) {
                System.out.println(ex.getMessage());
            }
    }
}

