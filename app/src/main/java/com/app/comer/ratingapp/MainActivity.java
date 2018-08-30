package com.app.comer.ratingapp;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button datebutton;
    LinearLayout rating_layout;
    CalendarView mcalenderView;
    InterstitialAd InterstitialAds;
    private ProgressDialog progressDialog;
    ArrayList<String>liste;
    ArrayAdapter veriadaptor;
    ArrayList<String>veriadaptoru=new ArrayList<>();
    private static String authorUrl = "http://www.ranini.tv/reyting/";
    private static String author = "http://www.ranini.tv/reyting/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InterstitialAds = new InterstitialAd(this);
        datebutton = (Button)findViewById(R.id.datebutton);
        mcalenderView = (CalendarView) findViewById(R.id.calendarView);

        InterstitialAds.setAdUnitId("ca-app-pub-3823592309982448/2535655851");
        reklamiYukle();

        InterstitialAds.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                InterstitialAds.show();
            }
        });

        new FetchRating().execute();
        mcalenderView.setVisibility(View.GONE);
        mcalenderView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int year, int month, int dayOfMonth) {
                String ay = null,yil,gun=null;
                month=month+1;
                yil=Integer.toString(year);
                if (month<10)
                {ay= "0"+ month;}
                else{ay=Integer.toString(month);}
                if (dayOfMonth<10)
                {gun= "0"+ dayOfMonth;}
                else{gun=Integer.toString(dayOfMonth);}

                String date = yil + "-" + ay + "-" + gun;
                authorUrl=author+date;
                System.out.println(authorUrl);
                System.out.println(date);
                new FetchRating().execute();
                mcalenderView.setVisibility(View.GONE);
            }
        });

        datebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if ( mcalenderView.getVisibility()==View.GONE)
                {
                    mcalenderView.setVisibility(View.VISIBLE);
                }
                else {mcalenderView.setVisibility(View.GONE);}


                //new FetchImageLogo().execute();  // logo çekmek için
            }
        });
    }

    private void reklamiYukle() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        InterstitialAds.loadAd(adRequest);
    }

    private class FetchRating extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setTitle("REYTİNG SONUÇLARI");
            progressDialog.setMessage("Sonuçlanıyor...");
            progressDialog.setIndeterminate(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            try{

                Document doc  = Jsoup.connect(authorUrl).get();
                Elements elements = doc.select("div[class=my_table_row]");
                // class ismi post-content olan verileri çekmek için
                veriadaptoru.clear();
                for (Element element: elements)
                {
                    veriadaptoru.add(element.text() + "\n");
                }



            }catch (Exception e){

                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            rating_layout = (LinearLayout)findViewById(R.id.rating_layout);
            ListView txt_rating = (ListView)findViewById(R.id.txt_rating);

            rating_layout.setVisibility(View.VISIBLE);
            veriadaptor=new ArrayAdapter(getApplication(),R.layout.listele,veriadaptoru);
            txt_rating.setAdapter(veriadaptor);
            progressDialog.dismiss();
        }
    }
}
