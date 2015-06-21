package ru.fegol.album2.ui.activities;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.software.shell.fab.ActionButton;

import ru.fegol.album2.App;
import ru.fegol.album2.R;
import ru.fegol.album2.callbacks.DetectScroll;
import ru.fegol.album2.callbacks.OnDetectScrollListener;
import ru.fegol.album2.callbacks.OnFragmentInteractionListener;
import ru.fegol.album2.core.camera.CameraModule;
import ru.fegol.album2.core.storage.Storage;
import ru.fegol.album2.ui.fragments.FolderFragment;
import ru.fegol.album2.ui.fragments.PhotoFragment;


public class MainActivity extends AppCompatActivity implements OnFragmentInteractionListener, View.OnClickListener {

    private ActionButton fab;

    private Storage storage;
    private CameraModule camera;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, FolderFragment.newInstance(), "folders")
                    .commit();
        }
        fab = (ActionButton)findViewById(R.id.action_button);
        fab.setOnClickListener(this);
        storage = ((App)getApplication()).getStorage();
        camera = new CameraModule(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void changeFragment(fragments f, Object data) {
        if(f == fragments.FOLDER_FRAGMENT){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, FolderFragment.newInstance(), "folders")
                    .addToBackStack("folders")
                    .commit();
        }
        else if(f == fragments.PHOTO_FRAGMENT){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, PhotoFragment.newInstance((Long)data), "photos")
                    .addToBackStack("photos")
                    .commit();
        }
    }

    @Override
    public OnDetectScrollListener getScrollListener() {
        return new OnDetectScrollListener() {
            @Override
            public void onScrollUp() {
                fab.show();
            }

            @Override
            public void onScrollDown() {
                fab.hide();
            }
        };
    }

    @Override
    public void onClick(View v) {
        String tag = getSupportFragmentManager().findFragmentById(R.id.container).getTag();
        Log.d(getClass().getName(), tag);
        switch (tag){
            case "folders":
                addFolderDialog();
                fab.hide();
                break;
            case "photos":
                camera.createPhoto();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        camera.onActivityResult(requestCode, resultCode, ((PhotoFragment)getSupportFragmentManager().findFragmentById(R.id.container)).getFolderId());
    }

    private void addFolderDialog(){
        Log.d(getClass().getName(), "addFolderDialog");
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_folder, null);
        final EditText folderName = (EditText)dialogView.findViewById(R.id.name_field);
        final TextInputLayout mTextInputLayout = (TextInputLayout)dialogView.findViewById(R.id.text_input);
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppDialogTheme);
        builder.setTitle(R.string.add_dialog_title)
                .setView(dialogView)
                .setPositiveButton(R.string.add_dialog_pos, null)
                .setNegativeButton(R.string.add_dialog_neg, null);
        final AlertDialog d = builder.create();
        d.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button pos = d.getButton(DialogInterface.BUTTON_POSITIVE);
                pos.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = folderName.getText().toString();
                        if (name.equals("")) {
                            mTextInputLayout.setError(getString(R.string.empty_field_name));
                            mTextInputLayout.setErrorEnabled(true);
                        }
                        else if(storage.findFolderByName(name) != null){
                            mTextInputLayout.setError(getString(R.string.folder_exist));
                            mTextInputLayout.setErrorEnabled(true);

                        }
                        else {
                            storage.addFolder(name);
                            d.dismiss();
                        }
                    }
                });
            }
        });
        d.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                fab.show();
            }
        });
        d.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        d.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        d.show();
    }
}
