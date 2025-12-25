# Spring Boot + Vendor JARs Demo 🚀

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.0-green.svg)](https://spring.io)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)](https://hub.docker.com/r/tushardashpute/springboot-vendor-demo)
[![Java 17](https://img.shields.io/badge/Java-17-orange.svg)](https://adoptium.net)
[![Port 33333](https://img.shields.io/badge/Port-33333-yellow.svg)](http://localhost:33333)

**Production demo: Spring Boot with vendor JARs in `lib/` (no Maven Central deps)**.

## 🎯 What This Solves

    ❌ Maven Central blocked (air-gapped/enterprise)
    ❌ Vendor supplies JARs out-of-band
    ✅ lib/ folder + JarLauncher classpath
    ✅ Dockerized + Kubernetes-ready


## 📁 Structure

    ├── Dockerfile # JarLauncher magic ✨
    ├── lib/
    │ └── commons-io-2.15.1.jar # Vendor JAR (501KB)
    ├── src/main/java/org/example/
    │ ├── CustomerController.java # Business API
    │ └── HelloController.java # File I/O demo
    ├── target/*.jar # Spring Boot fat JAR
    └── pom.xml # No vendor deps!


## 🚀 Quick Start (5 mins)

### Clone + Run
git clone https://github.com/tushardashpute/springboot-vendor-jars-demo.git
cd springboot-vendor-jars-demo

**Build Spring Boot**

./mvnw clean package -DskipTests

**Docker**

docker build -t springboot-vendor-demo:latest .
docker run -d -p 33333:33333 --name demo springboot-vendor-demo:latest


### Test Endpoints

Business API
    curl http://localhost:33333/listallcustomers

**File I/O (vendor JAR ready)**
 
    curl -X POST "http://localhost:33333/api/write?name=test.txt&content=hello"
    curl http://localhost:33333/api/read/test.txt

**Health**

    curl http://localhost:33333/actuator/health

**Sample Response**:
 
    [{"name":"Tushar","id":"001","country":"INDIA","state":"AP","type":"retail"}]


**undefined**
  
    {"status":"UP"}


## 🔍 Verify Vendor JAR Magic ✨

    docker exec demo ls -la /app/lib/ # commons-io-2.15.1.jar ✅
    docker exec demo ps aux | grep JarLauncher # -cp app.jar:lib/* ✅
    docker exec demo java -cp /app/lib/* FileUtils # No ClassNotFound ✅

## 🏗️ Why JarLauncher?

| ❌ Wrong | ✅ Correct |
|---------|-----------|
| `java -jar app.jar` | Ignores `lib/*` |
| `java -cp app.jar Main` | Ignores `BOOT-INF/*` |
| **`JarLauncher -cp app.jar:lib/*`** | **Loads EVERYTHING** |

    app.jar:
    ├── BOOT-INF/classes/ (your code)
    └── BOOT-INF/lib/* (Spring Boot)

lib/* (vendor JARs)

## ☁️ Kubernetes (kind)

    kind create cluster
    kind load docker-image springboot-vendor-demo:latest
    kubectl apply -f k8s/ # targetPort: 33333
    kubectl port-forward svc/demo 33333:80

## 🛠️ Customize

**Add your vendor JAR**:

    cp /path/to/myvendor.jar lib/
    docker build -t my-app .

**Multiple JARs**:

    lib/
    ├── vendor1.jar
    ├── vendor2.jar
    └── commons-io-2.15.1.jar

**JarLauncher loads ALL**: `-cp app.jar:lib/*`

## 📊 Status

| Feature | ✅ Status |
|---------|----------|
| Vendor JARs | `lib/commons-io-2.15.1.jar` |
| Docker | `tushardashpute/springboot-vendor-demo:latest` |
| Port | `33333` |
| Controllers | `CustomerController` + `HelloController` |
| K8s Ready | `targetPort: 33333` |

## 🧹 Cleanup

    docker stop demo && docker rm demo
    docker rmi springboot-vendor-demo:latest

## 🎉 Use Cases

- **Enterprise**: Vendor JARs not on Maven Central
- **Air-gapped**: No internet for Maven
- **Legacy**: Old proprietary JARs
- **Compliance**: Approved JARs only

---
**⭐ Star if helpful!**  
**Built by Tushar Dashpute** © 2025
