package android.guilhermedambros.whatsappclone.activity;

import android.content.Intent;
import android.guilhermedambros.whatsappclone.Model.Usuario;
import android.guilhermedambros.whatsappclone.config.ConfiguracaoFirebase;
import android.guilhermedambros.whatsappclone.helper.Base64Custom;
import android.guilhermedambros.whatsappclone.helper.Preferencias;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.guilhermedambros.whatsappclone.R;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class CadastroUsuarioActivity extends AppCompatActivity {

    private EditText nomeCadastro;
    private EditText senhaCadastro;
    private EditText emailCadastro;
    private Button btnCadastro;
    private Usuario usuario;

    private FirebaseAuth autenticacao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_usuario);

        nomeCadastro = (EditText) findViewById(R.id.edit_cadastrar_nome);
        senhaCadastro = (EditText) findViewById(R.id.edit_cadastrar_senha);
        emailCadastro = (EditText) findViewById(R.id.edit_cadastrar_email);
        btnCadastro = (Button) findViewById(R.id.btn_cadastrar);

        btnCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usuario = new Usuario();
                usuario.setNome(nomeCadastro.getText().toString());
                usuario.setEmail(emailCadastro.getText().toString());
                usuario.setSenha(senhaCadastro.getText().toString());
                cadastrarUsuario();
            }
        });
    }

    private void cadastrarUsuario(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(CadastroUsuarioActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if( task.isSuccessful() ){
                    Toast.makeText(CadastroUsuarioActivity.this, "Sucesso ao cadastrar usuário", Toast.LENGTH_LONG ).show();

                    String identificadorUsuario = Base64Custom.codificarBase64(usuario.getEmail());
                    usuario.setId( identificadorUsuario );
                    usuario.salvar();

                    Preferencias preferencias =  new Preferencias(CadastroUsuarioActivity.this);
                    preferencias.salvarUsuarioPreferencias(identificadorUsuario);

                    abrirLoginUsuario();

                }else {
                    String erroExcessao = "";
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e){
                        erroExcessao = "Digite uma senha mais forte!";
                    }catch (FirebaseAuthInvalidCredentialsException e ){
                        erroExcessao = "E-mail digitado é inválido!";
                    }catch (FirebaseAuthUserCollisionException e){
                        erroExcessao = "Esse e-mail já esta cadastrado!";
                    }catch (Exception e){
                        erroExcessao = "Ao efetuar o cadastro!";
                        e.printStackTrace();
                    }

                    Toast.makeText(CadastroUsuarioActivity.this, "Erro: "+erroExcessao, Toast.LENGTH_LONG ).show();
                }
            }
        });
    }

    public  void abrirLoginUsuario(){
        Intent intent = new Intent(CadastroUsuarioActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
