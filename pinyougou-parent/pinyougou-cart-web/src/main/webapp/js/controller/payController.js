app.controller('payController',function ($scope,payService) {
    //写一个方法 当页面加载的时候调用 ：发送请求获取二维码的连接信息 调用Qrious生成二维码

    $scope.createNative=function () {
        payService.createNative().success(
            function (response) {//Map

                $scope.total_fee=response.total_fee/100;//单位是元
                $scope.out_trade_no=response.out_trade_no;

                //生成二维码
                var qrious = new QRious({
                    element: document.getElementById("qrious"),
                    size:250,
                    level:'H',
                    value:response.code_url
                });

                //调用方法  查询该支付订单的状态
                $scope.queryStatus($scope.out_trade_no);

            }
        )
    }

    $scope.queryStatus=function (out_trade_no) {
        payService.queryStatus(out_trade_no).success(
            function (response) {
                if(response.success){
                    //支付成功
                    window.location.href="paysuccess.html#?money="+$scope.total_fee;
                }else{
                    //1.支付超时 重新生成新的二维码
                    if(response.message=='超时'){
                        $scope.createNative();
                    }else{
                        //2.支付失败
                        window.location.href="payfail.html";
                    }


                }
            }
        )
    }
})