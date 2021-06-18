<template>

  <a-layout>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >

      <p>
        <a-form
            layout="inline"
            :model="param"
            @finish="handleFinish"
            @finishFailed="handleFinishFailed"
        >
          <a-form-item>
            <a-input v-model:value="param.name" placeholder="输入查询内容">
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
          :data-source="categorys"
          :pagination="pagination"
          :loading="loading"
          @change="handleTableChange">
        <template #cover="{ text: cover }">
          <img v-if="cover" :src="cover" alt="avatar" />
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
        title="分类表单"
        @ok="handleModalOk">
      <a-form :model="category" :label-col="{span:6}" >
        <a-form-item label="名称">
          <a-input v-model:value="category.name" />
        </a-form-item>
        <a-form-item label="父分类">
          <a-input v-model:value="category.sort" />
        </a-form-item>
        <a-form-item label="顺序">
          <a-input v-model:value="category.sort" />
        </a-form-item>

      </a-form>
    </a-modal>
  </a-layout>


</template>

<script lang="ts">
import { defineComponent, onMounted, ref, reactive, toRef } from 'vue';
import {message} from 'ant-design-vue';
import axios from 'axios';
import {Tool} from "@/util/tool";


export default defineComponent({
  name: 'AdminCategory',
  setup() {
    const param = ref();
    param.value = {};
    const categorys = ref();
    const pagination = ref({
      current: 1,
      pageSize: 10,
      total: 0
    });
    const loading = ref(false);

    const columns = [{
      title: '名称',
      dataIndex: 'name'
    },
    {
      title: '父分类',
      key: 'parent',
      dataIndex: 'parent'
    },
    {
      title: '顺序',
      dataIndex: 'sort'
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
      axios.get("/category/list", {
        params: {
          page: params.page,
          size: params.size,
          name: param.value.name
        }
      }).then((response) => {
        loading.value = false;
        const data = response.data;

        if(data.success){
          categorys.value = data.content.list;

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
    const category = ref({});
    let isAdd = false;
    const modalVisible = ref(false);
    const modalLoading = ref(false);
    const handleModalOk = ()=>{
      modalLoading.value = true;

      axios.post("/category/save", category.value).then((response) => {
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
      category.value = Tool.copy(record);
      isAdd = false;
    };

    /**
     * 新增
     * @param record
     */
    const add = ()=>{
      modalVisible.value = true;
      category.value = {};
      isAdd = true;
    };

    const handleDelete = (id: number)=>{
      axios.delete("/category/delete/"+id).then((response) => {

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


    onMounted(() => {
      handleQuery({
        page: 1,
        size: pagination.value.pageSize
      });
    });

    return {
      param,
      categorys,
      pagination,
      columns,
      loading,
      handleTableChange,
      handleQuery,

      edit,
      add,

      category,
      modalVisible,
      modalLoading,
      handleModalOk,
      handleDelete
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

