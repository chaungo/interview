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
