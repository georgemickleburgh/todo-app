package gm502.todo.todoapp;

import java.util.List;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class TodoActivity extends FragmentActivity {
    // Private variables
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private String[] menuTitles;
    private ActionBarDrawerToggle drawerToggle;

    // Publicly accessible variables
    public static final String APP_TAG = "gm502.todo.todoapp";
    public TodoProvider provider;
    private ListView taskView;
    public CharSequence menuTitle;

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.main);

        // Setup main app stuff
        provider = new TodoProvider(this);
//        taskView = (ListView) findViewById(R.id.tasklist);
//        taskView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
//        taskView.setEmptyView(findViewById(R.id.empty_message));

        // Drawer (Off-side navigation)
        setTitle("Todo List");
        menuTitles = new String[]{"Add Todo", "Todo List", "Completed Todos"};
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, menuTitles));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

        // Drawer action bar toggle
        drawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(menuTitle);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(menuTitle);
            }
        };

        // Set the drawer toggle as the DrawerListener
        drawerLayout.setDrawerListener(drawerToggle);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(menuTitle);
        actionBar.show();

        // Locate the viewpager in activity_main.xml
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        // Set the ViewPagerAdapter into ViewPager
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...
        switch (item.getItemId()) {
            case R.id.add_todo:
                showAddDialog();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Swaps fragments in the main content view
     */
    private void selectItem(int position) {
        // Highlight the selected item, update the title, and close the drawer
        drawerLayout.closeDrawer(drawerList);
        Integer vpPos = null;

        switch (position) {
            case 0:
                showAddDialog();
                break;
            case 1:
                setTitle(menuTitles[position]);
                vpPos = 0;
                break;
            case 2:
                setTitle(menuTitles[position]);
                vpPos = 1;
                break;
            default:
                break;
        }

        if (vpPos != null) {
            Log.i(APP_TAG, "Changing page");
            ViewPager vp = (ViewPager) findViewById(R.id.pager);
            vp.setCurrentItem(vpPos);
        }
    }

    protected void showAddDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(TodoActivity.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TodoActivity.this);
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String text = editText.getText().toString();
                        if (!text.equals("")) {
                            provider.addTask(editText.getText().toString());
                            renderTodos(taskView);
                        }
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alert.show();
    }

    /**
     * renders the task list
     */
    public void renderTodos(ListView listView) {
        final ListView finalView = listView;
        List<String> todos = provider.findAll();

        if (!todos.isEmpty()) {
            // render the list
            finalView.setAdapter(new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_multiple_choice,
                    todos.toArray(new String[]{})));

            // Handle item clicks
            finalView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TextView v = (TextView) view;
                    final Handler handler = new Handler();
                    final String textValue = v.getText().toString();

                    finalView.setItemChecked(position, true);

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            provider.deleteTask(textValue);
                            renderTodos(finalView);
                        }
                    }, 1000);
                }
            });
        } else {
            // This needs to be run to clear the current items
            finalView.setAdapter(new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1,
                    todos.toArray(new String[]{})));
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        menuTitle = title;
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    public void setTaskView(ListView newTaskView) {
        taskView = newTaskView;
    }
}