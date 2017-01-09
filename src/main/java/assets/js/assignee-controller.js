/*
 * Set listerners for project, release, product field
 *
 $("#"+ gadget.id).find("#assigneeProject").change(function() {

 });

 $("#"+ gadget.id).find("#assigneeRelease").change(function() {

 });

 $("#"+ gadget.id).find("#assigneeProduct").change(function() {

 });
 /*
 * Set listeners for add, remove, add all and remove all test cycle list
 *
 $("#"+ gadget.id).find("#assignee-add-cycle-btn").click(function() {
 var options = $("#"+ gadget.id).find("#assigneeCycleAvailable option:selected").clone();
 if (options.length == 0) {
 return;
 }
 $("#"+ gadget.id).find("#assigneeCycle").append(options);
 $("#"+ gadget.id).find("#assigneeCycleAvailable option:selected").remove();
 });

 $("#"+ gadget.id).find("#assignee-remove-cycle-btn").click(function() {
 var options = $("#"+ gadget.id).find("#assigneeCycle option:selected").clone();
 if (options.length == 0) {
 return;
 }
 $("#"+ gadget.id).find("#assigneeCycleAvailable").append(options);
 $("#"+ gadget.id).find("#assigneeCycle option:selected").remove();
 });

 $("#"+ gadget.id).find("#assignee-add-all-cycle-btn").click(function() {
 var options = $("#"+ gadget.id).find("#assigneeCycleAvailable option").clone();
 if (options.length == 0) {
 return;
 }
 $("#"+ gadget.id).find("#assigneeCycle").find("option").remove().end();
 $("#"+ gadget.id).find("#assigneeCycle").append(options);
 $("#"+ gadget.id).find("#assigneeCycleAvailable option").remove();
 });

 $("#"+ gadget.id).find("#assignee-remove-all-cycle-btn").click(function() {
 var options = $("#"+ gadget.id).find("#assigneeCycle option").clone();
 if (options.length == 0) {
 return;
 }
 $("#"+ gadget.id).find("#assigneeCycleAvailable").find("option").remove().end();
 $("#"+ gadget.id).find("#assigneeCycleAvailable").append(options);
 $("#"+ gadget.id).find("#assigneeCycle option").remove();
 });

 /*
 * Set listener for Update button
 *

 $("#"+ gadget.id).find("#assignee-update-btn").click(function() {
 $(this).prop("disabled", true);
 var jsonString = createJsonStringFromAssigneeInput();
 updateAssigneeGadget(jsonString);
 });

 /*
 * Set listerners for input check option 
 *
 $("#"+ gadget.id).find("#assigneeCheckAll").click(function() {
 if ($(this).prop("checked") == true) {
 $("#"+ gadget.id).find("#assignee-container").fadeOut();
 } else {
 $("#"+ gadget.id).find("#assignee-container").fadeIn();
 }
 });

 $("#"+ gadget.id).find("#assigneeCheckAllCycle").click(function() {
 if ($(this).prop("checked")) {
 $("#"+ gadget.id).find("#assignee-cycle-container").fadeOut();
 addAllCycle(gadget);
 } else {
 $("#"+ gadget.id).find("#assignee-cycle-container").fadeIn();
 if ($("#"+ gadget.id).find("#assigneeCycle option").length == 0 && $("#"+ gadget.id).find("#assigneeCycleAvailable option").length == 0) {
 getExistingCycleAssigneeWidget();
 }
 }
 });

 /*
 * Function section
 */
function createJsonStringFromAssigneeInput(gadget) {
    var object = {};
    var options;
    var values;
    var jsonString;
    if (null == $("#" + gadget.id).find("#dashboardId").val()) {
        alert("No valid dashboard id provided.");
        $("#" + gadget.id).find("#assignee-update-btn").prop("disabled", false);
        return;
    } else if ($("#" + gadget.id).find("#assigneeProject").val() == null || $("#" + gadget.id).find("#assigneeProject").val() == "") {
        alert("No project selected");
        $("#" + gadget.id).find("#assignee-update-btn").prop("disabled", false);
        return;
    } else if ($("#" + gadget.id).find("#assigneeRelease").val() == null || $("#" + gadget.id).find("#assigneeRelease").val() == "") {
        alert("No release selected");
        $("#" + gadget.id).find("#assignee-update-btn").prop("disabled", false);
        return;
    } else if ($("#" + gadget.id).find("#assigneeProduct").val() == null || $("#" + gadget.id).find("#assigneeProduct").val() == "") {
        alert("No product selected");
        $("#" + gadget.id).find("#assignee-update-btn").prop("disabled", false);
        return;
    } else if ($("#" + gadget.id).find("#assigneeCycle option").length == 0 && !$("#" + gadget.id).find("#assigneeCheckAllCycle").prop("checked")) {
        $("#" + gadget.id).find("#assignee-update-btn").prop("disabled", false);
        alert("No cycle selected");
        return;
    } else if ($("#" + gadget.id).find("#assigneeMultiSelect").val() == null && !$("#" + gadget.id).find("#assigneeCheckAll").prop("checked")) {
        alert("No assignee selected");
        $("#" + gadget.id).find("#assignee-update-btn").prop("disabled", false);
        return;
    } else if ($("#" + gadget.id).find("#assigneeMetricMultiSelect").val() == null) {
        alert("No test metric selected");
        $("#" + gadget.id).find("#assignee-update-btn").prop("disabled", false);
        return;
    }
    object['dashboardId'] = $("#" + gadget.id).find("#dashboardId").val();
    options = $("#" + gadget.id).find("#assigneeCycle option");
    values = $.map(options, function (option) {
        return option.value;
    });

    object['id'] = gadget.id;
    object['projectName'] = gadget.dashboardId;
    object['release'] = $("#" + gadget.id).find("#assigneeRelease").val();
    object['products'] = [$("#" + gadget.id).find("#assigneeProduct").val()];
    object['metrics'] = $("#" + gadget.id).find("#assigneeMetricMultiSelect").val();

    if ($("#" + gadget.id).find("#assigneeCheckAllCycle").prop("checked")) {
        object['selectAllTestCycle'] = true;
    } else {
        object['cycles'] = values;
    }
    if ($("#" + gadget.id).find("#assigneeCheckAll").prop("checked")) {
        object['selectAllAssignee'] = true;
    } else {
        object['assignee'] = $("#" + gadget.id).find("#assigneeMultiSelect").val();
    }

    jsonString = JSON.stringify(object);
    return jsonString;
}

function updateAssigneeGadget(gadget, jsonString) {
    if (jsonString != null) {
        $.ajax({
            url: SAVE_GADGET_URI,
            method: 'POST',
            data: {
                type: ASSIGNEE_TYPE,
                data: jsonString
            },
            beforeSend: function () {
                hideAssigneeTable(gadget);
            },
            error: function (xhr, textStatus, error) {
                debugError(xhr, textStatus, error);
                $("#" + gadget.id).find("#assignee-update-btn").prop("disabled", false);
                showAssigneeTable(gadget);
            },
            success: function (data) {
                if (debugAjaxResponse(data)) {
                    $("#" + gadget.id).find("#assignee-update-btn").prop("disabled", false);
                    showAssigneeTable(gadget);
                    return;
                } else {
                    alert("Gadget updated succesfully")
                    drawAssigneeTable(data["data"], $("#" + gadget.id).find("#assigneeMetricMultiSelect").val());
                }

            }
        });
    }
}


function drawAssigneeTable(dataTable, gadget) {
    var columnList = getColumnArray(gadget.metrics, true);
    var jsonObjectForAssigneeTable;

    if (dataTable.loading == true) {
        dataTable.ajax.abort();
    }

    dataTable.ajax = $
        .ajax({
            url: GET_DATA_URI,
            method: "GET",
            data: {
                id: gadget.id
            },
            beforeSend: function () {
                dataTable.loading = true;
                hideAssigneeTable(gadget);
            },
            error: function (xhr, textStatus, error) {
                debugError(xhr, textStatus, error);
                showAssigneeTable(gadget);
            },
            success: function (responseData) {
                dataTable.loading = false;
                var index = 0;
                $("#" + gadget.id).find("#assignee-table-container").html("");
                if (debugAjaxResponse(responseData)) {
                    $("#" + gadget.id).find("#assignee-update-btn").prop("disabled", false);
                    showAssigneeTable(gadget);
                    return;
                }

                jsonObjectForAssigneeTable = responseData;
                $
                    .each(
                        jsonObjectForAssigneeTable["data"],
                        function (cycleKey, assigneeArray) {
                            console
                                .log(assigneeArray["issueData"].length);
                            if (assigneeArray["issueData"].length != 0) {
                                var customTableId = "assignee-table-" + index;
                                var assigneeTableDataSet = [];
                                var assigneeIndividualTable;

                                appendTemplateTable(
                                    customTableId,
                                    cycleKey,
                                    gadget,
                                    "#assignee-table-container");
                                $("#" + gadget.id).find("#" + customTableId)
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
                                            "render": function (data,
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
                                            "render": function (data,
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
                                            "render": function (data,
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
                                            "render": function (data,
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
                                            "render": function (data,
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
                $("#" + gadget.id).find("#assignee-update-btn").prop("disabled", false);
                showAssigneeTable(gadget);
            }
        });
}

function callAjaxOnAssigneeTable(gadget) {
    $.ajax({
        url: GET_GADGETS_URI,
        data: {
            dashboardId: $("#" + gadget.id).find("#dashboardId").val()
        },
        success: function (gadgetList) {
            if (debugAjaxResponse(gadgetList)) {
                $("#" + gadget.id).find("#assignee-update-btn").prop("disabled", false);
                showAssigneeTable(gadget);
                return;
            }
            drawAssigneeGadget(gadgetList);
        },
        error: function (xhr, textStatus, error) {
            debugError(xhr, textStatus, error);
            $("#" + gadget.id).find("#assignee-update-btn").prop("disabled", false);
        },
        beforeSend: function () {
            hideAssigneeTable(gadget);
        }
    });
}

function callAjaxOnAssigneeProjectAndRelease(gadget) {
    if ($("#" + gadget.id).find("#assigneeRelease").val() == null || $("#" + gadget.id).find("#assigneeProject").val() == null || $("#" + gadget.id).find("#assigneeProduct").val() == null) {
        return;
    } else if ($("#" + gadget.id).find("#assigneeRelease").val() == "" || $("#" + gadget.id).find("#assigneeProject").val() == "" || $("#" + gadget.id).find("#assigneeProduct").val() == "") {
        return;
    }

    if (!$("#" + gadget.id).find("#assigneeCheckAllCycle").prop("checked")) {
        $.ajax({
            url: GET_CYCLE_URI,
            data: {
                project: $("#" + gadget.id).find("#assigneeProject").val(),
                release: $("#" + gadget.id).find("#assigneeRelease").val(),
                products: JSON.stringify([$("#" + gadget.id).find("#assigneeProduct").val()])
            },

            beforeSend: function () {
                hideAssigneeCycle(gadget);
            },
            success: function (data) {
                if (debugAjaxResponse(data)) {
                    return;
                }
                data.sort();
                appendToSelect(true, data, "#assigneeCycleAvailable");
                $("#" + gadget.id).find("#assigneeCycle").find("option").remove().end();
            },
            error: function (xhr, textStatus, error) {
                debugError(xhr, textStatus, error);
                showAssigneeCycle(gadget);
            }
        }).always(function () {
            showAssigneeCycle(gadget);
        });
    }
    if (!$("#" + gadget.id).find("#assigneeCheckAll").prop("checked")) {

        $.ajax({
            url: GET_ASSIGNEE_URI,
            data: {
                project: $("#" + gadget.id).find("#assigneeProject").val(),
                release: $("#" + gadget.id).find("#assigneeRelease").val()
            },

            beforeSend: function () {
                hideAssignee(gadget);
            },
            success: function (data) {
                if (debugAjaxResponse(data)) {
                    return;
                }
                var tempAssigneeList = [];
                $.each(data, function (key, map) {
                    tempAssigneeList.push(map["assignee"]);
                });
                tempAssigneeList.sort();
                appendToSelect(true, tempAssigneeList, "#assigneeMultiSelect");
            },
            error: function (xhr, textStatus, error) {
                debugError(xhr, textStatus, error);
            }
        }).always(function () {
            showAssignee(gadget);
        });
    }
}

function getExistingCycleAssigneeWidget(gadget) {
    if (!$("#" + gadget.id).find("#assigneeCheckAllCycle").prop("checked")) {
        $.ajax({
            url: GET_EXISTING_CYCLE_URI,
            beforeSend: function () {
                hideAssigneeCycle(gadget);
            },
            success: function (data) {
                if (debugAjaxResponse(data)) {
                    return;
                }
                data.sort();
                appendToSelect(true, data, "#assigneeCycleAvailable");
                $("#" + gadget.id).find("#assigneeCycle").find("option").remove().end();
            },
            error: function (xhr, textStatus, error) {
                debugError(xhr, textStatus, error);
                showAssigneeCycle(gadget);
            }
        }).always(function () {
            showAssigneeCycle(gadget);
        });
    }
    if (!$("#" + gadget.id).find("#assigneeCheckAll").prop("checked")) {

        $.ajax({
            url: "/getassignee?",
            data: {
                project: $("#" + gadget.id).find("#assigneeProject").val(),
                release: $("#" + gadget.id).find("#assigneeRelease").val()
            },

            beforeSend: function () {
                hideAssignee(gadget);
            },
            success: function (data) {
                if (debugAjaxResponse(data)) {
                    return;
                }
                var tempAssigneeList = [];
                $.each(data, function (key, map) {
                    tempAssigneeList.push(map["assignee"]);
                });
                tempAssigneeList.sort();
                appendToSelect(true, tempAssigneeList, "#assigneeMultiSelect");
            },
            error: function (xhr, textStatus, error) {
                debugError(xhr, textStatus, error);
            }
        }).always(function () {
            showAssignee(gadget);
        });
    }
}

function addAllCycle(gadget) {
    var options = $("#" + gadget.id).find("#assigneeCycleAvailable option").clone();
    if (options.length == 0) {
        return;
    }
    $("#" + gadget.id).find("#assigneeCycle").append(options);
    $("#" + gadget.id).find("#assigneeCycleAvailable option").remove();
}

function removeAllCycle(gadget) {
    var options = $("#" + gadget.id).find("#assigneeCycle option").clone();
    if (options.length == 0) {
        return;
    }
    $("#" + gadget.id).find("#assigneeCycleAvailable").append(options);
    $("#" + gadget.id).find("#assigneeCycle option").remove();
}

function hideAssigneeCycle(gadget) {
    $("#" + gadget.id).find('#assignee-cycle-available-div').fadeOut();
    $("#" + gadget.id).find("#assignee-cycle-loader").fadeIn();
}

function showAssigneeCycle(gadget) {
    $("#" + gadget.id).find('#assignee-cycle-available-div').fadeIn();
    $("#" + gadget.id).find("#assignee-cycle-loader").fadeOut();
}

function hideAssignee(gadget) {
    $("#" + gadget.id).find('#assigneeMultiSelect').fadeOut();
    $("#" + gadget.id).find("#assignee-loader").fadeIn();
}

function showAssignee(gadget) {
    $("#" + gadget.id).find('#assigneeMultiSelect').fadeIn();
    $("#" + gadget.id).find("#assignee-loader").fadeOut();
}

function hideAssigneeTable(gadget) {
    $("#" + gadget.id).find('#assignee-table-container').fadeOut();
    $("#" + gadget.id).find("#assignee-table-loader").fadeIn();
}

function showAssigneeTable(gadget) {
    $("#" + gadget.id).find('#assignee-table-container').fadeIn();
    $("#" + gadget.id).find("#assignee-table-loader").fadeOut();
}
