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


function drawCycleTable(dataTable, gadget, callback, titleHandler, dataTableCallBack, clearCacheCallback, updateTimeCallback) {
    var columnList = getColumnArray(gadget.metrics, true);
    resetTableColumns(dataTable, true);
    if (dataTable != null) {
        hideCycleTable(gadget);
        dataTable.ajax.reload(function () {
            if (!dataTable.data().count()) {
                $("#" + gadget.id).find('#cycle-table-loader').fadeOut();
                $("#" + gadget.id).find('#cycle-table-container').hide();
            }
            else {
                showCycleTable(gadget);
                
            }
            clearCacheCallback();
            updateTimeCallback(0);
        });
        dataTable.columns(columnList).visible(false);
        dataTableCallBack(dataTable);

    } else {
        hideCycleTable(gadget);
        dataTable = $("#" + gadget.id).find('#cycle-table').on(
            'error.dt',
            function (e, settings, techNote, message) {
                callback('An error has been reported by DataTables: ' + message);
                clearCacheCallback();
                showCycleTable(gadget);
            }).DataTable({
            bSort: false,
            paging: false,
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
                        clearCacheCallback();
                        return [];
                    }
                    var tempArray = [];
                    $.each(responseJson["data"], function (k1, v1) {
                    	updateTimeCallback(v1["lastUpdate"]);
                        $.each(v1["issueData"], function (k2, v2) {
                            tempArray.push(v2);
                        });
                    });
                    clearCacheCallback();
                    showCycleTable(gadget);
                    if (tempArray.length == 0) {
                        $("#" + gadget.id).find('#cycle-table-container').hide();
                        titleHandler(0);
                    }
                    else {
                        titleHandler(1);
                    }
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
        dataTableCallBack(dataTable);
    }
}


function hideCycleSelect(gadget) {
    $("#" + gadget.id).find('#cycleMultiSelect').fadeOut();
    $("#" + gadget.id).find("#cycle-loader").fadeIn();
}

function showCycleSelect(gadget) {
    $("#" + gadget.id).find('#cycleMultiSelect').fadeIn();
    $("#" + gadget.id).find("#cycle-loader").hide();
}

function showCycleTable(gadget) {
    $("#" + gadget.id).find('#cycle-table-container').fadeIn();
    $("#" + gadget.id).find("#cycle-table-loader").hide();
}

function hideCycleTable(gadget) {
    $("#" + gadget.id).find('#cycle-table-container').hide();
    $("#" + gadget.id).find("#cycle-table-loader").fadeIn();
}
