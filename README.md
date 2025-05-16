## Шаг 0: Быстрый старт
cd frontend && npm install && npm run build --configuration frontend && cd .. && mvn clean package -Dmaven.test.skip=true

1. Запуск сервера:
``` bash
set -a; source .env; set +a; java -jar target/server-0.0.1.jar
```

2. Запуск фронта:
```bash
cd frontend/ && npm start
```
- Приложение доступно на [http://localhost:4200/](http://localhost:4200/)

Должен быть .env файл, в котором:
- DB_USERNAME=
- DB_PASSWORD=