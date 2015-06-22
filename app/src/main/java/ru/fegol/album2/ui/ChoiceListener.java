package ru.fegol.album2.ui;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import greendao.Photo;
import ru.fegol.album2.App;
import ru.fegol.album2.R;
import ru.fegol.album2.core.storage.Storage;

/**
 * Created by Андрей on 21.06.2015.
 */
public class ChoiceListener implements AbsListView.MultiChoiceModeListener {

    private Set<Long> selectedItems = new HashSet<>();
    private Storage storage;

    public ChoiceListener(App app) {
        storage = app.getStorage();
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        if(checked)
            selectedItems.add(id);
        else
            selectedItems.remove(id);
        mode.setSubtitle(selectedItems.size()+" selected.");
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.lc_menu, menu);
        mode.setTitle("Select photos");
        mode.setSubtitle(selectedItems.size()+" items selected");
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_delete:
                for (Long photoId: selectedItems)
                    storage.deletePhoto(photoId);
                break;
        }
        mode.finish();
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {

    }
}
