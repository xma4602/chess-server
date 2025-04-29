## 🔧 Шаг 1: Собери фронт и бэк как обычные приложения

---

### 📦 Сборка Angular

Перейди в `frontend/` и собери Angular-приложение:

```bash
cd frontend
npm install
npm run build --configuration frontend
```

У тебя появится папка `dist/<название-проекта>/`, которую можно будет отдавать через Nginx.

---

### ☕️ Сборка Spring Boot (Maven)

Перейди в корень проекта (там где `pom.xml`) и собери `.jar`:
```bash
mvn clean package -Dmaven.test.skip=true
```


## 🚀 Шаг 2: Сборка и запуск сервера

В корне проекта для запуска введи:
```bash
docker build -t server . && docker run --env-file .env -p 8080:8080 server
```

## 🚀 Шаг 3: Сборка и запуск фронта

В `frontend/`  для запуска введи:
```bash
npm start
```

После этого:
- Приложение доступно на [http://localhost:4200/](http://localhost:4200/)
