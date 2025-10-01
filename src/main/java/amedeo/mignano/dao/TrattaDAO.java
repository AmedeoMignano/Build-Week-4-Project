package amedeo.mignano.dao;

import amedeo.mignano.entities.MezzoTrasporto;
import amedeo.mignano.entities.Tratta;
import amedeo.mignano.exceptions.ElementoNonTrovatoException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.List;
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
            System.out.println("Tratta salvata in DB!\nId: " + t.getId());
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
        TypedQuery<Object[]> query = entityManager.createQuery("SELECT t.mezzoTrasporto, COUNT(t), AVG(t.tempoPercorrenzaEffettivo)" +
                        "FROM Tratta t" +
                        "WHERE t.id = :trattaId" +
                        "AND t.mezzoTrasporto.id = :mezzoId" +
                        "GROUP BY t.mezzoTrasporto",
                        Object[].class
                );
        query.setParameter("trattaId", trattaId);
        query.setParameter("mezzoId", mezzoId);
        List<Object[]> results = query.getResultList();

        if(results.isEmpty()){
            System.out.println("Nessuna percorrenza trovata per questo mezzo su questa tratta");
        }

    }
}
