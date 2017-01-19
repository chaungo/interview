var productTable = null;
var releaseTable = null;
function createProducTable() {
    productTable = $("#productTable").DataTable(
        {
            "searching": false,
            "paging": false,
            "ordering": false,
            "info": false,
            "columnDefs": [{
                "targets": -1,
                "data": null,
                "class": "action",
                "defaultContent": '<button type="button" class="btn btn-danger delete-action"><span class="glyphicon glyphicon-trash" aria-hidden="true"> Delete</span>'
                + '</button>'

            }]
        });
}

function createReleaseTable() {
    releaseTable = $("#releaseTable").DataTable(
        {
            "searching": false,
            "paging": false,
            "ordering": false,
            "info": false,
            "columnDefs": [{
                "targets": -1,
                "data": null,
                "class": "action",
                "defaultContent": '<button type="button" class="btn btn-danger delete-action"><span class="glyphicon glyphicon-trash" aria-hidden="true"> Delete</span>'
                + '</button>'

            }]
        });
}
function deleteProduct(product, callback) {
    $.ajax({
        method: "POST",
        url: "/product/deleteProduct",
        dataType: "json",
        data: {
            "product": product
        },
        dataContext: "json",
        success: function (data) {
            if (productTable != null && data.type == "SUCCESS" && data.data > 0) {
                callback();
            }
        },
        error: function (data) {
            alert(data.statusText);
        }
    });
}

function deleteRelease(release, callback) {
    $.ajax({
        method: "POST",
        url: "/product/deleteRelease",
        dataType: "json",
        data: {
            "release": release
        },
        dataContext: "json",
        success: function (data) {
            if (productTable != null && data.type == "SUCCESS" && data.data > 0) {
                callback();
            }
        },
        error: function (data) {
            alert(data.statusText);
        }
    });
}

function createEvent() {
    $('#addProduct').on('click', function () {
        var newProduct = $("#productInput").val();
        if (newProduct != null && newProduct != "") {
            $.ajax({
                method: "POST",
                url: "/product/insertProduct",
                dataType: "json",
                data: {
                    "product": newProduct
                },
                dataContext: "json",
                success: function (data) {
                    if (productTable != null && data.type == "SUCCESS" && data.data == true) {
                        productTable.row.add([newProduct, ""]).draw(false);
                        addEventTablesAction();
                        $("#productInput").val("");
                    }
                },
                error: function (data) {
                    alert(data);
                }
            });
        }
    });

    $('#addRelease').on('click', function () {
        var newRelease = $("#releaseInput").val();
        if (newRelease != null && newRelease != "") {
            $.ajax({
                method: "POST",
                url: "/product/insertRelease",
                dataType: "json",
                data: {
                    "release": newRelease
                },
                dataContext: "json",
                success: function (data) {
                    if (releaseTable != null && data.type == "SUCCESS" && data.data == true) {
                        releaseTable.row.add([newRelease, ""]).draw(false);
                        addEventTablesAction();
                        $("#releaseInput").val("");
                    } else {
                        alert(data.data);
                    }
                },
                error: function (data) {
                    alert(data);
                }
            });
        }
    });

    $('#clearCacheBtn').on('click', function () {
        $(this).attr("disabled", "true");
        $.get("/cleanAllCache", function (result) {
            if (result.type = "SUCCESS") {
                alert("Clear cache successful");
            }
            $(this).attr("disabled", "");
        })
    });
}

function addEventTablesAction() {
    $('.productWrapper').off();
    $('.productWrapper').unbind();
    $('.productWrapper').on('click', '.delete-action', function (e) {
        if (productTable != null) {
            var row = productTable.row($(this).parents('tr'));
            var removeRowFunction = function () {
                row.remove().draw();
            }
            deleteProduct(row.data()[0], removeRowFunction);
        }

    });

    $('.releaseWrapper').off();
    $('.releaseWrapper').unbind();
    $('.releaseWrapper').on('click', '.delete-action', function (e) {
        if (productTable != null) {
            var row = releaseTable.row($(this).parents('tr'));
            var removeRowFunction = function () {
                row.remove().draw();
            }
            deleteRelease(row.data()[0], removeRowFunction);
        }

    });
}

$(function () {
    createProducTable();
    createReleaseTable();
    createEvent();
    addEventTablesAction();
});
