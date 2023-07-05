package org.example;

import java.time.LocalDate;

public class Restaurant {
    private String nome;
    private  String indirizzo;
    private int id;

    private DbManager dbManager=new DbManager();

    public Restaurant (String nome, String indirizzo){
        this.nome=nome;
        this.indirizzo=indirizzo;
    }

    public Restaurant(int id, String nome, String indirizzo){
        this.id = id;
        this.nome = nome;
        this.indirizzo = indirizzo;
    }

    public Restaurant (int id){
        this.id=id;
    }

    public String getNome() {
        return nome;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public int getId(){
        return this.id;
    }
    public void inserisci_nel_db(long userid){
        dbManager.inserisci_ristorante(this,userid);
    }

    public void inserisci_recensione(Recensione r,long userId){
        LocalDate dataCorrente = LocalDate.now();
        java.sql.Date sqlDate = java.sql.Date.valueOf(dataCorrente);

        if (r.getDescrizione() != null)
            dbManager.inserisci_recensione(this.id,r.getValutazione(),r.getDescrizione(),sqlDate,userId);
        else
            dbManager.inserisci_recensione(this.id,r.getValutazione(),sqlDate,userId);
    }

    public  String elimina_ristorante(long userid){
        return dbManager.elimina_ristorante(this.id,userid);
    }

    public String mostra_ristorante(){
        return dbManager.mostra_ristorante(id);
    }

    public String modifica_ristorante(long userId){
        return dbManager.modifica_ristorante(this.id, this.nome, this.indirizzo, userId);
    }

}
