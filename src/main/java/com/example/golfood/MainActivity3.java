package com.example.golfood;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MainActivity3 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        EditText eposta=(EditText) findViewById(R.id.eposta_kayit);
        EditText sifre=(EditText) findViewById(R.id.sifre_kayit);
        Button btn_kayit2=(Button) findViewById(R.id.btn_kayit2);
        btn_kayit2.setOnClickListener(view -> {
            FirebaseFirestore db =FirebaseFirestore.getInstance();
            db.collection("Kullanıcılar").whereEqualTo("Mail",eposta.getText().toString()).get()
                    .addOnCompleteListener(task -> {
                QuerySnapshot gelen=task.getResult();
                if (!gelen.isEmpty())
                {
                    TextView tx=(TextView) findViewById(R.id.hata_kayit);
                    tx.setTextColor(getColor(R.color.red));
                    tx.setText("Bu eposta kullanımda. Lütfen başka bir eposta adresi giriniz.");
                }
                else
                {
                    Map<String,String> ekle=new HashMap<>();
                    ekle.put("Mail",eposta.getText().toString());
                    ekle.put("Şifre",sifre.getText().toString());
                    SwitchMaterial sw=(SwitchMaterial) findViewById(R.id.isletme_chk);
                    if (sw.isChecked())
                    {
                        ekle.put("Rol","İşletme");
                    }
                    else
                    {
                        ekle.put("Rol","Müşteri");
                    }
                    db.collection("Kullanıcılar").add(ekle);
                    Toast.makeText(getApplicationContext(),"Kayıt İşlemi Başarılı",Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(MainActivity3.this,MainActivity.class);
                    startActivity(intent);
                }
            });
        });
    }
}