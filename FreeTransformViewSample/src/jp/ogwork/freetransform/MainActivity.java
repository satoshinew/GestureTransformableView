package jp.ogwork.freetransform;

import jp.ogwork.freetransform.fragment.MainFragment;
import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

    public static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new MainFragment(), MainFragment.class.getName()).commit();
        }
    }
}
