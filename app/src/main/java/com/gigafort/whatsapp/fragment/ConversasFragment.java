package com.gigafort.whatsapp.fragment;


import android.content.Intent;
import android.os.Bundle;



import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.gigafort.whatsapp.R;
import com.gigafort.whatsapp.activity.ConversaActivity;
import com.gigafort.whatsapp.adapter.ConversaAdapter;
import com.gigafort.whatsapp.config.ConfiguracaoFirebase;
import com.gigafort.whatsapp.helper.Base64Custom;
import com.gigafort.whatsapp.helper.Preferencias;
import com.gigafort.whatsapp.model.Contato;
import com.gigafort.whatsapp.model.Conversa;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConversasFragment extends Fragment {

    private ListView listview;
    private ArrayAdapter<Conversa> adapter;
    private ArrayList<Conversa> conversas;

    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;


    public ConversasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversas, container, false);

        conversas = new ArrayList<>();
        listview = view.findViewById(R.id.listaConversas);
        adapter = new ConversaAdapter(getActivity(), conversas);
        listview.setAdapter(adapter);

        //recuperar dados do usuario
        Preferencias preferencias = new Preferencias(getActivity());
        String idUsuarioLogado = preferencias.getIdentificador();

        //recuperar conversas do firebase
        databaseReference = ConfiguracaoFirebase.getFirebase()
                                                .child("conversas")
                                                .child(idUsuarioLogado);
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                conversas.clear();
                for(DataSnapshot dados : dataSnapshot.getChildren()){
                    Conversa conversa = dados.getValue(Conversa.class);
                    conversas.add(conversa);
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        //Clicar na conversa e abri-la
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ConversaActivity.class);

                Conversa conversa = conversas.get(position);

                intent.putExtra("nome", conversa.getNome());
                String email = Base64Custom.DecodificarBase64(conversa.getIdUsuario());
                intent.putExtra("email", email);

                startActivity(intent);

            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        databaseReference.addValueEventListener(valueEventListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        databaseReference.removeEventListener(valueEventListener);
    }
}
