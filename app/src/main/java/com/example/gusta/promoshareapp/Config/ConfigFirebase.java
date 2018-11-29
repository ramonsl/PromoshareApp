package com.example.gusta.promoshareapp.Config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ConfigFirebase {

    private static DatabaseReference referenciaBancoDados;
    private static FirebaseAuth autenticacao;
    private static FirebaseStorage storage;
    private static StorageReference referenciaStorage;

    public static DatabaseReference pegaReferenciaBancoDados(){

        if (referenciaBancoDados == null){

            referenciaBancoDados = FirebaseDatabase.getInstance().getReference();
        }
        return referenciaBancoDados;
    }

    public static FirebaseAuth pegaAutenticacao(){

        if (autenticacao == null){

            autenticacao = FirebaseAuth.getInstance();
        }
        return autenticacao;
    }

    public static FirebaseStorage pegaStorage(){

        if (storage == null){

            storage = FirebaseStorage.getInstance();
        }
        return storage;
    }

    public static StorageReference pegaReferenciaStorage(){

        if (referenciaStorage == null){

            referenciaStorage = FirebaseStorage.getInstance().getReference();
        }
        return referenciaStorage;
    }
}
