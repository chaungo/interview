var SUCCESS = "SUCCESS";
function getGreenHopperProjectList(callBack) {
    $.get("/listproject", function (data) {
        callBack(data);
    });
}
function getGreenHopperProduct(callBack) {
    $.get("/product/getall", function (data) {
        callBack(data);
    });
}
function verifyValue(arrayArgument) {
    var verify = true;
    $.each(arrayArgument, function (index, value) {
        verify &= (value != null && value != "");
    });
    return verify;
}

app.controller('AssigneeSettingController', function ($scope, $rootScope, $window, $mdDialog, $mdToast, $location, $resource) {
    $scope.greenHopperProjectList = [];
    $scope.greenHopperProduct = [];
    $scope.selectedProduct = null;
    $scope.selectedProject = null;
    $scope.selectedRelease = null;
    $scope.cancel = function () {
        $mdDialog.cancel();
    }

    $scope.init = function () {
        console.log('init assignee controller');
        var callBack = function (result) {
            if (result.type == null) {
                $scope.greenHopperProjectList = result;
                $scope.$apply();
            } else {
                alert(result.data);
            }
        }
        var callBackProduct = function (result) {
            if (result.type == SUCCESS) {
                $scope.greenHopperProduct = result.data;
                $scope.$apply();
            } else {
                alert(result.data);
            }

        }

        getGreenHopperProjectList(callBack);
        getGreenHopperProduct(callBackProduct);
    }

    $scope.onCheckAllEpic = function () {
        var epicMultiSelect = $("#epicMultiSelect");
        var epicCheckAll = $("#epicCheckAll").prop('checked');
        if (epicCheckAll) {
            epicMultiSelect.css("display", "none");
        } else {
            epicMultiSelect.css("display", "");
        }
    }

    $scope.onProjectReleaseProductChanged = function () {
        var epicProject = $("#epicProject").val();
        var epicProduct = $("#epicProduct").val();
        var epicRelease = $("#epicRelease").val();
        var isNotEmpty = verifyValue([epicProject, epicProduct, epicRelease]);
        var epicCheckAll = $("#epicCheckAll").prop('checked');

        if (isNotEmpty && !epicCheckAll) {
            var requestData = {
                project: epicProject,
                release: epicRelease,
                products: JSON.stringify([epicProduct])
            }
            var epicLoader = $("#epiclinkloader");
            var callback = function (result) {
                console.log(result);
                $scope.greenHopperEpicLink = result;
                $scope.$apply();
            }
            //load Epic Link
            loadEpicLink(epicLoader, requestData, callback);

        }
    }
    function loadEpicLink(loader, requestData, callback) {
        loader.removeClass("hide");
        $.ajax({
            url: "/getEpicLinks",
            method: "GET",
            dataType: "json",
            data: requestData,
            success: function (result) {
                callback(result);
                loader.addClass("hide");
            },
            error: function (error) {
                console.log(error);
                loader.addClass("hide");
            }
        });
    }
});