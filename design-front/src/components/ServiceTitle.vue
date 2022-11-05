<template>
  <div class="service-title">
    <div class="title-header">
<!--todo转换成变量名-->
      <span class="title"><h1>{{titleHeader}}</h1></span>
    </div>
    <div class="title-linear"></div>
  </div>
</template>

<script>
export default {
  name: 'ServiceTitle',
  props: {
    titleHeader: String
  }
}
</script>

<style scoped>
.service-title{
  padding-top: 20px;
  padding-bottom: 20px;
}
.service-title .title{
  display: inline-block;
  color: #1D3667;
  padding: 0 40px 10px 40px;
  border-radius: 10px;
}
.service-title .title-linear{
  background-color:#D9D9D9;
  margin: -10px auto 15px;
  height: 10px;
  width: 20%;
  border-radius: 5px;
}
</style>
