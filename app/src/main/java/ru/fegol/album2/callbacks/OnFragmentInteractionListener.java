package ru.fegol.album2.callbacks;

/**
 * Created by Андрей on 15.06.2015.
 */
public interface OnFragmentInteractionListener {
    enum fragments{
        FOLDER_FRAGMENT,
        PHOTO_FRAGMENT
    }
    void changeFragment(fragments f, Object data);
    OnDetectScrollListener getScrollListener();
}
