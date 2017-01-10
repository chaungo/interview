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
    $scope.selectAllCycle=true;
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
        
        var callback = function(result) {
            if (result.type == null) {
                $scope.greenHopperCycleLink = result;
                $scope.$apply();
            } else {
                showError(result.data);
            }
        }
        loadCycle(callback);
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
        isNotEmpty = verifyValue([ assigneeProjectVal, assigneeProductVal, assigneeReleaseVal ]);
        if (!assigneeCheckAllCycleVal) {
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
        
    function loadCycle(callback){
        $.ajax({
            url: "/cycleExisting",
            method : "GET",
            dataType : "json",
            success : function (result){ 
                callback(result);
            },
            error : function (error){
                console.log(error);
                showError(error);
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