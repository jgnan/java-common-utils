package com.shenit.commons.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Regular expression utils.
 * @author jiangnan
 *
 */
public class RegexUtils {
    private static final Logger LOG = LoggerFactory.getLogger(RegexUtils.class);
    
    /**
     * Find the first string like the pattern specified
     * @param str
     * @param pattern
     * @return
     */
    public static String find(String str, Pattern pattern){
        return find(str,pattern,0);
    }
    
    /**
     * Find the first string like the expression specified
     * @param str
     * @param exp
     * @return
     */
    public static String find(String str, String exp){
        return find(str,exp,0);
    }
    
    /**
     * Find the last value specified in the expression
     * @param str
     * @param exp
     * @return
     */
    public String findLast(String str, String exp){
        return find(str,exp, -1);
    }
    
    /**
     * Find the first string like the pattern specified
     * @param str
     * @param exp Regular expression
     * @return
     */
    public static String find(String str, String exp,int index){
        if(StringUtils.isEmpty(exp)){
            LOG.warn("[find] No pattern input");
            return null;
        }
        return find(str,Pattern.compile(exp),index);
    }
    
    /**
     * Find last
     * @param str
     * @param pattern
     * @return
     */
    public static String findLast(String str, Pattern pattern){
        return find(str,pattern,-1);
    }
    
    /**
     * Find a fragment describe as the regex pattern at the index position.
     * @param str
     * @param pattern
     * @param index
     * @return
     */
    public static String find(String str, Pattern pattern, int index){
        if(StringUtils.isEmpty(str)){
            LOG.warn("[find] No input string");
            return null;
        }
        if(pattern == null){
            LOG.warn("[find] No input pattern");
            return null;
        }
        Matcher matcher = pattern.matcher(str);
        int i = 0;
        String result = null;
        while((index < 0 || i <= index) && matcher.find()) {
            i++;
            result = matcher.group(1);
        }
        return result;    //nothing found;
    }
    
    /**
     * Collects each row of the matching values to a consumer
     * @param str
     * @param exp
     * @param consumer
     */
    public static void collect(String str, String exp, Consumer<String[]> consumer){
        if(StringUtils.isEmpty(exp)){
            LOG.warn("[collect] No expression given");
            return;
        }
        collect(str, Pattern.compile(exp), consumer);
    }
    
    /**
     * Collection each row of the matching values to a consumer
     * @param str
     * @param pattern
     * @param consumer
     */
    public static void collect(String str, Pattern pattern, Consumer<String[]> consumer){
        if(StringUtils.isEmpty(str)){
            LOG.warn("[collect] No input string");
            return;
        }
        if(pattern == null){
            LOG.warn("[collect] No input pattern");
            return;
        }
        if(consumer == null){
            LOG.warn("[collect] No consumer defined");
        }
        Matcher matcher = pattern.matcher(str);
        String[] castType = new String[0];
        while(matcher.find())  {
            List<String> results = new ArrayList<>();
            for(int i=1;i<=matcher.groupCount();i++) results.add(matcher.group(i));
            consumer.accept(results.toArray(castType));
        }
    }
}
