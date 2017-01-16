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
function drawUsTable(dataTable, gadget, callback,titleHandler) {
    var columnList = getColumnArray(gadget.metrics, false);
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
        beforeSend: function () {
            dataTable.loading = true;
            hideUsTable(gadget);
        },
        error: function (xhr, textStatus, error) {
            callback("Server Error");
        },
        success: function (responseData) {
        	var index = 0;
            var title = "";
            if (debugAjaxResponse(responseData)) {
                callback(responseData);
                showUsTable(gadget);
                return;
            }
            
            $("#" + gadget.id).find("#us-table-container").html("");
            jsonObjectForUsTable = responseData;
            $.each(jsonObjectForUsTable["data"], function (epicKey,
                                                           storyArray) {
                if (storyArray["issueData"].length != 0) {
                    var customTableId = "us-table-" + index;
                    var usTableDataSet = [];
                    var usIndividualTable;
                    var tempTitle = [];
                    tempTitle.push(index + 1, ". ", epicKey, ": ",storyArray["summary"]);
                    appendTemplateUserStoryTable(customTableId, tempTitle.join(""), gadget,
                        "#us-table-container", epicKey);
                    $("#" + gadget.id).find("#" + customTableId).append(TEMPLATE_HEADER_FOOTER);


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

                    usIndividualTable = $("#" + gadget.id).find("#" + customTableId).DataTable({
                        bAutoWidth: false,
                        data: usTableDataSet,
                        columns: [{
                            title: "User Story",
                            "render": function (data, displayOrType, rowData, setting) {
                                return createIssueLinkForTitle(data);
                            }
                        }, {
                            title: "SUMMARY"
                        }, {
                            title: "PRIORITY"
                        }, {
                            title: "UNEXECUTED",
                            "render": function (data, displayOrType, rowData, setting) {
                                return createIssueLinks(data, displayOrType, rowData, setting);
                            }
                        }, {
                            title: "FAILED",
                            "render": function (data, displayOrType, rowData, setting) {
                                return createIssueLinks(data, displayOrType, rowData, setting);
                            }
                        }, {
                            title: "WIP",
                            "render": function (data, displayOrType, rowData, setting) {
                                return createIssueLinks(data, displayOrType, rowData, setting);
                            }
                        }, {
                            title: "BLOCKED",
                            "render": function (data, displayOrType, rowData, setting) {
                                return createIssueLinks(data, displayOrType, rowData, setting);
                            }
                        }, {
                            title: "PASSED",
                            "render": function (data, displayOrType, rowData, setting) {
                                return createIssueLinks(data, displayOrType, rowData, setting);
                            }
                        }, {
                            title: "PLANNED",
                            "render": function (data, displayOrType, rowData, setting) {
                                return createIssueLinks(data, displayOrType, rowData, setting);
                            }
                        }, {
                            title: "UNPLANNED",
                            "render": function (data, displayOrType, rowData, setting) {
                                return createIssueLinks(data, displayOrType, rowData, setting);
                            }
                        }]
                    });
                    usIndividualTable.columns(columnList).visible(false);
                    index++;
                    
                }
                dataTable.loading = false;
                showUsTable(gadget);
                titleHandler(index);
            });
        }
    });
    return index;
}

function showUsTable(gadget) {
    $("#" + gadget.id).find('#us-table-loader').fadeOut();
    $("#" + gadget.id).find("#us-table-container").fadeIn();
}

function hideUsTable(gadget) {
    $("#" + gadget.id).find('#us-table-loader').fadeIn();
    $("#" + gadget.id).find("#us-table-container").fadeOut();
}
