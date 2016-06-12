package com.example;

import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class DaoGenerator {
    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1, "com.binhnv.hdsplash.provider");

        Entity imageDB = schema.addEntity("ImageDB");

        imageDB.addIdProperty().autoincrement();
        imageDB.addStringProperty("author");
        imageDB.addIntProperty("category");
        imageDB.addStringProperty("color");
        imageDB.addIntProperty("date");
        imageDB.addIntProperty("displayDate");
        imageDB.addIntProperty("featured");
        imageDB.addStringProperty("identifier");
        imageDB.addIntProperty("loved");
        imageDB.addIntProperty("width");
        imageDB.addIntProperty("height");
        imageDB.addLongProperty("modifiedDate");
        imageDB.addStringProperty("platform");
        imageDB.addStringProperty("thumbnail");
        imageDB.addStringProperty("url");

        new de.greenrobot.daogenerator.DaoGenerator().generateAll(schema, "/Workspaces/Android_Studio_Workspace/HDSPlash/app/src/main/java/");
    }
}
