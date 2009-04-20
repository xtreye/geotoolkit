/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 * 
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotools.gui.swing.style;

import org.geotools.gui.swing.resource.MessageBundle;
import java.awt.Component;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import org.geotools.gui.swing.style.StyleElementEditor;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.style.MutableStyle;
import org.jdesktop.swingx.JXTitledPanel;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 */
public class JStylePane extends StyleElementEditor<MutableStyle>{
    
    private MapLayer layer = null;
    private MutableStyle style = null;
    
    /** Creates new form JRulePanel */
    public JStylePane() {
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {


        jXTitledPanel1 = new JXTitledPanel();
        jck_default = new JCheckBox();
        jLabel2 = new JLabel();
        jtf_name = new JTextField();
        jLabel1 = new JLabel();
        jtf_title = new JTextField();
        jtf_abstract = new JTextField();
        jLabel3 = new JLabel();

        jXTitledPanel1.setBorder(BorderFactory.createEtchedBorder());
        jXTitledPanel1.setTitle(MessageBundle.getString("general")); // NOI18N
        jck_default.setText(MessageBundle.getString("default")); // NOI18N
        jck_default.setOpaque(false);




        jLabel2.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel2.setText(MessageBundle.getString("name")); // NOI18N
        jLabel1.setFont(jLabel1.getFont().deriveFont(jLabel1.getFont().getStyle() | Font.BOLD));
        jLabel1.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel1.setText(MessageBundle.getString("title")); // NOI18N
        jLabel3.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel3.setText(MessageBundle.getString("abstract")); // NOI18N
        GroupLayout jXTitledPanel1Layout = new GroupLayout(jXTitledPanel1.getContentContainer());
        jXTitledPanel1.getContentContainer().setLayout(jXTitledPanel1Layout);
        jXTitledPanel1Layout.setHorizontalGroup(
            jXTitledPanel1Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jXTitledPanel1Layout.createSequentialGroup()
                .addGroup(jXTitledPanel1Layout.createParallelGroup(Alignment.LEADING)
                    .addGroup(jXTitledPanel1Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jLabel2, GroupLayout.DEFAULT_SIZE, 59, Short.MAX_VALUE)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(jtf_name, GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE))
                    .addGroup(jXTitledPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(jtf_title, GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE))
                    .addGroup(jXTitledPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel3)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(jtf_abstract, GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE))
                    .addGroup(jXTitledPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jck_default)))
                .addContainerGap())
        );

        jXTitledPanel1Layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {jLabel1, jLabel2, jLabel3});

        jXTitledPanel1Layout.setVerticalGroup(
            jXTitledPanel1Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jXTitledPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jXTitledPanel1Layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jtf_name, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(jXTitledPanel1Layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jtf_title, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(jXTitledPanel1Layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jtf_abstract, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(jck_default)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addComponent(jXTitledPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addComponent(jXTitledPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JXTitledPanel jXTitledPanel1;
    private JCheckBox jck_default;
    private JTextField jtf_abstract;
    private JTextField jtf_name;
    private JTextField jtf_title;
    // End of variables declaration//GEN-END:variables

    @Override
    public void setLayer(MapLayer layer) {
        this.layer = layer;
    }

    @Override
    public MapLayer getLayer() {
        return layer;
    }

    @Override
    public void parse(MutableStyle target) {
        this.style = target;
        if(style != null){
            jtf_name.setText(style.getName());
            jtf_title.setText(style.getDescription().getTitle().toString());
            jtf_abstract.setText(style.getDescription().getAbstract().toString());
            jck_default.setSelected(style.isDefault());
        }
    }

    @Override
    public MutableStyle create() {
        if(style == null){
            style = getStyleFactory().style();
        }
        
        style.setName(jtf_name.getText());
        style.setDescription(getStyleFactory().description(
                jtf_title.getText(), 
                jtf_abstract.getText()));
        style.setDefault(jck_default.isSelected());
        return style;
    }
    
    @Override
    public void apply() {
        create();
    }
    
}
