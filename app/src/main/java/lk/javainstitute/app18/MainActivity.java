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
import androidx.recyclerview.widget.LinearLayoutManager;
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


    }

    @Override
    protected void onResume() {
        super.onResume();
        RecyclerView recyclerView=findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager=new LinearLayoutManager(MainActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        NoteListAdapter noteListAdapter= new NoteListAdapter(cursor);
                        recyclerView.setAdapter(noteListAdapter);
                    }
                });

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
        View containerView;


        public NoteViewHolder(View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.textView5);
            content=itemView.findViewById(R.id.textView6);
            date=itemView.findViewById(R.id.textView7);
            containerView=itemView;
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
        cursor.moveToPosition(position);

        String id=cursor.getString(0);
        String title=cursor.getString(1);
        String content=cursor.getString(2);
        String date=cursor.getString(3);

        holder.title.setText(title);
        holder.content.setText(content);
        holder.date.setText(date);
        holder.containerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("NoteListAdapter","Clicked");
                Intent intent=new Intent(v.getContext(),CreateNoteActivity.class);
                intent.putExtra("id",id);
                intent.putExtra("title",title);
                intent.putExtra("content",content);
                v.getContext().startActivity(intent);
            }
        });
        holder.containerView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                SQLiteHelper sqLiteHelper=new SQLiteHelper(v.getContext(),"note.db",null,1);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SQLiteDatabase sqLiteDatabase=sqLiteHelper.getWritableDatabase();
                        int row=sqLiteDatabase.delete("note","`id`=?",new String[]{id});
                        Log.i("MyNoteBookApp","Deleted "+row+" row");
                    }
                }).start();
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }


}