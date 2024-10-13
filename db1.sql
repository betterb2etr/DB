CREATE DATABASE IF NOT EXISTS db1;

USE db1;

CREATE TABLE IF NOT EXISTS db1.theaters (
    theater_id INT NOT NULL AUTO_INCREMENT COMMENT '상영관 아이디',
    seats INT NOT NULL COMMENT '좌석 수',
    activation TINYINT NOT NULL COMMENT '상영관 사용 여부',
    `rows` INT NOT NULL COMMENT '가로',
    `cols` INT NOT NULL COMMENT '세로',
    PRIMARY KEY (theater_id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS db1.movie (
    movie_id INT NOT NULL AUTO_INCREMENT COMMENT '영화 ID',
    title VARCHAR(255) NOT NULL COMMENT '영화제목',
    duration TIME NOT NULL COMMENT '상영시간',
    rate VARCHAR(10) NOT NULL COMMENT '상영 등급',
    director VARCHAR(255) NOT NULL COMMENT '감독명',
    actor VARCHAR(255) NOT NULL COMMENT '배우명',
    genre VARCHAR(50) NOT NULL COMMENT '장르',
    `explain` TEXT NOT NULL COMMENT '영화 설명',
    release_Date DATE NOT NULL COMMENT '개봉일',
    avg_score DECIMAL(3,2) NOT NULL COMMENT '평균 평점',
    PRIMARY KEY (movie_id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS db1.member (
    member_id VARCHAR(50) NOT NULL COMMENT '회원아이디',
    name VARCHAR(255) NOT NULL COMMENT '이름',
    phone VARCHAR(20) NOT NULL COMMENT '전화번호',
    email VARCHAR(255) NOT NULL COMMENT '이메일',
    PRIMARY KEY (member_id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS db1.seats (
    seat_id INT NOT NULL AUTO_INCREMENT COMMENT '좌석 아이디',
    theater_id INT NOT NULL COMMENT '상영관 아이디',
    `row` INT NOT NULL COMMENT '가로',
    `col` INT NOT NULL COMMENT '세로',
    active TINYINT NOT NULL COMMENT '좌석 사용 여부',
    PRIMARY KEY (seat_id),
    INDEX theater_id_idx (theater_id ASC),
    CONSTRAINT fk_seat_theater FOREIGN KEY (theater_id) REFERENCES db1.theaters (theater_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS db1.reservation (
    reservation_id INT NOT NULL AUTO_INCREMENT COMMENT '예매 아이디',
    method VARCHAR(50) NOT NULL COMMENT '결제 수단',
    status VARCHAR(50) NOT NULL COMMENT '결제 상태',
    amount DECIMAL(10,2) NOT NULL COMMENT '결제 금액',
    member_id VARCHAR(50) NOT NULL COMMENT '회원 아이디',
    pay_date DATE NOT NULL COMMENT '결제 날짜',
    PRIMARY KEY (reservation_id),
    INDEX member_id_idx (member_id ASC),
    CONSTRAINT fk_reservation_member FOREIGN KEY (member_id) REFERENCES db1.member (member_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS db1.screen (
    screen_id INT NOT NULL AUTO_INCREMENT COMMENT '상영 일정 아이디',
    movie_id INT NOT NULL COMMENT '영화 아이디',
    theater_id INT NOT NULL COMMENT '상영관 아이디',
    `start` DATE NOT NULL COMMENT '상영 시작일',
    day_of_weeks VARCHAR(10) NOT NULL COMMENT '상영요일',
    show_start TIME NOT NULL COMMENT '상영 시작 시간',
    show_time INT NOT NULL COMMENT '상영 회차',
    PRIMARY KEY (screen_id),
    INDEX movie_id_idx (movie_id ASC),
    INDEX theater_id_idx (theater_id ASC),
    CONSTRAINT fk_screen_movie FOREIGN KEY (movie_id) REFERENCES db1.movie (movie_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_screen_theater FOREIGN KEY (theater_id) REFERENCES db1.theaters (theater_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS db1.ticket (
    ticket_id INT NOT NULL AUTO_INCREMENT COMMENT '티켓 ID',
    screen_id INT NOT NULL COMMENT '상영 일정 ID',
    theater_id INT NOT NULL COMMENT '상영관 ID',
    seat_id INT NOT NULL COMMENT '좌석 ID',
    reservation_id INT NULL COMMENT '예매 ID',
    TF TINYINT NOT NULL COMMENT '티켓 발권 여부',
    standard_price DECIMAL(10,2) NOT NULL COMMENT '티켓 표준 가격',
    sell_price DECIMAL(10,2) NULL COMMENT '티켓 판매 가격',
    PRIMARY KEY (ticket_id),
    INDEX screen_id_idx (screen_id ASC),
    INDEX theater_id_idx (theater_id ASC),
    INDEX seat_id_idx (seat_id ASC),
    INDEX reservation_id_idx (reservation_id ASC),
    CONSTRAINT fk_ticket_screen FOREIGN KEY (screen_id) REFERENCES db1.screen (screen_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_ticket_theater FOREIGN KEY (theater_id) REFERENCES db1.theaters (theater_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_ticket_seat FOREIGN KEY (seat_id) REFERENCES db1.seats (seat_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_ticket_reservation FOREIGN KEY (reservation_id) REFERENCES db1.reservation (reservation_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS db1.review (
    review_id INT NOT NULL AUTO_INCREMENT COMMENT '리뷰 아이디',
    movie_id INT NOT NULL COMMENT '영화 아이디',
    member_id VARCHAR(50) NOT NULL COMMENT '회원아이디',
    score DECIMAL(3,2) NOT NULL COMMENT '평점',
    review_date DATE NOT NULL COMMENT '리뷰 날짜',
    PRIMARY KEY (review_id),
    INDEX movie_id_idx (movie_id ASC),
    INDEX member_id_idx (member_id ASC),
    CONSTRAINT fk_review_movie FOREIGN KEY (movie_id) REFERENCES db1.movie (movie_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_review_member FOREIGN KEY (member_id) REFERENCES db1.member (member_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB;
