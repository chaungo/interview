function drawEpicTable(dataTable, gadget, callback) {
    var columnList = getColumnArray(gadget.metrics, false);
    resetTableColumns(dataTable, false);
    if (dataTable != null) {
        hideEpicTable(gadget);
        dataTable.ajax.reload(function () {
            showEpicTable(gadget);
        });
        dataTable.columns(columnList).visible(true);

    } else {
        hideEpicTable(gadget);
        dataTable = $("#" + gadget.id).find('#epic-table').on(
            'error.dt',
            function (e, settings, techNote, message) {
            	callback('An error has been reported by DataTables: ' + message)
                showEpicTable(gadget);
            }).DataTable({
            "fnDrawCallback": function (oSettings) {
                showEpicTable(gadget);
            },
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
                        return [];
                    }

                    $.each(responseJson["data"], function (k1, v1) {
                        $.each(v1["issueData"], function (k2, v2) {
                            tempArray.push(v2);
                        });
                    });
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
                "data": "key.summary"
            }, {
                "data": "key.priority.name",
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
    }
}



function showEpicTable(gadget) {
    $("#" + gadget.id).find('#epic-table-container').fadeIn();
    $("#" + gadget.id).find('#epic-table-loader').fadeOut();
}

function hideEpicTable(gadget) {
    $("#" + gadget.id).find('#epic-table-container').fadeOut();
    $("#" + gadget.id).find('#epic-table-loader').fadeIn();
}
