## Gson (Java)

의존성 예시 (Gradle):

```groovy
implementation 'com.google.code.gson:gson:2.11.0'
```

간단한 직렬화/역직렬화 예제:

```java
import com.google.gson.Gson;

class User {
  String name;
  int age;
}

public class GsonExample {
  public static void main(String[] args) {
    Gson gson = new Gson();

    User user = new User();
    user.name = "Alice";
    user.age = 30;

    String json = gson.toJson(user);
    System.out.println(json); // {"name":"Alice","age":30}

    User parsed = gson.fromJson(json, User.class);
    System.out.println(parsed.name + ", " + parsed.age); // Alice, 30
  }
}
```

### Object to Json

```java
// 객체를 Json 문자열로 직렬화
import com.google.gson.Gson;

class User {
  String name;
  int age;
}

public class ObjectToJsonExample {
  public static void main(String[] args) {
    Gson gson = new Gson();
    User user = new User();
    user.name = "Alice";
    user.age = 30;
    String json = gson.toJson(user); // {"name":"Alice","age":30}
    System.out.println(json); // {"name":"Alice","age":30}
  }
}
```

### Json to Object

```java
// Json 문자열을 객체로 역직렬화
import com.google.gson.Gson;

class User {
  String name;
  int age;
}

public class JsonToObjectExample {
  public static void main(String[] args) {
    Gson gson = new Gson();
    String json = "{\"name\":\"Alice\",\"age\":30}";
    User user = gson.fromJson(json, User.class);
    System.out.println(user.name + ", " + user.age); // Alice, 30
  }
}
```

컬렉션 타입은 `TypeToken`을 사용합니다.

```java
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;

public class JsonToListExample {
  public static void main(String[] args) {
    Gson gson = new Gson();
    String json = "[{\\\"name\\\":\\\"A\\\",\\\"age\\\":10},{\\\"name\\\":\\\"B\\\",\\\"age\\\":20}]";
    Type listType = new TypeToken<List<User>>() {}.getType();
    List<User> users = gson.fromJson(json, listType);
    System.out.println(users.size()); // 2
  }
}
```

### Json 필드 추가

```java
// JsonObject로 파싱 후 필드 추가
import com.google.gson.*;

public class JsonFieldAddExample {
  public static void main(String[] args) {
    String json = "{\"name\":\"Alice\",\"age\":30}";
    JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
    obj.addProperty("country", "KR");          // 문자열 필드 추가
    obj.addProperty("verified", true);          // 불리언 필드 추가
    JsonArray tags = new JsonArray();            // 배열 필드 추가
    tags.add("dev");
    tags.add("java");
    obj.add("tags", tags);                      // 값이 채워진 배열을 설정
    String updated = new Gson().toJson(obj);
    System.out.println(updated); // {"name":"Alice","age":30,"country":"KR","verified":true,"tags":["dev","java"]}
  }
}
```

### Json 필드 삭제

```java
// 특정 필드 제거
import com.google.gson.*;

public class JsonFieldRemoveExample {
  public static void main(String[] args) {
    String json = "{\"name\":\"Alice\",\"age\":30,\"country\":\"KR\"}";
    JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
    obj.remove("country");
    String updated = new Gson().toJson(obj);
    System.out.println(updated); // {"name":"Alice","age":30}
  }
}
```

### Json 필드의 Value 변경

```java
// 기존 필드 값을 갱신(문자열/숫자/불리언 모두 addProperty 사용 가능)
import com.google.gson.*;

public class JsonFieldUpdateExample {
  public static void main(String[] args) {
    String json = "{\"name\":\"Alice\",\"age\":30}";
    JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
    obj.addProperty("name", "Bob");     // 문자열 값 변경
    obj.addProperty("age", 31);          // 숫자 값 변경
    String updated = new Gson().toJson(obj);
    System.out.println(updated); // {"name":"Bob","age":31}
  }
}
```

### Json 수동 생성

#### Array

```java
import com.google.gson.*;

public class JsonArrayBuildExample {
  public static void main(String[] args) {
    JsonArray arr = new JsonArray();
    arr.add("apple");
    arr.add(123);
    arr.add(true);
    System.out.println(new Gson().toJson(arr)); // ["apple",123,true]
  }
}
```

#### Object

```java
import com.google.gson.*;

public class JsonObjectBuildExample {
  public static void main(String[] args) {
    JsonArray tags = new JsonArray();
    tags.add("dev");
    tags.add("java");

    JsonObject obj = new JsonObject();
    obj.addProperty("name", "Alice");
    obj.addProperty("age", 30);
    obj.add("tags", tags); // 배열을 필드로 추가

    System.out.println(new Gson().toJson(obj)); // {"name":"Alice","age":30,"tags":["dev","java"]}
  }
}
```

#### Primitive field

```java
import com.google.gson.*;

public class JsonPrimitiveFieldExample {
  public static void main(String[] args) {
    JsonObject obj = new JsonObject();
    obj.add("score", new JsonPrimitive(4.5));  // 숫자 프리미티브 추가
    obj.add("flag", new JsonPrimitive(true));  // 불리언 프리미티브 추가
    obj.add("nick", new JsonPrimitive("kwang")); // 문자열 프리미티브 추가
    System.out.println(new Gson().toJson(obj)); // {"score":4.5,"flag":true,"nick":"kwang"}
  }
}
```


