package demo.emtiaztafsir.docpoint;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

public class UpdateInfoActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ViewPager iViewPager;
    private TabLayout iTabLayout;
    private InfoTabsAdapter iTabAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_info);

        toolbar = findViewById(R.id.info_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Create Profile");

        iViewPager = findViewById(R.id.info_pager);
        iTabAdapter = new InfoTabsAdapter(getSupportFragmentManager());
        iTabLayout = findViewById(R.id.info_tabs);
        iViewPager.setAdapter(iTabAdapter);
        iTabLayout.setupWithViewPager(iViewPager);
    }
}
