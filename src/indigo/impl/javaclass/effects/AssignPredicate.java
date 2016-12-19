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
//package indigo.impl.javaclass.effects;
//
//import indigo.Bindings;
//import indigo.annotations.Assigns;
//import indigo.invariants.LogicExpression;
//
//import java.lang.reflect.Method;
//import java.util.ArrayList;
//import java.util.List;
//
//import com.microsoft.z3.BoolExpr;
//
//public class AssignPredicate extends Predicate {
//
//	List<BoolExpr> effects = new ArrayList<>();
//	String arg;
//
//	AssignPredicate(Method m, Assigns a) {
//		super(true, m, a.value().split(":=")[0]);
//		arg = a.value().split(":=")[1];
//	}
//
//	static public List<AssignPredicate> listFor(Method m) {
//		List<AssignPredicate> res = new ArrayList<>();
//
//		for (Assigns i : m.getAnnotationsByType(Assigns.class))
//			res.add(new AssignPredicate(m, i));
//
//		return res;
//	}
//
//	@Override
//	public String toString() {
//		return predicateName + "--->" + value;
//	}
//
//	@Override
//	public boolean applyEffect(LogicExpression le, int iteration) {
//		Bindings matches = le.matches(predicateName);
//		if (matches != null) {
//			le.assertion(String.format("%s = %s", predicateName, arg));
//			return true;
//		}
//		return false;
//	}
//
// }
