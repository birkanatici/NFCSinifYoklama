package birkan.nfcsinifyoklama;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.text.LocaleDisplayNames;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import birkan.nfcsinifyoklama.Tabs.TabDersler;
import birkan.nfcsinifyoklama.Tabs.TabOgrenciler;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private DatabaseHelper db;

    private FloatingActionButton ekleBtn;
    private TabLayout tabLayout;

    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private int tabPostion;

    Utility func;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mViewPager = (ViewPager) findViewById(R.id.container);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        ekleBtn = (FloatingActionButton) findViewById(R.id.ekle);

        setSupportActionBar(toolbar);
        db = new DatabaseHelper(getApplicationContext());
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), db, getApplicationContext());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);
        ekleBtn.setOnClickListener(this);

        func = new Utility();
        Utility.STATE = 0;
        tabPostion = 0;


        mAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mAdapter == null) {
            //nfc not support your device.
            return;
        }
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.dersList || v.getId() == R.id.ogrenciList) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_list, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch(item.getItemId()) {
            case R.id.delete:

                if(mViewPager.getCurrentItem() == 0)
                    mSectionsPagerAdapter.tabDers.deleteItem(info.position);
                else
                    mSectionsPagerAdapter.tabOgrenci.deleteItem(info.position);

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    protected void onNewIntent(Intent intent){
        getTagInfo(intent);
    }

    private void getTagInfo(Intent intent) {
        String action = intent.getAction();

        String rfid = "";

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            Tag tag = (Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            rfid = func.dumpTagData(tag); // kartın rfid'sini aldık

            if(Utility.STATE == 3){    ///öğrenci ekle

                Toast.makeText(this, "Öğrenci Ekle Rfid: "+(CharSequence)rfid, Toast.LENGTH_LONG).show();
                mSectionsPagerAdapter.tabOgrenci.showAddStudent(this, tabLayout.getRootView(), rfid);

            }else if(Utility.STATE == 2){   // yoklama al
                Toast.makeText(this, "Yoklama Al Rfid: "+(CharSequence)rfid, Toast.LENGTH_LONG).show();
                Log.d("YoklamaAl", "Rfid : "+rfid);
            }


        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAdapter != null) {
            mAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.ekle){    //ekle butonu onclick eylemi
            switch (tabLayout.getSelectedTabPosition()){
                case 0:  // ders tab'inde
                    Utility.STATE = 0;
                    mSectionsPagerAdapter.tabDers.showAddLesson(this, tabLayout.getRootView());
                    Log.i("ekle", "ders");
                    break;
                case 1:  // öğrenci tab'inde
                    Utility.STATE = 1;
                    mSectionsPagerAdapter.tabOgrenci.showGetRfid(this, tabLayout.getRootView());
                    Log.i("ekle", "öğr");
                    break;
            }
        }

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private DatabaseHelper db;
        private Context context;
        public TabOgrenciler tabOgrenci;
        TabDersler tabDers;

        public SectionsPagerAdapter(FragmentManager fm, DatabaseHelper _db, Context _context) {
            super(fm);
            this.db = _db;
            this.context = _context;
            tabOgrenci = new TabOgrenciler(db, context);
            tabDers = new TabDersler(db, context);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Dersler";
                case 1:
                    return "Öğrenciler";
            }
            return null;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    tabPostion = 0;
                    return tabDers;
                case 1:
                    tabPostion = 1;
                    return tabOgrenci;
                default:
                    return null;
            }
        }



    }
}
