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

INSERT INTO request_type VALUES
                             (1, 'Βεβαίωση Μόνιμης Κατοικίας',
                              'Έκδοση βεβαίωσης μόνιμης κατοικίας για χρήση σε δημόσιες ή ιδιωτικές υπηρεσίες.',
                              'APPLICATION', 7, TRUE, 1),

                             (2, 'Βεβαίωση Οικογενειακής Κατάστασης',
                              'Έκδοση πιστοποιητικού οικογενειακής κατάστασης για διοικητικές διαδικασίες.',
                              'APPLICATION', 7, TRUE, 1),

                             (3, 'Πιστοποιητικό Γέννησης',
                              'Αίτηση έκδοσης πιστοποιητικού γέννησης για επίσημη χρήση.',
                              'APPLICATION', 10, TRUE, 2),

                             (4, 'Πιστοποιητικό Οικογενειακής Μερίδας',
                              'Έκδοση πιστοποιητικού οικογενειακής μερίδας από το δημοτολόγιο.',
                              'APPLICATION', 10, TRUE, 2),

                             (5, 'Άδεια Κατάληψης Πεζοδρομίου',
                              'Αίτηση άδειας για προσωρινή κατάληψη κοινόχρηστου χώρου λόγω εργασιών.',
                              'APPLICATION', 12, TRUE, 3),

                             (6, 'Βεβαίωση Τεχνικών Στοιχείων Ακινήτου',
                              'Αίτηση βεβαίωσης για τεχνικά στοιχεία που αφορούν ακίνητο ή εγκατάσταση.',
                              'APPLICATION', 12, TRUE, 3),

                             (7, 'Βεβαίωση Μη Οφειλής',
                              'Έκδοση βεβαίωσης ότι δεν υπάρχουν ληξιπρόθεσμες οφειλές προς τον Δήμο.',
                              'APPLICATION', 8, TRUE, 4),

                             (8, 'Ρύθμιση Οφειλών',
                              'Υποβολή αιτήματος για ρύθμιση ή διακανονισμό οφειλών σε δόσεις.',
                              'APPLICATION', 15, TRUE, 4),

                             (9, 'Αίτηση Κοινωνικής Στήριξης',
                              'Υποβολή αιτήματος αξιολόγησης για παροχή κοινωνικής υποστήριξης.',
                              'APPLICATION', 15, TRUE, 5),

                             (10, 'Βοήθεια στο Σπίτι',
                              'Αίτημα ενημέρωσης ή ένταξης σε πρόγραμμα υποστήριξης στο σπίτι.',
                              'APPLICATION', 15, TRUE, 5);

INSERT INTO request_type VALUES
    (11, 'Αναφορά Προβλήματος στην Πόλη',
     'Αναφέρετε προβλήματα σε κοινόχρηστους χώρους όπως φωτισμός, λακκούβες, σπασμένα πεζοδρόμια, καθαριότητα/απορρίμματα ή άλλες τεχνικές βλάβες.',
     'PROBLEM_REPORT', 5, TRUE, 3);

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


ALTER TABLE request_type
    ADD COLUMN required_attachments INT NOT NULL DEFAULT 0;

UPDATE request_type SET required_attachments = 2 WHERE id IN (1,2,3,4,7);
UPDATE request_type SET required_attachments = 3 WHERE id IN (5,6);
UPDATE request_type SET required_attachments = 4 WHERE id IN (8,9,10);

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