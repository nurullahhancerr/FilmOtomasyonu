Film Otomasyon Sistemi

Bu proje, Java Swing kullanılarak geliştirilmiş basit bir film otomasyon sistemidir. Kullanıcıların film bilgilerini (ad, yönetmen, tür, yayın yılı, IMDb puanı vb.) eklemesine, silmesine, güncellemesine, listelemesine ve aramasına olanak tanır. Ayrıca, OMDb ve TMDb API'leri aracılığıyla film bilgilerini otomatik olarak çekme yeteneğine sahiptir.

## Özellikler

* Film Ekleme: Yeni film kayıtları ekleyebilme.
* Film Silme: Mevcut film kayıtlarını silebilme.
* Film Güncelleme: Film bilgilerini düzenleyebilme.
* Film Listeleme: Tüm filmleri listeleyebilme.
* Film Arama ve Filtreleme: Film adına, türüne, yayın yılına ve IMDb puanına göre arama ve filtreleme yapabilme.
* API Entegrasyonu: OMDb ve TMDb API'leri ile film bilgilerini otomatik olarak çekme.
* Veri Kalıcılığı: Filmlerin filmler.txt dosyasına kaydedilmesi ve uygulamayı yeniden başlattığınızda yüklenmesi.
* Kullanıcı Girişi: Basit bir kullanıcı adı/şifre doğrulama ekranı.

## Kurulum ve Çalıştırma

Bu projeyi çalıştırmak için Java Development Kit (JDK) kurulu olmalıdır. Proje NetBeans IDE ile geliştirilmiştir, ancak herhangi bir Java IDE (IntelliJ IDEA, Eclipse vb.) ile de açılıp derlenebilir.

1. Projeyi klonlayın veya zip dosyasını indirin ve çıkarın.
2. Tercih ettiğiniz IDE'de projeyi açın.
3. Gerekli kütüphanelerin (varsa) yüklendiğinden emin olun. (Bu proje için ek bir kütüphane gereksinimi bulunmamaktadır.)
4. Projeyi derleyin ve Main.java dosyasını çalıştırın.

## Kullanım

Uygulama başlatıldığında bir giriş ekranı ile karşılaşacaksınız. Varsayılan kullanıcı adı ve şifre:

* Kullanıcı Adı: admin
* Şifre: 1234 

Giriş yaptıktan sonra filmleri yönetebileceğiniz ana menüye yönlendirileceksiniz. Menüdeki seçenekleri kullanarak film ekleyebilir, silebilir, güncelleyebilir, listeleyebilir veya arama yapabilirsiniz.