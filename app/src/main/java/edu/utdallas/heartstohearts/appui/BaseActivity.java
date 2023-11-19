package edu.utdallas.heartstohearts.appui;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";

    static ArrayList<Class<? extends BaseActivity>> navigation_activities = new ArrayList<>();
    static ArrayList<String> navigation_display_names = new ArrayList<>();

    /**
     * Call this method at static time with a list of pairs (NavigationActivity.class, "MenuItemName")
     * and they will be added to the menu for all activities inheriting from this class
     *
     * @param items: tuples of navigation items
     * @return null- assign this to a value in a class that gets initialized at static time
     */
    protected static Void registerNavigationItems(Pair<Class<? extends BaseActivity>, String>... items) {
        for (int i = 0; i < items.length; i++){
            navigation_activities.add(items[i].first);
            navigation_display_names.add(items[i].second);
        }
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        for (int i = 0; i < navigation_activities.size(); i++) {
            // Add registered navigation items to menu with id indicating index in array
            menu.add(0, i, Menu.NONE, navigation_display_names.get(i));
        }
        return true;
    }

    /**
     * If the menu items is navigation to another activity, calls "switchActivity()" with the destination.
     * If an activity needs to ask for confirmation or do other cleanup before launching the other
     * activity, they can override that method.
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO: Heuristic check to guard against ID collisions between indices in menu and IDs in R.id.whatever
        int id = item.getItemId();
        if (id < navigation_activities.size())
        {
            // Switch Activities
            if (this.getClass() != navigation_activities.get(id)) {
                Intent nav_intent = new Intent(this, navigation_activities.get(id));
                startActivity(nav_intent);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * @return the context associated with this activity. Since activities are themselves contexts,
     * this is wholly redundant and could simply be replaced by the activity instance- but I think
     * this results in clearer code.
     */
    public Context getContext() {
        return (Context) this;
    }
}
