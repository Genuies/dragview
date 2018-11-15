package zx.com.genius.cn.myapplication;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * @author: Genius on 2018/9/30
 * @package: zx.com.genius.cn.myapplication
 * @function:
 */
public class ProgressDialogFragment extends DialogFragment {

    private ProgressBar mProgressBar;
    private TextView mProgressText;

    public static ProgressDialogFragment newInstance(){
        return new ProgressDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_progress, container);
        mProgressBar = view.findViewById(R.id.progress_dialog_bar);
        mProgressText = view.findViewById(R.id.progress_dialog_percent);
        return view;
    }

    public void setProgress(int percent){
        mProgressBar.setProgress(percent);
        mProgressText.setText(new StringBuffer().append(percent).append("%"));
    }

    public int getProgress(){
        return mProgressBar.getProgress();
    }
}
