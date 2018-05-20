package core.gui;

import core.encryptors.Encryptor;
import core.encryptors.aes.AESEncrypt;
import core.encryptors.blowfish.BlowfishEncrypt;
import core.encryptors.des.DESEncrypt;
import core.exceptions.CannotCreateEncryptException;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class MainGUI extends JPanel {

    private static Encryptor encrypt;

    private static final Insets EMPTY_INSETS = new Insets(0, 0, 0, 0);

    private static final String ADD_BUTTON_LABEL =    "Encode new file";

    private static final String REMOVE_BUTTON_LABEL = "Decode existing";

    private static final String DEFAULT_SOURCE_CHOICE_LABEL = "Coded files";

    private JList sourceList;

    private SortedListModel sourceListModel;

    public static void start() {
        JFrame f = new JFrame("File coder");

        Font font = new Font("Verdana", Font.PLAIN, 11);
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("Settings");
        fileMenu.setFont(font);

        JMenuItem chooseFolder = new JMenuItem("Choose folder");
        chooseFolder.setFont(font);
        fileMenu.add(chooseFolder);

        JMenu chooseAlgorithm = new JMenu("Choose algorithm");
        chooseAlgorithm.setFont(font);
        fileMenu.add(chooseAlgorithm);

        JMenuItem blowfish = new JMenuItem("Blowfish");
        blowfish.setFont(font);
        chooseAlgorithm.add(blowfish);

        JMenuItem AES = new JMenuItem("AES");
        AES.setFont(font);
        chooseAlgorithm.add(AES);

        JMenuItem DES = new JMenuItem("DES");
        DES.setFont(font);
        chooseAlgorithm.add(DES);

        blowfish.addActionListener((action) -> {
            try {
                MainGUI.encrypt = BlowfishEncrypt.getInstance();
            } catch (CannotCreateEncryptException c) {
                c.printStackTrace();
            }
        });

        AES.addActionListener((action) -> {
            try {
                MainGUI.encrypt = AESEncrypt.getInstance();
            } catch (CannotCreateEncryptException c) {
                c.printStackTrace();
            }
        });

        DES.addActionListener((action) -> {
            try {
                MainGUI.encrypt = DESEncrypt.getInstance();
            } catch (CannotCreateEncryptException c) {
                c.printStackTrace();
            }
        });

        chooseFolder.addActionListener((action) -> System.out.println("Choose folder"));

        menuBar.add(fileMenu);

        f.setJMenuBar(menuBar);

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

    MainGUI() {
        try {
            MainGUI.encrypt = BlowfishEncrypt.getInstance();
        } catch (CannotCreateEncryptException e) {
            e.printStackTrace();
        }
        initScreen();
    }

    void addSourceElements(Object newValue[]) {
        fillListModel(sourceListModel, newValue);
    }

    private void fillListModel(SortedListModel model, Object newValues[]) {
        model.addAll(newValues);
    }

    private void initScreen() {
        setBorder(BorderFactory.createEtchedBorder());
        setLayout(new GridBagLayout());
        JLabel sourceLabel = new JLabel(DEFAULT_SOURCE_CHOICE_LABEL);
        sourceListModel = new SortedListModel();
        sourceList = new JList(sourceListModel);
        add(sourceLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                EMPTY_INSETS, 0, 0));
        add(new JScrollPane(sourceList), new GridBagConstraints(0, 1, 1, 5, .5,
                1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                EMPTY_INSETS, 0, 0));

        JButton addButton = new JButton(ADD_BUTTON_LABEL);
        add(addButton, new GridBagConstraints(1, 2, 1, 2, 0, .25,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                EMPTY_INSETS, 0, 0));
        addButton.addActionListener(new AddListener());

        JButton removeButton = new JButton(REMOVE_BUTTON_LABEL);
        add(removeButton, new GridBagConstraints(1, 4, 1, 2, 0, .25,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
                0, 5, 0, 5), 0, 0));
        removeButton.addActionListener(new RemoveListener());
    }

    private void addDestinationElements(Object newValue) {
        sourceListModel.add(newValue);
    }

    private class AddListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileopen = new JFileChooser();
            int ret = fileopen.showDialog(null, "Encrypt");
            if (ret == JFileChooser.APPROVE_OPTION) {
                File source = fileopen.getSelectedFile();
                File result = new File(BlowfishEncrypt.PACKAGE + "." + source.getName());
                try {
                    encrypt.encrypt(new FileInputStream(source), new FileOutputStream(result));
                    addDestinationElements(result.getName());
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    }

    private class RemoveListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileopen = new JFileChooser();
            fileopen.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int ret = fileopen.showDialog(null, "Choose folder");
            if (ret == JFileChooser.APPROVE_OPTION) {
                File folder = fileopen.getSelectedFile();
                Object selected[] = sourceList.getSelectedValues();
                for (Object o : selected) {
                    File decoded = new File(folder.getAbsolutePath() + "/" + o.toString().replaceFirst(".", ""));
                    File encoded = new File(BlowfishEncrypt.PACKAGE + o.toString());
                    System.out.println(o);

                    try {
                        encrypt.decrypt(new FileInputStream(encoded), new FileOutputStream(decoded));
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
        }
    }

    class SortedListModel extends AbstractListModel {

        SortedSet model;

        SortedListModel() {
            model = new TreeSet();
        }

        public int getSize() {
            return model.size();
        }

        public Object getElementAt(int index) {
            return model.toArray()[index];
        }

        void add(Object element) {
            if (model.add(element)) {
                fireContentsChanged(this, 0, getSize());
            }
        }

        void addAll(Object elements[]) {
            Collection c = Arrays.asList(elements);
            model.addAll(c);
            fireContentsChanged(this, 0, getSize());
        }

        boolean removeElement(Object element) {
            boolean removed = model.remove(element);
            if (removed) {
                fireContentsChanged(this, 0, getSize());
            }
            return removed;
        }
    }
}
