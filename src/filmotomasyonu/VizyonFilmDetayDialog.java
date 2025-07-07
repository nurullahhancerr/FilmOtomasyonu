package filmotomasyonu;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

public class VizyonFilmDetayDialog extends JDialog {
    private JLabel posterLabel;
    private JTextArea detayAlani;
    private JButton kutuphanemeEkleButton;
    private JButton kapatButton;

    private final FilmYonetici filmYoneticiDialogIcın;
    private final int tmdbFilmIdDialog;
  
    private final String filmAdiDialogBaslangic; 
    private final String posterPathDialogBaslangic;
    private final String releaseDateDialogBaslangic;
    private final double tmdbPuaniDialogBaslangic;
    private final String ozetDialogBaslangic;

    private String TMDB_API_KEY;

    private void loadApiKey() {
        Properties prop = new Properties();
        try (InputStream input = new FileInputStream("config.properties")) {
            prop.load(input);
            TMDB_API_KEY = prop.getProperty("TMDB_API_KEY");
        } catch (IOException ex) {
            ex.printStackTrace();
            // Gerçek bir uygulamada burada daha iyi bir hata yönetimi yapılmalıdır.
            JOptionPane.showMessageDialog(this, "API anahtarı yüklenemedi. Lütfen config.properties dosyasını kontrol edin.", "Yapılandırma Hatası", JOptionPane.ERROR_MESSAGE);
        }
    }
    private final String TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/";
    private final String POSTER_SIZE = "w185";
    private final int POSTER_LABEL_WIDTH = 185;
    private final int POSTER_LABEL_HEIGHT = 278;
    private final HttpClient httpClient;

    private static final Map<Integer, String> GENRE_ID_MAP_TR;
    static {
        GENRE_ID_MAP_TR = new HashMap<>();
        GENRE_ID_MAP_TR.put(28, "Aksiyon"); GENRE_ID_MAP_TR.put(12, "Macera");
        GENRE_ID_MAP_TR.put(16, "Animasyon"); GENRE_ID_MAP_TR.put(35, "Komedi");
        GENRE_ID_MAP_TR.put(80, "Suç"); GENRE_ID_MAP_TR.put(99, "Belgesel");
        GENRE_ID_MAP_TR.put(18, "Dram"); GENRE_ID_MAP_TR.put(10751, "Aile");
        GENRE_ID_MAP_TR.put(14, "Fantastik"); GENRE_ID_MAP_TR.put(36, "Tarih");
        GENRE_ID_MAP_TR.put(27, "Korku"); GENRE_ID_MAP_TR.put(10402, "Müzikal");
        GENRE_ID_MAP_TR.put(9648, "Gizem"); GENRE_ID_MAP_TR.put(10749, "Romantik");
        GENRE_ID_MAP_TR.put(878, "Bilim Kurgu"); GENRE_ID_MAP_TR.put(10770, "TV Filmi");
        GENRE_ID_MAP_TR.put(53, "Gerilim"); GENRE_ID_MAP_TR.put(10752, "Savaş");
        GENRE_ID_MAP_TR.put(37, "Western");
    }
    private static final Map<String, String> GENRE_MAP_EN_TR;
    static {
        GENRE_MAP_EN_TR = new HashMap<>();
        GENRE_MAP_EN_TR.put("Action", "Aksiyon"); GENRE_MAP_EN_TR.put("Adventure", "Macera");
        GENRE_MAP_EN_TR.put("Sci-Fi", "Bilim Kurgu"); GENRE_MAP_EN_TR.put("Science Fiction", "Bilim Kurgu");
        GENRE_MAP_EN_TR.put("Comedy", "Komedi"); GENRE_MAP_EN_TR.put("Drama", "Dram");
        GENRE_MAP_EN_TR.put("Romance", "Romantik"); GENRE_MAP_EN_TR.put("Horror", "Korku");
        GENRE_MAP_EN_TR.put("Thriller", "Gerilim"); GENRE_MAP_EN_TR.put("Fantasy", "Fantastik");
        GENRE_MAP_EN_TR.put("Animation", "Animasyon"); GENRE_MAP_EN_TR.put("Documentary", "Belgesel");
        GENRE_MAP_EN_TR.put("Family", "Aile"); GENRE_MAP_EN_TR.put("Crime", "Suç");
        GENRE_MAP_EN_TR.put("Mystery", "Gizem"); GENRE_MAP_EN_TR.put("History", "Tarih");
        GENRE_MAP_EN_TR.put("Music", "Müzik"); GENRE_MAP_EN_TR.put("Musical", "Müzikal");
        GENRE_MAP_EN_TR.put("War", "Savaş"); GENRE_MAP_EN_TR.put("Western", "Western");
        GENRE_MAP_EN_TR.put("Biography", "Biyografi"); GENRE_MAP_EN_TR.put("Sport", "Spor");
        GENRE_MAP_EN_TR.put("TV Movie", "TV Filmi");
    }

    public VizyonFilmDetayDialog(JFrame parent, FilmYonetici filmYonetici,
                                 int tmdbFilmId, String filmAdi, String posterPath, String releaseDate,
                                 double tmdbPuani, String ozet) {
        super(parent, "Film Detayı", true);
        this.filmYoneticiDialogIcın = filmYonetici;
        this.tmdbFilmIdDialog = tmdbFilmId;
        this.filmAdiDialogBaslangic = filmAdi; // Başlangıç değerlerini ata
        this.posterPathDialogBaslangic = posterPath;
        this.releaseDateDialogBaslangic = releaseDate;
        this.tmdbPuaniDialogBaslangic = tmdbPuani;
        this.ozetDialogBaslangic = (ozet == null || ozet.trim().isEmpty() || "null".equalsIgnoreCase(ozet.trim())) ?
                                   "Açıklama bulunamadı." : ozet;
        this.httpClient = HttpClient.newHttpClient();

        initComponents();
        loadInitialData();
    }

    private void initComponents() {
        
        setSize(400, 600);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(50, 50, 50));

        posterLabel = new JLabel("Poster Yükleniyor...", SwingConstants.CENTER);
        posterLabel.setPreferredSize(new Dimension(POSTER_LABEL_WIDTH, POSTER_LABEL_HEIGHT));
        posterLabel.setOpaque(true);
        posterLabel.setBackground(Color.BLACK);
        posterLabel.setForeground(Color.WHITE);
        posterLabel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        add(posterLabel, BorderLayout.NORTH);

        detayAlani = new JTextArea();
        detayAlani.setEditable(false);
        detayAlani.setLineWrap(true);
        detayAlani.setWrapStyleWord(true);
        detayAlani.setFont(new Font("SansSerif", Font.PLAIN, 13));
        detayAlani.setBackground(new Color(60, 63, 65));
        detayAlani.setForeground(Color.WHITE);
        detayAlani.setMargin(new Insets(5,5,5,5));
        JScrollPane detayScrollPane = new JScrollPane(detayAlani);
        detayScrollPane.setBorder(BorderFactory.createEmptyBorder(0,5,0,5));
        add(detayScrollPane, BorderLayout.CENTER);

        JPanel butonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        butonPanel.setOpaque(false);
        kutuphanemeEkleButton = new JButton("Kütüphaneme Ekle");
        styleDialogButton(kutuphanemeEkleButton);
        kutuphanemeEkleButton.addActionListener(e -> filmiKutuphaneyeDirektEkle());

        kapatButton = new JButton("Kapat");
        styleDialogButton(kapatButton);
        kapatButton.addActionListener(e -> dispose());

        butonPanel.add(kutuphanemeEkleButton);
        butonPanel.add(kapatButton);
        add(butonPanel, BorderLayout.SOUTH);
    }

    private void loadInitialData() {
        
        if (this.posterPathDialogBaslangic != null && !this.posterPathDialogBaslangic.equals("null") && !this.posterPathDialogBaslangic.isEmpty()) {
            gosterPosteri(TMDB_IMAGE_BASE_URL + POSTER_SIZE + this.posterPathDialogBaslangic);
        } else {
            posterLabel.setText("Poster bulunamadı.");
            posterLabel.setIcon(null);
            posterLabel.setBackground(Color.DARK_GRAY);
        }

        String vizyonYili = "Bilinmiyor";
        if (this.releaseDateDialogBaslangic != null && this.releaseDateDialogBaslangic.length() >= 4) {
            vizyonYili = this.releaseDateDialogBaslangic.substring(0, 4);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Film Adı: ").append(this.filmAdiDialogBaslangic).append("\n\n");
        sb.append("Vizyon Yılı: ").append(vizyonYili).append("\n\n");
        sb.append("Puan: ").append(String.format("%.1f", this.tmdbPuaniDialogBaslangic).replace(",", ".")).append("/10\n\n");
        sb.append("Özet:\n").append(this.ozetDialogBaslangic);
        detayAlani.setText(sb.toString());
        SwingUtilities.invokeLater(() -> detayAlani.setCaretPosition(0));
    }

    private void styleDialogButton(JButton button) {
        button.setBackground(Color.BLACK);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(200, 35));
    }

    private void gosterPosteri(String posterUrlString) {
        
         SwingWorker<ImageIcon, Void> worker = new SwingWorker<ImageIcon, Void>() {
            @Override
            protected ImageIcon doInBackground() throws Exception {
                try {
                    URL imageUrl = new URL(posterUrlString);
                    BufferedImage originalImage = ImageIO.read(imageUrl);
                    if (originalImage != null) {
                        int originalWidth = originalImage.getWidth();
                        int originalHeight = originalImage.getHeight();
                        if (originalWidth == 0 || originalHeight == 0) return null;
                        int boxWidth = POSTER_LABEL_WIDTH - 10; 
                        int boxHeight = POSTER_LABEL_HEIGHT - 10;
                        float scaleFactorWidth = (float) boxWidth / originalWidth;
                        float scaleFactorHeight = (float) boxHeight / originalHeight;
                        float scaleFactor = Math.max(scaleFactorWidth, scaleFactorHeight); 
                        int newWidth = Math.round(originalWidth * scaleFactor);
                        int newHeight = Math.round(originalHeight * scaleFactor);
                        Image tempResizedImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                        BufferedImage finalImage = new BufferedImage(boxWidth, boxHeight, BufferedImage.TYPE_INT_ARGB);
                        Graphics2D g2d = finalImage.createGraphics();
                        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                        int x = (boxWidth - newWidth) / 2; int y = (boxHeight - newHeight) / 2;
                        g2d.drawImage(tempResizedImage, x, y, null);
                        g2d.dispose();
                        return new ImageIcon(finalImage);
                    }
                } catch (Exception e) { System.err.println("Dialog poster yükleme hatası: " + e.getMessage()); }
                return null;
            }
            @Override
            protected void done() {
                try {
                    ImageIcon imageIcon = get();
                    if (imageIcon != null && imageIcon.getImage() != null) {
                        posterLabel.setIcon(imageIcon); posterLabel.setText(""); 
                        posterLabel.setBackground(UIManager.getColor("Label.background")); 
                    } else {
                        posterLabel.setIcon(null); posterLabel.setText("Poster yüklenemedi.");
                        posterLabel.setBackground(Color.LIGHT_GRAY);
                    }
                } catch (Exception e) {
                    posterLabel.setIcon(null); posterLabel.setText("Poster gösterilemedi.");
                    posterLabel.setBackground(Color.LIGHT_GRAY); e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void filmiKutuphaneyeDirektEkle() {
        kutuphanemeEkleButton.setEnabled(false);
        kutuphanemeEkleButton.setText("Ekleniyor...");

       
        
        final String currentFilmAdi = VizyonFilmDetayDialog.this.filmAdiDialogBaslangic;
        final String currentReleaseDate = VizyonFilmDetayDialog.this.releaseDateDialogBaslangic;
        final double currentTmdbPuani = VizyonFilmDetayDialog.this.tmdbPuaniDialogBaslangic;
        final String currentPosterPath = VizyonFilmDetayDialog.this.posterPathDialogBaslangic;


        SwingWorker<Film, String> eklemeWorker = new SwingWorker<Film, String>() {
            private String hataMesajiWorker = null;
            private boolean duplikeOlduWorker = false;
            // Hata mesajında kullanılacak film adı ve yılı için, worker başlamadan önceki değerleri alalım
            private String filmAdiHataIcin = currentFilmAdi; 
            private String filmYiliHataIcin = (currentReleaseDate != null && currentReleaseDate.length() >=4 ? currentReleaseDate.substring(0,4) : "Bilinmeyen Yıl");


            @Override
            protected Film doInBackground() throws Exception {
                
                try {
                    String detayUrl = "https://api.themoviedb.org/3/movie/" + VizyonFilmDetayDialog.this.tmdbFilmIdDialog +
                                    "?api_key=" + TMDB_API_KEY + "&language=tr-TR&append_to_response=credits";
                    HttpRequest detayRequest = HttpRequest.newBuilder().uri(URI.create(detayUrl)).GET().build();
                    // httpClient'ı dış sınıfın üyesi olarak kullanın: VizyonFilmDetayDialog.this.httpClient
                    HttpResponse<String> detayResponse = VizyonFilmDetayDialog.this.httpClient.send(detayRequest, HttpResponse.BodyHandlers.ofString());

                    if (detayResponse.statusCode() != 200) {
                        hataMesajiWorker = "Film detayları çekilemedi. HTTP Kodu: " + detayResponse.statusCode() + "\n" + detayResponse.body();
                        return null;
                    }

                    JSONObject filmDetayJson = new JSONObject(detayResponse.body());
                    if (!filmDetayJson.has("id")) {
                        hataMesajiWorker = "API'den beklenen film detayı alınamadı.";
                        return null;
                    }
                     System.out.println("Film Detay JSON'u (Türkçe): " + filmDetayJson.toString(2));

                    String ad = filmDetayJson.optString("title", currentFilmAdi); // Dış sınıfın üyesini kullan
                    this.filmAdiHataIcin = ad; // Güncel adı hata mesajı için sakla
                    
                    int yayinYili = 0;
                    String releaseDateApi = filmDetayJson.optString("release_date", currentReleaseDate);
                    if (releaseDateApi != null && releaseDateApi.length() >= 4) {
                        try { 
                            yayinYili = Integer.parseInt(releaseDateApi.substring(0, 4));
                            this.filmYiliHataIcin = String.valueOf(yayinYili); // Güncel yılı hata mesajı için sakla
                        } 
                        catch (NumberFormatException e) { System.err.println("Yayın yılı parse edilemedi: " + releaseDateApi); }
                    }

                    String turkceTur = "Bilinmiyor";
                    JSONArray genresArray = filmDetayJson.optJSONArray("genres");
                    if (genresArray != null && genresArray.length() > 0) {
                        boolean eslesmeBulundu = false;
                        StringBuilder apiGenresForLog = new StringBuilder();
                        for (int j = 0; j < genresArray.length(); j++) {
                            JSONObject genreObj = genresArray.optJSONObject(j);
                            if (genreObj != null) {
                                int genreId = genreObj.optInt("id", -1);
                                String ingilizceApiTuru = genreObj.optString("name", "").trim();
                                if (j > 0) apiGenresForLog.append(", ");
                                apiGenresForLog.append(ingilizceApiTuru + "(ID:" + genreId + ")");

                                if (genreId != -1 && GENRE_ID_MAP_TR.containsKey(genreId)) {
                                    turkceTur = GENRE_ID_MAP_TR.get(genreId); eslesmeBulundu = true; break;
                                }
                                if (!eslesmeBulundu && !ingilizceApiTuru.isEmpty()) {
                                    for (Map.Entry<String, String> entry : GENRE_MAP_EN_TR.entrySet()) {
                                        if (entry.getKey().equalsIgnoreCase(ingilizceApiTuru)) {
                                            turkceTur = entry.getValue(); eslesmeBulundu = true; break;
                                        }
                                    }
                                }
                            }
                            if (eslesmeBulundu) break;
                        }
                        if (!eslesmeBulundu && apiGenresForLog.length() > 0) {
                            System.out.println("API'den gelen türlerden (" + apiGenresForLog.toString() + ") için haritalarda Türkçe eşleşme bulunamadı. '" + turkceTur + "' kullanılacak.");
                        }
                    } else {
                        System.out.println("API'den film için tür bilgisi (genres array) gelmedi veya null. '" + turkceTur + "' kullanılacak.");
                    }

                    String yonetmen = "Bilinmiyor";
                    JSONObject credits = filmDetayJson.optJSONObject("credits");
                    if (credits != null) {
                        JSONArray crewArray = credits.optJSONArray("crew");
                        if (crewArray != null) {
                            for (int i = 0; i < crewArray.length(); i++) {
                                JSONObject crewMember = crewArray.optJSONObject(i);
                                if (crewMember != null && "Director".equalsIgnoreCase(crewMember.optString("job", ""))) {
                                    yonetmen = crewMember.optString("name", "Bilinmiyor"); break;
                                }
                            }
                        }
                    }
                    int sure = filmDetayJson.optInt("runtime", 0);
                    double tmdbPuanAlinan = filmDetayJson.optDouble("vote_average", currentTmdbPuani);

                    String tamPosterUrl = "";
                    String apiDetayPosterPath = filmDetayJson.optString("poster_path", currentPosterPath);
                    if (apiDetayPosterPath != null && !apiDetayPosterPath.isEmpty() && !apiDetayPosterPath.equals("null")) {
                        tamPosterUrl = TMDB_IMAGE_BASE_URL + POSTER_SIZE + apiDetayPosterPath;
                    }

                    Film filmOlustur = new Film(0, ad, turkceTur, yonetmen, yayinYili, sure, tmdbPuanAlinan, tamPosterUrl, false);
                    
                    // FilmYonetici'ye erişirken dış sınıfın üyesini kullan
                    Film eklenenFilmSonuc = VizyonFilmDetayDialog.this.filmYoneticiDialogIcın.filmEkle(filmOlustur);

                    if (eklenenFilmSonuc == null) {
                        duplikeOlduWorker = true;
                        return null;
                    }
                    return eklenenFilmSonuc;

                } catch (Exception e) {
                    hataMesajiWorker = "Film eklenirken genel hata: " + e.getMessage();
                    e.printStackTrace(); return null;
                }
            }

            @Override
            protected void process(List<String> chunks) { }

            @Override
            protected void done() {
                try {
                    Film sonucFilm = get();
                    if (sonucFilm != null) {
                        JOptionPane.showMessageDialog(VizyonFilmDetayDialog.this,
                                "'" + sonucFilm.getAd() + "' kütüphaneye başarıyla eklendi!" +
                                "\nTür: " + sonucFilm.getTur(),
                                "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    } else if (duplikeOlduWorker) { 
                         JOptionPane.showMessageDialog(VizyonFilmDetayDialog.this,
                                "'" + filmAdiHataIcin + "' (" + filmYiliHataIcin + ") adlı film zaten kütüphanede mevcut!",
                                "Duplike Film Uyarısı", JOptionPane.WARNING_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(VizyonFilmDetayDialog.this,
                                "Film eklenemedi.\n" + (hataMesajiWorker != null ? hataMesajiWorker : "Bilinmeyen bir sorun oluştu."),
                                "Ekleme Hatası", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog( (getParent() != null ? getParent() : VizyonFilmDetayDialog.this) ,
                                                   "Film ekleme işlemi sonucunda bir hata oluştu: " + e.getMessage(),
                                                   "Kritik Hata", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                } finally {
                    kutuphanemeEkleButton.setText("Kütüphaneme Ekle");
                    kutuphanemeEkleButton.setEnabled(true);
                }
            }
        };
        eklemeWorker.execute();
    }
}