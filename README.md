# Production-Ready REST API & Database Test Automation Framework

![Java 21](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![REST Assured](https://img.shields.io/badge/REST_Assured-5.5.0-green)
![JUnit 5](https://img.shields.io/badge/JUnit-5.11.0-blue?logo=junit5)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?logo=postgresql)
![Testcontainers](https://img.shields.io/badge/Testcontainers-1.20.1-brightgreen)
![Allure Report](https://img.shields.io/badge/Allure-2.29.0-yellow?logo=qameta)
![CI/CD](https://img.shields.io/badge/GitHub_Actions-On--Demand-blue?logo=githubactions)

Проект представляет собой образец построения современного фреймворка автоматизации тестирования REST API и интеграционного тестирования баз данных на стеке **Java 21 + REST Assured + JUnit 5 + Allure + PostgreSQL (Testcontainers & HikariCP)**, спроектированный с учетом актуальных Best Practices, применимых в реальных enterprise-проектах.

В качестве тестируемого REST сервиса используется публичный API **[DummyJSON](https://dummyjson.com/)**, а для проверки состояния данных и транзакций подключена изолированная база данных **PostgreSQL** на базе **Testcontainers**.

---

## 🛠 Технологический стек

| Технология | Назначение |
|---|---|
| **Java 21 (LTS)** | Язык разработки |
| **REST Assured 5.5.0** | Клиент и библиотека для выполнения и валидации HTTP-запросов |
| **JUnit 5 (Jupiter) 5.11.0** | Тестовый фреймворк (параллелизация, вложенные структуры `@Nested`, параметризация) |
| **Allure Report 2.29.0** | Фреймворк для формирования наглядных интерактивных HTML-отчетов с аттачами запросов/ответов |
| **AspectJ Weaver 1.9.22** | Байткод-инструментация для Allure `@Step` шагов |
| **AssertJ 3.26.3** | Fluent assertions библиотека и кастомные типобезопасные ассёрты |
| **Lombok 1.18.34** | Генерация boilerplate кода (`@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`) |
| **Jackson 2.17.2** | JSON сериализация и десериализация (SerDe) с аннотациями игнорирования неизвестных полей |
| **Owner 1.0.12** | Управление конфигурацией (типобезопасные интерфейсы, чтение из properties, env, cli-аргументов) |
| **PostgreSQL JDBC 42.7.4** | Драйвер для прямого подключения к базе данных PostgreSQL |
| **HikariCP 5.1.0** | Высокопроизводительный пул соединений к базе данных |
| **Testcontainers 1.20.1** | Автоматический подъем герметичного контейнера PostgreSQL 16 в тестах |
| **DataFaker 2.3.1** | Генерация реалистичных тестовых данных |
| **SLF4J + Logback 1.5.7** | Логирование запросов, ответов и системных событий |
| **GitHub Actions** | On-Demand CI/CD конвейеры, валидация PR и деплой отчетов Allure на GitHub Pages |
| **Maven** | Сборка проекта и управление зависимостями |

---

## 🏛 Архитектурные паттерны и Best Practices

Фреймворк построен по принципу **многослойной архитектуры (Layered Architecture)** и паттерна **Service Object / API Client**:

```
                  ┌───────────────────────────────┐
                  │       Test Suites             │
                  │ (ProductCrudTest, UserCrud...)│
                  └───────────────┬───────────────┘
                                  │ Uses
                  ┌───────────────▼───────────────┐
                  │    Service Clients (Layer)    │
                  │ (ProductClient, UserClient)   │
                  └───────┬───────────────┬───────┘
                          │               │
            Uses Specs &  │               │ SerDe via Jackson
            Allure Steps  │               │
     ┌────────────────────▼─────┐   ┌─────▼─────────────────────────┐
     │      Specifications      │   │          Models (DTO)         │
     │ RequestSpecs/ResponseSpecs│  │  (ProductDto, UserDto, etc.)  │
     └────────────────────┬─────┘   └─────▲─────────────────────────┘
                          │               │
                          │ Calls         │ Generates
     ┌────────────────────▼─────┐   ┌─────┴─────────────────────────┐
     │       REST Assured       │   │         Data Factories        │
     │      (HTTP Engine)       │   │  (ProductDataFactory, etc.)   │
     └──────────────────────────┘   └───────────────────────────────┘
```

### Ключевые архитектурные решения:

1. **Разделение ответственности (Separation of Concerns)**:
   - Тесты содержат исключительно бизнес-логику сценариев и проверки, не завися от URL, эндпоинтов или параметров сериализации.
   - HTTP-вызовы инкапсулированы внутри классов-клиентов (`ProductClient`, `UserClient`).
   - Тестовые данные генерируются фабриками (`ProductDataFactory`, `UserDataFactory`).

2. **Спецификации запросов и ответов (`RequestSpecification`, `ResponseSpecification`)**:
   - `RequestSpecs.defaultRequestSpec()` централизованно задает Base URI, Content-Type, Accept, Allure-фильтр и логирование.
   - `ResponseSpecs` содержит переиспользуемые шаблоны проверок статус-кодов (`statusOk()`, `statusCreated()`, `statusNotFound()`, `entityDeleted()`).

3. **Типизированные DTO (Data Transfer Objects)**:
   - Все тела запросов и ответов представлены Java-классами с аннотациями Jackson (`@JsonIgnoreProperties(ignoreUnknown = true)`, `@JsonInclude(NON_NULL)`) и Lombok (`@Builder(toBuilder = true)`).
   - Исключена хрупкая ручная конкатенация JSON-строк.

4. **Полная интеграция с Allure**:
   - Фильтр `AllureRestAssured` автоматически прикрепляет все отправленные запросы, заголовки, параметры и полученные ответы в отчет Allure в виде интерактивных HTML-вкладок.
   - Все методы клиентов и валидаций снабжены аннотацией `@Step("...")`.
   - Тесты размечены аннотациями `@Epic`, `@Feature`, `@Story`, `@Owner`, `@Severity`, `@DisplayName`, `@Description`, `@Tag`.

5. **Кастомные Fluent Assertions (AssertJ)**:
   - Созданы `ProductAssert` и `UserAssert`, позволяющие писать декларативные, понятные проверки в стиле `assertThatProduct(product).hasTitle("...").hasPrice(10.0);`.

6. **Гибкая конфигурация (Owner)**:
   - Все параметры (URL окружения, таймауты, флаги логирования) вынесены в `config.properties` и могут быть переопределены через аргументы командной строки (`-Dbase.url=...`) или переменные окружения.

7. **Параллельное выполнение**:
   - В `junit-platform.properties` настроено параллельное выполнение тестовых классов и методов (`concurrent`), что ускоряет прогон регрессионного набора.

---

## 📁 Структура проекта

```
.github/
└── workflows/
    ├── pr-checks.yml                     # PR проверки (сборка, smoke suite, step summary)
    ├── regression-and-reporting.yml      # Регрессия (API + DB) + деплой Allure на GitHub Pages
    ├── manual-test-run.yml               # Параметризованный запуск по требованию
    └── security-and-dependency-scan.yml  # Сканирование безопасности и зависимостей Maven
src
├── main
│   └── java
│       └── org
│           └── example
│               └── api
│                   ├── assertions         # Кастомные AssertJ ассёрты (ProductAssert, UserAssert)
│                   ├── clients            # Service API клиенты (BaseClient, ProductClient, UserClient)
│                   ├── config             # Конфигурация проекта (ProjectConfig, ConfigManager)
│                   ├── data               # Фабрики генерации тестовых данных (DataFaker)
│                   ├── database           # База данных (DatabaseManager, TestcontainersManager)
│                   │   └── models         # Entity Records (UserRecord, ProductRecord, OrderRecord)
│                   ├── filters            # Фильтры логирования REST Assured
│                   ├── models             # DTO модели (Product, User, ErrorResponse)
│                   └── specifications     # Спецификации запросов и ответов REST Assured
└── test
    ├── java
    │   └── org
    │       └── example
    │           └── api
    │               └── tests              # Тестовые классы (BaseTest, ProductCrudTest, DatabaseIntegrationTest)
    └── resources
        ├── db/
        │   ├── init-schema.sql            # DDL: users, products, orders таблицы
        │   └── seed-data.sql              # DML: тестовые начальные данные
        ├── allure.properties              # Настройки генерации Allure
        ├── categories.json                # Категоризация дефектов Allure
        ├── config.properties              # Параметры тестового окружения и БД
        ├── junit-platform.properties      # Конфигурация многопоточности JUnit 5
        └── logback-test.xml               # Конфигурация логирования Logback
```

---

## 🧪 Покрытие автотестами (CRUD Operations)

Фреймворк содержит автотесты для двух независимых сервисов с позитивными, негативными и параметризованными проверками:

### 1. Products Management (`ProductCrudTest`):
- **Create (POST)**:
  - `POST /products/add` — Создание товара с полным набором атрибутов (цена, габариты, скидка, бренд, описание).
  - `POST /products/add` (Parameterized) — Создание товара с минимальным набором данных.
- **Read (GET)**:
  - `GET /products/{id}` — Получение существующего товара по ID.
  - `GET /products` — Получение пагинированного списка с параметрами `limit` и `skip`.
  - `GET /products/search` — Поиск товаров по ключевому слову (`?q=phone`).
  - `GET /products/{id}` (Negative) — Запрос несуществующего ID (проверка 404 Not Found и тела ошибки).
- **Update (PUT / PATCH)**:
  - `PUT /products/{id}` — Полное обновление сущности.
  - `PATCH /products/{id}` — Частичное обновление конкретного поля (title).
  - `PUT /products/{id}` (Negative) — Обновление несуществующего товара (404).
- **Delete (DELETE)**:
  - `DELETE /products/{id}` — Успешное удаление товара (проверка `isDeleted: true` и метки времени).
  - `DELETE /products/{id}` (Negative) — Удаление несуществующего товара (404).

### 2. Users Management (`UserCrudTest`):
- **Create (POST)**:
  - `POST /users/add` — Создание пользователя с полным профилем (адрес, компания, учетные данные).
  - `POST /users/add` (Parameterized) — Создание пользователя с базовыми полями.
- **Read (GET)**:
  - `GET /users/{id}` — Получение профиля пользователя по ID.
  - `GET /users` — Получение списка пользователей с пагинацией.
  - `GET /users/{id}` (Negative) — Запрос несуществующего пользователя (404).
- **Update (PUT / PATCH)**:
  - `PUT /users/{id}` — Полное обновление данных пользователя.
  - `PATCH /users/{id}` — Частичное обновление email.
- **Delete (DELETE)**:
  - `DELETE /users/{id}` — Удаление пользователя (проверка флага удаления).
  - `DELETE /users/{id}` (Negative) — Удаление несуществующего пользователя (404).

### 3. Database Testing (`DatabaseIntegrationTest`):
- **Схема и данные (`init-schema.sql`, `seed-data.sql`)**:
  - Таблицы: `users`, `products`, `orders` с foreign keys и каскадным удалением.
- **Сценарии проверок**:
  - `shouldVerifyDatabaseConnectivityAndSeedData` — проверка подключения и количества записей в таблицах.
  - `shouldQueryUserByUsernameSuccessfully` — выборка пользователя по username и проверка полей типизированной модели `UserRecord`.
  - `shouldInsertNewProductAndVerifyRecord` — вставка нового товара, проверка сгенерированного ID и валидация через `ProductRecord`.
  - `shouldUpdateUserStatusSuccessfully` — изменение статуса пользователя и проверка сохранения состояния.
  - `shouldQueryUserOrdersAndVerifyTotals` — выборка связанных заказов пользователя с расчетом и проверкой итоговой суммы.
  - `shouldCascadeDeleteOrdersWhenUserIsDeleted` — удаление пользователя с проверкой каскадного удаления заказов.

---

## 🗄️ Тестирование базы данных (PostgreSQL & Testcontainers)

### 1. Архитектура подключения
- `DatabaseManager` использует высокопроизводительный пул соединений **HikariCP** с ленивой инициализацией.
- При включенном `db.use.testcontainers=true` (по умолчанию) запускается легковесный контейнер `postgres:16-alpine`.
- Для подключения к внешней/локальной БД достаточно выставить `db.use.testcontainers=false` и указать `db.url`, `db.user`, `db.password`.

### 2. Конфигурационные параметры (`config.properties`):
```properties
db.url=jdbc:postgresql://localhost:5432/testdb
db.user=postgres
db.password=postgres
db.driver=org.postgresql.Driver
db.pool.size=10
db.use.testcontainers=true
```

---

## 🚀 On-Demand CI/CD Конвейеры (GitHub Actions)

В проекте реализованы модульные воркфлоу, запускаемые строго **по требованию (on-demand)**:

1. **`pr-checks.yml` (PR Validation & Smoke Tests)**:
   - Срабатывает при создании/обновлении Pull Request в `main`/`master`.
   - Запускает компиляцию и дымовые тесты (`-Dgroups="smoke"`).
   - Формирует и публикует Markdown-сводку в GitHub Step Summary.
2. **`regression-and-reporting.yml` (Regression Suite & Allure Reporting)**:
   - Срабатывает при пуше в `main` или по ручному вызову (`workflow_dispatch`).
   - Запускает полный набор тестов (API + DB с Testcontainers).
   - Подтягивает историю предыдущих прогонов из ветки `gh-pages`, генерирует свежий Allure-отчет и деплоит его на GitHub Pages.
3. **`manual-test-run.yml` (Parameterized On-Demand Test Run)**:
   - Запуск вручную через интерфейс GitHub Actions с параметрами: окружение (`staging`, `dev`, `prod`), теги JUnit (`regression`, `smoke`, `db`, `products`, `users`), число потоков параллелизации и базовый URL.
4. **`security-and-dependency-scan.yml` (Security & Dependency Audit)**:
   - Запуск по требованию и при изменении `pom.xml` для проверки дерева зависимостей и поиска обновлений.

---

## 🚀 Запуск автотестов

### 1. Запуск всех тестов:
```bash
mvn clean test
```

### 2. Запуск конкретного тестового класса:
```bash
mvn test -Dtest=ProductCrudTest
```

### 3. Запуск тестов по тегам JUnit 5:
```bash
# Запуск только тестов товаров:
mvn test -Dgroups=products

# Запуск только тестов пользователей:
mvn test -Dgroups=users

# Запуск тестов базы данных (PostgreSQL / Testcontainers):
mvn test -Dgroups=db

# Запуск регрессионного набора:
mvn test -Dgroups=regression
```

### 4. Запуск с переопределением базового URL (например, для другого стенда / мок-сервера):
```bash
mvn test -Dbase.url=https://dummyjson.com
```

---

## 📊 Формирование и просмотр отчетов Allure

### 1. Генерация HTML-отчета:
```bash
mvn allure:report
```
*Сгенерированный статический отчет будет доступен в директории `target/site/allure-maven-plugin`.*

### 2. Открытие интерактивного отчета в браузере (Allure Web Server):
```bash
mvn allure:serve
```

---

## 💡 Как расширять проект (Add new API)

1. **Создать DTO** в пакете `org.example.api.models.<entity>` с аннотациями `@Data`, `@Builder`, `@JsonIgnoreProperties(ignoreUnknown = true)`.
2. **Создать клиент** в пакете `org.example.api.clients`, унаследовав от `BaseClient`, с аннотациями `@Step`.
3. **Создать Data Factory** в пакете `org.example.api.data` для удобной генерации данных через `DataFaker`.
4. **Создать тест** в пакете `org.example.api.tests`, унаследовав от `BaseTest`, добавив Allure-аннотации (`@Epic`, `@Feature`, `@Story`, `@Severity`, `@DisplayName`).
