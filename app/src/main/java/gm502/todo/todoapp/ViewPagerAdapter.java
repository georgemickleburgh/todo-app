package gm502.todo.todoapp;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import gm502.todo.todoapp.Fragment.CompletedFragment;
import gm502.todo.todoapp.Fragment.TodoListFragment;

/**
 * Created by george on 14/03/15.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;
    // Tab Titles
    private String tabtitles[] = new String[] { "Todo List", "Completed Todos" };
    Context context;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                TodoListFragment todoFragment = new TodoListFragment();
                return todoFragment;
            case 1:
                CompletedFragment completedFragment = new CompletedFragment();
                return completedFragment;
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabtitles[position];
    }
}
