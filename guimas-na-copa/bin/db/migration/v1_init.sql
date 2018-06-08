CREATE TABLE usuario(
   id SERIAL PRIMARY  KEY,
   name VARCHAR(150)  NOT NULL,
   email VARCHAR(150) NOT NULL,
   pass VARCHAR(150),
   admin BOOLEAN NOT NULL
);

INSERT INTO usuario VALUES('admin','admin@admin','admin',true);