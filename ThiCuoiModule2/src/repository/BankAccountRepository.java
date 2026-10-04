package repository;

import exception.NotFoundBankAccountException;
import model.TaiKhoanNganHang;
import model.TaiKhoanThanhToan;
import model.TaiKhoanTietKiem;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;

public class BankAccountRepository {
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private final Path filePath;

    public BankAccountRepository(Path filePath) {
        this.filePath = filePath;
    }

    public List<TaiKhoanNganHang> findAll() throws IOException {
        List<TaiKhoanNganHang> accounts = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return accounts;
        }

        List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) {
                continue;
            }
            try {
                accounts.add(parseAccount(line));
            } catch (RuntimeException e) {
                throw new IOException("Du lieu khong hop le tai dong " + (i + 1) + ".", e);
            }
        }
        return accounts;
    }

    public void save(TaiKhoanNganHang account) throws IOException {
        createParentDirectory();
        Files.write(filePath, (account.toCsvLine() + System.lineSeparator()).getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    public void deleteByAccountNumber(String accountNumber)
            throws IOException, NotFoundBankAccountException {
        List<TaiKhoanNganHang> accounts = findAll();
        boolean removed = accounts.removeIf(account -> account.getMaTaiKhoan().equals(accountNumber));
        if (!removed) {
            throw new NotFoundBankAccountException();
        }

        createParentDirectory();
        List<String> lines = new ArrayList<>();
        for (TaiKhoanNganHang account : accounts) {
            lines.add(account.toCsvLine());
        }
        Files.write(filePath, lines, StandardCharsets.UTF_8);
    }

    private TaiKhoanNganHang parseAccount(String line) {
        String[] fields = line.split(",", -1);
        for (int i = 0; i < fields.length; i++) {
            fields[i] = fields[i].trim();
        }

        int id = Integer.parseInt(fields[0]);
        String accountNumber = fields[1];
        String ownerName = fields[2];
        LocalDate creationDate = LocalDate.parse(fields[3], DATE_FORMAT);

        if (fields.length == 8) {
            return new TaiKhoanTietKiem(id, accountNumber, ownerName, creationDate,
                    Long.parseLong(fields[4]), LocalDate.parse(fields[5], DATE_FORMAT),
                    new BigDecimal(fields[6]), Integer.parseInt(fields[7]));
        }
        if (fields.length == 6) {
            return new TaiKhoanThanhToan(id, accountNumber, ownerName, creationDate,
                    fields[4], Long.parseLong(fields[5]));
        }
        throw new IllegalArgumentException("So cot CSV khong dung.");
    }

    private void createParentDirectory() throws IOException {
        Path parent = filePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
    }
}
