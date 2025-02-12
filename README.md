## Run locally

### Database configuration

User Service uses Postgres. You can configure the database connection by specifying your `spring.datasource.url`,
`spring.datasource.username`, `spring.datasource.password` in `application.yaml`.

### ActiveMQ configuration

Also, User Service uses ActiveMQ for messaging. You can configure the queue connection by specifying your `spring.activemq.broker-url` in `application.yaml`.

### Docker

If you have Docker installed, you can use the provided `docker-compose.yml` file to start required containers.

To start Postgres and ActiveMQ containers using Docker, run the following command:

```bash
docker compose up
```

### Build & Start

Execute script to build and start User Service locally:
```bash
git clone https://github.com/Born2Flex/user.git
cd interview
./mvnw package
java -jar target/*.jar
```
Once the application started, you can access the Interview Service API at <http://localhost:8080/swagger-ui/index.html>.

### File links
|Spring Boot Configuration | Class or Java property files                                                                 |
|--------------------------|----------------------------------------------------------------------------------------------|
|The Main Class | [UserApplication](src/main/java/ua/edu/internship/user/UserApplication.java) |
|Properties Files | [application.yaml](src/main/resources/application.yaml)                                      |