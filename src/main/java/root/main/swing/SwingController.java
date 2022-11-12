/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */

package root.main.swing;


import javafx.application.Platform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.util.StringUtils;
import root.main.General;
import root.main.fx.MainController;
import root.main.common.Properties;
import root.main.fx.custom.MinMaxIntegerProperty;

public class SwingController extends javax.swing.JPanel {

    private MainController mainController;
    private General general;

    private Properties properties;

    @Autowired
    @Lazy
    private void autowire(General general, Properties properties) {
        this.general = general;
        this.properties = properties;
        mainController = general.getMainController();
        jFxIntegerField1.getMaxValue().bind(general.getNumberOfDataRecordsProperty());
        general.getPageSizeProperty().bindBidirectional(jFxIntegerField1.getProp());
        amplitudeRangeList1.setSelectionInterval(4, 4);
    }


    /**
     * Creates new form NewJPanel
     */
    public SwingController() {
        initComponents();


    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        firstPageButton1 = new javax.swing.JButton();
        playToggleButton1 = new javax.swing.JToggleButton();
        prevPageButton1 = new javax.swing.JButton();
        nextPageButton1 = new javax.swing.JButton();
        lastPageButton1 = new javax.swing.JButton();
        openEEGFileButton1 = new javax.swing.JButton();
        amplitudeRangeLabel1 = new javax.swing.JLabel();
        fpsSlider1 = new javax.swing.JSlider();
        jLabel6 = new javax.swing.JLabel();
        fpsLabel1 = new javax.swing.JLabel();
        slowdownLabel1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        slowdownSlider1 = new javax.swing.JSlider();
        numChannelsLabel1 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        amplitudeRangeList1 = new javax.swing.JList();
        open3DViewButton1 = new javax.swing.JButton();
        jFxIntegerField1 = new custom.component.JFxIntegerField();
        jLabel1 = new javax.swing.JLabel();

        setMinimumSize(new java.awt.Dimension(0, 0));
        setPreferredSize(new java.awt.Dimension(250, 750));

        firstPageButton1.setText("First page");
        firstPageButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                firstPageButton1ActionPerformed(evt);
            }
        });

        playToggleButton1.setText("Play/Pause");
        playToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playToggleButton1ActionPerformed(evt);
            }
        });

        prevPageButton1.setText("Prev page");
        prevPageButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prevPageButton1ActionPerformed(evt);
            }
        });

        nextPageButton1.setText("Next page");
        nextPageButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextPageButton1ActionPerformed(evt);
            }
        });

        lastPageButton1.setText("Last page");
        lastPageButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lastPageButton1ActionPerformed(evt);
            }
        });

        openEEGFileButton1.setText("Open EEG...");
        openEEGFileButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openEEGFileButton1ActionPerformed(evt);
            }
        });

        amplitudeRangeLabel1.setText("Signal amplitude range:");

        fpsSlider1.setMajorTickSpacing(20);
        fpsSlider1.setMaximum(60);
        fpsSlider1.setMinimum(1);
        fpsSlider1.setMinorTickSpacing(5);
        fpsSlider1.setPaintTicks(true);
        fpsSlider1.setValue(60);
        fpsSlider1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                fpsSlider1StateChanged(evt);
            }
        });

        jLabel6.setText("FPS:");

        fpsLabel1.setText("60");

        slowdownLabel1.setText("1");

        jLabel4.setText("Slowdown:");

        slowdownSlider1.setMajorTickSpacing(20);
        slowdownSlider1.setMinimum(1);
        slowdownSlider1.setMinorTickSpacing(5);
        slowdownSlider1.setPaintTicks(true);
        slowdownSlider1.setValue(1);
        slowdownSlider1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                slowdownSlider1StateChanged(evt);
            }
        });

        amplitudeRangeList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "+/- 5 uV", "+/- 10 uV", "+/- 20 uV", "+/- 50 uV", "+/- 100 uV", "+/- 200 uV", "+/- 500 uV", "+/- 1 mV", "+/- 2 mV", "+/- 10 mV", "+/- 20 mV", "+/- 50 mV", "+/- 100 mV" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        amplitudeRangeList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        amplitudeRangeList1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        amplitudeRangeList1.setPreferredSize(new java.awt.Dimension(42, 90));
        amplitudeRangeList1.setVisibleRowCount(5);
        amplitudeRangeList1.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                amplitudeRangeList1ValueChanged(evt);
            }
        });
        jScrollPane3.setViewportView(amplitudeRangeList1);

        open3DViewButton1.setText("Current Value Watcher");
        open3DViewButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                open3DViewButton1ActionPerformed(evt);
            }
        });

        jLabel1.setText("Displayed secounds:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(numChannelsLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(57, 57, 57))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(fpsLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fpsSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel4)
                            .addComponent(slowdownLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(slowdownSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addComponent(open3DViewButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(playToggleButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(openEEGFileButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)
                        .addGap(93, 93, 93))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(amplitudeRangeLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(prevPageButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(firstPageButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lastPageButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(nextPageButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jScrollPane3)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jFxIntegerField1, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(openEEGFileButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(numChannelsLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jFxIntegerField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(11, 11, 11)
                .addComponent(amplitudeRangeLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(playToggleButton1)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(slowdownLabel1))
                    .addComponent(slowdownSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(fpsSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fpsLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(firstPageButton1)
                    .addComponent(lastPageButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(prevPageButton1)
                    .addComponent(nextPageButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(open3DViewButton1)
                .addContainerGap(273, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void firstPageButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_firstPageButton1ActionPerformed
        Platform.runLater(() -> {
            general.getScrollBarValue().getPosition().getRecordProperty().set(0);
            general.getScrollBarValue().getPosition().getOffsetProperty().set(0);
        });
    }//GEN-LAST:event_firstPageButton1ActionPerformed

    private void playToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playToggleButton1ActionPerformed
        general.getUpdateHandlerController().playToggle();
    }//GEN-LAST:event_playToggleButton1ActionPerformed

    private void prevPageButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prevPageButton1ActionPerformed
        Platform.runLater(() -> general.previousPage(null));
    }//GEN-LAST:event_prevPageButton1ActionPerformed

    private void nextPageButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextPageButton1ActionPerformed
        Platform.runLater(() -> general.nextPage(null));
    }//GEN-LAST:event_nextPageButton1ActionPerformed

    private void lastPageButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lastPageButton1ActionPerformed
        Platform.runLater(() -> {
            general.getScrollBarValue().getPosition().getRecordProperty().set(((MinMaxIntegerProperty) general.getScrollBarValue().getPosition().getRecordProperty()).getMax().get());
            general.getScrollBarValue().getPosition().getOffsetProperty().set(0);
        });
    }//GEN-LAST:event_lastPageButton1ActionPerformed

    private void openEEGFileButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openEEGFileButton1ActionPerformed
        Platform.runLater(() -> general.open());
    }//GEN-LAST:event_openEEGFileButton1ActionPerformed

    private void fpsSlider1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_fpsSlider1StateChanged
        general.getFpsProperty().setValue(fpsSlider1.getValue());
        fpsLabel1.setText(Integer.valueOf(fpsSlider1.getValue()).toString());
    }//GEN-LAST:event_fpsSlider1StateChanged

    private void slowdownSlider1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_slowdownSlider1StateChanged
        general.getSlowDownProperty().setValue(slowdownSlider1.getValue());
        slowdownLabel1.setText(Integer.valueOf(slowdownSlider1.getValue()).toString());
    }//GEN-LAST:event_slowdownSlider1StateChanged

    private void open3DViewButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_open3DViewButton1ActionPerformed
        Platform.runLater(general::openValueWatcher);
    }//GEN-LAST:event_open3DViewButton1ActionPerformed

    private void amplitudeRangeList1ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_amplitudeRangeList1ValueChanged
        String selectedValue = (String) amplitudeRangeList1.getModel().getElementAt(amplitudeRangeList1.getSelectedIndex());
        int newLineSpacing = Integer.parseInt(selectedValue.replaceAll("[\\D]", "")) * 2;
        if (selectedValue.contains("mV")) {
            newLineSpacing *= 1000;
        }
        properties.getLineSpacingProperty().set(newLineSpacing);
    }//GEN-LAST:event_amplitudeRangeList1ValueChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel amplitudeRangeLabel1;
    private javax.swing.JList amplitudeRangeList1;
    private javax.swing.JButton firstPageButton1;
    private javax.swing.JLabel fpsLabel1;
    private javax.swing.JSlider fpsSlider1;
    private custom.component.JFxIntegerField jFxIntegerField1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JButton lastPageButton1;
    private javax.swing.JButton nextPageButton1;
    private javax.swing.JLabel numChannelsLabel1;
    private javax.swing.JButton open3DViewButton1;
    private javax.swing.JButton openEEGFileButton1;
    private javax.swing.JToggleButton playToggleButton1;
    private javax.swing.JButton prevPageButton1;
    private javax.swing.JLabel slowdownLabel1;
    private javax.swing.JSlider slowdownSlider1;
    // End of variables declaration//GEN-END:variables
}
