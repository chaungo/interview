function drawEpicTable(dataTable, gadget, callback, titleHandler, dataTableCallback, clearCacheCallback) {
    var columnList = getColumnArray(gadget.metrics, false);
    resetTableColumns(dataTable, false);
    if (dataTable != null) {
        hideEpicTable(gadget);
        dataTable.ajax.reload(function () {
            if (!dataTable.data().count()) {
                $("#" + gadget.id).find('#epic-table-loader').fadeOut();
                $("#" + gadget.id).find('#epic-table-container').hide();
                clearCacheCallback();
            }
            else {
                showEpicTable(gadget);
                clearCacheCallback();
            }

        });
        dataTable.columns(columnList).visible(true);
        dataTableCallback(dataTable);
    } else {
        hideEpicTable(gadget);
        dataTable = $("#" + gadget.id).find('#epic-table').on(
            'error.dt',
            function (e, settings, techNote, message) {
                clearCacheCallback();
                callback('An error has been reported by DataTables: ' + message)
                showEpicTable(gadget);
            }).DataTable({
            bSort: false,
            paging: false,
            bAutoWidth: false,
            "ajax": {
                url: GET_DATA_URI,
                data: {
                    id: gadget.id
                },
                dataSrc: function (responseJson) {
                    var tempArray = [];
                    if (debugAjaxResponse(responseJson)) {
                        callback(responseJson);
                        showEpicTable(gadget);
                        clearCacheCallback();
                        return [];
                    }

                    $.each(responseJson["data"], function (k1, v1) {
                        $.each(v1["issueData"], function (k2, v2) {
                            tempArray.push(v2);
                        });
                    });

                    showEpicTable(gadget);
                    clearCacheCallback();
                    if (tempArray.length == 0) {
                        $("#" + gadget.id).find('#epic-table-container').hide();
                        titleHandler(0);
                    }
                    else {
                        titleHandler(1);
                    }
                    return tempArray;
                }
            },
            "columns": [{
                "data": "key",
                "render": function (data, displayOrType,
                                    rowData, setting) {
                    return createIssueLinkForTitle(data);
                }
            }, {
                "data": "key.summary",
                "render": function (data, displayOrType,
                                    rowData, setting) {
                    if (data == null) {
                        return "";
                    }
                    else {
                        return data;
                    }
                }
            }, {
                "data": "key.priority.name",
                "render": function (data, displayOrType,
                                    rowData, setting) {
                    if (data == null) {
                        return "";
                    }
                    else {
                        return data;
                    }
                }
            }, {
                "data": "unexecuted",
                "render": function (data, displayOrType,
                                    rowData, setting) {
                    return createIssueLinks(data,
                        displayOrType, rowData, setting);
                }
            }, {
                "data": "failed",
                "render": function (data, displayOrType,
                                    rowData, setting) {
                    return createIssueLinks(data,
                        displayOrType, rowData, setting);
                }
            }, {
                "data": "wip",
                "render": function (data, displayOrType,
                                    rowData, setting) {
                    return createIssueLinks(data,
                        displayOrType, rowData, setting);
                }
            }, {
                "data": "blocked",
                "render": function (data, displayOrType,
                                    rowData, setting) {
                    return createIssueLinks(data,
                        displayOrType, rowData, setting);
                }
            }, {
                "data": "passed",
                "render": function (data, displayOrType,
                                    rowData, setting) {
                    return createIssueLinks(data,
                        displayOrType, rowData, setting);
                }
            }, {
                "data": "planned",
                "render": function (data, displayOrType,
                                    rowData, setting) {
                    return createIssueLinks(data,
                        displayOrType, rowData, setting);
                }
            }, {
                "data": "unplanned",
                "render": function (data, displayOrType,
                                    rowData, setting) {
                    return createIssueLinks(data,
                        displayOrType, rowData, setting);
                }
            }]
        });
        dataTable.columns(columnList).visible(false);
        dataTableCallback(dataTable);
    }
}


function showEpicTable(gadget) {
    $("#" + gadget.id).find('#epic-table-container').fadeIn();
    $("#" + gadget.id).find('#epic-table-loader').hide();
}

function hideEpicTable(gadget) {
    $("#" + gadget.id).find('#epic-table-container').hide();
    $("#" + gadget.id).find('#epic-table-loader').fadeIn();
}
