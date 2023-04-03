package com.example.golfood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity4 extends AppCompatActivity {
    RecyclerView recyclerView;
    AdapterSepet adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        recyclerView=(RecyclerView) findViewById(R.id.RcyViewSepet);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        Yenile();
        FloatingActionButton SepetOnay=(FloatingActionButton) findViewById(R.id.SepetOnay);
        FloatingActionButton MenuGeri=(FloatingActionButton) findViewById(R.id.MenuGeri);
        FloatingActionButton SiparisGecmis=(FloatingActionButton) findViewById(R.id.SiparisGecmis);
        SepetOnay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore db=FirebaseFirestore.getInstance();
                Map<String,String> A=new HashMap<>();
                MaterialAlertDialogBuilder builder=new MaterialAlertDialogBuilder(MainActivity4.this);
                builder.setTitle("Adres Giriniz");
                EditText editText=new EditText(MainActivity4.this);
                LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                editText.setLayoutParams(lp);
                builder.setView(editText);
                final String[] adres = new String[1];
                builder.setPositiveButton("Tamam",(dialogInterface, e) -> {
                    adres[0] =editText.getText().toString();
                    for (int i=0;i<MainActivity2.sepet.size();i++)
                    {
                        A.put("İşletme",MainActivity2.sepet.get(i).getString("İşletme").toString());
                        A.put("Müşteri",MainActivity2.ID);
                        A.put("Menu",MainActivity2.sepet.get(i).getId());
                        A.put("Durum","Hazırlanıyor");
                        A.put("Adres",adres[0]);

                    }
                    db.collection("siparis").add(A);
                    Toast.makeText(getApplicationContext(),"Siparişleriniz alınmıştır",Toast.LENGTH_LONG).show();
                    MainActivity2.sepet.clear();
                    Intent intent=new Intent(getApplicationContext(),MainActivity2.class);
                    startActivity(intent);
                });
                builder.setNegativeButton("İptal",(dialogInterface, i) -> {
                    return;
                });
                   builder.show();
                }

        });

        MenuGeri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),MainActivity2.class);
                startActivity(intent);
            }
        });

        SiparisGecmis.setOnClickListener(view -> {
            Intent intent=new Intent(getApplicationContext(),MainActivity8.class);
            startActivity(intent);
        });

    }


    public class AdapterSepet extends RecyclerView.Adapter<AdapterSepet.ViewHolder>{
        List<DocumentSnapshot> GelenSepet;

        public AdapterSepet(List<DocumentSnapshot> GelenSepet){
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
                imageView=(ImageView) itemView.findViewById(R.id.r1S);
                textView=(TextView) itemView.findViewById(R.id.T1S);
                textView1=(TextView) itemView.findViewById(R.id.T2S);
                textView2=(TextView) itemView.findViewById(R.id.T3S);
                button=(Button) itemView.findViewById(R.id.B1S);
            }
        }

        @NonNull
        @Override
        public AdapterSepet.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.ozel_kart_2,parent,false);
            ViewHolder holder=new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull AdapterSepet.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
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
                        MainActivity2.sepet.remove(position);
                        Yenile();

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
        adapter=new AdapterSepet(MainActivity2.sepet);
        recyclerView.setAdapter(adapter);

    }


}