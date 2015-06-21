package ru.fegol.album2.ui.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import greendao.Photo;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import ru.fegol.album2.App;
import ru.fegol.album2.R;
import ru.fegol.album2.callbacks.OnFragmentInteractionListener;
import ru.fegol.album2.core.storage.Storage;


public class FullScreenPhotoFragment extends Fragment implements View.OnClickListener {

    private static final String PHOTO_ID = "photoId";

    private Long photoId;
    private Storage storage;
    private Photo photo;

    public static FullScreenPhotoFragment newInstance(Long id) {
        FullScreenPhotoFragment fragment = new FullScreenPhotoFragment();
        Bundle args = new Bundle();
        args.putLong(PHOTO_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    public FullScreenPhotoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            photoId = getArguments().getLong(PHOTO_ID);
        }
        storage = ((App)getActivity().getApplication()).getStorage();
        photo = storage.getPhoto(photoId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_full_screen_photo, container, false);
        ImageView imageView = (ImageView)view.findViewById(R.id.fs_image);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Picasso.with(getActivity())
                .load("file:"+photo.getPath())
                .resize(displayMetrics.widthPixels, displayMetrics.heightPixels)
                .centerInside()
                .into(imageView);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if(actionBar != null) {
            if (actionBar.isShowing())
                actionBar.hide();
            else
                actionBar.show();
        }
    }
}
