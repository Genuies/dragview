package zx.com.genius.cn.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import zx.com.genius.cn.pointview.DragView;
import zx.com.genius.cn.pointview.listener.DragListener;

public class MainActivity extends AppCompatActivity {

    private DragView dragView;


    private DragListener mDragListener = new DragListener() {
        @Override
        public void reset() {
            Toast.makeText(MainActivity.this, "回复原位", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void disappear() {
            Toast.makeText(MainActivity.this, "消失", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dragView = findViewById(R.id.person_drag_one);
        dragView.setDragListener(mDragListener);
    }
}
