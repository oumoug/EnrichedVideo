package fr.enssat.ndzambagaye.enrichedvideo;

import android.os.AsyncTask;
import android.widget.VideoView;
import android.app.ProgressDialog;

import java.util.List;
import java.util.Map;


/**
 * Created by oumou on 21/12/2017.
 */
/*
public class TacheAsynchrone extends AsyncTask<Void,Void ,Void> {
    private  MainActivity mainActivity;
    private ProgressDialog mDialog;
    private List<TimeStamp> liste;
    private Map<String,Integer> map;

    public TacheAsynchrone( MainActivity mainActivity){
       this.mainActivity=mainActivity;
    }

    public List<TimeStamp> getListe() {
        return liste;
    }

    public void setListe(List<TimeStamp> liste) {
        this.liste = liste;
    }

    public Map<String, Integer> getMap() {
        return map;
    }

    public void setMap(Map<String, Integer> map) {
        this.map = map;
    }

    protected Void doInBackground(Void ... v ) {
        mainActivity.getUrltimeStamps();
        liste=mainActivity.getListeTimeStamp();
        mainActivity.getPositionChapitre();
        map=mainActivity.getMapPosition();


       return null;
    }

     protected void onPreExecute() {
        mDialog = new ProgressDialog(mainActivity);
        mDialog.setMessage("Chargement en cours");
        mDialog.setCanceledOnTouchOutside(false);
     }

    protected void onPostExecute(Void v) {
        mainActivity.setListeTimeStamp(liste);
        mainActivity.setMapPosition(map);
        mDialog.dismiss();
    }

}*/
