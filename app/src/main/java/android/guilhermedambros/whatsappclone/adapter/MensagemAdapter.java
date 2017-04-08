package android.guilhermedambros.whatsappclone.adapter;

import android.content.Context;
import android.guilhermedambros.whatsappclone.Model.Contato;
import android.guilhermedambros.whatsappclone.Model.Mensagem;
import android.guilhermedambros.whatsappclone.R;
import android.guilhermedambros.whatsappclone.helper.Preferencias;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by guili on 07/04/2017.
 */

public class MensagemAdapter extends ArrayAdapter<Mensagem> {

    private ArrayList<Mensagem> mensagens;
    Context context;

    public MensagemAdapter(Context c, ArrayList<Mensagem> objects) {
        super(c, 0, objects);
        this.mensagens = objects;
        this.context = c;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = null;

        if (mensagens != null){
            //recupera dados remetente
            Preferencias preferencias = new Preferencias(context);
            String idUsuarioRemetente = preferencias.getIdentificador();

            //iniicializa objeto para montagem do layout
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            //recupera mensagem
            Mensagem mensagem = mensagens.get(position);

            //monta a view a partir do xml
            if (idUsuarioRemetente.equals( mensagem.getIdUsuario() )){//enviando
                view = inflater.inflate(R.layout.item_mensagem_direita, parent, false);
            }else {//recebendo
                view = inflater.inflate(R.layout.item_mensagem_esquerda, parent, false);
            }
            //recupera elemento para exibição
            TextView textoMensagem = (TextView) view.findViewById(R.id.tv_mensagem);
            textoMensagem.setText(mensagem.getMensagem());


        }

        return view;
    }
}
