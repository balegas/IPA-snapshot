package indigo;

import indigo.interfaces.ConflictResolutionPolicy;
import indigo.interfaces.Invariant;
import indigo.interfaces.Operation;
import indigo.interfaces.PredicateAssignment;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

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
		getAllOperationEffects().values().forEach(flattenEffects::addAll);

		Map<PredicateAssignment, Set<Invariant>> affectedInvariantPerClauses = new HashMap<>();
		analysisLog.fine("Invariants affected by operations in the workload:");
		flattenEffects.forEach(pa -> {
			Set<Invariant> s = Sets.newHashSet();
			for (Invariant i : invariants) {
				if (pa.affects(i)) {
					s.add(i.copyOf());
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
	public Map<String, Collection<PredicateAssignment>> getAllOperationEffects() {
		Map<String, Collection<PredicateAssignment>> map = Maps.newHashMap();
		for (Operation op : operations) {
			map.put(op.opName(), op.getEffects());
		}
		return map;
	}

	@Override
	public Set<Operation> getOperations() {
		return ImmutableSet.copyOf(operations);
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
		return ImmutableMap.copyOf(affectedInvariantPerClauses);
	}

	@Override
	public abstract Invariant newEmptyInv();

	@Override
	public abstract ConflictResolutionPolicy getDefaultConflictResolutionPolicy();

}
