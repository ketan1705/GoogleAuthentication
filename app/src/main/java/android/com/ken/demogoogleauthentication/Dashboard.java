package android.com.ken.demogoogleauthentication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Dashboard extends AppCompatActivity {

    FloatingActionButton add_fab;
    RecyclerView recyclerView;
    NoteAdapter noteAdapter;
    FirebaseFirestore db;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        add_fab = findViewById(R.id.add_fab);
        recyclerView = findViewById(R.id.recyclerView);
        textView = findViewById(R.id.textView);

        db = FirebaseFirestore.getInstance();
        fetchData();
        noteAdapter = new NoteAdapter();
        recyclerView.setAdapter(noteAdapter);



        add_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Dashboard.this, AddNote.class);
                startActivity(intent);
            }
        });

    }

    private void fetchData() {


        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("Users").document(userUid).collection("Notes")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Note> notes = new ArrayList<>();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if (queryDocumentSnapshots!=null&& documentSnapshot.exists()){
                                Log.d("DashBoard", "Title" + documentSnapshot.get("Title"));
//                            Note note = documentSnapshot.toObject(Note.class);
                                String title = documentSnapshot.getString("Title");
                                String subject = documentSnapshot.getString("Subject");
//                            Log.d("DashBoard","Title" + note.getTitle());

                                Note note = new Note(title, subject);
                                Log.d("DashBoard", "Title" + note.getTitle());

                                notes.add(note);
                                recyclerView.setVisibility(View.VISIBLE);
                                textView.setVisibility(View.GONE);
                            }
                            else{
                                recyclerView.setVisibility(View.GONE);
                                textView.setVisibility(View.VISIBLE);
                            }

                        }
                        noteAdapter.setNotes(notes);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        recyclerView.setVisibility(View.GONE);
                        textView.setVisibility(View.VISIBLE);
                    }
                });

    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchData();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(Dashboard.this, MainActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
}
