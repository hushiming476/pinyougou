// 1、创建vue实例
var app = new Vue({
    //模板id
   el:"#app",
    // 数据
   data:{
       // 品牌列表
       entityList:[],
       // 总记录数
       total:0,
       // 页大小
       pageSize:10,
       // 当前页
       pageNum:1,
       // 实体
       entity:{},
       // 选择了的数组id
       ids:[],
       // 查询条件对象
       searchEntity:{},
       //全选的复选框
       checkAll: false
   } ,

    //2、方法
    methods:{
       //2.1 批量删除
        deleteList:function () {
            if (this.ids.length ==0){
                alert("请您选择要删除的记录");
                return;
            }
            //确认是否要删除--confirm 如果点击 确定 则返回true
            if(confirm("您确定要删除吗？")){
                axios.get("../brand/delete.do?ids=" + this.ids).then(function (response) {
                    if(response.data.success){
                        app.searchList(app.pageNum);
                        //清空选择的id
                       app.ids = []
                    }else{
                        alert(response.data.message);
                    }
                });
            }
        },

        //2.2 根据id查询
        findOne:function (id) {
            axios.get("../brand/findOne/" + id + ".do").then(function (response ) {
                app.entity = response.data;
            });
        },

        //2.3 保存/修改数据
        save:function () {
            var method = "add";
            if (this.entity.id !=null){
                method = "update";
            }
            axios.post("../brand/" + method + ".do", this.entity).then(function (response) {
                //操作结果
                if (response.data.success){
                    //保存成功；刷新列表
                    app.searchList(app.pageNum);
                }else{
                    //保存失败，则提示
                    alert(response.data.message);
                }
            });
        },

        //2.4分页查询
        searchList: function (curPage) {
            //设置页号
            this.pageNum =curPage;

            axios.post("../brand/search.do?pageNum=" + this.pageNum+"&pageSize=" + this.pageSize,this.searchEntity).then(function (response) {
                //response.data 分页信息对象PageInfo---记录列表
                app.entityList = response.data.list;
                //符合本次查询的总记录数
                app.total = response.data.total;
            });
        },
        
        //2、5反选
        selectAll:function () {
            if (!this.checkAll){
                this.ids=[];
                //选中
                for (var i = 0; i <this.entityList.length; i++) {
                    var entity =this.entityList[i];
                    this.ids.push(entity.id);
                }
            }else{
                //反选
                this.ids=[];
            }
        }
    },

    //监控数据属性的变化
    watch:{
       ids:{
           //开启深度监控，可以监控里面具体元素的变化
           deep:true,
           //处理方法
           handler:function (newValue,OldValue) {
               console.log(newValue);
               if (this.ids.length !=this.entityList.length){
                   this.checkAll = false;
               }else{
                   this.checkAll = true;
               }
           }
       }
    },

    //2.5钩子函数
    created(){
       this.searchList(this.pageNum);
    }

});