var app = new Vue({
   el:"#app",
   data:{
       //列表数据
       entityList:[],
       //总记录数
       total:0,
       //页大小
       pageSize:10,
       //当前页
       pageNum:1,
       //实体
       entity:{specification:{},specificationOptionList:[]},
       //选择的id数组
       ids:[],
       //定一个空的搜索条件对象
       searchEntity:{}
   } ,

    //1、删除规格选项
    method:{
       deleteTableRow:function (index) {
           // 删除实体--规格选项列表中的索引：splice:1
           this.entity.specificationOptionList.splice(index,1);
       },
        //2、添加规格选项
        addTableRow:function (index) {
            //添加实体--规格选项列表中的索引：push:1
            this.entity.specificationOptionList.push(index,1);
        },
        
        //3、 分页查询
        searchList:function (curPage) {
            this.pageSize = curPage;
                                                     //  1 2 3            共6条；                                   跳转至1
            axios.post("../specification/search.do?pageNum=" + this.pageNum+"&pageSize=" + this.pageNum,this.searchEntity).then(function (response) {
                app.entityList = response.data.list;
                app.total = response.data.total;
            });
        },
        
        //4、保存/修改数据---如果id为空则添加，不为空就修改
        save:function () {
            var method = "add";
            if (this.entity.specification.id!=null){
                method = "update";
            }
            axios.post("../specification/" + method + ".do",this.entity).then(function (response) {
                if (response.data().success){
                    app.searchList(app.pageNum);
                }else{
                    alert(response.data.message);
                }
            });
        },

        //5、根据主键查询
        findOne:function (id) {
            axios.get("/specification/" + "findOne" + id +".do").then(function (response) {
                app.entity = response.data;
            });
        },

        //6、删除；方法名不能直接使用vue关键字delete
        deleteList:function () {
            if (this.ids.length<1){
                alert("请选择要删除的记录！");
                return;
            }
            if (confirm("确定要删除这条记录吗？")){
                axios.get("specification/delete.do?ids="+ this.ids).then(function (response) {
                   if(response.data.success){
                       app.searchList(app.pageNum);
                       app.ids = [];
                   }else{
                       alert(response.data.message);
                   }
                });
            }
        }
    },

    //7、钩子函数
    created:function () {
        this.searchList(this.pageNum);
    }
});