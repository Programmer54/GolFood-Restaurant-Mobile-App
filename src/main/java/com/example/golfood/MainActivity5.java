package com.example.golfood;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class MainActivity5 extends AppCompatActivity {
    RecyclerView recyclerView;
    AdapterIsletme adapter;
    static String ID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);

        recyclerView=(RecyclerView) findViewById(R.id.RcyViewİsletme);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        Yenile();
        FloatingActionButton MenuEkle=(FloatingActionButton) findViewById(R.id.MenuEkle);
        FloatingActionButton Bekleyen=(FloatingActionButton) findViewById(R.id.BekleyenSiparis);
        MenuEkle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity5.this,MainActivity6.class);
                startActivity(intent);

            }
        });

        Bekleyen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity5.this,MainActivity7.class);
                startActivity(intent);

            }
        });


    }
    public class AdapterIsletme extends RecyclerView.Adapter<AdapterIsletme.ViewHolder>
    {
        List<DocumentSnapshot> GelenSepet;
        public AdapterIsletme(List<DocumentSnapshot> GelenSepet){
            this.GelenSepet=GelenSepet;
        }
        public class ViewHolder extends RecyclerView.ViewHolder {

            ImageView imageView;
            TextView textView;
            TextView textView1;
            TextView textView2;
            Button button;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView=(ImageView) itemView.findViewById(R.id.r1I);
                textView=(TextView) itemView.findViewById(R.id.T1I);
                textView1=(TextView) itemView.findViewById(R.id.T2I);
                textView2=(TextView) itemView.findViewById(R.id.T3I);
                button=(Button) itemView.findViewById(R.id.B1I);
            }
        }
        @NonNull
        @Override
        public AdapterIsletme.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.ozel_kart_3,parent,false);
            ViewHolder holder=new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull AdapterIsletme.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            try {
                StorageReference storage= FirebaseStorage.getInstance().getReference();
                Task<StorageMetadata> boyut=storage.child(GelenSepet.get(position).getString("Resim").toString()).getMetadata();
                while (!boyut.isComplete());
                Task<byte[]> bekle=storage.child(GelenSepet.get(position).getString("Resim").toString()).getBytes(boyut.getResult().getSizeBytes());
                while (!bekle.isComplete());
                Bitmap bm= BitmapFactory.decodeByteArray(bekle.getResult(),0,bekle.getResult().length);
                holder.imageView.setImageBitmap(bm);
                holder.textView.setText(GelenSepet.get(position).getString("Başlık"));
                holder.textView1.setText(GelenSepet.get(position).getString("Fiyat")+" TL");
                holder.textView2.setText(GelenSepet.get(position).getString("Açıklama"));
                holder.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        MainActivity6.Duzenle=GelenSepet.get(position);
                        Intent intent=new Intent(MainActivity5.this,MainActivity6.class);
                        startActivity(intent);

                    }
                });
            }catch (Exception e){}
        }

        @Override
        public int getItemCount() {
            return GelenSepet.size();
        }

    }
    public void Yenile(){//Usülsüzlük
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        db.collection("menu").whereEqualTo("İşletme",ID).get();
        Task<QuerySnapshot> gelen=db.collection("menu").get();
        while (!gelen.isComplete()); //Usülsüzlük :(
        adapter=new AdapterIsletme(gelen.getResult().getDocuments());
        recyclerView.setAdapter(adapter);

    }
}