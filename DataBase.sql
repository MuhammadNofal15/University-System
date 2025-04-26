drop database if exists universitysystem;
create database universitysystem;
use universitysystem;

drop table if exists department;
create table department (
    dept_id int primary key,
    dept_name varchar(100) not null
);

drop table if exists professor;
create table professor (
    prof_id int primary key,
    name varchar(100) not null,
    email varchar(100) not null unique,
    dept_id int not null,
    foreign key (dept_id) references department(dept_id)
);

drop table if exists student;
create table student (
    student_id int primary key,
    name varchar(100) not null,
    email varchar(100) not null unique,
    dob date not null,
    dept_id int not null,
    foreign key (dept_id) references department(dept_id)
);

drop table if exists course;
create table course (
    course_id varchar(16) primary key,
    name varchar(100) not null,
    credits int not null,
    dept_id int not null,
    foreign key (dept_id) references department(dept_id)
);

drop table if exists classroom;
create table classroom (
    room_id int primary key,
    location varchar(100) not null,
    capacity int not null
);

drop table if exists schedules;
create table schedules (
    schedule_id int primary key,
    course_id varchar(16) not null,
    prof_id int not null,
    room_id int not null,
    time_slot varchar(50) not null,
    foreign key (course_id) references course(course_id),
    foreign key (prof_id) references professor(prof_id),
    foreign key (room_id) references classroom(room_id)
);

drop table if exists enrollments;
create table enrollments (
    enrollment_id int primary key,
    student_id int not null,
    course_id varchar(16) not null,
    semester varchar(50) not null,
    foreign key (student_id) references student(student_id),
    foreign key (course_id) references course(course_id)
);

insert into department (dept_id, dept_name) values
(1, 'Information Technology (IT)'),
(2, 'Business And Economics'),
(3, 'Pharmacy And Nursing'),
(4, 'Music And Arts'),
(5, 'Education');

insert into professor (prof_id, name, email, dept_id) values
(3362, 'Dr.Bassem Sayrafi', 'bsayrafi@staff.birzeit.edu', 1), -- IT teacher
(1455, 'Mahmoud Yahya', 'MYahya@staff.birzeit.edu', 2), -- Business teacher
(4101, 'Abdel Salam Sayyad', 'ASSayyad@staff.birzeit.edu', 1), -- IT teacher
(8043, 'Dr.Hatem Idais Manasrah', 'HIManasrah@staff.birzeit.edu', 3), -- Pharmacy And Nursing teacher
(4401, 'Faten Khalaf', 'FKhalaf@staff.birzeit.edu', 4), -- Music And Arts teacher
(2200, 'Bashar Ramadan', 'BRamadan@staff.birzeit.edu', 5); -- Education teacher (PE)

insert into student (student_id, name, email, dob, dept_id) values
(1220282, 'Muhammad Haddad', '1220282@student.birzeit.edu', '2004-03-26', 1),
(1220949, 'Samaa Kali', '1220949@student.birzeit.edu', '2004-07-30', 1),
(1220022, 'Ibrahim Abuhania', '1220022@student.birzeit.edu', '2003-06-05', 1),
(1224550, 'Mustafa Hammad', '1214550@student.birzeit.edu', '2004-09-02', 2),
(1222011, 'Ahmad Salman', '1222011@student.edu', '2003-07-16', 3),
(1222244, 'Yara Taha', '1212244@student.edu', '2004-08-01', 4);

insert into course (course_id, name, credits, dept_id) values
('COMP242', 'Data Structures', 4, 1),
('COMP333', 'Data Base Systems', 3, 1),
('ENCS4130', 'Computer Network Laboratory', 1, 1),
('NURS222', 'First Aid', 2, 3),
('DSGN1350', 'Graphic Design And Computer Technology', 3, 4),
('PHED227', 'Swimming', 2, 5),
('MKET230', 'Principles Of Marketing II', 3, 2);

insert into classroom (room_id, location, capacity) values
(1, 'IT, Room 202', 40),
(2, 'IT, Room 405', 40),
(3, 'Arts, Room 303', 40),
(4, 'Pharmacy, Room 200', 40),
(5, 'Business, Room 108', 40);

insert into schedules (schedule_id, course_id, prof_id, room_id, time_slot) values
(1, 'COMP333', 3362, 1, 'Monday 10:00-10:50'),
(2, 'MKET230', 1455, 5, 'Tuesday 13:00-14:20'),
(3, 'ENCS4130', 4101, 2, 'Wednesday 11:00-13:50'),
(4, 'NURS222', 8043, 4, 'Wednesday 12:00-13:50'),
(5, 'DSGN1350', 4401, 3, 'Saturday 8:00-9:20'); 

insert into enrollments (enrollment_id, student_id, course_id, semester) values
(1, 1220282, 'COMP242', 'Fall 2024'),
(2, 1220949, 'COMP333', 'Fall 2024'),
(3, 1220022, 'ENCS4130', 'Fall 2024'),
(4, 1224550, 'MKET230', 'Fall 2024'),
(5, 1222011, 'NURS222', 'Fall 2024'),
(6, 1222244, 'DSGN1350', 'Fall 2024');