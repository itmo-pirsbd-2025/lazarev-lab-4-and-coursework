# CAS — Central Access Service (SSO/RBAC) для поддоменов в Docker

Проект поднимает локальный “домен” с поддоменами и закрывает доступ к сервисам через единый CAS.
Доступ к поддоменам возможен только после авторизации и при наличии прав.

Стек: Nginx (ForwardAuth) + Spring Boot + Postgres + Grafana + Prometheus

## Домен и поддомены (без hosts)
Используется `localtest.me` (и `*.localtest.me`), который резолвится в `127.0.0.1`.

- http://cas.localtest.me — CAS (логин/ЛК/админка)
- http://grafana.localtest.me — Grafana (закрыта CAS)
- http://whoami.localtest.me — тестовый сервис (закрыт CAS)
- http://prom.localtest.me — Prometheus (закрыт CAS)

## Быстрый старт
```bash
docker compose up -d --build
```

1) Открой http://cas.localtest.me
2) Открой http://grafana.localtest.me — пустит только при наличии прав

## Демо-пользователи (создаются автоматически)
- `admin / admin` (роль `ADMIN`)
- `user / user` (роль `USER`)


## Админ-панель
- http://cas.localtest.me/admin/services — добавить сервис (host)
- http://cas.localtest.me/admin/permissions — выдать доступ роли на сервис
- http://cas.localtest.me/ — личный кабинет (список доступных сервисов)

## Как работает защита поддоменов
Nginx для каждого запроса к защищённому хосту вызывает CAS:

`GET http://cas/auth/verify`

CAS проверяет:
- сессию пользователя (cookie домена `.localtest.me`)
- права на `X-Forwarded-Host`

Возвращает 200 / 401 / 403.

## OIDC (проверка что поднят)
- http://cas.localtest.me/.well-known/openid-configuration  
Issuer: `http://cas.localtest.me`

## Остановка
```bash
docker compose down -v
```

## Профилирование и бенчмарки

### JMH (микробенчмарки)
Бенчмарки лежат в `cas/src/jmh/java`.

Сборка и запуск:
```bash
cd cas
mvn -q -Pjmh -DskipTests package
java -jar target/cas-jmh.jar -bm avgt -tu ns -wi 5 -i 10
```

