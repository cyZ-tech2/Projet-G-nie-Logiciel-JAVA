



## How to build

### Instructions

Cells2D requires Java 22 or later.

First, clone the project with `git` :

```bash
git clone https://github.com/cyZ-tech2/Projet-G-nie-Logiciel-JAVA
```

Then simply run this command, which uses the bundled *maven* wrapper (or you can use your existing `mvn` installation instead) :

| Linux | Windows |
| --- | --- | 
| ```./mvnw package``` | ```mvnw.cmd package``` |

This command will package the application into a `.jar` file, which you can easily run at any time.

## How to run

*(Note : running the application also requires Java 22, due to JavaFX 24.)*

Make sure that you already have built the jar package. 

### Command-line version

First run the jar app:

```java -jar target/cells2d-1.0-SNAPSHOT.jar --console```

You will then have to input your simulation parameters (rows, columns, SEIR, etc)