## Шаг 0: Быстрый старт
``` bash
cd frontend && npm install && npm run build --configuration frontend && cd .. && mvn clean package -Dmaven.test.skip=true
```

1. Запуск сервера:
``` bash
set -a; source .env; set +a; java -jar target/server-0.0.1.jar
```

2. Запуск фронта:
```bash
cd frontend/ && npm start
```

3. Запуск бота
```bash
set -a; source .env; set +a; mvn compile exec:java
```

- Приложение доступно на [https://chessbratchikov.ru](https://chessbratchikov.ru)

Должен быть .env файл, в котором:
- DB_USERNAME=
- DB_PASSWORD=
- BOT_TOKEN=