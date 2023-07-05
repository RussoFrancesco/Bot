package org.example;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class CalcolaMedia implements Runnable{
private Statement stmt;
private ArrayList <Integer> idList=new ArrayList<>();
    public CalcolaMedia(Statement stmt){
        this.stmt=stmt;
    }

    @Override
    public void run() {
        //System.out.println("AGGIORNO");
        String query="SELECT DISTINCT id_ristorante FROM ristoranti.recensioni";
        try {
            ResultSet resultSet=stmt.executeQuery(query);
            while (resultSet.next()){
                //System.out.println(resultSet.getInt("id_ristorante"));
                idList.add(resultSet.getInt("id_ristorante"));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);}

        for (int id: idList) {
            query= "SELECT AVG(rating) as media FROM ristoranti.recensioni WHERE id_ristorante="+id;
            try {
                ResultSet resultSet=stmt.executeQuery(query);
                resultSet.next();
                double media= resultSet.getDouble("media");
                String queryUpdate="UPDATE ristoranti.ristoranti SET media="+media+" WHERE id="+id;
                stmt.executeUpdate(queryUpdate);
                //System.out.println("media: "+media + "   id "+ id);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }


    }
}
