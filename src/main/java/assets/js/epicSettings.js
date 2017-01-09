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

app.controller('EpicSettingController', function ($scope, $rootScope, $window, $mdDialog, $mdToast, $location, $resource) {
    $scope.greenHopperProjectList = [];
    $scope.greenHopperProduct = [];
    $scope.selectedProduct = null;
    $scope.selectedProject = null;
    $scope.selectedRelease = null;
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

    $scope.onCheckAllEpic = function () {
        var epicMultiSelect = $("#epicLinkSelection");
        var epicCheckAll = $("#epicCheckAll").prop('checked');
        if(epicCheckAll){
            epicMultiSelect.css("display", "none");
        }else{
            $scope.onProjectReleaseProductChanged();
            epicMultiSelect.css("display", "");
        }
    }

    $scope.onProjectReleaseProductChanged = function () {
        var epicProjectVal = $("#epicProject").val();
        var epicProductVal = $("#epicProduct").val();
        var epicReleaseVal = $("#epicRelease").val();
        var isNotEmpty = verifyValue([epicProjectVal, epicProductVal, epicReleaseVal]);
        
        var epicCheckAllVal = $("#epicCheckAll").prop('checked');

        if(isNotEmpty && !epicCheckAllVal){
            var requestData = {
                    project : epicProjectVal,
                    release : epicReleaseVal,
                    products : JSON.stringify([epicProductVal])
            }
            var epicLoader = $("#epicLinkSelection");
            
            var callback = function(result) {
                if (result.type == null) {
                    $scope.greenHopperEpicLink = result;
                    $scope.$apply();
                } else {
                    showError(result.data);
                }
            }
            // load Epic Link
            loadEpicLink(epicLoader,requestData, callback);

        }
    }
    

    $scope.isDisabled = false;
    
    $scope.saveGadget = function() {
        var epicProjectVal = $("#epicProject").val();
        var epicProductVal = $("#epicProduct").val();
        var epicReleaseVal = $("#epicRelease").val();
        var metricsVal = $("#epicMetricMultiSelect").val();
        var epicLink = $("#epicLinkSelection").val();
        var isNotEmpty;
        var epicCheckAllVal = $("#epicCheckAll").prop('checked');
        if (epicCheckAllVal) {
            isNotEmpty = verifyValue([ epicProjectVal, epicProductVal, epicReleaseVal ]);
        } else {
            isNotEmpty = verifyValue([ epicProjectVal, epicProductVal, epicReleaseVal ]);
            isNotEmpty &= (epicLink != null && epicLink.length > 0);
        }

        if (isNotEmpty && $rootScope.currentDashboard != null) {
            var object = {};
            // object['id'] = TEST_EPIC_ID;
            var dashboardId = $rootScope.currentDashboard.id;
            object['dashboardId'] = dashboardId;
            object['release'] = epicReleaseVal;
            object['projectName'] = epicProjectVal;
            object['products'] = [ epicProductVal ];
            object['metrics'] = metricsVal;
            if(epicCheckAllVal){
                object['selectAll'] = true;
            }else{
                object['epic'] = epicLink;
            }
            if($rootScope.gadgetIdToEdit!=null){
                object['id'] = $rootScope.gadgetToEdit.id;
            }
            var jsonObj = JSON.stringify(object);
            var callback = function(result) {
                if (result.type != SUCCESS) {
                    console.log(result);
                    showError(result.data);
                } else {
                    $scope.cancel();
                }
            }
            
            saveEpicSettings(jsonObj, callback);
        } else {
            showError("Need to select settings");
        }

    }
    function saveEpicSettings(settings, callback){
        $scope.isDisabled = true;
        $.ajax({
            url: "/gadget/save",
            method: 'POST',
            data: {
              type: 'EPIC_US_TEST_EXECUTION',
              data: settings
            },
            success: function(data) {
                callback(data);
                $scope.isDisabled = false;
            },
            error: function(xhr, textStatus, error) {
                showError(error);
                $scope.isDisabled = false;
            }
          });
    }
        
    function loadEpicLink(loader, requestData,callback){
        loader.addClass("loader");
        $.ajax({
            url: "/getEpicLinks",
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