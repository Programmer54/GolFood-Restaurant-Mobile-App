package com.example.golfood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StreamDownloadTask;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {
    List<DocumentSnapshot> gelen=new ArrayList<>();
    static List<DocumentSnapshot> sepet=new ArrayList<>();
    static String ID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        Task<QuerySnapshot> gelen=db.collection("menu").get();
        while (!gelen.isComplete()); //Usülsüzlük :(
        List<DocumentSnapshot> sonuc=gelen.getResult().getDocuments();
        RecyclerView recyclerView=(RecyclerView) findViewById(R.id.RcyView);
        Adapter adapter=new Adapter(sonuc);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView.setAdapter(adapter);
        FloatingActionButton button=(FloatingActionButton) findViewById(R.id.sepet_tut);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),MainActivity4.class);
                intent.putExtra("ID",ID);
                startActivity(intent);
            }
        });



    }


    public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>{
        List<DocumentSnapshot> veri;

        public Adapter(List<DocumentSnapshot> veri)
        {
            this.veri=veri;
        }


        public class ViewHolder extends RecyclerView.ViewHolder{
            ImageView imageView;
            TextView textView;
            TextView textView1;
            TextView textView2;
            Button button;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                imageView=(ImageView) itemView.findViewById(R.id.r1);
                textView=(TextView) itemView.findViewById(R.id.T1);
                textView1=(TextView) itemView.findViewById(R.id.T2);
                textView2=(TextView) itemView.findViewById(R.id.T3);
                button=(Button) itemView.findViewById(R.id.B1);

            }
        }


        /**
         * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
         * an item.
         * <p>
         * This new ViewHolder should be constructed with a new View that can represent the items
         * of the given type. You can either create a new View manually or inflate it from an XML
         * layout file.
         * <p>
         * The new ViewHolder will be used to display items of the adapter using
         * {@link #onBindViewHolder(ViewHolder, int, List)}. Since it will be re-used to display
         * different items in the data set, it is a good idea to cache references to sub views of
         * the View to avoid unnecessary {@link View#findViewById(int)} calls.
         *
         * @param parent   The ViewGroup into which the new View will be added after it is bound to
         *                 an adapter position.
         * @param viewType The view type of the new View.
         * @return A new ViewHolder that holds a View of the given view type.
         * @see #getItemViewType(int)
         * @see #onBindViewHolder(ViewHolder, int)
         */
        @NonNull
        @Override
        public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.ozel_kart,parent,false);
            ViewHolder holder=new ViewHolder(view);
            return holder;
        }

        /**
         * Called by RecyclerView to display the data at the specified position. This method should
         * update the contents of the {@link ViewHolder#itemView} to reflect the item at the given
         * position.
         * <p>
         * Note that unlike {@link ListView}, RecyclerView will not call this method
         * again if the position of the item changes in the data set unless the item itself is
         * invalidated or the new position cannot be determined. For this reason, you should only
         * use the <code>position</code> parameter while acquiring the related data item inside
         * this method and should not keep a copy of it. If you need the position of an item later
         * on (e.g. in a click listener), use {@link ViewHolder#getAdapterPosition()} which will
         * have the updated adapter position.
         * <p>
         * Override {@link #onBindViewHolder(ViewHolder, int, List)} instead if Adapter can
         * handle efficient partial bind.
         *
         * @param holder   The ViewHolder which should be updated to represent the contents of the
         *                 item at the given position in the data set.
         * @param position The position of the item within the adapter's data set.
         */
        @Override
        public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
                try {
                    StorageReference storage=FirebaseStorage.getInstance().getReference();
                    Task<StorageMetadata> boyut=storage.child(veri.get(position).getString("Resim").toString()).getMetadata();
                    while (!boyut.isComplete());//Usülsüzlük :(
                    Task<byte[]> bekle=storage.child(veri.get(position).getString("Resim").toString()).getBytes(boyut.getResult().getSizeBytes());
                    while (!bekle.isComplete());//Usülsüzlük :(
                    Bitmap bm= BitmapFactory.decodeByteArray(bekle.getResult(),0,bekle.getResult().length);
                    holder.imageView.setImageBitmap(bm);
                    holder.textView.setText(veri.get(position).getString("Başlık"));
                    holder.textView1.setText(veri.get(position).getString("Fiyat")+" TL");
                    holder.textView2.setText(veri.get(position).getString("Açıklama"));
                    holder.button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            sepet.add(veri.get(position));
                            Toast.makeText(getApplicationContext(),"Sepete Eklendi",Toast.LENGTH_SHORT).show();
                        }
                    });
                }catch (Exception e){}



        }

        /**
         * Returns the total number of items in the data set held by the adapter.
         *
         * @return The total number of items in this adapter.
         */
        @Override
        public int getItemCount() {
            return veri.size();
        }
    }


}