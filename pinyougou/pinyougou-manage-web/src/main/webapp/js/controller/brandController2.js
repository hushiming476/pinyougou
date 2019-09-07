    // I、创建vue实例
    var app = new Vue({
    //II、模板id
    el: "#app",
    //III、数据
    data:{
       // 1、品牌列表
     entityList:[],
       // 2、总记录数
     total:0,
       // 3、页大小
    pageSize:10,
       // 4、当前页
      pageNum:1,
       // 5、实体
     entity:{},
       // 6、选择了的数组id
     ids:[],
       // 7、查询条件对象
     searchEntity:{},
        //全选的复选框
        checkAll: false
},

    //2、方法
    methods:{
       //2.1 批量删除
     deleteList:function () {
         if (this.ids.length==0){
             alert("请您选择要删除的记录")
             return;
         }
         if (confirm("您确定要删除这条记录吗")){
             axios.get("../brand/delete.do?ids=" + this.ids).then(function (response) {
                 if (response.data.success){
                     app.searchList(app.pageNum);
                     app.ids=[]
                 }else{
                   alert(response.data.message);
                 }
             });
         }
     },

        //2.2根据id查询
        findOne:function (id) {
         axios.get("../brand/findOne/" + id +".do").then(function (response) {
             app.entity = response.data;
         });
        },

        //2.3 保存/修改数据
        save:function () {
          var method = "add";
             if (this.entity.id !=null){
                  method = "update";
         }
             axios.post("../brand/" + method + ".do" , this.entity).then(function (response) {
               if (response.data.success){
                   app.searchList(app.pageNum);
               } else{
                   alert(response.data.message);
             }
       });
    },

        //2.4 分页查询
        searchList:function (curPage) {
            this.pageNum = curPage;
            this.ids=[];

            axios.post("../brand/search.do?pageNum=" + this.pageNum+"&pageSize=" + this.pageSize,this.searchEntity ).then(function (response) {
                app.entityList = response.data.list;
                app.total = response.data.total;
            });
        },
        
        //2、5 全选
        selectAll:function () {
            if (!this.checkAll){
                this.ids=[];
                //选中
                for (let i = 0; i < this.entityList.length; i++) {
                    var entity = this.entityList[i];
                    this.ids.push(entity.id)
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
                //开启深度监控；可以监控里面具体元素的改变
                deep: true,
                //处理方法
                handler: function (newValue, oldValue) {
                    console.log(newValue);
                    if (this.ids.length != this.entityList.length) {
                        this.checkAll = false;
                    } else {
                        this.checkAll = true;
                    }
                }
            }
        },

        //2.5钩子函数
           created(){
           this.searchList(this.pageNum)

      }
 });