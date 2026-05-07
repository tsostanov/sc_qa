# SoundCloud Functional Tests

Автоматизированные функциональные UI-тесты для сайта [SoundCloud](https://soundcloud.com/) на Java, Selenium WebDriver и JUnit 5.

## О проекте

Проект покрывает основные пользовательские сценарии SoundCloud без выполнения необратимых действий и без использования реальных учетных записей.

Текущий набор тестов проверяет:

- открытие главной страницы;
- доступность поиска;
- переход на страницу результатов поиска;
- наличие результатов по поисковому запросу;
- открытие релевантного трека из выдачи;
- запуск воспроизведения;
- постановку трека на паузу;
- переход на страницу исполнителя;
- открытие формы `Share`;
- переход к странице `Upload`;
- требование авторизации для `Upload`;
- открытие формы входа;
- отказ во входе с некорректными данными.

## Технологии

- Java 17
- Maven
- JUnit 5
- Selenium WebDriver
- Google Chrome
- Mozilla Firefox
- XPath
- Page Object

## Структура проекта

```text
soundcloud-functional-tests
├── pom.xml
├── README.md
├── report.md
└── src
    └── test
        ├── java
        │   └── org
        │       └── example
        │           └── soundcloud
        │               ├── core
        │               ├── pages
        │               └── tests
        └── resources
            └── junit-platform.properties
```

## Запуск тестов

Все тесты:

```bash
mvn test
```

Только Chrome:

```bash
mvn test -Dbrowser=chrome
```

Только Firefox:

```bash
mvn test -Dbrowser=firefox
```

Chrome и Firefox параллельно:

```bash
mvn test -Dbrowser=all
```

Headless-режим:

```bash
mvn test -Dbrowser=chrome -Dheadless=true
```

Полный параллельный прогон в headless-режиме:

```bash
mvn clean test -Dbrowser=all -Dheadless=true
```

## Ограничения

- Тесты работают с внешним публичным сайтом, поэтому локаторы могут потребовать обновления при изменении DOM.
- Все элементы ищутся через XPath.
- Используются явные ожидания `WebDriverWait`.
- Реальные аккаунты и загрузка файлов не используются.
- Для запуска нужны установленные браузеры и доступный в `PATH` Maven.

## Отчет

Полный шаблон отчета для лабораторной работы вынесен в [report.md](report.md).

## Use Case Diagram

```plantuml
@startuml
left to right direction

actor "Пользователь" as User

rectangle "SoundCloud" {
    usecase "Открыть главную страницу" as UC1
    usecase "Найти трек" as UC2
    usecase "Открыть страницу результата" as UC3
    usecase "Воспроизвести трек" as UC4
    usecase "Поставить трек на паузу" as UC5
    usecase "Открыть страницу исполнителя" as UC6
    usecase "Открыть форму Share" as UC7
    usecase "Открыть форму входа" as UC8
    usecase "Выполнить вход с некорректными данными" as UC9
    usecase "Перейти к Upload" as UC10
    usecase "Получить требование авторизации" as UC11
}

User --> UC1
User --> UC2
User --> UC3
User --> UC4
User --> UC5
User --> UC6
User --> UC7
User --> UC8
User --> UC10

UC2 ..> UC3 : <<include>>
UC3 ..> UC4 : <<extend>>
UC4 ..> UC5 : <<extend>>
UC3 ..> UC6 : <<extend>>
UC3 ..> UC7 : <<extend>>
UC8 ..> UC9 : <<include>>
UC10 ..> UC11 : <<include>>

@enduml
```
