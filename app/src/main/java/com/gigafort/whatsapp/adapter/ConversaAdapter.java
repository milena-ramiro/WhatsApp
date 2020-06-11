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
import com.gigafort.whatsapp.model.Conversa;

import java.util.ArrayList;


public class ConversaAdapter extends ArrayAdapter<Conversa> {

    private ArrayList<Conversa> conversas;
    private Context context;


    public ConversaAdapter(@NonNull Context c, @NonNull ArrayList<Conversa> objects) {
        super(c, 0, objects);
        this.conversas = objects;
        this.context = c;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = null;

        //verifica se as conversas nao estao vazias
        if(conversas != null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            view = inflater.inflate(R.layout.lista_padrao, parent, false);

            //recupera elemento para exibição
            TextView nomeContato = view.findViewById(R.id.tv_titulo);
            TextView ultimaMensagem = view.findViewById(R.id.tv_subtitulo);

            Conversa conversa = conversas.get(position);
            nomeContato.setText(conversa.getNome());
            ultimaMensagem.setText(conversa.getMensagem());

        }

        return view;
    }
}
