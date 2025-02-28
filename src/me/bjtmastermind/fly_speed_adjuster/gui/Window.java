package me.bjtmastermind.fly_speed_adjuster.gui;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import me.bjtmastermind.fly_speed_adjuster.Main;
import me.bjtmastermind.fly_speed_adjuster.bedrock.LevelDB;
import me.bjtmastermind.nbt.io.NBTUtil;
import me.bjtmastermind.nbt.io.NamedTag;
import me.bjtmastermind.nbt.tag.CompoundTag;

public class Window {
    private File worldFolder;
    private JSpinner spinner;
    private JButton applyBtn;
    private JButton resetBtn;
    private float defaultSpeed = 0.05f;
    private float currentSpeed;

    private LevelDB db;
    private byte[] playerData;
    private CompoundTag root;
    private CompoundTag abilities;

    public void open() {
        JFrame frame = new JFrame("Fly Speed Adjuster");
        frame.getContentPane().setBackground(Color.WHITE);
        frame.getContentPane().setLayout(null);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                try {
                    db.close();
                } catch (NullPointerException e) {}
                System.exit(0);
            }
        });
        frame.setBounds(100, 100, 546, 275);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setResizable(false);
        this.populateWindow(frame);
    }

    private void populateWindow(JFrame frame) {
        JTextField fileSelected = new JTextField("Select World");
        fileSelected.setBounds(10, 30, 466, 50);
        fileSelected.setEditable(false);
        fileSelected.setVisible(true);
        frame.getContentPane().add(fileSelected);

        JButton uploadBtn = new JButton("");
        uploadBtn.setBounds(478, 30, 50, 50);
        ImageIcon icon = new ImageIcon(Window.class.getClassLoader().getResource("icons/select_world_icon.png"));
        Image img = icon.getImage();
        Image scaled = img.getScaledInstance(uploadBtn.getWidth() - 20, uploadBtn.getHeight() - 20, Image.SCALE_SMOOTH);
        uploadBtn.setIcon(new ImageIcon(scaled));
        uploadBtn.setVisible(true);
        uploadBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                if (event.getSource() == uploadBtn) {
                    int returnVal = chooser.showOpenDialog(null);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        fileSelected.setText(chooser.getSelectedFile().toString());
                        worldFolder = chooser.getSelectedFile();

                        if (!fileSelected.getText().equals("Select World")) {
                            String dbPath = worldFolder.toString() + "/db/";
                            if (!new File(dbPath).exists()) {
                                JOptionPane.showMessageDialog(frame, "Selected folder is not a vaild bedrock world.\nMissing '/db/' folder.", "Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }

                            spinner.setEnabled(true);
                            applyBtn.setEnabled(true);
                            resetBtn.setEnabled(true);

                            db = new LevelDB(dbPath);
                            playerData = db.get("~local_player".getBytes());
                            try {
                                NamedTag tag = NBTUtil.readLE(new ByteArrayInputStream(playerData), false);
                                root = (CompoundTag) tag.getTag();
                                abilities = root.getCompoundTag("abilities");
                                currentSpeed = abilities.getFloat("flySpeed");

                                spinner.setValue(currentSpeed / defaultSpeed);
                            } catch (IOException e) {
                                JOptionPane.showMessageDialog(frame, e, "Error", JOptionPane.ERROR_MESSAGE);
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });
        frame.getContentPane().add(uploadBtn);

        JLabel speedMultiplier = new JLabel("Fly Speed Multiplier");
        speedMultiplier.setBounds(10, 80, 466, 50);
        speedMultiplier.setVisible(true);
        frame.getContentPane().add(speedMultiplier);

        SpinnerModel model = new SpinnerNumberModel(1, 1, 50, 1);
        spinner = new JSpinner(model);
        spinner.setBounds(10, 120, 517, 25);
        spinner.setVisible(true);
        spinner.setEnabled(false);
        frame.getContentPane().add(spinner);

        applyBtn = new JButton("Apply");
        applyBtn.setBounds(10, 150, 254, 25);
        applyBtn.setVisible(true);
        applyBtn.setEnabled(false);
        applyBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                currentSpeed = defaultSpeed * (int) spinner.getValue();
                abilities.remove("flySpeed");
                abilities.put("flySpeed", currentSpeed);

                try {
                    ByteArrayOutputStream updatedData = NBTUtil.writeLE(root, false);
                    db.update("~local_player".getBytes(), updatedData.toByteArray());
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(frame, e, "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }

                JOptionPane.showMessageDialog(frame, "Successfully applied fly speed!", "Message", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        frame.getContentPane().add(applyBtn);

        resetBtn = new JButton("Reset");
        resetBtn.setBounds(274, 150, 254, 25);
        resetBtn.setVisible(true);
        resetBtn.setEnabled(false);
        resetBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                currentSpeed = defaultSpeed;
                abilities.remove("flySpeed");
                abilities.put("flySpeed", currentSpeed);

                spinner.setValue(currentSpeed / defaultSpeed);

                try {
                    ByteArrayOutputStream updatedData = NBTUtil.writeLE(root, false);
                    db.update("~local_player".getBytes(), updatedData.toByteArray());
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(frame, e, "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }

                JOptionPane.showMessageDialog(frame, "Successfully reset fly speed!", "Message", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        frame.getContentPane().add(resetBtn);

        JLabel version = new JLabel(Main.VERSION);
        version.setBounds(10, 200, 466, 50);
        version.setVisible(true);
        frame.getContentPane().add(version);

        frame.invalidate();
        frame.validate();
        frame.repaint();
    }
}
