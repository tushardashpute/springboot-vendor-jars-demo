# Spring Boot + Vendor JARs (Actimize/PNC JFrog Demo) 🚀

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.0-green.svg)](https://spring.io)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)](https://hub.docker.com/r/tushardashpute/springboot-vendor-demo)
[![Java 17](https://img.shields.io/badge/Java-17-orange.svg)](https://adoptium.net)
[![Port 33333](https://img.shields.io/badge/Port-33333-yellow.svg)](http://localhost:33333)

**Production demo for Actimize JARs NOT in PNC JFrog** - **System scope compile** + **Docker `lib/` runtime**.

## 🎯 PNC Actimize Use Case

```
❌ Actimize JARs missing from PNC JFrog
❌ Air-gapped Maven builds fail
✅ System scope in pom.xml (compile from lib/)
✅ Docker lib/ folder (runtime redundancy)
✅ JarLauncher loads app.jar + lib/* 
```

**Demo uses** `commons-io-2.15.1.jar` **(exact same pattern as Actimize)**.

## 📁 Complete Structure

```
├── Dockerfile                    # JarLauncher + lib/*
├── lib/
│   └── commons-io-2.15.1.jar     # Vendor JAR (501KB) ✅
├── pom.xml                       # System scope dep ✅
├── src/main/java/org/example/
│   ├── CustomerController.java   # Business API
│   └── VendorController.java     # Actimize test
├── target/*.jar                  # Fat JAR w/ system scope
└── README.md
```

## 🚀 Quick Start (5 Minutes)

### 1. Clone + Vendor JAR
```bash
git clone https://github.com/tushardashpute/springboot-vendor-jars-demo.git
cd springboot-vendor-jars-demo

# Add vendor JAR (like Actimize JARs from CD)
mkdir -p lib
curl -L -o lib/commons-io-2.15.1.jar \
  https://repo1.maven.org/maven2/commons-io/commons-io/2.15.1/commons-io-2.15.1.jar
```

### 2. System Scope Build (No JFrog!)
```bash
./mvnw clean package -DskipTests
# Compiles from lib/commons-io-2.15.1.jar ✅
# No JFrog/maven-central needed ✅
```

### 3. Docker Build & Run
```bash
docker build -t springboot-vendor-demo:latest .
docker run -d -p 33333:33333 --name demo springboot-vendor-demo:latest
```

## 🧪 Test Endpoints (Port 33333)

### Business API
```bash
curl http://localhost:33333/listallcustomers
# [{"name":"Tushar","id":"001","country":"INDIA","state":"AP","type":"retail"}] ✅
```

### Actimize/Vendor JAR Test
```bash
curl http://localhost:33333/actimize/test
# "✅ Actimize JAR loaded via system scope + Docker lib/" ✅
```

### Health Check
```bash
curl http://localhost:33333/actuator/health
# {"status":"UP"} ✅
```

## 🔍 Verify Vendor JAR Loading ✨

```bash
# Container structure
docker exec demo ls -la /app/
# app.jar (17MB) + lib/ (501KB) ✅

# Vendor JAR contents
docker exec demo ls -la /app/lib/
# commons-io-2.15.1.jar ✅

# JarLauncher classpath
docker exec demo ps aux | grep JarLauncher
# -cp '/app/app.jar:/app/lib/*' ✅

# Test vendor class (no ClassNotFound)
docker exec demo java -cp /app/lib/* org.apache.commons.io.FileUtils ✅
```

## 🏗️ System Scope + Docker Pattern

### 1. **pom.xml** (Compile Time - No JFrog)
```xml
<dependency>
    <groupId>commons-io</groupId>
    <artifactId>commons-io</artifactId>
    <version>2.15.1</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/lib/commons-io-2.15.1.jar</systemPath>
</dependency>
<!-- Real Actimize: com.nice.actimize:actimize-core:8.5.0 -->
```

### 2. **Dockerfile** (Runtime Redundancy)
```dockerfile
FROM eclipse-temurin:17-jre
WORKDIR /app

# Maven fat JAR (system scope baked in)
COPY target/*.jar /app/app.jar

# Docker lib/ (extra safety)
COPY lib/ /app/lib/

EXPOSE 33333
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -cp '/app/app.jar:/app/lib/*' org.springframework.boot.loader.JarLauncher"]
```

### 3. **Why Both?**
```
Maven: Compiles from lib/ (no JFrog needed)
Docker: Copies lib/ again (runtime failsafe)
JarLauncher: Loads app.jar + lib/* (everything works)
```

## 🏗️ Why JarLauncher? (Critical)

| ❌ Fails | ✅ Works |
|----------|---------|
| `java -jar app.jar` | Ignores `/app/lib/*` |
| `java -cp app.jar Main` | Ignores `BOOT-INF/lib/*` |
| **`JarLauncher -cp app.jar:lib/*`** | **Loads ALL JARs** |

```
Final Classpath:
├── BOOT-INF/classes/          (app code)
├── BOOT-INF/lib/*             (Spring Boot)
├── lib/commons-io-2.15.1.jar  (Actimize/vendor)
```

## ☁️ Kubernetes Deployment

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: actimize-demo
spec:
  replicas: 2
  template:
    spec:
      containers:
      - name: demo
        image: tushardashpute/springboot-vendor-demo:latest
        ports:
        - containerPort: 33333
        env:
        - name: JAVA_OPTS
          value: "-Xms512m -Xmx2g -Djava.security.egd=file:/dev/./urandom"
---
apiVersion: v1
kind: Service
metadata:
  name: actimize-demo
spec:
  ports:
  - port: 80
    targetPort: 33333
```

```bash
kind create cluster
kind load docker-image springboot-vendor-demo:latest
kubectl apply -f k8s/
kubectl port-forward svc/actimize-demo 33333:80
```

## 🛠️ Real Actimize Setup

**Replace commons-io with Actimize**:
```bash
# 1. Get Actimize JARs from vendor CD
cp /mnt/cdrom/lib/actimize-core-8.5.0.jar lib/

# 2. Update pom.xml
# <groupId>com.nice.actimize</groupId>
# <artifactId>actimize-core</artifactId>
# <version>8.5.0</version>
# <systemPath>${project.basedir}/lib/actimize-core-8.5.0.jar</systemPath>

# 3. Build + deploy (same commands)
./mvnw clean package && docker build . && docker run -p 33333:33333 .
```

## 📊 Production Status

| Feature | ✅ Status |
|---------|----------|
| **System Scope** | `lib/commons-io-2.15.1.jar` (no JFrog) |
| **Docker Image** | `tushardashpute/springboot-vendor-demo:latest` |
| **Port** | `33333` (custom) |
| **Controllers** | `CustomerController` + `VendorController` |
| **Classpath** | `app.jar:lib/*` (JarLauncher) |
| **Kubernetes** | `targetPort: 33333` ready |

## 🧹 Cleanup
```bash
docker stop demo && docker rm demo
docker rmi springboot-vendor-demo:latest
kind delete cluster  # if testing k8s
```

## 🎉 PNC Actimize Ready

```
✅ Compiles without JFrog (system scope)
✅ Docker runtime redundancy (lib/)
✅ Multiple JARs supported
✅ Kubernetes manifests ready
✅ Production JVM tuning
✅ Port 33333 support
```

**Demo replicates exact Actimize scenario** → **Copy `lib/actimize-*.jar`** → **Deploy**!

***
**⭐ Star if helpful!**  
**Built for PNC Actimize + JFrog scenario** © 2025 Tushar Dashpute[1][2][3]

[1](https://github.com/dimMaryanto93/k8s-nfs-springboot-upload)
[2](https://img.shields.io/badge/Spring%20Boot-3.3.0-green.svg)
[3](https://img.shields.io/badge/Docker-Ready-blue.svg)
