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
    System.out.println(json);

    User parsed = gson.fromJson(json, User.class);
    System.out.println(parsed.name + ", " + parsed.age);
  }
}
```


