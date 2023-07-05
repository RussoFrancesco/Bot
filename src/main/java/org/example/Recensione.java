package org.example;

public class Recensione  {
    private int valutazione;
    private String descrizione;


    public Recensione(int rating){
        this.valutazione=rating;
    }

    public Recensione(int rating,String descrizione){
        this.valutazione=rating;
        this.descrizione=descrizione;
    }


    public int getValutazione() {
        return valutazione;
    }

    public void setValutazione(int valutazione) {
        this.valutazione = valutazione;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }




}
