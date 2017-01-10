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

/*
 * 
 *
 $("#"+gadget.id).find("#cycleProject").change(function() {

 });

 $("#"+gadget.id).find("#cycleRelease").change(function() {

 });

 $("#"+gadget.id).find("#cycleProduct").change(function() {

 });

 /*
 * 
 *

 $("#"+gadget.id).find("#cycle-update-btn").click(function() {
 $(this).prop("disabled", true);
 var jsonString = createJsonStringObjectFromCycleInput();
 callAjaxToUpdateCycle(jsonString);
 });

 $("#"+gadget.id).find("#cycleCheckAll").click(function() {
 if ($(this).prop("checked")) {
 $("#"+gadget.id).find("#cycle-container").fadeOut();
 } else {
 $("#"+gadget.id).find("#cycle-container").fadeIn();
 if ($("#"+gadget.id).find("#cycleMultiSelect option").length == 0) {
 getExistingCycleList();
 }

 }
 });
 /*
 * 
 */

function createJsonStringObjectFromCycleInput(gadget) {
    var object = {};
    if (null == $("#" + gadget.id).find("#dashboardId").val()) {
        alert("No valid dashboard id provided.");
        $("#" + gadget.id).find("#cycle-update-btn").prop("disabled", false);
        return;
    } else if ($("#" + gadget.id).find("#cycleProject").val() == null || $("#" + gadget.id).find("#cycleProject").val() == "") {
        alert("No project selected");
        $("#" + gadget.id).find("#cycle-update-btn").prop("disabled", false);
        return;
    } else if ($("#" + gadget.id).find("#cycleRelease").val() == null || $("#" + gadget.id).find("#cycleRelease").val() == "") {
        alert("No release selected");
        $("#" + gadget.id).find("#cycle-update-btn").prop("disabled", false);
        return;
    } else if ($("#" + gadget.id).find("#cycleProduct").val() == null || $("#" + gadget.id).find("#cycleProduct").val() == "") {
        alert("No Products selected");
        $("#" + gadget.id).find("#cycle-update-btn").prop("disabled", false);
        return;
    } else if ($("#" + gadget.id).find("#cycleMultiSelect").val() == null && !$("#" + gadget.id).find("#cycleCheckAll").prop("checked")) {
        alert("No cycle selected");
        $("#" + gadget.id).find("#cycle-update-btn").prop("disabled", false);
        return;
    } else if ($("#" + gadget.id).find("#cycleMetricMultiSelect").val() == null) {
        alert("No metric selected for this widget");
        $("#" + gadget.id).find("#cycle-update-btn").prop("disabled", false);
        return;
    }

    object['id'] = gadget.id;
    object['dashboardId'] = $("#" + gadget.id).find("#dashboardId").val();
    object['projectName'] = $("#" + gadget.id).find("#cycleProject").val();
    object['release'] = $("#" + gadget.id).find("#cycleRelease").val();
    object['products'] = [$("#" + gadget.id).find("#cycleProduct").val()];
    object['metrics'] = $("#" + gadget.id).find("#cycleMetricMultiSelect").val();
    if ($("#" + gadget.id).find("#cycleCheckAll").prop("checked")) {
        object['selectAllCycle'] = true;
    } else {
        object['cycles'] = $("#" + gadget.id).find("#cycleMultiSelect").val();
    }
    var jsonString = JSON.stringify(object);
    return jsonString
}

function callAjaxToUpdateCycle(gadget, jsonString) {
    if (jsonString != null && jsonString != "") {
        $.ajax({
            url: SAVE_GADGET_URI,
            method: 'POST',
            data: {
                type: CYCLE_TYPE,
                data: jsonString
            },
            beforeSend: function () {
                hideCycleTable(gadget);
            },
            error: function (xhr, textStatus, error) {
                $(this).prop("disabled", true);
                debugError(xhr, textStatus, error);
                $("#" + gadget.id).find("#cycle-update-btn").prop("disabled", false);
                showCycleTable(gadget);
            },
            success: function (data) {
                if (debugAjaxResponse(data)) {
                    $("#" + gadget.id).find("#cycle-update-btn").prop("disabled", false);
                    showCycleTable(gadget);
                    return;
                } else {
                    alert("Gadget updated succesfully");
                    drawCycleTable(data["data"], $("#" + gadget.id).find("#cycleMetricMultiSelect").val());
                }

            }
        });
    }
}

function drawCycleTable(dataTable, gadget, callback) {
    var columnList = getColumnArray(gadget.metrics, true);
    resetTableColumns(dataTable, true);
    if (dataTable != null) {
        hideCycleTable(gadget);
        dataTable.ajax.reload(function () {
            showCycleTable(gadget);
        });
        dataTable.columns(columnList).visible(false);
    } else {
        hideCycleTable(gadget);
        dataTable = $("#" + gadget.id).find('#cycle-table').on(
            'error.dt',
            function (e, settings, techNote, message) {
                callback('An error has been reported by DataTables: ' + message);
                showCycleTable(gadget);
            }).DataTable({
            "fnDrawCallback": function (oSettings) {
                showCycleTable(gadget);
            },
            bAutoWidth: false,
            "ajax": {
                url: "/gadget/getData",
                data: {
                    "id": gadget.id
                },
                dataSrc: function (responseJson) {
                    if (debugAjaxResponse(responseJson)) {
                    	callback(responseJson);
                        showCycleTable(gadget);
                        return [];
                    }
                    var tempArray = [];
                    $.each(responseJson["data"], function (k1, v1) {
                        $.each(v1["issueData"], function (k2, v2) {
                            tempArray.push(v2);
                        });
                    });
                    return tempArray;
                }
            },
            "columns": [{
                "data": "key.key"
            }, {
                "data": "unexecuted",
                "render": function (data, displayOrType, rowData, setting) {
                    return createIssueLinks(data, displayOrType, rowData, setting);
                }
            }, {
                "data": "failed",
                "render": function (data, displayOrType, rowData, setting) {
                    return createIssueLinks(data, displayOrType, rowData, setting);
                }
            }, {
                "data": "wip",
                "render": function (data, displayOrType, rowData, setting) {
                    return createIssueLinks(data, displayOrType, rowData, setting);
                }
            }, {
                "data": "blocked",
                "render": function (data, displayOrType, rowData, setting) {
                    return createIssueLinks(data, displayOrType, rowData, setting);
                }
            }, {
                "data": "passed",
                "render": function (data, displayOrType, rowData, setting) {
                    return createIssueLinks(data, displayOrType, rowData, setting);
                }
            }]

        });
        dataTable.columns(columnList).visible(false);
    }
}

function callAjaxOnCycleProjectAndRelease(gadget) {
    if ($("#" + gadget.id).find("#cycleRelease").val() == null || $("#" + gadget.id).find("#cycleProduct").val() == null || $("#" + gadget.id).find("#cycleRelease").val() == null) {
        return;
    } else if ($("#" + gadget.id).find("#cycleRelease").val() == "" || $("#" + gadget.id).find("#cycleProduct").val() == "" || $("#" + gadget.id).find("#cycleRelease").val() == "") {
        return;
    }
    if (!$("#" + gadget.id).find("#cycleCheckAll").prop("checked")) {
        $.ajax({
            url: "/listcycle?",
            data: {
                project: $("#" + gadget.id).find("#cycleProject").val(),
                release: $("#" + gadget.id).find("#cycleRelease").val(),
                products: JSON.stringify([$("#" + gadget.id).find("#cycleProduct").val()])
            },

            beforeSend: function () {
                hideCycleSelect(gadget);
            },
            success: function (data) {
                if (debugAjaxResponse(data)) {
                    return;
                } else {
                    data.sort();
                    appendToSelect(true, data, "#cycleMultiSelect");
                }

            },
            error: function (xhr, textStatus, error) {
                debugError(xhr, textStatus, error);
            }
        }).always(function () {
            showCycleSelect(gadget);
        });
    }
}

function getExistingCycleList(gadget) {
    if (!$("#" + gadget.id).find("#cycleCheckAll").prop("checked")) {
        $.ajax({
            url: GET_EXISTING_CYCLE_URI,
            beforeSend: function () {
                hideCycleSelect(gadget);
            },
            success: function (data) {
                if (debugAjaxResponse(data)) {
                    return;
                }
                data.sort();
                appendToSelect(true, data, "#cycleMultiSelect");
            },
            error: function (xhr, textStatus, error) {
                debugError(xhr, textStatus, error);
            }
        }).always(function () {
            showCycleSelect(gadget);
        });
    }
}

function hideCycleSelect(gadget) {
    $("#" + gadget.id).find('#cycleMultiSelect').fadeOut();
    $("#" + gadget.id).find("#cycle-loader").fadeIn();
}

function showCycleSelect(gadget) {
    $("#" + gadget.id).find('#cycleMultiSelect').fadeIn();
    $("#" + gadget.id).find("#cycle-loader").fadeOut();
}

function showCycleTable(gadget) {
    $("#" + gadget.id).find('#cycle-table-container').fadeIn();
    $("#" + gadget.id).find("#cycle-table-loader").fadeOut();
}

function hideCycleTable(gadget) {
    $("#" + gadget.id).find('#cycle-table-container').fadeOut();
    $("#" + gadget.id).find("#cycle-table-loader").fadeIn();
}
