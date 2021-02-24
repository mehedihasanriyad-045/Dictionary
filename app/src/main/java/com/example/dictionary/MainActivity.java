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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    TextView show;
    String str_data = "", strLine = "";
    SearchView searchView;
    JSONObject jsonObject;
    ArrayList<String> engWord;
    ArrayList<String> bnWord;
    int slotNo = 100000;
    int q;
    int n;
    String currentDynamicKey;
    int[][] hash_arr = new int[slotNo][3];
    int[] collision = new int[slotNo];
    int[] Xcollision = new int[slotNo];
    //ArrayList<String>[][] secondaryBnWord;
    String[][] secondaryBnWord ;
    int collide = 0;
    int x ;
    int y ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        show = findViewById(R.id.textshow);

        // Search View
        searchView = findViewById(R.id.search);
        searchView.onActionViewExpanded();
        searchView.setBackgroundColor(Color.TRANSPARENT);
        searchView.setPadding(2, 0, 0, 0);
        searchView.setGravity(Gravity.CENTER_VERTICAL);
        searchView.setOnQueryTextListener(this);

        Arrays.fill(collision,0); //  Collision Array initialization //

        x = (int) (Math.random()*13)+1;
        y = (int) (Math.random()*13);

        // Two arraylist declared to catch the words
        engWord = new ArrayList<>();
        bnWord = new ArrayList<>();


        //Json Object to String

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



        // array list for bangla meaning is initializes as null  value
        for(int i = 0; i < slotNo; i++){

            bnWord.add(null);

        }

        Log.d("slotNo", "onCreate: "+slotNo);

        // selection of prime number q
        ///q = (int) ((Math.random() * Math.pow(2,16))+1);
        q = (int) ((Math.random() * slotNo)+1);

        while(!isPrime(q)){
            q = (int) ((Math.random() * slotNo)+1);
            //q = (int) ((Math.random() * Math.pow(2,16))+1);
        }

        //Log.d("searched", "onCreate: "+ q);

        // To check collision number in each slots
        try {
            jsonObject = new JSONObject(str_data);
            Iterator keys = jsonObject.keys();
            while(keys.hasNext()) {

                String currentDynamicKey = (String)keys.next();
                String currentDynamicValue = null;

                currentDynamicValue = jsonObject.getString(currentDynamicKey);



                int Newnum = string2Num(currentDynamicKey); // String to number conversation function call

                int num = primaryHash(Newnum);

                engWord.add(currentDynamicKey);

                if(bnWord.get(num) == null){

                    bnWord.set(num,currentDynamicValue);

                }
                collision[num]++;


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // max collision number
        Xcollision = collision;
        Arrays.sort(Xcollision);
        n = Xcollision[99999];
        n = n*n;
        secondaryBnWord =  new String[slotNo][n];

        // collision wise determination of  m, a and b for second hash function

        for(int i = 0; i < slotNo; i++)
        {
            int a = (int) ((Math.random() * (n - 1)) + 1);
            int b = (int) ((Math.random() * n) );
            int len = collision[i] * collision[i];
            hash_arr[i][0] = len;
            hash_arr[i][1] = a;
            hash_arr[i][2] = b;
        }


        hashFunction();


    }

    private int primaryHash(int newnum) {

        //int a = (int) (Math.pow(2,15)+1);
        //int b = (int) Math.pow(2,15);
        //int a = (int) (Math.+1);
        int mod = 98689;

        return ((((x*newnum) % mod ) + y) % mod) % slotNo;

    }

    private void hashFunction() {


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


            if(collision[num] == 0){

                bnWord.set( num,currentDynamicValue);




            }
            else {

                int newNum = secondaryHash(num, hash_arr[num][1], hash_arr[num][2]);

                if(secondaryBnWord[num][newNum] != null ){
                    collide++;
                    Log.d("second", "hashFunction: "+secondaryBnWord[num][newNum] +"when collide "+currentDynamicValue);
                }
                secondaryBnWord[num][newNum] = (currentDynamicValue);

            }


        }
        Log.d("second", "hashFunction: "+collide);



    }

    private int secondaryHash(int i, int i1, int i2) {

        int newNum;
        int p1 = i * i1;
        int p2 = p1 + i2;
        newNum = p2 % q ;
        return newNum % n;
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


        String result;
        int num = string2Num(query);

        int stringNum = primaryHash(num);
        if(collision[stringNum] == 0){

            result = bnWord.get(stringNum);

        }
        else {

            int newNum = secondaryHash(stringNum, hash_arr[stringNum][1], hash_arr[stringNum][2]);

            result = secondaryBnWord[stringNum][newNum];

        }
        show.setText(result);



    }

    private int string2Num(String txt) {

        int p = 0;
        txt = txt.toLowerCase();
        int c;
        for(int i = 0; i < txt.length(); i++){

            if(txt.charAt(i) >= 'a' && txt.charAt(i) <= 'z') {

                c =  (txt.charAt(i) - 'a');
                p = (p*26);
                p = (p + c) % q;

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

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}