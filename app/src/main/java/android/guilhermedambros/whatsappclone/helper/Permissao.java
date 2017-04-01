package android.guilhermedambros.whatsappclone.helper;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guili on 01/04/2017.
 */

public class Permissao {

    public static boolean validaPermissoes(int requestCode, Activity activity, String[] permissoes){

        if (Build.VERSION.SDK_INT >= 23){ //validação necessaria para SDK acima da 23
            List<String> listaPermissoes = new ArrayList<String>();

            //percorre as permissoes passadas, verificando uma a uma
            //se ja tem a permissão liberada
            for (String permissao : permissoes){
                boolean validaPermissao = ContextCompat.checkSelfPermission(activity, permissao) == PackageManager.PERMISSION_GRANTED;
                if (!validaPermissao) listaPermissoes.add(permissao);
            }

            //se a lista estiver vazia, não precisa de permissão
            if (listaPermissoes.isEmpty())return true;

            String[] novasPermissoes = new String[listaPermissoes.size()];
            listaPermissoes.toArray(novasPermissoes);
            //solicita permissão
            ActivityCompat.requestPermissions(activity, novasPermissoes, requestCode);



        }

        return true;
    }

}
