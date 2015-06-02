/***********************************************
 * EditTextMiningOptions.java
 ***********************************************
 *
 ***********************************************
 * VERSION 1
 *
 * Medical University Graz
 * Institut of Pathology
 * Group of Univ.Prof. Dr.med.univ. Kurt Zatloukal
 * kurt.zatloukal(at)medunigraz.at
 * http://forschung.medunigraz.at/fodok/suchen.person_uebersicht?sprache_in=en&menue_id_in=101&id_in=90075196
 *
 ***********************************************
 * VERSION 2
 * http://sourceforge.net/projects/saat/
 *
 * Medical University Graz
 * Institut of Pathology
 * Group of Univ.Prof. Dr.med.univ. Kurt Zatloukal
 * kurt.zatloukal(at)medunigraz.at
 * http://forschung.medunigraz.at/fodok/suchen.person_uebersicht?sprache_in=en&menue_id_in=101&id_in=90075196
 *
 * Fraunhofer-Gesellschaft
 * Fraunhofer Institute for Biomedical Engineering
 * Central Research Infrastructure for molecular Pathology
 * Dr. Christina Schr�der
 * Christina.Schroeder(at)ibmt.fraunhofer.de
 * http://www.crip.fraunhofer.de/en/about/staff?noCache=776:1304399536
 ***********************************************
 * DESCRIPTION
 *
 * The gui options setting for the textmining
 ***********************************************
 */

package SAAT.textmining.munich.gui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * The gui options setting for the textmining
 * 
 * @author  Reihs Robert
 * @author  Sauer Stefan
 * @version 2.0
 * @since   since_version_2_date
 */
public class EditTextMiningOptions extends javax.swing.JFrame {

    /**
     * Variable decleration
     */
    private TextMiningMunichGui main_window_;
    private ArrayList<String> options_;
    
    /** 
     * Creates new form EditTextMiningOptions
     *
     * @param options The options for the textmining
     * @param main_window The main Window
     */
    public EditTextMiningOptions(String[] options, TextMiningMunichGui main_window) {
        initComponents();

        // Set Icon
        try {
            BufferedImage icon;
            File imageFile = new File("SAAT.png");
            icon = ImageIO.read(imageFile);
            setIconImage(icon);
        } catch (IOException ex) {
            Logger.getLogger(EditTextMiningOptions.class.getName()).log(Level.SEVERE, null, ex);
        }

        main_window_ = main_window;
        options_ = new ArrayList<String>();
        options_.clear();
        for(int i=0; i<options.length; ++i)
            options_.add(options[i]);
        setOptions();
    }

    /**
     * Close the window and activate the main window again
     */
    private void closeWindow() {
        main_window_.setActive(true);
        dispose();
    }

    /**
     * Set the options in the window
     */
    private void setOptions() {
        if(options_.contains("-ICD"))
            jCheckBoxICD10O.setSelected(true);
        else
            jCheckBoxICD10O.setSelected(false);
        
        if(options_.contains("-T"))
            jCheckBoxT.setSelected(true);
        else
            jCheckBoxT.setSelected(false);
        
        if(options_.contains("-N"))
            jCheckBoxN.setSelected(true);
        else
            jCheckBoxN.setSelected(false);
        
        if(options_.contains("-M"))
            jCheckBoxM.setSelected(true);
        else
            jCheckBoxM.setSelected(false);
        
        if(options_.contains("-R"))
            jCheckBoxR.setSelected(true);
        else
            jCheckBoxR.setSelected(false);
        
        if(options_.contains("-G"))
            jCheckBoxG.setSelected(true);
        else
            jCheckBoxG.setSelected(false);
        
        if(options_.contains("-L"))
            jCheckBoxL.setSelected(true);
        else
            jCheckBoxL.setSelected(false);
        
        if(options_.contains("-V"))
            jCheckBoxV.setSelected(true);
        else
            jCheckBoxV.setSelected(false);
        
        if(options_.contains("-Prog"))
            jCheckBoxProgresteron.setSelected(true);
        else
            jCheckBoxProgresteron.setSelected(false);
        
        if(options_.contains("-Oest"))
            jCheckBoxOestrogen.setSelected(true);
        else
            jCheckBoxOestrogen.setSelected(false);
        
        if(options_.contains("-Her2"))
            jCheckBoxHer2.setSelected(true);
        else
            jCheckBoxHer2.setSelected(false);
    }

    /**
     * Get the options set in the window
     *
     * @return The options
     */
    private String[] getOptions() {
        String[] return_value = new String[0];
        return_value = options_.toArray(return_value);
        return return_value;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jLayeredPane1 = new javax.swing.JLayeredPane();
    jLabel1 = new javax.swing.JLabel();
    jCheckBoxICD10O = new javax.swing.JCheckBox();
    jLayeredPane2 = new javax.swing.JLayeredPane();
    jCheckBoxT = new javax.swing.JCheckBox();
    jLabel2 = new javax.swing.JLabel();
    jCheckBoxN = new javax.swing.JCheckBox();
    jCheckBoxM = new javax.swing.JCheckBox();
    jCheckBoxR = new javax.swing.JCheckBox();
    jCheckBoxG = new javax.swing.JCheckBox();
    jCheckBoxL = new javax.swing.JCheckBox();
    jCheckBoxV = new javax.swing.JCheckBox();
    jLayeredPane3 = new javax.swing.JLayeredPane();
    jLabel3 = new javax.swing.JLabel();
    jCheckBoxOestrogen = new javax.swing.JCheckBox();
    jCheckBoxProgresteron = new javax.swing.JCheckBox();
    jCheckBoxHer2 = new javax.swing.JCheckBox();
    jButtonOk = new javax.swing.JButton();
    jButtonCancle = new javax.swing.JButton();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setTitle("Edit Text Mining Options");
    setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowClosing(java.awt.event.WindowEvent evt) {
        formWindowClosing(evt);
      }
    });

    jLayeredPane1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

    jLabel1.setText("Coding Options:");
    jLabel1.setBounds(10, 10, 110, 14);
    jLayeredPane1.add(jLabel1, javax.swing.JLayeredPane.DEFAULT_LAYER);

    jCheckBoxICD10O.setText("ICD-10/ICD-O");
    jCheckBoxICD10O.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        jCheckBoxICD10OItemStateChanged(evt);
      }
    });
    jCheckBoxICD10O.setBounds(10, 30, 100, 23);
    jLayeredPane1.add(jCheckBoxICD10O, javax.swing.JLayeredPane.DEFAULT_LAYER);

    jLayeredPane2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

    jCheckBoxT.setText(" T");
    jCheckBoxT.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        jCheckBoxTItemStateChanged(evt);
      }
    });
    jCheckBoxT.setBounds(10, 30, 50, 23);
    jLayeredPane2.add(jCheckBoxT, javax.swing.JLayeredPane.DEFAULT_LAYER);

    jLabel2.setText("Tumor Staging:");
    jLabel2.setBounds(10, 10, 110, 14);
    jLayeredPane2.add(jLabel2, javax.swing.JLayeredPane.DEFAULT_LAYER);

    jCheckBoxN.setText(" N");
    jCheckBoxN.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        jCheckBoxNItemStateChanged(evt);
      }
    });
    jCheckBoxN.setBounds(10, 60, 50, 23);
    jLayeredPane2.add(jCheckBoxN, javax.swing.JLayeredPane.DEFAULT_LAYER);

    jCheckBoxM.setText(" M");
    jCheckBoxM.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        jCheckBoxMItemStateChanged(evt);
      }
    });
    jCheckBoxM.setBounds(10, 90, 50, 23);
    jLayeredPane2.add(jCheckBoxM, javax.swing.JLayeredPane.DEFAULT_LAYER);

    jCheckBoxR.setText(" R");
    jCheckBoxR.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        jCheckBoxRItemStateChanged(evt);
      }
    });
    jCheckBoxR.setBounds(10, 120, 50, 23);
    jLayeredPane2.add(jCheckBoxR, javax.swing.JLayeredPane.DEFAULT_LAYER);

    jCheckBoxG.setText(" G");
    jCheckBoxG.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        jCheckBoxGItemStateChanged(evt);
      }
    });
    jCheckBoxG.setBounds(60, 30, 60, 23);
    jLayeredPane2.add(jCheckBoxG, javax.swing.JLayeredPane.DEFAULT_LAYER);

    jCheckBoxL.setText(" L");
    jCheckBoxL.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        jCheckBoxLItemStateChanged(evt);
      }
    });
    jCheckBoxL.setBounds(60, 60, 60, 23);
    jLayeredPane2.add(jCheckBoxL, javax.swing.JLayeredPane.DEFAULT_LAYER);

    jCheckBoxV.setText(" V");
    jCheckBoxV.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        jCheckBoxVItemStateChanged(evt);
      }
    });
    jCheckBoxV.setBounds(60, 90, 50, 23);
    jLayeredPane2.add(jCheckBoxV, javax.swing.JLayeredPane.DEFAULT_LAYER);

    jLayeredPane3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

    jLabel3.setText("Receptors:");
    jLabel3.setBounds(10, 10, 110, 14);
    jLayeredPane3.add(jLabel3, javax.swing.JLayeredPane.DEFAULT_LAYER);

    jCheckBoxOestrogen.setText("Estrogen");
    jCheckBoxOestrogen.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        jCheckBoxOestrogenItemStateChanged(evt);
      }
    });
    jCheckBoxOestrogen.setBounds(10, 30, 110, 23);
    jLayeredPane3.add(jCheckBoxOestrogen, javax.swing.JLayeredPane.DEFAULT_LAYER);

    jCheckBoxProgresteron.setText("Progesterone");
    jCheckBoxProgresteron.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        jCheckBoxProgresteronItemStateChanged(evt);
      }
    });
    jCheckBoxProgresteron.setBounds(10, 60, 120, 23);
    jLayeredPane3.add(jCheckBoxProgresteron, javax.swing.JLayeredPane.DEFAULT_LAYER);

    jCheckBoxHer2.setText("Her2");
    jCheckBoxHer2.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        jCheckBoxHer2ItemStateChanged(evt);
      }
    });
    jCheckBoxHer2.setBounds(10, 90, 110, 23);
    jLayeredPane3.add(jCheckBoxHer2, javax.swing.JLayeredPane.DEFAULT_LAYER);

    jButtonOk.setText("OK");
    jButtonOk.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButtonOkActionPerformed(evt);
      }
    });

    jButtonCancle.setText("Cancel");
    jButtonCancle.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButtonCancleActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLayeredPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jLayeredPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
          .addGroup(layout.createSequentialGroup()
            .addComponent(jButtonCancle)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jButtonOk, javax.swing.GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE)))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLayeredPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 78, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(jButtonCancle)
              .addComponent(jButtonOk)))
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jLayeredPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)))
        .addContainerGap())
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

    /**
     * The cancel event button
     *
     * @param evt The action event
     */
private void jButtonCancleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancleActionPerformed
    closeWindow();
}//GEN-LAST:event_jButtonCancleActionPerformed

/**
 * The closing window event
 *
 * @param evt The action event
 */
private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    main_window_.setActive(true);
}//GEN-LAST:event_formWindowClosing

/**
 * The ok button event
 *
 * @param evt The action event
 */
private void jButtonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOkActionPerformed
    main_window_.setTextMiningOptions(getOptions());
    closeWindow();
}//GEN-LAST:event_jButtonOkActionPerformed

/**
 * Checkbox event for ICD 10 & O event
 *
 * @param evt The action event
 */
private void jCheckBoxICD10OItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxICD10OItemStateChanged
    if(jCheckBoxICD10O.isSelected()){
        if(!options_.contains("-ICD"))
            options_.add("-ICD");
    }
    else
        options_.remove("-ICD");
}//GEN-LAST:event_jCheckBoxICD10OItemStateChanged

/**
 * Checkbox event for t stade
 *
 * @param evt The action event
 */
private void jCheckBoxTItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxTItemStateChanged
    if(jCheckBoxT.isSelected()){
        if(!options_.contains("-T"))
            options_.add("-T");
    }
    else
        options_.remove("-T");
}//GEN-LAST:event_jCheckBoxTItemStateChanged

/**
 * Checkbox event for n stade
 *
 * @param evt The action event
 */
private void jCheckBoxNItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxNItemStateChanged
    if(jCheckBoxN.isSelected()){
        if(!options_.contains("-N"))
            options_.add("-N");
    }
    else
        options_.remove("-N");
}//GEN-LAST:event_jCheckBoxNItemStateChanged

/**
 * Checkbox event for m stade
 *
 * @param evt The action event
 */
private void jCheckBoxMItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxMItemStateChanged
    if(jCheckBoxM.isSelected()){
        if(!options_.contains("-M"))
            options_.add("-M");
    }
    else
        options_.remove("-M");
}//GEN-LAST:event_jCheckBoxMItemStateChanged

/**
 * Checkbox event for r stade
 *
 * @param evt The action event
 */
private void jCheckBoxRItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxRItemStateChanged
    if(jCheckBoxR.isSelected()){
        if(!options_.contains("-R"))
            options_.add("-R");
    }
    else
        options_.remove("-R");
}//GEN-LAST:event_jCheckBoxRItemStateChanged

/**
 * Checkbox event for g stade
 *
 * @param evt The action event
 */
private void jCheckBoxGItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxGItemStateChanged
    if(jCheckBoxG.isSelected()){
        if(!options_.contains("-G"))
            options_.add("-G");
    }
    else
        options_.remove("-G");
}//GEN-LAST:event_jCheckBoxGItemStateChanged

/**
 * Checkbox event for l stade
 *
 * @param evt The action event
 */
private void jCheckBoxLItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxLItemStateChanged
    if(jCheckBoxL.isSelected()){
        if(!options_.contains("-L"))
            options_.add("-L");
    }
    else
        options_.remove("-L");
}//GEN-LAST:event_jCheckBoxLItemStateChanged

/**
 * Checkbox event for v stade
 *
 * @param evt The action event
 */
private void jCheckBoxVItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxVItemStateChanged
    if(jCheckBoxV.isSelected()){
        if(!options_.contains("-V"))
            options_.add("-V");
    }
    else
        options_.remove("-V");
}//GEN-LAST:event_jCheckBoxVItemStateChanged

/**
 * Checkbox event for oestrogen receptor
 *
 * @param evt The action event
 */
private void jCheckBoxOestrogenItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxOestrogenItemStateChanged
    if(jCheckBoxOestrogen.isSelected()){
        if(!options_.contains("-Oest"))
            options_.add("-Oest");
    }
    else
        options_.remove("-Oest");
}//GEN-LAST:event_jCheckBoxOestrogenItemStateChanged

/**
 * Checkbox event for Progresteron receptor
 *
 * @param evt The action event
 */
private void jCheckBoxProgresteronItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxProgresteronItemStateChanged
    if(jCheckBoxProgresteron.isSelected()){
        if(!options_.contains("-Prog"))
            options_.add("-Prog");
    }
    else
        options_.remove("-Prog");
}//GEN-LAST:event_jCheckBoxProgresteronItemStateChanged

/**
 * Checkbox event for Her2 receptor
 *
 * @param evt The action event
 */
private void jCheckBoxHer2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxHer2ItemStateChanged
    if(jCheckBoxHer2.isSelected()){
        if(!options_.contains("-Her2"))
            options_.add("-Her2");
    }
    else
        options_.remove("-Her2");
}//GEN-LAST:event_jCheckBoxHer2ItemStateChanged


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton jButtonCancle;
  private javax.swing.JButton jButtonOk;
  private javax.swing.JCheckBox jCheckBoxG;
  private javax.swing.JCheckBox jCheckBoxHer2;
  private javax.swing.JCheckBox jCheckBoxICD10O;
  private javax.swing.JCheckBox jCheckBoxL;
  private javax.swing.JCheckBox jCheckBoxM;
  private javax.swing.JCheckBox jCheckBoxN;
  private javax.swing.JCheckBox jCheckBoxOestrogen;
  private javax.swing.JCheckBox jCheckBoxProgresteron;
  private javax.swing.JCheckBox jCheckBoxR;
  private javax.swing.JCheckBox jCheckBoxT;
  private javax.swing.JCheckBox jCheckBoxV;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLayeredPane jLayeredPane1;
  private javax.swing.JLayeredPane jLayeredPane2;
  private javax.swing.JLayeredPane jLayeredPane3;
  // End of variables declaration//GEN-END:variables

}
