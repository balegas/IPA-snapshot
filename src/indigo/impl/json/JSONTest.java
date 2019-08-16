/**
-------------------------------------------------------------------

Copyright (c) 2014 SyncFree Consortium.  All Rights Reserved.

This file is provided to you under the Apache License,
Version 2.0 (the "License"); you may not use this file
except in compliance with the License.  You may obtain
a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.

-------------------------------------------------------------------
**/
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
