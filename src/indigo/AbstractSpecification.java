package indigo;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import indigo.interfaces.ConflictResolutionPolicy;
import indigo.interfaces.Invariant;
import indigo.interfaces.Operation;
import indigo.interfaces.PredicateAssignment;

public abstract class AbstractSpecification implements ProgramSpecification {

	private final String appName;
	private Set<Operation> operations;
	// TODO: Should reduce to a single invariant?
	protected Set<Invariant> invariants;
	private Map<PredicateAssignment, Set<Invariant>> affectedInvariantPerClauses;

	protected final static Logger analysisLog = Logger.getLogger(AbstractSpecification.class.getName());

	public AbstractSpecification(String appName) {
		this.appName = appName;
	}

	protected void init() {
		this.invariants = readInvariants();
		this.operations = readOperations();
		this.affectedInvariantPerClauses = computeInvariantsForPredicate();
	}

	protected abstract Set<Invariant> readInvariants();

	protected abstract Set<Operation> readOperations();

	protected Map<PredicateAssignment, Set<Invariant>> computeInvariantsForPredicate() {
		Collection<PredicateAssignment> flattenEffects = Lists.newLinkedList();
		operations.forEach(op -> {
			op.getEffects().forEach(flattenEffects::add);
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
	public Map<PredicateAssignment, Set<Invariant>> invariantsAffectedPerPredicateAssignemnt() {
		return Maps.newHashMap(affectedInvariantPerClauses);
	}

	@Override
	public abstract Invariant newEmptyInv();

	@Override
	public abstract ConflictResolutionPolicy getDefaultConflictResolutionPolicy();

}
