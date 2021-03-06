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
package indigo.specification;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import indigo.generic.GenericInvariant;
import indigo.interfaces.interactive.ConflictResolutionPolicy;
import indigo.interfaces.logic.Invariant;
import indigo.interfaces.logic.PredicateAssignment;
import indigo.interfaces.operations.Operation;
import indigo.runtime.AnalysisContext;
import indigo.runtime.ProgramSpecification;

public abstract class AbstractSpecification implements ProgramSpecification {

	private final String appName;
	private Set<Operation> operations;
	// TODO: Should reduce to a single invariant?
	protected Set<Invariant> invariants;
	private Map<PredicateAssignment, Set<Invariant>> predicate2Invariants;

	protected final static Logger analysisLog = Logger.getLogger(AbstractSpecification.class.getName());

	public AbstractSpecification(String appName) {
		this.appName = appName;
	}

	protected void init() {
		this.invariants = readInvariants();
		this.operations = readOperations();
		this.predicate2Invariants = computeInvariantsForPredicate();

	}

	protected abstract Set<Invariant> readInvariants();

	protected abstract Set<Operation> readOperations();

	protected Map<PredicateAssignment, Set<Invariant>> computeInvariantsForPredicate() {
		Collection<PredicateAssignment> flattenEffects = Lists.newLinkedList();
		operations.forEach(op -> {
			op.getEffects().forEach(flattenEffects::add);
			op.getPreConditions().forEach(flattenEffects::add);
		});

		Map<PredicateAssignment, Set<Invariant>> affectedInvariantPerClauses = new HashMap<>();
		analysisLog.fine("Invariants affected by operations in the workload:");
		flattenEffects.forEach(pa -> {
			Set<Invariant> s = Sets.newHashSet();
			for (Invariant i : invariants) {
				if (pa.affects(i)) {
					s.add(i/* .copyOf() */);
					analysisLog.fine("Predicate " + pa + " present in invariant clauses " + s + " for operation "
							+ pa.getOperationName());
				}
			}
			ImmutableSet<Invariant> immutable = ImmutableSet.copyOf(s);
			affectedInvariantPerClauses.put(pa, immutable);
		});

		if (affectedInvariantPerClauses.isEmpty()) {
			analysisLog.warning("No invariants are affected by operations in the workload.");
		}
		return ImmutableMap.copyOf(affectedInvariantPerClauses);
	}

	@Override
	public Invariant invariantFor(Collection<String> ops, AnalysisContext context) {
		Invariant res;
		Set<Invariant> ss = null;
		for (String op : ops) {
			Set<Invariant> si = Sets.newHashSet();
			context.getOperationEffects(op, false, false).forEach(e -> {
				si.addAll(predicate2Invariants.get(e));
			});
			context.getOperationPreConditions(op, false, false).forEach(e -> {
				si.addAll(predicate2Invariants.get(e));
			});
			ss = Sets.intersection(ss != null ? ss : si, si);
		}

		if (ss.isEmpty()) {
			res = newEmptyInv();
		} else {
			res = ss.stream().reduce(null, (mergeAcc, next) -> {
				if (mergeAcc == null) {
					return next;
				} else {
					try {
						return new GenericInvariant(mergeAcc.mergeClause(next));
					} catch (Exception e) {
						e.printStackTrace();
					}
					return mergeAcc;
				}
			});
		}

		analysisLog.fine("\n; -----------------------------------------------------------------------");
		analysisLog.fine("Operations:");
		analysisLog.fine("; " + ops);
		analysisLog.fine("; Simplified Invariant for " + ops + " --> " + res);
		return res;
	}

	@Override
	public Set<Operation> getOperations() {
		return ImmutableSet.copyOf(operations);
	}

	@Override
	public Set<String> getOperationsNames() {
		return operations.stream().map(op -> op.opName()).collect(Collectors.toSet());
	}

	@Override
	public void updateOperations(Collection<Operation> newOperations) {
		operations.addAll(newOperations);
		predicate2Invariants = computeInvariantsForPredicate();
	}

	@Override
	public Set<Invariant> getInvariantClauses() {
		return ImmutableSet.copyOf(invariants);
	}

	@Override
	public String getAppName() {
		return appName;
	}

	@Override
	public abstract Invariant newEmptyInv();

	@Override
	public abstract ConflictResolutionPolicy getDefaultConflictResolutionPolicy();

}
