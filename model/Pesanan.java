package model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Pesanan {
    private int idPesanan;
    private Timestamp tanggal;
    private double total;
    private List<ItemPesanan> daftarItem;

    public Pesanan(int idPesanan, Timestamp tanggal, double total) {
        this.idPesanan = idPesanan;
        this.tanggal = tanggal;
        this.total = total;
        this.daftarItem = new ArrayList<>();
    }

    public int getIdPesanan() {
        return idPesanan;
    }

    public Timestamp getTanggal() {
        return tanggal;
    }

    public double getTotal() {
        return total;
    }

    public List<ItemPesanan> getDaftarItem() {
        return daftarItem;
    }

    public void setDaftarItem(List<ItemPesanan> daftarItem) {
        this.daftarItem = daftarItem;
    }

    public void tambahItem(ItemPesanan item) {
        this.daftarItem.add(item);
    }

    public double hitungTotal() {
        return daftarItem.stream()
                .mapToDouble(ItemPesanan::getSubtotal)
                .sum();
    }
}
