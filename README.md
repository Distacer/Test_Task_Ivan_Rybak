WinWin Travel - Microservices Test Task (2026)

Цей проєкт реалізує систему з двох мікросервісів для обробки тексту з використанням JWT-авторизації та логування запитів у базу даних PostgreSQL.

Архітектура системи

Auth API (Port 8080): Керує реєстрацією, логіном (JWT) та координує процес обробки тексту.
Data API (Port 8081): Приймає текст, трансформує його (Reverse + Uppercase) та повертає результат.
PostgreSQL: Зберігає дані користувачів та історію обробки тексту.

Технологічний стек

Java 25 / Spring Boot 3
Spring Security + JJWT (JSON Web Token)
Spring Data JPA + PostgreSQL
Docker & Docker Compose

Як запустити проєкт

1. Збірка JAR-файлів

Переконайтеся, що у вас встановлено Maven. Виконайте збірку обох сервісів:

mvn -f auth-api/pom.xml clean package -DskipTests
mvn -f data-api/pom.xml clean package -DskipTests

2. Запуск у Docker

Запустіть всі контейнери однією командою (база даних та обидва сервіси піднімуться автоматично):

docker compose up -d --build 

Тестування (End-to-End)
Крок 1: Реєстрація нового користувача

curl -X POST http://localhost:8080/api/auth/register \
     -H "Content-Type: application/json" \
     -d '{"email":"test@winwin.travel", "password":"password123"}'
     
Крок 2: Авторизація (отримання JWT токена)

curl -X POST http://localhost:8080/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{"email":"test@winwin.travel", "password":"password123"}'
     
Скопіюйте значення token з отриманої відповіді.

Крок 3: Захищена обробка тексту
Замініть <TOKEN> на ваш JWT токен:

Bash
curl -X POST http://localhost:8080/api/process \
     -H "Authorization: Bearer <TOKEN>" \
     -H "Content-Type: application/json" \
     -d '{"text":"jwt security works"}'
Очікуваний результат: {"result":"SKROW YTIRUCES TWJ"}.

Модель даних (Database)

Система автоматично створює дві таблиці:
users: id (UUID), email, password_hash (BCrypt).

processing_log: id, user_id, input_text, output_text, created_at (Timestamp).

🛡 Безпека
Міжсервісна комунікація (Auth API -> Data API) захищена за допомогою X-Internal-Token.

Паролі користувачів ніколи не зберігаються у відкритому вигляді.
