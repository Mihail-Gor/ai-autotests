# Production-Ready Full-Stack Test Automation Framework (API, UI & Database)

![Java 21](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![Playwright](https://img.shields.io/badge/Playwright-1.49.0-brightgreen?logo=playwright)
![REST Assured](https://img.shields.io/badge/REST_Assured-5.5.0-green)
![JUnit 5](https://img.shields.io/badge/JUnit-5.11.0-blue?logo=junit5)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?logo=postgresql)
![Testcontainers](https://img.shields.io/badge/Testcontainers-1.20.1-brightgreen)
![WireMock](https://img.shields.io/badge/WireMock-3.9.1-red)
![Allure Report](https://img.shields.io/badge/Allure-2.29.0-yellow?logo=qameta)
![CI/CD](https://img.shields.io/badge/GitHub_Actions-On--Demand-blue?logo=githubactions)

Проект представляет собой образец построения современного масштабируемого фреймворка автоматизации тестирования **UI (Web E2E)**, **REST API**, **сервисной виртуализации (мокирования)** и **интеграционного тестирования баз данных** на стеке:
**Java 21 + Playwright + REST Assured + JUnit 5 + Allure + PostgreSQL (Testcontainers & HikariCP) + WireMock + Owner**, спроектированный с учетом актуальных Best Practices и готовый для переиспользования в Enterprise-проектах.

---

## 🎯 Тестовые полигоны и стенды

1. **UI Web E2E Тестирование**: **[SauceDemo (Swag Labs)](https://www.saucedemo.com/)**
   - Общепризнанный эталонный тестовый полигон для UI автотестов.
   - Покрывает реальные сценарии: многопользовательская авторизация (различные персоны: standard, locked_out, problem, error), каталог товаров, сортировка по имени и цене, карточка товара, добавление/удаление из корзины с отслеживанием бейджей, многошаговый чекаут (Checkout Step One -> Overview с расчетом налогов и итоговой стоимости -> Complete) и обработка валидационных ошибок.
   - Все элементы имеют стабильные `data-test` атрибуты (Best Practice для Playwright).
2. **REST API Тестирование**: **[DummyJSON](https://dummyjson.com/)**
   - Полнофункциональный публичный REST-сервис с ресурсами `products` и `users`, пагинацией, поиском, валидациями и CRUD-операциями.
3. **Database & Transaction Testing**: Изолированный контейнер **PostgreSQL 16** на базе **Testcontainers**.
4. **Service Virtualization**: **WireMock** для эмуляции внешних шлюзов, задержек сети, сбоев 500/429 и стейт-машин.

---

## 🛠 Технологический стек

| Технология | Назначение |
|---|---|
| **Java 21 (LTS)** | Язык разработки |
| **Playwright 1.49.0** | Высокопроизводительный современный движок для автоматизации браузеров (Chromium, Firefox, WebKit) |
| **REST Assured 5.5.0** | Клиент и библиотека для выполнения и валидации HTTP-запросов |
| **JUnit 5 (Jupiter) 5.11.0** | Тестовый фреймворк (многопоточный параллельный запуск, параметризация, extensions) |
| **Allure Report 2.29.0** | Интерактивные HTML-отчеты с аттачами запросов/ответов, скриншотов, HTML-исходников и Playwright Trace |
| **AspectJ Weaver 1.9.22** | Байткод-инструментация для Allure `@Step` шагов |
| **AssertJ 3.26.3** | Fluent assertions библиотека и кастомные типобезопасные ассёрты |
| **Lombok 1.18.34** | Генерация boilerplate кода (`@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`) |
| **Jackson 2.17.2** | JSON сериализация и десериализация (SerDe) |
| **Owner 1.0.12** | Управление конфигурацией (типобезопасные интерфейсы, properties, env, cli-аргументы) |
| **PostgreSQL JDBC 42.7.4** | Драйвер подключения к БД PostgreSQL |
| **HikariCP 5.1.0** | Высокопроизводительный пул соединений к БД |
| **Testcontainers 1.20.1** | Автоматический подъем герметичного контейнера PostgreSQL 16 в тестах |
| **WireMock 3.9.1** | Сервисная виртуализация, эмуляция HTTP API, задержек сети и сбоев |
| **DataFaker 2.3.1** | Генерация реалистичных тестовых данных |
| **SLF4J + Logback 1.5.7** | Логирование запросов, ответов и системных событий |
| **GitHub Actions** | On-Demand CI/CD конвейеры, валидация PR и деплой отчетов Allure на GitHub Pages |
| **Maven** | Сборка проекта и управление зависимостями |

---

## 🏛 Архитектурные паттерны и Best Practices

### 1. UI Автоматизация (Playwright Best Practices):
- **Page Object Model (POM) & Component Object Model (COM)**:
  - Четкое разделение страниц (`LoginPage`, `InventoryPage`, `ProductDetailsPage`, `CartPage`, `CheckoutStepOnePage`, `CheckoutStepTwoPage`, `CheckoutCompletePage`) и переиспользуемых компонентов (`HeaderComponent`, `SidebarMenuComponent`, `ProductCardComponent`).
- **Thread-Safety и Параллельное выполнение**:
  - `PlaywrightDriverManager` хранит `Playwright`, `Browser`, `BrowserContext`, `Page` в `ThreadLocal`, обеспечивая 100% изоляцию тестов при параллельном выполнении JUnit 5.
- **Автоматический сбор артефактов при падениях (`PlaywrightAllureExtension`)**:
  - При падении теста в Allure автоматически прикрепляются:
    1. Полностраничный скриншот страницы (`Failure Screenshot`).
    2. Полный HTML-код страницы в момент сбоя (`Page HTML Source`).
    3. Полный архив трассировки Playwright (`Playwright Trace - trace.zip`) для пошагового воспроизведения в `playwright show-trace`.
- **User-Facing & Robust Locators**:
  - Использование стабильных атрибутов `data-test` (`page.locator("[data-test='...']")`) и селекторов `getByTestId`, исключающих хрупкость верстки.
- **Кастомные Fluent Assertions**:
  - `InventoryAssert.assertThatInventory(page).hasProductCount(6).hasProductsSortedByPriceAscending();`
  - `CartAssert.assertThatCart(page).hasItemCount(2).containsProduct("Sauce Labs Backpack");`

```
                  ┌─────────────────────────────────────────┐
                  │              UI Test Suites             │
                  │   (LoginUiTest, CheckoutE2ETest, ...)   │
                  └────────────────────┬────────────────────┘
                                       │ Uses
                  ┌────────────────────▼────────────────────┐
                  │               Page Objects              │
                  │     (LoginPage, InventoryPage, ...)     │
                  └───────┬─────────────────────────┬───────┘
                          │                         │ Aggregates
                          │ Uses                    ▼
                          │             ┌───────────────────────┐
                          │             │       Components      │
                          │             │ (Header, Sidebar, ...)│
                          │             └───────────┬───────────┘
                          │                         │
                          ▼                         ▼
                  ┌─────────────────────────────────────────┐
                  │          Playwright Driver Manager      │
                  │  (ThreadLocal Context, Page, Browser)   │
                  └────────────────────┬────────────────────┘
                                       │
                                       ▼
                  ┌─────────────────────────────────────────┐
                  │       Playwright Browser Engine         │
                  │      (Chromium / Firefox / WebKit)      │
                  └─────────────────────────────────────────┘
```

---

## 📁 Структура проекта

```
.github/
└── workflows/
    ├── pr-checks.yml                     # PR проверки (сборка, smoke suite, Playwright browser install)
    ├── regression-and-reporting.yml      # Регрессия (API + DB + UI) + деплой Allure на GitHub Pages
    ├── manual-test-run.yml               # Параметризованный запуск по требованию с выбором браузера и тегов
    └── security-and-dependency-scan.yml  # Сканирование безопасности и зависимостей Maven
src
├── main
│   └── java
│       └── org
│           └── example
│               ├── api
│               │   ├── assertions         # Кастомные AssertJ ассёрты (ProductAssert, UserAssert)
│               │   ├── clients            # Service API клиенты (BaseClient, ProductClient, UserClient)
│               │   ├── config             # Конфигурация проекта (ProjectConfig, ConfigManager)
│               │   ├── data               # Фабрики генерации данных (DataFaker)
│               │   ├── database           # База данных (DatabaseManager, TestcontainersManager)
│               │   │   └── models         # Entity Records (UserRecord, ProductRecord, OrderRecord)
│               │   ├── filters            # Фильтры логирования REST Assured
│               │   ├── models             # DTO модели (Product, User, ErrorResponse)
│               │   └── specifications     # Спецификации запросов и ответов REST Assured
│               └── ui
│                   ├── assertions         # Кастомные AssertJ ассёрты UI (InventoryAssert, CartAssert)
│                   ├── components         # UI компоненты (HeaderComponent, SidebarMenuComponent, ProductCardComponent)
│                   ├── core               # Драйвер Playwright, фабрика браузеров (BrowserFactory, PlaywrightDriverManager)
│                   ├── data               # Данные UI тестов (UserCredentials, SortOption, CheckoutInfo)
│                   └── pages              # Page Objects (LoginPage, InventoryPage, CartPage, Checkout pages)
└── test
    ├── java
    │   └── org
    │       └── example
    │           ├── api
    │           │   ├── mock               # Управление мок-сервером (WireMockManager)
    │           │   └── tests              # API Тесты (ProductCrudTest, UserCrudTest, DatabaseIntegrationTest, WireMockIntegrationTest)
    │           └── ui
    │               ├── core               # Test Watcher & Allure Extensions (PlaywrightAllureExtension)
    │               └── tests              # UI Тесты (LoginUiTest, InventoryUiTest, CartUiTest, CheckoutE2ETest)
    └── resources
        ├── db/
        │   ├── init-schema.sql            # DDL: users, products, orders таблицы
        │   └── seed-data.sql              # DML: тестовые начальные данные
        ├── allure.properties              # Настройки генерации Allure
        ├── categories.json                # Категоризация дефектов Allure
        ├── config.properties              # Параметры окружения (API, DB, Playwright UI)
        ├── junit-platform.properties      # Конфигурация параллелизма JUnit 5
        └── logback-test.xml               # Конфигурация логирования Logback
```

---

## 🧪 Наборы автотестов

### 1. UI Web Tests (`org.example.ui.tests`):
- **`LoginUiTest`**:
  - Успешный вход пользователя (`standard_user`) и переход в каталог.
  - Проверка блокировки учетной записи (`locked_out_user`) с валидацией баннера ошибки.
  - Параметризованные проверки некорректных учетных данных и пустых полей.
  - Полный сценарий логаута через боковое меню и возврат на страницу входа.
- **`InventoryUiTest`**:
  - Отображение полного каталога (6 товаров с описанием, ценами и кнопками).
  - Сортировка каталога по всем направлениям (A-Z, Z-A, Price Low-High, Price High-Low).
  - Навигация в карточку товара и возврат назад в каталог.
- **`CartUiTest`**:
  - Добавление одного и нескольких товаров в корзину с проверкой бейджа счетчика.
  - Удаление товаров из каталога и непосредственно со страницы корзины.
  - Навигация «Continue Shopping» обратно в каталог.
- **`CheckoutE2ETest`**:
  - Полный сквозной End-to-End процесс покупки: авторизация -> добавление в корзину -> переход в чекаут -> ввод данных покупателя (с генерацией через DataFaker) -> проверка налогов, сабтотала и итоговой суммы -> подтверждение заказа -> проверка экрана завершения заказа.
  - Валидационные проверки обязательных полей формы чекаута (First Name, Last Name, Postal Code).
  - Отмена оформления на шагах Step 1 и Step 2 с возвратом состояния.

### 2. REST API Tests (`ProductCrudTest`, `UserCrudTest`):
- Полное CRUD покрытие: `POST`, `GET`, `PUT`, `PATCH`, `DELETE` с позитивными, негативными (404/400) и параметризованными проверками.

### 3. Database Integration Tests (`DatabaseIntegrationTest`):
- Проверка подключений, транзакций, вставки, обновления и каскадного удаления в PostgreSQL с Testcontainers.

### 4. Service Virtualization (`WireMockIntegrationTest`):
- Эмуляция внешних платежных шлюзов, вебхуков, ошибок 500, Rate Limiting 429, задержек сети и стейт-машин.

---

## ⚙️ Конфигурация окружения (`config.properties`)

```properties
# API Configuration
base.url=https://dummyjson.com
logging.enabled=true
api.timeout.ms=10000

# Database Configuration
db.url=jdbc:postgresql://localhost:5432/testdb
db.user=postgres
db.password=postgres
db.driver=org.postgresql.Driver
db.pool.size=10
db.use.testcontainers=true

# UI (Playwright) Configuration
ui.base.url=https://www.saucedemo.com
ui.browser=chromium
ui.headless=true
ui.slow.mo=0
ui.timeout.ms=15000
ui.tracing.enabled=true
ui.video.enabled=false
ui.screenshot.on.failure=true
ui.page.source.on.failure=true
```

Все параметры могут быть переопределены через `-Dkey=value` или системные переменные окружения.

---

## 🚀 Запуск автотестов

### 1. Запуск UI тестов:
```bash
# Запуск всех UI тестов
mvn test -Dgroups="ui"

# Запуск UI Smoke набора
mvn test -Dgroups="ui & smoke"

# Запуск в видимом режиме браузера (Headed mode)
mvn test -Dgroups="ui" -Dui.headless=false

# Запуск в другом браузере (Firefox / WebKit)
mvn test -Dgroups="ui" -Dui.browser=firefox
```

### 2. Запуск полного регрессионного набора (API + DB + UI):
```bash
mvn clean test -Dgroups="regression"
```

### 3. Запуск конкретного тестового класса:
```bash
mvn test -Dtest=CheckoutE2ETest
```

---

## 📊 Формирование и просмотр отчетов Allure

### 1. Генерация HTML-отчета:
```bash
mvn allure:report
```

### 2. Запуск локального сервера Allure для просмотра отчета:
```bash
mvn allure:serve
```

---

## 🏢 Переиспользование на Enterprise-проекте

1. **Замена базового URL и локаторов**:
   - В `config.properties` укажите `ui.base.url` вашего enterprise-приложения.
   - Добавьте новые Page Objects в `org.example.ui.pages`, унаследовав от `BasePage`.
2. **Аутентификация и Cookie/State Storage**:
   - Playwright поддерживает сохранение состояния сессии (storage state). Вы можете использовать `context.storageState(...)` для переиспользования авторизованной сессии между тестами.
3. **CI/CD Внедрение**:
   - Готовые воркфлоу `.github/workflows/regression-and-reporting.yml` и `manual-test-run.yml` уже настроены на установку Playwright браузеров и генерацию Allure отчетов с публикацией в GitHub Pages и артефакты.
