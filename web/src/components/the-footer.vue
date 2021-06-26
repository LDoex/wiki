<template>
  <a-layout-footer style="text-align: center">
    Wiki <span v-show="user.id">欢迎：</span>{{user.name}}
  </a-layout-footer>
</template>

<script lang="ts">
import { computed, defineComponent } from 'vue';
import store from "@/store";

export default defineComponent({
  name: 'the-footer',
  setup(){
    const user = computed(() => {return store.state.user;});
    return{
      user,
    }
  }
});
</script>