package com.gigafort.whatsapp.helper;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class Permissao {

    public static boolean ValidaPermissoes(int requestCode, Activity activity, String[] permissoes){

        List<String> listaPermissoes = new ArrayList<String>();

        if(Build.VERSION.SDK_INT > 22){

            for (String permissao : permissoes){
                boolean validaPermissao = ContextCompat.checkSelfPermission(activity, permissao) == PackageManager.PERMISSION_GRANTED;

                if(!validaPermissao){
                    listaPermissoes.add(permissao);
                }
            }

            if(listaPermissoes.isEmpty()){
                return true;
            }

            String[] novasPermissoes = new String[listaPermissoes.size()];
            listaPermissoes.toArray(novasPermissoes);

            //SOLICITAR PERMISSÃO
            ActivityCompat.requestPermissions(activity, novasPermissoes, requestCode);

        }


        return true;
    }


}
