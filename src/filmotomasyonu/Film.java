package filmotomasyonu;

import java.io.Serializable;



public class Film implements Serializable {
    private int id;
    private String ad;
    private String tur;
    private String yonetmen;
    private int yayinYili;
    private int sure;
    private double imdbPuani; 
    private String posterUrl;
    private boolean izlendiMi; 

    /**
     * Film sınıfı yapıcı metodu
     *
     * @param id Film ID
     * @param ad Film adı
     * @param tur Film türü
     * @param yonetmen Film yönetmeni
     * @param yayinYili Yayın yılı
     * @param sure Film süresi (dakika)
     * @param imdbPuani Puan (IMDb veya TMDb'den gelen)
     * @param posterUrl Poster resminin URL'si
     * @param izlendiMi Filmin izlenip izlenmediği durumu
     */
    public Film(int id, String ad, String tur, String yonetmen, int yayinYili,
            int sure, double imdbPuani, String posterUrl, boolean izlendiMi) { 
        this.id = id;
        this.ad = ad;
        this.tur = tur;
        this.yonetmen = yonetmen;
        this.yayinYili = yayinYili;
        this.sure = sure;
        this.imdbPuani = imdbPuani;
        this.posterUrl = (posterUrl == null || posterUrl.equals("N/A")) ? "" : posterUrl;
        this.izlendiMi = izlendiMi; 
    }

    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getAd() { return ad; }
    public void setAd(String ad) { this.ad = ad; }
    public String getTur() { return tur; }
    public void setTur(String tur) { this.tur = tur; }
    public String getYonetmen() { return yonetmen; }
    public void setYonetmen(String yonetmen) { this.yonetmen = yonetmen; }
    public int getYayinYili() { return yayinYili; }
    public void setYayinYili(int yayinYili) { this.yayinYili = yayinYili; }
    public int getSure() { return sure; }
    public void setSure(int sure) { this.sure = sure; }
    public double getImdbPuani() { return imdbPuani; } 
    public void setImdbPuani(double imdbPuani) { this.imdbPuani = imdbPuani; }
    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) {
        this.posterUrl = (posterUrl == null || posterUrl.equals("N/A")) ? "" : posterUrl;
    }
    public boolean isIzlendiMi() { return izlendiMi; } 
    public void setIzlendiMi(boolean izlendiMi) { this.izlendiMi = izlendiMi; } 


    
    public String toFileString() {
        StringBuilder sb = new StringBuilder();
        sb.append(id).append("|");
        sb.append(ad).append("|");
        sb.append(tur).append("|");
        sb.append(yonetmen).append("|");
        sb.append(yayinYili).append("|");
        sb.append(sure).append("|");
        sb.append(imdbPuani).append("|");
        sb.append(posterUrl != null ? posterUrl : "").append("|"); 
        sb.append(izlendiMi); 
        return sb.toString();
    }

    
    public static Film fromFileString(String line) {
        String[] parts = line.split("\\|", -1); 
        if (parts.length != 9) { 
            throw new IllegalArgumentException("Geçersiz film verisi formatı: '" + line + "'. Beklenen parça sayısı 9, alınan: " + parts.length);
        }

        int id = Integer.parseInt(parts[0]);
        String ad = parts[1];
        String tur = parts[2];
        String yonetmen = parts[3];
        int yayinYili = Integer.parseInt(parts[4]);
        int sure = Integer.parseInt(parts[5]);
        double imdbPuani = Double.parseDouble(parts[6]);
        String posterUrl = parts[7];
        boolean izlendiMi = Boolean.parseBoolean(parts[8]); 

        return new Film(id, ad, tur, yonetmen, yayinYili, sure, imdbPuani, posterUrl, izlendiMi);
    }

    @Override
    public String toString() {
        return "Film{" +
               "id=" + id +
               ", ad='" + ad + '\'' +
               ", tur='" + tur + '\'' +
               ", yonetmen='" + yonetmen + '\'' +
               ", yayinYili=" + yayinYili +
               ", sure=" + sure +
               ", imdbPuani=" + imdbPuani +
               ", posterUrl='" + posterUrl + '\'' +
               ", izlendiMi=" + izlendiMi + 
               '}';
    }
}