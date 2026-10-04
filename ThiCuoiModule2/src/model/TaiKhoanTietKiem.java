package model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TaiKhoanTietKiem extends TaiKhoanNganHang {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final long soTienGui;
    private final LocalDate ngayGui;
    private final BigDecimal laiSuat;
    private final int kyHan;

    public TaiKhoanTietKiem(int id, String maTaiKhoan, String tenChuTaiKhoan, LocalDate ngayTao,
                            long soTienGui, LocalDate ngayGui, BigDecimal laiSuat, int kyHan) {
        super(id, maTaiKhoan, tenChuTaiKhoan, ngayTao);
        this.soTienGui = soTienGui;
        this.ngayGui = ngayGui;
        this.laiSuat = laiSuat;
        this.kyHan = kyHan;
    }

    @Override
    public String toCsvLine() {
        return baseCsvFields() + "," + soTienGui + "," + ngayGui.format(DATE_FORMAT)
                + "," + laiSuat.toPlainString() + "," + kyHan;
    }

    @Override
    public String toString() {
        return "TaiKhoanTietKiem{" + baseDisplayFields()
                + ", soTienGui=" + soTienGui
                + ", ngayGui=" + ngayGui.format(DATE_FORMAT)
                + ", laiSuat=" + laiSuat.toPlainString()
                + ", kyHan=" + kyHan + " thang}";
    }
}
