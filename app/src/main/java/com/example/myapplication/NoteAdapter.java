package com.example.myapplication;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder>{
    private static final String TAG = "NoteAdapter";
    ArrayList<Note> items = new ArrayList<Note>();
    static class ViewHolder extends RecyclerView.ViewHolder {


        LinearLayout layoutTodo;
        CheckBox checkBox;
        Button deleteButton;

        public void setItem(Note item) {
            checkBox.setText(item.getTodo());
        }

        public void setLayout() {
            layoutTodo.setVisibility(View.VISIBLE);
        }



        public ViewHolder(View itemView) {
            super(itemView);
            layoutTodo = itemView.findViewById(R.id.layoutTodo);
            checkBox = itemView.findViewById(R.id.checkBox);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            deleteButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick (View v){
                    String TODO = (String) checkBox.getText();
                    deleteToDo(TODO);
                    Toast.makeText(v.getContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show(); // 사용자에게 삭제됨을 알리는 메시지를 Toast.makeText를 이용하여 구현
                }

                private void deleteToDo (String TODO){
                }
            });


        }


    }
    public void setItems(ArrayList<Note> items) {
        this.items = items;
    }

        @NonNull
    @Override
    public NoteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View itemView = inflater.inflate(R.layout.todo_item, parent, false);

            return new ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull NoteAdapter.ViewHolder holder, int position) {
        Note item = items.get(position);
        holder.setItem(item);
        holder.setLayout();


    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
