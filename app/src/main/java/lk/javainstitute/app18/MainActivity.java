package lk.javainstitute.app18;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import lk.javainstitute.app18.model.SQLiteHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Button b2= findViewById(R.id.button);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MainActivity.this,CreateNoteActivity.class);
                startActivity(intent);
            }
        });
        RecyclerView recyclerView=findViewById(R.id.recyclerView);
        SQLiteHelper sqLiteHelper=new SQLiteHelper(this,"note.db",null,1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase sqLiteDatabase=sqLiteHelper.getReadableDatabase();
                Cursor cursor= sqLiteDatabase.query(
                        "note",
                        null,
                        null,
                        null,
                        null,
                        null,
                        "`id` DESC"
                );
                NoteListAdapter noteListAdapter= new NoteListAdapter(cursor);
                recyclerView.setAdapter(noteListAdapter);
            }}).start();





    }
}
class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.NoteViewHolder>{
    Cursor cursor;

    public NoteListAdapter(Cursor cursor){
        this.cursor=cursor;
    }

    static  class NoteViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        TextView content;
        TextView date;


        public NoteViewHolder(View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.textView5);
            content=itemView.findViewById(R.id.textView6);
            date=itemView.findViewById(R.id.textView7);
        }
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.notelist,parent,false);
        NoteViewHolder noteViewHolder=new NoteViewHolder(view);
        return noteViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        cursor.move(position);
        holder.title.setText(cursor.getString(1));
        holder.content.setText(cursor.getString(2));
        holder.date.setText(cursor.getString(3));

    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }


}