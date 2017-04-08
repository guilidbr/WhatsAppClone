package android.guilhermedambros.whatsappclone.helper;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by guili on 01/04/2017.
 */

public class Preferencias {

    private Context contexto;
    private SharedPreferences preferences;
    private String NOME_ARQUIVO = "WhatsApp.preferencias";
    private int MODE = 0;
    private SharedPreferences.Editor editor;

    private String CHAVE_TELEFONE = "telefone";
    private String CHAVE_TOKEN = "token";

    private final String CHAVE_IDENTIFICADOR = "identificadorUsuarioLogado";
    private final String CHAVE_NOME = "nomeUsuarioLogado";

    public Preferencias(Context contextoParametro){
        contexto = contextoParametro;
        preferences = contexto.getSharedPreferences(NOME_ARQUIVO, MODE);
        editor = preferences.edit();
    }

    public void salvarUsuarioPreferencias(String identificadorUsuario, String nomeUsuario){
        editor.putString(CHAVE_IDENTIFICADOR, identificadorUsuario);
        editor.putString(CHAVE_NOME, nomeUsuario);
        editor.commit();

    }

    public String getIdentificador(){
        return preferences.getString(CHAVE_IDENTIFICADOR, null);
    }

    public String getNome(){
        return preferences.getString(CHAVE_NOME, null);
    }


}
