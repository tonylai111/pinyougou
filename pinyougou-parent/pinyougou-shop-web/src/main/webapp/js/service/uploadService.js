app.service('uploadService',function ($http) {
    this.uploadFile=function(){

        //创建一个表单对象 h5 中的FormData 就是表单对象
        var formData=new FormData();
        //向表单中添加文件对象（图片）
        //  file.files[0]  --->file 指的是标签中id为file   files  是h5内部所定义的文件数组
        formData.append("file",file.files[0]);//<input type="file" name="file" >
        //formData.append("username","zhangsan");//表单中<input type="text" name="username" value="zhangsan">

        return $http({
            method:'POST',
            url:"/upload.do",
            data: formData,
            headers: {'Content-Type':undefined},//就是定义传递的表单的类型：multi-part/form-data
            transformRequest: angular.identity//使用anguarljs的方式来序列化(io流)对象 传递给服务器。
        });
    }
})