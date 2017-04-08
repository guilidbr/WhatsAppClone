package android.guilhermedambros.whatsappclone.adapter;

import android.content.Context;
import android.guilhermedambros.whatsappclone.Model.Conversa;
import android.guilhermedambros.whatsappclone.R;
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

public class ConversaAdapter extends ArrayAdapter<Conversa> {
    private ArrayList<Conversa> conversas;
    Context context;

    public ConversaAdapter(Context c, ArrayList<Conversa> objects) {
        super(c, 0, objects);
        this.conversas = objects;
        this.context = c;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = null;

        if (conversas != null) {
//inicializar o objeto para montar a view
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            //monta a view a partir do xml
            view = inflater.inflate(R.layout.listagem_padrao, parent, false);

            //recupera elemento para exibição
            TextView titulo = (TextView) view.findViewById(R.id.tv_titulo);//nome do contato
            TextView subtitulo = (TextView) view.findViewById(R.id.tv_subtitulo);//ultima mensagem

            Conversa conversa = conversas.get(position);
            titulo.setText(conversa.getNome());
            subtitulo.setText(conversa.getMensagem());
        }
        return view;
    }
}
