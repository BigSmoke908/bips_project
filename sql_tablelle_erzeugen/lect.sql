-- Unternehmen einfügen
INSERT INTO company (id, description, address, zip_code, city) VALUES
(1, 'Siemens Mobility', 'Werner-von-Siemens-Str. 1', '80333', 'München'),
(2, 'Volkswagen AG', 'Berliner Ring 2', '38440', 'Wolfsburg'),
(3, 'TechNova GmbH', 'Innovationsweg 5', '10115', 'Berlin');

-- Betreuer einfügen
INSERT INTO supervisor (id, firstname, lastname, title, phone, email, company_id) VALUES
(1, 'Claudia', 'Meier', 'Dr.', '089-123456', 'claudia.meier@siemens.com', 1),
(2, 'Jürgen', 'Keller', 'Prof.', '089-654321', 'juergen.keller@siemens.com', 1),
(3, 'Sandra', 'Beck', 'Dr.', '05361-123456', 'sandra.beck@volkswagen.de', 2),
(4, 'Thomas', 'Schwarz', 'Prof.', '05361-654321', 'thomas.schwarz@volkswagen.de', 2),
(5, 'Nina', 'Lang', 'Dr.', '030-123456', 'nina.lang@technova.de', 3),
(6, 'Felix', 'Berger', 'Prof.', '030-654321', 'felix.berger@technova.de', 3);


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
