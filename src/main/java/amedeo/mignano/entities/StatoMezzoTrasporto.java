package amedeo.mignano.entities;

import amedeo.mignano.entities.enums.Stato;
import jakarta.persistence.*;
import java.time.LocalDate;


@Entity
@Table(name = "stati_mezzoTrasporto")
public class StatoMezzoTrasporto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Enumerated(EnumType.STRING)
    @Column(name = "stato")
    private Stato stato;
    @Column(name = "data_inizio")
    private LocalDate dataInizio;
    @Column(name = "data_fine")
    private LocalDate dataFine;

    @ManyToOne
    @JoinColumn(name = "mezzoTrasporto_id")
    private MezzoTrasporto mezzoTrasporto;

    public  StatoMezzoTrasporto(){};
    public StatoMezzoTrasporto(Stato stato, LocalDate dataInizio, MezzoTrasporto mezzoTrasporto) {
        this.stato = stato;
        this.dataInizio = dataInizio;
        this.mezzoTrasporto = mezzoTrasporto;
    }


    public int getId() { return id; }
    public Stato getStato() { return stato; }
    public void setStato(Stato stato) { this.stato = stato; }
    public LocalDate getDataInizio() { return dataInizio; }
    public void setDataInizio(LocalDate dataInizio) { this.dataInizio = dataInizio; }
    public LocalDate getDataFine() { return dataFine; }
    public void setDataFine(LocalDate dataFine) { this.dataFine = dataFine; }
    public MezzoTrasporto getMezzoTrasporto() { return mezzoTrasporto; }
    public void setMezzoTrasporto(MezzoTrasporto mezzoTrasporto) { this.mezzoTrasporto = mezzoTrasporto; }
}
