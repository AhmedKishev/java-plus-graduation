# Explore With Me Plus
Доработанная версия приложения Explore With Me (https://github.com/AhmedKishev/java-explore-with-me-plus-group-project). Переработана структура приложения, использован переход из модульно-монолитной системы к микросервисной, реализовано перемещение к облачным средам (Spring Cloud) из за необходимости быстрого запуска и обновления приложения, а также добавлен броккер (Kafka) для масштабирования. Также реализована новая функциональность, связанная с рекомендациями для улучшения аналитической системы. 

## Languages and tools: 
<p style="display: flex; gap: 10px; flex-wrap: wrap;">
  <img width="100" height="100" alt="Java" src="https://raw.githubusercontent.com/devicons/devicon/54cfe13ac10eaa1ef817a343ab0a9437eb3c2e08/icons/java/java-original-wordmark.svg" />
  <img width="100" height="100" alt="Spring" src="https://raw.githubusercontent.com/devicons/devicon/54cfe13ac10eaa1ef817a343ab0a9437eb3c2e08/icons/spring/spring-original-wordmark.svg" />
  <img width="100" height="100" alt="IntelliJ" src="https://raw.githubusercontent.com/devicons/devicon/54cfe13ac10eaa1ef817a343ab0a9437eb3c2e08/icons/intellij/intellij-original.svg" />
  <img width="100" height="100" alt="PostgreSQL" src="https://raw.githubusercontent.com/devicons/devicon/54cfe13ac10eaa1ef817a343ab0a9437eb3c2e08/icons/postgresql/postgresql-original-wordmark.svg" />
  <img width="100" height="100" alt="Kafka" src="https://raw.githubusercontent.com/devicons/devicon/54cfe13ac10eaa1ef817a343ab0a9437eb3c2e08/icons/apachekafka/apachekafka-original.svg" />
  <img width="100" height="100" alt="Maven" src="https://raw.githubusercontent.com/devicons/devicon/54cfe13ac10eaa1ef817a343ab0a9437eb3c2e08/icons/maven/maven-original.svg" />
  <img width="100" height="100" alt="gRPC" src="https://raw.githubusercontent.com/devicons/devicon/54cfe13ac10eaa1ef817a343ab0a9437eb3c2e08/icons/grpc/grpc-plain.svg" />
  <img width="100" height="100" alt="Docker" src="https://raw.githubusercontent.com/devicons/devicon/54cfe13ac10eaa1ef817a343ab0a9437eb3c2e08/icons/docker/docker-plain.svg" />
  <img width="100" height="100" alt="Docker" src="https://raw.githubusercontent.com/devicons/devicon/54cfe13ac10eaa1ef817a343ab0a9437eb3c2e08/icons/postman/postman-original.svg" />
  <img width="100" height="100" alt="Docker" src="https://raw.githubusercontent.com/devicons/devicon/54cfe13ac10eaa1ef817a343ab0a9437eb3c2e08/icons/swagger/swagger-original.svg" />
</p>

# Модуль infa 
Данный модуль содержит всю инфраструктуру приложения, включая входной шлюз (`Gateway`), конфигурационные файлы (`Config`) и реестр сервисов (`Eureka`).   
## Подмодуль discovery-server
Данный подмодуль является реестром сервисов и, соответственно, реализует паттерн `Service Discovery`. Другие модули регистрируются в него и могут вызывать друг друга по имени приложения без ручной настройки сетевых адресов. 

## Подмодуль config-server
В данном подмодуле содержатся все конфигурационные файлы всех модулей приложения, он является сервером `Spring-Cloud-Config` и реализует паттерн `Externalized Configuration`.  


## Подмодуль gateway-server
Подмодуль `gateway-server` является входным шлюзом для всех входящих запросов, оттуда они адресуются в соответствующие контроллеры через роуты. Данный модуль реализует паттерн `API Gateway`.  

# Модуль core
Данный модуль содержит в себе "распиленные" части проекта `Explore With Me` из модуля `core`. Каждый из сервисов регистрируется в `discovery-server` и получает конфигурационные файлы из `config-server`. Также для баз данных был использован паттерн `Database per service`.

# Модуль stats
Данный модуль является переработанной версией аналитической системы проекта `Explore With Me`. В данном модуле также, как и в модуле `core`, все сервисы регистрируется в `discovery-server`, а также получают конфигурационные файлы из `config-server`.

## Подмодуль serialization
В данном подмодуле описаны все `avro/proto` схемы, а также сериализатор и диссериализаторы для `avro` схем.

## Подмодуль stat-client
Данный модуль содержит реализации gRPC контроллеров из модуля `serialization`, а также он является точкой входа всей цепочки аналитической системы. 

## Подмодуль collector 
Данный модуль получает данные из stat-client через gRPC и записывает их в топик `stats.user-actions.v1` в `Kafka` преобразуя к формату `avro`.

## Подмодуль aggregator 
Модуль `aggregator` читает данные из топика `stats.user-actions.v1` и рассчитывает сходство мероприятий и записывает результаты в топик `stats.events-similarity.v1`.
Сходство мероприятий рассчитывается по следующей формуле:  



## Подмодуль analyzer 
Данный модуль читает данные:
а) из топика `stats.user-actions.v1` для хранения информации о последней оценке пользователей мероприятий;
b) из топика `stats.user-actions.v1` для хранения информации о сходстве мероприятий.
Также модуль core обращается к данному модулю по gRPC для получения:
a) списка рекомендуемых мероприятий для конкретного пользователя на основе сходства мероприятий;
b) списка мероприятий, которые похожи на указанное и с которыми пользователь ещё не взаимодействовал.