package filmotomasyonu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class AnaMenu extends JFrame {
    private FilmYonetici filmYonetici;
    
    
    public AnaMenu() {
        filmYonetici = new FilmYonetici();
        
        setTitle("Film Otomasyonu - Ana Menü");
        setSize(800, 600); // Pencere boy
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel anaPanel = new JPanel(new BorderLayout());
        anaPanel.setBackground(new Color(52, 152, 219)); 
        
        JPanel baslikPanel = new JPanel();
        baslikPanel.setBackground(new Color(41, 128, 185)); 
        baslikPanel.setPreferredSize(new Dimension(getWidth(), 80)); // Genislik
        
        JLabel baslikLabel = new JLabel("Film Otomasyonu");
        baslikLabel.setFont(new Font("Arial", Font.BOLD, 28));
        baslikLabel.setForeground(Color.WHITE);
        baslikPanel.add(baslikLabel);
        
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(7, 1, 10, 10)); // satir say
        menuPanel.setBackground(new Color(52, 152, 219)); 
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50)); // Kenar bosluk
        
        JButton filmEkleButton = createMenuButton("Film Ekle");
        JButton filmListeleButton = createMenuButton("Film Listele");
        JButton filmSilButton = createMenuButton("Film Sil");
        JButton filmGuncelleButton = createMenuButton("Film Güncelle");
        JButton filmAraButton = createMenuButton("Film Ara");
        JButton vizyondakilerButton = createMenuButton("Vizyondakiler"); 
        JButton cikisButton = createMenuButton("Çıkış");
        
        filmEkleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new FilmEkleEkrani(filmYonetici).setVisible(true);
            }
        });
        
        filmListeleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new FilmListeleEkrani(filmYonetici).setVisible(true);
            }
        });
        
        filmSilButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new FilmSilEkrani(filmYonetici).setVisible(true);
            }
        });
        
        filmGuncelleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new FilmGuncelleEkrani(filmYonetici).setVisible(true);
            }
        });
        
        filmAraButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new FilmAraEkrani(filmYonetici).setVisible(true);
            }
        });

        
        vizyondakilerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                new VizyondakilerEkrani(filmYonetici).setVisible(true); // filmYonetici parametresi ile
            }
        });
        
        cikisButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int secim = JOptionPane.showConfirmDialog(
                    AnaMenu.this,
                    "Uygulamadan çıkmak istediğinize emin misiniz?",
                    "Çıkış",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
                );
                
                if (secim == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
        
        menuPanel.add(filmEkleButton);
        menuPanel.add(filmListeleButton);
        menuPanel.add(filmSilButton);
        menuPanel.add(filmGuncelleButton);
        menuPanel.add(filmAraButton);
        menuPanel.add(vizyondakilerButton); 
        menuPanel.add(cikisButton);        
        
        JPanel altBilgiPanel = new JPanel();
        altBilgiPanel.setBackground(new Color(41, 128, 185)); 
        altBilgiPanel.setPreferredSize(new Dimension(getWidth(), 40)); //genislik
        
        var altBilgiLabel = new JLabel("© 2025 Film Otomasyonu"); 
        altBilgiLabel.setForeground(Color.WHITE);
        altBilgiPanel.add(altBilgiLabel);
        
        anaPanel.add(baslikPanel, BorderLayout.NORTH);
        anaPanel.add(menuPanel, BorderLayout.CENTER);
        anaPanel.add(altBilgiPanel, BorderLayout.SOUTH);
        
        add(anaPanel);
    }
    
    
    
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(Color.BLACK);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(200, 50)); 
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(Color.RED);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.BLACK);
            }
        });
        
        return button;
    }
}