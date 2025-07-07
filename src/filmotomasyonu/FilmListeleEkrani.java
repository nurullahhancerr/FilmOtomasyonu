package filmotomasyonu;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer; 
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class FilmListeleEkrani extends JFrame {
    private FilmYonetici filmYonetici;
    private final JTable filmTable;
    private DefaultTableModel tableModel;
    private List<Film> gosterilenFilmlerListesi = new ArrayList<>(); 

    public FilmListeleEkrani(FilmYonetici filmYonetici) {
        this.filmYonetici = filmYonetici;

        setTitle("Film Listesi");
        setSize(900, 600); 
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel anaPanel = new JPanel(new BorderLayout());
        anaPanel.setBackground(new Color(52, 152, 219));

        JPanel baslikPanel = new JPanel();
        baslikPanel.setBackground(new Color(41, 128, 185));
        baslikPanel.setPreferredSize(new Dimension(getWidth(), 50));
        JLabel baslikLabel = new JLabel("Film Listesi");
        baslikLabel.setFont(new Font("Arial", Font.BOLD, 20));
        baslikLabel.setForeground(Color.WHITE);
        baslikPanel.add(baslikLabel);

        String[] kolonlar = {"ID", "Film Adı", "Tür", "Yönetmen", "Yayın Yılı", "Süre (dk)", "Puan", "İzlenme Durumu"};
        tableModel = new DefaultTableModel(kolonlar, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        filmTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) { 
                    if (gosterilenFilmlerListesi != null && row < gosterilenFilmlerListesi.size()) {
                        Film film = gosterilenFilmlerListesi.get(row);
                        if (film.isIzlendiMi()) {
                            c.setBackground(new Color(200, 255, 200)); // İzlenmişler için açık yeşil
                        } else {
                            c.setBackground(new Color(255, 220, 220)); // İZLENMEMİŞLER İÇİN AÇIK KIRMIZI/PEMBE
                        }
                        c.setForeground(Color.BLACK); 
                    } else {
                         c.setBackground(Color.WHITE); // Veri yoksa varsayılan
                         c.setForeground(Color.BLACK);
                    }
                }
                
                return c;
            }
        };
        
        filmTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        filmTable.setRowHeight(25);
        filmTable.getTableHeader().setReorderingAllowed(false);
        filmTable.getTableHeader().setBackground(new Color(41, 128, 185));
        filmTable.getTableHeader().setForeground(Color.BLACK);
        filmTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        
         
        

        JScrollPane scrollPane = new JScrollPane(filmTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        filmTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { 
                    handleFilmDoubleClick();
                }
            }
        });

        JPanel butonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        butonPanel.setBackground(new Color(52, 152, 219));
        butonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JButton yenileButton = createStyledButton("Listeyi Yenile");
        yenileButton.addActionListener(e -> filmleriListele());

        JButton kapatButton = createStyledButton("Kapat");
        kapatButton.addActionListener(e -> dispose());

        butonPanel.add(yenileButton);
        butonPanel.add(kapatButton);

        anaPanel.add(baslikPanel, BorderLayout.NORTH);
        anaPanel.add(scrollPane, BorderLayout.CENTER);
        anaPanel.add(butonPanel, BorderLayout.SOUTH);
        add(anaPanel);
        filmleriListele();
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(Color.BLACK);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(150, 30));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { if (button.isEnabled()) button.setBackground(Color.RED); }
            @Override
            public void mouseExited(MouseEvent e) { if (button.isEnabled()) button.setBackground(Color.BLACK); }
        });
        return button;
    }
    
    private void filmleriListele() {
        tableModel.setRowCount(0); 
        gosterilenFilmlerListesi = filmYonetici.tumFilmler(); 

        for (Film film : gosterilenFilmlerListesi) {
            Object[] row = {
                film.getId(), film.getAd(), film.getTur(), film.getYonetmen(),
                film.getYayinYili(), film.getSure(), film.getImdbPuani(),
                film.isIzlendiMi() ? "Evet" : "Hayır" 
            };
            tableModel.addRow(row);
        }
    }

    private void handleFilmDoubleClick() {
        int selectedRow = filmTable.getSelectedRow();
        if (selectedRow == -1 || gosterilenFilmlerListesi == null || selectedRow >= gosterilenFilmlerListesi.size()) {
            return; 
        }
        Film secilenFilm = gosterilenFilmlerListesi.get(selectedRow); // Doğrudan listeden al

        if (secilenFilm == null) {
            JOptionPane.showMessageDialog(this, "Film bulunamadı!", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String filmAdi = secilenFilm.getAd();
        String mevcutDurum = secilenFilm.isIzlendiMi() ? "İzlendi" : "İzlenmedi";
        Object[] secenekler = {"İzlendi Yap", "İzlenmedi Yap", "Tüm Detayları Göster", "İptal"};
        String mesaj = "'" + filmAdi + "' filmi için işlem seçin.\nŞu anki durum: " + mevcutDurum;
        
        int sonuc = JOptionPane.showOptionDialog(this, mesaj, "Film İşlemleri",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, secenekler, secenekler[3]);

        boolean durumDegisti = false;
        switch (sonuc) {
            case 0 -> {
                if (!secilenFilm.isIzlendiMi()) {
                    secilenFilm.setIzlendiMi(true); durumDegisti = true;
                }
            }
            case 1 -> {
                if (secilenFilm.isIzlendiMi()) {
                    secilenFilm.setIzlendiMi(false); durumDegisti = true;
                }
            }
            case 2 -> {
                eskiFilmDetayGosterSecilen(secilenFilm);
                return;
            }
            default -> {
                return;
            }
        }

        if (durumDegisti) {
            boolean guncellemeSonucu = filmYonetici.filmGuncelle(secilenFilm);
            if (guncellemeSonucu) {
                filmleriListele(); 
            } else {
                JOptionPane.showMessageDialog(this, "İzlenme durumu güncellenirken bir hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void eskiFilmDetayGosterSecilen(Film film) {
        
        if (film == null) {
             int selectedRow = filmTable.getSelectedRow();
             if (selectedRow == -1) {
                 JOptionPane.showMessageDialog(this, "Lütfen bir film seçin!", "Uyarı", JOptionPane.WARNING_MESSAGE);
                 return;
             }
             
             int filmId = (int) tableModel.getValueAt(selectedRow, 0);
             film = filmYonetici.filmBul(filmId);
             if (film == null) {
                 JOptionPane.showMessageDialog(this, "Film bulunamadı!", "Hata", JOptionPane.ERROR_MESSAGE);
                 return;
             }
        }
        
        String izlenmeDurumuMetni = film.isIzlendiMi() ? "Evet" : "Hayır";
        String detayMesaji = 
            "Film ID: " + film.getId() + "\n" +
            "Film Adı: " + film.getAd() + "\n" +
            "Tür: " + film.getTur() + "\n" +
            "Yönetmen: " + film.getYonetmen() + "\n" +
            "Yayın Yılı: " + film.getYayinYili() + "\n" +
            "Süre: " + film.getSure() + " dakika\n" +
            "Puan: " + film.getImdbPuani() + "\n" +
            "İzlenme Durumu: " + izlenmeDurumuMetni + 
            (film.getPosterUrl() != null && !film.getPosterUrl().isEmpty() ? "\nPoster URL: " + film.getPosterUrl() : "");

        JTextArea textArea = new JTextArea(detayMesaji);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setMargin(new Insets(10, 10, 10, 10));
        
        JScrollPane scrollPaneDetay = new JScrollPane(textArea);
        scrollPaneDetay.setPreferredSize(new Dimension(500, 320)); // Boyut 
        
        JOptionPane.showMessageDialog(this, scrollPaneDetay, film.getAd() + " - Detayları", JOptionPane.INFORMATION_MESSAGE);
    }
}