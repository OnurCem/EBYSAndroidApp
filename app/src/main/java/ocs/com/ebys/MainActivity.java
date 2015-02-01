package ocs.com.ebys;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.apache.http.params.CoreConnectionPNames;

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerCallbacks,
                   SearchView.OnQueryTextListener {

    private Toolbar mToolbar;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private Fragment activeFragment;
    private CourseFragment courseFragment;
    private TranscriptFragment transcriptFragment;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_drawer);
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);
        mNavigationDrawerFragment.setUserProfile(EBYSController.getUser());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EBYSController.getInstance().deleteCookies();
        DataStore.getInstance().deleteData();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        getSupportFragmentManager().putFragment(outState, "activeFragment", activeFragment);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        switch (position) {
            case 0:
                replaceFragment(CourseFragment.newInstance());
                break;

            case 1:
                replaceFragment(TranscriptFragment.newInstance());
                break;

            case 3:
                PreferenceController.saveSharedSetting(MainActivity.this,
                        PreferenceController.PREF_USERNAME, null);
                PreferenceController.saveSharedSetting(MainActivity.this,
                        PreferenceController.PREF_PASSWORD, null);

                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);

            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            MenuItem searchMenuItem = menu.findItem(R.id.menu_search);
            searchView = (SearchView) searchMenuItem.getActionView();
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setSubmitButtonEnabled(false);
            searchView.setOnQueryTextListener(this);

            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        if (activeFragment instanceof CourseFragment) {
            ((CourseFragment) activeFragment).filterList(s);
        } else if (activeFragment instanceof TranscriptFragment) {
            ((TranscriptFragment) activeFragment).filterList(s);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mNavigationDrawerFragment.isDrawerOpen()) {
            mNavigationDrawerFragment.closeDrawer();
        } else if (searchView.isFocused()) {
            searchView.post(new Runnable() {
                @Override
                public void run() {
                    searchView.setQuery("", true);
                    searchView.clearFocus();
                }
            });
        } else {
            super.onBackPressed();
        }
    }

    private void replaceFragment(Fragment fragment) {
        activeFragment = fragment;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment, fragment.getClass().getName())
                .commit();
    }
}
