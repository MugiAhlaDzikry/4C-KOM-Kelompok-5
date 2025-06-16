package main;

import com.formdev.flatlaf.FlatIntelliJLaf;

import javax.swing.*;
import javax.swing.plaf.LayerUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class RegisterForm extends JFrame {
    private JTextField tfUsername;
    private JPasswordField pfPassword;
    private JComboBox<String> cbRole;

    public RegisterForm() {
        setTitle("Register");
        setSize(600, 400); // Ukuran jendela bebas
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel wrapper untuk menengahkan isi
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        FadeInLayerUI layerUI = new FadeInLayerUI();
        JLayer<JPanel> jlayer = new JLayer<>(centerWrapper, layerUI);


        // Panel isi dengan lebar tetap
        JPanel contentPanel = new JPanel(new net.miginfocom.swing.MigLayout("wrap 1, fillx, insets 20", "[250!]", "[25!]"));
        contentPanel.setBorder(BorderFactory.createTitledBorder("Form Registrasi"));

        tfUsername = new JTextField();
        pfPassword = new JPasswordField();
        cbRole = new JComboBox<>(new String[]{"admin", "kasir"});

        JButton btnRegister = new JButton("📝 Register");
        JButton btnBack = new JButton("⬅ Kembali ke Login");

        contentPanel.add(new JLabel("Username"));
        contentPanel.add(tfUsername, "growx");
        contentPanel.add(new JLabel("Password"));
        contentPanel.add(pfPassword, "growx");
        contentPanel.add(new JLabel("Role"));
        contentPanel.add(cbRole, "growx");
        contentPanel.add(btnRegister, "growx");
        contentPanel.add(btnBack, "growx");

        centerWrapper.add(contentPanel);
        add(jlayer, BorderLayout.CENTER);


        btnRegister.addActionListener(e -> registerUser());
        btnBack.addActionListener(e -> {
            this.dispose();
            new LoginForm().setVisible(true);
        });

        Timer fadeTimer = new Timer(70, null);
        fadeTimer.addActionListener(new ActionListener() {
            float alpha = 0f;

            @Override
            public void actionPerformed(ActionEvent e) {
                alpha += 0.05f;
                if (alpha >= 1f) {
                    alpha = 1f;
                    fadeTimer.stop();
                }
                layerUI.setAlpha(alpha);
                jlayer.repaint();
            }
        });
        fadeTimer.start();

    }

    class FadeInLayerUI extends LayerUI<JPanel> {
        private float alpha = 0f;

        public void setAlpha(float alpha) {
            this.alpha = alpha;
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            super.paint(g2, c);
            g2.dispose();
        }
    }


    private void registerUser() {
        String username = tfUsername.getText().trim();
        String password = new String(pfPassword.getPassword());
        String role = cbRole.getSelectedItem().toString();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Isi semua field!");
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos_chicken", "root", "");
             PreparedStatement ps = conn.prepareStatement("INSERT INTO tbuser (username, password, role) VALUES (?, ?, ?)")) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Registrasi berhasil!");
            dispose();
            new LoginForm().setVisible(true);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Registrasi gagal: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        FlatIntelliJLaf.setup();
        SwingUtilities.invokeLater(() -> new RegisterForm().setVisible(true));
    }
}
