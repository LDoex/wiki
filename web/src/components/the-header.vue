<template>
  <a-layout-header class="header">
    <div class="logo" />
    <a-menu
        theme="dark"
        mode="horizontal"
        :style="{ lineHeight: '64px' }"
    >
      <a-menu-item key="/">
        <router-link to="/">首页</router-link>
      </a-menu-item>
      <a-menu-item key="/admin/user">
        <router-link to="/admin/user">用户管理</router-link>
      </a-menu-item>
      <a-menu-item key="/admin/ebook">
        <router-link to="/admin/ebook">电子书管理</router-link>
      </a-menu-item>
      <a-menu-item key="/admin/category">
        <router-link to="/admin/category">分类管理</router-link>
      </a-menu-item>
      <a-menu-item key="/about">
        <router-link to="/about">关于我们</router-link>
      </a-menu-item>
      <a class="login-menu" @click="showLoginModal">
        <span>登录</span>
      </a>
    </a-menu>

    <a-modal
        v-model:visible="loginModalVisible"
        :confirm-loading="loginModalLoading"
        title="登录"
        @ok="login">
      <a-form :model="loginUser" :label-col="{span:6}" :wrapper-col="{ span: 18 }">
        <a-form-item label="登录名">
          <a-input v-model:value="loginUser.loginName" />
        </a-form-item>

        <a-form-item label="密码">
          <a-input v-model:value="loginUser.password" />
        </a-form-item>

      </a-form>
    </a-modal>
  </a-layout-header>
</template>

<script lang="ts">
import {defineComponent, ref} from 'vue';
import validate = WebAssembly.validate;
import _default from "ant-design-vue/es/color-picker";
import axios from "axios";
import {message} from "ant-design-vue";

export default defineComponent({
  name: 'the-header',
  setup(){
    const loginUser = ref({
      loginName: "test",
      password: "test"
        });

    const loginModalVisible = ref(false);
    const loginModalLoading = ref(false);

    const showLoginModal = () => {
      loginModalVisible.value = true;
    };

    //登录
    const login = () => {
      console.log("开始登录");
    };

    // const handleLoginModalOk = () => {
    //   loginModalLoading.value = true;
    //   axios.post("/login", loginUser.value).then((response) => {
    //     loginModalLoading.value = false;
    //     const data = response.data;
    //     if(data.success){
    //       loginModalVisible.value = false;
    //       message.success('登录成功');
    //     } else{
    //       message.error(data.message);
    //     }
    //   });
    // };

    return{
      loginModalVisible,
      loginModalLoading,
      showLoginModal,
      login,
      loginUser,
    }
  }
});
</script>

<style>
.login-menu{
  float: right;
  color: white;
}
</style>