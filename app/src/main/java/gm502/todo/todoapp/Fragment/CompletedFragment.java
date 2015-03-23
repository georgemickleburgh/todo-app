package gm502.todo.todoapp.Fragment;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import gm502.todo.todoapp.R;
import gm502.todo.todoapp.TodoActivity;
import gm502.todo.todoapp.TodoProvider;

/**
 * Created by george on 14/03/15.
 */
public class CompletedFragment extends Fragment {

    protected TodoActivity activity;
    protected View view;
    protected ListView taskView;
    protected TodoProvider provider;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = (TodoActivity) getActivity();
        provider = new TodoProvider(activity);

        view = inflater.inflate(R.layout.completed, container, false);
        taskView = (ListView) view.findViewById(R.id.tasklist);
        taskView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        taskView.setEmptyView(view.findViewById(R.id.empty_message));

        activity.setTitle("Completed Todos");

        renderTodos(taskView);

        return view;
    }

    /**
     * renders the task list
     */
    public void renderTodos(ListView listView) {
        final ListView finalView = listView;
        List<String> todos = provider.findAll();

        if (!todos.isEmpty()) {
            // render the list
            finalView.setAdapter(new ArrayAdapter<>(activity,
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
            finalView.setAdapter(new ArrayAdapter<>(activity,
                    android.R.layout.simple_list_item_1,
                    todos.toArray(new String[]{})));
        }
    }

}
