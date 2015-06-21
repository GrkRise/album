package ru.fegol.album2.core.storage;

import java.util.List;

import de.greenrobot.dao.query.LazyList;
import greendao.Folder;
import greendao.Photo;

/**
 * Created by Андрей on 15.06.2015.
 */
public interface Storage {
    List<Folder> getFolders();
    Folder getFolder(Long id);
    Folder findFolderByName(String name);
    void renameFolder(Folder folder);
    void addFolder(String name);
    void deleteFolder(Long id);
    void addPhoto(String path, Long folderId);
    void deletePhoto(Long id);
    List<Photo> getPhotos(Long folderId);
    Photo getPhoto(Long id);
}
