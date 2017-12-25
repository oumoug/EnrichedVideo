package fr.enssat.ndzambagaye.enrichedvideo;

import android.webkit.WebView;
import android.widget.VideoView;

import  android.os.Handler;

/**
 * Created by oumou on 21/12/2017.
 */
 // charge une nouvelle URL dans la webView
public class RunPageWeb implements Runnable {
    private VideoView video;
    private WebView pageWeb;
    private Handler myHandler;
    private  MainActivity mainActivity;

    public RunPageWeb(VideoView video,WebView pageweb,Handler  myHandler, MainActivity mainActivity){
        this.video=video;
        this.pageWeb=pageweb;
        this.myHandler= myHandler;
        this.mainActivity=mainActivity;
    }

    // récupère la position de la vidéo et charge la page web correspondant
    // methode appelée à chaque fois que l'on clique sur les bouton de chapitrage
    public void run() {
        int positionvideo = video.getCurrentPosition();
        String url=mainActivity.getUrlACharger(positionvideo);
        if (url != null) {
            if (! pageWeb.getUrl().equals(url)) {
                pageWeb.loadUrl(url);
            }
        }
    }
}
