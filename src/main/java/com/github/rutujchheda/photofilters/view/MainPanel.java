package com.github.rutujchheda.photofilters.view;

import com.github.rutujchheda.photofilters.activity.ConvertPhotoActivity;
import com.github.rutujchheda.photofilters.dependency.DaggerServiceComponent;
import com.github.rutujchheda.photofilters.dependency.ServiceComponent;
import com.github.rutujchheda.photofilters.model.ConversionType;
import com.github.rutujchheda.photofilters.util.FileUtil;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class MainPanel extends JPanel {

    private JButton openUrlButton;
    private JLabel filePathLabel;
    private JButton openButton;
    private JButton saveButton;
    private JButton applyGreyscaleButton;
    private JButton applyInversionButton;
    private JButton applySepiaButton;
    private JButton clarendonButton;
    private DraggableZoomablePanel originalImagePanel;
    private DraggableZoomablePanel filteredImagePanel;
    private BufferedImage originalImage;
    private BufferedImage filteredImage;
    private File selectedFile;
    private List<String> filteredImagePaths;

    private static final ServiceComponent DAGGER = DaggerServiceComponent.create();

    public MainPanel() {
        setLayout(new BorderLayout());

        // Create top panel with file buttons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        openButton = new JButton("Open");
        saveButton = new JButton("Save");
        saveButton.setEnabled(false);
        openUrlButton = new JButton("Open from URL");
        topPanel.add(openUrlButton);
        topPanel.add(openButton);
        topPanel.add(saveButton);
        add(topPanel, BorderLayout.NORTH);

        // Create image panels
        originalImagePanel = new DraggableZoomablePanel();
        originalImagePanel.setBorder(BorderFactory.createTitledBorder("Original Image"));
        filteredImagePanel = new DraggableZoomablePanel();
        filteredImagePanel.setBorder(BorderFactory.createTitledBorder("Filtered Image"));
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(originalImagePanel), new JScrollPane(filteredImagePanel));
        splitPane.setResizeWeight(0.5);
        add(splitPane, BorderLayout.CENTER);

        // Create bottom panel with filter buttons
        JPanel bottomPanel = new JPanel(new GridLayout(1, 3));
        applyGreyscaleButton = new JButton("Apply Greyscale");
        applyInversionButton = new JButton("Apply Inversion");
        applySepiaButton = new JButton("Apply Sepia");
        clarendonButton = new JButton("Apply Clarendon");
        bottomPanel.add(clarendonButton);
        bottomPanel.add(applyGreyscaleButton);
        bottomPanel.add(applyInversionButton);
        bottomPanel.add(applySepiaButton);
        add(bottomPanel, BorderLayout.SOUTH);

        clarendonButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applyFilter(ConversionType.CLARENDON);
            }
        });

        // Add action listeners
        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseFile();
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveFilteredImages();
            }
        });

        openUrlButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField urlField = new JTextField();
                Object[] message = {
                        "Enter image URL:", urlField
                };

                int option = JOptionPane.showConfirmDialog(null, message, "Input", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    String imageUrl = urlField.getText();
                    if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                        try {
                            originalImage = FileUtil.loadImageFromUrl(imageUrl);
                            originalImagePanel.setImage(originalImage);
                            saveButton.setEnabled(false);
                            // Reset filtered image paths
                            filteredImagePaths = null;
                            filteredImagePanel.setImage(null);
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(MainPanel.this, "Error loading image: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });
        applyGreyscaleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applyFilter(ConversionType.GREYSCALE);
            }
        });

        applyInversionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applyFilter(ConversionType.INVERSION);
            }
        });

        applySepiaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applyFilter(ConversionType.SEPIA);
            }
        });

        // Add a drop target to the panel
        new DropTarget(this, new DropTargetAdapter() {
            @Override
            public void drop(DropTargetDropEvent dtde) {
                try {
                    Transferable tr = dtde.getTransferable();
                    DataFlavor[] flavors = tr.getTransferDataFlavors();
                    for (int i = 0; i < flavors.length; i++) {
                        if (flavors[i].isFlavorJavaFileListType()) {
                            dtde.acceptDrop(dtde.getDropAction());
                            List<File> files = (List<File>) tr.getTransferData(flavors[i]);
                            if (files.size() > 0) {
                                // Load the first file in the list
                                File originalFile = files.get(0);
                                // Create a copy of the file in the local directory
                                selectedFile = new File(System.getProperty("user.dir"), originalFile.getName());
                                Files.copy(originalFile.toPath(), selectedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                                originalImage = FileUtil.loadImage(selectedFile);
                                originalImagePanel.setImage(originalImage);
                                saveButton.setEnabled(false);
                                // Reset filtered image paths
                                filteredImagePaths = null;
                                filteredImagePanel.setImage(null);
                            }
                            dtde.dropComplete(true);
                            return;
                        }
                    }
                    dtde.rejectDrop();
                } catch (Exception e) {
                    e.printStackTrace();
                    dtde.rejectDrop();
                }
            }
        });
    }

    private void chooseFile() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Image Files", "jpg", "jpeg", "png", "bmp", "gif");
        fileChooser.setFileFilter(filter);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            try {
                originalImage = FileUtil.loadImage(selectedFile);
                originalImagePanel.setImage(originalImage);
                saveButton.setEnabled(false);
                // Reset filtered image paths
                filteredImagePaths = null;
                filteredImagePanel.setImage(null);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error loading image: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void applyFilter(ConversionType conversionType) {
        if (selectedFile == null) {
            JOptionPane.showMessageDialog(this, "Please choose a file first!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<ConversionType> conversions = new ArrayList<>();
        conversions.add(conversionType);

        ConvertPhotoActivity activity = DAGGER.provideConvertPhotoActivity();
        filteredImagePaths = activity.handleRequest(selectedFile.getAbsolutePath(), conversions);

        try {
            filteredImage = FileUtil.loadImage(new File(filteredImagePaths.get(0)));
            filteredImagePanel.setImage(filteredImage);
            saveButton.setEnabled(true);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error displaying filtered image: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveFilteredImages() {
        if (filteredImagePaths == null || filteredImagePaths.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No filtered images to save.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean saved = false;
        for (String path : filteredImagePaths) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File(path));
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "Image Files", "jpg", "jpeg", "png", "bmp", "gif");
            fileChooser.setFileFilter(filter);
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int returnValue = fileChooser.showSaveDialog(this);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File saveFile = fileChooser.getSelectedFile();
                try {
                    String fileName = saveFile.getName().toLowerCase();
                    if (!fileName.endsWith(".jpg") && !fileName.endsWith(".jpeg") && !fileName.endsWith(".png") && !fileName.endsWith(".bmp") && !fileName.endsWith(".gif")) {
                        JOptionPane.showMessageDialog(this, "Please choose a valid image file format.", "Error", JOptionPane.ERROR_MESSAGE);
                        continue;
                    }
                    File sourceFile = new File(path);
                    FileUtil.copyFile(sourceFile, saveFile);
                    saved = true;
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, "Error saving filtered image: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        if (saved) {
            JOptionPane.showMessageDialog(this, "Filtered images saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private class DraggableZoomablePanel extends JPanel {
        private BufferedImage image;
        private Point imageCorner;
        private Point prevPt;
        private double zoomFactor = 1.0;

        public DraggableZoomablePanel() {
            this.imageCorner = new Point(0, 0);
            ClickListener clickListener = new ClickListener();
            DragListener dragListener = new DragListener();
            ZoomListener zoomListener = new ZoomListener();
            this.addMouseListener(clickListener);
            this.addMouseMotionListener(dragListener);
            this.addMouseWheelListener(zoomListener);
        }

        public void setImage(BufferedImage image) {
            this.image = image;
            this.imageCorner = new Point(0, 0);
            this.zoomFactor = 1.0;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image != null) {
                int width = (int) (image.getWidth() * zoomFactor);
                int height = (int) (image.getHeight() * zoomFactor);

                // Calculate the center of the panel
                int panelCenterX = this.getWidth() / 2;
                int panelCenterY = this.getHeight() / 2;

                // Adjust the image's corner point to be at the center of the panel minus half the width and height of the image
                imageCorner.x = panelCenterX - width / 2;
                imageCorner.y = panelCenterY - height / 2;

                g.drawImage(image, imageCorner.x, imageCorner.y, width, height, this);
            }
        }

        private class ClickListener extends MouseAdapter {
            public void mousePressed(MouseEvent e) {
                prevPt = e.getPoint();
            }
        }

        private class DragListener extends MouseMotionAdapter {
            public void mouseDragged(MouseEvent e) {
                Point currentPt = e.getPoint();
                imageCorner.translate(
                        (int) ((currentPt.x - prevPt.x)),
                        (int) ((currentPt.y - prevPt.y))
                );
                prevPt = currentPt;
                repaint();
            }
        }

        private class ZoomListener extends MouseAdapter {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                double factor = (e.getWheelRotation() < 0) ? 1.1 : 0.9;
                zoomFactor *= factor;

                int mouseX = e.getX();
                int mouseY = e.getY();

                imageCorner.x = (int) (mouseX - (mouseX - imageCorner.x) * factor);
                imageCorner.y = (int) (mouseY - (mouseY - imageCorner.y) * factor);

                repaint();
            }
        }
    }
}