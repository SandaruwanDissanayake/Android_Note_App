package lk.javainstitute.app18;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;

import lk.javainstitute.app18.model.SQLiteHelper;

public class CreateNoteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String id;
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_note);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent i =getIntent();
        id= i.getStringExtra("id");
        String title= i.getStringExtra("title");
        String content= i.getStringExtra("content");
        EditText editText=findViewById(R.id.editTextText1);
        EditText editText2=findViewById(R.id.editTextTextMultiLine1);

        if(title !=null){
            editText.setText(title);
        }
        if(content !=null){
            editText2.setText(content);
        }


        Button b2=findViewById(R.id.saveNote);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title=editText.getText().toString();
                String content=editText2.getText().toString();

                if(title.isEmpty()){
                    Toast.makeText(CreateNoteActivity.this, "Please enter a title", Toast.LENGTH_SHORT).show();
                }else if(content.isEmpty()){
                    Toast.makeText(CreateNoteActivity.this, "Please enter a content", Toast.LENGTH_SHORT).show();
                }else {
                    //save
                    SQLiteHelper sqLiteHelper=new SQLiteHelper(CreateNoteActivity.this,"note.db",null,1);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SQLiteDatabase db=sqLiteHelper.getWritableDatabase();
                            ContentValues contentValues= new ContentValues();
                            contentValues.put("title",title);
                            contentValues.put("content",content);

                            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            contentValues.put("date_created",format.format(System.currentTimeMillis()));

                            if(id!=null){
                                long updatedId=db.update("note",contentValues,"`id`=?",new String[]{id});
                                Log.d("updatedId","Note Updated");
                            }else{
                                long insertedId=db.insert("note",null,contentValues);
                                Log.d("insertedId","Note Saved");
                            }



                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(CreateNoteActivity.this, "Note Saved", Toast.LENGTH_SHORT).show();
                                    editText.setText("");
                                    editText2.setText("");
                                    editText.requestFocus();
                                }
                            });

                        }
                    }).start();

                }
            }
        });
    }
}