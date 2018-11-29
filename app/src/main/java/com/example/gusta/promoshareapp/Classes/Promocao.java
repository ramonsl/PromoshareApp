package com.example.gusta.promoshareapp.Classes;

import android.net.Uri;

public class Promocao {

    private String key;
    private String nomeProd;
    private String descricaoProd;
    private String precoAntigo;
    private String precoPromo;
    private String urlImg;
    private String estabelecimento;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getNomeProd() {
        return nomeProd;
    }

    public void setNomeProd(String nomeProd) {
        this.nomeProd = nomeProd;
    }

    public String getDescricaoProd() {
        return descricaoProd;
    }

    public void setDescricaoProd(String descricaoProd) {
        this.descricaoProd = descricaoProd;
    }

    public String getPrecoAntigo() {
        return precoAntigo;
    }

    public void setPrecoAntigo(String precoAntigo) {
        this.precoAntigo = precoAntigo;
    }

    public String getPrecoPromo() {
        return precoPromo;
    }

    public void setPrecoPromo(String precoPromo) {
        this.precoPromo = precoPromo;
    }

    public String getUrlImg() {
        return urlImg;
    }

    public void setUrlImg(String urlImg) {
        this.urlImg = urlImg;
    }

    public String getEstabelecimento() {
        return estabelecimento;
    }

    public void setEstabelecimento(String estabelecimento) {
        this.estabelecimento = estabelecimento;
    }
}
