package com.example.gusta.promoshareapp.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.gusta.promoshareapp.Classes.Promocao;
import com.example.gusta.promoshareapp.Config.ConfigFirebase;
import com.example.gusta.promoshareapp.R;
import com.example.gusta.promoshareapp.Util;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.UUID;

public class CadastroPromocoes extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth autenticacao;
    private DatabaseReference referencia;
    private StorageReference referenciaStorage;
    private FirebaseStorage storage;
    private Promocao promocao;
    private ImageView imgCadPromo;
    private EditText campoNomeCadPromo;
    private EditText campoDescCadPromo;
    private EditText campoPrecoPromoCadPromo;
    private EditText campoPrecoAntigoCadPromo;
    public String urlImg;
    private CardView btnCadPromo;
    private CardView btnVoltar;
    private String usuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_promocoes);

        getSupportActionBar().hide();

        autenticacao = ConfigFirebase.pegaAutenticacao();
        referencia = ConfigFirebase.pegaReferenciaBancoDados();
        referenciaStorage = ConfigFirebase.pegaReferenciaStorage();
        storage = ConfigFirebase.pegaStorage();

        carregaImagemPadrao();
        usuarioLogado = autenticacao.getCurrentUser().getEmail();

        imgCadPromo = (ImageView) findViewById(R.id.imgCadPromo);
        campoNomeCadPromo = (EditText) findViewById(R.id.campoNomeCadProd);
        campoDescCadPromo = (EditText) findViewById(R.id.campoDescCadProd);
        campoPrecoPromoCadPromo = (EditText) findViewById(R.id.campoPrecoPromoCadProd);
        campoPrecoAntigoCadPromo = (EditText) findViewById(R.id.campoPrecoAntigoCadProd);
        btnCadPromo = (CardView) findViewById(R.id.btnCadPromo);
        btnVoltar = (CardView) findViewById(R.id.btnVoltar);

        imgCadPromo.setOnClickListener(this);
        btnCadPromo.setOnClickListener(this);
        btnVoltar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.imgCadPromo:{

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(intent, "Selecione uma imagem"), 123);
                break;
            }

            case R.id.btnCadPromo:{

                cadastraImgProduto();

                if (campoNomeCadPromo.getText().toString().isEmpty() ||
                        campoDescCadPromo.getText().toString().isEmpty() ||
                        campoPrecoPromoCadPromo.getText().toString().isEmpty() ||
                        campoPrecoAntigoCadPromo.getText().toString().isEmpty()){

                    Toast.makeText(CadastroPromocoes.this, "Preencha todos os campos!", Toast.LENGTH_LONG).show();
                }else {

                    if (Util.verificaConexaoInternet(CadastroPromocoes.this)){

                        promocao = new Promocao();
                        promocao.setNomeProd(campoNomeCadPromo.getText().toString().trim());
                        promocao.setDescricaoProd(campoDescCadPromo.getText().toString().trim());
                        promocao.setPrecoPromo(campoPrecoPromoCadPromo.getText().toString().trim());
                        promocao.setPrecoAntigo(campoPrecoAntigoCadPromo.getText().toString().trim());
                        promocao.setUrlImg("gs://promoshareapp-a9522.appspot.com/" + urlImg.toString().trim());
                        promocao.setEstabelecimento(usuarioLogado);

                        inserePromocao(promocao);
                    }else {

                        Toast.makeText(CadastroPromocoes.this, "Sem conexão com a internet!", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            }

            case R.id.btnVoltar:{

                Util.irParaPrincipal(CadastroPromocoes.this);
                break;
            }
        }
    }

    private boolean inserePromocao(Promocao promocao) {

        try {

            referencia = ConfigFirebase.pegaReferenciaBancoDados().child("promocoes");
            String key = referencia.push().getKey();
            promocao.setKey(key);
            referencia.child(key).setValue(promocao);
            Toast.makeText(CadastroPromocoes.this, "Promoção cadastrada!", Toast.LENGTH_LONG).show();
            Util.irParaPrincipal(CadastroPromocoes.this);
            return true;
        }catch (Exception erro){

            Toast.makeText(CadastroPromocoes.this, "Erro ao inserir promoção!", Toast.LENGTH_LONG).show();
            erro.printStackTrace();
            return false;
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
                Picasso.get().load(imagemSelecionada.toString()).resize(altura,largura).centerCrop().into(imgCadPromo);
            }
        }
    }

    private void cadastraImgProduto() {

        urlImg = "ImgPromocoes/" + UUID.randomUUID() + ".jpg";
        final StorageReference imagemReferencia = referenciaStorage.child(urlImg);
        imgCadPromo.setDrawingCacheEnabled(true);
        imgCadPromo.buildDrawingCache();
        Bitmap bitmap = imgCadPromo.getDrawingCache();

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
                    carregaImagemPadrao();
                }
            }
        });
    }

    private void carregaImagemPadrao(){

        final StorageReference storageReference = storage.getReferenceFromUrl("gs://promoshareapp-a9522.appspot.com/img_prod.png");

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        final int largura = metrics.widthPixels / 2;
        final int altura = metrics.heightPixels / 4;

        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Picasso.get().load(uri.toString()).resize(altura, largura).centerCrop().into(imgCadPromo);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
}
