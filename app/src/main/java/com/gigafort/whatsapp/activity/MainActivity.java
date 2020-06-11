package com.gigafort.whatsapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.gigafort.whatsapp.R;
import com.gigafort.whatsapp.adapter.TabAdapter;
import com.gigafort.whatsapp.config.ConfiguracaoFirebase;
import com.gigafort.whatsapp.helper.Base64Custom;
import com.gigafort.whatsapp.helper.Preferencias;
import com.gigafort.whatsapp.helper.SlidingTabLayout;
import com.gigafort.whatsapp.model.Contato;
import com.gigafort.whatsapp.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference firebase;
    private Button btnSair;
    private FirebaseAuth autenticacao;

    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;

    private String identContato;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_principal);
        toolbar.setTitle("WhatsApp");
        setSupportActionBar(toolbar);

        slidingTabLayout = findViewById(R.id.stl_tabs);
        viewPager = findViewById(R.id.vp_page);

        //Configurar sliding tabs
        slidingTabLayout.setDistributeEvenly(true);//distribuir na tela igualmente
        slidingTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(this, R.color.colorAccent));

        //Configurar adapter
        TabAdapter tabAdapter = new TabAdapter(getSupportFragmentManager()); //recuperar os fragmentos
        viewPager.setAdapter(tabAdapter);

        slidingTabLayout.setViewPager(viewPager);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.item_sair:
                DeslogarUsuario();
                return true;
            case R.id.item_configuracoes:
                return true;
            case R.id.item_adicionar:
                AbrirCadastroContato();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void AbrirCadastroContato(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Novo Contato");
        alert.setMessage("Email do usuário");
        alert.setCancelable(false);

        final EditText editText = new EditText(this);
        alert.setView(editText);

        alert.setPositiveButton("Cadastrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String emailContato = editText.getText().toString();
                if(emailContato.isEmpty()){
                    Toast.makeText(MainActivity.this, "Escreva o e-mail", Toast.LENGTH_SHORT).show();
                }
                else{
                    identContato = Base64Custom.CodificarBase64(emailContato);

                    firebase = ConfiguracaoFirebase.getFirebase().child("usuarios").child(identContato);
                    //consultar os dados uma unica vez e não ser notificado no firebase
                    firebase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getValue() != null){
                                //Recuperar dados do contato a ser adicionado
                                Usuario usuarioContato = dataSnapshot.getValue(Usuario.class);



                                //Referencias identificador usuario logado {BASE 64}
                                Preferencias preferencias = new Preferencias(MainActivity.this);
                                String identificadorUsuarioLogado = preferencias.getIdentificador();


                                firebase = ConfiguracaoFirebase.getFirebase();
                                firebase = firebase.child("contatos")
                                                    .child(identificadorUsuarioLogado)
                                                    .child(identContato);

                                Contato contato = new Contato();
                                contato.setIdentificadorUsuario(identContato);
                                contato.setEmail(usuarioContato.getEmail());
                                contato.setNome(usuarioContato.getNome());

                                firebase.setValue(contato);

                            }else{
                                Toast.makeText(MainActivity.this, "Usuario não possui cadastro", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }
        });

        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alert.create();
        alert.show();
    }

    private void DeslogarUsuario(){
        autenticacao.signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
