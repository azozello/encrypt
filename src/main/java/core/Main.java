package core;

import core.encryptors.blowfish.BlowfishEncrypt;
import core.gui.MainGUI;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        JFrame f = new JFrame("File coder");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        MainGUI dual = new MainGUI();
        File root = new File(BlowfishEncrypt.PACKAGE);
        ArrayList<String> fileNames = new ArrayList<>();
        File[] matchingFiles = root.listFiles((File dir, String name) -> {
            if (name.startsWith(".") && !name.equals(".acab")) {
                System.out.println(name);
                fileNames.add(name);
            }
            return name.startsWith(".");
        });
        dual.addSourceElements(fileNames.toArray());
        f.getContentPane().add(dual, BorderLayout.CENTER);
        f.setSize(400, 300);
        f.setVisible(true);
    }
}
