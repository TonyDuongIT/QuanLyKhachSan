/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package khachsan;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import sun.jdbc.odbc.JdbcOdbc;
import sun.security.krb5.internal.tools.Ktab;

/**
 *
 * @author Pham Duc Luong
 */
public class PhieuThuePhong extends javax.swing.JFrame {
    DefaultTableModel model;
    DefaultTableModel model1;
    DefaultTableModel model2;
    Connect cn=null;
    List<List<String>> ds=new ArrayList<>();
    List<String> dsmact_pt=new ArrayList<>();
    int SoKhach=0;
    int SoKhachToiDa=0;
    ResultSet rs=null;
    QuanLiKhachSan ql=null;  
    /**
     * Creates new form PhieuThuePhong
     */
    public PhieuThuePhong(Connect cn1) {
        initComponents();
        cn=cn1;
        ql=new QuanLiKhachSan(cn);
        this.setLocation(80, 0);
        txtMPTP.setEnabled(false);
        txtMa_CTPT.setEnabled(false);
        model=(DefaultTableModel)jT_DSPTP.getModel();
        jcbMaLoaiKhach.removeAllItems();
        jcbMaPhong.removeAllItems();
        txtMPTP.setText(MaPhieuThue());
        Date date= new Date();
        jD_NBDT.setDate(date);   
        txtMa_CTPT.setText(KiemTraMaCT_PT(dsmact_pt));
        model1 = (DefaultTableModel) jT_KhachHangThemVao.getModel();
        model2=(DefaultTableModel)jT_ThongTinPhieuThue.getModel();
        model = (DefaultTableModel) jT_DSPTP.getModel();
        String url="SELECT LOAIKHACH.MaLoaiKhach FROM LOAIKHACH";
        try{
        ResultSet rs=cn.ExcuteQuery(url);
        while(rs.next()){
            jcbMaLoaiKhach.addItem(rs.getString(1));
        }
        setjT_DSPTP();
        setjT_ThongTinPhieuThue();
        setjT_KhachHangThemVao();
        ThemDuLieuvaojT_DSPTP("");
        
        }catch(Exception ex){
            ex.printStackTrace();
        }
        Laysokhachtoida();
        PoPupTable();
    
    }
    void setjT_DSPTP(){
        jT_DSPTP.getColumnModel().getColumn(0).setPreferredWidth(1);
        jT_DSPTP.getColumnModel().getColumn(1).setPreferredWidth(1);
        jT_DSPTP.getColumnModel().getColumn(2).setPreferredWidth(1);

    }
    void setjT_ThongTinPhieuThue(){
        jT_ThongTinPhieuThue.getColumnModel().getColumn(0).setPreferredWidth(1);
        jT_ThongTinPhieuThue.getColumnModel().getColumn(2).setPreferredWidth(1);
    }
    void setjT_KhachHangThemVao(){
        jT_KhachHangThemVao.getColumnModel().getColumn(0).setPreferredWidth(1);
        jT_KhachHangThemVao.getColumnModel().getColumn(1).setPreferredWidth(1);
    }
    public void PoPupTable(){
        JPopupMenu jp =new JPopupMenu();
        JMenuItem item1=new JMenuItem("Delete");
        JMenuItem item2=new JMenuItem("Update");
        item1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
               Xoa(); 
               txtMa_CTPT.setText(KiemTraMaCT_PT(new ArrayList<String>()));
            }
        });
        item2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                Sua();
            }
        });
     
        jp.add(item1);
        jp.add(item2);
        jT_ThongTinPhieuThue.setComponentPopupMenu(jp);
                
    }
    public int LaySoKhachTrongPhieuThue(String a){
        try{

            String url="SELECT SoKhach FROM PHIEUTHUEPHONG WHERE MaPhieuThuePhong='"+a+"'";
            ResultSet rs=cn.ExcuteQuery(url);
            while(rs.next()){
                return rs.getInt(1);
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return 0;
    }
    public Boolean KiemTraPhieuThue(String a){
        try{
           String url="SELECT TinhTrangPhieuThue FROM PHIEUTHUEPHONG WHERE MaPhieuThuePhong='"+a+"'";
           ResultSet rs=cn.ExcuteQuery(url);
           while(rs.next()){
               if(rs.getString(1).equals("ChuaThanhToan"))
                   return true;
           }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return false;
    }
    public void Xoa(){
        int index1=jT_DSPTP.getSelectedRow();
        int index2=jT_ThongTinPhieuThue.getSelectedRow();
        if(KiemTraPhieuThue(model.getValueAt(index1, 0).toString())==false){
            JOptionPane.showMessageDialog(null, "Phiếu thuê đã thanh toán không thể xóa khách");
            return;
        }
        try{
            int sokhach=LaySoKhachTrongPhieuThue(model.getValueAt(index1, 0).toString());
            if(sokhach==1){
                JOptionPane.showMessageDialog(null, "Phòng chỉ có 1 khách nên không thể xóa khách");
                return;
            }
            else{
                String url="DELETE FROM CT_PHIEUTHUEPHONG WHERE MaCT_PTP='"+model2.getValueAt(index2, 0).toString()+"'";
                cn.ExcuteUpdate(url);
                float dongia=LayDonGiaPhongHienTai(model.getValueAt(index1, 1).toString());
                int sokhachnuocngoai= DemSoKhachNuocNgoai(model.getValueAt(index1, 0).toString());
                List<Float> thamso=LayDuLieuTrongThamSo();
                float thanhtien=ThanhTien(dongia, thamso.get(0), thamso.get(2), thamso.get(1),sokhachnuocngoai);
                url = String.format("UPDATE PHIEUTHUEPHONG \n" +
                            "SET SoKhach=%d\n, DonGia=%f, ThanhTien=%f" +
                            "WHERE MaPhieuThuePhong='%s'",sokhach-1,dongia,thanhtien,model.getValueAt(index1, 0).toString());
                cn.ExcuteUpdate(url);
                model2.removeRow(index2);
            }
                
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    public void Sua(){
        int index1=jT_DSPTP.getSelectedRow();
        int index2=jT_ThongTinPhieuThue.getSelectedRow();
        if(KiemTraPhieuThue(model.getValueAt(index1, 0).toString())==false){
            JOptionPane.showMessageDialog(null, "Phiếu thuê đã thanh toán không thể sửa khách");
        }
        else{
            List<String> a= new ArrayList<>();
            for(int i=0;i<jT_ThongTinPhieuThue.getColumnCount();i++){
                a.add(model2.getValueAt(index2, i).toString());
            }
            SuaKhachTrongPhong sk=new SuaKhachTrongPhong(this,cn);
            sk.LayDuLieu(a);
            sk.setVisible(true);
            this.setEnabled(false);            
        }
    }
    public String PhatSinhPhieuThuePhong(List<String> dsptp){
        Boolean check=false;
        int a=0;
        String maphieuthue="PT";
        String maphieuthuephatsinh=null;
        int j=1;
        if(dsptp.size()==0){
            return "PT1";
        }
        while(check==false){
            a=0;
            for(int i=0;i<dsptp.size();i++){
                maphieuthuephatsinh=maphieuthue+""+j;
                if(dsptp.get(i).equals(maphieuthuephatsinh)){
                    break;
                }
                else
                    a++;
            }
            j++;
            if(a==dsptp.size())
                check=true;
        }
        return maphieuthuephatsinh;
    }
    public String MaPhieuThue(){
        String url="SELECT MaPhieuThuePhong FROM PHIEUTHUEPHONG";
        List<String> dsptp=new ArrayList<>();
        try{
           rs = cn.ExcuteQuery(url);
           while(rs.next()){
               dsptp.add(rs.getString(1));
           }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return PhatSinhPhieuThuePhong(dsptp);
    }
    public String PhatSinhMaCT_PT(List<String> dsctpt){
        Boolean check=false;
        int a=0;
        String mact_phieuthue="CTPT";
        String mact_phieuthuephatsinh=null;
        int j=1;
        if(dsctpt.size()==0){
            return "CTPT1";
        }
        while(check==false){
            a=0;
            for(int i=0;i<dsctpt.size();i++){
                mact_phieuthuephatsinh =mact_phieuthue+""+j;
                if(dsctpt.get(i).equals(mact_phieuthuephatsinh)){
                    break;
                }
                else
                    a++;
            }
            j++;
            if(a==dsctpt.size())
                check=true;
        }
        return mact_phieuthuephatsinh;
    }
    public String KiemTraMaCT_PT(List<String>a){
        List<String> dsctpt=new ArrayList<>();
        try {
            dsctpt=a;
            String url = "SELECT MaCT_PTP FROM CT_PHIEUTHUEPHONG";
            rs = cn.ExcuteQuery(url);
            while(rs.next()){
                dsctpt.add(rs.getString(1));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
     
        
        return PhatSinhMaCT_PT(dsctpt);
    }
    public void ThemDuLieuvaojT_DSPTP(String url1){
        model = (DefaultTableModel) jT_DSPTP.getModel();
  
        try {
            String url = String.format("SELECT PHIEUTHUEPHONG.MaPhieuThuePhong,MaPhong,NgayBatDauThue,TinhTrangPhieuThue\n" +
                                       "FROM PHIEUTHUEPHONG\n" +
                                        url1);   
            rs = cn.ExcuteQuery(url);
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3),rs.getString(4)});
            }
     
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public void KiemtraPhongTrong(){
        try{
            jcbMaPhong.removeAllItems();
            String url = "SELECT PHONG.MaPhong FROM PHONG WHERE TinhTrang='Trong'";
            rs = cn.ExcuteQuery(url);
            while (rs.next()) {
                jcbMaPhong.addItem(rs.getString(1));
            }
            if (jcbMaPhong.getItemAt(0) == null) {
                JOptionPane.showMessageDialog(null, "Không còn phòng trống để thuê");
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    public void Laysokhachtoida(){
        String url="SELECT THAMSO.SoKhachToiDa\n" +
                    "FROM THAMSO";
       try{
           ResultSet rs=cn.ExcuteQuery(url);
           while(rs.next()){
               SoKhachToiDa=rs.getInt(1);
           }
       }catch(Exception ex){
           JOptionPane.showMessageDialog(null, ex);

       }
    }
    public void xoaModel(DefaultTableModel a){
        int b=a.getRowCount();
        for(int i=0;i<b;i++){
            a.removeRow(0);
        }
    }
    public void LayThongTinChiTiet(String a){//Lấy ra thông tin chi tiết khi click vào bảng Phiếuthuê
        String url="SELECT MaCT_PTP,TenKhachHang,LOAIKHACH.MaLoaiKhach,LOAIKHACH.TenLoaiKhach,CMND,DiaChi\n" +
            "FROM CT_PHIEUTHUEPHONG, LOAIKHACH\n" +
            "WHERE  CT_PHIEUTHUEPHONG.MaLoaiKhach=LOAIKHACH.MaLoaiKhach AND MaPhieuThuePhong='"+a+"'";
        model2=(DefaultTableModel)jT_ThongTinPhieuThue.getModel();
        xoaModel(model2);
       try{
            ResultSet rs=cn.ExcuteQuery(url);
            while(rs.next()){
                model2.addRow(new Object[]{rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6)});
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    public void LayKhachHangTuModel(){//Thêm thông tin khách hàng từ bảng vào danh sách
        for(int i=0;i<model1.getRowCount();i++){
            List<String> ds1=new ArrayList<>();
            for(int j=0;j<model1.getColumnCount();j++){
                ds1.add(model1.getValueAt(i, j).toString());
            }
            ds.add(ds1);
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

        jP_PhieuThuePhong = new javax.swing.JPanel();
        jP_ThongTinKhachHang = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        txtTenKhachHang = new javax.swing.JTextField();
        txtCMND = new javax.swing.JTextField();
        jcbMaLoaiKhach = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        txtMa_CTPT = new javax.swing.JTextField();
        txtDiaChi = new javax.swing.JTextField();
        jC_ThemKhach = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        jB_ThemKhach = new javax.swing.JButton();
        jB_ThemmoiKhach = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jT_KhachHangThemVao = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        txtMPTP = new javax.swing.JTextField();
        jcbMaPhong = new javax.swing.JComboBox();
        jD_NBDT = new com.toedter.calendar.JDateChooser();
        jLabel9 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jP_ChucNang = new javax.swing.JPanel();
        jB_Luu = new javax.swing.JButton();
        jB_Themmoi = new javax.swing.JButton();
        jP_Ds = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jT_DSPTP = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        jT_ThongTinPhieuThue = new javax.swing.JTable();
        jB_Thoat = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Phiếu Thuê Phòng");
        setResizable(false);

        jP_PhieuThuePhong.setBorder(javax.swing.BorderFactory.createTitledBorder("Phiếu thuê phòng"));
        jP_PhieuThuePhong.setToolTipText("");

        jP_ThongTinKhachHang.setBorder(javax.swing.BorderFactory.createTitledBorder("Thông tin khách hàng"));

        jLabel11.setText("Tên khách hàng");

        jLabel12.setText("Mã Loại Khách");

        jLabel13.setText("CMND");

        jLabel14.setText("Địa chỉ");

        jcbMaLoaiKhach.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel4.setText("Mã CT_PT");

        txtMa_CTPT.setEnabled(false);

        jC_ThemKhach.setText("Thêm Khách vào cùng Phiếu Thuê");

        javax.swing.GroupLayout jP_ThongTinKhachHangLayout = new javax.swing.GroupLayout(jP_ThongTinKhachHang);
        jP_ThongTinKhachHang.setLayout(jP_ThongTinKhachHangLayout);
        jP_ThongTinKhachHangLayout.setHorizontalGroup(
            jP_ThongTinKhachHangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jP_ThongTinKhachHangLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jP_ThongTinKhachHangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11)
                    .addComponent(jLabel13)
                    .addComponent(jLabel4))
                .addGap(33, 33, 33)
                .addGroup(jP_ThongTinKhachHangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jP_ThongTinKhachHangLayout.createSequentialGroup()
                        .addGroup(jP_ThongTinKhachHangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtTenKhachHang, javax.swing.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
                            .addComponent(txtCMND))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                    .addGroup(jP_ThongTinKhachHangLayout.createSequentialGroup()
                        .addComponent(txtMa_CTPT, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(104, 104, 104)))
                .addGroup(jP_ThongTinKhachHangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12)
                    .addComponent(jLabel14))
                .addGap(39, 39, 39)
                .addGroup(jP_ThongTinKhachHangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jC_ThemKhach)
                    .addComponent(txtDiaChi, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcbMaLoaiKhach, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jP_ThongTinKhachHangLayout.setVerticalGroup(
            jP_ThongTinKhachHangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jP_ThongTinKhachHangLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jP_ThongTinKhachHangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(txtTenKhachHang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(jcbMaLoaiKhach, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
                .addGroup(jP_ThongTinKhachHangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCMND, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(jLabel14)
                    .addComponent(txtDiaChi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addGroup(jP_ThongTinKhachHangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtMa_CTPT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jC_ThemKhach)))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Chức năng"));
        jPanel2.setToolTipText("");

        jB_ThemKhach.setText("Thêm Khách Hàng vào Phòng");
        jB_ThemKhach.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ThemKhach(evt);
            }
        });

        jB_ThemmoiKhach.setText("Thêm mới");
        jB_ThemmoiKhach.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ThemmoiKhach(evt);
            }
        });

        jButton1.setText("Xóa");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jB_ThemKhach)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jB_ThemmoiKhach, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jB_ThemKhach, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jB_ThemmoiKhach, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Danh sách khách hàng thêm vào phòng"));

        jT_KhachHangThemVao.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "MãCT_PT", "Tên Khách Hàng", "Mã Loại Khách", "Tên Loại Khách", "CMND", "ĐịaChỉ"
            }
        ));
        jScrollPane2.setViewportView(jT_KhachHangThemVao);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Thông tin Phiếu thuê"));

        txtMPTP.setEnabled(false);

        jcbMaPhong.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jD_NBDT.setDateFormatString("d MMM yyyy");
        jD_NBDT.setEnabled(false);

        jLabel9.setText("Mã Phiếu Thuê Phòng");

        jLabel1.setText("Mã Phòng");

        jLabel2.setText("Ngày bắt đầu thuê");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtMPTP, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jcbMaPhong, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jD_NBDT, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jD_NBDT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtMPTP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jcbMaPhong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jP_ChucNang.setBorder(javax.swing.BorderFactory.createTitledBorder("Chức năng"));

        jB_Luu.setText("Lưu");
        jB_Luu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Luu(evt);
            }
        });

        jB_Themmoi.setText("Thêm mới");
        jB_Themmoi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ThemmoiPT(evt);
            }
        });

        javax.swing.GroupLayout jP_ChucNangLayout = new javax.swing.GroupLayout(jP_ChucNang);
        jP_ChucNang.setLayout(jP_ChucNangLayout);
        jP_ChucNangLayout.setHorizontalGroup(
            jP_ChucNangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jP_ChucNangLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jB_Luu, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jB_Themmoi, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
                .addContainerGap())
        );
        jP_ChucNangLayout.setVerticalGroup(
            jP_ChucNangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jP_ChucNangLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jP_ChucNangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jB_Luu, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jB_Themmoi, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jP_PhieuThuePhongLayout = new javax.swing.GroupLayout(jP_PhieuThuePhong);
        jP_PhieuThuePhong.setLayout(jP_PhieuThuePhongLayout);
        jP_PhieuThuePhongLayout.setHorizontalGroup(
            jP_PhieuThuePhongLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jP_PhieuThuePhongLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jP_PhieuThuePhongLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jP_PhieuThuePhongLayout.createSequentialGroup()
                        .addGroup(jP_PhieuThuePhongLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jP_PhieuThuePhongLayout.createSequentialGroup()
                                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jP_ChucNang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jP_PhieuThuePhongLayout.createSequentialGroup()
                                .addComponent(jP_ThongTinKhachHang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 190, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jP_PhieuThuePhongLayout.setVerticalGroup(
            jP_PhieuThuePhongLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jP_PhieuThuePhongLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jP_PhieuThuePhongLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jP_ChucNang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jP_PhieuThuePhongLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jP_ThongTinKhachHang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jP_Ds.setBorder(javax.swing.BorderFactory.createTitledBorder("Danh sách Phiếu thuê phòng"));

        jT_DSPTP.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mã PTP", "Mã Phòng", "NBĐT", "Tình Trạng"
            }
        ));
        jT_DSPTP.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jT_DSPTPMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jT_DSPTP);

        jT_ThongTinPhieuThue.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "MaCT_PTP", "Tên Khách Hàng", "Loại Khách ", "Tên Loại Khách", "CMND ", "ĐịaChỉ"
            }
        ));
        jScrollPane3.setViewportView(jT_ThongTinPhieuThue);

        javax.swing.GroupLayout jP_DsLayout = new javax.swing.GroupLayout(jP_Ds);
        jP_Ds.setLayout(jP_DsLayout);
        jP_DsLayout.setHorizontalGroup(
            jP_DsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jP_DsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 397, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3))
        );
        jP_DsLayout.setVerticalGroup(
            jP_DsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jP_DsLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jP_DsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(336, 336, 336))
        );

        jB_Thoat.setText("Thoát");
        jB_Thoat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Thoat(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel10.setText("PHIẾU THUÊ PHÒNG");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jB_Thoat, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jP_Ds, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jP_PhieuThuePhong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(474, 474, 474)
                .addComponent(jLabel10)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jP_PhieuThuePhong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jP_Ds, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jB_Thoat, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    public int DemSoKhachNuocNgoai(String a){
        int dem=0;
        if(a.equals("")){
            for (int i = 0; i < ds.size(); i++) {
                System.out.println(ds.get(i).get(2));
                if (ds.get(i).get(3).equals("NuocNgoai")) {
                    dem++;
                }
            }
            return dem;
        }
        else{
            try{
               String url="SELECT COUNT(CT_PHIEUTHUEPHONG.MaLoaiKhach) \n" +
                        "FROM CT_PHIEUTHUEPHONG, LOAIKHACH\n" +
                        "WHERE CT_PHIEUTHUEPHONG.MaLoaiKhach=LOAIKHACH.MaLoaiKhach AND MaPhieuThuePhong='"+a+"' AND LOAIKHACH.TenLoaiKhach='NuocNgoai'";
               ResultSet rs=cn.ExcuteQuery(url);
               while(rs.next()){
                   return rs.getInt(1);
               }
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
        return dem;
    }
    public float LayDonGiaPhongHienTai(String a){
        try{
            String url="SELECT DonGia FROM LOAIPHONG,PHONG WHERE PHONG.MaLoaiPhong=LOAIPHONG.MaLoaiPhong AND PHONG.MaPhong='"+a+"'";
            ResultSet rs= cn.ExcuteQuery(url);
            while(rs.next()){
                return Float.parseFloat(rs.getString(1));
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return 0;
    }
    public List LayDuLieuTrongThamSo(){
        List<Float> thamso=new ArrayList<>();
        
        try{
            String url="SELECT * FROM THAMSO";
            ResultSet rs=cn.ExcuteQuery(url);
            while(rs.next()){
                thamso.add(Float.parseFloat(rs.getString(2)));
                thamso.add((float)rs.getInt(3));
                thamso.add(Float.parseFloat(rs.getString(4)));
                thamso.add((float)rs.getInt(1));
                return thamso;
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return thamso;
    }
 
    public float ThanhTien(float dongia, float tile,float heso, float sokhachkhongphuthu, int sokhachnuocngoai){
        float thanhtien =0;
        thanhtien=dongia*1;
        if(SoKhach<=sokhachkhongphuthu){
            if(sokhachnuocngoai>=1){
                thanhtien=thanhtien*heso;
            }
        } else {
            thanhtien = thanhtien+thanhtien * tile;
            if (sokhachnuocngoai >= 1) {
          
                thanhtien=thanhtien*heso;
            }

        }
        return thanhtien;
    }
    private void Luu(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Luu
       
        if(model1.getRowCount()==0){
            JOptionPane.showMessageDialog(null, "Chưa nhập khách hàng");
            return;
        }
        LayKhachHangTuModel();
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
        String Date = null;
        Date = date.format(jD_NBDT.getDate());
        
            try {
                float dongia=LayDonGiaPhongHienTai(jcbMaPhong.getSelectedItem().toString());
                int sokhachnuocngoai= DemSoKhachNuocNgoai("");
                System.out.println(sokhachnuocngoai);
                List<Float> thamso=LayDuLieuTrongThamSo();
                float thanhtien=ThanhTien(dongia, thamso.get(0), thamso.get(2), thamso.get(1),sokhachnuocngoai);
                String url = String.format("INSERT INTO PHIEUTHUEPHONG (MaPhieuThuePhong,MaPhong,NgayBatDauThue,SoKhach,TinhTrangPhieuThue,DonGia,ThanhTien) VALUES('%s','%s','%s',%d,'%s',%f,%f)", txtMPTP.getText(), jcbMaPhong.getSelectedItem().toString(), Date, SoKhach, "ChuaThanhToan",dongia,thanhtien);
                cn.ExcuteUpdate(url);
                try {
                    //Thêm dữ liệu vào bảng CT_PHIEUTHUEPHONG
                    
                    for (int i = 0; i < ds.size(); i++) {
                        url = String.format("INSERT CT_PHIEUTHUEPHONG (MaCT_PTP,MaPhieuThuePhong,TenKhachHang,MaLoaiKhach,CMND,DiaChi) VALUES('%s','%s','%s','%s','%s','%s')", ds.get(i).get(0), txtMPTP.getText(), ds.get(i).get(1), ds.get(i).get(2), ds.get(i).get(4), ds.get(i).get(5));
                        cn.ExcuteUpdate(url);
                    }

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, ex);
                }
                try {
                    //Lấy Dữ liệu in ra table model
                    ThemDuLieuvaojT_DSPTP("WHERE MaPhieuThuePhong='"+ txtMPTP.getText()+"'");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, ex);
                }
                try {
                    url = String.format("UPDATE PHONG \n"
                            + "SET TinhTrang='DaThue'\n"
                            + "WHERE MaPhong='%s'", jcbMaPhong.getSelectedItem().toString());
                    cn.ExcuteUpdate(url);
                    //Xóa trắng các ô
                    this.ThemmoiPT(evt);
                    //Lấy lại mã phòng chưa thuê
                  
                    KiemtraPhongTrong();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, ex);
                }   
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex);
            }
            // Cập nhật lại số khách
            SoKhach = 0;
            //Xóa danh sách các khách hàng trong phòng
            ds=new ArrayList<>();
            //Xóa dữ liệu trong bảng Thêm khách hàng
            xoaModel(model1);
            //Khởi tạo Mã phiếu thuê phòng
            txtMPTP.setText(MaPhieuThue());
            //Khởi tạo Mã Chi tiết phiếu thuê
            txtMa_CTPT.setText(KiemTraMaCT_PT(dsmact_pt));
            //Cập nhật lại danh sách chứa các mã phiếu thuê
            dsmact_pt=new ArrayList<>();       
    }//GEN-LAST:event_Luu

    private void ThemmoiPT(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ThemmoiPT
     jD_NBDT.setDate(new Date());
     txtMPTP.setText(MaPhieuThue());
     this.ThemmoiKhach(evt);
     
     
     
    }//GEN-LAST:event_ThemmoiPT

    private void Thoat(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Thoat
        int b = JOptionPane.showConfirmDialog(this, "Bạn có muốn thoát khỏi Phiếu Thuê Phòng", "Thoát", JOptionPane.YES_OPTION);
        if (b == 0) {
            int c = JOptionPane.showConfirmDialog(this, "Bạn có muốn trở về Màn hình chính không", "Thoát", JOptionPane.YES_OPTION);
            if (c == 0) {
                this.setVisible(false);
                ql.setEnabled(true);
                ql.setVisible(true);
            } else {
                System.exit(0);
            }
            
        }

        
    }//GEN-LAST:event_Thoat
//    public Boolean Kiemtratrong(){
//        if(txtTenKhachHang.getText().length()==0||txtDiaChi.getText().length()==0||txtCMND.getText().length()==0){
//            JOptionPane.showMessageDialog(null, "Chưa nhập đầy đủ thông tin khách hàng");
//            return false;
//        }
//        return true;
//    }
    public String LayTenLoaiKhach(String a){
        String tenloaikhach=null;
        try{
            String url="SELECT TenLoaiKhach FROM LOAIKHACH WHERE MaLoaiKhach='"+a+"'";
            ResultSet rs=cn.ExcuteQuery(url);
            if(rs.next()){
                return rs.getString(1);
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return tenloaikhach;
    }
    private void ThemKhach(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ThemKhach
//        if(Kiemtratrong()==false)
//            return;
        if(jC_ThemKhach.isSelected()){
            if(KiemTraPhieuThue(model.getValueAt(jT_DSPTP.getSelectedRow(),0).toString())==false){
                JOptionPane.showMessageDialog(null, "Phiếu thuê đã thanh toán không thể thêm khách");
                return;
            }
            try{
                int index= jT_DSPTP.getSelectedRow();
                int sokhach=LaySoKhachTrongPhieuThue(model.getValueAt(index, 0).toString());
                List<Float> thamso=LayDuLieuTrongThamSo();
                if((float)sokhach>=thamso.get(3)){
                    JOptionPane.showMessageDialog(null, "Số khách trong phòng đã đủ không thể thêm");
                    return;
                }
                String url=String.format("INSERT INTO CT_PHIEUTHUEPHONG VALUES('%s','%s','%s','%s','%s','%s')",txtMa_CTPT.getText(),model.getValueAt(index, 0).toString(),txtTenKhachHang.getText(),jcbMaLoaiKhach.getSelectedItem().toString(),txtCMND.getText(),txtDiaChi.getText());
                cn.ExcuteUpdate(url);
                float dongia=LayDonGiaPhongHienTai(model.getValueAt(index, 1).toString());
                int sokhachnuocngoai= DemSoKhachNuocNgoai(model.getValueAt(index, 0).toString());
             
                float thanhtien=ThanhTien(dongia, thamso.get(0), thamso.get(2), thamso.get(1),sokhachnuocngoai);
                url = String.format("UPDATE PHIEUTHUEPHONG \n" +
                            "SET SoKhach=%d\n, DonGia=%f, ThanhTien=%f" +
                            "WHERE MaPhieuThuePhong='%s'",sokhach+1,dongia,thanhtien,model.getValueAt(index, 0).toString());
                cn.ExcuteUpdate(url);
                txtCMND.setText(null);
                txtDiaChi.setText(null);
                txtTenKhachHang.setText(null);
                txtMa_CTPT.setText(KiemTraMaCT_PT(new ArrayList<String>()));
                return;
                
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
        if (SoKhach < SoKhachToiDa) {
            SoKhach++;
            dsmact_pt.add(txtMa_CTPT.getText());           
            model1 = (DefaultTableModel) jT_KhachHangThemVao.getModel();
            String tenloaikhach=LayTenLoaiKhach(jcbMaLoaiKhach.getSelectedItem().toString());
            model1.addRow(new Object[]{txtMa_CTPT.getText(),txtTenKhachHang.getText(), jcbMaLoaiKhach.getSelectedItem().toString(), tenloaikhach,txtCMND.getText(), txtDiaChi.getText()});
            if (SoKhach == SoKhachToiDa) {
                JOptionPane.showMessageDialog(null, "Bạn đã thêm vào đủ số khách tối đa trong phòng");
            }
        }
        else {
            JOptionPane.showMessageDialog(null, "Bạn đã thêm vào đủ số khách tối đa trong phòng");
        }
        txtCMND.setText(null);
        txtDiaChi.setText(null);
        txtTenKhachHang.setText(null);
        txtMa_CTPT.setText(KiemTraMaCT_PT(dsmact_pt));

    }//GEN-LAST:event_ThemKhach
  
    private void ThemmoiKhach(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ThemmoiKhach
        txtCMND.setText(null);
        txtDiaChi.setText(null);
        txtTenKhachHang.setText(null);
        txtMa_CTPT.setText(KiemTraMaCT_PT(dsmact_pt));
        
    }//GEN-LAST:event_ThemmoiKhach

    private void jT_DSPTPMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jT_DSPTPMouseClicked
        int index=jT_DSPTP.getSelectedRow();
        model=(DefaultTableModel)jT_DSPTP.getModel();
        LayThongTinChiTiet(model.getValueAt(index, 0).toString());
        
    }//GEN-LAST:event_jT_DSPTPMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
       int[] index=jT_KhachHangThemVao.getSelectedRows();
       if(index.length==0){
           JOptionPane.showMessageDialog(null, "Chưa chọn hàng để xóa");
           return;
       }
       for(int i=0;i<index.length;i++){
           dsmact_pt.remove(model1.getValueAt(index[i], 0));
           model1.removeRow(index[i]-i);
           SoKhach--;
       }
       txtMa_CTPT.setText(KiemTraMaCT_PT(dsmact_pt));
    }//GEN-LAST:event_jButton1ActionPerformed

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
            java.util.logging.Logger.getLogger(PhieuThuePhong.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PhieuThuePhong.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PhieuThuePhong.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PhieuThuePhong.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PhieuThuePhong(new Connect(null)).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jB_Luu;
    private javax.swing.JButton jB_ThemKhach;
    private javax.swing.JButton jB_Themmoi;
    private javax.swing.JButton jB_ThemmoiKhach;
    private javax.swing.JButton jB_Thoat;
    private javax.swing.JButton jButton1;
    private javax.swing.JCheckBox jC_ThemKhach;
    private com.toedter.calendar.JDateChooser jD_NBDT;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jP_ChucNang;
    private javax.swing.JPanel jP_Ds;
    private javax.swing.JPanel jP_PhieuThuePhong;
    private javax.swing.JPanel jP_ThongTinKhachHang;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jT_DSPTP;
    private javax.swing.JTable jT_KhachHangThemVao;
    private javax.swing.JTable jT_ThongTinPhieuThue;
    private javax.swing.JComboBox jcbMaLoaiKhach;
    private javax.swing.JComboBox jcbMaPhong;
    private javax.swing.JTextField txtCMND;
    private javax.swing.JTextField txtDiaChi;
    private javax.swing.JTextField txtMPTP;
    private javax.swing.JTextField txtMa_CTPT;
    private javax.swing.JTextField txtTenKhachHang;
    // End of variables declaration//GEN-END:variables
}
