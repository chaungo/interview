/*
 * List of html id for user story widget
 * #us-test-execution-div
 * #usProject
 * #usRelease
 * #usProduct
 * #us-epic-container
 * #usEpicAvailableDiv
 * #usEpicAvailable
 * #usEpic
 * #us-epic-loader
 * #usCheckAllEpic
 * #us-add-epic-btn
 * #us-remove-epic-btn
 * #us-add-all-epic-btn
 * #us-remove-all-epic-btn
 * #us-container
 * #usMultiSelect
 * #usCheckAllStory
 * #us-us-loader
 * #usMetricMultiSelect
 * #us-table-container
 * #us-table-loader
 * 
 */


/*
 * Set listeners for project, release, product select option
 *
$("#"+gadget.id).find("#usProject").change(function() {
  callAjaxOnUsProjectAndRelease(null);
});

$("#"+gadget.id).find("#usRelease").change(function() {
  callAjaxOnUsProjectAndRelease(null);
});

$("#"+gadget.id).find("#usProduct").change(function() {
  callAjaxOnUsProjectAndRelease(null);
});
/*
 * Set listeners for add, remove, add all, remove all epic links buttons
 *
$("#"+gadget.id).find("#us-add-epic-btn").click(function() {
  var options = $("#"+gadget.id).find("#usEpicAvailable option:selected").clone();
  if (options.length == 0) {
    return;
  }
  $("#"+gadget.id).find("#usEpic").append(options);
  $("#"+gadget.id).find("#usEpicAvailable option:selected").remove();
  reloadUSList(null);
});

$("#"+gadget.id).find("#us-add-all-epic-btn").click(function() {
  addAllEpic(gadget);
});

$("#"+gadget.id).find("#us-remove-epic-btn").click(function() {
  var options = $("#"+gadget.id).find("#usEpic option:selected").clone();
  if (options.length == 0) {
    return;
  }
  $("#"+gadget.id).find("#usEpicAvailable").append(options);
  $("#"+gadget.id).find("#usEpic option:selected").remove();
  reloadUSList(null);
});

$("#"+gadget.id).find("#us-remove-all-epic-btn").click(function() {
  removeAllEpic(gadget);
});


/*
 * Set listener for update button
 *
$("#"+gadget.id).find("#us-update-btn").click(
  function() {
    $(this).prop("disabled", true);
    var jsonString = createJsonStringObjectFromUsInputField();
    callAjaxToUpdateUsGadget(jsonString);
  });


/*
 * Set listeners for input check option
 *
$("#"+gadget.id).find("#usCheckAllEpic").click(function() {
  if ($(this).prop("checked")) {
    $("#"+gadget.id).find("#us-epic-container").fadeOut();
    addAllEpic(gadget);
  } else {
    $("#"+gadget.id).find("#us-epic-container").fadeIn();
    if ($("#"+gadget.id).find("#usEpicAvailable option").length == 0 && $("#"+gadget.id).find("#usEpic option").length == 0) {
      callAjaxOnUsProjectAndRelease(null);
    }
  }
});

$("#"+gadget.id).find("#usCheckAllStory").click(function() {
  if ($(this).prop("checked")) {
    $("#"+gadget.id).find("#us-container").fadeOut();
  } else {
    $("#"+gadget.id).find("#us-container").fadeIn();
  }
});

/*
 * create json object to update
 */

function createJsonStringObjectFromUsInputField(gadget) {
  var options;
  var values;
  var object = {};
  var jsonString;
  if (null == $("#"+gadget.id).find("#dashboardId").val()) {
    alert("No valid dashboard id provided.");
    $("#"+gadget.id).find("#us-update-btn").prop("disabled", false);
    return;
  } else if ($("#"+gadget.id).find("#usProject").val() == null || $("#"+gadget.id).find("#usProject").val() == "") {
    alert("No project selected");
    $("#"+gadget.id).find("#us-update-btn").prop("disabled", false);
    return;
  } else if ($("#"+gadget.id).find("#usRelease").val() == null || $("#"+gadget.id).find("#usRelease").val() == "") {
    alert("No release selected");
    $("#"+gadget.id).find("#us-update-btn").prop("disabled", false);
    return;
  } else if ($("#"+gadget.id).find("#usProduct").val() == null || $("#"+gadget.id).find("#usProduct").val() == "") {
    alert("No product selected");
    $("us-update-btn").prop("disabled", false);
    return;
  } else if ($("#"+gadget.id).find("#usEpic option").length == 0 && !$("#"+gadget.id).find("#usCheckAllEpic").prop("checked")) {
    alert('No epic links selected.');
    $("#"+gadget.id).find("#us-update-btn").prop("disabled", false);
    return;
  } else if ($("#"+gadget.id).find("#usMultiSelect").val() == null && !$("#"+gadget.id).find("#usCheckAllStory").prop("checked")) {
    alert("No user story selected for the fetched epic links ");
    $("#"+gadget.id).find("#us-update-btn").prop("disabled", false);
    return;
  } else if ($("#"+gadget.id).find("#usMetricMultiSelect").val() == null) {
    $("#"+gadget.id).find("#us-update-btn").prop("disabled", false);
    alert("No test metric selected");
    return;
  }

  options = $("#"+gadget.id).find("#usEpic option");
  values = $.map(options, function(option) {
    return option.value;
  });

  object['id'] = gadget.id;
  object['dashboardId'] = gadget.dashboardId;
  object['projectName'] = $("#"+gadget.id).find("#usProject").val();
  object['release'] = $("#"+gadget.id).find("#usRelease").val();
  object['products'] = [$("#"+gadget.id).find("#usProduct").val()];
  object['metrics'] = $("#"+gadget.id).find("#usMetricMultiSelect").val();

  if ($("#"+gadget.id).find("#usCheckAllEpic").prop("checked")) {
    object["selectAllEpic"] = true;
  } else {
    object['epic'] = values;
  }

  if ($("#"+gadget.id).find("#usCheckAllStory").prop("checked")) {
    object["selectAllStory"] = true;
  } else {
    object['stories'] = $("#"+gadget.id).find("#usMultiSelect").val();
  }
  jsonString = JSON.stringify(object);

  return jsonString;
}

function callAjaxToUpdateUsGadget(gadget, jsonString) {
  if (null != jsonString && "" != jsonString) {
    $.ajax({
      url: SAVE_GADGET_URI,
      method: 'POST',
      data: {
        type: 'STORY_TEST_EXECUTION',
        data: jsonString
      },
      beforeSend: function() {
        hideUsTable(gadget);
      },
      error: function(xhr, textStatus, error) {
        debugError(xhr, textStatus, error);
        $("#"+gadget.id).find("#us-update-btn").prop("disabled", false);
        showUsTable(gadget);
      },
      success: function(data) {
        if (debugAjaxResponse(data)) {
          $("#"+gadget.id).find("#us-update-btn").prop("disabled", false);
          showUsTable(gadget);
          return;
        } else {
          alert("Gadget updated succesfully");
          drawUsTable(data["data"], $("#"+gadget.id).find("#usMetricMultiSelect").val());
        }

      }
    }).always(function(returnMessage) {
      console.log(jsonString);
    });
  }
}

function reloadUSList(gadget, selectList) {
  if ($("#"+gadget.id).find("#usCheckAllStory").prop("checked,true")) {
    return;
  } else if ($("#"+gadget.id).find("#usEpic option").length == 0) {
    cleanSelect("#usMultiSelect");
    return;
  }
  var options = $("#"+gadget.id).find("#usEpic option")
  var values = $.map(options, function(option) {
    return option.value;
  });
  var jsonString = JSON.stringify(values);
  $.ajax({
    url: GET_STORY_URI,
    data: {
      epics: jsonString
    },
    error: function(xhr, textStatus, error) {
      debugError(xhr, textStatus, error);
    },
    beforeSend: function() {
      $("#"+gadget.id).find("#usMultiSelect").fadeOut();
      $("#"+gadget.id).find("#us-us-loader").fadeIn();
    },
    success: function(data) {
      var tempList = [];
      if (debugAjaxResponse(data)) {
        return;
      }
      $('#usMultiSelect').find("option").remove().end();
      $.each(data, function(key, list) {
        tempList.push.apply(tempList, list);
      })
      tempList.sort();
      for (var i = 0; i < tempList.length; i++) {
        $(
          '<option value="' + tempList[i] + '">' + tempList[i] + '</option>').appendTo(
          '#usMultiSelect');
      }
      if (selectList != null) {
        $('#usMultiSelect').val(selectList);
      }

    }
  }).always(
    function(data) {
      $("#"+gadget.id).find("#usMultiSelect").fadeIn();
      $("#"+gadget.id).find("#us-us-loader").fadeOut();
    });
}

function drawUsTable(dataTable, gadget) {
  var columnList = getColumnArray(gadget.metrics, false);
  console.log("DRAW US TABLE:");
  console.log(gadget);
  var jsonObjectForUsTable;
  if (dataTable.loading == true && GLOBAL_US_TABLES_AJAX.ajax != null) {
	  dataTable.ajax.abort();
  }
  dataTable.ajax = $.ajax({
    url: "/gadget/getData?",
    method: "GET",
    data: {
      "id": gadget.id
    },
    beforeSend: function() {
    	dataTable.loading = true;
    	hideUsTable(gadget);
    },
    error: function(xhr, textStatus, error) {
      debugError(xhr, textStatus, error);
    },
    success: function(responseData) {
    	console.log("Story Response: ");
    	console.log(responseData);
    	
      if (debugAjaxResponse(responseData)) {
        $("#"+gadget.id).find("#us-update-btn").prop("disabled", false);
        showUsTable(gadget);
        return;
      }
      $("#"+gadget.id).find("#us-table-container").html("");
      var index = 0;

      jsonObjectForUsTable = responseData;
      console.log(jsonObjectForUsTable["data"]);
      console.log("DRAWING TABLE");
      console.log(jsonObjectForUsTable);
      $.each(jsonObjectForUsTable["data"], function(epicKey,
        storyArray) {
        if (storyArray["issueData"].length != 0) {
          var customTableId = "us-table-" + index;
          var usTableDataSet = [];
          var usIndividualTable;
          console.log("here");
          appendTemplateTable(customTableId, epicKey + ": " + storyArray["summary"],gadget,
            "#us-table-container");
          $("#"+gadget.id).find("#" + customTableId).append(TEMPLATE_HEADER_FOOTER);
          console.log("Pass each function");

          for (var i = 0; i < storyArray['issueData'].length; i++) {
            var aStoryDataSet = [];
            aStoryDataSet.push(storyArray['issueData'][i]["key"]);
            aStoryDataSet.push(storyArray['issueData'][i]["key"]["summary"]);
            aStoryDataSet.push(storyArray['issueData'][i]["key"]["priority"]["name"]);
            aStoryDataSet.push(storyArray['issueData'][i]["unexecuted"]);
            aStoryDataSet.push(storyArray['issueData'][i]["failed"]);
            aStoryDataSet.push(storyArray['issueData'][i]["wip"]);
            aStoryDataSet.push(storyArray['issueData'][i]["blocked"]);
            aStoryDataSet.push(storyArray['issueData'][i]["passed"]);
            aStoryDataSet.push(storyArray['issueData'][i]["planned"]);
            aStoryDataSet.push(storyArray['issueData'][i]["unplanned"]);
            usTableDataSet.push(aStoryDataSet);
          }

          usIndividualTable = $("#"+gadget.id).find("#" + customTableId).DataTable({
            bAutoWidth: false,
            data: usTableDataSet,
            columns: [{
              title: "User Story",
              "render": function(data, displayOrType, rowData, setting) {
                return createIssueLinkForTitle(data);
              }
            }, {
              title: "SUMMARY"
            }, {
              title: "PRIORITY"
            }, {
              title: "UNEXECUTED",
              "render": function(data, displayOrType, rowData, setting) {
                return createIssueLinks(data, displayOrType, rowData, setting);
              }
            }, {
              title: "FAILED",
              "render": function(data, displayOrType, rowData, setting) {
                return createIssueLinks(data, displayOrType, rowData, setting);
              }
            }, {
              title: "WIP",
              "render": function(data, displayOrType, rowData, setting) {
                return createIssueLinks(data, displayOrType, rowData, setting);
              }
            }, {
              title: "BLOCKED",
              "render": function(data, displayOrType, rowData, setting) {
                return createIssueLinks(data, displayOrType, rowData, setting);
              }
            }, {
              title: "PASSED",
              "render": function(data, displayOrType, rowData, setting) {
                return createIssueLinks(data, displayOrType, rowData, setting);
              }
            }, {
              title: "PLANNED",
              "render": function(data, displayOrType, rowData, setting) {
                return createIssueLinks(data, displayOrType, rowData, setting);
              }
            }, {
              title: "UNPLANNED",
              "render": function(data, displayOrType, rowData, setting) {
                return createIssueLinks(data, displayOrType, rowData, setting);
              }
            }]
          });
          usIndividualTable.columns(columnList).visible(false);
          index++;
        }
        dataTable.loading = false;
        showUsTable(gadget);
      });
    }
  });

}

function callAjaxOnUsProjectAndRelease(gadget, selectList, usSelectList) {
  if ($("#"+gadget.id).find("#usProject").val() == null || $("#"+gadget.id).find("#usRelease").val() == null || $("#"+gadget.id).find("#usProduct").val() == null) {
    return;
  } else if ($("#"+gadget.id).find("#usProject").val() == "" || $("#"+gadget.id).find("#usRelease").val() == "" || $("#"+gadget.id).find("#usProduct").val() == "") {
    return;
  }

  $.ajax({
    url: GET_EPIC_URI,
    data: {

      project: $("#"+gadget.id).find("#usProject").val(),
      release: $("#"+gadget.id).find("#usRelease").val(),
      products: JSON.stringify([$("#"+gadget.id).find("#usProduct").val()])
    },

    beforeSend: function() {
      if (!$("#"+gadget.id).find("#usCheckAllEpic").prop("checked")) {
        hideUsEpic(gadget);
      } else if (!$("#"+gadget.id).find("#usCheckAllStory").prop("checked")) {
        hideUsStory(gadget);
      }

    },
    error: function(xhr, textStatus, error) {
      debugError(xhr, textStatus, error);
      if (!$("#"+gadget.id).find("#usCheckAllEpic").prop("checked")) {
        showUsEpic(gadget);
      } else if (!$("#"+gadget.id).find("#usCheckAllStory").prop("checked")) {
        showUsStory(gadget);
      }
    },
    success: function(data) {
      if (debugAjaxResponse(data)) {
        return;
      }
      data.sort();
      if (!$("#"+gadget.id).find("#usCheckAllEpic").prop("checked")) {
        if (selectList != null) {
          appendToSelect(true, data, "#usEpicAvailable");
          $("#"+gadget.id).find("#usEpicAvailable option").filter(function() {
            return $.inArray(this.value, selectList) !== -1
          }).remove();

          appendToSelect(true, selectList, "#usEpic");
        } else {
          appendToSelect(true, data, "#usEpicAvailable");
          $('#usEpic').find('option').remove().end();

        }
        showUsEpic(gadget);
      } else if ($("#"+gadget.id).find("#usCheckAllEpic").prop("checked")) {
        $('#usEpicAvailable').find('option').remove().end();
        appendToSelect(true, data, "#usEpic");
      }

      reloadUSList(usSelectList);
    }
  }).always(function() {
    if (!$("#"+gadget.id).find("#usCheckAllEpic").prop("checked")) {
      showUsEpic(gadget);
    } else if (!$("#"+gadget.id).find("#usCheckAllStory").prop("checked")) {
      showUsStory(gadget);
    }
  });
}

function addEpic(gadget) {
  var options = $("#"+gadget.id).find("#usEpicAvailable option:selected").clone();
  if (options.length == 0) {
    return;
  }
  $("#"+gadget.id).find("#usEpic").append(options);
  $("#"+gadget.id).find("#usEpicAvailable option:selected").remove();
  reloadUSList(null);
}

function removeEpic(gadget) {
  var options = $("#"+gadget.id).find("#usEpic option:selected").clone();
  if (options.length == 0) {
    return;
  }
  $("#"+gadget.id).find("#usEpicAvailable").append(options);
  $("#"+gadget.id).find("#usEpic option:selected").remove();
  reloadUSList(null);
}


function addAllEpic(gadget) {
  var options = $("#"+gadget.id).find("#usEpicAvailable option").clone();
  if (options.length == 0) {
    return;
  }
  $("#"+gadget.id).find("#usEpic").append(options);
  $("#"+gadget.id).find("#usEpicAvailable option").remove();
  reloadUSList(null);
}

function removeAllEpic(gadget) {
  var options = $("#"+gadget.id).find("#usEpic option").clone();
  if (options.length == 0) {
    return;
  }
  $("#"+gadget.id).find("#usEpicAvailable").append(options);
  $("#"+gadget.id).find("#usEpic option").remove();
  reloadUSList(null);
}

function showUsEpic(gadget) {
  $('#us-epic-loader').fadeOut();
  $("#"+gadget.id).find("#us-epic-available-div").fadeIn();
}

function hideUsEpic(gadget) {
  $("#"+gadget.id).find("#us-epic-loader").fadeIn();
  $("#"+gadget.id).find("#us-epic-available-div").fadeOut();
}

function showUsStory(gadget) {
  $("#"+gadget.id).find("#us-us-loader").fadeOut();
  $("#"+gadget.id).find("#usMultiSelect").fadeIn();
}

function hideUsStory(gadget) {
  $("#"+gadget.id).find("#us-epic-loader").fadeOut();
  $("#"+gadget.id).find("#us-epic-available-div").fadeIn();
}

function showUsTable(gadget) {
  $('#us-table-loader').fadeOut();
  $("#"+gadget.id).find("#us-table-container").fadeIn();
}

function hideUsTable(gadget) {
  $('#us-table-loader').fadeIn();
  $("#"+gadget.id).find("#us-table-container").fadeOut();
}
