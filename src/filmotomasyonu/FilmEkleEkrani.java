package filmotomasyonu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;
import java.util.Map;
import java.util.HashMap;
import java.net.URL;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import org.json.JSONException;

public class FilmEkleEkrani extends JFrame {
    private FilmYonetici filmYonetici;

    private JTextField adField;
    private JComboBox<String> turComboBox;
    private JTextField yonetmenField;
    private JComboBox<Integer> yilComboBox;
    private JTextField sureField;
    private JTextField imdbPuaniField;
    private JButton apiGetirButton;
    private JLabel posterLabel;
    private JCheckBox izlendiCheckBox; 
    private JButton kaydetButton, iptalButton;

    private String OMDB_API_KEY;

    private void loadApiKey() {
        Properties prop = new Properties();
        try (InputStream input = new FileInputStream("config.properties")) {
            prop.load(input);
            OMDB_API_KEY = prop.getProperty("OMDB_API_KEY");
        } catch (IOException ex) {
            ex.printStackTrace();
            // Gerçek bir uygulamada burada daha iyi bir hata yönetimi yapılmalıdır.
            JOptionPane.showMessageDialog(this, "API anahtarı yüklenemedi. Lütfen config.properties dosyasını kontrol edin.", "Yapılandırma Hatası", JOptionPane.ERROR_MESSAGE);
        }
    }
    private final String[] turlerListesi = {"Macera", "Aksiyon", "Romantik", "Komedi", "Dram", "Bilim Kurgu", "Korku", "Gerilim", "Fantastik", "Animasyon", "Belgesel", "Aile", "Suç", "Gizem", "Tarih", "Müzikal", "Savaş", "Western", "Biyografi", "Spor"};

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
    }

    private String apiGelenPosterUrl = "";

    public FilmEkleEkrani(FilmYonetici filmYonetici) {
        this.filmYonetici = filmYonetici;
        initComponents(); 
    }

    public FilmEkleEkrani(FilmYonetici filmYonetici, Map<String, Object> onBilgiler) {
        this(filmYonetici); 

        if (onBilgiler != null) {
            if (adField != null) adField.setText((String) onBilgiler.getOrDefault("ad", ""));
            if (yilComboBox != null && onBilgiler.containsKey("yil")) {
                Object yilDegeri = onBilgiler.get("yil");
                if (yilDegeri instanceof Integer) yilComboBox.setSelectedItem(yilDegeri);
                else if (yilDegeri != null) {
                     try { yilComboBox.setSelectedItem(Integer.valueOf(yilDegeri.toString())); } 
                     catch (NumberFormatException e) { System.err.println("FilmEkleEkrani (ön doldurma): Yıl parse edilemedi: " + yilDegeri); }
                }
            }
            if (imdbPuaniField != null) imdbPuaniField.setText((String) onBilgiler.getOrDefault("imdbPuani", ""));
            
            String posterUrl = (String) onBilgiler.get("posterUrl");
            if (posterUrl != null && !posterUrl.isEmpty()) {
                this.apiGelenPosterUrl = posterUrl; 
                if (posterLabel != null) gosterFilmPosteri(posterUrl); 
            }
            if (izlendiCheckBox != null && onBilgiler.containsKey("izlendiMi")) {
                 izlendiCheckBox.setSelected((Boolean) onBilgiler.getOrDefault("izlendiMi", false));
            } else if (izlendiCheckBox != null) {
                izlendiCheckBox.setSelected(false); 
            }
        }
    }
    
    private void initComponents(){
        setTitle("Film Ekle");
        setSize(680, 600); 
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel anaPanel = new JPanel(new BorderLayout());
        anaPanel.setBackground(new Color(46, 204, 113));

        JPanel baslikPanel = new JPanel();
        baslikPanel.setBackground(new Color(39, 174, 96));
        baslikPanel.setPreferredSize(new Dimension(getWidth(), 50));
        JLabel baslikLabelText = new JLabel("Yeni Film Ekle");
        baslikLabelText.setFont(new Font("Arial", Font.BOLD, 20));
        baslikLabelText.setForeground(Color.WHITE);
        baslikPanel.add(baslikLabelText);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(46, 204, 113));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Satır 0: Film Adı ve API Getir Butonu
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createWhiteLabel("Film Adı:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7; gbc.fill = GridBagConstraints.HORIZONTAL;
        adField = new JTextField(15); formPanel.add(adField, gbc);
        gbc.gridx = 2; gbc.weightx = 0.3; gbc.fill = GridBagConstraints.HORIZONTAL;
        apiGetirButton = createStyledButton("API Getir", 110, 28);
        apiGetirButton.setToolTipText("Film adını girdikten sonra bilgileri OMDb API'den çekin.");
        apiGetirButton.addActionListener(e -> filmBilgileriniApiIleGetir());
        formPanel.add(apiGetirButton, gbc);

        // Satır 1: Tür
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createWhiteLabel("Tür:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        turComboBox = new JComboBox<>(turlerListesi); formPanel.add(turComboBox, gbc);
        gbc.gridwidth = 1;

        // Satır 2: Yönetmen
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createWhiteLabel("Yönetmen:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        yonetmenField = new JTextField(20); formPanel.add(yonetmenField, gbc);
        gbc.gridwidth = 1;

        // Satır 3: Yayın Yılı
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createWhiteLabel("Yayın Yılı:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        DefaultComboBoxModel<Integer> yilModel = new DefaultComboBoxModel<>();
        int enDusukYil = 1950; int enYuksekYil = 2025;
        for (int i = enYuksekYil; i >= enDusukYil; i--) yilModel.addElement(i);
        yilComboBox = new JComboBox<>(yilModel); formPanel.add(yilComboBox, gbc);
        gbc.gridwidth = 1;

        // Satır 4: Süre
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createWhiteLabel("Süre (dk):"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        sureField = new JTextField(20); formPanel.add(sureField, gbc);
        gbc.gridwidth = 1;

        // Satır 5: Puan
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createWhiteLabel("Puan:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        imdbPuaniField = new JTextField(20); formPanel.add(imdbPuaniField, gbc);
        gbc.gridwidth = 1;
        
        // Satır 6: İzlenme Durumu CheckBox
        gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createWhiteLabel("Durum:"), gbc); // Etiket "İzlendi mi?" yerine "Durum:" olabilir.
        gbc.gridx = 1; gbc.gridy = 6; gbc.gridwidth = 2; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        izlendiCheckBox = new JCheckBox("İzlendi olarak işaretle");
        izlendiCheckBox.setBackground(formPanel.getBackground()); 
        izlendiCheckBox.setForeground(Color.WHITE);              
        izlendiCheckBox.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(izlendiCheckBox, gbc);
        gbc.gridwidth = 1;

        // Satır 7: Poster Alanı
        gbc.gridx = 0; gbc.gridy = 7; 
        gbc.gridwidth = 3; gbc.weightx = 1.0; gbc.weighty = 0.0; 
        gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.CENTER; 
        posterLabel = new JLabel("Film Posteri", SwingConstants.CENTER);
        posterLabel.setPreferredSize(new Dimension(170, 170)); 
        posterLabel.setMinimumSize(new Dimension(120, 120));   
        posterLabel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        posterLabel.setForeground(Color.DARK_GRAY);
        posterLabel.setOpaque(true);
        posterLabel.setBackground(Color.LIGHT_GRAY);
        formPanel.add(posterLabel, gbc);

        gbc.gridwidth = 1; gbc.weighty = 0.0; 
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.anchor = GridBagConstraints.WEST; 

        JPanel butonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        butonPanel.setBackground(new Color(46, 204, 113));
        butonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));

        kaydetButton = createStyledButton("Kaydet", 100, 30);
        kaydetButton.addActionListener(e -> filmEkle());
        butonPanel.add(kaydetButton);

        iptalButton = createStyledButton("İptal", 100, 30);
        iptalButton.addActionListener(e -> dispose());
        butonPanel.add(iptalButton);

        anaPanel.add(baslikPanel, BorderLayout.NORTH);
        anaPanel.add(formPanel, BorderLayout.CENTER);
        anaPanel.add(butonPanel, BorderLayout.SOUTH);
        add(anaPanel);
    }

    private JLabel createWhiteLabel(String text) {  
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        return label;
    }
    private JButton createStyledButton(String text, int width, int height) { 
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(width, height));
        button.setBackground(Color.BLACK);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.PLAIN, 12));
         button.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { if(button.isEnabled()) button.setBackground(Color.RED); }
            @Override public void mouseExited(MouseEvent e) { if(button.isEnabled()) button.setBackground(Color.BLACK); }
        });
        return button;
    }
    private void gosterFilmPosteri(String posterUrlString) { 
        if (posterLabel == null) return;
        if (posterUrlString == null || posterUrlString.trim().isEmpty() || "N/A".equalsIgnoreCase(posterUrlString)) {
            posterLabel.setIcon(null);
            posterLabel.setText("Poster URL geçersiz.");
            posterLabel.setBackground(Color.LIGHT_GRAY);
            return;
        }
        posterLabel.setIcon(null);
        posterLabel.setText("Poster yükleniyor...");
        posterLabel.setBackground(Color.LIGHT_GRAY);

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
                        int boxWidth = 160, boxHeight = 160;
                        if (posterLabel.getPreferredSize() != null) {
                             boxWidth = posterLabel.getPreferredSize().width -10;
                             boxHeight = posterLabel.getPreferredSize().height -10;
                             if(boxWidth <=0) boxWidth = 160; if(boxHeight <=0) boxHeight = 160;
                        }
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
                } catch (IOException e) { System.err.println("Poster yükleme hatası (FilmEkle): " + e.getMessage()); }
                return null;
            }
            @Override
            protected void done() {
                if (posterLabel == null) return;
                try {
                    ImageIcon imageIcon = get();
                    if (imageIcon != null && imageIcon.getImage() != null) {
                        posterLabel.setIcon(imageIcon); posterLabel.setText(""); 
                        posterLabel.setBackground(UIManager.getColor("Label.background")); 
                    } else {
                        posterLabel.setIcon(null); posterLabel.setText("Poster yüklenemedi.");
                        posterLabel.setBackground(Color.LIGHT_GRAY);
                    }
                } catch (InterruptedException | ExecutionException e) {
                    posterLabel.setIcon(null); posterLabel.setText("Poster gösterilemedi.");
                    posterLabel.setBackground(Color.LIGHT_GRAY);                 }
            }
        };
        worker.execute();
    }
    private void filmBilgileriniApiIleGetir() { 
        String filmAdi = adField.getText().trim();
        if (filmAdi.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen API'den bilgi çekmek için bir film adı girin.", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }
        apiGelenPosterUrl = "";
        if(posterLabel != null) {
            posterLabel.setIcon(null);
            posterLabel.setText("Film Posteri"); 
            posterLabel.setBackground(Color.LIGHT_GRAY); 
        }

        try {
            String encodedFilmAdi = URLEncoder.encode(filmAdi, StandardCharsets.UTF_8.toString());
            String apiUrl = "http://www.omdbapi.com/?t=" + encodedFilmAdi + "&apikey=" + OMDB_API_KEY + "&plot=short";
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl)).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONObject jsonResponse = new JSONObject(response.body());
                if (jsonResponse.has("Response") && jsonResponse.getString("Response").equalsIgnoreCase("True")) {
                    yonetmenField.setText(jsonResponse.optString("Director", "").equals("N/A") ? "" : jsonResponse.optString("Director", ""));
                    imdbPuaniField.setText(jsonResponse.optString("imdbRating", "").equals("N/A") ? "" : jsonResponse.optString("imdbRating", ""));
                    String yilStr = jsonResponse.optString("Year", "");
                    if (!yilStr.isEmpty() && !yilStr.equals("N/A")) {
                        try {
                            String ilkYil = yilStr.split("–")[0].replaceAll("[^0-9]", "");
                             if (ilkYil.length() >= 4) {
                                yilComboBox.setSelectedItem(Integer.parseInt(ilkYil.substring(0,4)));
                            }
                        } catch (NumberFormatException ex) { System.err.println("API'den gelen yıl parse edilemedi: " + yilStr); }
                    }
                    String genreApi = jsonResponse.optString("Genre", "");
                    if (!genreApi.isEmpty() && !genreApi.equals("N/A")) {
                        String[] apiGelenIngilizceTurler = genreApi.split(",\\s*");
                        boolean turEslesmesiBulundu = false;
                        for (String ingilizceApiTuru : apiGelenIngilizceTurler) {
                            String temizlenmisIngilizceApiTuru = ingilizceApiTuru.trim();
                            String turkceKarsiligi = null;
                            for (Map.Entry<String, String> entry : GENRE_MAP_EN_TR.entrySet()) {
                                if (entry.getKey().equalsIgnoreCase(temizlenmisIngilizceApiTuru)) {
                                    turkceKarsiligi = entry.getValue(); break;
                                }
                            }
                            if (turkceKarsiligi != null) {
                                for (String comboBoxTuru : turlerListesi) {
                                    if (comboBoxTuru.equals(turkceKarsiligi)) {
                                        turComboBox.setSelectedItem(comboBoxTuru);
                                        turEslesmesiBulundu = true; break;
                                    }
                                }
                            }
                            if (turEslesmesiBulundu) break;
                        }
                        if (!turEslesmesiBulundu) { System.out.println("API'den gelen İngilizce türler (" + genreApi + ") için Türkçe eşleşme veya ComboBox öğesi bulunamadı."); }
                    }
                    String runtimeApi = jsonResponse.optString("Runtime", "");
                    if (!runtimeApi.isEmpty() && !runtimeApi.equals("N/A")) {
                        sureField.setText(runtimeApi.replaceAll("[^0-9]", ""));
                    }
                    String posterUrlApi = jsonResponse.optString("Poster", "");
                    if (!posterUrlApi.isEmpty() && !posterUrlApi.equals("N/A")) {
                        this.apiGelenPosterUrl = posterUrlApi;
                        gosterFilmPosteri(posterUrlApi);
                    } else {
                        this.apiGelenPosterUrl = "";
                        if(posterLabel != null) {
                            posterLabel.setIcon(null); posterLabel.setText("Poster bulunamadı.");
                            posterLabel.setBackground(Color.LIGHT_GRAY);
                        }
                    }
                     JOptionPane.showMessageDialog(this, "Film bilgileri API'den başarıyla çekildi!\nKaydetmeden önce kontrol edebilirsiniz.", "Bilgi", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    String errorMessage = jsonResponse.optString("Error", "Bilinmeyen bir hata oluştu.");
                    JOptionPane.showMessageDialog(this, "Film bulunamadı veya API hatası: " + errorMessage, "API Hatası", JOptionPane.ERROR_MESSAGE);
                    this.apiGelenPosterUrl = "";
                     if(posterLabel != null) {
                        posterLabel.setIcon(null); posterLabel.setText("Film Posteri");
                        posterLabel.setBackground(Color.LIGHT_GRAY);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "API'ye ulaşılamadı. HTTP Kodu: " + response.statusCode(), "Bağlantı Hatası", JOptionPane.ERROR_MESSAGE);
            }
        } catch (HeadlessException | IOException | InterruptedException | JSONException e) { 
            JOptionPane.showMessageDialog(this, "API isteği sırasında bir hata oluştu: " + e.getMessage(), "Genel Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filmEkle() {
        try {
            String ad = adField.getText().trim();
            String tur = (String) turComboBox.getSelectedItem();
            String yonetmen = yonetmenField.getText().trim();
            int yayinYili = (Integer) yilComboBox.getSelectedItem();
            int sure = 0;
            if (!sureField.getText().trim().isEmpty()){
                 sure = Integer.parseInt(sureField.getText().trim());
            }
            double imdbPuani = 0.0;
            if (!imdbPuaniField.getText().trim().isEmpty()){
                imdbPuani = Double.parseDouble(imdbPuaniField.getText().trim());
            }
            String posterToSave = apiGelenPosterUrl;
            boolean izlendi = izlendiCheckBox.isSelected(); 

            if (ad.isEmpty() || yonetmen.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Film Adı ve Yönetmen boş bırakılamaz!", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (tur == null) {
                 JOptionPane.showMessageDialog(this, "Lütfen bir tür seçin!", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!sureField.getText().trim().isEmpty() && sure <= 0) {
                JOptionPane.showMessageDialog(this, "Süre (eğer girildiyse) pozitif bir değer olmalıdır!", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!imdbPuaniField.getText().trim().isEmpty() && (imdbPuani < 0 || imdbPuani > 10)) {
                JOptionPane.showMessageDialog(this, "Puan (eğer girildiyse) 0-10 arasında olmalıdır!", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Film yeniFilm = new Film(0, ad, tur, yonetmen, yayinYili, sure, imdbPuani, posterToSave, izlendi);
            Film eklenenFilm = filmYonetici.filmEkle(yeniFilm); 

            if (eklenenFilm != null) { 
                JOptionPane.showMessageDialog(this, "Film başarıyla eklendi!", "Bilgi", JOptionPane.INFORMATION_MESSAGE);
                adField.setText(""); turComboBox.setSelectedIndex(0);
                yonetmenField.setText(""); yilComboBox.setSelectedIndex(0);
                sureField.setText(""); imdbPuaniField.setText("");
                izlendiCheckBox.setSelected(false); 
                apiGelenPosterUrl = ""; 
                if(posterLabel != null) {
                    posterLabel.setIcon(null); posterLabel.setText("Film Posteri");
                    posterLabel.setBackground(Color.LIGHT_GRAY);
                }
            } else { 
                JOptionPane.showMessageDialog(this,
                        "'" + ad + "' (" + yayinYili + ") adlı film zaten kütüphanede mevcut!",
                        "Duplike Film", JOptionPane.WARNING_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Lütfen süre ve puan için sayısal değerleri doğru formatta girin (veya boş bırakın)!", "Hata", JOptionPane.ERROR_MESSAGE);
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(this, "Film eklenirken bir hata oluştu: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}