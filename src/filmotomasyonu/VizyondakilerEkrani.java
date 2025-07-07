package filmotomasyonu;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap; 
import java.util.List;
import java.util.Map; 
import java.util.concurrent.ExecutionException;


class LeftPaddedCellRenderer_Vizyon extends DefaultTableCellRenderer {
    private final int leftPadding;
    public LeftPaddedCellRenderer_Vizyon(int padding) { this.leftPadding = padding; }
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                 boolean isSelected, boolean hasFocus,
                                                 int row, int column) {
        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        label.setBorder(BorderFactory.createEmptyBorder(0, leftPadding, 0, 0));
        return label;
    }
}

public class VizyondakilerEkrani extends JFrame {
    private final FilmYonetici filmYonetici;
    private JTable filmTable;
    private DefaultTableModel tableModel;
    private final HttpClient httpClient;
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
    private final List<JSONObject> sonCekilenFilmJsonListesi = new ArrayList<>();

    // GENRE_ID_MAP_TR_LOCAL 
    private static final Map<Integer, String> GENRE_ID_MAP_TR_LOCAL;
    static {
        GENRE_ID_MAP_TR_LOCAL = new HashMap<>();
        GENRE_ID_MAP_TR_LOCAL.put(28, "Aksiyon"); GENRE_ID_MAP_TR_LOCAL.put(12, "Macera");
        GENRE_ID_MAP_TR_LOCAL.put(16, "Animasyon"); GENRE_ID_MAP_TR_LOCAL.put(35, "Komedi");
        GENRE_ID_MAP_TR_LOCAL.put(80, "Suç"); GENRE_ID_MAP_TR_LOCAL.put(99, "Belgesel");
        GENRE_ID_MAP_TR_LOCAL.put(18, "Dram"); GENRE_ID_MAP_TR_LOCAL.put(10751, "Aile");
        GENRE_ID_MAP_TR_LOCAL.put(14, "Fantastik"); GENRE_ID_MAP_TR_LOCAL.put(36, "Tarih");
        GENRE_ID_MAP_TR_LOCAL.put(27, "Korku"); GENRE_ID_MAP_TR_LOCAL.put(10402, "Müzikal");
        GENRE_ID_MAP_TR_LOCAL.put(9648, "Gizem"); GENRE_ID_MAP_TR_LOCAL.put(10749, "Romantik");
        GENRE_ID_MAP_TR_LOCAL.put(878, "Bilim Kurgu"); GENRE_ID_MAP_TR_LOCAL.put(10770, "TV Filmi");
        GENRE_ID_MAP_TR_LOCAL.put(53, "Gerilim"); GENRE_ID_MAP_TR_LOCAL.put(10752, "Savaş");
        GENRE_ID_MAP_TR_LOCAL.put(37, "Western");
    }

    public VizyondakilerEkrani(FilmYonetici filmYonetici) {
        this.filmYonetici = filmYonetici;
        this.httpClient = HttpClient.newHttpClient();
        initComponents();
        vizyondakiFilmleriGetirVeGoster();
    }

    private void initComponents() {
        setTitle("Vizyondaki Filmler (TMDb API)");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel anaPanel = new JPanel(new BorderLayout(10, 10));
        anaPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        anaPanel.setBackground(new Color(60, 63, 65));

        JPanel baslikPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        baslikPanel.setBackground(new Color(41, 128, 185));
        JLabel baslikLabel = new JLabel("Şu An Vizyonda Olan Filmler");
        baslikLabel.setFont(new Font("Arial", Font.BOLD, 22));
        baslikLabel.setForeground(Color.WHITE);
        baslikPanel.add(baslikLabel);
        anaPanel.add(baslikPanel, BorderLayout.NORTH);

        String[] kolonAdlari = {"Film Adı", "Tür", "Vizyon Tarihi", "Puan"};
        tableModel = new DefaultTableModel(kolonAdlari, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        filmTable = new JTable(tableModel);
        filmTable.setFillsViewportHeight(true);
        filmTable.setRowHeight(25);
        filmTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        filmTable.getTableHeader().setReorderingAllowed(false);
        filmTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        filmTable.getTableHeader().setBackground(new Color(41, 128, 185));
        filmTable.getTableHeader().setForeground(Color.BLACK);

        TableColumn filmAdiKolonu = filmTable.getColumnModel().getColumn(0);
        filmAdiKolonu.setCellRenderer(new LeftPaddedCellRenderer_Vizyon(10));

        filmTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = filmTable.getSelectedRow();
                    if (selectedRow != -1 && selectedRow < sonCekilenFilmJsonListesi.size()) {
                        JSONObject secilenFilmJson = sonCekilenFilmJsonListesi.get(selectedRow);
                        int tmdbId = secilenFilmJson.getInt("id");
                        String filmAdi = secilenFilmJson.getString("title");
                        String vizyonTarihi = secilenFilmJson.optString("release_date", "Bilinmiyor");
                        double tmdbPuani = secilenFilmJson.optDouble("vote_average", 0.0);
                        String ozet = secilenFilmJson.optString("overview", "Açıklama bulunamadı.");
                        String posterPath = secilenFilmJson.optString("poster_path", null);

                        VizyonFilmDetayDialog detayDialog = new VizyonFilmDetayDialog(
                                VizyondakilerEkrani.this, filmYonetici, tmdbId, filmAdi,
                                posterPath, vizyonTarihi, tmdbPuani, ozet);
                        detayDialog.setVisible(true);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(filmTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        anaPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel butonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        butonPanel.setBackground(new Color(60, 63, 65));
        JButton yenileButton = createStyledButton("Listeyi Yenile");
        yenileButton.addActionListener(e -> vizyondakiFilmleriGetirVeGoster());
        JButton kapatButton = createStyledButton("Kapat");
        kapatButton.addActionListener(e -> dispose());
        butonPanel.add(yenileButton);
        butonPanel.add(kapatButton);
        anaPanel.add(butonPanel, BorderLayout.SOUTH);

        add(anaPanel);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(Color.BLACK);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(150, 35));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { if (button.isEnabled()) button.setBackground(Color.RED); }
            @Override
            public void mouseExited(MouseEvent e) { if (button.isEnabled()) button.setBackground(Color.BLACK); }
        });
        return button;
    }

    private void vizyondakiFilmleriGetirVeGoster() {
        tableModel.setRowCount(0);
        sonCekilenFilmJsonListesi.clear();

        SwingWorker<List<Object[]>, String> worker;
        worker = new SwingWorker<List<Object[]>, String>() {
            @Override
            protected List<Object[]> doInBackground() throws Exception {
                List<Object[]> filmlerData = new ArrayList<>();
                String url = "https://api.themoviedb.org/3/movie/now_playing?api_key=" + TMDB_API_KEY + "&language=tr-TR&region=TR&page=1";
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    JSONObject jsonResponse = new JSONObject(response.body());
                    JSONArray results = jsonResponse.optJSONArray("results");
                    if (results == null) {
                        publish("API yanıtında 'results' alanı bulunamadı.");
                        return filmlerData;
                    }

                    for (int i = 0; i < results.length(); i++) {
                        JSONObject filmJson = results.optJSONObject(i);
                        if (filmJson == null) continue;
                        sonCekilenFilmJsonListesi.add(filmJson);

                        String turAdi = "Bilinmiyor";
                        JSONArray genreIds = filmJson.optJSONArray("genre_ids");
                        if (genreIds != null && genreIds.length() > 0) {
                            int ilkTurId = genreIds.optInt(0, -1);
                            
                            if (ilkTurId != -1 && GENRE_ID_MAP_TR_LOCAL.containsKey(ilkTurId)) {
                                turAdi = GENRE_ID_MAP_TR_LOCAL.get(ilkTurId);
                            } else if (ilkTurId != -1) {
                                System.out.println("Vizyondakiler: Tür ID (" + ilkTurId + ") için GENRE_ID_MAP_TR_LOCAL haritasında eşleşme yok.");
                            }
                        }

                        Object[] row = new Object[]{
                            filmJson.optString("title", "Başlık Yok"),
                            turAdi,
                            filmJson.optString("release_date", "Bilinmiyor"),
                            String.format("%.1f", filmJson.optDouble("vote_average", 0.0)).replace(",", ".")
                        };
                        filmlerData.add(row);
                    }
                } else {
                    publish("API'den yanıt alınamadı. HTTP Kodu: " + response.statusCode() + "\nYanıt: " + response.body());
                }
                return filmlerData;
            }

            @Override
            protected void process(List<String> chunks) {
                for (String message : chunks) {
                    JOptionPane.showMessageDialog(VizyondakilerEkrani.this, message, "API İsteği Bilgisi", JOptionPane.WARNING_MESSAGE);
                }
            }

            @Override
            protected void done() {
                try {
                    List<Object[]> filmlerData = get();
                    for (Object[] rowData : filmlerData) {
                        tableModel.addRow(rowData);
                    }
                    if (tableModel.getRowCount() == 0 && !isCancelled()) {
                        
                        if (filmlerData.isEmpty()) {
                            JOptionPane.showMessageDialog(VizyondakilerEkrani.this,
                                    "Vizyonda film bulunamadı veya API'den veri çekilemedi.",
                                    "Bilgi", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                } catch (HeadlessException | InterruptedException | ExecutionException e) {
                    String hataMesaji = "Filmler çekilirken bir hata oluştu.";
                    Throwable cause = e.getCause();
                    if (cause != null) {
                        hataMesaji = cause.getMessage();
                        if (cause instanceof JSONException) {
                            hataMesaji = "API yanıtı işlenirken bir sorun oluştu (JSON). " + cause.getMessage();
                        } else if (cause instanceof IOException) {
                            hataMesaji = "API'ye bağlanırken/veri alırken sorun (Ağ). " + cause.getMessage();
                        }
                    } else if (e.getMessage() != null) {
                        hataMesaji += "\nDetay: " + e.getMessage();
                    }
                    JOptionPane.showMessageDialog(VizyondakilerEkrani.this, hataMesaji, "API Hatası", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
}