package amedeo.mignano.dao;

import amedeo.mignano.entities.MezzoTrasporto;
import amedeo.mignano.entities.TempiPercorrenza;
import amedeo.mignano.entities.Tratta;
import amedeo.mignano.exceptions.ElementoNonTrovatoException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.stream.Collectors;

public class TempiPercorrenzaDAO {
    private final EntityManager entityManager;

    public TempiPercorrenzaDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void creaSalva(TempiPercorrenza tp){
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.persist(tp);
        transaction.commit();
        System.out.println("Tempo Percorrenza salvato in DB!\nId: " + tp.getId());
    }

    public void calcolaTempoMedioTratta() {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.println("Inserisci ID tratta: ");
            UUID idTratta = UUID.fromString(scanner.nextLine());
            Tratta tratta = entityManager.find(Tratta.class, idTratta);
            if (tratta == null) {
                throw new ElementoNonTrovatoException("TRATTA NON TROVATA IN DB");
            }
            TypedQuery<Double> query = entityManager.createQuery(
                    "SELECT tp.tempoPercorrenzaEffettivo FROM TempiPercorrenza tp WHERE tp.tratta.id = :trattaId", Double.class).setParameter("trattaId", idTratta);
            List<Double> tempiPercorrenza = query.getResultList();
            double tempoMedio = tempiPercorrenza.stream().collect(Collectors.averagingDouble(Double::doubleValue));
            System.out.println("Tempo medio di percorrenza: " + tempoMedio + " ore, in percorrenze n: " + tempiPercorrenza.size());
        } catch (IllegalArgumentException e) {
            System.out.println("ID TRATTA NON VALIDO");
        } catch (ElementoNonTrovatoException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
