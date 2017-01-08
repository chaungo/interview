/*
 * List of html id for epic gadget
 * #epic-test-execution-div
 * #epicProject
 * #epicRelease
 * #epicProduct
 * #epic-link-container
 * #epic-link-loader
 * #epicMultiSelect
 * #epicCheckAll
 * #epicMetricMultiSelect
 * #epic-add-gadget
 * #epic-table-container
 * #epic-table
 * #epic-table-loader
 */

/*
 * Set listeners for project, release, product select option.
 */
$("#epicProject").change(function() {
  callAjaxOnEpicProjectAndRelease(null);
});

$("#epicRelease").change(function() {
  callAjaxOnEpicProjectAndRelease(null);
});

$("#epicProduct").change(function() {
  callAjaxOnEpicProjectAndRelease(null);
});

/*
 * Set listener for update button
 */
$("#epic-add-gadget").click(function() {
  $(this).prop("disabled", true);
  var jsonString = createJsonStringObjectFromEpicInput();
  callAjaxToUpdateGadget(jsonString);
});

/*
 * Set listener for input check option.
 */
$("#epicCheckAll").click(function() {
  if ($(this).prop("checked")) {
    $("#epic-link-container").fadeOut();
  } else {
    $("#epic-link-container").fadeIn();
    if ($("#epicMultiSelect option").length == 0) {
      callAjaxOnEpicProjectAndRelease(null);
    }
  }
});

function callAjaxOnEpicProjectAndRelease(selectList) {
  if ($("#epicProject").val() == null || $("#epicRelease").val() == null || $("#epicProduct").val() == null) {
    return;
  } else if ($("#epicProject").val() == "" || $("#epicRelease").val() == "" || $("#epicProduct").val() == "") {
    return;

  }

  if (!$("#epicCheckAll").prop("checked")) {
    $.ajax({
      url: GET_EPIC_URI,
      data: {
        project: $("#epicProject").val(),
        release: $("#epicRelease").val(),
        products: JSON.stringify([$("#epicProduct").val()])
      },

      beforeSend: function() {
        hideEpicLinks();
      },
      success: function(data) {
        if (debugAjaxResponse(data)) {
          return;
        } else {
          data.sort();
          appendToSelect(true, data, "#epicMultiSelect");
          if (selectList != null) {
            $("#epicMultiSelect").val(selectList);
          }
        }

      }
    }).always(function(data) {
      showEpicLinks();
    });
  }
}

function createJsonStringObjectFromEpicInput() {
  var object = {};
  if (null == $("#dashboardId").val()) {
    alert("No valid dashboard id provided.");
    $("#epic-add-gadget").prop("disabled", false);
    return;
  } else if ($("#epicProject").val() == null || $("#epicProject").val() == "") {
    alert("No project selected");
    $("#epic-add-gadget").prop("disabled", false);
    return;
  } else if ($("#epicRelease").val() == null || $("#epicRelease").val() == "") {
    alert("No release selected");
    $("#epic-add-gadget").prop("disabled", false);
    return;
  } else if ($("#epicProduct").val() == null || $("#epicProduct").val() == "") {
    alert("No product selected");
    $("#epic-add-gadget").prop("disabled", false);
    return;
  } else if ($("#epicMultiSelect").val() == null && !$("#epicCheckAll").prop("checked")) {
    alert("No epic links selected");
    $("#epic-add-gadget").prop("disabled", false);
    return;
  } else if ($("#epicMetricMultiSelect") == null) {
    alert("No test metric selected");
    $("#epic-add-gadget").prop("disabled", false);
    return;
  }
  object['id'] = TEST_EPIC_ID;
  console.log("TEST_EPIC_ID: " + TEST_EPIC_ID);
  object['dashboardId'] = $("#dashboardId").val();
  object['projectName'] = $("#epicProject").val();
  object['release'] = $("#epicRelease").val();
  object['products'] = [$("#epicProduct").val()];
  object['metrics'] = $("#epicMetricMultiSelect").val();

  if ($("#epicCheckAll").prop("checked")) {
    object['selectAll'] = true;
    object['epic'] = null;
  } else {
    object['epic'] = $("#epicMultiSelect").val();
  }
  return JSON.stringify(object);
}

function callAjaxToUpdateGadget(jsonString) {
  if (jsonString != null && jsonString != "") {
    $.ajax({
      url: SAVE_GADGET_URI,
      method: 'POST',
      data: {
        type: 'EPIC_US_TEST_EXECUTION',
        data: jsonString
      },
      beforeSend: function() {
        hideEpicTable();
      },
      success: function(data) {
        if (debugAjaxResponse(data)) {
          $("#epic-add-gadget").prop("disabled", false);
          showEpicTable();
          return;
        } else {
          alert("Gadget updated succesfully");
          TEST_EPIC_ID = data["data"];
          drawEpicTable(TEST_EPIC_ID, $("#epicMetricMultiSelect").val());
        }

      },
      error: function(xhr, textStatus, error) {
        debugError(xhr, textStatus, error);
        $("#epic-add-gadget").prop("disabled", false);
        showEpicTable();
      }
    });
  }
}


function drawEpicTable(gadgetId, metricArray) {
  var columnList = getColumnArray(metricArray, false);
  console.log(gadgetId);
  resetTableColumns(GLOBAL_EPIC_TABLE, false);
  if (GLOBAL_EPIC_TABLE != null) {
    console.log(GLOBAL_EPIC_TABLE);
    hideEpicTable();
    GLOBAL_EPIC_TABLE.ajax.reload(function() {
      showEpicTable();
      $("#epic-add-gadget").prop("disabled", false);
    });
    GLOBAL_EPIC_TABLE.columns(columnList).visible(false);

  } else {
    hideEpicTable();
    console.log("DRAW EPIC TABLE: " + gadgetId);
    GLOBAL_EPIC_TABLE = $('#epic-table').on(
      'error.dt',
      function(e, settings, techNote, message) {
        console.log('An error has been reported by DataTables: ',
          message);
        $("#epic-add-gadget").prop("disabled", false);
        showEpicTable();
      }).DataTable({
      "fnDrawCallback": function(oSettings) {
        $("#epic-add-gadget").prop("disabled", false);
        showEpicTable();
      },
      bAutoWidth: false,
      "ajax": {
        url: GET_DATA_URI,
        data: {
          id: gadgetId
        },
        dataSrc: function(responseJson) {
          var tempArray = [];
          console.log(responseJson);
          if (debugAjaxResponse(responseJson)) {
            $("#epic-add-gadget").prop("disabled", false);
            showEpicTable();
            return [];
          }

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
        "data": "key",
        "render": function(data, displayOrType,
          rowData, setting) {
          return createIssueLinkForTitle(data);
        }
      }, {
        "data": "key.summary"
      }, {
        "data": "key.priority.name",
      }, {
        "data": "unexecuted",
        "render": function(data, displayOrType,
          rowData, setting) {
          return createIssueLinks(data,
            displayOrType, rowData, setting);
        }
      }, {
        "data": "failed",
        "render": function(data, displayOrType,
          rowData, setting) {
          return createIssueLinks(data,
            displayOrType, rowData, setting);
        }
      }, {
        "data": "wip",
        "render": function(data, displayOrType,
          rowData, setting) {
          return createIssueLinks(data,
            displayOrType, rowData, setting);
        }
      }, {
        "data": "blocked",
        "render": function(data, displayOrType,
          rowData, setting) {
          return createIssueLinks(data,
            displayOrType, rowData, setting);
        }
      }, {
        "data": "passed",
        "render": function(data, displayOrType,
          rowData, setting) {
          return createIssueLinks(data,
            displayOrType, rowData, setting);
        }
      }, {
        "data": "planned",
        "render": function(data, displayOrType,
          rowData, setting) {
          return createIssueLinks(data,
            displayOrType, rowData, setting);
        }
      }, {
        "data": "unplanned",
        "render": function(data, displayOrType,
          rowData, setting) {
          return createIssueLinks(data,
            displayOrType, rowData, setting);
        }
      }]
    });
    GLOBAL_EPIC_TABLE.columns(columnList).visible(false);
  }

}

function showEpicLinks() {
  $('#epicMultiSelect').fadeIn();
  $('#epic-link-loader').fadeOut();
}

function showEpicTable() {
  $('#epic-table-container').fadeIn();
  $('#epic-table-loader').fadeOut();
}

function hideEpicTable() {
  $('#epic-table-container').fadeOut();
  $('#epic-table-loader').fadeIn();
}

function hideEpicLinks() {
  $('#epicMultiSelect').fadeOut();
  $('#epic-link-loader').fadeIn();
}
