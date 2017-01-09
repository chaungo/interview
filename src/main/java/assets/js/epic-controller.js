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

function onClickUpdateEpic(gadget) {
  $("#" + gadget.id).find("#epic-add-gadget").prop("disabled", true);
  var jsonString = createJsonStringObjectFromEpicInput(gadget);
  callAjaxToUpdateGadget(gadget, jsonString);
}

function onCheckAllEpic(gadget) {
  if ($("#" + gadget.id).find("#epicCheckAll").prop("checked")) {
    $("#" + gadget.id).find("#epic-link-container").fadeOut();
  } else {
    $("#" + gadget.id).find("#epic-link-container").fadeIn();
    if ($("#" + gadget.id).find("#epicMultiSelect option").length == 0) {
      callAjaxOnEpicProjectAndRelease(gadget, null);
    }
  }
}

function callAjaxOnEpicProjectAndRelease(gadget, selectList) {
  if ($("#" + gadget.id).find("#epicProject").val() == null || $("#" + gadget.id).find("#epicRelease").val() == null || $("#" + gadget.id).find("#epicProduct").val() == null) {
    return;
  } else if ($("#" + gadget.id).find("#epicProject").val() == "" || $("#" + gadget.id).find("#epicRelease").val() == "" || $("#" + gadget.id).find("#epicProduct").val() == "") {
    return;

  }

  if (!$("#" + gadget.id).find("#epicCheckAll").prop("checked")) {
    $.ajax({
      url: GET_EPIC_URI,
      data: {
        project: $("#" + gadget.id).find("#epicProject").val(),
        release: $("#" + gadget.id).find("#epicRelease").val(),
        products: JSON.stringify([$("#" + gadget.id).find("#epicProduct").val()])
      },

      beforeSend: function() {
        hideEpicLinks(gadget);
      },
      success: function(data) {
        if (debugAjaxResponse(data)) {
          return;
        } else {
          data.sort();
          appendToSelect(true, data, "#" + gadget.id, "#epicMultiSelect");
          if (selectList != null) {
            $("#" + gadget.id).find("#epicMultiSelect").val(selectList);
          }
        }

      }
    }).always(function(data) {
      showEpicLinks(gadget);
    });
  }
}

function createJsonStringObjectFromEpicInput(gadget) {
  var object = {};
  if (null == $("#" + gadget.id).find("#dashboardId").val()) {
    alert("No valid dashboard id provided.");
    $("#" + gadget.id).find("#epic-add-gadget").prop("disabled", false);
    return;
  } else if ($("#" + gadget.id).find("#epicProject").val() == null || $("#" + gadget.id).find("#epicProject").val() == "") {
    alert("No project selected");
    $("#" + gadget.id).find("#epic-add-gadget").prop("disabled", false);
    return;
  } else if ($("#" + gadget.id).find("#epicRelease").val() == null || $("#" + gadget.id).find("#epicRelease").val() == "") {
    alert("No release selected");
    $("#" + gadget.id).find("#epic-add-gadget").prop("disabled", false);
    return;
  } else if ($("#" + gadget.id).find("#epicProduct").val() == null || $("#" + gadget.id).find("#epicProduct").val() == "") {
    alert("No product selected");
    $("#" + gadget.id).find("#epic-add-gadget").prop("disabled", false);
    return;
  } else if ($("#" + gadget.id).find("#epicMultiSelect").val() == null && !$("#" + gadget.id).find("#epicCheckAll").prop("checked")) {
    alert("No epic links selected");
    $("#" + gadget.id).find("#epic-add-gadget").prop("disabled", false);
    return;
  } else if ($("#" + gadget.id).find("#epicMetricMultiSelect") == null) {
    alert("No test metric selected");
    $("#" + gadget.id).find("#epic-add-gadget").prop("disabled", false);
    return;
  }
  object['id'] = gadget.id;
  object['dashboardId'] = gadget.dashboardid;
  object['projectName'] = $("#" + gadget.id).find("#epicProject").val();
  object['release'] = $("#" + gadget.id).find("#epicRelease").val();
  object['products'] = [$("#" + gadget.id).find("#epicProduct").val()];
  object['metrics'] = $("#" + gadget.id).find("#epicMetricMultiSelect").val();

  if ($("#" + gadget.id).find("#epicCheckAll").prop("checked")) {
    object['selectAll'] = true;
    object['epic'] = null;
  } else {
    object['epic'] = $("#" + gadget.id).find("#epicMultiSelect").val();
  }
  return JSON.stringify(object);
}

function callAjaxToUpdateGadget(gadget, jsonString) {
  if (jsonString != null && jsonString != "") {
    $.ajax({
      url: SAVE_GADGET_URI,
      method: 'POST',
      data: {
        type: 'EPIC_US_TEST_EXECUTION',
        data: jsonString
      },
      beforeSend: function() {
        hideEpicTable(gadget);
      },
      success: function(data) {
        if (debugAjaxResponse(data)) {
          $("#" + gadget.id).find("#epic-add-gadget").prop("disabled", false);
          showEpicTable(gadget);
          return;
        } else {
          alert("Gadget updated succesfully");
          drawEpicTable(data["data"], $("#" + gadget.id).find("#epicMetricMultiSelect").val());
        }

      },
      error: function(xhr, textStatus, error) {
        debugError(xhr, textStatus, error);
        $("#" + gadget.id).find("#epic-add-gadget").prop("disabled", false);
        showEpicTable(gadget);
      }
    });
  }
}

function drawEpicTable(dataTable, gadget) {
	
  var columnList = getColumnArray(gadget.metrics, false);
  resetTableColumns(dataTable, false);
  if (dataTable != null) {
    hideEpicTable(gadget);
    dataTable.ajax.reload(function() {
      showEpicTable(gadget);
    });
    dataTable.columns(columnList).visible(true);

  } else {
    hideEpicTable(gadget);
    dataTable = $("#" + gadget.id).find('#epic-table').on(
      'error.dt',
      function(e, settings, techNote, message) {
        console.log('An error has been reported by DataTables: ',
          message);
        showEpicTable(gadget);
      }).DataTable({
      "fnDrawCallback": function(oSettings) {
        showEpicTable(gadget);
      },
      bAutoWidth: false,
      "ajax": {
        url: GET_DATA_URI,
        data: {
          id: gadget.id
        },
        dataSrc: function(responseJson) {
          var tempArray = [];
          if (debugAjaxResponse(responseJson)) {
            $("#" + gadget.id).find("#epic-add-gadget").prop("disabled", false);
            showEpicTable(gadget);
            return [];
          }

          $.each(responseJson["data"], function(k1, v1) {
            $.each(v1["issueData"], function(k2, v2) {
              tempArray.push(v2);
            });
          });
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
    dataTable.columns(columnList).visible(false);
  }
}

function drawEpicWidget(gadget) {
  $("#" + gadget.id).find("#epic-test-execution-div").show();
  $("#" + gadget.id).find("#btn-add-gadget-epic").prop("disabled", true);
  hideEpicTable(gadget.id);
  if (gadget.projectName != "" && gadget.projectName != null) {
    $("#" + gadget.id).find("#epicProject").val(gadget.projectName);
  }

  if (gadget.product != null) {
    $("#" + gadget.id).find("#epicProduct").val(gadget.product);
  } else {
    $("#" + gadget.id).find("#epicProduct").val("");
  }

  if (gadget.release != null) {
    $("#" + gadget.id).find("#epicRelease").val(gadget.release);
  }

  if (gadget.metrics != null) {
    $("#" + gadget.id).find("#epicMetricMultiSelect").val(gadget.metrics);
  }
  if (gadget.selectAll == true) {
    $("#" + gadget.id).find("#epicCheckAll").prop("checked", true);
    $("#" + gadget.id).find("#epic-link-container").hide();
  } else if (gadget.epic != null) {
    $("#" + gadget.id).find("#epicCheckAll").prop("checked", false);
    $("#" + gadget.id).find("#epic-link-container").show();
    $("#" + gadget.id).find("#epic-link-loader").hide();
    callAjaxOnEpicProjectAndRelease(gadget.id, gadget.epic);
  }
}

function showEpicLinks(gadget) {
  $("#" + gadget.id).find('#epicMultiSelect').fadeIn();
  $("#" + gadget.id).find('#epic-link-loader').fadeOut();
}

function showEpicTable(gadget) {
  $("#" + gadget.id).find('#epic-table-container').fadeIn();
  $("#" + gadget.id).find('#epic-table-loader').fadeOut();
}

function hideEpicTable(gadget) {
  $("#" + gadget.id).find('#epic-table-container').fadeOut();
  $("#" + gadget.id).find('#epic-table-loader').fadeIn();
}

function hideEpicLinks(gadget) {
  $("#" + gadget.id).find('#epicMultiSelect').fadeOut();
  $("#" + gadget.id).find('#epic-link-loader').fadeIn();
}
