package ru.fegol.album2;

import android.app.Application;

import greendao.DaoMaster;
import greendao.DaoSession;
import ru.fegol.album2.core.storage.Storage;
import ru.fegol.album2.core.storage.StorageDb;

/**
 * Created by Андрей on 14.06.2015.
 */
public class App extends Application {

    private DaoSession session;
    private Storage storage;

    @Override
    public void onCreate() {
        super.onCreate();
        setupDatabase();
    }

    private void setupDatabase(){
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "mydb", null);
        session = new DaoMaster(helper.getWritableDatabase()).newSession();
        storage = new StorageDb(session);
    }

    public Storage getStorage() {
        return storage;
    }
}
