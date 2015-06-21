package ru.fegol.album2.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import greendao.Photo;
import ru.fegol.album2.App;
import ru.fegol.album2.R;
import ru.fegol.album2.core.storage.Storage;
import ru.fegol.album2.ui.fragments.FullScreenPhotoFragment;

public class FullScreenPhotoActivity extends AppCompatActivity {

    private static String FOLDER_ID = "folderId";

    public static void startSelf(Context context, Long folderId){
        Intent intent = new Intent(context, FullScreenPhotoActivity.class);
        intent.putExtra(FOLDER_ID, folderId);
        context.startActivity(intent);
    }

    private Long folderId;
    private Storage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_photo);
        Intent i = getIntent();
        if (folderId == null)
            folderId = i.getLongExtra(FOLDER_ID, -1);
        storage = ((App)getApplication()).getStorage();
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#551f1f1f")));
        ViewPager pager = (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(new PhotoPagerAdapter(getSupportFragmentManager()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_full_screen_photo, menu);
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

    private class PhotoPagerAdapter extends FragmentStatePagerAdapter{

        List<Photo> photos;

        public PhotoPagerAdapter(FragmentManager fm) {
            super(fm);
            photos = storage.getPhotos(folderId);
        }

        @Override
        public Fragment getItem(int position) {
            return FullScreenPhotoFragment.newInstance(photos.get(position).getId());
        }

        @Override
        public int getCount() {
            return photos.size();
        }
    }
}
