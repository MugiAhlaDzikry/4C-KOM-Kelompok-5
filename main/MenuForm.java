package main;


import com.formdev.flatlaf.FlatIntelliJLaf;
import net.miginfocom.swing.MigLayout;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MenuForm extends JFrame {
    private JLabel lblImagePreview;
    private JTextField tfNama, tfHarga;
    private JComboBox<String> cbKategori;
    private JTable table;
    private DefaultTableModel tableModel;
    private BufferedImage selectedImage;
    private File selectedImageFile;
    private KelolaMenuForm kelolaMenuFormInstance;

    public MenuForm() {
        super("Form Menu Makanan");
        initUI();
    }

    private void initUI() {
        // Tampilan & Layout
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new MigLayout("fill", "[50%][50%]", "[][][][][][][grow][][]"));

        // Panel input kiri
        JPanel leftPanel = new JPanel(new MigLayout("", "[grow]", ""));
        leftPanel.setBorder(new EmptyBorder(10,10,10,10));
        lblImagePreview = new JLabel("Tambahkan Foto", SwingConstants.CENTER);
        lblImagePreview.setPreferredSize(new Dimension(200,150));
        lblImagePreview.setBorder(BorderFactory.createDashedBorder(Color.GRAY));
        JButton btnPilih = new JButton("📷 Pilih Gambar");
        tfNama = new JTextField();
        tfHarga = new JTextField();
        cbKategori = new JComboBox<>(new String[]{"Paket Nasi","Chicken","Minuman"});
        JButton btnTambah = new JButton("➕ Tambah");

        leftPanel.add(lblImagePreview, "growx, span, wrap");
        leftPanel.add(btnPilih, "growx, wrap, gaptop 10");
        leftPanel.add(new JLabel("Nama Menu"), "wrap");
        leftPanel.add(tfNama, "growx, wrap");
        leftPanel.add(new JLabel("Harga"), "wrap");
        leftPanel.add(tfHarga, "growx, wrap");
        leftPanel.add(new JLabel("Kategori"), "wrap");
        leftPanel.add(cbKategori, "growx, wrap");
        leftPanel.add(btnTambah, "growx, wrap, gaptop 10");

        // Panel daftar tabel kanan
        String[] colNames = {"Foto","Menu","Harga","Kategori", "GambarPath"};
        tableModel = new DefaultTableModel(colNames,0) {
            @Override
            public Class<?> getColumnClass(int col) {
                return (col == 0 ? Icon.class : Object.class);
            }

            @Override
            public boolean isCellEditable(int row, int col) {
                return false; // semua kolom tidak bisa diedit
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(60);
        JScrollPane scrollPane = new JScrollPane(table);
        JButton btnReset = new JButton("🔄 Reset");
        JButton btnSimpan = new JButton("💾 Simpan");
        JButton btnKelola = new JButton("🛠 Kelola Menu");
        JPanel rightBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightBottom.add(btnReset);
        rightBottom.add(btnSimpan);

        // Tambah ke layout
        add(leftPanel, "grow, span 1 2");
        add(scrollPane, "grow, wrap");
        add(rightBottom, "growx, span 2");

        rightBottom.add(btnReset);
        rightBottom.add(btnSimpan);
        rightBottom.add(btnKelola);


        // Listener tombol
        btnPilih.addActionListener(e -> pilihGambar());
        btnTambah.addActionListener(e -> tambahKeTabel());
        btnReset.addActionListener(e -> tableModel.setRowCount(0));
        btnSimpan.addActionListener(e -> simpanKeDatabase());
        btnKelola.addActionListener(e -> {
            if (kelolaMenuFormInstance == null || !kelolaMenuFormInstance.isDisplayable()) {
                kelolaMenuFormInstance = new KelolaMenuForm();
                kelolaMenuFormInstance.setVisible(true);
            } else {
                kelolaMenuFormInstance.toFront();
                kelolaMenuFormInstance.requestFocus();
            }
        });

    }

    private void pilihGambar() {
        var fc = new JFileChooser();
        if (fc.showOpenDialog(this)==JFileChooser.APPROVE_OPTION) {
            selectedImageFile = fc.getSelectedFile();
            try {
                selectedImage = ImageIO.read(selectedImageFile);
                var img = selectedImage.getScaledInstance(
                        lblImagePreview.getWidth(),
                        lblImagePreview.getHeight(),
                        Image.SCALE_SMOOTH
                );
                lblImagePreview.setIcon(new ImageIcon(img));
                lblImagePreview.setText("");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Gagal memuat gambar",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void tambahKeTabel() {
        String nama = tfNama.getText().trim();
        String harga = tfHarga.getText().trim();
        String kat   = cbKategori.getSelectedItem().toString();
        if (selectedImageFile==null || nama.isEmpty() || harga.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lengkapi data & gambar terlebih dahulu.");
            return;
        }
        ImageIcon icon = new ImageIcon(
                selectedImage.getScaledInstance(60,60,Image.SCALE_SMOOTH));
        tableModel.addRow(new Object[]{
                icon, nama, "Rp. "+harga, kat, selectedImageFile.getAbsolutePath()
        });
        resetForm();
    }

    private void resetForm() {
        lblImagePreview.setIcon(null);
        lblImagePreview.setText("Tambahkan Foto");
        tfNama.setText("");
        tfHarga.setText("");
        cbKategori.setSelectedIndex(0);
        selectedImage = null;
        selectedImageFile = null;
    }

    private void simpanKeDatabase() {
        if (tableModel.getRowCount()==0) {
            JOptionPane.showMessageDialog(this, "Tidak ada data untuk disimpan.");
            return;
        }
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/pos_chicken","root","")) {
            String sql = "INSERT INTO tbmenu(nama,harga,kategori,gambar) VALUES(?,?,?,?)";
            var ps = conn.prepareStatement(sql);
            for (int i=0; i<tableModel.getRowCount(); i++) {
                ps.setString(1, tableModel.getValueAt(i,1).toString());
                ps.setString(2, tableModel.getValueAt(i,2).toString().replace("Rp. ",""));
                ps.setString(3, tableModel.getValueAt(i,3).toString());
                ps.setString(4, tableModel.getValueAt(i, 4).toString());

                ps.addBatch();
            }
            ps.executeBatch();
            JOptionPane.showMessageDialog(this, "Data berhasil disimpan.");
            tableModel.setRowCount(0);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal menyimpan ke DB: "+ex.getMessage());
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(new FlatIntelliJLaf()); }
        catch (Exception ignored){}
        SwingUtilities.invokeLater(() -> new MenuForm().setVisible(true));
    }
}
