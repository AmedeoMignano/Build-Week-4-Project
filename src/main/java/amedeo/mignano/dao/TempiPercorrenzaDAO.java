package amedeo.mignano.dao;

import amedeo.mignano.entities.MezzoTrasporto;
import amedeo.mignano.entities.TempiPercorrenza;
import amedeo.mignano.entities.Tratta;
import amedeo.mignano.exceptions.ElementoNonTrovatoException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.Scanner;
import java.util.UUID;

public class TempiPercorrenzaDAO {
    private final EntityManager entityManager;

    public TempiPercorrenzaDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void creaSalva(){
        Scanner scanner = new Scanner(System.in);
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
        Tratta tTrovata;
        try {
            System.out.println("Inserisci ID tratta: ");
            UUID id = UUID.fromString(scanner.nextLine());
            tTrovata = entityManager.find(Tratta.class, id);
            if (tTrovata == null) {
                throw new ElementoNonTrovatoException("ELEMENTO NON PRESENTE IN DB O ID INCORRETTO");
            }
        } catch (ElementoNonTrovatoException ex) {
            System.out.println(ex.getMessage());
            return;
        }
        TempiPercorrenza tp = new TempiPercorrenza(tempoPercorrenzaEffettivo, elTrovato, tTrovata);
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.persist(tp);
        transaction.commit();
        System.out.println("Tempo Percorrenza salvato in DB!\nId: " + tp.getId());
    }
}
