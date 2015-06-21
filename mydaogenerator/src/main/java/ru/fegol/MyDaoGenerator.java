package ru.fegol;


import java.io.File;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

public class MyDaoGenerator {
    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1, "greendao");
        Entity folder = schema.addEntity("Folder");
        folder.addIdProperty().primaryKey().autoincrement();
        folder.addStringProperty("name").notNull();
        Entity photo = schema.addEntity("Photo");
        photo.addIdProperty().primaryKey().autoincrement();
        photo.addStringProperty("path");
        Property folderId = photo.addLongProperty("folderId").getProperty();
        photo.addToOne(folder, folderId);
        File f = new File("../file");
        System.out.println(f.getAbsolutePath());
        new DaoGenerator().generateAll(schema, "C:\\apps\\Album2\\app\\src\\main\\java");
    }
}
