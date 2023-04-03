package com.example.golfood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.rpc.context.AttributeContext;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        /*Map<String,String> ekle=new HashMap<>();
        ekle.put("Mail","a");
        ekle.put("Şifre","a");
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        db.collection("user").document("kul").set(ekle,SetOptions.merge());*/


        FirebaseFirestore db=FirebaseFirestore.getInstance();

        EditText eposta=(EditText) findViewById(R.id.eposta);
        EditText sifre=(EditText) findViewById(R.id.sifre);
        Button btn_giris=(Button) findViewById(R.id.btn_giris);
        Button btn_kayit=(Button) findViewById(R.id.btn_kayit);
        btn_giris.setOnClickListener(view -> {

            db.collection("Kullanıcılar").whereEqualTo("Mail",eposta.getText().toString()).
                    whereEqualTo("Şifre",sifre.getText().toString()).
                    get().addOnCompleteListener(task -> {
                QuerySnapshot gelen=task.getResult();


                if (!gelen.isEmpty())
                {
                    if (gelen.getDocuments().get(0).get("Rol").equals("Müşteri"))
                    {
                        Intent intent=new Intent(MainActivity.this,MainActivity2.class);
                        MainActivity2.ID=gelen.getDocuments().get(0).getId();
                        MainActivity.this.startActivity(intent);
                    }
                    else
                    {
                        Intent intent=new Intent(MainActivity.this,MainActivity5.class);
                        MainActivity5.ID=gelen.getDocuments().get(0).getId();
                        startActivity(intent);
                    }
                }
                else
                {
                    TextView tx=(TextView) findViewById(R.id.hata);
                    tx.setTextColor(getColor(R.color.red));
                    tx.setText("Kullanıcı adı veya şifre hatalı");
                }
            });
        });

        btn_kayit.setOnClickListener(view ->
        {
            Intent intent=new Intent(MainActivity.this,MainActivity3.class);
            MainActivity.this.startActivity(intent);
        });

    }
}