package com.example.gusta.promoshareapp.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gusta.promoshareapp.Activities.EditarPromocaoActivity;
import com.example.gusta.promoshareapp.Activities.EstabelecimentoActivity;
import com.example.gusta.promoshareapp.Activities.PrincipalActivity;
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

public class PromocoesEstabelecimentoAdapter extends RecyclerView.Adapter<PromocoesEstabelecimentoAdapter.ViewHolder> {

    private DatabaseReference referencia;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private Promocao todasPromocoes;
    private List<Promocao> listaPromocoes;
    private Context contexto;
    private List<Promocao> promocoes;
    private AlertDialog alerta;

    public PromocoesEstabelecimentoAdapter(List<Promocao> l, Context c){

        contexto = c;
        listaPromocoes = l;
    }

    @NonNull
    @Override
    public PromocoesEstabelecimentoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.lista_promocoes_estabelecimento, viewGroup, false);
        return new PromocoesEstabelecimentoAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PromocoesEstabelecimentoAdapter.ViewHolder holder, int position) {

        final Promocao item = listaPromocoes.get(position);
        promocoes = new ArrayList<>();

        referencia = ConfigFirebase.pegaReferenciaBancoDados();
        storage = ConfigFirebase.pegaStorage();
        storageReference = ConfigFirebase.pegaReferenciaStorage();

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

                            Picasso.get().load(uri.toString()).resize(altura, largura).centerCrop().into(holder.imgPromocaoEstab);
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

        holder.txtNomeProdPromoEstab.setText(item.getNomeProd());
        holder.txtDescricaoProdPromoEstab.setText(item.getDescricaoProd());
        holder.txtPrecoAntigoPromoEstab.setText("R$ " + item.getPrecoAntigo());
        holder.txtPrecoAntigoPromoEstab.setPaintFlags(holder.txtPrecoAntigoPromoEstab.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.txtPrecoNovoPromoEstab.setText("R$ " + item.getPrecoPromo());

        holder.btnExcluirPromo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
                builder.setMessage("Tem certeza que deseja excluir essa promoção?");

                builder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        int inicio = item.getUrlImg().indexOf("ImgP");
                        int fim = item.getUrlImg().lastIndexOf("jpg");
                        String caminhoImagem = item.getUrlImg().substring(inicio, fim + 3);

                        StorageReference referenciaImagem = storageReference.child(caminhoImagem);

                        referenciaImagem.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                referencia.child("promocoes").child(item.getKey()).removeValue();
                                Toast.makeText(contexto, "Promoção deletada com sucesso!", Toast.LENGTH_LONG).show();
                                refresh();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(contexto, "Falha ao deletar promoção", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        builder.setCancelable(true);
                    }
                });

                alerta = builder.create();
                alerta.show();
            }
        });

        holder.btnEditarPromo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(contexto, EditarPromocaoActivity.class);
                intent.putExtra("key", item.getKey());
                intent.putExtra("nomeProd", item.getNomeProd());
                intent.putExtra("descProd", item.getDescricaoProd());
                intent.putExtra("precoPromoProd", item.getPrecoPromo());
                intent.putExtra("precoAntigoProd", item.getPrecoAntigo());
                contexto.startActivity(intent);
                ((Activity)contexto).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaPromocoes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        protected ImageView imgPromocaoEstab;
        protected TextView txtNomeProdPromoEstab;
        protected TextView txtDescricaoProdPromoEstab;
        protected TextView txtPrecoAntigoPromoEstab;
        protected TextView txtPrecoNovoPromoEstab;
        protected CardView btnEditarPromo;
        protected CardView btnExcluirPromo;

        public ViewHolder(View itemView){
            super(itemView);

            imgPromocaoEstab = (ImageView) itemView.findViewById(R.id.imageView);
            txtNomeProdPromoEstab = (TextView) itemView.findViewById(R.id.txtNomeProdPromoEstab);
            txtDescricaoProdPromoEstab = (TextView) itemView.findViewById(R.id.txtDescricaoProdPromoEstab);
            txtPrecoAntigoPromoEstab = (TextView) itemView.findViewById(R.id.txtPrecoAntigoPromoEstab);
            txtPrecoNovoPromoEstab = (TextView) itemView.findViewById(R.id.txtPrecoNovoPromoEstab);
            btnEditarPromo = (CardView) itemView.findViewById(R.id.btnEditarPromo);
            btnExcluirPromo = (CardView) itemView.findViewById(R.id.btnExcluirPromo);
        }
    }

    private void refresh() {

        contexto.startActivity(new Intent(contexto, PrincipalActivity.class));
    }
}
