package jp.ogwork.freetransform;

import static android.content.Intent.ACTION_PICK;
import static android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;

import com.squareup.picasso.Callback.EmptyCallback;
import com.squareup.picasso.Picasso;

import jp.ogwork.freetransform.R;
import jp.ogwork.freetransform.view.GestureImageView;

public class MainActivity extends Activity {

    public static final String TAG = MainActivity.class.getName();

    private static final int GALLERY_REQUEST = 9391;

    private ContentResolver cr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment(), PlaceholderFragment.class.getName()).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private GestureImageView imageView;

        private ImageView ivThumbnail1;
        
        public PlaceholderFragment() {
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            Intent gallery = new Intent(ACTION_PICK, EXTERNAL_CONTENT_URI);
            startActivityForResult(gallery, GALLERY_REQUEST);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            imageView = (GestureImageView) rootView.findViewById(R.id.image);

            final View view = rootView.findViewById(R.id.fl_gesture);

            view.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

                @SuppressWarnings("deprecation")
                @Override
                public void onGlobalLayout() {
                    imageView.setParentViewWidthHeight(view.getWidth(), view.getHeight());
                    view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            });

            ivThumbnail1 = (ImageView) rootView.findViewById(R.id.iv_thumnail_1);
            ivThumbnail1.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (imageView != null) {
                        // imageView.setStampResId(R.drawable.nikaidou);
                    }
                }
            });
            return rootView;
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data != null) {
                String image = data.getData().toString();
                loadImage(image, imageView);
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }

        private void loadImage(String image, ImageView imageView) {

            Picasso.with(getActivity()).load(image).into(imageView, new EmptyCallback() {
                @Override
                public void onSuccess() {
                }
            });
        }
    }

}
