package android.guilhermedambros.whatsappclone.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.guilhermedambros.whatsappclone.R;
import android.support.v7.widget.Toolbar;

public class ConversaActivity extends AppCompatActivity {

    private Toolbar toolbar;

    //dados destinatario
    private String nomeUsuarioDestinatario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversa);

        toolbar = (Toolbar) findViewById(R.id.tb_conversa);
        Bundle extra = getIntent().getExtras();

        if (extra != null){
            nomeUsuarioDestinatario = extra.getString("nome");
        }

        //config toolbar
        toolbar.setTitle(nomeUsuarioDestinatario);
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        setSupportActionBar(toolbar);

    }
}
