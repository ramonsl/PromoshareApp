package com.example.gusta.promoshareapp.Activities;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gusta.promoshareapp.Adapters.PromocoesAdapter;
import com.example.gusta.promoshareapp.Adapters.PromocoesEstabelecimentoAdapter;
import com.example.gusta.promoshareapp.Classes.Promocao;
import com.example.gusta.promoshareapp.Classes.Usuario;
import com.example.gusta.promoshareapp.Config.ConfigFirebase;
import com.example.gusta.promoshareapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EstabelecimentoActivity extends AppCompatActivity {

    private DatabaseReference referencia;
    private TextView txtNomeEstab;
    private TextView txtEnderecoEstab;
    private String emailEstab;
    private CardView btnComoChegar;
    private RecyclerView recyclerViewPromocoes;
    private PromocoesAdapter adapterPromo;
    private List<Promocao> promocoes;
    private Promocao todasPromocoes;
    private LinearLayoutManager layoutManagerTodasPromocoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estabelecimento);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        referencia = ConfigFirebase.pegaReferenciaBancoDados();

        recyclerViewPromocoes = (RecyclerView) findViewById(R.id.recycleViewPromocoes);
        Intent intent = getIntent();

        emailEstab = intent.getStringExtra("estabelecimento");

        txtNomeEstab = (TextView) findViewById(R.id.txtNomeEstab);
        txtEnderecoEstab = (TextView) findViewById(R.id.txtEnderecoEstab);
        btnComoChegar = (CardView) findViewById(R.id.btnComoChegar);

        listaDadosEstab();
        carregaEstabPromocoes();

        abreGoogleMaps();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(this, PrincipalActivity.class));
                finishAffinity();
                break;
            default:break;
        }
        return true;
    }

    private void listaDadosEstab() {

        referencia.child("usuarios").orderByChild("email").equalTo(emailEstab).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){

                    Usuario usuario = postSnapshot.getValue(Usuario.class);

                    txtNomeEstab.setText(usuario.getNome());
                    txtEnderecoEstab.setText(usuario.getRua()
                            + ", " + usuario.getNumero()
                            + ", " + usuario.getBairro()
                            + ", " + usuario.getCidade()
                            + " - " + usuario.getEstado()
                            + " - " + usuario.getPais());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void abreGoogleMaps() {

        btnComoChegar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String strUri = "http://maps.google.com/maps?q=" + txtEnderecoEstab.getText().toString();
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse(strUri));

                intent.setClassName("com.google.android.apps.maps",
                        "com.google.android.maps.MapsActivity");

                startActivity(intent);
            }
        });
    }

    private void carregaEstabPromocoes() {

        recyclerViewPromocoes.setHasFixedSize(true);
        layoutManagerTodasPromocoes = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewPromocoes.setLayoutManager(layoutManagerTodasPromocoes);
        promocoes = new ArrayList<>();

        referencia.child("promocoes").orderByChild("estabelecimento").equalTo(emailEstab).addValueEventListener(new ValueEventListener() {
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
}
