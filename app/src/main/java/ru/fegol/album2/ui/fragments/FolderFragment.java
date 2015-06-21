package ru.fegol.album2.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import greendao.Folder;
import ru.fegol.album2.App;
import ru.fegol.album2.R;
import ru.fegol.album2.callbacks.DetectScroll;
import ru.fegol.album2.callbacks.OnFragmentInteractionListener;
import ru.fegol.album2.core.Utils;
import ru.fegol.album2.core.observer.Observable;
import ru.fegol.album2.core.observer.Observer;
import ru.fegol.album2.core.storage.Storage;


public class FolderFragment extends Fragment implements Observer, AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {


    private OnFragmentInteractionListener mListener;
    private Storage storage;
    private FolderAdapter adapter;
    private GridView gridView;
    private TextView mTextView;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FolderFragment.
     */
    public static FolderFragment newInstance() {
        FolderFragment fragment = new FolderFragment();
        return fragment;
    }

    public FolderFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storage = ((App)getActivity().getApplication()).getStorage();
        adapter = new FolderAdapter(getActivity(), storage.getFolders());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_folder, container, false);
        mTextView = (TextView)root.findViewById(R.id.message);
        gridView = (GridView)root.findViewById(R.id.gv);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(this);
        gridView.setOnItemLongClickListener(this);
        gridView.setOnScrollListener(new DetectScroll(gridView,
                getResources().getDimensionPixelOffset(R.dimen.fab_scroll_threshold),
                mListener.getScrollListener()));
        initEmptyMessage(mTextView);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if(actionBar != null)
            actionBar.setTitle(R.string.app_name);
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
        mListener = null;
        ((Observable)storage).removeObserver(this);
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position,final long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppDialogTheme);
        builder.setTitle(R.string.long_click_folder)
                .setItems(new CharSequence[]{getString(R.string.rename_folder), getString(R.string.delete_folder)}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                renameFolderDialog(adapter.folders.get(position));
                                break;
                            case 1:
                                storage.deleteFolder(id);
                                break;
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
        return true;
    }

    @Override
    public void update() {
        Log.d(getClass().getName(), "update gridView");
        adapter = new FolderAdapter(getActivity(), storage.getFolders());
        gridView.invalidateViews();
        gridView.setAdapter(adapter);
        initEmptyMessage(mTextView);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mListener.changeFragment(OnFragmentInteractionListener.fragments.PHOTO_FRAGMENT, id);
    }

    private void initEmptyMessage(TextView textView){
        if(adapter.folders.isEmpty()){
            textView.setText(R.string.no_folders);
        }
        else
            textView.setText("");
    }

    private void renameFolderDialog(final Folder folder){
        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_add_folder, null);
        final EditText folderName = (EditText)dialogView.findViewById(R.id.name_field);
        folderName.setText(folder.getName());
        final TextInputLayout mTextInputLayout = (TextInputLayout)dialogView.findViewById(R.id.text_input);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppDialogTheme);
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
                        } else if (storage.findFolderByName(name) != null) {
                            mTextInputLayout.setError(getString(R.string.folder_exist));
                            mTextInputLayout.setErrorEnabled(true);

                        } else {
                            folder.setName(name);
                            storage.renameFolder(folder);
                            d.dismiss();
                        }
                    }
                });
            }
        });
        d.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        d.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        d.show();
    }

    private class FolderAdapter extends BaseAdapter{

        private List<Folder> folders;
        private LayoutInflater inflater;

        private FolderAdapter(Context context, List<Folder> folders) {
            this.folders = folders;
            inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return folders.size();
        }

        @Override
        public Object getItem(int position) {
            return folders.get(position);
        }

        @Override
        public long getItemId(int position) {
            return folders.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View grid;
            if(convertView == null)
                grid = inflater.inflate(R.layout.folder_item, parent, false);
            else
                grid = convertView;
            TextView folderName = (TextView)grid.findViewById(R.id.name_field);
            folderName.setText(Utils.trimFolderName(folders.get(position).getName(), 8));
            Log.d(getClass().getName(), folderName.getText().toString());
            return grid;
        }
    }

}
