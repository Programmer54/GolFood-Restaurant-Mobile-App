package com.example.golfood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class MainActivity7 extends AppCompatActivity {
    RecyclerView recyclerView;
    SiparisAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main7);
        recyclerView=(RecyclerView) findViewById(R.id.RcyViewBekleyenSiparisListe);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        Toast.makeText(getApplicationContext(),MainActivity5.ID,Toast.LENGTH_SHORT).show();
        Yenile();
        FloatingActionButton button=(FloatingActionButton) findViewById(R.id.BekleyenSiparis_Geri);
        button.setOnClickListener(view -> {
            Intent intent=new Intent(MainActivity7.this,MainActivity5.class);
            startActivity(intent);
        });
    }

    public class SiparisAdapter extends RecyclerView.Adapter<SiparisAdapter.ViewHolder>{

        List<DocumentSnapshot> Siparis;
        public SiparisAdapter(List<DocumentSnapshot> Siparis){
            this.Siparis=Siparis;
        }

        public class ViewHolder extends RecyclerView.ViewHolder{

            ImageView imageView;
            TextView textView;
            TextView textView1;
            TextView textView2;
            Button button;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView=(ImageView) itemView.findViewById(R.id.rB1I);
                textView=(TextView) itemView.findViewById(R.id.TB1I);
                textView1=(TextView) itemView.findViewById(R.id.TB2I);
                textView2=(TextView) itemView.findViewById(R.id.TB3I);
                button=(Button) itemView.findViewById(R.id.BB1I);
            }
        }

        @NonNull
        @Override
        public SiparisAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.ozel_kart_4,parent,false);
            ViewHolder holder=new ViewHolder(view);
            return holder;

        }

        @Override
        public void onBindViewHolder(@NonNull SiparisAdapter.ViewHolder holder, int position) {
            FirebaseFirestore db=FirebaseFirestore.getInstance();
            Task<DocumentSnapshot> SiparisMenu=db.collection("menu").document(Siparis.get(position).get("Menu").toString()).get();
            while (!SiparisMenu.isComplete());

            StorageReference storage= FirebaseStorage.getInstance().getReference();
            Task<StorageMetadata> boyut=storage.child(SiparisMenu.getResult().getString("Resim").toString()).getMetadata();
            while (!boyut.isComplete());
            Task<byte[]> bekle=storage.child(SiparisMenu.getResult().getString("Resim").toString()).getBytes(boyut.getResult().getSizeBytes());
            while (!bekle.isComplete());
            Bitmap bm= BitmapFactory.decodeByteArray(bekle.getResult(),0,bekle.getResult().length);
            holder.imageView.setImageBitmap(bm);
            holder.textView.setText(SiparisMenu.getResult().getString("Başlık"));
            holder.textView1.setText(SiparisMenu.getResult().getString("Fiyat")+" TL");
            holder.textView2.setText("Adres: "+Siparis.get(position).getString("Adres"));
            holder.button.setOnClickListener(view -> {
                //while(!db.collection("siparis").document(Siparis.get(position).getId()).update("Hazırlanıyor","Tamamlandı").isComplete());
                Siparis.get(position).getReference().update("Durum","Tamamlandı");
                Toast.makeText(getApplicationContext(),"Tamamlandı",Toast.LENGTH_SHORT).show();
                Yenile();
            });
        }

        @Override
        public int getItemCount() {
            return Siparis.size();
        }
    }

    private void Yenile() {
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        Task<QuerySnapshot> gelen=db.collection("siparis").whereEqualTo("Durum","Hazırlanıyor").whereEqualTo("İşletme",MainActivity5.ID).get();
        while(!gelen.isComplete());
        if (gelen.getResult().getDocuments().size()==0)
            Toast.makeText(getApplicationContext(),"Hiç siparişiniz yok",Toast.LENGTH_LONG).show();
        adapter=new SiparisAdapter(gelen.getResult().getDocuments());
        recyclerView.setAdapter(adapter);
    }
}