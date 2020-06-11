package com.gigafort.whatsapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.gigafort.whatsapp.R;
import com.gigafort.whatsapp.config.ConfiguracaoFirebase;
import com.gigafort.whatsapp.helper.Base64Custom;
import com.gigafort.whatsapp.helper.Preferencias;
import com.gigafort.whatsapp.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CadastroUsuarioActivity extends AppCompatActivity {

    private EditText nome;
    private EditText email;
    private EditText senha;
    private Button btnCadastrar;
    private Usuario usuario;

    private FirebaseAuth autenticacao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_usuario);

        nome = findViewById(R.id.editCadNome);
        email = findViewById(R.id.editCadEmail);
        senha = findViewById(R.id.editCadSenha);
        btnCadastrar = findViewById(R.id.btnCadastrar);

        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usuario = new Usuario();
                usuario.setNome(nome.getText().toString());
                usuario.setEmail(email.getText().toString());
                usuario.setSenha(senha.getText().toString());
                CadastrarUsuario();
            }
        });


    }

    private void CadastrarUsuario(){

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(CadastroUsuarioActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(CadastroUsuarioActivity.this,"Sucesso ao cadastrar usuário" ,Toast.LENGTH_LONG).show();
                    //FirebaseUser usuarioFirebase = task.getResult().getUser();

                    String identificadorUsuario = Base64Custom.CodificarBase64(usuario.getEmail());
                    usuario.setId(identificadorUsuario);
                    usuario.Salvar();

                    Preferencias preferencias = new Preferencias(CadastroUsuarioActivity.this);
                    preferencias.SalvarDados(identificadorUsuario, usuario.getNome());

                    AbrirLoginUsuario();
                }else{

                    String erroExcecao = "";

                    try{
                        throw task.getException();
                    }
                    catch (FirebaseAuthWeakPasswordException e){
                        erroExcecao = "Digite uma senha mais forte!";
                    }
                    catch (FirebaseAuthInvalidCredentialsException e) {
                        erroExcecao = "E-mail inválido!";
                    }
                    catch (FirebaseAuthUserCollisionException e) {
                        erroExcecao = "Usuário já cadastrado!";
                    }
                    catch (Exception e) {
                        erroExcecao = "Erro ao cadastrar usuário!";
                        e.printStackTrace();
                    }

                    Toast.makeText(CadastroUsuarioActivity.this,"Erro: " + erroExcecao ,Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void AbrirLoginUsuario(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
