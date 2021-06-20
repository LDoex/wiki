<template>
  <a-layout-sider width="200" style="background: #ffffff">
    <a-menu
        mode="inline"
        v-model:openKeys="openKeys"
        :style="{ height: '100%', borderRight: 0 }"
    >
      <a-menu-item key="welcome">
        <router-link to="/">
          <MailOutlined />
          <span>欢迎</span>
        </router-link>
      </a-menu-item>
      <a-sub-menu v-for="item in level1" :key="item.id">
        <template v-slot:title>
              <span>
                <user-outlined />
                {{item.name}}
              </span>
        </template>
        <a-menu-item v-for="child in item.children" :key="child.id">{{ child.name }}</a-menu-item>
      </a-sub-menu>
      <a-sub-menu key="sub2">
        <template #title>
              <span>
                <laptop-outlined />
                subnav 2
              </span>
        </template>
        <a-menu-item key="5">option5</a-menu-item>
        <a-menu-item key="6">option6</a-menu-item>
        <a-menu-item key="7">option7</a-menu-item>
        <a-menu-item key="8">option8</a-menu-item>
      </a-sub-menu>
      <a-sub-menu key="sub3">
        <template #title>
              <span>
                <notification-outlined />
                subnav 3
              </span>
        </template>
        <a-menu-item key="9">option9</a-menu-item>
        <a-menu-item key="10">option10</a-menu-item>
        <a-menu-item key="11">option11</a-menu-item>
        <a-menu-item key="12">option12</a-menu-item>
      </a-sub-menu>
    </a-menu>
  </a-layout-sider>
</template>

<script lang="ts">
import { defineComponent, onMounted, ref, reactive, toRef } from 'vue';
import axios from 'axios';
import {Tool} from "@/util/tool";
import {message} from "ant-design-vue";

const listData: any = [];

for (let i = 0; i < 23; i++) {
  listData.push({
    href: 'https://www.antdv.com/',
    title: `ant design vue part ${i}`,
    avatar: 'https://zos.alipayobjects.com/rmsportal/ODTLcjxAfvqbxHnVXCYX.png',
    description:
        'Ant Design, a design language for background applications, is refined by Ant UED Team.',
    content:
        'We supply a series of design principles, practical patterns and high quality design resources (Sketch and Axure), to help people create their product prototypes beautifully and efficiently.',
  });
}
export default defineComponent({
  name: 'the-slider',
  setup() {
    const level1 = ref();

    /**
     * 查询所有分类
     */
    const handleQueryCategory = () => {
      axios.get("/category/all").then((response) => {

        const data = response.data; //data = commonResp
        if(data.success) {
          let categorys = data.content;
          console.log("原始数组：", categorys);

          level1.value = [];
          level1.value = Tool.array2Tree(categorys, 0);
          console.log("树形结构：", level1.value);
        } else{
          message.error(data.message);
        }
      });
    };

    const handleClick = () => {
      console.log("menu click");
    };


    onMounted(()=>{
      handleQueryCategory();
    });

    return{
      level1,
      handleClick,
    }
  }
});
</script>