package main;

import com.formdev.flatlaf.FlatIntelliJLaf;
import net.miginfocom.swing.MigLayout;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.*;

public class KelolaMenuForm extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField tfNama, tfHarga;
    private JComboBox<String> cbKategori;
    private JLabel lblPreview;
    private File selectedFile;
    private int selectedId = -1;


    public KelolaMenuForm() {
        setTitle("Kelola Menu");
        setSize(900, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new MigLayout("fill", "[60%][40%]", "[grow][]"));

        // Table
        tableModel = new DefaultTableModel(new String[]{"ID", "Gambar", "Nama", "Harga", "Kategori"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
            public Class<?> getColumnClass(int column) {
                return column == 1 ? Icon.class : Object.class;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(60);
        JScrollPane scroll = new JScrollPane(table);

        // Form kanan
        JPanel panelForm = new JPanel(new MigLayout("", "[grow]", ""));
        panelForm.setBorder(new EmptyBorder(10, 10, 10, 10));

        lblPreview = new JLabel("Preview", SwingConstants.CENTER);
        lblPreview.setPreferredSize(new Dimension(200, 120));
        lblPreview.setBorder(BorderFactory.createDashedBorder(Color.GRAY));

        JButton btnBrowse = new JButton("📷 Ganti Gambar");
        tfNama = new JTextField();
        tfHarga = new JTextField();
        cbKategori = new JComboBox<>(new String[]{"Paket Nasi", "Chicken", "Minuman"});
        JButton btnUpdate = new JButton("✏️ Update");
        JButton btnHapus = new JButton("🗑️ Hapus");

        panelForm.add(lblPreview, "growx, wrap");
        panelForm.add(btnBrowse, "growx, wrap, gaptop 5");
        panelForm.add(new JLabel("Nama"), "wrap");
        panelForm.add(tfNama, "growx, wrap");
        panelForm.add(new JLabel("Harga"), "wrap");
        panelForm.add(tfHarga, "growx, wrap");
        panelForm.add(new JLabel("Kategori"), "wrap");
        panelForm.add(cbKategori, "growx, wrap");
        panelForm.add(btnUpdate, "split 2, growx");
        panelForm.add(btnHapus, "growx, wrap");

        // Layout
        add(scroll, "grow, span 1 2");
        add(panelForm, "grow");

        // Events
        loadData();

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) pilihBaris();
        });

        btnBrowse.addActionListener(e -> pilihGambar());
        btnUpdate.addActionListener(e -> updateMenu());
        btnHapus.addActionListener(e -> hapusMenu());
    }

    private void loadData() {
        tableModel.setRowCount(0);
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos_chicken", "root", "")) {
            var ps = conn.prepareStatement("SELECT * FROM tbmenu");
            var rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String nama = rs.getString("nama");
                int harga = rs.getInt("harga");
                String kategori = rs.getString("kategori");
                String gambarPath = rs.getString("gambar");

                ImageIcon icon = new ImageIcon(new ImageIcon(gambarPath)
                        .getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH));
                tableModel.addRow(new Object[]{id, icon, nama, harga, kategori});
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void pilihBaris() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        selectedId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
        tfNama.setText(tableModel.getValueAt(row, 2).toString());
        tfHarga.setText(tableModel.getValueAt(row, 3).toString());
        cbKategori.setSelectedItem(tableModel.getValueAt(row, 4).toString());

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos_chicken", "root", "")) {
            var ps = conn.prepareStatement("SELECT gambar FROM tbmenu WHERE id=?");
            ps.setInt(1, selectedId);
            var rs = ps.executeQuery();
            if (rs.next()) {
                String path = rs.getString("gambar");
                selectedFile = new File(path);
                if (selectedFile.exists()) {
                    Image img = ImageIO.read(selectedFile)
                            .getScaledInstance(lblPreview.getWidth(), lblPreview.getHeight(), Image.SCALE_SMOOTH);
                    lblPreview.setIcon(new ImageIcon(img));
                    lblPreview.setText("");
                } else {
                    lblPreview.setIcon(null);
                    lblPreview.setText("File hilang");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void pilihGambar() {
        var fc = new JFileChooser();
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedFile = fc.getSelectedFile();
            try {
                Image img = ImageIO.read(selectedFile)
                        .getScaledInstance(lblPreview.getWidth(), lblPreview.getHeight(), Image.SCALE_SMOOTH);
                lblPreview.setIcon(new ImageIcon(img));
                lblPreview.setText("");
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Gagal memuat gambar");
            }
        }
    }

    private void updateMenu() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih menu terlebih dahulu!");
            return;
        }

        String nama = tfNama.getText().trim();
        String harga = tfHarga.getText().trim();
        String kategori = cbKategori.getSelectedItem().toString();
        String gambar = (selectedFile != null) ? selectedFile.getAbsolutePath() : "";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos_chicken", "root", "")) {
            var ps = conn.prepareStatement("UPDATE tbmenu SET nama=?, harga=?, kategori=?, gambar=? WHERE id=?");
            ps.setString(1, nama);
            ps.setString(2, harga);
            ps.setString(3, kategori);
            ps.setString(4, gambar);
            ps.setInt(5, selectedId);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Data berhasil diupdate");
            loadData();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void hapusMenu() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih menu terlebih dahulu!");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Yakin ingin menghapus menu ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos_chicken", "root", "")) {
                var ps = conn.prepareStatement("DELETE FROM tbmenu WHERE id=?");
                ps.setInt(1, selectedId);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Data berhasil dihapus");
                loadData();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new KelolaMenuForm().setVisible(true));
    }
}
