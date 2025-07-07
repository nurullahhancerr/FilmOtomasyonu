package filmotomasyonu;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class FilmAraEkrani extends JFrame {
    private FilmYonetici filmYonetici;
    private JTable filmTable;
    private DefaultTableModel tableModel;

    private JTextField aramaField;
    private JComboBox<String> turComboBox;
    private JComboBox<Integer> yilComboBox;
    private JTextField minPuanField;
    private JTextField maxPuanField;

    private final String[] guncelTurlerListesi = {"Macera", "Aksiyon", "Romantik", "Komedi", "Dram", "Bilim Kurgu", "Korku", "Gerilim", "Fantastik", "Animasyon", "Belgesel", "Aile", "Suç", "Gizem", "Tarih", "Müzikal", "Savaş", "Western", "Biyografi", "Spor"};
    private List<Film> gosterilenFilmlerListesi = new ArrayList<>();

    public FilmAraEkrani(FilmYonetici filmYonetici) {
        this.filmYonetici = filmYonetici;
        initComponents();
    }

    private void initComponents() {
        setTitle("Film Ara ve Filtrele");
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel anaPanel = new JPanel(new BorderLayout());
        anaPanel.setBackground(new Color(155, 89, 182));

        JPanel baslikPanel = new JPanel();
        baslikPanel.setBackground(new Color(142, 68, 173));
        baslikPanel.setPreferredSize(new Dimension(getWidth(), 50));
        JLabel baslikLabel = new JLabel("Film Ara ve Filtrele");
        baslikLabel.setFont(new Font("Arial", Font.BOLD, 20));
        baslikLabel.setForeground(Color.WHITE);
        baslikPanel.add(baslikLabel);

        JPanel aramaPanel = new JPanel(new GridBagLayout());
        aramaPanel.setBackground(new Color(155, 89, 182));
        aramaPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.1; gbc.fill = GridBagConstraints.NONE;
        JLabel aramaLabelText = new JLabel("Film Adı:");
        aramaLabelText.setForeground(Color.WHITE);
        aramaPanel.add(aramaLabelText, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7; gbc.fill = GridBagConstraints.HORIZONTAL;
        aramaField = new JTextField(15);
        aramaPanel.add(aramaField, gbc);
        gbc.gridx = 2; gbc.weightx = 0.2; gbc.fill = GridBagConstraints.HORIZONTAL;
        JButton araButton = createStyledButton("Ara");
        araButton.setPreferredSize(new Dimension(160, 28));
        araButton.addActionListener(e -> filmAra());
        aramaPanel.add(araButton, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.1; gbc.fill = GridBagConstraints.NONE;
        JLabel turLabelText = new JLabel("Tür:");
        turLabelText.setForeground(Color.WHITE);
        aramaPanel.add(turLabelText, gbc);
        gbc.gridx = 1; gbc.gridwidth = 1; gbc.weightx = 0.7; gbc.fill = GridBagConstraints.HORIZONTAL;
        turComboBox = new JComboBox<>(guncelTurlerListesi);
        aramaPanel.add(turComboBox, gbc);
        gbc.gridwidth = 1; 
        gbc.gridx = 2; gbc.weightx = 0.2; gbc.fill = GridBagConstraints.HORIZONTAL;
        JButton turFiltreButton = createStyledButton("Türe Göre Filtrele");
        turFiltreButton.setPreferredSize(new Dimension(160, 28));
        turFiltreButton.addActionListener(e -> turFiltrele());
        aramaPanel.add(turFiltreButton, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.1; gbc.fill = GridBagConstraints.NONE;
        JLabel yilLabelText = new JLabel("Yayın Yılı:");
        yilLabelText.setForeground(Color.WHITE);
        aramaPanel.add(yilLabelText, gbc);
        gbc.gridx = 1; gbc.gridwidth = 1; gbc.weightx = 0.7; gbc.fill = GridBagConstraints.HORIZONTAL;
        DefaultComboBoxModel<Integer> yilModel = new DefaultComboBoxModel<>();
        int enDusukYil = 1950; int enYuksekYil = 2025;
        for (int i = enYuksekYil; i >= enDusukYil; i--) yilModel.addElement(i);
        yilComboBox = new JComboBox<>(yilModel);
        aramaPanel.add(yilComboBox, gbc);
        gbc.gridwidth = 1; 
        gbc.gridx = 2; gbc.weightx = 0.2; gbc.fill = GridBagConstraints.HORIZONTAL;
        JButton yilFiltreButton = createStyledButton("Yıla Göre Filtrele");
        yilFiltreButton.setPreferredSize(new Dimension(160, 28));
        yilFiltreButton.addActionListener(e -> yilFiltrele());
        aramaPanel.add(yilFiltreButton, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.1; gbc.fill = GridBagConstraints.NONE;
        JLabel puanLabelText = new JLabel("Puan Aralığı:");
        puanLabelText.setForeground(Color.WHITE);
        aramaPanel.add(puanLabelText, gbc);
        JPanel puanPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        puanPanel.setOpaque(false);
        minPuanField = new JTextField(5); puanPanel.add(minPuanField);
        JLabel araLabelPuan = new JLabel("-"); araLabelPuan.setForeground(Color.WHITE); puanPanel.add(araLabelPuan);
        maxPuanField = new JTextField(5); puanPanel.add(maxPuanField);
        gbc.gridx = 1; gbc.weightx = 0.7; gbc.fill = GridBagConstraints.HORIZONTAL;
        aramaPanel.add(puanPanel, gbc);
        gbc.gridx = 2; gbc.weightx = 0.2; gbc.fill = GridBagConstraints.HORIZONTAL;
        JButton puanFiltreButton = createStyledButton("Puana Göre Filtrele");
        puanFiltreButton.setPreferredSize(new Dimension(160, 28));
        puanFiltreButton.addActionListener(e -> puanFiltrele());
        aramaPanel.add(puanFiltreButton, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 3; gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.CENTER; gbc.fill = GridBagConstraints.NONE;
        JButton tumFilmlerButton = createStyledButton("Tüm Filmleri Göster");
        tumFilmlerButton.setPreferredSize(new Dimension(200, 30));
        tumFilmlerButton.addActionListener(e -> tumFilmleriGoster());
        aramaPanel.add(tumFilmlerButton, gbc);
        gbc.gridwidth = 1;

        String[] kolonlar = {"Film Adı", "Tür", "Yönetmen", "Yayın Yılı", "Süre (dk)", "Puan", "İzlenme Durumu"};
        tableModel = new DefaultTableModel(kolonlar, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        filmTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    if (gosterilenFilmlerListesi != null && row < gosterilenFilmlerListesi.size()) {
                        Film film = gosterilenFilmlerListesi.get(row);
                        if (film.isIzlendiMi()) {
                            c.setBackground(new Color(200, 255, 200)); 
                        } else {
                            c.setBackground(new Color(255, 220, 220)); 
                        }
                        c.setForeground(Color.BLACK);
                    } else {
                         c.setBackground(Color.WHITE);
                         c.setForeground(Color.BLACK);
                    }
                }
                return c;
            }
        };
        
        filmTable.setForeground(Color.BLACK); 
        filmTable.setBackground(Color.WHITE); 
        filmTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        filmTable.setRowHeight(25);
        filmTable.getTableHeader().setReorderingAllowed(false);
        filmTable.getTableHeader().setBackground(new Color(41, 128, 185)); 
        filmTable.getTableHeader().setForeground(Color.BLACK);             
        filmTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        // "Film Adı" sütununa (index 0) özel renderer'ı ata - Basit padding ile
        TableColumn filmAdiKolonu = filmTable.getColumnModel().getColumn(0); 
        DefaultTableCellRenderer paddedRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (c instanceof JLabel) {
                    JLabel label = (JLabel) c;
                    label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
                    label.setHorizontalAlignment(SwingConstants.LEFT);
                }
                return c;
            }
        };
        filmAdiKolonu.setCellRenderer(paddedRenderer);

        JScrollPane scrollPane = new JScrollPane(filmTable);
        scrollPane.getViewport().setBackground(Color.WHITE); 
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        filmTable.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) filmDetayGoster();
            }
        });

        JPanel butonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        butonPanel.setBackground(new Color(155, 89, 182));
        butonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        JButton detayButton = createStyledButton("Detayları Göster");
        detayButton.setPreferredSize(new Dimension(160, 30));
        detayButton.addActionListener(e -> filmDetayGoster());
        JButton kapatButton = createStyledButton("Kapat");
        kapatButton.setPreferredSize(new Dimension(100, 30));
        kapatButton.addActionListener(e -> dispose());
        butonPanel.add(detayButton);
        butonPanel.add(kapatButton);

        JPanel ortaPanel = new JPanel(new BorderLayout());
        ortaPanel.setBackground(new Color(155, 89, 182));
        ortaPanel.add(aramaPanel, BorderLayout.NORTH);
        ortaPanel.add(scrollPane, BorderLayout.CENTER);

        anaPanel.add(baslikPanel, BorderLayout.NORTH);
        anaPanel.add(ortaPanel, BorderLayout.CENTER);
        anaPanel.add(butonPanel, BorderLayout.SOUTH);
        add(anaPanel);

        aramaField.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) filmAra();
            }
        });
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(Color.BLACK);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { if(button.isEnabled()) button.setBackground(Color.RED); }
            @Override public void mouseExited(MouseEvent e) { if(button.isEnabled()) button.setBackground(Color.BLACK); }
        });
        return button;
    }
    
    private void filmAra() { 
        String aramaMetni = aramaField.getText().trim(); 
        if (aramaMetni.isEmpty()) { 
            JOptionPane.showMessageDialog(this, "Lütfen bir arama metni girin!", "Uyarı", JOptionPane.WARNING_MESSAGE); 
            return; 
        } 
        List<Film> sonuclar = filmYonetici.filmAra(aramaMetni); 
        tabloyuGuncelle(sonuclar); 
        if (sonuclar.isEmpty()) { 
            JOptionPane.showMessageDialog(this, "Arama kriterine uygun film bulunamadı!", "Bilgi", JOptionPane.INFORMATION_MESSAGE); 
        } 
    }
    
    private void turFiltrele() { 
        String tur = (String) turComboBox.getSelectedItem(); 
        List<Film> sonuclar = filmYonetici.turFiltrele(tur); 
        tabloyuGuncelle(sonuclar); 
        if (sonuclar.isEmpty()) { 
            JOptionPane.showMessageDialog(this, "Bu türde film bulunamadı!", "Bilgi", JOptionPane.INFORMATION_MESSAGE); 
        } 
    }
    
    private void yilFiltrele() { 
        try { 
            int yil = (Integer) yilComboBox.getSelectedItem(); 
            List<Film> sonuclar = filmYonetici.yilFiltrele(yil); 
            tabloyuGuncelle(sonuclar); 
            if (sonuclar.isEmpty()) { 
                JOptionPane.showMessageDialog(this, "Bu yılda film bulunamadı!", "Bilgi", JOptionPane.INFORMATION_MESSAGE); 
            } 
        } catch (Exception e) { 
            JOptionPane.showMessageDialog(this, "Geçerli bir yıl seçin!", "Hata", JOptionPane.ERROR_MESSAGE); 
            e.printStackTrace(); 
        } 
    }
    
    private void puanFiltrele() { 
        try { 
            String minPuanStr = minPuanField.getText().trim(); 
            String maxPuanStr = maxPuanField.getText().trim(); 
            if (minPuanStr.isEmpty() || maxPuanStr.isEmpty()) { 
                JOptionPane.showMessageDialog(this, "Lütfen minimum ve maksimum puanları girin!", "Uyarı", JOptionPane.WARNING_MESSAGE); 
                return; 
            } 
            double minPuan = Double.parseDouble(minPuanStr); 
            double maxPuan = Double.parseDouble(maxPuanStr); 
            if (minPuan < 0 || minPuan > 10 || maxPuan < 0 || maxPuan > 10) { 
                JOptionPane.showMessageDialog(this, "Puanlar 0-10 arasında olmalıdır!", "Uyarı", JOptionPane.WARNING_MESSAGE); 
                return; 
            } 
            if (minPuan > maxPuan) { 
                JOptionPane.showMessageDialog(this, "Minimum puan, maksimum puandan büyük olamaz!", "Uyarı", JOptionPane.WARNING_MESSAGE); 
                return; 
            } 
            List<Film> sonuclar = filmYonetici.puanFiltrele(minPuan, maxPuan); 
            tabloyuGuncelle(sonuclar); 
            if (sonuclar.isEmpty()) { 
                JOptionPane.showMessageDialog(this, "Bu puan aralığında film bulunamadı!", "Bilgi", JOptionPane.INFORMATION_MESSAGE); 
            } 
        } catch (NumberFormatException e) { 
            JOptionPane.showMessageDialog(this, "Geçerli puanlar girin!", "Hata", JOptionPane.ERROR_MESSAGE); 
        } 
    }
    
    private void tumFilmleriGoster() { 
        List<Film> filmler = filmYonetici.tumFilmler(); 
        tabloyuGuncelle(filmler); 
    }

    private void tabloyuGuncelle(List<Film> filmler) { 
        tableModel.setRowCount(0); 
        this.gosterilenFilmlerListesi = new ArrayList<>(filmler); 
        for (Film film : this.gosterilenFilmlerListesi) { 
            Object[] row = { 
                film.getAd(), film.getTur(), film.getYonetmen(),
                film.getYayinYili(), film.getSure(), film.getImdbPuani(), 
                film.isIzlendiMi() ? "Evet" : "Hayır" 
            };
            tableModel.addRow(row);
        }
    }

    private void filmDetayGoster() {
        int selectedRow = filmTable.getSelectedRow();
        if (selectedRow == -1 || gosterilenFilmlerListesi == null || selectedRow >= gosterilenFilmlerListesi.size()) {
            JOptionPane.showMessageDialog(this, "Lütfen bir film seçin!", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Film film = gosterilenFilmlerListesi.get(selectedRow);
        if (film != null) {
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
            textArea.setEditable(false); textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true); textArea.setMargin(new Insets(10, 10, 10, 10));
            JScrollPane scrollPaneDetay = new JScrollPane(textArea); 
            scrollPaneDetay.setPreferredSize(new Dimension(400, 280));
            JOptionPane.showMessageDialog(this, scrollPaneDetay, film.getAd() + " - Film Detayları", JOptionPane.INFORMATION_MESSAGE);
        } else {
             JOptionPane.showMessageDialog(this, "Seçilen film için detay bulunamadı.", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
}