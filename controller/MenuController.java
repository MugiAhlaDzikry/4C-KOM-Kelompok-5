package controller;

import database.Koneksi_DB;
import model.Menu;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class MenuController {
    public static ArrayList<Menu> getMenuByKategori(String kategori) {
        ArrayList<Menu> list = new ArrayList<>();
        Connection conn = Koneksi_DB.getConnection();
        try {
            String query = "SELECT * FROM tbmenu WHERE kategori = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, kategori);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Menu m = new Menu();
                m.setId(rs.getInt("id"));
                m.setNama(rs.getString("nama"));
                m.setHarga(rs.getInt("harga"));
                m.setKategori(rs.getString("kategori"));
                m.setGambarPath(rs.getString("gambar")); // path disimpan
                list.add(m);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}

