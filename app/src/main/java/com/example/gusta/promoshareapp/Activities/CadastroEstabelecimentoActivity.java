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

import com.example.gusta.promoshareapp.Config.ConfigFirebase;
import com.example.gusta.promoshareapp.R;
import com.example.gusta.promoshareapp.Classes.Usuario;
import com.example.gusta.promoshareapp.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class CadastroEstabelecimentoActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth autenticacao;
    private DatabaseReference referencia;
    private Usuario usuario;
    private EditText campoEmailCadEstab;
    private EditText campoNomeCadEstab;
    private EditText campoPaisCadEstab;
    private EditText campoEstadoCadEstab;
    private EditText campoCidadeCadEstab;
    private EditText campoBairroCadEstab;
    private EditText campoRuaCadEstab;
    private EditText campoNumeroCadEstab;
    private EditText campoSenhaCadEstab;
    private EditText campoConfirmSenhaCadEstab;
    private CardView btnCadEstab;
    private CardView btnVoltar;
    private TextView linkLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_estabelecimento);

        getSupportActionBar().hide();

        autenticacao = ConfigFirebase.pegaAutenticacao();
        referencia = ConfigFirebase.pegaReferenciaBancoDados();

        campoEmailCadEstab = (EditText) findViewById(R.id.campoEmailCadEstab);
        campoNomeCadEstab = (EditText) findViewById(R.id.campoNomeCadEstab);
        campoPaisCadEstab = (EditText) findViewById(R.id.campoPaisCadEstab);
        campoEstadoCadEstab = (EditText) findViewById(R.id.campoEstadoCadEstab);
        campoCidadeCadEstab = (EditText) findViewById(R.id.campoCidadeCadEstab);
        campoBairroCadEstab = (EditText) findViewById(R.id.campoBairroCadEstab);
        campoRuaCadEstab = (EditText) findViewById(R.id.campoRuaCadEstab);
        campoNumeroCadEstab = (EditText) findViewById(R.id.campoNumeroCadEstab);
        campoSenhaCadEstab = (EditText) findViewById(R.id.campoSenhaCadEstab);
        campoConfirmSenhaCadEstab = (EditText) findViewById(R.id.campoConfirmSenhaCadEstab);
        btnCadEstab = (CardView) findViewById(R.id.btnCadEstab);
        btnVoltar = (CardView) findViewById(R.id.btnVoltar);
        linkLogin = (TextView) findViewById(R.id.linkLogin);

        btnCadEstab.setOnClickListener(this);
        btnVoltar.setOnClickListener(this);
        linkLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btnCadEstab:{

                if (campoEmailCadEstab.getText().toString().isEmpty() ||
                        campoNomeCadEstab.getText().toString().isEmpty() ||
                        campoPaisCadEstab.getText().toString().isEmpty() ||
                        campoEstadoCadEstab.getText().toString().isEmpty() ||
                        campoCidadeCadEstab.getText().toString().isEmpty() ||
                        campoBairroCadEstab.getText().toString().isEmpty() ||
                        campoRuaCadEstab.getText().toString().isEmpty() ||
                        campoNumeroCadEstab.getText().toString().isEmpty() ||
                        campoSenhaCadEstab.getText().toString().isEmpty() ||
                        campoConfirmSenhaCadEstab.getText().toString().isEmpty()){

                    Toast.makeText(CadastroEstabelecimentoActivity.this, "Preencha todos os campos!", Toast.LENGTH_LONG).show();
                }else {

                    if (campoSenhaCadEstab.getText().toString().equals(campoConfirmSenhaCadEstab.getText().toString())){

                        if (Util.verificaConexaoInternet(CadastroEstabelecimentoActivity.this)){

                         usuario = new Usuario();
                         usuario.setEmail(campoEmailCadEstab.getText().toString().trim());
                         usuario.setNome(campoNomeCadEstab.getText().toString().trim());
                         usuario.setPais(campoPaisCadEstab.getText().toString().trim());
                         usuario.setEstado(campoEstadoCadEstab.getText().toString().trim());
                         usuario.setCidade(campoCidadeCadEstab.getText().toString().trim());
                         usuario.setBairro(campoBairroCadEstab.getText().toString().trim());
                         usuario.setRua(campoRuaCadEstab.getText().toString().trim());
                         usuario.setNumero(campoNumeroCadEstab.getText().toString().trim());
                         usuario.setSenha(campoSenhaCadEstab.getText().toString().trim());
                         usuario.setTipoUsuario("Estabelecimento");

                         verificaUsuario();
                        }else {

                            Toast.makeText(CadastroEstabelecimentoActivity.this, "Sem conexão com a internet!", Toast.LENGTH_LONG).show();
                        }
                    }else {

                        Toast.makeText(CadastroEstabelecimentoActivity.this, "As senhas devem ser iguais!", Toast.LENGTH_LONG).show();
                    }
                }

                break;
            }

            case R.id.btnVoltar:{

                startActivity(new Intent(CadastroEstabelecimentoActivity.this, InicioActivity.class));
                break;
            }

            case R.id.linkLogin:{

                Util.irParaLogin(CadastroEstabelecimentoActivity.this);
                break;
            }
        }
    }

    private void verificaUsuario() {

        autenticacao.createUserWithEmailAndPassword(usuario.getEmail(), usuario.getSenha()).addOnCompleteListener(CadastroEstabelecimentoActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){

                    insereUsuario(usuario);
                }else {

                    String erro = task.getException().toString();
                    Util.verificaErro(erro, CadastroEstabelecimentoActivity.this);
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
            Toast.makeText(CadastroEstabelecimentoActivity.this, "Estabelecimento cadastrado!", Toast.LENGTH_LONG).show();
            Util.irParaLogin(CadastroEstabelecimentoActivity.this);
            return true;
        }catch (Exception erro){

            Toast.makeText(CadastroEstabelecimentoActivity.this, "Erro ao inserir estabelecimento!", Toast.LENGTH_LONG).show();
            erro.printStackTrace();
            Util.irParaLogin(CadastroEstabelecimentoActivity.this);
            return false;
        }
    }
}
