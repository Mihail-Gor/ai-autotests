# Production-Ready REST API Test Automation Framework

Проект представляет собой образец построения современного фреймворка автоматизации тестирования REST API на стеке **Java 21 + REST Assured + JUnit 5 + Allure**, спроектированный с учетом актуальных Best Practices, применимых в реальных коммерческих проектах.

В качестве тестируемого сервиса используется стабильный публичный REST API **[DummyJSON](https://dummyjson.com/)**, предоставляющий полноценный функционал для CRUD операций над сущностями (товары, пользователи и др.).

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
| **DataFaker 2.3.1** | Генерация реалистичных тестовых данных |
| **SLF4J + Logback 1.5.7** | Логирование запросов, ответов и системных событий |
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
│                   ├── filters            # Фильтры логирования REST Assured
│                   ├── models             # DTO модели (Product, User, ErrorResponse)
│                   └── specifications     # Спецификации запросов и ответов REST Assured
└── test
    ├── java
    │   └── org
    │       └── example
    │           └── api
    │               └── tests              # Тестовые классы (BaseTest, ProductCrudTest, UserCrudTest)
    └── resources
        ├── allure.properties              # Настройки генерации Allure
        ├── categories.json                # Категоризация дефектов Allure
        ├── config.properties              # Параметры тестового окружения
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
