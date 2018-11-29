package com.example.gusta.promoshareapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.gusta.promoshareapp.Activities.EstabelecimentoActivity;
import com.example.gusta.promoshareapp.Classes.Promocao;
import com.example.gusta.promoshareapp.Config.ConfigFirebase;
import com.example.gusta.promoshareapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PromocoesAdapter extends RecyclerView.Adapter<PromocoesAdapter.ViewHolder> {

    private DatabaseReference referencia;
    private FirebaseStorage storage;
    private Promocao todasPromocoes;
    private List<Promocao> listaPromocoes;
    private Context contexto;
    private List<Promocao> promocoes;
    private String nomeEstabelecimento;

    public PromocoesAdapter(List<Promocao> l, Context c){

        contexto = c;
        listaPromocoes = l;
    }

    @NonNull
    @Override
    public PromocoesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.lista_promocoes, viewGroup, false);
        return new PromocoesAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PromocoesAdapter.ViewHolder holder, int position) {

        final Promocao item = listaPromocoes.get(position);
        promocoes = new ArrayList<>();

        referencia = ConfigFirebase.pegaReferenciaBancoDados();
        storage = ConfigFirebase.pegaStorage();

        referencia.child("promocoes").orderByChild("key").equalTo(item.getKey()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                promocoes.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){

                    todasPromocoes = postSnapshot.getValue(Promocao.class);

                    promocoes.add(todasPromocoes);

                    final StorageReference storageReference = storage.getReferenceFromUrl(item.getUrlImg());

                    DisplayMetrics metrics = contexto.getResources().getDisplayMetrics();
                    final int largura = metrics.widthPixels / 2;
                    final int altura = metrics.heightPixels / 4;

                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            Picasso.get().load(uri.toString()).resize(altura, largura).centerCrop().into(holder.imgPromocao);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        referencia.child("usuarios").orderByChild("email").equalTo(item.getEstabelecimento()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){

                    nomeEstabelecimento = postSnapshot.child("nome").getValue().toString();
                    holder.txtEstabPromo.setText(nomeEstabelecimento);
                    holder.txtEstabPromo.setPaintFlags(holder.txtEstabPromo.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        holder.txtNomeProdPromo.setText(item.getNomeProd());
        holder.txtDescricaoProdPromo.setText(item.getDescricaoProd());
        holder.txtPrecoAntigoPromo.setText("R$ " + item.getPrecoAntigo());
        holder.txtPrecoAntigoPromo.setPaintFlags(holder.txtPrecoAntigoPromo.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.txtPrecoNovoPromo.setText("R$ " + item.getPrecoPromo());

        holder.txtEstabPromo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(contexto, EstabelecimentoActivity.class);
                intent.putExtra("estabelecimento", item.getEstabelecimento().toString());
                contexto.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaPromocoes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        protected ImageView imgPromocao;
        protected TextView txtNomeProdPromo;
        protected TextView txtDescricaoProdPromo;
        protected TextView txtPrecoAntigoPromo;
        protected TextView txtPrecoNovoPromo;
        protected TextView txtEstabPromo;

        public ViewHolder(View itemView){
            super(itemView);

            imgPromocao = (ImageView) itemView.findViewById(R.id.imgPromocao);
            txtNomeProdPromo = (TextView) itemView.findViewById(R.id.txtNomeProdPromo);
            txtDescricaoProdPromo = (TextView) itemView.findViewById(R.id.txtDescricaoProdPromo);
            txtPrecoAntigoPromo = (TextView) itemView.findViewById(R.id.txtPrecoAntigoPromo);
            txtPrecoNovoPromo = (TextView) itemView.findViewById(R.id.txtPrecoNovoPromo);
            txtEstabPromo = (TextView) itemView.findViewById(R.id.txtEstabPromo);
        }
    }
}
