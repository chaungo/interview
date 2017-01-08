/*
 * List of html id for cycle gadget
 * #cycle-test-execution-div
 * #cycleProject
 * #cycleRelease
 * #cycleProduct
 * #cycle-container
 * #cycleLoader
 * #cycleMultiSelect
 * #cycleCheckAll
 * #cycleMetricMultiSelect
 * #cycle-table-container
 * #cycleTable
 * #cycle-update-btn
 * #cycle-table-loader
 * 
 */


$("#cycleProject").change(function() {

});

$("#cycleRelease").change(function() {

});

$("#cycleProduct").change(function() {

});

$("#cycle-update-btn").click(function() {
  $(this).prop("disabled", true);
  var jsonString = createJsonStringObjectFromCycleInput();
  callAjaxToUpdateCycle(jsonString);
});

$("#cycleCheckAll").click(function() {
  if ($(this).prop("checked")) {
    $("#cycle-container").fadeOut();
  } else {
    $("#cycle-container").fadeIn();
    if ($("#cycleMultiSelect option").length == 0) {
      getExistingCycleList();
    }

  }
});

function createJsonStringObjectFromCycleInput() {
  var object = {};
  if (null == $("#dashboardId").val()) {
    alert("No valid dashboard id provided.");
    $("#cycle-update-btn").prop("disabled", false);
    return;
  } else if ($("#cycleProject").val() == null || $("#cycleProject").val() == "") {
    alert("No project selected");
    $("#cycle-update-btn").prop("disabled", false);
    return;
  } else if ($("#cycleRelease").val() == null || $("#cycleRelease").val() == "") {
    alert("No release selected");
    $("#cycle-update-btn").prop("disabled", false);
    return;
  } else if ($("#cycleProduct").val() == null || $("#cycleProduct").val() == "") {
    alert("No Products selected");
    $("#cycle-update-btn").prop("disabled", false);
    return;
  } else if ($("#cycleMultiSelect").val() == null && !$("#cycleCheckAll").prop("checked")) {
    alert("No cycle selected");
    $("#cycle-update-btn").prop("disabled", false);
    return;
  } else if ($("#cycleMetricMultiSelect").val() == null) {
    alert("No metric selected for this widget");
    $("#cycle-update-btn").prop("disabled", false);
    return;
  }

  object['id'] = TEST_CYCLE_ID;
  object['dashboardId'] = $("#dashboardId").val();
  object['projectName'] = $("#cycleProject").val();
  object['release'] = $("#cycleRelease").val();
  object['products'] = [$("#cycleProduct").val()];
  object['metrics'] = $("#cycleMetricMultiSelect").val();
  if ($("#cycleCheckAll").prop("checked")) {
    object['selectAllCycle'] = true;
  } else {
    object['cycles'] = $("#cycleMultiSelect").val();
  }
  var jsonString = JSON.stringify(object);
  return jsonString
}

function callAjaxToUpdateCycle(jsonString) {
  if (jsonString != null && jsonString != "") {
    $.ajax({
      url: SAVE_GADGET_URI,
      method: 'POST',
      data: {
        type: CYCLE_TYPE,
        data: jsonString
      },
      beforeSend: function() {
        hideCycleTable();
      },
      error: function(xhr, textStatus, error) {
        $(this).prop("disabled", true);
        debugError(xhr, textStatus, error);
        $("#cycle-update-btn").prop("disabled", false);
        showCycleTable();
      },
      success: function(data) {
        if (debugAjaxResponse(data)) {
          $("#cycle-update-btn").prop("disabled", false);
          showCycleTable();
          return;
        } else {
          alert("Gadget updated succesfully");
          TEST_CYCLE_ID = data["data"];
          drawCycleTable(TEST_CYCLE_ID, $("#cycleMetricMultiSelect").val());
        }

      }
    }).always(function() {
      console.log(jsonString);
    });
  }
}

function drawCycleTable(gadgetId, metricArray) {
  var columnList = getColumnArray(metricArray, true);
  resetTableColumns(GLOBAL_CYCLE_TABLE, true);
  if (GLOBAL_CYCLE_TABLE != null) {
    console.log(GLOBAL_CYCLE_TABLE);
    hideCycleTable();
    GLOBAL_CYCLE_TABLE.ajax.reload(function() {
      $("#cycle-update-btn").prop("disabled", false);
      showCycleTable();
    });
    GLOBAL_CYCLE_TABLE.columns(columnList).visible(false);
  } else {
    hideCycleTable();
    GLOBAL_CYCLE_TABLE = $('#cycle-table').on(
      'error.dt',
      function(e, settings, techNote, message) {
        console.log('An error has been reported by DataTables: ',
          message);
        $("#cycle-update-btn").prop("disabled", false);
        showCycleTable();
      }).DataTable({
      "fnDrawCallback": function(oSettings) {
        $("#cycle-update-btn").prop("disabled", false);
        showCycleTable();
      },
      bAutoWidth: false,
      "ajax": {
        url: "/gadget/getData",
        data: {
          "id": gadgetId
        },
        dataSrc: function(responseJson) {
          if (debugAjaxResponse(responseJson)) {
            $("#cycle-update-btn").prop("disabled", false);
            showCycleTable();
            return [];
          }
          var tempArray = [];
          $.each(responseJson["data"], function(k1, v1) {
            $.each(v1["issueData"], function(k2, v2) {
              tempArray.push(v2);
            });
          });
          console.log(tempArray);
          return tempArray;
        }
      },
      "columns": [{
        "data": "key.key"
      }, {
        "data": "unexecuted",
        "render": function(data, displayOrType, rowData, setting) {
          return createIssueLinks(data, displayOrType, rowData, setting);
        }
      }, {
        "data": "failed",
        "render": function(data, displayOrType, rowData, setting) {
          return createIssueLinks(data, displayOrType, rowData, setting);
        }
      }, {
        "data": "wip",
        "render": function(data, displayOrType, rowData, setting) {
          return createIssueLinks(data, displayOrType, rowData, setting);
        }
      }, {
        "data": "blocked",
        "render": function(data, displayOrType, rowData, setting) {
          return createIssueLinks(data, displayOrType, rowData, setting);
        }
      }, {
        "data": "passed",
        "render": function(data, displayOrType, rowData, setting) {
          return createIssueLinks(data, displayOrType, rowData, setting);
        }
      }]

    });
    GLOBAL_CYCLE_TABLE.columns(columnList).visible(false);
  }
}

function callAjaxOnCycleProjectAndRelease() {
  if ($("#cycleRelease").val() == null || $("#cycleProduct").val() == null || $("#cycleRelease").val() == null) {
    return;
  } else if ($("#cycleRelease").val() == "" || $("#cycleProduct").val() == "" || $("#cycleRelease").val() == "") {
    return;
  }
  if (!$("#cycleCheckAll").prop("checked")) {
    $.ajax({
      url: "/listcycle?",
      data: {
        project: $("#cycleProject").val(),
        release: $("#cycleRelease").val(),
        products: JSON.stringify([$("#cycleProduct").val()])
      },

      beforeSend: function() {
        hideCycleSelect();
      },
      success: function(data) {
        if (debugAjaxResponse(data)) {
          return;
        } else {
          data.sort();
          appendToSelect(true, data, "#cycleMultiSelect");
        }

      },
      error: function(xhr, textStatus, error) {
        debugError(xhr, textStatus, error);
      }
    }).always(function() {
      showCycleSelect();
    });
  }
}

function getExistingCycleList() {
  if (!$("#cycleCheckAll").prop("checked")) {
    $.ajax({
      url: GET_EXISTING_CYCLE_URI,
      beforeSend: function() {
        hideCycleSelect();
      },
      success: function(data) {
        if (debugAjaxResponse(data)) {
          return;
        }
        data.sort();
        appendToSelect(true, data, "#cycleMultiSelect");
      },
      error: function(xhr, textStatus, error) {
        debugError(xhr, textStatus, error);
      }
    }).always(function() {
      showCycleSelect();
    });
  }
}

function hideCycleSelect() {
  $('#cycleMultiSelect').fadeOut();
  $("#cycle-loader").fadeIn();
}

function showCycleSelect() {
  $('#cycleMultiSelect').fadeIn();
  $("#cycle-loader").fadeOut();
}

function showCycleTable() {
  $('#cycle-table-container').fadeIn();
  $("#cycle-table-loader").fadeOut();
}

function hideCycleTable() {
  $('#cycle-table-container').fadeOut();
  $("#cycle-table-loader").fadeIn();
}
