# Сервис учёта пользовательских подписок

Необходимо реализовать сервис, который хранит информацию о подписках пользователей на различные сервисы или тарифы.

Что нужно реализовать
API для:

• создания подписки

• изменения статуса подписки

• просмотра подписки по id

• получения списка подписок

• отмены и приостановлении подписки

• получения активных подписок пользователя

Функциональные требования

• у подписки должен быть корректный жизненный цикл (например статусы активна, приостановлена, отменена, истекла)

• нельзя активировать уже истёкшую подписку без продления

• должны быть фильтры:

o по пользователю

o по сервису

o по статусу

o по диапазону дат

• пагинация и сортировка

• валидация дат и стоимости

Дополнительно

Можно реализовать:

• автоперевод подписок в EXPIRED по scheduler

• напоминания о скором окончании (например таблица с планируемыми уведомлениями)

• историю изменения статусов

• статистику по активным/отменённым подпискам

• экспорт списка подписок в CSV

Требования к стеку

• Kotlin

• Spring Boot 3.x+

• PostgreSQL(Можно использовать H2)

• Spring Data JPA

• Liquibase (Или Flyway)

• OpenAPI / Swagger

• JUnit

Будет плюсом

• scheduler

• Testcontainers

• docker-compose

• Actuator / Micrometer

# Запуск
docker compose up -d

# Swagger UI
http://localhost:8080/swagger-ui.html

# Методы
POST /api/v1/subscriptions Создать подписку

GET /api/v1/subscriptions поддерживает фильтрацию:
- `userId` по пользователю
- `serviceName` по сервису
- `status` по статусу
- `startDateFrom` дата начала от
- `endDateTo` дата окончания до
- `page`, `size`, `sort` пагинация и сортировка

GET /api/v1/subscriptions/{id} Получить по id

PUT /api/v1/subscriptions/{id} Обновить цену и дату окончания

PATCH /api/v1/subscriptions/{id}/suspend Приостановить

PATCH /api/v1/subscriptions/{id}/cancel Отменить

PATCH /api/v1/subscriptions/{id}/activate Активировать

GET /api/v1/subscriptions/users/{userId}/active Активные подписки пользователя

# Реализовано
- все 8 REST endpoints
- жизненный цикл подписки (ACTIVE/SUSPENDED/CANCELLED/EXPIRED)
- фильтры по всем полям
- пагинация и сортировка
- валидация дат и цены
- Kotlin + Spring Boot 3.5
- PostgreSQL + JPA
- Flyway миграции
- OpenAPI, Swagger
- JUnit тесты
- Scheduler 
- Docker Compose
- Actuator