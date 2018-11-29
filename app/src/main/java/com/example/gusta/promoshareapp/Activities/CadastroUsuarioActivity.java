package com.example.gusta.promoshareapp.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gusta.promoshareapp.Classes.Usuario;
import com.example.gusta.promoshareapp.Config.ConfigFirebase;
import com.example.gusta.promoshareapp.R;
import com.example.gusta.promoshareapp.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class CadastroUsuarioActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth autenticacao;
    private DatabaseReference referencia;
    private Usuario usuario;
    private EditText campoEmailCadUsu;
    private EditText campoNomeCadUsu;
    private EditText campoSenhaCadUsu;
    private EditText campoConfirmSenhaCadUsu;
    private CardView btnCadUsu;
    private CardView btnVoltar;
    private TextView linkLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_usuario);

        getSupportActionBar().hide();

        autenticacao = ConfigFirebase.pegaAutenticacao();
        referencia = ConfigFirebase.pegaReferenciaBancoDados();

        campoEmailCadUsu = (EditText) findViewById(R.id.campoEmailCadUsu);
        campoNomeCadUsu = (EditText) findViewById(R.id.campoNomeCadUsu);
        campoSenhaCadUsu = (EditText) findViewById(R.id.campoSenhaCadUsu);
        campoConfirmSenhaCadUsu = (EditText) findViewById(R.id.campoConfirmSenhaCadUsu);
        btnCadUsu = (CardView) findViewById(R.id.btnCadUsu);
        btnVoltar = (CardView) findViewById(R.id.btnVoltar);
        linkLogin = (TextView) findViewById(R.id.linkLogin);

        btnCadUsu.setOnClickListener(this);
        btnVoltar.setOnClickListener(this);
        linkLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btnCadUsu:{

                if (campoEmailCadUsu.getText().toString().isEmpty() ||
                        campoNomeCadUsu.getText().toString().isEmpty() ||
                        campoSenhaCadUsu.getText().toString().isEmpty() ||
                        campoConfirmSenhaCadUsu.getText().toString().isEmpty()){

                    Toast.makeText(CadastroUsuarioActivity.this, "Preencha todos os campos!", Toast.LENGTH_LONG).show();
                }else {

                    if (campoSenhaCadUsu.getText().toString().equals(campoConfirmSenhaCadUsu.getText().toString())){

                        if (Util.verificaConexaoInternet(CadastroUsuarioActivity.this)){

                            usuario = new Usuario();
                            usuario.setEmail(campoEmailCadUsu.getText().toString().trim());
                            usuario.setNome(campoNomeCadUsu.getText().toString().trim());
                            usuario.setSenha(campoSenhaCadUsu.getText().toString().trim());
                            usuario.setTipoUsuario("Usuario");

                            verificaUsuario();
                        }else {

                            Toast.makeText(CadastroUsuarioActivity.this, "Sem conexão com a internet!", Toast.LENGTH_LONG).show();
                        }
                    }else {

                        Toast.makeText(CadastroUsuarioActivity.this, "As senhas devem ser iguais!", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            }

            case R.id.btnVoltar:{

                startActivity(new Intent(CadastroUsuarioActivity.this, InicioActivity.class));
                break;
            }

            case R.id.linkLogin:{

                Util.irParaLogin(CadastroUsuarioActivity.this);
                break;
            }
        }
    }

    private void verificaUsuario() {

        autenticacao.createUserWithEmailAndPassword(usuario.getEmail(), usuario.getSenha()).addOnCompleteListener(CadastroUsuarioActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){

                    insereUsuario(usuario);
                }else {

                    String erro = task.getException().toString();
                    Util.verificaErro(erro, CadastroUsuarioActivity.this);
                }
            }
        });
    }

    private boolean insereUsuario(Usuario usuario) {

        try{

            referencia = ConfigFirebase.pegaReferenciaBancoDados().child("usuarios");
            String key = referencia.push().getKey();
            usuario.setKey(key);
            referencia.child(key).setValue(usuario);
            Toast.makeText(CadastroUsuarioActivity.this, "Usuário cadastrado!", Toast.LENGTH_LONG).show();
            Util.irParaLogin(CadastroUsuarioActivity.this);
            return true;
        }catch (Exception erro){

            Toast.makeText(CadastroUsuarioActivity.this, "Falha ao cadastrar usuário", Toast.LENGTH_LONG).show();
            erro.printStackTrace();
            Util.irParaLogin(CadastroUsuarioActivity.this);
            return false;
        }
    }
}

