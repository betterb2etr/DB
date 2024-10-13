package moviebook;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.ResultSetMetaData;


public class Db1 {
    public static void main (String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL 드라이버 로드
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db1", "root","1234"); // JDBC 연결
            System.out.println("DB 연결 완료");
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC 드라이버 로드 오류");
        } catch (SQLException e) {
            System.out.println("DB 연결 오류");
        }

        SwingUtilities.invokeLater(() -> {
            LoginWindow login = new LoginWindow();
            login.setVisible(true);
        });
    }

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); 
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db1", "root", "1234"); // JDBC 연결
            System.out.println("DB 연결 완료");
            return conn;
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC 드라이버 로드 오류");
        } catch (SQLException e) {
            System.out.println("DB 연결 오류");
        }
        return null;
    }
}


class LoginWindow extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton adminLoginButton;

    public LoginWindow() {
        setTitle("로그인");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel usernameLabel = new JLabel("아이디:");
        usernameField = new JTextField(15);

        JLabel passwordLabel = new JLabel("비밀번호:");
        passwordField = new JPasswordField(15);

        loginButton = new JButton("로그인");
        loginButton.addActionListener(new LoginButtonListener());

        adminLoginButton = new JButton("관리자 로그인");
        adminLoginButton.addActionListener(new AdminLoginButtonListener());

        JPanel panel = new JPanel(new GridLayout(12, 6));
        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(new JLabel());
        panel.add(loginButton);
        panel.add(new JLabel());
        panel.add(adminLoginButton);

        add(panel);
    }

    private class LoginButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            char[] password = passwordField.getPassword();

            if (validateLogin(username, new String(password))) {
                JOptionPane.showMessageDialog(LoginWindow.this, username + "님 반갑습니다.");
                MainWindow mainWindow = new MainWindow(username);
                mainWindow.setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(LoginWindow.this, "존재하지 않는 아이디 혹은 비밀번호입니다", "로그인 실패", JOptionPane.ERROR_MESSAGE);
            }
        }

        private boolean validateLogin(String username, String password) {
            if (username.equals("user1") && password.equals("user1")) {
                MovieSearch movieSearch = new MovieSearch();
                User user = movieSearch.getUserInfo(username);
                if (user == null) {
                    addDefaultUser(username, password);
                }
                return true;
            }
            return false;
        }

        private void addDefaultUser(String username, String password) {
            MovieSearch movieSearch = new MovieSearch();
            User newUser = new User(username, "홍길동", "010-0001-0202", "gildong@sju.ac", password);
            movieSearch.addUser(newUser);
        }
    }


    private class AdminLoginButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            AdminLoginWindow adminLogin = new AdminLoginWindow();
            adminLogin.setVisible(true);
            dispose();
        }
    }
}

class AdminLoginWindow extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public AdminLoginWindow() {
        setTitle("관리자 로그인");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel usernameLabel = new JLabel("관리자 아이디:");
        usernameField = new JTextField(15);

        JLabel passwordLabel = new JLabel("관리자 비밀번호:");
        passwordField = new JPasswordField(15);

        loginButton = new JButton("로그인");
        loginButton.addActionListener(new LoginButtonListener());

        JPanel panel = new JPanel(new GridLayout(12, 6));
        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(new JLabel());
        panel.add(loginButton);

        add(panel);
    }

    private class LoginButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (validateAdminLogin(username, password)) {
                JOptionPane.showMessageDialog(AdminLoginWindow.this, "관리자 " + username + "님 반갑습니다.");
                AdminMainWindow adminMain = new AdminMainWindow();
                adminMain.setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(AdminLoginWindow.this, "존재하지 않는 관리자 아이디 혹은 비밀번호입니다", "로그인 실패", JOptionPane.ERROR_MESSAGE);
            }
        }

        private boolean validateAdminLogin(String username, String password) {
            return username.equals("root") && password.equals("1234");
        }
    }
}

class AdminMainWindow extends JFrame {
    private JButton initializeButton;
    private JButton insertButton;
    private JButton deleteButton;
    private JButton updateButton;
    private JButton viewAllButton;
    private JLabel dateTimeLabel;
    private JLabel userIdLabel;
    private JTextField searchField;
    private JButton searchButton;
    private JTextArea resultArea;
    private MovieSearch movieSearch;
    private JList<String> tableList;
    private DefaultListModel<String> listModel;
    private JButton logoutButton;

    public AdminMainWindow() {
    	setTitle("관리자 페이지");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        movieSearch = new MovieSearch();
        
        initializeButton = new JButton("테이블 초기화");
        initializeButton.addActionListener(new InitializeButtonListener());
        
        insertButton = new JButton("테이블 입력");
        insertButton.addActionListener(new InsertButtonListener());
        
        deleteButton = new JButton("테이블 삭제");
        deleteButton.addActionListener(new DeleteButtonListener());
        
        updateButton = new JButton("테이블 변경");
        updateButton.addActionListener(new UpdateButtonListener());
        
        viewAllButton = new JButton("전체 테이블 보기");
        viewAllButton.addActionListener(new ViewAllButtonListener());

        dateTimeLabel = new JLabel();
        userIdLabel = new JLabel("관리자");

        searchField = new JTextField(20);
        searchButton = new JButton("검색");
        searchButton.addActionListener(new SearchButtonListener());

        logoutButton = new JButton("로그아웃"); 
        logoutButton.addActionListener(e -> logout());

        resultArea = new JTextArea(10, 30);
        resultArea.setEditable(false);

        listModel = new DefaultListModel<>();
        tableList = new JList<>(listModel);
        tableList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel topRightPanel = new JPanel();
        topRightPanel.setLayout(new BoxLayout(topRightPanel, BoxLayout.Y_AXIS));
        topRightPanel.add(dateTimeLabel);
        topRightPanel.setBorder(new EmptyBorder(0,0,0,30));
        topRightPanel.add(userIdLabel);

        JPanel centerPanel = new JPanel();
        centerPanel.add(searchField);
        centerPanel.add(searchButton);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(topRightPanel, BorderLayout.EAST);
        topPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(400, 600)); 
        leftPanel.add(new JScrollPane(tableList), BorderLayout.CENTER);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        mainPanel.add(leftPanel);
        mainPanel.add(new JScrollPane(resultArea));

        JPanel buttonPanel = new JPanel(new GridLayout(2, 1));

        JPanel descriptionPanel = new JPanel(new GridLayout(5, 1));
        descriptionPanel.add(new JLabel("테이블 초기화: 데이터베이스를 초기화하고 샘플 데이터를 삽입합니다."));
        descriptionPanel.add(new JLabel("테이블 입력: 특정 테이블에 sql을 작성한대로 데이터를 삽입합니다."));
        descriptionPanel.add(new JLabel("테이블 삭제: 특정 테이블에서 sql을 작성한대로 데이터를 삭제합니다."));
        descriptionPanel.add(new JLabel("테이블 변경: 특정 테이블의 sql을 작성한대로 데이터를 변경합니다."));
        descriptionPanel.add(new JLabel("전체 테이블 보기: 모든 테이블의 내용을 봅니다."));

        JPanel buttonsPanel = new JPanel(new GridLayout(1, 5));
        buttonsPanel.add(initializeButton);
        buttonsPanel.add(insertButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(updateButton);
        buttonsPanel.add(viewAllButton);
        buttonsPanel.add(logoutButton);

        buttonPanel.add(descriptionPanel);
        buttonPanel.add(buttonsPanel);

        add(topPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        updateDateTime();
    }
    private void logout() {
        LoginWindow loginWindow = new LoginWindow();
        loginWindow.setVisible(true);
        dispose(); 
    }

    private void updateDateTime() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                dateTimeLabel.setText(sdf.format(new Date()));
            }
        }, 0, 1000);
    }

    private class InitializeButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            initializeTables();
        }
    }

    private void initializeTables() {
        String url = "jdbc:mysql://localhost:3306/db1";
        String user = "root";
        String password = "1234";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("SET FOREIGN_KEY_CHECKS=0;");
            stmt.executeUpdate("DROP TABLE IF EXISTS `screen`;");
            stmt.executeUpdate("DROP TABLE IF EXISTS `movie`;");
            stmt.executeUpdate("DROP TABLE IF EXISTS `theaters`;");
            stmt.executeUpdate("DROP TABLE IF EXISTS `member`;");
            stmt.executeUpdate("DROP TABLE IF EXISTS `seats`;");
            stmt.executeUpdate("DROP TABLE IF EXISTS `reservation`;");
            stmt.executeUpdate("DROP TABLE IF EXISTS `ticket`;");
            stmt.executeUpdate("DROP TABLE IF EXISTS `review`;");
            stmt.executeUpdate("SET FOREIGN_KEY_CHECKS=1;");

            String createTheaterTableSQL = "CREATE TABLE IF NOT EXISTS db1.theaters (" +
                    "theater_id INT NOT NULL AUTO_INCREMENT COMMENT '상영관 아이디'," +
                    "seats INT NOT NULL COMMENT '좌석 수'," +
                    "activation TINYINT NOT NULL COMMENT '상영관 사용 여부'," +
                    "`rows` INT NOT NULL COMMENT '가로'," +
                    "`cols` INT NOT NULL COMMENT '세로'," +
                    "PRIMARY KEY (theater_id)" +
                    ") ENGINE = InnoDB;";

            String createMovieTableSQL = "CREATE TABLE IF NOT EXISTS db1.movie (" +
                    "movie_id INT NOT NULL AUTO_INCREMENT COMMENT '영화 ID'," +
                    "title VARCHAR(255) NOT NULL COMMENT '영화제목'," +
                    "duration TIME NOT NULL COMMENT '상영시간'," +
                    "rate VARCHAR(10) NOT NULL COMMENT '상영 등급'," +
                    "director VARCHAR(255) NOT NULL COMMENT '감독명'," +
                    "actor VARCHAR(255) NOT NULL COMMENT '배우명'," +
                    "genre VARCHAR(50) NOT NULL COMMENT '장르'," +
                    "`explain` TEXT NOT NULL COMMENT '영화 설명'," +
                    "release_Date DATE NOT NULL COMMENT '개봉일'," +
                    "avg_score DECIMAL(3,2) NOT NULL COMMENT '평균 평점'," +
                    "PRIMARY KEY (movie_id)" +
                    ") ENGINE = InnoDB;";

            String createMemberTableSQL = "CREATE TABLE IF NOT EXISTS db1.member (" +
                    "member_id VARCHAR(50) NOT NULL COMMENT '회원아이디'," +
                    "name VARCHAR(255) NOT NULL COMMENT '이름'," +
                    "phone VARCHAR(20) NOT NULL COMMENT '전화번호'," +
                    "email VARCHAR(255) NOT NULL COMMENT '이메일'," +
                    "password VARCHAR(255) NOT NULL COMMENT '비밀번호'," +
                    "PRIMARY KEY (member_id)" +
                    ") ENGINE = InnoDB;";

            String createSeatsTableSQL = "CREATE TABLE IF NOT EXISTS db1.seats (" +
                    "seat_id INT NOT NULL AUTO_INCREMENT COMMENT '좌석 아이디'," +
                    "theater_id INT NOT NULL COMMENT '상영관 아이디'," +
                    "`row` INT NOT NULL COMMENT '가로'," +
                    "`col` INT NOT NULL COMMENT '세로'," +
                    "active TINYINT NOT NULL COMMENT '좌석 사용 여부'," +
                    "PRIMARY KEY (seat_id)," +
                    "INDEX theater_id_idx (theater_id ASC)," +
                    "CONSTRAINT fk_seat_theater FOREIGN KEY (theater_id) REFERENCES db1.theaters (theater_id) ON DELETE NO ACTION ON UPDATE NO ACTION" +
                    ") ENGINE = InnoDB;";

            String createReservationTableSQL = "CREATE TABLE IF NOT EXISTS db1.reservation (" +
                    "reservation_id INT NOT NULL AUTO_INCREMENT COMMENT '예매 아이디'," +
                    "method VARCHAR(50) NOT NULL COMMENT '결제 수단'," +
                    "status VARCHAR(50) NOT NULL COMMENT '결제 상태'," +
                    "amount DECIMAL(10,2) NOT NULL COMMENT '결제 금액'," +
                    "member_id VARCHAR(50) NOT NULL COMMENT '회원 아이디'," +
                    "pay_date DATE NOT NULL COMMENT '결제 날짜'," +
                    "PRIMARY KEY (reservation_id)," +
                    "INDEX member_id_idx (member_id ASC)," +
                    "CONSTRAINT fk_reservation_member FOREIGN KEY (member_id) REFERENCES db1.member (member_id) ON DELETE NO ACTION ON UPDATE NO ACTION" +
                    ") ENGINE = InnoDB;";

            String createScreenTableSQL = "CREATE TABLE IF NOT EXISTS db1.screen (" +
                    "screen_id INT NOT NULL AUTO_INCREMENT COMMENT '상영 일정 아이디'," +
                    "movie_id INT NOT NULL COMMENT '영화 아이디'," +
                    "theater_id INT NOT NULL COMMENT '상영관 아이디'," +
                    "`start` DATE NOT NULL COMMENT '상영 시작일'," +
                    "day_of_weeks VARCHAR(10) NOT NULL COMMENT '상영요일'," +
                    "show_start TIME NOT NULL COMMENT '상영 시작 시간'," +
                    "show_time INT NOT NULL COMMENT '상영 회차'," +
                    "PRIMARY KEY (screen_id)," +
                    "INDEX movie_id_idx (movie_id ASC)," +
                    "INDEX theater_id_idx (theater_id ASC)," +
                    "CONSTRAINT fk_screen_movie FOREIGN KEY (movie_id) REFERENCES db1.movie (movie_id) ON DELETE NO ACTION ON UPDATE NO ACTION," +
                    "CONSTRAINT fk_screen_theater FOREIGN KEY (theater_id) REFERENCES db1.theaters (theater_id) ON DELETE NO ACTION ON UPDATE NO ACTION" +
                    ") ENGINE = InnoDB;";

            String createTicketTableSQL = "CREATE TABLE IF NOT EXISTS db1.ticket (" +
                    "ticket_id INT NOT NULL AUTO_INCREMENT COMMENT '티켓 ID'," +
                    "screen_id INT NOT NULL COMMENT '상영 일정 ID'," +
                    "theater_id INT NOT NULL COMMENT '상영관 ID'," +
                    "seat_id INT NOT NULL COMMENT '좌석 ID'," +
                    "reservation_id INT NULL COMMENT '예매 ID'," +
                    "TF TINYINT NOT NULL COMMENT '티켓 발권 여부'," +
                    "standard_price DECIMAL(10,2) NOT NULL COMMENT '티켓 표준 가격'," +
                    "sell_price DECIMAL(10,2) NULL COMMENT '티켓 판매 가격'," +
                    "PRIMARY KEY (ticket_id)," +
                    "INDEX screen_id_idx (screen_id ASC)," +
                    "INDEX theater_id_idx (theater_id ASC)," +
                    "INDEX seat_id_idx (seat_id ASC)," +
                    "INDEX reservation_id_idx (reservation_id ASC)," +
                    "CONSTRAINT fk_ticket_screen FOREIGN KEY (screen_id) REFERENCES db1.screen (screen_id) ON DELETE NO ACTION ON UPDATE NO ACTION," +
                    "CONSTRAINT fk_ticket_theater FOREIGN KEY (theater_id) REFERENCES db1.theaters (theater_id) ON DELETE NO ACTION ON UPDATE NO ACTION," +
                    "CONSTRAINT fk_ticket_seat FOREIGN KEY (seat_id) REFERENCES db1.seats (seat_id) ON DELETE NO ACTION ON UPDATE NO ACTION," +
                    "CONSTRAINT fk_ticket_reservation FOREIGN KEY (reservation_id) REFERENCES db1.reservation (reservation_id) ON DELETE NO ACTION ON UPDATE NO ACTION" +
                    ") ENGINE = InnoDB;";

            String createReviewTableSQL = "CREATE TABLE IF NOT EXISTS db1.review (" +
                    "review_id INT NOT NULL AUTO_INCREMENT COMMENT '리뷰 아이디'," +
                    "movie_id INT NOT NULL COMMENT '영화 아이디'," +
                    "member_id VARCHAR(50) NOT NULL COMMENT '회원아이디'," +
                    "score DECIMAL(3,2) NOT NULL COMMENT '평점'," +
                    "review_date DATE NOT NULL COMMENT '리뷰 날짜'," +
                    "PRIMARY KEY (review_id)," +
                    "INDEX movie_id_idx (movie_id ASC)," +
                    "INDEX member_id_idx (member_id ASC)," +
                    "CONSTRAINT fk_review_movie FOREIGN KEY (movie_id) REFERENCES db1.movie (movie_id) ON DELETE NO ACTION ON UPDATE NO ACTION," +
                    "CONSTRAINT fk_review_member FOREIGN KEY (member_id) REFERENCES db1.member (member_id) ON DELETE NO ACTION ON UPDATE NO ACTION" +
                    ") ENGINE = InnoDB;";

            stmt.executeUpdate(createTheaterTableSQL);
            stmt.executeUpdate(createMovieTableSQL);
            stmt.executeUpdate(createMemberTableSQL);
            stmt.executeUpdate(createSeatsTableSQL);
            stmt.executeUpdate(createReservationTableSQL);
            stmt.executeUpdate(createScreenTableSQL);
            stmt.executeUpdate(createTicketTableSQL);
            stmt.executeUpdate(createReviewTableSQL);

            String insertMovieDataSQL = "INSERT INTO `movie` (`title`, `duration`, `rate`, `director`, `actor`, `genre`, `explain`, `release_Date`, `avg_score`) VALUES" +
            		"('테넷', '02:30:00', '12', '크리스토퍼 놀란', '로버트 패틴슨', '액션', '세계대전을 막아야 한다!', '2023-01-26', 8.7)," +
                    "('라라랜드', '02:07:00', '12', '데이미언 셔젤', '엠마 스톤', '드라마', '꿈을 꾸는 사람들', '2023-02-07', 8.9)," +
                    "('엘리멘탈', '02:09:00', '전체', '피터 손', '레아 루이스', '애니메이션', '4개의 원소들이 살고 있는 엘리멘트 시티', '2023-03-14', 8.9)," +
                    "('바비', '01:54:00', '12', '그레타 거윅', '마고 로비', '드라마', '원하는 무엇이든 될 수 있는 바비랜드', '2023-04-19', 8.3)," +
                    "('서울의 봄', '01:40:00', '12', '김성수', '황정민', '드라마', '대한민국의 운명이 바뀌었다', '2023-05-22', 9.5)," +
                    "('파묘', '02:10:00', '15', '장재현', '이도현', '미스터리', '조상의 묫자리 가 회고', '2023-06-22', 8.6)," +
                    "('아가일', '02:20:00', '15', '매튜 본', '헨리 카빌', '액션', '현실을 넘치는 스파이 세계', '2023-07-07', 7.3)," +
                    "('그녀가 죽었다', '01:40:00', '15', '김세휘', '변요한', '미스터리', '그냥 보기만 하는 거예요', '2023-08-15', 8.4)," +
                    "('킹카', '02:30:00', '전체', '줄킹', '티모시 샬라메', '판타지', '모두 꿈에서부터 시작됐다', '2023-09-30', 7.9)," +
                    "('베테랑', '02:00:00', '15', '류승완', '황정민', '액션', '무조건 끝을 보는 행동파', '2023-10-15', 8.4)," +
                    "('알라딘', '02:08:00', '전체', '가이 리치', '윌 스미스', '판타지', '신비의 아그라바 왕국의 시대', '2023-11-23', 9.4)," +
                    "('도둑들', '02:15:00', '15', '최동훈', '전지현', '액션', '목적인 서로 다른 10인의 도둑들', '2023-12-25', 8.3)," +
                    "('어벤져스: 엔드게임', '03:01:00', '12', '안소니 루소, 조 루소', '로버트 다우니 주니어', '액션', '세계의 운명을 건 최후의 전투', '2023-02-28', 9.0),"+
                    "('기생충', '02:12:00', '15', '봉준호', '송강호', '드라마', '두 가족의 숨막히는 생존 게임', '2023-03-11', 8.6),"+
                    "('인터스텔라', '02:49:00', '12', '크리스토퍼 놀란', '매튜 맥커너히', 'SF', '인류의 미래를 위한 우주 탐사', '2023-04-22', 8.9),"+
                    "('스파이더맨: 파 프롬 홈', '02:09:00', '12', '존 왓츠', '톰 홀랜드', '액션', '유럽을 무대로 한 스파이더맨의 새로운 모험', '2023-05-15', 8.1),"+
                    "('인사이드 아웃', '01:35:00', '전체', '피트 닥터', '에이미 포엘러', '애니메이션', '감정들의 모험이 펼쳐지는 뇌 속 세계', '2023-06-20', 8.5),"+
                    "('주토피아', '01:48:00', '전체', '바이런 하워드, 리치 무어', '지니퍼 굿윈', '애니메이션', '동물들의 대도시에서 펼쳐지는 미스터리', '2023-07-30', 8.7),"+
                    "('포드 V 페라리', '02:32:00', '12', '제임스 맨골드', '맷 데이먼', '드라마', '르망 24시간 레이스에서의 역사적 대결', '2023-08-25', 8.4),"+
                    "('토이스토리 4', '01:40:00', '전체', '조시 쿨리', '톰 행크스', '애니메이션', '새로운 친구와의 모험을 떠나는 장난감들', '2023-09-10', 8.2),"+
                    "('헝거게임: 모킹제이', '02:23:00', '15', '프란시스 로렌스', '제니퍼 로렌스', '액션', '혁명을 이끄는 주인공의 마지막 전투', '2023-10-05', 7.5),"+
                    "('메이즈 러너: 데스 큐어', '02:22:00', '12', '웨스 볼', '딜런 오브라이언', 'SF', '미로를 탈출한 소년들의 마지막 생존 싸움', '2023-11-14', 7.3),"+
                    "('겨울왕국 2', '01:43:00', '전체', '크리스 벅, 제니퍼 리', '크리스틴 벨', '애니메이션', '엘사와 안나의 새로운 모험', '2023-12-01', 8.6),"+
                    "('오늘밤', '02:12:00', '15', '신재영', '최지훈', '학술', '깊어가는 학술제의 밤', '2023-05-30', 8.6);";


            String insertTheaterDataSQL = "INSERT INTO `theaters` (`seats`, `activation`, `rows`, `cols`) VALUES" +
                    "(9, 1, 3, 3)," +
                    "(9, 1, 3, 3)," +
                    "(9, 1, 3, 3)," +
                    "(9, 1, 3, 3)," +
                    "(9, 1, 3, 3)," +
                    "(9, 1, 3, 3)," +
                    "(9, 1, 3, 3)," +
                    "(9, 1, 3, 3)," +
                    "(9, 1, 3, 3)," +
                    "(9, 1, 3, 3)," +
                    "(9, 1, 3, 3)," +
                    "(9, 1, 3, 3)," +
                    "(9, 1, 3, 3)," +
                    "(9, 1, 3, 3)," +
                    "(9, 1, 3, 3)," +
                    "(9, 1, 3, 3)," +
                    "(9, 1, 3, 3)," +
                    "(9, 1, 3, 3)," +
                    "(9, 1, 3, 3)," +
                    "(9, 1, 3, 3)," +
                    "(9, 1, 3, 3)," +
                    "(9, 1, 3, 3)," +
                    "(9, 1, 3, 3);";
                    
                 

            String insertScreenDataSQL ="INSERT INTO `screen` (`movie_id`, `theater_id`, `start`, `day_of_weeks`, `show_start`, `show_time`) VALUES" +
            		 "(1, 1, '2023-01-27', '금요일', '10:00:00', 150)," +            	    
             	    "(2, 4, '2023-02-08', '수요일', '10:00:00', 127)," +            	    
             	    "(3, 7, '2023-03-15', '수요일', '10:00:00', 129)," +            	  
             	    "(4, 10, '2023-04-20', '목요일', '10:00:00', 114)," +            	    
             	    "(5, 2, '2023-05-23', '화요일', '10:00:00', 100)," +           	        	   
             	    "(6, 5, '2023-06-24', '토요일', '14:00:00', 140)," +
             	    "(7, 8, '2023-07-09', '일요일', '14:00:00', 130)," +           	  
             	    "(8, 11, '2023-08-17', '목요일', '14:00:00', 120)," +
             	    "(9, 3, '2023-10-03', '화요일', '18:00:00', 110)," +
             	    "(10, 6, '2023-10-18', '수요일', '18:00:00', 100)," +
             	    "(11, 9, '2023-11-26', '일요일', '18:00:00', 120)," +
             	    "(12, 12, '2023-12-28', '목요일', '18:00:00', 135),"+
             	    "(13, 1, '2023-01-28', '토요일', '12:00:00', 181)," + 
             	    "(14, 2, '2023-02-12', '일요일', '15:00:00', 132)," + 
             	    "(15, 3, '2023-03-16', '목요일', '17:00:00', 169)," + 
             	    "(16, 4, '2023-04-23', '일요일', '20:00:00', 129)," + 
             	    "(17, 5, '2023-05-27', '토요일', '10:00:00', 95)," + 
             	    "(18, 6, '2023-06-25', '일요일', '13:00:00', 140)," + 
             	    "(19, 7, '2023-07-30', '일요일', '16:00:00', 121)," + 
             	    "(20, 8, '2023-08-20', '일요일', '19:00:00', 105)," + 
             	    "(21, 9, '2023-09-15', '금요일', '11:00:00', 113)," + 
             	    "(22, 10, '2023-10-28', '토요일', '15:00:00', 125)," + 
             	    "(23, 11, '2023-11-17', '금요일', '18:00:00', 110)," +
             	    "(24, 12, '2023-05-30', '금요일', '23:30:00',132);";



            String insertMemberDataSQL = "INSERT INTO `member` (`member_id`, `name`, `phone`, `email`, `password`) VALUES" +
                    "('b001', '김서현', '010-2496-9408', 'b2etr@naver.com', '1234')," +
                    "('b002', '부창오', '010-4928-3284', 'bco3284@naver.com', '1234')," +
                    "('b003', '이윤담', '010-0801-1111', 'yundam01@naver.com', '1234')," +
                    "('b004', '송하종', '010-0802-2222', 'haiong01@naver.com', '1234')," +
                    "('b005', '김영우', '010-0803-3333', 'wonyoung@naver.com', '1234')," +
                    "('g006', '정지유', '010-0804-4444', 'geeyou03@naver.com', '1234')," +
                    "('h007', '유현성', '010-0805-5555', 'hyuonseong@naver.com', '1234')," +
                    "('h008', '이현아', '010-0806-6666', 'hyuna02@naver.com', '1234')," +
                    "('e009', '고은서', '010-0807-7777', 'eunseo03@naver.com', '1234')," +
                    "('j010', '김정안', '010-0808-8888', 'jeongan@naver.com', '1234')," +
                    "('s011', '이시연', '010-0809-9999', 'siyeon03@gmail.com', '1234')," +
                    "('z001', '최지훈', '010-2496-9402', 'b2r@naver.com', '1234')," +
                    "('z002', '신재영', '010-4928-3285', 'bc384@naver.com', '1234')," +
                    "('z003', '김학선', '010-0801-1115', 'yudm01@naver.com', '1234')," +
                    "('k004', '권혜진', '010-0802-2223', 'hiong01@naver.com', '1234')," +
                    "('k005', '백성우', '010-0803-3336', 'wnyung@naver.com', '1234')," +
                    "('k006', '손우진', '010-0804-4447', 'eyou03@naver.com', '1234')," +
                    "('l007', '이영현', '010-0805-5558', 'hyeong@naver.com', '1234')," +
                    "('l008', '김태윤', '010-0806-6669', 'una02@naver.com', '1234')," +
                    "('l009', '김현수', '010-0807-7770', 'eueo03@naver.com', '1234')," +
                    "('p010', '김승기', '010-0808-8812', 'jeoan@naver.com', '1234')," +
                    "('p011', '부창희', '010-0809-9932', 'son03@gmail.com', '1234')," +
                    "('p012', '부경환', '010-0809-9232', 'son0@gmail.com', '1234')," +
                    "('g012', '이경빈', '010-0810-1010', 'gyeongbin@gmail.com', '1234');";

            String insertSeatsDataSQL = "INSERT INTO `seats` (`theater_id`, `row`, `col`, `active`) VALUES" +
                    "(1, 1, 1, 0)," +
                    "(1, 1, 2, 0)," +
                    "(1, 1, 3, 0)," +
                    "(1, 2, 1, 0)," +
                    "(1, 2, 2, 0)," +
                    "(1, 2, 3, 0)," +
                    "(1, 3, 1, 0)," +
                    "(1, 3, 2, 0)," +
                    "(1, 3, 3, 0)," +
                    "(2, 1, 1, 0)," +
                    "(2, 1, 2, 0)," +
                    "(2, 1, 3, 0)," +
                    "(2, 2, 1, 0)," +
                    "(2, 2, 2, 0)," +
                    "(2, 2, 3, 0)," +
                    "(2, 3, 1, 0)," +
                    "(2, 3, 2, 0)," +
                    "(2, 3, 3, 0)," +
                    "(3, 1, 1, 0)," +
                    "(3, 1, 2, 0)," +
                    "(3, 1, 3, 0)," +
                    "(3, 2, 1, 0)," +
                    "(3, 2, 2, 0)," +
                    "(3, 2, 3, 0)," +
                    "(3, 3, 1, 0)," +
                    "(3, 3, 2, 0)," +
                    "(3, 3, 3, 0)," +
                    "(4, 1, 1, 0)," +
                    "(4, 1, 2, 0)," +
                    "(4, 1, 3, 0)," +
                    "(4, 2, 1, 0)," +
                    "(4, 2, 2, 0)," +
                    "(4, 2, 3, 0)," +
                    "(4, 3, 1, 0)," +
                    "(4, 3, 2, 0)," +
                    "(4, 3, 3, 0)," +
                    "(5, 1, 1, 0)," +
                    "(5, 1, 2, 0)," +
                    "(5, 1, 3, 0)," +
                    "(5, 2, 1, 0)," +
                    "(5, 2, 2, 0)," +
                    "(5, 2, 3, 0)," +
                    "(5, 3, 1, 0)," +
                    "(5, 3, 2, 0)," +
                    "(5, 3, 3, 0)," +
                    "(6, 1, 1, 0)," +
                    "(6, 1, 2, 0)," +
                    "(6, 1, 3, 0)," +
                    "(6, 2, 1, 0)," +
                    "(6, 2, 2, 0)," +
                    "(6, 2, 3, 0)," +
                    "(6, 3, 1, 0)," +
                    "(6, 3, 2, 0)," +
                    "(6, 3, 3, 0)," +
                    "(7, 1, 1, 0)," +
                    "(7, 1, 2, 0)," +
                    "(7, 1, 3, 0)," +
                    "(7, 2, 1, 0)," +
                    "(7, 2, 2, 0)," +
                    "(7, 2, 3, 0)," +
                    "(7, 3, 1, 0)," +
                    "(7, 3, 2, 0)," +
                    "(7, 3, 3, 0)," +
                    "(8, 1, 1, 0)," +
                    "(8, 1, 2, 0)," +
                    "(8, 1, 3, 0)," +
                    "(8, 2, 1, 0)," +
                    "(8, 2, 2, 0)," +
                    "(8, 2, 3, 0)," +
                    "(8, 3, 1, 0)," +
                    "(8, 3, 2, 0)," +
                    "(8, 3, 3, 0)," +
                    "(9, 1, 1, 0)," +
                    "(9, 1, 2, 0)," +
                    "(9, 1, 3, 0)," +
                    "(9, 2, 1, 0)," +
                    "(9, 2, 2, 0)," +
                    "(9, 2, 3, 0)," +
                    "(9, 3, 1, 0)," +
                    "(9, 3, 2, 0)," +
                    "(9, 3, 3, 0)," +
                    "(10, 1, 1, 0)," +
                    "(10, 1, 2, 0)," +
                    "(10, 1, 3, 0)," +
                    "(10, 2, 1, 0)," +
                    "(10, 2, 2, 0)," +
                    "(10, 2, 3, 0)," +
                    "(10, 3, 1, 0)," +
                    "(10, 3, 2, 0)," +
                    "(10, 3, 3, 0)," +
                    "(11, 1, 1, 0)," +
                    "(11, 1, 2, 0)," +
                    "(11, 1, 3, 0)," +
                    "(11, 2, 1, 0)," +
                    "(11, 2, 2, 0)," +
                    "(11, 2, 3, 0)," +
                    "(11, 3, 1, 0)," +
                    "(11, 3, 2, 0)," +
                    "(11, 3, 3, 0)," +
                    "(12, 1, 1, 0)," +
                    "(12, 1, 2, 0)," +
                    "(12, 1, 3, 0)," +
                    "(12, 2, 1, 0)," +
                    "(12, 2, 2, 0)," +
                    "(12, 2, 3, 0)," +
                    "(12, 3, 1, 0)," +
                    "(12, 3, 2, 0)," +
                    "(12, 3, 3, 0);";

            String insertReservationDataSQL = "INSERT INTO `reservation` (`method`, `status`, `amount`, `member_id`, `pay_date`) VALUES" +
            		"('신용카드', 'TRUE', 1, 'b001', '2024-06-08')," +
                    "('신용카드', 'TRUE', 1, 'b002', '2024-06-08')," +
                    "('기타결제', 'TRUE', 1, 'b003', '2024-06-08')," +
                    "('휴대폰결제', 'TRUE', 1, 'b004', '2024-06-08')," +
                    "('신용카드', 'TRUE', 1, 'b005', '2024-06-08')," +
                    "('휴대폰결제', 'TRUE', 1, 'g006', '2024-06-08')," +
                    "('신용카드', 'TRUE', 1, 'h007', '2024-06-08')," +
                    "('휴대폰결제', 'TRUE', 1, 'h008', '2024-06-08')," +
                    "('신용카드', 'TRUE', 1, 'e009', '2024-06-08')," +
                    "('휴대폰결제', 'TRUE', 1, 'j010', '2024-06-08')," +
                    "('신용카드', 'TRUE', 3, 's011', '2024-06-08')," +
                    "('신용카드', 'TRUE', 3, 'g012', '2024-06-08'),"+
                    "('신용카드', 'TRUE', 2, 'z001', '2024-01-15')," +
                    "('신용카드', 'TRUE', 2, 'z002', '2024-01-20')," +
                    "('기타결제', 'TRUE', 2, 'z003', '2024-02-05')," +
                    "('휴대폰결제', 'TRUE', 2, 'k004', '2024-02-25')," +
                    "('신용카드', 'TRUE', 2, 'k005', '2024-03-10')," +
                    "('휴대폰결제', 'TRUE', 2, 'k006', '2024-03-15')," +
                    "('신용카드', 'TRUE', 2, 'l007', '2024-03-30')," +
                    "('휴대폰결제', 'TRUE', 2, 'l008', '2024-04-12')," +
                    "('신용카드', 'TRUE', 2, 'l009', '2024-04-28')," +
                    "('휴대폰결제', 'TRUE', 2, 'p010', '2024-05-10')," +
                    "('신용카드', 'TRUE', 2, 'p011', '2024-05-20')," +
                    "('신용카드', 'TRUE', 2, 'g012', '2024-05-30');";


            String insertTicketDataSQL = "INSERT INTO `ticket` (`screen_id`, `theater_id`, `seat_id`, `reservation_id`, `TF`, `sell_price`, `standard_price`) VALUES" +
                    "(1, 1, 1, 1, 0, 15000, 15000)," +
                    "(2, 2, 2, 2, 0, 15000, 15000)," +
                    "(3, 3, 3, 3, 0, 15000, 15000)," +
                    "(4, 4, 4, 4, 0, 15000, 15000)," +
                    "(5, 5, 5, 5, 0, 15000, 15000)," +
                    "(6, 6, 6, 6, 0, 15000, 15000)," +
                    "(7, 7, 7, 7, 0, 15000, 15000)," +
                    "(8, 8, 8, 8, 0, 15000, 15000)," +
                    "(9, 9, 9, 9, 0, 15000, 15000)," +
                    "(10, 10, 10, 10, 0, 15000, 15000)," +
                    "(11, 11, 11, 11, 0, 15000, 15000)," +
                    "(2, 1, 1, 1, 0, 15000, 15000)," +
                    "(3, 2, 2, 2, 0, 15000, 15000)," +
                    "(4, 3, 3, 3, 0, 15000, 15000)," +
                    "(5, 4, 4, 4, 0, 15000, 15000)," +
                    "(6, 5, 5, 5, 0, 15000, 15000)," +
                    "(7, 6, 6, 6, 0, 15000, 15000)," +
                    "(8, 7, 7, 7, 0, 15000, 15000)," +
                    "(9, 8, 8, 8, 0, 15000, 15000)," +
                    "(10, 9, 9, 9, 0, 15000, 15000)," +
                    "(10, 10, 8, 10, 0, 15000, 15000)," +
                    "(11, 11, 10, 11, 0, 15000, 15000)," +
                    "(11, 10, 10, 11, 0, 15000, 15000)," +
                    "(12, 12, 12, 12, 0, 15000, 15000);";

            String insertReviewDataSQL = "INSERT INTO `review` (`movie_id`, `member_id`, `score`, `review_date`) VALUES" +
                    "(1, 'b001', 8.1, '2024-06-10')," +
                    "(2, 'b002', 8.2, '2024-06-10')," +
                    "(3, 'b003', 8.3, '2024-06-10')," +
                    "(4, 'b004', 8.4, '2024-06-10')," +
                    "(5, 'b005', 8.5, '2024-06-10')," +
                    "(6, 'g006', 8.6, '2024-06-10')," +
                    "(7, 'h007', 8.7, '2024-06-10')," +
                    "(8, 'h008', 8.8, '2024-06-10')," +
                    "(9, 'e009', 8.9, '2024-06-10')," +
                    "(10, 'j010', 8.1, '2024-06-10')," +
                    "(11, 's011', 9.2, '2024-06-10')," +
                    "(13, 'z001', 8.1, '2024-06-10')," +
                    "(14, 'z002', 8.2, '2024-06-10')," +
                    "(15, 'z003', 8.3, '2024-06-10')," +
                    "(16, 'k004', 8.4, '2024-06-10')," +
                    "(17, 'k005', 8.5, '2024-06-10')," +
                    "(18, 'k006', 8.6, '2024-06-10')," +
                    "(19, 'l007', 8.7, '2024-06-10')," +
                    "(20, 'l008', 8.8, '2024-06-10')," +
                    "(21, 'l009', 8.9, '2024-06-10')," +
                    "(22, 'p010', 8.1, '2024-06-10')," +
                    "(23, 'p011', 9.2, '2024-06-10')," +
                    "(23, 's011', 9.2, '2024-06-10')," +
                    "(12, 'g012', 8.4, '2024-06-10');";
            

            stmt.executeUpdate(insertMovieDataSQL);
            stmt.executeUpdate(insertTheaterDataSQL);
            stmt.executeUpdate(insertScreenDataSQL);
            stmt.executeUpdate(insertMemberDataSQL);
            stmt.executeUpdate(insertSeatsDataSQL);
            stmt.executeUpdate(insertReservationDataSQL);
            stmt.executeUpdate(insertTicketDataSQL);
            stmt.executeUpdate(insertReviewDataSQL);

            JOptionPane.showMessageDialog(this, "테이블이 샘플 데이터로 초기화되었습니다.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "테이블 초기화 오류: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    private class InsertButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            TableInsertWindow insertForm = new TableInsertWindow();
            insertForm.setVisible(true);
        }
    }

    private class DeleteButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            TableDeleteWindow deleteForm = new TableDeleteWindow();
            deleteForm.setVisible(true);
        }
    }

    private class UpdateButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            TableUpdateWindow updateForm = new TableUpdateWindow();
            updateForm.setVisible(true);
        }
    }

    private class ViewAllButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            viewAllTables();
        }
    }

    private void viewAllTables() {
        Connection connection = Db1.getConnection();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SHOW TABLES");

            listModel.clear();
            while (resultSet.next()) {
                listModel.addElement(resultSet.getString(1));
            }

            tableList.addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    String selectedTable = tableList.getSelectedValue();
                    if (selectedTable != null) {
                        displayTableContents(selectedTable);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayTableContents(String tableName) {
        Connection connection = Db1.getConnection();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName);
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            StringBuilder tableContents = new StringBuilder();
            for (int i = 1; i <= columnCount; i++) {
                tableContents.append(metaData.getColumnName(i)).append("\t");
            }
            tableContents.append("\n");

            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    tableContents.append(resultSet.getString(i)).append("\t");
                }
                tableContents.append("\n");
            }

            resultArea.setText(tableContents.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class SearchButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String searchText = searchField.getText();
            String[] searchParams = searchText.split(",");

            String title = searchParams.length > 0 ? searchParams[0].trim() : "";
            String director = searchParams.length > 1 ? searchParams[1].trim() : "";
            String actors = searchParams.length > 2 ? searchParams[2].trim() : "";
            String genre = searchParams.length > 3 ? searchParams[3].trim() : "";

            List<String> results = movieSearch.searchMovies(title, director, actors, genre);

            listModel.clear();
            if (results.isEmpty()) {
                resultArea.setText("해당하는 영화가 없습니다.");
            } else {
                for (String result : results) {
                    listModel.addElement(result);
                }
            }
        }
    }
}

class TableInsertWindow extends JFrame {
    private JTextField tableNameField;
    private JTextArea sqlField;
    private JButton insertButton;
    private JButton cancelButton;
    private Connection connection;

    public TableInsertWindow() {
        setTitle("테이블 입력");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        tableNameField = new JTextField(10);
        sqlField = new JTextArea(10, 30);
        insertButton = new JButton("입력");
        insertButton.addActionListener(e -> insertData());

        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("테이블 이름:"));
        panel.add(tableNameField);
        panel.add(new JLabel("SQL 문:"));
        panel.add(new JScrollPane(sqlField));
        panel.add(insertButton);
        
        cancelButton = new JButton("취소");
        cancelButton.addActionListener(e -> dispose());

        panel.add(cancelButton);

        add(panel);
    }

    private void insertData() {
        String sql = sqlField.getText().trim();
        String tableName = tableNameField.getText().trim();
        connection = Db1.getConnection();
        try {
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            ResultSet generatedKeys = statement.getGeneratedKeys();
            connection.commit();
            JOptionPane.showMessageDialog(this, "데이터가 입력되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "입력 오류: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}

class TableDeleteWindow extends JFrame {
    private JTextField tableNameField;
    private JTextArea conditionField;
    private JButton deleteButton;
    private JButton cancelButton;
    private Connection connection;

    public TableDeleteWindow() {
        setTitle("테이블 삭제");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        tableNameField = new JTextField(10);
        conditionField = new JTextArea(10, 30);
        deleteButton = new JButton("삭제");
        deleteButton.addActionListener(e -> deleteData());
        
        cancelButton = new JButton("취소");
        cancelButton.addActionListener(e -> dispose());

        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("테이블 이름:"));
        panel.add(tableNameField);
        panel.add(new JLabel("삭제 조건:"));
        panel.add(new JScrollPane(conditionField));
        panel.add(deleteButton);
        panel.add(cancelButton);

        add(panel);
    }

    private void deleteData() {
        String condition = conditionField.getText().trim();
        connection = Db1.getConnection();
        try {
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();
            statement.executeUpdate(condition);
            connection.commit();
            JOptionPane.showMessageDialog(this, "데이터가 삭제되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "삭제 오류: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}

class TableUpdateWindow extends JFrame {
    private JTextArea sqlField;
    private JButton updateButton;
    private JButton cancelButton;
    private Connection connection;

    public TableUpdateWindow() {
        setTitle("테이블 변경");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        sqlField = new JTextArea(30, 60);
        updateButton = new JButton("변경");
        updateButton.addActionListener(e -> updateData());
        
        cancelButton = new JButton("취소");
        cancelButton.addActionListener(e -> dispose());

        JPanel panel = new JPanel(new GridLayout(2, 6));
        panel.add(new JLabel("SQL 문:"));
        panel.add(new JScrollPane(sqlField));
        panel.add(updateButton);
        panel.add(cancelButton);

        add(panel);
    }

    private void updateData() {
        String sql = sqlField.getText().trim();
        connection = Db1.getConnection();
        try {
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
            connection.commit();
            JOptionPane.showMessageDialog(this, "데이터가 변경되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "변경 오류: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
class MainWindow extends JFrame {
    private JLabel dateTimeLabel;
    private JLabel userIdLabel;
    private JButton homeButton;
    private JButton logoutButton;
    private JButton myPageButton;
    private JPanel contentPanel;
    private String userId;
    private MovieSearch movieSearch;
    private DefaultListModel<String> movieListModel;

    public MainWindow(String userId) {
        this.userId = userId;
        movieSearch = new MovieSearch();
        setTitle("메인 페이지");
        setSize(1200, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        dateTimeLabel = new JLabel();
        userIdLabel = new JLabel(userId);
        
        JPanel topRightPanel = new JPanel(new BorderLayout());
        topRightPanel.add(dateTimeLabel, BorderLayout.EAST);
        topRightPanel.setBorder(new EmptyBorder(0,0,0,30));
      
        
        JPanel spacerPanel = new JPanel();
        spacerPanel.setPreferredSize(new Dimension(20, 0));


        homeButton = new JButton("홈");
        homeButton.addActionListener(e -> showHome());
        
        logoutButton = new JButton("로그아웃"); 
        logoutButton.addActionListener(e -> logout());
        
        myPageButton = new JButton("마이페이지");
        myPageButton.addActionListener(e -> showMyPage());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(topRightPanel, BorderLayout.EAST);
        topPanel.add(spacerPanel, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        
        buttonPanel.add(homeButton);
        buttonPanel.add(myPageButton);
        buttonPanel.add(logoutButton);
        topPanel.add(buttonPanel, BorderLayout.WEST);
        contentPanel = new JPanel(new CardLayout());

        add(topPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        updateDateTime();
        showHome(); 
    }
    private void logout() {
        LoginWindow loginWindow = new LoginWindow();
        loginWindow.setVisible(true);
        dispose(); 
    }

    private void updateDateTime() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                dateTimeLabel.setText(sdf.format(new Date()));
            }
        }, 0, 1000);
    }

    private void showHome() {
        JPanel homePanel = new HomePanel(userId);
        contentPanel.add(homePanel, "Home");
        CardLayout cl = (CardLayout) (contentPanel.getLayout());
        cl.show(contentPanel, "Home");
    }

    private void showMyPage() {
        JPanel myPagePanel = new MyPagePanel(userId);
        contentPanel.add(myPagePanel, "MyPage");
        CardLayout cl = (CardLayout) (contentPanel.getLayout());
        cl.show(contentPanel, "MyPage");
    }
}

class HomePanel extends JPanel {
 private JTextField searchField;
 private JButton searchButton;
 private JTextArea resultArea;
 private JList<String> movieList;
 private DefaultListModel<String> listModel;
 private MovieSearch movieSearch;
 private JButton bookButton;
 private String userId;

 public HomePanel(String userId) {
     this.userId = userId;
     movieSearch = new MovieSearch();
     setLayout(new BorderLayout());

     searchField = new JTextField(20);
     searchButton = new JButton("검색");
     searchButton.addActionListener(new SearchButtonListener());

     resultArea = new JTextArea(20, 50);
     resultArea.setEditable(false);

     listModel = new DefaultListModel<>();
     movieList = new JList<>(listModel);
     movieList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
     movieList.addListSelectionListener(e -> {
         if (!e.getValueIsAdjusting()) {
             String selectedMovie = movieList.getSelectedValue();
             if (selectedMovie != null) {
                 String movieDetails = movieSearch.getMovieDetails(selectedMovie);
                 resultArea.setText(movieDetails);
             }
         }
     });

     bookButton = new JButton("예매");
     bookButton.setPreferredSize(new Dimension(100, 30));
     bookButton.addActionListener(e -> {
         String selectedMovie = movieList.getSelectedValue();
         if (selectedMovie != null) {
             SeatSelectionWindow seatSelectionWindow = new SeatSelectionWindow(selectedMovie, userId);
             seatSelectionWindow.setVisible(true);
         } else {
             JOptionPane.showMessageDialog(this, "예매할 영화를 선택해주세요.");
         }
     });

     JPanel searchPanel = new JPanel();
     searchPanel.add(searchField);
     searchPanel.add(searchButton);

     JPanel leftPanel = new JPanel(new BorderLayout());
     leftPanel.add(new JScrollPane(movieList), BorderLayout.CENTER);

     JPanel centerPanel = new JPanel(new BorderLayout());
     centerPanel.add(new JScrollPane(resultArea), BorderLayout.CENTER);

     JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // FlowLayout 사용
     buttonPanel.add(bookButton);

     JPanel mainPanel = new JPanel(new BorderLayout());
     mainPanel.add(leftPanel, BorderLayout.WEST);
     mainPanel.add(centerPanel, BorderLayout.CENTER);
     mainPanel.add(buttonPanel, BorderLayout.EAST);

     add(searchPanel, BorderLayout.NORTH);
     add(mainPanel, BorderLayout.CENTER);

     showCurrentlyShowingMovies(); 
 }

 private void showCurrentlyShowingMovies() {
     List<String> currentlyShowingMovies = movieSearch.getCurrentlyShowingMovies();
     listModel.clear();
     for (String movie : currentlyShowingMovies) {
         listModel.addElement(movie);
     }
     resultArea.setText("현재 상영중인 영화 목록입니다.");
 }

 private class SearchButtonListener implements ActionListener {
     @Override
     public void actionPerformed(ActionEvent e) {
         String searchText = searchField.getText();
         String[] searchParams = searchText.split(",");

         String title = searchParams.length > 0 ? searchParams[0].trim() : "";
         String director = searchParams.length > 1 ? searchParams[1].trim() : "";
         String actors = searchParams.length > 2 ? searchParams[2].trim() : "";
         String genre = searchParams.length > 3 ? searchParams[3].trim() : "";

         List<String> results = movieSearch.searchMovies(title, director, actors, genre);

         listModel.clear();
         if (results.isEmpty()) {
             resultArea.setText("해당하는 영화가 없습니다.");
         } else {
             for (String result : results) {
                 listModel.addElement(result);
             }
         }
     }
 }
}


class MyPagePanel extends JPanel {
    private JLabel nameLabel;
    private JLabel phoneLabel;
    private JLabel emailLabel;
    private JLabel userIdLabel;
    private JButton updateButton;
    private JList<ReservationInfo> reservationList;
    private DefaultListModel<ReservationInfo> reservationListModel;
    private MovieSearch movieSearch;
    private String userId;

    public MyPagePanel(String userId) {
        this.userId = userId;
        movieSearch = new MovieSearch();
        setLayout(new BorderLayout());

        JPanel userInfoPanel = new JPanel(new GridLayout(5, 1));
        userIdLabel = new JLabel(userId);
        nameLabel = new JLabel();
        phoneLabel = new JLabel();
        emailLabel = new JLabel();

        userInfoPanel.add(new JLabel("아이디:"));
        userInfoPanel.add(userIdLabel);
        userInfoPanel.add(new JLabel("이름:"));
        userInfoPanel.add(nameLabel);
        userInfoPanel.add(new JLabel("전화번호:"));
        userInfoPanel.add(phoneLabel);
        userInfoPanel.add(new JLabel("이메일:"));
        userInfoPanel.add(emailLabel);

        updateButton = new JButton("정보 수정");
        updateButton.addActionListener(e -> openUpdateInfoWindow());

        JPanel reservationPanel = new JPanel(new BorderLayout());
        reservationListModel = new DefaultListModel<>();
        reservationList = new JList<>(reservationListModel);
        reservationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        reservationList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                ReservationInfo selectedReservation = reservationList.getSelectedValue();
                if (selectedReservation != null) {
                    ReservationDetailsWindow detailsWindow = new ReservationDetailsWindow(selectedReservation, this);
                    detailsWindow.setVisible(true);
                }
            }
        });

        reservationPanel.add(new JScrollPane(reservationList), BorderLayout.CENTER);

        add(userInfoPanel, BorderLayout.WEST);
        add(reservationPanel, BorderLayout.CENTER);
        add(updateButton, BorderLayout.SOUTH);

        loadUserInfo();
        loadUserReservations();
    }

    private void loadUserInfo() {
        User user = movieSearch.getUserInfo(userId);
        if (user == null) {
            user = new User(userId, "홍길동", "010-0001-0202", "gildong@sju.ac");
            movieSearch.addUser(user);
        }
        nameLabel.setText(user.getName());
        phoneLabel.setText(user.getPhone());
        emailLabel.setText(user.getEmail());
    }

    private void openUpdateInfoWindow() {
        User user = movieSearch.getUserInfo(userId);
        if (user != null) {
            UpdateInfoWindow updateInfoWindow = new UpdateInfoWindow(user, this);
            updateInfoWindow.setVisible(true);
        }
    }

    private void loadUserReservations() {
        List<ReservationInfo> reservations = movieSearch.getUserReservations(userId);
        reservationListModel.clear();
        for (ReservationInfo reservation : reservations) {
            reservationListModel.addElement(reservation);
        }
    }

    public void updateUserInfoPanel(User user) {
        nameLabel.setText(user.getName());
        phoneLabel.setText(user.getPhone());
        emailLabel.setText(user.getEmail());
    }

    public void refreshReservations() {
        loadUserReservations();
    }
}


class UpdateInfoWindow extends JFrame {
    private JTextField nameField;
    private JTextField phoneField;
    private JTextField emailField;
    private JButton saveButton;
    private User user;
    private MyPagePanel parentPanel;

    public UpdateInfoWindow(User user, MyPagePanel parentPanel) {
        this.user = user;
        this.parentPanel = parentPanel;

        setTitle("정보 수정");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        nameField = new JTextField(user.getName(), 15);
        phoneField = new JTextField(user.getPhone(), 15);
        emailField = new JTextField(user.getEmail(), 15);

        saveButton = new JButton("저장");
        saveButton.addActionListener(e -> saveUserInfo());

        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("이름:"));
        panel.add(nameField);
        panel.add(new JLabel("전화번호:"));
        panel.add(phoneField);
        panel.add(new JLabel("이메일:"));
        panel.add(emailField);
        panel.add(new JLabel());
        panel.add(saveButton);

        add(panel);
    }

    private void saveUserInfo() {
        String name = nameField.getText();
        String phone = phoneField.getText();
        String email = emailField.getText();

        user.setName(name);
        user.setPhone(phone);
        user.setEmail(email);

        MovieSearch movieSearch = new MovieSearch();
        movieSearch.updateUserInfo(user.getUserId(), name, phone, email);

        parentPanel.updateUserInfoPanel(user);
        JOptionPane.showMessageDialog(this, "정보가 수정되었습니다.");
        dispose();
    }
}

class UpdateReservationWindow extends JFrame {
    private String reservationId;
    private MovieSearch movieSearch;
    private JComboBox<String> movieComboBox;
    private JButton selectSeatButton;
    private String selectedMovie;

    public UpdateReservationWindow(String reservationId) {
        this.reservationId = reservationId;
        movieSearch = new MovieSearch();
        setTitle("예매 변경");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        movieComboBox = new JComboBox<>(movieSearch.getAllMovies().toArray(new String[0]));
        selectSeatButton = new JButton("좌석 선택");
        selectSeatButton.addActionListener(e -> openSeatSelectionWindow());

        JPanel panel = new JPanel(new GridLayout(3, 1));
        panel.add(new JLabel("영화 선택:"));
        panel.add(movieComboBox);
        panel.add(selectSeatButton);

        add(panel);
    }

    private void openSeatSelectionWindow() {
        selectedMovie = (String) movieComboBox.getSelectedItem();
        if (selectedMovie != null) {
            SeatSelectionWindow seatSelectionWindow = new SeatSelectionWindow(selectedMovie, reservationId);
            seatSelectionWindow.setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "변경할 영화를 선택해주세요.");
        }
    }
}

class SeatSelectionWindow extends JFrame {
    private JButton[][] seatButtons;
    private JButton nextButton;
    private String movieTitle;
    private String userId;
    private MovieSearch movieSearch;
    private JSpinner dateSpinner;
    private JTextArea showTimesArea;
    private List<String> selectedSeats = new ArrayList<>();
    private List<String> reservedSeats = new ArrayList<>();
    private boolean isUpdate;
    private String reservationId;

    public SeatSelectionWindow(String movieTitle, String userId) {
        this(movieTitle, userId, false, null);
    }

    public SeatSelectionWindow(String movieTitle, String userId, boolean isUpdate, String reservationId) {
        this.movieTitle = movieTitle;
        this.userId = userId;
        this.isUpdate = isUpdate;
        this.reservationId = reservationId;
        this.movieSearch = new MovieSearch();
        setTitle("좌석 선택");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        int[] rowsAndCols = movieSearch.getTheaterRowsAndCols(movieTitle);
        int rows = rowsAndCols[0];
        int cols = rowsAndCols[1];

        seatButtons = new JButton[rows][cols];
        JPanel seatPanel = new JPanel(new GridLayout(rows, cols));
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                seatButtons[i][j] = new JButton((i + 1) + "-" + (j + 1));
                seatButtons[i][j].setBackground(Color.GREEN);
                seatButtons[i][j].addActionListener(e -> selectSeat((JButton) e.getSource()));
                seatPanel.add(seatButtons[i][j]);
            }
        }

        SpinnerDateModel dateModel = new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH);
        dateSpinner = new JSpinner(dateModel);
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));
        dateSpinner.addChangeListener(e -> loadShowTimes());

        showTimesArea = new JTextArea(10, 20);
        showTimesArea.setEditable(false);

        nextButton = new JButton("다음");
        nextButton.addActionListener(e -> {
            if (isUpdate) {
                updateBooking();
            } else {
                proceedToBooking();
            }
        });

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(seatPanel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(dateSpinner, BorderLayout.NORTH);
        rightPanel.add(new JScrollPane(showTimesArea), BorderLayout.CENTER);

        add(leftPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
        add(nextButton, BorderLayout.SOUTH);

        loadShowTimes();
    }

    private void selectSeat(JButton button) {
        if (button.getBackground() == Color.RED) {
            JOptionPane.showMessageDialog(this, "이미 예약된 좌석입니다. 다른 좌석을 선택해주세요.");
        } else if (button.getBackground() == Color.GREEN) {
            button.setBackground(Color.YELLOW);
            selectedSeats.add(button.getText());
        } else if (button.getBackground() == Color.YELLOW) {
            button.setBackground(Color.GREEN);
            selectedSeats.remove(button.getText());
        }
    }

    private void loadShowTimes() {
        Date selectedDate = (Date) dateSpinner.getValue();
        if (selectedDate == null) {
            return;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(selectedDate);
        String dayOfWeek = getDayOfWeekString(calendar.get(Calendar.DAY_OF_WEEK));

        List<String> showTimes = movieSearch.getShowTimes(movieTitle, dayOfWeek);
        showTimesArea.setText("");

        if (showTimes.isEmpty()) {
            showTimesArea.setText("선택한 날짜에 상영이 없습니다.");
        } else {
            for (String showTime : showTimes) {
                showTimesArea.append(showTime + "\n");
            }
        }

        loadReservedSeats(selectedDate);
    }

    private void loadReservedSeats(Date selectedDate) {
        reservedSeats = movieSearch.getReservedSeats(movieTitle, selectedDate);
        for (String seat : reservedSeats) {
            String[] parts = seat.split("-");
            int row = Integer.parseInt(parts[0]) - 1;
            int col = Integer.parseInt(parts[1]) - 1;
            seatButtons[row][col].setBackground(Color.RED);
            seatButtons[row][col].setEnabled(false);
        }
    }

    private String getDayOfWeekString(int dayOfWeek) {
        switch (dayOfWeek) {
            case Calendar.MONDAY:
                return "월요일";
            case Calendar.TUESDAY:
                return "화요일";
            case Calendar.WEDNESDAY:
                return "수요일";
            case Calendar.THURSDAY:
                return "목요일";
            case Calendar.FRIDAY:
                return "금요일";
            case Calendar.SATURDAY:
                return "토요일";
            case Calendar.SUNDAY:
                return "일요일";
            default:
                return "";
        }
    }

    private void proceedToBooking() {
        Date selectedDate = (Date) dateSpinner.getValue();
        if (selectedDate == null || selectedDate.before(new Date())) {
            JOptionPane.showMessageDialog(this, "유효한 날짜를 선택해주세요.");
            return;
        }

        if (selectedSeats.isEmpty()) {
            JOptionPane.showMessageDialog(this, "예약할 좌석을 선택해주세요.");
            return;
        }

        BookingWindow bookingWindow = new BookingWindow(selectedDate, selectedSeats, movieTitle, userId);
        bookingWindow.setVisible(true);
        dispose();
    }

    private void updateBooking() {
        Date selectedDate = (Date) dateSpinner.getValue();
        if (selectedDate == null || selectedDate.before(new Date())) {
            JOptionPane.showMessageDialog(this, "유효한 날짜를 선택해주세요.");
            return;
        }

        if (selectedSeats.isEmpty()) {
            JOptionPane.showMessageDialog(this, "예약할 좌석을 선택해주세요.");
            return;
        }

        movieSearch.updateReservation(reservationId, movieTitle, selectedSeats, selectedDate);
        JOptionPane.showMessageDialog(this, "예매가 변경되었습니다.");
        dispose();
    }
}

class BookingWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	private JComboBox<String> paymentMethodComboBox;
    private JLabel amountLabel;
    private JButton payButton;
    private Date selectedDate;
    private List<String> selectedSeats;
    private String movieTitle;
    private String userId;
    private MovieSearch movieSearch;

    public BookingWindow(Date selectedDate, List<String> selectedSeats, String movieTitle, String userId) {
        this.selectedDate = selectedDate;
        this.selectedSeats = selectedSeats;
        this.movieTitle = movieTitle;
        this.userId = userId;
        this.movieSearch = new MovieSearch();
        
        setTitle("결제");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        String[] paymentMethods = {"휴대폰결제", "신용카드", "현금"};
        paymentMethodComboBox = new JComboBox<>(paymentMethods);

        amountLabel = new JLabel("결제 금액: 15000원");

        payButton = new JButton("결제");
        payButton.addActionListener(e -> completeBooking());

        JPanel panel = new JPanel(new GridLayout(4, 1));
        panel.add(new JLabel("결제 수단:"));
        panel.add(paymentMethodComboBox);
        panel.add(amountLabel);
        panel.add(payButton);

        add(panel);
    }

    private void completeBooking() {
        String paymentMethod = (String) paymentMethodComboBox.getSelectedItem();
        String status = "True";
        float amount = 1.0f;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String payDate = sdf.format(selectedDate);

        int reservationId = movieSearch.saveReservation(userId, paymentMethod, status, amount, payDate);
        if (reservationId > 0) {
            for (String seat : selectedSeats) {
                movieSearch.saveTicket(movieTitle, seat, reservationId);
            }
            JOptionPane.showMessageDialog(this, "결제가 완료되었습니다.");
            dispose();
            JOptionPane.showMessageDialog(null, "예매가 완료되었습니다.");
        } else {
            JOptionPane.showMessageDialog(this, "결제에 실패했습니다. 다시 시도해주세요.");
        }
    }
}

class MovieSearch {
    public List<String> getCurrentlyShowingMovies() {
        List<String> movies = new ArrayList<>();
        Connection connection = Db1.getConnection();
        try {
            String query = "SELECT title FROM movie";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                movies.add(resultSet.getString("title"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return movies;
    }
    public List<String> getAllMovies() {
        List<String> movies = new ArrayList<>();
        Connection connection = Db1.getConnection();
        try {
            String query = "SELECT title FROM movie";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                movies.add(resultSet.getString("title"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return movies;
    }
    

    public List<String> searchMovies(String title, String director, String actor, String genre) {
        List<String> movies = new ArrayList<>();
        Connection connection = Db1.getConnection();

        StringBuilder query = new StringBuilder("SELECT title FROM movie WHERE 1=1");
        List<String> parameters = new ArrayList<>();

        if (title != null && !title.isEmpty()) {
            query.append(" AND title LIKE ?");
            parameters.add("%" + title + "%");
        }
        if (director != null && !director.isEmpty()) {
            query.append(" AND director LIKE ?");
            parameters.add("%" + director + "%");
        }
        if (actor != null && !actor.isEmpty()) {
            query.append(" AND actor LIKE ?");
            parameters.add("%" + actor + "%");
        }
        if (genre != null && !genre.isEmpty()) {
            query.append(" AND genre LIKE ?");
            parameters.add("%" + genre + "%");
        }

        try {
            PreparedStatement statement = connection.prepareStatement(query.toString());
            for (int i = 0; i < parameters.size(); i++) {
                statement.setString(i + 1, parameters.get(i));
            }
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                movies.add(resultSet.getString("title"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return movies;
    }

    public String getMovieDetails(String title) {
        Connection connection = Db1.getConnection();
        StringBuilder movieDetails = new StringBuilder();

        try {
            String query = "SELECT * FROM movie WHERE title = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, title);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                movieDetails.append("제목: ").append(resultSet.getString("title")).append("\n")
                            .append("감독: ").append(resultSet.getString("director")).append("\n")
                            .append("배우: ").append(resultSet.getString("actor")).append("\n")
                            .append("장르: ").append(resultSet.getString("genre")).append("\n")
                            .append("상영시간: ").append(resultSet.getString("duration")).append("\n")
                            .append("개봉일: ").append(resultSet.getDate("release_Date")).append("\n")
                            .append("평점: ").append(resultSet.getFloat("avg_score")).append("\n")
                            .append("줄거리: ").append(resultSet.getString("explain"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return movieDetails.toString();
    }

    public int[] getTheaterRowsAndCols(String movieTitle) {
        Connection connection = Db1.getConnection();
        int[] rowsAndCols = new int[2];

        try {
            String query = "SELECT t.rows, t.cols FROM theaters t JOIN screen s ON t.theater_id = s.theater_id JOIN movie m ON s.movie_id = m.movie_id WHERE m.title = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, movieTitle);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                rowsAndCols[0] = resultSet.getInt("rows");
                rowsAndCols[1] = resultSet.getInt("cols");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowsAndCols;
    }

    public User getUserInfo(String userId) {
        Connection connection = Db1.getConnection();
        User user = null;

        try {
            String query = "SELECT * FROM member WHERE member_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, userId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                user = new User(
                    resultSet.getString("member_id"),
                    resultSet.getString("name"),
                    resultSet.getString("phone"),
                    resultSet.getString("email")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    public void addUser(User user) {
        Connection connection = Db1.getConnection();

        try {
            String query = "INSERT INTO member (member_id, name, phone, email, password) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, user.getUserId());
            statement.setString(2, user.getName());
            statement.setString(3, user.getPhone());
            statement.setString(4, user.getEmail());
            statement.setString(5, user.getPassword());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void updateUserInfo(String userId, String name, String phone, String email) {
        Connection connection = Db1.getConnection();

        try {
            String query = "UPDATE member SET name = ?, phone = ?, email = ? WHERE member_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, name);
            statement.setString(2, phone);
            statement.setString(3, email);
            statement.setString(4, userId);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void updateTicket(String movieTitle, String seat, int reservationId) {
        Connection connection = Db1.getConnection();
        try {
            String query = "UPDATE ticket SET screen_id = (SELECT s.screen_id FROM screen s JOIN movie m ON s.movie_id = m.movie_id WHERE m.title = ? AND s.day_of_weeks = ? LIMIT 1), " +
                           "theater_id = (SELECT t.theater_id FROM theaters t JOIN screen s ON t.theater_id = s.theater_id JOIN movie m ON s.movie_id = m.movie_id WHERE m.title = ? LIMIT 1), " +
                           "seat_id = (SELECT st.seat_id FROM seats st JOIN theaters t ON st.theater_id = t.theater_id JOIN screen s ON t.theater_id = s.theater_id JOIN movie m ON s.movie_id = m.movie_id WHERE m.title = ? AND st.row = ? AND st.col = ? LIMIT 1) " +
                           "WHERE reservation_id = ?";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, movieTitle);
            String[] seatPosition = seat.split("-");
            statement.setInt(2, Integer.parseInt(seatPosition[0]));
            statement.setInt(3, Integer.parseInt(seatPosition[1]));
            statement.setInt(4, reservationId);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public List<String> getTicketsForReservation(int reservationId) {
        List<String> tickets = new ArrayList<>();
        Connection connection = Db1.getConnection();
        try {
            String query = "SELECT CONCAT('좌석: ', st.row, '-', st.col, ', 가격: ', t.standard_price) AS ticket_info " +
                           "FROM ticket t " +
                           "JOIN seats st ON t.seat_id = st.seat_id " +
                           "WHERE t.reservation_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, reservationId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                tickets.add(resultSet.getString("ticket_info"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tickets;
    }
    public String getPaymentMethodForReservation(int reservationId) {
        String paymentMethod = "";
        Connection connection = Db1.getConnection();
        try {
            String query = "SELECT method FROM reservation WHERE reservation_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, reservationId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                paymentMethod = resultSet.getString("method");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return paymentMethod;
    }
    public List<String> getReservedSeats(String movieTitle, Date selectedDate) {
        List<String> reservedSeats = new ArrayList<>();
        Connection connection = Db1.getConnection();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = sdf.format(selectedDate);
            
            String query = "SELECT CONCAT(st.row, '-', st.col) AS seat FROM ticket t " +
                           "JOIN screen s ON t.screen_id = s.screen_id " +
                           "JOIN movie m ON s.movie_id = m.movie_id " +
                           "JOIN seats st ON t.seat_id = st.seat_id " +
                           "WHERE m.title = ? AND s.start = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, movieTitle);
            statement.setString(2, formattedDate);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                reservedSeats.add(resultSet.getString("seat"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reservedSeats;
    }
    public List<ReservationInfo> getUserReservations(String userId) {
        List<ReservationInfo> reservations = new ArrayList<>();
        Connection connection = Db1.getConnection();

        try {
            String query = "SELECT r.reservation_id, m.title, s.screen_id, s.start, s.day_of_weeks, m.duration, t.theater_id, st.row, st.col, t.sell_price, mem.name, t.ticket_id " +
                           "FROM reservation r " +
                           "JOIN ticket t ON r.reservation_id = t.reservation_id " +
                           "JOIN screen s ON t.screen_id = s.screen_id " +
                           "JOIN movie m ON s.movie_id = m.movie_id " +
                           "JOIN member mem ON r.member_id = mem.member_id " +
                           "JOIN seats st ON t.seat_id = st.seat_id " +
                           "WHERE r.member_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, userId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                reservations.add(new ReservationInfo(
                        resultSet.getInt("r.reservation_id"),
                        resultSet.getString("m.title"),
                        resultSet.getInt("s.screen_id"),
                        resultSet.getDate("s.start"),
                        resultSet.getString("s.day_of_weeks"),
                        resultSet.getString("m.duration"),
                        resultSet.getInt("t.theater_id"),
                        resultSet.getInt("st.row"),
                        resultSet.getInt("st.col"),
                        resultSet.getDouble("t.sell_price"),
                        resultSet.getString("mem.name"),
                        resultSet.getInt("t.ticket_id")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reservations;
    }


    private String getDayOfWeekString(int dayOfWeek) {
        switch (dayOfWeek) {
            case Calendar.MONDAY:
                return "월요일";
            case Calendar.TUESDAY:
                return "화요일";
            case Calendar.WEDNESDAY:
                return "수요일";
            case Calendar.THURSDAY:
                return "목요일";
            case Calendar.FRIDAY:
                return "금요일";
            case Calendar.SATURDAY:
                return "토요일";
            case Calendar.SUNDAY:
                return "일요일";
            default:
                return "";
        }
    }
    public void updateReservationWithNewDetails(int reservationId, String newMovieTitle, Date newDate, List<String> newSeats) throws Exception {
        Connection connection = Db1.getConnection();
        try {
            connection.setAutoCommit(false); 

            String deleteTicketsQuery = "DELETE FROM ticket WHERE reservation_id = ?";
            PreparedStatement deleteTicketsStatement = connection.prepareStatement(deleteTicketsQuery);
            deleteTicketsStatement.setInt(1, reservationId);
            deleteTicketsStatement.executeUpdate();

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(newDate);
            String dayOfWeek = getDayOfWeekString(calendar.get(Calendar.DAY_OF_WEEK));
            
            String queryScreenId = "SELECT screen_id FROM screen WHERE movie_id = (SELECT movie_id FROM movie WHERE title = ?) AND day_of_weeks = ?";
            PreparedStatement screenStatement = connection.prepareStatement(queryScreenId);
            screenStatement.setString(1, newMovieTitle);
            screenStatement.setString(2, dayOfWeek);
            ResultSet resultSet = screenStatement.executeQuery();

            int newScreenId = -1;
            if (resultSet.next()) {
                newScreenId = resultSet.getInt("screen_id");
            } else {
                throw new Exception("해당 날짜에 상영일정이 없습니다.");
            }

            for (String seat : newSeats) {
                String query = "INSERT INTO ticket (screen_id, theater_id, seat_id, reservation_id, TF, standard_price) " +
                               "SELECT ?, s.theater_id, st.seat_id, ?, 0, 15000 FROM screen s " +
                               "JOIN movie m ON s.movie_id = m.movie_id " +
                               "JOIN seats st ON s.theater_id = st.theater_id " +
                               "WHERE m.title = ? AND st.row = ? AND st.col = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setInt(1, newScreenId);
                statement.setInt(2, reservationId);
                statement.setString(3, newMovieTitle);
                String[] seatPosition = seat.split("-");
                statement.setInt(4, Integer.parseInt(seatPosition[0]));
                statement.setInt(5, Integer.parseInt(seatPosition[1]));
                statement.executeUpdate();
            }

            connection.commit(); 
        } catch (Exception e) {
            connection.rollback(); 
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public void deleteReservation(String reservationId) {
        Connection connection = Db1.getConnection();
        try {
            String deleteTicketsQuery = "DELETE FROM ticket WHERE reservation_id = ?";
            PreparedStatement deleteTicketsStatement = connection.prepareStatement(deleteTicketsQuery);
            deleteTicketsStatement.setString(1, reservationId);
            deleteTicketsStatement.executeUpdate();

            String deleteReservationQuery = "DELETE FROM reservation WHERE reservation_id = ?";
            PreparedStatement deleteReservationStatement = connection.prepareStatement(deleteReservationQuery);
            deleteReservationStatement.setString(1, reservationId);
            deleteReservationStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void updateReservation(String reservationId, String movieTitle, List<String> selectedSeats, Date selectedDate) {
        Connection connection = Db1.getConnection();
        try {
            String deleteTicketsQuery = "DELETE FROM ticket WHERE reservation_id = ?";
            PreparedStatement deleteTicketsStatement = connection.prepareStatement(deleteTicketsQuery);
            deleteTicketsStatement.setString(1, reservationId);
            deleteTicketsStatement.executeUpdate();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = sdf.format(selectedDate);

            for (String seat : selectedSeats) {
                saveTicket(movieTitle, seat, Integer.parseInt(reservationId), formattedDate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void saveTicket(String movieTitle, String seat, int reservationId) {
        saveTicket(movieTitle, seat, reservationId, null);
    }
    public List<String> getShowTimes(String movieTitle, String dayOfWeek) {
        List<String> showTimes = new ArrayList<>();
        Connection connection = Db1.getConnection();
        try {
            String query = "SELECT show_start FROM screen s JOIN movie m ON s.movie_id = m.movie_id WHERE m.title = ? AND s.day_of_weeks = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, movieTitle);
            statement.setString(2, dayOfWeek);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                showTimes.add(resultSet.getString("show_start"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return showTimes;
    }

    public int saveReservation(String memberId, String method, String status, float amount, String payDate) {
        Connection connection = Db1.getConnection();
        int reservationId = -1;
        try {
            String query = "INSERT INTO reservation (member_id, method, status, amount, pay_date) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, memberId);
            statement.setString(2, method);
            statement.setString(3, status);
            statement.setFloat(4, amount);
            statement.setString(5, payDate);
            statement.executeUpdate();

            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) {
                reservationId = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservationId;
    }

    public void saveTicket(String movieTitle, String seat, int reservationId, String date) {
        Connection connection = Db1.getConnection();
        try {
            String query = "INSERT INTO ticket (screen_id, theater_id, seat_id, reservation_id, TF, standard_price) " +
                           "SELECT s.screen_id, s.theater_id, st.seat_id, ?, 0, 15000 FROM screen s " +
                           "JOIN movie m ON s.movie_id = m.movie_id " +
                           "JOIN seats st ON s.theater_id = st.theater_id " +
                           "WHERE m.title = ? AND st.row = ? AND st.col = ?" +
                           (date != null ? " AND s.start = ?" : "");
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, reservationId);
            statement.setString(2, movieTitle);
            String[] seatPosition = seat.split("-");
            statement.setInt(3, Integer.parseInt(seatPosition[0]));
            statement.setInt(4, Integer.parseInt(seatPosition[1]));
            if (date != null) {
                statement.setString(5, date);
            }
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class User {
    private String userId;
    private String name;
    private String phone;
    private String email;
    private String password;

    public User(String userId, String name, String phone, String email, String password) {
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.password = password;
    }

    public User(String userId, String name, String phone, String email) {
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
class ReservationInfo {
    private int reservationId;
    private String movieTitle;
    private int screenId;
    private Date screeningDate;
    private String dayOfWeek;
    private Date showStartTime;
    private String duration;
    private int theaterId;
    private int ticketId;
    private int row;
    private int col;
    private double sellPrice;
    private String memberName;

    public ReservationInfo(int reservationId, String movieTitle, int screenId, Date screeningDate, String dayOfWeek, String duration, int theaterId, int row, int col, double sellPrice, String memberName, int ticketId) {
        this.reservationId = reservationId;
        this.movieTitle = movieTitle;
        this.screenId = screenId;
        this.screeningDate = screeningDate;
        this.dayOfWeek = dayOfWeek;
        this.theaterId = theaterId;
        this.row = row;
        this.col = col;
        this.sellPrice = sellPrice;
        this.duration = duration;
        this.memberName = memberName;
        this.ticketId = ticketId;
    }

    public int getReservationId() {
        return reservationId;
    }
    public String getDuration() {
        return duration;
    }

    public String getMovieTitle() {
        return movieTitle;
    }
    
    public int getTicketId() {
        return ticketId;
    }

    public int getScreenId() {
        return screenId;
    }

    public Date getScreeningDate() {
        return screeningDate;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public Date getShowStartTime() {
        return showStartTime;
    }

    public int getTheaterId() {
        return theaterId;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public String getMemberName() {
        return memberName;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return String.format("영화: %s, 개봉일: %s, 상영 시간: %s분, 상영관: %d, 좌석: %d-%d, 가격: %.0f원, 티켓 ID: %d",
                movieTitle, sdf.format(screeningDate), duration, theaterId, row, col, sellPrice, ticketId);
    }
}


class ChangeReservationWindow extends JFrame {
    private JButton[][] seatButtons;
    private JButton changeButton;
    private String movieTitle;
    private String userId;
    private int reservationId;
    private MovieSearch movieSearch;
    private JSpinner dateSpinner;
    private JTextArea showTimesArea;
    private List<String> selectedSeats = new ArrayList<>();

    public ChangeReservationWindow(String movieTitle, String userId, int reservationId) {
        this.movieTitle = movieTitle;
        this.userId = userId;
        this.reservationId = reservationId;
        movieSearch = new MovieSearch();
        setTitle("좌석 선택");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        int[] rowsAndCols = movieSearch.getTheaterRowsAndCols(movieTitle);
        int rows = rowsAndCols[0];
        int cols = rowsAndCols[1];

        seatButtons = new JButton[rows][cols];
        JPanel seatPanel = new JPanel(new GridLayout(rows, cols));
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                seatButtons[i][j] = new JButton((i + 1) + "-" + (j + 1));
                seatButtons[i][j].setBackground(Color.GREEN);
                seatButtons[i][j].addActionListener(e -> selectSeat((JButton) e.getSource()));
                seatPanel.add(seatButtons[i][j]);
            }
        }

        SpinnerDateModel dateModel = new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH);
        dateSpinner = new JSpinner(dateModel);
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));
        dateSpinner.addChangeListener(e -> loadShowTimes());

        showTimesArea = new JTextArea(10, 20);
        showTimesArea.setEditable(false);

        changeButton = new JButton("변경");
        changeButton.addActionListener(e -> changeReservation());

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(seatPanel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(dateSpinner, BorderLayout.NORTH);
        rightPanel.add(new JScrollPane(showTimesArea), BorderLayout.CENTER);

        add(leftPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
        add(changeButton, BorderLayout.SOUTH);
    }

    private void selectSeat(JButton button) {
        if (button.getBackground() == Color.GREEN) {
            button.setBackground(Color.RED);
            selectedSeats.add(button.getText());
        } else if (button.getBackground() == Color.RED) {
            button.setBackground(Color.GREEN);
            selectedSeats.remove(button.getText());
        }
    }

    private void loadShowTimes() {
        Date selectedDate = (Date) dateSpinner.getValue();
        if (selectedDate == null) {
            return;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(selectedDate);
        String dayOfWeek = getDayOfWeekString(calendar.get(Calendar.DAY_OF_WEEK));

        List<String> showTimes = movieSearch.getShowTimes(movieTitle, dayOfWeek);
        showTimesArea.setText("");

        if (showTimes.isEmpty()) {
            showTimesArea.setText("선택한 날짜에 상영이 없습니다.");
        } else {
            for (String showTime : showTimes) {
                showTimesArea.append(showTime + "\n");
            }
        }
    }

    private String getDayOfWeekString(int dayOfWeek) {
        switch (dayOfWeek) {
            case Calendar.MONDAY:
                return "월요일";
            case Calendar.TUESDAY:
                return "화요일";
            case Calendar.WEDNESDAY:
                return "수요일";
            case Calendar.THURSDAY:
                return "목요일";
            case Calendar.FRIDAY:
                return "금요일";
            case Calendar.SATURDAY:
                return "토요일";
            case Calendar.SUNDAY:
                return "일요일";
            default:
                return "";
        }
    }



    private void changeReservation() {
        Date selectedDate = (Date) dateSpinner.getValue();
        if (selectedDate == null || selectedDate.before(new Date())) {
            JOptionPane.showMessageDialog(this, "유효한 날짜를 선택해주세요.");
            return;
        }

        if (selectedSeats.isEmpty()) {
            JOptionPane.showMessageDialog(this, "예약할 좌석을 선택해주세요.");
            return;
        }

        try {
            movieSearch.updateReservationWithNewDetails(reservationId, movieTitle, selectedDate, selectedSeats);
            JOptionPane.showMessageDialog(this, "예매가 변경되었습니다.");
            dispose();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "예매 변경 실패: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
}



class MovieSelectionWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	private JList<String> movieList;
    private DefaultListModel<String> listModel;
    private MovieSearch movieSearch;
    private String userId;
    private int reservationId;

    public MovieSelectionWindow(String userId, int reservationId) {
        this.userId = userId;
        this.reservationId = reservationId;
        movieSearch = new MovieSearch();
        
        setTitle("영화 선택");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        listModel = new DefaultListModel<>();
        movieList = new JList<>(listModel);
        movieList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        List<String> movies = movieSearch.getCurrentlyShowingMovies();
        for (String movie : movies) {
            listModel.addElement(movie);
        }

        JButton selectButton = new JButton("선택");
        selectButton.addActionListener(e -> selectMovie());

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(movieList), BorderLayout.CENTER);
        panel.add(selectButton, BorderLayout.SOUTH);

        add(panel);
    }

    private void selectMovie() {
        String selectedMovie = movieList.getSelectedValue();
        if (selectedMovie != null) {
            ChangeReservationWindow changeReservationWindow = new ChangeReservationWindow(selectedMovie, userId, reservationId);
            changeReservationWindow.setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "영화를 선택해주세요.");
        }
    }
}
class ReservationDetailsWindow extends JFrame {
    private JLabel movieTitleLabel;
    private JLabel theaterLabel;
    private JLabel dayOfWeekLabel;
    private JPanel ticketInfoPanel;
    private JLabel paymentMethodLabel;
    private JLabel showStartTimeLabel;
    private JLabel ticketIdLabel;
    private JButton updateReservationButton;
    private JButton deleteReservationButton;
    private int reservationId;
    private String userId;
    private MyPagePanel parentPanel;

    public ReservationDetailsWindow(ReservationInfo reservation, MyPagePanel parentPanel) {
        this.reservationId = reservation.getReservationId();
        this.userId = reservation.getMemberName(); // Assuming member name is userId
        this.parentPanel = parentPanel;
        setTitle("예매 상세 정보");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        showStartTimeLabel = new JLabel("상영 시간: " + reservation.getDuration());
        ticketIdLabel = new JLabel("티켓 ID: " + reservation.getTicketId());

        movieTitleLabel = new JLabel("영화: " + reservation.getMovieTitle());
        theaterLabel = new JLabel("상영관: " + reservation.getTheaterId());
        dayOfWeekLabel = new JLabel("상영 요일: 매주 " + reservation.getDayOfWeek()); // 상영 요일 표시

        ticketInfoPanel = new JPanel();
        ticketInfoPanel.setLayout(new BoxLayout(ticketInfoPanel, BoxLayout.Y_AXIS));

        paymentMethodLabel = new JLabel();

        loadTicketInfo(reservation.getReservationId());

        updateReservationButton = new JButton("예매 변경");
        updateReservationButton.addActionListener(e -> updateReservation());

        deleteReservationButton = new JButton("예매 삭제");
        deleteReservationButton.addActionListener(e -> deleteReservation());

        JPanel panel = new JPanel(new GridLayout(6, 2));
        panel.add(movieTitleLabel);
        panel.add(theaterLabel);
        panel.add(dayOfWeekLabel);
        panel.add(new JScrollPane(ticketInfoPanel));
        panel.add(paymentMethodLabel);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 4));
        buttonPanel.add(updateReservationButton);
        buttonPanel.add(deleteReservationButton);

        panel.add(buttonPanel);

        add(panel);
    }

    private void loadTicketInfo(int reservationId) {
        MovieSearch movieSearch = new MovieSearch();
        List<String> tickets = movieSearch.getTicketsForReservation(reservationId);
        String paymentMethod = movieSearch.getPaymentMethodForReservation(reservationId);
        ticketInfoPanel.removeAll();
        for (String ticket : tickets) {
            JLabel ticketLabel = new JLabel(ticket);
            ticketInfoPanel.add(ticketLabel);
        }
        ticketInfoPanel.revalidate();
        ticketInfoPanel.repaint();

        paymentMethodLabel.setText("결제 수단: " + paymentMethod);
    }

    private void updateReservation() {
        MovieSelectionWindow movieSelectionWindow = new MovieSelectionWindow(userId, reservationId);
        movieSelectionWindow.setVisible(true);
        dispose();
    }

    private void deleteReservation() {
        MovieSearch movieSearch = new MovieSearch();
        movieSearch.deleteReservation(String.valueOf(reservationId));
        JOptionPane.showMessageDialog(this, "예매가 삭제되었습니다.");
        parentPanel.refreshReservations();
        dispose();
    }
}

