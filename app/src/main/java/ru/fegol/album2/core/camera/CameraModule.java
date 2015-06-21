package ru.fegol.album2.core.camera;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ru.fegol.album2.App;
import ru.fegol.album2.core.storage.Storage;

/**
 * Created by Андрей on 15.06.2015.
 */
public class CameraModule {

    private Activity activity;
    private String mFilePhotoPath;
    private Storage storage;

    public CameraModule(Activity activity) {
        this.activity = activity;
        this.storage = ((App)activity.getApplication()).getStorage();
    }

    public void createPhoto(){
        PackageManager pm = activity.getPackageManager();
        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            File photoFile = null;
            try {
                photoFile = createPhotoFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(photoFile != null) {
                i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                activity.startActivityForResult(i, 1);
            }
        } else {
            Toast.makeText(activity, "Camera is not available", Toast.LENGTH_LONG).show();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, long folderId) {
        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            Log.d("test", mFilePhotoPath);
            Log.d("test", String.valueOf(folderId));
            storage.addPhoto(mFilePhotoPath, folderId);
        }
    }

    private File createPhotoFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "album_image_"+timeStamp;
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "album_images");
        if(!dir.exists())
            dir.mkdir();
        File image = new File(dir, imageFileName+".jpg");
        image.createNewFile();
        mFilePhotoPath = image.getAbsolutePath();
        return image;
    }
}
