CREATE TABLE ConfiguracionSistema (
    clave VARCHAR(255) PRIMARY KEY,
    valor VARCHAR(MAX)
);

INSERT INTO ConfiguracionSistema (clave, valor) VALUES
('nombre_biblioteca', 'Biblioteca Universitaria'),
('direccion_biblioteca', 'Calle Falsa 123'),
('telefono_biblioteca', '555-1234'),
('email_biblioteca', 'info@biblioteca.edu');