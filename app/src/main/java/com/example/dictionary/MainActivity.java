package com.example.dictionary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    TextView show;
    String str_data = "", strLine = "";
    SearchView searchView;
    JSONObject jsonObject;
    StringBuilder stringBuilder;
    ArrayList<String> engWord;
    ArrayList<String> bnWord;
    int slotNo;
    int q;
    String currentDynamicKey;
    int num, max;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        show = findViewById(R.id.textshow);

        searchView = findViewById(R.id.search);
        searchView.onActionViewExpanded();

        searchView.setBackgroundColor(Color.TRANSPARENT);
        searchView.setPadding(2, 0, 0, 0);
        searchView.setGravity(Gravity.CENTER_VERTICAL);

        searchView.setOnQueryTextListener(this);
        engWord = new ArrayList<>();
        bnWord = new ArrayList<>();


        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(getAssets().open("dictionary.json")));
            while (strLine != null)
            {
                str_data += strLine;
                strLine = br.readLine();

            }
            //System.out.println(str_data);
            br.close();
        } catch (FileNotFoundException e) {
            System.err.println("File not found");
        } catch (IOException e) {
            System.err.println("Unable to read the file.");
        }


        try {
            jsonObject = new JSONObject(str_data);
            
            Iterator keys = jsonObject.keys();
            while(keys.hasNext()) {
                currentDynamicKey = (String)keys.next();
                engWord.add(currentDynamicKey);
            }
            for(int i = 0; i < currentDynamicKey.length(); i++){


                int j = (int)currentDynamicKey.charAt(i);

                int k = j % 100;

                Log.d("searched", "onCreate: searched: "+ String.valueOf(k));
            }
            slotNo = (engWord.size());


        } catch (JSONException e) {
            e.printStackTrace();
        }

        for(int i = 0; i < slotNo; i++){
            bnWord.add(null);
        }

        q = (int) ((Math.random() * (slotNo - 23)) + 23);

        while(!isPrime(q)){
            q = (int) ((Math.random() * (slotNo - 23)) + 23);
        }

        Log.d("searched", "onCreate: "+ q);

        Iterator keys = jsonObject.keys();

        while(keys.hasNext()) {

            String currentDynamicKey = (String)keys.next();
            String currentDynamicValue = null;
            try {
                currentDynamicValue = jsonObject.getString(currentDynamicKey);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            num = string2Num(currentDynamicKey);
            if(max < num) max = num;

            engWord.add(currentDynamicKey);
            if(bnWord.get(num) == null){

                bnWord.set( num,currentDynamicValue);

            }

        }
        Log.d("searched", "onCreate: number "+ String.valueOf(max));




    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        query = query.toLowerCase();

        try {
            searchWord(query);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void searchWord(String query) throws JSONException {



        int stringNum = string2Num(query);
        String result = bnWord.get(stringNum);
        //String match_str = engWord.get(stringNum);
        show.setText(result);


    }

    private int string2Num(String txt) {

        int p = 0;

        for(int i = 0; i < txt.length(); i++){

            p = (int) (((p * 256) + (int)txt.charAt(i)) % q);

            /*int  p1 = (int)txt.charAt(i);
            int p2 =  p1 % q;
            int p3 = (p * 256) % q;
            p =  (p2 + p3) % q;*/

        }

        return p;

    }

    private boolean isPrime(int q) {

        if( q % 2 == 0) return false;
        for (int i = 3; i * i <= q; i++ ) {
            if(q % i == 0) return false;
        }
        Log.d("isprime", "onCreate: "+ q);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}