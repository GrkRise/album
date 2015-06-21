package ru.fegol.album2.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import greendao.Photo;
import ru.fegol.album2.App;
import ru.fegol.album2.R;
import ru.fegol.album2.callbacks.DetectScroll;
import ru.fegol.album2.callbacks.OnFragmentInteractionListener;
import ru.fegol.album2.core.Utils;
import ru.fegol.album2.core.observer.Observable;
import ru.fegol.album2.core.observer.Observer;
import ru.fegol.album2.core.storage.Storage;
import ru.fegol.album2.ui.ChoiceListener;
import ru.fegol.album2.ui.activities.FullScreenPhotoActivity;
import ru.fegol.album2.ui.views.CheckableLayout;

public class PhotoFragment extends Fragment implements Observer, AdapterView.OnItemClickListener {

    private static String FOLDER_ID = "folder_id";

    private OnFragmentInteractionListener mListener;
    private Long folderId;

    private GridView gridView;
    private TextView mTextView;
    private PhotoAdapter adapter;
    private Storage storage;

    public static PhotoFragment newInstance(Long folderId){
        PhotoFragment photoFragment = new PhotoFragment();
        Bundle args = new Bundle();
        args.putLong(FOLDER_ID, folderId);
        photoFragment.setArguments(args);
        return photoFragment;
    }

    public PhotoFragment() {
    }

    public Long getFolderId() {
        return folderId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        folderId = getArguments().getLong(FOLDER_ID);
        adapter = new PhotoAdapter(getActivity(), storage.getPhotos(folderId));
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if(actionBar != null)
            actionBar.setTitle(storage.getFolder(folderId).getName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_photo, container, false);
        mTextView = (TextView)root.findViewById(R.id.message);
        gridView = (GridView)root.findViewById(R.id.gridphoto);
        gridView.setAdapter(adapter);
        gridView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        gridView.setMultiChoiceModeListener(new ChoiceListener((App) getActivity().getApplication()));
        gridView.setOnItemClickListener(this);
        gridView.setOnScrollListener(new DetectScroll(gridView,
                getResources().getDimensionPixelOffset(R.dimen.fab_scroll_threshold),
                mListener.getScrollListener()));
        initEmptyMessage(mTextView);
        return root;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        storage = ((App)getActivity().getApplication()).getStorage();
        ((Observable)storage).addObserver(this);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ((Observable)storage).removeObserver(this);
        mListener = null;
    }

    @Override
    public void update() {
        Log.d(getClass().getName(), "update gridView");
        adapter = new PhotoAdapter(getActivity(), storage.getPhotos(folderId));
        gridView.invalidateViews();
        gridView.setAdapter(adapter);
        initEmptyMessage(mTextView);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FullScreenPhotoActivity.startSelf(getActivity(), folderId);
    }

    private void initEmptyMessage(TextView textView){
        if(adapter.photos.isEmpty()){
            textView.setText(R.string.no_photos);
        }
        else
            textView.setText("");
    }

    private class PhotoAdapter extends BaseAdapter{

        private List<Photo> photos;
        private Context context;
        private LayoutInflater inflater;

        public PhotoAdapter(Context context, List<Photo> photos) {
            this.photos = photos;
            this.context = context;
            this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return photos.size();
        }

        @Override
        public Object getItem(int position) {
            return photos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return photos.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View grid;
            if(convertView == null)
                grid = inflater.inflate(R.layout.fragment_photo_item, parent, false);
            else
                grid = convertView;
            File photo = new File(photos.get(position).getPath());
            ImageView imageView = (ImageView)grid.findViewById(R.id.square_image);
            Log.d(getClass().getName(), ""+photo.exists());
            if(photo.exists()) {
                Picasso.with(context)
                        .load(photo)
                        .resize((int) context.getResources().getDimension(R.dimen.image_size), (int) context.getResources().getDimension(R.dimen.image_size))
                        .centerCrop()
                        .into(imageView);
            }
            else {
                Picasso.with(context)
                        .load(R.drawable.warning)
                        .resize((int) context.getResources().getDimension(R.dimen.image_size), (int) context.getResources().getDimension(R.dimen.image_size))
                        .centerCrop()
                        .into(imageView);
            }
            return grid;
        }
    }


}
