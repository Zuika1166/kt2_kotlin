# Ktor Books API

Backend-приложение на Kotlin и Ktor с CRUD для книг, регистрацией пользователей, BCrypt и JWT-аутентификацией.

## Требования

- JDK 21
- Gradle

## Запуск

```bash
gradle run
```

Приложение запускается на `http://localhost:8080`.

Для изменения JWT-секрета можно передать переменную окружения `JWT_SECRET`.

```bash
JWT_SECRET=my-strong-secret gradle run
```

Данные хранятся в памяти и сбрасываются после перезапуска приложения.

## Регистрация

`POST /auth/register`

```bash
curl -i -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"login":"john","password":"secret123"}'
```

Успешный ответ: `201 Created`.

Повторная регистрация того же логина возвращает `400 Bad Request`.

## Вход

`POST /auth/login`

```bash
curl -i -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login":"john","password":"secret123"}'
```

Успешный ответ: `200 OK`.

JWT содержит claims `userId` и `login`. При неверном логине или пароле возвращается `401 Unauthorized`.

## Получение списка книг

`GET /books`

```bash
curl -i http://localhost:8080/books
```

Маршрут публичный. Успешный ответ: `200 OK`.

## Получение книги по id

`GET /books/{id}`

```bash
curl -i http://localhost:8080/books/1
```

Для отсутствующей книги возвращается `404 Not Found`, для некорректного id — `400 Bad Request`.

## Создание книги

`POST /books`

```bash
curl -i -X POST http://localhost:8080/books \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{"title":"Clean Code","author":"Robert C. Martin","year":2008}'
```

Успешный ответ: `201 Created`. Без валидного JWT возвращается `401 Unauthorized`.

## Обновление книги

`PUT /books/{id}`

```bash
curl -i -X PUT http://localhost:8080/books/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{"title":"Clean Code","author":"Robert C. Martin","year":2009}'
```

Успешный ответ: `200 OK`. Для отсутствующей книги возвращается `404 Not Found`.

## Удаление книги

`DELETE /books/{id}`

```bash
curl -i -X DELETE http://localhost:8080/books/1 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

Успешный ответ: `204 No Content`. Для отсутствующей книги возвращается `404 Not Found`.

## Тесты

```bash
gradle test
```
