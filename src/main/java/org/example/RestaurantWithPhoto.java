package org.example;

import org.example.DbManager;
import org.example.Recensione;
import org.example.Restaurant;

public class RestaurantWithPhoto extends Restaurant {
    private byte [] photo;
    private static final DbManager dbmanager= new DbManager();

    public RestaurantWithPhoto(String nome, String indirizzo, byte [] photo) {
        super(nome, indirizzo);
        this.photo=photo;
    }
    public RestaurantWithPhoto(int id,byte [] photo) {
        super(id);
        this.photo=photo;
    }

    public RestaurantWithPhoto(int id) {
        super(id);
    }




    public byte[] getPhoto() {
        return photo;
    }

    @Override
    public String getNome() {
        return super.getNome();
    }

    @Override
    public String getIndirizzo() {
        return super.getIndirizzo();
    }

    @Override
    public int getId() {
        return super.getId();
    }

    @Override
    public void inserisci_recensione(Recensione r, long userId) {
        super.inserisci_recensione(r, userId);
    }

    @Override
    public String elimina_ristorante(long userid) {
        return super.elimina_ristorante(userid);
    }

    @Override
    public void inserisci_nel_db(long userid) {
        dbmanager.inserisci_ristorante(userid,this);
    }

    public byte[] getPhotoFromDb(){
       return dbmanager.getPhoto(getId());
    }

    public String mostra_ristorante(){
        return super.mostra_ristorante();
    }
}
