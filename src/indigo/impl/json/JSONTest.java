// package indigo.impl.json;
//
// import java.io.File;
// import java.io.FileInputStream;
// import java.io.IOException;
// import java.io.InputStream;
//
// import org.json.simple.JSONObject;
// import org.json.simple.JSONValue;
//
// public class JSONTest {
//
// public static void main(String[] args) throws IOException {
//
// File file = new File("web-parser/spec.json");
// InputStream inputStream = new FileInputStream(file);
// byte[] buffer = new byte[65000];
// StringBuilder spec = new StringBuilder();
// int count = -1;
// while (true) {
// count = inputStream.read(buffer);
// if (count > 0) {
// spec.append(new String(buffer, 0, count, "UTF-8"));
// } else {
// break;
// }
// }
// inputStream.close();
// Object obj = JSONValue.parse(spec.toString());
// new JSONSpecification((JSONObject) obj);
// }
// }
