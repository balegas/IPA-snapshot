$(document).ready(function() {
  var KB      = 1024;
  var MS_IN_S = 1000;

  var buildAndParseTimer = null;
  var parseTimer         = null;

  var oldGrammar        = null;
  var oldParserVar      = null;
  var oldOptionCache    = null;
  var oldOptionOptimize = null;
  var oldInput          = null;
  
  function buildErrorMessage(e) {
    return e.line !== undefined && e.column !== undefined
      ? "Line " + e.line + ", column " + e.column + ": " + e.message
      : e.message;
  }

  function parse() {
    oldInput = $("#input").val();

    $("#input").removeAttr("disabled");
    $("#parse-message").attr("class", "message progress").text("Parsing the input...");
    $("#output").addClass("disabled").text("Output not available.");

    try {
      var timeBefore = (new Date).getTime();
      var output = parser.parse($("#input").val());
      var timeAfter = (new Date).getTime();

      $("#parse-message")
        .attr("class", "message info")
	  .text("Input parsed successfully.");
      $("#output").removeClass("disabled").text(jsDump.parse(output));

      var result = true;
    } catch (e) {
      $("#parse-message").attr("class", "message error").text(buildErrorMessage(e));

      var result = false;
    }
    return result;
  }

  function scheduleParse() {
    if ($("#input").val() === oldInput) { return; }
    if (buildAndParseTimer !== null) { return; }

    if (parseTimer !== null) {
      clearTimeout(parseTimer);
      parseTimer = null;
    }

    parseTimer = setTimeout(function() {
      parse();
      parseTimer = null;
    }, 500);
  }

  $("#input")
    .change(scheduleParse)
    .mousedown(scheduleParse)
    .mouseup(scheduleParse)
    .click(scheduleParse)
    .keydown(scheduleParse)
    .keyup(scheduleParse)
    .keypress(scheduleParse);

  $("#input").focus();
  
  build_context(data);
  dataWithTypes = update_ops(data);
  if(errors.length > 0)
  	alert("Errors: "+errors);
  $("#output").text(dump(data));

});
