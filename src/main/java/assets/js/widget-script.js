var GLOBAL_EPIC_TABLE = null;
var TEMPLATE_HEADER_FOOTER = "<thead><tr><th>User Story</th><th>SUMMARY</th><th>PRIORITY</th><th>UNEXECUTED</th><th>FAILED</th><th>WIP</th><th>BLOCKED</th><th>PASSED</th><th>PLANNED</th><th>UNPLANNED</th></tr></thead><tfoot><tr><th>User Story</th><th>SUMMARY</th><th>PRIORITY</th><th>UNEXECUTED</th><th>FAILED</th><th>WIP</th><th>BLOCKED</th><th>PASSED</th><th>PLANNED</th><th>UNPLANNED</th></tr></tfoot>";
var TEMPLATE_HEADER_FOOTER_1 = "<thead><tr><th>Assignee</th><th>UNEXECUTED</th><th>FAILED</th><th>WIP</th><th>BLOCKED</th><th>PASSED</th></tr></thead><tfoot><tr><th>Assignee</th><th>UNEXECUTED</th><th>FAILED</th><th>WIP</th><th>BLOCKED</th><th>PASSED</th></tr></tfoot>";
var GLOBAL_CYCLE_TABLE = null;
var GREENHOPPER_ISSUE_API_LINK = 'https://greenhopper.app.alcatel-lucent.com/issues/?jql=';
var GREENHOPPER_BROWSE_ISSUE_LINK = "https://greenhopper.app.alcatel-lucent.com/browse/";
var IS_TESTING = true;
var GLOBAL_US_TABLES_AJAX = {"ajax": null, "loading": false};
var GLOBAL_ASSIGNEE_TABLES_AJAX = {"ajax": null, "loading": false};
var SAVE_GADGET_URI = "/gadget/save";
var GET_GADGETS_URI = "/gadget/gadgets";
var GET_EPIC_URI = "/getEpicLinks";
var GET_DATA_URI = "/gadget/getData";
var GET_STORY_URI = "/gadget/getStoryInEpic";
var GET_CYCLE_URI = "/listcycle";
var GET_PRODUCT_URI = "/product/getall";
var GET_ASSIGNEE_URI = "/getassignee";
var GET_EXISTING_CYCLE_URI = "/cycleExisting";
var EPIC_TYPE;
var US_TYPE;
var ASSIGNEE_TYPE;
var CYCLE_TYPE;
var TEST_EPIC_ID = null;
var TEST_US_ID = null;
var TEST_ASSIGNEE_ID = null;
var TEST_CYCLE_ID = null;

$("#btn-add-gadget-epic").click(function(){
	$("#epic-test-execution-div").show();
	$(this).prop("disabled",true);
});

$("#btn-add-gadget-us").click(function(){
	$("#us-test-execution-div").show();
	$(this).prop("disabled",true);
});

$("#btn-add-gadget-assignee").click(function(){
	$("#assignee-test-execution-div").show();
	$(this).prop("disabled",true);
});

$("#btn-add-gadget-cycle").click(function(){
	$("#cycle-test-execution-div").show();
	$(this).prop("disabled",true);
});


function fetchGadgetList() {
	if (null != $("#dashboardId").val()) {
		$.ajax({
			url : GET_GADGETS_URI,
			data: {
				dashboardId: $("#dashboardId").val()
			},
			success : function(gadgetList) {
				if (debugAjaxResponse(gadgetList)) {
					return;
				}
				drawGadgets(gadgetList);
			},
			error : function(xhr, textStatus, error) {
				debugError(xhr, textStatus, error);
			},
		}).always(function(){
			console.log("Setting Products");
			if(TEST_EPIC_ID == null){
				$("#epicProduct").val("");
			}
			if(TEST_US_ID == null){
				$("#epicProduct").val("");
			}
			if(TEST_ASSIGNEE_ID == null){
				$("#assigneeProduct").val("");
			}
			if(TEST_CYCLE_ID == null){
				$("#cycleProduct").val("");
			}
		});
	}
}


function drawGadgets(gadgetList) {
	for (var i = 0; i < gadgetList.length; i++) {
		if (EPIC_TYPE == gadgetList[i]["type"]) {
			$("#epic-test-execution-div").show();
			$("#btn-add-gadget-epic").prop("disabled", true);
			hideEpicTable();
			TEST_EPIC_ID = gadgetList[i]["id"];
			if (gadgetList[i]["projectName"] != ""
					&& gadgetList[i]["projectName"] != null) {
				$("#epicProject").val(gadgetList[i]["projectName"]);
			}

			if (gadgetList[i]["products"] != null) {
				$("#epicProduct").val(gadgetList[i]["products"]);
			}
			else{
				$("#epicProduct").val("");
			}

			if (gadgetList[i]["release"] != null) {
				$("#epicRelease").val(gadgetList[i]["release"]);
			}

			if (gadgetList[i]["metrics"] != null) {
				$("#epicMetricMultiSelect").val(gadgetList[i]["metrics"]);
			}
			if (gadgetList[i]["selectAll"] == true) {
				$("epicCheckAll").prop("checked", true);
				$("#epic-link-container").hide();
			} else if (gadgetList[i]["epic"] != null) {
				$("#epicCheckAll").prop("checked", false);
				$("#epic-link-container").show();
				$("#epic-link-loader").hide();
				callAjaxOnEpicProjectAndRelease(gadgetList[i]["epic"]);
			}
			drawEpicTable(gadgetList[i]["id"], gadgetList[i]["metrics"]);
		} else if (US_TYPE == gadgetList[i]["type"]) {
			TEST_US_ID = gadgetList[i]["id"];
			$("#btn-add-gadget-us").prop("disabled", true);
			$("#us-test-execution-div").show();
			if (gadgetList[i]["projectName"] != null) {
				$("#usProject").val(gadgetList[i]["projectName"]);
			}

			if (gadgetList[i]["release"] != null) {
				$("#usRelease").val(gadgetList[i]["release"]);
			}

			if (gadgetList[i]["products"] != null) {
				$("#usProduct").val(gadgetList[i]["products"]);
			}
			
			if (gadgetList[i]["metrics"] != null) {
				$("#usMetricMultiSelect").val(gadgetList[i]["metrics"]);
			}
			
			if(gadgetList[i]["selectAllEpic"] != false){
				$("#usCheckAllEpic").prop("checked", true);
				$("#us-epic-container").fadeOut();
			}
			else if (gadgetList[i]["epic"] != null) {
				$("#usCheckAllEpic").prop("checked", false);
				$("#us-epic-container").fadeIn();
				$("#us-epic-loader").hide();
				callAjaxOnUsProjectAndRelease(gadgetList[i]["epic"], gadgetList[i]["stories"]);
			}
			
			if(gadgetList[i]["selectAllStory"] != false){
				$("#usCheckAllStory").prop("checked", true);
				$("#us-container").fadeOut();
			}
			else if (gadgetList[i]["stories"] != null) {
				$("#usCheckAllStory").prop("checked", false);
				$("#us-container").fadeIn();
				$("#us-us-loader").hide();
			}

			
			console.log("prepare to draw table");
			console.log(gadgetList[i]["id"]);
			drawUsTable(gadgetList[i]["id"], gadgetList[i]["metrics"]);
		} else if (ASSIGNEE_TYPE == gadgetList[i]["type"]) {
			$("#btn-add-gadget-assignee").prop("disabled", true);
			TEST_ASSIGNEE_ID = gadgetList[i]["id"];
			$("#assignee-test-execution-div").show();
			if (gadgetList[i]["projectName"] != null) {
				$("#assigneeProject").val(gadgetList[i]["projectName"]);
			}

			if (gadgetList[i]["release"] != null) {
				$("#assigneeRelease").val(gadgetList[i]["release"]);
			}

			if (gadgetList[i]["products"] != null) {
				$("#assigneeProduct").val(gadgetList[i]["products"]);
			}

			if (gadgetList[i]["assignee"] != null) {
				appendToSelect(true, gadgetList[i]["assignee"],
						"#assigneeMultiSelect");
				$("#assigneeMultiSelect").val(gadgetList[i]["assignee"]);
			}

			if (gadgetList[i]["metrics"] != null) {
				$("#assigneeMetricMultiSelect").val(gadgetList[i]["metrics"]);
			}
			if(gadgetList[i]["selectAllTestCycle"] != false){
				$("#assigneeCheckAllCycle").prop("checked", true);
				$("#assignee-cycle-container").fadeOut();
			}
			else if (gadgetList[i]["cycles"] != null) {
				$("#assigneeCheckAllCycle").prop("checked", true);
				$("#assignee-cycle-container").fadeOut();
				$("#assignee-cycle-container").hide();
				appendToSelect(true, gadgetList[i]["cycles"], "#assigneeCycle");
				$("#assigneeCycle").val(gadgetList[i]["cycles"]);
			}
			console.log("prepare to draw table");
			drawAssigneeTable(gadgetList[i]["id"], gadgetList[i]["metrics"]);
		} else if (CYCLE_TYPE == gadgetList[i]["type"]) {
			$("#btn-add-gadget-cycle").prop("disabled", true);
			TEST_CYCLE_ID = gadgetList[i]["id"];
			$("#cycle-test-execution-div").show();
			if (gadgetList[i]["projectName"] != ""
					&& gadgetList[i]["projectName"] != null) {
				$("#cycleProject").val(gadgetList[i]["projectName"]);
			}

			if (gadgetList[i]["products"] != ""
					&& gadgetList[i]["products"] != null) {
				$("#cycleProduct").val(gadgetList[i]["products"]);
			}

			if (gadgetList[i]["release"] != ""
					&& gadgetList[i]["release"] != null) {
				$("#cycleRelease").val(gadgetList[i]["release"]);
			}
			if (gadgetList[i]["selectAllCycle"] == true) {
				$("#cycleCheckAll").prop("checked", true);
				$("#cycle-container").fadeOut();
			} else if (gadgetList[i]["cycles"] != ""
						&& gadgetList[i]["cycles"] != null) {
				$("#cycleCheckAll").prop("checked", false);
				$("#cycle-container").fadeIn();
				$("#cycle-loader").hide();
					appendToSelect(true, gadgetList[i]["cycles"],
							"#cycleMultiSelect");
					$("#cycleMultiSelect").val(gadgetList[i]["cycles"]);
				}

			if (gadgetList[i]["metrics"] != ""
					&& gadgetList[i]["metrics"] != null) {
				$("#cycleMetricMultiSelect").val(gadgetList[i]["metrics"]);
			}
			console.log("prepare to draw table");
			drawCycleTable(gadgetList[i]["id"], gadgetList[i]["metrics"]);
		}
	}
}

// On document ready, append projects list to 4 widget project field
$(document).ready(function() {
	if("dashboard" != window.location.href.split('/')[3] ){
		return;
	}
	else{
		setUpMinorSetting();
		setUpInitialAjax();
		fetchGadgetList();		
	}
});

// Create new table for each epic or cycle in Story table and Assignee table
/*
 * id: string: gadget id to create a custom table id title: string: epic link or
 * cycle name on top of table container: string: id of the <div> to append this
 * table to
 */
function appendTemplateTable(id, title, container) {
  $('<h4><b>' + title + '</b></h4>').appendTo(container);
  $('<table id="' + id + '" class="display"></table>').appendTo(container);
  $('<br><hr>').appendTo(container);
}

// Append list to a target "<select>" container
/*
 * cleanOption: boolean : clean up previous <option> in <select>
 * list: array: data to append
 * targetId: string: <select> id.
 */
function appendToSelect(cleanOption, list, targetId) {
  if (cleanOption == true) {
    cleanSelect(targetId);
  }
  for (var z = 0; z < list.length; z++) {
    $('<option value="' + list[z] + '" selected>' + list[z] + '</option>')
      .appendTo(targetId);
  }
}

//clean up previous <option> in <select>
/*
 * targetId: string: <select> id.
 */
function cleanSelect(targetId) {
  $(targetId).find('option').remove().end();
}


/*
 * Get column array of table to hide from view 
 * metricArray: list: a gadget's metric list
 * isCycleOrAssignee: boolean: column number of Cycle-Assignee and UserStory-Epic table is different.
 * 								put a condition here to check
 */
function getColumnArray(metricArray, isCycleOrAssignee) {
  var columnList = [];

  if (metricArray == null) {
    alert("There is no metric to show.");
    return;
  }
  if (!isCycleOrAssignee) {
    if ($.inArray('SUMMARY', metricArray) == -1) {
      columnList.push(1);
    }
    if ($.inArray('PRIORITY', metricArray) == -1) {
      columnList.push(2);
    }
    if ($.inArray('UNEXECUTED', metricArray) == -1) {
      columnList.push(3);
    }
    if ($.inArray('FAILED', metricArray) == -1) {
      columnList.push(4);
    }
    if ($.inArray('WIP', metricArray) == -1) {
      columnList.push(5);
    }
    if ($.inArray('BLOCKED', metricArray) == -1) {
      columnList.push(6);
    }
    if ($.inArray('PASSED', metricArray) == -1) {
      columnList.push(7);
    }

    if ($.inArray('PLANNED', metricArray) == -1) {
      columnList.push(8);
    }
    if ($.inArray('UNPLANNED', metricArray) == -1) {
      columnList.push(9);
    }
  } else {
    if ($.inArray('UNEXECUTED', metricArray) == -1) {
      columnList.push(1);
    }
    if ($.inArray('FAILED', metricArray) == -1) {
      columnList.push(2);
    }
    if ($.inArray('WIP', metricArray) == -1) {
      columnList.push(3);
    }
    if ($.inArray('BLOCKED', metricArray) == -1) {
      columnList.push(4);
    }
    if ($.inArray('PASSED', metricArray) == -1) {
      columnList.push(5);
    }
  }

  return columnList;
}

/*
 * Show all hidden table column
 * table: datatable object: designated table to show all table column
 * isCycleOrAssignee: boolean: is this epic-us or cycle-assignee table?
 */
function resetTableColumns(table, isCycleOrAssignee){
	var list;
	if(table == null) {
		return
	}
	else if(!isCycleOrAssignee){
		list = [1,2,3,4,5,6,7,8,9];
	}
	else{
		list = [1,2,3,4,5];
	}
	
	for(var i = 1; i <= list.length; i++){
		var column = table.column(i);
		if(!column.visible()){
			column.visible( ! column.visible() );
		}
	}
}

function debugAjaxResponse(data) {
  if (data == null) {
    alert("Ajax response error: Server returned null response");
    return true;
  } else if (data["type"] == "error") {
    alert("Ajax response error: " + data["data"]);
    return true;
  }
  console.log(data);

  return false;
}

window.onerror = function(msg, url, linenumber) {
    alert('Unhandled error message: '+msg+'\nURL: '+url+'\nLine Number: '+linenumber);
    return true;
}

function createIssueLinks(data, displayOrType, rowData, setting){
	var issue="issue in(";
	var htmlString = [];
	if(data['total'] == 1){
		htmlString.push('<a href="',GREENHOPPER_ISSUE_API_LINK, issue, data["issues"][0], ')">' ,data["total"],'</a>');
		return htmlString.join("");
	}
	else if (data['total'] > 1){
		htmlString.push('<a href="', GREENHOPPER_ISSUE_API_LINK, issue);
		for(var i = 0; i < data["issues"].length; i++){
			var isLastIndex;
			
			htmlString.push(data["issues"][i]);
			isLastIndex = ((data["issues"].length - i) == 1)? true : false; 
			if(!isLastIndex){
				htmlString.push(",");
			}
		}
		htmlString.push(')">',data["total"],'</a>')
		return htmlString.join("");
	}
	return data["total"];
}

function createIssueLinkForTitle(data){
	var htmlString = [];
	htmlString.push('<a href="', GREENHOPPER_BROWSE_ISSUE_LINK, data["key"],'">',data["key"],'</a>' );
	return htmlString.join("");
}

function debugError(xhr, textStatus, error){
	alert("Request status: " + xhr.statusText + "\nDescription: " + textStatus+ "\nError:" + error);
	console.log(xhr);
	console.log(textStatus);
	console.log(error);
}

function setUpInitialAjax(){
	if ($('#epicProject').length != 0) {
		$.get("/listproject", function(data) {
			if (debugAjaxResponse(data)) {
				return;
			}
			data.sort();
			appendToSelect(false, data, "#epicProject");
			$("#epicProject").val("FNMS 557x");
			appendToSelect(false, data, "#usProject");
			$("#usProject").val("FNMS 557x");
			appendToSelect(false, data, "#cycleProject");
			$("#cycleProject").val("FNMS 557x");
			appendToSelect(false, data, "#assigneeProject");
			$("#assigneeProject").val("FNMS 557x");
		});
	}
	
	$.ajax({
		url: GET_PRODUCT_URI,
		error: function(xhr, textStatus, error){
			debugError(xhr, textStatus, error);
		},
		success: function(data){
			if(debugAjaxResponse(data)){
				return;
			}
			else{
				console.log(data);
				appendToSelect(false, data["data"], "#epicProduct");

				appendToSelect(false, data["data"], "#usProduct");

				appendToSelect(false, data["data"], "#assigneeProduct");

				appendToSelect(false, data["data"], "#cycleProduct");

			}
		}
	});
	getExistingCycleAssigneeWidget();
	getExistingCycleList();
}

function setUpMinorSetting(){
	var productPage;
	var gadgetType;
	productPage = location.protocol + "//" + location.host+ "/product";
	console.log(productPage);
	
	$(".btn-to-product").attr("href", productPage);
	if($("#userRole").val() == undefined){
		$(".btn-to-product").attr("href", "");
	}
	
	$('[data-toggle="tooltip"]').tooltip(); 
	gadgetType = $(".gadgetType");
	if(gadgetType.length != 0){
		EPIC_TYPE = gadgetType[0].value;
		US_TYPE = gadgetType[1].value;
		CYCLE_TYPE = gadgetType[2].value;
		ASSIGNEE_TYPE = gadgetType[3].value;
	}
}