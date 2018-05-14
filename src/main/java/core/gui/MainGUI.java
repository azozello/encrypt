package core.gui;

import core.encryptors.blowfish.BlowfishEncrypt;
import core.exceptions.CannotCreateEncryptException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class MainGUI extends JPanel {

    private BlowfishEncrypt bfe;

    private static final Insets EMPTY_INSETS = new Insets(0, 0, 0, 0);

    private static final String ADD_BUTTON_LABEL =    "Encode new file";

    private static final String REMOVE_BUTTON_LABEL = "Decode existing";

    private static final String DEFAULT_SOURCE_CHOICE_LABEL = "Coded files";

    private JList sourceList;

    private SortedListModel sourceListModel;

    public MainGUI() {
        try {
            this.bfe = BlowfishEncrypt.getInstance();
        } catch (CannotCreateEncryptException e) {
            e.printStackTrace();
        }
        initScreen();
    }

    public void addSourceElements(Object newValue[]) {
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
                    bfe.encrypt(new FileInputStream(source), new FileOutputStream(result));
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
                        bfe.decrypt(new FileInputStream(encoded), new FileOutputStream(decoded));
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
