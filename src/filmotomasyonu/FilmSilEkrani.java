package filmotomasyonu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * FilmSilEkrani sınıfı, film silme ekranını oluşturur.
 */
public class FilmSilEkrani extends JFrame {
    private FilmYonetici filmYonetici;
    private JTextField filmIdField;
    private JTextArea filmBilgiArea;

    public FilmSilEkrani(FilmYonetici filmYonetici) {
        this.filmYonetici = filmYonetici;

        setTitle("Film Sil");
        setSize(500, 350); 
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel anaPanel = new JPanel(new BorderLayout());
        anaPanel.setBackground(new Color(231, 76, 60)); 

        JPanel baslikPanel = new JPanel();
        baslikPanel.setBackground(new Color(192, 57, 43)); 
        baslikPanel.setPreferredSize(new Dimension(getWidth(), 50));
        JLabel baslikLabel = new JLabel("Film Sil");
        baslikLabel.setFont(new Font("Arial", Font.BOLD, 20));
        baslikLabel.setForeground(Color.WHITE);
        baslikPanel.add(baslikLabel);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(231, 76, 60)); 
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel filmIdLabel = new JLabel("Film ID:");
        filmIdLabel.setForeground(Color.WHITE);
        formPanel.add(filmIdLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        filmIdField = new JTextField(10);
        formPanel.add(filmIdField, gbc);

        gbc.gridx = 2; gbc.gridy = 0;
        JButton araButton = createStyledButton("Ara", 80, 28);
        araButton.addActionListener(e -> filmBul());
        formPanel.add(araButton, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 3;
        JLabel filmBilgiLabel = new JLabel("Film Bilgileri:");
        filmBilgiLabel.setForeground(Color.WHITE);
        formPanel.add(filmBilgiLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 3;
        filmBilgiArea = new JTextArea(8, 30); 
        filmBilgiArea.setEditable(false);
        filmBilgiArea.setLineWrap(true);
        filmBilgiArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(filmBilgiArea);
        formPanel.add(scrollPane, gbc);

        JPanel butonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        butonPanel.setBackground(new Color(231, 76, 60)); 

        JButton silButton = createStyledButton("Sil", 80, 30);
        silButton.addActionListener(e -> filmSil());
        
        JButton iptalButton = createStyledButton("İptal", 80, 30);
        iptalButton.addActionListener(e -> dispose());
        
        butonPanel.add(silButton);
        butonPanel.add(iptalButton);

        anaPanel.add(baslikPanel, BorderLayout.NORTH);
        anaPanel.add(formPanel, BorderLayout.CENTER);
        anaPanel.add(butonPanel, BorderLayout.SOUTH);
        add(anaPanel);

        filmIdField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    filmBul();
                }
            }
        });
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
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) button.setBackground(Color.DARK_GRAY);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (button.isEnabled()) button.setBackground(Color.BLACK);
            }
        });
        return button;
    }

    private void filmBul() {
        try {
            String idStr = filmIdField.getText().trim();
            if (idStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Lütfen bir Film ID girin!", "Uyarı", JOptionPane.WARNING_MESSAGE);
                filmBilgiArea.setText(""); 
                return;
            }

            int id = Integer.parseInt(idStr);
            Film film = filmYonetici.filmBul(id);

            if (film == null) {
                filmBilgiArea.setText("Film bulunamadı!");
                return;
            }
            
            StringBuilder sb = new StringBuilder();
            sb.append("Film ID: ").append(film.getId()).append("\n");
            sb.append("Film Adı: ").append(film.getAd()).append("\n");
            sb.append("Tür: ").append(film.getTur()).append("\n");
            sb.append("Yönetmen: ").append(film.getYonetmen()).append("\n");
            sb.append("Yayın Yılı: ").append(film.getYayinYili()).append("\n");
            sb.append("Süre: ").append(film.getSure()).append(" dakika\n");
            sb.append("Puan: ").append(film.getImdbPuani());
            
            filmBilgiArea.setText(sb.toString());

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Geçerli bir Film ID girin!", "Hata", JOptionPane.ERROR_MESSAGE);
            filmBilgiArea.setText(""); 
        }
    }

    private void filmSil() {
        try {
            String idStr = filmIdField.getText().trim();
            if (idStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Lütfen bir Film ID girin!", "Uyarı", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int id = Integer.parseInt(idStr);
            Film film = filmYonetici.filmBul(id);

            if (film == null) {
                JOptionPane.showMessageDialog(this, "Silinecek film bulunamadı!", "Uyarı", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int secim = JOptionPane.showConfirmDialog(
                this,
                "\"" + film.getAd() + "\" adlı filmi silmek istediğinize emin misiniz?\nBu işlem geri alınamaz.",
                "Silme Onayı",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );

            if (secim == JOptionPane.YES_OPTION) {
                boolean sonuc = filmYonetici.filmSil(id);
                if (sonuc) {
                    JOptionPane.showMessageDialog(this, "Film başarıyla silindi!", "Bilgi", JOptionPane.INFORMATION_MESSAGE);
                    filmIdField.setText("");
                    filmBilgiArea.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "Film silinemedi!", "Hata", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Geçerli bir Film ID girin!", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
}