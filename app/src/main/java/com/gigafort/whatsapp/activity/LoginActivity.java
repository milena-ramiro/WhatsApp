package com.gigafort.whatsapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gigafort.whatsapp.R;
import com.gigafort.whatsapp.config.ConfiguracaoFirebase;
import com.gigafort.whatsapp.helper.Base64Custom;
import com.gigafort.whatsapp.helper.Preferencias;
import com.gigafort.whatsapp.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private Button btnLogar;
    private EditText senha;
    private EditText email;
    private Usuario usuario;
    private FirebaseAuth autenticacao;
    private String identificadorUsuarioLogado;

    private ValueEventListener valueEventListenerUsuario;
    private DatabaseReference firebase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        VerificarUsuarioLogado();

        btnLogar = findViewById(R.id.btnLogar);
        senha = findViewById(R.id.editSenha);
        email = findViewById(R.id.editEmail);

        btnLogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usuario = new Usuario();
                usuario.setEmail(email.getText().toString());
                usuario.setSenha(senha.getText().toString());
                ValidarLogin();
            }
        });

    }

    private void VerificarUsuarioLogado(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        if(autenticacao.getCurrentUser() != null){
            AbrirTelaPrincipal();
        }
    }

    private void ValidarLogin(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.signInWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>(){

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    identificadorUsuarioLogado = Base64Custom.CodificarBase64(usuario.getEmail());

                    //recuperar usuario
                    firebase = ConfiguracaoFirebase.getFirebase()
                            .child("usuarios")
                            .child(identificadorUsuarioLogado);

                    valueEventListenerUsuario = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            Usuario usuarioRecuperado = dataSnapshot.getValue(Usuario.class);
                            usuarioRecuperado.getNome();

                            Preferencias preferencias = new Preferencias(LoginActivity.this);
                            preferencias.SalvarDados(identificadorUsuarioLogado, "");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    };

                    firebase.addListenerForSingleValueEvent(valueEventListenerUsuario);



                    AbrirTelaPrincipal();
                    Toast.makeText(LoginActivity.this, "Sucesso ao fazer Login", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(LoginActivity.this, "Erro ao fazer Login", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void AbrirTelaPrincipal(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void AbrirCadastroUsuario(View view){
        Intent intent = new Intent(LoginActivity.this, CadastroUsuarioActivity.class);
        startActivity(intent);
    }

}
