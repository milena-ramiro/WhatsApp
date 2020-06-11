package com.gigafort.whatsapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gigafort.whatsapp.R;
import com.gigafort.whatsapp.helper.Preferencias;
import com.gigafort.whatsapp.model.Mensagem;

import java.util.ArrayList;
import java.util.List;

public class MensagemAdapter extends ArrayAdapter<Mensagem> {

    private Context context;
    private ArrayList<Mensagem> mensagens;

    public MensagemAdapter(@NonNull Context c,  @NonNull ArrayList<Mensagem> objects) {
        super(c, 0, objects);

        this.context = c;
        this.mensagens = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = null;

        //Verifica se tem mensagens
        if(mensagens != null){

            //Recuperar dados do usuario remetente
            Preferencias preferencias = new Preferencias(context);
            String idUsuarioRemetente = preferencias.getIdentificador();

            //Inicializar objeto para montagem do layout
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            //recuperar mensagem
            Mensagem mensagem = mensagens.get(position);

            //Monta view a partir do xml

            if(idUsuarioRemetente.equals(mensagem.getIdUsuario())){
                view = inflater.inflate(R.layout.item_mensagem_direita, parent, false);
            }
            else{
                view = inflater.inflate(R.layout.item_mensagem_esquerda, parent, false);
            }



            //Recupera o elemento para exibicao
            TextView textoMensagem = view.findViewById(R.id.tv_mensagem);
            textoMensagem.setText(mensagem.getMensagem());

        }

        return view;
    }
}
