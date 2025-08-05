<script setup>
</script>

<template>
  <div style="width: 100vw;height: 100vh;overflow: hidden;display: flex">
    <div style="flex: 1">
      <el-image style="width: 100%;height: 100%" fit="cover"
                src="/1.jpg"></el-image>

    </div>
    <div class="welcome-title">
      <div style="font-size: 30px;font-weight: bold">欢迎来到我们的学习平台</div>
      <div style="margin-top: 10px">lbwnb!!!在这里你可以学习如何使用java，如何搭建网站，并于java之母交谈</div>
      <div style="margin-top: 10px">最大同性交友网站欢迎您！！！</div>
    </div>
    <div style="width: 400px;text-align: center;background-color: white">
      <router-view></router-view>
    </div>
  </div>
</template>

<style scoped>
.welcome-title {
  position: absolute;
  bottom: 30px;
  left: 30px;
  color: whitesmoke;
  text-shadow: 0 0 10px black;
}
</style>