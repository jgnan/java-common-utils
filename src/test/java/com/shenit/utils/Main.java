package com.shenit.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.shenit.commons.utils.IOStreamUtils;
import com.shenit.commons.utils.URLUtils;

public class Main {

    public static void main(String[] args) throws IOException {
        int total = 86799;
        File file = new File("words.csv");
        if(file.exists()) file.delete();
        file.createNewFile();
        FileWriter writer =new FileWriter(file);
        writer.append("Words,Frequency\r\n");
        String pattern = "http://www.wordcount.org/dbquery.php?toFind=%d&method=SEARCH_BY_INDEX";
        Pattern regex = Pattern.compile("&word\\d+=([^&]+)&freq\\d+=([^&]+)");
        for(int startIndex=0;startIndex<total;startIndex+=301){
            HttpURLConnection conn = URLUtils.openConnection(String.format(pattern,startIndex));
            String resp = IOStreamUtils.read(conn);
            resp = resp.substring(resp.indexOf("&word0"));
            Matcher m = regex.matcher(resp);
            while(m.find()){
                writer.append(m.group(1)).append(",").append(m.group(2)).append("\r\n");
            }
            writer.flush();
        }
        writer.close();
        
        
    }

}
