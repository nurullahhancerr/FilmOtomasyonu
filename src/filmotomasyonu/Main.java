package filmotomasyonu;

import javax.swing.UnsupportedLookAndFeelException;


public class Main {
    
    public static void main(String[] args) {
        
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
            System.err.println("Look and Feel ayarlanamadı: " + e.getMessage());
        }
        
       
        java.awt.EventQueue.invokeLater(() -> {
            new GirisEkrani().setVisible(true);
        });
    }
}
