
data = {
   "INV": {
      "type": "forall",
      "vars": [
         {
            "var_name": "x",
            "type": "X"
         }
      ],
      "formula": {
         "type": "NOT",
         "formula": {
            "type": "predicate",
            "predicate": {
               "name": "Teste",
               "args": [
                  {
                     "type": "variable",
                     "value": {
                        "var_name": "x",
                        "type": "_"
                     }
                  }
               ]
            }
         }
      }
   },
   "OPS": [
      {
         "op_name": "OP",
         "args": [
            {
               "type": "variable",
               "value": {
                  "var_name": "x",
                  "type": "X"
               }
            }
         ],
         "effects": [
            {
               "type": "=",
               "formula": {
                  "type": "predicate",
                  "predicate": {
                     "name": "Teste",
                     "args": [
                        {
                           "type": "variable",
                           "value": {
                              "var_name": "x",
                              "type": "_"
                           }
                        }
                     ]
                  }
               },
               "value": {
                  "type": "const",
                  "value": {
                     "value": "false",
                     "type": "bool"
                  }
               }
            }
         ]
      }
   ]
}

errors = [];
predicate_types = {};

// TODO:Do not access atrirbutes directly.

function dump(Obj){
	return jsDump.parse(Obj);
}

function set_predicate_type(predicate, context){
	// TODO:Does not support predicates with different arity.
	predicate_types[predicate.name] = [];
	for( arg_i in predicate.args){
		predicate_types[predicate.name]
			.push(get_type(predicate.args[arg_i], context));
	}
}

function get_type(term, context){
	switch (term.type) {
		case "const": 
			return term.value.type;
		case "variable":
			var_name = term.value.var_name;
			type = context[var_name];
			return type;
		default:
			errors.push("Not defined term "+dump(term));
			return undefined;
	}
}

function get_context_type(term, context){
	var type = get_type(term, context);
	return type;
}

function set_context_vars(vars, context){
	for(v in vars){
		context[vars[v].var_name] = vars[v].type;
	}
}

function release_context_vars(vars, context){
	for( v in vars){
		delete context[vars[v].var_name];
	}
}

function process_forall(vars, formula, context){
	set_context_vars(vars, context);
	var checked = process_node(formula, context);
	release_context_vars(vars, context);
	return checked;
}

function process_single(formula, context){
	var checked = process_node(formula, context);
	return checked;
}

function process_binary(formula, context){
	var checkedLeft = process_node(formula.left, context);
	var checkedRight = process_node(formula.right, context);
	return checkedLeft && checkedRight;
}

function process_predicate(predicate, context){
	var result = true;
	var args = predicate.args;
	for( arg_i in args){
		type = get_context_type(args[arg_i], context);
		result &= type != undefined;
	}
	set_predicate_type(predicate, context);
	return result;
}

function process_op_assign(node, context){
	process_op_node(node.formula, context);
	var value_type = get_type(node.value, context);
}

function process_op_predicate(node, context){
	var pred_name = node.predicate.name;
	var pred_args = node.predicate.args;
	for( arg_i in pred_args){
		ctx_type = get_context_type(pred_args[arg_i], context);
		if(ctx_type == undefined){
			errors.push("Variable "+dump(pred_args[arg_i])+"is not in context ");
		}
		if(predicate_types[pred_name] == undefined){
			errors.push("Predicate not defined: "+pred_name);
		}
		else if(predicate_types[pred_name][arg_i] != ctx_type){
			errors.push("Argument type does not match predicate:"
				+dump(predicate_types[pred_name][arg_i])+" "+dump(ctx_type));
		}
		else{
			pred_args[arg_i].value.type = ctx_type;
		}
	}
}

function process_node(node, context){
	switch (node.type){
		case "forall":
			process_forall(node.vars, node.formula, context);
			break;
		case "NOT":
			process_single(node.formula, context);
			break;
		case "AND":
			process_binary(node, context);
			break;
		case "OR":
			process_binary(node, context);
			break;
		case "predicate":
			process_predicate(node.predicate, context);
			break;
		case "const":
			break;
		case "<":
		case ">":
		case "<=":
		case ">=":
			process_binary(node, context);
			break;
		default:
			errors.push("Cannot proces node type: "+dump(node));
	}
}

function process_op_node(node, context){
	switch (node.type){
		case "-=":
		case "+=":
		case "=" :
			process_op_assign(node, context);
			break;
		case "predicate":
			process_op_predicate(node, context);
			break;
		default:
			errors.push("Cannot proces node type: "+dump(node));
	}
}

function build_context(spec) {
	var context = new Object;
	process_node(spec.INV, context);
}

function update_ops(spec) {
	for(op_i in spec.OPS){
		// Create context with operation args
		var context = new Object;
		op = spec.OPS[op_i];
		for(a in op.args){
			var arg = op.args[a].value;
			if(context[arg.var_name] == undefined)
				context[arg.var_name] = arg.type;
			else
				error.push("Operation argument appears twice. "+dump(arg));
		}
		// Check predicate types
		for(e_i in op.effects){
			process_op_node(op.effects[e_i], context);
		}

	}
	//alert("OPS"+ dump(spec.OPS));
}
