package android.guilhermedambros.whatsappclone.activity;

import android.guilhermedambros.whatsappclone.helper.Preferencias;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.guilhermedambros.whatsappclone.R;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.github.rtoshiro.util.format.text.SimpleMaskTextWatcher;

import java.util.HashMap;
import java.util.Random;

public class LoginActivity extends AppCompatActivity {

    private EditText telefone;
    private EditText ddd;
    private EditText codigo;
    private EditText nome;
    private Button cadastrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        telefone = (EditText) findViewById(R.id.edit_telefone);
        ddd = (EditText) findViewById(R.id.edit_ddd);
        codigo = (EditText) findViewById(R.id.edit_codigo);
        nome = (EditText) findViewById(R.id.edit_nome);
        cadastrar = (Button) findViewById(R.id.botao_cadastrar);

        //mascara telefone
        SimpleMaskFormatter simpleMaskTelefone = new SimpleMaskFormatter("NNNNN-NNNN");
        MaskTextWatcher maskTelefone = new MaskTextWatcher(telefone, simpleMaskTelefone);
        telefone.addTextChangedListener(maskTelefone);

        //mascara ddd
        SimpleMaskFormatter simpleMaskDdd = new SimpleMaskFormatter("NN");
        MaskTextWatcher maskDdd = new SimpleMaskTextWatcher(ddd, simpleMaskDdd);
        ddd.addTextChangedListener(maskDdd);

        //mascara codigo
        SimpleMaskFormatter simpleMaskCodigo = new SimpleMaskFormatter("+NN");
        MaskTextWatcher maskCodigo = new SimpleMaskTextWatcher(codigo, simpleMaskCodigo);
        codigo.addTextChangedListener(maskCodigo);

        cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nomeUsuario = nome.getText().toString();
                String telefoneCompleto =
                        codigo.getText().toString() +
                        ddd.getText().toString() +
                        telefone.getText().toString();

                String telefoneSemFormatacao = telefoneCompleto.replace("+", "");
                telefoneSemFormatacao = telefoneSemFormatacao.replace("-", "");

                //Gerar Token
                Random randomico = new Random();
                int numeroRandomico = randomico.nextInt(9999 - 1000) + 1000;//numeros de 1000 a 9999
                String token = String.valueOf(numeroRandomico);
                String msgEnvio = "Código de confirmação: ";
                //Salvar dados para validação
                Preferencias preferencias = new Preferencias(LoginActivity.this);
                preferencias.salvarUsuarioPreferencias(nomeUsuario, telefoneCompleto, token);

                //Envio do SMS
                boolean smsEnviado = enviaSMS("+"+telefoneSemFormatacao, msgEnvio);


                //HashMap<String, String> usuario = preferencias.getDadosUsuario();

                //Log.i("TOKEN", "NOME:" + usuario.get("nome") + " FONE: "+ usuario.get("telefone"));

            }
        });
    }

    //Envio de SMS
    private boolean enviaSMS(String telefone, String msg){
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(telefone, null, msg, null, null);

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
