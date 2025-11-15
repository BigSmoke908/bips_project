INSERT INTO lecturer (id, firstname, lastname, title, phone, email, username) VALUES
(1, 'Anna', 'Schmidt', 'Prof. Dr.', '030-1234567', 'anna.schmidt@hochschule.de', 'aschmidt'),
(2, 'Markus', 'Weber', 'Dr.', '030-2345678', 'markus.weber@hochschule.de', 'mweber'),
(3, 'Julia', 'Klein', 'Prof.', '030-3456789', 'julia.klein@hochschule.de', 'jklein'),
(4, 'Thomas', 'Müller', 'Dr.', '030-4567890', 'thomas.mueller@hochschule.de', 'tmueller'),
(5, 'Sabine', 'Fischer', 'Prof. Dr.', '030-5678901', 'sabine.fischer@hochschule.de', 'sfischer'),
(6, 'Peter', 'Becker', 'Dr.', '030-6789012', 'peter.becker@hochschule.de', 'pbecker'),
(7, 'Laura', 'Neumann', 'Prof.', '030-7890123', 'laura.neumann@hochschule.de', 'lneumann'),
(8, 'Michael', 'Schulz', 'Prof. Dr.', '030-8901234', 'michael.schulz@hochschule.de', 'mschulz');

UPDATE lecturer
SET username = 'demo'
Where id < 10;
