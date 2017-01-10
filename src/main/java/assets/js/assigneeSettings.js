var SUCCESS = "SUCCESS";
function getGreenHopperProjectList(callBack){
    $.get("/listproject", function (data) {
        callBack(data);
    });
}
function getGreenHopperProduct(callBack){
    $.get("/product/getall", function (data) {
        callBack(data);
    });
}
function verifyValue(arrayArgument){
    var verify = true;
    $.each(arrayArgument, function( index, value ) {
        verify &= (value !=null && value!= "");
    });
    return verify;
}

app.controller('AssigneeSettingController', function ($scope, $rootScope, $window, $mdDialog, $mdToast, $location, $resource) {
    $scope.greenHopperProjectList = [];
    $scope.greenHopperProduct = [];
    $scope.selectedProduct = null;
    $scope.selectedProject = null;
    $scope.selectedRelease = null;
    $scope.greenHopperCycleLink = [];
    $scope.cancel = function () {
        $mdDialog.cancel();
    }

    $scope.init = function () {
        var callBack = function (result) {
            if(result.type ==null){
                $scope.greenHopperProjectList = result;
                $scope.$apply();
            }else{
                console.log(result);
                showError(result.data);
            }
        }
        var callBackProduct = function (result) {
            if (result.type == SUCCESS) {
                $scope.greenHopperProduct = result.data;
                $scope.$apply();
            }else{
                console.log(result);
                showError(result.data);
            }

        }
        getGreenHopperProjectList(callBack);
        getGreenHopperProduct(callBackProduct);
    }

    $scope.onCheckAllCycle = function () {
        var assigneeCycle = $("#assigneeCycle");
        var assigneeCheckAllCycle = $("#assigneeCheckAllCycle").prop('checked');
        if(assigneeCheckAllCycle){
            assigneeCycle.css("display", "none");
        }else{
            $scope.onProjectReleaseProductChanged();
            assigneeCycle.css("display", "");
        }
    }

    $scope.onProjectReleaseProductChanged = function () {
        var assigneeProjectVal = $("#assigneeProject").val();
        var assigneeProductVal = $("#assigneeProduct").val();
        var assigneeReleaseVal = $("#assigneeRelease").val();
        var isNotEmpty = verifyValue([assigneeProjectVal, assigneeProductVal, assigneeReleaseVal]);
        
        var assigneeCheckAllCycleVal = $("#assigneeCheckAllCycle").prop('checked');

        if(isNotEmpty && !assigneeCheckAllCycleVal){
            var requestData = {};
            var assigneeLoader = $("#assigneeCycle");
            
            var callback = function(result) {
                if (result.type == null) {
                    $scope.greenHopperCycleLink = result;
                    $scope.$apply();
                } else {
                    showError(result.data);
                }
            }
            // load Assignee Link
            loadCycle(assigneeLoader,requestData, callback);

        }
    }
    

    $scope.isDisabled = false;
    
    $scope.saveGadget = function() {
        var assigneeProjectVal = $("#assigneeProject").val();
        var assigneeProductVal = $("#assigneeProduct").val();
        var assigneeReleaseVal = $("#assigneeRelease").val();
        var metricsVal = $("#assigneeMetricMultiSelect").val();
        var assigneeCycle = $("#assigneeCycle").val();
        var isNotEmpty;
        var assigneeCheckAllCycleVal = $("#assigneeCheckAllCycle").prop('checked');
        if (assigneeCheckAllCycleVal) {
            isNotEmpty = verifyValue([ assigneeProjectVal, assigneeProductVal, assigneeReleaseVal ]);
        } else {
            isNotEmpty = verifyValue([ assigneeProjectVal, assigneeProductVal, assigneeReleaseVal ]);
            isNotEmpty &= (assigneeCycle != null && assigneeCycle.length > 0);
        }

        if (isNotEmpty && $rootScope.currentDashboard != null) {
            var object = {};
            // object['id'] = TEST_EPIC_ID;
            var dashboardId = $rootScope.currentDashboard.id;
            object['dashboardId'] = dashboardId;
            object['release'] = assigneeReleaseVal;
            object['projectName'] = assigneeProjectVal;
            object['products'] = [ assigneeProductVal ];
            object['metrics'] = metricsVal;
            if(assigneeCheckAllCycleVal){
                object['selectAllTestCycle'] = true;
            }else{
                object['cycles'] = assigneeCycle;
            }
            if($rootScope.gadgetToEdit!=null){
                object['id'] = $rootScope.gadgetToEdit.id;
            }
            var jsonObj = JSON.stringify(object);
            var callback = function(result) {
                if (result.type != SUCCESS) {
                    console.log(result);
                    showError(result.data);
                } else {
                    $scope.cancel();
                    $rootScope.showGadget();
                }
            }
            
            saveGadgetSettings('ASSIGNEE_TEST_EXECUTION', jsonObj, callback);
        } else {
            showError("Need to select settings");
        }

    }
        
    function loadCycle(loader, requestData, callback){
        loader.addClass("loader");
        $.ajax({
            url: "/cycleExisting",
            method : "GET",
            dataType : "json",
            data:requestData,
            success : function (result){ 
                callback(result);
                loader.removeClass("loader");
            },
            error : function (error){
                console.log(error);
                loader.removeClass("loader");
            }
        });
    }
    function showError(message){
        $mdToast.show(
                $mdToast.simple()
                    .textContent(message)
                    .hideDelay(5000)
            );
    }
});