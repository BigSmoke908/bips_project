SELECT * FROM bips_ws25.supervisor;

-- Tabelle neu erstellen

-- Neue Dozenten einfügen (basierend auf Ostfalia-Personensuche und Fakultätsseiten)
INSERT INTO lecturer (id, firstname, lastname, title, phone, email, username) VALUES
(1, 'Ina', 'Schiering', 'Prof. Dr. rer. nat.', '+49 5331 939-31140', 'i.schiering@ostfalia.de', 'ischiering'),
(2, 'Tobias', 'Dörnbach', 'Prof. PhD', '+49 5331 939-32160', 't.doernbach@ostfalia.de', 'tdoernbach'),
(3, 'Jürgen', 'Kreyssig', 'Prof. Dr.-Ing.', '+49 5331 939-32100', 'j.kreyssig@ostfalia.de', 'jkreyssig'),
(4, 'Frank', 'Höppner', 'Prof. Dr.', '+49 5331 939-32110', 'f.hoeppner@ostfalia.de', 'fhoeppner'),
(5, 'Kai', 'Gutenschwager', 'Prof. Dr.', '+49 5331 939-32120', 'k.gutenschwager@ostfalia.de', 'kgutenschwager');

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

