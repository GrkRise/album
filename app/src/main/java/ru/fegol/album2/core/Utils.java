package ru.fegol.album2.core;

/**
 * Created by Андрей on 21.06.2015.
 */
public class Utils {

    public static String trimFolderName(String name, int n){
        String trimName;
        if(name.length()>n){
            trimName = name.substring(0, n)+"...";
            return trimName;
        }
        return name;
    }

}
