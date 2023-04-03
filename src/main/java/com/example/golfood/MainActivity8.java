package com.example.golfood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.Task;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity8 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main8);
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        Task<QuerySnapshot> siparisler=db.collection("siparis").whereEqualTo("Müşteri",MainActivity2.ID).get();
        while (!siparisler.isComplete());// skcem ama :)
        List<DocumentSnapshot> sonucSip=siparisler.getResult().getDocuments();
        List<DocumentSnapshot> oncelik=new ArrayList<>();
        List<DocumentSnapshot> son=new ArrayList<>();
        for (DocumentSnapshot item : sonucSip) {
            if (item.get("Durum").toString().equals("Hazırlanıyor")){
                oncelik.add(item);
            }
            else{
                son.add(item);
            }
        }
        oncelik.addAll(son);
        oncelik.addAll(sonucSip);
        RecyclerView recyclerView=(RecyclerView) findViewById(R.id.SipList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        Adapter adapter=new Adapter(oncelik);
        recyclerView.setAdapter(adapter);

    }

    public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>{
        List<DocumentSnapshot> veri;
        public Adapter(List<DocumentSnapshot> veri)
        {
            this.veri=veri;
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            MaterialTextView T1,T2,T3,T4;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                T1=(MaterialTextView) itemView.findViewById(R.id.sipT1);
                T2=(MaterialTextView) itemView.findViewById(R.id.sipT2);
                T3=(MaterialTextView) itemView.findViewById(R.id.sipT3);
                T4=(MaterialTextView) itemView.findViewById(R.id.sipT4);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.gecmis_siparis,parent,false);
            ViewHolder holder=new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            FirebaseFirestore db=FirebaseFirestore.getInstance();
            Task<DocumentSnapshot> asd=db.collection("menu").document(veri.get(position).getString("Menu")).get();
            while(!asd.isComplete());
            try {
                holder.T1.setText(veri.get(position).getString("Durum").toString());
                holder.T2.setText(asd.getResult().getString("Başlık"));
                holder.T3.setText(asd.getResult().getString("Fiyat")+" TL");
                if (veri.get(position).getString("Adres")!=null)
                {
                    holder.T4.setText(veri.get(position).getString("Adres"));
                }
                else
                {
                    holder.T4.setText("Yok");
                }

            }
            catch (Exception e){}


        }

        @Override
        public int getItemCount() {
            return veri.size();
        }
    }
}