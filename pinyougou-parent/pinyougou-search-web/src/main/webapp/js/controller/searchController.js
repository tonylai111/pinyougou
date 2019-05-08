app.controller('searchController',function ($scope,$location,searchService) {
    //写一个方法 当点击搜索的按钮的时候去调用

    $scope.searchMap={'keywords':'','category':'','brand':'','spec':{},'price':'','pageNo':1,'pageSize':40,'sortField':'','sortType':'' };

    $scope.search=function () {
        searchService.search($scope.searchMap).success(
            function (response) {//Map
                $scope.resultMap=response;

                $scope.buildPageLable();
            }
        );
    }

    $scope.doSortBy=function (sortField,sortType) {
        //
        $scope.searchMap.sortField=sortField;

        $scope.searchMap.sortType=sortType;

        $scope.search();

    }

    /**
     * 构建分页标签
     */
    $scope.buildPageLable=function () {
        $scope.pageLable=[];

        var first=1;
        var last = $scope.resultMap.totalPages;

        $scope.prevdian=false;
        $scope.nextdian=false;

        if($scope.resultMap.totalPages>5){
            if($scope.searchMap.pageNo<=3){
                //显示前5页
                first=1;
                last=5;
                $scope.prevdian=false;
                $scope.nextdian=true;
            }else if($scope.searchMap.pageNo>=$scope.resultMap.totalPages-2){
                // 显示后5页
                first=$scope.resultMap.totalPages-4;
                 last=$scope.resultMap.totalPages;

                $scope.prevdian=true;
                $scope.nextdian=false;


            }else{
                first=$scope.searchMap.pageNo-2;
                last=$scope.searchMap.pageNo+2;

                $scope.prevdian=true;
                $scope.nextdian=true;
            }

        }else{
            $scope.prevdian=false;
            $scope.nextdian=false;
        }

        for(var i=first;i<=last;i++){
            $scope.pageLable.push(i);
        }

    }
    
    $scope.searchByPage=function (page) {

        //判断是否是数字
        console.log(page);
        if(isNaN(page)==true){
            console.log("不是数字");
            page=1;
        }
        //获取值 转换成数字
        var intPage = parseInt(page);
        //如果 值 小于1  显示第一页   如果 值 大于总页数 显示最后一页
        if(intPage<1){
            intPage=1;
        }
        if(intPage>  $scope.resultMap.totalPages){
            intPage= $scope.resultMap.totalPages;
        }
        //1.影响变量pageno的值 点击的页码的值
        $scope.searchMap.pageNo=intPage;
        //2.发送请求查询
        $scope.search();
    }
    
    //写一个方法 当点击 分类或者其他的搜索选项按钮的时候调用 影响变量searchMap的值
    $scope.addSearchItem=function (key,value) {
        if(key=='category' || key=='brand' || key=='price'){
            $scope.searchMap[key]=value;
        }else{
            $scope.searchMap.spec[key]=value;
        }
        $scope.search();

    }

    //页面加载  获取URL中的参数值  发送请求获取数据展示到搜索的结果页面中
    $scope.searchByKeywords=function () {
        //1.从url中获取参数的值
        var keywords = $location.search()['keywords'];
        $scope.searchMap.keywords=keywords;
        //2.发送请求获取数据
        $scope.search();
    }
    
    $scope.clear=function () {
        $scope.searchMap={'keywords':$scope.searchMap.keywords,'category':'','brand':'','spec':{},'price':'','pageNo':1,'pageSize':40,'sortField':'','sortType':'' };
    }


    
    $scope.removeSearchItem=function (key) {
        if(key=='category' || key=='brand' || key=='price'){
            $scope.searchMap[key]='';
        }else{
            delete $scope.searchMap.spec[key];
        }
        $scope.search();
    }

    //方法用于判断 搜索的关键字  是否包含在品牌列表中  true false
    $scope.isKeywordsBrand=function () {
        for(var i=0;i<$scope.resultMap.brandList.length;i++){
            //[品牌名称]
            if($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)!=-1){
                $scope.searchMap.brand=$scope.resultMap.brandList[i].text;
                return true;
            }
        }
        return false;
    }


})