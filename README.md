# Panduan Instalasi Spring Boot

Berikut adalah langkah-langkah untuk menginstal dan mengkonfigurasi aplikasi Spring Boot dengan Java 11.

## Persiapan

1. Pastikan Anda telah menginstal Java 11. Jika belum, Anda dapat mengunduhnya [di sini](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html).

2. Pastikan Anda memiliki Maven terinstal. Jika belum, Anda dapat mengunduhnya [di sini](https://maven.apache.org/download.cgi).

3. Siapkan database yang akan digunakan oleh aplikasi Spring Boot.

## Konfigurasi Aplikasi

1. Buka file `application.properties` di direktori `src/main/resources`.

2. Atur URL, username, dan password database sesuai dengan konfigurasi Anda:

    ```properties
    spring.datasource.url=jdbc:your_database_url
    spring.datasource.username=your_database_username
    spring.datasource.password=your_database_password
    ```

3. Konfigurasi hibernate untuk melakukan auto-generate tabel (DDL) dan menampilkan SQL:

    ```properties
    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.show-sql=true
    ```

4. Atur properti tambahan:

    ```properties
    spring.main.allow-circular-references=true
    spring.main.allow-bean-definition-overriding=true
    ```

## Generate Key Pair RSA

1. Generate pasangan kunci RSA. Buka terminal dan jalankan perintah berikut:

    ```bash
    openssl genpkey -algorithm RSA -out private_key.pem
    openssl rsa -pubout -in private_key.pem -out public_key.pem
    ```

   Tempatkan kedua file `private_key.pem` dan `public_key.pem` di direktori utama aplikasi.

## Menjalankan Aplikasi

1. Buka terminal di direktori utama aplikasi.

2. Jalankan perintah Maven untuk menjalankan aplikasi:

    ```bash
    mvn spring-boot:run
    ```

3. Aplikasi akan berjalan di [http://localhost:8080](http://localhost:8080).
