package com.example.gusta.promoshareapp.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.gusta.promoshareapp.Adapters.PromocoesEstabelecimentoAdapter;
import com.example.gusta.promoshareapp.Classes.Promocao;
import com.example.gusta.promoshareapp.Classes.Usuario;
import com.example.gusta.promoshareapp.Config.ConfigFirebase;
import com.example.gusta.promoshareapp.R;
import com.example.gusta.promoshareapp.Util;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class EditarPromocaoActivity extends AppCompatActivity{

    private FirebaseAuth autenticacao;
    private FirebaseStorage storage;
    private StorageReference referenciaStorage;
    private DatabaseReference referencia;
    private Promocao promocao;
    private ImageView imgEditPromo;
    private EditText campoNomeEditPromo;
    private EditText campoDescEditPromo;
    private EditText campoPrecoPromoEditPromo;
    private EditText campoPrecoAntigoEditPromo;
    private CardView btnEditPromo;
    private CardView btnVoltar;

    public String urlImg;
    public String usuarioLogado;

    private String key = "";
    private String urlImagemPromo = "";
    private String nomePromo = "";
    private String descPromo = "";
    private String precoPromo = "";
    private String precoAntigoPromo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_promocao);

        getSupportActionBar().hide();

        autenticacao = ConfigFirebase.pegaAutenticacao();
        referencia = ConfigFirebase.pegaReferenciaBancoDados();
        storage = ConfigFirebase.pegaStorage();
        referenciaStorage = ConfigFirebase.pegaReferenciaStorage();

        usuarioLogado = autenticacao.getCurrentUser().getEmail();

        imgEditPromo = (ImageView) findViewById(R.id.imgEditPromo);
        campoNomeEditPromo = (EditText) findViewById(R.id.campoNomeEditProd);
        campoDescEditPromo = (EditText) findViewById(R.id.campoDescEditProd);
        campoPrecoPromoEditPromo = (EditText) findViewById(R.id.campoPrecoPromoEditProd);
        campoPrecoAntigoEditPromo = (EditText) findViewById(R.id.campoPrecoAntigoEditProd);
        btnEditPromo = (CardView) findViewById(R.id.btnEditPromo);
        btnVoltar = (CardView) findViewById(R.id.btnVoltar);

        carregaPromocao();

        imgEditPromo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(intent, "Selecione uma imagem"), 123);
            }
        });

        btnEditPromo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cadastraImgProduto();

                if (campoNomeEditPromo.getText().toString().isEmpty() ||
                        campoDescEditPromo.getText().toString().isEmpty() ||
                        campoPrecoPromoEditPromo.getText().toString().isEmpty() ||
                        campoPrecoAntigoEditPromo.getText().toString().isEmpty()){

                    Toast.makeText(EditarPromocaoActivity.this, "Preencha todos os campos!", Toast.LENGTH_LONG).show();
                }else {

                    if (Util.verificaConexaoInternet(EditarPromocaoActivity.this)){

                        Promocao promocao = new Promocao();
                        promocao.setKey(key);
                        promocao.setNomeProd(campoNomeEditPromo.getText().toString());
                        promocao.setDescricaoProd(campoDescEditPromo.getText().toString());
                        promocao.setPrecoPromo(campoPrecoPromoEditPromo.getText().toString());
                        promocao.setPrecoAntigo(campoPrecoAntigoEditPromo.getText().toString());
                        promocao.setUrlImg("gs://promoshareapp-a9522.appspot.com/" + urlImg.toString());
                        promocao.setEstabelecimento(usuarioLogado.toString());

                        alteraPromocao(promocao);
                    }else {

                        Toast.makeText(EditarPromocaoActivity.this, "Sem conexão com a internet!", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Util.irParaPrincipal(EditarPromocaoActivity.this);
            }
        });
    }

    @Override
    public void onBackPressed() {

        startActivity(new Intent(EditarPromocaoActivity.this, PrincipalActivity.class));
    }

    private void alteraPromocao(Promocao promocao) {

        try {

            referencia = ConfigFirebase.pegaReferenciaBancoDados().child("promocoes");
            referencia.child(key).setValue(promocao);
            finish();

            startActivity(new Intent(EditarPromocaoActivity.this, PrincipalActivity.class));
            Toast.makeText(EditarPromocaoActivity.this, "Promoção alterada com sucesso!", Toast.LENGTH_LONG).show();

        }catch (Exception e){

            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        final int largura = metrics.widthPixels / 2;
        final int altura = metrics.heightPixels / 4;

        if (resultCode == Activity.RESULT_OK){

            if (requestCode == 123){

                Uri imagemSelecionada = data.getData();
                Picasso.get().load(imagemSelecionada.toString()).resize(altura,largura).centerCrop().into(imgEditPromo);
            }
        }
    }

    private void cadastraImgProduto() {

        urlImg = "ImgPromocoes/" + UUID.randomUUID() + ".jpg";
        final StorageReference imagemReferencia = referenciaStorage.child(urlImg);
        imgEditPromo.setDrawingCacheEnabled(true);
        imgEditPromo.buildDrawingCache();
        Bitmap bitmap = imgEditPromo.getDrawingCache();

        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArray);
        byte [] data = byteArray.toByteArray();

        UploadTask uploadTask = imagemReferencia.putBytes(data);

        Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                return imagemReferencia.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                }
            }
        });
    }

    private void carregaPromocao() {

        Intent intent = getIntent();

        key = intent.getStringExtra("key");
        nomePromo = intent.getStringExtra("nomeProd");
        descPromo = intent.getStringExtra("descProd");
        precoPromo = intent.getStringExtra("precoPromoProd");
        precoAntigoPromo = intent.getStringExtra("precoAntigoProd");


        referencia.child("promocoes").orderByChild("key").equalTo(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){

                    promocao = new Promocao();
                    promocao = postSnapshot.getValue(Promocao.class);
                    urlImagemPromo = promocao.getUrlImg();

                    final StorageReference storageReference = storage.getReferenceFromUrl(urlImagemPromo);

                    DisplayMetrics metrics = EditarPromocaoActivity.this.getResources().getDisplayMetrics();
                    final int largura = metrics.widthPixels / 2;
                    final int altura = metrics.heightPixels / 4;

                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            Picasso.get().load(uri.toString()).resize(altura, largura).centerCrop().into(imgEditPromo);
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

        campoNomeEditPromo.setText(nomePromo);
        campoDescEditPromo.setText(descPromo);
        campoPrecoPromoEditPromo.setText(precoPromo);
        campoPrecoAntigoEditPromo.setText(precoAntigoPromo);
    }
}
