package filmotomasyonu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * GirisEkrani sınıfı, kullanıcı giriş ekranını oluşturur.
 */
public class GirisEkrani extends JFrame {
    private final JTextField kullaniciAdiField;
    private final JPasswordField sifreField;
    private JButton girisButton;
    private JButton iptalButton;
    private final JLabel mesajLabel;
    
    // Sabit kullanıcı adı ve şifre
    private static final String KULLANICI_ADI = "admin";
    private static final String SIFRE = "1234";
    
    
    public GirisEkrani() {
        // Pencere ayarları
        setTitle("Film Otomasyonu - Giriş");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Panel oluşturma
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(70, 130, 180)); 
        
        // Başlık etiketi
        JLabel baslikLabel = new JLabel("Film Otomasyonu");
        baslikLabel.setBounds(100, 20, 200, 30);
        baslikLabel.setFont(new Font("Arial", Font.BOLD, 20));
        baslikLabel.setForeground(Color.WHITE);
        panel.add(baslikLabel);
        
        // Kullanıcı adı etiketi ve alanı
        JLabel kullaniciAdiLabel = new JLabel("Kullanıcı Adı:");
        kullaniciAdiLabel.setBounds(50, 80, 100, 25);
        kullaniciAdiLabel.setForeground(Color.WHITE);
        panel.add(kullaniciAdiLabel);
        
        kullaniciAdiField = new JTextField();
        kullaniciAdiField.setBounds(150, 80, 200, 25);
        panel.add(kullaniciAdiField);
        
        // Şifre etiketi ve alanı
        JLabel sifreLabel = new JLabel("Şifre:");
        sifreLabel.setBounds(50, 120, 100, 25);
        sifreLabel.setForeground(Color.WHITE);
        panel.add(sifreLabel);
        
        sifreField = new JPasswordField();
        sifreField.setBounds(150, 120, 200, 25);
        panel.add(sifreField);
        
        // Giriş butonu
        girisButton = new JButton("Giriş");
        girisButton.setBounds(150, 170, 90, 30);
        girisButton.setBackground(Color.BLACK);
        girisButton.setForeground(Color.WHITE);
        girisButton.setFocusPainted(false);
        girisButton.setBorderPainted(false);
        girisButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                girisButton.setBackground(Color.RED);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                girisButton.setBackground(Color.BLACK);
            }
        });
        girisButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                girisYap();
            }
        });
        panel.add(girisButton);
        
        // İptal butonu
        iptalButton = new JButton("İptal");
        iptalButton.setBounds(260, 170, 90, 30);
        iptalButton.setBackground(Color.BLACK);
        iptalButton.setForeground(Color.WHITE);
        iptalButton.setFocusPainted(false);
        iptalButton.setBorderPainted(false);
        iptalButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                iptalButton.setBackground(Color.RED);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                iptalButton.setBackground(Color.BLACK);
            }
        });
        iptalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        panel.add(iptalButton);
        
        // Mesaj etiketi
        mesajLabel = new JLabel("");
        mesajLabel.setBounds(50, 220, 300, 25);
        mesajLabel.setForeground(Color.YELLOW);
        mesajLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(mesajLabel);
        
        // Enter tuşu ile giriş yapma
        sifreField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    girisYap();
                }
            }
        });
        
        // Paneli pencereye ekle
        add(panel);
    }
    
    /**
     * Giriş işlemini gerçekleştirir
     */
    private void girisYap() {
        String kullaniciAdi = kullaniciAdiField.getText();
        String sifre = new String(sifreField.getPassword());
        
        if (kullaniciAdi.equals(KULLANICI_ADI) && sifre.equals(SIFRE)) {
            // Giriş başarılı
            mesajLabel.setText("Giriş başarılı! Ana menüye yönlendiriliyorsunuz...");
            mesajLabel.setForeground(new Color(46, 204, 113)); // Yeşil
            
            // Ana menüyü aç
            Timer timer = new Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose(); // Giriş ekranını kapat
                    new AnaMenu().setVisible(true); // Ana menüyü aç
                }
            });
            timer.setRepeats(false);
            timer.start();
        } else {
            // Giriş başarısız
            mesajLabel.setText("Hatalı kullanıcı adı veya şifre!");
            mesajLabel.setForeground(new Color(231, 76, 60)); // Kırmızı
            
            // Şifre alanını temizle
            sifreField.setText("");
        }
    }
}
