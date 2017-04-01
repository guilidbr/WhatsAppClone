package android.guilhermedambros.whatsappclone.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.guilhermedambros.whatsappclone.helper.Permissao;
import android.guilhermedambros.whatsappclone.helper.Preferencias;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.guilhermedambros.whatsappclone.R;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.SEND_SMS,
            Manifest.permission.INTERNET
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Permissao.validaPermissoes(1, this, permissoesNecessarias);

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
                //telefoneSemFormatacao = "5554";
                boolean smsEnviado = enviaSMS("+"+telefoneSemFormatacao, msgEnvio);
                if (smsEnviado){
                    Intent intent = new Intent(LoginActivity.this, ValidadorActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(LoginActivity.this, "Problema ao enviar o SMS! Tente novamente!", Toast.LENGTH_LONG).show();
                }


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


    //recebe o retorno da posição do usuário perante as permissões solicitadas pelo APP
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int resultado:grantResults){//percorre os resultados das permissões
            if (resultado == PackageManager.PERMISSION_DENIED){//quando uma é negada
                alertaValidacaoPermissao();

            }


        }

    }

    //Alerta sobre a necessidade da permissão e finaliza a Activity
    private void alertaValidacaoPermissao(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões negadas!");
        builder.setMessage("Para utilizar esse APP é necessário aceitar as permissões.");

        builder.setPositiveButton("CONFIRMAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
