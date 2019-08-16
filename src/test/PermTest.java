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
package test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class PermTest {

	private static List<List<Integer>> allPermutations(List<Integer> lists, List<List<Integer>> accum) {
		int n = lists.size();
		if (n == 0) {
			ArrayList<Integer> emptyList = Lists.newArrayList();
			accum.add(emptyList);
		} else {
			for (int i = 0; i < n; i++) {
				Integer elem = lists.get(i);
				List<Integer> rest = Lists.newArrayList(lists.subList(0, i));
				List<Integer> part2 = Lists.newArrayList(lists.subList(i + 1, n));
				rest.addAll(part2);
				List<List<Integer>> allOther = allPermutations(rest, accum);
				List<List<Integer>> mod = allOther.stream().map(l -> {
					List<Integer> ne = Lists.newArrayList(l);
					ne.add(0, elem);
					return ne;
				}).collect(Collectors.toList());
				accum.addAll(mod);
			}
		}
		return accum;
	}

	private static void permutation(List<Integer> prefix, List<Integer> sequence, List<List<Integer>> accum) {
		int n = sequence.size();
		if (n == 0)
			accum.add(prefix);
		else {
			for (int i = 0; i < n; i++) {
				ArrayList<Integer> prefixCopy = Lists.newArrayList(prefix);
				prefixCopy.add(sequence.get(i));
				List<Integer> rest = Lists.newArrayList(sequence.subList(0, i));
				List<Integer> part2 = Lists.newArrayList(sequence.subList(i + 1, n));
				rest.addAll(part2);
				permutation(prefixCopy, rest, accum);
			}
		}
	}

	public static void main(String[] args) {
		// System.out.println(allPermutations(Lists.newArrayList(1, 2, 3),
		// Lists.newArrayList()));
		List<List<Integer>> accum = Lists.newArrayList();
		permutation(Lists.newArrayList(), ImmutableList.of(1, 2, 3), accum);
		System.out.println(accum);

	}

}
