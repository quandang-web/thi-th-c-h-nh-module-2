package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public abstract class TaiKhoanNganHang {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final int id;
    private final String maTaiKhoan;
    private final String tenChuTaiKhoan;
    private final LocalDate ngayTao;

    protected TaiKhoanNganHang(int id, String maTaiKhoan, String tenChuTaiKhoan, LocalDate ngayTao) {
        this.id = id;
        this.maTaiKhoan = maTaiKhoan;
        this.tenChuTaiKhoan = tenChuTaiKhoan;
        this.ngayTao = ngayTao;
    }

    public int getId() {
        return id;
    }

    public String getMaTaiKhoan() {
        return maTaiKhoan;
    }

    public String getTenChuTaiKhoan() {
        return tenChuTaiKhoan;
    }

    public LocalDate getNgayTao() {
        return ngayTao;
    }

    protected String baseCsvFields() {
        return id + "," + maTaiKhoan + "," + tenChuTaiKhoan + "," + ngayTao.format(DATE_FORMAT);
    }

    protected String baseDisplayFields() {
        return "id=" + id
                + ", maTaiKhoan='" + maTaiKhoan + '\''
                + ", tenChuTaiKhoan='" + tenChuTaiKhoan + '\''
                + ", ngayTao=" + ngayTao.format(DATE_FORMAT);
    }

    public abstract String toCsvLine();

    @Override
    public abstract String toString();
}
