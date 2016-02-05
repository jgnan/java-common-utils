package com.shenit.commons.utils;
/**
 * Array utils.
 * @author jiangnan
 *
 */
public final class ArrayUtils {
    /**
     * Check whether an array is empty
     * @param array Array
     * @return
     */
    public static <T> boolean isEmpty(T[] array){
        return array == null || array.length == 0;
    }
    
    /**
     * Get value from index
     * @param array Array
     * @param i Index to get from array
     * @return
     */
    public static <T> T get(T[] array, int i){
        return get(array,i,null);
    }
    
    /**
     * Get value from index
     * @param array Array
     * @param i Index to get from array
     * @param defaultVal Default value if index is out of bounce
     * @return
     */
    public static <T> T get(T[] array, int i, T defaultVal){
        return array == null || Math.abs(i) > array.length - 1 ?  
                        defaultVal : array[i >= 0 ? i : array.length - 1 - Math.abs(i)];
    }
}
