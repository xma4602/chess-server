## 🔧 Шаг 1: Собери фронт и бэк как обычные приложения

---

### 📦 Сборка Angular

Перейди в `frontend/` и собери Angular-приложение:

```bash
cd frontend
npm install
ng build --configuration frontend
```

У тебя появится папка `dist/<название-проекта>/`, которую можно будет отдавать через Nginx.

---

### ☕️ Сборка Spring Boot (Maven)

Перейди в корень проекта (там где `pom.xml`) и собери `.jar`:
```bash
mvn clean package -Dmaven.test.skip=true
```


## 🚀 Шаг 2: Сборка и запуск

В корне проекта для сборки введи:
```bash
docker-compose build
```
В корне проекта для запуска введи:
```bash
docker-compose up
```

После этого:
- Angular-приложение доступно на [http://localhost](http://localhost)
- Spring Boot работает на [http://localhost:8080](http://localhost:8080)
- Все запросы типа `/api/...` из Angular будут идти в Spring Boot
