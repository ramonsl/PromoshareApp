package com.example.gusta.promoshareapp.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.gusta.promoshareapp.Adapters.PromocoesAdapter;
import com.example.gusta.promoshareapp.Adapters.PromocoesEstabelecimentoAdapter;
import com.example.gusta.promoshareapp.Classes.Promocao;
import com.example.gusta.promoshareapp.Config.ConfigFirebase;
import com.example.gusta.promoshareapp.R;
import com.example.gusta.promoshareapp.Util;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PrincipalActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private DatabaseReference referencia;
    private Menu menuSuperior;
    private String tipoUsuario;
    private GoogleSignInClient googleSignInClient;
    private RecyclerView recyclerViewPromocoes;
    private PromocoesAdapter adapterPromo;
    private PromocoesEstabelecimentoAdapter adapterPromoEstab;
    private List<Promocao> promocoes;
    private Promocao todasPromocoes;
    private LinearLayoutManager layoutManagerTodasPromocoes;
    private String emailLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        autenticacao = ConfigFirebase.pegaAutenticacao();
        referencia = ConfigFirebase.pegaReferenciaBancoDados();

        emailLogado = autenticacao.getCurrentUser().getEmail().toString();

        recyclerViewPromocoes = (RecyclerView) findViewById(R.id.recycleViewPromocoes);

        servicosGoogle();

        verificaTipoUsuario();
    }

    //verifica tipo de usuário para listar todas as promoções ou apenas as promoções do estabelecimento logado
    private void verificaTipoUsuario() {

        referencia.child("usuarios").orderByChild("email").equalTo(emailLogado.toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){

                    tipoUsuario = postSnapshot.child("tipoUsuario").getValue().toString();

                    if (tipoUsuario.equals("Estabelecimento")){

                        carregaEstabPromocoes();
                    }else if(tipoUsuario.equals("Usuario") || tipoUsuario.isEmpty()){

                        carregaTodosPromocoes();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    //lista todas as promoções
    private void carregaEstabPromocoes(){

        recyclerViewPromocoes.setHasFixedSize(true);
        layoutManagerTodasPromocoes = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewPromocoes.setLayoutManager(layoutManagerTodasPromocoes);
        promocoes = new ArrayList<>();

        referencia.child("promocoes").orderByChild("estabelecimento").equalTo(emailLogado).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){

                    todasPromocoes = postSnapshot.getValue(Promocao.class);

                    promocoes.add(todasPromocoes);
                }
                adapterPromoEstab.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        adapterPromoEstab = new PromocoesEstabelecimentoAdapter(promocoes, this);
        recyclerViewPromocoes.setAdapter(adapterPromoEstab);
    }

    //lista apenas promoções do estabelecimento logado com opções de editar ou excluir
    private void carregaTodosPromocoes() {

        recyclerViewPromocoes.setHasFixedSize(true);
        layoutManagerTodasPromocoes = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewPromocoes.setLayoutManager(layoutManagerTodasPromocoes);
        promocoes = new ArrayList<>();

        referencia.child("promocoes").orderByChild("nomeProd").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){

                    todasPromocoes = postSnapshot.getValue(Promocao.class);

                    promocoes.add(todasPromocoes);
                }
                adapterPromo.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        adapterPromo = new PromocoesAdapter(promocoes, this);
        recyclerViewPromocoes.setAdapter(adapterPromo);
    }


    //verifica tipo de usuário para a definição do menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        menu.clear();

        this.menuSuperior = menu;

        referencia.child("usuarios").orderByChild("email").equalTo(emailLogado).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){

                    tipoUsuario = postSnapshot.child("tipoUsuario").getValue().toString();

                    menuSuperior.clear();

                    if (tipoUsuario.equals("Estabelecimento")){

                        getMenuInflater().inflate(R.menu.menu_superior_estab, menuSuperior);
                    }else if(tipoUsuario.equals("Usuario")){

                        getMenuInflater().inflate(R.menu.menu_superior_usu, menuSuperior);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return true;
    }

    //define as funções dos itens do menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.ActionCadPromocao){

            Util.irParaCadPromocoes(PrincipalActivity.this);
        }

        if (id == R.id.ActionSair){

            deslogarUsuario();
        }
        return super.onOptionsItemSelected(item);
    }


    //inicia serviços do Google
    private void servicosGoogle() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(PrincipalActivity.this, gso);
    }

    //realiza o logoff
    private void deslogarUsuario() {

        autenticacao.signOut();
        googleSignInClient.signOut();
        startActivity(new Intent(PrincipalActivity.this, InicioActivity.class));
        finish();
    }

    private void refresh() {

        startActivity(new Intent(PrincipalActivity.this, PrincipalActivity.class));
    }
}
