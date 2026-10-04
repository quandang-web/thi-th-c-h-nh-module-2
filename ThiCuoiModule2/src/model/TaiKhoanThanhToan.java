package model;

import java.time.LocalDate;

public class TaiKhoanThanhToan extends TaiKhoanNganHang {
    private final String soThe;
    private final long soTienTrongTaiKhoan;

    public TaiKhoanThanhToan(int id, String maTaiKhoan, String tenChuTaiKhoan, LocalDate ngayTao,
                             String soThe, long soTienTrongTaiKhoan) {
        super(id, maTaiKhoan, tenChuTaiKhoan, ngayTao);
        this.soThe = soThe;
        this.soTienTrongTaiKhoan = soTienTrongTaiKhoan;
    }

    @Override
    public String toCsvLine() {
        return baseCsvFields() + "," + soThe + "," + soTienTrongTaiKhoan;
    }

    @Override
    public String toString() {
        return "TaiKhoanThanhToan{" + baseDisplayFields()
                + ", soThe='" + soThe + '\''
                + ", soTienTrongTaiKhoan=" + soTienTrongTaiKhoan + "}";
    }
}
