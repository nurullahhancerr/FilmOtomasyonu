package filmotomasyonu;

import javax.swing.*;
import javax.swing.border.TitledBorder; 
import java.awt.*;
import java.awt.event.*;

/**
 * Film güncellemek için kullanılan GUI ekranı
 * Kullanıcı film ID'si ile arama yapıp, bulduğu filmin bilgilerini güncelleyebilir
 */
public class FilmGuncelleEkrani extends JFrame {
    // Ana bileşenler
    private FilmYonetici filmYonetici;
    private Film guncellenecekFilm;

    // Form alanları
    private JTextField filmIdField;
    private JTextField adField;
    private JComboBox<String> turComboBox;
    private JTextField yonetmenField;
    private JComboBox<Integer> yilComboBox;
    private JTextField sureField;
    private JTextField puanField;

    // Butonlar
    private JButton araButton;
    private JButton guncelleButton;
    private JButton temizleButton;

    // Sabitler
    private static final String[] FILM_TURLERI = {
            "Macera", "Aksiyon", "Romantik", "Komedi", "Dram", "Bilim Kurgu",
            "Korku", "Gerilim", "Fantastik", "Animasyon", "Belgesel", "Aile",
            "Suç", "Gizem", "Tarih", "Müzikal", "Savaş", "Western", "Biyografi", "Spor"
    };

    private static final int MIN_YIL = 1950;
    private static final int MAX_YIL = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) + 1; 
    private static final Color ANA_RENK = new Color(241, 196, 15);
    private static final Color BASLIK_RENK = new Color(243, 156, 18);
    private static final Font BASLIK_YAZI_FONTU = new Font("Arial", Font.BOLD, 16); 

    
    public FilmGuncelleEkrani(FilmYonetici filmYonetici) {
        if (filmYonetici == null) {
            throw new IllegalArgumentException("FilmYonetici null olamaz!");
        }
        this.filmYonetici = filmYonetici;
        initComponents();
    }

    
    private void initComponents() {
        setupWindow();

        JPanel anaPanel = new JPanel(new BorderLayout());
        anaPanel.setBackground(ANA_RENK);

        
        JPanel baslikPanel = createBaslikPanel();
        JPanel aramaPanel = createAramaPanel();
        JPanel formPanel = createFormPanel();
        JPanel butonPanel = createButonPanel();

        
        JPanel ustPanel = new JPanel(new BorderLayout());
        ustPanel.setBackground(ANA_RENK);
        ustPanel.add(aramaPanel, BorderLayout.NORTH);
        ustPanel.add(formPanel, BorderLayout.CENTER);

        anaPanel.add(baslikPanel, BorderLayout.NORTH);
        anaPanel.add(ustPanel, BorderLayout.CENTER);
        anaPanel.add(butonPanel, BorderLayout.SOUTH);

        add(anaPanel);

        // Event listener'ları ekle
        setupEventListeners();

        // Başlangıçta formu deaktif et
        setFormEnabled(false);
    }

    
    private void setupWindow() {
        setTitle("Film Güncelle");
        setSize(650, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    /**
     * Başlık panelini oluşturur
     */
    private JPanel createBaslikPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BASLIK_RENK);
        panel.setPreferredSize(new Dimension(getWidth(), 60));

        JLabel baslikLabel = new JLabel("Film Güncelle");
        baslikLabel.setFont(new Font("Arial", Font.BOLD, 24));
        baslikLabel.setForeground(Color.WHITE);
        panel.add(baslikLabel);

        return panel;
    }

    
    private JPanel createAramaPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        panel.setBackground(ANA_RENK);

        
        TitledBorder aramaBorder = BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Film Ara",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                BASLIK_YAZI_FONTU, 
                Color.BLACK       
        );
        panel.setBorder(aramaBorder);


        JLabel label = new JLabel("Film ID:");
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(Color.BLACK);

        filmIdField = new JTextField(15);
        filmIdField.setFont(new Font("Arial", Font.PLAIN, 14));

        araButton = createStyledButton("Ara", 80, 35);

        panel.add(label);
        panel.add(filmIdField);
        panel.add(araButton);

        return panel;
    }

    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(ANA_RENK);

        // "Film Bilgileri" başlığı için TitledBorder özelleştiriliyor
        TitledBorder formBorder = BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Film Bilgileri",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                BASLIK_YAZI_FONTU, 
                Color.BLACK       
        );
        panel.setBorder(formBorder);


        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        addFormRow(panel, gbc, 0, "Film Adı:", adField = new JTextField(25));
        turComboBox = new JComboBox<>(FILM_TURLERI);
        addFormRow(panel, gbc, 1, "Tür:", turComboBox);
        addFormRow(panel, gbc, 2, "Yönetmen:", yonetmenField = new JTextField(25));

        DefaultComboBoxModel<Integer> yilModel = new DefaultComboBoxModel<>();
        for (int i = MAX_YIL; i >= MIN_YIL; i--) {
            yilModel.addElement(i);
        }
        yilComboBox = new JComboBox<>(yilModel);
        addFormRow(panel, gbc, 3, "Yayın Yılı:", yilComboBox);
        addFormRow(panel, gbc, 4, "Süre (dk):", sureField = new JTextField(25));
        addFormRow(panel, gbc, 5, "Puan (0-10):", puanField = new JTextField(25));

        return panel;
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent component) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(Color.BLACK);
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        if (component instanceof JTextField) {
            ((JTextField) component).setFont(new Font("Arial", Font.PLAIN, 14));
        } else if (component instanceof JComboBox) {
            ((JComboBox<?>) component).setFont(new Font("Arial", Font.PLAIN, 14));
        }
        panel.add(component, gbc);
    }

    private JPanel createButonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panel.setBackground(ANA_RENK);

        guncelleButton = createStyledButton("Güncelle", 120, 35);
        temizleButton = createStyledButton("Temizle", 120, 35);
        JButton iptalButton = createStyledButton("İptal", 120, 35);
        iptalButton.addActionListener(e -> dispose());

        panel.add(guncelleButton);
        panel.add(temizleButton);
        panel.add(iptalButton);

        return panel;
    }

    private JButton createStyledButton(String text, int width, int height) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(width, height));
        button.setBackground(Color.BLACK);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(new Color(220, 20, 60));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(Color.BLACK);
                }
            }
        });
        return button;
    }

    private void setupEventListeners() {
        araButton.addActionListener(e -> filmBul());
        guncelleButton.addActionListener(e -> filmGuncelle());
        temizleButton.addActionListener(e -> formuTemizle(true));

        filmIdField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    filmBul();
                }
            }
        });

        addNumericValidation(sureField, false);
        addNumericValidation(puanField, true);
        addNumericValidation(filmIdField, false);
    }

    private void addNumericValidation(JTextField field, boolean allowDecimal) {
        field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && (allowDecimal ? c != '.' : true) && c != KeyEvent.VK_BACK_SPACE) {
                    e.consume();
                }
                if (allowDecimal && c == '.' && field.getText().contains(".")) {
                    e.consume();
                }
            }
        });
    }

    private void filmBul() {
        try {
            String idStr = filmIdField.getText().trim();
            if (idStr.isEmpty()) {
                showWarning("Lütfen bir Film ID girin!");
                return;
            }
            int id = Integer.parseInt(idStr);
            guncellenecekFilm = filmYonetici.filmBul(id);

            if (guncellenecekFilm == null) {
                showWarning("ID: " + id + " olan film bulunamadı!");
                setFormEnabled(false);
                clearFormFields();
                return;
            }
            fillFormWithFilmData();
            setFormEnabled(true);
            showInfo("Film bulundu ve form dolduruldu.");
        } catch (NumberFormatException e) {
            showError("Geçerli bir Film ID girin! (Sadece sayı)");
            setFormEnabled(false);
            clearFormFields();
        } catch (Exception e) {
            showError("Film arama sırasında hata oluştu: " + e.getMessage());
            setFormEnabled(false);
            clearFormFields();
            e.printStackTrace();
        }
    }

    private void fillFormWithFilmData() {
        adField.setText(guncellenecekFilm.getAd());
        turComboBox.setSelectedItem(guncellenecekFilm.getTur());
        yonetmenField.setText(guncellenecekFilm.getYonetmen());
        yilComboBox.setSelectedItem(guncellenecekFilm.getYayinYili());
        sureField.setText(String.valueOf(guncellenecekFilm.getSure()));
        puanField.setText(String.valueOf(guncellenecekFilm.getImdbPuani()));
    }

    private void setFormEnabled(boolean enabled) {
        adField.setEnabled(enabled);
        turComboBox.setEnabled(enabled);
        yonetmenField.setEnabled(enabled);
        yilComboBox.setEnabled(enabled);
        sureField.setEnabled(enabled);
        puanField.setEnabled(enabled);
        guncelleButton.setEnabled(enabled);
        temizleButton.setEnabled(enabled);
    }

    private void clearFormFields() {
        adField.setText("");
        if (turComboBox.getItemCount() > 0) turComboBox.setSelectedIndex(0);
        yonetmenField.setText("");
        if (yilComboBox.getItemCount() > 0) yilComboBox.setSelectedIndex(0);
        sureField.setText("");
        puanField.setText("");
    }

    private void formuTemizle(boolean clearIdField) {
        if (clearIdField) filmIdField.setText("");
        clearFormFields();
        setFormEnabled(false);
        guncellenecekFilm = null;
        if (clearIdField) filmIdField.requestFocus();
    }

    private void filmGuncelle() {
        if (guncellenecekFilm == null) {
            showError("Güncellenecek film seçilmedi! Önce bir film arama yapın.");
            return;
        }
        try {
            FilmData filmData = extractFormData();
            if (!validateFilmData(filmData)) return;

            String posterUrl = guncellenecekFilm.getPosterUrl();
            boolean mevcutIzlendiMiDurumu = guncellenecekFilm.isIzlendiMi();

            Film guncelFilm = new Film(
                    guncellenecekFilm.getId(),
                    filmData.ad, filmData.tur, filmData.yonetmen,
                    filmData.yayinYili, filmData.sure, filmData.puan,
                    posterUrl,
                    mevcutIzlendiMiDurumu
            );

            boolean basarili = filmYonetici.filmGuncelle(guncelFilm);
            if (basarili) {
                showSuccess("Film başarıyla güncellendi!");
                formuTemizle(true);
            } else {
                showError("Film güncellenemedi! Lütfen tekrar deneyin.");
            }
        } catch (NumberFormatException e) {
            showError("Süre ve Puan alanları için geçerli sayısal değerler girin!");
        } catch (Exception e) {
            showError("Güncelleme sırasında hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private FilmData extractFormData() throws NumberFormatException {
        FilmData data = new FilmData();
        data.ad = adField.getText().trim();
        data.tur = (String) turComboBox.getSelectedItem();
        data.yonetmen = yonetmenField.getText().trim();
        data.yayinYili = (Integer) yilComboBox.getSelectedItem();
        String sureText = sureField.getText().trim();
        data.sure = (sureText.isEmpty() || sureText.equals(".")) ? 0 : Integer.parseInt(sureText);
        String puanText = puanField.getText().trim();
        data.puan = (puanText.isEmpty() || puanText.equals(".")) ? 0.0 : Double.parseDouble(puanText);
        return data;
    }

    private boolean validateFilmData(FilmData data) {
        if (data.ad.isEmpty()) {
            showError("Film adı boş bırakılamaz!");
            adField.requestFocus();
            return false;
        }
        if (data.tur == null || data.tur.trim().isEmpty()) {
            showError("Film türü seçilmelidir!");
            turComboBox.requestFocus();
            return false;
        }
        if (data.yonetmen.isEmpty()) {
            showError("Yönetmen adı boş bırakılamaz!");
            yonetmenField.requestFocus();
            return false;
        }
        if (data.sure < 0) {
            showError("Süre negatif olamaz!");
            sureField.requestFocus();
            return false;
        }
        if (data.puan < 0 || data.puan > 10) {
            showError("Puan 0-10 arasında olmalıdır!");
            puanField.requestFocus();
            return false;
        }
        return true;
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Bilgi", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Başarılı", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Uyarı", JOptionPane.WARNING_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Hata", JOptionPane.ERROR_MESSAGE);
    }

    private static class FilmData {
        String ad;
        String tur;
        String yonetmen;
        int yayinYili;
        int sure;
        double puan;
    }
}