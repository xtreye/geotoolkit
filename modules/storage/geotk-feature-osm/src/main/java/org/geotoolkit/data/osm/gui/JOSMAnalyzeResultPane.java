/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.data.osm.gui;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;

import org.geotoolkit.data.FeatureStore;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.osm.xml.OSMXMLConstants;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.feature.Feature;
import org.geotoolkit.filter.DefaultPropertyName;
import org.geotoolkit.filter.sort.DefaultSortBy;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.combobox.ListComboBoxModel;

import org.opengis.filter.FilterFactory;
import org.opengis.filter.sort.SortBy;
import org.opengis.filter.sort.SortOrder;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class JOSMAnalyzeResultPane extends javax.swing.JPanel {

    private FeatureStore store;
    private AnalyzeResult result;

    /** Creates new form JOSMAnalyzeResultPane */
    public JOSMAnalyzeResultPane() {
        initComponents();

        guiValues.setModel(new DefaultListModel());
        guiValues.setCellRenderer(new ValueRenderer());
        guiCross.setCellRenderer(new ValueRenderer());

    }

    public void setDataStore(final FeatureStore store) {
        this.store = store;
    }

    public void setAnalyzeResult(final AnalyzeResult result) {
        this.result = result;
    }

    public void sortBy(final boolean occurence){
        if(result != null){
            final List<Entry<String,Integer>> values = new ArrayList<Entry<String,Integer>>();
            values.addAll(result.values.entrySet());

            if(occurence){
                Collections.sort(values, new Comparator<Entry<String,Integer>>(){
                    @Override
                    public int compare(Entry<String,Integer> t, Entry<String,Integer> t1) {
                        return t1.getValue() - t.getValue();
                    }
                });
            }else{
                Collections.sort(values, new Comparator<Entry<String,Integer>>(){
                    @Override
                    public int compare(Entry<String,Integer> t, Entry<String,Integer> t1) {
                        return t.getKey().compareTo(t1.getKey());
                    }
                });
            }

            guiValues.setModel(new ListComboBoxModel(values));
        }
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        guiSearchId = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        guiIds = new javax.swing.JTextArea();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        guiValues = new javax.swing.JList();
        jPanel2 = new javax.swing.JPanel();
        guiSearchCross = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        guiCross = new javax.swing.JList();

        setLayout(new java.awt.BorderLayout());

        guiSearchId.setText("Search Id for this tag value");
        guiSearchId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guiSearchIdActionPerformed(evt);
            }
        });

        guiIds.setColumns(20);
        guiIds.setLineWrap(true);
        guiIds.setRows(5);
        jScrollPane3.setViewportView(guiIds);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 521, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(guiSearchId)
                .addContainerGap(334, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(guiSearchId)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE))
        );

        add(jPanel3, java.awt.BorderLayout.SOUTH);

        jSplitPane1.setDividerLocation(200);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Values");

        jScrollPane1.setViewportView(guiValues);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE))
        );

        jSplitPane1.setLeftComponent(jPanel1);

        guiSearchCross.setText("Search cross tags");
        guiSearchCross.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guiSearchCrossActionPerformed(evt);
            }
        });

        jScrollPane2.setViewportView(guiCross);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 315, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(guiSearchCross)
                .addContainerGap(184, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(guiSearchCross)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE))
        );

        jSplitPane1.setRightComponent(jPanel2);

        add(jSplitPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void guiSearchCrossActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guiSearchCrossActionPerformed
        final Entry<String,Integer> selected = (Entry<String, Integer>) guiValues.getSelectedValue();
        if(selected == null){
            new CrossThread(null).start();
        }else{
            new CrossThread(selected.getKey()).start();
        }

    }//GEN-LAST:event_guiSearchCrossActionPerformed

    private void guiSearchIdActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guiSearchIdActionPerformed
        final Entry<String,Integer> selected = (Entry<String, Integer>) guiValues.getSelectedValue();
        if(selected == null){
            new IdThread(null).start();
        }else{
            new IdThread(selected.getKey()).start();
        }
    }//GEN-LAST:event_guiSearchIdActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList guiCross;
    private javax.swing.JTextArea guiIds;
    private javax.swing.JButton guiSearchCross;
    private javax.swing.JButton guiSearchId;
    private javax.swing.JList guiValues;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSplitPane jSplitPane1;
    // End of variables declaration//GEN-END:variables

    private static final class ValueRenderer extends DefaultListCellRenderer{

        @Override
        public Component getListCellRendererComponent(final JList jlist, final Object o, final int i, final boolean bln, final boolean bln1) {
            final JLabel lbl = (JLabel) super.getListCellRendererComponent(jlist, o, i, bln, bln1);
            
            if(o instanceof Entry){
                final Entry t = (Entry) o;
                if(t.getValue() instanceof Integer){
                    lbl.setText("<html><b>" + t.getValue() +"</b> - "+t.getKey()+"</html>");
                }else{
                    AnalyzeResult res = (AnalyzeResult) t.getValue();
                    lbl.setText("<html><b>" + res.tagCount +"</b> - "+t.getKey()+"</html>");
                }
                
            }

            return lbl;
        }

    }


    private class CrossThread extends Thread{

        private final String tagValue;

        public CrossThread(final String value) {
            this.tagValue = value;
        }

        private void analyze() throws DataStoreException{

            final String idField;
            if(result.tableName.equals("WayTag")){
                idField = "wayId";
            }else if(result.tableName.equals("NodeTag")){
                idField = "nodeId";
            }else if(result.tableName.equals("RelationTag")){
                idField = "relationId";
            }else{
                throw new IllegalArgumentException("unexpected table name : " + result.tableName );
            }

            final QueryBuilder qb = new QueryBuilder();
            qb.setTypeName(store.getFeatureType(result.tableName).getName());
            qb.setSortBy(new SortBy[]{new DefaultSortBy(new DefaultPropertyName(idField), SortOrder.ASCENDING)});
            final FeatureReader reader = store.getFeatureReader(qb.buildQuery());

            final long before = System.currentTimeMillis();
            System.out.println("start cross analyze");

            //collect results
            final Map<String,AnalyzeResult> analyze = new HashMap<String, AnalyzeResult>();
            final Map<String,AnalyzeResult> byId = new HashMap<String, AnalyzeResult>();
            try{
                String lastId = "-1";
                while(reader.hasNext()){
                    final Feature f = reader.next();
                    final String currentID = f.getPropertyValue(idField).toString();
                    if(!lastId.equals(currentID)){
                        final AnalyzeResult res = byId.get(result.tagKey);
                        if(res == null || (tagValue != null && !res.values.containsKey(tagValue))){
                        }else{
                            for(Entry<String,AnalyzeResult> entry : byId.entrySet()){
                                if(entry.getKey().equals(result.tagKey)) continue;

                                AnalyzeResult rr = analyze.get(entry.getKey());
                                if(rr == null){
                                    rr = new AnalyzeResult(result.tableName,entry.getKey());
                                    analyze.put(entry.getKey(), rr);
                                }
                                rr.tagCount++;
                                for(Entry<String,Integer> rec : entry.getValue().values.entrySet()){
                                    rr.incrementValue(rec.getKey());
                                }
                            }
                        }

                        byId.clear();
                        lastId = currentID;
                    }

                    final String key = f.getPropertyValue(OSMXMLConstants.ATT_TAG_KEY).toString();
                    final String value = f.getPropertyValue(OSMXMLConstants.ATT_TAG_VALUE).toString();

                    AnalyzeResult ar = byId.get(key);
                    if(ar == null){
                        ar = new AnalyzeResult(result.tableName,key);
                        byId.put(key, ar);
                    }
                    ar.tagCount++;
                    ar.incrementValue(value);
                }
            }finally{
                reader.close();
            }

            if(true){
                final AnalyzeResult res = byId.get(result.tagKey);
                if(res == null || (tagValue != null && !res.values.containsKey(tagValue))){
                }else{
                    for(Entry<String,AnalyzeResult> entry : byId.entrySet()){
                        if(entry.getKey().equals(result.tagKey)) continue;

                        AnalyzeResult rr = analyze.get(entry.getKey());
                        if(rr == null){
                            rr = new AnalyzeResult(result.tableName,entry.getKey());
                            byId.put(entry.getKey(), rr);
                        }
                        rr.tagCount++;
                        for(Entry<String,Integer> rec : entry.getValue().values.entrySet()){
                            rr.incrementValue(rec.getKey());
                        }
                    }
                }

                byId.clear();
            }


            final long after = System.currentTimeMillis();
            System.out.println("end cross analyze : " + (after-before) +" ms");
            System.out.println("size = "+analyze.size());


            final List<Entry<String,AnalyzeResult>> values = new ArrayList<Entry<String,AnalyzeResult>>();
            values.addAll(analyze.entrySet());

            Collections.sort(values, new Comparator<Entry<String,AnalyzeResult>>(){
                @Override
                public int compare(Entry<String,AnalyzeResult> t, Entry<String,AnalyzeResult> t1) {
                    return t1.getValue().tagCount - t.getValue().tagCount;
                }
            });

            guiCross.setModel(new ListComboBoxModel(values));

        }

        @Override
        public void run() {
            try {
                analyze();
            } catch (DataStoreException ex) {
                JXErrorPane.showDialog(ex);
            }
        }

    }

    private class IdThread extends Thread{

        private final String tagValue;

        public IdThread(final String value) {
            this.tagValue = value;
        }

        private void analyze() throws DataStoreException{

            final String idField;
            if(result.tableName.equals("WayTag")){
                idField = "wayId";
            }else if(result.tableName.equals("NodeTag")){
                idField = "nodeId";
            }else if(result.tableName.equals("RelationTag")){
                idField = "relationId";
            }else{
                throw new IllegalArgumentException("unexpected table name : " + result.tableName );
            }

            FilterFactory FF = FactoryFinder.getFilterFactory(null);
            final QueryBuilder qb = new QueryBuilder();
            qb.setTypeName(store.getFeatureType(result.tableName).getName());
            qb.setFilter(
                    FF.and(
                        FF.equals(FF.property("k"),FF.literal(result.tagKey)),
                        FF.equals(FF.property("v"),FF.literal(tagValue))
                        )
                    );
            final FeatureReader reader = store.getFeatureReader(qb.buildQuery());

            final long before = System.currentTimeMillis();
            System.out.println("start id search");

            //collect results
            StringBuilder sb = new StringBuilder();
            try{
                while(reader.hasNext()){
                    Feature f = reader.next();
                    sb.append(f.getPropertyValue(idField)).append(',');
                }
            }finally{
                reader.close();
            }

            if(sb.length()>0){
                sb.setLength(sb.length()-1);
            }

            final long after = System.currentTimeMillis();
            System.out.println("end id search : " + (after-before) +" ms");
            guiIds.setText(sb.toString());
        }

        @Override
        public void run() {
            try {
                analyze();
            } catch (DataStoreException ex) {
                JXErrorPane.showDialog(ex);
            }
        }

    }

}
