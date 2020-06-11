package com.gigafort.whatsapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.gigafort.whatsapp.R;
import com.gigafort.whatsapp.adapter.MensagemAdapter;
import com.gigafort.whatsapp.config.ConfiguracaoFirebase;
import com.gigafort.whatsapp.helper.Base64Custom;
import com.gigafort.whatsapp.helper.Preferencias;
import com.gigafort.whatsapp.model.Conversa;
import com.gigafort.whatsapp.model.Mensagem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.EventListener;

public class ConversaActivity extends AppCompatActivity {

    private EditText editMensagem;
    private ImageButton btMensagem;
    private DatabaseReference database;
    private ListView listView;
    private ArrayList<Mensagem> mensagens;
    private ArrayAdapter<Mensagem> adapter;
    private ValueEventListener valueEventListenerMensagem;

    // dados do destinatário
    private String nomeUsuarioDestinatario;
    private String idUsuarioDestinatario;

    // dados do remetente
    private String idUsuarioRemetente;
    private String nomeUsuarioRemetente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversa);

        Toolbar toolbar = findViewById(R.id.tbConversa);

        editMensagem = findViewById(R.id.editMsg);
        btMensagem = findViewById(R.id.btEnviar);
        listView = findViewById(R.id.lv_conversas);

        //dados do usuario logado
        Preferencias preferencias = new Preferencias(ConversaActivity.this);
        idUsuarioRemetente = preferencias.getIdentificador();
        nomeUsuarioRemetente = preferencias.getNome();

        Bundle extra = getIntent().getExtras();

        if(extra != null){
            nomeUsuarioDestinatario = extra.getString("nome");
            String emailDestinatario = extra.getString("email");
            idUsuarioDestinatario = Base64Custom.CodificarBase64(emailDestinatario);
        }

        toolbar.setTitle(nomeUsuarioDestinatario);
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        setSupportActionBar(toolbar);

        //Montar listview e adapter
        mensagens = new ArrayList<>();

        adapter = new MensagemAdapter(ConversaActivity.this, mensagens);

        listView.setAdapter(adapter);

        //Recuperar as mensagens do Firebase
        database = ConfiguracaoFirebase.getFirebase()
                        .child("mensagens")
                        .child(idUsuarioRemetente)
                        .child(idUsuarioDestinatario);

        //Criar listener para mensagens
        valueEventListenerMensagem = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mensagens.clear();

                for(DataSnapshot dados : dataSnapshot.getChildren()){
                    Mensagem mensagem = dados.getValue(Mensagem.class);
                    mensagens.add(mensagem);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        database.addValueEventListener(valueEventListenerMensagem);

        //enviar mensagem
        btMensagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textoMensagem = editMensagem.getText().toString();

                if(textoMensagem.isEmpty()){
                    Toast.makeText(ConversaActivity.this, "Digite uma mensagem para enviar", Toast.LENGTH_LONG).show();
                }
                else{

                    Mensagem mensagem = new Mensagem();
                    mensagem.setIdUsuario(idUsuarioRemetente);
                    mensagem.setMensagem(textoMensagem);

                    //salvamos mensagem para o remetente
                    Boolean retornoMensagemRemetente = SalvarMensagem(idUsuarioRemetente, idUsuarioDestinatario, mensagem);

                    if(!retornoMensagemRemetente){
                        Toast.makeText(ConversaActivity.this, "Problema ao salvar mensagem, tente novamente!", Toast.LENGTH_LONG).show();
                    }
                    else{
                        //salvamos mensagem para o destinatario
                        Boolean retornoMensagemDestinatario = SalvarMensagem(idUsuarioDestinatario, idUsuarioRemetente, mensagem);

                        if(!retornoMensagemDestinatario){
                            Toast.makeText(ConversaActivity.this, "Problema ao enviar mensagem para o destinatário, tente novamente!", Toast.LENGTH_LONG).show();
                        }
                    }

                    //Salvar conversa para remetente
                    Conversa conversa = new Conversa();
                    conversa.setIdUsuario(idUsuarioDestinatario);
                    conversa.setNome(nomeUsuarioDestinatario);
                    conversa.setMensagem(textoMensagem);

                    Boolean retornoConversaRemetente = SalvarConversa(idUsuarioRemetente, idUsuarioDestinatario, conversa);
                    if(!retornoConversaRemetente){
                        Toast.makeText(ConversaActivity.this, "Problema ao salvar conversa, tente novamente!", Toast.LENGTH_LONG).show();
                    }
                    else{
                        //Salvar conversa para destinatario
                        Conversa conversaDestinatario = new Conversa();
                        conversaDestinatario.setIdUsuario(idUsuarioRemetente);
                        conversaDestinatario.setNome(nomeUsuarioRemetente);
                        conversa.setMensagem(textoMensagem);
                        Boolean retornoConversaDestinatario = SalvarConversa(idUsuarioDestinatario, idUsuarioRemetente, conversaDestinatario);

                        if(!retornoConversaDestinatario){
                            Toast.makeText(ConversaActivity.this, "Problema ao salvar conversa para o destinatário, tente novamente!", Toast.LENGTH_LONG).show();
                        }
                    }

                    editMensagem.setText("");
                }
            }
        });
    }

    private boolean SalvarMensagem(String idRemetente, String idDestinatario, Mensagem mensagem){
        try{

            database = ConfiguracaoFirebase.getFirebase().child("mensagens");
            database.child(idRemetente)
                    .child(idDestinatario)
                    .push()
                    .setValue(mensagem);


            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private Boolean SalvarConversa(String idRemetente, String idDestinatario, Conversa conversa){
        try{

            database = ConfiguracaoFirebase.getFirebase().child("conversas");

            database.child(idRemetente)
                    .child(idDestinatario)
                    .setValue(conversa);

            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }



    @Override
    protected void onStop() {
        super.onStop();
        //nao vou precisar desse evento se o usuario não estiver na tela,
        //assim eu economizo recursos
        database.removeEventListener(valueEventListenerMensagem);
    }
}
