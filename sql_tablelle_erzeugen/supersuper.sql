SELECT * FROM bips_ws25.supervisor;

DROP TABLE IF EXISTS lecturer;

-- Tabelle neu erstellen
CREATE TABLE lecturer (
  id INT PRIMARY KEY,
  firstname VARCHAR(45),
  lastname VARCHAR(45),
  title VARCHAR(45),
  phone VARCHAR(45),
  email VARCHAR(45),
  username VARCHAR(45)
);

-- Neue Dozenten einfügen (basierend auf Ostfalia-Personensuche und Fakultätsseiten)
INSERT INTO lecturer (id, firstname, lastname, title, phone, email, username) VALUES
(10, 'Ina', 'Schiering', 'Prof. Dr. rer. nat.', '+49 5331 939-31140', 'i.schiering@ostfalia.de', 'ischiering'),
(9, 'Tobias', 'Dörnbach', 'Prof. PhD', '+49 5331 939-32160', 't.doernbach@ostfalia.de', 'tdoernbach'),
(11, 'Jürgen', 'Kreyssig', 'Prof. Dr.-Ing.', '+49 5331 939-32100', 'j.kreyssig@ostfalia.de', 'jkreyssig');


INSERT INTO lecturer (id, firstname, lastname, title, phone, email, username) VALUES
(12, 'Frank', 'Höppner', 'Prof. Dr.', '+49 5331 939-32110', 'f.hoeppner@ostfalia.de', 'fhoeppner'),
(13, 'Kai', 'Gutenschwager', 'Prof. Dr.', '+49 5331 939-32120', 'k.gutenschwager@ostfalia.de', 'kgutenschwager');

INSERT INTO type_of_student_work (description, is_thesis)
VALUES 
  ('Projektarbeit', 0),
  ('Seminararbeit', 0),
  ('Abschlussarbeit', 1);

