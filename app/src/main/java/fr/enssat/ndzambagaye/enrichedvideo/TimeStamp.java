package fr.enssat.ndzambagaye.enrichedvideo;

/**
 * Created by oumou on 21/12/2017.
 */
// classe qui permet de stocket l'intervalle de temps dans lequelle sera charger une page wikipedia donnée
    // Nous avons mis une intervalle car sans cela plusieurs URL peuvent correspondre
public class TimeStamp {
    private int positionDebut;
    private int positionFin;
    private String url;

    public TimeStamp(int positionDebut,int positionFin, String url){
        this.positionDebut= positionDebut;
        this.positionFin=positionFin;
        this.url=url;

    }

    public int getPositionDebut() {
        return positionDebut;
    }

    public void setPositionDebut(int positionDebut) {
        this.positionDebut = positionDebut;
    }

    public int getPositionFin() {
        return positionFin;
    }

    public void setPositionFin(int positionFin) {
        this.positionFin = positionFin;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
