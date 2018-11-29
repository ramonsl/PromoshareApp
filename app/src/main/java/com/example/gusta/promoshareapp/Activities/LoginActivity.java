package com.example.gusta.promoshareapp.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gusta.promoshareapp.Config.ConfigFirebase;
import com.example.gusta.promoshareapp.R;
import com.example.gusta.promoshareapp.Classes.Usuario;
import com.example.gusta.promoshareapp.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth autenticacao;
    private Usuario usuario;
    private EditText campoEmailLogin;
    private EditText campoSenhaLogin;
    private TextView linkRecuperarSenha;
    private CardView btnLogin;
    private TextView linkCadUsu;
    private AlertDialog alerta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        autenticacao = ConfigFirebase.pegaAutenticacao();

        campoEmailLogin = (EditText) findViewById(R.id.campoEmailLogin);
        campoSenhaLogin = (EditText) findViewById(R.id.campoSenhaLogin);
        linkRecuperarSenha = (TextView) findViewById(R.id.linkRecuperarSenha);
        btnLogin = (CardView) findViewById(R.id.btnLogin);
        linkCadUsu = (TextView) findViewById(R.id.linkCadUsu);

        linkRecuperarSenha.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        linkCadUsu.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.linkRecuperarSenha:{

                recuperaSenha();
                break;
            }

            case R.id.btnLogin:{

                if (campoEmailLogin.getText().toString().isEmpty() && campoSenhaLogin.getText().toString().isEmpty()){

                    Toast.makeText(LoginActivity.this, "Preencha todos os campos!", Toast.LENGTH_LONG).show();
                }else {

                    if (Util.verificaConexaoInternet(LoginActivity.this)){

                        usuario = new Usuario();

                        usuario.setEmail(campoEmailLogin.getText().toString().trim());
                        usuario.setSenha(campoSenhaLogin.getText().toString().trim());

                        validaLogin();
                    }else {

                        Toast.makeText(LoginActivity.this, "Sem conexão com a internet!", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            }

            case R.id.linkCadUsu:{

                Util.irParaCadUsu(LoginActivity.this);
                break;
            }

            default:{

                break;
            }
        }
    }

    private void recuperaSenha() {

        final String campoEmail = campoEmailLogin.getText().toString().trim();

        final EditText campoRecuperaSenha = new EditText(LoginActivity.this);

        if (campoEmail.isEmpty()){

            campoRecuperaSenha.setHint("exemplo@exemplo.com");
        }else{

            campoRecuperaSenha.setText(campoEmail);
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setCancelable(false);
        builder.setTitle("Recuperação de senha");
        builder.setMessage("Informe seu e-mail e logo você receberá uma mensagem com um link para recuperação de senha.");
        builder.setView(campoRecuperaSenha);

        if (!campoRecuperaSenha.getText().equals("")){

            builder.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if (campoRecuperaSenha.getText().toString().trim().isEmpty()){

                        Toast.makeText(LoginActivity.this, "É necessário preencher o campo de e-mail. Tente novamente!", Toast.LENGTH_LONG).show();
                    }else {

                        String emailRecuperaSenha = campoRecuperaSenha.getText().toString().trim();

                        autenticacao.sendPasswordResetEmail(emailRecuperaSenha).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()){

                                    Toast.makeText(LoginActivity.this, "Em instantes você receberá um e-mail!", Toast.LENGTH_LONG).show();
                                    refresh();
                                }else{

                                    String erro = task.getException().toString();
                                    Util.verificaErro(erro, LoginActivity.this);
                                }
                            }
                        });
                    }
                }
            });

            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    refresh();
                }
            });
        }else{

            Toast.makeText(LoginActivity.this, "Informe seu e-mail!", Toast.LENGTH_LONG).show();
        }

        alerta = builder.create();
        alerta.show();
    }

    private void refresh(){

        Intent intent = getIntent();
        startActivity(intent);
        finish();
    }

    private void validaLogin() {

        autenticacao.signInWithEmailAndPassword(usuario.getEmail(), usuario.getSenha()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){

                    Toast.makeText(LoginActivity.this, "Bem-vindo!", Toast.LENGTH_LONG).show();
                    Util.irParaPrincipal(LoginActivity.this);
                }else {

                    String erro = task.getException().toString();
                    Util.verificaErro(erro, LoginActivity.this);
                }
            }
        });
    }
}
