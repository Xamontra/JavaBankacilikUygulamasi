package com.example.javabankacilikuygulamasi;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    private Stage stage;
    private Scene scene;          // Tek bir Scene kullanıyoruz, sadece içeriği değişiyor
    private HBox ustCubuk;        // Sağ üstteki küçült / tam ekran / kapat butonları
    private boolean tamEkranIdi;  // Küçültmeden önce tam ekran mıydı?

    // Pencereyi sürüklemek için
    private double xFark;
    private double yFark;

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        // İşletim sisteminin başlık çubuğunu ve butonlarını kaldırır (show'dan önce çağrılmalı)
        stage.initStyle(StageStyle.UNDECORATED);

        // Veritabanını hazırla
        VeritabaniYonetici.tablolariOlustur();
        // Deneme kullanıcısı. Zaten varsa eklenmez, sorun çıkmaz.
        VeritabaniYonetici.kullaniciEkle("admin", "1234");

        ustCubuk = ustCubukOlustur();
        scene = new Scene(sarmala(girisEkrani("")));

        // Görev çubuğundan pencere geri açılınca tekrar tam ekrana dön
        stage.iconifiedProperty().addListener((obs, eski, yeni) -> {
            if (!yeni && tamEkranIdi) {
                stage.setFullScreen(true);
                tamEkranIdi = false;
            }
        });

        stage.setTitle("Bankacılık Uygulaması");
        stage.setScene(scene);

        stage.setFullScreenExitHint("");
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.setFullScreen(true);

        stage.show();
    }

    // ---------- Üst çubuk (sürükleme alanı + pencere butonları) ----------

    private HBox ustCubukOlustur() {
        Button kucult = pencereButonu("—", "#e5e5e5", "black");
        kucult.setOnAction(e -> {
            tamEkranIdi = stage.isFullScreen();
            if (tamEkranIdi) {
                stage.setFullScreen(false);
            }
            stage.setIconified(true);
        });

        Button buyut = pencereButonu("□", "#e5e5e5", "black");
        buyut.setOnAction(e -> {
            if (stage.isFullScreen()) {
                // Tam ekrandan pencere moduna geç
                stage.setFullScreen(false);
                stage.setWidth(1000);
                stage.setHeight(650);
                stage.centerOnScreen();
            } else {
                stage.setFullScreen(true);
            }
        });
        // Tam ekranken "❐" (pencere yap), pencere modundayken "□" (tam ekran yap) göster
        stage.fullScreenProperty().addListener((obs, eski, yeni) -> buyut.setText(yeni ? "❐" : "□"));

        Button kapat = pencereButonu("✕", "#e81123", "white");
        kapat.setOnAction(e -> Platform.exit());

        // Butonları sağa yaslamak için esneyen boş alan
        Region bosluk = new Region();
        HBox.setHgrow(bosluk, Priority.ALWAYS);

        HBox cubuk = new HBox(bosluk, kucult, buyut, kapat);
        cubuk.setAlignment(Pos.TOP_RIGHT);

        // Çubuğun boş kısmından tutup pencereyi sürükleme (sadece pencere modunda)
        cubuk.setOnMousePressed(e -> {
            xFark = stage.getX() - e.getScreenX();
            yFark = stage.getY() - e.getScreenY();
        });
        cubuk.setOnMouseDragged(e -> {
            if (!stage.isFullScreen()) {
                stage.setX(e.getScreenX() + xFark);
                stage.setY(e.getScreenY() + yFark);
            }
        });

        return cubuk;
    }

    private Button pencereButonu(String yazi, String hoverRenk, String hoverYaziRenk) {
        String normal = "-fx-background-color: transparent; -fx-background-radius: 0; "
                + "-fx-font-size: 16px; -fx-min-width: 50px; -fx-min-height: 34px; -fx-cursor: hand;";
        String uzerinde = "-fx-background-color: " + hoverRenk + "; -fx-text-fill: " + hoverYaziRenk + "; "
                + "-fx-background-radius: 0; -fx-font-size: 16px; -fx-min-width: 50px; -fx-min-height: 34px; -fx-cursor: hand;";

        Button b = new Button(yazi);
        b.setFocusTraversable(false); // Enter/Tab ile yanlışlıkla tetiklenmesin
        b.setStyle(normal);
        b.setOnMouseEntered(e -> b.setStyle(uzerinde));
        b.setOnMouseExited(e -> b.setStyle(normal));
        return b;
    }

    // Ekran içeriğini üst çubukla birlikte sarmalar
    private Parent sarmala(Parent icerik) {
        BorderPane kok = new BorderPane();
        kok.setTop(ustCubuk);
        kok.setCenter(icerik);
        // Pencere modunda sınır belli olsun diye ince çerçeve
        kok.setStyle("-fx-border-color: #bdbdbd; -fx-border-width: 1;");
        return kok;
    }

    // Ekran değiştirmek için: pencereye dokunmadan sadece içeriği değiştirir
    private void ekranDegistir(Parent yeniEkran) {
        scene.setRoot(sarmala(yeniEkran));
    }

    // ---------- Ekranlar ----------

    // bilgiMesaji: kayıt sonrası "Kayıt başarılı" gibi yeşil bir mesaj göstermek için
    private Parent girisEkrani(String bilgiMesaji) {
        Label baslik = new Label("Banka Girişi");
        baslik.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        TextField kullaniciAdi = new TextField();
        kullaniciAdi.setPromptText("Kullanıcı adı");
        kullaniciAdi.setMaxWidth(250);

        PasswordField sifre = new PasswordField();
        sifre.setPromptText("Şifre");
        sifre.setMaxWidth(250);

        Label mesaj = new Label(bilgiMesaji);
        mesaj.setStyle("-fx-text-fill: green;");

        Button girisButonu = new Button("Giriş Yap");
        girisButonu.setDefaultButton(true);

        girisButonu.setOnAction(e -> {
            String k = kullaniciAdi.getText().trim();
            String s = sifre.getText();

            if (k.isEmpty() || s.isEmpty()) {
                mesaj.setStyle("-fx-text-fill: red;");
                mesaj.setText("Kullanıcı adı ve şifre boş olamaz.");
            } else if (VeritabaniYonetici.girisKontrol(k, s)) {
                ekranDegistir(anaEkran(k));
            } else {
                mesaj.setStyle("-fx-text-fill: red;");
                mesaj.setText("Kullanıcı adı veya şifre hatalı.");
                sifre.clear();
            }
        });

        Button kayitaGit = new Button("Kayıt Ol");
        kayitaGit.setOnAction(e -> ekranDegistir(kayitEkrani()));

        VBox kutu = new VBox(12, baslik, kullaniciAdi, sifre, girisButonu, kayitaGit, mesaj);
        kutu.setAlignment(Pos.CENTER);
        kutu.setStyle("-fx-padding: 30;");

        return kutu;
    }

    private Parent kayitEkrani() {
        Label baslik = new Label("Kayıt Ol");
        baslik.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        TextField kullaniciAdi = new TextField();
        kullaniciAdi.setPromptText("Kullanıcı adı (en az 3 karakter)");
        kullaniciAdi.setMaxWidth(250);

        PasswordField sifre = new PasswordField();
        sifre.setPromptText("Şifre (en az 6 karakter)");
        sifre.setMaxWidth(250);

        PasswordField sifreTekrar = new PasswordField();
        sifreTekrar.setPromptText("Şifre tekrar");
        sifreTekrar.setMaxWidth(250);

        Label mesaj = new Label();
        mesaj.setStyle("-fx-text-fill: red;");

        Button kayitButonu = new Button("Kayıt Ol");
        kayitButonu.setDefaultButton(true);

        kayitButonu.setOnAction(e -> {
            String k = kullaniciAdi.getText().trim();
            String s = sifre.getText();
            String s2 = sifreTekrar.getText();

            if (k.isEmpty() || s.isEmpty() || s2.isEmpty()) {
                mesaj.setText("Tüm alanları doldurmalısın.");
            } else if (k.length() < 3) {
                mesaj.setText("Kullanıcı adı en az 3 karakter olmalı.");
            } else if (s.length() < 6) {
                mesaj.setText("Şifre en az 6 karakter olmalı.");
            } else if (!s.equals(s2)) {
                mesaj.setText("Şifreler birbiriyle aynı değil.");
                sifreTekrar.clear();
            } else if (VeritabaniYonetici.kullaniciEkle(k, s)) {
                ekranDegistir(girisEkrani("Kayıt başarılı! Şimdi giriş yapabilirsin."));
            } else {
                mesaj.setText("Bu kullanıcı adı zaten alınmış.");
            }
        });

        Button geriDon = new Button("Girişe Dön");
        geriDon.setOnAction(e -> ekranDegistir(girisEkrani("")));

        VBox kutu = new VBox(12, baslik, kullaniciAdi, sifre, sifreTekrar, kayitButonu, mesaj, geriDon);
        kutu.setAlignment(Pos.CENTER);
        kutu.setStyle("-fx-padding: 30;");

        return kutu;
    }

    private Parent anaEkran(String kullanici) {
        Label hosgeldin = new Label("Hoş geldin, " + kullanici + "!");
        hosgeldin.setStyle("-fx-font-size: 20px;");

        Button cikisYap = new Button("Çıkış Yap");
        cikisYap.setOnAction(e -> ekranDegistir(girisEkrani("")));

        VBox kutu = new VBox(15, hosgeldin, cikisYap);
        kutu.setAlignment(Pos.CENTER);

        return kutu;
    }

    public static void main(String[] args) {
        launch();
    }
}