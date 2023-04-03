package com.example.golfood;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;

import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MainActivity6 extends AppCompatActivity {
    static String MenuID;
    static DocumentSnapshot Duzenle=null;
    MaterialButton ResimAl;
    ImageView imageView;
    Bitmap bitmap=null;
    String isim=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main6);
        imageView=(ImageView) findViewById(R.id.K3_Resim);
        TextInputEditText Baslik=(TextInputEditText) findViewById(R.id.K3_Baslik);
        TextInputEditText Fiyat=(TextInputEditText) findViewById(R.id.K3_Fiyat);
        TextInputEditText Aciklama=(TextInputEditText) findViewById(R.id.K3_Aciklama);
        MaterialButton Kaydet=(MaterialButton) findViewById(R.id.K3_Button);
        ResimAl=(MaterialButton) findViewById(R.id.ResimAl);

        //Dosya şeçmek için inten.
        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        //Galeiden resim seçme
        //ActivityResultLauncher ve registerForActivityResult araştır tam bilgi edin. Döküman kismi okundu
        ActivityResultLauncher<Intent> resultLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),result.getData().getData());
                            imageView.setImageBitmap(bitmap);

                        }
                        catch (Exception e){

                        }
                    }
                });
        //Kamera
        //Intent intent1=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //Deneme


        ResimAl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resultLauncher.launch(intent);
                //resultLauncher.launch(intent);
            }
        });
        if (Duzenle!=null)
        {
            StorageReference storage= FirebaseStorage.getInstance().getReference();
            Task<StorageMetadata> boyut=storage.child(Duzenle.getString("Resim")).getMetadata();
            while (!boyut.isComplete());//Usülsüzlük :(
            Task<byte[]> bekle=storage.child(Duzenle.getString("Resim")).getBytes(boyut.getResult().getSizeBytes());
            while (!bekle.isComplete());//Usülsüzlük :(
            bitmap= BitmapFactory.decodeByteArray(bekle.getResult(),0,bekle.getResult().length);
            imageView.setImageBitmap(bitmap);
            Baslik.setText(Duzenle.getString("Başlık"));
            Fiyat.setText(Duzenle.getString("Fiyat"));
            Aciklama.setText(Duzenle.getString("Açıklama"));
            isim=Duzenle.getString("Resim");
        }
        Kaydet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore db=FirebaseFirestore.getInstance();
                FirebaseStorage storage=FirebaseStorage.getInstance();
                ByteArrayOutputStream stream=new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
                Intent Git=new Intent(MainActivity6.this,MainActivity5.class);

                if (Duzenle!=null)
                {
                    UploadTask uploadTask= storage.getReference().child(Duzenle.getId()).putBytes(stream.toByteArray());
                    while(!uploadTask.isComplete());
                    Map<String,String> A=new HashMap<>();
                    A.put("Başlık",Baslik.getText().toString());
                    A.put("Fiyat",Fiyat.getText().toString());
                    A.put("Açıklama",Aciklama.getText().toString());
                    A.put("Resim",Duzenle.getId());
                    A.put("İşletme",MainActivity5.ID);
                    db.collection("menu").document(Duzenle.getId()).set(A);
                    Toast.makeText(getApplicationContext(),"Menu Güncellendi",Toast.LENGTH_LONG).show();
                    Duzenle=null;
                    startActivity(Git);
                }
                else
                {
                    Map<String,String> A=new HashMap<>();
                    A.put("Başlık",Baslik.getText().toString());
                    A.put("Fiyat",Fiyat.getText().toString());
                    A.put("Açıklama",Aciklama.getText().toString());
                    A.put("İşletme",MainActivity5.ID);

                    Task<DocumentReference> sonuc=db.collection("menu").add(A);
                    while (!sonuc.isComplete());
                    sonuc.getResult().update("Resim",sonuc.getResult().getId());

                    UploadTask uploadTask= storage.getReference().child(sonuc.getResult().getId()).putBytes(stream.toByteArray());
                    while(!uploadTask.isComplete());
                    Toast.makeText(getApplicationContext(),"Menu Eklendi",Toast.LENGTH_LONG).show();
                    Duzenle=null;
                    startActivity(Git);

                }
            }
        });}

    }