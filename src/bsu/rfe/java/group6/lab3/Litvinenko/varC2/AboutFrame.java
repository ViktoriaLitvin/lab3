package bsu.rfe.java.group6.lab3.Litvinenko.varC2;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class AboutFrame extends JFrame {
    private static final int WIDTH = 1000;
    private static final int HEIGHT = 700;

    public AboutFrame() throws IOException {
        super("О программе");
        setSize(WIDTH, HEIGHT);
        Toolkit kit = Toolkit.getDefaultToolkit();
        setLocation((kit.getScreenSize().width - WIDTH) / 2, (kit.getScreenSize().height - HEIGHT) / 2);
        Box vBoxText = Box.createVerticalBox();
        JLabel imageLabel = new JLabel();
        Image image = ImageIO.read(new File("src/2.jpg"));
        imageLabel.setIcon(new ImageIcon(image));
        JLabel secondNameLabel = new JLabel();
        secondNameLabel.setText("Литвиненко");
        secondNameLabel.setFont (secondNameLabel.getFont ().deriveFont (64.0f));
        JLabel groupLabel = new JLabel();
        groupLabel.setText("6-ая группа");
        groupLabel.setFont (groupLabel.getFont ().deriveFont (64.0f));
        vBoxText.add(Box.createVerticalGlue());
        vBoxText.add(secondNameLabel);
        vBoxText.add(Box.createVerticalStrut(15));
        vBoxText.add(groupLabel);
        vBoxText.add(Box.createVerticalGlue());
        Box contentBox = Box.createHorizontalBox();
        contentBox.add(Box.createHorizontalGlue());
        contentBox.add(imageLabel);
        contentBox.add(Box.createVerticalStrut(30));
        contentBox.add(vBoxText);
        contentBox.add(Box.createHorizontalGlue());
        getContentPane().add(contentBox, BorderLayout.CENTER);
    }

    public static void main(String[] args) throws IOException {
        AboutFrame frame = new AboutFrame();
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setVisible(true);
    }
}
