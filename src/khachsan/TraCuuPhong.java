/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package khachsan;

import java.awt.Dialog;
import javax.swing.JFrame;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 *
 * @author Pham Duc Luong
 */
public class TraCuuPhong extends javax.swing.JFrame {

    /**
     * Creates new form TraCuuPhong
     */
    DefaultTableModel model;
    Connect cn=null;
    String url=null;
    ResultSet rs=null;
    List<String> DS_PhongCanXoa=null;
    List<String> PhongCanSua=null;
    Boolean check=true;
    int[] dscx=null;
  
    public TraCuuPhong(Connect cn1) {
        initComponents();
        this.setLocation(300, 60);
        cn=cn1;
        model=(DefaultTableModel)jT_DsPhong.getModel();
//        Loaddulieuvaomodel("");
        jcbTinhTrang.removeAllItems();
        jcbTinhTrang.addItem("Trong");
        jcbTinhTrang.addItem("DaThue");
        jcbTinhTrang.addItem("TatCa");
        jcbMaLoaiPhong.removeAllItems();
        jcbMaLoaiPhong.addItem("TatCa");
        LayDuLieuTuData();
        
    }
  
    public void Loaddulieuvaomodel(String url2){
        Boolean kiemtra=false;
        try{
            url = "SELECT MaPhong,TenPhong,LOAIPHONG.MaLoaiPhong,LOAIPHONG.TenLoaiPhong,LOAIPHONG.DonGia,GhiChu,TinhTrang\n"
                        + "FROM PHONG,LOAIPHONG\n"
                        + "WHERE PHONG.MaLoaiPhong=LOAIPHONG.MaLoaiPhong";
            if (url2.length()!= 0) {
                url += url2;
        
            }
            rs=cn.ExcuteQuery(url);
           
            while(rs.next()){
                model.addRow(new Object[]{rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getInt(5),rs.getString(6),rs.getString(7)});  
                kiemtra=true;
            }
            if(kiemtra==false){
                JOptionPane.showMessageDialog(null, "Không tìm thấy");
            }
            
        }catch(Exception ex){
            ex.printStackTrace();
        }
            
    }
    public void LayDuLieuTuData(){
        String url="SELECT MaLoaiPhong FROM LOAIPHONG";
        
        try {
            ResultSet rs = this.cn.ExcuteQuery(url);
            while(rs.next()){
                jcbMaLoaiPhong.addItem(rs.getString(1));
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
    }
    public void XoaDuLieuTrongModel(){
        model=(DefaultTableModel)jT_DsPhong.getModel();
        if (model.getRowCount() > 0) {
            while(model.getRowCount()!=0){
                model.removeRow(0);
            }             
        }
    }
    public void CapNhatDongChoModel(int index,List<String> a){
        for(int i=1;i<a.size();i++){
            model.setValueAt(a.get(i), index, i);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtMaPhong = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtTenPhong = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jcbMaLoaiPhong = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        jcbTinhTrang = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        txtDonGiatu = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtDen = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jT_DsPhong = new javax.swing.JTable();
        jButton4 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel1.setText("TRA CỨU PHÒNG");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Thông tin phòng"));

        jLabel2.setText("Mã Phòng");

        jLabel3.setText("Tên Phòng");

        jLabel4.setText("Mã Loại Phòng");

        jcbMaLoaiPhong.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel5.setText("Tình trạng");

        jcbTinhTrang.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel6.setText("Đơn Giá từ");

        jLabel7.setText("Đến");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel6)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jcbMaLoaiPhong, 0, 120, Short.MAX_VALUE)
                    .addComponent(txtMaPhong)
                    .addComponent(txtDonGiatu))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel5)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDen, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTenPhong, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcbTinhTrang, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtMaPhong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(txtTenPhong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jcbMaLoaiPhong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jcbTinhTrang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtDonGiatu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(txtDen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Chức năng"));

        jButton1.setText("Tìm");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton5.setText("Sửa");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton3.setText("Xóa");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton2.setText("Thêm mới");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Danh sách phòng"));

        jT_DsPhong.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mã Phòng", "Tên Phòng", "Mã Loại Phòng", "Tên Loại Phòng", "Đơn giá", "Ghi chú", "Tình trạng"
            }
        ));
        jT_DsPhong.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jT_DsPhongMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jT_DsPhong);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 696, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                .addContainerGap())
        );

        jButton4.setText("Thoát");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(281, 281, 281)
                .addComponent(jLabel1)
                .addContainerGap(283, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        int b = JOptionPane.showConfirmDialog(null, "Bạn có muốn thoát khỏi Tra Cứu Phòng không?", "Thoát", JOptionPane.YES_OPTION);
        if (b == 0) {
            this.setVisible(false);
            DanhMucPhong a=new DanhMucPhong(cn);
            a.setVisible(true);  
        } 
    }//GEN-LAST:event_jButton4ActionPerformed
    public Boolean XoaMaHoaDonLienQuan(String a,List<String> b) {
        String chuoi1="";
        String chuoi2="";
        b.add("Không xóa được\nDo Mã Phòng "+a+" có trong Phiếu Thuê Phòng: ");
        try {
            String url = "SELECT MaPhieuThuePhong\n"
                    + "FROM PHONG,PHIEUTHUEPHONG\n"
                    + "WHERE PHONG.MaPhong=PHIEUTHUEPHONG.MaPhong AND PHONG.MaPhong='" + a + "'";
            ResultSet rs = cn.ExcuteQuery(url);
            while (rs.next()) {
                chuoi1=chuoi1+rs.getString(1)+"  ";
            }
            if(chuoi1.equals("")==false){
                 b.set(0, b.get(0)+chuoi1);
            }
            url="SELECT DISTINCT CT_HOADON.MaHoaDon\n" +
                "FROM PHONG,PHIEUTHUEPHONG,CT_HOADON\n" +
                "WHERE PHONG.MaPhong=PHIEUTHUEPHONG.MaPhong AND PHIEUTHUEPHONG.MaPhieuThuePhong=CT_HOADON.MaPhieuThuePhong AND PHONG.MaPhong='"+a+"'";
            rs=cn.ExcuteQuery(url);
            while(rs.next()){
                chuoi2=chuoi2+rs.getString(1)+"  ";
            }
            if(chuoi2.equals("")==false){
                 b.set(0, b.get(0)+"\nVà có trong Hóa Đơn: "+chuoi2);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if(chuoi2.equals("")==false||chuoi1.equals("")==false){
            return false;
        }
        return true;
    }
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        
        if(DS_PhongCanXoa==null)
           JOptionPane.showMessageDialog(null, "Chưa chọn mã phòng để xóa");
        else {
            if (check == false) {
                JOptionPane.showMessageDialog(null, "Những phòng muốn xóa có tình trạng Đã Thuê");
            } else {
                    try {
                        int dem=0;
                        for (int i = 0; i < DS_PhongCanXoa.size(); i++) {
                            List<String> b=new ArrayList<>();
                           
                            if(XoaMaHoaDonLienQuan(DS_PhongCanXoa.get(i),b)==false){
                                JOptionPane.showMessageDialog(null, b.get(0));
                            }
                            else{
                                String url = String.format("DELETE FROM PHONG WHERE MaPhong='%s'", DS_PhongCanXoa.get(i));
                                cn.ExcuteUpdate(url);
                                model.removeRow(dscx[i] - dem); 
                                dem++;
                            }                                                    
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
            }
        }
        dscx=null;
           
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        int index=-1;
        index = jT_DsPhong.getSelectedRow();
        if(index<0)
            JOptionPane.showMessageDialog(null, "Chưa chọn Phòng để sửa");
        else{
            model = (DefaultTableModel) jT_DsPhong.getModel();
            PhongCanSua=new ArrayList<>(); 
            if(model.getValueAt(index, 6).equals("DaThue")){
                JOptionPane.showMessageDialog(null, "Phòng đã thuê không thể chỉnh sửa");
            }         
            else {
                for (int i = 0; i < 7; i++) {
                    PhongCanSua.add(model.getValueAt(index, i).toString());
                }
                try {
                    SuaPhong sp = new SuaPhong(this,index,cn);
                    sp.setDuLieu(PhongCanSua);
                    sp.setVisible(true);
                    this.setEnabled(false);
//                    NewJPanel a = new NewJPanel();
//                    JOptionPane.showMessageDialog(null, a,"asdf",JOptionPane.PLAIN_MESSAGE);
//                    JDialog d = new JDialog(this, true);
//                    d.getContentPane().add(a);
//                    d.pack();
//                    d.setVisible(true);               
//                    JOptionPane.showMessageDialog(null, sp.getPanel(),"",JOptionPane.PLAIN_MESSAGE);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
 
            }
     
        }
        PhongCanSua=null;
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jT_DsPhongMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jT_DsPhongMouseClicked
        check=true;
        dscx = jT_DsPhong.getSelectedRows();
        DS_PhongCanXoa = new ArrayList<>();
        model = (DefaultTableModel) jT_DsPhong.getModel();
        for (int i = 0; i <dscx.length ; i++) {
            if(model.getValueAt(dscx[i], 6).toString().equals("DaThue")){
                check=false;
            }
            DS_PhongCanXoa.add(model.getValueAt(dscx[i], 0).toString());           
        }    
    }//GEN-LAST:event_jT_DsPhongMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Boolean checkdongia=true;
        String a = "";
        String url1 = null;
        if (txtMaPhong.getText().length() != 0) {
            url1 = String.format(" AND MaPhong='%s'", txtMaPhong.getText());
            a += url1;
        }
        if (txtTenPhong.getText().length() != 0) {
            url1 = String.format(" AND TenPhong='%s'", txtTenPhong.getText());
            a += url1;
        }
        if (txtDonGiatu.getText().length() != 0 || txtDen.getText().length() != 0) {
            try {
                int dongiatu = Integer.parseInt(txtDonGiatu.getText());
                int den = Integer.parseInt(txtDen.getText());
                if(dongiatu<0||den<0){
                    JOptionPane.showMessageDialog(null, "Giá không được âm");
                    checkdongia=false;
                }
                else if(dongiatu>den && dongiatu>0 && den>0){
                    JOptionPane.showMessageDialog(null, "Đơn giá từ lớn hơn Đơn giá đến");
                    checkdongia=false;
                }
                
                else {
                    url1 = String.format(" AND DonGia>=%d AND DonGia<=%d", Integer.parseInt(txtDonGiatu.getText()), Integer.parseInt(txtDen.getText()));
                    a += url1;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Dữ liệu nhập vào Đơn giá không phải kiểu số");
                checkdongia=false;
            }
        }
        if (jcbMaLoaiPhong.getSelectedItem() != "TatCa") {
            url1 = String.format(" AND LOAIPHONG.MaLoaiPhong='%s'", jcbMaLoaiPhong.getSelectedItem());
            a += url1;
        }
        if (jcbTinhTrang.getSelectedItem() != "TatCa") {
            url1 = String.format(" AND TinhTrang='%s'", jcbTinhTrang.getSelectedItem());
            a += url1;
        }
        if (checkdongia == true) {
            XoaDuLieuTrongModel();
            Loaddulieuvaomodel(a);
        }
        
    }//GEN-LAST:event_jButton1ActionPerformed
    
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        txtDen.setText(null);
        txtDonGiatu.setText(null);
        txtMaPhong.setText(null);
        txtTenPhong.setText(null);
        XoaDuLieuTrongModel();
    }//GEN-LAST:event_jButton2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TraCuuPhong.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TraCuuPhong.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TraCuuPhong.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TraCuuPhong.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TraCuuPhong(new Connect(null)).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jT_DsPhong;
    private javax.swing.JComboBox jcbMaLoaiPhong;
    private javax.swing.JComboBox jcbTinhTrang;
    private javax.swing.JTextField txtDen;
    private javax.swing.JTextField txtDonGiatu;
    private javax.swing.JTextField txtMaPhong;
    private javax.swing.JTextField txtTenPhong;
    // End of variables declaration//GEN-END:variables
}
