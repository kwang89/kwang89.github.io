## 자바 실행

### 1) javac/java로 컴파일/실행

```bash
javac Main.java
java Main
```

### 2) classpath 사용

```bash
javac -cp libs/* Main.java
java -cp .;libs/* Main
```

macOS/Linux는 `:` 구분자를 사용:

```bash
java -cp .:libs/* Main
```

### 3) JAR 생성/실행

`MANIFEST.MF`의 `Main-Class`를 지정 후:

```bash
jar cfm app.jar MANIFEST.MF *.class
java -jar app.jar
```


