var productTable = null;
var cycleTable = null;
function createProducTable() {
    productTable = $("#productTable").DataTable(
            {
                "searching" : false,
                "paging" : false,
                "ordering" : false,
                "info" : false,
                "columnDefs" : [ {
                    "targets" : -1,
                    "data" : null,
                    "class" : "action",
                    "defaultContent" : 
                            '<button type="button" class="btn btn-danger delete-action"><span class="glyphicon glyphicon-trash" aria-hidden="true"> Delete</span>'
                            + '</button>'

                } ]
            });
}

function createCycleTable(){
    cycleTable = $("#cycleTable").DataTable(
            {
                "searching" : false,
                "paging" : false,
                "ordering" : false,
                "info" : false,
                "columnDefs" : [ {
                    "targets" : -1,
                    "data" : null,
                    "class" : "action",
                    "defaultContent" : 
                            '<button type="button" class="btn btn-danger delete-action"><span class="glyphicon glyphicon-trash" aria-hidden="true"> Delete</span>'
                            + '</button>'

                } ]
            });
}
function deleteProduct(product, callback) {
    $.ajax({ method : "POST",
    url : "/product/deleteProduct",
    dataType : "json",
    data : { "product" : product
    },
    dataContext : "json",
    success : function(data) {
        if (productTable != null && data.type == "success" && data.data >0) {
            callback();
        }
    },
    error : function(data) {
        alert(data.statusText);
    }
    });
}

function deleteCycle(cycle, callback) {
    $.ajax({ method : "POST",
    url : "/product/deleteCycle",
    dataType : "json",
    data : { "cycle" : cycle
    },
    dataContext : "json",
    success : function(data) {
        if (productTable != null && data.type == "success" && data.data >0) {
            callback();
        }
    },
    error : function(data) {
        alert(data.statusText);
    }
    });
}

function createEvent() {
    $('#addProduct').on('click', function() {
        var newProduct = $("#productInput").val();
        if (newProduct != null && newProduct != "") {
            $.ajax({
                method : "POST",
                url : "/product/insertProduct",
                dataType : "json",
                data : {
                    "product" : newProduct
                },
                dataContext : "json",
                success : function(data) {
                    if (productTable != null && data.type == "success" && data.data == true) {
                        productTable.row.add([ newProduct, "" ]).draw(false);
                        addEventTablesAction();
                        $("#productInput").val("");
                    }
                },
                error : function(data) {
                    alert(data);
                }
            });
        }
    });
    
    $('#addCycle').on('click', function() {
        var newCycle = $("#cycleInput").val();
        if (newCycle != null && newCycle != "") {
            $.ajax({
                method : "POST",
                url : "/product/insertCycle",
                dataType : "json",
                data : {
                    "cycle" : newCycle
                },
                dataContext : "json",
                success : function(data) {
                    if (cycleTable != null && data.type == "success" && data.data == true) {
                        cycleTable.row.add([ newCycle, "" ]).draw(false);
                        addEventTablesAction();
                        $("#cycleInput").val("");
                    }
                },
                error : function(data) {
                    alert(data);
                }
            });
        }
    });
    
    $('#findCycle').on('click', function(){
        $("#searchComponent").attr("disabled", ""); 
        var releaseValue = $("#release").val();
        var productValue = $("#product").val();
        var productValues =[productValue];
        
        $("#suggest").html('');
        $("#loader").addClass("loader");
        $.ajax({
            method : "GET",
            url : "/listcycle",
            dataType : "json",
            dataContext: "json",
            data: {
                products : JSON.stringify(productValues),
                release : releaseValue,
                project : "FNMS 557x"
            },
            success : function(data) {
                console.log(data);
                if ($.isArray(data)) {
                    for (i = 0; i < data.length; i++) {
                        $("#suggest").append('<option>' + data[i] + '</option>');
                    }
                    $("#searchComponent").removeAttr("disabled");
                }else{
                    if(data.type == "error"){
                        alert(data.data);
                    }
                }
                $("#loader").removeClass("loader");
            },
            error : function(data) {
                $("#loader").removeClass("loader");
            }
        });
    });
}

function addEventTablesAction() {
    $('.productWrapper').off();
    $('.productWrapper').unbind();
    $('.productWrapper').on('click', '.delete-action', function(e) {
        if (productTable != null) {
            var row = productTable.row($(this).parents('tr'));
            var removeRowFunction = function() {
                row.remove().draw();
            }
            deleteProduct(row.data()[0], removeRowFunction);
        }
        
    });
    
    $('.cycleWrapper').off();
    $('.cycleWrapper').unbind();
    $('.cycleWrapper').on('click', '.delete-action', function(e) {
        if (productTable != null) {
            var row = cycleTable.row($(this).parents('tr'));
            var removeRowFunction = function() {
                row.remove().draw();
            }
            deleteCycle(row.data()[0], removeRowFunction);
        }
        
    });
}

$(function() {
    createProducTable();
    createCycleTable();
    createEvent();
    addEventTablesAction();
});
