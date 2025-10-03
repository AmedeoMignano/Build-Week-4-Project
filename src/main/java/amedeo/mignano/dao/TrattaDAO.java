package amedeo.mignano.dao;

import amedeo.mignano.entities.MezzoTrasporto;
import amedeo.mignano.entities.Tratta;
import amedeo.mignano.exceptions.ElementoNonTrovatoException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.OptionalDouble;
import java.util.UUID;

public class TrattaDAO {
    private final EntityManager entityManager;

    public TrattaDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void creaSalva( String partenza, String capolinea, double tempoPercorrenzaPrevisto) {
            Tratta t = new Tratta(capolinea, partenza, tempoPercorrenzaPrevisto);
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();
            entityManager.persist(t);
            transaction.commit();
            System.out.println(VERDE + "Tratta salvata in DB!\nId: " + t.getId() + RESET);
    }

    public Tratta getById(String id) {
        try {
            UUID uuid = UUID.fromString(id);
            Tratta elTrovato = entityManager.find(Tratta.class, uuid);
            if (elTrovato == null) {
                throw new ElementoNonTrovatoException("ELEMENTO NON PRESENTE IN DB O ID NON CORRETTO");
            }
            return elTrovato;
        } catch (IllegalArgumentException e) {
            throw new ElementoNonTrovatoException("ELEMENTO NON PRESENTE IN DB O ID NON CORRETTO");
        }
    }

    public void calcoloTrattaMezzoAvg(UUID trattaId, int mezzoId){
        TypedQuery<Double> query = entityManager.createQuery(
                "SELECT tp.tempoPercorrenzaEffettivo " +
                        "FROM TempiPercorrenza tp " +
                        "WHERE tp.tratta.id = :trattaId AND tp.mezzoTrasporto.id = :mezzoId",
                Double.class
        ).setParameter("trattaId", trattaId).setParameter("mezzoId", mezzoId);
        List<Double> doubleList = query.getResultList();
        if(doubleList.isEmpty()){
            throw new ElementoNonTrovatoException("Nessuna percorrenza trovata per questo mezzo su questa tratta");
        }
        double media = doubleList.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        System.out.printf(VERDE + STR."Media dei tempi effettivi di percorrenza: \{media}\n" + RESET);
    }
    public static final String GIALLO = "\u001B[33m";
    public static  final String BLU = "\u001B[34m";
    public static final String VERDE = "\u001B[32m";
    public static  final String VIOLA = "\u001B[35m";
    public static final String ROSSO = "\u001B[31m";
    public static final String RESET = "\u001B[0m";

}
