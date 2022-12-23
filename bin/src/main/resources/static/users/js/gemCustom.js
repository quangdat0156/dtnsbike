var fullNamme = function () {
    $('.fullname').text('Họ và tên: ' + $('#lastname').val() + ' ' + $('#middlename').val() + ' ' + $('#firstname').val());
};
$('.spell input').on('keyup', function () {
    fullNamme();
});

// Restricts input for the set of matched elements to the given inputFilter function.
(function ($) {
    $.fn.inputFilter = function (callback, errMsg) {
        return this.on("input keydown keyup mousedown mouseup select contextmenu drop focusout", function (e) {
            if (callback(this.value)) {
                // Accepted value
                if (["keydown", "mousedown", "focusout"].indexOf(e.type) >= 0) {
                    $(this).removeClass("input-error");
                    this.setCustomValidity("");
                }
                this.oldValue = this.value;
                this.oldSelectionStart = this.selectionStart;
                this.oldSelectionEnd = this.selectionEnd;
            } else if (this.hasOwnProperty("oldValue")) {
                // Rejected value - restore the previous one
                // $(this).addClass("input-error");
                // this.setCustomValidity(errMsg);
                // this.reportValidity();
                this.value = this.oldValue;
                this.setSelectionRange(this.oldSelectionStart, this.oldSelectionEnd);
            } else {
                // Rejected value - nothing to restore
                this.value = "";
            }
        });
    };
}(jQuery));

function findMaxValue(element) {
    var maxValue = 0;
    $('option', element).each(function () {
        var val = $(this).attr('value');
        val = parseInt(val);
        if (!isNaN(val) && maxValue < val) {
            maxValue = val;
        }
    });
    return maxValue;
}


const monthNames = [];
for (var i = 0; i < 12; i++) {
    monthNames.push('Tháng ' + (i + 1));
}

let qntYears = 69;
let selectYear = $("#year");
let selectMonth = $("#month");
let selectDay = $("#day");
let currentYear = new Date().getFullYear();
for (var y = 0; y < qntYears; y++) {
    let date = new Date(currentYear);
    let yearElem = document.createElement("option");
    yearElem.value = currentYear
    yearElem.textContent = currentYear;
    selectYear.append(yearElem);
    currentYear--;
}

for (var m = 0; m < 12; m++) {
    let month = monthNames[m];
    let monthElem = document.createElement("option");
    monthElem.value = m;
    monthElem.textContent = month;
    selectMonth.append(monthElem);
}


selectYear.on("change", AdjustDays);
selectMonth.on("change", AdjustDays);

AdjustDays();
// selectDay.val(day)
function AdjustDays() {
    var year = selectYear.val();
    var month = parseInt(selectMonth.val()) + 1;
    var oldDay = selectDay.val();
    //get the last day, so the number of days in that month
    var days = new Date(year, month, 0).getDate();
    var max = parseInt(selectDay.children('option').length);
    var x = max - days;
    if (max == 0) {
        for (var d = 1; d <= days; d++) {
            var dayElem = document.createElement("option");
            dayElem.value = d;
            dayElem.textContent = d;
            selectDay.append(dayElem);
        }
    } else {
        if (x > 0) {
            for (var d = 1; d <= x; d++) {
                var i = Number(max - d);
                var dayElem = selectDay.children('option');
                dayElem[i].remove();
            }
        } else if (x < 0) {
            for (var d = max; d < days; d++) {
                var dayElem = document.createElement("option");
                dayElem.value = d + 1;
                dayElem.textContent = d + 1;
                selectDay.append(dayElem);
            }
        }
    }
    if (oldDay != null) {
        if (oldDay <= days) {
            selectDay.val(oldDay).change();
        } else if (oldDay > days) {
            selectDay.val(days).change();
        }
    }
}


//UpdateProfile Angular.js
var host = "http://localhost:8080/DTNsBike/rest/";
var hostN = "http://localhost:8080/DTNsBike/";
var app = angular.module("GemApp", []);

app.directive('email', function () {
    var EMAIL_REGEXP = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return {
        require: '?ngModel',
        link: function (scope, elm, attrs, ctrl) {
            // only apply the validator if ngModel is present and AngularJS has added the email validator
            if (ctrl && ctrl.$validators.email) {

                // this will overwrite the default AngularJS email validator
                ctrl.$validators.email = function (modelValue) {
                    return ctrl.$isEmpty(modelValue) || EMAIL_REGEXP.test(modelValue);
                };
            }
        }
    };
});

app.directive('phone', function () {
    // var test = ((3[2-9])|(5[689])|(7[06-9])|(8[1-689])|(9[0-46-9]))(\\d)(\\s|\\.)?(\\d{3})(\\s|\\.)?(\\d{3});
    var PHONE_REGEXP = /(0(3[2-9])|(5[689])|(7[06-9])|(8[1-689])|(9[0-46-9]))+([0-9]{7})\b/;
    return {
        require: '?ngModel',
        link: function (scope, elm, attrs, ctrl) {
            ctrl.$validators.phone = function (modelValue) {
                return ctrl.$isEmpty(modelValue) || PHONE_REGEXP.test(modelValue);
            };
        }
    };
});

app.directive("cus", function () {
    return {
        restrict: "A",
        link: function (scope, elem, attrs) {
            $(elem).inputFilter(function (value) {
                return /^\d*$/.test(value);
            });
        }
    }
});



app.filter('replace', [function () {

    return function (input, from, to) {

        if (input === undefined) {
            return;
        }

        var regex = new RegExp(from, 'g');
        return input.replace(regex, to);

    };


}]);

app.controller("headerMenuCtrl", function ($scope, $http, $rootScope, $timeout) {
    $rootScope.$on('getToTalCart', function () {
        $http.get(`${hostN}cartList`).then(resp => {
            $scope.cartQty = resp.data.length;
            $("#navbarCollapse>div.loading-gem.bg-header").remove();
        }).catch(error => {
            $scope.cartQty = 0;
            $("#navbarCollapse>div.loading-gem.bg-header").remove();
        });
    })
    $rootScope.$emit('getToTalCart');

});

app.controller("detailCtrl", function ($scope, $http, $rootScope) {
    function reset() {
        $scope.ProDeId = null;
        $scope.size = null;
        $scope.color = null;
        $('.size').prop('disabled', false);
        $('.color').prop('disabled', false);
        $('.size').prop('checked', false);
        $('.color').prop('checked', false);
        $('div.position-relative>div.loading-gem').remove();
    }

    window.onload = function () {
        reset()
    }
    $scope.lood = function (ok) {
        $http.get(`${hostN}getProductDetail/${ok}`).then(resp => {
            $scope.getProD = resp.data;
            $scope.totalAmount = 0;
            $scope.hideColor = false;
            $scope.hideSize = false;
            for (var i = 0; i < resp.data.length; i++) {
                $scope.totalAmount += Number(resp.data[i].amount);
                if (i > 0 && $scope.getProD[0].sizeid.id != resp.data[i].sizeid.id) {
                    $scope.hideSize = true;
                }
                if (i > 0 && $scope.getProD[0].colorid.id != resp.data[i].colorid.id) {
                    $scope.hideColor = true;
                }
            }
            if ($scope.hideSize == false) {
                $scope.size = $scope.getProD[0].sizeid.id;
            }
            if ($scope.hideColor == false) {
                $scope.color = $scope.getProD[0].colorid.id;
            } if ($scope.hideSize == false && $scope.hideColor == false) {
                $scope.ProDeId = $scope.getProD[0].id;
            }
            $scope.amount = $scope.totalAmount;
        }).catch(error => {
            console.log("Error", error);
        });

    }
    console.log('%c Nguyễn AT ', 'color:red; font-size: 20px; font-weight: bold;border: 1px dotted red')
    $scope.chk = function (event) {
        var val = $(event.target).val();
        if ($scope.size != null && $scope.size == val) {
            $scope.size = null;
            $('.color').prop('disabled', false);
        }
        if ($scope.color != null && $scope.color == val) {
            $scope.color = null;
            $('.size').prop('disabled', false);
        } if ($scope.color == null || $scope.size == null) {
            $scope.amount = $scope.totalAmount;
        }
    }
    $scope.chooseSizeColor = function () {
        var have = 0;
        if ($scope.color != null) {
            $('.size').prop('disabled', true);
            for (var i = 0; i < $scope.getProD.length; i++) {
                if ($scope.color == $scope.getProD[i].colorid.id) {
                    if ($scope.getProD[i].amount > 0) {
                        $('#size' + $scope.getProD[i].sizeid.id).prop('disabled', false);
                    }
                    $scope.ProDeId = $scope.getProD[i].id;
                    if ($scope.size == $scope.getProD[i].sizeid.id && $scope.color != null) {
                        $scope.ProDeId = $scope.getProD[i].id;
                        have = 1;
                    }
                }
            }
        }
        if ($scope.size != null) {
            $('.color').prop('disabled', true);
            for (var i = 0; i < $scope.getProD.length; i++) {
                if ($scope.size == $scope.getProD[i].sizeid.id) {
                    if ($scope.getProD[i].amount > 0) {
                        $('#color' + $scope.getProD[i].colorid.id).prop('disabled', false);
                    }
                    if ($scope.color == $scope.getProD[i].colorid.id && $scope.size != null) {
                        $scope.ProDeId = $scope.getProD[i].id;
                        have = 1;
                    }
                }
            }
        }
        if (have == 0 && $scope.size != null && $scope.color != null) {
            reset();
        }
        if ($scope.size != null && $scope.color != null) {
            $http.get(`${hostN}getCounrPro?id=${$scope.ProDeId}`).then(resp => {
                if (resp.data != '') {
                    $scope.amount = resp.data;
                    $(".number").val(1);
                }
            }).catch(error => {
                console.log("Error", error);
            });
        }

    }

    $scope.addToCart = function () {
        checkQty();
        if ($scope.size != null && $scope.color != null && $scope.ProDeId != null) {
            var qty = $(".number").val();
            $http.get(`${hostN}addToCart?id=${$scope.ProDeId}&qty=${qty}`).then(resp => {
                $('#message').modal('show');
                $('#message>.modal-dialog>.modal-content>.modal-body').html(resp.data.icon + resp.data.message);
                $rootScope.$emit('getToTalCart');
            }).catch(error => {
                if (error.data.url != undefined) {
                    window.location = hostN + error.data.url;
                } else {
                    $('#message').modal('show');
                    $('#message>.modal-dialog>.modal-content>.modal-body').html(error.data.icon + error.data.message);
                }
            });
            setTimeout(function () {
                $('#message').modal('hide');
            }, 2000);
            $scope.errorChoose = "";
        } else {
            $scope.errorChoose = "Vui lòng chọn phân loại sản phẩm ";
        }
    }
    $scope.buyNow = function () {
        checkQty();
        if ($scope.size != null && $scope.color != null && $scope.ProDeId != null) {
            var qty = $(".number").val();
            $http.get(`${hostN}addToCart?id=${$scope.ProDeId}&qty=${qty}`).then(resp => {
                window.location = hostN + "cart.html";
            }).catch(error => {
                if (error.data.url != undefined) {
                    window.location = hostN + error.data.url;
                }
                $('#message').modal('show');
                $('#message>.modal-dialog>.modal-content>.modal-body').html(error.data.icon + error.data.message);
            });
            setTimeout(function () {
                $('#message').modal('hide');
            }, 2000);
            $scope.errorChoose = "";
        } else {
            $scope.errorChoose = "Vui lòng chọn phân loại sản phẩm ";
        }
    }
    $('.quantity button').on('click', function () {
        var button = $(this);
        var oldValue = button.parent().parent().find('input').val();
        if (button.hasClass('btn-plus') && $scope.amount > oldValue) {
            var newVal = parseFloat(oldValue) + 1;
        } else {
            if (oldValue >= 2 && !button.hasClass('btn-plus')) {
                var newVal = parseFloat(oldValue) - 1;
            } else {
                newVal = oldValue;
            }
        }
        button.parent().parent().find('input').val(newVal);
    });
    function checkQty() {
        $(".number").on('input keydown keyup mousedown mouseup select contextmenu drop focusout', function (e) {
            var qty = Number($(".number").val());
            if (qty <= 0) {
                $(".number").val(1)
            } else {
                if (qty > $scope.amount) {
                    $(".number").val($scope.amount);
                } else {
                    $(".number").val(qty);
                }
            }
        });
    }
    checkQty();
});

app.controller("updateProfileCtrl", function ($scope, $http, $timeout, $rootScope) {
    $scope.choose = function () {
        $('#ava').click();
    }
    $scope.account = [];
    //upload ảnh 
    $scope.upload = function (files) {
        var form = new FormData();
        form.append('files', files[0]);
        form.append('id', $scope.account.username);
        $http.post(`${host}upload/accounts_img`, form, {
            transformRequest: angular.identity,
            headers: { 'Content-Type': undefined }
        }).then(resp => {
            if (resp.data.name == undefined) {
                $('#message').modal('show');
                $('#message>.modal-dialog>.modal-content>.modal-body').html("<i class='far fa-times-circle text-danger mb-1' style='font-size: 60px;'></i><p>Ảnh không đúng định dạng</p>");

            } else {
                $scope.account.photo = resp.data.name;
            }
        }).catch(error => {
            $('#message').modal('show');
            $('#message>.modal-dialog>.modal-content>.modal-body').html("<i class='far fa-times-circle text-danger mb-1' style='font-size: 60px;'></i><p>Ảnh vượt quá giới hạn cho phép</p>");

        })
        setTimeout(function () {
            $('#message').modal('hide');
        }, 2000);
    }
    $http.get(`${host}update_profile/account`).then(resp => {
        $scope.account = resp.data;
        AdjustDays();
        function AdjustDays() {
            var year = $scope.account.year;
            var month = Number($scope.account.month) + 1;
            //get the last day, so the number of days in that month
            var days = new Date(year, month, 0).getDate();
            var max = findMaxValue($('#day'));
            var x = max - days;
            if (x > 0) {
                for (var d = 0; d < x; d++) {
                    var i = Number(max - d);
                    var dayElem = selectDay.children('option');
                    dayElem[i].remove();
                }
            } else if (x < 0) {
                for (var d = max; d < days; d++) {
                    var dayElem = document.createElement("option");
                    dayElem.value = d + 1;
                    dayElem.textContent = d + 1;
                    selectDay.append(dayElem);
                }
            }

        }
        //lấy đường dẫn ảnh
        $scope.url = function (filename) {
            var file = String(filename);
            if (file.length == 0) {
                return `${host}upload/accounts_img/default.jpg`;
            } else {
                return `${host}upload/accounts_img/${filename}`;
            }
        }
        $("#account-general>.loading-gem").remove()
    }).catch(error => {
        console.log("Error", error);
    });
    $scope.update = function () {
        var item = angular.copy($scope.account);
        $http.post(`${host}update_profile/account`, item).then(resp => {
            $('#message').modal('show');
            $('.avt').attr("src", `${host}upload/accounts_img/${resp.data.photo}`);
            $('#message>.modal-dialog>.modal-content>.modal-body').html("<i class='far fa-check-circle text-success mb-1' style='font-size: 60px;'></i><p>Cập nhật thành công</p>");
        }).catch(error => {
            $('#message').modal('show');
            $('#message>.modal-dialog>.modal-content>.modal-body').html("<i class='far fa-times-circle text-danger mb-1' style='font-size: 60px;'></i>");
            var loi = error.data.message;
            if (error.status == 302) {
                $('#message>.modal-dialog>.modal-content>.modal-body').append("<p>" + loi + "</p>");
            } else {
                $('#message>.modal-dialog>.modal-content>.modal-body').append("<p>Cập nhật thất bại! Do một số lỗi dữ liệu</p>");
            }
        })
        setTimeout(function () {
            $('#message').modal('hide');
        }, 2000);
    }

})
app.controller("updateAddressCtrl", function ($scope, $http, $timeout, $rootScope) {
    function refesh() {
        $http.get(`${host}update_profile/address`).then(resp => {
            $scope.addressList = resp.data;
        }).catch(error => {
            console.log("Error", error);
        });
        $http.get('https://provinces.open-api.vn/api/p/').then(resp => {
            $scope.provinceList = resp.data;
            $timeout(function () {
                $('.selectpicker').selectpicker('refresh');
            }, 1)
        }).catch(error => {
            console.log("Error", error);
        })
        $scope.add = true;
        $scope.update = false;
    }
    refesh();
    $scope.getdistrict = function () {
        if ($scope.selectProvince != undefined) {
            $http.get(`https://provinces.open-api.vn/api/p/${String($scope.selectProvince).replace("number:", "")}?depth=2`).then(resp => {
                $scope.districtList = resp.data.districts;
                $scope.wardList = null;
                $('.selectpicker').selectpicker('refresh');
            }).catch(error => {
                console.log("Error", error);
            });
        } else {
            $scope.districtList = null;
            $scope.wardList = null;
            $timeout(function () {
                $('.selectpicker').selectpicker('refresh');
            }, 1)
        }
    }
    $scope.getward = function (test) {
        if ($scope.selectDistrict != undefined) {
            $http.get(`https://provinces.open-api.vn/api/d/${String($scope.selectDistrict).replace("number:", "")}?depth=2`).then(resp => {
                $scope.wardList = resp.data.wards;
                $('.selectpicker').selectpicker('refresh');
            }).catch(error => {
                console.log("Error", error);
            });
        }

    }
    $scope.refeshList = function () {
        $timeout(function () {
            $('.selectpicker').selectpicker('refresh');
        }, 1)
    }

    $scope.updateModel = function (i) {
        $scope.add = false;
        $scope.update = true;
        $scope.address = {};
        $scope.updateForm = $scope.addressList[i];
        var adr = String($scope.updateForm.address);
        var province = adr.substring(adr.lastIndexOf(","), adr.length);
        adr = adr.substring(0, adr.lastIndexOf(","));
        var district = adr.substring(adr.lastIndexOf(","), adr.length);
        adr = adr.substring(0, adr.lastIndexOf(","));
        var ward = adr.substring(adr.lastIndexOf(","), adr.length);
        adr = adr.substring(0, adr.lastIndexOf(","));
        $scope.address = $scope.updateForm;
        $scope.address.adr = adr.trim();

        $http.get(`https://provinces.open-api.vn/api/p/${String($scope.selectProvince).replace("number:", "")}?depth=2`).then(resp => {
            $scope.districtList = resp.data.districts;
            $scope.wardList = null;
            $('.selectpicker').selectpicker('refresh');
        })
        $timeout(function () {
            $('#province').selectpicker('val', 'number:' + province.replace(", ", ""));
        }, 1);
        $timeout(function () {
            $('#district').selectpicker('val', 'number:' + district.replace(", ", ""));
        }, 200)
        $timeout(function () {
            $('#ward').selectpicker('val', 'number:' + ward.replace(", ", ""));
        }, 1)
        $("#addressModal").modal('show');
    }
    $scope.addModel = function () {
        $scope.add = true;
        $scope.update = false;

    }
    $scope.updateAddress = function () {
        if ($scope.updateForm != undefined || $scope.updateForm != null) {

        }
    }
    $scope.addAddress = function () {
        var loi = 0;
        if ($scope.address.fullname == null) {
            $scope.reqFullname = true;
            loi++;
        }
        if ($scope.address.phone == null) {
            $scope.reqPhone = true;
            loi++;
        }
        if ($scope.selectProvince == undefined) {
            $scope.reqProvince = true;
            loi++;
        }
        if ($scope.selectDistrict == undefined) {
            $scope.reqDistrict = true;
            loi++;
        }
        if ($scope.selectWard == undefined) {
            $scope.reqWard = true;
            loi++;
        }
        if ($scope.address.adr == null) {
            $scope.reqAdr = true;
            loi++;
        }

        if ($scope.af.$valid == true && loi == 0) {
            var item = angular.copy($scope.address);
            item.adr = item.adr.trim() + ", " + $scope.selectWard + ", " + $scope.selectDistrict + ", " + $scope.selectProvince;
            $http.post(`${host}update_profile/address`, item).then(resp => {
                $('#addressModal').modal('hide');
                $scope.address = {};
                $scope.getdistrict();
                refesh();
                $('#message').modal('show');
                $('#message>.modal-dialog>.modal-content>.modal-body').html("<i class='far fa-check-circle text-success mb-1' style='font-size: 60px;'></i><p>Thêm địa chỉ thành công</p>");
            }).catch(error => {
                $('#message').modal('show');
                $('#message>.modal-dialog>.modal-content>.modal-body').html("<i class='far fa-times-circle text-danger mb-1' style='font-size: 60px;'></i>");
                // var loi = error.data.message;
                if (error.status == 302) {
                    $('#message>.modal-dialog>.modal-content>.modal-body').append("<p>" + loi + "</p>");
                } else {
                    $('#message>.modal-dialog>.modal-content>.modal-body').append("<p>Thêm thất bại! Do một số lỗi dữ liệu</p>");
                }
            })
            setTimeout(function () {
                $('#message').modal('hide');
            }, 2000);
        }
    }
    $scope.confirmRemove = function (i) {
        if ($scope.addressList[i] != undefined) {
            $('#addressDel').modal('show');
            $scope.delTarget = $scope.addressList[i];
        } else {
            $('#message').modal('show');
            $('#message>.modal-dialog>.modal-content>.modal-body').html("<i class='far fa-times-circle text-danger mb-1' style='font-size: 60px;'></i><p>Lỗi khi xóa</p>");
            setTimeout(function () {
                $('#message').modal('hide');
            }, 2000);
        }
    }
    $scope.delAddress = function () {
        if ($scope.delTarget != undefined) {
            var id = $scope.delTarget.id;
            $http.delete(`${host}update_profile/deleteAddress/${id}`).then(resp => {
                $('#addressDel').modal('hide');
                refesh();
            }
            ).catch(error => {
                $('#cartDel').modal('hide');
                $('#message').modal('show');
                $('#message>.modal-dialog>.modal-content>.modal-body').html("<i class='far fa-times-circle text-danger mb-1' style='font-size: 60px;'></i><p>Lỗi khi xóa</p>");
                setTimeout(function () {
                    $('#message').modal('hide');
                }, 2000);
            });
        }
    }

})
app.controller("cartCtrl", function ($scope, $http, $rootScope) {
    function update(i, qty) {
        var data = $scope.cartList[i];
        if (data == undefined) {
            $('#message').modal('show');
            $('#message>.modal-dialog>.modal-content>.modal-body').html("<i class='far fa-times-circle text-danger mb-1' style='font-size: 60px;'></i><p>Lỗi bất định của hệ thống</p>");
            setTimeout(function () {
                $('#message').modal('hide');
            }, 2000);
        } else {
            data.qty = qty;
            $http.put(`${hostN}updateCart`, data).then(resp => {
                $scope.cartList[i].total = resp.data.total;
                $scope.totalPrice = 0;
                for (var e = 0; e < $scope.cartList.length; e++) {
                    $scope.totalPrice += $scope.cartList[e].total;
                }
            }
            ).catch(error => {
                console.log(error);
                if (error.status == 400) {
                    $scope.cartList[i].total = error.data.total;
                    $scope.cartList[i].qty = error.data.qty;
                    $('#message').modal('show');
                    $('#message>.modal-dialog>.modal-content>.modal-body').html("<i class='far fa-times-circle text-danger mb-1' style='font-size: 60px;'></i><p>Sản phẩm chỉ có tối đa là " + error.data.qty + " </p><div><button class='btn btn-outline-primary w-100 ' data-dismiss='modal'>ok </button></div>");
                } else {
                    $('#message').modal('show');
                    $('#message>.modal-dialog>.modal-content>.modal-body').html("<i class='far fa-times-circle text-danger mb-1' style='font-size: 60px;'></i><p>Lỗi khi cập nhật</p>");
                    setTimeout(function () {
                        $('#message').modal('hide');
                    }, 2000);
                }
            });
        }

    };
    function refesh() {
        $rootScope.$emit('getToTalCart');
        $http.get(`${hostN}cartList`).then(resp => {
            $scope.cartList = resp.data;
            $scope.totalPrice = 0;
            for (var i = 0; i < $scope.cartList.length; i++) {
                $scope.totalPrice += $scope.cartList[i].total;
            }
            $scope.url = function (filename) {
                var file = String(filename);
                if (file.length == 0) {
                    return `${host}upload/products_single_img/default.jpg`;
                } else {
                    return `${host}upload/product_single_img/${filename}`;
                }
            }
        }).catch(error => {
            console.log("Error", error);
        });
    }
    refesh();
    $scope.quantity = function (event, index) {
        var button = $(event.target);
        var oldValue = button.parent().parent().parent().find('input:text').val();
        var data = $scope.cartList[index];
        if (oldValue == '' && data != undefined) {
            oldValue = data.qty;
        }
        if (button.hasClass('btn-plus') || button.parent().hasClass('btn-plus')) {
            var newVal = Number(oldValue) + 1;
            update(index, newVal);
        } else if (button.hasClass('btn-minus') || button.parent().hasClass('btn-minus')) {
            if (oldValue >= 2) {
                var newVal = Number(oldValue) - 1;
            } else {
                newVal = 1;
            }
            update(index, newVal);
        } else {
            var newVal = oldValue;
        }
    }
    $scope.confirm = function (index) {
        var data = $scope.cartList[index];
        if (data != undefined) {
            $('#cartDel').modal('show');
            $scope.proName = data.name;
            $scope.proId = data.id;
        }

    }
    $scope.delCart = function () {
        var id = $scope.proId;
        $http.delete(`${hostN}deleteCart/${id}`).then(resp => {
            $('#cartDel').modal('hide');
            refesh();
        }
        ).catch(error => {
            $('#cartDel').modal('hide');
            $('#message').modal('show');
            $('#message>.modal-dialog>.modal-content>.modal-body').html("<i class='far fa-times-circle text-danger mb-1' style='font-size: 60px;'></i><p>Lỗi khi xóa</p>");
            setTimeout(function () {
                $('#message').modal('hide');
            }, 2000);
        });

    }
    $scope.changeQty = function (event, index) {
        var qty = $(event.target).val();
        if (qty != '') {
            update(index, qty);
        }
    }
    $scope.formData = {};
    $scope.voucherAct = false;
    $scope.sale = 0;
    $scope.checkVoucher = function () {
        var voucher = $scope.formData.voucher;
        if (voucher == null || voucher == undefined) {
            $(".require-voucher").html("Vui lòng nhập mã giảm giá để áp dụng")
        } else {
            $http.get(`${hostN}voucherAct/${voucher}`).then(resp => {
                if (resp.data.message != null) {
                    $('#message').modal('show');
                    $('#message>.modal-dialog>.modal-content>.modal-body').html(resp.data.message);
                    $scope.sale = 0;
                } else {
                    $scope.voucherAct = true;
                    $scope.sale = resp.data.value;
                }
            }
            ).catch(error => {
                $('#message').modal('show');
                $('#message>.modal-dialog>.modal-content>.modal-body').html("<i class='far fa-times-circle text-danger mb-1' style='font-size: 60px;'></i><p>Lỗi khi truy suất</p>");
                setTimeout(function () {
                    $('#message').modal('hide');
                }, 2000);
            });
        }
    }
})
app.controller("checkoutCtrl", function ($scope, $http, $rootScope) {
    window.myScope = $scope; //this allows console access
    $scope.myArray = [];
    function refesh() {
        $http.get(`${host}update_profile/address`).then(resp => {
            $scope.addressList = resp.data;
        }).catch(error => {
            console.log("Error", error);
        });
        $http.get(`${hostN}cartList`).then(resp => {
            $scope.cartList = resp.data;
            $scope.totalPrice = 0;
            for (var i = 0; i < $scope.cartList.length; i++) {
                $scope.totalPrice += $scope.cartList[i].total;
            }
            $scope.url = function (filename) {
                var file = String(filename);
                if (file.length == 0) {
                    return `${host}upload/products_single_img/default.jpg`;
                } else {
                    return `${host}upload/product_single_img/${filename}`;
                }
            }
        }).catch(error => {
            console.log("Error", error);
        });
    }
    refesh();
    $scope.formData = {};
    $scope.voucherAct = false;
    $scope.sale = 0;
    $scope.checkVoucher = function () {
        var voucher = $scope.formData.voucher;
        if (voucher == null || voucher == undefined || voucher == '') {
            $scope.sale = 0;
            $scope.voucherAct = false;
        } else {
            $http.get(`${hostN}voucherAct/${voucher}`).then(resp => {
                if (resp.data.message != null) {
                    $('#message').modal('show');
                    $('#message>.modal-dialog>.modal-content>.modal-body').html(resp.data.message);
                    $scope.sale = 0;
                } else {
                    $scope.voucherAct = true;
                    $scope.sale = resp.data.value;
                }
            }
            ).catch(error => {
                $('#message').modal('show');
                $('#message>.modal-dialog>.modal-content>.modal-body').html("<i class='far fa-times-circle text-danger mb-1' style='font-size: 60px;'></i><p>Lỗi khi truy suất</p>");
                setTimeout(function () {
                    $('#message').modal('hide');
                }, 2000);
            });
        }
    }
    $scope.createCheckout = function () {
        $scope.checkoutForm = {}
        $scope.checkoutForm.fullname = $scope.addressList[0].fullname;
        $scope.checkoutForm.phone = $scope.addressList[0].phone;
        $scope.checkoutForm.address = $scope.addressList[0].address;
        $scope.checkoutForm.cart = $scope.cartList;
        $scope.checkoutForm.totalPrice = $scope.totalPrice;
        if ($scope.voucherAct == true) {
            $scope.checkoutForm.saleId = $scope.formData.voucher;
        }
        $scope.checkoutForm.shipping = 0;
        $scope.checkoutForm.statusId = 'cxn';

        $http.post(`${hostN}createCheckOut`, $scope.checkoutForm).then(resp => {
            window.location = hostN + resp.data.url;
        }).catch(error => {
            $('#message').modal('show');
            $('#message>.modal-dialog>.modal-content>.modal-body').html("<i class='far fa-times-circle text-danger mb-1' style='font-size: 60px;'></i><p>Đặt hàng thất bại</p>");

        })
    }
})
$(document).ready(function () {
    $("#phone").inputFilter(function (value) {
        return /^\d*$/.test(value);    // Allow digits only, using a RegExp
    });
    $(".number").inputFilter(function (value) {
        return /^\d*$/.test(value);    // Allow digits only, using a RegExp
    });

});

