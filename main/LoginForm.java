package main;

import com.formdev.flatlaf.FlatIntelliJLaf;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.plaf.LayerUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class LoginForm extends JFrame {
    private JTextField tfUsername;
    private JPasswordField pfPassword;

    public LoginForm() {
        setTitle("Login");
        setSize(600, 400); // Ukuran jendela boleh besar
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel pembungkus untuk center (menggunakan BorderLayout.CENTER)
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        FadeInLayerUI layerUI = new FadeInLayerUI();
        JLayer<JPanel> jlayer = new JLayer<>(centerWrapper, layerUI);


        // Panel konten dengan ukuran tetap
        JPanel contentPanel = new JPanel(new MigLayout("wrap 1, fillx, insets 20", "[250]", "[30]"));
        contentPanel.setBorder(BorderFactory.createTitledBorder("Login ke POS"));

        tfUsername = new JTextField();
        pfPassword = new JPasswordField();
        JButton btnLogin = new JButton("🔓 Login");
        JButton btnRegister = new JButton("📝 Register");

        contentPanel.add(new JLabel("Username"));
        contentPanel.add(tfUsername, "growx");
        contentPanel.add(new JLabel("Password"));
        contentPanel.add(pfPassword, "growx");
        contentPanel.add(btnLogin, "growx");
        contentPanel.add(btnRegister, "growx");

        btnLogin.addActionListener(e -> login());
        btnRegister.addActionListener(e -> {
            this.dispose();
            new RegisterForm().setVisible(true);
        });

        centerWrapper.add(contentPanel); // Tempatkan di tengah
        add(jlayer, BorderLayout.CENTER);// Tambahkan ke frame

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



    private void login() {
        String user = tfUsername.getText().trim();
        String pass = new String(pfPassword.getPassword());

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/pos_chicken", "root", "")) {
            String sql = "SELECT * FROM tbuser WHERE username=? AND password=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, user);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                JOptionPane.showMessageDialog(this, "Login berhasil sebagai " + role);
                this.dispose();
                if (role.equals("admin")) {
                    new MenuForm().setVisible(true);
                } else {
                    new PemesananForm().setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Username atau password salah.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal login: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        FlatIntelliJLaf.setup();
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}
