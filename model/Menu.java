package model;

public class Menu {
    private int id;
    private String nama;
    private double harga;
    private String gambarPath;

    public Menu(int id, String nama, double harga) {
        this.id = id;
        this.nama = nama;
        this.harga = harga;
    }

    public Menu() {

    }

    public int getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public double getHarga() {
        return harga;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public void setHarga(double harga) {
        this.harga = harga;
    }

    public void setKategori(String kategori) {
    }

    public void setId(int id) {
    }

    public void setGambarPath(String gambar) {
    }

    public String getGambarPath() {
        // return path gambar yang sesuai
        return this.gambarPath; // pastikan ada atribut gambarPath di kelas Menu
    }

}
