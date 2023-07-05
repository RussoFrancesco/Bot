package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.util.List;


public class Bot extends TelegramLongPollingBot {
    private String name;
    private String token;


    private static final String pathFileXML = "/Users/francescorusso/Desktop/Bot/src/main/resources/config.xml";
    private static final String pathComandi = "/Users/francescorusso/Desktop/Bot/src/main/resources/comandi.txt";


    private Message last_msg;
    private DbManager dbm=new DbManager();
    private byte [] imgdata;


    public Bot() {
        Config configurationFile = new Config(pathFileXML);
        this.name = configurationFile.getBotName();
        this.token = configurationFile.getToken();
        dbm.startUpdateTask();
    }

    @Override
    public String getBotUsername() {
        return this.name;
    }

    @Override
    public String getBotToken() {
        return this.token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        var msg = update.getMessage();
        var user = msg.getFrom();
        long userId = user.getId();

        if (!dbm.userExists(userId)){
            dbm.insertUser(userId);
        }


        if(msg.isCommand()) {
            String comando= msg.getText();
            switch (comando){
                case "/start":
                    starting(userId);
                    break;
                case  "/add":
                    String message = "Inserisci il nome del ristorante e l'indirizzo con il formato\n <b>Nome:indirizzo</b>";
                    sendText(userId, message);
                    break;
                case "/show_my_restaurant":
                    sendText(userId,dbm.mostra_ristoranti_user(userId));
                    break;
                case "/delete":
                    sendText(userId,"Inserisci l'id del ristorante: ");
                    break;
                case "/show_recents_restaurant":
                    sendText(userId,dbm.recents_restaurant());
                    break;

                case "/add_rating":
                    sendText(userId,"Inserisci nel seguente ordine: <b>idRistorante:rating:Descrizione</b> \n " +
                            "Ricorda il rating deve essere un numero compreso tra 0 e 5 ! \n" +
                            "<u>La descrizione non è obbligatoria! </u>");
                    break;
                case "/show_all":
                    sendText(userId,dbm.mostra_ristoranti_all());
                    break;
                case "/add_with_photo":
                    sendText(userId,"Manda la foto del ristorante");
                    break;
                case "/show_restaurant":
                    sendText(userId, "Inserisci l'id del ristorante\n Se non lo sai usa il comando \n /show_all");
                    break;
                case "/show_recents_ratings":
                    sendText(userId,dbm.recensioni_recenti());
                    break;
                case "/show_best_restaurant":
                    sendText(userId, dbm.show_best_restaurant());
                    break;
                case "/delete_rating":
                    sendText(userId, "Inserisci l'id della recensione da eliminare\n Se non lo sai usa il comando \n/show_my_ratings");
                    break;
                case "/modify":
                    sendText(userId, "Inserisci l'id del ristorante e gli elementi da modificare usando questo formato\n \b<b>Id:Nome:Indirizzo\n Se non vuoi modificare un campo lascialo vuoto</b>\n Se non ricordi l'id del ristorante usa il comando /show_my_restaurant");
                    break;
                case "/modify_rating":
                    sendText(userId, "Inserisci l'id della recensione e gli elementi da modificare usando questo formato\n <b> Id:Valutazione:Descrizione\n Se non vuoi modificare la valutazione metti -1, se non vuoi modificare la descrizione lasciala vuota</b>\n Se non ricordi le tue recensioni usa il comando /show_my_ratings");
                    break;
                case "/show_my_ratings":
                    sendText(userId, dbm.mostra_recensioni_user(userId));
                    break;
            }

        }

        else{
            String p=dbm.getLast_msg(userId);
            System.out.println("prova "+p);

            switch (dbm.getLast_msg(userId)) {
                case "/add":
                    Restaurant r = get_info_from_message(msg.getText());
                    r.inserisci_nel_db(userId);
                    sendText(userId, "Ristorante inserito nel db!");
                    break;

                case "/add_with_photo":
                    if (msg.hasPhoto()) {
                        System.out.println("ok");
                        List<PhotoSize> photos = msg.getPhoto();
                        PhotoSize lastPhoto = photos.get(photos.size() - 1);
                        String photoId = lastPhoto.getFileId();
                        System.out.println(photoId);
                        imgdata = download_image(photoId);
                        System.out.println(imgdata);
                        sendText(userId, "ora manda le info");
                        dbm.update_last_msg(userId,"/info_foto");
                        System.out.println("il messaggii: "+dbm.getLast_msg(userId));
                    }
                    break;

                case "/info_foto":
                    System.out.println(msg.getText());
                    RestaurantWithPhoto restaurantWithPhoto2=get_info_from_message(msg.getText(),imgdata);
                    restaurantWithPhoto2.inserisci_nel_db(userId);
                    sendText(userId,"Ristorante e foto inserito nel db!");
                    break;

                case "/add_rating":
                    String messaggio = msg.getText();
                    System.out.println(messaggio);
                    String[] parts = messaggio.split(":");
                    int id_ristorante = Integer.parseInt(parts[0]);
                    Restaurant restaurant = new Restaurant(id_ristorante);
                    int rating = Integer.parseInt(parts[1]);
                    Recensione recensione;
                    if (parts.length == 3 && !(parts[2].isEmpty())) {
                        recensione = new Recensione(rating, parts[2]);
                    } else {
                        recensione = new Recensione(rating);
                    }
                    restaurant.inserisci_recensione(recensione, userId);
                    sendText(userId, "Recensione inviata al db");
                    break;


                case "/delete":
                    messaggio = msg.getText();
                    int id = Integer.parseInt(messaggio);
                    restaurant = new Restaurant(id);
                    sendText(userId, restaurant.elimina_ristorante(userId));
                    break;

                case "/show_restaurant":
                    id = Integer.parseInt(msg.getText());
                    if(!dbm.hasPhoto(id)){
                        restaurant=new Restaurant(id);
                        sendText(userId,restaurant.mostra_ristorante());
                    }
                    else{
                        RestaurantWithPhoto restaurantWithPhoto= new RestaurantWithPhoto(id);
                        ByteArrayInputStream inputStream = new ByteArrayInputStream(restaurantWithPhoto.getPhotoFromDb());
                        SendPhoto sendphoto = new SendPhoto();
                        sendphoto.setChatId(Long.toString(msg.getChatId()));
                        sendphoto.setPhoto(new InputFile(inputStream, "photo"));
                        try {
                            execute(sendphoto);
                            inputStream.close();

                            sendText(userId, restaurantWithPhoto.mostra_ristorante());
                        } catch (TelegramApiException | IOException e) {
                            throw new RuntimeException(e);
                        }

                    }
                    break;
                case "/delete_rating":
                    id = Integer.parseInt(msg.getText());
                    sendText(userId, dbm.elimina_recensione(id, userId));
                    break;

                case "/modify":
                    parts = msg.getText().split(":");
                    restaurant= null;
                    if (parts.length==2){
                        String indirizzo = "";
                        restaurant = new Restaurant(Integer.parseInt(parts[0]), parts[1], indirizzo);
                    } else if (parts.length == 3) {
                        restaurant = new Restaurant(Integer.parseInt(parts[0]), parts[1], parts[2]);
                    }
                    System.out.println(parts.length);
                    System.out.println(parts[0]+ " "+parts[1]);
                    sendText(userId, restaurant.modifica_ristorante(userId));
                    break;
                case "/modify_rating":
                    parts = msg.getText().split(":");
                    System.out.println(parts.length);
                    System.out.println(parts[0]+ " "+parts[1]);
                    if(parts.length==2){
                        String descrizione = "";
                        sendText(userId, dbm.modifica_recensione(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), descrizione, userId));
                    }
                    else {
                        sendText(userId, dbm.modifica_recensione(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), parts[2], userId));
                    }

                    break;
            }
        }

        last_msg = msg;
        if (!msg.getText().isEmpty()) {
            dbm.update_last_msg(userId, msg.getText());
        }
    }
    public void sendText(Long who, String what) {
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString())//Who are we sending a message to
                .text(what).build();    //Message content
        sm.enableHtml(true);
        try {
            execute(sm);                        //Actually sending the message
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);      //Any error will be printed here
        }
    }

    private void starting(Long userId) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(pathComandi));
            String line;
            String list = null;

            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                String modifiedLine = "/" + line+"\n";
                if(list == null)
                    list = modifiedLine;
                else
                    list = list + modifiedLine;
            }
            reader.close();
            sendText(userId, list);
        } catch (IOException e) {
            System.out.println("Si è verificato un errore durante la lettura del file.");
            e.printStackTrace();
        }
    }

    private byte[] download_image(String photoId) {
        String file_url="https://api.telegram.org/bot"+getBotToken()+"/getFile?file_id="+photoId;
        System.out.println(file_url+" ");
        Downloader d=new Downloader();
        byte[] imageData=d.download(file_url,getBotToken());
        return imageData;
    }

    private Restaurant get_info_from_message(String text){
        String[] parts =text.split(":");
        Restaurant r = new Restaurant(parts[0], parts[1]);
        System.out.println(parts[0]+" "+parts[1]);
        return  r;
    }

    private RestaurantWithPhoto get_info_from_message(String text, byte [] photo){
        String[] parts =text.split(":");
        RestaurantWithPhoto r = new RestaurantWithPhoto(parts[0], parts[1],photo);
        return  r;
    }

    private boolean isNumeric(String string){
        if(string == null || string.equals("")) {
            return false;
        }
        try {
            int intValue = Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
            
        }
        return false;
    }
}


