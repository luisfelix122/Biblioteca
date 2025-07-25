CREATE TABLE Libro (
    isbn VARCHAR(20) PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    autor VARCHAR(255) NOT NULL,
    anioPublicacion INT,
    disponible BIT NOT NULL
);