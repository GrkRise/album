package ru.fegol.album2.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.FrameLayout;

import ru.fegol.album2.R;

/**
 * Created by Андрей on 21.06.2015.
 */
public class CheckableLayout extends FrameLayout implements Checkable {

    private boolean checked;

    public CheckableLayout(Context context) {
        super(context);
    }

    public CheckableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void setChecked(boolean checked) {
        this.checked = checked;
        setBackgroundResource(checked? R.drawable.checkable_background:0);
    }

    @Override
    public boolean isChecked() {
        return checked;
    }

    @Override
    public void toggle() {
        setChecked(!checked);
    }
}
