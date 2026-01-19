DROP TABLE IF EXISTS request;
DROP TABLE IF EXISTS request_type;
DROP TABLE IF EXISTS department;

CREATE TABLE department (
                            id BIGINT PRIMARY KEY,
                            name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE request_type (
                              id BIGINT PRIMARY KEY,
                              name VARCHAR(150) NOT NULL,
                              description VARCHAR(500) NOT NULL,
                              category VARCHAR(30) NOT NULL,
                              sla_days INT NOT NULL,
                              active BOOLEAN NOT NULL,
                              department_id BIGINT NOT NULL,
                              CONSTRAINT fk_request_type_department
                                  FOREIGN KEY (department_id) REFERENCES department(id)
);

CREATE TABLE request (
                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         protocol_number VARCHAR(30) NOT NULL UNIQUE,
                         status VARCHAR(30) NOT NULL,
                         description VARCHAR(1000) NOT NULL,

    -- PROBLEM_REPORT
                         location_text VARCHAR(255),

    -- APPLICATION
                         address VARCHAR(255),
                         purpose VARCHAR(255),
                         afm VARCHAR(20),
                         amka VARCHAR(20),
                         citizen_id_number VARCHAR(30),

                         created_at TIMESTAMP NOT NULL,
                         due_at TIMESTAMP NOT NULL,

                         request_type_id BIGINT NOT NULL,
                         CONSTRAINT fk_request_request_type
                             FOREIGN KEY (request_type_id) REFERENCES request_type(id)
);

INSERT INTO department (id, name) VALUES
                                      (1, 'ΚΕΠ'),
                                      (2, 'Δημοτολόγιο / Ληξιαρχείο'),
                                      (3, 'Τεχνική Υπηρεσία'),
                                      (4, 'Οικονομική Υπηρεσία'),
                                      (5, 'Κοινωνική Υπηρεσία');

UPDATE request_type SET required_attachments = 1 WHERE id = 11;
UPDATE request_type SET required_attachments = 2 WHERE id IN (1,2,3,4,7);
UPDATE request_type SET required_attachments = 3 WHERE id IN (5,6);
UPDATE request_type SET required_attachments = 4 WHERE id IN (8,9,10);

INSERT INTO request_type
(name, description, category, sla_days, active, department_id, required_attachments)
VALUES
    ('Βεβαίωση Μόνιμης Κατοικίας',
     'Έκδοση βεβαίωσης μόνιμης κατοικίας για χρήση σε δημόσιες ή ιδιωτικές υπηρεσίες.',
     'APPLICATION', 7, b'1', 1, 2),

    ('Βεβαίωση Οικογενειακής Κατάστασης',
     'Έκδοση πιστοποιητικού οικογενειακής κατάστασης για διοικητικές διαδικασίες.',
     'APPLICATION', 7, b'1', 1, 2),

    ('Πιστοποιητικό Γέννησης',
     'Αίτηση έκδοσης πιστοποιητικού γέννησης για επίσημη χρήση.',
     'APPLICATION', 10, b'1', 2, 2),

    ('Πιστοποιητικό Οικογενειακής Μερίδας',
     'Έκδοση πιστοποιητικού οικογενειακής μερίδας από το δημοτολόγιο.',
     'APPLICATION', 10, b'1', 2, 2),

    ('Άδεια Κατάληψης Πεζοδρομίου',
     'Αίτηση άδειας για προσωρινή κατάληψη κοινόχρηστου χώρου λόγω εργασιών.',
     'APPLICATION', 12, b'1', 3, 3),

    ('Βεβαίωση Τεχνικών Στοιχείων Ακινήτου',
     'Αίτηση βεβαίωσης για τεχνικά στοιχεία που αφορούν ακίνητο.',
     'APPLICATION', 12, b'1', 3, 3),

    ('Βεβαίωση Μη Οφειλής',
     'Έκδοση βεβαίωσης ότι δεν υπάρχουν ληξιπρόθεσμες οφειλές.',
     'APPLICATION', 8, b'1', 4, 2),

    ('Ρύθμιση Οφειλών',
     'Υποβολή αιτήματος για ρύθμιση ή διακανονισμό οφειλών.',
     'APPLICATION', 15, b'1', 4, 4),

    ('Αίτηση Κοινωνικής Στήριξης',
     'Υποβολή αιτήματος κοινωνικής υποστήριξης.',
     'APPLICATION', 15, b'1', 5, 4),

    ('Βοήθεια στο Σπίτι',
     'Πρόγραμμα υποστήριξης στο σπίτι.',
     'APPLICATION', 15, b'1', 5, 4),

    ('Αναφορά Προβλήματος στην Πόλη',
     'Αναφέρετε προβλήματα σε κοινόχρηστους χώρους.',
     'PROBLEM_REPORT', 5, b'1', 3, 1);




UPDATE request_type
SET description = 'Έκδοση βεβαίωσης μόνιμης κατοικίας για χρήση σε δημόσιες ή ιδιωτικές υπηρεσίες.
\nΑπαιτούμενα δικαιολογητικά:\n- Αντίγραφο δελτίου ταυτότητας\n- Αποδεικτικό κατοικίας (λογαριασμός ΔΕΚΟ ή μισθωτήριο τελευταίου 3μήνου)'
WHERE id = 1;

UPDATE request_type
SET description = 'Έκδοση πιστοποιητικού οικογενειακής κατάστασης για διοικητικές ή κοινωνικές διαδικασίες.
\nΑπαιτούμενα δικαιολογητικά:\n- Αντίγραφο δελτίου ταυτότητας\n- Υπεύθυνη δήλωση (εφόσον ζητηθεί επικαιροποίηση στοιχείων)'
WHERE id = 2;

UPDATE request_type
SET description = 'Αίτηση έκδοσης πιστοποιητικού γέννησης για επίσημη χρήση σε δημόσιες ή ιδιωτικές υπηρεσίες.
\nΑπαιτούμενα δικαιολογητικά:\n- Αντίγραφο δελτίου ταυτότητας\n- Βεβαίωση ΑΜΚΑ'
WHERE id = 3;

UPDATE request_type
SET description = 'Έκδοση πιστοποιητικού οικογενειακής μερίδας από το δημοτολόγιο του Δήμου.
\nΑπαιτούμενα δικαιολογητικά:\n- Αντίγραφο δελτίου ταυτότητας\n- Υπεύθυνη δήλωση σε περίπτωση μεταβολής οικογενειακών στοιχείων'
WHERE id = 4;

UPDATE request_type
SET description = 'Αίτηση άδειας για προσωρινή κατάληψη κοινόχρηστου χώρου λόγω οικοδομικών ή τεχνικών εργασιών.
\nΑπαιτούμενα δικαιολογητικά:\n- Αντίγραφο δελτίου ταυτότητας\n- Σχέδιο ή φωτογραφία του χώρου\n- Απόδειξη καταβολής τέλους (εφόσον απαιτείται)'
WHERE id = 5;

UPDATE request_type
SET description = 'Αίτηση βεβαίωσης τεχνικών στοιχείων που αφορούν ακίνητο ή εγκατάσταση.
\nΑπαιτούμενα δικαιολογητικά:\n- Αντίγραφο δελτίου ταυτότητας\n- Τίτλος ιδιοκτησίας ή μισθωτήριο\n- Σχέδια ή τεχνική περιγραφή (εφόσον υπάρχουν)'
WHERE id = 6;

UPDATE request_type
SET description = 'Έκδοση βεβαίωσης ότι δεν υπάρχουν ληξιπρόθεσμες οφειλές προς τον Δήμο.
\nΑπαιτούμενα δικαιολογητικά:\n- Αντίγραφο δελτίου ταυτότητας\n- Βεβαώση Απόδοσης ΑΦΜ'
WHERE id = 7;

UPDATE request_type
SET description = 'Υποβολή αιτήματος για ρύθμιση ή διακανονισμό οφειλών προς τον Δήμο σε δόσεις.
\nΑπαιτούμενα δικαιολογητικά:\n- Αντίγραφο δελτίου ταυτότητας\n- Βεβαώση Απόδοσης ΑΦΜ\n- Στοιχεία οφειλής ή ειδοποίηση πληρωμής\n- Υπεύθυνη δήλωση αποδοχής όρων ρύθμισης'
WHERE id = 8;

UPDATE request_type
SET description = 'Έκδοση βεβαίωσης ότι δεν υπάρχουν ληξιπρόθεσμες οφειλές προς τον Δήμο.
\nΑπαιτούμενα δικαιολογητικά:\n- Αντίγραφο δελτίου ταυτότητας\n- Εκκαθαριστικό σημείωμα ή αποδεικτικό εισοδήματος\n- Πιστοποιητικό οικογενειακής κατάστασης\n- Ιατρική γνωμάτευση (εφόσον απαιτείται)'
WHERE id = 9;

UPDATE request_type
SET description = 'Αίτημα ενημέρωσης ή ένταξης σε πρόγραμμα υποστήριξης στο σπίτι για ηλικιωμένους ή άτομα με ανάγκη φροντίδας.
\nΑπαιτούμενα δικαιολογητικά:\n- Αντίγραφο δελτίου ταυτότητας\n- Ιατρική γνωμάτευση\n- Πιστοποιητικό οικογενειακής κατάστασης\n- Εκκαθαριστικό σημείωμα ή αποδεικτικό εισοδήματος'
WHERE id = 10;

UPDATE request_type
SET description = 'Αναφέρετε προβλήματα σε κοινόχρηστους χώρους όπως φωτισμός, λακκούβες, σπασμένα πεζοδρόμια, καθαριότητα/απορρίμματα ή άλλες τεχνικές βλάβες.
\nΑπαιτούμενα δικαιολογητικά:\n- Αντίγραφο δελτίου ταυτότητας'
WHERE id = 11;


CREATE TABLE request_attachment (
                                    id BIGINT PRIMARY KEY AUTO_INCREMENT,

                                    request_id BIGINT NOT NULL,

                                    doc_type VARCHAR(50) NOT NULL,          -- π.χ. ID_COPY, PROOF_OF_ADDRESS
                                    original_filename VARCHAR(255) NOT NULL,
                                    stored_filename VARCHAR(255) NOT NULL,
                                    content_type VARCHAR(100) NOT NULL,
                                    file_size BIGINT NOT NULL,
                                    storage_path VARCHAR(500) NOT NULL,

                                    uploaded_at TIMESTAMP NOT NULL,

                                    CONSTRAINT fk_request_attachment_request
                                        FOREIGN KEY (request_id) REFERENCES request(id)
);

ALTER TABLE request_attachment
    DROP column doc_type;

ALTER TABLE request_type
    ADD COLUMN required_attachments INT NOT NULL DEFAULT 0;


CREATE TABLE department_schedule (
                                     id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                     department_id BIGINT NOT NULL,
                                     day_of_week VARCHAR(10) NOT NULL,
                                     start_time TIME NOT NULL,
                                     end_time TIME NOT NULL,
                                     CONSTRAINT fk_schedule_department FOREIGN KEY (department_id) REFERENCES department(id)
);

CREATE INDEX idx_schedule_dept_day ON department_schedule(department_id, day_of_week);

CREATE TABLE appointment (
                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             department_id BIGINT NOT NULL,
                             citizen_id_number VARCHAR(30) NOT NULL,
                             start_at DATETIME(6) NOT NULL,
                             end_at DATETIME(6) NOT NULL,
                             status VARCHAR(20) NOT NULL,
                             created_at DATETIME(6) NOT NULL,
                             CONSTRAINT fk_appointment_department FOREIGN KEY (department_id) REFERENCES department(id)
);

CREATE INDEX idx_appointment_dept_start ON appointment(department_id, start_at);


-- 1) ΚΕΠ (id=1): Mon-Fri 09:00-15:00
INSERT INTO department_schedule(department_id, day_of_week, start_time, end_time) VALUES
                                                                                      (1,'MONDAY','09:00:00','15:00:00'),
                                                                                      (1,'TUESDAY','09:00:00','15:00:00'),
                                                                                      (1,'WEDNESDAY','09:00:00','15:00:00'),
                                                                                      (1,'THURSDAY','09:00:00','15:00:00'),
                                                                                      (1,'FRIDAY','09:00:00','15:00:00');

-- 2) Δημοτολόγιο/Ληξιαρχείο (id=2): Mon-Fri 09:00-13:00
INSERT INTO department_schedule(department_id, day_of_week, start_time, end_time) VALUES
                                                                                      (2,'MONDAY','09:00:00','13:00:00'),
                                                                                      (2,'TUESDAY','09:00:00','13:00:00'),
                                                                                      (2,'WEDNESDAY','09:00:00','13:00:00'),
                                                                                      (2,'THURSDAY','09:00:00','13:00:00'),
                                                                                      (2,'FRIDAY','09:00:00','13:00:00');

-- 3) Τεχνική Υπηρεσία (id=3): Mon-Fri 10:00-14:00
INSERT INTO department_schedule(department_id, day_of_week, start_time, end_time) VALUES
                                                                                      (3,'MONDAY','10:00:00','14:00:00'),
                                                                                      (3,'TUESDAY','10:00:00','14:00:00'),
                                                                                      (3,'WEDNESDAY','10:00:00','14:00:00'),
                                                                                      (3,'THURSDAY','10:00:00','14:00:00'),
                                                                                      (3,'FRIDAY','10:00:00','14:00:00');

-- 4) Οικονομική (id=4): Mon/Wed/Fri 09:00-13:00
INSERT INTO department_schedule(department_id, day_of_week, start_time, end_time) VALUES
                                                                                      (4,'MONDAY','09:00:00','13:00:00'),
                                                                                      (4,'WEDNESDAY','09:00:00','13:00:00'),
                                                                                      (4,'FRIDAY','09:00:00','13:00:00');

-- 5) Κοινωνική (id=5): Tue/Thu 09:00-13:00
INSERT INTO department_schedule(department_id, day_of_week, start_time, end_time) VALUES
                                                                                      (5,'TUESDAY','09:00:00','13:00:00'),
                                                                                      (5,'THURSDAY','09:00:00','13:00:00');


-- =========================
-- USERS
-- =========================
CREATE TABLE users (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,

                       username VARCHAR(50) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,

                       role VARCHAR(20) NOT NULL,         -- CITIZEN / EMPLOYEE / ADMIN
                       enabled BOOLEAN NOT NULL DEFAULT TRUE,

                       created_at DATETIME(6) NOT NULL
);

-- =========================
-- CITIZEN PROFILE (email only here)
-- =========================
CREATE TABLE citizen_profile (
                                 user_id BIGINT PRIMARY KEY,

                                 email VARCHAR(150) NOT NULL UNIQUE,

                                 afm VARCHAR(20),
                                 amka VARCHAR(20),
                                 full_name VARCHAR(120),

                                 CONSTRAINT fk_citizen_user
                                     FOREIGN KEY (user_id) REFERENCES users(id)
                                         ON DELETE CASCADE
);

-- =========================
-- EMPLOYEE PROFILE
-- =========================
CREATE TABLE employee_profile (
                                  user_id BIGINT PRIMARY KEY,

                                  department_id BIGINT NOT NULL,
                                  full_name VARCHAR(120),

                                  CONSTRAINT fk_employee_user
                                      FOREIGN KEY (user_id) REFERENCES users(id)
                                          ON DELETE CASCADE,

                                  CONSTRAINT fk_employee_department
                                      FOREIGN KEY (department_id) REFERENCES department(id)
);



-- Ensure next AUTO_INCREMENT continues after our inserts
ALTER TABLE users AUTO_INCREMENT = 12;

-- -------------------------
-- USERS
-- -------------------------
-- Password mapping (EASY, numeric, different):
-- admin  -> 111111
-- e2  -> 200002
-- e3  -> 200003
-- e4  -> 200004
-- e5  -> 200005
-- e6  -> 200006
-- e7  -> 200007
-- e8  -> 200008
-- e9  -> 200009
-- e10 -> 200010
-- e11 -> 200011

INSERT INTO users (id, username, password_hash, role, enabled, created_at) VALUES
                                                                               (1,  'admin',  '$2b$10$zEv4kS5pBpK8Xzf1e2c.T.ELl8VVBuMruFaEg95lmFi9hoLmAoS8q', 'ADMIN',    TRUE, NOW(6)),

                                                                               (2,  'e2',  '$2b$10$oUA.LSycPfbRC0p79FIoBeX8fUpTAcoB32YXahsc9Gqjt3qjlzDo.', 'EMPLOYEE', TRUE, NOW(6)),
                                                                               (3,  'e3',  '$2b$10$BhvpR2z7aGJVX5z1HeFv0uQ04IdUvJkLW9ZcXyFAPAO2VHeauuR0S', 'EMPLOYEE', TRUE, NOW(6)),
                                                                               (4,  'e4',  '$2b$10$ySJCn8FRJkVr08ImaH6jCuR1aw1yR0JO2k9CiszZfhd5UvDMas3Vi', 'EMPLOYEE', TRUE, NOW(6)),
                                                                               (5,  'e5',  '$2b$10$5umn5I1.7erVw.hkLp4HD.26xLf/H9y9Q0LfzZvmC5U2aj5LxZNAq', 'EMPLOYEE', TRUE, NOW(6)),
                                                                               (6,  'e6',  '$2b$10$KidLs50EaPczXUGGI02ameF3WJqaIVMV83BHC.9Um3YAzblAq/5DK', 'EMPLOYEE', TRUE, NOW(6)),
                                                                               (7,  'e7',  '$2b$10$M9d/qS2zADhsPedl4BiThuUsMPnRD3MgOpOtJv5aHFadYxLeHJKDG', 'EMPLOYEE', TRUE, NOW(6)),
                                                                               (8,  'e8',  '$2b$10$5v47nJCkegw.OD1CoHpnTuJwJt5TcOF0EKud/aRA4sk9du.zYWfyO', 'EMPLOYEE', TRUE, NOW(6)),
                                                                               (9,  'e9',  '$2b$10$jbu6bbUK0D2ibrUl1vSUcuoeqn1avHLsAmrampfgpporgMu91GKs6', 'EMPLOYEE', TRUE, NOW(6)),
                                                                               (10, 'e10', '$2b$10$HTnu9ukRhxKBkHo5SxTV2OTEEQCWN0.u6PFWXGdJbUYXlS9A1/Jfy', 'EMPLOYEE', TRUE, NOW(6)),
                                                                               (11, 'e11', '$2b$10$odAepXm5V1hIwqf/fX0LDukN5bOgRgmp7JCF4Oi4.5ErloLVSXfae', 'EMPLOYEE', TRUE, NOW(6));

-- -------------------------
-- EMPLOYEE PROFILES
-- (change department_id=1 if needed)
-- -------------------------
INSERT INTO employee_profile (user_id, department_id, full_name) VALUES
                                                                     (2,  1, 'Γεώργιος Αντωνίου'),
                                                                     (3,  1, 'Μαρία Παπαδοπούλου'),
                                                                     (4,  1, 'Ιωάννης Νικολάου'),
                                                                     (5,  2, 'Ελένη Κωνσταντίνου'),
                                                                     (6,  2, 'Δημήτρης Γεωργίου'),
                                                                     (7,  3, 'Κατερίνα Χριστοδούλου'),
                                                                     (8,  3, 'Νικόλαος Σταυρίδης'),
                                                                     (9,  4, 'Αναστασία Βασιλείου'),
                                                                     (10, 5, 'Παναγιώτης Ιωάννου'),
                                                                     (11, 5, 'Σοφία Δημητρίου');


ALTER TABLE request
    ADD COLUMN assigned_employee_user_id BIGINT NULL;

ALTER TABLE request
    ADD CONSTRAINT fk_request_assigned_employee
        FOREIGN KEY (assigned_employee_user_id) REFERENCES users(id);

CREATE TABLE request_note (
                              id BIGINT PRIMARY KEY AUTO_INCREMENT,
                              request_id BIGINT NOT NULL,
                              employee_user_id BIGINT NOT NULL,
                              note_type VARCHAR(20) NOT NULL,
                              text VARCHAR(1000) NOT NULL,
                              CONSTRAINT fk_request_note_request
                                  FOREIGN KEY (request_id) REFERENCES request(id) ON DELETE CASCADE,
                              CONSTRAINT fk_request_note_employee
                                  FOREIGN KEY (employee_user_id) REFERENCES users(id)
);

UPDATE users
SET username = 'a1'
WHERE id = 1;


SHOW CREATE TABLE `request_attachment`;