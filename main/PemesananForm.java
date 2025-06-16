package main;

import com.formdev.flatlaf.FlatIntelliJLaf;
import controller.MenuController;
import model.Menu;
import view.WrapLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;

public class PemesananForm extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel totalLabel;
    private int totalHarga = 0;
    private JPanel produkPanel;
    private JScrollPane produkScrollPane;

    public PemesananForm() {
        setTitle("Aplikasi Kasir - Ayam Geprek");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600); //
        setExtendedState(JFrame.MAXIMIZED_BOTH); //
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel contentWrapper = new JPanel(new GridBagLayout());
        JPanel contentPanel = new JPanel(new GridLayout(1, 2));
        contentPanel.setPreferredSize(new Dimension(1000, 600));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        contentWrapper.add(contentPanel, gbc);
        setContentPane(contentWrapper);

        // Panel Kiri
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setPreferredSize(new Dimension(300, getHeight()));
        leftPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Tabel Transaksi
        String[] columns = {"Produk", "Harga", "Qty", "Subtotal"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        JLabel emptyLabel = new JLabel("Tidak ada data!", SwingConstants.CENTER);
        scrollPane.setColumnHeaderView(emptyLabel);

        totalLabel = new JLabel("Total: Rp. 0");
        totalLabel.setHorizontalAlignment(SwingConstants.CENTER);
        totalLabel.setOpaque(true);
        totalLabel.setBackground(new Color(200, 200, 255));

        // Tombol
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton resetButton = new JButton("🔁 Reset");
        resetButton.setBackground(Color.RED);
        resetButton.setForeground(Color.WHITE);

        JButton bayarButton = new JButton("💳 Bayar");
        bayarButton.setBackground(new Color(120, 80, 180));
        bayarButton.setForeground(Color.WHITE);

        resetButton.addActionListener(e -> resetData());
        bayarButton.addActionListener(e -> {
            cetakStruk();
            bayar();
        });

        buttonPanel.add(resetButton);
        buttonPanel.add(bayarButton);

        leftPanel.add(scrollPane, BorderLayout.CENTER);
        leftPanel.add(totalLabel, BorderLayout.NORTH);
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Panel Kanan
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel kategoriPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton paketNasiBtn = new JButton("Paket Nasi");
        JButton chickenBtn = new JButton("Chicken");
        JButton minumanBtn = new JButton("Minuman");

        kategoriPanel.add(paketNasiBtn);
        kategoriPanel.add(chickenBtn);
        kategoriPanel.add(minumanBtn);

        // Panel Produk
        produkPanel = new JPanel();
        produkPanel.setLayout(new WrapLayout(FlowLayout.LEFT, 65, 20));
        produkPanel.setBackground(Color.WHITE);

        produkScrollPane = new JScrollPane(produkPanel);
        produkScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        produkScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        produkScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        produkScrollPane.setBorder(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                new javax.swing.Timer(20, evt -> {
                    loadMenu("Paket Nasi");
                }).start();
            }
        });
        // default

        paketNasiBtn.addActionListener(e -> tampilkanMenuDariKategori("Paket Nasi"));
        chickenBtn.addActionListener(e -> tampilkanMenuDariKategori("Chicken"));
        minumanBtn.addActionListener(e -> tampilkanMenuDariKategori("Minuman"));

        rightPanel.add(kategoriPanel, BorderLayout.NORTH);
        rightPanel.add(produkScrollPane, BorderLayout.CENTER);

        contentPanel.add(leftPanel);
        contentPanel.add(rightPanel);
    }

    private void tampilkanMenuDariKategori(String kategori) {
        produkPanel.removeAll();

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos_chicken", "root", "")) {
            String sql = "SELECT nama, harga, gambar FROM tbmenu WHERE kategori=?";
            var ps = conn.prepareStatement(sql);
            ps.setString(1, kategori);
            var rs = ps.executeQuery();

            boolean adaData = false;
            while (rs.next()) {
                adaData = true;
                String nama = rs.getString("nama");
                int harga = rs.getInt("harga");
                String pathGambar = rs.getString("gambar");
                produkPanel.add(buatCardProduk(nama, harga, pathGambar));
            }

            if (!adaData) {
                JLabel kosong = new JLabel("Menu belum ditambahkan", SwingConstants.CENTER);
                kosong.setForeground(Color.GRAY);
                produkPanel.add(kosong);
            }

            produkPanel.revalidate();
            produkPanel.repaint();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal mengambil data menu: " + e.getMessage());
        }
    }

    private void loadMenu(String kategori) {
        produkPanel.removeAll();
        ArrayList<Menu> menuList = MenuController.getMenuByKategori(kategori);

        if (menuList.isEmpty()) {
            JLabel kosong = new JLabel("Menu belum ditambahkan", SwingConstants.CENTER);
            kosong.setFont(new Font("Arial", Font.BOLD, 16));
            produkPanel.add(kosong);
        } else {
            for (Menu m : menuList) {
                produkPanel.add(buatCardProduk(m.getNama(), (int) m.getHarga(), m.getGambarPath()));
            }
        }

        produkPanel.revalidate();
        produkPanel.repaint();
    }

    private void tampilkanPreviewStruk() {
        StringBuilder struk = new StringBuilder();
        struk.append("========= STRUK PEMBAYARAN =========\n");
        struk.append(String.format("%-20s %-5s %-7s\n", "Item", "Qty", "Subtotal"));
        struk.append("-------------------------------------\n");

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String nama = tableModel.getValueAt(i, 0).toString();
            String qty = tableModel.getValueAt(i, 2).toString();
            String subtotal = tableModel.getValueAt(i, 3).toString();

            struk.append(String.format("%-20s %-5s Rp%-7s\n", nama, qty, subtotal));
        }

        struk.append("-------------------------------------\n");
        struk.append("Total: Rp. " + totalHarga + "\n");
        struk.append("Terima kasih!\n");

        JTextArea textArea = new JTextArea(struk.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        JOptionPane.showMessageDialog(this, scrollPane, "Preview Struk", JOptionPane.PLAIN_MESSAGE);
    }


    private void cetakStruk() {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable((graphics, pageFormat, pageIndex) -> {
            if (pageIndex > 0) return Printable.NO_SUCH_PAGE;

            Graphics2D g2d = (Graphics2D) graphics;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

            int y = 20;

            g2d.setFont(new Font("Monospaced", Font.BOLD, 14));
            g2d.drawString("== STRUK PEMBAYARAN ==", 100, y); y += 20;
            g2d.setFont(new Font("Monospaced", Font.PLAIN, 12));

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String nama = tableModel.getValueAt(i, 0).toString();
                String harga = tableModel.getValueAt(i, 1).toString();
                String qty = tableModel.getValueAt(i, 2).toString();
                String subtotal = tableModel.getValueAt(i, 3).toString();

                g2d.drawString(nama + " x" + qty + "  Rp. " + subtotal, 20, y);
                y += 15;
            }

            y += 10;
            g2d.drawString("-----------------------------", 20, y); y += 15;
            g2d.drawString("TOTAL: Rp. " + totalHarga, 20, y); y += 20;

            g2d.drawString("Terima kasih!", 100, y);

            return Printable.PAGE_EXISTS;
        });

        boolean doPrint = job.printDialog();
        if (doPrint) {
            try {
                job.print();
            } catch (PrinterException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Gagal mencetak struk: " + e.getMessage());
            }
        }
    }


    private JPanel buatCardProduk(String nama, int harga, String pathGambar) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 5));
        panel.setPreferredSize(new Dimension(220, 220));
        panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        panel.setBackground(Color.WHITE);

        JLabel namaLabel = new JLabel(nama, SwingConstants.CENTER);
        JLabel hargaLabel = new JLabel("Rp. " + harga, SwingConstants.CENTER);
        hargaLabel.setOpaque(true);
        hargaLabel.setBackground(Color.LIGHT_GRAY);

        JLabel imgLabel = new JLabel("No Image", SwingConstants.CENTER);
        imgLabel.setPreferredSize(new Dimension(200, 140));
        imgLabel.setOpaque(true);
        imgLabel.setBackground(Color.ORANGE);

        File file = null;
        if (pathGambar != null && !pathGambar.isEmpty()) {
            String fixedPath = pathGambar.replace("\\", "/");
            file = new File(fixedPath);
            if (file.exists()) {
                ImageIcon imgIcon = new ImageIcon(
                        new ImageIcon(file.getAbsolutePath())
                                .getImage().getScaledInstance(230, 140, Image.SCALE_SMOOTH)
                );
                imgLabel.setText("");
                imgLabel.setIcon(imgIcon);
            } else {
                imgLabel.setText("Gambar tidak ditemukan");
            }
        } else {
            imgLabel.setText("Tidak ada gambar");
        }


        System.out.println("Loading image from: " + file.getAbsolutePath() + " | exists: " + file.exists());


        panel.add(imgLabel, BorderLayout.NORTH);
        panel.add(namaLabel, BorderLayout.CENTER);
        panel.add(hargaLabel, BorderLayout.SOUTH);

        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tambahKeTabel(nama, harga);
            }
        });

        return panel;
    }

    private void tambahKeTabel(String nama, int harga) {
        int qty = 1;
        int subtotal = harga * qty;

        tableModel.addRow(new Object[]{nama, harga, qty, subtotal});
        totalHarga += subtotal;
        totalLabel.setText("Total: Rp. " + totalHarga);
    }

    private void resetData() {
        tableModel.setRowCount(0);
        totalHarga = 0;
        totalLabel.setText("Total: Rp. 0");
    }

    private void bayar() {
        tampilkanPreviewStruk();
        resetData();
    }


    public static void main(String[] args) {
        FlatIntelliJLaf.setup();
        SwingUtilities.invokeLater(() -> {
            new PemesananForm().setVisible(true);
        });
    }
}

