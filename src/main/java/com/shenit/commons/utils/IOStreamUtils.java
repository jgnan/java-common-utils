package com.shenit.commons.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.function.Function;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * IO Stream工具库.
 * @author jiangnan
 *
 */
public final class IOStreamUtils {
    private static final Logger LOG = LoggerFactory.getLogger(IOStreamUtils.class);
    
    /**
     * Read an input stream by line.
     * @param is InputStream to read
     * @param func Function to handle by line
     */
    public static String read(URLConnection conn){
        if(conn == null){
            LOG.warn("[read] no url connection to read");
            return null;
        }
        StringBuilder builder = new StringBuilder();
        try {
            if(conn instanceof HttpURLConnection && ((HttpURLConnection)conn).getResponseCode() != 200){
                HttpURLConnection httpConn = ((HttpURLConnection)conn);
                LOG.warn("[read] Response with error code -> {}, msg -> {}",httpConn.getResponseCode(), httpConn.getResponseMessage());
                return null;
            }
//            conn.setDoInput(true);
            readByLine(conn.getInputStream(),(line) -> {
               builder.append(line).append(ShenStringUtils.LINE_SEPERATOR);
               return true;
            });
            
            String resp = builder.toString();
            if (LOG.isDebugEnabled()) LOG.debug("[readUrl] url[{}] response -> {}", conn.getURL(),resp);
            return resp;
        }
        catch (IOException e) {
            LOG.warn("[readUrl] Read url[{}] with exception.", conn.getURL(),e);
        }finally{
            IOUtils.close(conn);
        }
        return null;
    }
    
    /**
     * Read an input stream by line.
     * @param is InputStream to read
     * @param func Function to handle by line
     */
    public static void readByLine(InputStream is,Function<String,Boolean> func){
        if(func == null){
            LOG.warn("[readByLine] no function supply");
            return;
        }
        BufferedReader reader = readBuffer(is);
        if(reader == null){
            LOG.warn("[readByLine] Could not open input stream");
            return;
        }
        String line;
        try {
            while((line = reader.readLine()) != null){
                if(!DataUtils.toBoolean(func.apply(line))) return;
            }
        } catch (IOException e) {
            LOG.warn("[readByLine] Could not read file due to exception.", e);
        }
    }
    
    /**
     * Write file to a specific file.
     * @param file File location
     * @return
     */
    public static FileWriter writeFile(String file){
        return writeFile(checkFileExists(file));
    }
    /**
     * Return a FileWriter instance to a speicific file
     * @param file
     * @return
     */
    public static FileWriter writeFile(File file){
        if(checkFileExists(file) == null){
            LOG.warn("[writeFile] No file to location -> {}",file);
            return null;
        }
        try {
            return new FileWriter(file);
        }
        catch (IOException e) {
            LOG.warn("[writeFile] Exception throws when tring to open file stream", e);
        }
        return null;
    }
    
    /**
     * Read file to the specific file location
     * @param file
     * @return
     */
    public static FileReader readFile(String file){
        return readFile(checkFileExists(file));
    }
    
    
    /**
     * Check file exists.
     * @param file
     * @return
     */
    public static File checkFileExists(String loc){
        if(StringUtils.isEmpty(loc)){
            LOG.warn("[checkFileExists] No file location!");
            return null;
        }
        return checkFileExists(new File(loc));
    }
    
    /**
     * Check file exists.
     * @param file
     * @return
     */
    public static File checkFileExists(File file){
        try{
            if(file == null || !file.exists()){
                LOG.warn("[checkFileExists] No file to location -> {}",file);
                return null;
            }
            return file;
        }catch(Exception ex){
            //throw exception when error happened
            return null;
        }
    }
    
    /**
     * Read stream as string
     * @param is
     * @return
     */
    public static final String readToString(InputStream is){
        if(is == null) {
            LOG.warn("[toString] Nothing to read");
            return null;
        }
        try {
            byte[] data = new byte[is.available()];
            is.read(data);
            return new String(data,HttpUtils.ENC_UTF8);
        }
        catch (IOException e) {
            LOG.error("[readToString] Read with error.", e);
        }
        return StringUtils.EMPTY;
    }
    
    
    /**
     * Return a FileReader according to the fileLocation
     * @param fileLocation
     * @return
     */
    public static FileReader readFile(File file){
        if(checkFileExists(file) == null){
            LOG.warn("[readFile] No file to location -> {}",file);
            return null;
        }
        try {
            return new FileReader(file);
        }
        catch (FileNotFoundException e) {
            LOG.warn("[readFile] file not found!", e);
        }
        return null;
    }
    
    
    /**
     * Encapsulate a buffer reader for an input stream.
     * @param is
     * @return
     */
    public static BufferedReader readBuffer(InputStream is){
        try {
            if(is == null || is.available() == 0){
                LOG.warn("[readBuffer] Nothing to read.");
                return null;
            }
        }
        catch (IOException e) {
            LOG.warn("[readBuffer] exception throws when trying to read stream",e);
            return null;
        }
        return readBuffer(new InputStreamReader(is));
    }
    
    /**
     * Encapsulate a buffer reader for an input stream.
     * @param is
     * @return
     */
    public static BufferedReader readBuffer(Reader reader){
        if(reader == null){
            LOG.warn("[readBuffer] Nothing to read.");
            return null;
        }
        return new BufferedReader(reader);
    }

    /**
     * Create a print writer instance
     * @param os
     * @return
     */
    public static PrintWriter printWriter(OutputStream os) {
        return os == null ? null : new PrintWriter(os);
    }

    /**
     * Write things to HttpURLConnection.
     * This method won't close the writer for further usage.
     * @param conn
     * @param body
     */
    public static HttpURLConnection write(HttpURLConnection conn, String body) {
        if(conn == null){
            LOG.warn("[write] No connection");
            return conn;
        }
        if(StringUtils.isEmpty(body)){
            LOG.warn("[write] No content to write");
            return conn;
        }
        conn.setDoOutput(true);
        try {
            conn.getOutputStream().write(body.getBytes(HttpUtils.ENC_UTF8));
        } catch (IOException e) {
            LOG.warn("[write] Could not write content to connection due to error", e);
        }
        return conn;
    }

    /**
     * Read a line
     * @param reader
     * @return
     */
    public static String readLine(BufferedReader reader) {
        if(reader == null){
            LOG.warn("[readLine] No reader provided");
            return null;
        }
        try {
            return reader.readLine();
        }
        catch (IOException e) {
            LOG.warn("[readLine] Could not read line due to exception", e);
        }
        return null;
    }
}
