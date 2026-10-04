package service;

import exception.NotFoundBankAccountException;
import model.TaiKhoanNganHang;
import model.TaiKhoanThanhToan;
import model.TaiKhoanTietKiem;
import repository.BankAccountRepository;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class BankAccountService {
    private final BankAccountRepository repository;

    public BankAccountService(BankAccountRepository repository) {
        this.repository = repository;
    }

    public List<TaiKhoanNganHang> getAllAccounts() throws IOException {
        return repository.findAll();
    }

    public boolean isAccountNumberAvailable(String accountNumber) throws IOException {
        return repository.findAll().stream()
                .noneMatch(account -> account.getMaTaiKhoan().equals(accountNumber));
    }

    public boolean accountExists(String accountNumber) throws IOException {
        return !isAccountNumberAvailable(accountNumber);
    }

    public TaiKhoanTietKiem createSavingsAccount(String accountNumber, String ownerName,
                                                 LocalDate creationDate, long depositAmount,
                                                 LocalDate depositDate, BigDecimal interestRate,
                                                 int termMonths) throws IOException {
        validateRequired(accountNumber, "Ma tai khoan");
        validateRequired(ownerName, "Ten chu tai khoan");
        validatePositive(depositAmount, "So tien gui");
        validatePositive(interestRate, "Lai suat");
        validatePositive(termMonths, "Ky han");
        return new TaiKhoanTietKiem(nextId(), accountNumber, ownerName, creationDate,
                depositAmount, depositDate, interestRate, termMonths);
    }

    public TaiKhoanThanhToan createPaymentAccount(String accountNumber, String ownerName,
                                                  LocalDate creationDate, String cardNumber,
                                                  long balance) throws IOException {
        validateRequired(accountNumber, "Ma tai khoan");
        validateRequired(ownerName, "Ten chu tai khoan");
        validateRequired(cardNumber, "So the");
        if (!cardNumber.matches("\\d+") || new BigDecimal(cardNumber).signum() <= 0) {
            throw new IllegalArgumentException("So the phai la day chu so duong.");
        }
        validatePositive(balance, "So tien trong tai khoan");
        return new TaiKhoanThanhToan(nextId(), accountNumber, ownerName, creationDate,
                cardNumber, balance);
    }

    public void addAccount(TaiKhoanNganHang account) throws IOException {
        if (!isAccountNumberAvailable(account.getMaTaiKhoan())) {
            throw new IllegalArgumentException("Ma tai khoan da ton tai.");
        }
        repository.save(account);
    }

    public void deleteAccount(String accountNumber)
            throws IOException, NotFoundBankAccountException {
        repository.deleteByAccountNumber(accountNumber);
    }

    public List<TaiKhoanNganHang> search(String keyword) throws IOException {
        String normalizedKeyword = keyword.trim().toLowerCase(Locale.ROOT);
        return repository.findAll().stream()
                .filter(account -> account.getMaTaiKhoan().toLowerCase(Locale.ROOT)
                        .contains(normalizedKeyword)
                        || account.getTenChuTaiKhoan().toLowerCase(Locale.ROOT)
                        .contains(normalizedKeyword))
                .collect(Collectors.toList());
    }

    private int nextId() throws IOException {
        List<TaiKhoanNganHang> accounts = repository.findAll();
        return accounts.isEmpty() ? 1 : accounts.get(accounts.size() - 1).getId() + 1;
    }

    private void validateRequired(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " khong duoc de trong.");
        }
    }

    private void validatePositive(long value, String fieldName) {
        if (value <= 0) {
            throw new IllegalArgumentException(fieldName + " phai la so duong.");
        }
    }

    private void validatePositive(int value, String fieldName) {
        if (value <= 0) {
            throw new IllegalArgumentException(fieldName + " phai la so duong.");
        }
    }

    private void validatePositive(BigDecimal value, String fieldName) {
        if (value == null || value.signum() <= 0) {
            throw new IllegalArgumentException(fieldName + " phai la so duong.");
        }
    }
}
