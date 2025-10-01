package amedeo.mignano.dao;

import amedeo.mignano.entities.MezzoTrasporto;
import amedeo.mignano.entities.Tratta;
import amedeo.mignano.exceptions.ElementoNonTrovatoException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.UUID;

public class TrattaDAO {
    private final EntityManager entityManager;

    public TrattaDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void creaSalva() {
        Scanner scanner = new Scanner(System.in);
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

            System.out.println("Inserisci TEMPO PERCORRENZA EFFETTIVO");
            String tempoEffettivoInput = scanner.nextLine();
            double tempoPercorrenzaEffettivo;
            try {
                tempoPercorrenzaEffettivo = Double.parseDouble(tempoEffettivoInput);
                if (tempoPercorrenzaEffettivo <= 0) {
                    throw new NumberFormatException("INPUT NON VALIDO");
                }
            } catch (NumberFormatException ex) {
                System.out.println(ex.getMessage());
                return;
            }

            int nVolteTrattaPercorsa;
            try {
                System.out.println("Inserisci numero di volte che la tratta viene percorsa: ");
                nVolteTrattaPercorsa = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException ex) {
                System.out.println("INSERISCI SOLO NUMERI");
                return;
            }

            MezzoTrasporto elTrovato;
            try {
                System.out.println("Inserisci ID mezzo di trasporto: ");
                int id = Integer.parseInt(scanner.nextLine());
                elTrovato = entityManager.find(MezzoTrasporto.class, id);
                if (elTrovato == null) {
                    throw new ElementoNonTrovatoException("ELEMENTO NON PRESENTE IN DB O ID INCORRETTO");
                }
            } catch (NumberFormatException ex) {
                System.out.println("INSERISCI SOLO NUMERI");
                return;
            } catch (ElementoNonTrovatoException ex) {
                System.out.println(ex.getMessage());
                return;
            }

            Tratta t = new Tratta(capolinea, partenza, tempoPercorrenzaPrevisto, tempoPercorrenzaEffettivo, nVolteTrattaPercorsa, elTrovato);
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();
            entityManager.persist(t);
            elTrovato.getTratte().add(t);
            entityManager.merge(elTrovato);
            transaction.commit();
            System.out.println("Tratta salvata in DB!\nId: " + t.getId());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
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
