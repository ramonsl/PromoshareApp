package com.example.gusta.promoshareapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gusta.promoshareapp.Config.ConfigFirebase;
import com.example.gusta.promoshareapp.R;
import com.example.gusta.promoshareapp.Classes.Usuario;
import com.example.gusta.promoshareapp.Util;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class InicioActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth autenticacao;
    private DatabaseReference referencia;
    private Usuario usuario;
    private CardView btnEntrarGoogle;
    private CardView btnEntrarEmail;
    private CardView btnCadConta;
    private TextView linkCadEstab;
    private GoogleSignInClient googleSignInClient;
    private String emailUsuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        autenticacao = ConfigFirebase.pegaAutenticacao();
        referencia = ConfigFirebase.pegaReferenciaBancoDados();

        getSupportActionBar().hide();

        servicosGoogle();

        btnEntrarGoogle = (CardView) findViewById(R.id.btnEntrarGoogle);
        btnEntrarEmail = (CardView) findViewById(R.id.btnEntrarEmail);
        btnCadConta = (CardView) findViewById(R.id.btnCadConta);
        linkCadEstab = (TextView) findViewById(R.id.linkCadEstab);

        btnEntrarGoogle.setOnClickListener(this);
        btnEntrarEmail.setOnClickListener(this);
        btnCadConta.setOnClickListener(this);
        linkCadEstab.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btnEntrarGoogle:{

                signInGoogle();
                break;
            }

            case R.id.btnEntrarEmail:{

                Util.irParaLogin(InicioActivity.this);
                break;
            }

            case R.id.btnCadConta:{

                Util.irParaCadUsu(InicioActivity.this);
                break;
            }

            case R.id.linkCadEstab:{

                Util.idParaCadEstab(InicioActivity.this);
                break;
            }

            default:{

                break;
            }
        }
    }

    private void servicosGoogle() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(InicioActivity.this, gso);
    }

    private void signInGoogle() {

        GoogleSignInAccount conta = GoogleSignIn.getLastSignedInAccount(InicioActivity.this);

        if (conta == null){

            Intent intent = googleSignInClient.getSignInIntent();
            startActivityForResult(intent, 555);
        }else {

            Toast.makeText(InicioActivity.this, "Bem-vindo(a) " + autenticacao.getCurrentUser().getDisplayName().toString() + "!", Toast.LENGTH_LONG).show();
            Util.irParaPrincipal(InicioActivity.this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 555){

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {

                GoogleSignInAccount conta = task.getResult(ApiException.class);
                adicionarContaGoogleAoFirebase(conta);
            }catch (ApiException erro){

                Toast.makeText(InicioActivity.this, "Erro ao realizar login com o Google!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void adicionarContaGoogleAoFirebase(final GoogleSignInAccount acct) {

        final AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        autenticacao.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){


                            referencia.child("usuarios").orderByChild("email").equalTo(autenticacao.getCurrentUser().getEmail()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if (!dataSnapshot.exists()){

                                        usuario = new Usuario();
                                        usuario.setEmail(autenticacao.getCurrentUser().getEmail().toString());
                                        usuario.setNome(autenticacao.getCurrentUser().getDisplayName().toString());
                                        usuario.setTipoUsuario("Usuario");

                                        insereUsuario(usuario);
                                    }else {

                                        Toast.makeText(InicioActivity.this, "Bem-vindo!", Toast.LENGTH_LONG).show();
                                        Util.irParaPrincipal(InicioActivity.this);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }else {

                            String erro = task.getException().toString();
                            Util.verificaErro(erro, InicioActivity.this);
                        }
                    }
                });
    }

    private void insereUsuario(Usuario usuario) {


        try{

            referencia = ConfigFirebase.pegaReferenciaBancoDados().child("usuarios");
            String key = referencia.push().getKey();
            usuario.setKey(key);
            referencia.child(key).setValue(usuario);
            Toast.makeText(InicioActivity.this, "Bem-vindo!", Toast.LENGTH_LONG).show();
            Util.irParaPrincipal(InicioActivity.this);
        }catch (Exception erro){

            Toast.makeText(InicioActivity.this, "Falha ao cadastrar usuário", Toast.LENGTH_LONG).show();
            erro.printStackTrace();
            Util.irParaInicio(InicioActivity.this);
        }
    }
}
