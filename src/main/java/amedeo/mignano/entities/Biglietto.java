package amedeo.mignano.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "biglietti")
public class Biglietto extends Ticket{
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "mezzoditrasporto_id")
    private MezzoTrasporto mezzo;

    private boolean validazione;

    public Biglietto(){}

    public Biglietto(Venditore venditore, LocalDate dataVendita, User user) {
        super(venditore, dataVendita);
        this.user = user;
        this.validazione = false;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public MezzoTrasporto getMezzo() {
        return mezzo;
    }

    public void setMezzo(MezzoTrasporto mezzo) {
        this.mezzo = mezzo;
    }

    public boolean isValidazione() {
        return validazione;
    }

    public void setValidazione(boolean validazione) {
        this.validazione = validazione;
    }

    public void valida(MezzoTrasporto mezzo){
        if(!this.validazione) {
            this.validazione = true;
            this.dataValidazione = LocalDate.now();
            this.dataScadenza = this.dataValidazione.plusDays(1);
            this.mezzo = mezzo;
        }
    }
}
