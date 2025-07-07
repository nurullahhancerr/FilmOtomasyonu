package filmotomasyonu;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class FilmYonetici {
    private final List<Film> filmler;
    private static final String DOSYA_ADI = "filmler.txt";
    private int sonId = 0;

    public FilmYonetici() {
        filmler = new ArrayList<>();
        dosyadanOku();
    }

    
    public Film filmEkle(Film film) {
        
        for (Film mevcutFilm : filmler) {
            if (mevcutFilm.getAd().trim().equalsIgnoreCase(film.getAd().trim()) &&
                mevcutFilm.getYayinYili() == film.getYayinYili()) {
                System.out.println("Duplikasyon engellendi (Ekleme): " + film.getAd() + " (" + film.getYayinYili() + ")");
                return null; 
            }
        }

        
        film.setId(++sonId);
        filmler.add(film);
        dosyayaYaz();
        System.out.println("Film eklendi: " + film.getAd() + ", ID: " + film.getId());
        return film; 
    }

    public boolean filmSil(int id) {
        boolean silindi = filmler.removeIf(film -> film.getId() == id);
        if (silindi) {
            dosyayaYaz();
            System.out.println(id + " ID'li film silindi.");
        } else {
            System.out.println(id + " ID'li film silinemedi (bulunamadı).");
        }
        return silindi;
    }

    public boolean filmGuncelle(Film guncelFilm) {
        for (int i = 0; i < filmler.size(); i++) {
            if (filmler.get(i).getId() == guncelFilm.getId()) {
                // film çakisma kontorlu
                for (int j = 0; j < filmler.size(); j++) {
                    if (i == j) continue;
                    Film digerFilm = filmler.get(j);
                    if (digerFilm.getAd().trim().equalsIgnoreCase(guncelFilm.getAd().trim()) &&
                        digerFilm.getYayinYili() == guncelFilm.getYayinYili()) {
                        System.out.println("Güncelleme sırasında duplikasyon engellendi (başka bir kayıtla çakışıyor): " + guncelFilm.getAd() + " (" + guncelFilm.getYayinYili() + ")");
                        return false; 
                    }
                }
                filmler.set(i, guncelFilm);
                dosyayaYaz();
                System.out.println(guncelFilm.getId() + " ID'li film güncellendi: " + guncelFilm.getAd());
                return true;
            }
        }
        System.out.println(guncelFilm.getId() + " ID'li film güncellenemedi (bulunamadı).");
        return false;
    }

    public Film filmBul(int id) {
        for (Film film : filmler) {
            if (film.getId() == id) {
                return film;
            }
        }
        return null;
    }

    public List<Film> filmAra(String ad) {
        List<Film> sonuclar = new ArrayList<>();
        String aramaMetniLower = ad.trim().toLowerCase();
        for (Film film : filmler) {
            if (film.getAd().toLowerCase().contains(aramaMetniLower)) {
                sonuclar.add(film);
            }
        }
        return sonuclar;
    }
    
    public List<Film> turFiltrele(String tur) {
        List<Film> sonuclar = new ArrayList<>();
        for (Film film : filmler) {
            if (film.getTur().equalsIgnoreCase(tur)) {
                sonuclar.add(film);
            }
        }
        return sonuclar;
    }
    
    public List<Film> yilFiltrele(int yil) {
        List<Film> sonuclar = new ArrayList<>();
        for (Film film : filmler) {
            if (film.getYayinYili() == yil) {
                sonuclar.add(film);
            }
        }
        return sonuclar;
    }
    
    public List<Film> puanFiltrele(double minPuan, double maxPuan) {
        List<Film> sonuclar = new ArrayList<>();
        for (Film film : filmler) {
            if (film.getImdbPuani() >= minPuan && film.getImdbPuani() <= maxPuan) {
                sonuclar.add(film);
            }
        }
        return sonuclar;
    }

    public List<Film> tumFilmler() {
        return new ArrayList<>(filmler);
    }

    private void dosyayaYaz() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DOSYA_ADI))) {
            for (Film film : filmler) {
                writer.write(film.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Dosya yazma hatası: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Filmler dosyaya kaydedilirken bir hata oluştu!", "Dosya Yazma Hatası", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void dosyadanOku() {
        File file = new File(DOSYA_ADI);
        if (!file.exists()) {
            System.out.println(DOSYA_ADI + " bulunamadı, uygulama başlatıldığında yeni dosya oluşturulacak.");
            return; 
        }
        
        filmler.clear();
        int maxId = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(DOSYA_ADI))) {
            String line;
            int satirNo = 0;
            while ((line = reader.readLine()) != null) {
                satirNo++;
                if (line.trim().isEmpty()) continue; 
                try {
                    Film film = Film.fromFileString(line);
                    filmler.add(film);
                    if (film.getId() > maxId) {
                        maxId = film.getId();
                    }
                } catch (IllegalArgumentException e) { 
                    System.err.println(satirNo + ". satırda geçersiz film verisi (dosyadan okunurken): " + e.getMessage());
                    
                } catch (Exception e) {
                     System.err.println(satirNo + ". satırda dosyadan film okunurken beklenmedik hata: " + line + " - " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Dosya okuma hatası: " + e.getMessage());
             JOptionPane.showMessageDialog(null, "Filmler dosyadan okunurken bir hata oluştu!", "Dosya Okuma Hatası", JOptionPane.ERROR_MESSAGE);
        }
        sonId = maxId; 
        System.out.println(filmler.size() + " film dosyadan okundu. Son ID: " + sonId);
    }
}