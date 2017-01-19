// Create new table for each epic or cycle in Story table and Assignee table
/*
 * id: string: gadget id to create a custom table id title: string: epic link or
 * cycle name on top of table container: string: id of the <div> to append this
 * table to
 */
function appendTemplateTable(id, title, gadget, container) {
    $('<h4><b>' + title + '</b></h4>').appendTo($("#" + gadget.id).find(container));
    $('<table id="' + id + '" class="display"></table>').appendTo($("#" + gadget.id).find(container));
    $('<br><hr>').appendTo($("#" + gadget.id).find(container));
}

function prependTemplateTable(id, title, gadget, container) {
    $('<br><hr>').prependTo($("#" + gadget.id).find(container));
    $('<table id="' + id + '" class="display"></table>').prependTo($("#" + gadget.id).find(container));
    $('<h4><b>' + title + '</b></h4>').prependTo($("#" + gadget.id).find(container));
}

function appendTemplateUserStoryTable(id, title, gadget, container, issueKey) {
    var hrefElement = [];
    var htmlTitle = [];
    hrefElement.push('<a href="', GREENHOPPER_BROWSE_ISSUE_LINK, issueKey, '">', title, '</a>');
    htmlTitle.push('<h4><b>', hrefElement.join(""), '</b></h4>')
    $(htmlTitle.join("")).appendTo($("#" + gadget.id).find(container));
    $('<table id="' + id + '" class="display"></table>').appendTo($("#" + gadget.id).find(container));
    $('<br><hr>').appendTo($("#" + gadget.id).find(container));
}
/*
 * Get column array of table to hide from view 
 * metricArray: list: a gadget's metric list
 * isCycleOrAssignee: boolean: column number of Cycle-Assignee and UserStory-Epic table is different.
 * 								put a condition here to check
 */
function getColumnArray(metricArray, isCycleOrAssignee) {
    var columnList = [];
    for (var i = 0; i < metricArray; i++) {
        metricArray[i] = metricArray[i].toUpperCase();
    }

    if (metricArray == null) {
        return;
    }
    if (!isCycleOrAssignee) {
        if ($.inArray('SUMMARY', metricArray) == -1) {
            columnList.push(1);
        }
        if ($.inArray('PRIORITY', metricArray) == -1) {
            columnList.push(2);
        }
        if ($.inArray('UNEXECUTED', metricArray) == -1) {
            columnList.push(3);
        }
        if ($.inArray('FAILED', metricArray) == -1) {
            columnList.push(4);
        }
        if ($.inArray('WIP', metricArray) == -1) {
            columnList.push(5);
        }
        if ($.inArray('BLOCKED', metricArray) == -1) {
            columnList.push(6);
        }
        if ($.inArray('PASSED', metricArray) == -1) {
            columnList.push(7);
        }

        if ($.inArray('PLANNED', metricArray) == -1) {
            columnList.push(8);
        }
        if ($.inArray('UNPLANNED', metricArray) == -1) {
            columnList.push(9);
        }
    } else {
        if ($.inArray('UNEXECUTED', metricArray) == -1) {
            columnList.push(1);
        }
        if ($.inArray('FAILED', metricArray) == -1) {
            columnList.push(2);
        }
        if ($.inArray('WIP', metricArray) == -1) {
            columnList.push(3);
        }
        if ($.inArray('BLOCKED', metricArray) == -1) {
            columnList.push(4);
        }
        if ($.inArray('PASSED', metricArray) == -1) {
            columnList.push(5);
        }
    }

    return columnList;
}

/*
 * Show all hidden table column
 * table: datatable object: designated table to show all table column
 * isCycleOrAssignee: boolean: is this epic-us or cycle-assignee table?
 */
function resetTableColumns(table, isCycleOrAssignee) {
    var list;
    if (table == null) {
        return;
    }
    else if (!isCycleOrAssignee) {
        list = [1, 2, 3, 4, 5, 6, 7, 8, 9];
    }
    else {
        list = [1, 2, 3, 4, 5];
    }

    for (var i = 1; i <= list.length; i++) {
        var column = table.column(i);
        if (!column.visible()) {
            column.visible(!column.visible());
        }
    }
}

function debugAjaxResponse(data) {
    if (data == null) {
        return true;
    } else if (data["type"] == "error") {
        return true;
    }
    console.log(data);

    return false;
}

window.onerror = function (msg, url, linenumber) {
    alert('Unhandled error message: ' + msg + '\nURL: ' + url + '\nLine Number: ' + linenumber);
    return true;
}

function createIssueLinks(data, displayOrType, rowData, setting) {
    var issue = "issue in(";
    var htmlString = [];
    if (data['total'] == 1) {
        htmlString.push('<a href="', GREENHOPPER_ISSUE_API_LINK, issue, data["issues"][0], ')">', data["total"], '</a>');
        return htmlString.join("");
    }
    else if (data['total'] > 1) {
        htmlString.push('<a href="', GREENHOPPER_ISSUE_API_LINK, issue);
        for (var i = 0; i < data["issues"].length; i++) {
            var isLastIndex;

            htmlString.push(data["issues"][i]);
            isLastIndex = ((data["issues"].length - i) == 1) ? true : false;
            if (!isLastIndex) {
                htmlString.push(",");
            }
        }
        htmlString.push(')">', data["total"], '</a>')
        return htmlString.join("");
    }
    return data["total"];
}

function createIssueLinkForTitle(data) {
    var htmlString = [];
    var tempTitle = data["key"];
    tempTitle = tempTitle.toLowerCase();
    if (tempTitle === "total") {
        htmlString.push(data["key"]);
    }
    else {
        htmlString.push('<a href="', GREENHOPPER_BROWSE_ISSUE_LINK, data["key"], '">', data["key"], '</a>');
    }
    return htmlString.join("");

}
