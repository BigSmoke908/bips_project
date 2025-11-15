SELECT * FROM bips_ws25.supervisor;

Update supervisor
Set title = ""
Where title = "Prof." and id <10;