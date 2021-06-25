<template>

  <a-layout>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >

      <p>
        <a-form
            layout="inline"
            :model="param"
        >
          <a-form-item>
            <a-input v-model:value="param.loginName" placeholder="登陆名">
              <template #prefix><UserOutlined style="color: rgba(0, 0, 0, 0.25)" /></template>
            </a-input>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="handleQuery({page: 1, size: pagination.pageSize})">
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
          :columns="columns"
          :row-key="record => record.id"
          :data-source="users"
          :pagination="pagination"
          :loading="loading"
          @change="handleTableChange">
        <template #cover="{ text: cover }">
        </template>

        <template v-slot:category="{text, record}">
          <span>{{getCategoryName(record.category1Id)}}/{{getCategoryName(record.category2Id)}}</span>
        </template>

        <template v-slot:action="{ text, record }">
          <a-space size="small">
            <a-button type="primary" @click="edit(record)">
              编辑
            </a-button>
            <a-popconfirm
                title="确认删除?"
                ok-text="是"
                cancel-text="否"
                @confirm="handleDelete(record.id)"
            >
              <a-button type="danger">
                删除
              </a-button>
            </a-popconfirm>

          </a-space>
        </template>
      </a-table>
    </a-layout-content>

    <a-modal
        v-model:visible="modalVisible"
        :confirm-loading="modalLoading"
        title="用户表单"
        @ok="handleModalOk">
      <a-form :model="user" :label-col="{span:6}" >
        <a-form-item label="登录名">
          <a-input v-model:value="user.loginName" :disabled="!!user.id" />
        </a-form-item>
        <a-form-item label="昵称">
          <a-input v-model:value="user.name" />
        </a-form-item>
        <a-form-item label="密码" v-show="!user.id">
          <a-input v-model:value="user.password" />
        </a-form-item>
      </a-form>
    </a-modal>
  </a-layout>


</template>

<script lang="ts">
import { defineComponent, onMounted, ref } from 'vue';
import {message} from 'ant-design-vue';
import axios from 'axios';
import {Tool} from "@/util/tool";

declare let hexMd5: any;
declare let KEY: any;


export default defineComponent({
  name: 'AdminUser',
  setup() {
    const param = ref();
    param.value = {};
    const users = ref();
    const pagination = ref({
      current: 1,
      pageSize: 3,
      total: 0
    });
    const loading = ref(false);

    const columns = [{
      title: '登录名',
      dataIndex: 'loginName'
    },
    {
      title: '昵称',
      dataIndex: 'name'
    },
    {
      title: '密码',
      dataIndex: 'password'
    },
    {
      title: 'Action',
      key:'action',
      slots:{customRender: 'action'}
    }
    ];

    /**
     * 数据查询 handleQuery相当于一个对象实例
     **/
    const handleQuery = (params: any) => {
      loading.value = true;
      //如果不清空现有数据，则编辑保存重新加载数据后，再次编辑，则列表显示的还是编辑前的数据
      users.value = [];
      axios.get("/user/list", {
        params: {
          page: params.page,
          size: params.size,
          loginName: param.value.loginName
        }
      }).then((response) => {
        loading.value = false;
        const data = response.data;

        if(data.success){
          users.value = data.content.list;

          // 重置分页按钮
          pagination.value.current = params.page;
          pagination.value.total = data.content.total;
        } else{
          message.error(data.message);
        }
      });
    };

    /**
     * 表格点击页码时触发
     */
    const handleTableChange = (pagination: any) => {
      console.log("看看自带的分页参数都有啥：" + pagination);
      handleQuery({
        page: pagination.current,
        size: pagination.pageSize
      });
    };


    // ------表单-------
    /**
     * 数组 [100, 101]对应：前端开发/vue
     **/
    const categoryIds = ref();
    const user = ref();
    let isAdd = false;
    const modalVisible = ref(false);
    const modalLoading = ref(false);
    const handleModalOk = ()=>{
      modalLoading.value = true;
      user.value.password = hexMd5(user.value.password + KEY);
      axios.post("/user/save", user.value).then((response) => {
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
          handleQuery({
            page: pagination.value.current,
            size: pagination.value.pageSize
          });
        } else{
          message.error(data.message);
        }
      });
    };

    /**
     * 编辑
     * @param record
     */
    const edit = (record: any)=>{
      modalVisible.value = true;
      user.value = Tool.copy(record);
      isAdd = false;
    };

    /**
     * 新增
     * @param record
     */
    const add = ()=>{
      modalVisible.value = true;
      user.value = {};
      isAdd = true;
    };

    const handleDelete = (id: number)=>{
      axios.delete("/user/delete/"+id).then((response) => {

        const data = response.data; //data = commonResp
        if(data.success){
          message.success('删除成功');
          //重新加载列表
          handleQuery({
            page: pagination.value.current,
            size: pagination.value.pageSize
          });
        }
      });
    };

    const level1 = ref();


    onMounted(() => {
      handleQuery({
        page: 1,
        size: pagination.value.pageSize,
      });
    });

    return {
      param,
      users,
      pagination,
      columns,
      loading,
      handleTableChange,
      handleQuery,

      edit,
      add,

      user,
      modalVisible,
      modalLoading,
      handleModalOk,
      handleDelete,
      categoryIds,
      level1,
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

