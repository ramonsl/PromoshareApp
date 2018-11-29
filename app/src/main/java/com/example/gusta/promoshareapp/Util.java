package com.example.gusta.promoshareapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.example.gusta.promoshareapp.Activities.CadastroEstabelecimentoActivity;
import com.example.gusta.promoshareapp.Activities.CadastroPromocoes;
import com.example.gusta.promoshareapp.Activities.CadastroUsuarioActivity;
import com.example.gusta.promoshareapp.Activities.InicioActivity;
import com.example.gusta.promoshareapp.Activities.LoginActivity;
import com.example.gusta.promoshareapp.Activities.PerfilEstabelecimentoActivity;
import com.example.gusta.promoshareapp.Activities.PerfilUsuarioActivity;
import com.example.gusta.promoshareapp.Activities.PrincipalActivity;

public class Util {

    //verificações

    public static boolean verificaConexaoInternet(Context contexto){

        ConnectivityManager conexao = (ConnectivityManager) contexto.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = conexao.getActiveNetworkInfo();

        if (info != null && info.isConnected()){

            return true;
        }else {

            return false;
        }
    }

    public static void verificaErro(String erro, Context contexto){

        if (erro.contains("There is no user record corresponding to this identifier")){

            Toast.makeText(contexto, "E-mail não cadastrado!", Toast.LENGTH_LONG).show();
        }else if (erro.contains("The email address is badly formatted")){

            Toast.makeText(contexto, "E-mail inválido!", Toast.LENGTH_LONG).show();
        }else if (erro.contains("The password is invalid")){

            Toast.makeText(contexto, "Senha inválida!", Toast.LENGTH_LONG).show();
        }else if (erro.contains("The email address is already in use")){

            Toast.makeText(contexto, "E-mail já cadastrado!", Toast.LENGTH_LONG).show();
        }else if (erro.contains("The given password is invalid")){

            Toast.makeText(contexto, "A senha deve conter no mínimo 6 caracteres!", Toast.LENGTH_LONG).show();
        }else {

            Toast.makeText(contexto, erro, Toast.LENGTH_LONG).show();
        }
    }

    //redirecionamentos

    public static void irParaLogin(Context contexto){

        contexto.startActivity(new Intent(contexto, LoginActivity.class));
    }

    public static void irParaCadUsu(Context contexto){

        contexto.startActivity(new Intent(contexto, CadastroUsuarioActivity.class));
    }

    public static void idParaCadEstab(Context contexto){

        contexto.startActivity(new Intent(contexto, CadastroEstabelecimentoActivity.class));
    }

    public static void irParaPrincipal(Context contexto){

        contexto.startActivity(new Intent(contexto, PrincipalActivity.class));
    }

    public static void irParaInicio(Context contexto){

        contexto.startActivity(new Intent(contexto, InicioActivity.class));
    }

    public static void irParaPerfilUsu(Context contexto){

        contexto.startActivity(new Intent(contexto, PerfilUsuarioActivity.class));
    }

    public static void irParaPerfilEstab(Context contexto){

        contexto.startActivity(new Intent(contexto, PerfilEstabelecimentoActivity.class));
    }

    public static void irParaCadPromocoes(Context contexto){

        contexto.startActivity(new Intent(contexto, CadastroPromocoes.class));
    }
}
