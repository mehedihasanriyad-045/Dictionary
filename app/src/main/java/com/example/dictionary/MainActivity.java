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
    long slotNo;
    long q;

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
                String currentDynamicKey = (String)keys.next();
                engWord.add(currentDynamicKey);
            }

            slotNo = (engWord.size());


        } catch (JSONException e) {
            e.printStackTrace();
        }

        for(long i = 0; i < slotNo; i++){
            bnWord.add(null);
        }

        q = (long) (Math.random() * (slotNo - (slotNo-100000) + 1) + (slotNo-10000));

        while(!isPrime(q)){
            q = (long) (Math.random() * (slotNo - (slotNo-100000) + 1) + (slotNo-10000));
        }

        Iterator keys = jsonObject.keys();

        while(keys.hasNext()) {

            String currentDynamicKey = (String)keys.next();
            String currentDynamicValue = null;
            try {
                currentDynamicValue = jsonObject.getString(currentDynamicKey);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            int num = string2Num(currentDynamicKey);
            engWord.set(num,currentDynamicKey);
            bnWord.set( num,currentDynamicValue);

        }




    }

    @Override
    public boolean onQueryTextSubmit(String query) {
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
        String match_str = engWord.get(stringNum);
        show.setText(result);


    }

    private int string2Num(String txt) {

        int p = 0;

        for(int i = 0; i < txt.length(); i++){

            p = (int) (((p * 256) + txt.charAt(i)) & q);
        }

        return p;

    }

    private boolean isPrime(long q) {

        if( q % 2 == 0) return false;
        for (long i = 3; i * i <= q; i++ ) {
            if(q % i == 0) return false;
        }
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