package org.example;

import java.io.File;
import java.io.FileInputStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.SimpleFormatter;

public class DbManager {
    private static final String pathFileXML = "/Users/francescorusso/Desktop/Bot/src/main/resources/config.xml";
    private java.sql.Statement stmt;

    private Connection con;
    private String url;
    private String username ;
    private String password;




    public DbManager(){
        try {
            Config c= new Config(pathFileXML);
            url=c.getDbaddress();
            username=c.getUsername();
            password=c.getPassword();

            System.out.println("forname");
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("mi connetto");
            con = DriverManager.getConnection(url, username, password);
            System.out.println("creo lo statement");
            stmt = con.createStatement();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean userExists(long userid){
        try {
            String query = "SELECT * FROM ristoranti.users WHERE id =?";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setLong(1, userid);
            //System.out.println(statement);
            ResultSet rs = statement.executeQuery();
            int count=0;
            if (rs.next()){
                //System.out.println("qualcosa la abbiamo");
                return true;
            }

            else{
                System.out.println("non c' è nulla");
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void insertUser(long userId) {
        try {
            String query="INSERT INTO ristoranti.users (id) VALUES (?)";
            //System.out.println("query: "+query);
            PreparedStatement preparedStmt=con.prepareStatement(query);
            preparedStmt.setLong(1,userId);
            System.out.println(preparedStmt);
            int row= preparedStmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public DbManager(String path){
        try {
            Config c= new Config(path);
            url=c.getDbaddress();
            username=c.getUsername();
            password=c.getPassword();

            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url, username, password);
            stmt = con.createStatement();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
    public void inserisci_ristorante(Restaurant ristorante,long userId){
        try {

            String query="INSERT INTO ristoranti.ristoranti (nome,indirizzo,user_id) VALUES (?,?,?);";
            System.out.println("prpd stmt");
            PreparedStatement preparedStmt=con.prepareStatement(query);
            preparedStmt.setString(1,ristorante.getNome());
            preparedStmt.setString(2,ristorante.getIndirizzo());
            preparedStmt.setLong(3,userId);
            int row= preparedStmt.executeUpdate();
            // stmt.executeUpdate();
            System.out.println("eseguita!!");
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        }
    }


    public void inserisci_ristorante (long userId, RestaurantWithPhoto ristorante){
        try {

            String query="INSERT INTO ristoranti.ristoranti (nome,indirizzo,user_id,img) VALUES (?,?,?,?);";
            System.out.println("prpd stmt");
            PreparedStatement preparedStmt=con.prepareStatement(query);
            preparedStmt.setString(1,ristorante.getNome());
            preparedStmt.setString(2,ristorante.getIndirizzo());
            preparedStmt.setLong(3,userId);
            preparedStmt.setBytes(4, ristorante.getPhoto());
            int row= preparedStmt.executeUpdate();
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        }
    }



    public String mostra_ristoranti_user(long userid){
        String risposta="";
        try {
            String query="SELECT *  FROM ristoranti.ristoranti WHERE user_id="+userid;
            ResultSet rs =stmt.executeQuery(query);

            if(!rs.next()){
                risposta="Non hai inserito nessun ristorante!";
            }
            else{
                do {
                    risposta = risposta + "<b>Id:</b><i> " + rs.getInt("id") + "</i><b> Nome:</b><i> " + rs.getString("nome") + "</i><b> Indirizzo:</b><i> " + rs.getNString("indirizzo") + "</i>\n";
                }
                while (rs.next());
            }

        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        }
        return  risposta;
    }


    public int get_restaurant_id(Restaurant r){
        try {
            String indirizzo=r.getIndirizzo();
            String nome=r.getNome();
            String query="SELECT *  FROM ristoranti.ristoranti WHERE nome='"+nome+"' AND indirizzo='"+indirizzo+"'";
            ResultSet rs =stmt.executeQuery(query);
            if (rs.next()){
               return rs.getInt("id");
            }

        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        }
        return -1;
    }


    public String elimina_ristorante(int id,long userId){
        String query="DELETE   FROM ristoranti.ristoranti WHERE id="+id+" AND user_id="+userId;
        System.out.println(query);
        int rowsAffected=0;
        try {
            rowsAffected = stmt.executeUpdate(query);
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        }
        if (rowsAffected==0)
            return  "Eliminazione  fallita";
        else
            return "Eliminazione eseguita";
    }


    public String recents_restaurant(){
        String risposta="";
        try{
        String query= "SELECT id,nome, indirizzo FROM ristoranti.ristoranti ORDER BY id DESC;";
        ResultSet rs= stmt.executeQuery(query);
        for (int i = 0; i < 5; i++) {
                rs.next();
                risposta = risposta+"<b>Id:</b><i> "+rs.getInt("id")+"</i><b> Nome:</b><i> "+rs.getString("nome")+"</i><b> Indirizzo:</b><i> "+rs.getNString("indirizzo")+"</i>\n";
        }
        }
        
        catch (SQLException e){
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        }
        return risposta;
    }


    public void inserisci_recensione(int id_r,int rating, String descrizione,Date sqlDate,long userId) {
        try {

            String query = "INSERT INTO ristoranti.recensioni (data_recensione, rating, descrizione, id_ristorante,user_id) VALUES (?,?,?,?,?);";
            PreparedStatement preparedStmt = con.prepareStatement(query);
            preparedStmt.setDate(1, sqlDate);
            preparedStmt.setInt(2, rating);
            preparedStmt.setString(3, descrizione);
            preparedStmt.setInt(4, id_r);
            preparedStmt.setLong(5,userId);
            //System.out.println(preparedStmt);
            int row = preparedStmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void inserisci_recensione (int id_r,int rating,Date sqlDate,long userId) {
        try {
            String query = "INSERT INTO ristoranti.recensioni (data_recensione, rating, id_ristorante,user_id) VALUES (?,?,?,?);";
            PreparedStatement preparedStmt=con.prepareStatement(query);
            preparedStmt.setDate(1,sqlDate);
            preparedStmt.setInt(2,rating);
            preparedStmt.setInt(3,id_r);
            preparedStmt.setLong(4,userId);
            //System.out.println(preparedStmt);
            int row = preparedStmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String mostra_ristoranti_all(){
        String risposta="";
        String query="SELECT nome,indirizzo,id FROM ristoranti.ristoranti WHERE 1";
        try {
            ResultSet rs = stmt.executeQuery(query);
            if(!rs.next()){
                risposta="Non ci sono ristoranti nel db!";}
            else{
                    do {
                        risposta = risposta + "<b>Id:</b><i> " + rs.getInt("id") + "</i><b> Nome:</b> <i>" + rs.getString("nome") + "</i> <b>Indirizzo:</b> <i>" + rs.getNString("indirizzo") + "</i>\n";
                    }while (rs.next());
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return risposta;
        }

        public String mostra_ristorante(int id){
        String risposta ="";
        String query = "SELECT nome, indirizzo,data_recensione, rating, descrizione " +
                "FROM ristoranti.recensioni, ristoranti.ristoranti " +
                "WHERE ristoranti.id=recensioni.id_ristorante AND id_ristorante="+id;

        try {
            ResultSet rs = stmt.executeQuery(query);
            if(!rs.next()){
                risposta="Non ci sono recensioni associate al ristorante!";}
            else{
                do {
                    risposta = risposta + "<b>Ristorante</b><i> "+rs.getString("nome")+"</i> in <i>"+rs.getString("indirizzo")
                            +"</i>\n\n <b>Data:</b><i> " + new SimpleDateFormat("dd-MM-yyyy").format(rs.getDate("data_recensione")) + "</i><b> Valutazione:</b><i> " +rs.getInt("rating")
                            + "</i><b> Descrizione:</b><i> " + rs.getString("descrizione") + "</i>\n";
                }while (rs.next());
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return risposta;
        }

        public boolean hasPhoto(int id){
        String query = "SELECT img FROM ristoranti.ristoranti WHERE id="+id;
        try{
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next() && rs.getBlob("img")!=null){
                return true;
            }
            else{
                return false;
            }
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
        }

        public byte [] getPhoto(int id){
        byte[] blobData = null;
        String query = "SELECT img FROM ristoranti.ristoranti WHERE id="+id;
        try{
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                Blob blob = rs.getBlob("img");
                System.out.println(blob);
                blobData = blob.getBytes(1, (int) blob.length());
                System.out.println(blobData);
            }
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
        return blobData;
        }

        public String recensioni_recenti(){
            String risposta="";
            String query="SELECT data_recensione, rating, descrizione, nome, indirizzo FROM "+
                    "ristoranti.recensioni JOIN ristoranti.ristoranti ON id_ristorante=id ORDER BY (id_recensione) DESC";

            try {
                ResultSet rs=stmt.executeQuery(query);
                for (int i = 0; i < 5; i++) {
                    if(rs.next()){
                        risposta=risposta+"<b>Ristorante</b><i> "+rs.getString("nome")+"</i> in <i>"+rs.getString("indirizzo")+
                        "</i>\n\b<b>Rating:</b><i>"+rs.getInt("rating")+"</i> del <i>"+new SimpleDateFormat("dd-MM-yyyy").format(rs.getDate("data_recensione"))+
                        "</i> \n <b>Descrizione:</b><i> "+rs.getString("descrizione")+"</i>\n\n";
                    }

                }
            }
            catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return risposta;
        }

    public void startUpdateTask() {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                //System.out.println("Eseguo l'aggiornamento delle medie");

                CalcolaMedia calcolaMedia = new CalcolaMedia(stmt);
                calcolaMedia.run();
            }
        };

        timer.schedule(task, 0, 10000);
        }

        public String show_best_restaurant(){
        String risposta="";
        String query = "SELECT id, nome, indirizzo, media FROM ristoranti.ristoranti ORDER BY media DESC";
        try{
            ResultSet rs = stmt.executeQuery(query);
            for (int i = 0; i < 5; i++){
                if(rs.next()){
                    risposta = risposta+ "<b>Id:</b><i>"+rs.getInt("id")+"</i>\n<b>Nome:</b><i>"+rs.getString("nome")+"</i>\n<b>Indirizzo:</b><i>"+rs.getString("indirizzo")+"</i>\n<b>Media:</b><i>"+rs.getDouble("media")+"</i>\n\n";
                }
            }

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        return risposta;
        }

    public String mostra_recensioni_user(long userId) {
        String risposta="";
        try {
            String query="SELECT id_recensione, data_recensione, rating, descrizione, nome, indirizzo  FROM ristoranti.recensioni JOIN ristoranti.ristoranti ON recensioni.id_ristorante = ristoranti.id WHERE ristoranti.recensioni.user_id="+userId;
            ResultSet rs =stmt.executeQuery(query);

            if(!rs.next()){
                risposta="Non hai inserito nessuna recensione!";
            }
            else{
                do {
                    risposta=risposta+"<b>Id recensione:</b><i> "+rs.getInt("id_recensione")+"</i><b> Data:</b><i> "+new SimpleDateFormat("dd-MM-yyyy").format(rs.getDate("data_recensione"))+
                            "</i>\n<b>Rating:</b><i>"+rs.getInt("rating")+"</i><b> Descrizione:</b><i> "+rs.getString("descrizione")+
                            "</i> \n <b>Ristorante:</b><i> "+rs.getString("nome")+"</i><b> Indirizzo:</b><i> "+rs.getString("indirizzo")+"</i>\n\n";                }
                while (rs.next());
            }

        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        }
        return  risposta;
    }

    public String elimina_recensione(int id, long userId) {
        String query="DELETE FROM ristoranti.recensioni WHERE id_recensione="+id+" AND user_id="+userId;
        System.out.println(query);
        int rowsAffected=0;
        try {
            rowsAffected = stmt.executeUpdate(query);
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        }
        if (rowsAffected==0)
            return  "Eliminazione  fallita";
        else
            return "Eliminazione eseguita";
    }

    public String modifica_ristorante(int id, String nome, String indirizzo, long userId) {
        String query = "SELECT id FROM ristoranti.ristoranti WHERE id="+id+" AND user_id="+userId;
        System.out.println(query);
        int rowsAffected = 0;
        try{
            ResultSet rs = stmt.executeQuery(query);
            if(!rs.next()){
                return "Seleziona un ristorante valido";
            }
            else{
                if(!nome.isEmpty()){
                    try{
                    query = "UPDATE ristoranti.ristoranti SET nome='"+nome+"' WHERE id="+id;
                    System.out.println(query);
                    stmt.executeUpdate(query);
                    rowsAffected = stmt.executeUpdate(query);
                    }catch (SQLException e){
                        throw new RuntimeException(e);
                    }
                }
                if(!indirizzo.isEmpty()){
                    try{
                    query = "UPDATE ristoranti.ristoranti SET indirizzo='"+indirizzo+"' WHERE id="+id;
                    System.out.println(query);
                    stmt.executeUpdate(query);
                    rowsAffected = stmt.executeUpdate(query);
                    }catch (SQLException e){
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
        if (rowsAffected==0)
            return  "Modifica  fallita";
        else
            return "Modifica eseguita";
    }

    public String modifica_recensione(int id, Integer rating, String descrizione, long userId){
        String query = "SELECT id_recensione FROM ristoranti.recensioni WHERE id_recensione="+id+" AND user_id="+userId;
        System.out.println(query);
        int rowsAffected = 0;
        try{
            ResultSet rs = stmt.executeQuery(query);
            if(!rs.next()){
                return "Seleziona una recensione valida";
            }
            else{
                if(rating!=-1){
                    try {
                    query = "UPDATE ristoranti.recensioni SET rating="+rating+", data_recensione='"+java.sql.Date.valueOf(LocalDate.now()) +"' WHERE id_recensione="+id;
                    System.out.println(query);
                    stmt.executeUpdate(query);
                    rowsAffected = stmt.executeUpdate(query);
                    }catch (SQLException e){
                        throw new RuntimeException(e);
                    }
                }
                if(!descrizione.isEmpty()){
                    try {
                    query = "UPDATE ristoranti.recensioni SET descrizione='"+descrizione+"', data_recensione='"+java.sql.Date.valueOf(LocalDate.now())+"' WHERE id_recensione="+id;
                    System.out.println(query);
                    stmt.executeUpdate(query);
                    rowsAffected = stmt.executeUpdate(query);
                    }catch (SQLException e){
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
        if (rowsAffected==0)
            return  "Modifica  fallita";
        else
            return "Modifica eseguita";
    }

    public void update_last_msg(long userid, String msg) {
        String query="UPDATE ristoranti.users SET last_msg=? WHERE id=?";
        try {
            PreparedStatement preparedStatement=con.prepareStatement(query);
            preparedStatement.setString(1,msg);
            preparedStatement.setLong(2,userid);
            preparedStatement.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public  String getLast_msg(long userid){
        String last_msg="";
        String query="SELECT * FROM ristoranti.users WHERE id=?";
        try {
            PreparedStatement preparedStatement=con.prepareStatement(query);
            preparedStatement.setLong(1,userid);
            ResultSet rs=preparedStatement.executeQuery();
            if (rs.next()){
                last_msg=rs.getString("last_msg");
            }
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return last_msg;
    }
}

