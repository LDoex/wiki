<template>
  <a-layout-header class="header">
    <div class="logo">欢迎</div>
    <template #avatar><a-avatar :src="item.cover" /></template>
    <a-menu
        theme="dark"
        mode="horizontal"
        :style="{ lineHeight: '64px' }"
    >
      <a-menu-item key="/">
        <router-link to="/">首页</router-link>
      </a-menu-item>
      <a-menu-item key="/admin/user" :style="user.id?{}:{display:'none'}">
        <router-link to="/admin/user">用户管理</router-link>
      </a-menu-item>
      <a-menu-item key="/admin/ebook" :style="user.id?{}:{display:'none'}">
        <router-link to="/admin/ebook">电子书管理</router-link>
      </a-menu-item>
      <a-menu-item key="/admin/category" :style="user.id?{}:{display:'none'}">
        <router-link to="/admin/category">分类管理</router-link>
      </a-menu-item>
      <a-menu-item key="/about">
        <router-link to="/about">关于我们</router-link>
      </a-menu-item>
      <a-popconfirm
          title="确认退出登录?"
          ok-text="是"
          cancel-text="否"
          @confirm="logout()"
      >
        <a class="login-menu" v-show="user.id">
          <span>退出登录</span>
        </a>
      </a-popconfirm>

      <a class="login-menu" v-show="user.id">
        <span>您好：{{ user.name }}</span>
      </a>

      <a class="login-menu" @click="showLoginModal" v-show="!user.id">
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
          <a-input v-model:value="loginUser.loginName">
            <template #prefix><UserOutlined style="color: rgba(0, 0, 0, 0.25)" /></template>
          </a-input>
        </a-form-item>

        <a-form-item label="密码">
          <a-input v-model:value="loginUser.password" type="password">
            <template #prefix><LockOutlined style="color: rgba(0, 0, 0, 0.25)" /></template>
          </a-input>
        </a-form-item>

      </a-form>
    </a-modal>
  </a-layout-header>
</template>

<script lang="ts">
import {computed, defineComponent, ref} from 'vue';
import validate = WebAssembly.validate;
import _default from "ant-design-vue/es/color-picker";
import axios from "axios";
import {message} from "ant-design-vue";
import {LockOutlined, UserOutlined} from "@ant-design/icons-vue";
import store from "@/store";

declare let hexMd5: any;
declare let KEY: any;

export default defineComponent({
  name: 'the-header',
  components: {
    UserOutlined,
    LockOutlined,
  },
  setup(){
    //登录后保存
    // const user = ref();
    // user.value = {};
    const user = computed(() => {return store.state.user;});

    //用来登录
    const loginUser = ref({
      loginName: "test",
      password: "test"
        });

    const loginModalVisible = ref(false);
    const loginModalLoading = ref(false);

    const showLoginModal = () => {
      loginModalVisible.value = true;
      loginUser.value.loginName = "";
      loginUser.value.password = "";
    };

    //登录
    const login = () => {
      console.log("开始登录");
      loginModalLoading.value = true;
      loginUser.value.password = hexMd5(loginUser.value.password + KEY);
      axios.post("/user/login", loginUser.value).then((response) => {
        loginModalLoading.value = false;
        const data = response.data;
        if(data.success){
          loginModalVisible.value = false;
          message.success('登录成功');
          // user.value = data.content;
          console.log("调用前：", user.value);
          store.commit("setUser", data.content);
          console.log("调用后", user.value);
        } else{
          message.error(data.message);
        }
      });
    };

    //退出登录
    const logout = () => {
      console.log("退出登录开始");
      axios.get("/user/logout/"+user.value.token).then((response) => {
        const data = response.data;
        if(data.success){
          message.success('退出登录成功');
          // user.value = data.content;
          console.log("调用前：", user.value);
          store.commit("setUser", {});
          console.log("调用后", user.value);
        } else{
          message.error(data.message);
        }
      });
    };

    return{
      loginModalVisible,
      loginModalLoading,
      showLoginModal,
      login,
      loginUser,
      user,
      logout,
    }
  }
});
</script>

<style>
.logo {
  width: 120px;
  height: 31px;
  /*background: rgba(255, 255, 255, 0.2);*/
  /*margin: 16px 28px 16px 0;*/
  float: left;
  color: white;
  font-size: 18px;
}
.login-menu{
  float: right;
  color: white;
  padding-left: 10px;
}
</style>