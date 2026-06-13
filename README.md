## Cells2D : building and running

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

Arguments:

```
Cells2D
  Options:
    --console, -c
      Launch in console mode
      Default: false
    --genkey, -g
      Generates a 256-bit AES key and outputs it in Base64
      Default: false
    --help, -h
      Show help
      Default: false
    --load, -l
      File to load simulation from (in console mode)
      Default: <empty string>
    --save, -s
      File to save simulation (in console mode)
      Default: console-save.dat
    --skipsave, -S
      Skip saving at end of simulation (in console mode)
      Default: false

```

### Command-line version

First run the jar app:

```java -jar target/cells2d-1.0-SNAPSHOT.jar --console```

You will then have to input your simulation parameters (rows, columns, SEIR, etc).

Load/save files can be passed via command line arguments (see above for reference).

### Graphical interface

Run the generated jar without any arguments

```java -jar target/cells2d-1.0-SNAPSHOT.jar```

Use one of the provided login credentials. Keep in mind that Doctor and Researcher have different graphical interfaces.

For Doctor interface, you need to generate and declare an environement variable as shown:

```shell
java -jar target/cells2d-1.0-SNAPSHOT.jar --genkey

export ENCRYPTION_KEY="<your-key>"
```

Remember it, as you will need it to decrypt saved patient data.

### Javadoc

To generate and open the Javadoc, run the following commands:

| Linux                        | Windows                        |
|------------------------------|--------------------------------| 
| ```./mvnw javadoc:javadoc``` | ```mvnw.cmd javadoc:javadoc``` |

You will find it in `target/site/apidocs/index.html`.

