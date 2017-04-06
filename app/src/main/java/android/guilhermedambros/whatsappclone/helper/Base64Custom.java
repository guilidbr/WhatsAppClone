package android.guilhermedambros.whatsappclone.helper;

import android.util.Base64;

/**
 * Created by guili on 06/04/2017.
 */

public class Base64Custom {

    public static String codificarBase64(String texto){
        return Base64.encodeToString(texto.getBytes(), Base64.DEFAULT).replaceAll("(\\n|\\r)", "");
    }

    public static String decodificarBase64(String textoCodigicado){
        return new String(Base64.decode(textoCodigicado, Base64.DEFAULT));
    }
}
