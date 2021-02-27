package com.example.dictionary;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity  {

    TextView show;
    String str_data = "", strLine = "";
    EditText searchText;
    ImageButton submit;
    JSONObject jsonObject;
    ArrayList<Word> wordTry;
    int slotNo = 104035;
    int q;
    int n;
    String currentDynamicKey;
    int[][] hash_arr = new int[slotNo][3];
    int[] wordInSlot = new int[slotNo];
    int[] XwordInSlot = new int[slotNo];
    Word[][] secondaryWordTry ;
    int collide = 0;
    int x ;
    int y ;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        show = findViewById(R.id.textshow);

        // Search View
        searchText = findViewById(R.id.search);
        searchText.setPadding(2, 0, 0, 0);
        submit = findViewById(R.id.submit_btn);

        ActionBar actionBar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#37003c"));
        actionBar.setBackgroundDrawable(colorDrawable);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchText.getText().toString();
                query = query.toLowerCase();

                try {
                    searchWord(query);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Arrays.fill(wordInSlot,0); //  wordInSlot Array initialization //

        x = (int) (Math.random()*13)+1;
        y = (int) (Math.random()*13);

        // Two arraylist declared to catch the words
        wordTry = new ArrayList<>();


        //Json Object to String

        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(getAssets().open("Dataset.json")));
            while (strLine != null)
            {
                str_data += strLine;
                strLine = br.readLine();

            }
            br.close();
        } catch (FileNotFoundException e) {
            System.err.println("File not found");
        } catch (IOException e) {
            System.err.println("Unable to read the file.");
        }



        // array list for bangla meaning is initializes as null  value
        for(int i = 0; i < slotNo; i++){

            //bnWord.add(null);
            Word word = new Word(null, null);
            wordTry.add(word);

        }

        Log.d("slotNo", "onCreate: "+slotNo);


        q = (int) (1e9+7);


        // To check wordInSlot number in each slots
        try {
            jsonObject = new JSONObject(str_data);
            Iterator keys = jsonObject.keys();
            while(keys.hasNext()) {

                String currentDynamicKey = (String)keys.next();
                String currentDynamicValue = null;

                currentDynamicValue = jsonObject.getString(currentDynamicKey);



                int Newnum = string2Num(currentDynamicKey); // String to number conversation function call

                int num = primaryHash(Newnum);


                Word word = wordTry.get(num);

                /*if(word.getEnWord() == null){

                    Word word1 = new Word(currentDynamicKey, currentDynamicValue);
                    wordTry.set(num,word1);

                }*/
                wordInSlot[num]++;


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // max wordInSlot number
        XwordInSlot = wordInSlot;
        Arrays.sort(XwordInSlot);
        n = XwordInSlot[slotNo-1];
        n = n*n;

        secondaryWordTry = new Word[slotNo][n];

        // wordInSlot wise determination of  m, a and b for second hash function

        for(int i = 0; i < slotNo; i++)
        {
            int a = (int) ((Math.random() * (n - 1)) + 1);
            int b = (int) ((Math.random() * n) );
            int len = wordInSlot[i] * wordInSlot[i];
            hash_arr[i][0] = len;
            hash_arr[i][1] = a;
            hash_arr[i][2] = b;
        }


        hashFunction();


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private int primaryHash(int newnum) {


        int mod = 98689;
        int p1 = Math.floorMod(x * newnum,q);
        int p2 = Math.floorMod(p1+y,q);
        int p = Math.floorMod(p2,slotNo);



        return p;

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void hashFunction() {

        // array list for bangla meaning is initializes as null  value
        for(int i = 0; i < slotNo; i++){


            Word word = new Word(null, null);
            wordTry.add(word);

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

            int Newnum = string2Num(currentDynamicKey); // String to number conversation function call

            int num = primaryHash(Newnum);
            currentDynamicKey = currentDynamicKey.toLowerCase();

            if(wordInSlot[num] == 0){

                //bnWord.set( num,currentDynamicValue);
                Word word = new Word(currentDynamicKey, currentDynamicValue);
                wordTry.set(num,word);

            }
            else {

                int newNum = secondaryHash(num, hash_arr[num][1], hash_arr[num][2]);


                collide++;
                Word word = new Word(currentDynamicKey, currentDynamicValue);
                secondaryWordTry[num][newNum] = word;
            }


        }
        int col = 0;
        for(int i = 0; i < wordInSlot.length;i++)
        {
            col += wordInSlot[i];
        }
        Log.d("second", "hashFunction: "+collide+" "+col);



    }

    private int secondaryHash(int i, int i1, int i2) {

        int newNum;
        int p1 = i * i1;
        int p2 = p1 + i2;
        newNum = p2 % q ;
        return newNum % n;
    }




    @RequiresApi(api = Build.VERSION_CODES.N)
    private void searchWord(String query) throws JSONException {


        String result,res;
        query = query.toLowerCase();
        int num = string2Num(query);

        int stringNum = primaryHash(num);
        Word word;
        if(wordInSlot[stringNum] == 0){


            word = wordTry.get(stringNum);
            result = word.getBnWord();
            res = word.getEnWord();

            if(query.equals(res)){
                show.setText(result);
            }
            else{
                show.setText("Sorry! Not available!!");
            }

        }
        else {

            int newNum = secondaryHash(stringNum, hash_arr[stringNum][1], hash_arr[stringNum][2]);


            word = secondaryWordTry[stringNum][newNum];
            result = word.getBnWord();
            res = word.getEnWord();
            if(query.equals(res)){
                show.setText(result);
            }
            else{
                show.setText("Sorry! Not available!!");
            }

        }



    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private int string2Num(String txt) {

        int p = 0;
        txt = txt.toLowerCase();
        int c;
        for(int i = 0; i < txt.length(); i++){

            if(txt.charAt(i) >= 'a' && txt.charAt(i) <= 'z') {

                c =  (txt.charAt(i) - 'a');
                p = Math.floorMod(p*26,q);
                p = Math.floorMod(p + c,q);

            }


        }

        return p;

    }

    private boolean isPrime(int q) {

        if( q % 2 == 0) return false;
        for (int i = 3; i * i <= q; i++ ) {
            if(q % i == 0) return false;
        }
        return true;
    }


}