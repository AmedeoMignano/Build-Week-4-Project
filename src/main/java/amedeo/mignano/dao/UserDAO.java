package amedeo.mignano.dao;

import amedeo.mignano.entities.*;
import jakarta.persistence.*;
import java.time.format.*;
import java.time.LocalDate;
import java.util.Scanner;

public class UserDAO {
    private final EntityManager em;

    public UserDAO(EntityManager em) {
        this.em = em;
    }

    public void saveUser(User user){
        EntityTransaction tr = em.getTransaction();
        tr.begin();
        em.persist(user);
        tr.commit();
    }


}
