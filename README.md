# JavaBankacilikUygulamasi
Java ve SQLite kullanılarak yapılmış Java Bankacılık Uygulaması örneğidir.


## Özellikler

- **Giriş ekranı:** Kullanıcı adı ve şifre ile giriş
- **Kayıt ol ekranı:** Boş alan, uzunluk, şifre eşleşmesi ve tekrar eden kullanıcı adı kontrolleri
- **Güvenli şifre saklama:** Şifreler düz metin olarak değil, kullanıcıya özel rastgele **salt** ile **SHA-256** hash'lenerek saklanır
- **SQL injection koruması:** Tüm sorgularda `PreparedStatement` kullanılır
- **Gerçek tam ekran arayüz:** İşletim sistemi başlık çubuğu kaldırılmıştır, sağ üstte özel **küçült / tam ekran-pencere / kapat** butonları bulunur
- **Hata mesajları:** Yanlış giriş durumunda kullanıcı adı ile şifre ayrımı yapılmadan ortak mesaj verilir (kullanıcı adı tahminini zorlaştırır)

## Kullanılan Teknolojiler

| Teknoloji | Sürüm |
|---|---|
| Java | 17 |
| JavaFX | 21.0.6 |
| Maven | - |
| SQLite (sqlite-jdbc) | 3.45.3.0 |
| IDE | IntelliJ IDEA |

## Proje Yapısı

```
JavaBankacilikUygulamasi
├── pom.xml
└── src
    └── main
        └── java
            ├── module-info.java
            └── com/example/javabankacilikuygulamasi
                ├── Main.java                 # Arayüz ve ekran geçişleri
                └── VeritabaniYonetici.java   # Veritabanı işlemleri
```

## Kurulum ve Çalıştırma

### Gereksinimler

- JDK 17 veya üstü
- Maven (IntelliJ ile birlikte gelir)
- İnternet bağlantısı (ilk çalıştırmada bağımlılıklar indirilir)

### Adımlar

1. Projeyi klonla:

   ```bash
   git clone https://github.com/Xamontra/JavaBankacilikUygulamasi.git
   cd JavaBankacilikUygulamasi
   ```

2. Projeyi IntelliJ IDEA ile aç ve Maven bağımlılıklarının inmesini bekle.

3. Çalıştır:
   - `Main.java` dosyasındaki ▶ düğmesiyle, **veya**
   - Terminalden:

     ```bash
     mvn clean javafx:run
     ```

İlk çalıştırmada proje klasöründe `banka.db` adında veritabanı dosyası otomatik oluşturulur.

## Deneme Hesabı

Program ilk açıldığında test amaçlı bir hesap oluşturur:

| Kullanıcı adı | Şifre |
|---|---|
| `admin` | `1234` |

> **Uyarı:** Bu hesap yalnızca geliştirme içindir. Gerçek kullanımdan önce `Main.java` içindeki `VeritabaniYonetici.kullaniciEkle("admin", "1234");` satırını silmelisin.

## Veritabanı

`kullanicilar` tablosu:

| Sütun | Tür | Açıklama |
|---|---|---|
| `id` | INTEGER | Otomatik artan birincil anahtar |
| `kullanici_adi` | TEXT | Benzersiz kullanıcı adı |
| `sifre_hash` | TEXT | Salt ile hash'lenmiş şifre |
| `salt` | TEXT | Kullanıcıya özel rastgele değer |
| `bakiye_kurus` | INTEGER | Bakiye (kuruş cinsinden tam sayı, yuvarlama hatalarını önlemek için) |

## Yol Haritası

- [x] Giriş ekranı
- [x] Kayıt ol ekranı
- [x] SQLite entegrasyonu
- [x] Tam ekran arayüz ve özel pencere butonları
- [ ] Bakiye görüntüleme
- [ ] Para yatırma ve çekme
- [ ] İşlem geçmişi
- [ ] Yanlış giriş denemelerinde geçici kilit
- [ ] BCrypt / Argon2 ile şifre hash'leme