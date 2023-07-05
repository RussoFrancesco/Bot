package org.example;

import org.apache.maven.shared.utils.StringUtils;
import org.telegram.telegrambots.meta.api.objects.Message;

public class MessageParser {

    private String msg_text;
    public MessageParser(Message msg){
        this.msg_text=msg.getText();
    }

    public String modify_format(){
        String returnString=msg_text.trim();
        //System.out.println(returnString);
        returnString=returnString.toLowerCase();
        System.out.println(returnString);
        String [] parts=returnString.split(":");
        for (int i = 0; i < parts.length; i++) {
            parts[i]=parts[i].trim();
        }
        returnString=String.join(":",parts);
        return returnString;
    }

    public boolean checkFormat(){
        int count= StringUtils.countMatches(this.msg_text,":");
        return count == 2;
    }
    public boolean checkFormatInsertRating(){
        int count= StringUtils.countMatches(this.msg_text,":");
        return count == 2 || count == 1;
    }

    public  boolean checkFormatInsertRestaurant(){
        int count= StringUtils.countMatches(this.msg_text,":");
        return count == 1;
    }

}