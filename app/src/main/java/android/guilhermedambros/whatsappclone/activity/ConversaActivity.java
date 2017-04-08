package android.guilhermedambros.whatsappclone.activity;

import android.guilhermedambros.whatsappclone.Model.Contato;
import android.guilhermedambros.whatsappclone.Model.Conversa;
import android.guilhermedambros.whatsappclone.Model.Mensagem;
import android.guilhermedambros.whatsappclone.adapter.MensagemAdapter;
import android.guilhermedambros.whatsappclone.config.ConfiguracaoFirebase;
import android.guilhermedambros.whatsappclone.helper.Base64Custom;
import android.guilhermedambros.whatsappclone.helper.Preferencias;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.guilhermedambros.whatsappclone.R;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ConversaActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText editMensagem;
    private ImageButton btnMensagem;
    private DatabaseReference firebase;
    private ListView listView;
    private ArrayList<Mensagem> mensagens;
    private ArrayAdapter<Mensagem> adapter;
    private ValueEventListener eventListenerMensagem;

    //dados destinatario
    private String nomeUsuarioDestinatario;
    private String idUsuarioDestinatario;

    //dados do remetente
    private String idUsuarioRemetente;
    private String nomeUsuarioRemetente;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversa);

        toolbar = (Toolbar) findViewById(R.id.tb_conversa);
        editMensagem = (EditText) findViewById(R.id.edit_mensagem);
        btnMensagem = (ImageButton) findViewById(R.id.btn_enviar);
        listView = (ListView) findViewById(R.id.lv_conversas);

        //dados do usuario logado
        final Preferencias preferencias = new Preferencias(ConversaActivity.this);
        idUsuarioRemetente = preferencias.getIdentificador();
        nomeUsuarioDestinatario = preferencias.getNome();

        Bundle extra = getIntent().getExtras();

        if (extra != null){
            nomeUsuarioDestinatario = extra.getString("nome");
            String emailDestinatario = extra.getString("email");
            idUsuarioDestinatario = Base64Custom.codificarBase64(emailDestinatario);
        }

        //config toolbar
        toolbar.setTitle(nomeUsuarioDestinatario);
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        setSupportActionBar(toolbar);

        //montar lis view e adapter
        mensagens = new ArrayList<>();
        //adapter = new ArrayAdapter(ConversaActivity.this, android.R.layout.simple_list_item_1, mensagens);
        adapter = new MensagemAdapter(ConversaActivity.this, mensagens);
        listView.setAdapter(adapter);

        firebase = ConfiguracaoFirebase.getFirebase()
                    .child("mensagens")
                    .child(idUsuarioRemetente)
                    .child(idUsuarioDestinatario);

        eventListenerMensagem = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mensagens.clear();
                //recupera mensagens
                for (DataSnapshot dados:dataSnapshot.getChildren()){
                    Mensagem mensagem = dados.getValue(Mensagem.class);
                    mensagens.add(mensagem);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        firebase.addValueEventListener(eventListenerMensagem);

        //enviar msgm
        btnMensagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textoMensagem = editMensagem.getText().toString();

                if (textoMensagem.isEmpty()){
                    Toast.makeText(ConversaActivity.this, "Digite uma mensagem para enviar!", Toast.LENGTH_LONG).show();
                }else{//salva os dados no firebase

                    Mensagem mensagem = new Mensagem();
                    mensagem.setIdUsuario(idUsuarioRemetente);
                    mensagem.setMensagem(textoMensagem);

                    //salando mensagem para o remetente
                    Boolean retornoMensagemRemetente = salvarMensagem(idUsuarioRemetente, idUsuarioDestinatario, mensagem);
                    if (!retornoMensagemRemetente){
                        Toast.makeText(
                                ConversaActivity.this,
                                "Problema ao salvar mensagem, tente novamente!",
                                Toast.LENGTH_LONG
                        ).show();
                    }else {
                        //salvando msgm para o destinatario
                        Boolean retornoMensagemDestinatario = salvarMensagem(idUsuarioDestinatario, idUsuarioRemetente, mensagem);

                        if (!retornoMensagemDestinatario) {
                            Toast.makeText(
                                    ConversaActivity.this,
                                    "Problema ao enviar mensagem para o destinatário, tente novamente!",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }

                    //salvando msgm para o remetente
                    Conversa conversa = new Conversa();
                    conversa.setIdUsuario( idUsuarioDestinatario );
                    conversa.setNome( nomeUsuarioDestinatario );
                    conversa.setMensagem(textoMensagem);
                    Boolean retornoConversaRemetente = salvarConversa(idUsuarioRemetente, idUsuarioDestinatario, conversa);
                    if (!retornoConversaRemetente){
                        Toast.makeText(
                                ConversaActivity.this,
                                "Problema ao salvar mensagem, tente novamente!",
                                Toast.LENGTH_LONG
                        ).show();
                    }else {
                        conversa.setIdUsuario(idUsuarioRemetente);
                        conversa.setNome(nomeUsuarioRemetente);
                        conversa.setMensagem(textoMensagem);
                        //salvando msgm para o destinatario
                        Boolean retornoConversaDestinatario = salvarConversa(idUsuarioDestinatario, idUsuarioRemetente, conversa);
                        if (!retornoConversaDestinatario){
                            Toast.makeText(
                                    ConversaActivity.this,
                                    "Problema ao salvar mensagem para o destinatario, tente novamente!",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }
                    editMensagem.setText("");

                }
            }
        });

    }

    private boolean salvarMensagem(String idRemetente, String idDestinatario, Mensagem mensagem){
        try {
            firebase = ConfiguracaoFirebase.getFirebase();
            firebase.child("mensagens")
                    .child(idRemetente)
                    .child(idDestinatario)
                    .push()
                    .setValue(mensagem);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }

    private boolean salvarConversa(String idRemetente, String idDestinatario, Conversa conversa){
        try {
            firebase = ConfiguracaoFirebase.getFirebase();
            firebase.child("conversas")
                    .child(idRemetente)
                    .child(idDestinatario)
                    .setValue(conversa);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebase.removeEventListener(eventListenerMensagem);
    }
}
