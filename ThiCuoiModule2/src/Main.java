import exception.NotFoundBankAccountException;
import model.TaiKhoanNganHang;
import model.TaiKhoanThanhToan;
import model.TaiKhoanTietKiem;
import repository.BankAccountRepository;
import service.BankAccountService;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private final Scanner scanner = new Scanner(System.in);
    private final BankAccountService service =
            new BankAccountService(new BankAccountRepository(Path.of("data", "bank_accounts.csv")));

    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {
        boolean running = true;
        while (running) {
            System.out.println("\n===== QUAN LY TAI KHOAN NGAN HANG =====");
            System.out.println("1. Them moi");
            System.out.println("2. Xoa");
            System.out.println("3. Xem danh sach cac tai khoan ngan hang");
            System.out.println("4. Tim kiem");
            System.out.println("5. Thoat");
            String choice = readLine("Chon chuc nang: ");

            try {
                switch (choice) {
                    case "1":
                        addAccount();
                        break;
                    case "2":
                        deleteAccount();
                        break;
                    case "3":
                        printAccounts(service.getAllAccounts());
                        break;
                    case "4":
                        searchAccounts();
                        break;
                    case "5":
                        running = false;
                        System.out.println("Da thoat chuong trinh.");
                        break;
                    default:
                        System.out.println("Lua chon khong hop le.");
                }
            } catch (IOException e) {
                System.out.println("Loi doc/ghi file: " + e.getMessage());
            }
        }
    }

    private void addAccount() throws IOException {
        String type;
        do {
            System.out.println("1. Tai khoan tiet kiem");
            System.out.println("2. Tai khoan thanh toan");
            type = readLine("Chon loai tai khoan: ");
            if (!type.equals("1") && !type.equals("2")) {
                System.out.println("Loai tai khoan khong hop le.");
            }
        } while (!type.equals("1") && !type.equals("2"));

        String accountNumber;
        do {
            accountNumber = readRequired("Ma tai khoan: ");
            if (!service.isAccountNumberAvailable(accountNumber)) {
                System.out.println("Ma tai khoan da ton tai. Vui long nhap lai.");
                accountNumber = "";
            }
        } while (accountNumber.isEmpty());

        String ownerName = readRequired("Ten chu tai khoan: ");
        LocalDate creationDate = readDate("Ngay tao (dd/MM/yyyy): ");
        TaiKhoanNganHang account;

        if (type.equals("1")) {
            long depositAmount = readPositiveLong("So tien gui: ");
            LocalDate depositDate = readDate("Ngay gui (dd/MM/yyyy): ");
            BigDecimal interestRate = readPositiveDecimal("Lai suat: ");
            int termMonths = readPositiveInt("Ky han (thang): ");
            account = service.createSavingsAccount(accountNumber, ownerName, creationDate,
                    depositAmount, depositDate, interestRate, termMonths);
        } else {
            String cardNumber = readPositiveDigitNumber("So the: ");
            long balance = readPositiveLong("So tien trong tai khoan: ");
            account = service.createPaymentAccount(accountNumber, ownerName, creationDate,
                    cardNumber, balance);
        }

        service.addAccount(account);
        System.out.println("Them tai khoan thanh cong.");
    }

    private void deleteAccount() throws IOException {
        while (true) {
            String accountNumber = readRequired("Nhap ma tai khoan can xoa (Enter de quay lai): ", true);
            if (accountNumber.isEmpty()) {
                return;
            }
            try {
                if (!service.accountExists(accountNumber)) {
                    throw new NotFoundBankAccountException();
                }
                String confirmation = readConfirmation();
                if (!confirmation.equals("y")) {
                    System.out.println("Da huy thao tac xoa.");
                    return;
                }
                service.deleteAccount(accountNumber);
                System.out.println("Xoa tai khoan thanh cong. Danh sach sau khi xoa:");
                printAccounts(service.getAllAccounts());
                return;
            } catch (NotFoundBankAccountException e) {
                System.out.println(e.getMessage());
                System.out.println("Nhap lai ma tai khoan hoac nhan Enter de quay lai menu.");
            }
        }
    }

    private String readConfirmation() {
        while (true) {
            String answer = readRequired("Xac nhan xoa? (Yes/No): ");
            if (answer.equalsIgnoreCase("yes") || answer.equalsIgnoreCase("y")) {
                return "y";
            }
            if (answer.equalsIgnoreCase("no") || answer.equalsIgnoreCase("n")) {
                return "n";
            }
            System.out.println("Vui long nhap Yes hoac No.");
        }
    }

    private void searchAccounts() throws IOException {
        String keyword = readRequired("Nhap ma tai khoan hoac ten chu tai khoan can tim: ");
        List<TaiKhoanNganHang> results = service.search(keyword);
        if (results.isEmpty()) {
            System.out.println("Khong tim thay tai khoan.");
        } else {
            printAccounts(results);
        }
    }

    private void printAccounts(List<TaiKhoanNganHang> accounts) {
        if (accounts.isEmpty()) {
            System.out.println("Danh sach tai khoan trong.");
            return;
        }
        for (TaiKhoanNganHang account : accounts) {
            System.out.println(account);
        }
    }

    private String readRequired(String prompt) {
        return readRequired(prompt, false);
    }

    private String readRequired(String prompt, boolean allowEmpty) {
        while (true) {
            String value = readLine(prompt).trim();
            if (!value.isEmpty() || allowEmpty) {
                return value;
            }
            System.out.println("Thong tin khong duoc de trong.");
        }
    }

    private LocalDate readDate(String prompt) {
        while (true) {
            String value = readRequired(prompt);
            try {
                return LocalDate.parse(value, DATE_FORMAT);
            } catch (RuntimeException e) {
                System.out.println("Ngay khong hop le. Vui long nhap theo dd/MM/yyyy.");
            }
        }
    }

    private long readPositiveLong(String prompt) {
        while (true) {
            String value = readRequired(prompt);
            try {
                long number = Long.parseLong(value);
                if (number > 0) {
                    return number;
                }
            } catch (NumberFormatException ignored) {
                // Hien thi thong bao validate ben duoi.
            }
            System.out.println("Gia tri phai la so nguyen duong.");
        }
    }

    private String readPositiveDigitNumber(String prompt) {
        while (true) {
            String value = readRequired(prompt);
            try {
                if (value.matches("\\d+") && new BigDecimal(value).signum() > 0) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                // Hien thi thong bao validate ben duoi.
            }
            System.out.println("Gia tri phai la mot day chu so duong.");
        }
    }

    private BigDecimal readPositiveDecimal(String prompt) {
        while (true) {
            String value = readRequired(prompt);
            try {
                BigDecimal number = new BigDecimal(value);
                if (number.signum() > 0) {
                    return number;
                }
            } catch (NumberFormatException ignored) {
                // Hien thi thong bao validate ben duoi.
            }
            System.out.println("Gia tri phai la mot so duong.");
        }
    }

    private int readPositiveInt(String prompt) {
        while (true) {
            String value = readRequired(prompt);
            try {
                int number = Integer.parseInt(value);
                if (number > 0) {
                    return number;
                }
            } catch (NumberFormatException ignored) {
                // Hien thi thong bao validate ben duoi.
            }
            System.out.println("Gia tri phai la so nguyen duong.");
        }
    }

    private String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.hasNextLine() ? scanner.nextLine() : "5";
    }
}
