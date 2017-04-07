package android.guilhermedambros.whatsappclone.fragment;


import android.content.Intent;
import android.guilhermedambros.whatsappclone.Model.Contato;
import android.guilhermedambros.whatsappclone.activity.ConversaActivity;
import android.guilhermedambros.whatsappclone.adapter.ContatoAdapter;
import android.guilhermedambros.whatsappclone.config.ConfiguracaoFirebase;
import android.guilhermedambros.whatsappclone.helper.Preferencias;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.guilhermedambros.whatsappclone.R;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContatosFragment extends Fragment {

    private ListView listView;
    private ArrayAdapter adapter;
    private ArrayList<Contato> contatos;
    private DatabaseReference firebase;
    private ValueEventListener valueEventListenerContatos;

    public ContatosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        firebase.addValueEventListener(valueEventListenerContatos);
    }

    @Override
    public void onStop() {
        super.onStop();
        firebase.removeEventListener(valueEventListenerContatos);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        contatos = new ArrayList<>();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contatos, container, false);

        //monta listView e adapter
        listView = (ListView) view.findViewById(R.id.lv_contatos);
        /*adapter = new ArrayAdapter(
                getActivity(),
                R.layout.lista_contato,
                contatos
                );*/

        adapter = new ContatoAdapter(getActivity(), contatos);
        listView.setAdapter(adapter);
        Preferencias preferencias = new Preferencias(getActivity());
        firebase = ConfiguracaoFirebase.getFirebase()
                .child("contatos")
                .child(preferencias.getIdentificador());


        valueEventListenerContatos = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //limpar lista
                contatos.clear();

                //listar contatos
                for (DataSnapshot dados: dataSnapshot.getChildren()){
                    Contato contato = dados.getValue(Contato.class);
                    contatos.add(contato);
                }
                adapter.notifyDataSetChanged();//avisa o adapter que os dados mudaram
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ConversaActivity.class);
                //recupera dados a serem passados
                Contato contato = contatos.get(position);

                //envia dados para conversa
                intent.putExtra("nome", contato.getNome());
                intent.putExtra("email", contato.getEmail());
                startActivity(intent);
            }
        });

        return view;
    }

}
