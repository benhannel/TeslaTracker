package app;

import static app.Car.PARSER;
import com.google.gson.JsonElement;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author Ben Hannel
 */
public class Login extends JDialog {

    private LoginCallback callback;
    private static final File CREDENTIALS = new File("token.txt");

    public Login(Frame parent, boolean modal) {
        super(parent, modal);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();

        if (CREDENTIALS.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(CREDENTIALS))) {
                String username = reader.readLine();
                String pass = reader.readLine();
                System.out.println("loaded credentials: " + username + ", " + pass);
                usernameField.setText(username);
                passField.setText(pass);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
        //loginButtonActionPerformed(null);
    }

    public void setCallback(LoginCallback callback) {
        this.callback = callback;
    }

    public void setStatus(String status) {
        statusLabel.setText(status);
    }
    
    private static String getToken(String user, String pass) throws IOException {
        Map<String,String> arguments = new HashMap<>();
        arguments.put("grant_type", "password");
        arguments.put("client_id", "e4a9949fcfa04068f59abb5a658f2bac0a3428e4652315490b659d5ab3f35a9e");
        arguments.put("client_secret", "c75f14bbadc8bee3a7594412c31416f8300256d7668ea7e6e7f06727bfb9d220");
        arguments.put("email", user);
        arguments.put("password", pass);
        JsonElement result = PARSER.parse(HttpHelper.post("https://owner-api.teslamotors.com/oauth/token", arguments));
        return result.getAsJsonObject().get("access_token").getAsString();
    }
    

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        statusLabel = new javax.swing.JLabel();
        loginButton = new javax.swing.JButton();
        javax.swing.JPanel jPanel2 = new javax.swing.JPanel();
        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        usernameField = new javax.swing.JTextField();
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        passField = new javax.swing.JPasswordField();
        remember = new javax.swing.JCheckBox();
        javax.swing.JTextArea jTextArea1 = new javax.swing.JTextArea();

        setMinimumSize(new java.awt.Dimension(400, 200));
        setPreferredSize(new java.awt.Dimension(500, 250));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        jPanel1.setLayout(new java.awt.BorderLayout());
        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        statusLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        statusLabel.setText("Use your Tesla website login.");
        getContentPane().add(statusLabel, java.awt.BorderLayout.PAGE_START);

        loginButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        loginButton.setText("Log In");
        loginButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginButtonActionPerformed(evt);
            }
        });
        getContentPane().add(loginButton, java.awt.BorderLayout.PAGE_END);

        jPanel2.setLayout(new java.awt.GridLayout(0, 2));

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setText("Username");
        jPanel2.add(jLabel1);

        usernameField.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jPanel2.add(usernameField);

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setText("Password");
        jPanel2.add(jLabel2);

        passField.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jPanel2.add(passField);

        remember.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        remember.setText("Remember Me");
        jPanel2.add(remember);

        jTextArea1.setEditable(false);
        jTextArea1.setBackground(new java.awt.Color(238, 238, 238));
        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(3);
        jTextArea1.setText("Your username and password will be saved only on your own computer, and will not be transmitted");
        jTextArea1.setWrapStyleWord(true);
        jTextArea1.setAutoscrolls(false);
        jTextArea1.setBorder(null);
        jTextArea1.setOpaque(false);
        jTextArea1.setPreferredSize(new java.awt.Dimension(250, 120));
        jPanel2.add(jTextArea1);

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        setVisible(false);
        dispose();
    }//GEN-LAST:event_closeDialog

    private void loginButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loginButtonActionPerformed
        final String user = usernameField.getText();
        final String pass = passField.getText();
        final String token;
        
        try {
            token = getToken(user, pass);
        } catch (IOException ex) {
            statusLabel.setText("Login failed. Are your email and password correct?");
            Main.logError(ex);
            return;
        }
        
        if (remember.isSelected()) {
            try (FileWriter writer = new FileWriter(CREDENTIALS)) {
                writer.write(user + "\n" + pass);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "I'm sorry, the password could not be saved");
                ex.printStackTrace();
            }
        }
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                callback.submit(token);
            }
        });
    }//GEN-LAST:event_loginButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton loginButton;
    private javax.swing.JPasswordField passField;
    private javax.swing.JCheckBox remember;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JTextField usernameField;
    // End of variables declaration//GEN-END:variables
}
