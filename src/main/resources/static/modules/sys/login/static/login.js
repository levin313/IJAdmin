var login = angular.module('login', [])
    .controller('loginCtrl', function ($scope, $rootScope, $http, $interval) {
        $scope.isShow = true;
        $scope.focusEye = function () {
            $scope.eyeClose = true;
        }
        $scope.blurEye = function () {
            $scope.eyeClose = false;
        }
        $scope.mySwiper = new Swiper('.swiper-container', {
            effect: 'flip',
            width: 400,
            height: 450,
            noSwiping: true
        });
        //翻转到找回密码
        $scope.gotoForgetPwd = function () {
            this.mySwiper.slideNext(function () {
            }, 800);
        }
        //翻转到登录页
        $scope.gotoLogin = function () {
            this.mySwiper.slidePrev(function () {
            }, 800);
        }

        //用于判断用户名是否被注册
        $scope.UsernameUsed = false;
        //用于判断密码是否正确
        $scope.UsernameError = false;
        //用于发送验证码后提示手机号是否注册
        $scope.phoneNumberUsed = false;
        //用于判断验证码是否正确的提示
        $scope.vCodeError = false;
        //登录提交
        $scope.submitLoginForm = function () {
            $http({
                url: "/admin/login",
                method: "post",
                params: {
                    userName: $scope.userName,
                    password: $scope.passWord
                },
            }).success(function (data) {
                if (data && data.success) {
                    window.location.href = 'admin/index';
                } else {
                    $scope.UsernameError = true;
                }

            }).error(function (xhr, error) {
                $scope.vCodeBtnkey = false;
                layer.msg("发送失败");
            });
        }
        //发送验证码
        $scope.vCodeBtnWord = "获取验证码";
        $scope.vCodeBtnNum = 60;
        //监听验证码倒计时 若为0 则重新获取 并关闭定时器
        $scope.$watch("vCodeBtnNum", function () {
            if ($scope.vCodeBtnNum == 0) {
                $scope.vCodeBtnNum = 60;
                $scope.vCodeBtnWord = "再次获取验证码";
                $scope.vCodeBtnkey = false;
                $interval.cancel($scope.vCodeTM);
            }
        })
        $scope.vCodeBtnkey = false;
        $scope.sendVcode = function () {
            $http({
                url: "/comm/sms",
                method: "post",
                params: {
                    flag: 5,
                    mobilePhone: $scope.phoneNumber
                },
            }).success(function (data) {
                //alert(data);
                $scope.vCodeBtnkey = true;
                $scope.vCodeTM = $interval(function () {
                    $scope.vCodeBtnNum--;
                    $scope.vCodeBtnWord = $scope.vCodeBtnNum + "秒后再获取";
                }, 1000);

            }).error(function (xhr, error) {
                $scope.vCodeBtnkey = false;
                layer.msg("发送失败");
            });
        }
        //找回密码提交
        $scope.submitFindPwd = function () {
            $http({
                url: "/admin/reset",
                method: "post",
                params: {
                    password: $scope.newPwd,
                    phone: $scope.phoneNumber,
                    vcode: $scope.vCode
                },
            }).success(function (data) {
                layer.open({
                    title: '找回密码',
                    closeBtn: "2",
                    content: '密码找回成功',
                    yes: function () {
                        localStorage.clear();
                        window.location.href = 'admin/login';
                    }
                });
            }).error(function (xhr, error) {
                layer.msg("提交失败");
            });
        }
    }).directive("checkpwd", function () { //校验密码规则自定义
        return {
            restrict: "A",
            require: 'ngModel',
            link: function ($scope, $element, $attr, ngModel) {
                $scope.pwdRegExp = new RegExp("^[a-zA-Z0-9~!@#$%^&*()_+\-={}:;<>?,.\/]*$");
                $scope.$watch("passWord", function () {
                    ngModel.$setValidity("password", $scope.pwdRegExp.test($scope.passWord));
                    ngModel.$setValidity("usernameerror", true); //密码一旦有修改，则把错误信息去掉
                });
                //若账号或密码输入有误，则改变UsernameError值，来显示错误信息
                $scope.$watch("UsernameError", function () {
                    if ($scope.UsernameError) {
                        $scope.UsernameError = false;
                        ngModel.$setValidity("usernameerror", false);
                    }
                });
                //监听账号输入框，一旦有变动，则把提示密码错误的信息去除
                $scope.$watch("userName", function () {
                    ngModel.$setValidity("usernameerror", true);
                });
            }
        }
    })
    .directive("checkvcode", function () { //校验密码规则自定义
        return {
            restrict: "A",
            require: 'ngModel',
            link: function ($scope, $element, $attr, ngModel) {
                $scope.vCodeRegExp = new RegExp("^[0-9]*$");
                $scope.$watch("vCode", function () {
                    var value = ngModel.$modelValue || ngModel.$viewValue;
                    ngModel.$setValidity("vcode", $scope.vCodeRegExp.test(value));
                    ngModel.$setValidity("vcodeerror", true);
                });
                //远程校验
                $scope.$watch("vCodeError", function () {
                    if ($scope.vCodeError) {
                        $scope.vCodeError = false;
                        ngModel.$setValidity("vcodeerror", false);
                    }

                });

            }
        }
    })
    .directive("checkphonenumber", function () { //校验手机号规则自定义
        return {
            restrict: "A",
            require: 'ngModel',
            link: function ($scope, $element, $attr, ngModel) {
                $scope.phoneNumberRegExp = new RegExp("^1[3|4|5|7|8][0-9]{9}$");
                $scope.$watch("phoneNumber", function () {
                    ngModel.$setValidity("phonenumber", $scope.phoneNumberRegExp.test($scope.phoneNumber));
                    ngModel.$setValidity("phonenumberused", true); //若发送验证码后提示手机号未被注册，则一经变动就隐藏提示
                });
                $scope.$watch("phoneNumberUsed", function () {
                    if ($scope.phoneNumberUsed) {
                        $scope.phoneNumberUsed = false;
                        ngModel.$setValidity("phonenumberused", false);
                    }
                });

            }
        }
    })
    .directive("checkusername", function () { //校验用户名是否注册
        return {
            restrict: "A",
            require: 'ngModel',
            link: function ($scope, $element, $attr, ngModel) {
                $scope.$watch("UsernameUsed", function () {
                    if ($scope.UsernameUsed) {
                        $scope.UsernameUsed = false;
                        ngModel.$setValidity("usernameused", false);
                    }
                });
                $scope.$watch("userName", function () {
                    ngModel.$setValidity("usernameused", true);
                });
            }
        }
    });