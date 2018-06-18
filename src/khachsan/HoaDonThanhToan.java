/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package khachsan;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.table.DefaultTableModel;
import java.util.*;


/**
 *
 * @author Pham Duc Luong
 */
public class HoaDonThanhToan extends javax.swing.JFrame {

    /**
     * Creates new form HoaDonThanhToan
     */
    Connect cn=null;
    String url=null;
    List<String> PTCTT=null;
    DefaultTableModel model=null;
    List<List<String>> ds=new ArrayList<>();
    List<String> MaPhong=new ArrayList<>();
    QuanLiKhachSan ql=null;
    
    float TongTien=0;
    public HoaDonThanhToan(Connect cn1) {
        initComponents();
        this.setLocation(200, 60);
        jcbPTCTT.removeAllItems();
        PTCTT=new ArrayList<>();
        model=(DefaultTableModel)jT_DSPT.getModel();
        jD_NgayLapHoaDon.setDate(new Date());
        cn = cn1;
        ql=new QuanLiKhachSan(cn);
        try {    
            txtMaHoaDon.setText(MaHoaDon());        
            url = "SELECT MaPhieuThuePhong FROM PHIEUTHUEPHONG WHERE TinhTrangPhieuThue='ChuaThanhToan'";
            ResultSet rs = cn.ExcuteQuery(url);
            while (rs.next()) {
                jcbPTCTT.addItem(rs.getString(1));
                
            }
         
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
        }           
    }
    public void ThongBao(){
        if(jcbPTCTT.getSelectedIndex()==-1){
           JOptionPane.showMessageDialog(null, "Không có phiếu thuê để thanh toán");
        }
    }
    public long TinhNgay(String url1) {
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
        Date d1 = jD_NgayLapHoaDon.getDate();
        System.out.println(d1.getTime());
        long ngay = 0;
        try {
            Date date1 = null;
            url = "SELECT NgayBatDauThue\n"
                    + "FROM PHIEUTHUEPHONG\n"
                    + "WHERE MaPhieuThuePhong='" + url1 + "'";

            ResultSet rs = cn.ExcuteQuery(url);
            while (rs.next()) {
                date1 = date.parse(rs.getString(1));
                System.out.println(date1.getTime());
                long time = d1.getTime() - date1.getTime();
                ngay = (time / 86400000);
                System.out.println(ngay);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
        }

        return ngay;

    }

    public int LaysokhachNuocNgoai(String url1) {
        url = "SELECT MaLoaiKhach\n"
                + "FROM CT_PHIEUTHUEPHONG,PHIEUTHUEPHONG\n"
                + "WHERE CT_PHIEUTHUEPHONG.MaPhieuThuePhong='" + url1 + "'" + "AND PHIEUTHUEPHONG.MaPhieuThuePhong=CT_PHIEUTHUEPHONG.MaPhieuThuePhong AND MaLoaiKhach='NN'\n";
        int sokhachnn = 0;
        try {

            ResultSet rs = cn.ExcuteQuery(url);
            while (rs.next()) {
                sokhachnn++;

            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
        return sokhachnn;
    }

    public List LaydulieutuTHAMSO() {//:ấy Số khách không phụ thu và hệ số phụ thu
        url = "SELECT THAMSO.SoKhachKhongPhuThu,THAMSO.HeSoPhuThu\n"
                + "FROM THAMSO";
        List<Float> a = new ArrayList<>();
        try {
            ResultSet rs = cn.ExcuteQuery(url);
            while (rs.next()) {
                a.add((float)rs.getInt(1));
                a.add(Float.parseFloat(rs.getString(2)));
       

            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);

        }
        return a;
    }

    public float ThanhTien(long songay,float thanhtien) {
        float tien = 0;
        tien=thanhtien*songay;
        return tien;
    }
    public void Indulieuramodel(String url1) {

        url = "SELECT PHONG.MaPhong,PHONG.TenPhong,LOAIPHONG.MaLoaiPhong,PHIEUTHUEPHONG.SoKhach, PHIEUTHUEPHONG.ThanhTien\n"
                + "FROM PHIEUTHUEPHONG,PHONG,LOAIPHONG\n"
                + "WHERE PHONG.MaPhong=PHIEUTHUEPHONG.MaPhong AND LOAIPHONG.MaLoaiPhong=PHONG.MaLoaiPhong AND MaPhieuThuePhong='" + url1 + "'";
        try {
            ResultSet rs = cn.ExcuteQuery(url);
            long songaythue = TinhNgay(url1);

            while (rs.next()) {
                float tien = ThanhTien(songaythue, Float.parseFloat(rs.getString(5)));
                TongTien += tien;
                List<String> ds1 = new ArrayList<>();
                ds1.add(url1);
                ds1.add("" + tien);
                ds.add(ds1);
                MaPhong.add(rs.getString(1));
                model.addRow(new Object[]{rs.getString(1), rs.getString(2), songaythue, rs.getString(3), rs.getString(5), rs.getString(4), tien});
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
    }

    public void LuuHoaDon() {
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
        String Date = date.format(jD_NgayLapHoaDon.getDate());
        url = String.format("INSERT INTO HOADON VALUES('%s','%s','%s','%s',%f)", txtMaHoaDon.getText(), Date, txtTenKhachHang.getText(), txtDiaChi.getText(), TongTien);
        try {
            cn.ExcuteUpdate(url);
            System.out.println(url);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);

        }
    }

    public void LuuCT_HoaDon(String url1, float tien) {
        url = String.format("INSERT INTO CT_HOADON VALUES('%s','%s',%d,%f)", txtMaHoaDon.getText(), url1, TinhNgay(url1), tien);
        try {
            cn.ExcuteUpdate(url);
            System.out.println(url);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
    }

    public void ChuyenDoiTinhTrangPTT(String url1) {
        url = "UPDATE PHIEUTHUEPHONG\n"
                + "SET TinhTrangPhieuThue='DaThanhToan'\n"
                + "WHERE MaPhieuThuePhong='" + url1 + "'";
        try {
            cn.ExcuteUpdate(url);
            System.out.println(url);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
        }

    }

    public void ChuyenTinhTrangPhong(String url1) {
        url = "UPDATE PHONG\n"
                + "SET TinhTrang='Trong'\n"
                + "WHERE MaPhong='" + url1 + "'";
        try {
            cn.ExcuteUpdate(url);
            System.out.println(url);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
    }

    public String MaHoaDon() {
        url = "SELECT MaHoaDon FROM HOADON";
        List<String> dshd = new ArrayList<>();
        try {
            if(cn==null){
                JOptionPane.showMessageDialog(null, "NOOO");
            }
            ResultSet rs = cn.ExcuteQuery(url);
            while (rs.next()) {
                dshd.add(rs.getString(1));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return PhatSinhHoaDon(dshd);
    }

    public String PhatSinhHoaDon(List<String> b) {
        Boolean check = false;
        int a = 0;
        String mahoadon = "HD";
        String mahoadonphatsinh = null;
        int j = 1;
        if (b.size() == 0) {
            return "HD1";
        }
        while (check == false) {
            a = 0;
            for (int i = 0; i < b.size(); i++) {
                mahoadonphatsinh = mahoadon + "" + j;
                if (b.get(i).equals(mahoadonphatsinh)) {
                    break;
                } else {
                    a++;
                }
            }
            j++;
            if (a == b.size()) {
                check = true;
            }
        }
        return mahoadonphatsinh;
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
        jLabel3 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jT_DSPT = new javax.swing.JTable();
        txtTenKhachHang = new javax.swing.JTextField();
        txtDiaChi = new javax.swing.JTextField();
        txtTongTien = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtPTP = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jD_NgayLapHoaDon = new com.toedter.calendar.JDateChooser();
        jLabel7 = new javax.swing.JLabel();
        txtMaHoaDon = new javax.swing.JTextField();
        jcbPTCTT = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel1.setText("HÓA ĐƠN THANH TOÁN");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Thông tin hóa đơn"));

        jLabel2.setText("Tên Khách hàng/Cơ quan");

        jLabel3.setText("Địa chỉ");

        jLabel6.setText("Tổng tiền");

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Danh sách phòng đã thuê"));

        jT_DSPT.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mã Phòng", "Tên Phòng", "Số Ngày Thuê", "Mã Loại Phòng", "Đơn Giá 1 Ngày", "Số Khách", "Thành Tiền"
            }
        ));
        jScrollPane1.setViewportView(jT_DSPT);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 745, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)
                .addContainerGap())
        );

        txtTongTien.setEnabled(false);

        jLabel4.setText("Phiếu Thuê Phòng");

        txtPTP.setEnabled(false);

        jLabel5.setText("Ngày lập Hóa Đơn");

        jLabel7.setText("Mã hóa đơn");

        txtMaHoaDon.setEnabled(false);

        jLabel9.setText("PTCTT");

        jButton1.setText("Chọn Phiếu");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel8)
                        .addGap(290, 290, 290))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel4))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtPTP)
                                    .addComponent(txtTongTien, javax.swing.GroupLayout.DEFAULT_SIZE, 196, Short.MAX_VALUE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel2))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtTenKhachHang, javax.swing.GroupLayout.DEFAULT_SIZE, 196, Short.MAX_VALUE)
                                    .addComponent(txtDiaChi))))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jD_NgayLapHoaDon, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jcbPTCTT, javax.swing.GroupLayout.Alignment.LEADING, 0, 102, Short.MAX_VALUE)
                                    .addComponent(txtMaHoaDon, javax.swing.GroupLayout.Alignment.LEADING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton1)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(txtTenKhachHang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5))
                    .addComponent(jD_NgayLapHoaDon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtDiaChi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7)
                        .addComponent(txtMaHoaDon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtPTP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jcbPTCTT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel9)
                        .addComponent(jButton1))
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtTongTien, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8))
        );

        jButton3.setText("Thoát");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Chức năng"));

        jButton4.setText("Thêm mới");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setText("Thanh Toán");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap(13, Short.MAX_VALUE)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(335, 335, 335))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel1.getAccessibleContext().setAccessibleDescription("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        int b = JOptionPane.showConfirmDialog(null, "Bạn có muốn thoát khỏi Hóa Đơn không?", "Thoát", JOptionPane.YES_OPTION);
        if (b == 0) {
            int c = JOptionPane.showConfirmDialog(this, "Bạn có muốn trở về Màn hình chính không", "Thoát", JOptionPane.YES_OPTION);
            if (c == 0) {
                this.setVisible(false);
                ql.setEnabled(true);
                ql.setVisible(true);
                
            } 
            else 
            {
                System.exit(0);
            }
            
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
       
       
       if(TinhNgay(jcbPTCTT.getSelectedItem().toString())<=0){
           JOptionPane.showMessageDialog(null, "Ngày lập hóa đơn nhỏ hơn ngày thuê của Phiếu thuê");
       }
       else{
           PTCTT.add(jcbPTCTT.getSelectedItem().toString());
           txtPTP.setText(txtPTP.getText() + " + " + jcbPTCTT.getSelectedItem().toString());
           jcbPTCTT.removeItemAt(jcbPTCTT.getSelectedIndex());
       }
       
       
       
       
    }//GEN-LAST:event_jButton1ActionPerformed
    public Boolean Kiemtratrong(){
        if(txtTenKhachHang.getText().length()==0||txtDiaChi.getText().length()==0||txtPTP.getText().length()==0){
            JOptionPane.showMessageDialog(null, "Nhập chưa đủ thông tin");
            return false;
        }
        return true;
    }
    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
    //IN du lieu ra model(bang) và chuyen doi tinh trang phieu thue phong và chuyen doi tinh trang phong
        if(Kiemtratrong()==false){
            return;
        }
        for (int i = 0; i < PTCTT.size(); i++) {
            Indulieuramodel(PTCTT.get(i));
            ChuyenDoiTinhTrangPTT(PTCTT.get(i));
            ChuyenTinhTrangPhong(MaPhong.get(i));;
        }
            // Luu Hoa Dơn vào csdl
            LuuHoaDon();
            // Luu CT_HoaDon vào csdl
            for (int i = 0; i < ds.size(); i++) {
                LuuCT_HoaDon(ds.get(i).get(0), Float.parseFloat(ds.get(i).get(1)));
            }
            txtTongTien.setText("" + TongTien);
            TongTien = 0;
            ds = new ArrayList<>();
            PTCTT = new ArrayList<>();
            MaPhong = new ArrayList<>();
        
       
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        txtDiaChi.setText(null);
        txtMaHoaDon.setText(MaHoaDon());
        txtTongTien.setText(null);
        txtPTP.setText(null);
        txtTenKhachHang.setText(null);
        jD_NgayLapHoaDon.setDate(new Date());
        TongTien=0;
        ds=new ArrayList<>();
        PTCTT=new ArrayList<>();
        MaPhong=new ArrayList<>();
        int a=model.getRowCount();
        for(int i=0;i<a;i++){
            model.removeRow(0);
        }
        jcbPTCTT.removeAllItems();
        try {
            url = "SELECT MaPhieuThuePhong FROM PHIEUTHUEPHONG WHERE TinhTrangPhieuThue='ChuaThanhToan'";
            ResultSet rs = cn.ExcuteQuery(url);
            while (rs.next()) {
                jcbPTCTT.addItem(rs.getString(1));
                
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
        
    }//GEN-LAST:event_jButton4ActionPerformed

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
            java.util.logging.Logger.getLogger(HoaDonThanhToan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(HoaDonThanhToan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(HoaDonThanhToan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(HoaDonThanhToan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new HoaDonThanhToan(new Connect(null)).setVisible(true);
                
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private com.toedter.calendar.JDateChooser jD_NgayLapHoaDon;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jT_DSPT;
    private javax.swing.JComboBox jcbPTCTT;
    private javax.swing.JTextField txtDiaChi;
    private javax.swing.JTextField txtMaHoaDon;
    private javax.swing.JTextField txtPTP;
    private javax.swing.JTextField txtTenKhachHang;
    private javax.swing.JTextField txtTongTien;
    // End of variables declaration//GEN-END:variables
}
