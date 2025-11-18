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
INSERT INTO lecturer (id, firstname, lastname, title, phone, email, username) VALUES
(1, 'Anna', 'Schmidt', 'Prof. Dr.', '030-1234567', 'anna.schmidt@hochschule.de', 'aschmidt'),
(2, 'Markus', 'Weber', 'Dr.', '030-2345678', 'markus.weber@hochschule.de', 'mweber'),
(3, 'Julia', 'Klein', 'Prof.', '030-3456789', 'julia.klein@hochschule.de', 'jklein'),
(4, 'Thomas', 'Müller', 'Dr.', '030-4567890', 'thomas.mueller@hochschule.de', 'tmueller'),
(5, 'Sabine', 'Fischer', 'Prof. Dr.', '030-5678901', 'sabine.fischer@hochschule.de', 'sfischer'),
(6, 'Peter', 'Becker', 'Dr.', '030-6789012', 'peter.becker@hochschule.de', 'pbecker'),
(7, 'Laura', 'Neumann', 'Prof.', '030-7890123', 'laura.neumann@hochschule.de', 'lneumann'),
(8, 'Michael', 'Schulz', 'Prof. Dr.', '030-8901234', 'michael.schulz@hochschule.de', 'mschulz');


-- Neue Dozenten einfügen (basierend auf Ostfalia-Personensuche und Fakultätsseiten)
INSERT INTO lecturer (id, firstname, lastname, title, phone, email, username) VALUES
(10, 'Ina', 'Schiering', 'Prof. Dr. rer. nat.', '+49 5331 939-31140', 'i.schiering@ostfalia.de', 'ischiering'),
(9, 'Tobias', 'Dörnbach', 'Prof. PhD', '+49 5331 939-32160', 't.doernbach@ostfalia.de', 'tdoernbach'),
(11, 'Jürgen', 'Kreyssig', 'Prof. Dr.-Ing.', '+49 5331 939-32100', 'j.kreyssig@ostfalia.de', 'jkreyssig');


INSERT INTO lecturer (id, firstname, lastname, title, phone, email, username) VALUES
(12, 'Frank', 'Höppner', 'Prof. Dr.', '+49 5331 939-32110', 'f.hoeppner@ostfalia.de', 'fhoeppner'),
(13, 'Kai', 'Gutenschwager', 'Prof. Dr.', '+49 5331 939-32120', 'k.gutenschwager@ostfalia.de', 'kgutenschwager');

UPDATE lecturer
SET username = 'demo'
Where id < 100;
INSERT INTO type_of_student_work (description, is_thesis)
VALUES 
  ('Projektarbeit', 0),
  ('Seminararbeit', 0),
  ('Abschlussarbeit', 1);


INSERT INTO course_of_studies (id, description) VALUES
(1, 'Angewandte Informatik'),
(2, 'Bauingenieurwesen'),
(3, 'Elektrotechnik'),
(4, 'Maschinenbau'),
(5, 'Wirtschaftsingenieurwesen'),
(6, 'Soziale Arbeit'),
(7, 'Pflegewissenschaft'),
(8, 'Verkehrsmanagement'),
(9, 'Tourismusmanagement'),
(10, 'Medienmanagement'),
(11, 'Recht'),
(12, 'BWL'),
(13, 'Logistikmanagement'),
(14, 'Umweltingenieurwesen'),
(15, 'Sportmanagement');

