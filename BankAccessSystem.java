package Bank;

import java.sql.*;
import java.util.Scanner;

public class BankAccessSystem {
    public static void main(String[] args) throws SQLException {
        Scanner sc=new Scanner(System.in);
        menuInfo();
        while(true) {
            int serialNumber = sc.nextInt();
            Bank b = new Bank();
            switch (serialNumber) {
                case 1 -> b.creatAccount();
                case 2 -> b.login();
                case 3 -> b.deposit();
                case 4 -> b.withdrawal();
                case 5 -> System.exit(0);
                default -> System.out.println("您输入的序号有误，请重新输入！");
            }
        }
    }

    private static void menuInfo(){
        System.out.println("----------银行存取款系统----------");
        System.out.println("1.创建账户");
        System.out.println("2.登录");
        System.out.println("3.存款");
        System.out.println("4.取款");
        System.out.println("5.退出系统");
        System.out.println("-------------------------------");
        System.out.println("请输入您想进行的操作：");
    }
}

class Account {
    private String account;
    private String password;
    private double balance;

    public Account(){}
    public Account(String account,String password,double balance){
        this.account=account;
        this.password=password;
        this.balance=balance;
    }

    public String getAccount(){
        return account;
    }
    public String getPassword(){
        return password;
    }
    public double getBalance(){
        return balance;
    }
}

class Bank {
    String[] userName = new String[10];//用户名
    String[] userPass = new String[10];//密码
    double[] userBala = new double[10];//余额
    int idx = 0;

    String JDBC_URL = "jdbc:mysql://localhost:3306/testdb";
    String JDBC_USER = "root";
    String JDBC_PASSWORD = "20021022cza";
    Connection connection;

    {
        try {
            connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //创建账户
    public void creatAccount() throws SQLException {
        String sql = "INSERT INTO bankaccess (userName, userPass, balance) VALUES (?, ?, ?)";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入您想创建的账户：");
        String account = sc.next();
        System.out.println("请输入密码：");
        String password = sc.next();
        System.out.println("请输入账户余额：");
        double balance = sc.nextDouble();
        pstmt.setString(1, account);
        pstmt.setString(2, password);
        pstmt.setDouble(3, balance);
        pstmt.executeUpdate();
        boolean flag = true;
        for (int i = 0; i < userName.length; i++) {
            // 判断是否已经存在用户
            if (account.equals(userName[i])) {
                System.out.println("用户名已经存在！");
                flag = false;
            }
        }
        if (flag) {
            userName[idx] = account;
            userPass[idx] = password;
            userBala[idx] = balance;
            System.out.println("您已成功创建了一个账户。");
        }
        idx++;
    }

    public Account login() throws SQLException {
        String sql = "SELECT * FROM bankaccess WHERE userName = ? AND userPass = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        Scanner sc=new Scanner(System.in);
        System.out.println("请输入您的用户名 ：");
        String account=sc.next();
        System.out.println("请输入您的密码：");
        String password=sc.next();
        pstmt.setString(1, account);
        pstmt.setString(2, password);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            System.out.println("登录成功！");
            return new Account(rs.getString("userName"), rs.getString("userPass"), rs.getDouble("balance"));
        } else {
            return null;
        }
    }
    //存款
    public void deposit() throws SQLException {
        String sql = "UPDATE bankaccess SET balance = balance + ? WHERE userName = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        boolean a = false;
        boolean b = false;
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入你的用户名：");
        String account = sc.next();
        for (int i = 0; i < userName.length; i++) {
            if (account.equals(userName[i]))
                a = true;
        }
        System.out.println("请输入您的密码：");
        String upass = sc.next();
        for (int j = 0; j < userPass.length; j++) {
            if (upass.equals(userPass[j]))
                b = true;
        }
        System.out.println("请输入你想存的金额：");
        double amount = sc.nextDouble();
        pstmt.setDouble(1, amount);
        pstmt.setString(2, account);
        pstmt.executeUpdate();
        if (a && b) {
            System.out.println("存款成功！");
        }
    }

    //取款
    public void withdrawal() throws SQLException {
        String sql = "SELECT balance FROM bankaccess WHERE userName = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        boolean a = false,b = false;
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入您的账户：");
        String account = sc.next();
        for (int i = 0; i < userName.length; ++i) {
            if (account.equals(userName[i]))
                a = true;
        }
        System.out.println("请输入您的密码：");
        String upass = sc.next();
        for (int j = 0; j < userPass.length; ++j) {
            if (upass.equals(userPass[j]))
                b = true;
        }
        System.out.println("请输入你要取款的金额");
        double amount = sc.nextDouble();
        pstmt.setString(1, account);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next() && rs.getDouble("balance") >= amount) {
            sql = "UPDATE bankaccess SET balance = balance - ? WHERE userName = ?";
            pstmt = connection.prepareStatement(sql);
            pstmt.setDouble(1, amount);
            pstmt.setString(2, account);
            pstmt.executeUpdate();
            if (a && b) {
                System.out.println("取款成功");
            }
        }
    }


}

