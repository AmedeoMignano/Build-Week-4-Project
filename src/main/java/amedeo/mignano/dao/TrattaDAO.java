package amedeo.mignano.dao;

import amedeo.mignano.entities.Tratta;
import amedeo.mignano.exceptions.ElementoEsistenteException;
import amedeo.mignano.exceptions.ElementoNonTrovatoException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
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
            TypedQuery<Tratta> query = entityManager.createQuery("SELECT t FROM Tratta t WHERE LOWER(t.capolinea) LIKE LOWER(:capolinea) AND LOWER(t.partenza) LIKE LOWER(:partenza)", Tratta.class).setParameter("capolinea", capolinea).setParameter("partenza", partenza);
            Tratta tratta = query.getSingleResultOrNull();
            if (tratta != null) {
                throw  new ElementoEsistenteException("TRATTA GIà PRESENTE IN DB!");
            }
             Tratta t = new Tratta(capolinea, partenza, tempoPercorrenzaPrevisto);
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();
            entityManager.persist(t);
            transaction.commit();
            System.out.println("Tratta salvata in DB!\nId: " + t.getId());
        } catch (ElementoEsistenteException ex) {
            System.out.println(ex.getMessage());
        }
            scanner.close();
    }

    public Tratta getById(String id) {
        try {
            UUID uuid = UUID.fromString(id);
            Tratta elTrovato = entityManager.find(Tratta.class, uuid);
            if (elTrovato == null) {
                throw new ElementoNonTrovatoException("ELEMENTO NON PRESENTE IN DB O ID NON CORRETTO");
            }
            return elTrovato;
        }  catch (IllegalArgumentException e) {
            throw new ElementoNonTrovatoException("ELEMENTO NON PRESENTE IN DB O ID NON CORRETTO");
        }
    };



}
