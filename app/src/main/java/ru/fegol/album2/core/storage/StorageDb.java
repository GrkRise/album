package ru.fegol.album2.core.storage;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.util.List;

import de.greenrobot.dao.query.LazyList;
import greendao.DaoSession;
import greendao.Folder;
import greendao.FolderDao;
import greendao.Photo;
import greendao.PhotoDao;
import ru.fegol.album2.core.observer.Observable;

/**
 * Created by Андрей on 15.06.2015.
 */
public class StorageDb extends Observable implements Storage {

    private DaoSession session;

    public StorageDb(DaoSession session) {
        this.session = session;
    }

    @Override
    public List<Folder> getFolders() {
        return session.getFolderDao().queryBuilder().listLazy();
    }

    @Override
    public Folder getFolder(Long id) {
        return session.getFolderDao().queryBuilder().where(FolderDao.Properties.Id.eq(id)).listLazy().get(0);
    }

    @Override
    public Folder findFolderByName(String name) {
        List<Folder> folders = session.getFolderDao().queryBuilder().where(FolderDao.Properties.Name.eq(name)).listLazy();
        if(folders.isEmpty())
            return null;
        return folders.get(0);
    }

    @Override
    public void renameFolder(Folder folder) {
        session.getFolderDao().update(folder);
        notifyObservers();
    }

    @Override
    public void addFolder(String name) {
        session.getFolderDao().insert(new Folder(null, name));
        notifyObservers();
    }

    @Override
    public void deleteFolder(Long id) {
        session.getFolderDao().deleteByKey(id);
        notifyObservers();
    }

    @Override
    public void addPhoto(String path, Long folderId) {
        session.getPhotoDao().insert(new Photo(null, path, folderId));
        notifyObservers();
    }

    @Override
    public void deletePhoto(Long id) {
        Photo photo = getPhoto(id);
        File file = new File(photo.getPath());
        if(file.exists())
            file.delete();
        session.getPhotoDao().deleteByKey(id);
        notifyObservers();
    }

    @Override
    public List<Photo> getPhotos(Long folderId) {
        LazyList<Photo> list = session.getPhotoDao()
                .queryBuilder()
                .where(PhotoDao.Properties.FolderId.eq(folderId))
                .listLazy();
        return list;
    }

    @Override
    public Photo getPhoto(Long id) {
        return session.getPhotoDao().queryBuilder().where(FolderDao.Properties.Id.eq(id)).list().get(0);
    }
}
