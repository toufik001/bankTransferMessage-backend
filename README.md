# README - Système Bancaire avec IBM MQ, PostgreSQL et Spring Boot

## Table des matières
1. [Prérequis](#prérequis)
2. [Démarrage rapide](#démarrage-rapide)
3. [Architecture des services](#architecture-des-services)
4. [Configuration initiale](#configuration-initiale)
5. [Gestion des services](#gestion-des-services)
6. [Dépannage](#dépannage)
7. [Développement](#développement)

## Prérequis
- Docker 20.10+
- Docker Compose 2.0+
- Java JDK 17+ (pour développement local)
- Maven 3.8+ (pour développement local)

## Démarrage rapide

1. **Cloner le dépôt et se positionner dans le dossier du projet**
   ```bash
   git clone [votre-repo]
   cd [nom-du-projet]
   ```

2. **Démarrer les services**
   ```bash
   docker-compose up -d --build
   ```

3. **Vérifier que tous les services sont opérationnels**
   ```bash
   docker-compose ps
   ```

## Architecture des services

### 1. IBM MQ (ibmmq)
- **Ports**:
  - 1414 : Port MQ principal
  - 9443 : Console web d'administration
- **Identifiants**:
  - Utilisateur: `admin`
  - Mot de passe: `passw0rd`
- **Queue Manager**: `QM1`

### 2. PostgreSQL (postgres)
- **Port**: 5432
- **Base de données**: `bankingdb`
- **Identifiants**:
  - Utilisateur: `postgres`
  - Mot de passe: `postgres`

### 3. Application Spring Boot (app)
- **Port**: 8082
- **Dépendances**:
  - Se connecte automatiquement à IBM MQ et PostgreSQL au démarrage

## Configuration initiale

### Initialisation d'IBM MQ
Le script `init-mq.sh` est exécuté au démarrage pour:
1. Créer la queue `PAYMENT.INPUT.QUEUE`
2. Configurer les autorisations
3. Définir les canaux nécessaires

Pour vérifier la configuration:
```bash
docker exec -it mq runmqsc QM1 << EOF
DISPLAY QLOCAL('PAYMENT.INPUT.QUEUE')
DISPLAY CHANNEL('DEV.APP.SVRCONN')
EOF
```

### Initialisation de la base de données
Les tables sont créées automatiquement via Flyway/Liquibase (selon la configuration Spring Boot).

Pour se connecter manuellement:
```bash
docker exec -it postgres psql -U postgres -d bankingdb
```

## Gestion des services

### Commandes de base
- **Démarrer tous les services**:
  ```bash
  docker-compose up -d
  ```

- **Arrêter tous les services**:
  ```bash
  docker-compose down
  ```

- **Reconstruire et redémarrer**:
  ```bash
  docker-compose up -d --build
  ```

### Commandes spécifiques

**Pour IBM MQ**:
- Voir les messages dans la queue:
  ```bash
  docker exec -it mq /opt/mqm/samp/bin/amqsbcg PAYMENT.INPUT.QUEUE QM1
  ```

- Purger la queue:
  ```bash
  docker exec -it mq runmqsc QM1 << EOF
  CLEAR QLOCAL('PAYMENT.INPUT.QUEUE')
  EOF
  ```

**Pour PostgreSQL**:
- Exécuter un backup:
  ```bash
  docker exec -t postgres pg_dump -U postgres -d bankingdb > backup.sql
  ```

- Restaurer un backup:
  ```bash
  cat backup.sql | docker exec -i postgres psql -U postgres -d bankingdb
  ```

## Dépannage

### Problèmes courants

1. **IBM MQ ne répond pas**:
   ```bash
   docker-compose logs ibmmq
   docker exec -it mq dspmq
   ```

2. **Problèmes de connexion à PostgreSQL**:
   ```bash
   docker-compose logs postgres
   docker exec -it postgres psql -U postgres -c "SELECT 1"
   ```

3. **L'application ne démarre pas**:
   ```bash
   docker-compose logs app
   ```

### Vérification des healthchecks
```bash
docker ps --filter "health=unhealthy"
```

## Développement

### Build local
```bash
mvn clean package
docker-compose build app
```

### Exécution des tests
```bash
mvn test
```

### Débogage
Pour attacher un debugger à l'application:
1. Modifier le docker-compose.yml pour le service app:
   ```yaml
   ports:
     - "8082:8082"
     - "5005:5005" # Port debug
   environment:
     JAVA_TOOL_OPTIONS: "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
   ```
2. Connecter votre IDE au port 5005

### Variables d'environnement clés
- `SPRING_DATASOURCE_URL`: URL JDBC pour PostgreSQL
- `IBM_MQ_CONNNAME`: Connection string pour IBM MQ
- `IBM_MQ_QUEUE_MANAGER`: Nom du queue manager (QM1)
- `IBM_MQ_CHANNEL`: Canal de communication (DEV.APP.SVRCONN)
- `IBM_MQ_QUEUE`: Nom de la queue (PAYMENT.INPUT.QUEUE)

## Sécurité
- **Changer les mots de passe par défaut** dans le docker-compose.yml pour la production
- **Configurer SSL** pour les connexions à IBM MQ et PostgreSQL
- **Restreindre les accès** réseau aux ports exposés