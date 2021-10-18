CREATE DATABASE PTT;
SET GLOBAL validate_password_policy = MEDIUM;
CREATE USER 'pttadmin'@'localhost' IDENTIFIED BY 'AdminPass@$24';
GRANT ALL PRIVILEGES ON PTT.* TO 'pttadmin'@'localhost';
FLUSH PRIVILEGES;

CREATE TABLE PTT.user(
    userid INTEGER PRIMARY KEY AUTO_INCREMENT,
    firstname VARCHAR(100) NOT NULL DEFAULT '',
    lastname VARCHAR(100) NOT NULL DEFAULT '',
    email VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE PTT.project(
    projectid INTEGER PRIMARY KEY AUTO_INCREMENT,
    userid INTEGER NOT NULL,
    projectname VARCHAR(100) NOT NULL UNIQUE,
    UNIQUE(projectid, userid),
    FOREIGN KEY (userid) REFERENCES user(userid) ON UPDATE RESTRICT ON DELETE CASCADE
);

CREATE TABLE PTT.session(
    sessionid INTEGER PRIMARY KEY AUTO_INCREMENT,
    projectid INTEGER NOT NULL,
    userid INTEGER NOT NULL,
    starttime DATETIME NOT NULL,
    starttimezone VARCHAR(100) NOT NULL,
    endtime DATETIME DEFAULT NULL,
    endtimezone VARCHAR(100) DEFAULT NULL,
    counter INTEGER NOT NULL DEFAULT 0,
    FOREIGN KEY (projectid, userid) REFERENCES project(projectid, userid) ON UPDATE RESTRICT ON DELETE CASCADE,
    FOREIGN KEY (userid) REFERENCES user(userid) ON UPDATE RESTRICT ON DELETE CASCADE,
    FOREIGN KEY (projectid) REFERENCES project(projectid) ON UPDATE RESTRICT ON DELETE CASCADE
);
