package android.guilhermedambros.whatsappclone.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.guilhermedambros.whatsappclone.Model.Usuario;
import android.guilhermedambros.whatsappclone.config.ConfiguracaoFirebase;
import android.guilhermedambros.whatsappclone.helper.Base64Custom;
import android.guilhermedambros.whatsappclone.helper.Permissao;
import android.guilhermedambros.whatsappclone.helper.Preferencias;
import android.os.StrictMode;
import android.support.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Random;

public class LoginActivity extends AppCompatActivity {

    private DatabaseReference referenciaFirebase;
    private EditText email;
    private EditText senha;
    private Button logar;
    private Usuario usuario;
    private FirebaseAuth autenticacao;
    private String identificadorUsuarioLogado;

    private DatabaseReference firebase;
    private ValueEventListener eventListenerUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = (EditText) findViewById(R.id.edit_login_email);
        senha = (EditText) findViewById(R.id.edit_login_senha);
        logar = (Button) findViewById(R.id.btn_logar);

        verificarUsuarioLogado();
        logar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((email.getText().toString().equals("")) || (senha.getText().toString().equals(""))){
                    Toast.makeText(LoginActivity.this, "Preencha todos os campos!", Toast.LENGTH_LONG).show();
                }else {
                    usuario = new Usuario();
                    usuario.setEmail(email.getText().toString());
                    usuario.setSenha(senha.getText().toString());
                    validarLogin();
                }
            }
        });

    }

    private void verificarUsuarioLogado(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        if(autenticacao.getCurrentUser() != null){
            abrirTelaPrincipal();
        }
    }

    private void validarLogin(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.signInWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){


                    identificadorUsuarioLogado = Base64Custom.codificarBase64(usuario.getEmail());

                    firebase = ConfiguracaoFirebase.getFirebase()
                            .child("usuarios")
                            .child(identificadorUsuarioLogado);

                    eventListenerUsuario = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Usuario usuarioRecuperado = dataSnapshot.getValue(Usuario.class);

                            Preferencias preferencias =  new Preferencias(LoginActivity.this);
                            preferencias.salvarUsuarioPreferencias(identificadorUsuarioLogado, usuarioRecuperado.getNome());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };
                    firebase.addListenerForSingleValueEvent(eventListenerUsuario);



                    abrirTelaPrincipal();
                    Toast.makeText(LoginActivity.this, "Sucesso ao logar.", Toast.LENGTH_LONG).show();

                }else{
                    String erro = "";
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthInvalidCredentialsException e ){
                        erro = "E-mail ou senha incorretos. Tente novamente!";
                    }catch (FirebaseAuthInvalidUserException e ){
                        erro = "E-mail ou senha incorretos. Tente novamente!";
                    }catch (Exception e){
                        erro = "Erro ao logar. Tente novamente!";
                        e.printStackTrace();
                    }
                    Toast.makeText(LoginActivity.this, "Erro: "+erro, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void abrirTelaPrincipal(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void abrirCadastroUsuario(View view) {
    Intent intent = new Intent(LoginActivity.this, CadastroUsuarioActivity.class);
        startActivity(intent);
    }

}
