package com.br.app;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.br.adapter.NavDrawerListAdapter;
import com.br.awesomewallpapers.R;
import com.br.model.Category;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;

    //Navigation drawer title
    private CharSequence drawerTitle;
    private CharSequence mTitle;
    private List<Category> albumsList;
    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTitle = drawerTitle = getTitle();

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerList = (ListView)findViewById(R.id.list_slidermenu);

        navDrawerItems = new ArrayList<NavDrawerItem>();

        // Getting the albums from shared preferences
        albumsList = AppController.getInstance().getPrefManager().getCategories();

        // Insert "Recently Added" in navigation drawer first position
        Category recentAlbum = new Category(null, getString(R.string.nav_drawer_recently_added));

        albumsList.add(0, recentAlbum);

        //Loop through albums in add them to navigation drawer adapter
        for(Category a : albumsList){
            navDrawerItems.add(new NavDrawerItem(a.getId(), a.getTitle()));
        }

        drawerList.setOnItemClickListener(new SlideMenuClickListener());

        //Setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
        drawerList.setAdapter(adapter);

        //Enabling action bar app icon and behaving it as toggle button
        if(getActionBar() != null){
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setHomeButtonEnabled(true);
            getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        }

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, R.string.app_name, R.string.app_name){
            @Override
            public void onDrawerClosed(View view){
                getActionBar().setTitle("Menu");
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };

        drawerLayout.setDrawerListener(drawerToggle);

        if(savedInstanceState == null){
            displayView(0);
        }
    }

    /*
    * Navigation drawer menu item click listener
    * */
    private class SlideMenuClickListener implements ListView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // display view for selected nav drawer item
            displayView(position);
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
        // toggle nav drawer on selecting action bar app icon/title
        if(drawerToggle.onOptionsItemSelected(item)){
            return true;
        }

        switch(item.getItemId()){
            case R.id.action_settings:
                // Selected settings menu item
                // launch Settings activity
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    /*
    * Diplaying fragment view for selected nav drawer list item
    * */
    private void displayView(int position){
        // update the main content by replacing fragments
        Fragment fragment = null;

        switch(position){
            case 0:
                // Recently added item selected
                // don't pass album id to grid fragment
                fragment = GridFragment.newInstance(null);
                break;
            default:
                // selected wallpaper category
                // send album id to grid fragment to list all the wallpapers
                String albumId = albumsList.get(position).getId();
                fragment = GridFragment.newInstance(albumId);
                break;
        }

        if(fragment != null){
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();

            // update selected item and title, then close the drawer
            drawerList.setItemChecked(position, true);
            drawerList.setSelection(position);
            setTitle(albumsList.get(position).getTitle());
            drawerLayout.closeDrawer(drawerList);
        }
        else{
            // error in creating fragment
            Log.e(TAG, "Error in creating fragment");
        }
    }

    @Override
    public void setTitle(CharSequence title){
        mTitle = title;
        if(getActionBar() != null){
            getActionBar().setTitle("Menu");
        }
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls

    }

}
