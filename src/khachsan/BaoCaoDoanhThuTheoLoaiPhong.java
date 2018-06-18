/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package khachsan;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JOptionPane;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.*;
import org.jfree.chart.plot.PiePlot;
/**
 *
 * @author Pham Duc Luong
 */
public class BaoCaoDoanhThuTheoLoaiPhong extends javax.swing.JFrame {

    /**
     * Creates new form BaoCaoDoanhThuTheoLoaiPhong
     */
    Connect cn=null;
    String url=null;
    float tongtien=0;
    DefaultTableModel model;
//    List<String> DSLP=null;
//    List<Float> SoLieu=null;
    DefaultPieDataset dataset=null;
    ChartPanel panel1=new ChartPanel(null);
    List<List<String>> danhsachLoaiPhong=null;
    QuanLiKhachSan ql=null;
    String MBCDT;
    public BaoCaoDoanhThuTheoLoaiPhong(Connect cn1) {
        initComponents();
        this.setLocation(300, 60);
        cn=cn1;
        tongtien=0;
        model=(DefaultTableModel)jT_DS.getModel();
//        DSLP=new ArrayList<>();
//        SoLieu=new ArrayList<>();
        danhsachLoaiPhong =new ArrayList<>();
        ql=new QuanLiKhachSan(cn);
//        txtMaBCDT.setText(MaBCDT());
        
    }
     public void setChart (){
        dataset=new DefaultPieDataset();
        for(int i=0;i<danhsachLoaiPhong.size();i++){
            dataset.setValue(danhsachLoaiPhong.get(i).get(0), new Double(danhsachLoaiPhong.get(i).get(3)));
        }
    }
    public void XoaModle(){
        int n=model.getRowCount();
        for(int i=0;i<n;i++){
            model.removeRow(0);
        }
        txtTongDoanhThu.setText("0");
        panel1.setVisible(false);
    }
    public void creatchart(){
        JFreeChart chart=ChartFactory.createPieChart(null, dataset,true, true,false);
        PiePlot P=(PiePlot)chart.getPlot();
        panel1=new ChartPanel(chart);
        panel1.setPreferredSize(new java.awt.Dimension(150, 150));
        jPanel1.setLayout(new java.awt.BorderLayout());
        jPanel1.add(panel1,BorderLayout.CENTER);
        jPanel1.validate();
    }
    public Boolean KiemTraTonTai(List<String> a){
        ResultSet rs=null;
        try{
            String url="SELECT MaBCDT,TongDoanhThu FROM BAOCAODOANHTHU WHERE Thang="+Integer.parseInt(jcbThang.getSelectedItem().toString())+" AND Nam="+Integer.parseInt(txtNam.getText());
            rs=cn.ExcuteQuery(url);
            if(rs.next()==false)
                return false;
            a.add(rs.getString(1));
            a.add(rs.getString(2));
            return true;
         }catch(Exception ex){
            ex.printStackTrace();
        }
        return true;
    }
    public void LoadDuLieu(){
        List<String> a=new ArrayList<>();
        KiemTraTonTai(a);
        txtTongDoanhThu.setText(a.get(1));
        try {
            String url = "select LOAIPHONG.MaLoaiPhong, LOAIPHONG.TenLoaiPhong, DoanhThu,TiLe\n"
                    + "from LOAIPHONG, CT_BCDT\n"
                    + "where LOAIPHONG.MaLoaiPhong=CT_BCDT.MaLoaiPhong AND MaBCDT='" + a.get(0) + "'";
            ResultSet rs = cn.ExcuteQuery(url);
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4)});
                List <String> ds1=new ArrayList<>();
                for(int i=0;i<model.getColumnCount();i++){
                    ds1.add(rs.getString(i+1));
                }
                danhsachLoaiPhong.add(ds1);
             
            }
            if (Float.parseFloat(a.get(1)) == 0) {
                panel1.setVisible(false);  
                danhsachLoaiPhong=new ArrayList<>();  
                return;
            }
            setChart();
            creatchart();
            danhsachLoaiPhong=new ArrayList<>();    
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
    }
    public String PhatSinhMaBCDT(List<String> dsp){
        Boolean check=false;
        int a=0;
        String mabcdt="BCDT";
        String mabcdtphatsinh=null;
        int j=1;
        if(dsp.size()==0){
            return "BCDT1";
        }
        while(check==false){
            a=0;
            for(int i=0;i<dsp.size();i++){
                mabcdtphatsinh=mabcdt+""+j;
                if(dsp.get(i).equals(mabcdtphatsinh)){
                    break;
                }
                else
                    a++;
            }
            j++;
            if(a==dsp.size())
                check=true;
        }
        return mabcdtphatsinh;
    }
    public String MaBCDT(){
        String url="SELECT MaBCDT FROM BAOCAODOANHTHU";
        List<String> dsp=new ArrayList<>();
        try{
    
            ResultSet rs=cn.ExcuteQuery(url);
            while(rs.next()){
                dsp.add(rs.getString(1));  
            }        
        }
        catch(Exception ex){
            ex.printStackTrace();
        }

        return PhatSinhMaBCDT(dsp);
    }
    public void TongTien(){
        url="SELECT SUM(HOADON.TriGia)\n" +
            "FROM HOADON\n" +
            "WHERE YEAR(NgayLapHoaDon)="+Integer.parseInt(txtNam.getText())+" AND MONTH(NgayLapHoaDon)="+Integer.parseInt(jcbThang.getSelectedItem().toString());

        try{
            ResultSet rs=cn.ExcuteQuery(url);
            while(rs.next()){
                tongtien=Float.parseFloat(rs.getString(1));
            }
        }catch(Exception ex){

            tongtien=0;
        }
        
    }
    public void LayBangLoaiPhong(){
        danhsachLoaiPhong =new ArrayList<>();
        try{
            String url="SELECT * FROM LOAIPHONG";
            ResultSet rs=cn.ExcuteQuery(url);
            while(rs.next()){
                List<String> ds=new ArrayList<>();
                ds.add(rs.getString(1));
                ds.add(rs.getString(2));
                danhsachLoaiPhong.add(ds);
            }
            
        }catch(Exception ex){
            ex.printStackTrace();
        }
        KhoiTaoGiaTriChoLoaiPhong();
    }
    public void KhoiTaoGiaTriChoLoaiPhong(){
        for(int i=0;i<danhsachLoaiPhong.size();i++){
            danhsachLoaiPhong.get(i).add("0");
            danhsachLoaiPhong.get(i).add("0");
        }
    }
    public void CapNhatGiaTriChoLoaiPhong(String a, float DoanhThu,float Tile) {
        for (int i = 0; i < danhsachLoaiPhong.size(); i++) {
            if (danhsachLoaiPhong.get(i).get(0).equals(a)) {
                danhsachLoaiPhong.get(i).set(2, "" + DoanhThu);
                danhsachLoaiPhong.get(i).set(3, "" + Tile);

            }
        }
    }
    public void LuuBaoCao(){
        try{
 
            String url=String.format("INSERT INTO BAOCAODOANHTHU VALUES('%s',%d,%d,%f)",MBCDT,Integer.parseInt(jcbThang.getSelectedItem().toString()),Integer.parseInt(txtNam.getText()),Float.parseFloat(txtTongDoanhThu.getText()));
            cn.ExcuteUpdate(url);
            
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    public void LuuCT_BaoCao(){
        try{
            for(int i=0;i<danhsachLoaiPhong.size();i++){
                String url=String.format("INSERT INTO CT_BCDT VALUES('%s','%s',%f,%f)",MBCDT,danhsachLoaiPhong.get(i).get(0),Float.parseFloat(danhsachLoaiPhong.get(i).get(2)),Float.parseFloat(danhsachLoaiPhong.get(i).get(3)));
                cn.ExcuteUpdate(url);
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    public Boolean KiemTraNgayXemBaoCao(){
        Calendar c=Calendar.getInstance();
        int thang=c.get(c.MONTH)+1;
        int nam=c.get(c.YEAR);
        int nam1 = Integer.parseInt(txtNam.getText());
        if (Integer.parseInt(txtNam.getText()) > nam) {
            JOptionPane.showMessageDialog(null, "Năm muốn xem Báo cáo lớn hơn Năm hiện tại");
            return false;
        } else {
            if (Integer.parseInt(jcbThang.getSelectedItem().toString()) > thang) {
                JOptionPane.showMessageDialog(null, "Tháng muốn xem Báo cáo lớn hơn tháng hiện tại");
                return false;
            } else if (Integer.parseInt(jcbThang.getSelectedItem().toString()) == thang) {
                JOptionPane.showMessageDialog(null, "Chưa kết thúc tháng nên không thể xem được Báo cáo");
                return false;
            }
        }
        return true;
    }

   
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jP_Thongtin = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jcbThang = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        txtNam = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtTongDoanhThu = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jT_DS = new javax.swing.JTable();
        jButton2 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();

        jLabel3.setText("jLabel3");

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel1.setText("BÁO CÁO DOANH THU THEO LOẠI PHÒNG");

        jP_Thongtin.setBorder(javax.swing.BorderFactory.createTitledBorder("Thông tin Tháng và Năm"));

        jLabel2.setText("Tháng");

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Chức năng"));

        jButton1.setText("Xem báo cáo");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jcbThang.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" }));

        jLabel4.setText("Năm");

        jLabel6.setText("Tổng Doanh Thu");

        txtTongDoanhThu.setEnabled(false);

        javax.swing.GroupLayout jP_ThongtinLayout = new javax.swing.GroupLayout(jP_Thongtin);
        jP_Thongtin.setLayout(jP_ThongtinLayout);
        jP_ThongtinLayout.setHorizontalGroup(
            jP_ThongtinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jP_ThongtinLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jP_ThongtinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jP_ThongtinLayout.createSequentialGroup()
                        .addGroup(jP_ThongtinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel4))
                        .addGap(53, 53, 53))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jP_ThongtinLayout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addGroup(jP_ThongtinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jP_ThongtinLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jcbThang, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(83, 83, 83))
                    .addGroup(jP_ThongtinLayout.createSequentialGroup()
                        .addGroup(jP_ThongtinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtTongDoanhThu, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtNam, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jP_ThongtinLayout.setVerticalGroup(
            jP_ThongtinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jP_ThongtinLayout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(jP_ThongtinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jP_ThongtinLayout.createSequentialGroup()
                        .addGroup(jP_ThongtinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jcbThang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jP_ThongtinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(txtNam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jP_ThongtinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtTongDoanhThu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Danh sách các loại phòng"));

        jT_DS.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mã loại phòng", "Tên loại phòng", "Doanh Thu", "Tỉ lệ"
            }
        ));
        jScrollPane1.setViewportView(jT_DS);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButton2.setText("Thoát");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Biểu đồ"));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jP_Thongtin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 214, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(118, 118, 118))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jP_Thongtin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
            if(txtNam.getText().length()==0){
                JOptionPane.showMessageDialog(null, "Chưa nhập năm cần xem báo cáo");
                return;
            }
            int nam = Integer.parseInt(txtNam.getText());
            
            if (KiemTraNgayXemBaoCao() == false) {
                XoaModle();
                return;
            }
            if (KiemTraTonTai(new ArrayList<String>()) == true) {
                XoaModle();
                LoadDuLieu();
                if(Float.parseFloat(txtTongDoanhThu.getText())==0){
                     JOptionPane.showMessageDialog(null, "Không có doanh thu");
                }
                
                return;
            }
            XoaModle();
            TongTien();
            System.out.println(tongtien);
            LayBangLoaiPhong();
            if (tongtien != 0) {
                try {
                    MBCDT=MaBCDT();
                    String url = "SELECT LOAIPHONG.MaLoaiPhong,LOAIPHONG.TenLoaiPhong ,SUM(CT_HOADON.ThanhTien)\n"
                    + "FROM LOAIPHONG,HOADON,PHONG,CT_HOADON,PHIEUTHUEPHONG\n"
                    + "WHERE HOADON.MaHoaDon=CT_HOADON.MaHoaDon AND CT_HOADON.MaPhieuThuePhong=PHIEUTHUEPHONG.MaPhieuThuePhong AND PHIEUTHUEPHONG.MaPhong=PHONG.MaPhong AND PHONG.MaLoaiPhong=LOAIPHONG.MaLoaiPhong AND YEAR(NgayLapHoaDon)=" + nam + " AND MONTH(NgayLapHoaDon)=" + Integer.parseInt(jcbThang.getSelectedItem().toString()) +"\n"
                    + "GROUP BY LOAIPHONG.MaLoaiPhong,LOAIPHONG.TenLoaiPhong";
                    ResultSet rs = cn.ExcuteQuery(url);
                    while (rs.next()) {
                        CapNhatGiaTriChoLoaiPhong(rs.getString(1), Float.parseFloat(rs.getString(3)), (Float.parseFloat(rs.getString(3)) / tongtien));
                        System.out.println(Float.parseFloat(rs.getString(3)));
                    }
                    for (int i = 0; i < danhsachLoaiPhong.size(); i++) {
                        model.addRow(new Object[]{danhsachLoaiPhong.get(i).get(0), danhsachLoaiPhong.get(i).get(1), danhsachLoaiPhong.get(i).get(2), danhsachLoaiPhong.get(i).get(3)});
                    }
                    
                    txtTongDoanhThu.setText("" + tongtien);
                    LuuBaoCao();
                    LuuCT_BaoCao();
                 

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                setChart();
                creatchart();
            }
            else{
                MBCDT=MaBCDT();
                txtTongDoanhThu.setText("0"); 
                LuuBaoCao();
                LuuCT_BaoCao();
                LoadDuLieu();
                panel1.setVisible(false);
                JOptionPane.showMessageDialog(null, "Không có doanh thu");
           
            }
//            DSLP.clear();
//            SoLieu.clear();
            tongtien = 0;
//            txtMaBCDT.setText(MaBCDT());
        }catch(NumberFormatException ex){
            JOptionPane.showMessageDialog(null, "Nhập không đúng kiểu dữ kiểu cho năm");
        }
        
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        int b = JOptionPane.showConfirmDialog(null, "Bạn có muốn thoát khỏi Báo Cáo Doanh Thu không?", "Thoát", JOptionPane.YES_OPTION);
        if(b==0){
            int c=JOptionPane.showConfirmDialog(this, "Bạn có muốn trở về Màn hình chính không", "Thoát", JOptionPane.YES_OPTION);
             if (c == 0) {
                this.setVisible(false);
                ql.setEnabled(true);
                ql.setVisible(true);
            } else {
                System.exit(0);
            }
            
        }
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
            java.util.logging.Logger.getLogger(BaoCaoDoanhThuTheoLoaiPhong.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(BaoCaoDoanhThuTheoLoaiPhong.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(BaoCaoDoanhThuTheoLoaiPhong.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(BaoCaoDoanhThuTheoLoaiPhong.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new BaoCaoDoanhThuTheoLoaiPhong(new Connect(null)).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jP_Thongtin;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jT_DS;
    private javax.swing.JComboBox jcbThang;
    private javax.swing.JTextField txtNam;
    private javax.swing.JTextField txtTongDoanhThu;
    // End of variables declaration//GEN-END:variables
}
