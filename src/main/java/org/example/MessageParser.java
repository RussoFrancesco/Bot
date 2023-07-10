package org.example;

import org.apache.maven.shared.utils.StringUtils;
import org.telegram.telegrambots.meta.api.objects.Message;

public class MessageParser {

    private String msg_text;
    public MessageParser(String msg){
        this.msg_text=msg;
    }

    public String modify_format(){
        String returnString=msg_text.trim();
        returnString=returnString.toLowerCase();
        String [] parts=returnString.split(":");
        for (int i = 0; i < parts.length; i++) {
            parts[i]=parts[i].trim();
        }
        returnString=String.join(":",parts);
        return returnString;
    }

    public boolean checkFormat(){
        int count= StringUtils.countMatches(this.msg_text,":");
        return count == 2 && (this.msg_text.split(":").length==3 || this.msg_text.split(":").length==2);
    }
    public boolean checkFormatInsertRating(){
        int count= StringUtils.countMatches(this.msg_text,":");
        String[] parts = this.msg_text.split(":");
        int valutazione = Integer.parseInt(parts[1]);
        return ((count == 2)
                && (parts.length == 3 || parts.length == 2)
                && (!parts[1].isEmpty() && !parts[0].isEmpty())
                && (valutazione>=0 && valutazione<=5));
    }

    public  boolean checkFormatInsertRestaurant(){
        int count= StringUtils.countMatches(this.msg_text,":");
        return count == 1 && this.msg_text.split(":").length==2;
    }

}