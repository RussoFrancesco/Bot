package org.example;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;


//definisco classe Config
public class Config {
    private String token;
    private String botName;
    private String dbaddress;
    private String username;
    private  String password;

    public Config(String filepath){
        try {
            //creo oggettto di tipo file partendo dal path
            File xmlFile=new File(filepath);
            //creo ogg. di tipo documentbuilder per riuscire a recuperare dati dal file xml
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            //recupero gli elementi name e token
            Element rootElement = doc.getDocumentElement();
            Node nameNode = rootElement.getElementsByTagName("name").item(0);
            Node tokenNode = rootElement.getElementsByTagName("token").item(0);
            Node addressNode = rootElement.getElementsByTagName("dbaddress").item(0);
            Node userNode=rootElement.getElementsByTagName("dbuser").item(0);
            Node passwdNode=rootElement.getElementsByTagName("dbpassword").item(0);



            //Assgno alle variabili d'istanza
            setBotName(nameNode.getTextContent());
            setToken(tokenNode.getTextContent());
            setDbaddress(addressNode.getTextContent());
            setUsername(userNode.getTextContent());
            setPassword(passwdNode.getTextContent());
        }
        catch (IOException e){
            System.out.println("ERRORE IO");
            e.printStackTrace();
        }
        catch (ParserConfigurationException e){
            System.out.println("ERRORE PARSING");
            throw new RuntimeException(e);
        }
        catch (SAXException e){
            System.out.println("errore ");
            throw new RuntimeException(e);
        }

    }

    public String getToken() {
        return token;
    }

    public String getBotName() {
        return botName;
    }

    public String getDbaddress() {return dbaddress;}

    private void setDbaddress(String dbaddress) {
        this.dbaddress = dbaddress;}

    public String getUsername() {return username;}

    private void setUsername(String username) {
        this.username = username;}

    private void setPassword(String password) {
        this.password = password;}

    public String getPassword(){return password;}

    private void setToken(String token){
        this.token=token;}

    private  void setBotName(String name){
        this.botName=name;}
}