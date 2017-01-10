/*
 * Function section
 */


function drawAssigneeTable(dataTable, gadget, callback) {
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
                callback(error);
                showAssigneeTable(gadget);
            },
            success: function (responseData) {
                dataTable.loading = false;
                var index = 0;
                $("#" + gadget.id).find("#assignee-table-container").html("");
                if (debugAjaxResponse(responseData)) {
                    callback(responseData);
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
