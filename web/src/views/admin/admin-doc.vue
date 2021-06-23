<template>

  <a-layout>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >

      <a-row :gutter="24">
        <a-col :span="8">
          <p>
            <a-form
                layout="inline"
                :model="param"
                @finish="handleFinish"
                @finishFailed="handleFinishFailed"
            >

              <a-form-item>
                <a-button type="primary" @click="handleQuery()">
                  查询
                </a-button>
              </a-form-item>
              <a-form-item>
                <a-button type="primary" @click="add()" >
                  新增
                </a-button>
              </a-form-item>
            </a-form>

          </p>
          <a-table
              v-if="level1.length>0"
              :columns="columns"
              :row-key="record => record.id"
              :data-source="level1"
              :pagination="false"
              :loading="loading"
              size="small"
              :defaultExpandAllRows="true"
          >
            <template #name="{ text, record}">
              {{record.sort}} {{text}}
            </template>

            <template v-slot:action="{ text, record }">
              <a-space size="small">
                <a-button type="primary" @click="edit(record)" size="small">
                  编辑
                </a-button>
                <a-popconfirm
                    title="确认删除?"
                    ok-text="是"
                    cancel-text="否"
                    @confirm="handleDelete(record.id)"
                >
                  <a-button type="danger" size="small">
                    删除
                  </a-button>
                </a-popconfirm>
              </a-space>
            </template>
          </a-table>
        </a-col>
        <a-col :span="16">
          <p>
            <a-form
                layout="inline"
                :model="param"
            >
              <a-form-item>
                <a-button type="primary" @click="handleSave()">
                  保存
                </a-button>
              </a-form-item>
            </a-form>
          </p>
          <a-form :model="doc" layout="vertical">
            <a-form-item>
              <a-input v-model:value="doc.name" placeholder="名称"/>
            </a-form-item>
            <a-form-item>
              <a-tree-select
                  v-model:value="doc.parent"
                  style="width: 100%"
                  :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
                  :tree-data="treeSelectData"
                  placeholder="请选择父文档"
                  tree-default-expand-all
                  :replaceFields="{title: 'name', key: 'id', value: 'id'}"
              >
              </a-tree-select>
            </a-form-item>

            <a-form-item>
              <a-input v-model:value="doc.sort" placeholder="顺序"/>
            </a-form-item>
            <a-form-item>
              <div id="content"></div>
            </a-form-item>
          </a-form>
        </a-col>
      </a-row>

    </a-layout-content>
<!--    <a-modal-->
<!--        v-model:visible="modalVisible"-->
<!--        :confirm-loading="modalLoading"-->
<!--        title="文档表单"-->
<!--        @ok="handleModalOk">-->
<!--      -->
<!--    </a-modal>-->
  </a-layout>


</template>

<script lang="ts">
import {createVNode, defineComponent, onMounted, ref } from 'vue';
import {message, Modal} from 'ant-design-vue';
import axios from 'axios';
import {Tool} from "@/util/tool";
import {useRoute} from "vue-router";
import {ExclamationCircleOutlined} from "@ant-design/icons-vue";
import E from "wangeditor";


export default defineComponent({
  name: 'AdminDoc',
  setup() {
    const editor = new E("#content");
    editor.config.zIndex = 0;

    //获取当前路由
    const route = useRoute();
    console.log("路由：", route);
    console.log("route.path：", route.path);
    console.log("route.query：", route.query);
    console.log("route.param：", route.params);
    console.log("route.fullPath：", route.fullPath);
    console.log("route.name：", route.name);
    //meta是自定义变量
    console.log("route.meta：", route.meta);

    console.log()
    const param = ref();
    param.value = {};
    const docs = ref();
    const loading = ref(false);

    const columns = [{
      title: '名称',
      dataIndex: 'name',
      slots: {customRender: 'name'}
    },
    {
      title: 'Action',
      key:'action',
      slots:{customRender: 'action'}
    }
    ];

    /**
     * 一级文档树，children属性就是二级文档
     * [{
     *   id:"",
     *   name: "",
     *   children: [{
     *     id: "",
     *     name: "",
     *   }]
     * }]
     **/
    const level1 = ref();
    level1.value = [];


    /**
     * 数据查询 handleQuery相当于一个对象实例
     **/
    const handleQuery = () => {
      loading.value = true;
      //缓存数据滞空
      level1.value = [];
      axios.get("/doc/all").then((response) => {
        loading.value = false;
        const data = response.data;

        if(data.success){
          docs.value = data.content;
          console.log("原始数组", docs.value);

          level1.value = [];
          level1.value = Tool.array2Tree(docs.value, 0);
          console.log("树形结构", level1);

        } else{
          message.error(data.message);
        }
      });
    };




    // ------表单-------
    //因为树选择组件的属性状态，会随当前编辑的节点而变化，所以单独声明一个响应式变量
    const treeSelectData = ref();
    treeSelectData.value = [];
    const doc = ref();
    doc.value = {};
    let isAdd = false;
    const modalVisible = ref(false);
    const modalLoading = ref(false);
    const handleSave = ()=>{
      modalLoading.value = true;
      doc.value.content = editor.txt.html();

      axios.post("/doc/save", doc.value).then((response) => {
        modalLoading.value = false;
        const data = response.data; //data = commonResp
        if(data.success){
          modalVisible.value = false;
          if(isAdd===true){
            message.success('新增成功');
          } else{
            message.success('编辑成功');
          }

          //重新加载列表
          handleQuery();
        } else{
          message.error(data.message);
        }
      });
    };

    /**
     * 递归将某节点及其子孙节点全部置为disabled
     * */
    const setDisable = (treeSelectData: any, id: any) => {
      //console.log("treeSelecData结构：",treeSelectData, id);
      for(let i = 0; i<treeSelectData.length; i++){
        const node = treeSelectData[i];
        if(node.id === id){
          //如果当前节点就是目标节点
          console.log("disabled", node);
          //将目标节点设置为disabled
          node.disabled = true;

          //遍历所有子节点，将所有子节点全部加上disabled
          const children = node.children;
          if(Tool.isNotEmpty(children)){
            for(let j=0; j<children.length; j++){
              setDisable(children, children[j].id);
            }
          }
        }
        else{
          //如果当前节点不是目标节点，则到其子节点再找
          const children = node.children;
          if(Tool.isNotEmpty(children)){
            setDisable(children, id);
          }
        }
      }
    };

    const deleteIds: Array<string> = [];
    const deleteNames: Array<string> = [];
    /**
     * 递归将某节点及其子孙节点全部存进ids
     * */
    const getDeleteIds = (treeSelectData: any, id: any) => {
      //console.log("treeSelecData结构：",treeSelectData, id);
      for(let i = 0; i<treeSelectData.length; i++){
        const node = treeSelectData[i];
        if(node.id === id){
          //如果当前节点就是目标节点
          console.log("disabled", node);
          //将目标节点放入ids
          deleteIds.push(id);
          deleteNames.push(node.name)

          //遍历所有子节点，将所有子节点全部放入ids
          const children = node.children;
          if(Tool.isNotEmpty(children)){
            for(let j=0; j<children.length; j++){
              getDeleteIds(children, children[j].id);
            }
          }
        }
        else{
          //如果当前节点不是目标节点，则到其子节点再找
          const children = node.children;
          if(Tool.isNotEmpty(children)){
            getDeleteIds(children, id);
          }
        }
      }
    };

    /**
     * 编辑
     * @param record
     */
    const edit = (record: any)=>{

      modalVisible.value = true;
      doc.value = Tool.copy(record);
      handleQueryContent();

      //不能选择当前节点及其所有子孙节点作为父节点，否则树会断开
      treeSelectData.value = Tool.copy(level1.value);
      setDisable(treeSelectData.value, record.id);

      //为选择树添加一个”无“,unshift方法是往数组头部插入一个元素
      treeSelectData.value.unshift({id: 0, name: '无'});
      isAdd = false;
    };

    /**
     * 内容查询 handleQuery相当于一个对象实例
     **/
    const handleQueryContent = () => {
      axios.get("/doc/find-content/"+doc.value.id).then((response) => {
        const data = response.data;

        if(data.success){
          editor.txt.html(data.content);
        } else{
          message.error(data.message);
        }
      });
    };


    /**
     * 新增
     * @param record
     */
    const add = ()=>{
      modalVisible.value = true;
      doc.value = {
        ebookId: route.query.ebookId
      };
      treeSelectData.value = Tool.copy(level1.value);
      //为选择树添加一个”无“,unshift方法是往数组头部插入一个元素
      treeSelectData.value.unshift({id: 0, name: '无'});
      isAdd = true;
    };

    const handleDelete = (id: number)=>{
      // 清空数组，否则多次删除时，数组会一直增加
      deleteIds.length = 0;
      deleteNames.length = 0;
      getDeleteIds(level1.value, id);
      Modal.confirm({
        title: '确定删除文档?',
        icon: createVNode(ExclamationCircleOutlined),
        content: createVNode('div', { style: 'color:red;' }, '这会将'+deleteNames+'全部删除'),
        onOk() {
          // console.log('OK');
          axios.delete("/doc/delete/" + deleteIds.join(",")).then((response) => {

            const data = response.data; //data = commonResp
            if(data.success){
              message.success('删除成功');
              //重新加载列表
              handleQuery();
            }
          });
        },
        onCancel() {
          console.log('Cancel');
        },
        class: 'test',
      });

    };


    onMounted(() => {
      handleQuery();
      editor.create();
    });

    return {
      param,
      // docs,
      level1,
      columns,
      loading,
      handleQuery,

      edit,
      add,

      doc,
      modalVisible,
      modalLoading,
      handleSave,

      handleDelete,

      treeSelectData
    }
  }
});
</script>

<style scoped>
img {
  width: 50px;
  height: 50px;
}
</style>

