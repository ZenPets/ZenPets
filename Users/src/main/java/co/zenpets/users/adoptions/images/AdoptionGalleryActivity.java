package co.zenpets.users.adoptions.images;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import co.zenpets.users.R;
import me.relex.photodraweeview.PhotoDraweeView;

public class AdoptionGalleryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adoption_gallery_activity);

        /* CONFIGURE THE TOOLBAR */
        configTB();

        /* CAST THE VIEW PAGER INSTANCE */
        ViewPager viewPager = findViewById(R.id.pager);

        /* GET THE INCOMING POSITION */
        Bundle bundle = getIntent().getExtras();
        String[] strImages = new String[0];
        int position = 0;
        if (bundle != null) {
            strImages = bundle.getStringArray("array");
            position = bundle.getInt("position");
        }

        /* CONVERT THE STRING ARRAY TO AN ARRAY LIST */
        List<String> arrImages = new ArrayList<>(Arrays.asList(strImages));

        /* INSTANTIATE AND SET THE ADAPTER TO THE VIEW PAGER */
        AdoptionFullScreenAdapter adapter = new AdoptionFullScreenAdapter(arrImages);
        viewPager.setAdapter(adapter);

        /* DISPLAY THE SELECTED IMAGE FIRST */
        viewPager.setCurrentItem(position);
    }

    /***** THE GALLERY PAGER ADAPTER *****/
    private class AdoptionFullScreenAdapter extends PagerAdapter {

        final List<String> images;
        LayoutInflater inflater;

        AdoptionFullScreenAdapter(List<String> arrImages) {
            this.images = arrImages;
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            PhotoDraweeView imgvwClinicImage;

            /* CAST THE LAYOUT ELEMENT */
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View viewLayout = inflater.inflate(R.layout.clinic_gallery_fullscreen, container, false);
            imgvwClinicImage = viewLayout.findViewById(R.id.imgvwClinicImage);

            /* SET THE IMAGE */
            Uri uri = Uri.parse(images.get(position));
            imgvwClinicImage.setPhotoUri(uri);
            container.addView(viewLayout);
            return viewLayout;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((LinearLayout) object);
        }
    }

    /***** CONFIGURE THE TOOLBAR *****/
    private void configTB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        String strTitle = "Images";
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(strTitle);
        getSupportActionBar().setSubtitle(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            default:
                break;
        }
        return false;
    }
}