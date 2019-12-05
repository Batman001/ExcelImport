<style scoped>
  .ivu-card-head p{height:35px;}
  .listDetail{padding: 10px;border: 1px solid #DDDEE1;border-bottom:none;}
  .handStyle{width: 100%;text-align: center;line-height: 44px;}
  .demo-spin-icon-load{
    animation: ani-demo-spin 1s linear infinite;
  }
  @keyframes ani-demo-spin {
    from { transform: rotate(0deg);}
    50%  { transform: rotate(180deg);}
    to   { transform: rotate(360deg);}
  }
  .orgTitltStyle{width:100%;height:50px;padding:10px 18px 10px 10px;background:#fff; top:10px;}
</style>
<template>
    <div style="padding:10px;margin-bottom:10px;">
      <Spin fix v-show="markShow">
        <Icon type="load-c" size=18 class="demo-spin-icon-load" style="color:#000;"></Icon>
        <div style="font-size: 18px;">Loading</div>
      </Spin>
      <Row class="orgTitltStyle">
        <Col span="19">
          <Button type="primary"  @click="creatdict"><Icon type="ios-plus-outline" style="font-size:16px;color:#fff;display:inline;cursor: pointer"></Icon>&ensp;新增</Button>
        </Col>
        <Col span="5"  style="text-align: right">
          <Input placeholder="请输入描述"  style="width: 185px;" v-model="description" ></Input> <Button type="success" @click="searchFun" style="margin-left:10px;">查询</Button>
        </Col>
      </Row>
      <componentPage :title="textTitle" :page="page" :total="total" :sumPage="sumPage" v-on:pageChange="dictChange" style="margin:20px 0 10px;"></componentPage>
      <div :style="scrollTableStyle" style="text-align: center" >
           <Table size="small" :height="tableHeight" :columns="columns7" :data="putPlandata" :loading="loading" ></Table>
      </div>
      <Modal v-model="delModal" title="系统提示" @on-ok="deleteOK" width="252px">
        <p>确定要删除么？</p>
      </Modal>
      <Modal v-model="modalAdd" :title="title" width="400px" @on-ok="checkSure('formValidate')" :id="modelId" :loading="loading" >
          <Form ref="formValidate" :model="modelformValidate"  :label-width="90">
            <#list table.columnList as c>
			<#if c.isEdit?? && c.isEdit == "1" && (c.isNotBaseField )>
                <FormItem label="${c.comments}：" prop="loginName">
                  <Input placeholder="请输入" clearable v-model="modelformValidate.${c.javaFieldId}"></Input>
                </FormItem>
       </#if>
		    </#list>

                <FormItem label="备注：">
                  <Input v-model="modelformValidate.remark"></Input>
                </FormItem>
          </Form>
      </Modal>
    </div>
</template>

<script>
import componentPage from '../my-components/newPage.vue';
import {initDictionary, queryDictionary, deleteDictionary, saveDictionary} from '@/api/baseInfo'

export default {
  name: 'container',
  components:{
    componentPage:componentPage
  },
  data () {
    return {
      columns7: [
        {type: 'index2',title:'序号',width: 100,align: 'center',
          render: (h, params) => {
            return h('span', params.index+(this.page-1)*this.pageSize+1);
          }
        },

        {
          title: '操作',
          key: 'action',
          align: 'center',
          render: (h, params) => {
            return h('div', [
              h('Button', {
                props: {type: 'primary', size: 'small',icon: 'ios-compose-outline'},
                style: {marginRight: '5px'},
                on: {
                  click: () => {
                    this.editData(params.row);
                  }
                }
              }, '修改'),
              h('Button', {
                props: {type: 'error', size: 'small',icon: 'ios-trash-outline'},
                on: {
                  click: () => {
                    this.removeData(params.row)
                  }
                }
              }, '删除')
            ]);
          }
        },
        <#list table.columnList as c>
			<#if c.isEdit?? && c.isEdit == "1" && (c.isNotBaseField )>
        {
          title: '${c.comments}',
          align: 'center',
          key: '${c.javaFieldId}'
        },
      </#if>
		    </#list>

        {
          title: '备注',
          align: 'center',
          key: 'remark'
        }
      ],
      textTitle:'字典管理表单',
      putPlandata: [],//表格数据
      loading:true,
      page:1,//当前页
      listCount:[''],//新增内容
      pageSize:20,
      title:"系统提示",
      deleteId:'',//删除id
      editId:'',//编辑id
      editIndex:0,//编辑时的索引
      submitNumber:'',//表单编号
      sumitState:'',//表单状态
      beginData:'',//开始时间
      endData:'',//结束时间
      deleteSureModel:false,
      deletIndex:0,//删除时的索引
      delModal:false,
      modalAdd:false,
      scrollTableStyle:{},//表格高度
      modelId:'',//模态框id
      value:'',//键值
      description:'',//描述
      markShow:false,//是否显示加载状态
      modelformValidate: {
        name:'', 
        remark:''
      },
      ruleValidate: {
        
        

      },
      tableHeight:0,
      total:0,
      sumPage:0,

    }
  },
  mounted:function(){
    let window_width=$(window).height()-190;
    this.scrollTableStyle = {height:window_width+'px'};
    this.tableHeight = window_width;
    this.initialData();

  },
  computed: {},
  methods:{
    //字典初始化
    initialData:function(){
      let that = this;
       this.$post('${r"${ctx}"}/${urlPrefix}/asyInitial',{"page":{"pageNo":that.page,"pageSize":that.pageSize}})
      //initDictionary({"page":{"pageNo":that.page,"pageSize":that.pageSize}})
        .then((res)=>{
          this.formStates = res.searchContent;
          this.putPlandata = res.result;
          this.total = res.totalNum;
          if(this.total>0){
            this.sumPage = Math.ceil(this.total/this.pageSize);
          }
          this.loading = false;
          this.markShow = false;
        }).catch(function(error){
        that.markShow = false;
      });
    },
    dictChange:function(page){
        this.loading = true;
        this.page = page;
          this.initialData();
    },
    //查询功能
    searchFun:function(){
      let that = this,data = {};
      data = {"page":{"pageNo":that.page,"pageSize":that.pageSize},"description":this.description}
      this.$post('${r"${ctx}"}/${urlPrefix}/asyQuery',data)
      //queryDictionary(data)
        .then((res) => {
          this.putPlandata = res.result;
        }).catch(function(){
        that.$Message.error('查询失败');
      });
    },
    //删除功能
    removeData:function(pas){
      this.delModal = true;
      this.deleteId = pas.id;
      this.deletIndex = pas._index;
    },
//删除确定
    deleteOK:function(){
       this.$post('${r"${ctx}"}/${urlPrefix}/asyDelete',{id:this.deleteId})
      //deleteDictionary({id:this.deleteId})
        .then((res)=>{
          if(res.state==0){
            this.$Message.success(res.msg);
            this.putPlandata.splice(this.deletIndex,1);
          }else{
            this.$Message.error('数据删除失败');
          }

        }).catch((error)=>{
          this.$Message.error("数据连接失败！")
      })
    },
    //编辑功能
    editData:function(params){
      this.$refs.formValidate.resetFields();
      this.title = '编辑';
      
      this.modelId = params.id;
      <#list table.columnList as c>
			<#if c.isEdit?? && c.isEdit == "1" && (c.isNotBaseField )>
      this.modelformValidate.${c.javaFieldId} = params.${c.javaFieldId};
      </#if>
		  </#list>

      this.editIndex = params._index;
      this.modalAdd = true;
      this.initialData();
    },
//新增容器
    creatdict:function(){
      //点击新建按钮
      this.title = "新建字典"
      this.modalAdd = true;
      this.$refs.formValidate.resetFields();
      /* this.modelformValidate.value = '';
       this.modelformValidate.label = '';
       this.modelformValidate.type = '';
       this.modelformValidate.description = '';
       this.modelformValidate.sort = 0;
       this.modelformValidate.remark = '';*/
      this.modelId = '';
    },
    checkSure:function(name){
      //保存数据
      var modelId = this.modelId, that = this;
      setTimeout(() => {
        that.$refs[name].validate((valid) => {
          if (valid) {
            var date = {'id':modelId,
            <#list table.columnList as c>
			<#if c.isEdit?? && c.isEdit == "1" && (c.isNotBaseField )>
              '${c.javaFieldId}':this.modelformValidate.${c.javaFieldId} ,
      </#if>
		  </#list>
              'remark':this.modelformValidate.remark};
             this.$post('${r"${ctx}"}/${urlPrefix}/asySave',date)
            //saveDictionary(data)
              .then((res) => {
                if(res.state==0){
                    that.$Message.success(res.msg);
                    that.initialData();
                }else{
                    that.$Message.error(res.msg);
                }
                let req=JSON.parse(res);
                if(modelId){
                  that.putPlandata.splice(that.editIndex,1,req);
                }else{
                  that.putPlandata.push(req);
                }

              })

          } else {
            this.$Message.error('请填写必填项');
            this.modalAdd = true;
          }
        })

      },1);
       this.$post('${r"${ctx}"}/${urlPrefix}/asyInitial',{"page":{"pageNo":this.page,"pageSize":this.pageSize}})
      //initDictionary({"page":{"pageNo":this.page,"pageSize":this.pageSize}})
        .then((res) => {
          this.formStates = res.searchContent;
          this.putPlandata = res.result;
        }).catch(function(error){
        that.$Message.error('数据请求失败！')
      })
      this.modelId = '';
    }

  }
}
</script>
