/*
 * Set listerners for project, release, product field
 */
$("#assigneeProject").change(function() {

});

$("#assigneeRelease").change(function() {

});

$("#assigneeProduct").change(function() {

});
/*
 * Set listeners for add, remove, add all and remove all test cycle list
 */
$("#assignee-add-cycle-btn").click(function() {
  var options = $("#assigneeCycleAvailable option:selected").clone();
  if (options.length == 0) {
    return;
  }
  $("#assigneeCycle").append(options);
  $("#assigneeCycleAvailable option:selected").remove();
});

$("#assignee-remove-cycle-btn").click(function() {
  var options = $("#assigneeCycle option:selected").clone();
  if (options.length == 0) {
    return;
  }
  $("#assigneeCycleAvailable").append(options);
  $("#assigneeCycle option:selected").remove();
});

$("#assignee-add-all-cycle-btn").click(function() {
  var options = $("#assigneeCycleAvailable option").clone();
  if (options.length == 0) {
    return;
  }
  $("#assigneeCycle").find("option").remove().end();
  $("#assigneeCycle").append(options);
  $("#assigneeCycleAvailable option").remove();
});

$("#assignee-remove-all-cycle-btn").click(function() {
  var options = $("#assigneeCycle option").clone();
  if (options.length == 0) {
    return;
  }
  $("#assigneeCycleAvailable").find("option").remove().end();
  $("#assigneeCycleAvailable").append(options);
  $("#assigneeCycle option").remove();
});

/*
 * Set listener for Update button
 */

$("#assignee-update-btn").click(function() {
  $(this).prop("disabled", true);
  var jsonString = createJsonStringFromAssigneeInput();
  updateAssigneeGadget(jsonString);
});

function createJsonStringFromAssigneeInput() {
  var object = {};
  var options;
  var values;
  var jsonString;
  if (null == $("#dashboardId").val()) {
    alert("No valid dashboard id provided.");
    $("#assignee-update-btn").prop("disabled", false);
    return;
  } else if ($("#assigneeProject").val() == null || $("#assigneeProject").val() == "") {
    alert("No project selected");
    $("#assignee-update-btn").prop("disabled", false);
    return;
  } else if ($("#assigneeRelease").val() == null || $("#assigneeRelease").val() == "") {
    alert("No release selected");
    $("#assignee-update-btn").prop("disabled", false);
    return;
  } else if ($("#assigneeProduct").val() == null || $("#assigneeProduct").val() == "") {
    alert("No product selected");
    $("#assignee-update-btn").prop("disabled", false);
    return;
  } else if ($("#assigneeCycle option").length == 0 && !$("#assigneeCheckAllCycle").prop("checked")) {
    $("#assignee-update-btn").prop("disabled", false);
    alert("No cycle selected");
    return;
  } else if ($("#assigneeMultiSelect").val() == null && !$("#assigneeCheckAll").prop("checked")) {
    alert("No assignee selected");
    $("#assignee-update-btn").prop("disabled", false);
    return;
  } else if ($("#assigneeMetricMultiSelect").val() == null) {
    alert("No test metric selected");
    $("#assignee-update-btn").prop("disabled", false);
    return;
  }
  object['dashboardId'] = $("#dashboardId").val();
  options = $("#assigneeCycle option");
  values = $.map(options, function(option) {
    return option.value;
  });

  object['id'] = TEST_ASSIGNEE_ID;
  object['projectName'] = $("#assigneeProject").val();
  object['release'] = $("#assigneeRelease").val();
  object['products'] = [$("#assigneeProduct").val()];
  object['metrics'] = $("#assigneeMetricMultiSelect").val();

  if ($("#assigneeCheckAllCycle").prop("checked")) {
    object['selectAllTestCycle'] = true;
  } else {
    object['cycles'] = values;
  }
  if ($("#assigneeCheckAll").prop("checked")) {
    object['selectAllAssignee'] = true;
  } else {
    object['assignee'] = $("#assigneeMultiSelect").val();
  }

  jsonString = JSON.stringify(object);
  return jsonString;
}

function updateAssigneeGadget(jsonString) {
  if (jsonString != null) {
    $.ajax({
      url: SAVE_GADGET_URI,
      method: 'POST',
      data: {
        type: ASSIGNEE_TYPE,
        data: jsonString
      },
      beforeSend: function() {
        hideAssigneeTable();
      },
      error: function(xhr, textStatus, error) {
        debugError(xhr, textStatus, error);
        $("#assignee-update-btn").prop("disabled", false);
        showAssigneeTable();
      },
      success: function(data) {
        if (debugAjaxResponse(data)) {
          $("#assignee-update-btn").prop("disabled", false);
          showAssigneeTable();
          return;
        } else {
          alert("Gadget updated succesfully")
          TEST_ASSIGNEE_ID = data["data"];
          drawAssigneeTable(data["data"], $("#assigneeMetricMultiSelect").val());
        }

      }
    }).always(function(returnMessage) {
      console.log(jsonString);
    });
  }
}

/*
 * Set listerners for input check option 
 */
$("#assigneeCheckAll").click(function() {
  if ($(this).prop("checked") == true) {
    $("#assignee-container").fadeOut();
  } else {
    $("#assignee-container").fadeIn();
  }
});

$("#assigneeCheckAllCycle").click(function() {
  if ($(this).prop("checked")) {
    $("#assignee-cycle-container").fadeOut();
    addAllCycle();
  } else {
    $("#assignee-cycle-container").fadeIn();
    if ($("#assigneeCycle option").length == 0 && $("#assigneeCycleAvailable option").length == 0) {
      getExistingCycleAssigneeWidget();
    }
  }
});

function drawAssigneeTable(gadgetId, metricArray) {
  var columnList = getColumnArray(metricArray, true);
  var jsonObjectForAssigneeTable;

  if (GLOBAL_ASSIGNEE_TABLES_AJAX.loading == true) {
    GLOBAL_ASSIGNEE_TABLES_AJAX.ajax.abort();
  }

  GLOBAL_ASSIGNEE_TABLES_AJAX.ajax = $
    .ajax({
      url: GET_DATA_URI,
      method: "GET",
      data: {
        id: gadgetId
      },
      beforeSend: function() {
        GLOBAL_ASSIGNEE_TABLES_AJAX.loading = true;
        hideAssigneeTable();
      },
      error: function(xhr, textStatus, error) {
        debugError(xhr, textStatus, error);
        showAssigneeTable();
      },
      success: function(responseData) {
        GLOBAL_ASSIGNEE_TABLES_AJAX.loading = false;
        var index = 0;
        $("#assignee-table-container").html("");
        if (debugAjaxResponse(responseData)) {
          $("#assignee-update-btn").prop("disabled", false);
          showAssigneeTable();
          return;
        }

        console.log("Get individual data ok");

        jsonObjectForAssigneeTable = responseData;
        $
          .each(
            jsonObjectForAssigneeTable["data"],
            function(cycleKey, assigneeArray) {
              console
                .log(assigneeArray["issueData"].length);
              if (assigneeArray["issueData"].length != 0) {
                var customTableId = "assignee-table-" + index;
                var assigneeTableDataSet = [];
                var assigneeIndividualTable;

                appendTemplateTable(
                  customTableId,
                  cycleKey,
                  "#assignee-table-container");
                $("#" + customTableId)
                  .append(
                    TEMPLATE_HEADER_FOOTER_1);

                console
                  .log("Pass each function");

                for (var i = 0; i < assigneeArray["issueData"].length; i++) {
                  var anAssigneeDataSet = [];
                  anAssigneeDataSet
                    .push(assigneeArray["issueData"][i]["key"]["key"]);
                  anAssigneeDataSet
                    .push(assigneeArray["issueData"][i]["unexecuted"]);
                  anAssigneeDataSet
                    .push(assigneeArray["issueData"][i]["failed"]);
                  anAssigneeDataSet
                    .push(assigneeArray["issueData"][i]["wip"]);
                  anAssigneeDataSet
                    .push(assigneeArray["issueData"][i]["blocked"]);
                  anAssigneeDataSet
                    .push(assigneeArray["issueData"][i]["passed"]);
                  anAssigneeDataSet
                    .push(assigneeArray["issueData"][i]["planned"]);
                  anAssigneeDataSet
                    .push(assigneeArray["issueData"][i]["unplanned"]);
                  assigneeTableDataSet
                    .push(anAssigneeDataSet);
                }

                assigneeIndividualTable = $(
                    "#" + customTableId)
                  .DataTable({
                    bAutoWidth: false,
                    data: assigneeTableDataSet,
                    columns: [{
                      title: "Assignee"
                    }, {
                      title: "UNEXECUTED",
                      "render": function(
                        data,
                        displayOrType,
                        rowData,
                        setting) {
                        return createIssueLinks(
                          data,
                          displayOrType,
                          rowData,
                          setting);
                      }
                    }, {
                      title: "FAILED",
                      "render": function(
                        data,
                        displayOrType,
                        rowData,
                        setting) {
                        return createIssueLinks(
                          data,
                          displayOrType,
                          rowData,
                          setting);
                      }
                    }, {
                      title: "WIP",
                      "render": function(
                        data,
                        displayOrType,
                        rowData,
                        setting) {
                        return createIssueLinks(
                          data,
                          displayOrType,
                          rowData,
                          setting);
                      }
                    }, {
                      title: "BLOCKED",
                      "render": function(
                        data,
                        displayOrType,
                        rowData,
                        setting) {
                        return createIssueLinks(
                          data,
                          displayOrType,
                          rowData,
                          setting);
                      }
                    }, {
                      title: "PASSED",
                      "render": function(
                        data,
                        displayOrType,
                        rowData,
                        setting) {
                        return createIssueLinks(
                          data,
                          displayOrType,
                          rowData,
                          setting);
                      }
                    }]
                  });
                assigneeIndividualTable
                  .columns(columnList)
                  .visible(false);
                index++;
              }
            });
      }
    }).always(function(responseData) {
      $("#assignee-update-btn").prop("disabled", false);
      showAssigneeTable();
    });
}

function callAjaxOnAssigneeTable() {
  $.ajax({
    url: GET_GADGETS_URI,
    data: {
      dashboardId: $("#dashboardId").val()
    },
    success: function(gadgetList) {
      if (debugAjaxResponse(gadgetList)) {
        $("#assignee-update-btn").prop("disabled", false);
        showAssigneeTable();
        return;
      }
      drawAssigneeGadget(gadgetList);
    },
    error: function(xhr, textStatus, error) {
      debugError(xhr, textStatus, error);
      $("#assignee-update-btn").prop("disabled", false);
    },
    beforeSend: function() {
      hideAssigneeTable();
    }
  }).always(function(gadgetList) {
    console.log(gadgetList);
  });
}

function callAjaxOnAssigneeProjectAndRelease() {
  if ($("#assigneeRelease").val() == null || $("#assigneeProject").val() == null || $("#assigneeProduct").val() == null) {
    return;
  } else if ($("#assigneeRelease").val() == "" || $("#assigneeProject").val() == "" || $("#assigneeProduct").val() == "") {
    return;
  }

  if (!$("#assigneeCheckAllCycle").prop("checked")) {
    $.ajax({
      url: GET_CYCLE_URI,
      data: {
        project: $("#assigneeProject").val(),
        release: $("#assigneeRelease").val(),
        products: JSON.stringify([$("#assigneeProduct").val()])
      },

      beforeSend: function() {
        hideAssigneeCycle();
      },
      success: function(data) {
        if (debugAjaxResponse(data)) {
          return;
        }
        data.sort();
        appendToSelect(true, data, "#assigneeCycleAvailable");
        $("#assigneeCycle").find("option").remove().end();
      },
      error: function(xhr, textStatus, error) {
        debugError(xhr, textStatus, error);
        showAssigneeCycle();
        console.log(data);
      }
    }).always(function() {
      showAssigneeCycle();
    });
  }
  if (!$("#assigneeCheckAll").prop("checked")) {

    $.ajax({
      url: GET_ASSIGNEE_URI,
      data: {
        project: $("#assigneeProject").val(),
        release: $("#assigneeRelease").val()
      },

      beforeSend: function() {
        hideAssignee();
      },
      success: function(data) {
        if (debugAjaxResponse(data)) {
          return;
        }
        var tempAssigneeList = [];
        $.each(data, function(key, map) {
          tempAssigneeList.push(map["assignee"]);
        });
        tempAssigneeList.sort();
        appendToSelect(true, tempAssigneeList, "#assigneeMultiSelect");
      },
      error: function(xhr, textStatus, error) {
        debugError(xhr, textStatus, error);
        console.log(data);
      }
    }).always(function() {
      showAssignee();
    });
  }
}

function getExistingCycleAssigneeWidget() {
  if (!$("#assigneeCheckAllCycle").prop("checked")) {
    $.ajax({
      url: GET_EXISTING_CYCLE_URI,
      beforeSend: function() {
        hideAssigneeCycle();
      },
      success: function(data) {
        if (debugAjaxResponse(data)) {
          return;
        }
        data.sort();
        appendToSelect(true, data, "#assigneeCycleAvailable");
        $("#assigneeCycle").find("option").remove().end();
      },
      error: function(xhr, textStatus, error) {
        debugError(xhr, textStatus, error);
        showAssigneeCycle();
        console.log(data);
      }
    }).always(function() {
      showAssigneeCycle();
    });
  }
  if (!$("#assigneeCheckAll").prop("checked")) {

    $.ajax({
      url: "/getassignee?",
      data: {
        project: $("#assigneeProject").val(),
        release: $("#assigneeRelease").val()
      },

      beforeSend: function() {
        hideAssignee();
      },
      success: function(data) {
        if (debugAjaxResponse(data)) {
          return;
        }
        var tempAssigneeList = [];
        $.each(data, function(key, map) {
          tempAssigneeList.push(map["assignee"]);
        });
        tempAssigneeList.sort();
        appendToSelect(true, tempAssigneeList, "#assigneeMultiSelect");
      },
      error: function(xhr, textStatus, error) {
        debugError(xhr, textStatus, error);
        console.log(data);
      }
    }).always(function() {
      showAssignee();
    });
  }
}

function addAllCycle() {
  var options = $("#assigneeCycleAvailable option").clone();
  if (options.length == 0) {
    return;
  }
  $("#assigneeCycle").append(options);
  $("#assigneeCycleAvailable option").remove();
}

function removeAllCycle() {
  var options = $("#assigneeCycle option").clone();
  if (options.length == 0) {
    return;
  }
  $("#assigneeCycleAvailable").append(options);
  $("#assigneeCycle option").remove();
}

function hideAssigneeCycle() {
  $('#assignee-cycle-available-div').fadeOut();
  $("#assignee-cycle-loader").fadeIn();
}

function showAssigneeCycle() {
  $('#assignee-cycle-available-div').fadeIn();
  $("#assignee-cycle-loader").fadeOut();
}

function hideAssignee() {
  $('#assigneeMultiSelect').fadeOut();
  $("#assignee-loader").fadeIn();
}

function showAssignee() {
  $('#assigneeMultiSelect').fadeIn();
  $("#assignee-loader").fadeOut();
}

function hideAssigneeTable() {
  $('#assignee-table-container').fadeOut();
  $("#assignee-table-loader").fadeIn();
}

function showAssigneeTable() {
  $('#assignee-table-container').fadeIn();
  $("#assignee-table-loader").fadeOut();
}
