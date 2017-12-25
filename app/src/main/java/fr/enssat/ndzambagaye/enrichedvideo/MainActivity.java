package fr.enssat.ndzambagaye.enrichedvideo;


import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.VideoView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import android.util.Log;
import android.app.ProgressDialog;


public class MainActivity extends AppCompatActivity {
    private MediaPlayer mediaplayer;
    private VideoView video;
    private WebView pageWeb;
    private List<TimeStamp> listeTimeStamp;
    private Map<String,Integer> mapPosition;
    private int ancienPosition;
    private Handler myHandler;
    private HorizontalScrollView scrollBar;
    private LinearLayout layoutChapitre;
    private AsyncTask tacheAsyncrone;
    private RunPageWeb runPageWeb;
    private ProgressDialog mDialog;

    //private TacheAsynchrone tacheAsynchrone;

    MediaController mediaControler;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //tacheAsynchrone= new TacheAsynchrone(this);
        //tacheAsynchrone.execute();
        Log.i("lifecycle", "onCreate");
        createChapitre();
        myHandler=new Handler();
        chargementVideo();
        prepareChargementPageWeb();
        runPageWeb=new RunPageWeb(video, pageWeb,  myHandler, this);

    }
    // crée les boutons des chapitre
    public void createChapitre(){
        layoutChapitre=(LinearLayout) findViewById(R.id.linearLayout1);
        getPositionChapitre();
        Button  bouton;
        for(Map.Entry<String,Integer> elem: mapPosition.entrySet()){
            bouton=new Button(this);
            bouton.setText(elem.getKey());
            bouton.setWidth(300);
            bouton.setHeight(200);
            bouton.setId(elem.getValue());
            bouton.setOnClickListener(listenerScrollBar);
            layoutChapitre.addView(bouton);
        }
    }
  // Charge la vidéo affiche une barre de progression
    public void chargementVideo(){
        video= (VideoView) findViewById(R.id.videoView1);
        video.setVideoURI(Uri.parse(getString(R.string.url_video)));
        mediaControler=new MediaController(MainActivity.this);
        mediaControler.setAnchorView(video);
        video.setMediaController(mediaControler);
        Log.i("lifecycle", "chargement video");
        mDialog = new ProgressDialog(MainActivity.this);
        mDialog.setMessage("Chargement en cours");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){
            public void onPrepared(MediaPlayer mp) {
                video.setMediaController(mediaControler);
                mediaControler.setAnchorView(video);
                video.start();
                mDialog.dismiss();
                ancienPosition=video.getCurrentPosition();
                getUrltimeStamps();
            }
        });
    }
    // defini la webView et charge la page web de depart
    public void prepareChargementPageWeb(){
        pageWeb= (WebView) findViewById(R.id.webView1);
        pageWeb.setWebViewClient(new WebViewClient());
        pageWeb.setWebChromeClient(new WebChromeClient());
        Log.i("lifecycle", "Charge page web");
        pageWeb.getSettings().setJavaScriptEnabled(true);
        pageWeb.setWebViewClient(new WebViewClient(){
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                          pageWeb.loadUrl(url);
                          return false;
        }
    });
        pageWeb.loadUrl(getString(R.string.page_wiki));
    }

    // trouve la page web à charger en fonction de la position de la vidéo
    public String getUrlACharger(int position){
        String  url=null;
        int i=0;
        TimeStamp ts;
        Boolean trouve=false;
        String urlCourant=pageWeb.getUrl();
        while(i<listeTimeStamp.size() && trouve==false){
           ts=listeTimeStamp.get(i);
            if(ts.getPositionDebut()<=position && ts.getPositionFin()>position){
                url=ts.getUrl();
                trouve=true;
            }
            i=i+1;
        }
        return url;
    }
    // charge le fichier JSON des chapitres dans une map
    public void getPositionChapitre(){
        this.mapPosition=new HashMap<String,Integer>();
        JSONObject objetJson;
        JSONArray tableauJson;
        int position;
        String titre;
        InputStream inputStream=getResources().openRawResource(R.raw.chapitre);
        ByteArrayOutputStream byteArrayOutputStream= new  ByteArrayOutputStream();
        try {
            int elem = inputStream.read();
            while (elem != -1) {
                byteArrayOutputStream.write(elem);
                elem = inputStream.read();
            }
            inputStream.close();
            try {
                objetJson = new JSONObject(byteArrayOutputStream.toString());
                tableauJson = objetJson.getJSONArray("chapitre");

                for (int i = 0; i < tableauJson.length(); i++) {
                    position = tableauJson.getJSONObject(i).getInt("position");
                    titre = tableauJson.getJSONObject(i).getString("titre");
                    this.mapPosition.put(titre,position);
                }
            } catch (JSONException e){
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // met la vidéo à une position  données lorqu'on clique sur un bouton
    private View.OnClickListener listenerScrollBar=new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            video.seekTo(view.getId());
            myHandler.postDelayed(runPageWeb,200);
        }
    };

    public void setMapPosition(Map<String, Integer> mapPosition) {
        this.mapPosition = mapPosition;
    }

    // recupère les URL et les positions ou on doit les charges
    // les données sont dans un Fichier JSon
    public void getUrltimeStamps(){
        this.listeTimeStamp=new ArrayList<TimeStamp>();
        TimeStamp ts;
        JSONObject objetJson;
        JSONArray tableauJson;
        TimeStamp timeStamp;
        int position;
        int positionFin;
        String url;
        Log.i("lifecycle", "Récupere les URL");
        InputStream inputStream=getResources().openRawResource(R.raw.timestamp);
        ByteArrayOutputStream byteArrayOutputStream= new  ByteArrayOutputStream();
        try {

            int elem = inputStream.read();
            // lecture du fichier et stockage dans un InputStream
            while (elem != -1) {
                byteArrayOutputStream.write(elem);
                elem = inputStream.read();
            }
            inputStream.close();
            try {
                // recuperation des données
                objetJson = new JSONObject(byteArrayOutputStream.toString());
                tableauJson = objetJson.getJSONArray("timeStamp");
                for (int i = 0; i < tableauJson.length(); i++) {
                    position = tableauJson.getJSONObject(i).getInt("positionDebut");
                    url = tableauJson.getJSONObject(i).getString("url");
                    positionFin = tableauJson.getJSONObject(i).getInt("positionFin");
                    this.listeTimeStamp.add(new TimeStamp(position,positionFin,url));
                }
            } catch (JSONException e){
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public List<TimeStamp> getListeTimeStamp() {
        return listeTimeStamp;
    }

    public void setListeTimeStamp(List<TimeStamp> listeTimeStamp) {
        this.listeTimeStamp = listeTimeStamp;
    }

    public Map<String, Integer> getMapPosition() {
        return mapPosition;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("lifecycle", "OnResume");
        video.seekTo(ancienPosition);
        video.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("lifecycle", "Onpause");
        ancienPosition=video.getCurrentPosition();
        video.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myHandler.removeCallbacks(runPageWeb);

    }
    // enregistre la position de la video
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("duree", ancienPosition);
        Log.i("savePosition", Integer.toString(ancienPosition));
    }
    // charge la position de la video
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        ancienPosition = savedInstanceState.getInt("duree");
        Log.i("loadPosition", Integer.toString(ancienPosition));


    }
}
