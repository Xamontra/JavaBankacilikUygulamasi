package com.example.javabankacilikuygulamasi;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;

public class VeritabaniYonetici {

    // Proje klasöründe banka.db adında bir dosya oluşur
    private static final String URL = "jdbc:sqlite:banka.db";

    private static Connection baglan() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    // Program açılırken bir kez çağrılır, tablo yoksa oluşturur
    public static void tablolariOlustur() {
        String sql = """
                CREATE TABLE IF NOT EXISTS kullanicilar (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    kullanici_adi TEXT NOT NULL UNIQUE,
                    sifre_hash TEXT NOT NULL,
                    salt TEXT NOT NULL,
                    bakiye_kurus INTEGER NOT NULL DEFAULT 0
                )
                """;
        try (Connection c = baglan(); Statement st = c.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Yeni kullanıcı ekler. Aynı kullanıcı adı varsa false döner.
    public static boolean kullaniciEkle(String kullaniciAdi, String sifre) {
        String salt = saltUret();
        String hash = hashle(sifre, salt);

        String sql = "INSERT INTO kullanicilar (kullanici_adi, sifre_hash, salt) VALUES (?, ?, ?)";
        try (Connection c = baglan(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, kullaniciAdi);
            ps.setString(2, hash);
            ps.setString(3, salt);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false; // büyük ihtimalle bu kullanıcı adı zaten var
        }
    }

    // Kullanıcı adı ve şifre doğruysa true döner
    public static boolean girisKontrol(String kullaniciAdi, String sifre) {
        String sql = "SELECT sifre_hash, salt FROM kullanicilar WHERE kullanici_adi = ?";
        try (Connection c = baglan(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, kullaniciAdi);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return false; // böyle bir kullanıcı yok
                }
                String kayitliHash = rs.getString("sifre_hash");
                String salt = rs.getString("salt");
                String girilenHash = hashle(sifre, salt);
                return MessageDigest.isEqual(
                        kayitliHash.getBytes(StandardCharsets.UTF_8),
                        girilenHash.getBytes(StandardCharsets.UTF_8));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static String saltUret() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    private static String hashle(String sifre, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(Base64.getDecoder().decode(salt));
            byte[] hash = md.digest(sifre.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}